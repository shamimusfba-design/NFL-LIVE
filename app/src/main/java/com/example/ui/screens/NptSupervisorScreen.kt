package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.NflRepository
import com.example.model.NptEvent
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NptSupervisorScreen(
  repository: NflRepository,
  onNavigateBack: () -> Unit
) {
  val assignedLine by repository.assignedLine.collectAsState()
  val nptEvents by repository.nptEvents.collectAsState()

  var selectedLine by remember { mutableStateOf(assignedLine) }
  var selectedStyle by remember { mutableStateOf("NFL-204") }
  var selectedReason by remember { mutableStateOf("Machine Breakdown") }
  var isFullLine by remember { mutableStateOf(false) }
  var affectedManpowerText by remember { mutableStateOf("2") }
  var machineRefText by remember { mutableStateOf("") }
  var remarksText by remember { mutableStateOf("") }

  var showLineDropdown by remember { mutableStateOf(false) }
  var showReasonDropdown by remember { mutableStateOf(false) }
  var showNewNptDialog by remember { mutableStateOf(false) }

  // Timer ticker for running events
  var ticker by remember { mutableLongStateOf(System.currentTimeMillis()) }
  LaunchedEffect(Unit) {
    while (true) {
      delay(1000)
      ticker = System.currentTimeMillis()
    }
  }

  val activeEvents = nptEvents.filter { !it.isClosed }
  val closedEvents = nptEvents.filter { it.isClosed }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = "NPT Tracking (Non-Productive Time)",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp
          )
        },
        navigationIcon = {
          IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("npt_back_btn")) {
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
    floatingActionButton = {
      FloatingActionButton(
        onClick = { showNewNptDialog = true },
        containerColor = NflRed,
        contentColor = Color.White,
        modifier = Modifier.testTag("start_npt_fab")
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 16.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(Icons.Default.PlayArrow, contentDescription = null)
          Text("START NPT", fontWeight = FontWeight.Bold)
        }
      }
    },
    containerColor = Color(0xFFF1F5F9)
  ) { paddingValues ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Explanatory Card / Principles
      item {
        Card(
          shape = RoundedCornerShape(8.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(1.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Icon(Icons.Default.Timer, contentDescription = null, tint = NflRed, modifier = Modifier.size(24.dp))
            Column {
              Text(
                text = "Live Downtime & Lost Man-Minutes",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF102A43)
              )
              Text(
                text = "Formula: Lost man-min = Duration (mins) × Affected Manpower. Running losses are provisional; closed losses are finalized.",
                fontSize = 11.sp,
                color = Color(0xFF627D98)
              )
            }
          }
        }
      }

      // Active / Running NPT Section
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Active Stoppages (${activeEvents.size})",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = if (activeEvents.isNotEmpty()) NflRed else Color(0xFF334E68)
          )
          if (activeEvents.isNotEmpty()) {
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = NflRedLight
            ) {
              Text(
                text = "PROVISIONAL LOSS",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = NflRed,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
        }
      }

      if (activeEvents.isEmpty()) {
        item {
          Card(
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
          ) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "No active downtime events. Factory lines are running smoothly.",
                fontSize = 13.sp,
                color = Color(0xFF829AB1)
              )
            }
          }
        }
      } else {
        items(activeEvents) { event ->
          val elapsedSec = ((ticker - event.startTimeMillis) / 1000).coerceAtLeast(0)
          val elapsedMin = elapsedSec / 60
          val elapsedSecRem = elapsedSec % 60
          val provLostManMin = (elapsedMin.toDouble() + (elapsedSecRem / 60.0)) * event.affectedManpower

          Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(3.dp),
            modifier = Modifier.testTag("active_npt_card_${event.id}")
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
              verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              // Header
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                  Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(NflRed))
                  Text(
                    text = "Line ${event.lineId} • ${event.reason}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = NflRed
                  )
                }

                Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFFFEBEE)) {
                  Text(
                    text = "${event.affectedManpower} people affected",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = NflRed,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
              }

              // Overlap warning banner if multiple overlaps detected
              if (event.overlapFlagged) {
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = Color(0xFFFFF3E0),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Row(modifier = Modifier.padding(8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = NflAmber, modifier = Modifier.size(16.dp))
                    Text(
                      text = "Overlap detected on Line ${event.lineId}. Deduplication applied to avoid double counting available minutes.",
                      fontSize = 11.sp,
                      color = Color(0xFFBF360C)
                    )
                  }
                }
              }

              // Details
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Column {
                  Text("Start Time", fontSize = 11.sp, color = Color(0xFF78909C))
                  val df = SimpleDateFormat("hh:mm:ss a", Locale.US)
                  Text(df.format(Date(event.startTimeMillis)), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Text("Elapsed Duration", fontSize = 11.sp, color = Color(0xFF78909C))
                  Text(
                    text = "%02d:%02d".format(elapsedMin, elapsedSecRem),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = NflRed
                  )
                }
                Column(horizontalAlignment = Alignment.End) {
                  Text("Prov. Lost Man-Min", fontSize = 11.sp, color = Color(0xFF78909C))
                  Text(
                    text = "%.1f min".format(provLostManMin),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF102A43)
                  )
                }
              }

              if (event.machineOrOpRef.isNotBlank() || event.remarks.isNotBlank()) {
                Text(
                  text = "Ref: ${event.machineOrOpRef.ifBlank { "N/A" }} | Remarks: ${event.remarks.ifBlank { "None" }}",
                  fontSize = 11.sp,
                  color = Color(0xFF546E7A)
                )
              }

              // END NPT Button
              Button(
                onClick = {
                  repository.endNptEvent(event.id)
                },
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("end_npt_btn_${event.id}"),
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF37474F))
              ) {
                Icon(Icons.Default.Stop, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("END NPT (Close Event)", fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }

      // Finalized NPT History
      item {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "Finalized Losses Today (${closedEvents.size})",
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF334E68)
        )
      }

      if (closedEvents.isEmpty()) {
        item {
          Text("No closed downtime events yet today.", fontSize = 12.sp, color = Color(0xFF90A4AE))
        }
      } else {
        items(closedEvents) { event ->
          val df = SimpleDateFormat("hh:mm a", Locale.US)
          val startStr = df.format(Date(event.startTimeMillis))
          val endStr = event.endTimeMillis?.let { df.format(Date(it)) } ?: "--"

          Card(
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(1.dp)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text("Line ${event.lineId} • ${event.reason}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF263238))
                Text("$startStr - $endStr • ${event.affectedManpower} people", fontSize = 11.sp, color = Color(0xFF78909C))
              }
              Column(horizontalAlignment = Alignment.End) {
                Text(
                  text = "${event.lostManMinutes.roundToInt()} lost man-min",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold,
                  color = NflRed
                )
                Text("Finalized", fontSize = 10.sp, color = NflGreen)
              }
            }
          }
        }
      }
    }
  }

  // Dialog: Start New NPT Event
  if (showNewNptDialog) {
    val lineSetup = remember(selectedLine) { repository.getLineSetup(selectedLine) }

    AlertDialog(
      onDismissRequest = { showNewNptDialog = false },
      title = { Text("Start New NPT Event", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          // Line selector
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("Line:", fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Box {
              OutlinedButton(onClick = { showLineDropdown = true }) {
                Text(selectedLine)
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

          // Reason selector
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("Reason:", fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Box {
              OutlinedButton(onClick = { showReasonDropdown = true }) {
                Text(selectedReason, fontSize = 12.sp)
              }
              DropdownMenu(expanded = showReasonDropdown, onDismissRequest = { showReasonDropdown = false }) {
                repository.nptReasons.forEach { rsn ->
                  DropdownMenuItem(text = { Text(rsn) }, onClick = {
                    selectedReason = rsn
                    showReasonDropdown = false
                  })
                }
              }
            }
          }

          // Scope: Full line or Partial
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              RadioButton(
                selected = !isFullLine,
                onClick = { isFullLine = false }
              )
              Text("Partial", fontSize = 12.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
              RadioButton(
                selected = isFullLine,
                onClick = {
                  isFullLine = true
                  affectedManpowerText = "${lineSetup.includedManpower}"
                }
              )
              Text("Full Line (${lineSetup.includedManpower} ops)", fontSize = 12.sp)
            }
          }

          // Affected manpower
          if (!isFullLine) {
            OutlinedTextField(
              value = affectedManpowerText,
              onValueChange = { if (it.all { c -> c.isDigit() }) affectedManpowerText = it },
              label = { Text("Affected Manpower Count") },
              modifier = Modifier.fillMaxWidth(),
              singleLine = true
            )
          }

          // Optional Machine / Operation ref
          OutlinedTextField(
            value = machineRefText,
            onValueChange = { machineRefText = it },
            label = { Text("Machine / Operation Ref (Optional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
          )

          // Remarks
          OutlinedTextField(
            value = remarksText,
            onValueChange = { remarksText = it },
            label = { Text("Remarks") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            val count = affectedManpowerText.toIntOrNull() ?: 1
            repository.startNptEvent(
              lineId = selectedLine,
              styleNumber = selectedStyle,
              reason = selectedReason,
              isFullLine = isFullLine,
              affectedCount = count,
              machineRef = machineRefText,
              remarks = remarksText
            )
            showNewNptDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = NflRed)
        ) {
          Text("START NPT")
        }
      },
      dismissButton = {
        TextButton(onClick = { showNewNptDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }
}
