import java.time.Duration;
import java.util.Objects;
import java.util.function.LongSupplier;

public final class ClienteResiliente<T> {
    public enum TipoFallo { TIMEOUT, HTTP_503, HTTP_400 }

    public static final class FalloProveedor extends Exception {
        private final TipoFallo tipo;

        public FalloProveedor(TipoFallo tipo) {
            super(tipo.name());
            this.tipo = tipo;
        }
    }

    public static final class PresupuestoAgotado extends Exception {
        public PresupuestoAgotado() {
            super("El presupuesto no permite completar otro intento dentro del plazo");
        }
    }

    @FunctionalInterface
    public interface Proveedor<T> {
        T consultar(Duration maximo) throws FalloProveedor, InterruptedException;
    }

    @FunctionalInterface
    public interface Espera {
        void esperar(Duration duracion) throws InterruptedException;
    }

    private final Proveedor<T> proveedor;
    private final LongSupplier relojNano;
    private final Espera espera;

    public ClienteResiliente(Proveedor<T> proveedor, LongSupplier relojNano, Espera espera) {
        this.proveedor = Objects.requireNonNull(proveedor);
        this.relojNano = Objects.requireNonNull(relojNano);
        this.espera = Objects.requireNonNull(espera);
    }

    public T consultar(Duration presupuesto)
            throws FalloProveedor, PresupuestoAgotado, InterruptedException {
        long limite = presupuesto.toNanos();
        if (limite < 0) throw new IllegalArgumentException("Presupuesto negativo");
        long inicio = relojNano.getAsLong();

        for (int intento = 1; intento <= 3; intento++) {
            long restante = limite - (relojNano.getAsLong() - inicio);
            if (restante <= 0) throw new PresupuestoAgotado();
            try {
                T resultado = proveedor.consultar(Duration.ofNanos(restante));
                if (relojNano.getAsLong() - inicio >= limite) throw new PresupuestoAgotado();
                return resultado;
            } catch (FalloProveedor fallo) {
                if (fallo.tipo == TipoFallo.HTTP_400) throw fallo;
                restante = limite - (relojNano.getAsLong() - inicio);
                if (restante <= 0) throw new PresupuestoAgotado();
                if (intento == 3) throw fallo;
                Duration demora = Duration.ofMillis(100L * intento);
                if (restante <= demora.toNanos()) throw new PresupuestoAgotado();
                espera.esperar(demora);
            }
        }
        throw new AssertionError("El último intento debe devolver un resultado o lanzar un error");
    }
}
