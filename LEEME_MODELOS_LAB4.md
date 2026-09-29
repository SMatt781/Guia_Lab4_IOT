# Modelos completos para Laboratorio 4

Abre cada carpeta por separado con **File → Open** en Android Studio. Son tres aplicaciones independientes, no tres partes incompletas.

| Orden | Proyecto | Servicio externo | Sensor de la lista | Guía |
|---|---|---|---|---|
| 1 | recetas | TheMealDB | Acelerómetro: receta aleatoria | [Paso a paso](recetas/GUIA_PASO_A_PASO.md) |
| 2 | catalogoProductos | DummyJSON Products | Luz: lectura y aviso | [Paso a paso](catalogoProductos/GUIA_PASO_A_PASO.md) |
| 3 | directorioEmpleados | DummyJSON Users | Proximidad: actualización | [Paso a paso](directorioEmpleados/GUIA_PASO_A_PASO.md) |

Cada uno incluye portada con comprobación de Internet, AppActivity con tres fragments, Navigation Component, menú inferior Material, RecyclerView alimentado por API, búsqueda, detalle por click o ID y control de errores.

Estudia **recetas primero**, porque reproduce el patrón del laboratorio gastronómico pasado. Los otros dos son variaciones para practicar sin depender de un solo dominio. No son predicciones del enunciado.

Se conserva Java, XML y minSdk 34 de los proyectos originales. Se revisaron las semanas 3–6; no se añadió base de datos, Compose ni una arquitectura avanzada. Cada guía describe dónde ir, qué componente colocar en Design, cómo conectarlo a Java, por qué se usa y cómo probarlo.

Los botones “Probar acción del sensor” son ayudas: no reemplazan SensorManager ni la demostración del sensor real.

Pendiente personal: colocar nombre/código en lab_strings.xml y practicar en tu teléfono los tres sensores. Consulta README.md para abrir los proyectos después de clonarlos.

