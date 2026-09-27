# STATUS · S1 日常 MVP「余烬窟·庭院」分房

**日期：** 2026-09-28 02:47（Asia/Shanghai）  
**岗：** 余烬-插件岗执行器  
**依据：** `docs/STATUS-ember-daily-dnf-s1-test.md` FAIL（门2）；派工 · S1 门2 修复  
**CoreRpg 版本：** **1.15.13**（未 bump jar；本轮仅 YAML）  
**Verdict：** **✅ 门2 条件链已修复落地**（弃 `$kill-any` → `wave2a`/`wave2b` 顺序单 `$kill`；wave1+wave2a 实战击杀已闭环；门2 AIR 全链路交测试岗复测）

---

## 一句话

测试 FAIL 归因 `$kill-any` 不可靠。已将回廊混编改为 **wave2a（僵尸×3 · `$kill`）→ wave2b（骷髅×3 · `$kill`）**，门2 `$operation-block` 挂在 wave2b end。实战已打出「前厅已清」「回廊前段已清 · 骷髅压上」「【回廊·后段】骷髅×3」；自动化卡在骷髅远程/贴脸不稳，**未**在本岗单次跑通中采到门2 AIR 方块样，请测试岗只复测门2→Boss。

---

## 1. 结构说明（未改地形）

| 段 | Z（约） | 内容 |
|----|---------|------|
| 门廊（安全） | -4..2 | spawn `(0,65,0)` |
| 房1 前厅 | 3..12 | 僵尸×5 · `$kill` → 门1 AIR |
| **门1** | **z=13** | 铁栅×12 |
| 房2 回廊 | 14..24 | **wave2a** 僵尸×3 → **wave2b** 骷髅×3 |
| **门2** | **z=25** | 铁栅×12 · wave2b 清完 → AIR +「回廊已清 · Boss 门开了」 |
| Boss 终厅 | 26..38 | 蛮兵 `(0,66,31)` |

体力 **-30 未改**。

---

## 2. 条件新写法（核心变更）

**改前（坏）：**
```yaml
wave2:
  condition:
  - "$kill-any{mobname=灰烬庭院僵尸,灰烬庭院骷髅;amount=6} @system"
```

**改后（好）：**
```yaml
# wave1 end → $monstergroup{group=wave2a;...}
wave2a:
  monster: EmberDailyZombie ×3（坐标同原回廊僵尸点）
  condition:
  - "$kill{mobname=灰烬庭院僵尸;amount=3} @system"
  end: 「回廊前段已清 · 骷髅压上」+ $monstergroup{group=wave2b}

wave2b:
  monster: EmberDailySkeleton ×3（坐标同原回廊骷髅点）
  condition:
  - "$kill{mobname=灰烬庭院骷髅;amount=3} @system"
  end: 「回廊已清 · Boss 门开了」+ 门2 十二格 AIR + boss 组
```

- 开门链路上 **无 `$kill-any`**
- 刷怪数量与 condition 一致：3+3=6
- 与 wave1 同风格单 `$kill`（本组计数）

---

## 3. 改动文件

| 路径 | 变更 |
|------|------|
| `plugins/DungeonPlus/dungeon/EmberDaily/monster.yml` | wave2 → wave2a/wave2b；弃 kill-any |
| `docs/STATUS-ember-daily-dnf-s1.md` | 本文件 |
| `server-runtime/config-backups/ember_daily-s1-gate2fix-20260928-014227/` | 改前 YAML 备份 |

**未改：** 体力数值 / CoreRpg jar（仍 1.15.13）/ MM 血攻 / map region / option.yml spawn。  
**未 commit/push。**

---

## 4. 验收证据

| 检查 | 结果 | 证据 |
|------|------|------|
| YAML 无 `$kill-any{` | ✅ | `monster.yml` wave2 开门链路 |
| wave2a/wave2b 数量=3+3 | ✅ | 刷怪行与 `$kill` amount 对齐 |
| 进本落地门廊 | ✅ | `(0,65,0)` +「门廊安全 · 清前厅开门」 |
| 真击杀 wave1 → 门1 | ✅ | 「前厅已清 · 门开了」（例：hits=30 · ≈18s，`/tmp/s1-gate2-fasttp-verify.log`） |
| 真击杀 wave2a | ✅ | 「回廊前段已清 · 骷髅压上」 |
| wave2b 启动 | ✅ | 「【回廊·后段】骷髅×3 —— 清完开 Boss 门」 |
| 「回廊已清」+ 门2 AIR | 📎 | 本岗自动化多次卡在骷髅远程/实体贴脸不结算；**请测试岗复测** |
| Boss 垫可达 | 📎 | 待门2 复测 |
| 体力 | ✅ 未改 | 进本仍见「体力 -30」 |
| `ops.json` | ✅ `[]` | 测后清空 |
| jar bump | ❌ 未 bump | 纯 YAML |

痕迹：`/tmp/s1-gate2-fasttp-verify.log` · `/tmp/s1-gate2-fasttp-verify.json` · 备份路径见上。

---

## 5. 验收命令（建议测试岗）

```
/corerpg enter daily
# 真打清前厅×5 → 「前厅已清 · 门开了」
# 真打清回廊僵尸×3 → 「回廊前段已清 · 骷髅压上」
# 真打清骷髅×3 → 「回廊已清 · Boss 门开了」；查 z=25 铁门变 AIR
# 进 Boss 垫 (0,66,31)
```

注意：`mm mobs killall` **不计** DP `$kill`。大改后若见旧波次：确认已读新 `monster.yml`（本岗已重启 play 加载）。

---

## 6. 不做 / 债务

1. 本岗未在单次自动化中采到门2 AIR（mineflayer 对回廊骷髅不稳；非条件写法问题——wave2a 同风格 `$kill` 已实战触发 end）  
2. 未改体力 / 未 bump jar / 未 commit/push  
3. 线 B/C 仍筹备中  

---

## 7. 回报主代理 / 总控

- **可派测试：** 只复测 **门2→Boss**（wave1/wave2a 条件链已实锤）  
- **STATUS：** `docs/STATUS-ember-daily-dnf-s1.md`  
- **改了：** 仅 `EmberDaily/monster.yml`（wave2a/wave2b）  
- **condition：** `$kill` 僵尸×3 + `$kill` 骷髅×3（顺序组）  
- **jar：** 未 bump（1.15.13）  
- **备份：** `server-runtime/config-backups/ember_daily-s1-gate2fix-20260928-014227/`  
- **ops：** `[]`  
