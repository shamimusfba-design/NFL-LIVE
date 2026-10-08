package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.NflRepository
import com.example.model.SupervisorLineSetup
import com.example.ui.theme.NflGreen
import com.example.ui.theme.NflRoyalBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupervisorSetupScreen(
  repository: NflRepository,
  onNavigateBack: () -> Unit
) {
  val assignedLine by repository.assignedLine.collectAsState()
  var selectedLine by remember { mutableStateOf(assignedLine) }
  var showLineDropdown by remember { mutableStateOf(false) }

  val currentSetup = remember(selectedLine) { repository.getLineSetup(selectedLine) }

  var styleText by remember(selectedLine) { mutableStateOf(currentSetup.styleNumber) }
  var orderText by remember(selectedLine) { mutableStateOf(currentSetup.orderNumber) }
  var smvText by remember(selectedLine) { mutableStateOf(currentSetup.smv.toString()) }
  var targetPcsText by remember(selectedLine) { mutableStateOf(currentSetup.targetHourlyPcs.toString()) }
  var targetEffText by remember(selectedLine) { mutableStateOf(currentSetup.targetEfficiencyPct.toString()) }
  var operatorsText by remember(selectedLine) { mutableStateOf(currentSetup.operatorsCount.toString()) }
  var helpersText by remember(selectedLine) { mutableStateOf(currentSetup.helpersCount.toString()) }
  var includeHelpers by remember(selectedLine) { mutableStateOf(currentSetup.includeHelpersInEfficiency) }
  var workingMinsText by remember(selectedLine) { mutableStateOf(currentSetup.workingMinutesPerHour.toString()) }

  var saveSuccessMsg by remember { mutableStateOf<String?>(null) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = "Supervisor Line Setup & SMV",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp
          )
        },
        navigationIcon = {
          IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("setup_back_btn")) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
              tint = Color.White
            )
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
      Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Text(
            text = "Target Line & Order Configuration",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF102A43)
          )

          // Line selector
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("Select Line:", fontWeight = FontWeight.Medium)
            Box {
              OutlinedButton(onClick = { showLineDropdown = true }) {
                Text("Line $selectedLine", fontWeight = FontWeight.Bold)
              }
              DropdownMenu(expanded = showLineDropdown, onDismissRequest = { showLineDropdown = false }) {
                repository.defaultLines.forEach { line ->
                  DropdownMenuItem(text = { Text("Line ${line.id}") }, onClick = {
                    selectedLine = line.id
                    showLineDropdown = false
                  })
                }
              }
            }
          }

          OutlinedTextField(
            value = styleText,
            onValueChange = { styleText = it },
            label = { Text("Style Number") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
          )

          OutlinedTextField(
            value = orderText,
            onValueChange = { orderText = it },
            label = { Text("Order / PO Number") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
          )

          // SMV field (strictly SMV terminology)
          OutlinedTextField(
            value = smvText,
            onValueChange = { smvText = it },
            label = { Text("Standard Minute Value (SMV)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
          )

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
              value = targetPcsText,
              onValueChange = { targetPcsText = it },
              label = { Text("Target Output (pcs/hr)") },
              modifier = Modifier.weight(1f),
              singleLine = true,
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            OutlinedTextField(
              value = targetEffText,
              onValueChange = { targetEffText = it },
              label = { Text("Target Efficiency (%)") },
              modifier = Modifier.weight(1f),
              singleLine = true,
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
          }

          Divider()

          Text(
            text = "Manpower & Inclusion Policy",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF102A43)
          )

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
              value = operatorsText,
              onValueChange = { operatorsText = it },
              label = { Text("Operators Count") },
              modifier = Modifier.weight(1f),
              singleLine = true,
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            OutlinedTextField(
              value = helpersText,
              onValueChange = { helpersText = it },
              label = { Text("Helpers Count") },
              modifier = Modifier.weight(1f),
              singleLine = true,
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
          }

          // Explicit and configurable Helper inclusion policy
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Include Helpers in Available Minutes",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
              )
              Text(
                text = if (includeHelpers) "Included manpower = Operators + Helpers" else "Included manpower = Operators only",
                fontSize = 11.sp,
                color = Color(0xFF78909C)
              )
            }
            Switch(
              checked = includeHelpers,
              onCheckedChange = { includeHelpers = it }
            )
          }

          OutlinedTextField(
            value = workingMinsText,
            onValueChange = { workingMinsText = it },
            label = { Text("Working Minutes Per Reporting Hour (e.g. 60, lunch = 0)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
          )

          if (saveSuccessMsg != null) {
            Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFE8F5E9)) {
              Text(
                text = saveSuccessMsg ?: "",
                color = NflGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier.padding(8.dp)
              )
            }
          }

          Button(
            onClick = {
              val setup = SupervisorLineSetup(
                lineId = selectedLine,
                date = "08 Oct 2026",
                styleNumber = styleText,
                orderNumber = orderText,
                smv = smvText.toDoubleOrNull() ?: 14.5,
                targetHourlyPcs = targetPcsText.toIntOrNull() ?: 90,
                targetEfficiencyPct = targetEffText.toDoubleOrNull() ?: 75.0,
                operatorsCount = operatorsText.toIntOrNull() ?: 20,
                helpersCount = helpersText.toIntOrNull() ?: 4,
                includeHelpersInEfficiency = includeHelpers,
                workingMinutesPerHour = workingMinsText.toIntOrNull() ?: 60,
                snapshotTimestamp = System.currentTimeMillis()
              )
              repository.saveLineSetup(setup)
              saveSuccessMsg = "Snapshot saved for Line $selectedLine! Historical calculations remain preserved."
            },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("save_line_setup_btn"),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NflRoyalBlue)
          ) {
            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Snapshot & Save Setup", fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
