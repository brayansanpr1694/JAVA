// Lee N enteros y muestra el máximo y el mínimo sin almacenarlos.
import java.util.Scanner;

public class MaximoYMinimo {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            int cantidad = scanner.nextInt();
            if (cantidad <= 0) {
                System.out.println("La cantidad debe ser positiva.");
                return;
            }
            int minimo = scanner.nextInt();
            int maximo = minimo;
            for (int leidos = 1; leidos < cantidad; leidos++) {
                int numero = scanner.nextInt();
                minimo = Math.min(minimo, numero);
                maximo = Math.max(maximo, numero);
            }
            System.out.println("Máximo: " + maximo);
            System.out.println("Mínimo: " + minimo);
        }
    }
}
