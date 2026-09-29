# Guía de Laboratorio 4 — IOT

Tres aplicaciones Android independientes para practicar interfaz Material, Fragments, Navigation Component, RecyclerView, servicios web y sensores, con guías paso a paso en español.

| Proyecto | Servicio | Sensor | Guía |
|---|---|---|---|
| [Recetas](recetas) | TheMealDB | Acelerómetro: receta sorpresa | [Paso a paso](recetas/GUIA_PASO_A_PASO.md) |
| [Catálogo de productos](catalogoProductos) | DummyJSON Products | Luz: medición y aviso | [Paso a paso](catalogoProductos/GUIA_PASO_A_PASO.md) |
| [Directorio de empleados](directorioEmpleados) | DummyJSON Users | Proximidad: actualizar lista | [Paso a paso](directorioEmpleados/GUIA_PASO_A_PASO.md) |

## Empezar

1. Clona este repositorio:

   ```bash
   git clone https://github.com/SMatt781/Guia_Lab4_IOT.git
   ```

2. En Android Studio usa **File → Open** y selecciona **una carpeta de proyecto**, por ejemplo `Guia_Lab4_IOT/recetas`. No abras la raíz esperando un único proyecto Gradle.
3. Usa el JDK incluido en Android Studio y espera la sincronización Gradle. Cada carpeta contiene su Gradle Wrapper.
4. Instala el SDK requerido por su `app/build.gradle` (compileSdk 37) si Android Studio lo solicita.
5. Ejecuta en Android 14/API 34 o superior. Se necesita Internet para consultar los servicios.
6. Personaliza `lab_author` en `app/src/main/res/values/lab_strings.xml`.
7. Lee la guía del proyecto. Empieza por **recetas**, el ejemplo más cercano al laboratorio gastronómico pasado.

Los proyectos usan Java y layouts XML editables en Design. No incluyen una base de datos propia ni login. Los datos de DummyJSON son ficticios.

## Verificación

Consulta [las pruebas realizadas y sus límites](VALIDACION.md) y [el resumen de modelos](LEEME_MODELOS_LAB4.md).

Los tres proyectos compilaron; se comprobaron flujos y eventos de sensores en el emulador. El botón de prueba del sensor no sustituye probarlo físicamente.

Los archivos `local.properties`, cachés, ajustes del editor, APKs y claves de firma se excluyen del repositorio. Android Studio genera la configuración local y las clases View Binding al sincronizar/compilar.

Este es material de estudio elaborado con asistencia de IA. Los ejemplos no garantizan el enunciado del laboratorio; sigue las reglas de tu curso respecto a asistencia y atribución.
