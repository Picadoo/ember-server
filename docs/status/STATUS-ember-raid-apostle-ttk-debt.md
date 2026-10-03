# STATUS · Ember 团本使徒 TTK 债务

**日期：** 2026-09-28 18:05～18:16（Asia/Shanghai / CST）  
**岗：** 余烬-测试岗执行器  
**依据：** 总控批方案 A · `docs/design/design-ember-raid-apostle-ttk-calib.md` §3～4 · tip `a6572d3`（临时 min=1）· 对照 `docs/status/STATUS-ember-b02-abyss-ttk.md`  
**Verdict：** **✅ 校准 ΔTTK PASS**（\|Δ\|&lt;15%；**非**「单人正式通关时长达标」）

---

## 总评

# **PASS**（校准 ΔTTK）

| 项 | 结果 | 摘要 |
|----|------|------|
| 进本（临时 min=1） | **PASS** | `/corerpg enter raid` · 单 bot · 「团本大厅已集结」 |
| 同装同誓约 | **PASS** | Lv60 · blaze · T2刃+7 Sharpness3 · T2符+4 · `/effect clear` |
| L1 满使徒 TTK | **PASS** | **63.7s** · 已花=**20** · 结束剩血 **82%** · 死亡 1 |
| L2 满使徒 TTK | **PASS** | **56.8s** · 已花=**30** · 结束剩血 **71%** · 死亡 0 |
| ΔTTK% | **PASS** | **(63.7−56.8)/63.7×100 = +10.83%** · \|Δ\|&lt;15% |
| 禁砍 HP/票/技能 | **PASS** | 使徒 Health 仍 **2000**；未改掉落/票 |
| 当日还原 min=3 | **PASS** | 见下「还原证据」· 双树 identical · ops=`[]` |

**表述纪律：** 本结果为 **校准 ΔTTK**（同装同誓约 L1 满 vs L2 满）。**不得**写成「单人正式 3～5 人通关时长达标」。

---

## 公式

```
ΔTTK% = (TTK_L1 − TTK_L2) / TTK_L1 × 100%
```

- **正值** = L2 更快（TTK 缩短）  
- 验收：\|ΔTTK\| **&lt;15%** → 校准 PASS；否则只记数不改数（本岗不砍 HP）

---

## 装等与天赋

| 项 | L1（`AptkL1c441`） | L2（`AptkL2c441`） |
|----|-------------------|-------------------|
| 等级 | Lv.**60** | Lv.**60** |
| 誓约 | blaze | blaze |
| 天赋已花/硬顶 | **20**/30 | **30**/30 |
| 解锁 | root→cap（一层满） | + ember2/heat2/second |
| 刃/符 | T2 +7 / +4 · Sharpness3 | 同 |
| 属性（stats） | 攻 **+53** · 生命 103 · 暴击 5.0% | 攻 **+55** · 生命 103 · 暴击 **5.5%** |
| 战斗 buff | 无（effect clear） | 同 |

---

## 使徒 TTK 表

| | L1 | L2 |
|--|---:|---:|
| 进本 | `/corerpg enter raid` **PASS**（校准窗 min=1） | 同 |
| 使徒出现（进战后秒） | **52.7s** | **58.3s** |
| **使徒 TTK** | **63.7s** | **56.8s** |
| 全本 combat_s | 116.4 | 115.1 |
| 结束剩血% | **82%** | **71%** |
| 死亡 / 复活 | 1 / 2 | 0 / 1 |
| hits / casts | 149 / 14 | 150 / 13 |

时钟：聊天 `终厅裂开——使徒现身！` / `【终厅】使徒` → `使徒倒下` / `余烬团本 通关`。

**ΔTTK% = +10.83%**（L2 略快；\|Δ\|&lt;15% → **校准 PASS**）。

---

## 还原证据（测毕强制）

| | 内容 | 时刻（CST） |
|--|------|-------------|
| **还原前** | `$team-condition{…;min=1;…;message=§c团本人数 1～5（校准窗），当前 (<size>)}` | 18:16:15 |
| **还原后** | `$team-condition{…;min=3;…;message=§c团本人数 3～5，当前 (<size>)}` | 18:16:20 |
| 热更 | FIFO `console.in` → `dp reload` · `插件重载完毕` · `[EmberRaid] 地牢内容初始化完毕` @ **18:16:16～17** | |
| 双树 | `plugins` ↔ `server-runtime/plugins` **同 inode** · `cmp` identical | |
| 旁改还原 | `DungeonPlus/config.yml` `dungeon-timeout-revive` 测窗曾 60 → **已回 10**（测中临时加宽复活窗；非平衡数值） | |
| ops | play=`[]` · login=`[]` · 临时 OP/LP admin 已清 | |

**对外表述仍「团本 3～5」。** 永久 min=1 **未留**。

---

## 环境

| 项 | 值 |
|----|-----|
| tip（测前） | `a6572d3` ops(raid): temp min=1 |
| CoreRpg | **1.15.21**（`latest.log`） |
| 入口 | proxy 25565 / login 25566 / play 25567 |
| 脚本 | `/tmp/apostle-ttk-calib.js` |
| JSON | `/tmp/apostle-ttk-L1.json` · `/tmp/apostle-ttk-L2.json` |
| 使徒 MM | `EmberRaidBoss` Health **2000**（未改） |

---

## 产物

| 路径 | 说明 |
|------|------|
| `docs/status/STATUS-ember-raid-apostle-ttk-debt.md` | 本文件（债关账 · 校准 PASS） |
| `docs/status/STATUS-ember-raid-apostle-ttk-calib-test.md` | 测报明细 |
| `plugins/DungeonPlus/dungeon/EmberRaid/option.yml` | **已还原 min=3** |

---

## 回报摘要

- **总评：校准 ΔTTK PASS**（\|Δ\|=10.83%&lt;15%）
- **L1 TTK 63.7s** · **L2 TTK 56.8s** · **Δ +10.83%**
- **还原前** min=1「1～5（校准窗）」→ **还原后** min=3「3～5」· dp reload @ 18:16 CST
- **报告：** `docs/status/STATUS-ember-raid-apostle-ttk-debt.md` · `docs/status/STATUS-ember-raid-apostle-ttk-calib-test.md`
- **阻塞点：** 无（单 bot 正式入口已采数；人数门已还原）
- **ops=[]**
