import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public final class PruebasSenior {
    public static void main(String[] args) throws Exception {
        transferencias();
        cola();
        cache();
        cliente();
        inventario();
        rutas();
        System.out.println("6 ejercicios senior verificados");
    }

    private static void transferencias() throws Exception {
        var inicial = Map.of("A", 100L, "B", 0L);
        var servicio = new TransferenciasIdempotentes(inicial);
        var repetida = new TransferenciasIdempotentes.Solicitud("unica", "A", "B", 30);
        var ejecutor = Executors.newFixedThreadPool(20);
        try {
            var inicio = new CountDownLatch(1);
            var futuros = new ArrayList<Future<TransferenciasIdempotentes.Resultado>>();
            for (int i = 0; i < 20; i++) {
                futuros.add(ejecutor.submit(() -> {
                    inicio.await();
                    return servicio.transferir(repetida);
                }));
            }
            inicio.countDown();
            for (var futuro : futuros) {
                assert futuro.get(5, TimeUnit.SECONDS) == TransferenciasIdempotentes.Resultado.APLICADA;
            }
            assert servicio.saldos().equals(Map.of("A", 70L, "B", 30L));
            assert servicio.transferir(new TransferenciasIdempotentes.Solicitud("unica", "A", "B", 1))
                    == TransferenciasIdempotentes.Resultado.CONFLICTO;

            var segundo = new TransferenciasIdempotentes(inicial);
            var puerta = new CountDownLatch(1);
            var uno = new TransferenciasIdempotentes.Solicitud("1", "A", "B", 70);
            var dos = new TransferenciasIdempotentes.Solicitud("2", "A", "B", 70);
            var primero = ejecutor.submit(() -> {
                puerta.await();
                return segundo.transferir(uno);
            });
            var siguiente = ejecutor.submit(() -> {
                puerta.await();
                return segundo.transferir(dos);
            });
            puerta.countDown();
            var resultadoUno = primero.get(5, TimeUnit.SECONDS);
            var resultadoDos = siguiente.get(5, TimeUnit.SECONDS);
            assert resultadoUno != resultadoDos;
            assert List.of(resultadoUno, resultadoDos).contains(TransferenciasIdempotentes.Resultado.APLICADA);
            assert List.of(resultadoUno, resultadoDos).contains(TransferenciasIdempotentes.Resultado.SALDO_INSUFICIENTE);
            assert segundo.saldos().equals(Map.of("A", 30L, "B", 70L));
            segundo.transferir(new TransferenciasIdempotentes.Solicitud("reintegro", "B", "A", 70));
            assert segundo.transferir(uno) == resultadoUno;
            assert segundo.transferir(dos) == resultadoDos;
            assert segundo.saldos().equals(inicial);
        } finally {
            ejecutor.shutdownNow();
        }
    }

    private static void cola() throws Exception {
        var ocupado = new CountDownLatch(1);
        var liberar = new CountDownLatch(1);
        var terminados = new AtomicInteger();
        var fallo = new IllegalStateException("Fallo de prueba");
        try (var ejecutor = new ColaTrabajos(1, 2)) {
            try {
                assert ejecutor.enviar(() -> {
                    ocupado.countDown();
                    try {
                        liberar.await();
                        terminados.incrementAndGet();
                    } catch (InterruptedException error) {
                        Thread.currentThread().interrupt();
                        throw new IllegalStateException(error);
                    }
                }) == ColaTrabajos.Admision.ACEPTADO;
                assert ocupado.await(5, TimeUnit.SECONDS);
                assert ejecutor.enviar(() -> {
                    terminados.incrementAndGet();
                    throw fallo;
                }) == ColaTrabajos.Admision.ACEPTADO;
                assert ejecutor.enviar(terminados::incrementAndGet) == ColaTrabajos.Admision.ACEPTADO;
                assert ejecutor.enviar(terminados::incrementAndGet) == ColaTrabajos.Admision.COLA_LLENA;
                ejecutor.close();
                assert ejecutor.enviar(terminados::incrementAndGet) == ColaTrabajos.Admision.CERRADO;
                assert !ejecutor.esperarCierre(Duration.ZERO);
            } finally {
                liberar.countDown();
            }
            assert ejecutor.esperarCierre(Duration.ofSeconds(5));
            assert terminados.get() == 3 && ejecutor.fallos().equals(List.of(fallo));
        }
    }

    private static void cache() {
        var reloj = new RelojManual();
        var cache = new CacheLru<String, Integer>(2, reloj);
        var ttl = Duration.ofSeconds(10);
        cache.guardar("A", 1, ttl);
        cache.guardar("B", 2, ttl);
        assert cache.obtener("A").orElseThrow() == 1;
        cache.guardar("C", 3, ttl);
        assert cache.obtener("B").isEmpty() && cache.obtener("A").isPresent() && cache.obtener("C").isPresent();

        cache = new CacheLru<>(2, reloj);
        cache.guardar("A", 1, ttl);
        cache.guardar("B", 2, Duration.ofSeconds(1));
        assert cache.obtener("B").isPresent();
        reloj.ahora = Instant.EPOCH.plusSeconds(1);
        cache.guardar("C", 3, ttl);
        assert cache.obtener("A").isPresent() && cache.obtener("B").isEmpty();
        reloj.ahora = Instant.EPOCH.plusSeconds(10);
        assert cache.obtener("A").isEmpty() && cache.obtener("C").isPresent();
    }

    private static void cliente() throws Exception {
        var reloj = new AtomicLong();
        var limites = new ArrayList<Duration>();
        var esperas = new ArrayList<Duration>();
        ClienteResiliente.Espera espera = duracion -> {
            esperas.add(duracion);
            reloj.addAndGet(duracion.toNanos());
        };
        var transitorio = new ClienteResiliente<String>(maximo -> {
            limites.add(maximo);
            throw new ClienteResiliente.FalloProveedor(ClienteResiliente.TipoFallo.HTTP_503);
        }, reloj::get, espera);
        try {
            transitorio.consultar(Duration.ofMillis(250));
            throw new AssertionError("Se esperaba presupuesto agotado");
        } catch (ClienteResiliente.PresupuestoAgotado esperado) {
            assert limites.equals(List.of(Duration.ofMillis(250), Duration.ofMillis(150)));
            assert esperas.equals(List.of(Duration.ofMillis(100)));
        }
        limites.clear();
        esperas.clear();
        try {
            transitorio.consultar(Duration.ofMillis(400));
            throw new AssertionError("Se esperaba el fallo del tercer intento");
        } catch (ClienteResiliente.FalloProveedor esperado) {
            assert limites.equals(List.of(Duration.ofMillis(400), Duration.ofMillis(300), Duration.ofMillis(100)));
            assert esperas.equals(List.of(Duration.ofMillis(100), Duration.ofMillis(200)));
        }
        var permanente = new ClienteResiliente.FalloProveedor(ClienteResiliente.TipoFallo.HTTP_400);
        var llamadas = new AtomicInteger();
        var sinReintento = new ClienteResiliente<String>(maximo -> {
            llamadas.incrementAndGet();
            throw permanente;
        }, reloj::get, espera);
        try {
            sinReintento.consultar(Duration.ofSeconds(1));
            throw new AssertionError("Se esperaba un fallo permanente");
        } catch (ClienteResiliente.FalloProveedor error) {
            assert error == permanente && llamadas.get() == 1;
        }
        llamadas.set(0);
        var recuperable = new ClienteResiliente<String>(maximo -> {
            if (llamadas.incrementAndGet() == 1) {
                throw new ClienteResiliente.FalloProveedor(ClienteResiliente.TipoFallo.TIMEOUT);
            }
            return "OK";
        }, reloj::get, espera);
        assert recuperable.consultar(Duration.ofMillis(250)).equals("OK") && llamadas.get() == 2;
    }

    private static void inventario() {
        var proyeccion = new ProyeccionInventario();
        var uno = new ProyeccionInventario.Evento("a", "P", 1, 10);
        var dos = new ProyeccionInventario.Evento("b", "P", 2, -3);
        var tres = new ProyeccionInventario.Evento("c", "P", 3, -8);
        var cuatro = new ProyeccionInventario.Evento("d", "P", 4, 2);
        assert proyeccion.recibir(dos) == ProyeccionInventario.Resultado.PENDIENTE;
        assert proyeccion.vista("P").equals(new ProyeccionInventario.Vista(0, 0));
        assert proyeccion.recibir(uno) == ProyeccionInventario.Resultado.APLICADO;
        assert proyeccion.vista("P").stock() == 7;
        assert proyeccion.recibir(dos) == ProyeccionInventario.Resultado.DUPLICADO;
        assert proyeccion.vista("P").stock() == 7;
        assert proyeccion.recibir(new ProyeccionInventario.Evento("b", "Q", 1, 3))
                == ProyeccionInventario.Resultado.CONFLICTO;
        assert proyeccion.recibir(new ProyeccionInventario.Evento("otro", "P", 2, -3))
                == ProyeccionInventario.Resultado.CONFLICTO;
        assert proyeccion.recibir(cuatro) == ProyeccionInventario.Resultado.PENDIENTE;
        assert proyeccion.recibir(tres) == ProyeccionInventario.Resultado.RECHAZADO;
        assert proyeccion.vista("P").equals(new ProyeccionInventario.Vista(9, 4));
        var reconstruida = ProyeccionInventario.reconstruir(List.of(dos, uno, dos, cuatro, tres));
        assert reconstruida.vista("P").equals(proyeccion.vista("P"));
        assert reconstruida.resultados().equals(proyeccion.resultados());
    }

    private static void rutas() {
        var red = new EnrutadorLatencias();
        for (String nodo : List.of("A", "B", "C", "D", "E", "F")) red.agregarNodo(nodo);
        red.agregarEnlace("A", "B", 8);
        red.agregarEnlace("A", "C", 2);
        red.agregarEnlace("C", "B", 3);
        red.agregarEnlace("C", "A", 0);
        assert red.ruta("A", "B").orElseThrow().equals(new EnrutadorLatencias.Ruta(5, List.of("A", "C", "B")));
        assert red.ruta("A", "A").orElseThrow().equals(new EnrutadorLatencias.Ruta(0, List.of("A")));
        red.agregarEnlace("A", "B", 5);
        assert red.ruta("A", "B").orElseThrow().costo() == 5;
        assert red.ruta("B", "A").isEmpty();
        red.agregarEnlace("D", "E", Integer.MAX_VALUE);
        red.agregarEnlace("E", "F", Integer.MAX_VALUE);
        assert red.ruta("D", "F").orElseThrow().costo() == 2L * Integer.MAX_VALUE;
        try {
            red.ruta("A", "desconocido");
            throw new AssertionError("Se esperaba un error de nodo desconocido");
        } catch (IllegalArgumentException esperado) {
            assert esperado.getMessage().contains("desconocido");
        }

        int cantidad = 10_000;
        var grande = generarRed(cantidad, 42);
        long inicio = System.nanoTime();
        var ruta = grande.ruta("0", Integer.toString(cantidad - 1)).orElseThrow();
        long duracion = System.nanoTime() - inicio;
        assert ruta.costo() == cantidad - 1 && ruta.nodos().size() == cantidad;
        System.out.printf(Locale.ROOT, "Dijkstra: %,d nodos, %,d enlaces, semilla 42, %.3f ms%n",
                cantidad, 2 * cantidad - 3, duracion / 1_000_000.0);
    }

    private static EnrutadorLatencias generarRed(int nodos, long semilla) {
        var red = new EnrutadorLatencias();
        var random = new Random(semilla);
        for (int i = 0; i < nodos; i++) red.agregarNodo(Integer.toString(i));
        for (int i = 0; i < nodos - 1; i++) {
            red.agregarEnlace(Integer.toString(i), Integer.toString(i + 1), 1);
            if (i + 2 < nodos) {
                red.agregarEnlace(Integer.toString(i), Integer.toString(i + 2), 3 + random.nextInt(20));
            }
        }
        return red;
    }

    private static final class RelojManual extends Clock {
        private Instant ahora = Instant.EPOCH;

        @Override
        public ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(ZoneId zona) {
            return Clock.fixed(ahora, zona);
        }

        @Override
        public Instant instant() {
            return ahora;
        }
    }
}
