import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class ColaTrabajos implements AutoCloseable {
    public enum Admision { ACEPTADO, COLA_LLENA, CERRADO }

    private final ThreadPoolExecutor ejecutor;
    private final ConcurrentLinkedQueue<Throwable> fallos = new ConcurrentLinkedQueue<>();

    public ColaTrabajos(int trabajadores, int capacidadPendiente) {
        ejecutor = new ThreadPoolExecutor(trabajadores, trabajadores, 0L,
                TimeUnit.NANOSECONDS, new ArrayBlockingQueue<>(capacidadPendiente));
    }

    public synchronized Admision enviar(Runnable trabajo) {
        Objects.requireNonNull(trabajo);
        if (ejecutor.isShutdown()) return Admision.CERRADO;
        try {
            ejecutor.execute(() -> {
                try {
                    trabajo.run();
                } catch (Throwable fallo) {
                    fallos.add(fallo);
                    if (fallo instanceof Error error) throw error;
                }
            });
            return Admision.ACEPTADO;
        } catch (RejectedExecutionException lleno) {
            return Admision.COLA_LLENA;
        }
    }

    public List<Throwable> fallos() {
        return List.copyOf(fallos);
    }

    @Override
    public synchronized void close() {
        ejecutor.shutdown();
    }

    public boolean esperarCierre(Duration limite) throws InterruptedException {
        if (limite.isNegative()) throw new IllegalArgumentException("Límite negativo");
        return ejecutor.awaitTermination(limite.toNanos(), TimeUnit.NANOSECONDS);
    }
}
