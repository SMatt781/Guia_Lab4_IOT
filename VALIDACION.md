# Comprobaciones realizadas

Fecha: 28/09/2026. Material de práctica para Lab4.

## Compilación y comprobaciones automáticas

| Proyecto | assembleDebug | testDebugUnitTest | lintDebug |
|---|---|---|---|
| recetas | Correcto | 3 pruebas, 0 fallos | 0 errores, 28 advertencias |
| catalogoProductos | Correcto | 3 pruebas, 0 fallos | 0 errores, 30 advertencias |
| directorioEmpleados | Correcto | 3 pruebas, 0 fallos | 0 errores, 29 advertencias |

Cada proyecto tiene dos pruebas nuevas ApiModelsTest y la prueba aritmética inicial ExampleUnitTest. Las pruebas nuevas verifican Gson y respuestas vacías. No prueban toda la interfaz ni los sensores físicos.

Lint incluye avisos de versiones más nuevas, recursos no usados de las plantillas, pluralización, notifyDataSetChanged y dibujo del fondo. No se cambiaron versiones automáticamente ni se ocultaron esos avisos. Los informes completos quedan en app/build/reports/lint-results-debug.html de cada proyecto y se regeneran con Gradle.

## Servicios públicos

Se comprobó respuesta HTTP 200 para categorías TheMealDB, ingrediente chicken_breast y receta 52959; categorías/búsqueda phone/producto 1 de DummyJSON; filtro company.department=Engineering y usuario 1 de DummyJSON. Esto verifica disponibilidad en el momento de la prueba, no garantiza que el servicio externo esté siempre activo.

## Prueba de ejecución: Pixel 4, Android 14/API 34

- Recetas: portada; 14 categorías descargadas; click en Beef y lista filtrada; una lectura de acelerómetro 20:20:20 activó random.php y abrió una receta con ingredientes/preparación; el botón Atrás del sistema volvió a la portada.
- Catálogo: portada; 24 categorías; click en Beauty y cinco productos; lectura de luz 10 lux y aviso de poca iluminación; click en producto 1 y detalle con precio, valoración, stock y descripción. Se inspeccionó además la captura de la lista para comprobar legibilidad y menú inferior.
- Empleados: portada; 12 departamentos derivados de la API; click en Engineering y 19 empleados; evento de proximidad 0 cm activó actualización preservando el filtro; búsqueda Emily mostró coincidencias; click en Emily Johnson abrió la ficha.
- ID manual 999999 en empleados: mensaje HTTP 404, sin conservar la ficha anterior visible.
- Sin Wi-Fi/datos en el emulador: apareció el diálogo Cancelar/Configuración de la portada. Se restauró la configuración de conectividad después.
- No se detectaron FATAL EXCEPTION en AndroidRuntime durante estos recorridos.

Los valores del servicio pueden cambiar: no memorices esas cantidades como requisito.

Se generaron eventos del sensor virtual con adb, no solo con el botón de prueba. No se ha verificado un Samsung A55 ni otros sensores físicos en esta tarea. Rotación, todas las combinaciones de error y todos los tamaños de pantalla quedan en las listas de prueba de las guías; no se afirma que se hayan ejecutado todas.

Al arrancar el emulador hubo avisos de lentitud del lanzador/System UI antes de las pruebas; se estabilizó y los recorridos de las apps se completaron. No se atribuyen esos avisos al código de los proyectos.

## Qué debes hacer tú

1. Abrir cada proyecto individualmente.
2. Colocar tu nombre/código en res/values/lab_strings.xml → lab_author.
3. Leer GUIA_PASO_A_PASO.md dentro de la carpeta correspondiente.
4. Repetir los recorridos en un dispositivo API 34+.
5. Comprobar especialmente los sensores reales y la navegación con teclado abierto.
6. Explicar por tu cuenta el flujo XML → Binding → botón → Retrofit/Gson → ViewModel → RecyclerView/Detalle.

Este informe describe las comprobaciones de los proyectos antes de su publicación.

