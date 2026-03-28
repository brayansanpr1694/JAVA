# ☕ Ejercicios Java — DevSenior Code

> Repositorio de ejercicios prácticos del curso **DevSenior Code**, enfocados en el uso de operadores condicionales, ternarios y lógica de control en Java.

---

## 📁 Estructura del repositorio

```
📦 java-devsenior/
 ┣ 📂 src/
 ┃ ┣ 📂 semana1/
 ┃ ┃ ┣ 📄 ClasificadorAnchoBanda.java
 ┃ ┃ ┣ 📄 FizzBuzz.java
 ┃ ┃ ┗ 📄 ValidadorContrasena.java
 ┗ 📄 README.md
```

---

## 🧩 Ejercicios

### 🌐 Clasificador de Ancho de Banda

Programa que recibe la velocidad de descarga de una conexión a internet (en Mbps) y la clasifica según su rendimiento.

**Funcionalidades:**
- Determina si la conexión es **Buena** (≥ 50 Mbps) o **Lenta** (< 50 Mbps)
- Indica si la velocidad es **par o impar**
- Clasifica en tres categorías usando **ternario anidado**:
  - 🟢 **Excelente** — ≥ 100 Mbps
  - 🟡 **Buena** — ≥ 50 Mbps
  - 🔴 **Lenta** — < 50 Mbps

---

### 🏢 FizzBuzz

Programa clásico que imprime los números del 1 al 30 aplicando reglas de divisibilidad.

**Reglas:**
| Condición | Salida |
|---|---|
| Múltiplo de 3 y 5 | `FizzBuzz` |
| Múltiplo de 3 | `Fizz` |
| Múltiplo de 5 | `Buzz` |
| Ninguna anterior | El número |

> 🔒 **Restricción:** Resuelto usando exclusivamente el **operador ternario**.

---

### 🔒 Validador de Contraseña

Programa que valida si una contraseña ingresada por el usuario cumple con criterios de seguridad.

**Validaciones:**
- ✅ Mínimo **8 caracteres**
- ✅ Al menos **un número**
- ✅ Al menos **una letra mayúscula**

**Resultado final:**
- 🛡️ `SEGURA` — cumple todas las condiciones
- ⚠️ `DÉBIL` — falla al menos una condición

> 🔒 **Restricción:** Cada validación retorna su resultado usando el **operador ternario**.

---

## 🛠️ Tecnologías

![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![IDE](https://img.shields.io/badge/IDE-IntelliJ%20%2F%20VS%20Code-blue?style=for-the-badge&logo=intellijidea&logoColor=white)

---

## ▶️ ¿Cómo ejecutar?

**Requisitos:**
- JDK 17 o superior instalado
- Un IDE como IntelliJ IDEA o VS Code con extensión Java

**Pasos:**

```bash
# 1. Clona el repositorio
git clone https://github.com/tu-usuario/java-devsenior.git

# 2. Navega al directorio
cd java-devsenior/src/semana1

# 3. Compila el archivo que desees
javac ClasificadorAnchoBanda.java

# 4. Ejecútalo
java ClasificadorAnchoBanda
```

---

## 📚 Conceptos practicados

- Operadores relacionales y lógicos
- Condicionales `if/else`
- Operador ternario `? :`
- Ternario anidado
- Entrada de datos con `Scanner`
- Métodos de `String` (`length()`, `matches()`)

---

## 👤 Autor

**Alejo** — Estudiante de Ingeniería en Telecomunicaciones  
[UTS Bucaramanga](https://www.uts.edu.co/) · Curso DevSenior Code
