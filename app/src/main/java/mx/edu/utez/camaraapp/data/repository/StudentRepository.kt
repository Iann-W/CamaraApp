package mx.edu.utez.camaraapp.data.repository

import mx.edu.utez.camaraapp.data.local.StudentDao
import mx.edu.utez.camaraapp.data.local.StudentEntity
import kotlinx.coroutines.flow.Flow

class StudentRepository(private val studentDao: StudentDao) {

    val allStudents: Flow<List<StudentEntity>> = studentDao.getAllStudents()

    suspend fun insertStudent(student: StudentEntity) {
        studentDao.insertStudent(student)
    }

    suspend fun deleteStudent(student: StudentEntity) {
        studentDao.deleteStudent(student)
    }
}
