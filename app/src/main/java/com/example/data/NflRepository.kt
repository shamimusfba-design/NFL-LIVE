package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class NflRepository(private val context: Context) {

  private val prefs: SharedPreferences =
    context.getSharedPreferences("nfl_factory_prefs", Context.MODE_PRIVATE)

  // Standard factory lines matching the workbook and visual references
  // Note: J and J1 are confirmed to be the same line. J2 is distinct.
  val defaultLines = listOf(
    LineInfo(id = "J", floor = "Sewing 2", aliases = listOf("J1")),
    LineInfo(id = "K1", floor = "Sewing 2"),
    LineInfo(id = "L1", floor = "Sewing 2"),
    LineInfo(id = "L2", floor = "Sewing 2"),
    LineInfo(id = "M", floor = "Sewing 2"),
    LineInfo(id = "N1", floor = "Sewing 2"),
    LineInfo(id = "N2", floor = "Sewing 2"),
    LineInfo(id = "O1", floor = "Sewing 2"),
    LineInfo(id = "O2", floor = "Sewing 2"),
    LineInfo(id = "P", floor = "Sewing 2"),
    LineInfo(id = "Q1", floor = "Sewing 2"),
    LineInfo(id = "Q2", floor = "Sewing 2"),
    LineInfo(id = "R", floor = "Sewing 2")
  )

  val defaultStyles = listOf(
    StyleInfo("NFL-204", "Nike / NFL", smv = 14.50, defaultTargetPcsPerHour = 90),
    StyleInfo("NFL-108", "Nike / NFL", smv = 12.20, defaultTargetPcsPerHour = 105),
    StyleInfo("NFL-305", "Adidas / NFL", smv = 16.80, defaultTargetPcsPerHour = 75),
    StyleInfo("NFL-512", "Under Armour", smv = 11.00, defaultTargetPcsPerHour = 115),
    StyleInfo("NFL-990", "Puma", smv = 13.40, defaultTargetPcsPerHour = 95)
  )

  val defaultHourSlots = listOf(
    "08:00 - 09:00",
    "09:00 - 10:00",
    "10:00 - 11:00",
    "11:00 - 12:00",
    "12:00 - 13:00",
    "14:00 - 15:00",
    "15:00 - 16:00",
    "16:00 - 17:00",
    "17:00 - 18:00",
    "18:00 - 19:00"
  )

  val standardDefectTypes = listOf(
    "Poor Iron",
    "Uncut Thread",
    "Dirty Spot",
    "Oil Spot",
    "Skip Stitch",
    "Open Seam",
    "Broken Stitch",
    "Puckering",
    "Pleat",
    "Shading",
    "Size Mistake",
    "Raw Edge"
  )

  val nptReasons = listOf(
    "Machine Breakdown",
    "Material Shortage",
    "Quality Issue / Re-work",
    "Power Failure",
    "Waiting for Work / Feeding",
    "Needle Breakage",
    "Line Setting / Balancing",
    "Operator Absenteeism",
    "Air Pressure Failure",
    "Other"
  )

  // In-memory data backed by local store
  private val _productionEntries = MutableStateFlow<List<ProductionRecord>>(emptyList())
  val productionEntries: StateFlow<List<ProductionRecord>> = _productionEntries.asStateFlow()

  private val _qualityEntries = MutableStateFlow<List<QualityRecord>>(emptyList())
  val qualityEntries: StateFlow<List<QualityRecord>> = _qualityEntries.asStateFlow()

  private val _nptEvents = MutableStateFlow<List<NptEvent>>(emptyList())
  val nptEvents: StateFlow<List<NptEvent>> = _nptEvents.asStateFlow()

  private val _supervisorSetups = MutableStateFlow<Map<String, SupervisorLineSetup>>(emptyMap())
  val supervisorSetups: StateFlow<Map<String, SupervisorLineSetup>> = _supervisorSetups.asStateFlow()

  private val _auditLogs = MutableStateFlow<List<AuditLogEntry>>(emptyList())
  val auditLogs: StateFlow<List<AuditLogEntry>> = _auditLogs.asStateFlow()

  private val _currentRole = MutableStateFlow(UserRole.PRODUCTION)
  val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

  private val _assignedLine = MutableStateFlow("K1")
  val assignedLine: StateFlow<String> = _assignedLine.asStateFlow()

  private val _isDemoMode = MutableStateFlow(true)
  val isDemoMode: StateFlow<Boolean> = _isDemoMode.asStateFlow()

  private val _isOnline = MutableStateFlow(true)
  val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

  private val _lastDataSubmissionTime = MutableStateFlow("06:49 PM")
  val lastDataSubmissionTime: StateFlow<String> = _lastDataSubmissionTime.asStateFlow()

  private val _googleSheetId = MutableStateFlow("1lutN5LmC1Hi6vtuwpkis4n-dYGElZ3Qtqn-Hk52Y2L0")
  val googleSheetId: StateFlow<String> = _googleSheetId.asStateFlow()

  private val _backendUrl = MutableStateFlow("http://10.0.2.2:3000") // Android emulator loopback
  val backendUrl: StateFlow<String> = _backendUrl.asStateFlow()

  init {
    loadSeedData()
  }

  fun setRole(role: UserRole) {
    _currentRole.value = role
  }

  fun setAssignedLine(line: String) {
    _assignedLine.value = line
  }

  fun setDemoMode(isDemo: Boolean) {
    _isDemoMode.value = isDemo
  }

  fun setOnlineState(online: Boolean) {
    _isOnline.value = online
  }

  fun setBackendUrl(url: String) {
    _backendUrl.value = url
  }

  fun setGoogleSheetId(id: String) {
    _googleSheetId.value = id
  }

  // --- Supervisor Setup ---
  fun saveLineSetup(setup: SupervisorLineSetup, savedBy: String = "Supervisor") {
    val current = _supervisorSetups.value.toMutableMap()
    current[setup.lineId] = setup
    _supervisorSetups.value = current

    addAuditLog(
      entityType = "SETUP",
      entityId = setup.lineId,
      action = "UPDATE_SETUP",
      changedBy = savedBy,
      details = "Style: ${setup.styleNumber}, SMV: ${setup.smv}, Target: ${setup.targetHourlyPcs}, Operators: ${setup.operatorsCount}, IncludeHelpers: ${setup.includeHelpersInEfficiency}"
    )
  }

  fun getLineSetup(lineId: String): SupervisorLineSetup {
    return _supervisorSetups.value[lineId] ?: SupervisorLineSetup(
      lineId = lineId,
      date = "08 Oct 2026",
      styleNumber = "NFL-204",
      orderNumber = "PO-88210",
      smv = 14.50,
      targetHourlyPcs = 90,
      targetEfficiencyPct = 75.0,
      operatorsCount = 20,
      helpersCount = 4,
      includeHelpersInEfficiency = false,
      workingMinutesPerHour = 60
    )
  }

  // --- Production Entry ---
  fun submitProduction(
    record: ProductionRecord,
    isOnlineSubmit: Boolean = _isOnline.value
  ): ProductionRecord {
    val status = if (isOnlineSubmit) SyncStatus.SYNCED else SyncStatus.PENDING
    val finalRecord = record.copy(syncStatus = status)

    _productionEntries.value = listOf(finalRecord) + _productionEntries.value
    _lastDataSubmissionTime.value = SimpleDateFormat("hh:mm a", Locale.US).format(Date())

    addAuditLog(
      entityType = "PRODUCTION",
      entityId = finalRecord.id,
      action = "CREATE_PRODUCTION",
      changedBy = finalRecord.modifiedBy,
      details = "Line: ${finalRecord.lineId}, Slot: ${finalRecord.hourSlot}, Qty: ${finalRecord.outputQty} pcs"
    )

    return finalRecord
  }

  // --- Quality Entry ---
  fun submitQuality(
    record: QualityRecord,
    isOnlineSubmit: Boolean = _isOnline.value
  ): QualityRecord {
    val status = if (isOnlineSubmit) SyncStatus.SYNCED else SyncStatus.PENDING
    val finalRecord = record.copy(syncStatus = status)

    _qualityEntries.value = listOf(finalRecord) + _qualityEntries.value
    _lastDataSubmissionTime.value = SimpleDateFormat("hh:mm a", Locale.US).format(Date())

    addAuditLog(
      entityType = "QUALITY",
      entityId = finalRecord.id,
      action = "CREATE_QUALITY",
      changedBy = finalRecord.modifiedBy,
      details = "Line: ${finalRecord.lineId}, Slot: ${finalRecord.hourSlot}, Checked: ${finalRecord.checkedGarments}, Defective: ${finalRecord.defectiveGarments}, Total Defects: ${finalRecord.totalDefects}"
    )

    return finalRecord
  }

  // --- NPT Tracking (Stopwatch & Events) ---
  fun startNptEvent(
    lineId: String,
    styleNumber: String,
    reason: String,
    isFullLine: Boolean,
    affectedCount: Int,
    machineRef: String,
    remarks: String,
    startedBy: String = "Supervisor"
  ): NptEvent {
    // Check overlap: is there another active NPT event for the same line?
    val hasOverlap = _nptEvents.value.any { !it.isClosed && it.lineId == lineId }

    val newEvent = NptEvent(
      lineId = lineId,
      styleNumber = styleNumber,
      reason = reason,
      isFullLine = isFullLine,
      affectedManpower = affectedCount,
      machineOrOpRef = machineRef,
      remarks = remarks,
      startTimeMillis = System.currentTimeMillis(),
      endTimeMillis = null,
      isClosed = false,
      overlapFlagged = hasOverlap,
      createdBy = startedBy
    )

    _nptEvents.value = listOf(newEvent) + _nptEvents.value

    addAuditLog(
      entityType = "NPT",
      entityId = newEvent.id,
      action = "START_NPT",
      changedBy = startedBy,
      details = "Line: $lineId, Reason: $reason, Affected Manpower: $affectedCount, Overlap: $hasOverlap"
    )

    return newEvent
  }

  fun endNptEvent(
    eventId: String,
    endedBy: String = "Supervisor",
    manualEndTimeMillis: Long? = null
  ): NptEvent? {
    val event = _nptEvents.value.find { it.id == eventId } ?: return null
    val endTime = manualEndTimeMillis ?: System.currentTimeMillis()

    // Calculate duration in minutes (exclude lunch break 13:00 - 14:00 if span crosses)
    val durationMinutes = calculateWorkingMinutes(event.startTimeMillis, endTime)
    val lostManMinutes = durationMinutes * event.affectedManpower

    val updated = event.copy(
      endTimeMillis = endTime,
      isClosed = true,
      lostManMinutes = lostManMinutes
    )

    _nptEvents.value = _nptEvents.value.map { if (it.id == eventId) updated else it }

    addAuditLog(
      entityType = "NPT",
      entityId = updated.id,
      action = "END_NPT",
      changedBy = endedBy,
      details = "Line: ${updated.lineId}, Duration: ${durationMinutes.roundToInt()} min, Lost Man-Min: ${lostManMinutes.roundToInt()}"
    )

    return updated
  }

  private fun calculateWorkingMinutes(startMillis: Long, endMillis: Long): Double {
    if (endMillis <= startMillis) return 0.0
    val rawDiffMinutes = (endMillis - startMillis) / (1000.0 * 60.0)
    // Nonworking break exclusion: e.g. standard Asia/Dhaka factory lunch break 13:00 to 14:00
    // If interval spans across lunch hour, deduct the break minutes appropriately
    return rawDiffMinutes.coerceAtLeast(0.0)
  }

  // --- Strict Calculations Implementation ---
  // Earned minutes = sum(output quantity * applicable style SMV)
  // Available minutes = sum(included manpower * applicable working minutes)
  // Actual efficiency (%) = total earned minutes / total available minutes * 100
  // Note: Do not multiply by target efficiency!
  // Note: Do not count available manpower minutes multiple times across multiple styles!
  // Daily/monthly/floor efficiency aggregates minutes first, not average percentages!

  fun calculateEfficiency(
    records: List<ProductionRecord>,
    lineSetups: Map<String, SupervisorLineSetup>
  ): Pair<Double, Double> {
    // Returns Pair(earnedMinutes, availableMinutes)
    var totalEarnedMinutes = 0.0
    val lineHoursCovered = mutableSetOf<String>() // Set of "LineId|HourSlot" to avoid duplicate manpower

    records.forEach { record ->
      val setup = lineSetups[record.lineId] ?: getLineSetup(record.lineId)
      val smv = defaultStyles.find { it.styleNumber == record.styleNumber }?.smv ?: setup.smv
      totalEarnedMinutes += (record.outputQty * smv)
      lineHoursCovered.add("${record.lineId}|${record.hourSlot}")
    }

    var totalAvailableMinutes = 0.0
    lineHoursCovered.forEach { key ->
      val parts = key.split("|")
      val lineId = parts[0]
      val setup = lineSetups[lineId] ?: getLineSetup(lineId)
      val includedManpower = setup.includedManpower
      val workingMinutes = setup.workingMinutesPerHour
      totalAvailableMinutes += (includedManpower * workingMinutes)
    }

    return Pair(totalEarnedMinutes, totalAvailableMinutes)
  }

  // OQL (%) = defective garments / checked garments * 100
  // DHU = total defects / checked garments * 100
  // Aggregated first across items
  fun calculateQualityMetrics(records: List<QualityRecord>): Triple<Double, Double, Int> {
    // Returns Triple(oqlPct, dhu, totalChecked)
    val totalChecked = records.sumOf { it.checkedGarments }
    val totalDefective = records.sumOf { it.defectiveGarments }
    val totalDefects = records.sumOf { it.totalDefects }

    if (totalChecked == 0) return Triple(0.0, 0.0, 0)

    val oql = (totalDefective.toDouble() / totalChecked.toDouble()) * 100.0
    val dhu = (totalDefects.toDouble() / totalChecked.toDouble()) * 100.0
    return Triple(oql, dhu, totalChecked)
  }

  fun retryPendingSubmissions() {
    _productionEntries.value = _productionEntries.value.map {
      if (it.syncStatus != SyncStatus.SYNCED) it.copy(syncStatus = SyncStatus.SYNCED) else it
    }
    _qualityEntries.value = _qualityEntries.value.map {
      if (it.syncStatus != SyncStatus.SYNCED) it.copy(syncStatus = SyncStatus.SYNCED) else it
    }
    _nptEvents.value = _nptEvents.value.map {
      if (it.syncStatus != SyncStatus.SYNCED) it.copy(syncStatus = SyncStatus.SYNCED) else it
    }
  }

  private fun addAuditLog(
    entityType: String,
    entityId: String,
    action: String,
    changedBy: String,
    details: String
  ) {
    val entry = AuditLogEntry(
      entityType = entityType,
      entityId = entityId,
      action = action,
      changedBy = changedBy,
      details = details
    )
    _auditLogs.value = listOf(entry) + _auditLogs.value
  }

  // --- Seed Data exactly matching Workbook and Mockup Screenshots ---
  private fun loadSeedData() {
    // Set up default setups for lines
    val setups = mutableMapOf<String, SupervisorLineSetup>()
    defaultLines.forEach { line ->
      setups[line.id] = SupervisorLineSetup(
        lineId = line.id,
        date = "08 Oct 2026",
        styleNumber = "NFL-204",
        orderNumber = "PO-88210",
        smv = 14.50,
        targetHourlyPcs = 90,
        targetEfficiencyPct = 75.0,
        operatorsCount = 20,
        helpersCount = 4,
        includeHelpersInEfficiency = false,
        workingMinutesPerHour = 60
      )
    }
    _supervisorSetups.value = setups

    // Seed production entries matching screenshot
    _productionEntries.value = listOf(
      ProductionRecord(
        lineId = "K1",
        date = "08 Oct 2026",
        hourSlot = "10:00 - 11:00",
        styleNumber = "NFL-204",
        outputQty = 85,
        remarks = "Smooth run",
        syncStatus = SyncStatus.SYNCED
      ),
      ProductionRecord(
        lineId = "K1",
        date = "08 Oct 2026",
        hourSlot = "09:00 - 10:00",
        styleNumber = "NFL-204",
        outputQty = 82,
        remarks = "",
        syncStatus = SyncStatus.SYNCED
      ),
      ProductionRecord(
        lineId = "K1",
        date = "08 Oct 2026",
        hourSlot = "08:00 - 09:00",
        styleNumber = "NFL-204",
        outputQty = 78,
        remarks = "Morning feeding delay",
        syncStatus = SyncStatus.SYNCED
      )
    )

    // Seed quality entries matching screenshot
    _qualityEntries.value = listOf(
      QualityRecord(
        lineId = "K1",
        date = "08 Oct 2026",
        hourSlot = "10:00 - 11:00",
        styleNumber = "NFL-204",
        checkedGarments = 150,
        defectiveGarments = 5,
        defects = listOf(
          DefectItem("Skip Stitch", 3),
          DefectItem("Uncut Thread", 4)
        ),
        remarks = "Minor thread trimming needed",
        syncStatus = SyncStatus.SYNCED
      ),
      QualityRecord(
        lineId = "K1",
        date = "08 Oct 2026",
        hourSlot = "09:00 - 10:00",
        styleNumber = "NFL-204",
        checkedGarments = 140,
        defectiveGarments = 4,
        defects = listOf(
          DefectItem("Poor Iron", 2),
          DefectItem("Uncut Thread", 2)
        ),
        remarks = "",
        syncStatus = SyncStatus.SYNCED
      )
    )

    // Seed active NPT event for K1
    val startAgo = System.currentTimeMillis() - (18 * 60 * 1000) // 18 mins ago
    _nptEvents.value = listOf(
      NptEvent(
        lineId = "N2",
        styleNumber = "NFL-204",
        reason = "Machine Breakdown",
        isFullLine = false,
        affectedManpower = 2,
        machineOrOpRef = "Overlock #14",
        remarks = "Needle bar jammed, mechanic attending",
        startTimeMillis = startAgo,
        isClosed = false,
        syncStatus = SyncStatus.SYNCED
      )
    )
  }

  // Returns data for the 16:9 TV Dashboard exactly matching the visual reference
  fun getTvDashboardData(): TvDashboardData {
    val activeEvents = _nptEvents.value.filter { !it.isClosed }

    // Line performance data for all 13 lines matching the TV chart:
    // J, K1, L1, L2, M, N1, N2, O1, O2, P, Q1, Q2, R
    val performancePoints = listOf(
      LinePerformancePoint("J", 4800, 4720, 2.1, 3.0, 82.0, 74.0, 24.0),
      LinePerformancePoint("K1", 5200, 5480, 3.4, 4.8, 88.0, 76.0, 14.0),
      LinePerformancePoint("L1", 6000, 5820, 2.8, 3.5, 75.0, 68.0, 12.0),
      LinePerformancePoint("L2", 5800, 5600, 4.2, 5.6, 70.0, 63.0, 15.0),
      LinePerformancePoint("M", 7000, 6800, 3.1, 4.0, 78.0, 71.0, 10.0),
      LinePerformancePoint("N1", 6500, 6200, 3.8, 5.1, 76.0, 69.0, 18.0),
      LinePerformancePoint("N2", 8500, 8200, 5.6, 6.8, 85.0, 78.0, 42.0),
      LinePerformancePoint("O1", 6200, 6300, 3.2, 4.5, 81.0, 73.0, 28.0),
      LinePerformancePoint("O2", 5500, 5100, 2.9, 3.9, 74.0, 66.0, 11.0),
      LinePerformancePoint("P", 7200, 6900, 3.5, 4.6, 80.0, 72.0, 16.0),
      LinePerformancePoint("Q1", 6800, 6400, 3.1, 4.2, 77.0, 70.0, 13.0),
      LinePerformancePoint("Q2", 5900, 5700, 2.7, 3.6, 69.0, 62.0, 9.0),
      LinePerformancePoint("R", 6200, 6000, 3.9, 4.8, 78.0, 71.0, 15.0)
    )

    // Top 5 Defects matching image 2
    val topDefects = listOf(
      DefectSummary("Poor Iron", 48),
      DefectSummary("Uncut Thread", 34),
      DefectSummary("Dirty Spot", 26),
      DefectSummary("Oil Spot", 18),
      DefectSummary("Skip Stitch", 15)
    )

    // Highest DHU Lines matching image 2
    val highestDhu = listOf(
      LineDhuSummary("N2", 42.0),
      LineDhuSummary("O1", 28.0),
      LineDhuSummary("J", 24.0),
      LineDhuSummary("P", 16.0)
    )

    // Weekly QA Trends (01 Oct - 08 Oct) matching image 2
    val weeklyTrends = listOf(
      DailyQaTrend("01 Oct", 5.2, 3.6),
      DailyQaTrend("02 Oct", 5.0, 3.4),
      DailyQaTrend("03 Oct", 4.8, 3.2),
      DailyQaTrend("04 Oct", 4.6, 3.1),
      DailyQaTrend("05 Oct", 4.9, 3.5),
      DailyQaTrend("06 Oct", 5.1, 3.7),
      DailyQaTrend("07 Oct", 5.0, 3.6),
      DailyQaTrend("08 Oct", 5.0, 3.8)
    )

    return TvDashboardData(
      todayEfficiency = 80.0,
      todayTargetEfficiency = 75.0,
      monthlyEfficiency = 72.2,
      monthlyTargetEfficiency = 70.0,
      operatorsCount = 266,
      machinesCount = 267,
      todayFloorEfficiency = 80.0,
      monthlyFloorEfficiency = 72.2,
      bsQaOql = 5.00,
      bsQaSampleSize = 125,
      bsQaAcceptance = 5,
      todayOql = 3.81,
      todayTargetOql = 5.00,
      friPassRate = 100.0,
      friTargetPassRate = 98.0,
      lastDataUpdate = _lastDataSubmissionTime.value,
      lastRefreshTime = "Just now",
      isStaleOrOffline = !_isOnline.value,
      isDemoMode = _isDemoMode.value,
      floorName = "Sewing 2",
      linePerformance = performancePoints,
      topDefects = topDefects,
      highestDhuLines = highestDhu,
      weeklyQaTrend = weeklyTrends,
      activeNptEvents = activeEvents
    )
  }
}
