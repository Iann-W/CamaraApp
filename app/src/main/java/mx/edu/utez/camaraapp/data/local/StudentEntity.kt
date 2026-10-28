package mx.edu.utez.camaraapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class StudentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val matricula: String,
    val carrera: String = "Desarrollo de Software",
    val photoPath: String,
    val timestamp: Long = System.currentTimeMillis()
)