// Compara desde ambos extremos, ignorando espacios y mayúsculas.
public class Palindromo {
    public static boolean esPalindromo(String texto) {
        int[] letras = texto.codePoints()
                .filter(caracter -> !Character.isWhitespace(caracter) && !Character.isSpaceChar(caracter))
                .map(Character::toLowerCase)
                .toArray();
        for (int izquierda = 0, derecha = letras.length - 1; izquierda < derecha; izquierda++, derecha--) {
            if (letras[izquierda] != letras[derecha]) {
                return false;
            }
        }
        return true;
    }
}
