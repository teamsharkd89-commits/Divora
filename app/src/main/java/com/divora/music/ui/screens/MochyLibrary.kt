package com.divora.music.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.divora.music.R
import com.divora.music.data.model.BrowseItem
import com.divora.music.tr
import com.divora.music.ui.components.Artwork
import kotlinx.coroutines.delay

/**
 * Biblioteca de Divora, diseño propio a partir de Mochy y Prism.
 *
 * Mochy aporta la estructura y el diseño: pastillas Todo / Local / Descargas / Importado,
 * tarjetas de categoría de 78 dp con el número grande y dos portadas apiladas en la esquina,
 * y tarjetas anchas de 104 dp para Recién añadido, Me gusta, Historial, Descargas y Etiquetas.
 * Prism aporta el sistema: los datos, las listas, el orden, la búsqueda y las rutas de Divora
 * siguen siendo los de siempre, solo cambia cómo se presentan. Nada de Prism se borra:
 * Historial, Descargas y Etiquetas viven como filas de la portada, con su contador y su
 * pantalla completa al tocarlas.
 *
 * Los colores no se copian tal cual de Mochy (sus grises duros romperían el tema claro de
 * Divora): se usan los colores del tema de Divora, con la misma jerarquía que en Mochy
 * (fondo de tarjeta, texto principal, texto secundario).
 */

/** Las pastillas de arriba, estilo Mochy: una pista oscura y la elegida un paso más clara. */
@Composable
fun MochyLibPills(labels: List<String>, selected: Int, onSelect: (Int) -> Unit, modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    Row(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(percent = 50))
            .background(scheme.surfaceContainerHigh)
            .padding(3.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        labels.forEachIndexed { i, label ->
            val background by animateColorAsState(
                targetValue = if (i == selected) scheme.surfaceContainerHighest else Color.Transparent,
                animationSpec = tween(220),
                label = "mochyLibPill",
            )
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(percent = 50))
                    .background(background)
                    .clickable { onSelect(i) }
                    .heightIn(min = 28.dp)
                    .padding(horizontal = 14.dp, vertical = 5.dp),
            ) {
                Text(
                    text = label,
                    color = if (i == selected) scheme.onSurface else scheme.onSurface.copy(alpha = 0.72f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

/** Título de sección, 20 sp en negrita, como en Mochy. */
@Composable
fun MochySectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.onSurface,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier.padding(horizontal = 16.dp),
    )
}

/**
 * Dos portadas reales, abanicadas en la esquina inferior derecha de una tarjeta.
 * La de atrás, atenuada y girada al otro lado, para que el par se lea como una pila.
 * No dibuja nada cuando no hay portadas: una categoría vacía deja la esquina libre,
 * no muestra cuadrados de relleno.
 */
@Composable
private fun MochyCoverStack(covers: List<String?>, tile: Dp = 46.dp, round: Boolean = false, modifier: Modifier = Modifier) {
    val shown = covers.filterNotNull().take(2)
    if (shown.isEmpty()) return
    val back = shown.getOrNull(1)
    val corner = if (round) tile / 2 else tile * 0.22f
    val shape = if (round) CircleShape else RoundedCornerShape(corner)
    Box(modifier = modifier.size(width = tile * 1.55f, height = tile)) {
        if (back != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(tile)
                    .graphicsLayer { alpha = 0.55f; rotationZ = if (round) 0f else -9f },
            ) {
                Artwork(back, Modifier.fillMaxWidth().height(tile), shape, size = 226)
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(tile)
                .graphicsLayer { rotationZ = if (round) 0f else 7f },
        ) {
            Artwork(shown[0], Modifier.fillMaxWidth().height(tile), shape, size = 226)
        }
    }
}

/** Una tarjeta de categoría: Canciones, Playlists, Álbumes o Artistas. */
@Composable
fun MochyCategoryCard(
    title: String,
    count: Int,
    covers: List<String?>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    roundCovers: Boolean = false,
) {
    val scheme = MaterialTheme.colorScheme
    Box(
        modifier = modifier
            .height(78.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(scheme.surfaceContainerHigh)
            .clickable(onClick = onClick),
    ) {
        MochyCoverStack(
            covers = covers,
            round = roundCovers,
            modifier = Modifier.align(Alignment.BottomEnd).offset(x = 10.dp, y = 12.dp),
        )
        Column(
            modifier = Modifier.align(Alignment.TopStart).padding(start = 14.dp, top = 11.dp, end = 12.dp),
        ) {
            Text(
                text = title,
                color = scheme.onSurface,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = count.toString(),
                color = scheme.onSurfaceVariant,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
            )
        }
    }
}

/** Una tarjeta ancha: Recién añadido, Me gusta, Historial, Descargas o Etiquetas. */
@Composable
fun MochyWideCard(
    title: String,
    count: Int,
    covers: List<String?>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scheme = MaterialTheme.colorScheme
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(104.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(scheme.surfaceContainerHigh)
            .clickable(onClick = onClick),
    ) {
        MochyCoverStack(
            covers = covers,
            tile = 60.dp,
            modifier = Modifier.align(Alignment.BottomEnd).offset(x = 10.dp, y = 14.dp),
        )
        Column(
            modifier = Modifier.align(Alignment.BottomStart).padding(start = 14.dp, bottom = 14.dp, end = 110.dp),
        ) {
            Text(
                text = title,
                color = scheme.onSurface,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = count.toString(),
                color = scheme.onSurfaceVariant,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
            )
        }
    }
}

/** Una fila de la portada que abre una página: título, subtítulo y flecha implícita al tocar. */
@Composable
fun MochyHomeRow(title: String, subtitle: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(scheme.surfaceContainerHigh)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = scheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Spacer(Modifier.width(8.dp))
        Text("›", color = scheme.onSurfaceVariant, fontSize = 22.sp)
    }
}

/** El botón flotante del dado, esquina inferior derecha de la Biblioteca: un PNG sin fondo. */
@Composable
fun DiceFabButton(description: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    Box(
        modifier = modifier
            .size(60.dp)
            .shadow(8.dp, CircleShape)
            .clip(CircleShape)
            .background(scheme.primary)
            .clickable(onClick = onClick)
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center,
    ) {
        androidx.compose.foundation.Image(
            androidx.compose.ui.res.painterResource(R.drawable.dice),
            contentDescription = null,
            modifier = Modifier.size(42.dp),
        )
    }
}

/** Una carta de rodillo: portada arriba, título y subtítulo debajo. */
@Composable
private fun SlotReelCard(item: BrowseItem, highlighted: Boolean, modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    Column(
        modifier = modifier
            .width(96.dp)
            .clip(RoundedCornerShape(14.dp))
            .then(if (highlighted) Modifier.border(2.dp, scheme.primary, RoundedCornerShape(14.dp)) else Modifier)
            .background(if (highlighted) scheme.surfaceContainerHighest else scheme.surfaceContainerHigh)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Artwork(item.thumbnail, Modifier.size(80.dp), RoundedCornerShape(10.dp), size = 226)
        Spacer(Modifier.height(6.dp))
        Text(
            item.title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (highlighted) FontWeight.Bold else FontWeight.Medium,
            color = scheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            item.subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = scheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/** La palanca de la tragaperras: bola arriba, brazo abajo, gira al bajarla. */
@Composable
private fun SlotLever(pulled: Boolean, modifier: Modifier = Modifier) {
    val angle by animateFloatAsState(if (pulled) 52f else 0f, tween(380))
    Box(modifier.size(48.dp, 190.dp), contentAlignment = Alignment.TopCenter) {
        // El riel por el que baja la palanca.
        Box(
            Modifier
                .size(10.dp, 150.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHighest),
        )
        // Brazo + bola, con el giro en la base.
        Column(
            Modifier
                .graphicsLayer {
                    rotationZ = angle
                    transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0.5f, 1f)
                },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE5484D)),
            )
            Box(
                Modifier
                    .size(8.dp, 128.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.onSurfaceVariant),
            )
        }
    }
}

/**
 * La tragaperras del dado (1.0.6): a pantalla completa, al abrirse baja la palanca y los tres
 * rodillos giran con las playlists guardadas de la biblioteca; cada rodillo se para en su
 * momento, los tres en la misma playlist ganadora (jackpot), salen sus canciones y empieza
 * a sonar. Mochy no tiene un dado ni una tragaperras como tal: lo parecido que tiene es el
 * shuffle (mezclar y elegir al azar). La máquina es propia de Divora y la ganadora se
 * reproduce con el sistema de Prism ([onPlay]); [onDismiss] la cierra sin reproducir nada.
 */
@Composable
fun DiceRouletteOverlay(
    playlists: List<BrowseItem>,
    loadSongs: suspend (BrowseItem) -> List<com.divora.music.data.model.Song>,
    onPlay: (BrowseItem, List<com.divora.music.data.model.Song>) -> Unit,
    onDismiss: () -> Unit,
) {
    val scheme = MaterialTheme.colorScheme
    val winner = remember(playlists) { playlists.random() }
    // Cada rodillo tiene su propia cinta, que termina en la ganadora, y se para en su momento.
    val steps = listOf(22, 28, 34)
    val strips = remember(playlists, winner) {
        steps.map { n ->
            val shuffled = playlists.shuffled() + playlists.shuffled()
            (shuffled.take(n) + winner)
        }
    }
    // Sonido sin archivos: pitidos del generador de tonos del sistema (rodillos y jackpot).
    val tones = remember {
        runCatching { android.media.ToneGenerator(android.media.AudioManager.STREAM_MUSIC, 50) }.getOrNull()
    }
    androidx.compose.runtime.DisposableEffect(tones) { onDispose { tones?.release() } }
    val lastTick = remember { androidx.compose.runtime.mutableLongStateOf(0L) }
    fun tick() {
        val now = android.os.SystemClock.uptimeMillis()
        if (now - lastTick.longValue < 45) return
        lastTick.longValue = now
        tones?.startTone(android.media.ToneGenerator.TONE_PROP_BEEP, 40)
    }
    fun clunk() = tones?.startTone(android.media.ToneGenerator.TONE_DTMF_5, 90)
    var leverDown by remember { mutableStateOf(false) }
    val reelStep = listOf(remember { mutableIntStateOf(0) }, remember { mutableIntStateOf(0) }, remember { mutableIntStateOf(0) })
    val reelStopped = listOf(remember { mutableStateOf(false) }, remember { mutableStateOf(false) }, remember { mutableStateOf(false) })
    val allStopped = reelStopped.all { it.value }
    var winSongs by remember { mutableStateOf<List<com.divora.music.data.model.Song>?>(null) }
    var shownSongs by remember { mutableIntStateOf(0) }
    var gone by remember { mutableStateOf(false) }
    // La palanca baja al abrir la máquina y los rodillos empiezan a girar.
    LaunchedEffect(Unit) {
        delay(350)
        leverDown = true
    }
    // Cada rodillo gira y se para en su momento, cada vez más despacio.
    steps.forEachIndexed { i, n ->
        LaunchedEffect(strips[i]) {
            delay(500L + i * 120L)
            var delayMs = 45L
            while (reelStep[i].intValue < n) {
                delay(delayMs)
                reelStep[i].intValue++
                tick()
                delayMs = (delayMs * 1.09).toLong().coerceAtMost(260L)
            }
            reelStopped[i].value = true
            clunk()
        }
    }
    // Al pararse los tres: jackpot, se cargan las canciones de la ganadora y salen una a una.
    LaunchedEffect(allStopped) {
        if (!allStopped) return@LaunchedEffect
        leverDown = false
        tones?.startTone(android.media.ToneGenerator.TONE_DTMF_1, 120)
        delay(140)
        tones?.startTone(android.media.ToneGenerator.TONE_DTMF_3, 120)
        delay(140)
        tones?.startTone(android.media.ToneGenerator.TONE_DTMF_5, 120)
        delay(140)
        tones?.startTone(android.media.ToneGenerator.TONE_DTMF_8, 320)
        winSongs = runCatching { loadSongs(winner) }.getOrDefault(emptyList())
        if (winSongs.isNullOrEmpty()) {
            delay(1200)
            if (!gone) onDismiss()
            return@LaunchedEffect
        }
        repeat(minOf(3, winSongs!!.size)) {
            delay(280)
            shownSongs++
        }
        delay(1000)
        if (!gone) onPlay(winner, winSongs!!)
    }
    val infinite = rememberInfiniteTransition()
    val flashAlpha by infinite.animateFloat(
        0.45f, 1f,
        infiniteRepeatable(tween(450), RepeatMode.Reverse),
        label = "jackpot",
    )
    val winScale by animateFloatAsState(if (allStopped) 1.05f else 1f, tween(300))
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.82f)),
        contentAlignment = Alignment.Center,
    ) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(tr(R.string.dice_title), color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(18.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Los tres rodillos en su marco, con el borde dorado parpadeando al ganar.
                Row(
                    Modifier
                        .border(2.dp, scheme.primary.copy(alpha = if (allStopped) flashAlpha else 0.9f), RoundedCornerShape(18.dp))
                        .background(scheme.surfaceContainerLow, RoundedCornerShape(18.dp))
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    strips.forEachIndexed { i, strip ->
                        SlotReelCard(
                            strip[reelStep[i].intValue],
                            highlighted = reelStopped[i].value,
                            Modifier.scale(if (allStopped) winScale else 1f),
                        )
                    }
                }
                Spacer(Modifier.width(6.dp))
                SlotLever(leverDown)
            }
            Spacer(Modifier.height(14.dp))
            if (allStopped) {
                Text(tr(R.string.dice_jackpot), color = Color(0xFFFFC94D), fontSize = 26.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
            }
            // Las canciones de la ganadora salen una a una debajo de los rodillos.
            val songs = winSongs
            if (songs != null && songs.isNotEmpty()) {
                Column(Modifier.fillMaxWidth().padding(horizontal = 12.dp)) {
                    songs.take(shownSongs).forEach { s ->
                        Row(Modifier.fillMaxWidth().padding(vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) {
                            Artwork(s.thumbnail, Modifier.size(36.dp), RoundedCornerShape(8.dp), size = 226)
                            Column(Modifier.weight(1f).padding(start = 10.dp)) {
                                Text(s.title, color = Color.White, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(s.primaryArtist, color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
                    }
                }
            } else if (allStopped && songs != null) {
                Text(tr(R.string.dice_empty_playlist), color = Color.White, style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(Modifier.height(10.dp))
            if (!allStopped) {
                TextButton(onClick = { gone = true; onDismiss() }) {
                    Text(tr(R.string.cancel), color = Color.White)
                }
            }
        }
    }
}
