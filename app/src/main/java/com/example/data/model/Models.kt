package com.example.data.model

import org.json.JSONArray
import org.json.JSONObject

data class Question(
    val id: String,
    val number: Int,
    val subject: String,
    val topic: String,
    val text: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val explanation: String
) {
    fun toJsonObject(): JSONObject {
        val obj = JSONObject()
        obj.put("id", id)
        obj.put("number", number)
        obj.put("subject", subject)
        obj.put("topic", topic)
        obj.put("text", text)
        val arr = JSONArray()
        options.forEach { arr.put(it) }
        obj.put("options", arr)
        obj.put("correctAnswerIndex", correctAnswerIndex)
        obj.put("explanation", explanation)
        return obj
    }

    companion object {
        fun fromJsonObject(obj: JSONObject, defaultNumber: Int = 1): Question {
            val id = obj.optString("id", "Q$defaultNumber")
            val number = obj.optInt("number", defaultNumber)
            val subject = obj.optString("mata_pelajaran", obj.optString("subject", "Matematika"))

            val elemen = obj.optString("elemen", "")
            val materi = obj.optString("materi", obj.optString("topic", ""))
            val topic = when {
                elemen.isNotEmpty() && materi.isNotEmpty() -> "$elemen: $materi"
                elemen.isNotEmpty() -> elemen
                materi.isNotEmpty() -> materi
                else -> "TKA SMP"
            }

            val text = obj.optString("pertanyaan", obj.optString("text", ""))

            val opts = mutableListOf<String>()
            if (obj.has("pilihan")) {
                val pilihanObj = obj.optJSONObject("pilihan")
                if (pilihanObj != null) {
                    val keys = listOf("A", "B", "C", "D", "E")
                    for (k in keys) {
                        if (pilihanObj.has(k)) {
                            val v = pilihanObj.getString(k)
                            opts.add("$k. $v")
                        }
                    }
                } else {
                    val optArr = obj.optJSONArray("pilihan")
                    if (optArr != null) {
                        for (i in 0 until optArr.length()) {
                            opts.add(optArr.getString(i))
                        }
                    }
                }
            } else if (obj.has("options")) {
                val optArr = obj.getJSONArray("options")
                for (i in 0 until optArr.length()) {
                    opts.add(optArr.getString(i))
                }
            }

            // Parse correct answer index
            val correctIdx = if (obj.has("correctAnswerIndex")) {
                obj.getInt("correctAnswerIndex")
            } else {
                val ansStr = obj.optString("jawaban_benar", "A").trim().uppercase()
                val firstChar = ansStr.firstOrNull() ?: 'A'
                when (firstChar) {
                    'A' -> 0
                    'B' -> 1
                    'C' -> 2
                    'D' -> 3
                    'E' -> 4
                    else -> 0
                }
            }

            val explanation = obj.optString("pembahasan", obj.optString("explanation", "Tidak ada pembahasan."))

            return Question(
                id = id,
                number = number,
                subject = subject,
                topic = topic,
                text = text,
                options = opts,
                correctAnswerIndex = correctIdx,
                explanation = explanation
            )
        }

        fun listToJsonString(questions: List<Question>): String {
            val arr = JSONArray()
            questions.forEach { arr.put(it.toJsonObject()) }
            return arr.toString()
        }

        fun parseFlexibleQuestionsJson(jsonString: String): Pair<String, List<Question>> {
            val list = mutableListOf<Question>()
            val trimmed = jsonString.trim()
            if (trimmed.isBlank()) return Pair("Paket Soal", list)

            var packetTitle = "Try Out TKA Matematika SMP"

            try {
                if (trimmed.startsWith("{")) {
                    val root = JSONObject(trimmed)
                    packetTitle = root.optString("judul", packetTitle)
                    val soalArr = root.optJSONArray("soal") ?: root.optJSONArray("questions")
                    if (soalArr != null) {
                        for (i in 0 until soalArr.length()) {
                            list.add(fromJsonObject(soalArr.getJSONObject(i), defaultNumber = i + 1))
                        }
                    }
                } else if (trimmed.startsWith("[")) {
                    val arr = JSONArray(trimmed)
                    for (i in 0 until arr.length()) {
                        list.add(fromJsonObject(arr.getJSONObject(i), defaultNumber = i + 1))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return Pair(packetTitle, list)
        }

        fun listFromJsonString(jsonString: String): List<Question> {
            return parseFlexibleQuestionsJson(jsonString).second
        }
    }
}

data class StudentUser(
    val nisn: String,
    val nama: String,
    val kelas: String,
    val password: String,
    val createdAt: Long = System.currentTimeMillis()
)

data class ExamPacket(
    val id: String,
    val title: String,
    val subject: String,
    val durationMinutes: Int,
    val questions: List<Question>,
    val token: String,
    val isActive: Boolean = true
)

data class StudentAnswer(
    val questionId: String,
    val selectedOptionIndex: Int,
    val isDoubtful: Boolean = false
)

data class ExamSubmission(
    val packetId: String,
    val packetTitle: String,
    val studentNisn: String,
    val studentName: String,
    val studentClass: String,
    val answers: Map<String, StudentAnswer>,
    val durationSecondsTaken: Int
)
