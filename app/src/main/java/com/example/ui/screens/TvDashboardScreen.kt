package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.NflRepository
import com.example.model.TvDashboardData
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun TvDashboardScreen(
  repository: NflRepository,
  onNavigateBack: () -> Unit
) {
  var data by remember { mutableStateOf(repository.getTvDashboardData()) }
  var selectedTab by remember { mutableStateOf("SUMMARY") }
  var isAutoRotateEnabled by remember { mutableStateOf(false) }
  var refreshCountdown by remember { mutableIntStateOf(30) }
  val isOnline by repository.isOnline.collectAsState()

  // Auto-refresh countdown every 1 second, reload data every 30s
  LaunchedEffect(Unit) {
    while (true) {
      delay(1000)
      if (refreshCountdown > 1) {
        refreshCountdown -= 1
      } else {
        refreshCountdown = 30
        data = repository.getTvDashboardData()
      }
    }
  }

  // Timed Tab rotation (if enabled on TV)
  LaunchedEffect(isAutoRotateEnabled) {
    if (isAutoRotateEnabled) {
      val tabs = listOf("SUMMARY", "PRODUCTION", "QUALITY", "NPT")
      var idx = tabs.indexOf(selectedTab).coerceAtLeast(0)
      while (isAutoRotateEnabled) {
        delay(15000) // 15 seconds per view
        idx = (idx + 1) % tabs.size
        selectedTab = tabs[idx]
      }
    }
  }

  // TV 16:9 Dark Blue Header and Slate Background Container
  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFFE9EDF2))
  ) {
    // Top Bar (Exact layout from Image 2)
    Surface(
      color = Color(0xFF003067),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // App Title & Back button
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.size(32.dp).testTag("tv_back_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
              tint = Color.White
            )
          }

          Text(
            text = "NFL | Live Production & Quality Performance",
            fontSize = 17.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
          )

          // Sample Data / Live Data Badge
          Surface(
            shape = RoundedCornerShape(4.dp),
            color = if (data.isDemoMode) Color(0xFFE53935) else NflGreen
          ) {
            Text(
              text = if (data.isDemoMode) "SAMPLE DATA" else "LIVE DATA",
              fontSize = 10.sp,
              fontWeight = FontWeight.ExtraBold,
              color = Color.White,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }

        // Date, Floor Filter, and Last Data Update
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
          ) {
            Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color(0xFFB0C4DE), modifier = Modifier.size(14.dp))
            Text("08 OCT 2026", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
          }

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
          ) {
            Icon(Icons.Default.Domain, contentDescription = null, tint = Color(0xFFB0C4DE), modifier = Modifier.size(14.dp))
            Text("Floor: ${data.floorName}", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
          }

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
          ) {
            Icon(Icons.Default.Schedule, contentDescription = null, tint = Color(0xFFB0C4DE), modifier = Modifier.size(14.dp))
            Column {
              Text("Last data update:", fontSize = 9.sp, color = Color(0xFFB0C4DE))
              Text(data.lastDataUpdate, fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }

    // Active NPT Alert Strip (Compact ticker when downtime is occurring)
    if (data.activeNptEvents.isNotEmpty()) {
      Surface(
        color = Color(0xFFD32F2F),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(Icons.Default.Warning, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
          val alertText = data.activeNptEvents.joinToString("  •  ") {
            "ACTIVE NPT on Line ${it.lineId}: ${it.reason} (${it.affectedManpower} people affected)"
          }
          Text(
            text = alertText,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
          )
        }
      }
    }

    // Stale / Offline warning if connection dropped
    if (!isOnline) {
      Surface(
        color = Color(0xFFF57F17),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 3.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Icon(Icons.Default.CloudOff, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
          Text(
            text = "Offline / Connection Lost. Displaying latest cached dashboard figures.",
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }

    // Main Dashboard Body (Scrollable or 16:9 dense layout)
    Column(
      modifier = Modifier
        .weight(1f)
        .verticalScroll(rememberScrollState())
        .padding(8.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      // Top Row of 7 KPI Cards (Matches Image 2 exactly)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .height(115.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        // 1. Today Efficiency (80.0%)
        KpiArcGaugeCard(
          title = "Today Efficiency",
          valueText = "${data.todayEfficiency}%",
          subLabel1Title = "Target",
          subLabel1Val = "${data.todayTargetEfficiency}%",
          subLabel2Title = "Achievement",
          subLabel2Val = "${data.todayEfficiency}%",
          percentage = (data.todayEfficiency / 100f).toFloat(),
          gaugeColor = NflPrimaryBlue,
          modifier = Modifier.weight(1f)
        )

        // 2. Monthly Efficiency (72.2%)
        KpiArcGaugeCard(
          title = "Monthly Efficiency",
          valueText = "${data.monthlyEfficiency}%",
          subLabel1Title = "Target",
          subLabel1Val = "${data.monthlyTargetEfficiency}%",
          subLabel2Title = "Achievement",
          subLabel2Val = "${data.monthlyEfficiency}%",
          percentage = (data.monthlyEfficiency / 100f).toFloat(),
          gaugeColor = NflPrimaryBlue,
          modifier = Modifier.weight(1f)
        )

        // 3. Operators / Machines (266 / 267)
        KpiDonutCard(
          title = "Operators / Machines",
          centerText = "${data.operatorsCount} / ${data.machinesCount}",
          label1 = "Operators",
          val1 = "${data.operatorsCount}",
          label2 = "Machines",
          val2 = "${data.machinesCount}",
          percentage = (data.operatorsCount.toFloat() / data.machinesCount.toFloat()).coerceIn(0f, 1f),
          modifier = Modifier.weight(1f)
        )

        // 4. Floor Efficiency (80.0% / 72.2%)
        KpiFloorEfficiencyCard(
          title = "Floor Efficiency",
          todayVal = data.todayFloorEfficiency,
          monthlyVal = data.monthlyFloorEfficiency,
          modifier = Modifier.weight(1f)
        )

        // 5. BS QA OQL (5.00%)
        KpiStatBoxCard(
          title = "BS QA OQL",
          statValue = "${"%.2f".format(data.bsQaOql)}%",
          sub1Title = "Sample Size",
          sub1Val = "${data.bsQaSampleSize}",
          sub2Title = "Acceptance",
          sub2Val = "${data.bsQaAcceptance}",
          statColor = NflPrimaryBlue,
          modifier = Modifier.weight(1f)
        )

        // 6. Today OQL (3.81%)
        KpiArcGaugeCard(
          title = "Today OQL",
          valueText = "${data.todayOql}%",
          subLabel1Title = "Target",
          subLabel1Val = "${data.todayTargetOql}%",
          subLabel2Title = "Actual",
          subLabel2Val = "${data.todayOql}%",
          percentage = (data.todayOql / 10f).toFloat(),
          gaugeColor = NflGreen,
          modifier = Modifier.weight(1f)
        )

        // 7. FRI Pass Rate (100.0%)
        KpiArcGaugeCard(
          title = "FRI Pass Rate",
          valueText = "${data.friPassRate.toInt()}%",
          subLabel1Title = "Target",
          subLabel1Val = "${data.friTargetPassRate.toInt()}%",
          subLabel2Title = "Actual",
          subLabel2Val = "${data.friPassRate.toInt()}%",
          percentage = 1.0f,
          gaugeColor = NflPrimaryBlue,
          modifier = Modifier.weight(1f)
        )
      }

      // Middle Row of Charts (Matches Image 2 Row 1):
      // 1. Live Target vs Achievement (Bar)
      // 2. Today vs Monthly OQL (Bar)
      // 3. Top 5 Defects (Horizontal Bar)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .height(200.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        // Live Target vs Achievement
        GroupedBarChartCard(
          title = "Live Target vs Achievement",
          legend1Text = "Target (Pcs)",
          legend1Color = NflPrimaryBlue,
          legend2Text = "Achievement (Pcs)",
          legend2Color = NflGreenBright,
          dataPoints = data.linePerformance.map { it.lineId to (it.targetPcs.toDouble() to it.achievementPcs.toDouble()) },
          maxValue = 10000.0,
          modifier = Modifier.weight(1.35f)
        )

        // Today vs Monthly OQL
        GroupedBarChartCard(
          title = "Today vs Monthly OQL",
          legend1Text = "Today OQL",
          legend1Color = NflPrimaryBlue,
          legend2Text = "Monthly OQL",
          legend2Color = NflCoral,
          dataPoints = data.linePerformance.map { it.lineId to (it.todayOqlPct to it.monthlyOqlPct) },
          maxValue = 8.0,
          isPercentage = true,
          modifier = Modifier.weight(1.15f)
        )

        // Top 5 Defects
        HorizontalBarsCard(
          title = "Top 5 Defects",
          tagText = "Today",
          items = data.topDefects.map { it.defectType to it.count.toDouble() },
          barColor = NflCoral,
          modifier = Modifier.weight(0.9f)
        )
      }

      // Bottom Row of Charts (Matches Image 2 Row 2):
      // 1. Line Efficiency: Today vs Monthly (Bar)
      // 2. Weekly BS OQL & NFL AQC (Line chart)
      // 3. Highest DHU Lines (Horizontal Bar)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .height(195.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        // Line Efficiency: Today vs Monthly
        GroupedBarChartCard(
          title = "Line Efficiency: Today vs Monthly",
          legend1Text = "Today",
          legend1Color = NflPrimaryBlue,
          legend2Text = "Monthly",
          legend2Color = NflCoral,
          dataPoints = data.linePerformance.map { it.lineId to (it.todayEfficiencyPct to it.monthlyEfficiencyPct) },
          maxValue = 100.0,
          isPercentage = true,
          modifier = Modifier.weight(1.35f)
        )

        // Weekly BS OQL & NFL AQC Trend
        LineChartCard(
          title = "Weekly BS OQL & NFL AQC",
          legend1Text = "BS OQL",
          legend1Color = NflPrimaryBlue,
          legend2Text = "NFL AQC",
          legend2Color = NflCoral,
          trendData = data.weeklyQaTrend,
          modifier = Modifier.weight(1.15f)
        )

        // Highest DHU Lines
        HorizontalBarsCard(
          title = "Highest DHU Lines",
          tagText = "Today",
          items = data.highestDhuLines.map { it.lineId to it.dhu },
          barColor = NflPrimaryBlue,
          modifier = Modifier.weight(0.9f)
        )
      }
    }

    // Bottom Navigation & Status Bar (Matches Image 2 Bottom)
    Surface(
      color = Color(0xFF001F45),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Views Tabs: SUMMARY, PRODUCTION, QUALITY, NPT
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
          listOf("SUMMARY", "PRODUCTION", "QUALITY", "NPT").forEach { tab ->
            val isSelected = selectedTab == tab
            Text(
              text = tab,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = if (isSelected) Color.White else Color(0xFF78909C),
              modifier = Modifier
                .clickable { selectedTab = tab }
                .padding(vertical = 4.dp)
            )
          }
        }

        // TV Rotation & Refresh status & Connected Indicator
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.clickable { isAutoRotateEnabled = !isAutoRotateEnabled }
          ) {
            Icon(
              imageVector = Icons.Default.Autorenew,
              contentDescription = null,
              tint = if (isAutoRotateEnabled) NflGreenBright else Color(0xFF78909C),
              modifier = Modifier.size(14.dp)
            )
            Text(
              text = if (isAutoRotateEnabled) "Auto-Rotate: ON (15s)" else "Auto-Rotate: OFF",
              fontSize = 11.sp,
              color = if (isAutoRotateEnabled) Color.White else Color(0xFF78909C)
            )
          }

          Text(
            text = "Refresh in ${refreshCountdown}s",
            fontSize = 11.sp,
            color = Color(0xFF90A4AE)
          )

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Box(
              modifier = Modifier
                .size(9.dp)
                .clip(CircleShape)
                .background(if (isOnline) NflGreenBright else NflAmber)
            )
            Text(
              text = if (isOnline) "Connected" else "Offline Cache",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }
        }
      }
    }
  }
}
