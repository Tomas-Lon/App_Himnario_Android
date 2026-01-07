package com.example.himnariobeta

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.regex.Pattern

data class Hymn(
    val id: Int,
    val title: String,
    val lyrics: String
)

fun loadHymns(context: Context): List<Hymn> {
    val hymns = mutableListOf<Hymn>()
    try {
        // Trying to open with windows-1252 as the file seems to have legacy encoding
        val inputStream = context.assets.open("Himario_TabernaculoLaEsperanza_V2.txt")
        val reader = BufferedReader(InputStreamReader(inputStream, "windows-1252"))

        val pattern = Pattern.compile("^\\s*(\\d+)\\.\\s+(.*)")
        var currentId = 0
        var currentTitle = ""
        var currentLyrics = StringBuilder()

        var line: String? = reader.readLine()
        while (line != null) {
            if (line.isNotBlank()) {
                val matcher = pattern.matcher(line)
                if (matcher.find()) {
                    // New Hymn Start
                    val id = matcher.group(1)!!.toInt()
                    val title = matcher.group(2)!!.trim()

                    // If we collected a hymn previously, add it
                    if (currentId != 0) {
                        hymns.add(Hymn(currentId, currentTitle, currentLyrics.toString().trim()))
                    }

                    // Reset for new hymn
                    currentId = id
                    currentTitle = title
                    currentLyrics = StringBuilder()

                    // Check for reset (transition from index to body)
                    if (id == 1 && hymns.size > 10) {
                        // We found a '1' after collecting a bunch of hymns (the index).
                        // Clear the index to start collecting the real hymns with lyrics.
                        hymns.clear()
                    }
                } else {
                    // Lyrics line
                    if (currentId != 0) {
                        currentLyrics.append(line).append("\n")
                    }
                }
            }
            line = reader.readLine()
        }

        // Add the last hymn
        if (currentId != 0) {
            hymns.add(Hymn(currentId, currentTitle, currentLyrics.toString().trim()))
        }

        reader.close()
    } catch (e: Exception) {
        e.printStackTrace()
        // Fallback or error handling
        return emptyList()
    }
    return hymns
}
