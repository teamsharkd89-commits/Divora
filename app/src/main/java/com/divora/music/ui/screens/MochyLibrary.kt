package com.divora.music.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.geometry.Offset
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
import com.divora.music.data.model.PlaylistItem
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

/** Un dado dibujado (5 puntos): Divora no trae icono de dado, así que se dibuja a mano. */
@Composable
fun DiceIcon(modifier: Modifier = Modifier) {
    val pip = MaterialTheme.colorScheme.onPrimary
    Canvas(modifier) {
        val r = size.minDimension
        val o = r * 0.28f
        val c = center
        val pr = r * 0.09f
        drawCircle(pip, pr, Offset(c.x - o, c.y - o))
        drawCircle(pip, pr, Offset(c.x + o, c.y - o))
        drawCircle(pip, pr, Offset(c.x, c.y))
        drawCircle(pip, pr, Offset(c.x - o, c.y + o))
        drawCircle(pip, pr, Offset(c.x + o, c.y + o))
    }
}

/** El botón flotante del dado, esquina inferior derecha de la Biblioteca. */
@Composable
fun DiceFabButton(description: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    Box(
        modifier = modifier
            .size(56.dp)
            .shadow(8.dp, CircleShape)
            .clip(CircleShape)
            .background(scheme.primary)
            .clickable(onClick = onClick)
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center,
    ) {
        DiceIcon(Modifier.size(30.dp))
    }
}

/** Una fila de la ruleta: portada, título y subtítulo de una playlist. */
@Composable
private fun RouletteRow(item: PlaylistItem, highlighted: Boolean, modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    val tile = if (highlighted) 64.dp else 48.dp
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .then(if (highlighted) Modifier.border(2.dp, scheme.primary, RoundedCornerShape(14.dp)) else Modifier)
            .background(if (highlighted) scheme.surfaceContainerHighest else Color.Transparent)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Artwork(item.thumbnail, Modifier.size(tile), RoundedCornerShape(10.dp), size = 226)
        Column(Modifier.weight(1f).padding(start = 12.dp)) {
            Text(
                item.title,
                style = MaterialTheme.typography.bodyLarge,
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
}

/**
 * La ruleta jackpot del dado (1.0.5): a pantalla completa, las playlists guardadas de la
 * biblioteca desfilan en una ventanilla que va frenando poco a poco hasta caer en una al azar.
 * Mochy no tiene un dado ni una ruleta como tal: lo parecido que tiene es el shuffle (mezclar
 * y elegir al azar). Aquí la ruleta es propia de Divora y la ganadora se reproduce con el
 * sistema de Prism ([onPick]); [onDismiss] la cierra sin reproducir nada.
 */
@Composable
fun DiceRouletteOverlay(
    playlists: List<PlaylistItem>,
    onPick: (PlaylistItem) -> Unit,
    onDismiss: () -> Unit,
) {
    val scheme = MaterialTheme.colorScheme
    val winner = remember(playlists) { playlists.random() }
    // La cinta: varias vueltas barajadas y la ganadora al final, para que el frenado caiga en ella.
    // 32 pasos como mucho, da igual cuántas playlists haya guardadas.
    val strip = remember(playlists, winner) {
        val shuffled = playlists.shuffled() + playlists.shuffled()
        (shuffled.take(31) + winner)
    }
    var step by remember { mutableIntStateOf(0) }
    var landed by remember { mutableStateOf(false) }
    var gone by remember { mutableStateOf(false) }
    LaunchedEffect(strip) {
        var delayMs = 55L
        while (step < strip.lastIndex) {
            delay(delayMs)
            step++
            delayMs = (delayMs * 1.12).toLong().coerceAtMost(320L)
        }
        landed = true
        delay(1100)
        if (!gone) onPick(winner)
    }
    val scale by animateFloatAsState(if (landed) 1.06f else 1f, tween(300))
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.78f)),
        contentAlignment = Alignment.Center,
    ) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(tr(R.string.dice_title), color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(20.dp))
            // La ventanilla: la de arriba y la de abajo atenuadas, la del centro es la que manda.
            Column(Modifier.scale(scale)) {
                val prev = strip.getOrNull(step - 1)
                val current = strip[step]
                val next = strip.getOrNull(step + 1)
                if (prev != null) RouletteRow(prev, highlighted = false, Modifier.padding(bottom = 8.dp))
                RouletteRow(current, highlighted = true)
                if (next != null) RouletteRow(next, highlighted = false, Modifier.padding(top = 8.dp))
            }
            Spacer(Modifier.height(24.dp))
            if (!landed) {
                TextButton(onClick = { gone = true; onDismiss() }) {
                    Text(tr(R.string.cancel), color = Color.White)
                }
            }
        }
    }
}
