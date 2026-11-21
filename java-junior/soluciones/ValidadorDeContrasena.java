// Comprueba longitud mínima de ocho caracteres, dígito y mayúscula.
import java.util.Scanner;

public class ValidadorDeContrasena {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            String clave = scanner.nextLine();
            boolean longitud = clave.codePointCount(0, clave.length()) >= 8;
            boolean numero = clave.codePoints().anyMatch(Character::isDigit);
            boolean mayuscula = clave.codePoints().anyMatch(Character::isUpperCase);
            System.out.println("Longitud: " + (longitud ? "✅" : "❌"));
            System.out.println("Número: " + (numero ? "✅" : "❌"));
            System.out.println("Mayúscula: " + (mayuscula ? "✅" : "❌"));
            System.out.println("Resultado: " + (longitud && numero && mayuscula ? "SEGURA" : "DÉBIL"));
        }
    }
}
