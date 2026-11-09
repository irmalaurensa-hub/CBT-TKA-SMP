package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class StudentEntity(
    @PrimaryKey val nisn: String,
    val nama: String,
    val kelas: String,
    val password: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "question_packets")
data class QuestionPacketEntity(
    @PrimaryKey val id: String,
    val title: String,
    val subject: String,
    val durationMinutes: Int,
    val encryptedPayload: String,
    val encryptionIv: String,
    val checksum: String,
    val questionCount: Int,
    val token: String,
    val isActive: Boolean = true,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "exam_results")
data class ExamResultEntity(
    @PrimaryKey val id: String,
    val packetId: String,
    val packetTitle: String,
    val studentNisn: String,
    val studentName: String,
    val studentClass: String,
    val score: Double,
    val correctCount: Int,
    val wrongCount: Int,
    val unansweredCount: Int,
    val totalQuestions: Int,
    val completedAt: Long = System.currentTimeMillis(),
    val answersJson: String,
    val durationSecondsTaken: Int
)
