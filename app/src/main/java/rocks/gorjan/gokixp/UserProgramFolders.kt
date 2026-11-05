package rocks.gorjan.gokixp

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Folders the user makes in the Programs list. One level only: a folder holds programs, never
 * other folders, and a program is in at most one folder.
 *
 * Kept as a single JSON string in the launcher's own preferences, which is the file the
 * existing backup (PrefsBackup, local file and Google Drive) writes and restores - so folders
 * ride along in a backup with no code of their own. Nothing is cached: every call reads the
 * preferences, so a restore is picked up at once.
 */
class UserProgramFolders(private val prefs: SharedPreferences) {

    data class Folder(val name: String, val packages: List<String>)

    private val gson = Gson()

    fun all(): List<Folder> {
        val json = prefs.getString(KEY, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<Folder>>() {}.type
            val parsed: List<Folder?> = gson.fromJson(json, type) ?: emptyList()
            // A hand-edited or damaged value must not take the Programs list down with it
            parsed.filterNotNull().filter { !it.name.isNullOrBlank() }.map { Folder(it.name, (it.packages ?: emptyList()).distinct()) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    /** The name of the folder [packageName] is in, if any. */
    fun folderOf(packageName: String): String? = all().firstOrNull { packageName in it.packages }?.name

    /** Why [name] cannot be used, or null when it can. [except] is the folder being renamed. */
    fun problemWith(name: String, except: String? = null): Problem? {
        val clean = name.trim()
        if (clean.isEmpty() || clean.length > MAX_NAME) return Problem.BAD_NAME
        val taken = all().any { it.name.equals(clean, ignoreCase = true) && !it.name.equals(except, ignoreCase = true) }
        return if (taken || RESERVED.any { it.equals(clean, ignoreCase = true) }) Problem.TAKEN else null
    }

    enum class Problem { BAD_NAME, TAKEN }

    /** Makes an empty folder. Returns false when the name is not usable. */
    fun create(name: String): Boolean {
        if (problemWith(name) != null) return false
        save(all() + Folder(name.trim(), emptyList()))
        return true
    }

    fun rename(old: String, new: String): Boolean {
        if (problemWith(new, except = old) != null) return false
        save(all().map { if (it.name == old) it.copy(name = new.trim()) else it })
        return true
    }

    /** Deletes the folder; its programs go back to the plain Programs list. */
    fun delete(name: String) = save(all().filterNot { it.name == name })

    /** Puts [packageName] in [folder] (taking it out of any other), or just out of its folder when [folder] is null. */
    fun move(packageName: String, folder: String?) {
        val stripped = all().map { it.copy(packages = it.packages - packageName) }
        save(if (folder == null) stripped else stripped.map { if (it.name == folder) it.copy(packages = it.packages + packageName) else it })
    }

    /** [move] for several programs at once (the multi-select). */
    fun moveAll(packageNames: Collection<String>, folder: String?) {
        val gone = packageNames.toSet()
        val stripped = all().map { it.copy(packages = it.packages.filterNot { p -> p in gone }) }
        save(if (folder == null) stripped else stripped.map { if (it.name == folder) it.copy(packages = (it.packages + gone.filter { p -> p !in it.packages })) else it })
    }

    private fun save(folders: List<Folder>) {
        if (folders.isEmpty()) prefs.edit().remove(KEY).apply() else prefs.edit().putString(KEY, gson.toJson(folders)).apply()
    }

    companion object {
        const val KEY = "user_program_folders"
        const val MAX_NAME = 30

        // Names the Programs list already uses for its own folders
        private val RESERVED = listOf("Accessories", "Entertainment", "Games", "Private")
    }
}
