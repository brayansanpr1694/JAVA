// Clasifica una velocidad en Mbps con un ternario anidado e indica su paridad.
import java.util.Scanner;

public class ClasificadorAnchoDeBanda {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            int velocidad = scanner.nextInt();
            String categoria = velocidad >= 100 ? "Excelente" : (velocidad >= 50 ? "Buena" : "Lenta");
            String paridad = velocidad % 2 == 0 ? "par" : "impar";
            System.out.println("Categoría: " + categoria);
            System.out.println("Velocidad: " + paridad);
        }
    }
}
