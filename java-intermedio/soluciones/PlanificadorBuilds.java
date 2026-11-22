import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public final class PlanificadorBuilds {
    public static List<String> ordenar(Map<String, ? extends Collection<String>> modulos) {
        Map<String, List<String>> dependientes = new HashMap<>();
        Map<String, Set<String>> dependencias = new HashMap<>();
        for (String modulo : modulos.keySet()) {
            dependientes.put(modulo, new ArrayList<>());
            dependencias.put(modulo, new HashSet<>(modulos.get(modulo)));
        }

        PriorityQueue<String> disponibles = new PriorityQueue<>();
        for (var entrada : dependencias.entrySet()) {
            String modulo = entrada.getKey();
            if (entrada.getValue().isEmpty()) disponibles.add(modulo);
            for (String dependencia : entrada.getValue()) {
                if (!modulos.containsKey(dependencia)) {
                    throw new IllegalArgumentException(modulo + " depende de un módulo desconocido: " + dependencia);
                }
                dependientes.get(dependencia).add(modulo);
            }
        }

        List<String> orden = new ArrayList<>();
        while (!disponibles.isEmpty()) {
            String modulo = disponibles.remove();
            orden.add(modulo);
            for (String dependiente : dependientes.get(modulo)) {
                Set<String> pendientes = dependencias.get(dependiente);
                pendientes.remove(modulo);
                if (pendientes.isEmpty()) disponibles.add(dependiente);
            }
        }
        if (orden.size() != modulos.size()) {
            // En el grafo residual cada módulo bloqueado tiene una dependencia pendiente.
            String actual = dependencias.keySet().stream()
                    .filter(m -> !dependencias.get(m).isEmpty()).findFirst().orElseThrow();
            Set<String> visitados = new HashSet<>();
            while (visitados.add(actual)) actual = dependencias.get(actual).iterator().next();
            throw new IllegalArgumentException("Ciclo de dependencias que incluye a " + actual);
        }
        return List.copyOf(orden);
    }
}
