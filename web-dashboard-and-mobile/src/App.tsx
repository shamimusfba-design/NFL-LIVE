import React, { useState, useEffect } from 'react';

// Types
interface DefectRow {
  type: string;
  count: number;
}

export default function App() {
  const [currentTab, setCurrentTab] = useState<'PRODUCTION' | 'QUALITY' | 'NPT' | 'SETUP' | 'TV'>('TV');
  const [assignedLine, setAssignedLine] = useState('K1');
  const [isOnline, setIsOnline] = useState(true);

  // Production State
  const [prodDate] = useState('08 Oct 2026');
  const [prodHour, setProdHour] = useState('10:00 - 11:00');
  const [prodStyle, setProdStyle] = useState('NFL-204');
  const [prodQty, setProdQty] = useState('85');
  const [prodRemarks, setProdRemarks] = useState('');
  const [prodSuccess, setProdSuccess] = useState<string | null>(null);

  // Quality State
  const [checkedGarments, setCheckedGarments] = useState('150');
  const [defectiveGarments, setDefectiveGarments] = useState('5');
  const [defects, setDefects] = useState<DefectRow[]>([
    { type: 'Skip Stitch', count: 3 },
    { type: 'Uncut Thread', count: 4 }
  ]);
  const [qualitySuccess, setQualitySuccess] = useState<string | null>(null);

  // NPT State
  const [activeNpt, setActiveNpt] = useState<{ reason: string; elapsed: number } | null>({
    reason: 'Machine Breakdown',
    elapsed: 18 * 60
  });

  const totalDefects = defects.reduce((sum, d) => sum + d.count, 0);

  // TV Data
  const tvPerformance = [
    { line: 'J', target: 4800, ach: 4720, todayOql: 2.1, monthOql: 3.0, todayEff: 82.0, monthEff: 74.0, dhu: 24 },
    { line: 'K1', target: 5200, ach: 5480, todayOql: 3.4, monthOql: 4.8, todayEff: 88.0, monthEff: 76.0, dhu: 14 },
    { line: 'L1', target: 6000, ach: 5820, todayOql: 2.8, monthOql: 3.5, todayEff: 75.0, monthEff: 68.0, dhu: 12 },
    { line: 'L2', target: 5800, ach: 5600, todayOql: 4.2, monthOql: 5.6, todayEff: 70.0, monthEff: 63.0, dhu: 15 },
    { line: 'M', target: 7000, ach: 6800, todayOql: 3.1, monthOql: 4.0, todayEff: 78.0, monthEff: 71.0, dhu: 10 },
    { line: 'N1', target: 6500, ach: 6200, todayOql: 3.8, monthOql: 5.1, todayEff: 76.0, monthEff: 69.0, dhu: 18 },
    { line: 'N2', target: 8500, ach: 8200, todayOql: 5.6, monthOql: 6.8, todayEff: 85.0, monthEff: 78.0, dhu: 42 },
    { line: 'O1', target: 6200, ach: 6300, todayOql: 3.2, monthOql: 4.5, todayEff: 81.0, monthEff: 73.0, dhu: 28 },
    { line: 'O2', target: 5500, ach: 5100, todayOql: 2.9, monthOql: 3.9, todayEff: 74.0, monthEff: 66.0, dhu: 11 },
    { line: 'P', target: 7200, ach: 6900, todayOql: 3.5, monthOql: 4.6, todayEff: 80.0, monthEff: 72.0, dhu: 16 },
    { line: 'Q1', target: 6800, ach: 6400, todayOql: 3.1, monthOql: 4.2, todayEff: 77.0, monthEff: 70.0, dhu: 13 },
    { line: 'Q2', target: 5900, ach: 5700, todayOql: 2.7, monthOql: 3.6, todayEff: 69.0, monthEff: 62.0, dhu: 9 },
    { line: 'R', target: 6200, ach: 6000, todayOql: 3.9, monthOql: 4.8, todayEff: 78.0, monthEff: 71.0, dhu: 15 }
  ];

  const topDefectsList = [
    { defect: 'Poor Iron', count: 48 },
    { defect: 'Uncut Thread', count: 34 },
    { defect: 'Dirty Spot', count: 26 },
    { defect: 'Oil Spot', count: 18 },
    { defect: 'Skip Stitch', count: 15 }
  ];

  return (
    <div className="min-h-screen flex flex-col bg-slate-100 text-slate-800">
      {/* Top Switcher Navigation */}
      <header className="bg-[#0A2558] text-white px-4 py-2 flex items-center justify-between shadow-md">
        <div className="flex items-center gap-3">
          <span className="font-extrabold text-base tracking-wide">NFL FACTORY SYSTEM</span>
          <span className="bg-red-600 text-white text-[10px] font-bold px-2 py-0.5 rounded">SAMPLE DATA</span>
        </div>

        <nav className="flex items-center gap-2">
          {(['TV', 'PRODUCTION', 'QUALITY', 'NPT', 'SETUP'] as const).map(tab => (
            <button
              key={tab}
              onClick={() => setCurrentTab(tab)}
              className={`px-3 py-1 rounded text-xs font-bold transition-all ${
                currentTab === tab ? 'bg-blue-600 text-white shadow' : 'text-slate-300 hover:text-white'
              }`}
            >
              {tab === 'TV' ? 'Smart TV (16:9)' : tab}
            </button>
          ))}
        </nav>
      </header>

      {/* Main View Area */}
      <main className="flex-1 flex flex-col overflow-auto">
        {/* ===================== TV DASHBOARD (16:9 Fullscreen) ===================== */}
        {currentTab === 'TV' && (
          <div className="flex-1 flex flex-col bg-[#071933] text-white p-3 select-none">
            {/* TV Header Bar */}
            <div className="flex items-center justify-between pb-2 border-b border-blue-900 mb-2">
              <div className="flex items-center gap-3">
                <h1 className="text-xl font-black text-white tracking-wide">
                  NFL | Live Production & Quality Performance
                </h1>
                <span className="bg-red-600 text-[11px] font-extrabold px-2 py-0.5 rounded shadow">
                  SAMPLE DATA
                </span>
              </div>
              <div className="flex items-center gap-6 text-xs text-blue-200">
                <span>📅 08 OCT 2026</span>
                <span>🏭 Floor: Sewing 2</span>
                <div className="text-right">
                  <div className="text-[10px] text-blue-300">Last data update:</div>
                  <div className="font-bold text-white">06:49 PM</div>
                </div>
              </div>
            </div>

            {/* Top Row: 7 KPI Cards */}
            <div className="grid grid-cols-7 gap-2 mb-2">
              {/* 1. Today Efficiency */}
              <div className="bg-white rounded-lg p-2 text-slate-800 shadow flex flex-col justify-between">
                <div className="flex justify-between items-center text-[11px] font-bold">
                  <span>Today Efficiency</span>
                  <span className="text-slate-400">ⓘ</span>
                </div>
                <div className="text-center my-1">
                  <span className="text-2xl font-black text-blue-900">80.0%</span>
                </div>
                <div className="flex justify-between text-[10px] text-slate-500">
                  <span>Target: <b>75.0%</b></span>
                  <span className="text-blue-600">Ach: <b>80.0%</b></span>
                </div>
              </div>

              {/* 2. Monthly Efficiency */}
              <div className="bg-white rounded-lg p-2 text-slate-800 shadow flex flex-col justify-between">
                <div className="flex justify-between items-center text-[11px] font-bold">
                  <span>Monthly Efficiency</span>
                  <span className="text-slate-400">ⓘ</span>
                </div>
                <div className="text-center my-1">
                  <span className="text-2xl font-black text-blue-900">72.2%</span>
                </div>
                <div className="flex justify-between text-[10px] text-slate-500">
                  <span>Target: <b>70.0%</b></span>
                  <span className="text-blue-600">Ach: <b>72.2%</b></span>
                </div>
              </div>

              {/* 3. Operators / Machines */}
              <div className="bg-white rounded-lg p-2 text-slate-800 shadow flex flex-col justify-between">
                <div className="flex justify-between items-center text-[11px] font-bold">
                  <span>Operators / Machines</span>
                  <span className="text-slate-400">ⓘ</span>
                </div>
                <div className="text-center my-1">
                  <span className="text-lg font-bold text-blue-900">266 / 267</span>
                </div>
                <div className="flex justify-between text-[10px] text-slate-500">
                  <span>Ops: <b>266</b></span>
                  <span>Mach: <b>267</b></span>
                </div>
              </div>

              {/* 4. Floor Efficiency */}
              <div className="bg-white rounded-lg p-2 text-slate-800 shadow flex flex-col justify-between">
                <div className="flex justify-between items-center text-[11px] font-bold">
                  <span>Floor Efficiency</span>
                  <span className="text-slate-400">ⓘ</span>
                </div>
                <div className="space-y-1">
                  <div className="flex justify-between text-[9px] text-slate-600">
                    <span>Today</span>
                    <span className="font-bold text-blue-600">80.0%</span>
                  </div>
                  <div className="w-full bg-slate-200 h-2 rounded">
                    <div className="bg-blue-600 h-2 rounded" style={{ width: '80%' }}></div>
                  </div>
                  <div className="flex justify-between text-[9px] text-slate-600">
                    <span>Monthly</span>
                    <span className="font-bold text-blue-950">72.2%</span>
                  </div>
                  <div className="w-full bg-slate-200 h-2 rounded">
                    <div className="bg-blue-950 h-2 rounded" style={{ width: '72%' }}></div>
                  </div>
                </div>
              </div>

              {/* 5. BS QA OQL */}
              <div className="bg-white rounded-lg p-2 text-slate-800 shadow flex flex-col justify-between">
                <div className="flex justify-between items-center text-[11px] font-bold">
                  <span>BS QA OQL</span>
                  <span className="text-slate-400">ⓘ</span>
                </div>
                <div className="text-center my-1">
                  <span className="text-2xl font-black text-blue-600">5.00%</span>
                </div>
                <div className="flex justify-between text-[10px] text-slate-500">
                  <span>Sample: <b>125</b></span>
                  <span>Accept: <b>5</b></span>
                </div>
              </div>

              {/* 6. Today OQL */}
              <div className="bg-white rounded-lg p-2 text-slate-800 shadow flex flex-col justify-between">
                <div className="flex justify-between items-center text-[11px] font-bold">
                  <span>Today OQL</span>
                  <span className="text-slate-400">ⓘ</span>
                </div>
                <div className="text-center my-1">
                  <span className="text-2xl font-black text-emerald-600">3.81%</span>
                </div>
                <div className="flex justify-between text-[10px] text-slate-500">
                  <span>Target: <b>5.00%</b></span>
                  <span className="text-emerald-600">Actual: <b>3.81%</b></span>
                </div>
              </div>

              {/* 7. FRI Pass Rate */}
              <div className="bg-white rounded-lg p-2 text-slate-800 shadow flex flex-col justify-between">
                <div className="flex justify-between items-center text-[11px] font-bold">
                  <span>FRI Pass Rate</span>
                  <span className="text-slate-400">ⓘ</span>
                </div>
                <div className="text-center my-1">
                  <span className="text-2xl font-black text-blue-900">100.0%</span>
                </div>
                <div className="flex justify-between text-[10px] text-slate-500">
                  <span>Target: <b>98.0%</b></span>
                  <span className="text-blue-600">Actual: <b>100.0%</b></span>
                </div>
              </div>
            </div>

            {/* Middle & Lower Grid of 6 Charts */}
            <div className="flex-1 grid grid-cols-3 gap-2">
              {/* Chart 1: Live Target vs Achievement */}
              <div className="bg-white rounded-lg p-2 text-slate-800 shadow flex flex-col">
                <div className="flex justify-between items-center mb-1">
                  <h3 className="font-bold text-xs">Live Target vs Achievement</h3>
                  <div className="flex gap-2 text-[10px]">
                    <span className="flex items-center gap-1">
                      <span className="w-2 h-2 bg-blue-600 rounded-sm"></span> Target
                    </span>
                    <span className="flex items-center gap-1">
                      <span className="w-2 h-2 bg-emerald-500 rounded-sm"></span> Achievement
                    </span>
                  </div>
                </div>
                <div className="flex-1 flex items-end justify-between pt-4 border-b border-slate-200">
                  {tvPerformance.map(item => (
                    <div key={item.line} className="flex flex-col items-center gap-1 w-full">
                      <div className="flex items-end gap-0.5 h-28">
                        <div
                          className="w-2 bg-blue-600 rounded-t"
                          style={{ height: `${(item.target / 9000) * 100}%` }}
                        ></div>
                        <div
                          className="w-2 bg-emerald-500 rounded-t"
                          style={{ height: `${(item.ach / 9000) * 100}%` }}
                        ></div>
                      </div>
                      <span className="text-[9px] font-bold text-slate-600">{item.line}</span>
                    </div>
                  ))}
                </div>
              </div>

              {/* Chart 2: Today vs Monthly OQL */}
              <div className="bg-white rounded-lg p-2 text-slate-800 shadow flex flex-col">
                <div className="flex justify-between items-center mb-1">
                  <h3 className="font-bold text-xs">Today vs Monthly OQL</h3>
                  <div className="flex gap-2 text-[10px]">
                    <span className="flex items-center gap-1">
                      <span className="w-2 h-2 bg-blue-600 rounded-sm"></span> Today
                    </span>
                    <span className="flex items-center gap-1">
                      <span className="w-2 h-2 bg-orange-500 rounded-sm"></span> Monthly
                    </span>
                  </div>
                </div>
                <div className="flex-1 flex items-end justify-between pt-4 border-b border-slate-200">
                  {tvPerformance.map(item => (
                    <div key={item.line} className="flex flex-col items-center gap-1 w-full">
                      <div className="flex items-end gap-0.5 h-28">
                        <div
                          className="w-2 bg-blue-600 rounded-t"
                          style={{ height: `${(item.todayOql / 8) * 100}%` }}
                        ></div>
                        <div
                          className="w-2 bg-orange-500 rounded-t"
                          style={{ height: `${(item.monthOql / 8) * 100}%` }}
                        ></div>
                      </div>
                      <span className="text-[9px] font-bold text-slate-600">{item.line}</span>
                    </div>
                  ))}
                </div>
              </div>

              {/* Chart 3: Top 5 Defects */}
              <div className="bg-white rounded-lg p-2 text-slate-800 shadow flex flex-col justify-between">
                <div className="flex justify-between items-center mb-1">
                  <h3 className="font-bold text-xs">Top 5 Defects</h3>
                  <span className="text-[10px] text-slate-400">Today</span>
                </div>
                <div className="space-y-2 py-1">
                  {topDefectsList.map(def => (
                    <div key={def.defect} className="flex items-center gap-2 text-xs">
                      <span className="w-24 text-[11px] font-medium text-slate-600 truncate">{def.defect}</span>
                      <div className="flex-1 bg-slate-100 h-3 rounded overflow-hidden">
                        <div
                          className="bg-orange-500 h-full rounded"
                          style={{ width: `${(def.count / 50) * 100}%` }}
                        ></div>
                      </div>
                      <span className="font-bold text-xs w-6 text-right">{def.count}</span>
                    </div>
                  ))}
                </div>
              </div>

              {/* Chart 4: Line Efficiency Today vs Monthly */}
              <div className="bg-white rounded-lg p-2 text-slate-800 shadow flex flex-col">
                <div className="flex justify-between items-center mb-1">
                  <h3 className="font-bold text-xs">Line Efficiency: Today vs Monthly</h3>
                  <div className="flex gap-2 text-[10px]">
                    <span className="flex items-center gap-1"><span className="w-2 h-2 bg-blue-600 rounded-sm"></span> Today</span>
                    <span className="flex items-center gap-1"><span className="w-2 h-2 bg-orange-500 rounded-sm"></span> Monthly</span>
                  </div>
                </div>
                <div className="flex-1 flex items-end justify-between pt-4 border-b border-slate-200">
                  {tvPerformance.map(item => (
                    <div key={item.line} className="flex flex-col items-center gap-1 w-full">
                      <div className="flex items-end gap-0.5 h-28">
                        <div className="w-2 bg-blue-600 rounded-t" style={{ height: `${item.todayEff}%` }}></div>
                        <div className="w-2 bg-orange-500 rounded-t" style={{ height: `${item.monthEff}%` }}></div>
                      </div>
                      <span className="text-[9px] font-bold text-slate-600">{item.line}</span>
                    </div>
                  ))}
                </div>
              </div>

              {/* Chart 5: Weekly BS OQL & NFL AQC */}
              <div className="bg-white rounded-lg p-2 text-slate-800 shadow flex flex-col justify-between">
                <div className="flex justify-between items-center mb-1">
                  <h3 className="font-bold text-xs">Weekly BS OQL & NFL AQC</h3>
                  <div className="flex gap-2 text-[10px]">
                    <span className="text-blue-600 font-bold">• BS OQL</span>
                    <span className="text-orange-500 font-bold">• NFL AQC</span>
                  </div>
                </div>
                <div className="h-28 flex items-center justify-center text-xs text-slate-500 border border-slate-100 rounded">
                  <div className="w-full px-4 flex justify-between items-end h-20 border-b border-slate-300">
                    {['01 Oct', '02 Oct', '03 Oct', '04 Oct', '05 Oct', '06 Oct', '07 Oct', '08 Oct'].map(date => (
                      <div key={date} className="flex flex-col items-center">
                        <div className="w-1.5 h-1.5 bg-blue-600 rounded-full mb-1"></div>
                        <span className="text-[8px] text-slate-500">{date}</span>
                      </div>
                    ))}
                  </div>
                </div>
              </div>

              {/* Chart 6: Highest DHU Lines */}
              <div className="bg-white rounded-lg p-2 text-slate-800 shadow flex flex-col justify-between">
                <div className="flex justify-between items-center mb-1">
                  <h3 className="font-bold text-xs">Highest DHU Lines</h3>
                  <span className="text-[10px] text-slate-400">Today</span>
                </div>
                <div className="space-y-2 py-1">
                  {[
                    { line: 'N2', dhu: 42 },
                    { line: 'O1', dhu: 28 },
                    { line: 'J', dhu: 24 },
                    { line: 'P', dhu: 16 }
                  ].map(item => (
                    <div key={item.line} className="flex items-center gap-2 text-xs">
                      <span className="w-10 text-[11px] font-bold text-slate-700">{item.line}</span>
                      <div className="flex-1 bg-slate-100 h-3 rounded overflow-hidden">
                        <div
                          className="bg-blue-600 h-full rounded"
                          style={{ width: `${(item.dhu / 50) * 100}%` }}
                        ></div>
                      </div>
                      <span className="font-bold text-xs w-6 text-right">{item.dhu}</span>
                    </div>
                  ))}
                </div>
              </div>
            </div>

            {/* Bottom Bar on TV */}
            <div className="flex items-center justify-between pt-2 border-t border-blue-900 text-xs">
              <div className="flex gap-4 font-bold tracking-wide">
                <span className="text-white cursor-pointer hover:underline">SUMMARY</span>
                <span className="text-blue-300 cursor-pointer hover:underline">PRODUCTION</span>
                <span className="text-blue-300 cursor-pointer hover:underline">QUALITY</span>
              </div>
              <div className="flex items-center gap-2 text-emerald-400 font-bold">
                <span className="w-2 h-2 rounded-full bg-emerald-400 animate-pulse"></span>
                Connected (Auto-refresh: 30s)
              </div>
            </div>
          </div>
        )}

        {/* ===================== PRODUCTION ENTRY (Matches Image 1 Left) ===================== */}
        {currentTab === 'PRODUCTION' && (
          <div className="max-w-md mx-auto w-full p-4 flex flex-col gap-4">
            <div className="bg-white rounded-xl shadow p-5 space-y-4">
              <div className="flex justify-between items-center">
                <div className="text-sm">
                  Assigned line: <b className="text-blue-900 text-base">{assignedLine}</b>
                </div>
                <span className="flex items-center gap-1.5 text-xs text-emerald-600 font-bold">
                  <span className="w-2 h-2 rounded-full bg-emerald-500"></span> Online
                </span>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="text-xs text-slate-500 font-medium">Date</label>
                  <div className="mt-1 border border-slate-200 rounded-lg p-2 text-xs font-semibold bg-slate-50 flex items-center gap-2">
                    📅 {prodDate}
                  </div>
                </div>
                <div>
                  <label className="text-xs text-slate-500 font-medium">Hour</label>
                  <select
                    value={prodHour}
                    onChange={e => setProdHour(e.target.value)}
                    className="mt-1 w-full border border-slate-200 rounded-lg p-2 text-xs font-semibold bg-slate-50"
                  >
                    <option>08:00 - 09:00</option>
                    <option>09:00 - 10:00</option>
                    <option>10:00 - 11:00</option>
                    <option>11:00 - 12:00</option>
                    <option>14:00 - 15:00</option>
                    <option>15:00 - 16:00</option>
                  </select>
                </div>
              </div>

              <div>
                <label className="text-xs text-slate-500 font-medium">Style</label>
                <div className="mt-1 border border-slate-200 rounded-lg p-2 text-xs font-bold bg-slate-50 flex items-center justify-between">
                  <span>👕 {prodStyle}</span>
                  <span className="text-slate-400 text-[10px]">SMV: 14.50</span>
                </div>
              </div>

              <div>
                <label className="text-xs font-bold text-slate-700">Hourly Output (pcs)</label>
                <input
                  type="number"
                  value={prodQty}
                  onChange={e => setProdQty(e.target.value)}
                  className="mt-1 w-full border border-slate-300 rounded-lg p-3 text-2xl font-black text-slate-800 focus:outline-blue-600"
                />
                <span className="text-[11px] text-slate-400 mt-1 block">Enter this hour only.</span>
              </div>

              <div>
                <input
                  type="text"
                  placeholder="💬 Downtime / Remarks (Optional)"
                  value={prodRemarks}
                  onChange={e => setProdRemarks(e.target.value)}
                  className="w-full border border-slate-200 rounded-lg p-2.5 text-xs focus:outline-blue-600"
                />
              </div>

              {/* Confirmation summary pill */}
              <div className="bg-blue-50 border border-blue-200 rounded-lg p-2.5 flex items-center gap-2 text-xs font-bold text-blue-900">
                🏭 Line {assignedLine} • {prodHour} • {prodQty || 0} pcs
              </div>

              {prodSuccess && (
                <div className="bg-emerald-50 text-emerald-800 border border-emerald-200 p-2 rounded text-xs font-bold">
                  {prodSuccess}
                </div>
              )}

              <button
                onClick={() => {
                  setProdSuccess(`Hourly output recorded: ${prodQty} pcs`);
                  setTimeout(() => setProdSuccess(null), 3500);
                }}
                className="w-full bg-[#0D47A1] hover:bg-blue-800 text-white font-bold py-3 rounded-lg text-sm transition-all shadow"
              >
                SUBMIT PRODUCTION
              </button>
            </div>
          </div>
        )}

        {/* ===================== QUALITY ENTRY (Matches Image 1 Right) ===================== */}
        {currentTab === 'QUALITY' && (
          <div className="max-w-md mx-auto w-full p-4 flex flex-col gap-4">
            <div className="bg-white rounded-xl shadow p-5 space-y-4">
              <div className="flex justify-between items-center">
                <div className="text-sm">
                  Assigned line: <b className="text-blue-900 text-base">{assignedLine}</b>
                </div>
                <span className="flex items-center gap-1.5 text-xs text-emerald-600 font-bold">
                  <span className="w-2 h-2 rounded-full bg-emerald-500"></span> Online
                </span>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div className="border border-slate-200 rounded-lg p-2 text-xs font-semibold bg-slate-50">
                  📅 08 Oct 2026
                </div>
                <div className="border border-slate-200 rounded-lg p-2 text-xs font-semibold bg-slate-50">
                  ⏰ 10:00 - 11:00
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="text-xs font-bold text-slate-700">Checked Garments (pcs)</label>
                  <input
                    type="number"
                    value={checkedGarments}
                    onChange={e => setCheckedGarments(e.target.value)}
                    className="mt-1 w-full border border-slate-300 rounded-lg p-2.5 text-lg font-bold text-slate-800"
                  />
                </div>
                <div>
                  <label className="text-xs font-bold text-slate-700">Defective Garments (pcs)</label>
                  <input
                    type="number"
                    value={defectiveGarments}
                    onChange={e => setDefectiveGarments(e.target.value)}
                    className="mt-1 w-full border border-slate-300 rounded-lg p-2.5 text-lg font-bold text-slate-800"
                  />
                </div>
              </div>

              {/* Defects list */}
              <div className="border-t border-slate-200 pt-3 space-y-2">
                <label className="text-xs font-bold text-slate-700 block">Defects</label>

                {defects.map((def, idx) => (
                  <div key={idx} className="flex items-center gap-2">
                    <select
                      value={def.type}
                      onChange={e => {
                        const updated = [...defects];
                        updated[idx].type = e.target.value;
                        setDefects(updated);
                      }}
                      className="flex-1 border border-slate-200 rounded p-1.5 text-xs bg-white font-medium"
                    >
                      <option>Skip Stitch</option>
                      <option>Uncut Thread</option>
                      <option>Poor Iron</option>
                      <option>Dirty Spot</option>
                      <option>Oil Spot</option>
                    </select>
                    <input
                      type="number"
                      value={def.count}
                      onChange={e => {
                        const updated = [...defects];
                        updated[idx].count = Number(e.target.value);
                        setDefects(updated);
                      }}
                      className="w-16 border border-slate-200 rounded p-1.5 text-xs text-center font-bold"
                    />
                    <button
                      onClick={() => setDefects(defects.filter((_, i) => i !== idx))}
                      className="text-slate-400 hover:text-red-500 text-sm px-1"
                    >
                      🗑️
                    </button>
                  </div>
                ))}

                <button
                  onClick={() => setDefects([...defects, { type: 'Poor Iron', count: 1 }])}
                  className="text-xs font-bold text-blue-600 hover:underline pt-1 block"
                >
                  + Add defect
                </button>

                <div className="flex justify-between items-center text-xs pt-2">
                  <span className="text-slate-400 text-[11px]">One garment may have multiple defects.</span>
                  <span className="font-bold">Total defects: <b>{totalDefects}</b></span>
                </div>
              </div>

              {qualitySuccess && (
                <div className="bg-emerald-50 text-emerald-800 border border-emerald-200 p-2 rounded text-xs font-bold">
                  {qualitySuccess}
                </div>
              )}

              <button
                onClick={() => {
                  setQualitySuccess(`Quality logged: Defective=${defectiveGarments}, Total Defects=${totalDefects}`);
                  setTimeout(() => setQualitySuccess(null), 3500);
                }}
                className="w-full bg-[#1B5E20] hover:bg-emerald-800 text-white font-bold py-3 rounded-lg text-sm transition-all shadow"
              >
                SUBMIT QUALITY
              </button>
            </div>
          </div>
        )}

        {/* ===================== NPT SUPERVISOR STOPWATCH ===================== */}
        {currentTab === 'NPT' && (
          <div className="max-w-lg mx-auto w-full p-4 space-y-4">
            <div className="bg-white rounded-xl shadow p-5 space-y-4">
              <h2 className="text-base font-bold text-slate-800">Supervisor NPT Stopwatch</h2>
              <p className="text-xs text-slate-500">
                Formula: Lost man-minutes = Duration (mins) × Affected Manpower. Overlaps are flagged for resolution.
              </p>

              {activeNpt ? (
                <div className="bg-red-50 border border-red-200 rounded-lg p-4 space-y-3">
                  <div className="flex justify-between items-center">
                    <span className="text-xs font-bold text-red-700">● RUNNING STOPPAGE</span>
                    <span className="text-xs text-red-600 font-semibold">{activeNpt.reason}</span>
                  </div>
                  <div className="text-3xl font-black text-red-600 text-center py-2">
                    {Math.floor(activeNpt.elapsed / 60)}m {activeNpt.elapsed % 60}s
                  </div>
                  <button
                    onClick={() => setActiveNpt(null)}
                    className="w-full bg-slate-800 text-white text-xs font-bold py-2 rounded hover:bg-black"
                  >
                    END NPT (Close Event & Finalize Minutes)
                  </button>
                </div>
              ) : (
                <button
                  onClick={() => setActiveNpt({ reason: 'Machine Breakdown', elapsed: 0 })}
                  className="w-full bg-red-600 text-white font-bold py-3 rounded-lg text-sm hover:bg-red-700 shadow"
                >
                  START NPT EVENT
                </button>
              )}
            </div>
          </div>
        )}

        {/* ===================== SUPERVISOR SETUP & SMV ===================== */}
        {currentTab === 'SETUP' && (
          <div className="max-w-lg mx-auto w-full p-4 space-y-4">
            <div className="bg-white rounded-xl shadow p-5 space-y-3">
              <h2 className="text-base font-bold text-slate-800">Line Setup & Manpower Snapshot</h2>
              <div className="space-y-2 text-xs">
                <div>
                  <label className="font-bold text-slate-600">Standard Minute Value (SMV):</label>
                  <input type="number" defaultValue="14.50" className="w-full border p-2 rounded mt-1 font-bold" />
                </div>
                <div>
                  <label className="font-bold text-slate-600">Hourly Target (pcs):</label>
                  <input type="number" defaultValue="90" className="w-full border p-2 rounded mt-1 font-bold" />
                </div>
                <div>
                  <label className="font-bold text-slate-600">Operators Count:</label>
                  <input type="number" defaultValue="20" className="w-full border p-2 rounded mt-1 font-bold" />
                </div>
                <div>
                  <label className="font-bold text-slate-600">Include Helpers in Efficiency Denominator:</label>
                  <select className="w-full border p-2 rounded mt-1 font-bold">
                    <option>No (Operators only)</option>
                    <option>Yes (Operators + Helpers)</option>
                  </select>
                </div>
              </div>
              <button className="w-full bg-blue-700 text-white font-bold py-2.5 rounded text-xs">
                Snapshot & Save Line Setup
              </button>
            </div>
          </div>
        )}
      </main>
    </div>
  );
}
