# Divora — Changelog

## 1.0.7

- Novedades solo una vez y solo lo nuevo: el tour de bienvenida volvía a salir entero cada vez que se abría la app, porque sus páginas llevaban los números de versión viejos de Prism (39–67) y Divora cuenta desde 1; ahora cada novedad lleva la versión de Divora en la que salió, así que tras actualizar solo salen las de esa versión, una sola vez.
- La tragaperras ya no se cierra sola: con pocas playlists los rodillos se salían de su cinta y la app se cerraba a los pocos segundos de girar; la cinta ahora se repite hasta cubrir todo el giro y termina siempre en la ganadora.
- La tragaperras, con más espectáculo: los rodillos deslizan al girar, la palanca rebota al volver, y al ganar hay confeti, un destello dorado y el JACKPOT latiendo en dorado.
- El dado y el jackpot ya hablan español: "¿Qué suena hoy?", "¡Jackpot!" y los avisos, que antes salían en inglés.

## 1.0.6

- El dado de la Biblioteca ya es una imagen PNG sin fondo, mejor diseñada. Al tocarlo sale una máquina tragaperras a pantalla completa: baja la palanca, los tres rodillos giran con las playlists guardadas y se paran uno a uno en la misma ganadora (jackpot), con sonidos de rodillo y de premio; salen las canciones de la ganadora una a una y empieza a sonar.
- Actualización desde la app corregida: si el instalador no dejaba ninguna pantalla que reabrir, el botón se quedaba sin hacer nada en "Instalando"; ahora vuelve a intentar la instalación. Y si al buscar actualizaciones GitHub no contesta, la app ya no se cierra sin decir nada: avisa el motivo.

## 1.0.5

- Dado de la ruleta en la Biblioteca, esquina inferior derecha: al tocarlo se abre una animación formato ruleta jackpot a pantalla completa, con las playlists guardadas desfilando en una ventanilla que va frenando hasta caer en una al azar, y esa playlist empieza a sonar. Si la playlist ganadora está vacía, avisa y no suena nada. El dado solo aparece cuando hay playlists guardadas.

## 1.0.4

- Biblioteca nueva, diseño propio a partir de Mochy con el sistema de Prism: portada con pastillas Todo, Local, Descargas e Importado, tarjetas de Canciones, Playlists, Álbumes y Artistas con el número grande y dos portadas apiladas, y tarjetas anchas de Recién añadido y Me gusta. Historial, Descargas y Etiquetas siguen, como filas de la portada. Local muestra los archivos del teléfono, Descargas las canciones ya bajadas para escuchar sin internet, e Importado las importaciones y las playlists de Spotify que sigues. Al tocar una tarjeta se abre la lista de siempre de Prism, con su orden, su búsqueda y sus funciones; "‹ Biblioteca" vuelve a la portada.

## 1.0.3

- Nuevo logo: la manita estilo cartoon agarrando un rayo encendido, en blanco y negro (la variante B/N 3 que eligió Daniel). Reemplaza los dos rayos cruzados en el icono de la app, en la tele, en el README y en la cabecera de Configuración; el icono de notificaciones sigue siendo el rayito blanco solo, porque la manita a ese tamaño no se distinguiría.

## 1.0.2

- Configuración estilo Mochy: secciones limpias con solo el nombre y una descripción, filas con icono en círculo gris, sin colores de espectro ni cabeceras encimadas (en 1.0.1 la cabecera de cada sección caía encima de su primera fila).
- Divora ahora se firma con su propia llave en cada construcción, así las actualizaciones se instalan encima desde la app. Pasar de 1.0.1 a 1.0.2 pide una reinstalación guiada (copia, guardar el APK, desinstalar, instalar, restaurar) porque la 1.0.1 llevaba una llave de prueba; a partir de aquí ya no vuelve a pasar.

## 1.0.1

- Configuración rediseñada: cabecera con el logo y la versión, secciones con icono y descripción (Hazlo tuyo, Escucha, Tu música, Conexiones, App) y su propio apartado de actualizaciones con "Buscar actualizaciones".
- Al entrar a la app, si hay una actualización en GitHub, aparece un aviso para actualizar desde la app, sin ir a GitHub. La revisión ahora es cada 15 minutos.
- El APK del release se llama siempre Divora.apk, para que la actualización lo encuentre.

## 1.0.0

- Primera versión de Divora, basada en Prism, rebrandeada con nombre, paquete, icono y textos propios.
