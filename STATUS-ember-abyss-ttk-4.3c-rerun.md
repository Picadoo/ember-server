# STATUS · 深渊看守·深 TTK 同档复跑（4.3c-rerun）

**日期：** 2026-09-27 17:38～17:46（Asia/Shanghai / CST）  
**执行岗：** 余烬-测试  
**依据：** `docs/design-stage4-abyss-9-12.md` §2.3；前置 `STATUS-ember-abyss-ttk-4.3c.md`（Health6000/Damage18 · 10=66.1s/94%/minHp≈36/复活0 · 12=87.9s/44%/minHp≈1/复活2）  
**目的：** **勿改怪**，同档再跑 1 局，确认第 10 层压力是 **方差** 还是 **稳定偏软**  
**账号：** 主测 **`Atk43cr_5521`**（非 op）；管理 `RpgBot`（uuid `148ec8b6-253f-36f4-bcd7-bf2395b8df80`，仅测时 op）  
**脚本：** `/tmp/abyss-ttk-43c.js`（未改）· stdout `/tmp/abyss-ttk-43c-rerun-run.log` · 标记 **`ABYSS_TTK_43C_RERUN_RESULT`** · `/tmp/abyss-ttk-43c-rerun-out.txt`  
**未改：** 怪物 / DP / CoreRpg YAML / jar / Paper（Health **6000** / Damage **18** / LM health **0** 只读确认）

---

## 总评

# **FAIL**（严格剩血窗 · 第 12 **PASS**）——第 10「稳定偏软」假说 **否定（方差）** · **不建议 Damage 19**

同档 Health6000/Damage18 复跑后：

| 对照点 | 上局 4.3c | 本局 rerun | 解读 |
|--------|-----------|------------|------|
| 10 TTK | 66.1s | **72.6s** | 均入窗 50～80；略慢 |
| 10 结束% | 94% | **100%** | 仍 ≫50；本局被 **复活满血** 拉高 |
| 10 战中 minHp | ≈36.2/40 | **≈0.95/40** | 上局轻压 → 本局近死 |
| 10 复活 | 0 | **1** | 本局第 10 真实致死 |
| 12 TTK | 87.9s | **78.1s** | 均入窗 55～90 |
| 12 结束% | 44% | **48%** | 均入窗 25～55 |
| 12 战中 minHp | ≈1.08/40 | **≈2.19/40** | 两局均高压 |
| 12 复活 | 2 | **1** | 仍需复活撑住 |

**判定指引落地：**
- 「仍明显偏软（minHp 高、复活0、剩血≫50%）」→ **不满足**（本局 minHp≈0.95、复活1）→ **不建议再试 Damage 19**
- 「剩血/minHp 已入窗或接近」→ 战中压力已入致死区；结束% 仍出窗但是 **复活后采样假象**，可议看战中口径
- 硬规则 &lt;30s：**未触发**（72.6 / 78.1）

**结论给总控：** 第 10 上局偏软是 **单局方差**，不是稳定偏软。维持 **6000/18**；若再抬 Damage→19，第 12 已靠复活余量（本局剩 1 次、上局用尽 2 次），风险更大。严格结束%窗下总评仍 **FAIL**，但调参方向应 **保持 Damage 18**，勿因结束% 盲目抬伤。

---

## 第 10 / 12 判定

| 层 | 目标 TTK | 实测 TTK | 上局(4.3c) | 目标剩血 | 实测结束% | 战中 minHp | 复活 | 判定 |
|----|----------|----------|------------|----------|-----------|------------|------|------|
| **10 看守·深** | 50～80s | **≈72.6s** | ≈66.1s | 25～50% | **≈100%** | ≈0.95/40 | **1** | **FAIL**（TTK✓ 结束%✗；结束%=复活满血，战中已致死） |
| **12 看守·深** | 55～90s | **≈78.1s** | ≈87.9s | 25～55% | **≈48%** | ≈2.19/40 | **1** | **PASS**（TTK✓ 剩血✓） |

硬规则：烬刃轻易 **&lt;30s** → FAIL：**未触发**。

### TTK 取证（游玩服 `latest.log`，CST）

| 事件 | 时间 |
|------|------|
| `/mm reload` + `mobs info EmberAbyssWatcherDeep` | 17:40:34～36 |
| 菜单进本 / 地牢创建 EmberAbyss | 17:41:42～49 |
| 第8→9 | 17:43:28 |
| 第10 战中阵亡+`/dungeon revive` | **17:44:28**（剩 1） |
| 第9→10（floor10 通过） | 17:44:44 → bot **TTK_10=72.6s** |
| 第10→11 | 17:44:48 |
| 第12 战中阵亡+`/dungeon revive` | **17:45:48**（剩 0） |
| 第11→12（floor12 通过） | 17:46:09 → bot **TTK_12=78.1s** |
| 顶层 settle | 17:46:12 |

无 MythicMobs `Mob HP is greater than server's maxHealth` WARN。配置未改动。

---

## 配置确认（测前/测后只读 · 未改）

| 项 | 值 |
|----|-----|
| `EmberAbyss.yml` → WatcherDeep Health | **6000**（未改） |
| Damage | **18**（未改） |
| LevelModifiers health | **0** |
| `spigot.yml` `attribute.maxHealth.max` | **20000.0**（未改） |

---

## 参照档落实（与 4.3c 同）

| 项 | 值 |
|----|-----|
| 等级 | **Lv.60**（earned=30） |
| 誓约 | 烬刃 blaze |
| 天赋 | 一层满 + 二层 `blaze_ember2` / `blaze_heat2` / `blaze_second` · **已花=30 / 已获=30** |
| 武器 | `gear_ember_t2_blade` **+7** · Sharpness III |
| 饰品 | `gear_ember_t2_talisman` **+4** |
| 属性（进本前） | 攻击 **+55.0** · 生命 **103.0** · 减伤 52.4% · 暴击 5.5% |
| 补给 | 面包×128 · `ticket_ember_abyss` |
| 战斗 buff | **无** |
| 进本 | `/ember` → 深渊 → 开始下潜 · **PASS** |
| 清层 | 自然 1→12；DP 复活×2（10 层 1 次 + 12 层 1 次） |

---

## 调参建议（只建议 · 本岗不改）

1. **Health 维持 6000 / Damage 维持 18** —— 两层 TTK 入窗；第 10 战中压力已可致死（方差），抬伤无依据。  
2. **不建议 Damage 19**：上局指引「若仍明显偏软再试 19」；本局已否偏软。第 12 复活余量紧张（0～2）。  
3. 判定继续同时看 **结束% · 战中 minHp · 复活次数**；结束% 在复活局易虚高。  
4. LM health 保持 **0**；`maxHealth.max` 维持 **20000**。  
5. 若总控仍盯结束%窗，优先再同档采样 1～2 局看结束%分布，**勿**先抬伤。

---

## ops / 端口

| 步骤 | 结果 |
|------|------|
| 停游玩服 → 写 ops RpgBot op4 | OK（17:38） |
| `JAVA_HOME=.../jdk8u504-b01 ./start.sh custom` | Done **17:38:44**（测前 pid **3259166**） |
| 测中临时 `/op Atk43cr_*` 仅用于 `enhance set`，随即 `/deop` | OK |
| 测完停服 → **`ops.json=[]`** → 再起 | Done（pid **2048201**；**17:46:37** CST） |
| 三端口 | **25565** 代理 · **25566** 登录 · **25567** 游玩 · 均 LISTEN |
| 结束后 ops | **`[]`** |

---

## 产物路径

| 路径 | 说明 |
|------|------|
| `/workspace/minecraft/STATUS-ember-abyss-ttk-4.3c-rerun.md` | 本报告 |
| `/workspace/minecraft/STATUS-ember-abyss-ttk-4.3c.md` | 上局（同档首跑）对照 |
| `/tmp/abyss-ttk-43c.js` | 测法脚本（复用 · 勿提交） |
| `/tmp/abyss-ttk-43c-rerun-out.txt` | `ABYSS_TTK_43C_RERUN_RESULT` JSON |
| `/tmp/abyss-ttk-43c-rerun-run.log` | 本局日志（`Atk43cr_5521`） |

---

## ABYSS_TTK_43C_RERUN_RESULT（摘要）

```
verdict=FAIL
account=Atk43cr_5521
TTK_10=72.6  endHpPct_10=100  minHp_10≈0.95/40  revives_10=1  judge_10=FAIL
TTK_12=78.1  endHpPct_12=48   minHp_12≈2.19/40 revives_12=1  judge_12=PASS
deaths=0  revives=2  level=60  blade=+7  tal=+4  talent_spent=30
minHp≈0.82/40  combatSec=254  hits=320
config=Health6000/Damage18/LMhealth0/maxHealth20000  (UNCHANGED)
vs_last(4.3c): TTK_10 66.1→72.6  end% 94→100  minHp 36.2→0.95  revives 0→1
               TTK_12 87.9→78.1  end% 44→48   minHp 1.08→2.19  revives 2→1
note: 第10上局偏软=方差；本局战中致死+复活1 → 结束100%为复活拉高。不建议 Damage19。
suggest_damage19=NO
```
