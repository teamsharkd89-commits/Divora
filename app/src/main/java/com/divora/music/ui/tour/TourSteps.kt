package com.divora.music.ui.tour

import com.divora.music.R
import com.divora.music.tr
import android.content.Context
import androidx.compose.ui.graphics.vector.ImageVector
import com.divora.music.BuildConfig
import com.divora.music.ui.Routes
import com.divora.music.ui.screens.settings.SettingsPageId
import com.divora.music.ui.theme.DivoraIcons

/**
 * One page of Divora's tour.
 *
 * [since] is the versionCode of the release the feature (or its current look) came in. A new install sees every step
 * (the full tour); someone updating sees only the steps newer than the last tour they saw ([Tour.seenVersion]). To
 * introduce a later release's features: add steps with that release's versionCode, and keep the older steps true to
 * the app (new installs still see all of them).
 */
data class TourStep(
    val since: Int,
    val icon: ImageVector,
    val title: String,
    val body: String,
    val points: List<String> = emptyList(),
    /** Small icons beside the big one, for what the step covers. */
    val extras: List<ImageVector> = emptyList(),
    /** "Show me": where it opens (the tour closes; it can be taken again from Settings → About). */
    val showMe: String? = null,
    val kind: Kind = Kind.PLAIN,
    /**
     * 1.8.7.6 (plan-1.8.7.6 §4.1 S6, D5): the card ends with one line asking to support Divora, shown once per release and
     * never with "I've supported Divora" on ([com.divora.music.ui.support.SupportWhatsNewLine]). Only on the last card of
     * a release's cards (`TourTest`).
     */
    val support: Boolean = false,
) {
    enum class Kind { PLAIN, NOTIFICATIONS }
}

object Tour {
    /**
     * Divora's own version line (1.0.x): [TourStep.since] is the versionCode of the Divora release the feature came
     * in. Everything Divora inherited from Prism's 1.7–1.8.7.6 shipped together in Divora 1.0.0, so all of those steps
     * are [V1_0_0]. (Prism's old build numbers 39–67 are gone on purpose: Divora's versionCode restarted at 1, so a
     * step dated 39 could never be marked seen — [Tour.markSeen] writes the Divora versionCode, and the tour came
     * back in full at every start.)
     */
    private const val V1_0_0 = 1
    /** Divora 1.0.4: the new Library home, Mochy-style with Prism's system. */
    private const val V1_0_4 = 5
    /** Divora 1.0.6: the dice opens a slot machine that picks a playlist at random. */
    private const val V1_0_6 = 7
    /** Divora 1.0.7: the slot machine, fixed and with more show. */
    private const val V1_0_7 = 8

    /** Every step, in the order they're shown. */
    val steps: List<TourStep> = listOf(
        TourStep(
            V1_0_0, DivoraIcons.Home, tr(R.string.home),
            tr(R.string.your_starting_point_made_from_what),
            listOf(
                tr(R.string.the_replay_card_at_the_top),
                tr(R.string.quick_picks_songs_around_the_artists),
                tr(R.string.made_for_you_daily_mixes_your),
                tr(R.string.the_grid_button_at_the_top),
                tr(R.string.continue_picks_up_the_album_you),
            ),
            listOf(DivoraIcons.StarFilled, DivoraIcons.Customize),
        ),
        TourStep(
            V1_0_0, DivoraIcons.PlayCircle, tr(R.string.the_player),
            tr(R.string.tap_the_mini_player_or_swipe),
            listOf(
                tr(R.string.best_part_jumps_to_where_listeners),
                tr(R.string.double_tap_the_cover_s_sides),
                tr(R.string.hold_previous_or_next_to_seek),
                tr(R.string.has_the_sleep_timer_stats_downloads),
            ),
            listOf(DivoraIcons.Flame, DivoraIcons.Moon),
        ),
        TourStep(
            V1_0_0, DivoraIcons.Lyrics, tr(R.string.lyrics),
            tr(R.string.open_them_from_the_button_under),
            listOf(
                tr(R.string.tap_a_line_to_jump_there),
                tr(R.string.their_source_is_at_the_bottom),
                tr(R.string.translate_them_or_read_other_scripts),
            ),
            listOf(DivoraIcons.Translate, DivoraIcons.Romanize),
        ),
        TourStep(
            // Simpler in 1.7.8: one button.
            V1_0_0, DivoraIcons.Mic, tr(R.string.sing),
            tr(R.string.karaoke_with_the_song_s_own),
            listOf(
                tr(R.string.with_the_lyrics_open_tap_the),
                tr(R.string.hold_it_and_slide_for_how),
                tr(R.string.move_the_key_up_or_down),
            ),
        ),
        TourStep(
            V1_0_0, DivoraIcons.Equalizer, tr(R.string.dj_mode_and_crossfade),
            tr(R.string.let_divora_mix_your_music_songs),
            listOf(
                tr(R.string.in_the_queue_tap_dj_then),
                tr(R.string.long_press_an_album_or_playlist),
                tr(R.string.crossfade_settings_playback_sound_blends_any),
            ),
            listOf(DivoraIcons.Queue, DivoraIcons.Shuffle),
        ),
        TourStep(
            V1_0_0, DivoraIcons.StarFilled, tr(R.string.made_for_you),
            tr(R.string.mixes_made_on_your_phone_from),
            listOf(
                tr(R.string.daily_mixes_group_the_artists_you),
                tr(R.string.release_radar_finds_new_music_from),
                tr(R.string.made_for_you_also_has_mixes),
                tr(R.string.when_the_queue_ends_divora_s),
                tr(R.string.tap_shuffle_twice_on_a_playlist),
                tr(R.string.not_for_me_and_leave_out),
            ),
            listOf(DivoraIcons.SmartShuffle, DivoraIcons.Block),
        ),
        TourStep(
            V1_0_0, DivoraIcons.Search, tr(R.string.search_and_recognize),
            tr(R.string.find_anything_in_divora_music_or),
            listOf(
                tr(R.string.filters_for_songs_videos_albums_artists),
                tr(R.string.on_this_phone_searches_your_own),
                tr(R.string.the_waveform_button_in_the_search),
                tr(R.string.add_the_recognize_widget_or_quick),
                tr(R.string.podcasts_the_button_at_the_top),
            ),
            listOf(DivoraIcons.Recognize),
        ),
        TourStep(
            V1_0_0, DivoraIcons.LibraryFilled, tr(R.string.your_library),
            tr(R.string.everything_you_keep_with_or_without),
            listOf(
                tr(R.string.liked_songs_downloads_and_the_music),
                tr(R.string.make_playlists_and_save_albums_without),
                tr(R.string.a_divora_account_settings_at_the),
                tr(R.string.blend_makes_a_playlist_of_two),
                tr(R.string.listen_together_plays_in_step_with),
            ),
            listOf(DivoraIcons.Blend, DivoraIcons.Groups),
        ),
        TourStep(
            V1_0_0, DivoraIcons.Stats, tr(R.string.replay_and_rewind),
            tr(R.string.your_listening_recapped_on_your_phone),
            listOf(
                tr(R.string.replay_your_top_songs_artists_and),
                tr(R.string.rewind_your_year_in_music_as),
                tr(R.string.share_any_slide_as_a_picture),
            ),
            listOf(DivoraIcons.History),
        ),
        TourStep(
            V1_0_0, DivoraIcons.Cast, tr(R.string.divora_connect),
            tr(R.string.music_beyond_your_phone),
            listOf(
                tr(R.string.play_on_speakers_tvs_and_chromecasts),
                tr(R.string.control_divora_from_a_browser_on),
                tr(R.string.hand_what_s_playing_to_another),
            ),
            listOf(DivoraIcons.Tv, DivoraIcons.Handoff),
            showMe = Routes.settings("connect"),
        ),
        TourStep(
            V1_0_0, DivoraIcons.Palette, tr(R.string.make_it_yours),
            tr(R.string.divora_takes_its_color_from_what),
            listOf(
                tr(R.string.appearance_the_spectrum_s_colors_or),
                tr(R.string.layout_the_tab_bar_your_tabs),
                tr(R.string.player_its_background_the_cover_and),
                tr(R.string.search_settings_from_the_box_at),
            ),
            listOf(DivoraIcons.Customize, DivoraIcons.Settings),
            showMe = Routes.settings("appearance"),
        ),
        TourStep(
            V1_0_0, DivoraIcons.Palette, tr(R.string.color_your_way),
            tr(R.string.settings_customize_divora_color_every_choice),
            listOf(
                tr(R.string.the_light_12_presets_white_black),
                tr(R.string.your_accent_any_color_from_a),
                tr(R.string.the_page_background_plain_tinted_with),
                tr(R.string.each_group_says_everything_it_changes),
            ),
            listOf(DivoraIcons.Customize),
            showMe = Routes.settings("color"),
        ),
        TourStep(
            V1_0_0, DivoraIcons.LibraryFilled, tr(R.string.also_new),
            tr(R.string.smaller_things_that_make_divora_yours),
            listOf(
                tr(R.string.hold_divora_s_icon_for_search),
                tr(R.string.library_pick_its_tiles_or_put),
                tr(R.string.pin_divora_playlists_to_home_customize),
                tr(R.string.song_credits_in_any_song_s),
                tr(R.string.see_which_downloads_failed_and_why),
            ),
            listOf(DivoraIcons.Edit),
        ),
        TourStep(
            V1_0_0, DivoraIcons.Looks, tr(R.string.looks),
            tr(R.string.settings_customize_divora_looks_your_whole),
            listOf(
                tr(R.string.try_divora_s_own_paper_midnight),
                tr(R.string.save_yours_update_it_when_you),
                tr(R.string.browse_looks_people_shared_under_from),
                tr(R.string.one_look_by_day_and_another),
                tr(R.string.every_look_page_shows_home_the),
                tr(R.string.customize_by_tapping_hold_the_settings),
            ),
            listOf(DivoraIcons.Sun, DivoraIcons.Moon, DivoraIcons.Undo),
            showMe = Routes.settings("looks"),
        ),
        TourStep(
            V1_0_0, DivoraIcons.PlayCircle, tr(R.string.the_player_your_way),
            tr(R.string.settings_customize_divora_player_or_player),
            listOf(
                tr(R.string.layouts_standard_a_big_cover_lyrics),
                tr(R.string.a_visualizer_if_you_d_like),
                tr(R.string.pick_the_buttons_under_the_controls),
                tr(R.string.put_the_lyric_line_progress_bar),
                tr(R.string.a_waveform_progress_bar_its_color),
                tr(R.string.choose_what_swipes_a_double_tap),
            ),
            listOf(DivoraIcons.Turntable, DivoraIcons.Customize),
            showMe = Routes.settings("player"),
        ),
        TourStep(
            V1_0_0, DivoraIcons.Speed, tr(R.string.faster),
            tr(R.string.divora_scrolls_searches_and_opens_the),
            listOf(
                tr(R.string.divora_tells_android_which_of_its),
                tr(R.string.less_work_while_lists_scroll_while),
                tr(R.string.if_something_still_stutters_settings_backup),
            ),
        ),
        TourStep(
            V1_0_0, DivoraIcons.Lyrics, tr(R.string.lyrics_your_way),
            tr(R.string.settings_customize_divora_lyrics_how_your),
            listOf(
                tr(R.string.six_styles_divora_classic_karaoke_bold),
                tr(R.string.the_sung_line_and_the_others),
                tr(R.string.weight_line_spacing_how_dim_the),
                tr(R.string.the_sung_line_near_the_top),
                tr(R.string.translations_over_or_under_each_line),
                tr(R.string.a_background_of_their_own_plain),
            ),
            listOf(DivoraIcons.Lyrics, DivoraIcons.Palette),
            showMe = Routes.settings("lyrics_look"),
        ),
        TourStep(
            V1_0_0, DivoraIcons.History, tr(R.string.also_new),
            tr(R.string.a_few_things_you_asked_for),
            listOf(
                tr(R.string.library_history_what_you_played_by),
                tr(R.string.liked_a_song_and_its_video),
                tr(R.string.divora_s_own_volume_apart_from),
                tr(R.string.hide_the_status_bar_customize_divora),
                tr(R.string.settings_pages_are_tidier_tap_beside),
                tr(R.string.library_s_tiles_the_edit_button),
            ),
            listOf(DivoraIcons.History, DivoraIcons.HeartFilled),
        ),
        TourStep(
            V1_0_0, DivoraIcons.Home, tr(R.string.home_your_way),
            tr(R.string.customize_home_the_button_at_the),
            listOf(
                tr(R.string.hold_any_album_playlist_artist_or),
                tr(R.string.sections_from_your_own_listening_liked),
                tr(R.string.rename_any_section_leave_youtube_music),
                tr(R.string.the_top_of_home_the_greeting),
                tr(R.string.home_shows_at_once_when_divora),
            ),
            listOf(DivoraIcons.Home, DivoraIcons.Pin),
            showMe = Routes.CATALOGUE,
        ),
        TourStep(
            V1_0_0, DivoraIcons.Library, tr(R.string.library_and_tabs_your_way),
            tr(R.string.folders_pins_and_tabs_of_your),
            listOf(
                tr(R.string.hold_a_playlist_move_to_a),
                tr(R.string.playlists_albums_and_artists_each_a),
                tr(R.string.make_a_cover_for_a_playlist),
                tr(R.string.keep_youtube_playlists_and_copy_them),
                tr(R.string.tabs_downloads_liked_songs_or_podcasts),
            ),
            listOf(DivoraIcons.Folder, DivoraIcons.Library),
            showMe = Routes.settings("interface"),
        ),
        TourStep(
            V1_0_0, DivoraIcons.Search, tr(R.string.search_redesigned),
            tr(R.string.search_starts_from_your_music),
            listOf(
                tr(R.string.type_a_line_of_a_song),
                tr(R.string.what_you_opened_lately_your_artists),
                tr(R.string.artists_and_songs_with_their_pictures),
                tr(R.string.results_already_in_your_library_right),
            ),
            listOf(DivoraIcons.Search, DivoraIcons.Recognize),
            showMe = Routes.SEARCH,
        ),
        TourStep(
            V1_0_0, DivoraIcons.Settings, tr(R.string.settings_easier_to_find),
            tr(R.string.settings_are_sorted_by_what_they),
            listOf(
                tr(R.string.everything_about_how_divora_looks_is),
                tr(R.string.gestures_and_buttons_in_controls_gestures),
                tr(R.string.every_mobile_data_choice_on_one),
                tr(R.string.android_auto_speakers_discord_and_last),
                tr(R.string.performance_mode_at_the_top_for),
            ),
            listOf(DivoraIcons.Settings, DivoraIcons.Speed),
            showMe = Routes.SETTINGS,
        ),
        TourStep(
            V1_0_0, DivoraIcons.Category, tr(R.string.shapes_icons_and_type),
            tr(R.string.make_the_pieces_of_divora_yours),
            listOf(
                tr(R.string.covers_as_circles_squircles_flowers_arches),
                tr(R.string.corners_on_a_slider_and_pill),
                tr(R.string.icons_in_outline_duotone_or_filled),
                tr(R.string.bolder_or_lighter_text_wider_spacing),
                tr(R.string.each_home_screen_widget_with_its),
            ),
            listOf(DivoraIcons.Category, DivoraIcons.Signature),
            showMe = Routes.settings("icons"),
        ),
        TourStep(
            V1_0_0, DivoraIcons.QueueMusic, tr(R.string.also_new),
            tr(R.string.from_your_requests),
            listOf(
                tr(R.string.rearrange_your_playlists_songs_rearrange_songs),
                tr(R.string.swipe_a_song_out_of_the),
                tr(R.string.the_light_from_the_playing_cover),
                tr(R.string.ribbons_a_progress_bar_with_strands),
                tr(R.string.a_bold_title_on_the_player),
            ),
            listOf(DivoraIcons.SwapVertical, DivoraIcons.Palette),
        ),
        TourStep(
            V1_0_0, DivoraIcons.Equalizer, tr(R.string.sound_your_way),
            tr(R.string.settings_playback_sound),
            listOf(
                tr(R.string.speed_pitch_slowed_sped_up_or),
                tr(R.string.how_songs_cross_in_a_crossfade),
                tr(R.string.normalize_volume_s_level_and_albums),
                tr(R.string.your_own_equalizer_presets_shared_as),
                tr(R.string.fades_for_the_sleep_timer_and),
            ),
            listOf(DivoraIcons.Speed, DivoraIcons.Equalizer),
            showMe = Routes.settings("sound"),
        ),
        TourStep(
            V1_0_0, DivoraIcons.Looks, tr(R.string.classic_divora),
            tr(R.string.liked_how_divora_1_6_looked),
            listOf(
                tr(R.string.the_classic_look_customize_divora_looks),
                tr(R.string.or_only_its_icons_its_bold),
                tr(R.string.the_player_s_buttons_apart_instead),
            ),
            listOf(DivoraIcons.Looks, DivoraIcons.Category),
            showMe = Routes.settings("looks"),
        ),
        TourStep(
            V1_0_0, DivoraIcons.QueueMusic, tr(R.string.also_new),
            tr(R.string.from_your_requests),
            listOf(
                tr(R.string.last_fm_scrobbling_devices_connections_and),
                tr(R.string.pick_several_songs_in_the_queue),
                tr(R.string.choose_what_goes_in_a_backup),
                tr(R.string.swipe_up_on_the_player_for),
                tr(R.string.lyrics_into_your_m4a_files_and),
            ),
            listOf(DivoraIcons.Queue, DivoraIcons.Calendar),
        ),
        TourStep(
            V1_0_0, DivoraIcons.Globe, tr(R.string.tour_179_language),
            tr(R.string.tour_179_language_body),
            listOf(tr(R.string.tour_179_language_youtube)),
            showMe = Routes.settings("language"),
        ),
        TourStep(
            V1_0_0, DivoraIcons.QueueMusic, tr(R.string.also_new),
            tr(R.string.from_your_requests),
            listOf(
                tr(R.string.tour_179_versions),
                tr(R.string.tour_179_playlists),
                tr(R.string.search_in_local_files),
                tr(R.string.tour_179_lyrics_button, tr(R.string.sing)),
                tr(R.string.tour_179_widget),
            ),
            listOf(DivoraIcons.Lyrics, DivoraIcons.Palette),
        ),
        TourStep(
            V1_0_0, DivoraIcons.QueueMusic, tr(R.string.also_new),
            tr(R.string.from_your_requests),
            listOf(
                tr(R.string.tour_1792_search_chips),
                tr(R.string.tour_1792_small_player),
                tr(R.string.tour_1792_slide),
                tr(R.string.tour_1792_gestures),
                tr(R.string.tour_1792_colors),
            ),
            listOf(DivoraIcons.Search, DivoraIcons.Palette),
        ),
        TourStep(
            V1_0_0, DivoraIcons.Tv, tr(R.string.tour_180_tv),
            tr(R.string.tour_180_tv_body),
            listOf(
                tr(R.string.tour_180_tv_phone),
                tr(R.string.tour_180_tv_get, com.divora.music.connect.TvInstall.DOWNLOADER_CODE),
                tr(R.string.tour_180_tv_big),
            ),
            listOf(DivoraIcons.Tv, DivoraIcons.Handoff),
            showMe = Routes.settings("connect"),
        ),
        TourStep(
            V1_0_0, DivoraIcons.Settings, tr(R.string.tour_180_settings),
            tr(R.string.tour_180_settings_body),
            listOf(
                tr(R.string.tour_180_settings_merged),
                tr(R.string.tour_180_settings_search),
                tr(R.string.tour_180_settings_tablet),
            ),
            listOf(DivoraIcons.Settings, DivoraIcons.Search),
            showMe = Routes.settings("sound"),
        ),
        TourStep(
            V1_0_0, DivoraIcons.PlayCircle, tr(R.string.tour_180_player),
            tr(R.string.tour_180_player_body),
            listOf(tr(R.string.tour_180_player_parts), tr(R.string.tour_180_player_menu)),
            listOf(DivoraIcons.PlayCircle, DivoraIcons.Edit),
            showMe = Routes.settings("player"),
        ),
        TourStep(
            V1_0_0, DivoraIcons.Lyrics, tr(R.string.tour_180_lyrics),
            tr(R.string.tour_180_lyrics_body),
            listOf(tr(R.string.tour_180_lyrics_start), tr(R.string.tour_180_lyrics_smooth)),
            listOf(DivoraIcons.Lyrics, DivoraIcons.Palette),
            showMe = Routes.settings("lyrics_look"),
        ),
        TourStep(
            V1_0_0, DivoraIcons.QueueMusic, tr(R.string.also_new),
            tr(R.string.from_your_requests),
            listOf(
                tr(R.string.tour_180_desk),
                tr(R.string.tour_180_crossfade),
                tr(R.string.tour_180_silence),
                tr(R.string.tour_180_normalize),
                tr(R.string.tour_180_size),
            ),
            listOf(DivoraIcons.Moon, DivoraIcons.Equalizer),
        ),
        TourStep(
            V1_0_0, DivoraIcons.History, tr(R.string.tour_181_history),
            tr(R.string.tour_181_history_body),
            listOf(
                tr(R.string.tour_181_history_spotify),
                tr(R.string.tour_181_history_others),
                tr(R.string.tour_181_history_replay),
            ),
            listOf(DivoraIcons.Download, DivoraIcons.Stats),
            showMe = Routes.BRING_HISTORY,
        ),
        TourStep(
            V1_0_0, DivoraIcons.Library, tr(R.string.tour_181_spotify),
            tr(R.string.tour_181_spotify_body),
            listOf(
                tr(R.string.tour_181_spotify_lists),
                tr(R.string.tour_181_spotify_plays),
                tr(R.string.tour_181_spotify_likes),
            ),
            listOf(DivoraIcons.Sync, DivoraIcons.HeartFilled),
            showMe = Routes.settings("spotify"),
        ),
        TourStep(
            // Opens Recommendations & Replay at its History group (pause, what counts as a play).
            V1_0_0, DivoraIcons.Stats, tr(R.string.tour_181_replay),
            tr(R.string.tour_181_replay_body),
            listOf(
                tr(R.string.tour_181_replay_pause),
                tr(R.string.tour_181_replay_counts),
                tr(R.string.tour_181_replay_graphs),
            ),
            listOf(DivoraIcons.Pause, DivoraIcons.History),
            showMe = Routes.settings("library", find = tr(R.string.hist_title)),
        ),
        TourStep(
            V1_0_0, DivoraIcons.Palette, tr(R.string.tour_181_yours),
            tr(R.string.tour_181_yours_body),
            listOf(
                tr(R.string.tour_181_yours_cover),
                tr(R.string.tour_181_yours_explicit),
                tr(R.string.tour_181_yours_white),
                tr(R.string.tour_181_yours_fonts),
                tr(R.string.tour_181_yours_artists),
            ),
            listOf(DivoraIcons.Photo, DivoraIcons.Signature),
            showMe = Routes.settings("appearance"),
        ),
        TourStep(
            V1_0_0, DivoraIcons.QueueMusic, tr(R.string.also_new),
            tr(R.string.from_your_requests),
            listOf(
                tr(R.string.tour_181_files),
                tr(R.string.tour_181_crossfade),
                tr(R.string.tour_181_auto),
                tr(R.string.tour_181_tv),
            ),
            listOf(DivoraIcons.Car, DivoraIcons.Tv),
        ),
        TourStep(
            V1_0_0, DivoraIcons.Lock, tr(R.string.tour_182_lock_title),
            tr(R.string.tour_182_lock_body),
            listOf(tr(R.string.tour_182_lock_point_1), tr(R.string.tour_182_lock_point_2), tr(R.string.tour_182_lock_point_3)),
            showMe = Routes.settings("player", find = tr(R.string.lock_screen_title)),
        ),
        TourStep(
            V1_0_0, DivoraIcons.Radio, tr(R.string.tour_182_discover_title),
            tr(R.string.tour_182_discover_body),
            listOf(
                tr(R.string.tour_182_discover_new),
                tr(R.string.tour_182_discover_deep),
                tr(R.string.tour_182_discover_day),
                tr(R.string.tour_182_discover_tune),
            ),
            showMe = Routes.settings("library", find = tr(R.string.tune_radios)),
        ),
        TourStep(
            V1_0_0, DivoraIcons.QueueMusic, tr(R.string.also_new),
            tr(R.string.from_your_requests),
            listOf(
                tr(R.string.tour_182_also_shows),
                tr(R.string.tour_182_also_offline),
                tr(R.string.tour_182_also_pages),
                tr(R.string.tour_182_also_files),
                tr(R.string.tour_182_also_crossfade),
            ),
        ),
        TourStep(
            V1_0_0, DivoraIcons.SmartShuffle, tr(R.string.tour_183_smart_title),
            tr(R.string.tour_183_smart_body),
            listOf(tr(R.string.tour_183_smart_point_1), tr(R.string.tour_183_smart_point_2), tr(R.string.tour_183_smart_point_3)),
            showMe = Routes.LIBRARY,
        ),
        TourStep(
            V1_0_0, DivoraIcons.Tag, tr(R.string.tour_183_tags_title),
            tr(R.string.tour_183_tags_body),
            listOf(tr(R.string.tour_183_tags_point_1), tr(R.string.tour_183_tags_point_2), tr(R.string.tour_183_tags_point_3)),
        ),
        TourStep(
            V1_0_0, DivoraIcons.CheckCircle, tr(R.string.tour_183_select_title),
            tr(R.string.tour_183_select_body),
            listOf(tr(R.string.tour_183_select_point_1), tr(R.string.tour_183_select_point_2)),
        ),
        TourStep(
            V1_0_0, DivoraIcons.Search, tr(R.string.tour_183_search_title),
            tr(R.string.tour_183_search_body),
            listOf(tr(R.string.tour_183_search_point_1), tr(R.string.tour_183_search_point_2), tr(R.string.tour_183_search_point_3)),
            showMe = Routes.librarySearch(),
        ),
        TourStep(
            V1_0_0, DivoraIcons.QueueMusic, tr(R.string.also_new),
            tr(R.string.from_your_requests),
            listOf(
                tr(R.string.tour_183_also_file),
                tr(R.string.tour_183_also_duplicates),
                tr(R.string.tour_183_also_songs_from),
                tr(R.string.tour_183_also_lock),
                tr(R.string.tour_183_also_uploads),
            ),
        ),
        TourStep(
            // Opens Settings' new first page (plan §5).
            V1_0_0, DivoraIcons.Settings, tr(R.string.tour_184_settings_title),
            tr(R.string.tour_184_settings_body),
            listOf(
                tr(R.string.tour_184_settings_point_1),
                tr(R.string.tour_184_settings_point_2),
                tr(R.string.tour_184_settings_point_3),
                tr(R.string.tour_184_settings_point_4),
                tr(R.string.tour_184_settings_point_5),
            ),
            listOf(DivoraIcons.Search, DivoraIcons.Speed),
            showMe = Routes.SETTINGS,
        ),
        TourStep(
            // #404, with #392 and the car's Long lines. Opens Lyrics & Sing at the group with "Lyrics earlier / later".
            V1_0_0, DivoraIcons.Lyrics, tr(R.string.tour_184_lyrics_title),
            tr(R.string.tour_184_lyrics_body),
            listOf(tr(R.string.tour_184_lyrics_point_1), tr(R.string.tour_184_lyrics_point_2), tr(R.string.tour_184_lyrics_point_3)),
            listOf(DivoraIcons.Timer, DivoraIcons.Car),
            showMe = Routes.settings(SettingsPageId.LYRICS_SING.key, find = tr(R.string.ls_sources_and_timing)),
        ),
        TourStep(
            // D53. Opens Player at Modes, where the lock screen and its options are.
            V1_0_0, DivoraIcons.Lock, tr(R.string.tour_184_lock_title),
            tr(R.string.tour_184_lock_body),
            listOf(
                tr(R.string.tour_184_lock_point_1, tr(R.string.settings_player_same_look), tr(R.string.settings_player_lock_options)),
                tr(R.string.tour_184_lock_point_2),
                tr(R.string.tour_184_lock_point_3),
            ),
            listOf(DivoraIcons.PlayCircle, DivoraIcons.HeartFilled),
            showMe = Routes.settings(SettingsPageId.PLAYER.key, find = tr(R.string.settings_player_modes)),
        ),
        TourStep(
            // D54, D55, D56 and #406. Opens Home & Library at Lists, with "Button to find the playing song".
            V1_0_0, DivoraIcons.QueueMusic, tr(R.string.tour_184_lists_title),
            tr(R.string.tour_184_lists_body),
            listOf(
                tr(R.string.tour_184_lists_point_1, tr(R.string.go_to_the_playing_song)),
                tr(R.string.tour_184_lists_point_2, tr(R.string.hl_locate_button)),
                tr(R.string.tour_184_lists_point_3),
                tr(R.string.tour_184_lists_point_4),
                tr(R.string.tour_184_lists_point_5),
            ),
            listOf(DivoraIcons.Album, DivoraIcons.Library),
            showMe = Routes.settings(SettingsPageId.HOME_LIBRARY.key, find = tr(R.string.hl_lists)),
        ),
        TourStep(
            // D57 + #416, D58/D59, #413, and the fixes #432 and #429.
            V1_0_0, DivoraIcons.QueueMusic, tr(R.string.also_new),
            tr(R.string.from_your_requests),
            listOf(
                tr(R.string.tour_184_also_shuffle),
                tr(R.string.tour_184_also_replay),
                tr(R.string.tour_184_also_color),
                tr(R.string.tour_184_also_crossfade),
                tr(R.string.tour_184_also_glass),
            ),
            listOf(DivoraIcons.Shuffle, DivoraIcons.Stats),
        ),
        TourStep(
            // The listening clock, the calendar, milestones and the monthly recap. Opens Replay.
            V1_0_0, DivoraIcons.Stats, tr(R.string.tour_185_replay_title),
            tr(R.string.tour_185_replay_body),
            listOf(
                tr(R.string.tour_185_replay_point_1),
                tr(R.string.tour_185_replay_point_2),
                tr(R.string.tour_185_replay_point_3),
                tr(R.string.tour_185_replay_point_4),
            ),
            listOf(DivoraIcons.Timer, DivoraIcons.StarFilled),
            showMe = Routes.REPLAY,
        ),
        TourStep(
            V1_0_0, DivoraIcons.ReplayFilled, tr(R.string.tour_185_rewind_title),
            tr(R.string.tour_185_rewind_body),
            listOf(tr(R.string.tour_185_rewind_point_1), tr(R.string.tour_185_rewind_point_2), tr(R.string.tour_185_rewind_point_3)),
        ),
        TourStep(
            // The widgets, the two notices and Finish the lyric. Opens Recommendations & history at Replay, where the switches are.
            V1_0_0, DivoraIcons.Update, tr(R.string.tour_185_more_title),
            tr(R.string.tour_185_more_body),
            listOf(
                tr(R.string.tour_185_more_point_1),
                tr(R.string.tour_185_more_point_2, tr(R.string.replay_notices)),
                tr(R.string.tour_185_more_point_3, tr(R.string.finish_lyric_title)),
            ),
            listOf(DivoraIcons.Lyrics, DivoraIcons.Home),
            showMe = Routes.settings(SettingsPageId.RECOMMENDATIONS_HISTORY.key, find = tr(R.string.replay_2)),
        ),
        TourStep(
            // #469, #477, face-down pause, #374, #481 and Open with Divora.
            V1_0_0, DivoraIcons.QueueMusic, tr(R.string.also_new),
            tr(R.string.from_your_requests),
            listOf(
                tr(R.string.tour_185_also_history),
                tr(R.string.tour_185_also_search),
                tr(R.string.tour_185_also_face_down, tr(R.string.playback_face_down_pause)),
                tr(R.string.tour_185_also_dj),
                tr(R.string.tour_185_also_open_with),
            ),
            listOf(DivoraIcons.Search, DivoraIcons.Shuffle),
        ),
        TourStep(
            // 1.8.6 Canvas. Opens the Player page at Cover, the group the Canvas row is in.
            V1_0_0, DivoraIcons.AnimatedCover, tr(R.string.tour_186_canvas_title),
            tr(R.string.tour_186_canvas_about),
            listOf(
                tr(R.string.tour_186_canvas_point_1),
                // Owner, 2026-11-13: Canvas (the loops from music videos) and Animated covers are two settings.
                tr(R.string.tour_186_canvas_two, tr(R.string.canvas), tr(R.string.animated_covers)),
                tr(R.string.tour_186_canvas_point_3, tr(R.string.canvas_pick_video), tr(R.string.canvas_pick_part)),
                tr(R.string.tour_186_canvas_point_4),
            ),
            listOf(DivoraIcons.Video, DivoraIcons.Fullscreen),
            showMe = Routes.settings(SettingsPageId.PLAYER.key, find = tr(R.string.cover)),
        ),
        TourStep(
            // The hours on Replay's "Hours by month" bars (the owner's ask, 2026-11-13).
            V1_0_0, DivoraIcons.Stats, tr(R.string.also_new),
            tr(R.string.from_your_requests),
            listOf(tr(R.string.tour_186_also_hours, tr(R.string.hours_by_month))),
            showMe = Routes.REPLAY,
        ),
        TourStep(
            // 1.8.7 It just plays. Opens Backup & about at Diagnostics, where "Is playback working?" is.
            V1_0_0, DivoraIcons.Play, tr(R.string.tour_187_plays_title),
            tr(R.string.tour_187_plays_about),
            listOf(
                tr(R.string.tour_187_plays_point_1),
                tr(R.string.tour_187_plays_point_2),
                tr(R.string.tour_187_plays_point_3),
                tr(R.string.tour_187_plays_point_4, tr(R.string.selftest_title), tr(R.string.diagnostics)),
            ),
            listOf(DivoraIcons.Check, DivoraIcons.Info),
            showMe = Routes.settings(SettingsPageId.BACKUP_ABOUT.key, find = tr(R.string.diagnostics)),
        ),
        TourStep(
            // Opens the Player page at Cover: "Keep lyrics showing" is in the Canvas row's sheet there.
            V1_0_0, DivoraIcons.Lyrics, tr(R.string.also_new),
            tr(R.string.tour_187_also_about),
            listOf(
                tr(R.string.tour_187_also_lyrics_line, tr(R.string.canvas_lyrics)),
                tr(R.string.tour_187_also_lyrics_right),
                tr(R.string.tour_187_also_blend, tr(R.string.crossfade)),
                tr(R.string.tour_187_also_lighter),
            ),
            listOf(DivoraIcons.Shuffle, DivoraIcons.Speed),
            showMe = Routes.settings(SettingsPageId.PLAYER.key, find = tr(R.string.cover)),
        ),
        TourStep(
            // 1.8.7.5 Your Spotify. Opens Accounts & sharing at Spotify, where the Connect sheet's two cards are.
            V1_0_0, DivoraIcons.Link, tr(R.string.tour_1875_spotify_title),
            tr(R.string.tour_1875_spotify_about),
            listOf(
                tr(R.string.tour_1875_spotify_point_1),
                tr(R.string.tour_1875_spotify_point_2),
                tr(R.string.tour_1875_spotify_point_3),
                tr(R.string.tour_1875_spotify_point_4),
            ),
            listOf(DivoraIcons.Heart, DivoraIcons.QueueMusic),
            showMe = Routes.settings(SettingsPageId.ACCOUNTS_SHARING.key, find = tr(R.string.spotify)),
        ),
        TourStep(
            V1_0_0, DivoraIcons.Check, tr(R.string.also_new),
            tr(R.string.from_your_requests),
            listOf(
                tr(R.string.tour_1875_also_queue),
                tr(R.string.tour_1875_also_home),
                tr(R.string.tour_1875_also_busy),
                tr(R.string.tour_1875_also_heard),
            ),
        ),
        TourStep(
            V1_0_0, DivoraIcons.PlayCircle, tr(R.string.tour_1876_canvas_title),
            tr(R.string.tour_1876_canvas_about),
            listOf(
                tr(R.string.tour_1876_canvas_point_1),
                tr(R.string.tour_1876_canvas_point_2),
                tr(R.string.tour_1876_canvas_point_3),
            ),
            // The Canvas switch and "Spotify's canvas first" under it are in the Player page's Cover group.
            showMe = Routes.settings(SettingsPageId.PLAYER.key, find = tr(R.string.cover)),
        ),
        TourStep(
            V1_0_0, DivoraIcons.Check, tr(R.string.also_new),
            tr(R.string.from_your_requests),
            listOf(
                tr(R.string.tour_1876_also_loop),
                tr(R.string.tour_1876_also_eq),
                tr(R.string.tour_1876_also_kids),
                tr(R.string.tour_1876_also_cast),
                tr(R.string.tour_1876_also_history),
            ),
        ),
        TourStep(
            V1_0_0, DivoraIcons.Update, tr(R.string.stay_in_the_loop),
            tr(R.string.let_divora_tell_you_when_something),
            listOf(
                tr(R.string.download_progress),
                tr(R.string.new_releases_from_artists_you_star),
                tr(R.string.your_rewind_ready_on_december_1),
            ),
            kind = TourStep.Kind.NOTIFICATIONS,
            // The release's last card carries the one line about supporting Divora (plan-1.8.7.6 §4.1 S6).
            support = true,
        ),
        // Divora 1.0.4: the Library home, Mochy-style with Prism's system.
        TourStep(
            V1_0_4, DivoraIcons.Library, tr(R.string.tour_104_library_title),
            tr(R.string.tour_104_library_body),
            listOf(
                tr(R.string.tour_104_library_point_1),
                tr(R.string.tour_104_library_point_2),
                tr(R.string.tour_104_library_point_3),
            ),
            showMe = Routes.LIBRARY,
        ),
        // Divora 1.0.6: the dice opens a slot machine that picks a playlist at random.
        TourStep(
            V1_0_6, DivoraIcons.Shuffle, tr(R.string.tour_106_slot_title),
            tr(R.string.tour_106_slot_body),
            listOf(
                tr(R.string.tour_106_slot_point_1),
                tr(R.string.tour_106_slot_point_2),
                tr(R.string.tour_106_slot_point_3),
            ),
            showMe = Routes.LIBRARY,
        ),
        // Divora 1.0.7: the slot machine, fixed and with more show.
        TourStep(
            V1_0_7, DivoraIcons.Star, tr(R.string.tour_107_slot_title),
            tr(R.string.tour_107_slot_body),
            listOf(
                tr(R.string.tour_107_slot_point_1),
                tr(R.string.tour_107_slot_point_2),
                tr(R.string.tour_107_slot_point_3),
            ),
            showMe = Routes.LIBRARY,
        ),
    )

    private fun prefs(context: Context) = context.getSharedPreferences("tour", Context.MODE_PRIVATE)

    /** The versionCode of the last tour finished or skipped; 0 for none (a new install, or one from before tours). */
    fun seenVersion(context: Context): Int = prefs(context).getInt("seen_version", 0)

    /** The tour is done for this version: later only newer steps are shown. */
    fun markSeen(context: Context) = prefs(context).edit().putInt("seen_version", BuildConfig.VERSION_CODE).apply()

    /** The steps someone who last saw the tour at [seen] hasn't seen: all of them for 0. */
    fun stepsAfter(seen: Int): List<TourStep> = steps.filter { it.since > seen }

    /** Whether it's the whole tour (never seen one) or only what's new since the last one. */
    fun isFullTour(seen: Int) = seen == 0
}
