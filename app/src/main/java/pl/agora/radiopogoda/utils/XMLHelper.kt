package pl.agora.radiopogoda.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.w3c.dom.Document
import org.w3c.dom.Node
import org.xml.sax.InputSource
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.StringReader
import java.net.HttpURLConnection
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory

object XMLHelper {

    suspend fun fetchXmlFromUrl(urlString: String, contentType: String?): String? = withContext(Dispatchers.IO) {
        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"

        if (contentType != null) {
            connection.setRequestProperty("Content-Type", contentType)
        }

        val responseCode = connection.responseCode

        if (responseCode == HttpURLConnection.HTTP_OK) {
            val inputStream = connection.inputStream
            val reader = BufferedReader(InputStreamReader(inputStream))
            val xmlStringBuilder = StringBuilder()
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                xmlStringBuilder.append(line)
            }

            reader.close()
            inputStream.close()

            xmlStringBuilder.toString()
        } else null
    }

    fun parseXml(xmlString: String): Document {
        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()

        val xmlInputSource = InputSource(StringReader(xmlString))

        return builder.parse(xmlInputSource)
    }

    fun containsTag(node: Node, tagName: String): Boolean {
        if (node.nodeType == Node.ELEMENT_NODE && node.nodeName == tagName) {
            return true
        }
        val childNodes = node.childNodes
        for (i in 0 until childNodes.length) {
            val childNode = childNodes.item(i)
            if (containsTag(childNode, tagName)) {
                return true
            }
        }
        return false
    }
}