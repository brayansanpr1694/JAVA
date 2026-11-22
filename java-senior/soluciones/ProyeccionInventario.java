import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class ProyeccionInventario {
    public record Evento(String id, String producto, long version, long cambio) {
        public Evento {
            Objects.requireNonNull(id);
            Objects.requireNonNull(producto);
            if (id.isBlank() || producto.isBlank() || version <= 0) {
                throw new IllegalArgumentException("Evento inválido");
            }
        }
    }

    public enum Resultado { APLICADO, PENDIENTE, DUPLICADO, CONFLICTO, RECHAZADO }

    public record Vista(long stock, long versionProcesada) {}

    private static final class Producto {
        private final Map<Long, Evento> pendientes = new HashMap<>();
        private final Map<Long, String> idsPorVersion = new HashMap<>();
        private long version;
        private long stock;
    }

    private final Map<String, Resultado> resultados = new HashMap<>();
    private final Map<String, Evento> eventos = new HashMap<>();
    private final Map<String, Producto> productos = new HashMap<>();

    public synchronized Resultado recibir(Evento evento) {
        Evento previo = eventos.get(evento.id());
        if (previo != null) {
            return previo.equals(evento) ? Resultado.DUPLICADO : Resultado.CONFLICTO;
        }
        Producto producto = productos.computeIfAbsent(evento.producto(), clave -> new Producto());
        if (producto.idsPorVersion.containsKey(evento.version())) return Resultado.CONFLICTO;

        eventos.put(evento.id(), evento);
        producto.idsPorVersion.put(evento.version(), evento.id());
        producto.pendientes.put(evento.version(), evento);
        resultados.put(evento.id(), Resultado.PENDIENTE);

        while (producto.version < Long.MAX_VALUE) {
            Evento siguiente = producto.pendientes.remove(producto.version + 1);
            if (siguiente == null) break;
            resultados.put(siguiente.id(), aplicar(producto, siguiente.cambio()));
            producto.version++;
        }
        return resultados.get(evento.id());
    }

    private Resultado aplicar(Producto producto, long cambio) {
        if (cambio < -producto.stock || cambio > Long.MAX_VALUE - producto.stock) {
            return Resultado.RECHAZADO;
        }
        producto.stock += cambio;
        return Resultado.APLICADO;
    }

    public synchronized Vista vista(String idProducto) {
        Producto producto = productos.get(idProducto);
        return producto == null ? new Vista(0, 0) : new Vista(producto.stock, producto.version);
    }

    public synchronized Map<String, Resultado> resultados() {
        return Map.copyOf(resultados);
    }

    public static ProyeccionInventario reconstruir(Iterable<Evento> historial) {
        ProyeccionInventario proyeccion = new ProyeccionInventario();
        historial.forEach(proyeccion::recibir);
        return proyeccion;
    }
}
