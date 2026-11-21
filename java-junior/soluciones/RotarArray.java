// Rota un array K posiciones hacia la derecha en el mismo array.
public class RotarArray {
    public static void rotar(int[] numeros, int posiciones) {
        if (numeros.length == 0) {
            return;
        }
        int desplazamiento = Math.floorMod(posiciones, numeros.length);
        invertir(numeros, 0, numeros.length - 1);
        invertir(numeros, 0, desplazamiento - 1);
        invertir(numeros, desplazamiento, numeros.length - 1);
    }

    private static void invertir(int[] numeros, int izquierda, int derecha) {
        while (izquierda < derecha) {
            int temporal = numeros[izquierda];
            numeros[izquierda] = numeros[derecha];
            numeros[derecha] = temporal;
            izquierda++;
            derecha--;
        }
    }
}
