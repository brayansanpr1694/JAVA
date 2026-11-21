// Invierte los dígitos de un entero mediante operaciones aritméticas.
public class InvertirNumero {
    public static long invertir(int numero) {
        long invertido = 0;
        while (numero != 0) {
            invertido = invertido * 10 + numero % 10;
            numero /= 10;
        }
        return invertido;
    }
}
