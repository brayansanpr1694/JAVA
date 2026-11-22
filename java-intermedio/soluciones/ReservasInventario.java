import java.util.HashMap;
import java.util.Map;

public final class ReservasInventario {
    private enum Estado { PENDIENTE, CONFIRMADA, CANCELADA }

    private record Reserva(String producto, int cantidad, Estado estado) {}

    private final Map<String, Integer> disponibles = new HashMap<>();
    private final Map<String, Reserva> reservas = new HashMap<>();

    public ReservasInventario(Map<String, Integer> inventario) {
        Map.copyOf(inventario).forEach((producto, cantidad) -> {
            if (cantidad < 0) throw new IllegalArgumentException("Stock negativo");
            disponibles.put(producto, cantidad);
        });
    }

    public synchronized int disponible(String producto) {
        Integer cantidad = disponibles.get(producto);
        if (cantidad == null) throw new IllegalArgumentException("Producto desconocido");
        return cantidad;
    }

    public synchronized int reservado(String producto) {
        disponible(producto);
        return reservas.values().stream()
                .filter(r -> r.producto().equals(producto) && r.estado() == Estado.PENDIENTE)
                .mapToInt(Reserva::cantidad)
                .sum();
    }

    public synchronized void reservar(String id, String producto, int cantidad) {
        int stock = disponible(producto);
        if (id == null || id.isBlank() || reservas.containsKey(id) || cantidad <= 0) {
            throw new IllegalArgumentException("Identificador o cantidad inválidos");
        }
        if (cantidad > stock) throw new IllegalStateException("Stock insuficiente");
        disponibles.put(producto, stock - cantidad);
        reservas.put(id, new Reserva(producto, cantidad, Estado.PENDIENTE));
    }

    public synchronized void confirmar(String id) {
        finalizar(id, Estado.CONFIRMADA);
    }

    public synchronized void cancelar(String id) {
        finalizar(id, Estado.CANCELADA);
    }

    private void finalizar(String id, Estado estado) {
        Reserva reserva = reservas.get(id);
        if (reserva == null || reserva.estado() != Estado.PENDIENTE) {
            throw new IllegalStateException("La reserva debe estar pendiente");
        }
        if (estado == Estado.CANCELADA) {
            disponibles.merge(reserva.producto(), reserva.cantidad(), Integer::sum);
        }
        reservas.put(id, new Reserva(reserva.producto(), reserva.cantidad(), estado));
    }
}
