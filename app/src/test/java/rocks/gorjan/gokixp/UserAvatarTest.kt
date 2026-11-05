package rocks.gorjan.gokixp

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class UserAvatarTest {
    @Test
    fun portraitPhotoIsCutToACentredSquare() {
        assertEquals(UserAvatar.Crop(0, 500, 3000), UserAvatar.squareCrop(3000, 4000))
    }

    @Test
    fun landscapePhotoIsCutToACentredSquare() {
        assertEquals(UserAvatar.Crop(500, 0, 3000), UserAvatar.squareCrop(4000, 3000))
    }

    @Test
    fun squareAndTinyImagesAreKeptWhole() {
        assertEquals(UserAvatar.Crop(0, 0, 64), UserAvatar.squareCrop(64, 64))
        assertEquals(UserAvatar.Crop(0, 0, 1), UserAvatar.squareCrop(1, 1))
    }

    @Test
    fun bigPhotosAreShrunkWhileDecodingButNeverBelowTheTarget() {
        assertEquals(1, UserAvatar.sampleSizeFor(200, 200))
        assertEquals(1, UserAvatar.sampleSizeFor(300, 3000))
        assertEquals(8, UserAvatar.sampleSizeFor(3000, 4000))
        // the shorter side stays at least 256 after shrinking
        assertTrue(3000 / UserAvatar.sampleSizeFor(3000, 4000) >= 256)
    }

    @Test
    fun storedPathsOnlyPointInsideTheAppsPictureFolder() {
        val files = File("/data/files")
        val path = UserAvatar.storedPath("user_1.png")
        assertEquals(File("/data/files/user_pictures/user_1.png"), UserAvatar.resolve(files, path))
        assertNull(UserAvatar.resolve(files, "xp_user_icons/a.png"))
        assertNull(UserAvatar.resolve(files, "file:user_pictures/../secret"))
        assertNull(UserAvatar.resolve(files, "file:user_pictures/a/b.png"))
        assertNull(UserAvatar.resolve(files, "file:user_pictures/"))
    }
}
