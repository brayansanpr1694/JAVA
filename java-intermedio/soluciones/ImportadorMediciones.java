import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public final class ImportadorMediciones {
    public record Medicion(String sensor, Instant instante, BigDecimal valor) {}
    public record Rechazo(long linea, String motivo) {}
    public record Resultado(List<Medicion> mediciones, List<Rechazo> rechazos) {}

    public static Resultado importar(Path archivo) throws IOException {
        List<Medicion> mediciones = new ArrayList<>();
        List<Rechazo> rechazos = new ArrayList<>();
        try (BufferedReader lector = Files.newBufferedReader(archivo, StandardCharsets.UTF_8)) {
            if (!"sensor;instante;valor".equals(lector.readLine())) {
                throw new IOException("Cabecera inválida: se esperaba sensor;instante;valor");
            }
            long numero = 1;
            String linea;
            while ((linea = lector.readLine()) != null) {
                numero++;
                try {
                    mediciones.add(convertir(linea));
                } catch (IllegalArgumentException error) {
                    rechazos.add(new Rechazo(numero, error.getMessage()));
                }
            }
        }
        return new Resultado(List.copyOf(mediciones), List.copyOf(rechazos));
    }

    private static Medicion convertir(String linea) {
        String[] columnas = linea.split(";", -1);
        if (columnas.length != 3 || linea.indexOf('"') >= 0) {
            throw new IllegalArgumentException("Se esperaban tres columnas sin comillas");
        }
        if (columnas[0].isBlank()) throw new IllegalArgumentException("Sensor vacío");
        Instant instante;
        try {
            instante = Instant.parse(columnas[1]);
        } catch (DateTimeParseException error) {
            throw new IllegalArgumentException("Instante inválido", error);
        }
        BigDecimal valor;
        try {
            valor = new BigDecimal(columnas[2]);
        } catch (NumberFormatException error) {
            throw new IllegalArgumentException("Valor decimal inválido", error);
        }
        if (valor.signum() < 0 || valor.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Valor fuera del rango de 0 a 100");
        }
        return new Medicion(columnas[0], instante, valor);
    }
}
