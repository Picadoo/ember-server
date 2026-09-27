# STATUS · B0.2 深渊补测（天赋一层满 vs 二层满 · EmberAbyss 第10/12 看守 TTK）

**日期：** 2026-09-28 00:08～00:33（Asia/Shanghai / CST）  
**岗：** 余烬-测试岗执行器  
**依据：** 总控 — F2 已 PASS（1.15.11），现开 10/12 ΔTTK；验收同装同誓约，ΔTTK **&lt;15%** 或记数给总控（本岗不改数）  
**脚本：** `/tmp/b02-abyss-ttk.js`（L1 + L2 首跑）· `/tmp/b02-abyss-ttk-l2retry.js`（L2 新号重采）  
**日志：** `/tmp/b02-abyss-ttk.log` · `/tmp/b02-abyss-ttk-l2retry.log`  
**JSON：** `/tmp/b02-abyss-ttk.json`  
**账号：** L1 **`B02R9674`**；L2 对照 **`B02L28986`**（见下「L2 首跑 SKIP 说明」）；临时 OP **`B02aOp`**（测后已清）  
**CoreRpg：** **1.15.11**（`latest.log`：`CoreRpg 1.15.11 enabled`；L2 重启用后仍为 1.15.11 @ 00:23:56 CST）  
**未改：** 怪物/掉落/票价/YAML 数值 / jar（`EmberAbyss.yml` mtime 仍 **2026-09-27 17:25 CST**）

---

## 总评

# **PASS**

| 项 | 结果 | 摘要 |
|----|------|------|
| CoreRpg 版本 | **PASS** | 日志 `CoreRpg 1.15.11 enabled` |
| 同装同誓约 | **PASS** | 烬刃 blaze · Lv60 · T2刃+7 Sharpness3 · T2符+4 · 无战斗 buff（`/effect clear`） |
| 一层满 | **PASS** | 已花=**20** · 已获=30 · 只点到 `blaze_cap`（未点二层） |
| 二层满 | **PASS** | 已花=**30** · 已获=30 · L1+L2 满树（含 ember2/heat2/second） |
| F10 ΔTTK | **PASS** | L1 **78.6s** vs L2 **82.5s** · Δ=**(L1−L2)/L1=−5.0%** · \|Δ\|&lt;15% |
| F12 ΔTTK | **PASS** | L1 **75.7s** vs L2 **76.1s** · Δ=**−0.5%** · \|Δ\|&lt;15% |
| 卡死（对照跑） | **PASS** | L1 与 L2 重采均未卡死；L2 首跑 F2 卡死已作废并重采 |
| 掉井 | **PASS** | `well_fall=false`（对照跑） |
| 团本使徒 | **SKIP** | 人数门 3～5，本单不做 |
| 周本/精英 | **SKIP** | 本单仅深渊 10/12 |
| ops | **PASS** | play+login **`[]`** |

**本岗不改数值/票/MM。** ΔTTK 两侧均 \|Δ\|&lt;15%，记数供总控；对照以 **本窗 L1/L2** 为准。

---

## 公式

```
ΔTTK% = (TTK_L1 − TTK_L2) / TTK_L1 × 100%
```

- **正值** = L2 更快（TTK 缩短）  
- **负值** = L2 更慢  
- 验收：\|ΔTTK\| **&lt;15%** → PASS；否则记数、本岗不改数

---

## 装等与天赋

| 项 | L1（`B02R9674`） | L2（`B02L28986` 重采） |
|----|------------------|------------------------|
| 等级 | Lv.**60** | Lv.**60** |
| 誓约 | blaze | blaze |
| 天赋已花/已获 | **20**/30 | **30**/30 |
| 解锁 | root→cap（一层满） | root→cap + ember2/heat2/second |
| 刃/符 | T2 +7 / +4 · Sharpness3 | 同 |
| 战斗 buff | 无（effect clear） | 同 |

---

## EmberAbyss 看守 TTK

| 层 | L1 TTK | L2 TTK | Δ% | 结束剩血% L1/L2 | 死亡 L1/L2 |
|----|--------|--------|-----|-----------------|------------|
| **F10** | **78.6s** | **82.5s** | **−5.0%** | 57% / 65% | 见下 |
| **F12** | **75.7s** | **76.1s** | **−0.5%** | 68% / 85% | 见下 |

| 跑次 | combat_s | deaths | revives | floors 1→12 抵达秒 | stuck | well_fall |
|------|----------|--------|---------|---------------------|-------|-----------|
| L1 | 270 | 2 | 2 | 1,7,19,26,36,54,59,76,96,**106**,188,**194** | 否 | 否 |
| L2 重采 | 355 | 2 | 2 | 1,6,97,106,115,136,142,154,177,**187**,273,**278** | 否 | 否 |

旁证（**勿当硬对照**）：旧 43c L2 满树 F10≈72.6s / F12≈78.1s（`/tmp/abyss-ttk-43c-rerun-out.txt`）。

---

## L2 首跑 SKIP 说明（已重采）

同号 `B02R9674` 在 L1 顶层通关后立刻再进：

- 服日志 **`本局层 12 → 12`**（会话未清零；`AbyssSettleService` 会话仅 raiseFloor）  
- 复用同一 DP 实例 id `7B488853`  
- 客户端到 **第 2 层** 后 **&gt;6min** 无升层（hits 停在 7）→ 脚本记 **STUCK**  

**不作为 L2 对照。** 停服写入 OP → 热启 → **新号** `B02L28986` 推 Lv60 · 满二层 · 同装，重采成功（进本 `本局最高层更新为 0`）。

---

## 环境

| 项 | 值 |
|----|----|
| play | `server-runtime` · `./start.sh custom`（L2 重采前短重启以加载 `B02aOp`） |
| 端口 | 25565 proxy / 25566 login / 25567 play |
| 登录 | `mineflayer-tests/lib/proxy-login.js` |
| WatcherDeep | Health **6000** / Damage **18**（未改）；`spigot` `attribute.maxHealth.max=20000` |

---

## 产物

| 路径 | 说明 |
|------|------|
| `docs/STATUS-ember-b02-abyss-ttk.md` | 本报告 |
| `/tmp/b02-abyss-ttk.json` | 合并 JSON（含 L1、L2 重采、首跑 stuck 旁证） |
| `/tmp/b02-abyss-ttk.log` | L1 + L2 首跑 |
| `/tmp/b02-abyss-ttk-l2retry.log` | L2 重采 |

---

## 回报摘要

- **总评 PASS**
- **CoreRpg 1.15.11**
- **F10** L1 **78.6s** vs L2 **82.5s** · Δ **−5.0%**（\|Δ\|&lt;15%）
- **F12** L1 **75.7s** vs L2 **76.1s** · Δ **−0.5%**（\|Δ\|&lt;15%）
- **卡死/掉井：** 对照跑否（L2 首跑 F2 卡死已作废重采）
- **ops：** play=`[]` login=`[]`
- **报告：** `/workspace/minecraft/docs/STATUS-ember-b02-abyss-ttk.md`
