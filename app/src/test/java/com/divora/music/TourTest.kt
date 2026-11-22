package com.divora.music

import com.divora.music.ui.tour.Tour
import com.divora.music.ui.tour.TourStep
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The tour's rules: a new install (or the first release with tours) sees every step; someone who finished the tour at
 * a version sees only steps added since; and no step is dated after the build it ships in (it could never be marked
 * seen, and would come back at every start).
 *
 * Divora's own version line (1.0.x, versionCode 1..): everything inherited from Prism shipped together in Divora
 * 1.0.0, so those steps are all dated 1.0.0; Divora's own features are dated with the Divora release that brought
 * them (Biblioteca 1.0.4, the dice's slot machine 1.0.6, its redesign 1.0.7).
 */
// Robolectric: the tour's words come from strings.xml (1.7.9).
@org.junit.runner.RunWith(org.robolectric.RobolectricTestRunner::class)
@org.robolectric.annotation.Config(sdk = [34], application = android.app.Application::class)
class TourTest {
    @Test
    fun newInstallsSeeTheWholeTour() {
        assertEquals(Tour.steps, Tour.stepsAfter(0))
        assertTrue(Tour.isFullTour(0))
    }

    @Test
    fun aTourSeenAtThisVersionShowsNothingMore() {
        assertTrue(Tour.stepsAfter(BuildConfig.VERSION_CODE).isEmpty())
    }

    @Test
    fun updatesSeeOnlyNewerSteps() {
        for (seen in listOf(1, 4, 7)) {
            assertTrue(Tour.stepsAfter(seen).all { it.since > seen })
            assertEquals(Tour.steps.count { it.since > seen }, Tour.stepsAfter(seen).size)
        }
    }

    /** The cards with the support line (1.0.0's last card) that are not the last of their release's cards. */
    private fun supportMisplaced(steps: List<TourStep>): List<String> = steps.withIndex()
        .filter { (i, s) -> s.support && steps.drop(i + 1).any { it.since == s.since } }
        .map { (_, s) -> "${s.since}: ${s.title}" }

    /**
     * 1.8.7.6 (plan-1.8.7.6 §4.1 S6, D5): the line asking to support Divora sits only on the last card of a release's
     * cards (so a release asks once, at its end).
     */
    @Test
    fun theSupportLineOnlyEndsARelease() {
        assertEquals(emptyList<String>(), supportMisplaced(Tour.steps))
        // The rule catches a line put on an earlier card of a release.
        val icon = com.divora.music.ui.theme.DivoraIcons.Heart
        val wrong = listOf(TourStep(70, icon, "a", "b", support = true), TourStep(70, icon, "c", "d"))
        assertEquals(listOf("70: a"), supportMisplaced(wrong))
        assertEquals(emptyList<String>(), supportMisplaced(listOf(TourStep(70, icon, "a", "b"), TourStep(70, icon, "c", "d", support = true), TourStep(71, icon, "e", "f", support = true))))
    }

    @Test
    fun stepsAreDatedNoLaterThanThisBuild() {
        assertTrue(Tour.steps.all { it.since in 1..BuildConfig.VERSION_CODE })
        assertTrue("every step says something", Tour.steps.all { it.title.isNotBlank() && it.body.isNotBlank() })
    }

    /** 1.0.4: someone updating from 1.0.3 sees the new Library home, and nothing older. */
    @Test
    fun anUpdateFrom103SeesThe104LibraryPage() {
        val pages = Tour.stepsAfter(4).filter { it.since <= 5 }
        assertEquals(1, pages.size)
        val page = pages.single()
        assertEquals(5, page.since)
        assertEquals(tr(R.string.tour_104_library_title), page.title)
        assertTrue(page.points.size in 3..5 && page.points.all { it.isNotBlank() })
        assertTrue((page.points + page.body).none { "%" in it })
        assertEquals(com.divora.music.ui.Routes.LIBRARY, page.showMe)
    }

    /** 1.0.6: someone updating from 1.0.5 sees the dice's slot machine, and nothing older. */
    @Test
    fun anUpdateFrom105SeesThe106SlotPage() {
        val pages = Tour.stepsAfter(6).filter { it.since <= 7 }
        assertEquals(1, pages.size)
        val page = pages.single()
        assertEquals(7, page.since)
        assertEquals(tr(R.string.tour_106_slot_title), page.title)
        assertTrue(page.points.size in 3..5 && page.points.all { it.isNotBlank() })
        assertTrue((page.points + page.body).none { "%" in it })
        assertEquals(com.divora.music.ui.Routes.LIBRARY, page.showMe)
    }

    /** 1.0.7: someone updating from 1.0.6 sees only the slot machine's redesign, once. */
    @Test
    fun anUpdateFrom106SeesOnlyThe107Page() {
        val pages = Tour.stepsAfter(7)
        assertEquals(listOf(8), pages.map { it.since })
        assertEquals(tr(R.string.tour_107_slot_title), pages.single().title)
        assertTrue(pages.single().points.size in 3..5)
        assertEquals(com.divora.music.ui.Routes.LIBRARY, pages.single().showMe)
    }

    /** 1.0.7: someone updating from 1.0.3 sees the new pages in order, and nothing older. */
    @Test
    fun anUpdateFrom103SeesTheNewPagesInOrder() {
        assertEquals(listOf(5, 7, 8), Tour.stepsAfter(4).map { it.since })
    }

    /**
     * Every "Show me" opens a page that exists: a settings page by a key Settings resolves (1.8.4's page keys and the
     * old ones), or one of the app's routes.
     */
    @Test
    fun everyShowMeOpensARealPage() {
        val routes = setOf(
            com.divora.music.ui.Routes.SETTINGS, com.divora.music.ui.Routes.SEARCH, com.divora.music.ui.Routes.CATALOGUE,
            com.divora.music.ui.Routes.BRING_HISTORY, com.divora.music.ui.Routes.LIBRARY, com.divora.music.ui.Routes.librarySearch(),
            com.divora.music.ui.Routes.REPLAY,
        )
        for (route in Tour.steps.mapNotNull { it.showMe }) {
            if (route.startsWith("settings/")) {
                val (key, find) = parse(route)
                assertNotNull("$route: no settings page \"$key\"", com.divora.music.ui.screens.settings.SettingsMap.resolve(key, find))
            } else assertTrue("$route isn't a page the tour knows", route in routes)
        }
    }

    /** `settings/<key>?find=<group>` → its key and group, as the app's route reads it. */
    private fun parse(route: String): Pair<String, String?> {
        val key = route.removePrefix("settings/").substringBefore('?')
        val find = route.substringAfter("?find=", "").takeIf { it.isNotBlank() }?.let { android.net.Uri.decode(it) }
        return key to find
    }
}
