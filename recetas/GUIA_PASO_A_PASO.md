# Guía desde cero: Recetas y gastronomía

Este proyecto es un simulacro completo para estudiar, no una solución garantizada del laboratorio de mañana. Está hecho en Java y XML, con Material Design. No contiene Firebase, Room, una base de datos propia ni login.

El acelerómetro es el sensor del laboratorio gastronómico pasado. Por eso este es el primer modelo que debes estudiar.

## 0. Qué vas a construir y cómo abrirlo

El flujo es:

Portada → Ingresar → Categorías → seleccionar grupo → Platos → seleccionar elemento → Receta.

El menú inferior permite entrar directamente a cualquiera de los tres fragments. En Platos puedes buscar; en Receta puedes escribir un ID. Atrás del sistema desde AppActivity vuelve a la portada. El sensor de acelerómetro actúa únicamente en la lista.

1. Abre Android Studio.
2. Usa File → Open.
3. Selecciona esta carpeta completa: `recetas`, no solo `app` ni toda `Modelos`.
4. Espera la sincronización Gradle.
5. Si aparece un aviso de confianza, confirma solo si reconoces esta carpeta.
6. En la barra del proyecto, elige la vista Android. Si no ves todas las carpetas, cambia a Project.
7. Abre `app → res → layout → activity_main.xml`.
8. Cambia a Design o Split. Todos los diseños principales son XML y se pueden editar desde Design.
9. No ejecutes todavía si quieres entenderlo: recorre esta guía en orden.
10. El proyecto conserva minSdk 34 del proyecto inicial: usa un dispositivo Android 14/API 34 o superior.

Para recrearlo desde un proyecto vacío: New Project → Empty Views Activity → Java. No elijas Empty Activity de Compose. Usa el paquete de este proyecto: `com.example.recetas_gastronomia`.

## 1. Relación con tus clases y la rúbrica

Se revisaron las teorías de semanas 3, 4, 5 y 6, la rúbrica de Lab4 y el laboratorio gastronómico pasado. Se usa su estilo de trabajo: vistas XML, Binding, Activities, Fragments, Retrofit/Gson, RecyclerView, sensores y Navigation Component.

| Parte a practicar | Implementación que debes localizar |
|---|---|
| Interfaz | MaterialToolbar, TextInputLayout, MaterialButton, MaterialCardView, menú inferior. |
| Fragments y navegación | AppActivity, NavHostFragment, nav_graph.xml, tres destinos y Bundle. |
| Sensor | ListFragment, SensorManager, registro y retirada del listener. |
| RecyclerView con servicio externo | ApiService, ApiModels, CatalogRepository, CardAdapter. |
| Funcionamiento | Internet, errores, búsquedas, detalle por ID, resultados vacíos, Atrás. |

Semana 3 explica Binding/Material/diálogos; semana 4, conexión y ejecución fuera del hilo de interfaz; semana 5, RecyclerView y sensores; semana 6, fragments, estado compartido y navegación.

Se incluyen pequeñas clases auxiliares para no repetir código. No necesitas memorizar todas: entiende primero qué información entra y sale. Estos ejemplos añaden controles de errores y de ciclo de vida que ayudan a practicar, pero no cambian el temario por una arquitectura avanzada.

## 2. Preparar Gradle: habilitar Binding y librerías

1. Abre Gradle Scripts → build.gradle (Module :app). En vista Project es `app/build.gradle`.
2. Dentro del bloque `android`, localiza:

```groovy
buildFeatures {
    viewBinding true
}
```

3. En `dependencies` localiza:

```groovy
implementation 'androidx.recyclerview:recyclerview:1.4.0'
implementation 'androidx.navigation:navigation-fragment:2.9.7'
implementation 'androidx.navigation:navigation-ui:2.9.7'
implementation 'androidx.lifecycle:lifecycle-viewmodel:2.9.4'
implementation 'androidx.lifecycle:lifecycle-livedata:2.9.4'
implementation 'com.squareup.retrofit2:retrofit:2.9.0'
implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
```

4. AppCompat, Material y Activity ya estaban declaradas mediante `libs...`; no las dupliques.
5. Pulsa Sync Now al modificar Gradle.
6. Espera a que termine antes de escribir código Binding.

¿Por qué? Android no incluye todas estas herramientas automáticamente. Binding genera clases Java con referencias a los componentes de tus XML. Retrofit hace las peticiones; Gson convierte JSON a objetos; Navigation organiza destinos; RecyclerView presenta listas; ViewModel guarda estado en memoria.

Las versiones indicadas son las verificadas en estos proyectos, no una instrucción de actualizar todo tu curso a la última versión.

## 3. Manifest y tema Material

1. Abre `app → manifests → AndroidManifest.xml`.
2. Las dos líneas de permisos se ubican fuera de `application`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

3. No aparece un diálogo de permiso para Internet: no es un permiso peligroso de ejecución.
4. Se declara el sensor como opcional, para que la aplicación pueda instalarse incluso en un emulador sin sensor.
5. Dentro de `application` se declaran MainActivity y AppActivity.
6. MainActivity tiene MAIN/LAUNCHER: es la primera pantalla.
7. AppActivity no está exportada: se abre desde la propia app.
8. `android:name=".LabApplication"` conecta la clase que comparte el ejecutor de imágenes.
9. `android:theme="@style/Theme.Lab4"` conecta el tema Material.
10. Abre `res/values/lab_styles.xml`: el tema deriva de `Theme.Material3.Light.NoActionBar`.

¿Por qué NoActionBar? Porque AppActivity dibuja su propio MaterialToolbar. Si mantienes además la barra automática, tendrás dos encabezados.

Los colores y el tema están en `lab_styles.xml`; textos en `lab_strings.xml`. Cambia tu nombre y código en `lab_author`. El `hint` es una pista de entrada, no un texto ya escrito; el `text` sí es el contenido del componente.

## 4. Portada en Design: activity_main.xml

1. Abre este layout y elige Design.
2. En Component Tree observa un NestedScrollView como raíz.
3. Dentro tiene un LinearLayout vertical. NestedScrollView admite un solo hijo directo.
4. Ese contenedor contiene icono, título, explicación, botón y autor.
5. Selecciona cada componente y usa Attributes para cambiar sus propiedades.

| Componente | ID o función | Configuración importante |
|---|---|---|
| NestedScrollView | raíz | width/height match_parent; fillViewport true. |
| LinearLayout | contenedor | orientation vertical; padding 24dp; gravity center. |
| ImageView | icono | imagen vectorial; tamaño en dp. |
| TextView | título | text = lab_title; textSize en sp. |
| TextView | explicación | texto fijo, no EditText. |
| MaterialButton | enter_button | text = Ingresar; height wrap_content. |
| TextView | autor | text = lab_author. |

`match_parent` ocupa lo disponible del padre. `wrap_content` mide lo necesario. dp sirve para tamaños/márgenes; sp para texto. NestedScrollView permite desplazarse si el contenido no cabe: no crea navegación entre pantallas.

Si no aparece MaterialButton en Palette, arrástralo si está disponible o escribe la etiqueta Material en Split. Luego puedes configurar sus atributos desde Design. No necesitas hacer todo arrastrando para que siga siendo un diseño XML.

## 5. Conectar XML a MainActivity

1. Abre `app/java/com.example.recetas_gastronomia/MainActivity.java`.
2. En `onCreate`, primero se llama `super.onCreate`.
3. Se infla el XML mediante su clase Binding.
4. Se coloca como contenido de la Activity.
5. Se configura el botón.

```java
binding = ActivityMainBinding.inflate(getLayoutInflater());
setContentView(binding.getRoot());
UiInsets.apply(binding.getRoot());
binding.enterButton.setOnClickListener(v -> enter());
```

`activity_main.xml` genera `ActivityMainBinding`. El ID `enter_button` genera `binding.enterButton`. No se importa el XML como un archivo Java: Binding hace esa conexión.

Al pulsar el botón se ejecuta `enter()`. Primero consulta `NetworkUtils.hasInternet(this)`; si hay red validada abre:

```java
startActivity(new Intent(this, AppActivity.class));
```

Si no hay red, MaterialAlertDialogBuilder muestra Cancelar o Configuración. El segundo botón ejecuta Settings.ACTION_WIRELESS_SETTINGS. Al regresar hay que pulsar Ingresar otra vez; no se inventa una conexión ni se entra automáticamente.

Internet detectado no garantiza que la API funcione: el servicio puede fallar. Por eso también se controlan errores en cada petición.

## 6. Crear la Activity contenedora y el menú

Para recrear la segunda Activity: clic derecho en paquete → New → Activity → Empty Views Activity → AppActivity. El asistente crea Java/XML y suele declararla en Manifest.

En `activity_app.xml`, Design muestra:

1. LinearLayout vertical raíz.
2. MaterialToolbar arriba.
3. FragmentContainerView al centro.
4. BottomNavigationView abajo.

| Componente | ID | Medidas y función |
|---|---|---|
| MaterialToolbar | toolbar | match_parent / wrap_content; título del destino. |
| FragmentContainerView | nav_host | altura 0dp y layout_weight 1; ocupa el resto. |
| BottomNavigationView | bottom_nav | match_parent / wrap_content; menu = bottom_navigation. |

El `0dp + layout_weight=1` es intencional: el fragmento usa el espacio sobrante sin empujar el menú fuera de la pantalla. No fijes una altura grande al menú ni añadas márgenes para un modelo de celular.

FragmentContainerView debe tener `android:name="androidx.navigation.fragment.NavHostFragment"`, `app:navGraph="@navigation/nav_graph"` y `app:defaultNavHost="true"`. No basta con colocar un rectángulo en Design.

Para crear un menú: res → New → Android Resource Directory → menu; luego New → Menu Resource File → bottom_navigation. Sus tres IDs coinciden con los destinos: groupsFragment, listFragment y detailFragment.

## 7. Crear los fragments y el grafo

Para practicar la creación: paquete → New → Fragment → Fragment (Blank). En este ejemplo se usan GroupsFragment, ListFragment y DetailFragment. Puedes eliminar el código de ejemplo del asistente que no vayas a usar.

Sus XML son fragment_groups, fragment_list y fragment_detail.

1. En res crea Android Resource Directory de tipo navigation si no existe.
2. Crea Navigation Resource File llamado nav_graph.
3. Abre su vista Design.
4. Añade los tres fragments como destinos.
5. Marca GroupsFragment como Start Destination.
6. Crea la acción de grupos a lista y de lista a detalle.
7. Comprueba los IDs de acciones: action_groups_to_list y action_list_to_detail.

AppActivity encuentra NavHostFragment, obtiene su NavController y navega al ID del menú pulsado. Al cambiar el destino, actualiza título y selección del menú.

Las acciones usan popUpTo para que no se acumulen pantallas al elegir repetidamente categorías/elementos. El menú limpia la pila del grafo al cambiar pestaña. Además AppActivity intercepta Atrás y termina: vuelve a la portada, conforme al laboratorio pasado. No es el único comportamiento posible en una aplicación comercial.

## 8. Estado compartido: ViewModel, LiveData y Bundle

Abre CatalogViewModel. Guarda:

- groups: la lista de categorías.
- items: la lista de platos.
- detail: el elemento seleccionado.
- category, query y selectedId: filtro y selección.

Cada fragment obtiene la misma instancia:

```java
model = new ViewModelProvider(requireActivity()).get(CatalogViewModel.class);
```

Usar `requireActivity()` comparte el modelo entre sus fragments. Usar `this` dentro del fragment crearía otro alcance.

Al tocar una fila se envía un Bundle:

```java
Bundle args = new Bundle();
args.putString("id", item.id);
Navigation.findNavController(view).navigate(R.id.action_list_to_detail, args);
```

Bundle comunica el argumento del salto. ViewModel mantiene listas/estado mientras vive esa Activity, incluso en una rotación. LiveData avisa a la pantalla cuando llegan datos. No es una base de datos: tras cerrar realmente la Activity o morir el proceso no se garantiza conservar ese estado.

Se observa con `getViewLifecycleOwner()`, no con un observador que quede ligado indefinidamente a una vista destruida.

## 9. Entender la API y sus objetos Java

API usada: TheMealDB. Base URL: https://www.themealdb.com/api/json/v1/1/

| Operación | Ruta o ejemplo | Respuesta |
|---|---|---|
| Categorías | `categories.php` | Lista `categories`. |
| Por categoría | `filter.php?c=Seafood` | Lista `meals`, con ID, nombre y foto. |
| Por ingrediente | `filter.php?i=chicken_breast` | Lista `meals`. |
| Ejemplos sin filtro | `search.php?s=` | Selección de recetas, no toda la base del servicio. |
| Receta por ID | `lookup.php?i=52959` | Información completa de una receta. |
| Sorpresa | `random.php` | Una receta aleatoria. |

Abre ApiModels.java. `CategoriesResponse.categories` contiene `Category`. `MealsResponse.meals` contiene `Meal`. Las propiedades se llaman igual que el JSON: `idMeal`, `strMeal`, `strCategory`, `strArea`, `strInstructions`, `strIngredient1`…`strIngredient20` y sus medidas.

¿Por qué crear estas clases? El servidor devuelve JSON, no TextViews. Gson lo convierte a objetos Java. El nombre de cada propiedad coincide con la clave del JSON; si no coincide, habría que usar @SerializedName.

Al crear manualmente una clase: clic derecho en paquete → New → Java Class. Aquí se agrupan las clases de respuesta dentro de ApiModels para que sean fáciles de encontrar.

No confundas una respuesta de lista con una de detalle. La lista puede traer menos campos y un envoltorio; el detalle puede venir como objeto o lista de un elemento.

## 10. ApiService: declarar las peticiones Retrofit

1. Crea una interfaz Java llamada ApiService.
2. Coloca @GET con la ruta relativa.
3. Declara el tipo Call que esperas recibir.
4. Usa @Query para parámetros de consulta.

```java
@GET("filter.php")
Call<ApiModels.MealsResponse> byIngredient(@Query("i") String ingredient);
```

La interfaz declara qué se solicita; no es el lugar donde dibujas la lista.

Abre CatalogRepository. Su Retrofit.Builder configura URL base, tiempos máximos y GsonConverterFactory. Después crea la implementación de ApiService.

`enqueue` ejecuta de manera asíncrona: la pantalla no queda congelada esperando Internet. onResponse distingue respuesta válida de HTTP de error; onFailure maneja fallos de red. No se usa execute() en el hilo principal.

El repositorio convierte respuestas a CardItem/DetailData. Eso permite usar el mismo diseño de fila sin que el adapter necesite conocer todos los campos del servicio.

Se cancela una petición anterior al repetir una búsqueda y se cancela al destruir la vista. Así una respuesta tardía no intenta modificar una pantalla cerrada.

## 11. Diseñar una fila RecyclerView

1. Crea Layout Resource File → item_card.
2. Raíz: MaterialCardView, width match_parent, height wrap_content.
3. Dentro: LinearLayout horizontal con padding 12dp.
4. Imagen photo: 80dp × 80dp y scaleType centerCrop.
5. A su lado: LinearLayout vertical, width 0dp, weight 1.
6. Añade TextView title y subtitle.
7. title tiene texto grande/negrita; subtitle admite hasta tres líneas.
8. La tarjeta es clickable y focusable para mostrar respuesta al tocar.

No agregues una tarjeta por cada dato de la API. Diseñas una sola fila; RecyclerView la repite.

CardItem es el dato mínimo de una fila: id, title, subtitle e imageUrl. DetailData contiene el dato de detalle. Son clases normales, no Activities.

## 12. Adapter y ViewHolder

Abre CardAdapter. Lee sus métodos en este orden:

1. Constructor: recibe qué hacer al pulsar una fila.
2. onCreateViewHolder: infla ItemCardBinding y crea un Holder.
3. onBindViewHolder: toma el dato de la posición y lo coloca en título/subtítulo/imagen.
4. getItemCount: cuántas filas hay.
5. submit: reemplaza la lista y notifica al RecyclerView.

El Holder guarda referencias de los componentes. RecyclerView reutiliza filas: no crea todas las vistas una y otra vez al desplazarte.

En cada fragment de lista se configura:

```java
binding.recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
binding.recycler.setAdapter(adapter);
```

Sin LayoutManager la lista no sabe distribuir sus filas. Sin Adapter no sabe qué contenido mostrar.

Se usa notifyDataSetChanged por sencillez didáctica. En listas grandes de producción podría usarse ListAdapter/DiffUtil; no se añadió para no desviarte del curso.

## 13. Pantalla Categorías: configurar y cargar

En fragment_groups.xml:

1. LinearLayout vertical.
2. TextView de encabezado.
3. MaterialButton reload_button.
4. ProgressBar progress, inicialmente gone.
5. TextView status.
6. RecyclerView recycler con altura 0dp y weight 1.

En GroupsFragment:

1. onCreateView infla FragmentGroupsBinding.
2. onViewCreated obtiene ViewModel, crea Adapter y lo conecta.
3. Observa groups; cuando llega una lista, llama adapter.submit.
4. load consulta CatalogRepository.groups.
5. Muestra progreso mientras consulta.
6. En success guarda la lista en LiveData y oculta progreso.
7. En error muestra el mensaje.
8. Al tocar un grupo guarda su ID como filtro y navega a la lista con Bundle.

El botón Actualizar vuelve a consultar Internet. No agregues datos manualmente para simular que es una API: este modelo realmente consume el servicio.

## 14. Pantalla Platos: buscador y lista

En fragment_list.xml, el orden es título, campo, botones, tarjeta del sensor, botón de prueba, progreso, estado y RecyclerView.

1. TextInputLayout search_layout: estilo OutlinedBox y hint = Ingrediente principal.
2. Dentro, TextInputEditText search_input: inputType text y una línea.
3. search_button: Buscar.
4. all_button: quitar filtro.
5. sensor_card contiene la instrucción y sensor_status.
6. sensor_test_button sirve para probar la acción manualmente.
7. recycler ocupa el espacio restante.

Pon la pista en TextInputLayout; no repitas el mismo hint y text dentro del EditText. Así evitas textos superpuestos.

Al pulsar Buscar:

1. Lee el campo y usa trim().
2. Si está vacío, TextInputLayout.setError informa.
3. Si tiene texto, limpia el error y cambia el filtro del ViewModel.
4. Se cancela la petición previa, limpia filas antiguas y muestra Cargando.
5. Consulta la API.
6. Guarda resultados en model.items.
7. El observador actualiza Adapter y muestra cantidad/filtro.
8. Si no hay coincidencias, muestra “No se encontraron resultados”.

Ejemplo para esta app: `chicken_breast`. Los datos y búsquedas del servicio pueden estar en inglés aunque las instrucciones de la interfaz estén en español.

## 15. Detalle: selección e ID manual

En fragment_detail.xml hay un NestedScrollView con un único LinearLayout vertical. Dentro: título, instrucciones, campo ID, botón Buscar, progreso y contenido de detalle.

1. id_layout contiene id_input, con entrada numérica.
2. Al llegar desde una lista se lee el argumento id.
3. Si no hay detalle en memoria se consulta al servidor.
4. Si llegas desde el menú sin selección previa, se solicita escribir un ID.
5. Al buscar se valida con `[1-9][0-9]*`: positivo y sin letras.
6. Una búsqueda nueva oculta el detalle anterior.
7. Cuando responde, se llenan photo, title, subtitle y description.
8. Si no existe o falla, se informa sin cerrar la app.

Prueba el ID `52959`. El campo no es posición de RecyclerView: es el identificador de la API. No asumas que posición 0 significa ID 0.

### Ingredientes: por qué se recorren veinte posiciones

Una respuesta de lista trae pocos campos. La respuesta de detalle incluye hasta veinte pares ingrediente/medida. En `ingredientsText()` se crean dos arreglos y se recorre cada posición con `for`. Solo se muestran ingredientes no vacíos. Así no aparecen renglones “null” ni se inventan ingredientes para alcanzar un número.

El laboratorio pasado solicita al menos tres ingredientes: comprueba una receta que realmente los tenga, por ejemplo ID 52959. Si la API devuelve una receta de dos ingredientes, mostrar datos reales es correcto técnicamente, pero elige otra para demostrar ese requisito. No se traducen automáticamente los ingredientes o las instrucciones del servicio.

### Relación con el laboratorio gastronómico pasado

Este proyecto replica sus partes principales: portada con autor, Internet y diálogo, dos Activities, tres fragments, menú inferior, lista por categoría/ingrediente, detalle por selección o ID y agitación que abre una receta sorpresa. Cambia `lab_author` por tu nombre y código. El menú Atrás del sistema vuelve a la portada sin recorrer pestañas, como indica ese enunciado.

## 16. Sensor: crear, registrar y retirar

Todo está en ListFragment. Implementa SensorEventListener.

```java
sensorManager = (SensorManager) requireContext()
        .getSystemService(Context.SENSOR_SERVICE);
sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
```

En onResume se registra con SENSOR_DELAY_UI. En onPause se retira con unregisterListener. Tus diapositivas usan también onStop: retirar en onPause es más estricto, porque no necesitamos lecturas mientras la pantalla deja de estar activa. Nunca se deja el listener funcionando indefinidamente.

Si getDefaultSensor devuelve null, no se registra: se muestra un aviso. Ninguno de estos tres sensores requiere ubicación ni permiso de cámara.

1. Se pide el acelerómetro, no el sensor de orientación.
2. Sus tres valores son aceleración X, Y y Z en m/s².
3. La gravedad también aparece: un teléfono quieto puede medir cerca de 9.8 m/s² en un eje. Comparar la magnitud cruda con 4 provocaría activaciones sin agitar.
4. El primer evento inicializa una estimación de gravedad.
5. En cada evento se actualiza `gravity = 0.8 × gravity + 0.2 × lectura`.
6. Se resta esa gravedad: `linear = lectura - gravity`.
7. Se calcula `sqrt(x² + y² + z²)` de la aceleración restante.
8. Si supera 4 m/s², han pasado 2 segundos desde la última activación y no hay consulta aleatoria pendiente, se llama a `requestSurprise()`.
9. La consulta usa `random.php`; no elige un objeto fijo de una lista local.
10. Al responder, se guarda el detalle en el ViewModel y se navega a Receta. El menú también cambia a Receta.
11. El filtro es una aproximación de laboratorio; no una clasificación profesional de movimientos. El umbral puede requerir calibración según el teléfono.

```java
float linear = event.values[i] - gravity[i];
squared += linear * linear;
// Tras sumar los tres ejes:
if (Math.sqrt(squared) > 4.0 && now - lastShake > 2000 && randomCall == null) {
    lastShake = now;
    requestSurprise();
}
```

El botón de prueba llama al mismo método. Fuera de Platos el listener no queda activo; al salir también se cancela una sorpresa pendiente.

## 17. Imágenes sin congelar la interfaz

ImageLoader descarga imágenes HTTPS con HttpURLConnection, siguiendo el manejo de tareas de semana 4.

1. La vista muestra un placeholder.
2. LabApplication comparte un ExecutorService de cuatro hilos.
3. La descarga ocurre fuera del hilo principal.
4. Un Handler del hilo principal coloca el Bitmap.
5. La etiqueta de la vista permite verificar que sigue esperando esa imagen.
6. Si RecyclerView reutiliza la fila para otro elemento, la respuesta antigua no le coloca una foto equivocada.
7. Se cierra el stream y se desconecta la conexión.
8. Si falla la imagen, queda el placeholder; la lista y el detalle siguen funcionando.

No se añadió Glide, Picasso o Coil para no introducir otra librería respecto al patrón de conexiones/ejecutor visto en clase. Este cargador es deliberadamente sencillo y no tiene una caché de producción.

## 18. Barras del celular, teclado y tamaños

UiInsets recibe el espacio de las barras de estado/navegación y del teclado. El padding inferior usa el máximo entre teclado y barra del sistema; no suma dos veces la misma zona.

La raíz consume esos insets para que el menú Material no vuelva a aplicarlos. Eso evita el hueco duplicado que puede diferir entre Samsung y Pixel.

No configures el XML “para Pixel 4” ni agregues 100dp por intuición. El teléfono decide el área de sus botones/gestos. Prueba las dos navegaciones y fuentes grandes. Cuando aparece el teclado, el menú se desplaza para permanecer fuera de él.

Los TextViews y tarjetas usan wrap_content para no recortar nombres. En listas pequeñas o con teclado la región RecyclerView puede reducirse: cierra el teclado al terminar de escribir.

## 19. Ciclo de vida: dónde va cada cosa

| Método | Responsabilidad |
|---|---|
| Activity.onCreate | Binding, contenido, listeners y host de navegación. |
| Fragment.onCreateView | Inflar su XML. |
| Fragment.onViewCreated | Configurar lista, observadores y botones. |
| Fragment.onResume | Activar sensor de la lista. |
| Fragment.onPause | Retirar sensor; detener acción aleatoria pendiente cuando aplica. |
| Fragment.onDestroyView | Cancelar llamada, quitar Adapter y liberar Binding. |

Un fragment puede seguir existiendo después de destruirse su vista. Por eso se asigna `binding = null` en onDestroyView y se comprueba antes de actualizar respuestas asíncronas.

ViewModel no conserva componentes visuales ni Context de Activity. Así no retiene una pantalla vieja después de rotar.

## 20. Cómo ejecutar y probar paso a paso

1. Pulsa Sync Project with Gradle Files.
2. Usa Build → Assemble Project, o el comando de abajo.
3. Selecciona el módulo app y el emulador/teléfono API 34+.
4. Pulsa Run ▶.
5. Activa Internet.
6. Personaliza nombre/código en la portada.
7. Recorre esta lista de comprobación.

1. Ingresar conectado → aparecen categorías reales y sus imágenes.
2. Tocar Seafood → solo sus platos; la pestaña Platos se selecciona.
3. Escribir chicken_breast y Buscar → platos por ingrediente, no búsqueda por nombre.
4. Tocar un plato → foto, categoría, origen, preparación e ingredientes.
5. En Receta escribir 52959 → llega la receta consultada por ID.
6. Agitar en Platos → se consulta una sorpresa y se abre Receta.
7. Agitar en Categorías/Receta → no se activa la sorpresa.
8. Mostrar ejemplos → quita el filtro; no promete recuperar toda TheMealDB.
9. Probar un ingrediente inexistente → mensaje sin resultados.
10. Buscar un ID inexistente → no debe quedar el detalle anterior visible.

Pruebas comunes adicionales:

- Desactiva Wi-Fi/datos desde la portada → diálogo y entrada bloqueada.
- Configuración abre los ajustes; vuelve, activa conexión y pulsa Ingresar.
- Desconecta Internet dentro de la app y busca → error controlado, no cierre.
- Pulsa Buscar con campo vacío → error en el campo.
- Repite búsquedas rápidas → la anterior se cancela.
- Gira el celular → ViewModel mantiene el estado mientras vive AppActivity.
- Alterna menú varias veces y pulsa Atrás → portada.
- Ejecuta sin sensor → aviso y navegación/listas siguen disponibles.
- Deja la app en segundo plano → no continúa leyendo el sensor.
- Comprueba que texto e imágenes no se superponen a las barras del sistema.

Para probar sensores en Emulator: abre Extended controls (…) y busca Virtual sensors. Las opciones dependen de la imagen Android y el dispositivo virtual; el botón de prueba permite verificar la acción si el control no está disponible. Para acreditar sensores usa además hardware/eventos reales, no solo el botón.

En Terminal de Android Studio, desde la raíz de este proyecto:

```powershell
.\gradlew.bat :app:assembleDebug
.\gradlew.bat :app:testDebugUnitTest
.\gradlew.bat :app:lintDebug
```

Si Gradle dice que JAVA_HOME no es válido, en Settings → Build, Execution, Deployment → Build Tools → Gradle selecciona el JDK incluido de Android Studio. No cambies versiones al azar para silenciar un error.

## 21. Pruebas automáticas incluidas

En app/src/test/java/com/example/recetas_gastronomia/ApiModelsTest.java hay pruebas de conversión JSON y respuesta vacía. Usan datos mínimos inventados y Gson; no necesitan Internet ni prueban el sensor físico.

ExampleUnitTest es la prueba que traía el proyecto vacío. Las pruebas nuevas complementan, no sustituyen las comprobaciones de pantalla, API y sensor.

## 22. Errores frecuentes y qué revisar

| Síntoma | Revisión |
|---|---|
| Activity...Binding no existe | viewBinding, Sync, nombre del XML y compilación de recursos. |
| binding.algo no existe | ID del XML; snake_case se convierte a camelCase. |
| Clase Java roja | package/imports y carpeta correspondiente. |
| Error de tema Material | Manifest debe usar Theme.Lab4 con padre Material3. |
| Pantalla con doble barra | Usa NoActionBar y un solo MaterialToolbar. |
| Lista vacía sin filas | Internet, estado de respuesta, Adapter y LayoutManager. |
| HTTP 404 | Ruta/ID/filtro, no es necesariamente un error de RecyclerView. |
| Sensor null | Hardware/emulador no lo tiene; no lo inventes con un permiso. |
| Recibe datos después de salir | Cancelar Call y liberar Binding en onDestroyView. |
| Menú muy alto/hueco inferior | No dupliques padding de insets, fitsSystemWindows o altura fija. |
| Texto del campo se monta | Hint en TextInputLayout, no text duplicado como pista. |

## 23. Orden recomendado para aprender a reproducirlo

No copies todas las clases a la vez. Haz una práctica por bloques:

1. Portada + botón que abre una segunda Activity.
2. Segunda Activity + menú + tres fragments vacíos.
3. RecyclerView con dos objetos locales para entender Adapter.
4. Sustituir esos objetos por la API.
5. Click de fila + Bundle + detalle.
6. Búsqueda y validación.
7. Sensor y ciclo de vida.
8. Sin Internet, errores, rotación y limpieza.

En el paso 3 los objetos locales son un ejercicio; el proyecto terminado sí usa el servicio externo requerido.

Puedes explicar el recorrido de un dato así: el usuario pulsa un botón → Fragment solicita al repositorio → Retrofit recibe JSON → Gson construye objetos → repositorio prepara filas/detalle → LiveData notifica → Binding y Adapter muestran.

## 24. Qué no se implementó y por qué

- Sin base de datos/login: no son necesarios para practicar esta rúbrica.
- Sin Compose/Kotlin/DI: se mantiene Java/XML del curso.
- Sin almacenamiento permanente: los filtros viven en memoria.
- Sin paginación compleja: APIs de prueba pequeñas y enfoque en RecyclerView.
- Sin inventar datos cuando Internet falla: el error debe ser visible.
- Sin prometer el mismo enunciado mañana: estos modelos entrenan un patrón transferible.

El primer entregable de tu práctica personal debe ser que puedas explicar una búsqueda, una fila y un salto entre fragments antes de intentar memorizar todas las líneas.

## 25. Fuentes y uso como material de estudio

- Teorías locales de semanas 3–6 de tu carpeta Clases.
- Rúbrica actual de Practicas/Lab4 y laboratorio pasado IOT_LAB4_2026.pdf.
- [TheMealDB: API](https://www.themealdb.com/api.php).
- [DummyJSON: productos](https://dummyjson.com/docs/products).
- [DummyJSON: usuarios](https://dummyjson.com/docs/users).

Material de práctica elaborado con asistencia de IA. Si tu laboratorio permite esa asistencia y exige declararla, usa el formato que indique el profesor; esta guía no reemplaza sus reglas ni tu explicación personal.

