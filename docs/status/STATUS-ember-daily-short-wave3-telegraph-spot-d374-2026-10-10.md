# STATUS · D374 抽测：日更第三拍四线预警环（庭院/焦骨/潮蚀/断塔）+ 霜回归

**日期：** 2026-10-10 03:11 CST（Asia/Shanghai）  
**岗：** 余烬-测试岗执行器  
**依据：** DESIGN `docs/design/DESIGN-ember-daily-short-wave3-telegraph-2026-10-10.md` · 施工 STATUS `docs/status/STATUS-ember-daily-short-wave3-telegraph-2026-10-10.md` · MM tip **`761b0678`**（已 `mm reload`）  
**测号：** `D374Yard`（未抢 D373 `D373Pet*`）  
**证据：** `/workspace/tmp/d374-wave3-telegraph/`（`00-static.json` · `10-*.json` · `99-summary.json` · `run-v3.log`）  
**Verdict：** **✅ PASS**（四线 + 霜回归全绿 · 静态无旧环 · HP/掉落/DP 零改 · 开关未拧 · ≠关观察）

---

## 一句话

庭院 / 焦骨 / 潮蚀 / 断塔 四条 Cast live：提示先出 → delay 窗 **earlyDrop=0** → 圈内 **lateDrop=1.2** → 提示后立刻拉开 **dodgeDrop=0**；霜晶回归同构仍绿。无预警 onTimer 环已删（潮蚀独立 SLOW 光环已删）；Boss HP / mmgive / DP 相对 tip 零 diff；三开关 true + bv62 未动。

---

## 验收表

| 线 | Cast | 提示 | early | late≈1.2 | 拉开 drop=0 | 进本/体力文案 | 无旧环 | HP |
|----|------|------|-------|----------|-------------|---------------|--------|-----|
| 庭院 | `EmberYardPulseCast` | 【庭院】…拉开 | 0 | **1.2** | **0** | 开始！·体力 | ✅ | 180 |
| 焦骨 | `EmberAshBurstCast` | 【焦骨】…拉开 | 0 | **1.2** | **0** | 开始！·体力 | ✅ | 200 |
| 潮蚀 | `EmberTideCrashCast` | 【潮蚀】…拉开 | 0 | **1.2** | **0** | 开始！·体力 | ✅（无独立 SLOW 光环） | 205 |
| 断塔 | `EmberSpireSlamCast` | 【断塔】…拉开 | 0 | **1.2** | **0** | 开始！·体力 | ✅（逼近 ~180 保留） | 215 |
| 霜（回归） | `EmberFrostNovaCast` | 【霜厅】…拉开 | 0 | **1.2** | **0** | 开始！·体力 | ✅ | 210 |

**总评：** PASS

---

## 测法摘要

1. **静态：** Skills 四 Cast = message→粒子→`delay 25`→`damage 1.2 @r=5`（潮蚀另轻 SLOW）；Boss 挂 `~onTimer:80`；无 `damage ~onTimer:40/45` 裸环；断塔 `~onTimer:180` 逼近保留；DP `EmberDaily*` 相对 `761b0678^..HEAD` **空 diff**。  
2. **进本：** `/corerpg enter daily|daily_ash|daily_tide|daily_spire|daily_frost` → 出现「开始！」与体力文案后 `/dp leave`。  
3. **Cast live（hub 出生点垫）：** `/mm m spawn <Boss> 1` + `NoAI` → 贴脸等提示 → 量 early/late；再生成后见提示立刻 TP 出半径量 dodge。  
4. **硬约束：** 未改 ×0.97 / 三开关 / bv / 体力表 / 掉落 / MM 数值；未切分支 / stash / reset；未关观察；测后 ops play+login=`[]`。

---

## 环境

| 项 | 值 |
|----|-----|
| tip / 施工 | `761b0678` feat(D374) 四线 Cast |
| HEAD（测时） | `819c1a33`（MM 相对 tip 未再改） |
| 分支 | `main` |
| jar / bv / 六槽 | `1.65.101-d335.local` 语境续观察；**bv62**；enabled/migrate/set_bonus=**true** |
| 端口 | proxy 25565 / play 25567 |
| killall | 测后 `mm m killall` 清场；`killall_used` 仅清测垫，非本验收杀怪 |

---

## 回执（给总控）

| 项 | 内容 |
|----|------|
| 总评 | **PASS** |
| 四线 | 庭院/焦骨/潮蚀/断塔 提示·1.2·拉开全过 |
| 回归 | 霜晶一条同绿 |
| 静态 | 无旧环 · HP/掉落/DP 零改 · 断塔逼近保留 |
| 证据 | `/workspace/tmp/d374-wave3-telegraph/` |
| 本 STATUS | `docs/status/STATUS-ember-daily-short-wave3-telegraph-spot-d374-2026-10-10.md` |
| 观察 | **续**（≠关窗） |
| 开关 | **未拧** |

---

*D374 抽测 PASS · tip `761b0678` · 证据 `/workspace/tmp/d374-wave3-telegraph/` · ≠关观察 ≠改开关。*
