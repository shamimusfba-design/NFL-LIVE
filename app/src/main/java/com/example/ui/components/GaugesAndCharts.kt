package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun KpiArcGaugeCard(
  title: String,
  valueText: String,
  subLabel1Title: String,
  subLabel1Val: String,
  subLabel2Title: String,
  subLabel2Val: String,
  percentage: Float,
  gaugeColor: Color = NflPrimaryBlue,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(8.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(8.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = title,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF263238)
        )
        Icon(
          imageVector = Icons.Default.Info,
          contentDescription = "Info",
          tint = Color(0xFF90A4AE),
          modifier = Modifier.size(13.dp)
        )
      }

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(58.dp),
        contentAlignment = Alignment.BottomCenter
      ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
          val strokeWidth = 10f
          val diameter = size.height * 1.8f
          val topLeft = Offset((size.width - diameter) / 2f, size.height - diameter / 2f + 4f)
          val arcSize = Size(diameter, diameter)

          // Background track
          drawArc(
            color = Color(0xFFECEFF1),
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
          )

          // Value arc
          val sweep = (percentage.coerceIn(0f, 1f)) * 180f
          drawArc(
            color = gaugeColor,
            startAngle = 180f,
            sweepAngle = sweep,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
          )
        }

        Text(
          text = valueText,
          fontSize = 16.sp,
          fontWeight = FontWeight.ExtraBold,
          color = Color(0xFF102A43),
          modifier = Modifier.padding(bottom = 2.dp)
        )
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column(horizontalAlignment = Alignment.Start) {
          Text(text = subLabel1Title, fontSize = 9.sp, color = Color(0xFF78909C))
          Text(
            text = subLabel1Val,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF263238)
          )
        }
        Column(horizontalAlignment = Alignment.End) {
          Text(text = subLabel2Title, fontSize = 9.sp, color = Color(0xFF78909C))
          Text(
            text = subLabel2Val,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = gaugeColor
          )
        }
      }
    }
  }
}

@Composable
fun KpiDonutCard(
  title: String,
  centerText: String,
  label1: String,
  val1: String,
  label2: String,
  val2: String,
  percentage: Float = 0.95f,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(8.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(8.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = title,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF263238)
        )
        Icon(
          imageVector = Icons.Default.Info,
          contentDescription = "Info",
          tint = Color(0xFF90A4AE),
          modifier = Modifier.size(13.dp)
        )
      }

      Box(
        modifier = Modifier
          .size(54.dp),
        contentAlignment = Alignment.Center
      ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
          val stroke = 8f
          drawArc(
            color = Color(0xFFE2E8F0),
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(stroke)
          )
          drawArc(
            color = NflPrimaryBlue,
            startAngle = -90f,
            sweepAngle = 360f * percentage,
            useCenter = false,
            style = Stroke(stroke, cap = StrokeCap.Round)
          )
        }
        Text(
          text = centerText,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF102A43)
        )
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column(horizontalAlignment = Alignment.Start) {
          Text(text = label1, fontSize = 9.sp, color = Color(0xFF78909C))
          Text(
            text = val1,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF263238)
          )
        }
        Column(horizontalAlignment = Alignment.End) {
          Text(text = label2, fontSize = 9.sp, color = Color(0xFF78909C))
          Text(
            text = val2,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF263238)
          )
        }
      }
    }
  }
}

@Composable
fun KpiFloorEfficiencyCard(
  title: String,
  todayVal: Double,
  monthlyVal: Double,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(8.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(8.dp),
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = title,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF263238)
        )
        Icon(
          imageVector = Icons.Default.Info,
          contentDescription = "Info",
          tint = Color(0xFF90A4AE),
          modifier = Modifier.size(13.dp)
        )
      }

      Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        // Today bar
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("Today", fontSize = 9.sp, color = Color(0xFF78909C))
          Text("${todayVal}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NflPrimaryBlue)
        }
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .background(Color(0xFFECEFF1), RoundedCornerShape(4.dp))
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth((todayVal / 100.0).toFloat().coerceIn(0f, 1f))
              .fillMaxHeight()
              .background(NflPrimaryBlue, RoundedCornerShape(4.dp))
          )
        }

        // Monthly bar
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("Monthly", fontSize = 9.sp, color = Color(0xFF78909C))
          Text("${monthlyVal}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NflNavyHeader)
        }
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .background(Color(0xFFECEFF1), RoundedCornerShape(4.dp))
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth((monthlyVal / 100.0).toFloat().coerceIn(0f, 1f))
              .fillMaxHeight()
              .background(NflNavyHeader, RoundedCornerShape(4.dp))
          )
        }
      }
    }
  }
}

@Composable
fun KpiStatBoxCard(
  title: String,
  statValue: String,
  sub1Title: String,
  sub1Val: String,
  sub2Title: String,
  sub2Val: String,
  statColor: Color = NflPrimaryBlue,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(8.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(8.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = title,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF263238)
        )
        Icon(
          imageVector = Icons.Default.Info,
          contentDescription = "Info",
          tint = Color(0xFF90A4AE),
          modifier = Modifier.size(13.dp)
        )
      }

      Text(
        text = statValue,
        fontSize = 22.sp,
        fontWeight = FontWeight.Black,
        color = statColor
      )

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column(horizontalAlignment = Alignment.Start) {
          Text(text = sub1Title, fontSize = 9.sp, color = Color(0xFF78909C))
          Text(
            text = sub1Val,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF263238)
          )
        }
        Column(horizontalAlignment = Alignment.End) {
          Text(text = sub2Title, fontSize = 9.sp, color = Color(0xFF78909C))
          Text(
            text = sub2Val,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF263238)
          )
        }
      }
    }
  }
}

@Composable
fun GroupedBarChartCard(
  title: String,
  legend1Text: String,
  legend1Color: Color,
  legend2Text: String,
  legend2Color: Color,
  dataPoints: List<Pair<String, Pair<Double, Double>>>,
  maxValue: Double,
  isPercentage: Boolean = false,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(8.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(8.dp)
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = title,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF102A43)
        )
        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Box(modifier = Modifier.size(7.dp).background(legend1Color, RoundedCornerShape(1.dp)))
            Text(legend1Text, fontSize = 9.sp, color = Color(0xFF546E7A))
          }
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Box(modifier = Modifier.size(7.dp).background(legend2Color, RoundedCornerShape(1.dp)))
            Text(legend2Text, fontSize = 9.sp, color = Color(0xFF546E7A))
          }
        }
      }

      Spacer(modifier = Modifier.height(4.dp))

      // Canvas chart
      Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
          val bottomPadding = 30f
          val leftPadding = 40f
          val chartWidth = size.width - leftPadding
          val chartHeight = size.height - bottomPadding

          // Grid lines
          val gridCount = 4
          for (i in 0..gridCount) {
            val y = chartHeight - (i * chartHeight / gridCount)
            drawLine(
              color = Color(0xFFECEFF1),
              start = Offset(leftPadding, y),
              end = Offset(size.width, y),
              strokeWidth = 1f
            )
          }

          if (dataPoints.isEmpty()) return@Canvas

          val groupWidth = chartWidth / dataPoints.size
          val barWidth = (groupWidth * 0.35f).coerceAtLeast(3f)

          dataPoints.forEachIndexed { index, (label, values) ->
            val groupCenterX = leftPadding + (index * groupWidth) + (groupWidth / 2f)

            // Bar 1
            val h1 = ((values.first / maxValue).toFloat().coerceIn(0f, 1f)) * chartHeight
            val x1 = groupCenterX - barWidth - 1f
            val y1 = chartHeight - h1
            drawRect(
              color = legend1Color,
              topLeft = Offset(x1, y1),
              size = Size(barWidth, h1)
            )

            // Bar 2
            val h2 = ((values.second / maxValue).toFloat().coerceIn(0f, 1f)) * chartHeight
            val x2 = groupCenterX + 1f
            val y2 = chartHeight - h2
            drawRect(
              color = legend2Color,
              topLeft = Offset(x2, y2),
              size = Size(barWidth, h2)
            )
          }
        }

        // X-axis labels
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter)
            .padding(start = 16.dp),
          horizontalArrangement = Arrangement.SpaceAround
        ) {
          dataPoints.forEach { (label, _) ->
            Text(
              text = label,
              fontSize = 9.sp,
              fontWeight = FontWeight.SemiBold,
              color = Color(0xFF455A64),
              textAlign = TextAlign.Center
            )
          }
        }
      }
    }
  }
}

@Composable
fun HorizontalBarsCard(
  title: String,
  tagText: String = "Today",
  items: List<Pair<String, Double>>,
  barColor: Color = NflCoral,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(8.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(8.dp),
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = title,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF102A43)
        )
        Text(tagText, fontSize = 9.sp, color = Color(0xFF78909C))
      }

      val maxVal = items.maxOfOrNull { it.second }?.coerceAtLeast(1.0) ?: 1.0

      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        items.forEach { (label, value) ->
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Text(
              text = label,
              fontSize = 9.sp,
              fontWeight = FontWeight.Medium,
              color = Color(0xFF37474F),
              modifier = Modifier.width(62.dp)
            )
            Box(
              modifier = Modifier
                .weight(1f)
                .height(11.dp)
                .background(Color(0xFFF1F5F9), RoundedCornerShape(2.dp))
            ) {
              Box(
                modifier = Modifier
                  .fillMaxWidth((value / maxVal).toFloat().coerceIn(0f, 1f))
                  .fillMaxHeight()
                  .background(barColor, RoundedCornerShape(2.dp))
              )
            }
            Text(
              text = "${value.toInt()}",
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF102A43),
              modifier = Modifier.width(18.dp),
              textAlign = TextAlign.End
            )
          }
        }
      }
    }
  }
}

@Composable
fun LineChartCard(
  title: String,
  legend1Text: String,
  legend1Color: Color,
  legend2Text: String,
  legend2Color: Color,
  trendData: List<DailyQaTrend>,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(8.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(8.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = title,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF102A43)
        )
        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Box(modifier = Modifier.size(7.dp).background(legend1Color, RoundedCornerShape(3.dp)))
            Text(legend1Text, fontSize = 9.sp, color = Color(0xFF546E7A))
          }
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Box(modifier = Modifier.size(7.dp).background(legend2Color, RoundedCornerShape(3.dp)))
            Text(legend2Text, fontSize = 9.sp, color = Color(0xFF546E7A))
          }
        }
      }

      Spacer(modifier = Modifier.height(4.dp))

      Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
          val bottomPadding = 30f
          val leftPadding = 30f
          val chartWidth = size.width - leftPadding
          val chartHeight = size.height - bottomPadding
          val maxVal = 8.0 // 8% top

          // Horizontal grid
          for (i in 0..4) {
            val y = chartHeight - (i * chartHeight / 4)
            drawLine(
              color = Color(0xFFECEFF1),
              start = Offset(leftPadding, y),
              end = Offset(size.width, y),
              strokeWidth = 1f
            )
          }

          if (trendData.size < 2) return@Canvas

          val stepX = chartWidth / (trendData.size - 1)

          // Line 1: BS OQL
          for (i in 0 until trendData.size - 1) {
            val x1 = leftPadding + (i * stepX)
            val y1 = chartHeight - ((trendData[i].bsOqlPct / maxVal).toFloat() * chartHeight)
            val x2 = leftPadding + ((i + 1) * stepX)
            val y2 = chartHeight - ((trendData[i + 1].bsOqlPct / maxVal).toFloat() * chartHeight)
            drawLine(
              color = legend1Color,
              start = Offset(x1, y1),
              end = Offset(x2, y2),
              strokeWidth = 3f,
              cap = StrokeCap.Round
            )
            drawCircle(
              color = legend1Color,
              radius = 3.5f,
              center = Offset(x1, y1)
            )
            if (i == trendData.size - 2) {
              drawCircle(
                color = legend1Color,
                radius = 3.5f,
                center = Offset(x2, y2)
              )
            }
          }

          // Line 2: NFL AQC
          for (i in 0 until trendData.size - 1) {
            val x1 = leftPadding + (i * stepX)
            val y1 = chartHeight - ((trendData[i].nflAqcPct / maxVal).toFloat() * chartHeight)
            val x2 = leftPadding + ((i + 1) * stepX)
            val y2 = chartHeight - ((trendData[i + 1].nflAqcPct / maxVal).toFloat() * chartHeight)
            drawLine(
              color = legend2Color,
              start = Offset(x1, y1),
              end = Offset(x2, y2),
              strokeWidth = 3f,
              cap = StrokeCap.Round
            )
            drawCircle(
              color = legend2Color,
              radius = 3.5f,
              center = Offset(x1, y1)
            )
            if (i == trendData.size - 2) {
              drawCircle(
                color = legend2Color,
                radius = 3.5f,
                center = Offset(x2, y2)
              )
            }
          }
        }

        // X-axis date labels
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter)
            .padding(start = 12.dp),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          trendData.forEach {
            Text(
              text = it.dateLabel,
              fontSize = 8.sp,
              color = Color(0xFF546E7A)
            )
          }
        }
      }
    }
  }
}
