package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.model.ProductionRecord
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductionEntryScreen(
  repository: NflRepository,
  onNavigateBack: () -> Unit,
  onViewHistory: () -> Unit
) {
  val assignedLine by repository.assignedLine.collectAsState()
  val isOnline by repository.isOnline.collectAsState()
  val entries by repository.productionEntries.collectAsState()

  var selectedDate by remember { mutableStateOf("08 Oct 2026") }
  var selectedHour by remember { mutableStateOf("10:00 - 11:00") }
  var selectedStyle by remember { mutableStateOf("NFL-204") }
  var outputQtyText by remember { mutableStateOf("85") }
  var remarksText by remember { mutableStateOf("") }
  var showHourDropdown by remember { mutableStateOf(false) }
  var showStyleDropdown by remember { mutableStateOf(false) }
  var submissionSuccessMsg by remember { mutableStateOf<String?>(null) }
  var validationError by remember { mutableStateOf<String?>(null) }

  val lineSetup = remember(assignedLine) { repository.getLineSetup(assignedLine) }

  // Check missing reporting hours for today on this line
  val enteredHoursForLine = remember(entries, assignedLine, selectedDate) {
    entries.filter { it.lineId == assignedLine && it.date == selectedDate }.map { it.hourSlot }.toSet()
  }
  val missingHours = remember(enteredHoursForLine) {
    repository.defaultHourSlots.filter { it !in enteredHoursForLine }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = "Production Entry",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
          )
        },
        navigationIcon = {
          IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("prod_back_btn")) {
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
      // Main Entry Card (Matches Image 1 Left)
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
                    .testTag("hour_picker_btn"),
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
                    val isEntered = slot in enteredHoursForLine
                    DropdownMenuItem(
                      text = {
                        Row(
                          modifier = Modifier.fillMaxWidth(),
                          horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                          Text(slot)
                          if (isEntered) {
                            Text("✓ Entered", fontSize = 11.sp, color = NflGreen)
                          }
                        }
                      },
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
                  .testTag("style_picker_btn"),
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
                    Column {
                      Text(
                        text = selectedStyle,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF263238)
                      )
                      val sInfo = repository.defaultStyles.find { it.styleNumber == selectedStyle }
                      if (sInfo != null) {
                        Text(
                          text = "SMV: ${sInfo.smv} • Target: ${sInfo.defaultTargetPcsPerHour} pcs/hr",
                          fontSize = 11.sp,
                          color = Color(0xFF78909C)
                        )
                      }
                    }
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
                    text = {
                      Column {
                        Text(style.styleNumber, fontWeight = FontWeight.Bold)
                        Text(
                          "${style.buyer} • SMV: ${style.smv}",
                          fontSize = 11.sp,
                          color = Color(0xFF78909C)
                        )
                      }
                    },
                    onClick = {
                      selectedStyle = style.styleNumber
                      showStyleDropdown = false
                    }
                  )
                }
              }
            }
          }

          // Hourly Output (pcs) Input Box (Matches large bold entry field in Image 1)
          Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
              text = "Hourly Output (pcs)",
              fontSize = 13.sp,
              fontWeight = FontWeight.SemiBold,
              color = Color(0xFF263238)
            )

            OutlinedTextField(
              value = outputQtyText,
              onValueChange = { input ->
                if (input.all { it.isDigit() }) {
                  outputQtyText = input
                  validationError = null
                }
              },
              singleLine = true,
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("hourly_output_input"),
              textStyle = LocalTextStyle.current.copy(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF102A43)
              ),
              shape = RoundedCornerShape(8.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = NflRoyalBlue,
                unfocusedBorderColor = Color(0xFFCFD8DC)
              )
            )
            Text(
              text = "Enter this hour only.",
              fontSize = 11.sp,
              color = Color(0xFF78909C)
            )
          }

          // Downtime / Remarks (Optional)
          Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            OutlinedTextField(
              value = remarksText,
              onValueChange = { remarksText = it },
              placeholder = { Text("Downtime / Remarks (Optional)", fontSize = 13.sp, color = Color(0xFF90A4AE)) },
              leadingIcon = {
                Icon(
                  imageVector = Icons.Default.ChatBubbleOutline,
                  contentDescription = null,
                  tint = Color(0xFF78909C),
                  modifier = Modifier.size(18.dp)
                )
              },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("remarks_input"),
              shape = RoundedCornerShape(8.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = NflRoyalBlue,
                unfocusedBorderColor = Color(0xFFCFD8DC)
              ),
              singleLine = true
            )
          }

          // Live Summary Pill (Light blue card matching Image 1: "Line K1 • 10:00 - 11:00 • 85 pcs")
          val displayQty = outputQtyText.toIntOrNull() ?: 0
          Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFFE8F1FC)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Icon(
                imageVector = Icons.Default.PrecisionManufacturing,
                contentDescription = null,
                tint = NflRoyalBlue,
                modifier = Modifier.size(20.dp)
              )
              Text(
                text = "Line $assignedLine  •  $selectedHour  •  $displayQty pcs",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = NflRoyalBlue
              )
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

          // SUBMIT PRODUCTION Button (Large blue rounded button matching screenshot)
          Button(
            onClick = {
              val qty = outputQtyText.toIntOrNull()
              if (qty == null || qty < 0) {
                validationError = "Please enter a valid non-negative output quantity."
                return@Button
              }

              val record = ProductionRecord(
                lineId = assignedLine,
                date = selectedDate,
                hourSlot = selectedHour,
                styleNumber = selectedStyle,
                outputQty = qty,
                remarks = remarksText,
                isConfirmedZero = (qty == 0)
              )
              repository.submitProduction(record)
              submissionSuccessMsg = "Production submitted for $selectedHour ($qty pcs)"
              validationError = null
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("submit_production_button"),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NflRoyalBlue)
          ) {
            Text(
              text = "SUBMIT PRODUCTION",
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
                .testTag("view_my_entries_link")
            )
          }
        }
      }

      // Missing Hours Alert Card
      if (missingHours.isNotEmpty()) {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(8.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
          elevation = CardDefaults.cardElevation(1.dp)
        ) {
          Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Icon(Icons.Default.Warning, contentDescription = null, tint = NflAmber, modifier = Modifier.size(16.dp))
              Text(
                text = "Pending Reporting Hours Today (${missingHours.size})",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE65100)
              )
            }
            Text(
              text = "Missing: " + missingHours.joinToString(", "),
              fontSize = 11.sp,
              color = Color(0xFF5D4037)
            )
          }
        }
      }

      // Recent Line Entries
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
      ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text("Recent Output on $assignedLine", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF263238))
          val lineEntries = entries.filter { it.lineId == assignedLine }.take(4)
          if (lineEntries.isEmpty()) {
            Text("No entries yet today.", fontSize = 12.sp, color = Color(0xFF90A4AE))
          } else {
            lineEntries.forEach { entry ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .background(Color(0xFFF8FAFC), RoundedCornerShape(6.dp))
                  .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Text(entry.hourSlot, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF263238))
                  Text(entry.styleNumber, fontSize = 11.sp, color = Color(0xFF78909C))
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                  Text("${entry.outputQty} pcs", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NflRoyalBlue)
                  Box(
                    modifier = Modifier
                      .size(8.dp)
                      .clip(CircleShape)
                      .background(if (entry.syncStatus == com.example.model.SyncStatus.SYNCED) NflGreen else NflAmber)
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}
