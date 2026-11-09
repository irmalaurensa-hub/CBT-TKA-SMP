package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {
    @Query("SELECT * FROM students ORDER BY kelas ASC, nama ASC")
    fun getAllStudentsFlow(): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE nisn = :nisn LIMIT 1")
    suspend fun getStudentByNisn(nisn: String): StudentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudents(students: List<StudentEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: StudentEntity)

    @Query("DELETE FROM students WHERE nisn = :nisn")
    suspend fun deleteStudent(nisn: String)

    @Query("DELETE FROM students")
    suspend fun deleteAllStudents()
}

@Dao
interface QuestionPacketDao {
    @Query("SELECT * FROM question_packets ORDER BY updatedAt DESC")
    fun getAllPacketsFlow(): Flow<List<QuestionPacketEntity>>

    @Query("SELECT * FROM question_packets WHERE id = :id LIMIT 1")
    suspend fun getPacketById(id: String): QuestionPacketEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPacket(packet: QuestionPacketEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPackets(packets: List<QuestionPacketEntity>)

    @Query("DELETE FROM question_packets WHERE id = :id")
    suspend fun deletePacket(id: String)

    @Query("DELETE FROM question_packets")
    suspend fun deleteAllPackets()
}

@Dao
interface ExamResultDao {
    @Query("SELECT * FROM exam_results ORDER BY completedAt DESC")
    fun getAllResultsFlow(): Flow<List<ExamResultEntity>>

    @Query("SELECT * FROM exam_results WHERE studentNisn = :studentNisn ORDER BY completedAt DESC")
    fun getResultsByStudentFlow(studentNisn: String): Flow<List<ExamResultEntity>>

    @Query("SELECT * FROM exam_results WHERE id = :id LIMIT 1")
    suspend fun getResultById(id: String): ExamResultEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResult(result: ExamResultEntity)

    @Query("DELETE FROM exam_results WHERE id = :id")
    suspend fun deleteResult(id: String)

    @Query("DELETE FROM exam_results")
    suspend fun deleteAllResults()
}
