# Soluciones de Java senior

Consulta los [enunciados](../README.md). Cada ejercicio se resuelve en su propio archivo.

1. [TransferenciasIdempotentes](TransferenciasIdempotentes.java)
2. [ColaTrabajos](ColaTrabajos.java)
3. [CacheLru](CacheLru.java)
4. [ClienteResiliente](ClienteResiliente.java)
5. [ProyeccionInventario](ProyeccionInventario.java)
6. [EnrutadorLatencias](EnrutadorLatencias.java)

Desde esta carpeta, con Java 17 o posterior, compila y ejecuta las pruebas con las aserciones activadas.

```sh
javac --release 17 -encoding UTF-8 *.java
java -ea PruebasSenior
```

## Contratos e invariantes

### Transferencias idempotentes

`transferir` recibe una solicitud y devuelve un resultado. Una clave no vacía identifica los datos del primer intento y su resultado, incluidos los rechazos. Reutilizarla con datos diferentes devuelve conflicto. `saldos` devuelve una copia coherente. Un monitor por instancia protege la consulta de la clave, los dos cambios de saldo y el registro. Cada saldo permanece entre cero y `Long.MAX_VALUE`, y cada transferencia aplicada conserva la suma matemática. El registro dura lo que dure la instancia.

### Cola de trabajos

`enviar` devuelve el resultado de admisión. La capacidad positiva cuenta trabajos en espera y el número fijo de trabajadores limita los trabajos en ejecución. El monitor serializa admisión y cierre. `close` inicia el drenaje sin esperar. `esperarCierre` devuelve si el ejecutor terminó y propaga `InterruptedException` si se interrumpe la espera. Los trabajos admitidos continúan su ejecución. `fallos` devuelve una copia de los fallos observados. Una excepción ordinaria queda registrada y el trabajador continúa. Ante un `Error`, el ejecutor puede reemplazar al trabajador que termina.

### Caché LRU

`guardar` inserta o reemplaza una entrada con TTL positivo. `obtener` devuelve un `Optional` y actualiza el orden de uso sin renovar el TTL. La expiración incluye el instante de vencimiento. Un monitor cubre las lecturas, que también modifican el orden, y las escrituras. El mapa enlazado ofrece lecturas O(1) en promedio. Cada escritura recorre hasta O(capacidad) entradas para retirar las vencidas antes de desalojar la menos reciente.

### Cliente resiliente

`consultar` recibe el presupuesto total y devuelve la respuesta del proveedor dentro del plazo o propaga un fallo. El reloj inyectado debe ser monotónico, como `System::nanoTime`. Cada llamada recibe el presupuesto restante y el adaptador debe respetar ese límite. Se reintentan los fallos de timeout y HTTP 503 con esperas de 100 y 200 ms, hasta tres llamadas. HTTP 400 y las interrupciones se propagan directamente. Si el plazo se agota o la próxima espera lo consumiría, se lanza `PresupuestoAgotado`.

### Proyección de inventario

`recibir` devuelve el estado del evento enviado. `vista` devuelve el stock y la última versión procesada del producto. `resultados` permite consultar el estado final de eventos que estuvieron pendientes. Por producto solo se procesan versiones consecutivas y cada versión queda ligada a un único identificador. Un cambio que causaría stock negativo o desbordamiento se registra como rechazado, mantiene el stock y consume su versión para que las siguientes puedan avanzar. El monitor hace atómica la aplicación de cada lote consecutivo. `reconstruir` reproduce la secuencia del historial en el mismo orden.

### Enrutador de latencias

Primero se crean los nodos y enlaces dirigidos con latencias no negativas. `ruta` devuelve costo y nodos si existe un camino, `Optional.empty()` si es inalcanzable y `IllegalArgumentException` para identificadores desconocidos. El grafo se usa sin modificaciones concurrentes. Dijkstra usa una lista de adyacencia y una cola de prioridad que descarta candidatos obsoletos. Solo una mejora estricta cambia el predecesor, por lo que los ciclos de costo cero no impiden la reconstrucción. Los empates permiten cualquier ruta mínima. Para V nodos y E enlaces, el peor caso es O(V + E log(E + 1)) en tiempo y O(V + E) en espacio, incluyendo una cola de hasta O(E) candidatos.

La prueba genera 10 000 nodos y 19 997 enlaces con semilla 42. Usa una cadena de costo unitario y enlaces que saltan un nodo con latencias de 3 a 22. Cronometra solo la búsqueda y verifica un costo de 9 999 con los 10 000 nodos en la ruta.

En la ejecución de verificación, la búsqueda tardó 123,430 ms con JDK 25. El tiempo depende del equipo y del estado de la JVM.
