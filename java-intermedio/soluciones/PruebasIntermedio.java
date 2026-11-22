import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class PruebasIntermedio {
    public static void main(String[] args) throws Exception {
        reservas();
        facturacion();
        mediciones();
        ventanas();
        acuerdos();
        builds();
        System.out.println("6 ejercicios intermedios verificados");
    }

    private static void reservas() {
        var servicio = new ReservasInventario(Map.of("P", 10));
        servicio.reservar("r1", "P", 4);
        assert servicio.disponible("P") == 6 && servicio.reservado("P") == 4;
        falla(IllegalStateException.class, () -> servicio.reservar("r2", "P", 7));
        falla(IllegalArgumentException.class, () -> servicio.reservar("r1", "P", 1));
        falla(IllegalArgumentException.class, () -> servicio.reservar("r2", "X", 1));
        assert servicio.disponible("P") == 6;
        servicio.cancelar("r1");
        assert servicio.disponible("P") == 10;
        falla(IllegalStateException.class, () -> servicio.confirmar("r1"));
        servicio.reservar("r2", "P", 4);
        servicio.confirmar("r2");
        assert servicio.disponible("P") == 6 && servicio.reservado("P") == 0;
    }

    private static void facturacion() {
        var compra = new ResumenFacturacion.Transaccion("1", "A", "100.10");
        var devolucion = new ResumenFacturacion.Transaccion("2", "A", "-30.05");
        var otra = new ResumenFacturacion.Transaccion("3", "B", "70.05");
        assert ResumenFacturacion.resumir(List.of(otra, compra, devolucion)).equals(List.of(
                new ResumenFacturacion.Resumen("A", 2, new BigDecimal("70.05")),
                new ResumenFacturacion.Resumen("B", 1, new BigDecimal("70.05"))));
        assert ResumenFacturacion.resumir(List.of()).isEmpty();
        falla(IllegalArgumentException.class, () -> ResumenFacturacion.resumir(List.of(compra, compra)));
        falla(ArithmeticException.class, () -> ResumenFacturacion.resumir(List.of(
                new ResumenFacturacion.Transaccion("4", "A", "0.001"))));
    }

    private static void mediciones() throws IOException {
        var archivo = Files.createTempFile("mediciones", ".csv");
        try {
            Files.writeString(archivo, """
                    sensor;instante;valor
                    S;2026-01-01T10:00:00Z;0
                    S;fecha-invalida;55
                    S;2026-01-01T10:01:00Z;100
                    """);
            var resultado = ImportadorMediciones.importar(archivo);
            assert resultado.mediciones().stream().map(ImportadorMediciones.Medicion::valor).toList()
                    .equals(List.of(BigDecimal.ZERO, BigDecimal.valueOf(100)));
            assert resultado.rechazos().size() == 1 && resultado.rechazos().get(0).linea() == 3;
            Files.writeString(archivo, "cabecera inválida");
            falla(IOException.class, () -> ImportadorMediciones.importar(archivo));
        } finally {
            Files.deleteIfExists(archivo);
        }
    }

    private static void ventanas() {
        var entrada = List.of(new VentanasMantenimiento.Ventana(60, 90),
                new VentanasMantenimiento.Ventana(10, 30), new VentanasMantenimiento.Ventana(25, 60),
                new VentanasMantenimiento.Ventana(100, 110));
        assert VentanasMantenimiento.consolidar(entrada).equals(List.of(
                new VentanasMantenimiento.Ventana(10, 90), new VentanasMantenimiento.Ventana(100, 110)));
        assert entrada.get(0).inicio() == 60;
        assert VentanasMantenimiento.consolidar(List.of()).isEmpty();
        falla(IllegalArgumentException.class, () -> new VentanasMantenimiento.Ventana(60, 60));
    }

    private static void acuerdos() {
        var apertura = Instant.parse("2026-01-01T10:00:00Z");
        var limite = apertura.plusSeconds(1800);
        var enPlazo = Clock.fixed(limite, ZoneOffset.UTC);
        var tarde = Clock.fixed(limite.plusSeconds(1), ZoneOffset.UTC);
        var prioridad = AcuerdosNivelServicio.Prioridad.CRITICO;
        assert AcuerdosNivelServicio.clasificar(prioridad, apertura, Optional.empty(), enPlazo)
                == AcuerdosNivelServicio.Estado.ABIERTO_EN_PLAZO;
        assert AcuerdosNivelServicio.clasificar(prioridad, apertura, Optional.empty(), tarde)
                == AcuerdosNivelServicio.Estado.ABIERTO_VENCIDO;
        assert AcuerdosNivelServicio.clasificar(prioridad, apertura, Optional.of(limite), tarde)
                == AcuerdosNivelServicio.Estado.RESUELTO_EN_PLAZO;
        assert AcuerdosNivelServicio.clasificar(prioridad, apertura, Optional.of(tarde.instant()), tarde)
                == AcuerdosNivelServicio.Estado.RESUELTO_FUERA_DE_PLAZO;
        assert AcuerdosNivelServicio.clasificar(AcuerdosNivelServicio.Prioridad.NORMAL,
                apertura, Optional.empty(), tarde) == AcuerdosNivelServicio.Estado.ABIERTO_EN_PLAZO;
        falla(IllegalArgumentException.class, () -> AcuerdosNivelServicio.clasificar(
                prioridad, apertura, Optional.of(tarde.instant()), enPlazo));
        falla(IllegalArgumentException.class, () -> AcuerdosNivelServicio.clasificar(
                prioridad, apertura, Optional.of(apertura.minusSeconds(1)), tarde));
    }

    private static void builds() {
        assert PlanificadorBuilds.ordenar(Map.of("api", List.of("core"), "cli", List.of("core"),
                "core", List.of())).equals(List.of("core", "api", "cli"));
        var ciclo = falla(IllegalArgumentException.class, () -> PlanificadorBuilds.ordenar(
                Map.of("api", List.of("core"), "cli", List.of("core"), "core", List.of("cli"))));
        assert ciclo.getMessage().endsWith("core") || ciclo.getMessage().endsWith("cli");
        falla(IllegalArgumentException.class, () -> PlanificadorBuilds.ordenar(Map.of("api", List.of("X"))));
        assert PlanificadorBuilds.ordenar(Map.of("solo", List.of())).equals(List.of("solo"));
    }

    @FunctionalInterface
    private interface Operacion {
        void ejecutar() throws Exception;
    }

    private static <T extends Exception> T falla(Class<T> tipo, Operacion operacion) {
        try {
            operacion.ejecutar();
        } catch (Exception error) {
            if (tipo.isInstance(error)) return tipo.cast(error);
            throw new AssertionError("Excepción inesperada", error);
        }
        throw new AssertionError("Se esperaba " + tipo.getSimpleName());
    }
}
