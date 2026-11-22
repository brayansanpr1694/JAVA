# 🟡 Java intermedio — Ingeniería de software

[Ver las soluciones y ejecutar las pruebas](soluciones/README.md).

Implementa cada ejercicio en Java 17 o posterior. Incluye pruebas de los casos de aceptación y documenta cómo ejecutarlas. Las entradas de ejemplo describen el comportamiento esperado y puedes exponerlo mediante métodos en Java.

| # | Ejercicio | Temas |
|---|---|---|
| 01 | [Reservas de inventario](#01--reservas-de-inventario) | POO, invariantes |
| 02 | [Resumen de facturación](#02--resumen-de-facturación) | BigDecimal, colecciones, streams |
| 03 | [Importador de mediciones](#03--importador-de-mediciones) | Archivos, validación, excepciones |
| 04 | [Consolidación de ventanas de mantenimiento](#04--consolidación-de-ventanas-de-mantenimiento) | Intervalos, ordenamiento |
| 05 | [Acuerdos de nivel de servicio](#05--acuerdos-de-nivel-de-servicio) | Instant, Clock, pruebas deterministas |
| 06 | [Planificador de builds](#06--planificador-de-builds) | Grafos, orden topológico |

## 01 — Reservas de inventario

Una tienda reserva productos mientras el cliente completa el pago. Diseña un servicio con operaciones para reservar, confirmar y cancelar una reserva. Cada reserva tiene un identificador único, un producto y una cantidad positiva.

- Reservar reduce las unidades disponibles. Confirmar consume las unidades reservadas. Cancelar devuelve las unidades al disponible.
- Una reserva terminada no puede volver a cambiar de estado. Si falta stock, el producto no existe o el identificador se repite, la operación falla sin modificar el inventario.

**Aceptación.** Con 10 unidades disponibles, reservar 4 deja 6. Una segunda solicitud de 7 falla y quedan 6. Cancelar la primera reserva devuelve el disponible a 10. En otro caso, reservar 4 y confirmar deja 6 disponibles y 0 reservadas.

## 02 — Resumen de facturación

Recibe transacciones con identificador, cliente e importe en COP. Un importe negativo representa una devolución. Genera un resumen con la cantidad de transacciones y el importe neto por cliente, ordenado por neto descendente y luego por identificador de cliente ascendente.

- Usa `BigDecimal` construido a partir de texto y conserva dos decimales. Rechaza importes que no puedan representarse exactamente con dos decimales e identificadores repetidos.
- Usa streams para agrupar y ordenar el resumen.

**Aceptación.** Para A con `100.10` y `-30.05` y B con `70.05`, devuelve A con 2 transacciones y B con 1, ambos con neto `70.05`. Una entrada vacía devuelve un resumen vacío.

## 03 — Importador de mediciones

Procesa un archivo UTF-8 con columnas `sensor;instante;valor` y cabecera obligatoria. Cada fila contiene un sensor no vacío, un instante ISO 8601 válido y una medida decimal entre 0 y 100 inclusive.

- Lee el archivo línea por línea. Entrega las mediciones válidas y los rechazos con su número físico de línea y motivo. Tras una fila inválida, continúa con la siguiente.
- Trata un fallo de lectura o una cabecera incorrecta como un error del archivo completo. Las comillas y los separadores escapados quedan fuera del formato.

**Aceptación.** Con la cabecera en la línea 1, una medida de `0` en la línea 2, una fecha inválida en la 3 y una medida de `100` en la 4, devuelve dos mediciones y un rechazo en la línea 3.

## 04 — Consolidación de ventanas de mantenimiento

Un equipo de operaciones recibe intervalos de mantenimiento desordenados. Cada intervalo se expresa como `[inicio, fin)` en minutos enteros de un día. Devuelve una lista ordenada de ventanas disjuntas para publicar el cronograma.

- Combina intervalos superpuestos o contiguos. Valida que `0 <= inicio < fin <= 1440`.
- Mantén la lista de entrada intacta. La complejidad objetivo es O(n log n).

**Aceptación.** `[[60,90), [10,30), [25,60), [100,110)]` produce `[[10,90), [100,110)]`. Una entrada vacía produce una lista vacía.

## 05 — Acuerdos de nivel de servicio

Clasifica incidentes de soporte por el tiempo transcurrido desde su apertura. Los incidentes críticos tienen 30 minutos para resolverse y los normales tienen 4 horas. Usa `Instant`, `Duration` y un `Clock` inyectado para evaluar incidentes abiertos.

- Distingue abierto en plazo, abierto vencido, resuelto en plazo y resuelto fuera de plazo.
- Resolver en el instante exacto del vencimiento cuenta como en plazo. Un incidente abierto se considera vencido cuando el reloj supera ese instante.
- Rechaza una resolución anterior a la apertura o un registro con fechas futuras respecto al reloj de evaluación.

**Aceptación.** Un incidente crítico abierto a las `10:00:00Z` sigue en plazo a las `10:30:00Z` y está vencido a las `10:30:01Z`. Las pruebas usan un reloj fijo.

## 06 — Planificador de builds

Dado un conjunto de módulos y las dependencias directas de cada uno, produce un orden de compilación válido. Si varios módulos están disponibles al mismo tiempo, elige el menor identificador en orden lexicográfico.

- Detecta dependencias desconocidas y ciclos. Devuelve un error comprensible en ambos casos. Si hay un ciclo, identifica al menos un módulo que participe en él.
- Incluye los módulos sin dependencias y los módulos aislados.

**Aceptación.** Si `api` depende de `core` y `cli` también depende de `core`, el resultado es `[core, api, cli]`. Si `core` pasa a depender de `cli`, el plan falla por ciclo.
