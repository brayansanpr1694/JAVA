# 🔴 Java senior — Diseño de sistemas

[Ver las soluciones y ejecutar las pruebas](soluciones/README.md).

Implementa cada ejercicio en Java 17 o posterior. Incluye pruebas reproducibles y un documento breve con el contrato público, las invariantes y las decisiones de diseño. Los ejercicios se pueden ejecutar localmente con adaptadores en memoria y los dobles de prueba que necesites.

| # | Ejercicio | Temas |
|---|---|---|
| 01 | [Transferencias idempotentes](#01--transferencias-idempotentes) | Atomicidad, concurrencia |
| 02 | [Cola de trabajos con capacidad limitada](#02--cola-de-trabajos-con-capacidad-limitada) | Backpressure, cierre ordenado |
| 03 | [Caché LRU con expiración](#03--caché-lru-con-expiración) | Estructuras de datos, reloj, concurrencia |
| 04 | [Cliente resiliente con presupuesto de tiempo](#04--cliente-resiliente-con-presupuesto-de-tiempo) | Reintentos, clasificación de fallos |
| 05 | [Proyección de inventario por eventos](#05--proyección-de-inventario-por-eventos) | Idempotencia, orden, reconstrucción |
| 06 | [Enrutador de red con latencias](#06--enrutador-de-red-con-latencias) | Grafos ponderados, rendimiento |

## 01 — Transferencias idempotentes

Implementa un servicio de transferencias entre cuentas con saldo expresado en unidades monetarias mínimas enteras. Una solicitud incluye una clave de idempotencia, origen, destino y monto positivo.

- Aplica débito, crédito y registro del resultado como una sola operación lógica. Rechaza saldo insuficiente, monto fuera de rango, cuentas desconocidas y transferencias a la misma cuenta.
- Una solicitud válida que reutiliza la misma clave y los mismos datos devuelve el resultado original, incluso si fue rechazada por saldo insuficiente. Si cambian los datos de la solicitud, reporta un conflicto.
- Define las garantías del servicio en memoria y demuestra que las operaciones concurrentes conservan la suma de saldos.

**Aceptación.** Con A en 100 y B en 0, ejecutar 20 solicitudes concurrentes idénticas para transferir 30 con la misma clave deja A en 70 y B en 30. Dos transferencias de 70 con claves distintas desde una cuenta con 100 no pueden tener éxito ambas.

## 02 — Cola de trabajos con capacidad limitada

Diseña un ejecutor con un número fijo de trabajadores y un límite configurable para los trabajos pendientes. La admisión debe informar si el trabajo fue aceptado, si la cola está llena o si el ejecutor está cerrado.

- Al iniciar el cierre, deja de admitir trabajos y procesa todos los previamente aceptados. Expón una operación de espera con límite de tiempo.
- Una excepción de un trabajo debe quedar observable y el trabajador debe poder seguir procesando. Define qué ocurre si se interrumpe la espera de cierre.
- Coordina las pruebas de saturación con barreras o latches para controlar cuándo empiezan y terminan los trabajos.

**Aceptación.** Con un trabajador ocupado y dos espacios de cola llenos, un cuarto envío devuelve cola llena. Tras solicitar el cierre y liberar el trabajador, los tres trabajos aceptados terminan y los nuevos envíos devuelven cerrado.

## 03 — Caché LRU con expiración

Implementa una caché genérica de capacidad fija. Cada entrada vence después del TTL especificado al insertarla. Una lectura exitosa marca la entrada como la más reciente y conserva su fecha de vencimiento.

- En una inserción, elimina primero las entradas vencidas. Si aún falta espacio, desaloja la entrada vigente usada menos recientemente.
- Usa un reloj controlable y define cómo sincronizas lecturas y escrituras. Documenta el coste de las operaciones y cómo localizas entradas vencidas.
- Considera vencida una entrada cuando el reloj alcanza su instante de expiración.

**Aceptación.** Con capacidad 2, inserta A y B, lee A e inserta C mientras A y B siguen vigentes. El resultado contiene A y C. Si B ya venció antes de insertar C, debe salir B aunque se haya accedido a B más recientemente que a A.

## 04 — Cliente resiliente con presupuesto de tiempo

Un servicio consulta un proveedor remoto a través de una interfaz que recibe un tiempo máximo para cada intento. Implementa una política de hasta tres intentos con espera exponencial de 100 ms y 200 ms, dentro de un presupuesto total configurable.

- Reintenta fallos transitorios, como un timeout del proveedor o una respuesta 503. Devuelve inmediatamente errores que simulen fallos permanentes, como una respuesta 400.
- Calcula el presupuesto restante con tiempo monotónico. Pasa ese tiempo al adaptador como límite por intento y comprueba el presupuesto antes de esperar y antes de volver a llamar.
- Sustituye el adaptador, el reloj y la espera en las pruebas.

**Aceptación.** Con presupuesto de 250 ms y dos fallos instantáneos consecutivos, solo caben dos llamadas. Con un fallo permanente, hay una llamada. Si el segundo intento tiene éxito dentro del plazo, devuelve su resultado.

## 05 — Proyección de inventario por eventos

Construye una vista local del stock a partir de eventos con identificador global, producto, versión por producto y cambio firmado de unidades. Cada producto parte de stock 0 y su primera versión es 1.

- Aplica versiones consecutivas. Si hay un hueco, conserva los eventos pendientes de ese producto y aplícalos cuando lleguen los que faltan. Un evento idéntico repetido no debe volver a modificar el stock.
- Reporta conflictos cuando un identificador reaparece con otros datos o cuando dos eventos distintos ocupan la misma versión del mismo producto. Describe la política ante un evento que produciría stock negativo.
- Reconstruye la vista a partir del historial y verifica que coincide con el procesamiento incremental de esa misma secuencia.

**Aceptación.** Para P, recibe primero v2 con `-3` y después v1 con `+10`. Tras v2 el stock sigue en 0 y, tras v1, queda en 7. Repetir v2 conserva el stock en 7.

## 06 — Enrutador de red con latencias

Modela una red como un grafo dirigido. Cada enlace tiene una latencia entera no negativa. Devuelve el costo y la secuencia de nodos de una ruta de latencia mínima entre un origen y un destino.

- Usa una lista de adyacencia y una cola de prioridad. Distingue entre un destino inalcanzable y un identificador de nodo desconocido.
- Usa `long` para sumar latencias enteras de tipo `int`. Documenta la complejidad y cubre ciclos y empates. En un empate, se acepta cualquiera de las rutas de costo mínimo.
- Incluye un generador reproducible para una prueba con al menos 10 000 nodos. Registra sus características y el tiempo observado.

**Aceptación.** Con A→B de costo 8, A→C de costo 2 y C→B de costo 3, la ruta de A a B es `[A, C, B]` con costo 5. De A a A, la ruta contiene solo A y cuesta 0.
