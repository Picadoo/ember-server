# 状态 · D391 抽测：短征 sx01 烬门哨岗（测岗）

**日期：** 2026-10-10（Asia/Shanghai）  
**执行：** 余烬-测试岗  
**裁决：** **FAIL**（结构/Cast/门闩绿 · **S40 结算与体力净扣红**）  
**jar：** `1.65.104-d391.local`（play Enabling 已核）  
**tip：** 键 `516db4da`/`4d555507` · MM `7a512ad7` · 菜单 `70c4b3f8` · 刷点 `5f8919ea`  
**证据：** `/workspace/tmp/d391-sx01-spot/`（`99-final-summary.json` · `retest.log` · `run.log`）  
**观察：** 三开关未关 · afk 未拧 · 未进旧 EmberDaily 主路径 · login/proxy/MariaDB 未碰

## 人话

能从短征菜单/命令进本，三房刷点齐，终厅 Cast 文案出现；但通关后 **CoreRpg 没有发 S40**，体力也被退回。根因是短征 runs 还没有 rooms/boss，Director 从来不算「开战」，DP 自己 `$end` 把人送出后按「开战前离开」释放。

## 分项

| ID | 结果 | 证据摘要 |
|----|------|----------|
| V1 Q01 未通不能进 | **PASS** | `D391SxNo` →「未解锁短征（需本人首通 Q01）」 |
| V1 已通→菜单/命令进本 | **PASS** | `ember_p1_short` 打开；`/corerpg p1 enter sx01` 创建实例 · world=`dungeon_EmberSx01_*` |
| V1 体力扣 30 | **FAIL** | 进本文案「已预留体力 30」；通关后 abort「开战前全员离开」**退还**；出本仍 **90/90**（净扣 0） |
| V5 日有奖帽 3 | **PASS*** | 进本文案「今日有奖 0/3」诚实；*因无结算，计数未 +1（旁注） |
| V3 三房通关 | **PASS** | 前庭→兵营→终厅；刷点 tip `5f8919ea`；`check-dp-spawns` 9 points ok |
| V3 Cast | **PASS** | chat「【烬门】哨长蓄力——拉开！」（`EmberSx01SentryCast`） |
| V4 S40 结算进账 | **FAIL** | 无 `short settle` / `sx_clear_*`；币 0→0；仓库空；DP 仅「结算发放中…」文案 |
| V2 gate_daily 仍拒 | **PASS** | console `papi parse` → `no` |
| V2 勿进旧 EmberDaily | **PASS** | 旧 start 拒/不可达；本号未走日常主路径 |
| V0 三开关 / afk | **PASS** | enabled/migrate/set_bonus=true；bv **63**；`daily_kills=2400` 未改 |

## 根因（测岗旁注 · 非改码）

`ember-v1-runs.yml` `short.sx01` **无 rooms/boss** → `EmberRunDirector` 永不进入 FIGHTING、不 index DP/MM 刷怪；DP `monster.yml` boss `$end reward=false` 结束后实例送出 → CoreRpg `release …（开战前全员离开）` → 退体力、**不走** `EmberShortService.settle` / S40。

另：开本大量 `operation-block` 报错（门栅脚本）；波次仍靠 `$kill` 推进，旁注不影响本号 FAIL 主因。

## 不动确认

关观察 · 抬 afk.tiers/daily_kills · 放开 gate_daily · 开 R/K3 · 改 ×0.97 · 切分支 · 动 login/proxy/MariaDB

## 建议回修（交总控派号 · 本号不改）

1. 短征 runs 补 rooms/boss（或 Director/短征专用：认 DP/MM 终厅击杀 / DP COMPLETE → `shortExpedition().settle`）  
2. 开战态与体力 commit 对齐，避免通关仍「开战前离开」退还  
3. 修 EmberSx01 `operation-block` 门洞脚本（若门栅为验收硬项）

---
