package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.NflRepository
import com.example.model.UserRole
import com.example.ui.theme.NflGreen
import com.example.ui.theme.NflNavyHeader
import com.example.ui.theme.NflRoyalBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
  repository: NflRepository,
  onNavigateBack: () -> Unit
) {
  val currentRole by repository.currentRole.collectAsState()
  val assignedLine by repository.assignedLine.collectAsState()
  val isDemoMode by repository.isDemoMode.collectAsState()
  val isOnline by repository.isOnline.collectAsState()
  val sheetId by repository.googleSheetId.collectAsState()
  val backendUrl by repository.backendUrl.collectAsState()

  var inputSheetId by remember { mutableStateOf(sheetId) }
  var inputBackendUrl by remember { mutableStateOf(backendUrl) }
  var showRoleDropdown by remember { mutableStateOf(false) }
  var showLineDropdown by remember { mutableStateOf(false) }
  var saveBannerMsg by remember { mutableStateOf<String?>(null) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Factory System Settings", color = Color.White, fontWeight = FontWeight.Bold) },
        navigationIcon = {
          IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("settings_back_btn")) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = NflRoyalBlue)
      )
    },
    containerColor = Color(0xFFF1F5F9)
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .verticalScroll(rememberScrollState())
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Role & Assigned Line
      Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Text("User Role & Station Assignment", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = NflNavyHeader)

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("Current User Role:", fontWeight = FontWeight.Medium, fontSize = 13.sp)
            Box {
              OutlinedButton(onClick = { showRoleDropdown = true }, modifier = Modifier.testTag("role_select_btn")) {
                Text(currentRole.displayName, fontSize = 12.sp)
              }
              DropdownMenu(expanded = showRoleDropdown, onDismissRequest = { showRoleDropdown = false }) {
                UserRole.values().forEach { role ->
                  DropdownMenuItem(text = { Text(role.displayName) }, onClick = {
                    repository.setRole(role)
                    showRoleDropdown = false
                  })
                }
              }
            }
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("Assigned Sewing Line:", fontWeight = FontWeight.Medium, fontSize = 13.sp)
            Box {
              OutlinedButton(onClick = { showLineDropdown = true }, modifier = Modifier.testTag("line_select_btn")) {
                Text("Line $assignedLine", fontWeight = FontWeight.Bold)
              }
              DropdownMenu(expanded = showLineDropdown, onDismissRequest = { showLineDropdown = false }) {
                repository.defaultLines.forEach { line ->
                  DropdownMenuItem(text = { Text("Line ${line.id} (${line.floor})") }, onClick = {
                    repository.setAssignedLine(line.id)
                    showLineDropdown = false
                  })
                }
              }
            }
          }

          Text(
            text = "Line Alias Policy: J and J1 are confirmed to be the same line. J2 is treated as a separate distinct line.",
            fontSize = 11.sp,
            color = Color(0xFF78909C)
          )
        }
      }

      // Demo Mode vs Live Mode
      Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Text("Operating Mode & Integration", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = NflNavyHeader)

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = if (isDemoMode) "Demo Mode (Safe Preview)" else "Live Mode (Real Sheets Integration)",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = if (isDemoMode) Color(0xFFD32F2F) else NflGreen
              )
              Text(
                text = if (isDemoMode)
                  "Demo operations will not write to the real Google Sheet."
                else
                  "Live mode requires configured Service Account backend credentials.",
                fontSize = 11.sp,
                color = Color(0xFF78909C)
              )
            }
            Switch(
              checked = isDemoMode,
              onCheckedChange = { repository.setDemoMode(it) },
              modifier = Modifier.testTag("demo_mode_switch")
            )
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text("Network Connection Simulation", fontWeight = FontWeight.Bold, fontSize = 13.sp)
              Text("Toggle offline mode to test mobile persistence and retry queue.", fontSize = 11.sp, color = Color(0xFF78909C))
            }
            Switch(
              checked = isOnline,
              onCheckedChange = { repository.setOnlineState(it) },
              modifier = Modifier.testTag("online_toggle_switch")
            )
          }

          Divider()

          // Google Sheet ID
          OutlinedTextField(
            value = inputSheetId,
            onValueChange = { inputSheetId = it },
            label = { Text("Google Sheet ID") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(fontSize = 12.sp)
          )

          // Backend URL
          OutlinedTextField(
            value = inputBackendUrl,
            onValueChange = { inputBackendUrl = it },
            label = { Text("Backend Service URL") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(fontSize = 12.sp)
          )

          if (saveBannerMsg != null) {
            Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFE8F5E9)) {
              Text(
                text = saveBannerMsg ?: "",
                color = NflGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier.padding(8.dp)
              )
            }
          }

          Button(
            onClick = {
              repository.setGoogleSheetId(inputSheetId.trim())
              repository.setBackendUrl(inputBackendUrl.trim())
              saveBannerMsg = "Settings updated successfully."
            },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("save_settings_btn"),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NflRoyalBlue)
          ) {
            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Save Integration Settings", fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
