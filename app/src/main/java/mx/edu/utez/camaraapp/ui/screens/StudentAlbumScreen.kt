package mx.edu.utez.camaraapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.PhotoAlbum
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import mx.edu.utez.camaraapp.ui.components.AddStudentBottomSheet
import mx.edu.utez.camaraapp.ui.components.StudentCard
import mx.edu.utez.camaraapp.ui.viewmodel.StudentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentAlbumScreen(
    viewModel: StudentViewModel,
    modifier: Modifier = Modifier
) {
    val students by viewModel.studentsState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val formState by viewModel.formState.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    // Estado para controlar la visibilidad del diálogo de confirmación
    var showDeleteAllDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Álbum de Alumnos",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "${students.size} registro(s) guardado(s)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    // Ícono de papelera solo si hay estudiantes en la lista
                    if (students.isNotEmpty()) {
                        IconButton(onClick = { showDeleteAllDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = "Eliminar todos",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.openBottomSheet() },
                icon = { Icon(Icons.Default.Add, contentDescription = "Registrar alumno") },
                text = { Text("Registrar Foto") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                label = { Text("Buscar por nombre o matrícula") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Box(modifier = Modifier.weight(1f)) {
                if (students.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoAlbum,
                            contentDescription = null,
                            modifier = Modifier.size(96.dp),
                            tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (searchQuery.isEmpty()) "No hay registros aún" else "No se encontraron alumnos",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (searchQuery.isEmpty()) {
                                "Presiona el botón 'Registrar Foto' para ingresar la matrícula, nombre y foto del alumno."
                            } else {
                                "Intenta buscar con otro nombre o matrícula."
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(top = 8.dp, bottom = 88.dp)
                    ) {
                        items(
                            items = students,
                            key = { student -> student.id }
                        ) { student ->
                            StudentCard(
                                student = student,
                                onDeleteClick = { viewModel.deleteStudent(it) }
                            )
                        }
                    }
                }
            }
        }

        // Diálogo de confirmación para eliminar todos los registros
        if (showDeleteAllDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteAllDialog = false },
                title = { Text("¿Eliminar todos los registros?") },
                text = { Text("Esta acción eliminará de forma permanente todos los alumnos guardados y sus fotografías.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.clearAll()
                            showDeleteAllDialog = false
                        }
                    ) {
                        Text("Eliminar Todo", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteAllDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }

        if (formState.isBottomSheetOpen) {
            AddStudentBottomSheet(
                formState = formState,
                onNameChange = viewModel::onNameChange,
                onMatriculaChange = viewModel::onMatriculaChange,
                onCarreraChange = viewModel::onCarreraChange,
                onPhotoCaptured = viewModel::onPhotoCaptured,
                onSave = {
                    viewModel.saveStudent {
                        coroutineScope.launch { sheetState.hide() }
                    }
                },
                onDismiss = {
                    coroutineScope.launch { sheetState.hide() }
                        .invokeOnCompletion { viewModel.closeBottomSheet() }
                },
                sheetState = sheetState
            )
        }
    }
}