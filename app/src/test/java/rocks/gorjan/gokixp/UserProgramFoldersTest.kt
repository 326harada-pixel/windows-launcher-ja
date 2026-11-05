package rocks.gorjan.gokixp

import android.content.SharedPreferences
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** A SharedPreferences that lives in a map, so the folder store and the backup can run on the JVM. */
class FakePrefs(initial: Map<String, Any?> = emptyMap()) : SharedPreferences {
    val map = initial.toMutableMap()

    override fun getAll(): MutableMap<String, *> = map.toMutableMap()
    override fun getString(key: String, defValue: String?): String? = map[key] as? String ?: defValue
    override fun getStringSet(key: String, defValues: MutableSet<String>?): MutableSet<String>? = defValues
    override fun getInt(key: String, defValue: Int): Int = map[key] as? Int ?: defValue
    override fun getLong(key: String, defValue: Long): Long = map[key] as? Long ?: defValue
    override fun getFloat(key: String, defValue: Float): Float = map[key] as? Float ?: defValue
    override fun getBoolean(key: String, defValue: Boolean): Boolean = map[key] as? Boolean ?: defValue
    override fun contains(key: String): Boolean = map.containsKey(key)
    override fun registerOnSharedPreferenceChangeListener(l: SharedPreferences.OnSharedPreferenceChangeListener?) {}
    override fun unregisterOnSharedPreferenceChangeListener(l: SharedPreferences.OnSharedPreferenceChangeListener?) {}

    override fun edit(): SharedPreferences.Editor = object : SharedPreferences.Editor {
        private val pending = mutableMapOf<String, Any?>()
        private val removed = mutableSetOf<String>()
        private var cleared = false
        override fun putString(key: String, value: String?) = apply { pending[key] = value }
        override fun putStringSet(key: String, values: MutableSet<String>?) = apply { pending[key] = values }
        override fun putInt(key: String, value: Int) = apply { pending[key] = value }
        override fun putLong(key: String, value: Long) = apply { pending[key] = value }
        override fun putFloat(key: String, value: Float) = apply { pending[key] = value }
        override fun putBoolean(key: String, value: Boolean) = apply { pending[key] = value }
        override fun remove(key: String) = apply { removed.add(key) }
        override fun clear() = apply { cleared = true }
        override fun commit(): Boolean { apply(); return true }
        override fun apply() {
            if (cleared) map.clear()
            removed.forEach { map.remove(it) }
            map.putAll(pending)
        }
    }
}

class UserProgramFoldersTest {

    private fun store(prefs: SharedPreferences = FakePrefs()) = UserProgramFolders(prefs)

    @Test
    fun `create, rename and delete a folder`() {
        val s = store()
        assertTrue(s.create("Work"))
        assertEquals(listOf("Work"), s.all().map { it.name })
        assertTrue(s.rename("Work", "Office"))
        assertEquals(listOf("Office"), s.all().map { it.name })
        s.delete("Office")
        assertTrue(s.all().isEmpty())
    }

    @Test
    fun `names are trimmed, bounded, unique ignoring case, and never one of the built-in folders`() {
        val s = store()
        assertTrue(s.create("  Work  "))
        assertEquals("Work", s.all().single().name)
        assertFalse(s.create("work"))
        assertFalse(s.create(""))
        assertFalse(s.create("   "))
        assertFalse(s.create("x".repeat(UserProgramFolders.MAX_NAME + 1)))
        assertFalse(s.create("Accessories"))
        assertFalse(s.create("games"))
        assertEquals(UserProgramFolders.Problem.TAKEN, s.problemWith("WORK"))
        // renaming a folder to its own name (different case) is allowed
        assertNull(s.problemWith("WORK", except = "Work"))
    }

    @Test
    fun `a program is in at most one folder and moving out leaves the folder`() {
        val s = store()
        s.create("A"); s.create("B")
        s.move("pkg.one", "A")
        s.move("pkg.one", "B")
        assertEquals("B", s.folderOf("pkg.one"))
        assertTrue(s.all().first { it.name == "A" }.packages.isEmpty())
        s.move("pkg.one", null)
        assertNull(s.folderOf("pkg.one"))
        assertEquals(setOf("A", "B"), s.all().map { it.name }.toSet())
    }

    @Test
    fun `several programs move into a folder at once and out again`() {
        val s = store()
        s.create("A"); s.create("B")
        s.move("pkg.one", "B")
        s.moveAll(listOf("pkg.one", "pkg.two", "pkg.three"), "A")
        assertEquals(setOf("pkg.one", "pkg.two", "pkg.three"), s.all().first { it.name == "A" }.packages.toSet())
        assertTrue(s.all().first { it.name == "B" }.packages.isEmpty())
        // moving the same ones again does not duplicate them
        s.moveAll(listOf("pkg.one", "pkg.two"), "A")
        assertEquals(3, s.all().first { it.name == "A" }.packages.size)
        s.moveAll(listOf("pkg.one", "pkg.two"), null)
        assertEquals(listOf("pkg.three"), s.all().first { it.name == "A" }.packages)
    }

    @Test
    fun `deleting a folder releases its programs`() {
        val s = store()
        s.create("A")
        s.move("pkg.one", "A")
        s.delete("A")
        assertNull(s.folderOf("pkg.one"))
    }

    @Test
    fun `a damaged stored value reads as no folders instead of throwing`() {
        for (bad in listOf("not json", "{}", "[null]", "[{\"name\":null}]", "[{\"name\":\"X\"}]")) {
            val prefs = FakePrefs(mapOf(UserProgramFolders.KEY to bad))
            val names = store(prefs).all().map { it.name }
            assertTrue(bad, names.isEmpty() || names == listOf("X"))
        }
    }

    @Test
    fun `folders are part of the existing backup and come back after a restore`() {
        val prefs = FakePrefs(mapOf("some_setting" to true))
        val s = store(prefs)
        s.create("Games2")
        s.move("pkg.one", "Games2")

        val json = PrefsBackup.toJson(prefs)
        assertTrue("the backup file carries the folders key", json.contains(UserProgramFolders.KEY))

        // a phone with no folders yet restores that backup
        val fresh = FakePrefs(mapOf("other" to 1))
        PrefsBackup.restore(fresh, json)
        val restored = store(fresh).all()
        assertEquals(listOf("Games2"), restored.map { it.name })
        assertEquals(listOf("pkg.one"), restored.single().packages)
        assertEquals(true, fresh.getBoolean("some_setting", false))
    }
}
