// Comprueba si dos palabras contienen los mismos caracteres sin ordenarlas.
import java.util.HashMap;
import java.util.Map;

public class Anagrama {
    public static boolean sonAnagramas(String primera, String segunda) {
        Map<Integer, Integer> frecuencias = new HashMap<>();
        primera.codePoints().forEach(letra -> frecuencias.merge(letra, 1, Integer::sum));
        segunda.codePoints().forEach(letra -> frecuencias.merge(letra, -1, Integer::sum));
        return frecuencias.values().stream().allMatch(cantidad -> cantidad == 0);
    }
}
