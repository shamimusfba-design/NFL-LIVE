package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Refresh
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
import com.example.model.SyncStatus
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorySyncScreen(
  repository: NflRepository,
  onNavigateBack: () -> Unit
) {
  val prodEntries by repository.productionEntries.collectAsState()
  val qualityEntries by repository.qualityEntries.collectAsState()
  val auditLogs by repository.auditLogs.collectAsState()
  var selectedTab by remember { mutableStateOf(0) } // 0: Production, 1: Quality, 2: Audit Log

  val pendingCount = prodEntries.count { it.syncStatus != SyncStatus.SYNCED } +
      qualityEntries.count { it.syncStatus != SyncStatus.SYNCED }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Submission History & Sync", color = Color.White, fontWeight = FontWeight.Bold) },
        navigationIcon = {
          IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("history_back_btn")) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
          }
        },
        actions = {
          IconButton(onClick = { repository.retryPendingSubmissions() }) {
            Icon(Icons.Default.Refresh, contentDescription = "Retry Sync", tint = Color.White)
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
    ) {
      // Sync Status Header Card
      Surface(
        color = if (pendingCount == 0) Color(0xFFE8F5E9) else Color(0xFFFFF8E1),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Icon(
              imageVector = Icons.Default.CloudSync,
              contentDescription = null,
              tint = if (pendingCount == 0) NflGreen else NflAmber
            )
            Column {
              Text(
                text = if (pendingCount == 0) "All entries synced with backend" else "$pendingCount pending entries in offline queue",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = if (pendingCount == 0) NflGreen else Color(0xFFB78103)
              )
              Text(
                text = "Each submission maintains a stable UUID for idempotent deduplication.",
                fontSize = 11.sp,
                color = Color(0xFF607D8B)
              )
            }
          }

          if (pendingCount > 0) {
            Button(
              onClick = { repository.retryPendingSubmissions() },
              colors = ButtonDefaults.buttonColors(containerColor = NflAmber),
              shape = RoundedCornerShape(6.dp)
            ) {
              Text("Retry Now", fontSize = 12.sp)
            }
          }
        }
      }

      // Tab Row
      TabRow(
        selectedTabIndex = selectedTab,
        containerColor = Color.White,
        contentColor = NflRoyalBlue
      ) {
        Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Production (${prodEntries.size})") })
        Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Quality (${qualityEntries.size})") })
        Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("Audit Trail (${auditLogs.size})") })
      }

      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        when (selectedTab) {
          0 -> {
            if (prodEntries.isEmpty()) {
              item { Text("No production submissions yet.", color = Color(0xFF90A4AE)) }
            } else {
              items(prodEntries) { item ->
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
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                      Text("Line ${item.lineId} • ${item.hourSlot}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                      Text("Style: ${item.styleNumber} • ${item.date}", fontSize = 11.sp, color = Color(0xFF78909C))
                      Text("Submission ID: ${item.submissionId.take(8)}... (Rev ${item.revision})", fontSize = 10.sp, color = Color(0xFF90A4AE))
                      if (item.remarks.isNotBlank()) {
                        Text("Remarks: ${item.remarks}", fontSize = 11.sp, color = Color(0xFF546E7A))
                      }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                      Text("${item.outputQty} pcs", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = NflRoyalBlue)
                      Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = when (item.syncStatus) {
                          SyncStatus.SYNCED -> Color(0xFFE8F5E9)
                          SyncStatus.PENDING -> Color(0xFFFFF8E1)
                          SyncStatus.FAILED -> Color(0xFFFFEBEE)
                        }
                      ) {
                        Text(
                          text = item.syncStatus.name,
                          fontSize = 10.sp,
                          fontWeight = FontWeight.Bold,
                          color = when (item.syncStatus) {
                            SyncStatus.SYNCED -> NflGreen
                            SyncStatus.PENDING -> NflAmber
                            SyncStatus.FAILED -> NflRed
                          },
                          modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                      }
                    }
                  }
                }
              }
            }
          }
          1 -> {
            if (qualityEntries.isEmpty()) {
              item { Text("No quality submissions yet.", color = Color(0xFF90A4AE)) }
            } else {
              items(qualityEntries) { item ->
                val oql = if (item.checkedGarments > 0) (item.defectiveGarments.toDouble() / item.checkedGarments) * 100.0 else 0.0

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
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                      Text("Line ${item.lineId} • ${item.hourSlot}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                      Text("Checked: ${item.checkedGarments} • Defective: ${item.defectiveGarments} • Defects: ${item.totalDefects}", fontSize = 11.sp, color = Color(0xFF78909C))
                      Text(
                        "Breakdown: " + item.defects.joinToString(", ") { "${it.defectType} (${it.count})" },
                        fontSize = 11.sp,
                        color = Color(0xFF546E7A)
                      )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                      Text("OQL ${"%.2f".format(oql)}%", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = if (oql <= 5.0) NflGreen else NflRed)
                      Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = when (item.syncStatus) {
                          SyncStatus.SYNCED -> Color(0xFFE8F5E9)
                          SyncStatus.PENDING -> Color(0xFFFFF8E1)
                          SyncStatus.FAILED -> Color(0xFFFFEBEE)
                        }
                      ) {
                        Text(
                          text = item.syncStatus.name,
                          fontSize = 10.sp,
                          fontWeight = FontWeight.Bold,
                          color = when (item.syncStatus) {
                            SyncStatus.SYNCED -> NflGreen
                            SyncStatus.PENDING -> NflAmber
                            SyncStatus.FAILED -> NflRed
                          },
                          modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                      }
                    }
                  }
                }
              }
            }
          }
          2 -> {
            items(auditLogs) { log ->
              Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(1.dp)
              ) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Text("${log.action} (${log.entityType})", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = NflNavyHeader)
                    Text("By: ${log.changedBy}", fontSize = 11.sp, color = Color(0xFF78909C))
                  }
                  Text(log.details, fontSize = 11.sp, color = Color(0xFF37474F))
                }
              }
            }
          }
        }
      }
    }
  }
}
