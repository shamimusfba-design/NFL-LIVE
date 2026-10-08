package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.NflRepository
import com.example.model.DefectItem
import com.example.model.QualityRecord
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QualityEntryScreen(
  repository: NflRepository,
  onNavigateBack: () -> Unit,
  onViewHistory: () -> Unit
) {
  val assignedLine by repository.assignedLine.collectAsState()
  val isOnline by repository.isOnline.collectAsState()
  val entries by repository.qualityEntries.collectAsState()

  var selectedDate by remember { mutableStateOf("08 Oct 2026") }
  var selectedHour by remember { mutableStateOf("10:00 - 11:00") }
  var selectedStyle by remember { mutableStateOf("NFL-204") }
  var checkedGarmentsText by remember { mutableStateOf("150") }
  var defectiveGarmentsText by remember { mutableStateOf("5") }

  // Defects list state
  var defectRows by remember {
    mutableStateOf(
      listOf(
        DefectItem("Skip Stitch", 3),
        DefectItem("Uncut Thread", 4)
      )
    )
  }

  var remarksText by remember { mutableStateOf("") }
  var showHourDropdown by remember { mutableStateOf(false) }
  var showStyleDropdown by remember { mutableStateOf(false) }
  var submissionSuccessMsg by remember { mutableStateOf<String?>(null) }
  var validationError by remember { mutableStateOf<String?>(null) }

  val totalDefects = defectRows.sumOf { it.count }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = "Quality Entry",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
          )
        },
        navigationIcon = {
          IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("quality_back_btn")) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
              tint = Color.White
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = NflRoyalBlue
        )
      )
    },
    containerColor = Color(0xFFF0F4F8)
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .verticalScroll(rememberScrollState())
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Main Card matching Image 1 Right
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          // Top Info Row: Assigned line & Online dot
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Text(
                text = "Assigned line:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF37474F)
              )
              Text(
                text = assignedLine,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = NflRoyalBlue
              )
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Box(
                modifier = Modifier
                  .size(9.dp)
                  .clip(CircleShape)
                  .background(if (isOnline) NflGreenBright else NflAmber)
              )
              Text(
                text = if (isOnline) "Online" else "Offline Queue",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isOnline) NflGreen else NflAmber
              )
            }
          }

          // Date & Hour Slot Row
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            // Date box
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Text("Date", fontSize = 12.sp, color = Color(0xFF78909C))
              OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.outlinedCardColors(containerColor = Color(0xFFF8FAFC))
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 12.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = Color(0xFF546E7A),
                    modifier = Modifier.size(16.dp)
                  )
                  Text(
                    text = selectedDate,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF263238)
                  )
                }
              }
            }

            // Hour box with Dropdown
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Text("Hour", fontSize = 12.sp, color = Color(0xFF78909C))
              Box {
                OutlinedCard(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showHourDropdown = true }
                    .testTag("quality_hour_picker_btn"),
                  shape = RoundedCornerShape(8.dp),
                  colors = CardDefaults.outlinedCardColors(containerColor = Color(0xFFF8FAFC))
                ) {
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(horizontal = 10.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                      Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = Color(0xFF546E7A),
                        modifier = Modifier.size(16.dp)
                      )
                      Text(
                        text = selectedHour,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF263238)
                      )
                    }
                    Icon(
                      imageVector = Icons.Default.KeyboardArrowDown,
                      contentDescription = null,
                      tint = Color(0xFF546E7A),
                      modifier = Modifier.size(16.dp)
                    )
                  }
                }

                DropdownMenu(
                  expanded = showHourDropdown,
                  onDismissRequest = { showHourDropdown = false }
                ) {
                  repository.defaultHourSlots.forEach { slot ->
                    DropdownMenuItem(
                      text = { Text(slot) },
                      onClick = {
                        selectedHour = slot
                        showHourDropdown = false
                      }
                    )
                  }
                }
              }
            }
          }

          // Style Selector Box
          Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Style", fontSize = 12.sp, color = Color(0xFF78909C))
            Box {
              OutlinedCard(
                modifier = Modifier
                  .fillMaxWidth()
                  .clickable { showStyleDropdown = true }
                  .testTag("quality_style_picker_btn"),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.outlinedCardColors(containerColor = Color(0xFFF8FAFC))
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                  ) {
                    Icon(
                      imageVector = Icons.Default.Checkroom,
                      contentDescription = null,
                      tint = Color(0xFF546E7A),
                      modifier = Modifier.size(18.dp)
                    )
                    Text(
                      text = selectedStyle,
                      fontSize = 14.sp,
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFF263238)
                    )
                  }
                  Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color(0xFF546E7A),
                    modifier = Modifier.size(18.dp)
                  )
                }
              }

              DropdownMenu(
                expanded = showStyleDropdown,
                onDismissRequest = { showStyleDropdown = false }
              ) {
                repository.defaultStyles.forEach { style ->
                  DropdownMenuItem(
                    text = { Text(style.styleNumber, fontWeight = FontWeight.Bold) },
                    onClick = {
                      selectedStyle = style.styleNumber
                      showStyleDropdown = false
                    }
                  )
                }
              }
            }
          }

          // Checked Garments (pcs) & Defective Garments (pcs) Side-by-Side (Matches Screenshot)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Text(
                text = "Checked Garments (pcs)",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF263238)
              )
              OutlinedTextField(
                value = checkedGarmentsText,
                onValueChange = { input ->
                  if (input.all { it.isDigit() }) {
                    checkedGarmentsText = input
                    validationError = null
                  }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("checked_garments_input"),
                textStyle = LocalTextStyle.current.copy(
                  fontSize = 20.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF102A43)
                ),
                shape = RoundedCornerShape(8.dp)
              )
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Text(
                text = "Defective Garments (pcs)",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF263238)
              )
              OutlinedTextField(
                value = defectiveGarmentsText,
                onValueChange = { input ->
                  if (input.all { it.isDigit() }) {
                    defectiveGarmentsText = input
                    validationError = null
                  }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("defective_garments_input"),
                textStyle = LocalTextStyle.current.copy(
                  fontSize = 20.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF102A43)
                ),
                shape = RoundedCornerShape(8.dp)
              )
            }
          }

          // Defects Section
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
              text = "Defects",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF263238)
            )

            // Defect Rows
            defectRows.forEachIndexed { index, defectItem ->
              var showDefectTypeDropdown by remember { mutableStateOf(false) }

              Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                // Defect type dropdown selector
                Box(modifier = Modifier.weight(1.3f)) {
                  OutlinedCard(
                    modifier = Modifier
                      .fillMaxWidth()
                      .clickable { showDefectTypeDropdown = true },
                    shape = RoundedCornerShape(6.dp),
                    colors = CardDefaults.outlinedCardColors(containerColor = Color.White)
                  ) {
                    Row(
                      modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 10.dp),
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                      Text(
                        text = defectItem.defectType,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF263238)
                      )
                      Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = Color(0xFF78909C),
                        modifier = Modifier.size(16.dp)
                      )
                    }
                  }

                  DropdownMenu(
                    expanded = showDefectTypeDropdown,
                    onDismissRequest = { showDefectTypeDropdown = false }
                  ) {
                    repository.standardDefectTypes.forEach { dType ->
                      DropdownMenuItem(
                        text = { Text(dType) },
                        onClick = {
                          defectRows = defectRows.mapIndexed { idx, itm ->
                            if (idx == index) itm.copy(defectType = dType) else itm
                          }
                          showDefectTypeDropdown = false
                        }
                      )
                    }
                  }
                }

                // Count input
                OutlinedTextField(
                  value = defectItem.count.toString(),
                  onValueChange = { newVal ->
                    val cnt = newVal.toIntOrNull() ?: 0
                    defectRows = defectRows.mapIndexed { idx, itm ->
                      if (idx == index) itm.copy(count = cnt) else itm
                    }
                  },
                  singleLine = true,
                  keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                  modifier = Modifier
                    .weight(0.7f)
                    .testTag("defect_count_input_$index"),
                  textStyle = LocalTextStyle.current.copy(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF102A43)
                  ),
                  shape = RoundedCornerShape(6.dp)
                )

                // Delete button
                IconButton(
                  onClick = {
                    defectRows = defectRows.filterIndexed { idx, _ -> idx != index }
                  },
                  modifier = Modifier.size(36.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Remove defect",
                    tint = Color(0xFF90A4AE),
                    modifier = Modifier.size(20.dp)
                  )
                }
              }
            }

            // "+ Add defect" button
            TextButton(
              onClick = {
                defectRows = defectRows + DefectItem("Poor Iron", 1)
              },
              modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .testTag("add_defect_btn")
            ) {
              Icon(Icons.Default.Add, contentDescription = null, tint = NflPrimaryBlue, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("+ Add defect", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NflPrimaryBlue)
            }

            // Explanatory footnote and Total defects count
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "One garment may have multiple defects.",
                fontSize = 11.sp,
                color = Color(0xFF78909C)
              )
              Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                  text = "Total defects:",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Medium,
                  color = Color(0xFF37474F)
                )
                Text(
                  text = "$totalDefects",
                  fontSize = 16.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF102A43)
                )
              }
            }
          }

          // Optional remarks
          OutlinedTextField(
            value = remarksText,
            onValueChange = { remarksText = it },
            placeholder = { Text("Quality Remarks (Optional)", fontSize = 12.sp, color = Color(0xFF90A4AE)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            singleLine = true
          )

          // Calculated live metrics banner
          val checkedInt = checkedGarmentsText.toIntOrNull() ?: 0
          val defectiveInt = defectiveGarmentsText.toIntOrNull() ?: 0
          if (checkedInt > 0) {
            val liveOql = (defectiveInt.toDouble() / checkedInt.toDouble()) * 100.0
            val liveDhu = (totalDefects.toDouble() / checkedInt.toDouble()) * 100.0
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = Color(0xFFF1F8E9),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceAround
              ) {
                Text(
                  text = "Computed OQL: ${"%.2f".format(liveOql)}%",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = NflGreen
                )
                Text(
                  text = "Computed DHU: ${"%.2f".format(liveDhu)}%",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF1B5E20)
                )
              }
            }
          }

          if (validationError != null) {
            Text(
              text = validationError ?: "",
              color = NflRed,
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium
            )
          }

          if (submissionSuccessMsg != null) {
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = NflGreenLight,
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = NflGreen, modifier = Modifier.size(16.dp))
                Text(submissionSuccessMsg ?: "", color = NflGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
              }
            }
          }

          // SUBMIT QUALITY Button (Solid Dark Green Rounded Button matching Image 1 Right)
          Button(
            onClick = {
              val checked = checkedGarmentsText.toIntOrNull()
              val defective = defectiveGarmentsText.toIntOrNull()

              if (checked == null || checked <= 0) {
                validationError = "Please enter checked garment quantity greater than 0."
                return@Button
              }
              if (defective == null || defective < 0) {
                validationError = "Defective garments cannot be negative."
                return@Button
              }
              if (defective > checked) {
                validationError = "Defective garments ($defective) cannot exceed checked garments ($checked)."
                return@Button
              }

              val record = QualityRecord(
                lineId = assignedLine,
                date = selectedDate,
                hourSlot = selectedHour,
                styleNumber = selectedStyle,
                checkedGarments = checked,
                defectiveGarments = defective,
                defects = defectRows,
                remarks = remarksText,
                isConfirmedZero = (defective == 0)
              )

              repository.submitQuality(record)
              val oqlVal = (defective.toDouble() / checked.toDouble()) * 100.0
              submissionSuccessMsg = "Quality submitted! OQL: ${"%.2f".format(oqlVal)}% (Defects: $totalDefects)"
              validationError = null
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("submit_quality_button"),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NflGreen)
          ) {
            Text(
              text = "SUBMIT QUALITY",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }

          // "View my entries" link text below button
          Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text(
              text = "View my entries",
              fontSize = 13.sp,
              fontWeight = FontWeight.SemiBold,
              color = NflPrimaryBlue,
              modifier = Modifier
                .clickable { onViewHistory() }
                .padding(vertical = 4.dp)
                .testTag("view_quality_entries_link")
            )
          }
        }
      }

      // Recent Quality Inspections Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
      ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text("Recent Inspections on $assignedLine", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF263238))
          val lineQuality = entries.filter { it.lineId == assignedLine }.take(4)
          if (lineQuality.isEmpty()) {
            Text("No inspection records yet today.", fontSize = 12.sp, color = Color(0xFF90A4AE))
          } else {
            lineQuality.forEach { entry ->
              val oql = if (entry.checkedGarments > 0) (entry.defectiveGarments.toDouble() / entry.checkedGarments) * 100 else 0.0
              val dhu = if (entry.checkedGarments > 0) (entry.totalDefects.toDouble() / entry.checkedGarments) * 100 else 0.0

              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .background(Color(0xFFF8FAFC), RoundedCornerShape(6.dp))
                  .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Text("${entry.hourSlot} • ${entry.styleNumber}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF263238))
                  Text("Checked: ${entry.checkedGarments} • Defective: ${entry.defectiveGarments}", fontSize = 11.sp, color = Color(0xFF78909C))
                }
                Column(horizontalAlignment = Alignment.End) {
                  Text("OQL ${"%.2f".format(oql)}%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = if (oql <= 5.0) NflGreen else NflRed)
                  Text("DHU ${"%.2f".format(dhu)}%", fontSize = 11.sp, color = Color(0xFF546E7A))
                }
              }
            }
          }
        }
      }
    }
  }
}
