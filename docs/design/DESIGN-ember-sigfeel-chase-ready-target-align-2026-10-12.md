# DESIGN · SigFeel / Hope / remain 追烙就绪目标件对齐（薄窗 · 可施工）· 2026-10-12

> **状态：** OPEN · TIP-AO · 荐 A·M · 2026-10-12  
> **上游 RESEARCH：** [`RESEARCH-ember-sigfeel-chase-ready-target-align-2026-10-12.md`](RESEARCH-ember-sigfeel-chase-ready-target-align-2026-10-12.md)  
> **对接 live：** TIP-AN CLOSED live `1.65.779-d12727`（ready cue / held-ready 已齐）· TIP-AJ imprint_hope · TIP-AH preview  
> **HOLD 旁注：** **L13 施工 HOLD**（TIP-AI FAIL · PASS≠开工）；TIP-AD exclude 刃 L11/L13 **保留**  
> **硬禁：** 零功率表 · 零 STAMP 抬 · 零扩 stampable · 零 L11/L13 mods · 零 Affix/伤乘/skill_mult/glance twin/深化 densify/sx20/ForgeChoice densify · **零再扫甲闸 · 零新 PAPI**

---

## 0. 一句话

让 hub 手感行、希望追烙轨、remain 满档摘要与 AN ready 钮写同一目标件槽名。

---

## 1. 问题陈述

| 层 | 现状 | 应然 |
|----|------|------|
| ready cue 按钮半行 | `readyCueSlotWord` 已认甲 | 保持 |
| `remainLine` / `progressLine`=`sigChase` | 满档无槽 / 不辨缺件 | **跟 readyCueSlotWord；缺件明示** |
| `SigFeel.menuLine` 追烙枝 | 只写徽记 m/n | 满档写可烙槽或缺件 |
| 未满档 | 已 OK | **不变** |

---

## 2. 方案 M（定稿荐）

### 2.1 SoT（改既有串 · 禁新族）

```
// 共用尾（仅 marks >= need）
readyTail(lo, def):
  if lo != null && imprintWornTarget(lo, def) == null:
    return "缺可烙穿着件"
  return "可烙" + readyCueSlotWord(lo, def)   // lo==null → slotName(def.slot)

remainLine(def, marks, lo=null):
  if def == null: 旧「未选…」
  if marks < need: 旧「徽记 m/need · 还差g」
  return "追烙 " + id + " · 徽记够 · " + readyTail(lo, def)

HopeMeter.sigChase(def, marks, lo=null):
  if def == null: 旧未选
  if marks < need: 旧「追烙 m/need · … · 徽记 · 满可烙印（不锁通关）」
  // 满档：保留 id/名/map/不锁通关；把「满可烙印」换成 readyTail
  return "§a追烙就绪 need/need · who · map · " + readyTail + "（不锁通关）"

SigFeel.menuLine(active, wornOff, chase, marks, lo=null):
  // active / wornOff 枝不变
  if chase != null && marks >= need:
    return "§e追烙就绪 §f" + id + " " + nm + " §7· " + readyTail(lo, chase)
  if chase != null: 旧「追烙 · 徽记 m/need」
```

- **复用** `EmberSigChase.readyCueSlotWord` / `imprintWornTarget`；**禁止**复制 prio。  
- 旧二元重载保留并委托 `lo=null`（单测回归）。  
- **禁止**新 `%corerpg_p1_*` / 新 remainLine 族 / TrMenu 加 lore。

### 2.2 调用方接线（有 lo 才喂）

| 点 | 动作 |
|----|------|
| `GrowthService.sigFeelLine` | `menuLine(..., chase, marks, lo)` |
| `EmberSigChase.maybeReadyCue` | `progressLine(def,marks,lo)` + `remainLine(def,marks,lo)` |
| `EmberSigChase.progressLine` | 委托 `HopeMeter.sigChase(def,marks,lo)` |
| `HopeMeter.priorityTracks` / RunPapi 希望 resolve | **若调用栈已有 lo**：`sigChase(chase, marks, lo)`；否则保持 null（不假造甲） |
| `MapFarmCue` 追烙按钮 tooltip | 有 pd 时可 refresh lo 喂 `remainLine`（可选轻触；无 lo 则旧串） |
| `statusGlance` | 无 lo 可保持旧满档；**不**强改 |
| TrMenu / 甲闸三文件 / GearNextHint | **本窗不改** |

### 2.3 单测（最小包）

| 用例 | 期望 |
|------|------|
| L02 + lo 穿同族头盔 · marks=need | remain / sigChase / SigFeel 含「头」类槽词；**不含**强制「护符」当目标是甲 |
| L02 + lo 空穿着 · marks=need | 含「缺可烙穿着件」 |
| L01 + lo 穿刃 · marks=need | 含刃槽词（回归） |
| marks < need | 与旧串同形（无「可烙头/缺可烙」） |
| `lo==null` 满档 | 不炸；回落 def.slot 或旧「满可烙印」可接受（测钉一种） |
| `git show` | 无 STAMP/C12/loot/DEFS mods/L11/L13/Affix/skill_mult；无甲闸 densify；无新 PAPI |

### 2.4 玩家口径（固定）

- 追烙徽记够、身上穿着可烙防具：枢纽手感 / 希望追烙轨 / remain 会写 **可烙该部位**，与聊天就绪钮同词。  
- 徽记够但身上没有可烙件：写 **缺可烙穿着件**，不再假装已经能烙。  
- 徽记未满、价钱、掉落、徽记数、签名表 **本窗不动**。

---

## 3. 验收

- [ ] L02 + 穿甲 · 满徽记：SigFeel / hope / remain 认甲槽  
- [ ] 满徽记无目标：明示缺件  
- [ ] L01 / 未满档回归  
- [ ] 无新 PAPI / 无 glance twin / lore≤3 不加成行  
- [ ] 未改 stampable / STAMP / L11·L13 / 甲闸三文件  
- [ ] L13 施工仍 HOLD  

---

## 4. 明确不做

- 扩 stampable 甲 / 抬 STAMP / 开 L13 施工 tip  
- Hope/SetChase/gem **新** glance twin / ForgeChoice densify  
- 再扫甲闸；GearNextHint 全局甲闸；SetChase 六槽扩容  
- 签名四甲格 / 甲待命半行 / TrMenu 加 lore  

---

## 5. 施工指针（真路径）

| 岗 | 路径 |
|----|------|
| 插件主责 | `EmberHopeMeter.sigChase` · `EmberSigFeel.menuLine` · `EmberSigChase.remainLine`/`progressLine` |
| 复用 | `EmberSigChase.readyCueSlotWord` · `imprintWornTarget` |
| 接线 | `EmberGrowthService.sigFeelLine` · `maybeReadyCue`；（可选）Hope resolve / MapFarmCue |
| 单测 | `EmberHopeMeterTest` · `EmberSigFeelTest` ·（可）`EmberSigChaseTest` / HeldReady 扩满档摘要 |
