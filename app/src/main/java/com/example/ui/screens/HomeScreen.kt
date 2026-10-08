package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.NflRepository
import com.example.model.UserRole
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
  repository: NflRepository,
  onNavigateToProduction: () -> Unit,
  onNavigateToQuality: () -> Unit,
  onNavigateToNpt: () -> Unit,
  onNavigateToSetup: () -> Unit,
  onNavigateToTv: () -> Unit,
  onNavigateToHistory: () -> Unit,
  onNavigateToSettings: () -> Unit
) {
  val assignedLine by repository.assignedLine.collectAsState()
  val currentRole by repository.currentRole.collectAsState()
  val isOnline by repository.isOnline.collectAsState()
  val isDemoMode by repository.isDemoMode.collectAsState()
  val tvData = remember { repository.getTvDashboardData() }
  val activeNpt = repository.nptEvents.collectAsState().value.filter { !it.isClosed }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = "NFL Production & Quality",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 17.sp
            )
            Text(
              text = "Factory Floor System • Sewing 2",
              color = Color(0xFFB0C4DE),
              fontSize = 11.sp
            )
          }
        },
        actions = {
          IconButton(onClick = onNavigateToSettings, modifier = Modifier.testTag("home_settings_btn")) {
            Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
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
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Header Station Badge Card
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
      ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .clip(CircleShape)
                  .background(Color(0xFFE3F2FD)),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.PrecisionManufacturing, contentDescription = null, tint = NflRoyalBlue, modifier = Modifier.size(20.dp))
              }
              Column {
                Text(
                  text = "Assigned Line: $assignedLine",
                  fontSize = 15.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF102A43)
                )
                Text(
                  text = "Role: ${currentRole.displayName}",
                  fontSize = 12.sp,
                  color = Color(0xFF627D98)
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(6.dp),
              color = if (isDemoMode) Color(0xFFFFEBEE) else Color(0xFFE8F5E9)
            ) {
              Text(
                text = if (isDemoMode) "DEMO MODE" else "LIVE MODE",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDemoMode) Color(0xFFD32F2F) else NflGreen,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }

          // Active NPT Banner if any
          if (activeNpt.isNotEmpty()) {
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = Color(0xFFFFEBEE),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Icon(Icons.Default.Warning, contentDescription = null, tint = NflRed, modifier = Modifier.size(16.dp))
                Text(
                  text = "${activeNpt.size} active downtime stoppage(s) recorded",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = NflRed
                )
              }
            }
          }
        }
      }

      // Quick KPI Snapshot
      Text(
        text = "Floor Performance Snapshot",
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF334E68)
      )

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        KpiMiniCard(
          title = "Today Efficiency",
          value = "${tvData.todayEfficiency}%",
          subtitle = "Target: ${tvData.todayTargetEfficiency}%",
          accentColor = NflPrimaryBlue,
          modifier = Modifier.weight(1f)
        )
        KpiMiniCard(
          title = "Today OQL",
          value = "${tvData.todayOql}%",
          subtitle = "Target: ${tvData.todayTargetOql}%",
          accentColor = NflGreen,
          modifier = Modifier.weight(1f)
        )
        KpiMiniCard(
          title = "FRI Pass Rate",
          value = "${tvData.friPassRate.toInt()}%",
          subtitle = "Target: 98%",
          accentColor = NflNavyHeader,
          modifier = Modifier.weight(1f)
        )
      }

      // Primary Operation Cards (Large touch-friendly buttons)
      Text(
        text = "Factory Modules",
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF334E68)
      )

      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        ModuleActionCard(
          title = "Production Entry",
          description = "Submit hourly sewing output quantity for Line $assignedLine",
          icon = Icons.Default.PrecisionManufacturing,
          buttonText = "Enter Production",
          buttonColor = NflRoyalBlue,
          onClick = onNavigateToProduction,
          testTag = "home_production_card"
        )

        ModuleActionCard(
          title = "Quality Inspection Entry",
          description = "Log checked garments, defect breakdown and compute OQL/DHU",
          icon = Icons.Default.CheckCircle,
          buttonText = "Enter Quality",
          buttonColor = NflGreen,
          onClick = onNavigateToQuality,
          testTag = "home_quality_card"
        )

        ModuleActionCard(
          title = "NPT Stopwatch & Stoppages",
          description = "Live downtime tracker, start/stop NPT events and lost man-minutes",
          icon = Icons.Default.Timer,
          buttonText = "NPT Tracking",
          buttonColor = NflRed,
          onClick = onNavigateToNpt,
          testTag = "home_npt_card"
        )

        ModuleActionCard(
          title = "Supervisor Line Setup & SMV",
          description = "Configure style SMV, target output and operator/helper policy",
          icon = Icons.Default.AdminPanelSettings,
          buttonText = "Line Setup",
          buttonColor = Color(0xFF37474F),
          onClick = onNavigateToSetup,
          testTag = "home_setup_card"
        )

        ModuleActionCard(
          title = "Android Smart TV Dashboard",
          description = "16:9 fullscreen performance dashboard for factory floor display",
          icon = Icons.Default.Tv,
          buttonText = "Launch TV Dashboard",
          buttonColor = Color(0xFF003067),
          onClick = onNavigateToTv,
          testTag = "home_tv_card"
        )
      }
    }
  }
}

@Composable
private fun KpiMiniCard(
  title: String,
  value: String,
  subtitle: String,
  accentColor: Color,
  modifier: Modifier = Modifier
) {
  Card(
    shape = RoundedCornerShape(8.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(1.dp),
    modifier = modifier
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp),
      verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
      Text(title, fontSize = 11.sp, color = Color(0xFF78909C), maxLines = 1)
      Text(value, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = accentColor)
      Text(subtitle, fontSize = 9.sp, color = Color(0xFF90A4AE))
    }
  }
}

@Composable
private fun ModuleActionCard(
  title: String,
  description: String,
  icon: ImageVector,
  buttonText: String,
  buttonColor: Color,
  onClick: () -> Unit,
  testTag: String
) {
  Card(
    shape = RoundedCornerShape(10.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(2.dp),
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .testTag(testTag)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        modifier = Modifier.weight(1f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Box(
          modifier = Modifier
            .size(42.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(buttonColor.copy(alpha = 0.12f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(icon, contentDescription = null, tint = buttonColor, modifier = Modifier.size(24.dp))
        }

        Column {
          Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF102A43))
          Text(description, fontSize = 11.sp, color = Color(0xFF627D98), maxLines = 2)
        }
      }

      Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
        shape = RoundedCornerShape(6.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
      ) {
        Text(buttonText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
      }
    }
  }
}
