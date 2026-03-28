# 🟢 Java Junior — Ejercicios de entrevista

> Ejercicios progresivos de nivel junior orientados a entrevistas técnicas. Cada ejercicio incluye el enunciado, las restricciones y el ejemplo de entrada/salida esperado.

---

## 📋 Índice

| # | Ejercicio | Tema | Dificultad |
|---|---|---|---|
| 01 | [Clasificador de Ancho de Banda](#01---clasificador-de-ancho-de-banda) | Condicionales | ⭐ |
| 02 | [FizzBuzz](#02---fizzbuzz) | Condicionales + Ciclos | ⭐ |
| 03 | [Validador de Contraseña](#03---validador-de-contraseña) | Strings + Condicionales | ⭐ |
| 04 | [Invertir un número](#04---invertir-un-número) | Matemáticas + Ciclos | ⭐⭐ |
| 05 | [Palíndromo](#05---palíndromo) | Strings | ⭐⭐ |
| 06 | [Contar vocales y consonantes](#06---contar-vocales-y-consonantes) | Strings + Ciclos | ⭐⭐ |
| 07 | [Número primo](#07---número-primo) | Matemáticas + Ciclos | ⭐⭐ |
| 08 | [Suma de dígitos](#08---suma-de-dígitos) | Matemáticas + Recursión | ⭐⭐ |
| 09 | [Máximo y mínimo sin arrays](#09---máximo-y-mínimo-sin-arrays) | Ciclos + Lógica | ⭐⭐ |
| 10 | [Anagrama](#10---anagrama) | Strings + Arrays | ⭐⭐⭐ |
| 11 | [Fibonacci iterativo vs recursivo](#11---fibonacci-iterativo-vs-recursivo) | Recursión | ⭐⭐⭐ |
| 12 | [Buscar elemento en array](#12---buscar-elemento-en-array) | Arrays + Métodos | ⭐⭐⭐ |
| 13 | [Rotar array](#13---rotar-array) | Arrays | ⭐⭐⭐ |
| 14 | [Comprimir String](#14---comprimir-string) | Strings + Ciclos | ⭐⭐⭐ |
| 15 | [Dos sumas](#15---dos-sumas) | Arrays + Lógica | ⭐⭐⭐ |

---

## 01 - Clasificador de Ancho de Banda

**Contexto de entrevista:** *"Necesitamos clasificar la calidad de conexión de nuestros usuarios para mostrarles una advertencia si su red es insuficiente."*

Escribe un programa que reciba la velocidad de descarga en Mbps y la clasifique en tres categorías.

**Requisitos:**
- Si la velocidad es ≥ 100 Mbps → `"Excelente"`
- Si la velocidad es ≥ 50 Mbps → `"Buena"`
- Si la velocidad es < 50 Mbps → `"Lenta"`
- Indicar además si la velocidad es par o impar

**Restricción:** Usar operador ternario anidado para la clasificación.

**Ejemplo:**
```
Entrada: 75
Salida:
  Categoría: Buena
  Velocidad: impar
```

---

## 02 - FizzBuzz

**Contexto de entrevista:** *"Es el ejercicio más pedido en entrevistas junior. Simple en apariencia, revela si entiendes operadores y estructuras de control."*

Imprime los números del 1 al 30 aplicando las siguientes reglas:

- Múltiplo de 3 y 5 → `"FizzBuzz"`
- Múltiplo de 3 → `"Fizz"`
- Múltiplo de 5 → `"Buzz"`
- Ninguna condición → el número

**Restricción:** Resolverlo usando únicamente el operador ternario.

**Ejemplo:**
```
1, 2, Fizz, 4, Buzz, Fizz, 7, 8, Fizz, Buzz, 11, Fizz, 13, 14, FizzBuzz...
```

---

## 03 - Validador de Contraseña

**Contexto de entrevista:** *"Los sistemas de autenticación necesitan validar contraseñas en el lado del servidor. ¿Cómo implementarías esa lógica?"*

Recibe una contraseña e indica si cumple cada condición por separado, y si es `SEGURA` o `DÉBIL` en general.

**Requisitos:**
- Mínimo 8 caracteres
- Al menos un número
- Al menos una letra mayúscula

**Restricción:** Cada validación debe retornar su resultado con el operador ternario.

**Ejemplo:**
```
Entrada: "Java2024"
Salida:
  Longitud: ✅
  Número: ✅
  Mayúscula: ✅
  Resultado: SEGURA
```

---

## 04 - Invertir un número

**Contexto de entrevista:** *"Sin usar String ni reverse(), invierte un número entero positivo."*

Dado un número entero, devuelve el número con sus dígitos en orden inverso usando solo operaciones matemáticas.

**Restricción:** No se permite convertir el número a String.

**Ejemplo:**
```
Entrada: 1234
Salida: 4321

Entrada: 1200
Salida: 21
```

---

## 05 - Palíndromo

**Contexto de entrevista:** *"Determina si una palabra o frase es un palíndromo. Ignora mayúsculas y espacios."*

Escribe un método `boolean esPalindromo(String texto)` que retorne `true` si el texto se lee igual de izquierda a derecha que de derecha a izquierda.

**Restricción:** No uses `StringBuilder.reverse()`. Compara con dos punteros (índices).

**Ejemplo:**
```
Entrada: "Anita lava la tina"
Salida: true

Entrada: "Java"
Salida: false
```

---

## 06 - Contar vocales y consonantes

**Contexto de entrevista:** *"Dado un String, cuenta cuántas vocales y consonantes tiene. Ignora espacios y caracteres especiales."*

Escribe un método que reciba un String y retorne la cantidad de vocales y consonantes por separado.

**Ejemplo:**
```
Entrada: "Telecomunicaciones"
Salida:
  Vocales: 9
  Consonantes: 9
```

---

## 07 - Número primo

**Contexto de entrevista:** *"¿Cómo determinas si un número es primo de forma eficiente? ¿Por qué no necesitas revisar hasta N?"*

Escribe un método `boolean esPrimo(int n)` optimizado. Luego úsalo para imprimir todos los primos entre 1 y 100.

**Restricción:** La complejidad del método debe ser O(√n).

**Ejemplo:**
```
Entrada: 17
Salida: true

Primos entre 1 y 100:
2, 3, 5, 7, 11, 13, 17, 19, 23...
```

---

## 08 - Suma de dígitos

**Contexto de entrevista:** *"Implementa la suma de dígitos de forma recursiva. ¿Cuándo se detiene la recursión?"*

Dado un número entero positivo, retorna la suma de todos sus dígitos de forma recursiva.

**Restricción:** El método debe ser recursivo, no iterativo.

**Ejemplo:**
```
Entrada: 493
Salida: 16  (4 + 9 + 3)

Entrada: 9999
Salida: 36
```

---

## 09 - Máximo y mínimo sin arrays

**Contexto de entrevista:** *"El usuario ingresa N números uno por uno. Al final muestra el mayor y el menor. No puedes almacenar todos los valores."*

Lee N números desde el teclado usando Scanner y determina el máximo y el mínimo sin guardar todos los valores en memoria.

**Restricción:** No se permite usar arrays ni colecciones.

**Ejemplo:**
```
Entrada: 5 números → 3, 17, 8, 1, 22
Salida:
  Máximo: 22
  Mínimo: 1
```

---

## 10 - Anagrama

**Contexto de entrevista:** *"¿Cómo determinas si dos palabras son anagramas sin usar métodos de ordenamiento de la librería estándar?"*

Dos palabras son anagramas si contienen exactamente los mismos caracteres en cualquier orden.

**Restricción:** No uses `Arrays.sort()`. Implementa tu propia lógica de conteo.

**Ejemplo:**
```
Entrada: "listen", "silent"
Salida: true

Entrada: "hello", "world"
Salida: false
```

---

## 11 - Fibonacci iterativo vs recursivo

**Contexto de entrevista:** *"Implementa Fibonacci de ambas formas. ¿Cuál es más eficiente y por qué?"*

Escribe dos métodos: `fibonacciIterativo(int n)` y `fibonacciRecursivo(int n)`. Compara su resultado para los primeros 10 términos.

**Ejemplo:**
```
n = 7
Iterativo:  13
Recursivo:  13

Serie: 0, 1, 1, 2, 3, 5, 8, 13, 21, 34...
```

---

## 12 - Buscar elemento en array

**Contexto de entrevista:** *"Implementa búsqueda binaria desde cero. ¿Qué condición debe cumplir el array?"*

Dado un array de enteros ordenado ascendentemente y un valor objetivo, retorna el índice donde se encuentra o -1 si no existe.

**Restricción:** No uses métodos de búsqueda de Java. Implementa búsqueda binaria manualmente.

**Ejemplo:**
```
Array: [2, 5, 8, 12, 16, 23, 38, 56]
Buscar: 23
Salida: índice 5

Buscar: 10
Salida: -1
```

---

## 13 - Rotar array

**Contexto de entrevista:** *"Rota un array K posiciones hacia la derecha in-place, sin crear un segundo array."*

Dado un array de enteros y un valor K, desplaza todos los elementos K posiciones hacia la derecha. Los elementos que salen por la derecha entran por la izquierda.

**Restricción:** No crear un array auxiliar del mismo tamaño.

**Ejemplo:**
```
Array: [1, 2, 3, 4, 5]
K = 2
Salida: [4, 5, 1, 2, 3]
```

---

## 14 - Comprimir String

**Contexto de entrevista:** *"Implementa compresión básica de un String contando caracteres consecutivos repetidos."*

Dado un String, retorna una versión comprimida donde cada grupo de caracteres repetidos se representa como `caracter + cantidad`. Si la compresión no reduce el tamaño, retorna el String original.

**Ejemplo:**
```
Entrada: "aaabbbccddddee"
Salida: "a3b3c2d4e2"

Entrada: "abcd"
Salida: "abcd"  (la compresión sería más larga)
```

---

## 15 - Dos sumas

**Contexto de entrevista:** *"Uno de los problemas más frecuentes en entrevistas técnicas de empresas como Google y Amazon."*

Dado un array de enteros y un valor objetivo, encuentra los dos índices cuyos valores sumen exactamente el objetivo. Se garantiza que hay exactamente una solución.

**Restricción:** Primero resuélvelo en O(n²) con fuerza bruta. Luego optimízalo a O(n).

**Ejemplo:**
```
Array: [2, 7, 11, 15]
Objetivo: 9
Salida: índices [0, 1]  (porque 2 + 7 = 9)

Array: [3, 2, 4]
Objetivo: 6
Salida: índices [1, 2]
```

---

## 📌 Notas

- Cada ejercicio debe estar en su propio archivo `.java`
- Nombra los archivos en `PascalCase`: `FizzBuzz.java`, `Palindromo.java`, etc.
- Incluye el enunciado como comentario al inicio de cada archivo
- Intenta resolver cada ejercicio antes de buscar la solución

---

