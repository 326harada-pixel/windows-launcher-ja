package rocks.gorjan.gokixp

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.text.Spanned
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView

/**
 * Japanese for the labels the launcher draws from English literals (menus, dialogs, layout
 * text, program names). Everything funnels through one exact-match dictionary:
 *
 *  - [tr] for text built in code (menus, notifications, the Win* dialog widgets via WinCaption);
 *  - [translateTree] for ordinary TextViews, run (debounced) over the whole window so layout
 *    text and text set from code are covered without touching every call site.
 *
 * The pairs live in res/values-ja/strings.xml as "English|Japanese" items of
 * [R.array.translation_pairs]; res/values has the same array empty, so on a phone that is
 * not set to Japanese nothing changes. Edit boxes are never touched (that is the user's text).
 */
object L {
    @Volatile
    private var pairs: Map<String, String> = emptyMap()

    private val handler = Handler(Looper.getMainLooper())

    val isJa: Boolean get() = pairs.isNotEmpty()

    fun init(context: Context) {
        pairs = context.resources.getStringArray(R.array.translation_pairs)
            .mapNotNull { line ->
                val i = line.indexOf('|')
                if (i <= 0) null else line.substring(0, i) to line.substring(i + 1)
            }
            .toMap()
    }

    /** The Japanese for [text], or [text] itself when there is none. */
    fun tr(text: String): String = pairs[text] ?: text

    /** Keeps the window's text in Japanese: re-checks the whole tree shortly after it changes. */
    fun keepTranslated(root: View) {
        if (!isJa) return
        val run = Runnable { translateTree(root) }
        root.viewTreeObserver.addOnGlobalLayoutListener {
            handler.removeCallbacks(run)
            handler.postDelayed(run, 250)
        }
    }

    fun translateTree(view: View) {
        if (view is TextView && view !is EditText) {
            val text = view.text
            if (text != null && text.isNotEmpty() && text.length <= 300 && text !is Spanned) {
                pairs[text.toString()]?.let { view.text = it }
            }
            val hint = view.hint
            if (hint != null && hint.isNotEmpty() && hint.length <= 300) {
                pairs[hint.toString()]?.let { view.hint = it }
            }
        }
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) translateTree(view.getChildAt(i))
        }
    }
}
