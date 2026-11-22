import java.util.HashMap;
import java.util.Map;

public final class TransferenciasIdempotentes {
    public record Solicitud(String clave, String origen, String destino, long monto) {}

    public enum Resultado {
        APLICADA, SALDO_INSUFICIENTE, CUENTA_DESCONOCIDA, MISMA_CUENTA,
        MONTO_FUERA_DE_RANGO, CLAVE_INVALIDA, CONFLICTO
    }

    private record Registro(Solicitud solicitud, Resultado resultado) {}

    private final Map<String, Long> saldos = new HashMap<>();
    private final Map<String, Registro> registros = new HashMap<>();

    public TransferenciasIdempotentes(Map<String, Long> inicial) {
        Map.copyOf(inicial).forEach((cuenta, saldo) -> {
            if (saldo < 0) throw new IllegalArgumentException("Saldo inicial negativo");
            saldos.put(cuenta, saldo);
        });
    }

    public synchronized Map<String, Long> saldos() {
        return Map.copyOf(saldos);
    }

    public synchronized Resultado transferir(Solicitud solicitud) {
        String clave = solicitud.clave();
        if (clave == null || clave.isBlank()) return Resultado.CLAVE_INVALIDA;

        Registro previo = registros.get(clave);
        if (previo != null) {
            return previo.solicitud().equals(solicitud) ? previo.resultado() : Resultado.CONFLICTO;
        }

        Resultado resultado = aplicar(solicitud);
        registros.put(clave, new Registro(solicitud, resultado));
        return resultado;
    }

    private Resultado aplicar(Solicitud solicitud) {
        if (solicitud.monto() <= 0) return Resultado.MONTO_FUERA_DE_RANGO;
        Long origen = saldos.get(solicitud.origen());
        Long destino = saldos.get(solicitud.destino());
        if (origen == null || destino == null) return Resultado.CUENTA_DESCONOCIDA;
        if (solicitud.origen().equals(solicitud.destino())) return Resultado.MISMA_CUENTA;
        if (origen < solicitud.monto()) return Resultado.SALDO_INSUFICIENTE;
        if (destino > Long.MAX_VALUE - solicitud.monto()) return Resultado.MONTO_FUERA_DE_RANGO;

        saldos.put(solicitud.origen(), origen - solicitud.monto());
        saldos.put(solicitud.destino(), destino + solicitud.monto());
        return Resultado.APLICADA;
    }
}
