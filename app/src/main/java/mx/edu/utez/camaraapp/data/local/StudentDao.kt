package mx.edu.utez.camaraapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {

    @Query("SELECT * FROM students ORDER BY timestamp DESC")
    fun getAllStudents(): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE name LIKE '%' || :query || '%' OR matricula LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchStudents(query: String): Flow<List<StudentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: StudentEntity)

    @Delete
    suspend fun deleteStudent(student: StudentEntity)

    // Consulta para eliminar todos los alumnos de la base de datos
    @Query("DELETE FROM students")
    suspend fun deleteAllStudents()
}