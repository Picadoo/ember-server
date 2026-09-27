# STATUS · 深渊看守·深 TTK 复测（4.3 抬血后）

**日期：** 2026-09-27 16:50～17:02（Asia/Shanghai / CST）  
**执行岗：** 余烬-测试  
**依据：** `docs/design-stage4-abyss-9-12.md` §2.3；前置 `STATUS-ember-abyss-ttk-layer2.md` FAIL；怪物岗 `STATUS-ember-abyss-watcher-deep-4.3.md`（Health **20000** / Damage **7** / LM health **0**）  
**账号：** 主测 **`Atk43_5102`**（非 op）；管理 `RpgBot`（uuid `148ec8b6-253f-36f4-bcd7-bf2395b8df80`，仅测时 op）  
**脚本：** `/tmp/abyss-ttk-43.js` · stdout `/tmp/abyss-ttk-43-out.txt` · 标记 **`ABYSS_TTK_43_RESULT`**  
**未改：** 怪物 / DP / CoreRpg YAML / jar / spigot（只读确认 + 复测）

---

## 总评

# **FAIL**

抬血后「看守·深」已不再被秒杀（对比上次 ≈5s / ≈2s），但 **TTK 远超 §2.3 窗口（≈205s）**，结束剩血仍接近满血（采样 **100%**；战中 `minHp` 曾降至客户端 ≈18/40）。方向从「过脆」翻成「过肉 + 压力不足」。

---

## 第 10 / 12 判定

| 层 | 目标 TTK | 实测 TTK | 上次 | 目标剩血 | 实测剩血 | 判定 |
|----|----------|----------|------|----------|----------|------|
| **10 看守·深** | 50～80s | **≈204.8s** | ≈5.0s | 25～50% | **≈100%** | **FAIL** |
| **12 看守·深** | 55～90s | **≈204.7s** | ≈2.0s | 25～55% | **≈100%** | **FAIL** |

硬规则：烬刃轻易 **&lt;30s** → FAIL：**未触发**（两层均 ≫30s）。  
未达标原因：TTK **偏长** + 结束剩血 **偏高**。

### TTK 取证（游玩服 `latest.log`，CST）

| 事件 | 时间 |
|------|------|
| 第8→9（floor9 通过） | 16:54:59 |
| 第9→10（floor10 通过） | 16:58:27 → **TTK_10 ≈ 205s**（与 bot appear 时钟 204.8s 一致；层间 DP delay≈3s 已计入启动侧） |
| 第10→11（floor11 通过） | 16:58:31 |
| 第11→12（floor12 通过） | 17:01:59 → **TTK_12 ≈ 205s**（bot 204.7s） |
| 顶层 settle | 17:02:02 |

整局战斗 ~519s · hits 699 · casts 60 · **死亡 0**。  
**无** MythicMobs `Mob HP is greater than server's maxHealth` WARN（20000 上限已兑现）。

---

## 配置确认（测前只读）

| 项 | 值 |
|----|-----|
| `EmberAbyss.yml` → WatcherDeep Health | **20000** |
| Damage | **7** |
| LevelModifiers health | **0** |
| `spigot.yml` `attribute.maxHealth.max` | **20000.0** |
| DP spawn `level=` | **1**（floor 10/12） |
| 游玩服 | 怪物岗短重启后已加载；本测前再停服写 ops → `./start.sh custom` |

---

## 参照档落实

| 项 | 值 |
|----|-----|
| 等级 | **Lv.60**（earned=30） |
| 誓约 | 烬刃 blaze |
| 天赋 | 一层满 + 二层 `blaze_ember2` / `blaze_heat2` / `blaze_second` · **已花=30 / 已获=30** |
| 武器 | `gear_ember_t2_blade` **+7** · Sharpness III |
| 饰品 | `gear_ember_t2_talisman` **+4** |
| 属性（进本前） | 攻击 **+55.0** · 生命 **103.0** · 减伤 52.4% · 暴击 5.5% |
| 补给 | 面包×64 · `ticket_ember_abyss` |
| 战斗 buff | **无**（不做 resistance/strength） |
| 进本 | `/ember` → 深渊 → 开始下潜 · **PASS** |
| 清层 | 自然 1→12（真实 DP 刷怪 / 真实 Boss 战） |

---

## 调参建议（只建议 · 本岗不改）

估算 bot 有效 DPS ≈ 20000/205 ≈ **98**/s（同脚本、同攻击节奏；人手点攻可能略高）。

1. **下调 Health**：目标中位 TTK≈65s → 粗估 Health **≈5500～8000**（人手若更高 DPS 可取偏上，如 **7000～9000**）；**勿再抬**至接近 20000。  
2. **大幅抬 Damage**（现 **7**）：战中 `minHp`≈18/40 说明有过压力，但吸血/清层瞬奶使结束采样≈满血；建议试 **14～22** 把结束剩血压进 **25～50%（10）/ 25～55%（12）**。  
3. LM health 保持 **0**；`maxHealth.max` 维持 **20000** 即可。  
4. 改完同参照档再跑 `/tmp/abyss-ttk-43.js`（或等价脚本）。  
5. **不要**为达标削玩家天赋/装备。

---

## ops / 端口

| 步骤 | 结果 |
|------|------|
| 停游玩服 → 写 ops RpgBot op4 | OK（16:50） |
| `JAVA_HOME=.../jdk8u504-b01 ./start.sh custom` | Done **16:50:13** |
| 测中临时 `/op Atk43_5102` 仅用于 `enhance set`，随即 `/deop` | OK |
| 测完 `/deop RpgBot` → 停服 → **`ops.json=[]`** → 再起 | Done（pid **2009387**；**17:02** 前后） |
| 三端口 | **25565** 代理 · **25566** 登录 · **25567** 游玩 · 均 LISTEN |
| 结束后 ops | **`[]`** |

---

## 产物路径

| 路径 | 说明 |
|------|------|
| `/workspace/minecraft/STATUS-ember-abyss-ttk-4.3.md` | 本报告 |
| `/workspace/minecraft/STATUS-ember-abyss-ttk-layer2.md` | 上次（抬血前）对照 |
| `/tmp/abyss-ttk-43.js` | 测法脚本（勿提交） |
| `/tmp/abyss-ttk-43-out.txt` | `ABYSS_TTK_43_RESULT` JSON |
| `server-runtime/logs/latest.log` | 16:54:59～17:02:02 progress 证据 |

---

## ABYSS_TTK_43_RESULT（摘要）

```
verdict=FAIL
account=Atk43_5102
TTK_10=204.8  endHpPct_10=100  judge_10=FAIL
TTK_12=204.7  endHpPct_12=100  judge_12=FAIL
deaths=0  level=60  blade=+7  tal=+4  talent_spent=30
minHp≈17.8/40  combatSec=519  hits=699
config=Health20000/Damage7/LMhealth0/maxHealth20000
vs_last: TTK_10 5.0→204.8  TTK_12 2.0→204.7
```
