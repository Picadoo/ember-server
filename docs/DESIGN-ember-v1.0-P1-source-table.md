# 余烬 v1.0 P1 · 来源开关表（G01 准备阶段）

> 对应策划书 `docs/design-ember-v1.0-P1.md` §4.5、§7.2、§19.4、§20.2、§22.2（C04/C05/C13）、§23.1。
> 本表只读代码与配置得出，**本次未改 Java、未重启、未做需要重载的实服测试**。
> 读取基线：`main` @ `f5f7de2`（CoreRpg 1.15.30，紧接 `0bea374`）。策划书自身核查快照为 `f73651e`，二者之间的提交只涉及 DP 刷怪点、超时文案等，不改变本表结论。
> 规则：表外没有登记的来源，在新模式 `ember-v1.0-P1` 里一律视为**不允许接入**（书 §20.2）。

## 0. 读了什么

- CoreRpg：`StatService`、`SkillService`、`GearPassiveService`、`SetService`、`EnhanceService`、`GearLore`、`ScrapService`（重铸）、`ForgeService`、`TalentService`、`CovenantService`、`LifeService`、`QuestService`（副本内事件）、`AfkTierService`、`CalamityService`、`PetService`、`FlexSkillService`、`ArenaService`、`LadderService`、`NiBridge`、`CoreRpgPlugin.onDeath`。
- 其他自研插件：`CoreCombat`（图腾/盾）、`CoreEnchant`（附魔表）、`CoreCraft`（`wipe_vanilla: true`）、`CoreWorldRules`、`CoreBrew/CoreFish/CoreSmelt/CoreAnvil`（未找到任何改伤害、改回复或加药水的代码）。
- 配置：`plugins/CoreRpg/{config.yml 的 stats 段,enhance,passives,set,skills,talent,covenant,life,scrap,forge,pet,guild,progress}.yml`；NI `Items/ember-*.yml`；MythicMobs `Mobs/*.yml`、`Skills/Ember*.yml`；DungeonPlus `dungeon/*/monster.yml`；`server.properties`、`Multiverse-Core/worlds.yml`、`spigot.yml`。
- **AttributePlus 是否在用**：`AttributePlus.jar` 在 `plugins/_parked/`，`latest.log` 中没有任何 AttributePlus 加载记录，实际加载的插件中也没有它。物品 lore 仍是 AP 格式（`物理伤害: +14` / `生命力: +35` / `物理防御: +6`），目前**只由 CoreRpg `StatService` 用正则自行解析这三个键**。AP 的 `data.db`/配置都还在目录里，但都不起作用。结论：当前物品**不经过** AttributePlus；如果有人恢复这个 jar，所有 lore 属性会被重复计算（见 A25）。

## 1. 当前伤害/回复时序（实际顺序）

玩家近战打怪（`EntityDamageByEntityEvent`，cause=`ENTITY_ATTACK`）：

1. **NMS 事件前**（`EntityHuman.attack`）：原版攻击属性（剑 6/7 + 力量 +3/级 − 虚弱 4）× (0.2+0.8c²)；锋利 (0.5L+0.5)×c；满足条件时**原版跳劈 ×1.5**（只乘原版部分）。结果作为 `getDamage()` 进入 Bukkit 事件。冷却在事件前已被重置。
2. `LOWEST`：`StatService.onDamage`（加装备平坦伤害 × 近似蓄力、系统暴击 ×(1.5+暴伤)、按**未减免**伤害吸血）；`QuestService.onPlayerHit`（记录最后攻击者）。
3. `NORMAL`？：MythicMobs `DamageModifiers` 生效（注释和 `docs/critic-fixes-20260927.md` 都假设它晚于 LOWEST，**优先级尚未实测**，见 §4）。
4. `HIGH`（都是乘法，同一优先级内按注册顺序执行）：`SkillService` 灰印 ×(1+pct)；`GearPassiveService.onSetBonus` 同袍 ×1.05；`CalamityService` 灾厄 ÷(1+0.15(n−1))；`QuestService.onInfight`（取消非玩家来源对 MM 怪的伤害）。
5. `MONITOR`：`GearPassiveService.onHit` 触发 T1/T2/T3 刃被动（另行造成伤害或回血）；任务/灾厄记账。
6. 事件结束后，引擎按 `setDamage` 后的基值**按比例重算**原版 ARMOR / RESISTANCE / MAGIC(保护附魔) / ABSORPTION / BLOCKING 修正，然后扣血。

技能和被动伤害走 `SkillService.dealInternal → le.damage(dmg, player)`：同样是 `ENTITY_ATTACK`，期间静态标志 `internalDamage=true`，并**清空目标无敌帧**（`setNoDamageTicks(0)`，结束后再恢复）。StatService 和刃被动会跳过它，**但第 4 步的三个 HIGH 乘区和 MM DamageModifiers 都不跳过**。

怪打玩家：NMS 先做**难度缩放**（EASY）→ `HIGH` `StatService.onDefend`（防御 × 承伤%，下限 0.2，PvP 不处理）→ 引擎再算原版护甲、抗性、吸收、格挡。非 ByEntity 伤害（凋零、中毒、着火、摔落、岩浆、溺水）**完全绕过** StatService。

回复：原版饱食/再生/瞬间治疗会触发 `EntityRegainHealthEvent`；而 CoreRpg 自己的回血（吸血、技能吸血、T2 暴击回血、同袍击杀回血、入本回满）全部直接调用 `p.setHealth(...)`，**不触发任何事件**，所以无法用一个统一的回复监听器拦下。

## 2. 来源开关表

列说明：**类别**用书中词；**处理**＝替换 / 禁用 / 保留为唯一入口；**挂点**＝新模式要加闸门的具体位置（`P1(p)` 表示 `EmberMode.isP1(player)`，见 §5）。

### A. 玩家造成伤害

| # | 来源 | 书中类别 | 实现位置 | 当前怎样结合（优先级） | P1 处理 | 理由 | 闸门挂点 |
|---|---|---|---|---|---|---|---|
| A01 | 原版武器攻击属性：T0 `IRON_SWORD` 6、T1–T3 `DIAMOND_SWORD` 7（NI `material`） | 原版基础攻击 | NMS `EntityHuman.attack`；同时被 `StatService.fullHitDamage()` 读 `GENERIC_ATTACK_DAMAGE` 作为技能和被动的基数 | 事件前计算，作为 `getDamage()` 基值；StatService 在它上面**再加**平坦伤害 | **替换** | 书 §7.1：A 是满蓄力基础一击本身，不能叠在钻石剑伤害上 | 新 `EmberCombatListener.onMelee(LOWEST，注册顺序先于 StatService)`：`P1(p)` 时 `e.setDamage(B×k蓄力)`，**覆盖**而不是累加；`StatService.fullHitDamage()` 在 P1 下直接返回 B |
| A02 | 锋利 `DAMAGE_ALL`：T3 刃出厂自带 I；附魔台各阶 I–III（`CoreEnchant/config.yml offer_tables`） | 原版附魔 | NMS（事件前）；`StatService.fullHitDamage` 另加 0.5L+0.5 | 计入事件前基值；技能和被动通过 fullHitDamage 再算一次 | **禁用** | §4.5 原版附魔不得绕过统一结算 | A01 的覆盖写法会自动去掉；新 NI 模板 `ember_v1_*` 不进 `CoreEnchant whitelist_ni_ids` |
| A03 | 火焰附加 `FIRE_ASPECT` I/II（各阶刃附魔表的第 3 档） | 原版附魔 | NMS 点燃 → 目标每秒 1 点 `FIRE_TICK`（非 ByEntity） | 独立的持续伤害，不受 StatService 和 MM `ENTITY_ATTACK` 修正影响 | **禁用** | 未登记的持续伤害，会和焚烬燃烧重叠 | `EmberCombatListener` 监听 `EntityCombustByEntityEvent`：攻击者 `P1` 时取消；新模板不给附魔 |
| A04 | 原版横扫 `ENTITY_SWEEP_ATTACK`（剑，地面、非疾跑、满蓄力） | 原版基础攻击（横扫） | NMS | 单独产生伤害事件；StatService 和刃被动不管它，但 HIGH 的同袍和灰印会乘它 | **禁用** | §4.4 横扫不计数；没有归属的来源不允许接入 | `EmberCombatListener`：`P1` 时 cause=`ENTITY_SWEEP_ATTACK` 直接取消 |
| A05 | 力量药水 `potion_ember_strength`（`INCREASE_DAMAGE:0:1200`，+3）／MM 技能给的虚弱（−4，`EmberDungeonDiffSkills`） | 药水 / 食物 buff | `LifeService.onConsume`(HIGH) 施加效果；属性修饰进入 NMS 基值 **以及** `fullHitDamage` | 力量同时作用于普攻、技能和被动 | **禁用** | §19.4 首版不卖增伤药 | `LifeService.onConsume`：`P1` 时按白名单拒绝并提示；A01 覆盖后属性修饰自然失效（虚弱如需保留，要作为怪物机制单独进入公式） |
| A06 | 旧装备 lore `物理伤害`：T0 8 / T1 14 / T2 22 / T3 30；团戒 +3（团戒不在 `accessories` 默认列表里，**当前实际不生效**） | 旧装备 lore | `StatService.itemStats()` 正则 `P_DMG`；`compute()` 只读主手且 id ∈ `stats.weapons`（未配置，使用代码默认的 4 把刃） | LOWEST **加法**：`getDamage() + flat×damage_scale×蓄力` | **替换** | 书 B 公式中 A 只来自阶级表，不来自 lore | `StatService.compute()`：`P1` 时返回空表；`onDamage()` 开头 `if P1 return` |
| A07 | 旧强化 `enhance.yml stat_per_level`：刃每级 +2/3/4/5 物伤 | 旧强化 | `StatService.itemStats()` × `GearLore.readEnhance()`（lore 标记 `#ember_en:`） | 合进平坦伤害（加法） | **替换** | 书 §6.1 改为 e% 表并带保底 | 同 A06；新强化状态只从 `ember_v1` NBT＋DB 读取 |
| A08 | 旧宝石 `gem_ember_sharp` +2 物伤（孔位 lore `#ember_sk:`） | 旧宝石 | `StatService.itemStats()` 遍历 sockets | 合进平坦伤害 | **禁用** | §4.1 宝石不叠加 | 同 A06；`EnhanceService.cmdSocketInsert` 拒绝 `ember_v1` 物品 |
| A09 | 重铸次要词缀（`scrap.yml reforge.secondary_pool`：暴击 1–3%、攻速 1–4%、生命 2–8、物防 1–3，`#ember_aff:`） | 其他（旧重铸词缀） | `ScrapService` 重铸写入 → `StatService.itemStats()` 读取 | 分别加到对应属性 | **禁用** | §5.2 不设自由词缀池 | `itemStats()` 的词缀循环在 `P1` 下跳过；`ScrapService` 重铸拒绝 `ember_v1` 物品 |
| A10 | 旧天赋 stats（`talent.yml`）：烬刃树物伤合计 +5、暴击 +2.5%、暴伤 +8%、吸血 0.5%；灰行树攻速合计 +8%、移速 +3%、`on_hit_slow_pct`（**没有代码读取，实际无效**）；守墓树防御 +2、承伤 −6%、生命 +6、护符加成 3% | 旧天赋 | `StatService.compute()` 遍历 `PlayerData.getTalentNodes()`；`SkillService.talentBonus()` 每个节点给技能 +0.1×（灰印 +2%），最多 3 个 | 加法并入总属性；`charm_stat_bonus_pct` 乘到"最佳护符" | **禁用**（保留记录） | §3.3 旧树不得同时运行独立攻击、暴击、攻速、回复 | `compute()` 天赋循环；`SkillService.talentBonus()`；均为 `if P1 return 0` |
| A11 | 誓约属性（`covenant.yml covenants.*.stats`）：烬刃暴击 +3%、技能吸血 4%；灰行攻速 +4%、移速 +2%；守墓承伤 −4%、护符 +5%。`config.yml stats.apply_covenant_stats: true`（代码注释写默认 false，实际代码默认值和配置都是 true） | 誓约 | `StatService.compute()` 的誓约段 | 加法；守墓护符加成另外加一次（与天赋护符加成是两条路径） | **禁用**（誓约保留为称号/外观） | §4.2 / §4.1 | `compute()`：`applyCovenantStats && !P1(p)` |
| A12 | 誓约主动·烬斩 `ember_blaze_slash`：1.5×满蓄力（每个天赋节点 +0.1，最多 1.8×，上限 `max_hit_mult` 2.5），100°、3.5 格、**最多 6 目标**，CD 8s | 誓约（主动） | `SkillService.castBlazeSlash` → `dealInternal` | 走 ENTITY_ATTACK，绕过无敌帧；会被同袍、灰印、灾厄、MM 修正再乘；之后还会触发技能吸血 | **保留为唯一入口**（改成全员共用的烬斩） | §4.2：1.5B、最多 5 目标、不暴击、不吸血、不触发套装、不再加原版武器伤害 | `SkillService.cast()`：`P1` 时不看誓约，走 `castEmberSlash(B)`；`max_targets=5`；关闭 `talentBonus` 和 `skillHeal`；伤害打上 `SKILL` 标记，HIGH 乘区跳过 |
| A13 | 其他誓约主动：灰印 0.5× 打击＋减速、守墓壁垒 0.5× 冲击波（最多 8 个目标）＋嘲讽＋抗性 II | 誓约（主动） | `SkillService.castAshMark` / `castWardenTaunt` | 同 A12 | **禁用** | §4.2 只有一项公开战技 | `SkillService.cast()` 的 P1 分支 |
| A14 | 灰印标记：被标记目标受到**所有玩家**的伤害（含投射物、技能、被动）×(1+0.20+0.02/节点) | 队友标记（"微尘使魔"节点） | `SkillService.onEntityDamageByEntity` HIGH | 乘法，6s | **禁用** | §7.3 不增加团体乘区；C12 | 该监听开头 `if P1(attacker) return`；P1 下不再生成新标记 |
| A15 | T1 刃·余烬引燃：蓄力≥0.7、15% 概率、ICD 6s；`setFireTicks(3s)`（**原版着火另算**）＋3 次 0.12×满蓄力的内部伤害 | 被动 | `GearPassiveService.onHit` MONITOR，按主手 NI id `gear_ember_t1_blade` 判定 | 额外伤害；受 HIGH 乘区和 MM 修正影响；原版着火不受 | **替换**为焚烬（每 3 次有效命中、4 跳、0.26/0.34/0.42B、最多 5 个目标、不叠原版着火） | §4.3/§4.4 | `onHit()` 开头 `if P1 return`；新 `SetRuntime`（G02） |
| A16 | T3 刃·烬爆：每 5 次蓄力≥0.7 的命中、ICD 2.5s、0.6×满蓄力、半径 3、最多 5 个目标 | 被动 | 同上，`gear_ember_t3_blade` | 同上 | **替换**为烬爆（0.65/0.80/0.95B，ICD 3s，按距离＋实体 ID 排序） | §4.4 | 同上 |
| A17 | 同袍共鸣 +5%：自己同袍套装激活（任一余烬刃＋团戒，**背包里持有即算**），24 格内至少 2 名同袍 | 同袍 set.yml（+`passives.yml set_comrade`） | `GearPassiveService.onSetBonus` HIGH；`SetService.isSetActive` 扫描整个背包 | 乘法；**不检查 internalDamage 和 cause**，所以技能、被动燃烧、烬爆、横扫都会被乘 | **禁用** | §1.3、§7.3、C01 | `onSetBonus()` 开头 `if P1 return`；`SetService.isSetActive()` 在 P1 下返回 false |
| A18 | 灾厄人数缩放：对灾厄 Boss 的伤害 ÷(1+0.15(n−1))，n 为最近的攻击者人数（战斗中会变） | 其他（人数缩放） | `CalamityService.onBossDamageScale` HIGH | 乘法 | **保留**在旧灾厄；Q01–Q07 **替换**为开局锁定的血量倍率 1+0.65(n−1) | §7.5：不在副本和怪物两层同时乘；中途掉线不先降血再升血 | 新 `RunSession` 开局按人数设定 MM 血量；断言 P1 实例世界里没有 calamity 实体 |
| A19 | MythicMobs `DamageModifiers`（EmberRaid/EmberGuildBoss `ENTITY_ATTACK 0.45`，EmberCalamity 1.0）以及怪物身上的原版护甲（如 `IRON_HELMET`） | MythicMobs DamageModifiers | MM 怪物 yml | 乘法，作用于普攻和内部伤害（同为 ENTITY_ATTACK），**不作用于 FIRE_TICK** | Q01–Q07：**禁用**（不配置 DamageModifiers、不穿护甲，首领强度只体现在 Health 上）；旧本保留 | §7.5：有效生命修正必须对技能、被动、普攻一致；`spigot.yml maxHealth.max` 已经是 20000，不再需要用 DM 绕过 2048 上限 | Q 系列 MM 配置加 lint（扫描 `DamageModifiers:`、`Equipment` 护甲）；伤害日志记录 MM 修正系数 |
| A20 | 原版弓/投射物 | 原版基础攻击 | NMS；StatService 只认 `damager instanceof Player`，箭矢不加成；灰印和灾厄会乘 | 未经统一结算 | **禁用** | 当前没有合法获取途径（`CoreCraft wipe_vanilla`、怪物掉落被禁），但也没有拦截 | `EmberCombatListener`：`P1` 时玩家射出的 Projectile 打怪一律取消 |
| A21 | 余烬等级 `emberLevel`（10–60） | 新增 | `PlayerData.getEmberLevel()`；**当前不提供任何战斗属性**，只用于进本门槛 `progress.yml level_gates` | — | **保留为唯一入口**（+0.2 攻/级，从 Lv10 起算） | §3.3 | `EmberFormula.B()` 读取；`level_gates` 在 P1 地图换成首通门（不叠成双门） |
| A22 | 使魔 `PetService`（灰灵/烬火） | 使魔 | 盔甲架跟随；`onDamage` 会取消对宠物的伤害；`power_bonus` 只进入天梯战力显示 | 没有战斗数值 | **保留**（不加战斗值） | §19.5 | 无需闸门；以后新增宠物功能时必须登记 |
| A23 | AttributePlus（已停用） | 其他（AttributePlus 属性） | `plugins/_parked/AttributePlus.jar`，配置目录仍在 | 现在为 0；一旦恢复，会再读取同一批 lore（双算），并带来自己的吸血和生命恢复 | **禁用**（保持停用） | 避免第二条属性管线 | `EmberMode.enable()` 检测 `isPluginEnabled("AttributePlus")`，为 true 时拒绝开启 P1 并报错 |

### B. 承伤 / 防御（怪 → 玩家）

| # | 来源 | 书中类别 | 实现位置 | 当前怎样结合（优先级） | P1 处理 | 理由 | 闸门挂点 |
|---|---|---|---|---|---|---|---|
| B01 | 自定义物防 `phys_defense`：护符 lore 3/6/10/14＋护符强化每级 1/2/3/4＋`gem_ember_steady` +2＋重铸物防＋副手（守腕 2、灰箍 1）＋守墓天赋 +2＋护符加成（天赋 3% 乘到"最佳护符"，誓约 5% 另加一次） | 护甲（自定义）/ 旧 lore / 旧强化 / 旧宝石 / 旧天赋 / 誓约 | `StatService.onDefend` HIGH；"最佳护符"＝**背包任意位置加副手**里按 `生命+3×防御` 自动挑分最高的一件 | `mult=(1−def/(def+20))×(1+damage_taken_pct)`，**下限 0.2**；只处理 ByEntity，PvP 跳过 | **替换**：M=max(0.5, 40/(40+D))，D 只取已选护符 | §7.2、§4.1（不扫描背包自动挑最强） | `onDefend()` 的 P1 分支 → `EmberFormula.M(loadout)`；在 HIGHEST 一次性完成，见 B03 |
| B02 | 承伤% `damage_taken_pct`：守墓誓约 −4%、天赋合计 −6%，下限 −25% | 誓约 / 旧天赋 | 同上 | 乘法 | **禁用** | §7.2 承伤只在一处计算 | `compute()` 的 P1 分支不产出该键 |
| B03 | 原版护甲/韧性：钓鱼垃圾 `junk_ember_boot`（`LEATHER_BOOTS`，**可以穿**，+1 护甲）以及任何遗留的原版护甲 | 护甲 | 引擎 `DamageModifier.ARMOR`，在插件改完伤害后按比例重算 | 乘法，在 StatService 之后 | **禁用** | §4.5；C13 | `EmberCombatListener.onPlayerHurt(HIGHEST)`：`P1` 受害者 `setDamage(ARMOR,0)` |
| B04 | 原版保护附魔：附魔台给护符 `PROTECTION_ENVIRONMENTAL` I–III | 原版附魔 | 引擎 MAGIC 修正，**只计算盔甲槽**；护符放不进盔甲槽，预计不生效（**需要实测确认**） | — | **禁用** | §4.5 | HIGHEST `setDamage(MAGIC,0)`；新模板不进附魔白名单 |
| B05 | 原版抗性 Resistance：守墓壁垒 II 3s；**入本/重连回满时给 V 级 5–7s（100% 免伤）**；挂机复活保护 V 级 | 抗性 | `SkillService.castWardenTaunt`；`QuestService.instanceGrace`（`PlayerChangedWorldEvent` 和 `PlayerJoinEvent` 都会触发）；`AfkTierService.protect` | 引擎 RESISTANCE 修正，乘在 StatService 之后 | **禁用**（P1 战斗内）；只保留首次入场的"开局保护"，并作为登记过的来源 | §7.2：0.5 下限不能被抗性压到 0.1；§20.5 重连不得刷新 | HIGHEST `setDamage(RESISTANCE,0)`（标记为 grace 的保护窗口除外）；`QuestService.onJoinInstance` 在 P1 下不再给 |
| B06 | 吸收生命 Absorption：图腾复活自带；金苹果（目前没有合法来源） | 抗性 / 饱食 | 引擎 ABSORPTION | 额外血量缓冲 | **禁用** | §19.4 不叠吸收盾 | HIGHEST `setDamage(ABSORPTION,0)` 并在 P1 中清除效果（1.12 没有 `EntityPotionEffectEvent`，只能在消耗/复活时清） |
| B07 | CoreCombat 盾牌 `shield_ember_guard`（原版格挡，正面 100%；耐久 ×1.5）。**目前没有获取途径**（只能管理员发放） | 其他（CoreCombat） | `CoreCombatPlugin` → `ShieldNmsHooks` 校验器（只拿到 ItemStack，拿不到玩家） | BLOCKING 修正 | **禁用**（P1 实例内） | 未登记的减伤 | HIGHEST `setDamage(BLOCKING,0)`；不改 CoreCombat |
| B08 | CoreCombat 图腾 `totem_ember_life`（复活：1 HP＋再生 II 45s＋吸收 II 5s，CD 15s）。目前没有获取途径 | 其他（CoreCombat） | `TotemNmsHooks`＋`onResurrect` | 免死 | **禁用**（P1 实例内） | §20.5 单人死亡即本局失败 | CoreRpg 新增 `EntityResurrectEvent` HIGH 监听，`P1` 时取消 |
| B09 | 难度 EASY（`server.properties difficulty=1`，MV 各世界 EASY） | 其他（难度） | NMS，在事件**之前**缩放怪物近战 | 先于所有插件 | **保留为唯一入口** | §7.2：普通难度修正只能在一处做一次 | 不改；P1 怪物伤害以"已含 EASY"为基准调参，并实测 MM `damage{}` 是否也被缩放 |
| B10 | MythicMobs 技能伤害 `damage{amount}`（含 `ignorearmor=true`）、各图光环 `damage{0.4–1}@PlayersInRadius ~onTimer` | MythicMobs | MM → ByEntity（damager＝施法怪） | 会经过 StatService 防御；`ignorearmor` 只跳过原版护甲 | **保留为唯一入口**（敌方伤害），但要统一乘 M | §7.2：特殊首领伤害必须写明走哪条管道 | P1 下 `onDefend` 统一处理；日志记录 MM 技能名 |
| B11 | 药水类持续伤害：MM 技能凋零 35–55 tick、凋零骷髅近战凋零（>150 tick 时被 `stats.strip_wither_on_hit` 剥掉）、`VanillaMobs.yml` 中毒 | 抗性 / 其他 | 引擎 `WITHER`/`POISON`，**非 ByEntity → 绕过 StatService** | 不经过防御 | **替换**：登记为"敌方持续伤害"，P1 也乘 M（或改写成 MM 伤害） | §7.2 不得有未登记的旁路 | 新 `EmberCombatListener.onPlayerEnv(EntityDamageEvent, HIGHEST)` 按 cause 登记表处理 |
| B12 | 环境伤害：摔落（MM `throw` 击飞后摔落）、岩浆、着火、溺水、窒息、虚空 | 其他（环境） | 引擎 | 不经过防御 | **保留为唯一入口**：按登记表走"环境管道"（默认原值，不乘装备 M） | §7.2 环境伤害要写明管道 | 同 B11，按 cause 分支，并记日志 |
| B13 | PvP（竞技场） | 其他 | `StatService.onDefend` 遇到玩家来源直接 return；`onDamage` 只处理打非玩家 | 原版数值 | **保留**（旧模式；P1 不覆盖竞技场） | 不在 P1 范围 | `EmberMode` 的作用域不包含 arena 世界 |

### C. 最大生命

| # | 来源 | 书中类别 | 实现位置 | 当前怎样结合 | P1 处理 | 理由 | 闸门挂点 |
|---|---|---|---|---|---|---|---|
| C01 | 角色基础 20 | 原版 | `StatService.refresh` `want = 20 + …` | 基数 | **保留为唯一入口** | H0=20+… | — |
| C02 | `max_health` 汇总：护符 lore 生命 20/35/55/80（自动挑"最佳护符"，背包/副手都算）＋护符强化每级 4/5/7/9＋重铸生命＋守墓天赋 +6＋护符加成（天赋 3% 乘法，誓约 5% 另加）；团戒 lore `生命力 +10` **不生效**（不在 accessories 列表中） | 旧 lore / 旧强化 / 旧天赋 / 誓约 | `StatService.refresh()` 每 20 tick 一次，外加 join/respawn/切换手持/副手点击时：`GENERIC_MAX_HEALTH.setBaseValue(20+floor(max_health×health_scale))` | 加法 | **替换**为 H0=20+h(1+e+q+f)+(L−10)；炽愈 ×1.12 | §7.2 | `refresh()`：`want = P1 ? EmberFormula.H(loadout,L) : 旧逻辑` |
| C03 | 副手白名单（`stats.offhand`）：`acc_ember_offhand_ward` 物防 +2、`acc_ember_offhand_vita` 生命 +8、`part_ember_ash_brace` 物防 +1，和护符**并行叠加** | 其他（副手） | `StatService.compute()` 中的 B-flex-1 段 | 加法 | **禁用** | §4.1 副手不是第二份护符 | `compute()` 的副手段在 P1 下跳过 |
| C04 | 心形显示上限 `hearts_display_cap: 40`（`setHealthScale`） | 其他（显示） | `StatService.refresh` | 只影响显示 | **保留** | 不影响数值 | — |

### D. 回复 / 回血

| # | 来源 | 书中类别 | 实现位置 | 当前怎样结合 | P1 处理 | 理由 | 闸门挂点 |
|---|---|---|---|---|---|---|---|
| D01 | 原版自然回复：饱食≥18 每 4s 回 1；饱食 20 且有饱和度时每 0.5s 回 1（1.11+ 规则） | 饱食回复 | 引擎（`EntityRegainHealthEvent` reason `SATIATED`）；入本时 `instanceGrace` 会把饱食设为 20、饱和度设为 10 → **一进本就是快速回血状态** | 持续回复 | **禁用**（P1 实例内） | §19.4：副本内原版自然回血关闭，并且按实例隔离 | 新 `EmberCombatListener.onRegain(LOWEST)`：`P1` 且 reason=`SATIATED` 时取消（不改世界 gamerule，避免忘记恢复） |
| D02 | 食物附带效果：`food_ember_grilled_fish` 再生 I 6s；面包/牛排只补饱食 | 食物 buff | `LifeService.onConsume`（`life.yml consumables`） | 再生 → `REGEN` | **禁用**效果（食物只负责饱食度） | §19.4 | `LifeService.onConsume`：`P1` 时按白名单处理；`onRegain` 取消 reason=`REGEN` |
| D03 | 回复药 `potion_ember_heal`：`HEAL:1` ＝ 瞬间治疗 II，**固定 8 HP**；按物品 id 单独计 CD 20s；CD 存在内存里（重启清零，重连不清） | 药水 | `LifeService.onConsume` | 瞬间回复（`MAGIC`） | **替换**为最大生命 20%、全部同类药共用 15s CD，CD 持久化（重连/重启都不清） | §19.4 | `LifeService.onConsume` 的 P1 分支 → `HealLedger.potion()`；CD 存进 PlayerData |
| D04 | 迅捷药 `potion_ember_swift`（速度 I 3 分钟）、夜视药 | 药水 | 同上 | — | 迅捷：**禁用**（待裁决，默认不进白名单）；夜视：**保留** | §19.4 旧药按白名单生效，未支持的要明确提示 | 同上 |
| D05 | 普攻吸血 `life_steal_pct`：宝石 `gem_ember_drain` 每颗 1%、天赋 `blaze_leech1` 0.5%，上限 5% | 旧宝石 / 旧天赋（旧吸血） | `StatService.onDamage` LOWEST，`p.setHealth(+dmg×ls)`；**dmg 是 MM、护甲修正之前的值** | 直接改血，不触发事件 | **禁用** | §4.5 旧吸血 | `onDamage()` 在 P1 下直接 return（同 A06） |
| D06 | 技能吸血 `skill_life_steal_pct`：烬刃誓约 4%（上限 15%），每次施放最多回最大生命的 `skill_heal_per_cast_pct` 3% | 誓约（旧吸血） | `StatService.skillHeal()`，由 `SkillService.cast` 调用 | setHealth | **禁用** | §4.2 烬斩不吸血 | `skillHeal()`：`if P1 return 0` |
| D07 | T2 刃·炽愈：每次暴击（系统暴击**或原版跳劈**）回 min(2, max(1, 4%最大生命))，ICD 3s | 被动 | `GearPassiveService.onHit` MONITOR，按 `gear_ember_t2_blade` 判定 | setHealth | **替换**为炽愈套（每 5 次有效命中、6s ICD、回 2.5/3.25/4% 最大生命） | §4.3/§4.4：不能依赖暴击 | 同 A15 |
| D08 | 同袍击杀回能：每次击杀给饱和 I 2s（→ 补饱食，叠加 D01）＋回 2% 最大生命（至少 0.2） | 同袍 set.yml | `CoreRpgPlugin.onDeath`(MONITOR) → `SetService.onKill` | setHealth＋药水 | **禁用** | §4.4："一击杀十只怪也不会回十次" | `SetService.onKill()`：`if P1 return` |
| D09 | DP 波次结束回血：`effect %player% instant_health 1 2`（瞬间治疗 III ＝ 16 HP），出现在 EmberWeekly/Raid/GuildBoss/EliteWeekly/DailyAsh/Tide/Rail/Crypt 等 `monster.yml` | 其他（副本脚本） | DungeonPlus `$command` | `MAGIC` | **禁用**（Q01–Q07 不写）；旧本保留 | §19.4 统一回复入口 | Q 系列 DP 配置 lint；`onRegain` 在 P1 下只放行 `HealLedger` 标记过的 MAGIC |
| D10 | MM 首领给玩家回血：EmberWeekly `heal{amount=6} @PlayersInRadius{r=20} ~onTimer:300` | MythicMobs | MM | 需实测它是 setHealth 还是 regain 事件 | **禁用**（新图不写） | 同上 | MM lint；实测确认 |
| D11 | 入本/重连恩典：进入实例世界或**在实例中重连**时，`setHealth(max)` 执行 4 次＋饱食 20＋饱和 10＋清除着火＋抗性 V | 其他 | `QuestService.instanceGrace` / `onJoinInstance` | setHealth | **替换**：仅首次进入时补满（城内补满是允许的）；重连恢复断线前保存的血量，不给抗性 V | §20.5：不能反复重连刷新回复 | `QuestService.onJoinInstance` / `instanceGrace` 在 P1 下改走 `RunSession` 逻辑 |
| D12 | 挂机复活保护、竞技场回满、图腾再生（见 B08） | 其他 | `AfkTierService.protect`、`ArenaService`、CoreCombat | — | **保留**（都不在 P1 作用域；图腾已在 B08 禁用） | 作用域外 | — |

### E. 暴击

| # | 来源 | 书中类别 | 实现位置 | 当前怎样结合 | P1 处理 | 理由 | 闸门挂点 |
|---|---|---|---|---|---|---|---|
| E01 | 系统暴击：蓄力>0.9 且随机数 < `crit_chance_pct`（上限 35%）→ **整击** ×(`crit_multiplier` 1.5 + `crit_damage_pct`) | 其他（旧暴击） | `StatService.onDamage` LOWEST | 乘法，乘的是"原版部分（可能已含跳劈）＋平坦伤害" | **替换**为固定 10% × 1.5，只结算一次 | §7.2 | 新 `EmberCombatListener.onMelee` 中统一处理；StatService 在 P1 下 return |
| E02 | 原版跳劈 ×1.5（下落中、不在地面、不疾跑、蓄力>0.9） | 跳劈 | NMS 事件前，只乘原版部分；StatService 还会把它记为 `lastCrit`，用来触发 T2 回血 | **会和 E01 连乘**：原版部分最高 ×2.25 | **禁用**（并入 E01） | §4.5 / C05：只保留一个最终暴击处理 | A01 用 `setDamage(B×k)` 覆盖后，跳劈自然被剥离；从 `getDamage()/攻击属性` 的比值反推是否跳劈，只用于日志 |
| E03 | 暴击率/暴伤属性来源：烬刃誓约 3%、天赋 2.5%＋暴伤 8%、重铸暴击 1–3% | 誓约 / 旧天赋 / 旧词缀 | `StatService.compute()` | 加法汇总 | **禁用** | §7.2 不开放暴击和暴伤词条 | 同 A09–A11 |

### F. 攻速 / 蓄力

| # | 来源 | 书中类别 | 实现位置 | 当前怎样结合 | P1 处理 | 理由 | 闸门挂点 |
|---|---|---|---|---|---|---|---|
| F01 | 原版剑攻速 1.6/s（铁剑、钻石剑一致） | 原版基础攻击 | 引擎 `GENERIC_ATTACK_SPEED` | 决定真实的蓄力冷却 | **保留为唯一入口** | 书中有效普攻频率上限正好是 1.6/s | 新模板一律用剑类 material |
| F02 | 攻速修饰 `ember_attack_speed`（`MULTIPLY_SCALAR_1`，上限 +10%）：灰行誓约 4%、天赋 8%、重铸攻速 1–4% | 旧天赋 / 誓约 / 旧词缀 | `StatService.refresh()`（UUID `6e6d6265-…0001`） | 乘到攻速属性，最高 1.76/s | **禁用**（P1 下移除该修饰） | §5.1、§7.2 不开放攻速 | `refresh()`：`P1` 时 `asp=0` 并移除修饰 |
| F03 | 蓄力近似：按同一玩家两次命中的间隔推算 `charge=0.2+0.8t²`（625ms/(1+攻速)），首击记 1.0；没有装备属性的玩家固定记 1.0 | 其他 | `StatService.onDamage` → `lastSwing`，被动用 `lastCharge` 判断（阈值 0.7） | 平坦伤害、暴击、被动都用它 | **替换**为真实蓄力：用"事件伤害 ÷ 当前攻击属性"反推 c（NMS 中 c>0.9 才会出现跳劈，可以消除歧义）；更稳的方案是在自研 Paper 中加一个记录 `EntityHuman.attack` 中 `f2` 的 hook | §4.4：蓄力 ≥0.9 要真实校验，不能看发包间隔 | `EmberCombatListener.onMelee` 计算 `k`；`SetRuntime` 只接受 k≥0.9 |

### G. 其他

| # | 来源 | 书中类别 | 实现位置 | P1 处理 | 理由 / 挂点 |
|---|---|---|---|---|---|
| G01 | 移动速度 `move_speed_pct`：宝石 `gem_ember_gale` 2%/颗、灰行誓约 2%、天赋 3%，上限 8% → `setWalkSpeed` | 旧宝石 / 誓约 / 旧天赋 | `StatService.refresh()` | **禁用** | §4.1 旧属性不额外叠加；P1 下 `ws=0.2` |
| G02 | 公会 | 公会 buff | `GuildService`：只有创建、捐献、公会 Boss 入场，**没有战斗加成** | **保留**（无战斗值） | §19.5；以后加 buff 必须登记 |
| G03 | 称号 / VIP | 称号 | `CoreRpgExpansion vip_title` 只是展示；VIP 只加余烬经验% | **保留**（无战斗值） | §19.5 |
| G04 | 战力 / 天梯分（含使魔 power_bonus） | 其他（显示） | `LadderService` | **保留**；P1 角色以后改为显示 B/H/套装 | §19.2 不给"绝对最强评分" |
| G05 | 余烬踏步 `flex_ember_step`：14s、5 格、无伤害、不穿墙 | 其他（踏步） | `FlexSkillService` | **保留为唯一入口** | 已经和书 §4.2 一致；但 `onQuit` 会清 CD，P1 需要持久化 |
| G06 | 怪物互伤取消（非玩家来源打 MM 怪会被取消）；副本 MM 怪窒息取消；挂机世界窒息抬升 | 其他 | `QuestService.onInfight` / `onInstanceMobSuffocate`、`AfkTierService.onSuffocate` | **保留** | 防刷，不是玩家来源；P1 爆炸/燃烧必须以玩家为 damager 发出，否则会被 onInfight 取消 |
| G07 | 物品身份：`NiBridge.matchesNiId` 在 NI id 匹配不上时，会退回到**"lore 里含有这个 id 字符串"**的匹配；强化、孔位、词缀全部存放在 lore 标记里 | 其他（数据可信度） | `NiBridge`、`GearLore` | **替换**：P1 只认 NBT `ember_v1`＋DB 行 | §4.1、§20.3：显示行不能作为身份依据 |

**合计：65 条来源**（A23＋B13＋C4＋D12＋E3＋F3＋G7）。按处理方式：替换 15 条、禁用 34 条、保留为唯一入口 8 条、保留（不在 P1 作用域或没有战斗值）8 条。A18 灾厄按"Q 系列替换"计入替换；D04 按迅捷计入禁用。

## 3. 最容易和新公式重复计算的 5 个来源

1. **原版一击（A01/A02/A05/E02）**：`getDamage()` 里已经包含剑 6/7、锋利、力量、跳劈 ×1.5，而 `fullHitDamage()` 还会把攻击属性和锋利再读一遍。如果新模式沿用旧的"在 getDamage 上加数值"的写法，B 会叠在钻石剑上（书 §4.5 明令禁止），跳劈也会和新暴击连乘。**必须用 setDamage 覆盖，不能累加。**
2. **StatService 整条旧属性管线（A06–A11、B01/B02、C02/C03、E01、F02）**：LOWEST 加平坦伤害和暴击、HIGH 防御（下限 0.2）、每秒重设最大生命和攻速修饰。只要有一个 handler 没加闸门，就会和新的 B/H/D 同时生效；而且 `refresh()` 每 20 tick 会把新模式设好的最大生命改回旧值。
3. **HIGH 层乘区（A14 灰印、A17 同袍、A18 灾厄、A19 MM DamageModifiers）**：它们不区分普攻、技能和被动（`internalDamage` 只有 StatService 和刃被动检查），所以新的焚烬燃烧、烬爆、烬斩都会被再乘一次。这正是书 §7.3 所说的"套装总增伤／团体共鸣"乘区。
4. **旧刃被动按 NI id 判定（A15/A16/D07，以及 A03 原版着火）**：如果新模板沿用 `gear_ember_t*_blade` 这些 id，或者迁移时直接改写旧物品，旧的引燃、炽愈、烬爆会和新 SetRuntime **同时触发**；旧引燃还会额外叠加原版着火伤害。
5. **绕过事件的回血（D05/D06/D07/D08/D11）加上原版回复（D01/D02/D09）**：吸血按减免前的伤害计算；入本和重连都回满；击杀给饱和度会直接拉起原版快速回血。这些都和炽愈、回复药叠加，而 setHealth 不触发 `EntityRegainHealthEvent`，单一监听拦不住，只能逐个加闸门，或者统一改走 `HealLedger`。

次一级风险：B05 抗性 V 和 B01 防御下限 0.2 都会打破 M≥0.5 的下限；A12 烬斩目前最多 6 个目标，还会吃天赋加成。

## 4. 与策划书冲突、需要裁决或需要知道的点

1. **强化规则完全不同**：现在是 +2 90%、+3 80%……+4 起失败降 1 级、+10 失败直接掉到 +8，并有保护券和稳固符；数值是每级固定加值（刃 +2–5）。书中是 +1～+3 必成、失败不降级、按次数保底、累计 e% 加成。另外现有付费/产出道具（保护券、稳固符）需要列入迁移清单（§19.5、§21）。
2. **升阶会减半强化**：`forge.yml keep_enhance: 0.5`，书中明确写了"不把 +8 砍成 +4"；现在升阶也不要求首通 Q04/Q07。
3. **"阶级＝被动"与"家族＝被动"不一致**：现在 T1 引燃、T2 炽愈、T3 烬爆是跟着阶级走的；书中改为每个阶级都有三个家族。现有 8 个模板（`gear_ember_blade/charm`、`t1–t3_blade/talisman`）没有家族字段，需要新建 20 个 `ember_v1_*` 模板（书 §5.4），旧物品走 §21 迁移。触发方式也不同：现在是概率和暴击触发，书中是固定计数触发。
4. **护符选择方式**：现在自动挑背包里分数最高的一件（副手也算），书中要求玩家明确选择、不自动挑最强；书中副手不生效，而现在 B-flex-1 的副手饰品和灰箍是并行叠加的，这三个物品可以从商店（每周）和烬砧买到或做出来。
5. **套装识别方式**：现在同袍套是"背包里持有刃＋团戒"（书 §2.1 的描述准确）；P1 需要改成只看主手＋已选护符。团戒的 lore 属性目前本来就不生效。
6. **主动技能**：现在由誓约决定（三选一），烬斩最多 6 个目标、可以吃天赋到 1.8×、会吸血、会被同袍和灰印乘；**`SkillService.onQuit` 和 `FlexSkillService.onQuit` 下线就清 CD**，与书 §4.4"不能通过切换或重连清零"冲突。
7. **重连回满加抗性 V**（`QuestService.onJoinInstance`）与书 §20.5 冲突；入本时把饱食和饱和度拉满，也和"副本内原版自然回血关闭"冲突。
8. **防御公式和下限**：现在是 20/(20+def)，下限 0.2，后面还会再乘原版抗性和护甲；书中是 40/(40+D)，下限 0.5，并且只在一处结算。
9. **暴击**：现在暴击率最高 35%，有暴伤加成，还会和跳劈连乘；书中固定 10%×1.5，并且不再开放暴击、暴伤、攻速词条，而重铸词缀池、天赋、誓约里目前都有这些。
10. **等级**：现在等级不提供战斗属性，但有进本等级门（日本 10、周本 20……精英 40）。书中改为等级给少量属性、开图看首通，要求在新模式里明确替换，不能变成双重门槛。
11. **回复药**：现在固定回 8 HP，每种药单独计 CD 20s，CD 不持久化；书中是 20% 最大生命、所有同类药共用 15s CD、持久化。
12. **人数缩放**：灾厄是动态"最近攻击者"÷伤害；书中要求开局锁定人数并乘敌人血量。灾厄不属于 P1 主线，但新的七张图不能照搬这种做法。
13. **蓄力判定**：1.12 Bukkit API 拿不到真实攻击冷却，现在用命中间隔估算（被动阈值 0.7）；书中要求真实蓄力 ≥0.9。需要用伤害比值反推，或者在自研 Paper 里加 hook（`Paper/` 里已有 Totem/Shield 等 NMS hook 的先例）。
14. **技能和被动伤害伪装成普攻**：内部伤害同样是 `ENTITY_ATTACK`，只靠一个静态布尔值区分，第三方插件（MM、灾厄、同袍）看不出来；书 §20.6 要求持续伤害和爆炸"不伪装成另一记普攻"，需要显式的伤害标记和 root_id。
15. **物品身份**：现在完全依赖 NI id 加 lore 标记，`matchesNiId` 甚至接受"lore 里含有 id 字符串"的物品；书 §20.3 要求 item_uid 加服务端可信存储。
16. **书中可能已过时的一处**：策划书和 `docs/critic-fixes-20260927.md` 的前提是"引擎血量上限 2048，所以要用 DamageModifiers 补有效生命"，但当前 `spigot.yml attribute.maxHealth.max` 已经是 20000，P1 首领可以直接设血量，不必再用 DM。

## 5. 实测计划（只写方案，本次未执行）

前提：游戏服 127.0.0.1:25567 由另一位 worker 在跑，**不重启、不 reload**；下面带 ★ 的步骤需要新 jar 才能做（要重启），必须和对方约好窗口再进行。

### 5.1 需要先加的日志（下一个提交，默认关闭）

- `DamageTrace`（CoreRpg，开关 `ember-v1.yml debug.damage_trace` ＋ 命令 `/corerpg dmgtrace <玩家|off>`）：同时在 `LOWEST`（比 StatService 先注册）、`LOW`、`NORMAL`、`HIGH`（最后注册）、`HIGHEST`、`MONITOR` 注册 `EntityDamageEvent`，每一层输出：`root_id`（服务端生成，格式为玩家＋tick＋挥击序号）、cause、damager 类型、`internalDamage` 标记以及新的 `DamageTag`（MELEE/SKILL/BURN/BURST/ENV/MOB_SKILL）、该层的 `getDamage()`、5 个 `DamageModifier` 的值、`getFinalDamage()`、MM 怪 id、受害者扣血前后的血量（扣血后的值在下一 tick 读取）。由相邻两层的差值即可定位每个插件的贡献（例如 LOWEST→NORMAL 的差值就是 MM DamageModifiers）。
- `HealLedger`：把 CoreRpg 里所有 `setHealth(+x)` 的回血路径（吸血、技能吸血、T2 暴击回血、同袍击杀回血、入本恩典）都改成调用 `HealLedger.heal(p, amt, src)`，统一写日志；另外监听 `EntityRegainHealthEvent`，记录 reason 和 amount（MONITOR）。
- `StatTrace`：`StatService.refresh()` 中最大生命、攻速修饰、步速发生变化时输出一行，包含来源分解（lore、强化、宝石、词缀、天赋、誓约、副手）。
- 现有可用工具：`/corerpg stats <玩家>`（打印 raw 属性表）、`passives.yml debug: true`（需要 `corerpg reload`，会影响对方正在进行的测试，必须先约）。

### 5.2 测试台

- 一只专用的 MM 木桩 `EmberP1Dummy`（2000 血、无 AI、不配置 DamageModifiers、不穿护甲，放在空旷的测试世界）。另做一只带 `ENTITY_ATTACK 0.45` 的变体，用来对比 MM 修正发生在哪个优先级。需要 `mm reload`，同样要先约。
- 机器人：`mineflayer-tests/lib/proxy-login.js joinPlay()`；发装备用 `scripts/console.sh play "ni give <bot> <id> 1"`、`effect`、`corerpg enhance set` 等控制台命令。
- 每条来源测 3 组：只有该来源 / 该来源＋其他所有来源 / P1 开启后（★）。

### 5.3 逐条验证

| 来源 | 机器人动作 | 期望看到 |
|---|---|---|
| A01/F01/F03 | 拿无附魔 T1 刃，分别以 1.0s 间隔和 0.2s 连点打木桩 | LOWEST 原值 7×(0.2+0.8c²)；StatService 平坦伤害 14×近似蓄力；对比两者推算的 c |
| A02/A03 | 发放锋利 III 和火焰附加 II 的刃 | 原值多出 2.0×c；出现 FIRE_TICK 事件，且 cause 不是 ENTITY_ATTACK |
| E02/E01 | 跳起后在下落阶段（`velocity.y<0`）攻击；同时给机器人设暴击率（`/corerpg covenant set blaze`＋天赋） | 原值 ×1.5；StatService 暴击再 ×1.5（连乘的证据） |
| A04 | 两只木桩相邻，站在地面、不疾跑、满蓄力攻击 | 第二只木桩出现 `ENTITY_SWEEP_ATTACK`，HIGH 层被同袍乘 1.05 |
| A05 | 喝 `potion_ember_strength` | 原值 +3；`/corerpg skill info` 显示的满蓄力也 +3 |
| A06–A11/E03/F02/G01 | 用 `corerpg stats` 对比装备前后、强化 +0/+5、镶孔、重铸、选誓约和天赋 | raw 表逐项变化与本表一致；攻速修饰 ≤0.10 |
| A12–A14 | 三个誓约分别放技能；灰印期间另一个机器人攻击 | 内部伤害在 HIGH 被灰印和同袍乘；两个机器人都吃到灰印 |
| A15–A17/D07 | 分别手持 T1/T2/T3 刃攻击 30 次；两个机器人同时带同袍套站在 24 格内 | 被动日志的触发频率；同袍 ×1.05 也作用于 BURN/BURST |
| A19 | 分别攻击普通木桩和 0.45 变体 | 找出 MM 修正生效的优先级层 |
| A20 | （如果能拿到弓）射击 | StatService 不加成，灰印会乘 |
| B01–B05 | 木桩改成会攻击的版本；机器人穿 `junk_ember_boot`、戴护符、放守墓技能；重连进副本 | onDefend 系数；ARMOR/RESISTANCE 修正；重连后 7s 内伤害为 0 |
| B07/B08 | 管理员发盾和图腾，格挡并受到致死伤害 | BLOCKING 修正；resurrect 事件 |
| B09/B10 | 让 MM `damage{amount=4}` 技能和近战分别命中玩家 | 对比 EASY 是否对两者都生效 |
| B11/B12 | 被凋零命中；被 `throw` 击飞后摔落 | WITHER/FALL 事件没有经过 onDefend |
| D01–D11 | 进入副本后保持满饱食站立；吃烤鱼；喝回复药；击杀小怪；打完 DP 一波；在副本里重连 | RegainHealth 的 reason 和数值；HealLedger 记下的 setHealth 路径（重连 4 次回满） |
| G07 | 拿一件 lore 含 `acc_ember_raid_ring` 字样、但不是 NI 的物品放进背包（管理员制作） | 同袍被判定为激活（证明 lore 兜底匹配） |

★ P1 开启后，用同一套脚本回归书 §22.2 的 C01–C16：总伤害 = B×k×暴击，承伤 = 原值×M（M≥0.5），回复只有 HealLedger 白名单里的来源。凡是不在登记表里却产生了伤害或回复的，都必须在日志里打出 `UNREGISTERED`。

## 6. 新模式 `ember-v1.0-P1` 最小实现方案

### 6.1 开关和作用域

- 新增配置文件 `plugins/CoreRpg/ember-v1.yml`（同时打进 jar 的 resources；**不放进 config.yml**，因为 config.yml 里有本地密码、不入库）：
  ```yaml
  mode: ember-v1.0-P1
  enabled: false            # 总开关
  balance_version: 1
  data_version: 1
  scope:
    worlds: [ember_p1_lab]  # 测试场
    world_prefixes: []      # Q01–Q07 的 DP 实例世界前缀，地图接入后再填
    test_players: []        # 可选：只对测试角色开放
  require_mysql: true
  debug: { damage_trace: false }
  tiers:   { 0: {A: 12, h: 20, D: 2}, 1: {A: 24, h: 50, D: 6}, 2: {A: 42, h: 95, D: 10}, 3: {A: 70, h: 165, D: 14} }
  quality: [0, 0.04, 0.08, 0.12]
  craft:   [0, 0.02, 0.04, 0.06]
  enhance: { 1: [1.00, 1, 0.04], 2: [1.00, 1, 0.08], …, 10: [0.15, 12, 0.60] }  # 成功率, 最多尝试次数, 累计 e
  crit: { chance: 0.10, mult: 1.5 }
  mitigation: { k: 40, floor: 0.5 }
  ```
- `EmberMode.isP1(Player)` 的判定：`enabled` 为真，并且玩家所在世界命中 scope，并且（`test_players` 为空或包含该玩家）。旧世界保持旧规则，同一局不会混用两套规则（书 §20.1）。开启时检查以下三项，任一不满足就拒绝开启并写 SEVERE：AttributePlus 已加载、`storage != mysql`、`ember-v1.yml` 校验失败。

### 6.2 要动的类

| 新增 | 作用 |
|---|---|
| `EmberMode` | 开关、作用域、版本号、开启前检查 |
| `EmberFormula` | 纯函数 B、H0、H、D、M、暴击，不依赖 Bukkit；带 `selfTest()`，用书 §7.4 的两组参照数值做断言（B=58.6/H=163.5/EHP=204.375；B=134.6/H=363.7） |
| `EmberItemData` | 读写和校验 NBT `ember_v1`（使用 NI 内置的 `bot.inker.bukkit.nbt`，或 NMS 反射）；非法值（未知族、NaN、越界）拒绝生效并记录日志 |
| `EmberItemStore` | MySQL DAO（见 6.3），异步读写，带事务和乐观锁 |
| `EmberLoadoutService` | 主手 uid＋已选护符 uid → `active_set`、`awakening`；`/corerpg charm select`；缓存放内存，战斗时只读缓存 |
| `EmberCombatListener` | LOWEST 覆盖普攻伤害（B×k、10%×1.5）；取消横扫、玩家投射物、火焰附加点燃；HIGHEST 把玩家受伤时的 ARMOR/MAGIC/RESISTANCE/ABSORPTION/BLOCKING 置 0 再乘 M；非 ByEntity 伤害按登记表处理；RegainHealth 闸门；在 P1 中取消 Resurrect |
| `DamageLedger` / `HealLedger` / `DamageTag` | root_id、伤害标记、追踪日志、统一回血入口 |
| `SetRuntime`（G02，后续提交） | 焚烬、烬爆、炽愈的计数、tick、冷却、根事件去重 |

| 修改 | 闸门 |
|---|---|
| `StatService` | `compute`/`onDamage`/`onDefend`/`refresh`/`fullHitDamage`/`skillHeal` 在 P1 下分支或 return |
| `SkillService` | `cast` 的 P1 分支只放共用烬斩；`onEntityDamageByEntity` 灰印闸门；CD 存入 PlayerData（单调时钟加剩余时长） |
| `FlexSkillService` | CD 持久化 |
| `GearPassiveService` | `onHit`、`onSetBonus` 闸门 |
| `SetService` | `isSetActive`、`onKill` 闸门 |
| `LifeService` | 消耗品白名单；P1 回复药（20%、共用 15s、持久化） |
| `QuestService` | `instanceGrace`/`onJoinInstance` 的 P1 分支 |
| `EnhanceService`/`ForgeService`/`ScrapService` | 拒绝处理 `ember_v1` 物品（新的强化和升阶在 G03 另写） |
| `CoreRpgPlugin` | 注册和 reload 新服务；`onDeath` 里同袍击杀的闸门 |
| 配置 | 新增 20 个 NI 模板 `ember_v1_{family}_{slot}_t{tier}`（剑类 material、无附魔、lore 只做展示）；**不加入** CoreEnchant/enhance 的白名单 |

CoreCombat、CoreEnchant 的代码不改，闸门都放在 CoreRpg 一侧。

### 6.3 数据模型（NBT 只是缓存，以 MySQL 为准）

物品 NBT 复合标签 `ember_v1`：
```
uid:   String  (UUIDv4，服务端生成)
ni:    String  (冗余，用来和 NI id 交叉校验)
fam:   String  none|scorch|burst|sustain   (T0 固定为 none)
slot:  String  blade|charm
tier:  Byte    0..3
q:     Byte    0..3      (标准/精良/卓越/极品)
craft: Byte    0..3      (0/2/4/6%)
enh:   Byte    0..10
pity:  Short   当前目标等级已失败的次数
bound: Byte    0|1
src:   String  drop|first_clear|mark_exchange|forge|admin|test|migrate
ver:   Short   data_version
rev:   Int     与 DB 行版本一致
sig:   String  HMAC-SHA256(上述字段)，密钥放在 secrets/，不入库、不打印
```
MySQL（加到 `CoreRpg/sql/schema.sql` 和 `MysqlStorage`，`CREATE TABLE IF NOT EXISTS`）：
```sql
CREATE TABLE IF NOT EXISTS cr_item_instance (
  item_uid CHAR(36) PRIMARY KEY,
  owner_uuid CHAR(36) NOT NULL,
  ni_id VARCHAR(64) NOT NULL,
  family VARCHAR(8) NOT NULL, slot VARCHAR(8) NOT NULL,
  tier TINYINT NOT NULL, quality TINYINT NOT NULL, craft TINYINT NOT NULL,
  enhance TINYINT NOT NULL, pity SMALLINT NOT NULL DEFAULT 0,
  bound TINYINT(1) NOT NULL DEFAULT 1, source VARCHAR(16) NOT NULL,
  state VARCHAR(12) NOT NULL DEFAULT 'active',   -- active|consumed|migrated|quarantine
  data_version SMALLINT NOT NULL, balance_version SMALLINT NOT NULL,
  rev INT NOT NULL DEFAULT 0,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_owner (owner_uuid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS cr_loadout (
  uuid CHAR(36) PRIMARY KEY,
  blade_uid CHAR(36) NULL, charm_uid CHAR(36) NULL,
  active_set VARCHAR(8) NOT NULL DEFAULT 'none', awakening TINYINT NOT NULL DEFAULT 0,
  rules_version VARCHAR(24) NOT NULL DEFAULT 'ember-v1.0-P1', rev INT NOT NULL DEFAULT 0,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS cr_item_txn (           -- 幂等键＋审计（强化/互换/升阶/精工/成色/分解/发放）
  txn_id CHAR(36) PRIMARY KEY,
  kind VARCHAR(12) NOT NULL, owner_uuid CHAR(36) NOT NULL,
  uid_a CHAR(36) NOT NULL, uid_b CHAR(36) NULL,
  before_json TEXT NOT NULL, after_json TEXT NOT NULL, cost_json TEXT NULL,
  result VARCHAR(12) NOT NULL, created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  KEY idx_uid_a (uid_a), KEY idx_owner (owner_uuid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```
规则：
- 读取时，NBT 的 `uid`、`rev`、`sig` 必须和 DB 行一致，否则该物品战斗贡献为 0，标记 `quarantine` 并写日志（书 §20.6）。
- 每次写操作先写 `cr_item_txn` 和 `cr_item_instance`（同一事务，`WHERE rev=?`），成功后再改 NBT；如果中途掉线，以 DB 为准，下次登录时重写 NBT。
- 强化轨道互换在同一事务里锁定两行（`SELECT … FOR UPDATE`），重复的 `txn_id` 直接返回原结果（书 §6.3、E08）。
- MySQL 5.x 不执行 CHECK 约束，枚举和范围由 `EmberItemData.validate()` 校验。
- 现有的 `storage: yaml` 模式不支持 P1（开启时拒绝）。

### 6.4 提交顺序（每一步都可以单独回滚；默认 `enabled: false`，不改变线上行为）

1. `docs:` 本表（就是本提交）。
2. `CoreRpg: EmberMode + ember-v1.yml + DamageTrace/HealLedger（只加日志）`：所有 `setHealth` 回血改走 HealLedger，行为不变 → 可以先在旧模式下按 §5 采集基线。★部署。
3. `CoreRpg: EmberFormula + selfTest`（纯函数，不接线，只在启动时跑断言）。
4. `CoreRpg: P1 闸门`：在 StatService/SkillService/GearPassiveService/SetService/LifeService/QuestService/FlexSkillService 中加 `if P1` 分支，加入 EmberCombatListener 的原版旁路处理（横扫、火焰附加、护甲、抗性、吸收、格挡、自然回复、图腾、投射物）。开关关闭时与现网完全一致。
5. `CoreRpg: ember_v1 物品数据`：EmberItemData、schema、DAO、`/corerpg v1 give <fam> <slot> <tier> [q] [craft] [enh]`（`src=test`），以及 20 个 NI 模板。
6. `CoreRpg: Loadout + B/H/D 接线`：护符选择、`refresh` 的 P1 分支、普攻覆盖、M、10%×1.5 暴击、等级加成。★在测试世界开启 P1，跑 C01、C04、C05、C13、C14、C16。
7. `CoreRpg: 共用烬斩 + CD 持久化`（C06 中"主动不计数"的部分）。
8. 之后进入 G02 `SetRuntime`（C02、C03、C06–C11、C15），再做 G03 强化保底和互换、G04 统一结算——都不在本准备阶段内。

每次推送前先 `git pull --rebase`，只 add 自己的文件；★步骤需要先和运行实服的 worker 约好重启窗口。

## 7. G01 实现状态（CoreRpg 1.16.0，2026-10-01，未部署）

开关默认关闭（`plugins/CoreRpg/ember-v1.yml enabled: false`，`scope.worlds/world_prefixes` 为空）。所有闸门都是 `EmberMode.isP1(实体)`：总开关关闭、或实体不在 P1 世界时直接返回，旧模式路径不变。代码在 `CoreRpg/src/main/java/town/sunshine/corerpg/p1/`。

与 §6 计划的差异：
- 表名用 `cr_p1_item`、`cr_p1_loadout`（都是新表，`CREATE TABLE IF NOT EXISTS`，P1 开启或管理员发放时才建）；`cr_item_txn` 留给 G03（强化/互换事务）。
- 校验不一致时，物品只是"不计入战斗"并在 `/corerpg p1 status` 中写原因，还不会把 DB 行改成 `quarantine`（G03 再做）。
- YAML 存储下不拒绝开启，退化为"只校验 HMAC 签名的 NBT"（线上是 MySQL）。
- 提交顺序合并成 6 步，见提交记录。

| ID | 状态 | 位置 |
|---|---|---|
| A01 A02 E01 E02 F03 | ✅ 替换 | `EmberCombatListener.onMelee`（LOWEST，`setDamage` 覆盖，按伤害比反推蓄力，跳劈剥离，i-frame 只计超出部分） |
| A03 A04 A20 | ✅ 禁用 | `onCombust` / `onMelee` 取消 |
| A05 D02 D03 D04 | ✅ | `LifeService.consumeP1`：回复药改为 20% H，所有回复药共用 15s CD（重连不清；MySQL 下重启也不清）；夜视在白名单中；其余药剂提示后拒绝；食物只补饱食度 |
| A06–A11 B02 C03 D05 E03 G01 | ✅ 禁用 | `StatService.stats()` 对 P1 玩家返回空表；`onDamage` 在 P1 下 return |
| A12 A13 | ✅ | `SkillService.castEmberSlashP1`：1.5B、8s、100°、3.5 格、最多 5 个目标、不暴击/吸血/天赋；CD 存 `EmberPlayerState` |
| A14 A17 A18 D08 | ✅ 禁用 | 灰印、同袍、灾厄人数缩放、`SetService.isSetActive` |
| A15 A16 D07 | ✅ 替换 | 旧被动在 `GearPassiveService.onHit` 禁用；新的焚烬/烬爆/炽愈见 §8 G02 |
| A19 D09 D10 | ⏳ 配置 lint | 代码侧：回复事件全部取消，MM 的 BASE 改动会在 trace 里显示为"未登记来源"；Q 系列 MM/DP 配置 lint 还没写 |
| A21 C01 C02 F01 F02 | ✅ | `EmberFormula` 等级加成；`StatService.refreshP1`：最大生命设为 H，移除攻速修饰，移速固定 0.2 |
| A23 | ✅ | AttributePlus 启用时 `EmberMode` 阻止开启 |
| B01 B03 B04 B05 B06 B07 B10 B11 B12 | ✅ | `onFinal`（HIGHEST）：敌方伤害和凋零/中毒/魔法/CUSTOM 乘一次 M，环境伤害按原值；护甲/抗性/保护/吸收/格挡/头盔修正全部清零；入场改为 5s 登记保护窗口；金苹果拒绝；吸收和生命提升效果清除 |
| B08 | ✅ | `onResurrect` 取消 |
| B09 | 保留 | 需实测 MM `damage{}` 是否受 EASY 缩放 |
| B13 | ✅ | P1 世界内 PvP 取消 |
| D01 | ✅ | `onRegain`：P1 玩家的所有回复事件都取消；P1 只通过 `EmberHeal`（setHealth＋trace）回血 |
| D06 | ✅ | `skillHeal()` 返回 0 |
| D11 | ✅ | `QuestService.p1Entry/p1Reconnect`：首次进入补满一次；重连恢复断线前的生命，不给抗性 |
| G05 | ⏳ | 踏步 CD 仍会在退出时清空（FlexSkillService），尚未持久化 |
| G07 | ✅ | P1 只认签名的 `ember_v1` NBT＋NI ID＋uid 唯一＋DB 行（owner/rev/state） |

其他还没做的：G03 强化保底/互换/升阶事务；RunSession（A18 开局锁定血量，以及 D11 的按局记录）；`/corerpg stats` 在 P1 下显示 B/H；NI 模板 `ember-v1-gear.yml` 需要重启或 `ni reload` 才会加载。

## 8. G02 SetRuntime 实现状态（CoreRpg 1.17.0，未部署）

- 纯逻辑：`EmberSetRules`（系数表）、`EmberBurnBook`（燃烧）、`EmberSetEngine`（计数、内置冷却、根事件去重、爆炸选目标、HUD 文本）。单测：`EmberSetEngineTest`、`EmberBurnBookTest`、`EmberSetEnvelopeTest`、`EmberLoadoutTest`（C02/C03）。
- Bukkit：`EmberSetService`。MONITOR `EntityDamageByEntityEvent`（不忽略已取消事件）里按 `EmberCombatListener.takeSwing(e)` 取同一事件的 root id/c；`SkillService.internalDamage` 时按 `EmberCombatListener.internalKind` 分为 SKILL/BURN/EXPLOSION，都不计数。
- 有效命中：直接主目标近战、c≥0.9（估算不可靠时视为无效）、最终伤害 >0、未取消、root id 只计一次、存活敌对（Monster/Slime/Ghast/Shulker/末影龙或 MythicMobs 怪；驯服生物、盔甲架、玩家除外）、非无敌。
- 焚烬：每 3 次有效命中点燃主目标，4 跳 × 1s，每跳 = 觉醒系数 × 点燃时的 B；同一目标续燃只延长结束时间（下一跳时间和快照都不变，不补跳）；最多 5 个目标，第 6 个挤掉最近一次（续）燃最早的。
- 烬爆：每 5 次有效命中，以主目标为圆心、半径 3/3/3.5，按距离再按实体 ID 取最多 5 个（含主目标）；ICD 3s，冷却中计数停在 5/5，冷却结束后要等下一次有效命中才触发。
- 炽愈：每 5 次有效命中回 2.5/3.25/4% 最大生命（`EmberHeal`），ICD 6s，计数封顶 5。
- 套装事件伤害：`EmberCombatListener.dealP1(..., BURN|EXPLOSION, tag)`；`onFinal`（HIGHEST）把 BASE 复位为预定值（被其它监听改过时在 trace 里记一行），并清零原版修正。怪物没有 M；P1 世界内 PvP 已取消，所以"受击方 M"不会出现。
- 清空：脱战 8s（造成或受到敌方伤害都算战斗）、套装变化、死亡、换世界、退出时清计数和燃烧；内置冷却不清。冷却按单调时钟（nanoTime）计时，退出/关服时把剩余毫秒写入 `cr_p1_loadout.burst_cd_ms/sustain_cd_ms`，下次会话恢复（上限为一次 ICD）。
- HUD：每秒和每次计数变化时发 ActionBar，例如「烬爆 4/5 · 冷却 1.8s」「焚烬 2/3 · 燃烧 3 目标」；空闲时清掉，不发聊天消息。`/corerpg p1 status` 多一行运行时状态；`/corerpg p1 debug` 中以 `[P1套装]` 显示点燃、挤出、爆炸目标和脱战清空。

与书的差异／取舍：
- 点燃和爆炸在命中后的下一 tick 执行（不在原伤害事件内嵌套造成伤害）；炽愈立即执行。
- 满血时炽愈照样触发并开始 6s 冷却（回复量为 0，trace 记一行）。
- §8.4 五目标焚烬 731.888/s 是上界：按 §4.2 规则（每 3 击点燃一次、每次 4 跳）每秒最多 2.133 跳，实际可达约 569.9/s（测试里同时断言两者）。

## 9. G03 P1 强化／互换／升阶／精工／成色／分解（CoreRpg 1.17.0，未部署）

旧 `EnhanceService`/`ForgeService`/`ScrapService` 和旧菜单不变（它们只认白名单 NI id，本来就拒绝 `ember_v1` 物品）。

- 纯规则 `EmberUpgradeRules`：§6.1 表；保底计数 `pity` 存在物品数据里，下一次尝试序号 = pity+1，序号达到"本档最多尝试"即必成；成功 enh+1、pity 归零；失败 pity+1，不降级、不爆装。§6.3 互换只交换 enh+pity，两件都绑定。§6.4 原位升阶（uid 不变，NI id 换成下一阶模板，强化轨道/成色/精工/绑定保留）。§5.3 精工、成色（卓越→极品拒绝）、分解（只限 `src=drop` 的 T1–T3）。每个操作返回 cost 和 rev+1 的新数据，预览就是结果。
- 单测 `EmberUpgradeRulesTest`：E05–E09；§6.2 精确分布（均值 22.784、中位 22、P90 31、P95 33、最坏 51；期望 927.87 碎片／68.37 核心／12,984.12 币；全失败路线正好 2,132／157／29,720）＋20 万次种子蒙特卡洛（走真实规则代码）。
- Bukkit `EmberForgeService`（`/corerpg p1 enhance|upgrade|refine [craft|quality]|dismantle|swap [uid前缀]`，不带 `confirm` 只预览；`rid:<id>` 可选；`/corerpg p1 sync`；管理员 `/corerpg p1 flag <玩家> q04|q07 [clear]`）：
  1. P1 总开关开启，且不在 P1 战斗世界或副本世界（"城内"）。
  2. 物品可信：签名 NBT＋DB 行（owner、rev、state=active）；每个 uid 同时只允许一个操作。
  3. 先检查全部材料和金币，再一起扣除（任何一步失败都退回）；材料不足时不抽随机、不推进保底。
  4. 强化在扣费后掷随机数；新物品堆叠预先生成（模板缺失时直接退回，不写库）。
  5. 一个 DB 事务（单独线程）：按 request id 查重放 → 按 uid 顺序 `SELECT … FOR UPDATE` → 校验 owner/rev/state → `UPDATE … WHERE rev=?` → 写 `cr_p1_txn` 账本 → COMMIT。成功后按 uid 找到背包里的堆叠并替换；失败或重放时退回全部材料和金币。
  6. 默认 request id = 操作类型＋uid＋rev（互换为两件的 uid/rev 哈希），所以同一状态的重复请求只执行一次；完成后物品 rev 已变，再点一次是新的操作。
  7. 分解：先把物品从背包拿走再提交，成功给胚料、DB 状态改成 `dismantled`；失败把原物品还回去。
  8. DB 为准：登录 3 秒后和 `/corerpg p1 sync` 会把 rev 落后于 DB 的堆叠按 DB 重写。
  9. YAML 存储没有 DB，只有签名 NBT；重放表在内存里。
- 首通条件是桩：`PlayerData` 计数 `p1_first_clear_q04@all` / `p1_first_clear_q07@all` > 0，等 Q04/Q07 接线时由通关事件写入。
- 新增 NI 材料 `mat_ember_v1_blank`（余烬胚料，`ember-v1-gear.yml`）；碎片／骨尘／核心碎片沿用 `mat_ember_shard`／`mat_ember_bone_dust`／`mat_ember_core_fragment`。
- TrMenu 桩 `ember_p1_forge.yml`（`/ember_p1_forge`，未挂主菜单）：只转发到上面的命令；互换和分解的确认仍需手打命令。
- 新表 `cr_p1_txn`（request_id 主键、kind、owner、uid_a/uid_b、before/after 规范串、cost、note）；`cr_p1_loadout` 增加 `burst_cd_ms`、`sustain_cd_ms`（表已存在时用 information_schema 检查后 `ALTER TABLE … ADD COLUMN`，只动 P1 自己的表）。

与书的差异／取舍：
- 书 §6 写的是同步事务；这里是"主线程先扣费，DB 事务在后台线程，失败退款"，避免主线程等数据库。代价是掉线时的退款只存在内存里（会写 WARN 日志），重启前登录会补发。
- 强化本身不绑定物品（书没有要求）；互换会绑定两件。
- `src=migrate` 也不可分解（书只点名任务／补发／测试件，这里按保守处理）。T0 不可升阶、不可分解。


## 10. G04 首批：统一结算 + Q01–Q03（CoreRpg 1.18.0，2026-10-01，未部署）

只作用于 P1 主线本（`ember-v1.yml` `enabled=false` 时进本直接拒绝）。旧日常 `EmberDaily/Ash/Crypt` 定义与菜单不变。

### 10.1 代码

- 纯规则 `p1/EmberRunRules`：Boss 结算包（300 币、24 碎片、6 骨尘、2 核心碎片、120 余烬经验、1 枚本阶印记、1 件随机 P1 装备）；装备 = 地图阶 · 目标族 60/20/20（未选 1/3）· 刃/护符 50/50 · 成色 70/23/6/1 · 精工 70/20/9/1；附加事件 70/15/10/5；`settle()` 没有 Boss 击杀时返回空（E11）；奖励 uid 由 (run seed, 玩家, run_id, reward_key) 决定，重试不重抽。
- `EmberRunRules.Ledger`：每行 (玩家, run_id, reward_key)，`record` 永不覆盖已有行（E03/E04）；状态 pending → delivered / mailed，首通自选为 await_choice；体力 reserved → committed / released。
- `EmberRunMaps`：读 `ember-v1-runs.yml`（地图、房间触发盒、门、刷怪点、A/B 组成、Boss 技能、加怪），校验每房 ≤8 只、≤2 远程、≤1 施法、刷怪点足够。
- `EmberRunSession` + `EmberRunStore`：`plugins/CoreRpg/p1-runs/runs/<run>.yml`、`p1-runs/ledger/<uuid>.yml` 为准，MySQL 镜像表 `cr_p1_run`、`cr_p1_reward`（主键 player, run_id, reward_key）。session 记 run_id、地图/规则版本、参与者、seed、入场时目标族快照、开本时抽定的附加事件、锁定人数与血量系数。
- `EmberRunDirector`：一局一个；房间由已提交的参与者进入触发盒开始，按 seed 选 A/B 组成；清房开门（铁栏平面变空气，DP 复用缓存世界，所以挂载时重新关门）；拴绳把跑远的怪拉回；附加事件在指定房间后出现（宝藏怪 / 奖励精英 / 末影箱）；最后一房清完 1.5 秒后刷 Boss；Boss 技能有火焰轮廓 + 聊天 + 缓慢预警，50% 以下追加招，Q03 50% 加怪。
- `EmberRunService`：进本 = 全员校验（解锁、30 体力、不在别的局）→ 预留体力 → 建 session → 30 秒通行证 → `TicketEntryService.dispatchStart` 建 DP 实例 → 40 tick 后核对：在实例里的成员提交体力，其余释放；没人进去则全部释放并作废。旧的每周首免、等级门槛、OP 免费都不走。人数 1–3，敌人血量 ×(1+0.65(n−1)) 在核对时锁定（A18）。
- 首通（每角色 × content_version）：Q01 自选族 T1 刃、Q02 自选族 T1 护符（绑定、`src=quest` 不可分解），Q03 30 碎片 / 4 核心 / 600 币；首通写下一张图的解锁（Q03 写 `p1_unlock_q04`，Q04 本批未做）。
- 印记：`p1_mark_t<n>@all` 账户计数，绑定；8 枚同阶换指定族 + 部位的同阶标准件。
- 新手包：没有任何 P1 装备的角色一次性得到 T0 刃 + T0 护符（`p1_starter@all`）。
- 命令：`/corerpg enter q01|q02|q03`、`/corerpg p1 target|marks|firstclear|claim|run`；管理员 `/corerpg p1 runs list|unlock|firstclear|starter`。PAPI `%corerpg_p1_…%`。

### 10.2 防重复发放（E01/E02/E11）

P1 局世界（`dungeon_EmberQ0*`，已加入 `scope.world_prefixes`）里：怪物掉落与经验球清空；DP 奖励脚本为空、超时 `reward=false`；`CoreRpgPlugin.onDeath` 的旧 MM 结算、`mmcredit` / `mmgiveall`、`ProgressService` 进度与管理发放全部跳过；非 CUSTOM 刷怪被拦截。唯一出口是 Boss 结算 → 账本 → `deliver()`。

### 10.3 地图改编（实际地图没有书里的 R0–RB/E 布局）

| 本 | 模板 | R1 | R2 | R3 | Boss | 附加事件锚点 | 门 |
|---|---|---|---|---|---|---|---|
| Q01 灰烬庭院 | `ember_daily` | 前厅 z3–12（门廊 z−3..2 当 R0） | 回廊 z15–24 | Boss 厅外圈 z27–37（与 Boss 同厅） | 0,66,31 | −6,65,23，R2 后 | z=13、z=25 |
| Q02 焦骨甬道 | `ember_daily_ash` | 骨廊 z7–15 | 3–5 格宽窄道 z18–35 | 鼓形厅外沿 z39–48 | 0,65,44 | 假岔路 5,65,10，R1 旁 | z=16、z=36 |
| Q03 残誓地窖 | `ember_daily_crypt` | 上层厅 y72 | 中层厅 y66 | Boss 圆厅 y60 | 0,60,48 | −5,66,38，R2 后 | z=18 @y72、z=40 @y66 |

- 书的 A/B 组成全部保留，每房 6 个刷怪点；Q03 加怪点用 (±6,60,44)，书的 ±8 在墙里。
- 所有点（spawn、房间点、Boss、加怪、事件锚点）和门方块（必须是铁栏）由 `scripts/check-dp-spawns.py` 检查：Q01 23 点、Q02 23、Q03 25，全部通过。
- 怪物 `plugins/MythicMobs/Mobs/EmberP1Main.yml`：Health / Damage = 规则表数值（`EmberRunMobsTest` 校验），防晒、不掉落、不捡物品、不随机装备、不消失；技能和攻击间隔由 CoreRpg 控制。

### 10.4 与书的差异 / 未验证

- 宝藏怪血量书没给，取 4×B_ref；奖励精英 10×B_ref。
- 近战 / 远程 / Boss 攻击间隔在伤害层强制（原版箭照样会射出，只是伤害按间隔）。
- P1 装备不能走邮件（签名物品），背包满时留在账本里，`/corerpg p1 claim` 补领（E10）；材料溢出走邮件。
- 开打后离开 / 团灭不退体力；开本失败、刷怪坏掉、重启（未开打）退还。重启时 settling 的局补完结算，其余作废并写一条 `refund` 账本行。
- DP 结束：CoreRpg 调 `dp script trigger ember_p1_complete|ember_p1_fail <玩家>`，失败时退回 `/dp leave` —— 未实测。
- DP `js-condition` 是否对每个队员单独求值未实测；CoreRpg 进本前自己也逐个校验。

## 11. G04 第二批：Q04–Q05 + T2 升阶 / 定向锻造 + 二阶觉醒（CoreRpg 1.19.0 → 实测修正 1.19.1，2026-10-02 部署）

书 §23.2「Q04～05＋升阶＋二阶觉醒」。P1 仍默认关闭（`enabled=false`）；旧日常 `EmberDailyTide/Spire` 定义不变（只修了断塔旧日常的楼梯问题，见 11.5）。

### 11.1 代码

- **Boss 多技能**（B2.145）：每招一个 `nextAt`，从首领出现时起算；同一时刻多招到点 → 先放列表靠前的（`dueSkill`），另一招在收招（`recover`，默认 0.5 秒）后补放，节拍仍按出现时刻对齐（`nextDue`，长时间卡住不连放）。前摇期间首领普攻取消（`onMobHit`）。
- **锁定圈** `target: player`：开始前摇时按 seed 选一名在场参与者，以其脚下为圆心；前摇中位置不变（可走出）。
- **击退** `kb`（0–1 格）：从技能源点向外推，逐 0.25 格检查脚下有地、身体两格无方块、仍在 Boss 区内，否则停在最后一个安全点；不会推下平台或推进墙。
- **组队分散**（B2.146）：DP 把全队传到同一出生点；第 2、3 名成员 1.5 秒后移到 `spread` 点（仍在出生点 2.5 格内才移）。
- **通道 / 护栏 / 清块**：`links`（某房清完后站进 `from` 盒 → 传到 `to`）、`rails`（实例挂载时往空气里放铁栏）、`clear`（实例挂载时把指定方块设为空气）。模板文件都不改。校验：通道目标不能在自己的入口盒里，护栏不能盖住任何站点（出生 / 分散 / 刷怪 / Boss / 事件 / 通道目标）；`scripts/check-dp-spawns.py` 同样检查。
- **首通材料**：`first_clear` 新增 `blank`（胚料 `mat_ember_v1_blank`）与 `bone`（骨尘），账本键 `fc_<图>_blank` / `fc_<图>_bone`。
- **T2 定向锻造门槛**（B2.147）：`/corerpg p1 marks exchange … 2` 需本人首通 Q04（T3 需 Q07）。首通读真实主线本键 `p1_first_clear_q04@v1`，管理员桩 `@all` 也认。
- **二阶觉醒提示**（B2.148）：`/corerpg p1 status` 与 `%corerpg_p1_awaken_next%` 显示下一档缺什么（§19.1）。觉醒规则本身（§4.3）在 G02 已实现，未改。
- 入口：`/corerpg enter q04|q05`、冒险菜单图标 4/5；菜单显示 T2 印记、T2 定向锻造状态、当前觉醒与缺项。

### 11.2 地图改编

| 本 | 模板 | 出生 / 分散 | R1 | R2 | R3 + Boss | 附加事件 | 门 |
|---|---|---|---|---|---|---|---|
| Q04 潮蚀水道 | `ember_daily_tide` | 0,64,0 / ±2,64,0 | 沿岸 z5–17 | 折桥 z19–37 | 闸厅外圈 z39–51，Boss 0,64,46 | R1 后，侧室锚点 6,64,12 | z=18、z=38 |
| Q05 断塔回廊 | `ember_daily_spire` | 0,64,2 / ±2,64,2 | 塔底厅 y64 | 中层环廊 y70 | 塔冠外圈 y76，Boss 0,76,0 | R2 后，环廊锚点 6,70,7 | z=4 @y64、z=4 @y70 |

- 每房 8 个刷怪点（书的最大组成 7 只）。Q04 31 点 / Q05 33 点 + 门方块全部通过 `check-dp-spawns.py`。
- Q04：侧室旧告示牌 (6,64,11) 只在实例开局清除（`clear`）。
- Q05：通道 1 = 清完 R1 后进门后的阶梯口 (x−1..1, z3..9, y64–69) → 环廊北侧 (0,70,−6)；通道 2 = 清完 R2 后进门 (x−1..1, z5..7, y70–72) → 塔冠南侧 (0,76,3)。环廊外沿四边、中空斜角缺口与中空北缘（z=2）放运行时护栏；r2 门前 z=3 一排低铁栏在实例中清除（否则门开了也过不去）。塔冠铁栏圈是孤立细栏柱，斜角缝能钻过，楼梯井 z=7 敞开——实测机器人、远程怪和首领都掉到了环廊（B2.155/B2.156），1.19.1 补了 15 段塔冠护栏；泛洪检查（站立格 8 邻域内无落差格）塔冠与环廊都通过。

### 11.3 数值（书 §9 / 第 14–15 章；没给的见 11.4）

| | Q04 | Q05 |
|---|---|---|
| B_ref | 35 | 59 |
| 近战 / 远程 / 重甲 / 施法 HP | 105 / 77 / 147 / 91 | 177 / 130 / 248 / 153 |
| 宝藏怪 / 奖励精英 HP | 140 / 350 | 236 / 590 |
| 小怪伤害 | 10 | 14 |
| Boss | 潮闸重卫 2200 HP · 普攻 18 / 3 秒 | 断塔斧卫 4000 HP · 普攻 26 / 3 秒 |
| 技能 1 | 冲击圈：锁定玩家 半径 2.5 · 每 10 秒 · 前摇 1.2 · 30 伤害 | 斧刃横扫：120° 扇形 5 格 · 每 9 秒 · 前摇 1.2 · 44 · 击退 0.5 |
| 技能 2 | 闸杆推击：90° 扇形 4 格 · 每 14 秒 · 前摇 1.0 · 18 · 击退 1 | 径向冲击：以首领为中心半径 3 · 每 15 秒 · 前摇 1.0 · 26 · 击退 1 |
| 首通 | 胚料 6 · 核心 6 · 币 900，开放 Q05 | 骨尘 20 · 胚料 6，记录 Q06 开放（后续批次） |

每局 Boss 结算与第一批相同（300 币 / 24 碎片 / 6 骨尘 / 2 核心 / 120 经验 / 1 枚本阶印记 / 1 件本阶装备）；本批两张图均为 T2。附加事件池 无 70 / 宝藏怪 15 / 奖励精英 10 / 箱 5；人数血量系数 1+0.65(n−1)；结算资格 = 已提交 且 有过战斗（打过或被打过本局怪）且 在场或阵亡。

### 11.4 裁决记录（书中含糊处）

| # | 问题 | 裁决 | 理由 |
|---|---|---|---|
| D1 | 「二次觉醒」指什么 | = 书中「二阶觉醒」（§23.2、§23.1、§3.4），即觉醒 II：两件都 ≥T2 且都 ≥+6；焚烬 0.34B、烬爆 0.80B / 3 格、炽愈 3.25% | §4.3 已有完整规则，G02 已实现；保守读法 = 不加新机制，只端到端验证（真实 T2 掉落 + 升阶）并补 §19.1 缺项提示 |
| D2 | 「开放 T2 定向锻造」 | = 8 枚 T2 印记兑换指定族 + 部位；需本人首通 Q04（T3 需 Q07） | 与 §6.4 升阶门槛同一把锁；第一批印记兑换没有门槛，T2 印记在 Q04 之前拿不到，门槛只防管理员发放 / 未来跨阶来源 |
| D3 | Boss 区「RB 减 4」 | 保持整个 Boss 厅为区域（同 Q01–Q03） | 这两张模板的厅减 4 后只剩约 3×3，击退与走位都没空间 |
| D4 | 技能收招时间 | 0.5 秒 | 书没给；够看清、不拖节奏 |
| D5 | Q05 横扫「轻推」 | 0.5 格；其他推击 1 格（书上限） | 书只写「轻推」 |
| D6 | 宝藏怪 / 奖励精英血量 | 4×B_ref / 10×B_ref | 沿用第一批 |
| D7 | 多技能撞车 | 先放列表靠前的，另一招收招后补放，节拍仍从开战起算 | 书只说「互斥」；不跳号保证每招频率与书一致 |
| D8 | Q04 附加事件位置 | R1 后侧室 | 水道没有 R2 侧室（同 Q02 的处理） |
| D9 | 断塔模板楼梯走不上去 | Q05 用清房后单向通道 + 运行时护栏；偏离书 §15 / M02「楼梯可步行」 | 机器人实测卡在 z≈7.7 y67（x=−1/0/1），第二段楼梯离地 3 格且被门后铁栏挡住，环廊外沿约 14 格落差无护栏；改地图不在本批 → 待办 B2.151 新做 `ember_daily_spire_v1` |
| D10 | 潮蚀模板侧室旧告示牌 | 只在 Q04 实例开局清除，不改模板文件 | 旧日常共用模板；模板改动留给地图重做（B2.153） |
| D11 | 旧日常断塔同一楼梯 bug | 低风险修：DP 脚本在清完 wave1 / wave2b 时传送全队上一层 | 只加两行已在深渊用过的 `$teleport`，不动地图、刷怪与奖励（B2.152） |

### 11.5 实测（2026-10-02 03:12–04:04 CST，CoreRpg 1.19.0/1.19.1，P1 运行期临时开启，测完关闭）

| 项 | 结果 |
|---|---|
| Q03 → Q04 → Q05 首通与解锁（P1Alpha 单人） | PASS：Q03 首通「开放 Q04」；Q04 首通 核心 6 · 币 900 · 胚料 6 · 「开放 Q05」；Q05 首通 骨尘 20 · 胚料 6 · 「开放 Q06（后续批次）」 |
| 每局结算 | PASS：币 300 · 碎片 24 · 骨尘 6 · 核心 2 · 经验 120 · T2 印记 1 · T2 装备 1；奖励精英事件 +碎片 10 · 核心 1 |
| Q04 三人局 | PASS：敌方生命 ×2.30；三人分散出生；老玩家 7 行，两名首通 11 行 |
| Q05 三人局 + 挂机队员 | PASS：ScrapT1 已提交、在场、从未战斗 →「本局没有你的结算资格」，日志 acted=false |
| T1→T2 升阶 | PASS：首通前「需要本人已首通 Q04」；真实 Q04 首通后升阶成功（uid、强化 +3 保留） |
| T2 定向锻造 | PASS：首通前被拒，首通后兑换成功 |
| 二阶觉醒 | PASS：T2+6 同族 → 觉醒II，烬爆 0.80×B 半径 3 实际触发；真实 T2 +0 护符 → 觉醒I + 缺项提示 |
| 旧掉落 | PASS：无 MM/DP 旧掉落；P1Alpha 背包里 3 个火把为既有问题（Q03 地窖，与本批无关） |
| 重启退体力 | PASS：入场未开打即重启 → aborted，ledger refund stamina:30 → 上线到账 |
| 分解「唯一 P1 刃」警告 | PASS：警告 + 30 秒确认，未确认不销毁 |
| check-dp-spawns | PASS：18 个地牢 0 坏点（含 spread、通道目标、护栏不压点） |
| 旧日常断塔（B2.152） | PASS：两次传送触发，通关发奖 |
| gameplay 套件（P1 关） | PASS 9/9（1.19.1） |

### 11.6 未完成 / 已知问题

- B2.151 可步行断塔地图（待做）；B2.153 潮蚀模板告示牌（待地图重做）。
- 击退距离未单独测量（技能命中与预警已实测）。
- 2026-10-02 01:53 重启丢库（B2.154）：DB 回退到备份时间点，而 `p1-runs/ledger/*.yml` 与玩家背包里的 P1 物品是重启后的版本——部分 P1 物品在 `cr_p1_item` 里没有记录（显示「DB 记录未找到」，不能用），个别账号有旧的首通自选待领行。
