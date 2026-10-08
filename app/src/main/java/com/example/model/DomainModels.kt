package com.example.model

import java.util.UUID

enum class SyncStatus {
  SYNCED,
  PENDING,
  FAILED
}

enum class UserRole(val displayName: String) {
  ADMIN_IE("Admin / IE"),
  SUPERVISOR("Supervisor"),
  PRODUCTION("Production Operator"),
  QUALITY("Quality Inspector"),
  MANAGEMENT_TV("Management / TV Display")
}

data class LineInfo(
  val id: String,
  val floor: String,
  val aliases: List<String> = emptyList(),
  val isActive: Boolean = true
) {
  fun matches(lineInput: String): Boolean {
    val trimmed = lineInput.trim()
    return id.equals(trimmed, ignoreCase = true) ||
        aliases.any { it.equals(trimmed, ignoreCase = true) }
  }
}

data class StyleInfo(
  val styleNumber: String,
  val buyer: String,
  val smv: Double, // Standard Minute Value (Strictly SMV terminology)
  val defaultTargetPcsPerHour: Int
)

data class ProductionRecord(
  val id: String = UUID.randomUUID().toString(),
  val submissionId: String = UUID.randomUUID().toString(),
  val lineId: String,
  val date: String,
  val hourSlot: String,
  val styleNumber: String,
  val outputQty: Int,
  val remarks: String = "",
  val timestamp: Long = System.currentTimeMillis(),
  val syncStatus: SyncStatus = SyncStatus.SYNCED,
  val revision: Int = 1,
  val modifiedBy: String = "Production",
  val isConfirmedZero: Boolean = false
)

data class DefectItem(
  val defectType: String,
  val count: Int
)

data class QualityRecord(
  val id: String = UUID.randomUUID().toString(),
  val submissionId: String = UUID.randomUUID().toString(),
  val lineId: String,
  val date: String,
  val hourSlot: String,
  val styleNumber: String,
  val checkedGarments: Int,
  val defectiveGarments: Int,
  val defects: List<DefectItem> = emptyList(),
  val totalDefects: Int = defects.sumOf { it.count },
  val remarks: String = "",
  val timestamp: Long = System.currentTimeMillis(),
  val syncStatus: SyncStatus = SyncStatus.SYNCED,
  val revision: Int = 1,
  val modifiedBy: String = "Quality",
  val isConfirmedZero: Boolean = false
)

data class SupervisorLineSetup(
  val id: String = UUID.randomUUID().toString(),
  val lineId: String,
  val date: String,
  val styleNumber: String,
  val orderNumber: String,
  val smv: Double,
  val targetHourlyPcs: Int,
  val targetEfficiencyPct: Double,
  val operatorsCount: Int,
  val helpersCount: Int,
  val includeHelpersInEfficiency: Boolean = false,
  val workingMinutesPerHour: Int = 60,
  val snapshotTimestamp: Long = System.currentTimeMillis()
) {
  val includedManpower: Int
    get() = operatorsCount + (if (includeHelpersInEfficiency) helpersCount else 0)
}

data class NptEvent(
  val id: String = UUID.randomUUID().toString(),
  val submissionId: String = UUID.randomUUID().toString(),
  val lineId: String,
  val styleNumber: String,
  val reason: String,
  val isFullLine: Boolean,
  val affectedManpower: Int,
  val machineOrOpRef: String = "",
  val remarks: String = "",
  val startTimeMillis: Long = System.currentTimeMillis(),
  val endTimeMillis: Long? = null,
  val isClosed: Boolean = false,
  val lostManMinutes: Double = 0.0,
  val overlapFlagged: Boolean = false,
  val syncStatus: SyncStatus = SyncStatus.SYNCED,
  val createdBy: String = "Supervisor"
)

data class AuditLogEntry(
  val id: String = UUID.randomUUID().toString(),
  val timestamp: Long = System.currentTimeMillis(),
  val entityType: String,
  val entityId: String,
  val action: String,
  val changedBy: String,
  val details: String
)

data class LinePerformancePoint(
  val lineId: String,
  val targetPcs: Int,
  val achievementPcs: Int,
  val todayOqlPct: Double,
  val monthlyOqlPct: Double,
  val todayEfficiencyPct: Double,
  val monthlyEfficiencyPct: Double,
  val dhu: Double
)

data class DefectSummary(
  val defectType: String,
  val count: Int
)

data class LineDhuSummary(
  val lineId: String,
  val dhu: Double
)

data class DailyQaTrend(
  val dateLabel: String,
  val bsOqlPct: Double,
  val nflAqcPct: Double
)

data class TvDashboardData(
  val todayEfficiency: Double = 80.0,
  val todayTargetEfficiency: Double = 75.0,
  val monthlyEfficiency: Double = 72.2,
  val monthlyTargetEfficiency: Double = 70.0,
  val operatorsCount: Int = 266,
  val machinesCount: Int = 267,
  val todayFloorEfficiency: Double = 80.0,
  val monthlyFloorEfficiency: Double = 72.2,
  val bsQaOql: Double = 5.00,
  val bsQaSampleSize: Int = 125,
  val bsQaAcceptance: Int = 5,
  val todayOql: Double = 3.81,
  val todayTargetOql: Double = 5.00,
  val friPassRate: Double = 100.0,
  val friTargetPassRate: Double = 98.0,
  val lastDataUpdate: String = "06:49 PM",
  val lastRefreshTime: String = "Just now",
  val isStaleOrOffline: Boolean = false,
  val isDemoMode: Boolean = true,
  val floorName: String = "Sewing 2",
  val linePerformance: List<LinePerformancePoint> = emptyList(),
  val topDefects: List<DefectSummary> = emptyList(),
  val highestDhuLines: List<LineDhuSummary> = emptyList(),
  val weeklyQaTrend: List<DailyQaTrend> = emptyList(),
  val activeNptEvents: List<NptEvent> = emptyList()
)
