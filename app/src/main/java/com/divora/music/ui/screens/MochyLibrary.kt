package com.divora.music.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.divora.music.ui.components.Artwork

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
