import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class VentanasMantenimiento {
    public record Ventana(int inicio, int fin) {
        public Ventana {
            if (inicio < 0 || inicio >= fin || fin > 1440) {
                throw new IllegalArgumentException("Ventana fuera del rango permitido");
            }
        }
    }

    public static List<Ventana> consolidar(List<Ventana> ventanas) {
        List<Ventana> ordenadas = ventanas.stream()
                .sorted(Comparator.comparingInt(Ventana::inicio)).toList();
        List<Ventana> resultado = new ArrayList<>();
        for (Ventana actual : ordenadas) {
            int ultima = resultado.size() - 1;
            if (ultima < 0 || actual.inicio() > resultado.get(ultima).fin()) {
                resultado.add(actual);
            } else {
                Ventana previa = resultado.get(ultima);
                resultado.set(ultima, new Ventana(previa.inicio(), Math.max(previa.fin(), actual.fin())));
            }
        }
        return List.copyOf(resultado);
    }
}
