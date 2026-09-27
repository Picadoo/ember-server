# STATUS · 深渊看守·深 TTK 复测（4.3b 二调后）

**日期：** 2026-09-27 17:08～17:24（Asia/Shanghai / CST）  
**执行岗：** 余烬-测试  
**依据：** `docs/design-stage4-abyss-9-12.md` §2.3；前置 `STATUS-ember-abyss-ttk-4.3.md` FAIL（20000/7 → ≈205s / ≈100%）；怪物岗 `STATUS-ember-abyss-watcher-deep-4.3b.md`（Health **7500** / Damage **18** / LM health **0**）  
**账号：** 主测 **`Atk43b_8302`**（非 op）；管理 `RpgBot`（uuid `148ec8b6-253f-36f4-bcd7-bf2395b8df80`，仅测时 op）  
**脚本：** `/tmp/abyss-ttk-43b.js`（自 `/tmp/abyss-ttk-43.js` 复用 + 复活点击 / 低血拉开 / 修正「则挑战失败」误判）· stdout `/tmp/abyss-ttk-43b-out.txt` · 标记 **`ABYSS_TTK_43B_RESULT`**  
**未改：** 怪物 / DP / CoreRpg YAML / jar / Paper（只读确认 + `/mm reload` + 复测）

---

## 总评

# **FAIL**

血量 20000→7500 后 TTK 已从 ≈205s **压到 ≈98s / ≈91s**（对比抬血前 ≈5s / ≈2s），方向正确且 **未触发**「轻易 &lt;30s」硬 FAIL。但仍 **略长于** §2.3 窗口（10 层上沿 80s；12 层上沿 90s 外溢 ≈0.9s），结束剩血采样仍偏高（10≈61% / 12≈100%）。中途 **2 次 DP 复活**（Damage 18 站桩可致死），清层 Instant Health + 复活满血使结束剩血采样偏乐观。

另：首次账号 `Atk43b_7201` 因脚本把「超过 N 秒…**则**挑战失败」警告误判为团灭提前退出（TTK=null），已修正则后以 `Atk43b_8302` 重跑；**以本局为准**。

---

## 第 10 / 12 判定

| 层 | 目标 TTK | 实测 TTK | 上次(4.3) | 再上次(抬血前) | 目标剩血 | 实测剩血 | 判定 |
|----|----------|----------|-----------|----------------|----------|----------|------|
| **10 看守·深** | 50～80s | **≈97.9s** | ≈204.8s | ≈5.0s | 25～50% | **≈61%** | **FAIL** |
| **12 看守·深** | 55～90s | **≈90.9s** | ≈204.7s | ≈2.0s | 25～55% | **≈100%** | **FAIL** |

硬规则：烬刃轻易 **&lt;30s** → FAIL：**未触发**（两层均 ≫30s）。  
未达标：TTK **略长** + 结束剩血 **偏高**（12 层采样被清层瞬奶/复活拉满）。

### TTK 取证（游玩服 `latest.log`，CST）

| 事件 | 时间 |
|------|------|
| 第8→9（floor9 通过） | 17:20:31 |
| 第10 战中阵亡 + `/dungeon revive`（剩 1） | 17:21:12 |
| 第9→10（floor10 通过） | 17:22:13 → bot **TTK_10=97.9s**（appear 时钟） |
| 第10→11 | 17:22:19 |
| 第12 战中阵亡 + revive（剩 0） | 17:23:53 |
| 第11→12（floor12 通过） | 17:23:54 → bot **TTK_12=90.9s** |
| 顶层 settle | 17:23:54～ |

整局战斗 ~306s · hits 375 · casts 33 · **revives 2** · eats 18 · 客户端 `minHp≈0.36/40`（战中压力真实）。  
无 MythicMobs `Mob HP is greater than server's maxHealth` WARN。

---

## 配置确认（测前只读）

| 项 | 值 |
|----|-----|
| `EmberAbyss.yml` → WatcherDeep Health | **7500** |
| Damage | **18** |
| LevelModifiers health | **0** |
| `spigot.yml` `attribute.maxHealth.max` | **20000.0** |
| DP spawn `level=` | **1**（floor 10/12） |
| 游玩服 | 怪物岗短重启后已加载；本测前再停服写 ops → `./start.sh custom`；进本前 `/mm reload` |

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
| 清层 | 自然 1→12（真实 DP 刷怪 / 真实 Boss 战；DP 复活×2 用尽） |

---

## 调参建议（只建议 · 本岗不改）

粗估有效 DPS（含复活空档）：7500/98 ≈ **76**/s 量级；去掉死亡空档后 TTK 会更接近设计中位。

1. **再降 Health**：目标中位 TTK≈65s → 粗估 **≈5500～6500**（人手若更高 DPS 可取偏上如 **6000～7000**）。现 7500 对 bot 仍略肉（97.9 / 90.9）。  
2. **Damage 勿再大幅抬**：18 已使满配烬刃站桩 **必须复活**（minHp≈0）；再抬易变成「靠复活硬磨」而非「剩血 25～50%」。若要压结束剩血，优先 **略抬至 19～20 试一档** 或接受清层 Instant Health 会拉高结束采样——建议同时看战中 `minHp` / 复活次数，勿只看通关瞬间剩血%。  
3. LM health 保持 **0**；`maxHealth.max` 维持 **20000**。  
4. 改完同参照档再跑 `/tmp/abyss-ttk-43b.js`。  
5. **不要**为达标削玩家天赋/装备。

---

## ops / 端口

| 步骤 | 结果 |
|------|------|
| 停游玩服 → 写 ops RpgBot op4 | OK（17:08） |
| `JAVA_HOME=.../jdk8u504-b01 ./start.sh custom` | Done **17:09:01** |
| 测中临时 `/op Atk43b_*` 仅用于 `enhance set`，随即 `/deop` | OK |
| 测完停服 → **`ops.json=[]`** → 再起 | Done（pid **2005815**；**17:24:28**） |
| 三端口 | **25565** 代理 · **25566** 登录 · **25567** 游玩 · 均 LISTEN |
| 结束后 ops | **`[]`** |

---

## 产物路径

| 路径 | 说明 |
|------|------|
| `/workspace/minecraft/STATUS-ember-abyss-ttk-4.3b.md` | 本报告 |
| `/workspace/minecraft/STATUS-ember-abyss-ttk-4.3.md` | 上次（20000/7）对照 |
| `/workspace/minecraft/STATUS-ember-abyss-watcher-deep-4.3b.md` | 怪物岗二调说明 |
| `/tmp/abyss-ttk-43b.js` | 测法脚本（勿提交） |
| `/tmp/abyss-ttk-43b-out.txt` | `ABYSS_TTK_43B_RESULT` JSON |
| `/tmp/abyss-ttk-43b-run2.log` | 有效局日志（`Atk43b_8302`） |
| `server-runtime/logs/latest.log` | 17:18～17:24 progress 证据（测后已短重启，旧证据在测时 log） |

---

## ABYSS_TTK_43B_RESULT（摘要）

```
verdict=FAIL
account=Atk43b_8302
TTK_10=97.9  endHpPct_10=61  judge_10=FAIL
TTK_12=90.9  endHpPct_12=100 judge_12=FAIL
deaths=0  revives=2  level=60  blade=+7  tal=+4  talent_spent=30
minHp≈0.36/40  combatSec=306  hits=375
config=Health7500/Damage18/LMhealth0/maxHealth20000
vs_last: TTK_10 5.0→204.8→97.9  TTK_12 2.0→204.7→90.9
```
