// Determina si un número es primo en O(√n) y muestra los primos del 1 al 100.
public class NumeroPrimo {
    public static boolean esPrimo(int numero) {
        if (numero < 2) {
            return false;
        }
        for (int divisor = 2; divisor <= numero / divisor; divisor++) {
            if (numero % divisor == 0) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        for (int numero = 1; numero <= 100; numero++) {
            if (esPrimo(numero)) {
                System.out.println(numero);
            }
        }
    }
}
