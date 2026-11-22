import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

public final class AcuerdosNivelServicio {
    public enum Prioridad {
        CRITICO(Duration.ofMinutes(30)), NORMAL(Duration.ofHours(4));

        private final Duration limite;

        Prioridad(Duration limite) {
            this.limite = limite;
        }
    }

    public enum Estado { ABIERTO_EN_PLAZO, ABIERTO_VENCIDO, RESUELTO_EN_PLAZO, RESUELTO_FUERA_DE_PLAZO }

    public static Estado clasificar(Prioridad prioridad, Instant apertura,
                                   Optional<Instant> resolucion, Clock reloj) {
        Objects.requireNonNull(prioridad);
        Instant ahora = reloj.instant();
        Instant fin = resolucion.orElse(ahora);
        if (apertura.isAfter(ahora) || fin.isAfter(ahora) || fin.isBefore(apertura)) {
            throw new IllegalArgumentException("Fechas incompatibles con el reloj o la apertura");
        }
        boolean vencido = Duration.between(apertura, fin).compareTo(prioridad.limite) > 0;
        if (resolucion.isPresent()) {
            return vencido ? Estado.RESUELTO_FUERA_DE_PLAZO : Estado.RESUELTO_EN_PLAZO;
        }
        return vencido ? Estado.ABIERTO_VENCIDO : Estado.ABIERTO_EN_PLAZO;
    }
}
