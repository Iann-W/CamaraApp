package mx.edu.utez.camaraapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import mx.edu.utez.camaraapp.data.local.AppDatabase
import mx.edu.utez.camaraapp.data.repository.StudentRepository
import mx.edu.utez.camaraapp.ui.screens.StudentAlbumScreen
import mx.edu.utez.camaraapp.ui.theme.CamaraAppTheme
import mx.edu.utez.camaraapp.ui.viewmodel.StudentViewModel
import mx.edu.utez.camaraapp.ui.viewmodel.StudentViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(applicationContext)
        val repository = StudentRepository(database.studentDao())
        val viewModelFactory = StudentViewModelFactory(repository)

        setContent {
            CamaraAppTheme {
                val viewModel: StudentViewModel = viewModel(factory = viewModelFactory)
                StudentAlbumScreen(viewModel = viewModel)
            }
        }
    }
}