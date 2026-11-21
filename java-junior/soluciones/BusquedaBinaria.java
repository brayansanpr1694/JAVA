// Busca un valor en un array ordenado y devuelve su índice o -1.
public class BusquedaBinaria {
    public static int buscar(int[] numeros, int objetivo) {
        int izquierda = 0;
        int derecha = numeros.length - 1;
        while (izquierda <= derecha) {
            int medio = izquierda + (derecha - izquierda) / 2;
            if (numeros[medio] == objetivo) {
                return medio;
            }
            if (numeros[medio] < objetivo) {
                izquierda = medio + 1;
            } else {
                derecha = medio - 1;
            }
        }
        return -1;
    }
}
