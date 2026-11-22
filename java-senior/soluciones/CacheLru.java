import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Optional;

public final class CacheLru<K, V> {
    private record Entrada<V>(V valor, Instant vence) {}

    private final int capacidad;
    private final Clock reloj;
    private final LinkedHashMap<K, Entrada<V>> entradas = new LinkedHashMap<>(16, 0.75f, true);

    public CacheLru(int capacidad, Clock reloj) {
        if (capacidad <= 0) throw new IllegalArgumentException("Capacidad no positiva");
        this.capacidad = capacidad;
        this.reloj = Objects.requireNonNull(reloj);
    }

    public synchronized Optional<V> obtener(K clave) {
        Entrada<V> entrada = entradas.get(Objects.requireNonNull(clave));
        if (entrada == null) return Optional.empty();
        if (!reloj.instant().isBefore(entrada.vence())) {
            entradas.remove(clave);
            return Optional.empty();
        }
        return Optional.of(entrada.valor());
    }

    public synchronized void guardar(K clave, V valor, Duration ttl) {
        Objects.requireNonNull(clave);
        Objects.requireNonNull(valor);
        if (ttl.isNegative() || ttl.isZero()) throw new IllegalArgumentException("TTL no positivo");
        Instant ahora = reloj.instant();
        Instant vencimiento = ahora.plus(ttl);
        entradas.values().removeIf(entrada -> !ahora.isBefore(entrada.vence()));
        entradas.put(clave, new Entrada<>(valor, vencimiento));
        if (entradas.size() > capacidad) {
            var iterador = entradas.keySet().iterator();
            iterador.next();
            iterador.remove();
        }
    }
}
