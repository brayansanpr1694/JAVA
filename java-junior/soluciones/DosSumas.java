// Encuentra dos índices que suman el objetivo con fuerza bruta y con un mapa.
import java.util.HashMap;
import java.util.Map;

public class DosSumas {
    public static int[] fuerzaBruta(int[] numeros, int objetivo) {
        for (int primero = 0; primero < numeros.length; primero++) {
            for (int segundo = primero + 1; segundo < numeros.length; segundo++) {
                if ((long) numeros[primero] + numeros[segundo] == objetivo) {
                    return new int[]{primero, segundo};
                }
            }
        }
        throw new IllegalArgumentException("No hay dos números que sumen el objetivo.");
    }

    public static int[] conMapa(int[] numeros, int objetivo) {
        Map<Long, Integer> indices = new HashMap<>();
        for (int indice = 0; indice < numeros.length; indice++) {
            long valor = numeros[indice];
            Integer anterior = indices.get(objetivo - valor);
            if (anterior != null) {
                return new int[]{anterior, indice};
            }
            indices.put(valor, indice);
        }
        throw new IllegalArgumentException("No hay dos números que sumen el objetivo.");
    }
}
