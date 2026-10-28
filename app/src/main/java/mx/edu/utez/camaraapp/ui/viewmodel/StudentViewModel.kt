package mx.edu.utez.camaraapp.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import mx.edu.utez.camaraapp.data.local.StudentEntity
import mx.edu.utez.camaraapp.data.repository.StudentRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

data class FormUiState(
    val name: String = "",
    val matricula: String = "",
    val carrera: String = "",
    val photoUri: Uri? = null,
    val photoPath: String? = null,
    val errorMessage: String? = null,
    val isBottomSheetOpen: Boolean = false
)

class StudentViewModel(private val repository: StudentRepository) : ViewModel() {

    val searchQuery = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    val studentsState: StateFlow<List<StudentEntity>> = searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                repository.allStudents
            } else {
                repository.searchStudents(query)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _formState = MutableStateFlow(FormUiState())
    val formState: StateFlow<FormUiState> = _formState.asStateFlow()

    fun onSearchQueryChange(newQuery: String) {
        searchQuery.value = newQuery
    }

    fun onNameChange(newName: String) {
        _formState.update { it.copy(name = newName, errorMessage = null) }
    }

    fun onMatriculaChange(newMatricula: String) {
        _formState.update { it.copy(matricula = newMatricula, errorMessage = null) }
    }

    fun onCarreraChange(newCarrera: String) {
        _formState.update { it.copy(carrera = newCarrera, errorMessage = null) }
    }

    fun onPhotoCaptured(uri: Uri, path: String) {
        _formState.update {
            it.copy(
                photoUri = uri,
                photoPath = path,
                errorMessage = null
            )
        }
    }

    fun openBottomSheet() {
        _formState.update { FormUiState(isBottomSheetOpen = true) }
    }

    fun closeBottomSheet() {
        _formState.update { it.copy(isBottomSheetOpen = false) }
    }

    fun saveStudent(onSuccess: () -> Unit) {
        val currentState = _formState.value
        val name = currentState.name.trim()
        val matricula = currentState.matricula.trim()
        val carrera = currentState.carrera.trim()
        val path = currentState.photoPath ?: currentState.photoUri?.toString()

        if (name.isEmpty()) {
            _formState.update { it.copy(errorMessage = "El nombre del alumno es requerido.") }
            return
        }

        if (matricula.isEmpty()) {
            _formState.update { it.copy(errorMessage = "La matrícula es requerida.") }
            return
        }

        if (carrera.isEmpty()) {
            _formState.update { it.copy(errorMessage = "La carrera es requerida.") }
            return
        }

        if (path.isNullOrEmpty()) {
            _formState.update { it.copy(errorMessage = "Debe tomar una foto del alumno.") }
            return
        }

        val newStudent = StudentEntity(
            name = name,
            matricula = matricula,
            carrera = carrera,
            photoPath = path,
            timestamp = System.currentTimeMillis()
        )

        viewModelScope.launch {
            repository.insertStudent(newStudent)
            _formState.update { FormUiState(isBottomSheetOpen = false) }
            onSuccess()
        }
    }

    fun deleteStudent(student: StudentEntity) {
        viewModelScope.launch {
            repository.deleteStudent(student)
            try {
                val file = File(student.photoPath)
                if (file.exists()) {
                    file.delete()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Función para vaciar todos los registros
    fun clearAll() {
        viewModelScope.launch {
            val currentList = repository.allStudents.first()
            currentList.forEach { student ->
                try {
                    val file = File(student.photoPath)
                    if (file.exists()) {
                        file.delete()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            repository.deleteAllStudents()
        }
    }
}