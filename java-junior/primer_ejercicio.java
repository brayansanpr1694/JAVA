public class primer_ejercicio {

    public static void main(String[] args) throws Exception {
        /*
        Contexto de entrevista: "Necesitamos clasificar la calidad de conexión de nuestros usuarios 
        para mostrarles una advertencia si su red es insuficiente."

        Escribe un programa que reciba la velocidad de descarga en Mbps y 
        la clasifique en tres categorías.

        Requisitos:

        Si la velocidad es ≥ 100 Mbps → "Excelente"
        Si la velocidad es ≥ 50 Mbps → "Buena"
        Si la velocidad es < 50 Mbps → "Lenta"
        Indicar además si la velocidad es par o impar
        Restricción: Usar operador ternario anidado para la clasificación.
         */

        int vel_internet_en_megas = 50;
        int velocidad = (int)(Math.random()*300)+1; 
        int velocidad_de_descarga = velocidad + vel_internet_en_megas;
        
        
    } 


}