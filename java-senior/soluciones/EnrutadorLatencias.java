import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.PriorityQueue;

public final class EnrutadorLatencias {
    private record Enlace(String destino, int latencia) {}
    private record Candidato(String nodo, long costo) {}

    public record Ruta(long costo, List<String> nodos) {
        public Ruta {
            nodos = List.copyOf(nodos);
        }
    }

    private final Map<String, List<Enlace>> adyacencia = new HashMap<>();

    public void agregarNodo(String nodo) {
        adyacencia.computeIfAbsent(Objects.requireNonNull(nodo), clave -> new ArrayList<>());
    }

    public void agregarEnlace(String origen, String destino, int latencia) {
        List<Enlace> enlaces = enlacesDesde(origen);
        enlacesDesde(destino);
        if (latencia < 0) throw new IllegalArgumentException("Latencia negativa");
        enlaces.add(new Enlace(destino, latencia));
    }

    public Optional<Ruta> ruta(String origen, String destino) {
        enlacesDesde(origen);
        enlacesDesde(destino);

        Map<String, Long> costos = new HashMap<>();
        Map<String, String> anteriores = new HashMap<>();
        PriorityQueue<Candidato> candidatos = new PriorityQueue<>(Comparator.comparingLong(Candidato::costo));
        costos.put(origen, 0L);
        candidatos.add(new Candidato(origen, 0L));

        while (!candidatos.isEmpty()) {
            Candidato actual = candidatos.remove();
            if (actual.costo() != costos.get(actual.nodo())) continue;
            if (actual.nodo().equals(destino)) {
                List<String> nodos = new ArrayList<>();
                for (String nodo = destino; nodo != null; nodo = anteriores.get(nodo)) {
                    nodos.add(nodo);
                }
                Collections.reverse(nodos);
                return Optional.of(new Ruta(actual.costo(), nodos));
            }
            for (Enlace enlace : enlacesDesde(actual.nodo())) {
                long costo = actual.costo() + enlace.latencia();
                if (costo < costos.getOrDefault(enlace.destino(), Long.MAX_VALUE)) {
                    costos.put(enlace.destino(), costo);
                    anteriores.put(enlace.destino(), actual.nodo());
                    candidatos.add(new Candidato(enlace.destino(), costo));
                }
            }
        }
        return Optional.empty();
    }

    private List<Enlace> enlacesDesde(String nodo) {
        List<Enlace> enlaces = adyacencia.get(nodo);
        if (enlaces == null) throw new IllegalArgumentException("Nodo desconocido: " + nodo);
        return enlaces;
    }
}
