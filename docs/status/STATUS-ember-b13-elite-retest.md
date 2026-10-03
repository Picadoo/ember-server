# STATUS · B1.3 精英图修复后复测（ember_elite）

**日期：** 2026-09-27 23:35～23:49（Asia/Shanghai / CST）  
**岗：** 余烬-测试岗执行器  
**依据：** `docs/status/STATUS-ember-b13-elite-map-check.md`（厅三东门已封 + 东护台；CoreRpg **1.15.10**）  
**前次 FAIL：** `docs/status/STATUS-ember-b0-ttk-test.md`（Boss@493s 后 ~220s 未击杀 · TTK=null · 全本 713s 超时）  
**对照 4.4f：** `docs/status/STATUS-ember-elite-weekly-test-4.4f.md`（Boss~70s / 清本~510s；窗 Boss 45–75s、全本 480–720s、剩血 25–55%）  
**脚本：** `/tmp/ttk-b0-b1-run.js`（`ONLY=elite`）· 东门 `/tmp/b13-east-door-tfb2.js`  
**原始 JSON：** `/tmp/b13-elite-retest.json` · 东门 `/tmp/b13-east-door.json` · 日志 `/tmp/b13-elite-retest-run.log`  
**账号：** 主测 **`B13R4140`**（非 op）；临时管理 **`B13OpBot`**（测后已清）  
**未改：** MM / 票 / YAML HP / option 奖励 / 数值

---

## 总评

# **条件 PASS**（东门实墙 **PASS** · 通关 **PASS** · Boss TTK **63s PASS** · 剩血 58% 略超 25–55）

相对前次 FAIL（Boss 掉崖/不可击杀 · TTK=null），本窗 **能打到 Boss 并击杀通关**，Boss TTK 回到验收窗，与 4.4f（70s）同量级。剩血快照 58% 略高于 55% 硬条（脚本记 FAIL），本岗**不改数**。

| 项 | 结果 | 摘要 |
|----|------|------|
| CoreRpg | **1.15.10** | play 重启后日志 `CoreRpg 1.15.10 enabled` |
| 东门实墙 + 护台 | **PASS** | 进本 `testforblock`：东门 gold/quartz；护台 y67 gold；向东走被挡 |
| 进本 / 扣票 | **PASS** / 记注 | 菜单/elite start →「精英试炼开启」；票采样同窗过密记注 |
| 通关 | **PASS** | 594s · 「试炼通过」 |
| Boss TTK 45–75s | **PASS** | **63s**（Boss@531s → 通关@594s） |
| 全本 480–720s | **PASS** | **594s** |
| 通关前剩血 25–55% | **FAIL 略超** | **58%**（23.27/40） |
| 死亡 / 复活 | 记录 | **1 / 1**（Boss 战中，同 4.4f） |
| minHp | 记录 | **0.59/40** |
| 掉崖 | **无** | Boss 战可近战击杀；未再现前次长时间失联 |
| 战斗 buff | **PASS** | 无 strength/resistance；`effect clear` |
| ops | **PASS** | play+login **`[]`** |

---

## 环境与准备

1. **重启 play** 加载 CoreRpg **1.15.10**（确认日志 enabled）。login/proxy 未动。  
2. 清 `dungeon-caches/dungeon_EmberEliteWeekly_*` 后随启动重建预缓存 `D3C5F2CA`（MCA 与模板一致：东门 quartz/gold）。  
3. 临时 OP：`B13OpBot`（ops.json offline UUID + LP admin）；测后 ops 置空。

---

## 东门抽检（必做）

| 检查 | 结果 |
|------|------|
| 进本 | `/corerpg elite start` → spawn ≈ `(-37,70,269)` · 厅三 TP `(22.5,68,270.5)` |
| `(29,68,270)` | **gold_block**（`testforblock` Successfully found） |
| `(29,68,269)` / `(29,69,271)` | **quartz_block** |
| `(29,71,270)` | **gold_block** |
| 反证：期望 air @ 东门 | **FAIL（仍是 Block of Gold）** → 实墙成立 |
| 东护台 `(31,67,270)` | **gold_block** |
| 厅内/Boss 垫 `(28,67,270)` `(24,67,270)` | **gold_block** |
| 向东走（自 x=27.3） | dx=0，**挡在门内** |
| 模板 MCA（对照） | 门列 155/41；护台 y67=41；修前备份门列为 air |

---

## ONLY=elite 通关跑

| 项 | 值 |
|----|-----|
| 等级 | **Lv42** |
| 装 | 烬刃 T2 **+7** · Sharpness **III** · 护符 T2 **+4** |
| 誓约 / 天赋 | blaze · L1 满 + `blaze_ember2`（点数不足满二层） |
| 战斗 buff | **无** |
| hits / casts | **618 / 63** |

### 波次

| | 4.4f | 前次 FAIL | **本窗** |
|--|-----:|----------:|--------:|
| 试炼一 | 0s | 0s | **0s** |
| 试炼二 | 247s | 327s | **358s** |
| Boss | 439s | 493s | **531s** |
| 通关/结束 | 509s 通关 | 713s 超时失败 | **594s 通关** |
| Boss TTK | **70s** | null（~220s 未杀） | **63s** |

### 对照窗

| 指标 | 窗口 / 4.4f | 本窗 | 判定 |
|------|-------------|------|------|
| Boss TTK | 45–75 / 70s | **63s** | **PASS** |
| 全本 | 480–720 / 510s | **594s** | **PASS** |
| 剩血 | 25–55 / 34% | **58%** | **略超** |
| 死亡 | 1/1 | **1/1** | 记录 |

---

## vs 前次 FAIL

| | 前次（B0 同测窗） | 本窗（图修后） |
|--|------------------|---------------|
| 东门 | 空气 → scrub 崖 | **quartz/gold 实墙** |
| 打到 Boss | 是（@493s） | 是（@531s） |
| Boss 击杀 | **否**（超时） | **是**（63s） |
| 通关 | **否** | **是** |

根因修复方向（插件岗已封东门）与本窗结果一致：**非削血即可通关**。本岗未改数值。

---

## 测后状态

- `server-runtime/ops.json`：`[]`  
- `login-runtime/ops.json`：`[]`  
- play **25567** / proxy **25565** / login **25566** 仍运行  
- CoreRpg 保持 **1.15.10**

---

## 建议（本岗不改）

1. 剩血 58% 略松：可观察是否 bot 复活后残留偏高，或接受放宽至 ≤60%；**勿**借此削 Boss。  
2. 预缓存：改模板后需 **重启 play**（或等价重建预缓存）；运行中删 cache 易得到空图实例。  
3. 总控若要以脚本硬条（含剩血 25–55）为唯一总评，本窗为 **FAIL（仅剩血）**；以 B1.3 图修验收（通关+TTK 回窗）则为 **PASS**。
