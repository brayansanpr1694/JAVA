// Imprime del 1 al 30 y sustituye los múltiplos de 3, 5 y 15 usando ternarios.
public class FizzBuzz {
    public static void main(String[] args) {
        for (int numero = 1; numero <= 30; numero++) {
            String salida = numero % 15 == 0 ? "FizzBuzz"
                    : numero % 3 == 0 ? "Fizz"
                    : numero % 5 == 0 ? "Buzz"
                    : Integer.toString(numero);
            System.out.println(salida);
        }
    }
}
