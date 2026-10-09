# 状态 · D391 复测：短征 sx01 烬门哨岗（测岗）

**日期：** 2026-10-10（Asia/Shanghai）  
**执行：** 余烬-测试岗  
**裁决：** **PASS**  
**jar：** `1.65.105-d391fix.local`（play Enabling 已核 · sha256 `0087d4f95a1da96c1c9b415989ec02b964f8dfcfa645757415bbee12db2f85a4`）  
**tip：** fix **`0ad59e46`**  
**上游 FAIL：** spot @`dbf001cc` · 修 STATUS `STATUS-ember-short-dungeon-sx01-d391-fail-fix-2026-10-10.md`  
**证据：** `/workspace/tmp/d391-sx01-retest/`（`99-summary.json` · `50-fight.json` · `60-after.json` · `run4.log`）  
**观察：** 三开关未关 · afk `daily_kills=2400` 未拧 · bv **64** · 未进旧 EmberDaily · login/proxy/MariaDB 未碰

## 人话

旧 FAIL（体力退还 + 无 S40）已消。Director 进 **FIGHTING**，三房清完、Cast 出现、`short settle` 发 S40；体力净扣 30；有奖+首通包到账（币 280=80+200，碎片 12=4+8，骨尘 3，胚料 1）。

## 分项

| ID | 结果 | 证据摘要 |
|----|------|----------|
| V1 Q01 未通不能进 | **PASS** | `D391SxNo` →「未解锁短征（需本人首通 Q01）」 |
| V1 已通→进本 | **PASS** | `/corerpg p1 enter sx01` · runId `sx01-mv1gpsq4-t93` · 预留体力 30 · 今日有奖 0/3 |
| V1 体力净扣 30 | **PASS** | 90→60；无「开战前全员离开」退还 |
| V5 日有奖帽 3 | **PASS** | 进本 0/3；通关后 1/3 |
| V3 Director FIGHTING | **PASS** | `runs list` 见 `fighting`；r1/r2/r3 开战+已清空 |
| V3 三房通关 | **PASS** | 前庭→兵营→甬道 均「已清空 · 门已打开」 |
| V3 Cast | **PASS** | chat「【烬门】哨长蓄力——拉开！」+ 砸地/重斩预警 |
| V4 S40 结算 | **PASS** | log `short settle … rows+6 reward day=1/3`；币 0→**280**；仓 shard×12 bone×3 blank×1 |
| V2 gate_daily 仍拒 | **PASS** | `papi parse` → `no` |
| V2 勿进旧 EmberDaily | **PASS** | 未走日常主路径 |
| V0 三开关 / afk | **PASS** | enabled/migrate/set_bonus=true；bv64；daily_kills=2400 |
| 旧 FAIL 项消 | **PASS** | 对照 @`dbf001cc`：体力净扣红、S40 红 → 本号均绿 |

## 对照旧 FAIL

| 旧红 | 复测 |
|------|------|
| 通关 abort「开战前全员离开」退体力（净扣 0） | 开战态成立；出本 90→60 净扣 30 |
| 无 `short settle` / 币仓不变 | `short settle` + S40 有奖+首通到账 |

## 不动确认

关观察 · 抬 afk.tiers/daily_kills · 放开 gate_daily · 开 K3 · 改 ×0.97 · 切分支 · 动 login/proxy/MariaDB

---
