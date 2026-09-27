# STATUS · 深渊看守·深 TTK 复测（4.3c 三调后）

**日期：** 2026-09-27 17:28～17:37（Asia/Shanghai / CST）  
**执行岗：** 余烬-测试  
**依据：** `docs/design-stage4-abyss-9-12.md` §2.3；前置 `STATUS-ember-abyss-ttk-4.3b.md` FAIL（7500/18 → ≈97.9s/≈90.9s · 剩血偏高）；怪物岗已将 Health **7500→6000**（Damage **18** / LM health **0** 不变）  
**账号：** 主测 **`Atk43c_9104`**（非 op）；管理 `RpgBot`（uuid `148ec8b6-253f-36f4-bcd7-bf2395b8df80`，仅测时 op）  
**脚本：** `/tmp/abyss-ttk-43c.js`（自 43b 复用 + 分Boss `minHp`/`revives` 快照）· stdout `/tmp/abyss-ttk-43c-run.log` · 标记 **`ABYSS_TTK_43C_RESULT`** · `/tmp/abyss-ttk-43c-out.txt`  
**未改：** 怪物 / DP / CoreRpg YAML / jar / Paper（只读确认 + `/mm reload` + 复测）

---

## 总评

# **FAIL**（第 12 **PASS** · 第 10 **FAIL** 仅因结束剩血）

Health 7500→**6000** 后 TTK 轨迹 **5→205→97.9/90.9→66.1/87.9**：两层 TTK **均落入** §2.3 窗口（10：50～80；12：55～90），未触发「轻易 &lt;30s」硬 FAIL。  
第 12 层结束剩血 **44%** 达标；第 10 层结束剩血 **94%** 仍偏高 → 总评 FAIL。

**结束% vs 战中压力（重要）：**
- 第 10：战中 `minHp_10≈36.2/40`（≈90%）、**复活 0** —— 本局 10 层压力本来就轻，结束 94% **不是**清层瞬奶/复活拉高，而是战中几乎未承伤。
- 第 12：战中 `minHp_12≈1.08/40`、**复活 2**（用尽）—— 结束 44% 落在窗口内，但战中压力真实致死；结束采样已含复活满血后的回升。
- 全局 `minHp≈1.08` · `revives=2` · `combatSec≈264` · hits 324 · casts 28。

---

## 第 10 / 12 判定

| 层 | 目标 TTK | 实测 TTK | 上次(4.3b) | 再上次(4.3) | 抬血前 | 目标剩血 | 实测结束% | 战中 minHp | 复活 | 判定 |
|----|----------|----------|------------|-------------|--------|----------|-----------|------------|------|------|
| **10 看守·深** | 50～80s | **≈66.1s** | ≈97.9s | ≈204.8s | ≈5.0s | 25～50% | **≈94%** | ≈36.2/40 | **0** | **FAIL**（TTK✓ 剩血✗；结束%≈战中高血，非瞬奶假象） |
| **12 看守·深** | 55～90s | **≈87.9s** | ≈90.9s | ≈204.7s | ≈2.0s | 25～55% | **≈44%** | ≈1.08/40 | **2** | **PASS**（TTK✓ 剩血✓；结束%低于战中压力，复活拉回） |

硬规则：烬刃轻易 **&lt;30s** → FAIL：**未触发**（两层均 ≫30s）。

### TTK 取证（游玩服 `latest.log`，CST）

| 事件 | 时间 |
|------|------|
| `/mm reload` + `mobs info EmberAbyssWatcherDeep` | 17:30:54～56 |
| 地牢创建 EmberAbyss | 17:32:09 |
| 第8→9 | 17:33:52 |
| 第9→10（floor10 通过） | 17:35:02 → bot **TTK_10=66.1s**（appear 时钟） |
| 第10→11 | 17:35:07 |
| 第12 战中阵亡+revive（剩 1） | 17:35:37 |
| 第12 再阵亡+revive（剩 0） | 17:36:00 |
| 第11→12（floor12 通过） | 17:36:38 → bot **TTK_12=87.9s** |
| 顶层 settle | 17:36:38～40 |

无 MythicMobs `Mob HP is greater than server's maxHealth` WARN。

---

## 配置确认（测前只读）

| 项 | 值 |
|----|-----|
| `EmberAbyss.yml` → WatcherDeep Health | **6000** |
| Damage | **18** |
| LevelModifiers health | **0** |
| `spigot.yml` `attribute.maxHealth.max` | **20000.0**（未改） |
| 游玩服 | 怪物岗短重启后已加载 6000；本测前停服写 ops → `./start.sh custom`；进本前 `/mm reload` |

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
| 补给 | 面包×128 · `ticket_ember_abyss` |
| 战斗 buff | **无**（不做 resistance/strength） |
| 进本 | `/ember` → 深渊 → 开始下潜 · **PASS** |
| 清层 | 自然 1→12（真实 DP 刷怪 / 真实 Boss 战；DP 复活×2 用尽，均在第 12） |

---

## 调参建议（只建议 · 本岗不改）

粗估有效 DPS（含复活空档）：6000/66 ≈ **91**/s（10 层）；6000/88 ≈ **68**/s（12 层含 2 次死亡空档）。TTK 窗口已基本到位。

1. **Health 维持 ≈6000**（或极窄微调 5800～6200）：两层 TTK 已入窗，勿再大幅削血以免逼近 &lt;30s / 下沿。  
2. **Damage 勿大幅抬**：18 已使第 12 **必须双复活**（minHp≈1）；再抬易变成「复活断档团灭」而非「剩血 25～50%」。第 10 结束 94% 是本局战中压力偏轻（minHp≈36），**不是**清层奶假象——若要压 10 层结束%，优先 **复测确认方差**，再考虑 **+1 试 19** 并盯 12 层复活余量。  
3. 判定请同时看 **结束% · 战中 minHp · 复活次数**；12 层结束 44% 已达标，但压力靠复活撑住。  
4. LM health 保持 **0**；`maxHealth.max` 维持 **20000**。  
5. 改完同参照档再跑 `/tmp/abyss-ttk-43c.js`。  
6. **不要**为达标削玩家天赋/装备。

---

## ops / 端口

| 步骤 | 结果 |
|------|------|
| 停游玩服 → 写 ops RpgBot op4 | OK（17:28） |
| `JAVA_HOME=.../jdk8u504-b01 ./start.sh custom` | Done **17:29:07**（pid 起服后） |
| 测中临时 `/op Atk43c_*` 仅用于 `enhance set`，随即 `/deop` | OK |
| 测完停服 → **`ops.json=[]`** → 再起 | Done（pid **2674382**；**17:37:20** CST） |
| 三端口 | **25565** 代理 · **25566** 登录 · **25567** 游玩 · 均 LISTEN |
| 结束后 ops | **`[]`** |

---

## 产物路径

| 路径 | 说明 |
|------|------|
| `/workspace/minecraft/STATUS-ember-abyss-ttk-4.3c.md` | 本报告 |
| `/workspace/minecraft/STATUS-ember-abyss-ttk-4.3b.md` | 上次（7500/18）对照 |
| `/workspace/minecraft/STATUS-ember-abyss-ttk-4.3.md` | 再上次（20000/7）对照 |
| `/tmp/abyss-ttk-43c.js` | 测法脚本（勿提交） |
| `/tmp/abyss-ttk-43c-out.txt` | `ABYSS_TTK_43C_RESULT` JSON |
| `/tmp/abyss-ttk-43c-run.log` | 本局日志（`Atk43c_9104`） |
| `server-runtime/logs/latest.log` | 17:30～17:36 progress / 复活证据（测后已短重启清 ops） |

---

## ABYSS_TTK_43C_RESULT（摘要）

```
verdict=FAIL
account=Atk43c_9104
TTK_10=66.1  endHpPct_10=94  minHp_10≈36.2/40  revives_10=0  judge_10=FAIL
TTK_12=87.9  endHpPct_12=44  minHp_12≈1.08/40 revives_12=2  judge_12=PASS
deaths=0  revives=2  level=60  blade=+7  tal=+4  talent_spent=30
minHp≈1.08/40  combatSec=264  hits=324
config=Health6000/Damage18/LMhealth0/maxHealth20000
vs_last: TTK_10 5.0→204.8→97.9→66.1  TTK_12 2.0→204.7→90.9→87.9
note: 10层结束%高=战中压力轻（非瞬奶）；12层结束%达标但靠双复活，战中 minHp≈1
```
