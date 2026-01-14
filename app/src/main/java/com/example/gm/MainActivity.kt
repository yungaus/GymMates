package com.example.gm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

// --- CONFIG COLORS ---
val DarkBlue = Color(0xFF1A2C50)
val LightBlue = Color(0xFF2D4B85)
val BackgroundGray = Color(0xFFE5E5E5)
val TextWhite = Color(0xFFFFFFFF)
val CustomGreen = Color(0xFF00C853)
val DangerRed = Color(0xFFFF0000)
val GenderMan = Color(0xFFD32F2F)
val GenderWoman = Color(0xFFE91E63)

// --- DATA MODELS & VIEWMODEL ---
data class Program(val id: Int, var day: String, var muscle: String, var progress: Float)

class MainViewModel : ViewModel() {
    var userName by mutableStateOf("Guest")
    var userAge by mutableStateOf("")
    var userGender by mutableStateOf("Man")
    var userWeight by mutableStateOf("")
    var userHeight by mutableStateOf("")
    var userTargetWeight by mutableStateOf("")
    var userLifestyle by mutableStateOf("")
    var userGoals by mutableStateOf("")

    val programs = mutableStateListOf(
        Program(1, "Monday", "Chest", 0.8f),
        Program(2, "Tuesday", "Back", 0.65f),
        Program(3, "Wednesday", "Shoulder", 0.7f),
        Program(4, "Thursday", "Bicep & Tricep", 0.75f),
        Program(5, "Friday", "Leg", 0.95f),
        Program(6, "Saturday", "Rest", 0.0f)
    )

    fun deleteProgram(program: Program) { programs.remove(program) }

    fun updateProgram(id: Int, newDay: String, newMuscle: String) {
        val index = programs.indexOfFirst { it.id == id }
        if (index != -1) {
            programs[index] = programs[index].copy(day = newDay, muscle = newMuscle)
        }
    }

    fun addProgram(day: String, muscle: String) {
        val newId = (System.currentTimeMillis() % 10000).toInt()
        programs.add(Program(newId, day, muscle, 0.0f))
    }

    fun registerUser(name: String, age: String, gender: String, weight: String, height: String, target: String, lifestyle: String, goals: String) {
        userName = name
        userAge = age
        userGender = gender
        userWeight = weight
        userHeight = height
        userTargetWeight = target
        userLifestyle = lifestyle
        userGoals = goals
    }
}

// --- ENTRY POINT ---
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val viewModel: MainViewModel = viewModel()

            NavHost(navController = navController, startDestination = "register") {
                composable("register") { RegisterScreen(navController, viewModel) }
                composable("home") { HomeScreen(navController, viewModel) }
                composable("tracker") { TrackerScreen(navController, viewModel) }
                composable("schedule") { ScheduleScreen(navController, viewModel) }
                composable("custom_program") { CustomProgramScreen(navController, viewModel) }
                composable("add_program") { AddProgramScreen(navController, viewModel) }
                composable("edit_form/{id}") { backStackEntry ->
                    val id = backStackEntry.arguments?.getString("id")?.toIntOrNull()
                    EditFormScreen(navController, viewModel, id)
                }
                composable("edit_profile") { EditProfileScreen(navController, viewModel) }
            }
        }
    }
}

// ==========================================
// 1. REGISTER SCREEN (LAYOUT AMAN & SCROLLABLE)
// ==========================================
@Composable
fun RegisterScreen(navController: NavController, viewModel: MainViewModel) {
    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf("Man") }

    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var targetWeight by remember { mutableStateOf("") }
    var lifestyle by remember { mutableStateOf("") }
    var goals by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
            .verticalScroll(rememberScrollState()) // Scroll Aktif
            .imePadding(), // Solusi Tombol Ketutup Keyboard
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top // Mulai dari atas biar rapi
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Logo
        Image(
            painter = painterResource(id = R.drawable.logo_gm),
            contentDescription = "Logo Aplikasi GM",
            modifier = Modifier.size(160.dp).padding(bottom = 16.dp)
        )

        Text("Register Now", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = DarkBlue, modifier = Modifier.align(Alignment.Start))
        Spacer(modifier = Modifier.height(16.dp))

        RegisterInput(value = name, onValueChange = { name = it }, label = "Name")
        Spacer(modifier = Modifier.height(12.dp))
        RegisterInput(value = password, onValueChange = { password = it }, label = "Password", isPassword = true)
        Spacer(modifier = Modifier.height(12.dp))
        RegisterInput(value = age, onValueChange = { age = it }, label = "Age", isNumber = true)

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(onClick = { selectedGender = "Man" }, colors = ButtonDefaults.buttonColors(containerColor = if(selectedGender == "Man") GenderMan else Color.Gray), modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) { Text("Man") }
            Button(onClick = { selectedGender = "Woman" }, colors = ButtonDefaults.buttonColors(containerColor = if(selectedGender == "Woman") GenderWoman else Color.Gray), modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) { Text("Woman") }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            RegisterInput(value = weight, onValueChange = { weight = it }, label = "Weight", isNumber = true, modifier = Modifier.weight(1f))
            RegisterInput(value = height, onValueChange = { height = it }, label = "Height", isNumber = true, modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(12.dp))
        RegisterInput(value = targetWeight, onValueChange = { targetWeight = it }, label = "Target Weight", isNumber = true)
        Spacer(modifier = Modifier.height(12.dp))
        RegisterInput(value = lifestyle, onValueChange = { lifestyle = it }, label = "Lifestyle")
        Spacer(modifier = Modifier.height(12.dp))
        RegisterInput(value = goals, onValueChange = { goals = it }, label = "Goals")

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (name.isNotEmpty()) {
                    viewModel.registerUser(name, age, selectedGender, weight, height, targetWeight, lifestyle, goals)
                    navController.navigate("home") { popUpTo("register") { inclusive = true } }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = DarkBlue),
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("CREATE", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(50.dp)) // Padding bawah ekstra
    }
}

@Composable
fun RegisterInput(value: String, onValueChange: (String) -> Unit, label: String, isPassword: Boolean = false, isNumber: Boolean = false, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        keyboardOptions = if (isNumber) KeyboardOptions(keyboardType = KeyboardType.Number) else KeyboardOptions.Default,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = BackgroundGray,
            focusedContainerColor = BackgroundGray,
            focusedBorderColor = DarkBlue,
            unfocusedBorderColor = Color.Transparent
        )
    )
}

// ==========================================
// 2. HOME SCREEN (SEKARANG LEBIH PENUH!)
// ==========================================
@Composable
fun HomeScreen(navController: NavController, viewModel: MainViewModel) {
    Scaffold(bottomBar = { BottomNavBar(navController) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()), // Induk Scroll
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // HEADER
            Column {
                Text("Welcome ${viewModel.userName}", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = DarkBlue)
                Text("Ready to Upgrade Yourself?", fontSize = 14.sp, color = Color.Gray)
            }

            // ROW KARTU ATAS
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Card(colors = CardDefaults.cardColors(containerColor = DarkBlue), shape = RoundedCornerShape(16.dp), modifier = Modifier.weight(1f).height(160.dp).clickable { navController.navigate("schedule") }) {
                    Column {
                        Box(modifier = Modifier.fillMaxWidth().height(40.dp).background(Color(0xFF121E36)), contentAlignment = Alignment.Center) { Text("Today", color = TextWhite, fontWeight = FontWeight.Bold) }
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Chest Day", color = TextWhite, fontSize = 20.sp, fontStyle = FontStyle.Italic) }
                    }
                }
                Card(colors = CardDefaults.cardColors(containerColor = BackgroundGray), shape = RoundedCornerShape(16.dp), modifier = Modifier.weight(1f).height(160.dp).clickable { navController.navigate("tracker") }) {
                    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Text("Tracker", color = DarkBlue, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(Modifier.width(10.dp).height(30.dp).background(Color.Gray, RoundedCornerShape(4.dp)))
                            Box(Modifier.width(10.dp).height(50.dp).background(DarkBlue, RoundedCornerShape(4.dp)))
                            Box(Modifier.width(10.dp).height(20.dp).background(LightBlue, RoundedCornerShape(4.dp)))
                        }
                    }
                }
            }

            // TOMBOL START
            Button(onClick = { navController.navigate("schedule") }, shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = DarkBlue), modifier = Modifier.fillMaxWidth().height(120.dp)) { Text("Start Workout!", fontSize = 22.sp, fontWeight = FontWeight.Bold) }

            // ROW TOMBOL KECIL
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                GrayBox(text = "Tutorial", modifier = Modifier.weight(1f))
                GrayBox(text = "Mates", modifier = Modifier.weight(1f))
            }

            // --- BAGIAN BARU: QUICK SCHEDULE (BIAR GAK KOSONG) ---
            Spacer(modifier = Modifier.height(8.dp))
            Text("Quick Schedule", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = DarkBlue)

            // Kita tampilkan list program langsung di sini (Pake Column manual karena di dalam ScrollView)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                viewModel.programs.forEach { program ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = BackgroundGray),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().clickable { navController.navigate("schedule") }
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            // Dot indikator
                            Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(50)).background(if(program.progress > 0) CustomGreen else Color.Gray))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = "${program.day}: ${program.muscle}", color = DarkBlue, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp)) // Padding bawah
        }
    }
}

// ==========================================
// 3. TRACKER SCREEN
// ==========================================
@Composable
fun TrackerScreen(navController: NavController, viewModel: MainViewModel) {
    Scaffold(bottomBar = { BottomNavBar(navController) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Text("Weekly Tracker", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = DarkBlue)
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(viewModel.programs) { program ->
                    Box(modifier = Modifier.fillMaxWidth().height(60.dp).clip(RoundedCornerShape(12.dp)).background(DarkBlue)) {
                        Box(modifier = Modifier.fillMaxWidth(fraction = if(program.progress == 0f) 0.01f else program.progress).fillMaxHeight().background(LightBlue))
                        Row(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("${program.day} - ${program.muscle}", color = TextWhite, fontWeight = FontWeight.SemiBold)
                            if(program.progress > 0) Text("${(program.progress * 100).toInt()}%", color = TextWhite, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 4. SCHEDULE SCREEN
// ==========================================
@Composable
fun ScheduleScreen(navController: NavController, viewModel: MainViewModel) {
    Scaffold(bottomBar = { BottomNavBar(navController) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Text("Weekly Schedule", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = DarkBlue)
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(viewModel.programs) { program ->
                    Card(colors = CardDefaults.cardColors(containerColor = DarkBlue), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text(program.day, color = Color.Gray, fontSize = 12.sp)
                                Text(program.muscle, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            }
                        }
                    }
                }
            }
            Button(onClick = { navController.navigate("custom_program") }, colors = ButtonDefaults.buttonColors(containerColor = CustomGreen), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().height(50.dp)) { Text("CUSTOM PROGRAM", fontWeight = FontWeight.Bold) }
        }
    }
}

// ==========================================
// 5. CUSTOM PROGRAM
// ==========================================
@Composable
fun CustomProgramScreen(navController: NavController, viewModel: MainViewModel) {
    Scaffold(bottomBar = { BottomNavBar(navController) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Text("Custom Program", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = DarkBlue)
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(viewModel.programs) { program ->
                    Card(colors = CardDefaults.cardColors(containerColor = DarkBlue), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Column(modifier = Modifier.weight(1f)) { Text("${program.day} - ${program.muscle}", color = TextWhite, fontWeight = FontWeight.SemiBold) }
                            IconButton(onClick = { navController.navigate("edit_form/${program.id}") }) { Icon(Icons.Default.Edit, contentDescription = "Edit", tint = TextWhite) }
                            IconButton(onClick = { viewModel.deleteProgram(program) }) { Icon(Icons.Default.Delete, contentDescription = "Delete", tint = DangerRed) }
                        }
                    }
                }
            }
            Button(
                onClick = { navController.navigate("add_program") },
                colors = ButtonDefaults.buttonColors(containerColor = CustomGreen),
                shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().height(50.dp)
            ) { Icon(Icons.Default.Add, contentDescription = null); Spacer(Modifier.width(8.dp)); Text("ADD NEW (+)", fontWeight = FontWeight.Bold) }
        }
    }
}

// ==========================================
// 6. ADD & EDIT FORMS
// ==========================================
@Composable
fun AddProgramScreen(navController: NavController, viewModel: MainViewModel) {
    var day by remember { mutableStateOf("") }
    var muscle by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("Add New Program", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = DarkBlue)
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(value = day, onValueChange = { day = it }, label = { Text("Day (e.g. Sunday)") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = muscle, onValueChange = { muscle = it }, label = { Text("Muscle (e.g. Cardio)") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                if (day.isNotEmpty() && muscle.isNotEmpty()) {
                    viewModel.addProgram(day, muscle)
                    navController.popBackStack()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = DarkBlue), modifier = Modifier.fillMaxWidth().height(50.dp)
        ) { Text("SAVE PROGRAM") }
    }
}

@Composable
fun EditFormScreen(navController: NavController, viewModel: MainViewModel, programId: Int?) {
    val program = viewModel.programs.find { it.id == programId }
    var day by remember { mutableStateOf(program?.day ?: "") }
    var muscle by remember { mutableStateOf(program?.muscle ?: "") }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("Edit Program", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = DarkBlue)
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(value = day, onValueChange = { day = it }, label = { Text("Day") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = muscle, onValueChange = { muscle = it }, label = { Text("Muscle Group") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                if (programId != null) {
                    viewModel.updateProgram(programId, day, muscle)
                    navController.popBackStack()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = DarkBlue), modifier = Modifier.fillMaxWidth().height(50.dp)
        ) { Text("SAVE CHANGES") }
    }
}

// ==========================================
// 7. EDIT PROFILE
// ==========================================
@Composable
fun EditProfileScreen(navController: NavController, viewModel: MainViewModel) {
    var name by remember { mutableStateOf(viewModel.userName) }
    var age by remember { mutableStateOf(viewModel.userAge) }

    Scaffold(bottomBar = { BottomNavBar(navController) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp).verticalScroll(rememberScrollState())) {
            Text("Edit Profile", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = DarkBlue)
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(value = age, onValueChange = { age = it }, label = { Text("Age") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(value = viewModel.userGender, onValueChange = {}, label = { Text("Gender (Read Only)") }, enabled = false, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(value = viewModel.userWeight, onValueChange = {}, label = { Text("Weight (Read Only)") }, enabled = false, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(value = viewModel.userHeight, onValueChange = {}, label = { Text("Height (Read Only)") }, enabled = false, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    viewModel.userName = name
                    viewModel.userAge = age
                    navController.popBackStack()
                },
                colors = ButtonDefaults.buttonColors(containerColor = DarkBlue), modifier = Modifier.fillMaxWidth().height(50.dp)
            ) { Text("UPDATE PROFILE") }
        }
    }
}

// ==========================================
// COMPONENTS
// ==========================================
@Composable
fun GrayBox(text: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.height(120.dp).clip(RoundedCornerShape(16.dp)).background(BackgroundGray), contentAlignment = Alignment.Center) {
        Text(text, color = DarkBlue, fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic)
    }
}

@Composable
fun BottomNavBar(navController: NavController) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    NavigationBar(containerColor = DarkBlue) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = null, tint = if (currentRoute == "home") TextWhite else Color.Gray) },
            selected = currentRoute == "home",
            onClick = { navController.navigate("home") { popUpTo("home") { inclusive = true } } },
            colors = NavigationBarItemDefaults.colors(indicatorColor = LightBlue)
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = null, tint = if (currentRoute == "edit_profile") TextWhite else Color.Gray) },
            selected = currentRoute == "edit_profile",
            onClick = { navController.navigate("edit_profile") },
            colors = NavigationBarItemDefaults.colors(indicatorColor = LightBlue)
        )
    }
}

@Composable
fun androidx.navigation.NavController.currentBackStackEntryAsState(): State<androidx.navigation.NavBackStackEntry?> {
    val currentNavBackStackEntry = remember { mutableStateOf(currentBackStackEntry) }
    DisposableEffect(this) {
        val listener = NavController.OnDestinationChangedListener { controller, _, _ -> currentNavBackStackEntry.value = controller.currentBackStackEntry }
        addOnDestinationChangedListener(listener)
        onDispose { removeOnDestinationChangedListener(listener) }
    }
    return currentNavBackStackEntry
}