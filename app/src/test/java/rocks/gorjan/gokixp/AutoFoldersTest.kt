package rocks.gorjan.gokixp

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AutoFoldersTest {
    @Test
    fun `known apps land in the owner's folders`() {
        assertEquals(AutoFolders.SNS, AutoFolders.categoryOf("jp.naver.line.android"))
        assertEquals(AutoFolders.MANGA, AutoFolders.categoryOf("com.comic_fuz"))
        assertEquals(AutoFolders.VOD, AutoFolders.categoryOf("tv.abema"))
        assertEquals(AutoFolders.FOOD, AutoFolders.categoryOf("jp.co.mcdonalds.android"))
        assertEquals(AutoFolders.MONEY, AutoFolders.categoryOf("jp.ne.paypay.android.app"))
        assertEquals(AutoFolders.SHOP, AutoFolders.categoryOf("com.kouzoh.mercari"))
        assertEquals(AutoFolders.IMAGE, AutoFolders.categoryOf("com.canva.editor"))
        assertEquals(AutoFolders.HEALTH, AutoFolders.categoryOf("com.fitbit.FitbitMobile"))
        assertEquals(AutoFolders.GAME, AutoFolders.categoryOf("jp.pokemon.pokemontcgp"))
        assertEquals(AutoFolders.RACE, AutoFolders.categoryOf("jp.boatrace.android.boatraceapp"))
        assertEquals(AutoFolders.RACE, AutoFolders.categoryOf("jp.co.netdreamers.netkeiba"))
        assertEquals(AutoFolders.RACE, AutoFolders.categoryOf("com.example.keirin.app"))
    }

    @Test
    fun `a company's family, words in the id, then the declared category, in that order`() {
        assertEquals(AutoFolders.MONEY, AutoFolders.categoryOf("jp.co.japannetbank.smtapp.anything"))
        assertEquals(AutoFolders.MANGA, AutoFolders.categoryOf("com.example.mangareader"))
        assertEquals(AutoFolders.GAME, AutoFolders.categoryOf("com.example.unknown", declaredCategory = 0))
        assertEquals(AutoFolders.TOOL, AutoFolders.categoryOf("com.example.unknown", declaredCategory = 7))
        // an exact id beats what the app declares about itself
        assertEquals(AutoFolders.SNS, AutoFolders.categoryOf("com.twitter.android", declaredCategory = 7))
    }

    @Test
    fun `an unknown app is left alone`() {
        assertNull(AutoFolders.categoryOf("com.example.totally.unknown"))
        assertNull(AutoFolders.categoryOf("com.example.unknown", declaredCategory = -1))
    }

    @Test
    fun `every category the sorter can return is one of the twelve folders`() {
        val samples = listOf("tv.abema", "com.comic_fuz", "x.bank.y", "x.game.y", "jp.sammynet.next.ort.a0001")
        for (s in samples) assertTrue(AutoFolders.categoryOf(s) in AutoFolders.ALL)
        assertEquals(12, AutoFolders.ALL.size)
    }
}
