// Comprime los grupos consecutivos y conserva el texto si no se acorta.
public class ComprimirTexto {
    public static String comprimir(String texto) {
        StringBuilder comprimido = new StringBuilder();
        int inicio = 0;
        while (inicio < texto.length()) {
            int caracter = texto.codePointAt(inicio);
            int ancho = Character.charCount(caracter);
            int fin = inicio + ancho;
            while (fin < texto.length() && texto.codePointAt(fin) == caracter) {
                fin += ancho;
            }
            comprimido.appendCodePoint(caracter).append((fin - inicio) / ancho);
            inicio = fin;
        }
        return comprimido.length() < texto.length() ? comprimido.toString() : texto;
    }
}
