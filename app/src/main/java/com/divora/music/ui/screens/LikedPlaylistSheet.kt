package com.divora.music.ui.screens

import com.divora.music.R
import com.divora.music.tr
import com.divora.music.ui.components.DivoraButton
import com.divora.music.ui.components.DivoraTextButton
import com.divora.music.ui.theme.DivoraIcons
import com.divora.music.data.friendlyMessage
import android.text.format.DateUtils
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.divora.music.ui.LocalNavigator
import com.divora.music.ui.Routes
import com.divora.music.ui.components.ActionGroup
import com.divora.music.ui.components.ActionHeader
import com.divora.music.ui.components.ActionItem
import com.divora.music.ui.components.ActionSheet
import com.divora.music.ui.components.Segmented
import com.divora.music.ui.components.runAction
import com.divora.music.ui.theme.LocalAppSettings
import com.divora.music.ui.theme.LocalContainer
import kotlinx.coroutines.launch

/**
 * Liked songs as a real YouTube Music playlist that keeps itself up to date
 * ([com.divora.music.data.LikedPlaylist]): make it here, then open, refresh or stop it.
 */
@Composable
fun LikedPlaylistSheet(art: String?, onDismiss: () -> Unit) {
    val c = LocalContainer.current
    val nav = LocalNavigator.current
    val context = LocalContext.current
    val settings = LocalAppSettings.current
    // The app's scope: these outlive the sheet (some only start once it has slid away).
    val scope = remember { kotlinx.coroutines.CoroutineScope(c.scope.coroutineContext + kotlinx.coroutines.Dispatchers.Main) }
    val mirror = c.likedPlaylist
    val id by mirror.playlistId.collectAsState()
    val title by mirror.title.collectAsState()
    val syncing by mirror.syncing.collectAsState()
    val lastSynced by mirror.lastSynced.collectAsState()
    val error by mirror.lastError.collectAsState()
    val liked by c.library.liked.collectAsState()
    var name by remember { mutableStateOf(tr(R.string.my_liked_songs)) }
    var privacy by remember { mutableStateOf("PRIVATE") }
    var making by remember { mutableStateOf(false) }
    var confirmDelete by remember { mutableStateOf(false) }
    val scheme = MaterialTheme.colorScheme
    fun toast(text: String) = Toast.makeText(context, text, Toast.LENGTH_SHORT).show()

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text(tr(R.string.delete_the_playlist)) },
            text = { Text(tr(R.string.will_be_deleted_from_your_youtube_3, title ?: tr(R.string.the_playlist))) },
            confirmButton = {
                DivoraTextButton(onClick = {
                    confirmDelete = false
                    scope.launch { mirror.stop(delete = true).onFailure { toast(tr(R.string.couldn_t_delete_it, it.friendlyMessage())) } }
                }) { Text(tr(R.string.delete), color = scheme.error) }
            },
            dismissButton = { DivoraTextButton(onClick = { confirmDelete = false }) { Text(tr(R.string.cancel)) } },
        )
    }

    ActionSheet(art, onDismiss) { close ->
        ActionHeader(
            art, tr(R.string.liked_songs_playlist),
            if (id != null) tr(R.string.kept_up_to_date) else com.divora.music.data.model.songCount(liked.size),
            placeholderIcon = DivoraIcons.HeartFilled,
        )
        when {
            !settings.isLoggedIn -> Text(
                tr(R.string.sign_in_to_youtube_music_to),
                style = MaterialTheme.typography.bodyMedium, color = scheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 24.dp),
            )
            id == null -> Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    tr(R.string.makes_a_youtube_music_playlist_of_all),
                    style = MaterialTheme.typography.bodyMedium, color = scheme.onSurfaceVariant,
                )
                com.divora.music.ui.components.DivoraTextField(name, { name = it.take(150) }, label = { Text(tr(R.string.name)) }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Segmented(listOf("PRIVATE", "UNLISTED", "PUBLIC"), privacy, { when (it) { "PRIVATE" -> tr(R.string.private_text); "UNLISTED" -> tr(R.string.unlisted); else -> tr(R.string.public_text) } }) { privacy = it }
                DivoraButton(
                    onClick = {
                        making = true
                        scope.launch {
                            mirror.create(name.trim(), privacy)
                                .onSuccess { toast(tr(R.string.made_with, name.trim(), com.divora.music.data.model.songCount(liked.size))) }
                                .onFailure { toast(tr(R.string.couldn_t_make_the_playlist_2, it.friendlyMessage())) }
                            making = false
                        }
                    },
                    enabled = name.isNotBlank() && !making,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                ) {
                    if (making) {
                        CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp, color = scheme.onPrimary)
                        Spacer(Modifier.width(10.dp))
                        Text(tr(R.string.making_it))
                    } else Text(tr(R.string.make_playlist), fontWeight = FontWeight.Medium)
                }
            }
            else -> {
                Row(Modifier.padding(horizontal = 24.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (syncing) { com.divora.music.ui.components.DivoraSpinner(Modifier.size(14.dp), stroke = 2.dp); Spacer(Modifier.width(8.dp)) }
                    Text(
                        when {
                            syncing -> tr(R.string.updating)
                            error != null -> tr(R.string.last_update_failed, error)
                            lastSynced > 0 -> tr(R.string.updated, title ?: tr(R.string.playlist), DateUtils.getRelativeTimeSpanString(lastSynced, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS))
                            else -> "\"${title ?: "Playlist"}\""
                        },
                        style = MaterialTheme.typography.bodyMedium, color = if (error != null && !syncing) scheme.error else scheme.onSurfaceVariant,
                    )
                }
                ActionGroup(null, listOf(
                    ActionItem(DivoraIcons.PlaylistPlay, tr(R.string.open_the_playlist)) { nav.go(Routes.playlist(id!!)) },
                    ActionItem(DivoraIcons.Refresh, tr(R.string.update_now), keepOpen = true) { scope.launch { mirror.sync(full = true) } },
                )) { runAction(it, close) }
                ActionGroup(null, listOf(
                    ActionItem(DivoraIcons.SyncOff, tr(R.string.stop_updating_it)) { scope.launch { mirror.stop(delete = false); toast(tr(R.string.the_playlist_stays_but_won_t)) } },
                    ActionItem(DivoraIcons.Delete, tr(R.string.delete_the_playlist_2), danger = true, keepOpen = true) { confirmDelete = true },
                )) { runAction(it, close) }
            }
        }
    }
}
