// Compara los primeros diez términos calculados de forma iterativa y recursiva.
import java.math.BigInteger;

public class Fibonacci {
    public static BigInteger fibonacciIterativo(int n) {
        validarIndice(n);
        BigInteger actual = BigInteger.ZERO;
        BigInteger siguiente = BigInteger.ONE;
        for (int indice = 0; indice < n; indice++) {
            BigInteger suma = actual.add(siguiente);
            actual = siguiente;
            siguiente = suma;
        }
        return actual;
    }

    public static BigInteger fibonacciRecursivo(int n) {
        validarIndice(n);
        if (n < 2) {
            return BigInteger.valueOf(n);
        }
        return fibonacciRecursivo(n - 1).add(fibonacciRecursivo(n - 2));
    }

    private static void validarIndice(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("El índice debe ser no negativo.");
        }
    }

    public static void main(String[] args) {
        // La versión iterativa hace O(n) sumas. La recursiva repite subproblemas.
        System.out.println("n | Iterativo | Recursivo");
        for (int n = 0; n < 10; n++) {
            System.out.printf("%d | %d | %d%n", n, fibonacciIterativo(n), fibonacciRecursivo(n));
        }
    }
}
