package rocks.gorjan.gokixp

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/** The Japanese dictionary in res/values-ja is "English|Japanese" per item; a malformed or duplicated pair would silently drop labels. */
class LocalizationDictionaryTest {
    private val items: List<String> by lazy {
        val xml = File("src/main/res/values-ja/strings.xml").readText()
        val block = xml.substringAfter("<string-array name=\"translation_pairs\">").substringBefore("</string-array>")
        Regex("<item>(.*?)</item>").findAll(block).map { it.groupValues[1] }.toList()
    }

    @Test
    fun `every pair has an English key and a Japanese value`() {
        assertTrue(items.size > 300)
        for (line in items) {
            val i = line.indexOf('|')
            assertTrue("no separator: $line", i > 0)
            assertTrue("empty Japanese: $line", line.substring(i + 1).isNotBlank())
        }
    }

    @Test
    fun `no English key appears twice`() {
        val keys = items.map { it.substringBefore('|') }
        assertEquals(keys.toSet().size, keys.size)
    }

    @Test
    fun `the default values folder carries the same array empty, so other locales see no change`() {
        val xml = File("src/main/res/values/strings.xml").readText()
        assertTrue(xml.contains("<string-array name=\"translation_pairs\" />"))
    }
}
