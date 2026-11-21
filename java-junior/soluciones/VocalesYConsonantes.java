// Cuenta las vocales y consonantes e ignora los demás caracteres.
import java.text.Normalizer;

public class VocalesYConsonantes {
    public record Conteo(int vocales, int consonantes) {}

    public static Conteo contar(String texto) {
        String normalizado = Normalizer.normalize(texto, Normalizer.Form.NFD);
        int vocales = 0;
        int consonantes = 0;
        for (int indice = 0; indice < normalizado.length();) {
            int letra = normalizado.codePointAt(indice);
            indice += Character.charCount(letra);
            if ("aeiou".indexOf(Character.toLowerCase(letra)) >= 0) {
                vocales++;
            } else if (Character.isLetter(letra)) {
                consonantes++;
            }
        }
        return new Conteo(vocales, consonantes);
    }
}
