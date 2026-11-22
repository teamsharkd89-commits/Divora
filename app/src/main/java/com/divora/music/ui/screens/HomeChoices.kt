package com.divora.music.ui.screens

import com.divora.music.R
import com.divora.music.data.prefs.AppSettings
import com.divora.music.data.prefs.SettingsRepository
import com.divora.music.tr

/** The round buttons at the top of Home (1.8.0): Podcasts and Customize as one choice. */
enum class TopButtons(val label: String) {
    BOTH(tr(R.string.podcasts_and_customize)), PODCASTS(tr(R.string.podcasts)), CUSTOMIZE(tr(R.string.customize)), NONE(tr(R.string.none))
}

/** Home & Library's merged choices, over the settings Divora already keeps (homePodcastsButton, homeCustomizeTopButton). */
object HomeChoices {
    fun topButtons(s: AppSettings): TopButtons = when {
        s.home.podcastsButton && s.home.customizeTopButton -> TopButtons.BOTH
        s.home.podcastsButton -> TopButtons.PODCASTS
        s.home.customizeTopButton -> TopButtons.CUSTOMIZE
        else -> TopButtons.NONE
    }

    /** homePodcastsButton, homeCustomizeTopButton. */
    fun topButtonsValues(t: TopButtons): Pair<Boolean, Boolean> = when (t) {
        TopButtons.BOTH -> true to true
        TopButtons.PODCASTS -> true to false
        TopButtons.CUSTOMIZE -> false to true
        TopButtons.NONE -> false to false
    }

    fun setTopButtons(prefs: SettingsRepository, t: TopButtons) {
        val (podcasts, customize) = topButtonsValues(t)
        prefs.setHomePodcastsButton(podcasts)
        prefs.setHomeCustomizeTopButton(customize)
    }
}
