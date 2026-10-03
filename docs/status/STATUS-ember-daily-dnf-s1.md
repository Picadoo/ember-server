# STATUS · S1 日常 MVP「余烬窟·庭院」分房

**日期：** 2026-09-28 03:38（Asia/Shanghai）  
**岗：** 余烬-插件岗执行器  
**依据：** 总控【派工 · S1 回廊近战化 · 便于门2验收闭环】；前序 `docs/status/STATUS-ember-daily-dnf-s1-retest.md` FAIL（wave2b 骷髅远程不稳）  
**CoreRpg 版本：** 近战化落地时 **1.15.13**；验收窗口已至 **1.15.14**（S2 建图 bump，见 retest2）  
**方案：** **A（采用）** — `EmberDailySkeleton` 近战化；**未退 B**  
**Verdict：** **✅ 回廊近战化落地**；独立验收见 `docs/status/STATUS-ember-daily-dnf-s1-retest2.md` **PASS**（门2 AIR + Boss 通关）

---

## 一句话

将 `EmberDailySkeleton` 去弓改木剑贴脸（Display/`$kill` 名仍为「灰烬庭院骷髅」）。单次真击杀自动化跑通：wave1 → wave2a → wave2b → **「回廊已清 · Boss 门开了」** + 门2 z=25 **AIR×12** → 终厅播报「Boss 蛮兵」且垫可达。未改体力；未 bump jar；未 commit/push。

---

## 1. 结构说明（未改地形 / DP 波次）

| 段 | Z（约） | 内容 |
|----|---------|------|
| 门廊（安全） | -4..2 | spawn `(0,65,0)` |
| 房1 前厅 | 3..12 | 僵尸×5 · `$kill` → 门1 AIR |
| **门1** | **z=13** | 铁栅×12 |
| 房2 回廊 | 14..24 | **wave2a** 僵尸×3 → **wave2b** 骷髅×3（近战） |
| **门2** | **z=25** | 铁栅×12 · wave2b 清完 → AIR +「回廊已清 · Boss 门开了」 |
| Boss 终厅 | 26..38 | 蛮兵 `(0,66,31)` |

体力 **-30 未改**。`monster.yml` wave2a/wave2b 条件链本轮**未再改**（沿用门2修）。

---

## 2. 方案 A 核心变更（MM）

**改前：**
```yaml
EmberDailySkeleton:
  Equipment:
  - LEATHER_HELMET HEAD
  - BOW HAND
```

**改后：**
```yaml
EmberDailySkeleton:
  # 2026-09-28 S1 回廊近战化：去弓 → 木剑贴脸；Display/$kill 名不变
  Type: SKELETON
  Display: '&a灰烬庭院骷髅'   # 不变 → DP $kill{mobname=灰烬庭院骷髅;amount=3}
  Options:
    PreventRandomEquipment: true   # 新增，避免随机回弓
  Equipment:
  - LEATHER_HELMET HEAD
  - WOOD_SWORD HAND               # 1.12.2 材质名（非 WOODEN_SWORD）
```

- 无 Shoot/弓 skill（原本也无，远程来自原版弓 AI）
- SoftHit / 死亡掉落 / Health35 Damage4 **未改**
- **备选 B 未启用**（未把 wave2b 改刷僵尸）

---

## 3. 改动文件

| 路径 | 变更 |
|------|------|
| `plugins/MythicMobs/Mobs/EmberDaily.yml` | EmberDailySkeleton：BOW→WOOD_SWORD + PreventRandomEquipment |
| `docs/status/STATUS-ember-daily-dnf-s1.md` | 本文件 |
| `server-runtime/config-backups/ember_daily-s1-skel-melee-20260928-033209/` | 改前 MM + DP YAML 备份 |

**未改：** 体力数值 / CoreRpg jar（仍 1.15.13）/ DP `monster.yml` 波次 / map region / option.yml。  
**未 commit/push。**

---

## 4. 验收证据（真击杀 · 禁 killall）

账号：`MeOp77` / `MeAc5991` · 日志 `/tmp/s1-skel-melee-verify.log` · JSON `/tmp/s1-skel-melee-verify.json`

| 检查 | 结果 | 证据 |
|------|------|------|
| MM 无 BOW · 有 WOOD_SWORD · Display 灰烬庭院骷髅 | ✅ | YAML 抽检 + checks |
| YAML 无 `$kill-any` · wave2a/2b | ✅ | `monster.yml` |
| 进本 · 体力 -30 | ✅ | 「[日常] 正在进入……（体力 -30）」 |
| 真击杀 wave1 → 门1 | ✅ | 「前厅已清 · 门开了」（hits=20 · ≈13s） |
| 真击杀 wave2a | ✅ | 「回廊前段已清 · 骷髅压上」（hits=12 · ≈8s） |
| wave2b 启动 | ✅ | 「【回廊·后段】骷髅×3 —— 清完开 Boss 门」 |
| 真击杀 wave2b →「回廊已清」 | ✅ | 「**回廊已清 · Boss 门开了**」（hits=9 · ≈4s） |
| 门2 z=25 AIR×12 | ✅ | iron=0 air=**12**（十二格全 AIR） |
| Boss 垫可达 | ✅ | pos `(0.5,66,29.5)` +「【终厅·中央垫】Boss 蛮兵！…」 |
| 无「was shot by」远程击杀 | ✅ | deaths=[] · `no_shot_death` |
| 未用 `mm mobs killall` 推进 `$kill` | ✅ | 脚本仅真挥击（探针曾用 killall 清测试刷怪，与验收无关） |
| 体力 | ✅ 未改 | 仍见「体力 -30」 |
| `ops.json` | ✅ `[]` | play + login |
| jar bump | ❌ 未 bump | 纯 YAML · CoreRpg 1.15.13 |

---

## 5. 验收命令（建议测试岗复测门2→Boss）

```
/corerpg enter daily
# 真打清前厅×5 → 「前厅已清 · 门开了」
# 真打清回廊僵尸×3 → 「回廊前段已清 · 骷髅压上」
# 真打清骷髅×3（现为近战木剑）→ 「回廊已清 · Boss 门开了」；查 z=25 十二格 AIR
# 进 Boss 垫 (0,66,31) → 终厅播报
```

注意：`mm mobs killall` **不计** DP `$kill`。大改后若见旧装备：确认 play 已加载新 `EmberDaily.yml`（本岗验收前已短重启 play）。

---

## 6. 不做 / 债务

1. ~~回廊 start「先清远程」~~ → 已改为「先清僵尸」（随 S1 PASS 入库）  
2. 未打 Boss 通关结算（本派工成功标准止于门2 AIR + Boss 垫可达）  
3. 未改体力 / 未 bump jar / 未 commit/push  
4. 备选 B 未用  

---

## 7. 回报主代理 / 总控

- **方案：** A（骷髅近战）· **未退 B**  
- **可派测试：** priority true · 复测 **门2→Boss**（wave1/2a/2b + 门2 AIR 本岗已实锤）  
- **STATUS：** `docs/status/STATUS-ember-daily-dnf-s1.md`  
- **改了：** 仅 `plugins/MythicMobs/Mobs/EmberDaily.yml`（EmberDailySkeleton）  
- **Display/$kill：** 仍「灰烬庭院骷髅」  
- **jar：** 未 bump（1.15.13）  
- **备份：** `server-runtime/config-backups/ember_daily-s1-skel-melee-20260928-033209/`  
- **证据：** `/tmp/s1-skel-melee-verify.json` · `/tmp/s1-skel-melee-verify.log`  
- **ops：** `[]`  
