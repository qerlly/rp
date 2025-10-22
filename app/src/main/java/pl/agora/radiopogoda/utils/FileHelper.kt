package pl.agora.radiopogoda.utils

import java.nio.file.Files
import java.nio.file.Paths

object FileHelper {

    fun deleteFile(path: String): Boolean {
        return try {
            Files.delete(Paths.get(path))
            true
        } catch (e: NoSuchFileException) {
            e.printStackTrace()
            false
        } catch (e: Exception) {
           e.printStackTrace()
            false
        }
    }
}