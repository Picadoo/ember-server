# DESIGN · 词缀行为原语（D241 / ARCH S3-12 / CoreRpg 1.65.67）

**日期：** 2026-10-06 Asia/Shanghai · **范围：** ARCH §5 S3 最后一项（每个词缀一个原语类 + 单测 + 伤害轨迹回放比对 + p1sim 建模说明）

## 1. 包与类

`CoreRpg/src/main/java/town/sunshine/corerpg/p1/encounter/`

| 类 | 作用 |
|---|---|
| `AffixFamily` | 形状族（10 个）：CIRCLE / STRIP / CROSS / BEAM / TETHER / AURA / CHANNEL / DEATH_BLAST / DEATH_SPAWN / STAT |
| `AffixBehavior` | 接口：`id()` / `family()` / `fx()`（待机粒子名）/ `firstEvery(v)`（晋升时首轮节奏）/ `how(v)`（进房介绍文案）/ `label()` |
| `AffixCycle` | 周期词缀共用的预警时钟（毫秒，算式与旧 Director 一致）：`firstNext` = now + 1.5 s + every；`ready` / `armed` / `lands`；`after`；`fmt` |
| `EmberShape` | `inShape`（circle / strip / cross 命中判定），从 Director 原样搬出 |
| `EmberAffixes` | 12 个原语的注册表（`Variety.KNOWN` 顺序）；未知 id 保持旧回退（炽热节奏 / id 原文 / END_ROD） |

12 个词缀各一类（`INSTANCE` + 静态数学，Bukkit 只用 `Location`）：

| 类 | 族 | 原语里有什么 |
|---|---|---|
| `AffixBlazing` 炽热 | CIRCLE | `skill(atk,v)` 脚下火圈；`ENGAGE`=6 |
| `AffixSplit` 分裂 | DEATH_SPAWN | `count` / `addMaxHp`（≥1） |
| `AffixShield` 厚甲 | STAT | `maxHp`（基础 × hp 倍率） |
| `AffixRegen` 再生 | CHANNEL | `windowEnd` / `interrupted` / `heal` / `counts`；`ENGAGE`=8 |
| `AffixCharge` 冲锋 | STRIP | `skill(atk,v,dir)` / `roomFor` / `aim`；`ENGAGE`=10 |
| `AffixFrost` 凝霜 | AURA | `nextTick` / `inAura` / `slowTicks` / `ownSlow` |
| `AffixMortar` 投弹 | CIRCLE | `skill` 落在最近玩家脚下的圈；`REACH`=16 |
| `AffixMolten` 亡爆 | DEATH_BLAST | `triggers`（分裂小怪不触发）/ `blastDmg` / `warnAt` / `boomAt` / `blast` |
| `AffixVenom` 毒十字 | CROSS | `skill` / `dirs` / `hits`；`ENGAGE`=8 |
| `AffixJailer` 禁锢 | CIRCLE | `skill` + `rootTicks` |
| `AffixArcane` 旋光 | BEAM | `dmg` / `angle` / `startAngle` / `swept`（扫掠区间判定）/ `spun`；`ENGAGE`=8 |
| `AffixFirechain` 火链 | TETHER | `dmg` / `liveAt` / `tooFar` / `touches`（点到线段）/ `burnReady`；`RELINK_RETRY_MS`=1000 |

`EmberRunDirector`：`promote` 与 `affixTick` 改走注册表 + `AffixCycle` + 原语；旧静态助手（`blazeSkill` / `chargeSkill` / `mortarSkill` / `moltenSkill` / `venomSkill/Dirs/Hits` / `jailerSkill/RootTicks` / `arcaneAngle/StartAngle/Swept` / `chainTouches/BurnReady` / `inShape`）保留为一行委托（老单测和调用点不动）。2448 → 2328 行。

## 2. 行为保持的证据

- **单测**：`EmberAffixPrimitivesTest` ×15（注册表顺序 / 回退、每类数值、几何边界、周期时钟、介绍文案逐字）。
- **伤害轨迹回放**：`EmberAffixReplayTest`。`LegacyAffixPath` = 46736db（1.65.66）Director 静态方法逐字拷贝；`PrimitiveAffixPath` = D241 原语。`AffixReplay` 用固定种子生成脚本（玩家走位 / 受击 / 死亡），640 tick × 50 ms，第 520 tick 精英死亡（触发熔火 / 分裂）。200 种子 × 12 词缀 → 18133 行轨迹（施放 / 命中 / 伤害 / 其他事件），两条路径 **sha256 相同**：`de42455402bbce314c4d6622e603c2f4d4a761bc03981634066fa1f1b5797c39`。第二个测试把一处伤害改 1 ULP，确认比对能抓到。输出：`CoreRpg/target/affix-replay/summary.txt` + 两个 `.tsv`。
- **live 冒烟**：12 个词缀在 Q01 重复本各强制一次（`corerpg p1 runs variety <affix>:r1`），见 `docs/tests/smoke-2026-10-06-d241-papi-affix.md`。

## 3. p1sim 怎么用同一套原语（本刀未改 p1sim）

现状（`tools/p1sim/p1sim.py` `affix_mob`，约 949 行）与 Java 原语的差异：

| 词缀 | p1sim | Java 原语（真实） |
|---|---|---|
| blazing / venom / jailer | 周期 = `every`，首击 t0 + 1.5 + every | 周期 = every + warn（`ready` 后才开始预警，`lands` 在 warn 之后，再 `next = now + every`），首击晚 warn 秒 |
| arcane | 周期 = every + spin | 周期 = warn + spin + every |
| firechain | tick / FIRECHAIN_EXPOSURE（无坐标，按接触比例） | 每 tick 秒最多烧一次，需真正碰到链（`touches`） |
| mortar / charge / regen / frost / molten | 未建模 | 有 |
| shield / split | 已建模（血量倍率 / 分裂） | 同 |

p1sim 偏差方向：周期词缀**偏密**（更难），是安全侧；直接改数字会移动 gate 结果，所以本刀不动。

建议做法（S4 之后单独一刀，需要重跑 gate）：

1. 仿照 `tools/p1sim/javacheck/BurnCheck.java`，写一个 `AffixTable.java` 驱动：遍历 `EmberAffixes.all()`，对 live `variety` 配置输出每个词缀的 `family`、首击时间、周期（含 warn / spin）、每击伤害系数、命中形状参数，写成 JSON/TSV。
2. p1sim `affix_mob` 读这张表，替换手抄的 `every` / `dmg`；形状族决定闪避模型（CIRCLE / CROSS / STRIP 走现有 dodge；BEAM 一次 / 人 / 轮；TETHER 按暴露比例；AURA / STAT / DEATH_* 走 HP 或节奏修正）。
3. 加一个 `javacheck` 一致性检查（像 burncheck）：表里数值与 p1sim 实际用的数值不一致就失败，防止再手抄漂移。
4. 有了表之后再决定是否把 mortar / charge / regen / frost / molten 纳入 sim（会让结果更难，需要重新看 gate）。

## 4. 不变

所有词缀数值、`variety` yml、伤害、节奏、文案、粒子；`balance_version` 58；p1sim；C15；内容包。
