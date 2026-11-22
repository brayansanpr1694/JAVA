import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public final class ResumenFacturacion {
    public record Transaccion(String id, String cliente, String importe) {}
    public record Resumen(String cliente, long cantidad, BigDecimal neto) {}

    public static List<Resumen> resumir(List<Transaccion> transacciones) {
        Set<String> ids = new HashSet<>();
        for (Transaccion transaccion : transacciones) {
            if (!ids.add(Objects.requireNonNull(transaccion.id()))) {
                throw new IllegalArgumentException("Transacción duplicada: " + transaccion.id());
            }
        }

        return transacciones.stream()
                .collect(Collectors.groupingBy(Transaccion::cliente))
                .entrySet().stream()
                .map(grupo -> new Resumen(grupo.getKey(), grupo.getValue().size(),
                        grupo.getValue().stream()
                                .map(t -> new BigDecimal(t.importe()).setScale(2, RoundingMode.UNNECESSARY))
                                .reduce(new BigDecimal("0.00"), BigDecimal::add)))
                .sorted(Comparator.comparing(Resumen::neto).reversed()
                        .thenComparing(Resumen::cliente))
                .toList();
    }
}
