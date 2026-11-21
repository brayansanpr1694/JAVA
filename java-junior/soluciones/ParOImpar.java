// Lee un entero e imprime Par si es divisible por dos e Impar en caso contrario.
import java.util.Scanner;

public class ParOImpar {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            int numero = scanner.nextInt();
            if (numero % 2 == 0) {
                System.out.println("Par");
            } else {
                System.out.println("Impar");
            }
        }
    }
}
