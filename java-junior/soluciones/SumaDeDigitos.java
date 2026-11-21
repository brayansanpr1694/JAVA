// Suma los dígitos de un entero usando recursión.
public class SumaDeDigitos {
    public static int sumar(int numero) {
        if (numero == 0) {
            return 0;
        }
        return Math.abs(numero % 10) + sumar(numero / 10);
    }
}
