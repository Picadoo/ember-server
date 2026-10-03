# STATUS · 团本使徒 TTK 校准测报（方案 A）

**日期：** 2026-09-28 18:05～18:16 CST  
**岗：** 余烬-测试岗执行器  
**设计：** `docs/design/design-ember-raid-apostle-ttk-calib.md` §3～4（已批 A）  
**对照：** `docs/status/STATUS-ember-b02-abyss-ttk.md`（同装同誓约 · L1 花20 / L2 花30 · blaze · effect clear）  
**Verdict：** **PASS**（校准 ΔTTK \|Δ\|&lt;15%）

---

## 一句话

临时 min=1 单 bot 正式 `/corerpg enter raid` 采 L1/L2 使徒 TTK；Δ=+10.83%；测毕当日还原 min=3。

---

## 跑次

| 层 | 账号 | 进本 | 使徒出现 | TTK(s) | 结束剩血% | 死亡 | 已花 |
|----|------|------|----------|--------|-----------|------|------|
| L1 | `AptkL1c441` | 18:06 CST PASS | 52.7s | **63.7** | 82% | 1 | 20 |
| L2 | `AptkL2c441` | 18:11 CST PASS | 58.3s | **56.8** | 71% | 0 | 30 |

临时 OP：`AptkOp28`（测后 deop + LP admin 卸；ops=`[]`）。

---

## ΔTTK

```
(63.7 − 56.8) / 63.7 × 100% = +10.83%
```

\|Δ\|&lt;15% → **校准 PASS**。本岗 **未**改使徒 HP/伤/技能/票掉落。

---

## 还原前 / 后（必填）

| | option.yml `$team-condition` |
|--|------------------------------|
| **还原前**（18:16:15 CST） | `min=1` · message=`团本人数 1～5（校准窗），当前 (<size>)` |
| **还原后**（18:16:20 CST） | `min=3` · message=`团本人数 3～5，当前 (<size>)` |

- FIFO `dp reload` → `插件重载完毕` @ **18:16:16 CST**  
- `plugins` 与 `server-runtime/plugins` **同 inode** · cmp identical  
- 测窗旁改：`dungeon-timeout-revive` 60→**已回 10**

---

## 脚本 / 日志

| 路径 | 说明 |
|------|------|
| `/tmp/apostle-ttk-calib.js` | 采数脚本 |
| `/tmp/apostle-ttk-L1.json` · `L1.log` | L1 |
| `/tmp/apostle-ttk-L2.json` · `L2.log` | L2 |

---

## 禁项自检

| 禁 | 本窗 |
|----|------|
| 改使徒 HP/伤/技能 | **未改**（Health 2000） |
| 改票掉落 | **未改** |
| 永久留 min=1 | **已还原 3** |
| 宣称单人正式通关时长达标 | **未宣称**（仅校准 ΔTTK） |
