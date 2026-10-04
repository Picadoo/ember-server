# 余烬 v1.0 P1 · 来源开关表（G01 准备阶段）

> 对应策划书 `docs/design/design-ember-v1.0-P1.md` §4.5、§7.2、§19.4、§20.2、§22.2（C04/C05/C13）、§23.1。
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
3. `NORMAL`？：MythicMobs `DamageModifiers` 生效（注释和 `docs/reviews/critic-fixes-20260927.md` 都假设它晚于 LOWEST，**优先级尚未实测**，见 §4）。
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
16. **书中可能已过时的一处**：策划书和 `docs/reviews/critic-fixes-20260927.md` 的前提是"引擎血量上限 2048，所以要用 DamageModifiers 补有效生命"，但当前 `spigot.yml attribute.maxHealth.max` 已经是 20000，P1 首领可以直接设血量，不必再用 DM。

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
| G05 | ✅ | 1.29.0：踏步冷却用单调时钟，下线不清（只有重启会清，见 13.13） |
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

## 12. G04 第三批：Q06–Q07 + T3 升阶 / 定向锻造 + 三阶觉醒 + 挑战版（CoreRpg 1.20.0 → 实测修正 1.20.4，2026-10-02 部署）

书 §23.2 后段「Q06～07＋三阶觉醒＋挑战版」。P1 仍默认关闭（`enabled=false`）；旧日常 `EmberDailyFrost/Rail` 定义不变。

### 12.1 代码

- **技能引擎**（B2.158）：`line` / `charge` 条带可设 `start`（离首领 N 格开始，Q06 刀气 1..7 格）；追加段 `follow` 可设 `shift`（沿用第一段锁定的原点与方向，向首领右侧平移 N 格，不重新瞄准）；每招可设 `recover`（Q07 重砸 1.5 秒）；新类型 `charge`（冲撞）：条带按真实可走地面截短（遇墙 / 悬空 / 出 Boss 区即止，最长 8 格），命中时首领传到条带末端；追加段等待期也算施法（停普攻）；追加段默认总是触发（`below` 1.01）。
- **挑战版**（B2.159，§18.1）：`/corerpg enter <q01..q07> challenge`（或 `q03c`），全队每人需本人首通 Q07；根节 `challenge:` 统一 T3 参照覆盖值（近战 270 / 远程 198 / 重甲 378 / 术者 234 / 普攻 24；宝藏怪 360 / 奖励精英 900；首领 8000 / 普攻 44 / 重技 72 / 轻技 44），人数倍率不变；装备与印记全部 T3；成色 60/28/10/2，精工不变；事件池不变；无首通奖励；局号后缀 `c`。`light: true` 标在 Q02 横扫（追加段）、Q04 闸杆推击、Q05 径向冲击。
- **模板掉落物**（B2.157）：ember_daily* 模板存档里有掉在地上的火把物品实体（玩家进本「捡到火把」）；实例挂载与区块加载时清除物品实体，模板不改。
- **Q06 / Q07**（B2.161/B2.162）：两张图、MM 怪、DP 定义、`/corerpg enter q06|q07`、冒险菜单图标 6/7。
- **T3 门槛**：T2→T3 升阶与 T3 定向锻造（8 枚 T3 印记兑换）都读 `p1_first_clear_q07@v1`（第二批已接好同一把锁，Q07 上线后自动生效）。
- **成套进度 / P1 完结**（B2.163）：`/corerpg p1 status` 与 `%corerpg_p1_set_progress%`：「成套进度（族）：同族 n/2 · T3 n/2 · T3+9 n/2」，满即「P1 套装完成」；Q07 首通结算后提示 P1 完结与已开放的内容。
- **菜单**（B2.164）：冒险页 7 个图标、T3 印记 / T3 定向锻造 / 挑战版 / 成套进度；新菜单 `ember_p1_challenge`（七图挑战版）。
- **出生点背后落差**（B2.160）：Q01/Q02/Q06/Q07 模板出生走廊 z=−5 外是空的（落下无路回），Q05 塔底西门洞有 2 格地洞；全部加实例护栏。
- `scripts/db-dump.sh [标签] [目录]`：mysqldump 到 `/workspace/backup/db-<库>-<时间>-<标签>.sql`（0600），不打印密码（sudo socket，或配置里的账号写进 0600 临时 defaults 文件）。

### 12.2 地图改编

| 本 | 模板 | 出生 / 分散 | R1 | R2 | R3 | Boss 厅 | 附加事件 | 门 |
|---|---|---|---|---|---|---|---|---|
| Q06 霜封哨所 | `ember_daily_frost`（脚底 y70） | 0,70,0 / ±2,70,−2 | 霜旗外院 z5–15 | 军械仓 z17–33 | 盾墙内院（前厅）z35–41 | z43–53，11×11，Boss 0,70,48 | R2 后，西侧裂隙台锚点 −5,70,25 | z=16、z=34（模板铁栏），z=42（运行时门） |
| Q07 锈轨矿道 | `ember_daily_rail`（脚底 y64） | 0,64,0 / ±2,64,−2 | 装卸场 z5–14 | 侧置货仓 z16–32 | 废压机房（前厅）z34–39 | z41–51，11×11，Boss 0,64,46 | R2 后，东侧支廊锚点 9,64,20 | z=15、z=33（模板铁栏），z=40（运行时门） |

- 每房 8 个刷怪点；Q06 31 点 / Q07 31 点 + 门方块通过 `check-dp-spawns.py`（`# runtime:` 注释的门要求模板处为空气）。
- **楼梯**：两张模板都没有楼梯（单层）。**落差**：霜封两侧裂隙槽（x ±4..6）落差 4 格、无路回 → 40 段 2 格高实例护栏（+ 大厅口两侧 1×1 坑）；锈轨全平地。泛洪检查（站立格四邻无未护栏落差）两张图都通过。**告示牌**：各 5 块（出生两侧、两扇门前、Boss 厅）只在实例开局清除。
- 两张模板的 Boss 厅是 11×11，书要求 17×17 / 21×21；厅里 3 块发光石（霜封）/ 4 根栏杆灯柱（锈轨）保留。

### 12.3 数值（书第 16–17 章、§9.4、§18.1）

| | Q06 | Q07 |
|---|---|---|
| B_ref | 59 | 68 |
| 近战 / 远程 / 重甲 / 术者 HP | 177 / 130 / 248 / 153 | 204 / 150 / 286 / 177 |
| 宝藏怪 / 奖励精英 HP | 236 / 590 | 272 / 680 |
| 小怪伤害 | 15 | 19 |
| Boss | 霜封统领 4600 HP · 普攻 28 / 3 秒 | 炉锁巨卫 5800 HP · 普攻 34 / 3 秒 |
| 技能 1 | 刀气：前方 1..7 格 × 宽 3 · 每 10 秒 · 前摇 1.2 · 48 | 重砸：锁定一名玩家脚下 半径 3.5 · 每 9 秒 · 前摇 1.3 · 56 · 收招 1.5 |
| 技能 2 | 刀气·二段：第一段命中后 1 秒，同方向右移 4 格 · 前摇 1.2 · 48 | 冲撞：≤8 格 × 宽 4 · 每 14 秒 · 前摇 1.3 · 56，首领冲到条带末端 |
| 编排 A / B | R1 近6+远1 / 近5+重1；R2 近5+远2 / 近5+术1；R3 近6+重1 / 近5+远2 | 同左，R3 B = 近6+远1 |
| 掉落 | T2 装备 + T2 印记 | T3 装备 + T3 印记 |
| 首通 | 核心 10 · 币 1200，开放 Q07 | 胚料 12 · 核心 12 · 币 1800；开放 T3 定向锻造、T2→T3 升阶、七图挑战版 |

### 12.4 裁决记录（D12 起）

| # | 问题 | 裁决 | 理由 |
|---|---|---|---|
| D12 | 「P1 末段内容」指什么 | = §18.1 七图挑战版 + Q07 首通的 P1 完结提示；不做深渊新导演 | §23.2 后段写「挑战版」；§18.3 深渊明确「不作为首批必做」 |
| D13 | 「套装完成」 | 不加新奖励，只显示成套进度（同族 / T3 / T3+9 各 x/2），满即「P1 套装完成」= 觉醒III 条件 | §1 不增第四套、不叠新维度；§19.1 要求装备页显示套装与下一次突破缺项 |
| D14 | Q07「定点重砸」的点 | 开始前摇时按 seed 选一名在场参与者，锁定其脚下（同 Q04 冲击圈），前摇中不跟随 | 「定点」= 预警后不移动；以首领自身为圆心会让近战无处可站 |
| D15 | 模板 Boss 厅只有 11×11、没有书中的 R0/E 布局 | R3 = 大厅前的前厅，大厅墙口放运行时门（清 R3 后开）；Boss 区 = 整个大厅；厅内小灯块保留 | 改模板不在本批；运行时门保证 Boss 不会在 R3 战斗时被引出（**1.26.0 起由 §13.9 取代：七张图换书白盒，首领厅 29–41 宽**） |
| D16 | 霜封裂隙槽 | 只在实例加 2 格高铁栏（40 段 + 出生走廊 1 段） | 落差 4 格无路回，书 §16 §9 禁止；模板与旧日常共用 |
| D17 | Q06 第二段刀气的触发与方向 | 每次都放（书没写血量门）；沿第一段锁定的方向，以首领右侧为正平移 4 格；首领左侧始终安全 | 书 ch.16 §7「第一条带右侧平移 4 格」「左侧始终保留安全走位面」 |
| D18 | 挑战版重 / 轻技能 | 刀气两段、重砸、冲撞、重斩、Q01 单技能 = 72；Q02 横扫追加段、Q04 闸杆推击、Q05 径向冲击 = 44；术者直线 = 普攻 24 | §18.1「第二项较轻技能按 44，重斩／刀气／重砸／冲撞按 72」 |
| D19 | 挑战版首通 / 解锁 | 没有首通包、不写首通标记、不解锁；局号加 `c` | 首通按图 + 内容版本一次（§9.4），挑战版是同一张图的另一难度 |
| D20 | 模板里的火把掉落物 | 实例挂载 / 区块加载时清除物品实体 | 模板存档残留；不改模板 |
| D21 | 冲撞「最多 8 格直线」 | 条带按真实可走地面截短；命中时首领传到末端；不推人 | 书禁止真实物理；截短后预警与实际一致 |
| D22 | Q06/Q07 附加事件位置 | Q06 R2 西侧裂隙台；Q07 R2 东侧支廊（唯一侧室） | 书 E「只从 R2 进入」；模板没有独立 E 房 |
| D23 | 挑战版资格 | 全队每人都要本人首通 Q07（`p1_first_clear_q07@v1`，管理员桩也认） | §18.1「需先首通 Q07」；与 T3 升阶同一把锁 |
| D24 | 冲撞终点 / 首领爬墙（实测 B2.165） | 冲撞路径同时检查首领碰撞箱两侧与前沿（半宽 0.4）；被挡则改向大厅中心，不足 2 格不放；离家地面高 4.5 格以上或卡在方块里立即回原点 | 只查中心线时 Q07 首领半身冲进厅墙 (5,64,40)，爬上墙顶 y70 走了整场；书 §I「冲锋终点夹到区域内，越界请求取消」 |
| D25 | 同一 DP 世界被下一局复用（实测 B2.166） | 结束兜底（脚本触发、30 秒 dp leave）先确认该世界仍属本局（`stillOurs()`）再动手 | DP 复用 `dungeon_EmberQ04_xxx` 世界名；旧局的 30 秒兜底把刚进新局的玩家踢出 |
| D26 | 首领技能击退距离（实测 B2.167） | 首领技能命中与首领普攻都不带原版击退速度，只有 P1 传送式推开（≤ kb，遇墙 / 悬空 / 出区即止） | 1.20.2 实测横扫 = 0.5（推开）+ 约 1.85 格原版击退并跳起 0.96，径向冲击同理；书 ch.15「水平击退最多 1 格」「可轻推但不能落台」、§I「击退不能送过外墙或下层」 |
| D27 | P1 装备耐久（实测 B2.168） | 带 P1 数据的物品永不掉耐久（取消 PlayerItemDamageEvent），所有世界生效 | 书把 P1 装备当永久资产（§18.3「不掉永久装备」、强化「不爆装」）；原版耐久会在 DB 行仍 active 时把物品碎掉 |
| D28 | §3.1「基础补给」的数量（B2.169） | 首次进入（与 T0 刃 + 护符同一次、`C_STARTER` 只发一次）给 **5 瓶绑定余烬回复药**；单一配置 `ember-v1.yml starter.heal_potions`（0 = 不发） | 书没给数。§19.4：每瓶 20% 最大生命、所有回复药共用 15 秒冷却、单局普通通关 300 币「足以负担正常使用」。Q01 一局 60–90 秒按冷却最多喝 4–6 瓶，5 瓶 ≈ 一局的冷却上限、价值 50 币（一局收入的 1/6），保守且不改变「药靠商店买」 |
| D29 | §19.4 商店的形态（B2.169） | `/corerpg p1 shop [buy n]`（菜单「生活」旁可接）：价格单一配置 `shop.heal_potion.price`（10）；每次 1–16 瓶、受余烬币与空格限制；只在城内买（P1 副本世界拒绝）；物品带 `ember_bound` 标记：不能丢出、不能寄售，没有回售 | 书「绑定、不回售」；书只写「商店」，没写副本内能否买，保守取「入场前备药」，避免战斗中无限补给绕过 15 秒冷却之外的消耗；回复药不堆叠，所以按空格限量 |
| D30 | 对账与补发（B2.170） | `/corerpg p1 audit [玩家]` 只读对比在线玩家背包 / 盔甲 / 副手 / 末影箱 / 光标里的 P1 物品与 cr_p1_item：NO_ROW / HELD_NOT_ACTIVE / DUPLICATE / ACTIVE_NOT_HELD；`audit restore <玩家> <uid 前缀>` 只在行属于该玩家、state=active、且所有在线玩家都没有这个 uid 时，按 DB 行原样重建（同 uid、同 rev，行不改），lore 加「bug 补发（B2.168 耐久丢失）」并记日志 | §20「测试员补发的装备必须标来源」；同 uid 重建不新增资产，在线查重防复制 |
| D31 | 试玩后数值调整（用户批准 2026-10-02 14:31，B2.171） | Q03 首领 1300→1000；Q04 近战 105→96、R3 A 近战 6+重甲 1→近战 5+重甲 1、首领 2200→1900、首通币 900→2100；Q05/Q06/Q07 首领 4000/4600/5800→3500/3800/4800。两份 ember-v1-runs.yml 与 MM EmberP1Main.yml 同改 | docs/reviews/PLAYTEST-P1-progression.md 第二次试玩：Q03/Q04 远高于书的参考投入仍低通关率，Q05 自然节奏 0/13，参考装备 Q05–Q07 各 0/2，死亡集中在首领段 |
| D32 | 每日首次倒下退还回复药（用户批准 2026-10-02 14:31，B2.172） | 每个体力日（`DailyService.today()`，与体力同一天）第一次在 P1 主线本（含挑战版）倒下：退还该局喝掉的 P1 回复药，最多 `ember-v1.yml death_refund.max_potions`（5，0=关）。账本行 `deathrefund@<日>/potions` = `potion:<n>:<局号>`，`record` 不覆盖 → 同日第二次倒下、重连、重启、重复死亡事件都不会再发；该局 0 瓶也算用掉当天名额。只在城里（非 P1 副本世界）到账，背包空格不够就暂存（`/corerpg p1 claim`）。喝药计数存在局记录里（重启不丢） | 第二次试玩的「0 币 0 药 → 连 Q02 都死」螺旋；绑定药不可交易，不能变成金币来源 |
| D33 | `charm select` 套装提示（B2.173） | 回复按「选定护符 + 主手刃；主手不是刃时用刚才装备的刃 / 快捷栏第一把刃」计算并注明「手持该刃时生效」；实际判定仍只认主手 | 选护符时护符在主手，旧提示总是「未成套」 |
| D31–D33 实测 | 1.21.0 实机验证（2026-10-02 14:43–15:08 CST） | 观测怪物最大生命：Q03 首领 1000、Q04 近战 96 / 首领 1900、Q05 首领 3500、Q06 首领 3800、Q07 首领 4800；Q04 R3 A 两局都是近战 5＋重甲 1（按全本怪数拆分唯一）；Q04 首通结算余烬币 2100。退药：首次倒下用 1 瓶 → 日志 `death refund … 1 potion(s)`、回城「结算到账：今日首次倒下退还 回复药 ×1」；同日再倒下（用 1 瓶）→ `already used today`、不退；重登、重启服务器后再倒下都不补发；账本 `deathrefund@2026-10-02 / potions` 只有 1 行 delivered。`charm select`：「套装: 烬爆 觉醒III（配 T3 烬爆刃…，手持该刃时生效）」。P1 关闭时 gameplay 9/9，check-dp-spawns 0 个坏点 | 通过 |

### 12.5 实测（2026-10-02 04:28–06:35 CST，CoreRpg 1.20.0 → 1.20.4，P1 运行期临时开启，测完关闭）

| 项 | 结果 |
|---|---|
| Q06 单人首通（P1Alpha） | PASS：结算 币 300 · 碎片 24 · 骨尘 6 · 核心 2 · 经验 120 · T2 印记 · T2 装备；首通 核心 10 · 币 1200 ·「开放 Q07」；护栏、运行时门 z42、告示牌清除、10 个模板掉落物清除 |
| Q07 单人首通（P1Alpha） | PASS（第二次；第一次见 B2.165）：T3 印记 + T3 装备；首通 核心 12 · 币 1800 · 胚料 12；P1 完结提示 |
| Q07 重复通关 | PASS：没有第二份首通包 |
| Q06 三人局（Alpha/Beta/Gamma） | PASS：敌方生命 ×2.30，分散出生 (±2,70,−2)；Beta 首通 + 开放 Q07；Alpha 7 行；挂机 Gamma「本局没有你的结算资格」，日志 acted=false |
| Q07 两人局 | PASS：首领生命 9570 = 5800×1.65；Beta 首通 + P1 完结提示 |
| 解锁门槛（队伍） | PASS：Gamma 未首通 Q06 → 「未解锁（需先首通 Q06）」，整队拒绝 |
| T2→T3 升阶门槛 | PASS：Beta 首通 Q07 前「需要本人已首通 Q07」；首通后预览正常，碎片不足 14 → 补发后升阶成功，uid 保留 |
| T3 定向锻造 | PASS：首通前「T3 定向锻造需本人首通 Q07」；首通后 8 枚 T3 印记换 T3 烬爆护符 |
| 三阶觉醒 + 套装完成 | PASS：Beta T3+9 刃 + 护符（管理员补发，标来源）→「觉醒III」「成套进度…T3+9 2/2 · P1 套装完成」；实战「烬爆 0.95×B(107.40)=102.03 半径 3.5，4/4 目标」 |
| 挑战版 Q04c | PASS：T3 印记 + T3 装备，宝藏额外币 100；日志「settle … (challenge T3)」，局号 `q04c-…`，无首通 |
| 挑战版门槛 | PASS：单人 Beta 首通 Q07 前被拒；队伍拒绝列出 Beta 与 Gamma |
| 旧掉落 | PASS：Alpha / Beta 背包无原版怪物掉落 |
| 重启退体力 | PASS：Gamma Q01 入场未开打即重启 → aborted；上线「结算到账：体力 30」 |
| 同世界复用（B2.166） | PASS（1.20.2）：失败后立即再进 Q04 拿到同一世界名，30 秒后没被踢 |
| DP 5 秒冷却 | PASS：进本被拒，体力退回 |
| check-dp-spawns | PASS：20 个地牢 0 坏点 |
| 首领击退距离（Q05 断塔斧卫） | 1.20.2 FAIL → B2.167；1.20.3 PASS：11 次命中，横扫每次 0.50 格、径向冲击每次 1.00 格（靠墙时截短到 0.50 / 0.25），无跳起；首领普攻不再位移 |
| gameplay 套件（P1 关） | PASS 9/9（1.20.3，06:09 CST；1.20.4 复跑 PASS 9/9，07:35 CST） |
| P1 装备耐久（B2.168） | 1.20.4 PASS：起始之刃 25 次命中后耐久 0（修前约 9 局 Q01 碎掉） |
| 起步补给（B2.169 / D28，1.20.5） | PASS：新角色 P1Echo 首次进入 → T0 刃 + 护符 + 「起步补给：余烬回复药 ×5（绑定）」，日志 `starter P1Echo heal potions 5/5` |
| 补给商店（D29，1.20.5） | PASS：价目「10 余烬币 / 瓶」；P1Alpha 买 2 瓶扣 20（8900 → 8880）；0 币「余烬币不足（需要 10，现有 0）」；背包满「背包没有空格」；绑定药丢出被拒、寄售被拒 |
| 对账命令（B2.170，1.20.5） | PASS：Echo 0 项；Delta 1 项（ACTIVE_NOT_HELD 起始之刃 30d7d197，已于 1.20.4 按新 uid 补发）；Alpha 3 项（两把 T2+6 烬爆刃 + 一件 T2+6 烬爆护符） |
| 补发 P1Alpha 的 T2+6 刃 | PASS：`audit restore P1Alpha acbedacb` → 原 uid、rev 0、「NBT 校验: OK · DB state=active」；第二次补发被拒「仍有在线副本」；对账剩 2 项 |

### 12.6 未完成 / 已知问题

- 模板 Boss 厅 11×11（书 17×17 / 21×21，D15）；运行时门打开后首领可在容差内走出门口 2 格。
- B2.151（可步行断塔）、B2.153（潮蚀告示牌）仍开着。
- B2.168 / B2.170：P1Alpha 磨碎的 T2+6 烬爆刃（acbedacb，03:11 发放、配仍在身上的护符 07c25987）已按原 uid 补发。对账还剩 Alpha 的 f9f8c968（T2+6 刃）+ acc86b0f（T2+6 护符）——03:10 第一次管理员发放的那一对，1 分钟后又发了一对，不在身上、去向无日志；Delta 的 30d7d197（旧起始之刃，已换新 uid）。这三行是否改 state 由策划决定，没动。
- B2.169 已做（1.20.5，D28 / D29）。
- 机器人在潮蚀水道里寻路不可靠（测试工具问题）。

## 13. 内容批 C1：§19 菜单 / 帮助 / 烬斩快捷键（CoreRpg 1.22.0，2026-10-02 16:55 CST 部署）

用户要求（2026-10-02）：平衡用离线模拟器（`tools/p1sim`，见 docs/reviews/PLAYTEST-P1-progression.md 2b）估，时间放在内容上。Q01–Q07 + 挑战版已经全部落地，书里剩下没做的是 §19 菜单（装备页、帮助、一级入口、假入口）、B2.151 可步行断塔地图、D15 大首领厅（后两项是地图工作）。本批做菜单，不改任何数值。

### 13.1 内容

| 项 | 落点 | 书 |
|---|---|---|
| 装备页 `ember_p1_gear`（B2.174） | 主手刃 / 已选护符（`shortLabel`）、实际 B · 生命 · 防御 · 承伤倍率 · 等级 · 有效生命、套装 + 三套说明 + 觉醒条件、下一次突破、成套进度；按钮：查看手持物品、手持护符选定、回复药买 1 / 5 瓶、补领、工坊、冒险。数字全部来自 PAPI `p1_stats / ehp / blade / charm / awaken / awaken_next / set_progress`（与 `/corerpg p1 status` 同一份 EmberLoadout） | §19.1、§19.2 |
| 帮助页 `ember_help`（B2.175） | 9 格只读说明：怎么玩、操作、三套装、变强、收益与体力、补给与倒下、七张图、背景（3 句，可不读）、旧系统在主线本里不加数值 | §3.1、§19.1、§19.3、§19.4 |
| 烬斩快捷键（B2.175 / D34） | `SkillService.onSwapHotkeyP1`：P1 世界里、主手是有效 P1 刃时按 F（副手键）= 烬斩，取消这次副手交换 | §3.1、§19.1 |
| 主菜单（B2.176 / B2.177） | 新增「装备 · 主线本」「工坊 · 主线本」两格；「冒险」说明改 Q01–Q07 + 挑战版；「帮助」打开帮助页；「礼包」「外观」改成灰色「暂未上架」、不可点击（D35）；「技能」说明补一行主线本 F 键 | §19.1 |
| 工坊（B2.176） | `ember_p1_forge` 加「互换 · 确认」按钮和「返回装备页」 | §19.1「不需要手打命令」 |

### 13.2 裁决记录

| # | 问题 | 裁决 | 理由 |
|---|---|---|---|
| D34 | 烬斩怎么放（书只说「默认有可用主动」「不需要手打命令」，现状只能 `/corerpg skill` 或开菜单点） | P1 世界里主手是本人有效 P1 刃时，F（副手键）= 烬斩，交换被取消；P1 世界外、主手不是 P1 刃、P1 关闭时 F 照旧交换。冷却与目标规则不变（8 秒、5 目标、不暴击、不计套装） | P1 不认副手（§4.1「放进副手也不会成为第二份护符」），占用 F 不损失功能；只在 P1 世界生效，旧玩法不受影响；用刃作条件避免拿着药 / 食物时误放 |
| D35 | 主菜单「礼包」「外观」点开是空商城 | 不删格子（保持布局和非 OP 菜单测试），改灰色「暂未上架」、去掉点击动作；有货上架时再恢复 | §19.1「未完成系统不以可点击假入口充数」；商城本身已在 2026-09-26 下架未接线货架 |
| D36 | 帮助页的背景文字（书只说「两三句说明，不读也能进入 Q01」，没给原文） | 自写 3 句、放在独立一格「背景（可不读）」，不加剧情任务、不给奖励 | §3.1；不新增系统 |
| D37 | 装备页数字的来源 | 只用 PAPI 读 EmberLoadout（与 status 同源），菜单里不写死任何 B / H；说明里写的公式与 §7.2 一字一致 | §19.2「物品展示的伤害必须是统一公式的输出」 |

### 13.3 实测（2026-10-02 16:55–17:03 CST，1.22.0）

| 项 | 结果 |
|---|---|
| 加载 | CoreRpg v1.22.0、TrMenu「43 个菜单已加载」无错误 |
| gameplay 套件（P1 关） | PASS 9/9（Hub 44 项 missing=[] unreplacedPAPI=0） |
| 装备页 / 帮助页（非 OP 机器人，`menu-lore-dump.js`） | 两页都打开，unreplacedPAPI=0；装备页的实际 B · 生命 · 防御 · 等级、主手 / 护符、觉醒缺项全部由 PAPI 替换（主手不是 P1 刃时显示「主手不是有效 P1 刃」） |
| F 键烬斩（P1 运行期临时开启，P1Fox，Q01） | 出生点按 F →「附近没有目标」（没消耗冷却）、副手仍空、主手仍是 T1 烬爆刃；进 R1 对着怪按 F →「释放 烬斩」 |
| Q01 一局 | 通关并结算（`settle … rows+9`，奖励精英额外碎片 10 + 核心 1）；之后 `corerpg p1 follow` → active=false |
| 备份 | jar `/workspace/backup/CoreRpg-1.21.0.jar`（旧）+ `CoreRpg-1.22.0.jar`；DB `db-ember-authme-20261002-170254-after-1.22.0.sql` |

### 13.4 还没做（书里剩下的）

- B2.151 可步行断塔地图 `ember_daily_spire_v1`、D15 17×17 / 21×21 首领厅、B2.153 潮蚀告示牌：都是地图工作（需要做模板），不在菜单批。
- 图录：已在 13.5（1.22.1）补上主线首领页；装备图鉴、阶段奖励仍未上架。

### 13.5 内容批 C2：图录 · 主线首领（CoreRpg 1.22.1，2026-10-02 17:06 CST 部署）

| 项 | 落点 | 书 |
|---|---|---|
| 首领图录 `ember_p1_codex`（B2.178） | Q01–Q07 每格：首领名、单人普通版生命 / 普攻间隔、每个技能的名字（与战斗中「蓄力」提示同名）/ 周期 / 形状 / 预警 / 伤害、躲法一句、本人状态 `%corerpg_p1_qXX_state%`；顶部说明组队与挑战版只改生命 / 伤害、预警是火焰轮廓 | §7（各图）、§19.3、§19.5 |
| 入口 | 主菜单「图录」→「主线首领」；冒险页新增「图录 · 主线首领」；帮助页「七张图」可点 | §19.1 |
| 旧图录假入口（D38） | 「怪物图鉴 · 暂未开放」改为主线首领入口；「装备图鉴」「阶段奖励」改灰色「暂未上架」、不可点击；删掉「集齐有奖（称号 / 币 / 天赋点）」承诺 | §19.1、§19.5 |
| 防漂移 | `tools/p1sim/selfcheck.py` 新检查：菜单写的生命 / 普攻 / 间隔 / 技能名 / 周期 / 预警 / 伤害必须等于 MM `EmberQxxBoss` 与 ember-v1-runs.yml，改平衡数值后忘了改图录会 FAIL | §19.2「展示必须是实际数字」 |

| # | 问题 | 裁决 | 理由 |
|---|---|---|---|
| D38 | 旧图录写「集齐有奖：称号 / 币 / 天赋点」，三个子项两个是可点击的「暂未开放」 | 去掉天赋点承诺；未实现的子项灰色不可点；首版图录只放首领说明（纯展示，无奖励、无属性） | §19.5「图鉴先给展示和一次性资源，不能每集齐一页就永久叠一点战斗属性」；§19.1 不做假入口；奖励内容书没定，不自创 |
| D39 | 图录里写哪些数字 | 只写单人普通版（与书 §7 一致）+ 一句「组队生命随人数增加、挑战版生命伤害更高」，不写挑战版具体倍率 | 避免一页塞满；挑战倍率在冒险页「挑战版」里 |

实测（1.22.1）：TrMenu「44 个菜单已加载」；P1Fox（非 OP）从 图录 → 主线首领、帮助 → 七张图、冒险 → 图录 三条路都打开首领页，状态显示「已首通 / 已解锁 / 未解锁（需 Q04 首通）」，unreplacedPAPI=0；gameplay 套件（P1 关）PASS 9/9；DB `db-ember-authme-20261002-171028-after-1.22.1.sql`；P1 active=false。平衡没改，模拟器不需重跑（selfcheck 20/20）。

### 13.6 内容批 C3：§19.2 物品卡与整套比较（CoreRpg 1.23.0 → 1.23.1，2026-10-02 17:14 / 17:16 CST 部署）

| 项 | 落点 | 书 |
|---|---|---|
| 物品卡（B2.179） | `EmberCompare.card`：`T2 烬爆刃｜卓越｜锋刃4%｜+6` → 基础攻击 `42×(1+0.30+0.08+0.04) = 59.6 · 角色等级另加 X（LvN）`（护符：生命 `20 + h×(…)`、防御与承伤倍率，注明强化不改防御）→ 套装条件 → 绑定 · 来源（掉落可分解 / 首通、补发、测试、迁移不可分解） | §19.2 物品说明顺序与示例 |
| 整套比较 | `EmberCompare.diff`：手持未选定的护符时，比较「当前生效」与「选定它之后」的 B / 最大生命 / 防御 / 有效生命 / 套装；套装丢失红字「会取消烬爆」，同族觉醒降档红字；手持刃时显示主手是否生效、当前整套与下一档缺项 | §19.2「比较一整套当前生效状态……必须提前显示」 |
| 入口 | 装备页「主手 · 刃」点击（原来是 uid 调试），「已选护符」提示先比较再选定；uid 行保留（灰色），NBT / DB 调试行只给管理员 | §19.1 |
| 1.23.1 修正 | inspect 先 `refresh` 再显示（切快捷栏后不读旧缓存）；DB 校验进行中显示「正在向数据库校验，2 秒后再点」而不是「没有生效」 | — |

| # | 问题 | 裁决 | 理由 |
|---|---|---|---|
| D40 | 刃怎么比较（主手那把就是生效的那把，手持即装备） | 刃只显示「主手生效中 + 当前整套 + 下一档缺项」，提示切快捷栏再看；只对护符做「选定前 / 后」对比 | 刃没有「选定」这一步，换手即换装；护符是唯一需要提前确认的更换（书例正是高阶异族护符） |
| D41 | 给不给综合评分 | 不给；只列 B、生命、防御、有效生命与套装变化 | §19.2「不给一个号称适用于所有场景的绝对最强评分」 |

实测（1.23.1，P1 关，机器人 P1Fox）：选定 T1 烬爆护符后手持 T1 烬爆刃 →「主手生效中 · B 38 · 生命 105.5 · 烬爆 觉醒I · 下一档 觉醒II 缺：刃 T1→T2；护符 T1→T2」；手持 T0 起始护符 →「最大生命 105.5 → 59.6 · 防御 6 → 2 · 有效生命 121.3 → 62.6 · 套装 烬爆 觉醒I → 未成套（会取消烬爆）」；之后 charm clear 恢复。gameplay 套件 PASS 9/9；mvn test 全过（含 EmberCompareTest 4 项）；DB `db-ember-authme-20261002-171744-after-1.23.1.sql`；P1 active=false。平衡没改。

### 13.7 内容批 C4：图录 · 装备 + 阶段奖励（CoreRpg 1.24.0，2026-10-02 17:28 CST 部署）

| 项 | 落点 | 书 |
|---|---|---|
| 装备图鉴（B2.180） | `EmberCodex`：20 种 = T0 刃 / T0 护符 + 焚烬 / 烬爆 / 炽愈 × 刃 / 护符 × T1–T3。`EmberLoadoutService.refresh` 扫背包时，校验通过（签名 NBT）的 P1 物品第一次出现就登记（PlayerData 计数 `p1_codex_<族>_<部位>_t<阶>@all`），聊天提示「[图录] 登记 … (n/20)」；每次开服后第一次加入时从 `cr_p1_item` 补登本人拥有过的种类（`source<>'admin'`）。升阶后的新阶级算新的一种 | §19.5 |
| 阶段奖励 | 集齐 5 / 10 / 15 / 20 种各领一次余烬币 200 / 400 / 600 / 1000；`/corerpg p1 codex claim`（菜单「领取」）先写计数器 `p1_codex_stage_<n>` 再写主线结算账本行（run `codex`），由同一套 `deliver` 发放 | §19.5「一次性资源」、§22.3 E10 |
| 菜单 | `ember_p1_codex_gear`：20 格状态（PAPI `p1_codex_<key>`）、计数（`p1_codex_count`）、4 档奖励（`p1_codex_stage_<n>`）+ 首领图录 / 返回；图录页「装备图鉴」「阶段奖励」从灰色占位改为打开此页 | §19.1 |

| # | 问题 | 裁决 | 理由 |
|---|---|---|---|
| D42 | 图鉴阶段奖励给什么、给多少（书只说「展示和一次性资源」） | 只给余烬币，总计 2,200（约 7 局普通本收入），按 5 / 10 / 15 / 20 种分四档；不给碎片 / 核心 / 装备 / 属性 / 称号加成 | 一次性、可预期、不进入强化循环之外的新资源线；20 种里有 12 种是 T2 / T3，后两档自然落在 Q04 之后，不改变前期节奏；不在 §2c 模拟里（量级 < 1 天收入） |
| D43 | 什么算「获得」 | 背包里出现过校验通过的本人 P1 物品，或 DB 里本人拥有过的行；管理员 `give`（source=admin）不算；同一种重复获得不重复登记 | §22.5「测试员补发的装备必须标来源，不能混入真实获取节奏」 |

实测（1.24.0，P1 关，P1Fox）：`/corerpg p1 codex` → 8/20（DB 补登：T0 两件 + 若干 T1）；`codex claim` →「结算到账：余烬币 200」，日志 `[P1 codex] P1Fox stage 5 -> coin 200`，再开页显示「已领取」；图录 → 装备图鉴打开，unreplacedPAPI=0；TrMenu 45 个菜单；gameplay PASS 9/9；mvn test 全过（含 EmberCodexTest 3 项）；DB `db-ember-authme-20261002-173136-after-1.24.0.sql`；P1 active=false。


### 13.8 内容批 C5：B2.151 可步行断塔 `ember_daily_spire_v1`（CoreRpg 1.25.0，2026-10-02 17:39 CST 部署）

| 项 | 落点 | 书 |
|---|---|---|
| 白盒高度图 | `p1/map/P1MapLayout`（纯 Java，无 Bukkit）：房间净范围 + 7 宽通道体积，每格脚底 F 与净高；不同 F 的通道段每格升 1、居中、两端 ≥ 3 格平地，升格标记为台阶；拐点因段两端各延半宽而得到 7×7 平台。`P1MapLayoutTest`：无重叠冲突、相邻格高差 ≤ 1、全部格从入场点 (0,5) 不跳可达、书中节点 F、通道宽 7、四道门面都在空腔内 | 第 15 章 §2–§4，第 10 章 B（M02 楼梯可步行） |
| 建图 | `p1/map/P1MapBuilder` + `/corerpg spirebuild v1`：复制旧断塔图为工作世界，按 x 列每 tick 重写 x −48..95 × z −32..191 × y 40..160（地面石 / 土 / 草，抬高地板下实心，升格放石砖台阶，墙 2 厚到 F+净高，有顶的通道 / R3 / E 有天花板与梁，R2 / RB 外缘 1 实心 + 1 铁栏，地面与墙内嵌萤石，R1 西侧直径 9 高 22 的封顶塔筒，RB 北侧宽 23 高 20 的冠冕，R3 西墙开窗），删实体，导出到 `plugins/DungeonPlus/map/ember_daily_spire_v1`（发布产物，不进 git） | 第 15 章 §2、§5 |
| Q05 切换 | runs yml q05：template / map_version 改 v1，spawn (0,64,5)，三房触发区 = 净范围 × [F−1, F+4]，P1–P6 用书坐标，门 G2 / G3 / GB = 书门面 7×6，首领 (0,80,146)、区域 = RB 全厅 33×33，事件锚 E (66,72,63)；删除 links / rails / clear；DP `ember_daily_spire_v1: {EmberQ05: 1}`，EmberQ05 option setmap / setspawn / teleport 改 v1 与 (0,64,5)；旧日常 EmberDailySpire 仍用旧图 | 第 15 章 §3、§6、§8 |
| 首领等待 | boss 新键 `wait_in_area`：最后一房清完后，首领在有人走进 Boss 区时才现身（最迟 20 秒）；提示改为「在前方首领厅等候，走进大厅即现身」。只 Q05 开启 | 第 10 章 I「首领不在玩家到达前开打」 |

| # | 问题 | 裁决 | 理由 |
|---|---|---|---|
| D44 | G1（R0→R1）要不要运行时门 | 不放（书：G1 开局直接打开）；三道运行时门 = G2 / G3 / GB，分别在 r1 / r2 / r3 清完时打开 | 第 15 章 §4 |
| D45 | P7 / P8 补充点放哪 | R1 (−12,64,33) (12,64,33)；R2 (32,72,58) (48,72,58)；R3 (−14,80,94) (6,80,94)：同房净范围内、离门面 ≥ 4 格、不在 6 个书点上 | 第 10 章 I「P7/P8 补充点」，现有刷怪逻辑每房需要 8 点 |
| D46 | 书 §5 的装饰件（R0 远景塔冠、R1 塔筒、RB 冠冕、R3 窗）做到什么程度 | 做成实心 / 封顶外形，全部在可走范围外；不放告示牌、盔甲架、NPC；门前后 4 格无装饰 | 只要形体与遮挡；装饰细节不影响路径与战斗，留给地图美术迭代 |
| D47 | 首领 1.5 秒后在远处大厅现身，会不会在玩家还在 R3 时被引出 | 加 `wait_in_area`（有人进 Boss 区才现身，最迟 20 秒）；R3→RB 走廊 18 格 | 新图 R3 与 RB 距离 25 格，旧逻辑会让首领在厅里空转 / 追出 |

实测（1.25.0，2026-10-02 17:44–17:46 CST，P1 运行期临时开启后恢复 follow）：`/corerpg spirebuild v1` 7 秒建完（403,596 方块，删 38 实体）；P1Fox 进 Q05（创造模式以免被打，管理员 kill 代替战斗）：入场 (0,64,5) 脚下石砖 → R1「第一层环院 · 敌人 6」→ 清空开门 → 只用前进、禁跳沿 C12 台阶 y 64 → 64.5 → 67.5 → 72 进 R2「第二层外台 · 敌人 7」→ C23 台阶 72 → 75 → 78.5 → 80，西拐过 G3 → R3「第三层横廊 · 敌人 7（B）」→ 过 GB → RB「断塔斧卫 在前方首领厅等候」→ 进厅后首领在 (0,80,146) 现身、两技能预警正常；管理员 kill 首领被判「首领非玩家击杀」异常终止、退还体力（防作弊仍有效）。gameplay PASS 9/9；mvn test 全过（含 P1MapLayoutTest 5 项）；P1 active=false。

### 13.9 内容批 C6：D15 七张主线图全部换成书白盒（CoreRpg 1.26.0，2026-10-02 17:53 CST 部署）

| 项 | 落点 | 书 |
|---|---|---|
| 书表解析 | `tools/p1map/book.py`：从第 11–17 章读入场点 / 首领点、房间净范围（x / z / F / 净高）、通道节点、门面中心与固定平面、P1–P6、E 中心；`gen.py` 生成 `CoreRpg/src/main/resources/p1-book-maps.yml`（建图输入）并改写 runs yml 与 DP 定义 | 第 11–17 章 §2–§8 |
| 通用白盒 | `P1MapLayout.fromBook`：房间按书净范围，通道 7×6 带顶，只在端点与拐角放 7×7 平台（同向相接的段共用节点，Q03 纯坡段的 3 格平地由前后段提供）；RB 北墙外 23 宽、F+16 的门楼背景；Q02 / Q03 房间带顶（甬道 / 地窖），其余露天；Q05 的 R2 / RB 外缘护栏。`P1MapLayoutTest.allBookMapsWalkable`：七张图无冲突、高差 ≤ 1、从入场点全部可达，Q03 64→60→56 有台阶，通用 Q05 与手工 spire v1 每格一致 | 第 10 章 B、M02 |
| 建图命令 | `/corerpg p1 mapbuild <q01..q07>`（Q05 = spire v1）→ `plugins/DungeonPlus/map/<旧模板>_v1`；七张图各 7–10 秒 | — |
| runs yml | Q01–Q04、Q06、Q07：template / map_version 改 `_v1@d15-2026-10-02`；spawn = 书入场点，spread 左右 2 格；三房 label = 书房名、触发区 = 净范围 × [F−1, F+4]、P1–P6 = 书坐标 + P7 / P8（D48）、门 = G2 / G3 / GB；首领 at = 书首领点、area = RB 全厅、`wait_in_area`；事件 after r2、锚 = 书 E 中心、area = E 净范围；Q03 首领召唤点 = 书「首领 x±8、z−4」；删除全部 rails / clear / links（旧模板的坑、告示牌、楼梯问题随旧模板一起离开主线） | 第 11–17 章 §3、§4、§6–§8 |
| DP | `ember_daily_v1 … ember_daily_rail_v1` 各挂对应 EmberQ0x；EmberQ0x option setmap / setspawn / teleport 改书入场点；旧日常 EmberDaily* 仍用旧模板（不动） | — |

| # | 问题 | 裁决 | 理由 |
|---|---|---|---|
| D48 | P7 / P8 怎么取 | 生成器规则：同房净范围内、离墙 ≥ 3、离书点 ≥ 3、离门面 ≥ 4，左右各一个，取最靠房间中线 z 的格 | 第 10 章 I 只要求补充点在本房；现有刷怪每房用 8 点（Q05 手工点同规则） |
| D49 | 事件房 E 何时生成事件 | 全部改为 r2 清完（旧 Q02 / Q04 是 r1） | 书：E 只从 R2 进入并原路返回 |
| D50 | §5 每图的特色装饰（Q01 掩体 / 石柱 / 兵营屋壳、Q04 泵房形体……） | 本批只做路径、墙、顶、灯与 RB 背景门楼；地面掩体与特色外形未做，记 backlog B2.181 | 不影响路径与战斗验收；先把首领厅、门、刷怪点与可步行性按书落地 |
| D51 | 平衡 | 数值、房间数、怪数都没改；房间变大、走廊变长（每房多走约 10–20 格），模拟器（§2c）没重跑 | 首领厅从 11×11 变成书的 29–41 宽，首领技能距离不变，更容易走位；需要实测确认 |

实测（1.26.0，2026-10-02 17:55–18:05 CST，P1 运行期临时开启后恢复 follow；P1Fox 创造模式、管理员 kill 代替战斗、只前进禁跳、按书通道节点走）：Q01–Q07 七张图全部走通——每张 3 房「敌人 N（变体 A/B）→ 已清空 · 门已打开」，Q03 下台阶 64 → 60.9 → 60 → 56.9 → 56，Q02 / Q04 / Q03 的 E 事件（宝藏怪）在 E 生成，首领均为「在前方首领厅等候」→ 进厅现身并开始预警；管理员 kill 首领都被判「首领非玩家击杀」终止并退还体力。日志无 anomaly / stuck / 无法生成。mvn test 全过（P1MapLayoutTest 6 项）；gameplay PASS 9/9；DB `db-ember-authme-20261002-180753-after-1.26.0.sql`；P1 active=false。

### 13.10 内容批 C7：§9 安全回退点 + 全息锚点、§20.5 断线重连（CoreRpg 1.27.0，2026-10-02 18:12 CST 部署）

| 项 | 落点 | 书 |
|---|---|---|
| 数据 | `tools/p1map/book.py` 读各图 §9「安全回退点」「全息预留」；`gen.py safe` 写进 runs yml 每图 `safe: {r0..r3, rb}`、`holo: {entry, event, exit}`；`validate()` 检查每个安全点在本房触发区内、rb 在首领区内 | 第 11–17 章 §9 |
| 全息 | 实例内隐形标记盔甲架（模板里没有任何实体）：H_ENTRY 开局两行（副本名 + 一句目标）；H_EVENT 只在有额外事件时出现（「额外事件：宝藏怪 / 奖励精英 / 额外宝箱」），事件完成或首领死亡时撤掉；H_EXIT 首领死亡后出现（「已通关 · 结算已发放 · /dp leave 离开」），留到实例关闭 | 第 10 章 J、第 11–17 章 §9 |
| 断线重连 | 已确认参战的玩家在开着的局里断线时记时间；120 秒内回到实例 → 传到最近已清房的安全点（未清任何房 = R0；首领已现身或已死 = RB 安全点），提示离线秒数，生命保持断线前（D11 原有）；超过 120 秒 → 提示并 `dp leave`，按离开副本处理；局已结束 → 原有 B2.139 送出实例 | §20.5「技术断线可在 120 秒内回到最近已清房安全点……不随机落在首领脚下」 |

| # | 问题 | 裁决 | 理由 |
|---|---|---|---|
| D52 | 全息用什么实现 | 原版标记盔甲架（不依赖 HolographicDisplays，实例卸载即消失） | 只在实例世界、生命周期跟局走；不写入模板 |
| D53 | 超过 120 秒回来怎么办 | 不再放回本局：送出实例，等同离开副本（已开战不退体力） | 书只允许 120 秒内恢复；不能反复重连刷新回复或奖励 |
| D54 | 已清 r3、首领未现身（`wait_in_area`）时断线 | 回 R3 安全点，不直接放进 RB（否则进厅即触发首领） | 书「回到最近已清房安全点」；首领战进行中才用 RB |

实测（1.27.0，18:15–18:19 CST，P1 运行期临时开启后恢复 follow，P1Fox，Q01）：开局 (0,67,5) / (0,66.7,5) 两行全息「§6灰烬庭院 / 清空三处房间开门 · 击败首领后统一结算」；R1 开战未清时断线 12.7 秒重连 →「断线重连：已回到最近已清房的安全点（离线 12 秒）」落在 R0 (0,64,3)；清 R1 后断线 10.7 秒重连 → R1 安全点 (0,64,21)；清 R2 后宝藏怪在 E 生成，(48,67,38) 出现「额外事件：宝藏怪」。超时路径（> 120 秒）与 H_EXIT 没实测（需要真打首领），代码路径简单。gameplay PASS 9/9；mvn test 全过；DB `db-ember-authme-20261002-182200-after-1.27.0.sql`；P1 active=false。
（实测中一条写错的 `kill @e[type=!player,type=!armor_stand]` 选择器在机器人误留枢纽时杀了枢纽 5 个 NPC 碰撞箱村民，已用 `/corerpg hubnpc` 与重启恢复；之后清怪只用 `r≤20` 且人在实例里。）

### 13.11 内容批 C8：B2.181 书 §5 各图形体（CoreRpg 1.28.0，2026-10-02 18:24 CST 部署）

| 图 | 做了（`tools/p1map/gen.py` DECOR → `p1-book-maps.yml` decor，`/corerpg p1 mapbuild` 重建） |
|---|---|
| Q01 | R0 门顶深色木横梁 9 宽 2 厚到 F+6、入口两侧熄灭火盆（炼药锅 + 地下萤石）；R1 两处掩体按书坐标 x=−10..−8 / 8..10, z=23..25 高 2；R2 东墙外兵营屋壳 9×6×3（避开 E 口）；R3 四角外 3×3 石柱，两根到 F+10、两根断（裂石砖、矮） |
| Q02 | R0 入口横梁 11 宽、左右直径 3 炉管贴外墙；R2 北墙外侧炉壳 9×5×4，墙里 3×3 炉口 = 玻璃后萤石；R3 西墙外两只错位沉渣罐（直径 5 高 8，封顶）；RB 北墙外主炉 15×18×9 |
| Q04 | R1 双渠平台：两条水渠 x=−25..−21 / 21..25, z=22..46，水面 y62；平台外缘改为 1 实心 + 1 铁栏（看得见水渠，跌不下去）；R2 北墙外泵机外壳 15×10×5 + 两根直径 3 管；RB 闸门立面 25×18 + 六道竖肋 |
| Q06 | R1 两侧墙顶雪块；R2 改带顶并加双坡屋顶（屋脊 F+13，云杉木板）；R3 北墙外垒盾墙 11×8（通道处保持开口）；RB 北哨楼 17×9×22 + 两个方形塔肩 |
| Q07 | R1 西侧吊装柱 5×5 + 13 格深色木横臂（下缘 F+10）；R2 远端 11×6 封闭货门立面；R3 北墙外废压机 9×12×5；RB 四角 4×4 支柱高 20 + 两向大吊架（梁下 F+17，高于 F+16） |

| # | 问题 | 裁决 | 理由 |
|---|---|---|---|
| D55 | Q03 的 §5 形体（碑龛、棺匣、观察窗、石棺台） | 本批不做（1.29.0 已做，见 13.13） | Q03 房间在 F 56–60，墙外是地下实心层，外部形体看不见；要做成墙内浅龛，留给下一批（B2.181 保留 Q03 一项） |
| D56 | 室内掩体 / 横梁会不会挡路或盖住刷怪点 | 生成器检查：书刷怪点、P7/P8、安全点、入场点、首领点、事件锚都不在任何方块盒的站立高度内；室内只有 Q01 两处书定掩体是地面障碍，其余在头顶以上 | 书 §5 只允许这两处地面掩体 |
| D57 | Q04 R1 外缘改护栏 | 整房外缘 1 实心 + 1 铁栏（与 Q05 R2 / RB 同做法），通道口仍是全高墙 | 书「与可走地面之间必须有实心边梁和连续护栏」 |

实测（1.28.0，18:26–18:30 CST，P1 运行期临时开启后恢复 follow）：五张图重建各 7–9 秒；Q07 按书路线禁跳走完三房到首领厅（首领等候 → 现身）；Q04 方块抽查：水渠 (−23,62,34) / (23,62,34) / (−21,62,26) 为水，R1 外缘 (−19,64,26) 石砖 + (−19,65,26) 铁栏、(19,65,26) 铁栏，外圈 (−20,64,26) 实心、其上空气；Q06 R1 墙顶 (−19,73,32) 雪块、R2 屋脊 (40,77,64) / 檐 (27,73,64) 云杉木板，室内 (40,70,64) 净空。gameplay PASS 9/9；mvn test 全过；DB `db-ember-authme-20261002-183359-after-1.28.0.sql`；P1 active=false。

### 13.12 地图模板备份（2026-10-02 18:37 CST）

七张 `ember_daily*_v1` DP 模板（1.28.0 建，含 B2.181 形体）打包为 `ember-p1-maps-v1-2026-10-02.zip`（8,056,349 字节，36 个文件，内含 SHA256SUMS.txt；zip sha256 `a3ad0448870a8dfde34263a2aae4af4076f620145fd43c9d53384c1f11751e80`），上传到 GitHub Release **maps-v1-2026-10-02**（https://github.com/Picadoo/ember-server/releases/tag/maps-v1-2026-10-02 ，目标 main）。重新下载校验一致。恢复：解压到仓库根目录（路径以 `plugins/DungeonPlus/map/` 开头），或服内 `/corerpg p1 mapbuild q01..q07` 重建。之后若重建模板，需另发新的 maps release。

1.29.0 重建 Q03 后（18:46 CST）：同一个 Release 加传附加资产 `ember_daily_crypt_v1-1.29.0.zip`（1,557,957 字节，sha256 `fcc70124a2089c2b47d91c24ae720536311383dd2fa49e0063f28b6f31e695bd`），恢复时用它覆盖总包里的旧 crypt。

### 13.13 内容批 C9：Q03 墙内形体 + 踏步冷却（CoreRpg 1.29.0，2026-10-02 18:41 CST 部署）

| 项 | 做了（Q03 全部做成墙层 / 顶层里的方块盒 kind 3，D55 的「墙内浅龛」） | 书 |
|---|---|---|
| R1 碑龛 + 拱顶 | 四面墙每 5 格一个 1 宽 × 3 高 × 1 深的龛（F+1..F+3），龛底錾制石砖当石碑；西墙避开 C12 口、南墙避开 C01 口，共 14 个。顶改成阶梯拱：两侧 3 格仍是 F+8，向中间三级抬到 F+11，两端补山墙，拱顶中线 5 盏灯 | ch.13 §5 R1「每隔 5 格一个碑龛，拱顶最高 F+11，最低净空 F+8」「墙龛只给 1 格视觉深度」 |
| R2 棺匣 | 西墙里 4 只封闭棺匣 3×2×2（云杉木板，嵌在墙层后面，前面 1 格龛口能看见），E 口 z57..63 两侧各 2 只，E 口不动 | §5 R2 |
| R3 分节拱顶 + 观察窗 | 顶抬高 1 格，每 5 格一道深色橡木横肋（F+8），每节中间一盏灯；东墙两处 3×3 玻璃观察窗，窗外是更低的空墓库（地面 F−7，7×17，地面萤石照明，无入口） | §5 R3 |
| RB 石棺台 | 北墙凹进一个 13 宽凹室（在首领区之外），里面石棺台宽 11 × 高 5 × 深 4（石砖，顶面錾制石砖）；厅中央不放；四角各一个灯龛（龛底萤石） | §5 RB |
| 踏步冷却（G05） | `FlexSkillService`：冷却改用单调时钟，下线时只清掉已到期的冷却 → 退出重进不再刷新余烬踏步 14 秒冷却（重启服务器仍会清空） | §20.6「不因系统时钟回调或重连清零」 |

| # | 问题 | 裁决 | 理由 |
|---|---|---|---|
| D58 | 「右侧外墙」是哪一侧 | 取 +x（东）墙，与其他图「东墙外」的书坐标一致 | 书坐标 x 向东；书没给朝向，取和其他图一致的写法 |
| D59 | R2「四排棺匣」 | 4 只、一层，离地 1 格，3 长（沿墙）× 2 深 × 2 高 | 房高 8，两层会顶到灯；书没给层数 |

实测（1.29.0，18:43–18:45 CST，P1 运行期临时开启后恢复 follow）：Q03 重建 8 秒；P1Fox 按书路线禁跳走完 R1 → R2 → R3 → 首领厅（首领等候 → 现身），23 个抽查方块全部到位（碑龛空气 + 錾制石碑、拱顶 F+11 灯、棺匣木板、横肋、观察窗玻璃、低墓库萤石、石棺台、四角灯龛）。踏步冷却只做了代码修改，没做机器人实测。地图模板备份：Q03 重建后的 crypt 单独作为附加资产传到同一个 Release（见 13.12）。

### 13.14 数值包 balance_version 2：§2c 方案 B（CoreRpg 1.30.0，2026-10-02 19:41 CST）

| 项 | 落点 |
|---|---|
| 覆盖值 | `tools/p1sim/proposal_2c.json` 69 项原样写入两份 `ember-v1-runs.yml`（小怪 hp/atk、首领 hp/atk、首领技能与追加段 dmg）和 `EmberP1Main.yml` 对应 Health / Damage；脚本 `tools/p1sim/apply_2c.py`（只改数字，保留注释，可重复执行） |
| 每图系数 | Q03 0.95、Q04 0.75、Q05 0.65、Q06 0.70、Q07 0.65（生命与伤害同乘，取整）；Q01 / Q02、宝藏怪、挑战版（自己的覆盖值）不变 |
| 版本 | runs yml 新增 `balance_version: 2`；每局 `cr_p1_run.rule_version` 记为 `g04-1/b2`（旧局是 `g04-1`）。`content_version` 仍是 v1，已拿过的首通不重置（书 §23.2「仅重建地图不能重置奖励」同理） |
| 图录 | `ember_p1_codex.yml` Q03–Q07 首领生命 / 普攻 / 技能伤害改成新值；`selfcheck.py` 0 failed |
| 测试 | `EmberRunRulesTest` 断言改为新值，加 balance_version / rule_version 断言；`EmberRunMobsTest`（MM = runs）通过 |

| # | 问题 | 裁决 | 理由 |
|---|---|---|---|
| D60 | §2c 方案 B 是否应用 | **应用**，数值包升到 balance_version 2（用户授权 2026-10-02 19:37：「全部自行决定，不再询问」） | 模拟器：dodge 0.5 时 Q07 首通第 8–9 天（P90 11–12），各图最难已开放阶段通关率 35–56%；旧数 Q04 通关率 5%、Q07 60 天内只有 20% 玩家到达。书 §7 的初测值保留为历史基线，现行数以 runs yml 为准 |

### 13.15 P1 默认模式（CoreRpg 1.31.0 → 1.31.1，2026-10-02 19:46 / 19:55 CST 部署）

用户 2026-10-02 19:37：全部自行决定；服内**没有旧玩家**，不需要迁移（书 §21 的迁移表不适用，记为不做）。

| 项 | 落点 |
|---|---|
| 总开关 | 两份 `ember-v1.yml` `enabled: true`；scope 仍是 `dungeon_EmberQ0*`（Q01–Q07 主线本实例） |
| 进服 | `ember-v1.yml join_message` 替换 `config.yml` 旧路线提示（CoreRpgPlugin.onJoin，只在 P1 生效时）；起步包（T0 刃 + T0 护符）与 5 瓶回复药照旧由 `EmberRunService.starterKit` / `EmberSupplyService` 在进服 3 秒后发 |
| 旧主线 | `QuestService.onJoin` 在 P1 生效时不再自动开第 1 章；右键灰烛 → 一句主线本说明 + 打开冒险页 |
| 主菜单 | `ember_hub.yml` 改为书 §19.1：冒险 · 主线本 / 装备 / 工坊 / 图录 / 轻技 / 仓库 / 补给·生活 / 帮助 / 邮寄 / 好友 / 盟约 / 设置 / 关闭（13 格）；旧主界面整份移到 `ember_hub_legacy.yml`（不绑定命令） |
| 枢纽 NPC | `hub_npcs.yml`：烬砧 → `ember_p1_forge`；门吏·灰钥 → `ember_p1_adventure`；余晶 → `ember_help`（附魔在主线本不生效）；灰粮仍是补给·生活 |
| 回血 | `StatService`：复活（任何非 P1 世界）和在枢纽进服后，刷新最大生命后补满（D65） |
| 测试 | 套件「Hub menu」改查新主菜单 6 项（冒险 · 主线本 / 装备 · 主线本 / 工坊 / 仓库 / 设置 / 帮助），并要求旧入口（团本 / 深渊 / 精英试炼 / 日常 · 余烬窟 / 挂机庭 / 天赋）**不出现**；其余 8 项（熔炉 / 附魔 / 钓鱼 / 图腾 / 盾 / 合成 / 世界规则）是枢纽里的通用插件检查，不受 P1 影响，原样保留 |

| # | 问题 | 裁决 | 理由 |
|---|---|---|---|
| D61 | P1 在哪些世界生效 | 战斗规则只在 Q01–Q07 实例（scope 不变）；枢纽不进 scope；模式本身（起步包、菜单、进服提示）对所有人生效 | 书 §19.4「城内可正常补满……自然回血开关必须按实例隔离」；枢纽没有战斗，进 scope 只会让 D01 关掉城内回血。§20.1 的「隔离测试模式」在用户批准默认化后改为「隔离作用域」 |
| D62 | 旧主线（灰烛 第 1–N 章，引向挂机 / 日常 / 誓约 / 天赋） | P1 生效时不自动开始；灰烛改指冒险页；进服提示换成 P1 路线 | 书 §19.1「玩家不需要手打命令」「主入口精简」；旧主线的每一步都是旧来源（源表 A06–A11 等已在 P1 禁用） |
| D63 | 旧系统入口 | 从主菜单拿掉：挂机庭、日常窟、周常、精英、深渊、团本、灾厄、誓约、天赋、使魔、旧技能、旧强化 / 镶嵌 / 附魔 / 套装 / 分解 / 锻炉、角色（战力评分）、天梯、竞技、寄售、悬赏 / 活跃 / 签到、战令 / 勋阶 / 商城 / 月卡 / 礼包 / 外观。命令和世界都不删（可回滚），菜单文件整份留在 `ember_hub_legacy.yml` | 书 §19.1 一级菜单五项、「未完成系统不以可点击假入口充数」；§19.2 不给综合战力分；§19.5 交易首版不开；§23.2 准备 / 首批阶段「暂不做」深渊导演、团本、交易市场；签到 / 活跃 / 悬赏发币没进模拟器经济（§19.4 收入只来自通关） |
| D64 | 枢纽 NPC 指向 | 三个 NPC 改指 P1 页（见上表），名字和位置不动 | 同 D63；NPC 是场景交互入口（§19.1「通过现有菜单或场景交互进入」） |
| D65 | 新角色第一次倒下复活后只有 1/7 血条（原版 20 → 刷新把上限抬到 140，饱食 17 不回血） | 复活与枢纽进服在刷新最大生命后补满 | §19.4「城内可正常补满，不收费制造往返负担」 |

实测（1.31.0 / 1.31.1，19:47–19:58 CST；套件 9/9，Hub menu 13 格、旧入口 0）：全新角色 P1Rookie 注册进服 → 收到 P1 进服提示、T0 刃 + T0 护符 + 5 瓶回复药；`/ember` 13 格新主菜单，点「冒险 · 主线本」→ 冒险页 → 点 Q01 → 建实例开局；生存模式 T0 装备清掉 R1（4 只）、R2（4 只），R3 未喝药倒下（机器人不会喝药），当日首倒退药规则正常；枢纽 NPC 4 个都在；重进服 HP 20/20。

### 13.16 P2-1 每周挑战轮换（CoreRpg 1.32.0 → 1.32.1，2026-10-02 20:03 / 20:09 CST 部署）

设计：`docs/design/design-ember-v1.1-P2-draft.md` §3。模型：`tools/p1sim/p2econ.py`。

| 项 | 落点 |
|---|---|
| 参数源 | 两份 `ember-v1-runs.yml` 的 `rotation.bonus_marks: 1`、`weekly_cap: 3`，`balance_version: 3`（rule_version `g04-1/b3`） |
| 规则 | `EmberRunRules.weekIndex / featuredChallenge / rotationWeekKey`（周一 00:00 Asia/Shanghai，七图按 runs 顺序轮换） |
| 结算 | `EmberRunService.settleFor`：挑战局 + 精选图 + 本周计数 < cap → 账本行 `rot_mark`（MARK，本局阶）；计数器 `p2_rotation` 只在账本行新建时 +1 |
| 显示 | PAPI `p1_featured`、`p1_featured_key`；`ember_p1_challenge.yml`、`ember_p1_adventure.yml` lore；`/corerpg p1 run` |
| 测试钩子 | `/corerpg p1 runs weaken <玩家>`（管理员；活怪 → 1 HP，玩家击杀、结算照常）；`tools/p1map/chal-smoke.sh` |
| 测试 | `EmberRunRulesTest.weeklyRotationIsMondayBasedAndCyclesAllSeven_P2_1`，runs 解析断言 balance_version 3 / rotation 1·3；套件 9/9 |

| # | 问题 | 裁决 | 理由 |
|---|---|---|---|
| D66 | P2 第一个工作包 | 每周挑战轮换：精选图每周前 3 次挑战通关各 +1 枚本局阶印记，不发币、不发物品 | p2econ：8 周 T3 印记 +6%（157 对 148），币不增，成套 / 强化时间不变；书 §18.2 让玩家刷不同的图。§23.3 四步：参数源、模型、设计、版本 |
| D67 | Q01–Q07 困难模式 / 第 4 套 / 新成色档 | 不做 | 挑战版就是困难版；递进交给 P2-2 有限层表（书 §18.3、§23.3） |
| D68 | P1 团本 | 推迟 | 书 §18.4：需五人模型和单独工作包 |
| D69 | P1 交易 | 只定规则（P2-3），没有模型证明就不开 | 书 §19.5 |

实测（1.32.0，20:04 CST）：P1Fox 打 Q01 挑战版（本周精选），admin weaken 后玩家逐房击杀，结算到账「本周精选挑战 灰烬庭院：额外 T3 锻造印记 +1（本周 1/3）」。1.32.1 重启后 `/corerpg p1 run` 显示「加成剩 2/3」，挑战菜单 lore 有本周精选。

### 13.17 P2-2 深渊 · 余烬层（CoreRpg 1.33.0 → 1.33.1，2026-10-02 20:25 / 20:37 CST 部署）

设计：`docs/design/design-ember-v1.1-P2-draft.md` §4。模型：`tools/p1sim/p2econ.py --abyss`（输出 `out-p2econ-abyss-d050.md` / `-d065.md`）。

| 项 | 落点 |
|---|---|
| 参数源 | 两份 `ember-v1-runs.yml` 的 `abyss.requires: q07` 和 `abyss.tiers`（10 行：hp / dmg / quality / fee），`balance_version: 4`；validate 检查上限（≤10 层、hp ≤1.6、dmg ≤1.3）和单调 |
| 段与数值 | `EmberRunMaps.abyssMap(seed)` 从七图里抽；`Challenge.scaled(tier)` 给导演用（`EmberRunDirector` 的 `ch`）；`EmberRunSession.abyss` / `fee` 落盘 |
| 入场 | `EmberRunService.tryEnterAbyss` → 公共的 `enter(...)`；资格：首通 Q07、层 ≤ 最高 + 1、币够 |
| 费用 | 账本行 `cost_coin`（reserved → committed / released）；`releaseFee` 和体力一起退；重启时写 `refund_coin` 待领行 |
| 结算 | `settleFor`：本层成色表；不给精选加成；`p2_abyss_best` 记新纪录并开放下一层 |
| 菜单 | `ember_p1_abyss.yml`（新）、`ember_hub.yml`「深渊 · 余烬层」、`ember_p1_adventure.yml` A、`ember_p1_codex.yml` Y（表格）；PAPI `p1_abyss_state` / `p1_abyss_best` / `p1_abyss_t1..10` |
| 测试 | `EmberRunRulesTest.abyssTableIsCappedMonotonicAndScalesTheChallenge_P2_2`、`abyssTableRejectsAnUncappedRow_P2_2`；套件 Hub 检查要求「深渊 · 余烬层」出现，旧的「深渊」仍然不能出现（9/9） |
| 冒烟 | `tools/p1map/abyss-smoke.sh`（admin weaken + 玩家击杀） |

| # | 问题 | 裁决 | 理由 |
|---|---|---|---|
| D70 | P2-2 深渊做法 | 段 = 种子抽的已验收主线图，挑战值 × 10 层封顶表，每段单独付费和结算 | 书 §18.3 全部条款；不新造导演、房间或肉鸽能力 |
| D71 | p2econ 后期不互换强化 | 改为 Q07 后默认互换（书 §6.3） | 不互换时高成色掉落永远换不上，结论失真 |
| D72 | 深渊层费 | 0～540 币 / 段，退还规则同体力 | p2econ：第 8 周币中位 2.6 万 → 约 1.5 千，两件极品 20% → 32%（dodge 0.65：27% → 60%），B 不变 |
| D73 | P2-3 开市 | 否决 | p2econ `--trade`：两件极品 20% → 52%，成套提前超过 1 周 |
| D74 | 团本 | 只写规格（P2 草案 §5b） | 书 §18.4 要五人模型 |

实测（1.33.0，20:27–20:31 CST）：P1Fox 第 1 层（抽到 Q01），玩家逐房击杀，结算「新纪录，开放第 2 层」，没有精选加成；第 2 层（抽到 Q03）开局后重启服务器 → 自动中止 → 进服到账「体力 30、余烬币 60」。1.33.1 只换了表的数值（v2），`/corerpg p1 abyss` 显示第 10 层「生命 ×1.45 伤害 ×1.18 · 极品 12% · 费 540」。

### 13.18 P2-5 团本 锈轨矿道·团（CoreRpg 1.34.0 → 1.34.1，2026-10-02 20:58 / 21:07 CST 部署）

| 项 | 内容 |
|---|---|
| 参数源 | `ember-v1-runs.yml` `raids.r01`（两份副本）；balance_version 5（规则 g04-1/b5） |
| 模型 | `tools/p1sim/p1party.py`，结果 `out-p1party-r01-w2.md` / `-w4.md` |
| 代码 | EmberRunMaps 读 raids，加人数、费用、周上限和生命 / 伤害斜率；EmberRunService 管入口门槛、周计数和结算（额外 T3 掉落 + 印记），团本里怪物伤害乘 dmgFactor |
| 副本 | DungeonPlus `EmberQ0R1`（从 EmberQ07 复制），队伍 3～5 人 |
| 菜单 | 冒险页 G、主菜单 G、图录 G |
| 测试 | `raidIsSeparateFromTheMainLineAndScalesWithTheParty_P2_5`；套件 Hub 检查 |
| 冒烟 | `tools/p1map/raid-smoke.sh LEADER "成员…"` |
| 1.34.1 | 团本开局提示合成一行；新手：起步回复药放进快捷栏右侧，开局告诉玩家药在第几格，生命低于 40% 且药不在冷却时，动作栏提示怎么喝 |

| # | 问题 | 裁决 | 理由 |
|---|---|---|---|
| D75 | 团本做法 | 复用 Q07 地图，单开 DP 副本，3～5 人，50 体力，每周 3 次，结算 +1 次 T3 掉落 +1 个 T3 印记，没有专属掉落 | 书 §18.4：只复用首领招式，不加专属掉落，周上限写进参数源 |
| D76 | 团本人数缩放 | 每人生命斜率 0.85、伤害斜率 0.20 | p1party：没有职业的队伍分摊伤害，只缩放生命时 5 人比 3 人容易得多；选数后第 4 周的池子 3～5 人通关率 58～85% |

实测（21:00–21:09 CST）：5 个机器人通关（用了 weaken），5 人都结算，周计数 1/3；断线 14 秒后回到安全点；重启后退还 50 体力。TTK 对比没做（用了 weaken）。

### 13.19 P2-6 团本 R02 霜封哨所·团 + 团本合计周上限（CoreRpg 1.35.0 → 1.35.1，2026-10-02 21:25 / 21:31 CST 部署）

| 项 | 内容 |
|---|---|
| 参数源 | `raids.r02`，r01 / r02 都加 `cap_group: raid`；balance_version 6 |
| 模型 | `p1party.py --raid r02`（`out-p1party-r02-w2.md` / `-w4.md`）；`p2econ.py --raid`（`out-p2econ-raid-d050.md`） |
| 代码 | EmberRunMaps `capGroup`；EmberRunService `capKey`（周计数和菜单标签）；EmberRunDirector 顶层招式按 `below` 分阶段，进入第二阶段时提示；1.35.1 倒下的人重新进入实例后放回观战 |
| 副本 / 菜单 | DP `EmberQ0R2`（从 EmberQ06 复制，3～5 人）；冒险页 R、主菜单 h、图录 J；套件 Hub 检查要求「霜封哨所·团」 |
| 测试 | `secondRaidSharesTheWeeklyCapAndGatesItsSecondPhase_P2_6` |

| # | 问题 | 裁决 | 理由 |
|---|---|---|---|
| D77 | 第二个团本 | Q06 地图，首领只用已有招式，靠半血后的第二阶段施压，不加增援 | 书 §18.4；和 R01（冲撞 + 增援）打法不同；首领生命 13000 时第 4 周 3～5 人通关率 60～78%，和 R01 同档 |
| D78 | 团本周上限 | 两本合计每周 3 次 | p2econ：团本不让奖励膨胀（第 8 周两件极品 22% → 17%，各 3 次是 25%），合计上限更保守，团本保持每周活动的定位 |

### 13.20 P2-7 每日委托（CoreRpg 1.36.0，2026-10-02 21:38 CST 部署）

| 项 | 内容 |
|---|---|
| 参数源 | `ember-v1.yml` `bounty.daily: [{clears: 1, coin: 30}, {clears: 3, coin: 60, shard: 6}]`（两份）；balance_version 7 |
| 代码 | EmberRunRules `BountyTier` / `bountyTiers` / `bountyGrants` / `bountyLine`；EmberRunService.settleFor 只在本局还没有基础结算行时计数（计数器 `p2_bounty@<体力日>`），奖励记作账本行 `bounty_*_<n>` |
| 显示 | 冒险页 W、`/corerpg p1 run`、结算后的提示；PAPI `p1_bounty` |
| 模型 | p1sim / p2econ（`--no-bounty` 对比）；`out-p1sim-bounty.md`、`out-p1sim-nobounty.md`、`out-p2econ-bounty-d050.md` |
| 测试 | `dailyBountyPaysEachTierOnceOnTheMatchingClear_P2_7`；实测 P1Fox 挑战 Q01 结算多出「余烬币 30」，提示还差 2 局 |

| # | 问题 | 裁决 | 理由 |
|---|---|---|---|
| D79 | 每日上线理由 | 每日委托只发币和碎片，两档（第 1 次 / 第 3 次通关） | §23.3：不加物品、印记或体力；模型显示进度和成套不变，后期币由深渊层费吸收 |

### 13.21 P2-8 精选图周规则（CoreRpg 1.37.0 → 1.37.1，2026-10-02 22:20 / 22:25 CST 部署）

| 项 | 内容 |
|---|---|
| 参数源 | `ember-v1-runs.yml` `rotation.modifiers`：`lean` 限药 `potion_cap: 3`；`casters` 术者换防 `remap: {ranged: caster}`；`reverse` 逆行 `swap_rooms: true`（两份）；balance_version 8 |
| 选择 | `EmberRunMaps.modifierFor(day)` = modifiers[ISO 周 mod 3]；只在 `challenge && abyss == 0 && key == featured(today)` 时写进 `EmberRunSession.modifier` |
| 代码 | `EmberRunMaps.Modifier`（`role(role, map)` 只换到本图定义过的角色）、`layout(comp, points, seed)`；EmberRunDirector.spawnRoom 换组 / 换角色并记日志；LifeService.consumeP1 在冷却判断前调用 `EmberRunService.potionCapped`；PAPI `p1_modifier` |
| 显示 | `/corerpg p1 run` 两行、挑战页精选格、开局 tellRun |
| 模型 | `p2econ.py --mods` → `out-p2econ-mods-d050.md` |
| 测试 | EmberRunRulesTest：3 条规则、Q01 不换术者、21 周 21 种组合、同周同规则、换组 layout。实测（22:20–22:28 CST）：本周精选 Q01「限药」，P1Fox 第 4 瓶被拦下并提示「已用完」；强制 `reverse` 打 Q03 挑战，日志 `r1 rule reverse (group of r3): melee×4,heavy`，r3 换成 r1 的组，首领击败后正常结算 8 行 |

| # | 问题 | 裁决 | 理由 |
|---|---|---|---|
| D80 | 轮换图的新鲜感 | 每周一条玩法规则，三条轮换，奖励不变 | §23.3：不加倍率区、不加产出；模型显示精选通关率 −5～+4 点，印记中位不变 |

### 13.22 P2-9 掉落个性 + 团本定向 + 荣誉（CoreRpg 1.38.0，2026-10-02 22:40 CST 部署）

| 项 | 内容 |
|---|---|
| 参数源 | `ember-v1-runs.yml`（两份）：各图 `loot: {family, slot}`；`loot_bias: {own_family: 0.625, map_share: 0.75, slot: 0.55}`；`raid_item: {quality_floor: 1}`；团本 `loot.family`（R01 burst，R02 sustain）；balance_version 9 |
| 代码 | EmberRunRules `LootBias`、`pickFamily/pickSlot/familyProbability(…, LootBias)`、`rollItem(…, LootBias)`、`raidItem(in, key, raidFamily, floor)`；SettleInput.loot（团本为 null）；EmberRunMaps `lootFamily/lootSlot/lootBias()/lootLabel()`；EmberCosmetics（称号、足迹、聊天前缀、每 4 tick 足迹粒子） |
| 命令 / PAPI | `/corerpg p1 title [id\|off]`、`/corerpg p1 trail [id\|off]`；`p1_loot_<图>`、`p1_title`、`p1_honors` |
| 菜单 | 冒险页 Q01–Q07「偏向」行；挑战页每图「偏向」；图录装备页 w「哪里刷什么」、h「荣誉」（仍是 47 个菜单） |
| 模型 | `p2econ.py --raid`（新增 `--no-loot`、`--old-raid-item`、`--loot-own`、`--loot-slot`）；`out-p2econ-loot-d050.md`、`out-p2econ-loot-base-d050.md`；p1sim `--no-loot`，`out-p1sim-loot.md` |
| 测试 | `mapLootIdentityKeepsTheTargetFloorAndRaidItemIsTargeted_P2_9`：概率和为 1、目标族 ≥60%、抽样吻合、raid_item 目标族 + 保底、无目标族回退团本族；实测见 P2 草案 §5f |

### 13.23 新手第一周（CoreRpg 1.39.0 → 1.39.1，2026-10-02 23:05 / 23:23 CST 部署）

| 项 | 内容 |
|---|---|
| 参数源 | `ember-v1-runs.yml`（两份）Q01 普通版：首领 hp 200、atk 4、重斩 dmg 9；melee/ranged atk 2.5；`EmberP1Main.yml` 中 Q01 远程 Damage 2.5；balance_version 10 |
| 代码 | EmberLoadoutService `autoSelectCharm(p, uid, newTier)`、`starterBladeSlot`；giveItem 到账消息；EmberRunService `familyButtons`（bungee TextComponent）、`nextStep(d, uuid)`；首通自选在没有目标族时设目标族；status 的调试行和生命行只给管理员 |
| 命令 / PAPI | `/corerpg p1 firstclear`、`/corerpg p1 target` 不带参数时出按钮；`p1_next` 新增「领取首通自选」「选掉落目标族」两步 |
| 菜单 | 主菜单冒险图标加 `%corerpg_p1_next%` 行；图录首领页的 Q01 数值；团本 lore（仍是 47 个菜单） |
| 模型 | `out-p1sim-q01ease.md`（dodge 0.3：Q01 前沿 4%→56%，Q07 中位第 31→25 天）；`out-p2econ-q01ease-d050.md`（后期在噪声内） |
| 测试 | EmberRunRulesTest：Q01 首领 hp 200、balance 10；EmberLoadoutTest：T0 刃提示；实测见 P2 草案 §5h |
| 工具 | `tools/p1map/norm-smoke.sh`、`newbie-run.sh`，`fight.sh` 加 `DRINK=1` |

### 13.24 新手第一周 · 第二批（CoreRpg 1.40.0 → 1.40.1，2026-10-02 23:34 / 23:40 CST 部署）

| 项 | 内容 |
|---|---|
| 参数源 | 没有改；balance_version 10 |
| 代码 | EmberRunService `cmdMarks` 的按钮和确认、`exchangeButtons`、`tidyHotbar`、`onRoomStarted(…, comp)`；EmberRunRules `compositionLabel`；FlexSkillService `autoEquipStarter`（giveStarter 调用） |
| 菜单 | 冒险页「锻造印记」lore；主菜单轻技、邮寄 lore；好友页「组队 · 队伍面板」点击执行 `dp team`；帮助页的踏步、组队、旧系统（仍是 47 个菜单） |
| 测试 | `roomLineNamesTheEnemiesAndTheFirstThreat_D89`；实测见 P2 草案 §5i |

### 13.25 修正批（CoreRpg 1.40.2，2026-10-02 23:48 CST 部署）

| 项 | 内容 |
|---|---|
| 代码 | EmberLoadoutService：`loadOwnerItems` 和 `lookupItem` 回调里 `cache.remove`（D93）；EmberRunService：`p1_pending` 不算 ST_AWAIT；EmberCommand status：背包最好成色 |
| 参数源 | 只改 Q04 `purpose` 文字（币 2100，D31），两份 runs yml 和 `plugins/CoreRpg/ember-v1-runs.yml` 同步；balance_version 10 不变 |
| 模型 | `tools/p1sim/modnorm.py`：周规则套到普通版的单局通关率参考（`out-modnorm-ref.md`），还没上线 |

### 13.26 普通版周规则（CoreRpg 1.41.0，2026-10-03 00:00 CST 部署）

| 项 | 内容 |
|---|---|
| 参数源 | `ember-v1-runs.yml`（两份）`rotation.modifiers` 里 lean、reverse 加 `normal: true`；balance_version 11 |
| 代码 | EmberRunMaps.Modifier `normal`；EmberRunService.enter：普通版、非深渊、非团本、精选图、规则 normal、全队都已首通 → `s.modifier`；`normalRule()`、`ruleLine()`，PAPI `p1_rule_<图>` |
| 菜单 | 冒险页 Q01–Q07 状态行加规则；挑战页规则说明 |
| 模型 | `p1sim.py --normal-mods [all]`、`p2econ.py --normal-mods [all]`；输出 `out-p1sim-nm*.md`、`out-p2econ-nm*.md` |
| 测试 | EmberRunRulesTest：balance 11 / `g04-1/b11`，lean、reverse 是 normal，casters 不是；实测见 P2 草案 §5j |

### 13.27 去掉打命令（CoreRpg 1.41.1 → 1.41.2，2026-10-03 00:08 / 00:10 CST 部署）

| 项 | 内容 |
|---|---|
| 代码 | EmberForgeService：enhance、simple（升阶 / 精工 / 成色）、swap 预览改为 `ConfirmTokens.sendButton`；EmberRunService.deliver 暂存提示 [补领]；EmberCosmetics 列表 [装上] / [取下]；ConfirmTokens 新增 `sendButton`（不带过期尾巴） |
| 参数源 | 没有改；balance_version 11 |
| 测试 | 实测见 P2 草案 §5k；套件 9/9 |

### 13.28 第一周复查（CoreRpg 1.42.0，2026-10-03 00:21 CST 部署，D96）

| 项 | 内容 |
|---|---|
| 裁决 | 书 §3.2 的 Q02 参考「T1刃+T0符」在本引擎模型清通率 0%（闪避 0.3 / 0.5 都是）；实际参考改记为 T1刃+T1符（6% / 60%）。Q03 / Q04 与书一致。数值不动 |
| 代码 | EmberRunService：`nextStep` 在 Q01 首通后缺 T1 护符时提示刷护符；`enter` 对 Q02+ 未首通的普通版检查队员 T1 刃 / T1 护符，缺就提醒并给 [仍然进入]（`force` 一次性放行）；`runs modifier` 回执文字。EmberForgeService：手持提示、互换缺副手说明、「来源」行、分解投入警告 |
| 参数源 | 没有改；balance_version 11 |
| 测试 | 实测见 P2 草案 §5l |

### 13.29 枢纽氛围（CoreRpg 1.43.0，2026-10-03 00:32 CST 部署，D97）

| 项 | 内容 |
|---|---|
| 裁决 | 枢纽只加展示性内容（闲话、指路牌、粒子、排行榜、荣誉陈列），不碰属性、掉落和价格；§23.3 不适用（没有数值） |
| 代码 | 新增 `HubAmbienceService` 和资源 `hub_ambience.yml`；EmberRunService 新增 `topRows`、`featuredShort`；CoreRpgPlugin 接线和 `/corerpg hubambience`。EmberForgeService 分解警告只列真正的投入 |
| 参数源 | 没有改；balance_version 11 |
| 测试 | 实测见 P2 草案 §5m；套件 9/9 |


### 13.30 首通自选互换（CoreRpg 1.44.0，2026-10-03 00:59 CST 部署，D98）

| 项 | 内容 |
|---|---|
| 裁决 | Q01 首通自选 T1 护符、Q02 首通自选 T1 刃（原来相反）；Q01 掉落偏向刃不变。p1sim 600 人：Q02 首通都是第 2 天，Q07 首通持平（闪避 0.5 第 8 天），Q02 首次尝试通关率 45% → 52%（0.5）/ 70% → 88%（0.7），所以采用，不另选变体 |
| 参数源 | 两份 `ember-v1-runs.yml`：Q01 `choice: charm`、Q02 `choice: blade`，用途文字改写；balance_version 11 → 12（规则 g04-1/b12） |
| 代码 | Q02 提醒和 [回 Q01]/[仍然进入]（`ConfirmTokens.sendButtons`）；「下一步」顺序；D87 目标族文字；`EmberItemData.familyBlurb()`、`EmberRunService.lootOdds()`；领 Q02 刃时提示免费互换 |
| 测试 | selfcheck 0 failed；EmberRunRulesTest 断言改 12；p2econ 两件极品持平，「T3 两件目标族」85% → 79%（模型不会用免费互换，已加提示）；实测 FreshH1 |

### 13.31 文字、术语和菜单（CoreRpg 1.45.0，2026-10-03 01:16 CST 部署，D99）

| 项 | 内容 |
|---|---|
| 裁决 | 按点评 #2/#3/#5–#9 改文字和菜单，不改属性、掉落、价格；§23.3 不适用 |
| 代码 / 配置 | ember_hub（Q07 前合成一个图标，PAPI `p1_q07done`）、ember_life（只留面包 / 钓竿 / 代烤 / 熬药）、ember_bestiary、ember_flex_skill、各菜单标题；EmberItems lore（攻击 / 生命、来源名，无 uid）；NeigeItems `ember-v1-gear.yml`、`ember-life.yml`；回复药 lore 重写；进服消息和按钮；精选招牌「重打」；EmberCommand 前缀「[余烬]」 |
| 参数源 | 没有改；balance_version 12 |
| 测试 | 实测 FreshI1；套件见 P2 草案 §5n |

### 13.32 好友组队、锻造页、小问题（CoreRpg 1.46.0，2026-10-03 01:24 CST 部署，D100）

| 项 | 内容 |
|---|---|
| 裁决 | 好友和组队全部改成点按钮；DP 入队申请本来就带可点的 [同意] [拒绝]，不另写监听。DP 队伍上限 20 → 5（团本最多 5 人；不是数值平衡项）。锻造确认放在预览正下方，T0 预览加黄字 |
| 代码 / 配置 | FriendService（列表按钮、`addlist`、申请按钮、`invite` 先建 DP 队再发 DP 邀请）、`EmberRunBridges.hasTeam`；ember_friends（上限 40、师徒移出布局）；DP `config.yml`、`team.yml`；ember_p1_forge 布局；EmberForgeService `warnT0`、F 键文字；ember_help、ember_p1_gear 限药说明；玩家版 `/corerpg p1` 帮助改按钮 |
| 参数源 | 没有改；balance_version 12 |
| 数据 | 删除测试号 RevNewA 的游戏数据（cr_players / cr_p1_item / cr_p1_reward / cr_p1_loadout / cr_warehouse、playerdata、stats、advancements、ledger）；先备份 `db-ember-authme-20261003-012424-pre-revnewa-delete.sql` 和 `/workspace/backup/revnewa-files/`；AuthMe 账号保留 |
| 测试 | 实测 FreshJ1/FreshJ2；套件见 P2 草案 §5n |

### 13.33 装备缓存、测试号不上榜、里程碑称号（CoreRpg 1.47.0，2026-10-03 02:07 CST 部署，D101–D103）

| 项 | 内容 |
|---|---|
| 裁决 | 主手 / 攻击显示从源头修：装备缓存由事件标脏、主线程刷新，异步读取等主线程最多 750 ms。测试号不进排行榜、主城悬浮字、荣誉陈列（配置名单，不按权限）。主菜单「盟约」位改成「荣誉与排行」。三个里程碑称号只做展示 |
| 代码 / 配置 | EmberLoadoutService（`markDirty`、事件监听、`get` / `refresh`）；EmberMode `boardExcluded`；EmberLeaderboard（`at` 时间、同分先达到在前、`purgeExcluded`、`rankOf`）；HubAmbience 过滤；EmberCosmetics（q04 / q07 / abyss3）；EmberCompare、EmberItemData 短名；ember-v1.yml `leaderboard_exclude`（两份）；ember_hub 第 23 格；Q02 提醒和 [领 Q01 首通护符]；DP `lang/zh_CN.yml` |
| 参数源 | 没有数值改动；balance_version 12 |
| 数据 | leaderboard.yml 载入时清掉 P1Fox（运行时文件，不提交） |
| 测试 | 实测 FreshL1；备份 `after-1.47.0` |

### 13.34 挑战版减压、深渊换算、T3 升阶（CoreRpg 1.48.0，2026-10-03 02:29 CST 部署，D104，balance_version 13）

| 项 | 内容 |
|---|---|
| 裁决 | 挑战敌人生命 ×0.70、伤害 ×0.85；深渊层系数按同比放大（生命 ÷0.70、伤害 ×1.20），每层绝对强度不变，第 1 层 = 改前挑战版；T2→T3 升阶费用减半；Q05 首通 +900 币；升阶 / 印记兑换预览互相标注；Q07 首通和挑战页写「按 T3 来调」，进挑战版时主手不是 T3 提醒一次 |
| 参数源 | `ember-v1-runs.yml`（两份）：challenge mobs 189/139/265/164/252/630 · atk 20，boss 5600 / 37 / 61 / 37；abyss tiers 1.43/1.20 … 2.07/1.42；q05 `first_clear` 加 `coin: 900`；balance_version 13（g04-1/b13）。`EmberRunMaps.ABYSS_MAX_HP/DMG` 2.3 / 1.56；`EmberUpgradeRules` T2→T3 (60, 15, 6, 0, 1800) |
| 模型 | chrate：0.5 档刚首通 0% → 11%，免费互换后 0% → 17%，刃 T3 8% → 52%，两件 T3 16% → 86%。p2econ 12 周：线上组合（轮换 / 轮换 + 团本 / 深渊）两件极品 ≥30% 的周数在 0.5 / 0.7 档变化 ≤1 周；0.3 档从「12 周内没到」变成第 9～11 周。p1sim Q07 首通 26 / 8 / 3 天不变。selfcheck 0 failed。输出见 `tools/p1sim/out-*-d104*.md`，表格见 P2 草案 §5o |
| 代码 | EmberForgeService（升阶「另一条路」）、EmberRunService（`exchangeCompare`、`endOfP1` 引导、`warnedT3`、深渊第 1 层文字）；ember_p1_challenge、ember_p1_abyss、ember_help |
| 测试 | 单元测试（挑战值、层表、升阶费用、Q05 首通、balance_version 13）；实测 FreshM1；备份 `after-1.48.0` |

### 13.35 团本委托和招募（CoreRpg 1.48.0 / 1.48.1，2026-10-03 02:29 / 02:32 CST 部署，D105）

| 项 | 内容 |
|---|---|
| 裁决 | 团本结算在每日委托里算 2 局（发跨过的每一档，账本键不变）。团本图标右键全服招募，只发给在线的已首通 Q07 玩家；[申请入队] 走 DP 入队申请；每个队长 60 秒一次。体力 60 的备选没用 |
| 代码 / 配置 | `EmberRunRules.bountyGrants(tiers, prev, now)`；EmberRunService 委托计数 `bountyW`、`cmdRecruit`、`OPS` 加 recruit；ember_p1_adventure 团本图标左 / 右键和建议人数；ember_help、ember-v1.yml 注释；p1sim `bounty()`、p2econ 团本模式 `bounty_weight = 2` |
| 参数源 | 委托档位不变 |
| 测试 | 单元测试（跨档发放）；实测 FreshM1 + FreshN1（招募 → 申请 → 入队 → 冷却）；1.48.1 去掉招募对测试号的过滤；套件 9/9；备份 `after-1.48.1` |

### 13.36 团本倒下：观战队友 + 自动复活（CoreRpg 1.49.0，2026-10-03 03:13 CST 部署，D106）

| 项 | 内容 |
|---|---|
| 裁决 | 团本倒下后在实例里观战队友，不能离开（观战传送出实例被拦；离队友超过 24 格拉回；`/dp leave` 10 秒内输两次才算放弃，放弃后不复活也不结算）。下一个房间开打、首领现身、首领转阶段时在队友身边复活，50% 生命。全员倒下仍失败。倒下的人保留结算资格 |
| 代码 / 配置 | EmberRunDirector（`participantsHere` 不算观战者、`nextRevive`、每秒 `leashFallen`、`onBossPhase`）；EmberRunService（`watchTeammate`、`watchLater`、`reviveFallen` 先 `dp revive <玩家> true true` 再传送 / 冒险模式 / 50% 生命 / `EmberHeal.rebase`、`onSpectateTeleport`、`onFallenLeave`、`cmdWatch`，`OPS` 加 watch）；QuestService `quietHint`；EmberSupplyService 观战时不显示低血提示；DP `EmberQ0R1/2/option.yml` `revive=true;number=0`、`config.yml` 白名单加 `corerpg p1 watch`、`lang/zh_CN.yml` 倒下 / 复活文字；ember_hub、ember_p1_adventure、ember_p1_codex 文字 |
| 参数源 | 没有数值改动 |
| 模型 | p1party 复活模型（`--no-revive` 对照），R01 / R02 通关率见 P2 草案 §5p；p2econ `RAID_RATES` 加 `revive` 档（默认），`--raid-rate` 选档。输出见 `tools/p1sim/out-p1party-d106-*.md` |
| 测试 | 实测 FreshP4–P6（R01：第 2、3 间开打、首领现身、首领半血都复活，最后提示没有复活点，首领死后倒下的人照常结算）、FreshO7–O9（全员倒下判失败，[观战队友]，/dp leave 被拦） |

### 13.37 外观商店（CoreRpg 1.49.0，2026-10-03 02:50 CST 部署，D107）

| 项 | 内容 |
|---|---|
| 裁决 | 只做展示的花币出口：称号颜色（只用在已有称号上）2000～5000，朴素足迹 4000～12000（比团本足迹朴素），名牌标记 6000～20000（只在主城和野外显示）。买一次永久有，不回收，不加属性 |
| 代码 / 配置 | EmberCosmetics（`SHOP`、COLOR / FLAIR 类型、`shop`、`select`、`titleText` 套颜色、`flairOf`、`syncFlair` 队伍前缀 `efl_<id>`、普通足迹 8 tick 一次）；EmberCommand `cosmetic` / `外观`；CoreRpgPlugin `refreshBoard` 调 `syncFlair`；EmberRunService `flushData`；ember_p1_gear 第 43 格「外观商店」（原写 44，E-review 更正）。计数：`p2_cosbuy_<id>@all`、`p2_colorsel`、`p2_flairsel` |
| 参数源 | 价钱写在 EmberCosmetics（只做展示，不进 balance） |
| 测试 | 实测 FreshO1（列表、没称号不能买颜色、买金辉后聊天称号变金色、✦ 名牌前缀、灰烬足迹、装备页图标） |

### 13.38 精选图重打给中期印记（CoreRpg 1.49.0，2026-10-03 02:50 CST 部署，D108，balance_version 14）

| 项 | 内容 |
|---|---|
| 裁决 | 本周精选图首通后重打普通版，每局额外 +1 枚该图阶印记（Q01～Q03 T1，Q04～Q06 T2；Q07 不变），和挑战版的精选加成共用每周 3 次。共用和分开在 p2econ 里结果一样，共用不会多给，所以选共用 |
| 参数源 | `ember-v1-runs.yml`（两份）`rotation.normal_bonus_marks: 1`；balance_version 14（g04-1/b14） |
| 模型 | p1sim Q07 首通中位 26 / 8 / 3 天；只顺路拿 26 / 8 / 3，专门刷 25 / 8 / 4。p2econ 线上「轮换 + 团本」两件极品 ≥30%：0.3 档第 10 → 9 周，0.5 档第 10 → 9 周，0.7 档第 7 → 8 周。selfcheck 0 failed。输出 `tools/p1sim/out-p1sim-d108-*.md`、`out-p2econ-d108-*.md` |
| 代码 | EmberRunMaps `rotationNormalBonusMarks`；EmberRunService `settleFor`（精选普通版重打）、`featuredLabel`、`ruleLine`、状态行；ember_p1_challenge、hub_ambience 文字；p1sim / p2econ `feat_*` |
| 测试 | 单元测试（`normal_bonus_marks`、balance_version 14）；实测 FreshO1（精选 Q01 冒险页那一行；重打 Q01 拿到「额外 T1 锻造印记 +1（本周 1/3）」）；备份 `after-1.49.0` |

### 13.39 深渊重排 + 层费（CoreRpg 1.50.0，2026-10-03 04:13 CST 部署，D109 / D111，balance_version 15）

| 项 | 内容 |
|---|---|
| 裁决 | 第 1 层 = 挑战版（×1.00），线性到第 10 层（生命 ×2.07、伤害 ×1.42 不变）；成色跟强度（2/3/3/4/5/6/7/8/10/12% 极品）；第 8～10 层费 400 / 440 / 480 |
| 参数源 | `ember-v1-runs.yml`（两份）`abyss.tiers`；balance_version 15（g04-1/b15） |
| 模型 | p2econ 200 人 12 周 `--abyss`：0.3 档 ≥1 层 30% → 100%；两件极品 ≥30% 周数 9/8/4 → 9/7/5；0.7 档卡币天数 59 → 55。输出 `tools/p1sim/out-p2econ-e3-{base,new,new-raid,fees-low}-*.md`。selfcheck 0 failed |
| 代码 | EmberRunService `abyssLine` / `cmdAbyss` 文案；ember_p1_abyss、ember_p1_adventure、ember_p1_codex_gear 叫法；EmberCosmetics `abyss1`；单元测试第 1 / 10 层和极品单调 |
| 测试 | `/corerpg p1 abyss` 层表（FreshQ3）；备份 `after-1.50.0` |

### 13.40 Q02 首通定向兑换（CoreRpg 1.50.0，D110，balance_version 15）

| 项 | 内容 |
|---|---|
| 裁决 | Q02 首通 = 一次免费定向兑换：自选族和部位（刃 / 护符）的 T1 标准件，绑定、不可分解 |
| 参数源 | `q02.first_clear: {choice: piece, tier: 1}`（两份） |
| 代码 | EmberRunMaps `firstClearLabel`；EmberRunService `choiceButtons` / `pieceButtons`、`cmdFirstClear <族> q02 <blade|charm>`（只点族名归 Q01 护符）、Q02 推荐文案；菜单 help / adventure / codex_gear；p1sim `piece` 领较弱的那一格 |
| 模型 | p1sim 300 人：Q02 首通中位 2 / 2 / 1 天，Q07 26 / 8 / 3 天（不变）。输出 `out-p1sim-e3-q02.md` |
| 测试 | FreshQ1：6 个按钮，领到 T1 烬爆刃；单元测试 `choiceSlot = piece` |

### 13.41 印记出口、外观商店改版、新称号（CoreRpg 1.50.0，D112）

| 项 | 内容 |
|---|---|
| 裁决 | 商店可用多余印记付（T1/T2/T3 = 1/2/4 点，1 点 = 50 币，每阶留 8 枚）；印记专属：余烬辉光 160、星辉 240、紫焰辉光 320（主城、手持余烬刃）、流火 / 霜光称号动效各 240；试穿 10 秒；新称号 q01/q02/q03/q05/q06、raids10，abyss3 → abyss1 |
| 代码 | EmberCosmetics（GLOW / ANIM、`markPts`、`points`、`markCost`、`surplus`、`TRIALS`、`titleText(uuid, d, step)`、`syncFlair` 每人一个 `efl_<名字>` 队伍：前缀标记 + 后缀动效称号、`tick` 主城辉光）；EmberRunService `loadouts()`；ember_p1_gear 第 43 格 lore |
| 参数源 | 价钱在 EmberCosmetics（只做展示，不进 balance） |
| 测试 | FreshQ1：印记买金辉 / 流火 / 余烬辉光；聊天称号逐句换色；名牌后缀；试穿紫焰辉光粒子 |

### 13.42 团本文案 / 招募板、精选后期、其余文案（CoreRpg 1.50.0，D113–D115）

| 项 | 内容 |
|---|---|
| 裁决 | 团本「人越多越稳」+ p1party 数字；招募挂 10 分钟；只留 DP 一条 [同意]；拒绝反馈；已首通 Q07 的人重打精选普通版不给印记、不占名额；挑战页预期；进服消息按进度 |
| 代码 | EmberRunService（`recruits` / `liveRecruits` / `recruitsLabel` → `%corerpg_p1_recruits%`、`showRecruits`、`recruit list`、拒绝监听、`settleFor` 的 `featNormal` 加 `!progressFlag(q07)`、`featuredLabel` / `ruleLine`、`joinLines`）；CoreRpgPlugin `onJoin`；ember_hub、ember_p1_adventure、ember_p1_challenge、ember-v1.yml 注释 |
| 测试 | FreshQ2 / Q3 招募、申请、拒绝；FreshQ3 重打精选 Q01 无加成、进服消息；FreshQ1 进服消息 |

### 13.43 赛季与排行（CoreRpg 1.51.0，D116，balance_version 16）

| 项 | 内容 |
|---|---|
| 裁决 | 4 周一季，锚点 2026-09-28，周一 0 点 CST 换周；本周 / 赛季榜：深渊最高层（MAX）、精选挑战通关（ADD）、团本通关（ADD）、R01 / R02 最快通关（MIN 秒）；季末每榜前 3 赛季称号、各榜第 1 ❖ 名牌框、赛季内深渊 ≥8 层「赛季深潜者」；归档；leaderboard_exclude 不上榜 |
| 代码 | EmberSeason（`onAbyss` / `onFeatured` / `onRaid`、`top` / `rankOf`、`tick` → `finalizeSeason`、`pending` + `apply`、`seasonCommand`、`papi` 的 `season` / `season_last` / `sboard_<w|s>_<榜>_<i>` / `srank_<w|s>_<榜>`、`adminPreview` / `adminAward`）；EmberRunService `settleFor` 钩子、`season` / `goals` 子命令、`runs season preview|award|badges|goal`、`topCommand` 加赛季行；EmberRunSession `fightStart`；EmberCosmetics 6 个 season_ 荣誉（5 称号 + ❖ FLAIR）；CoreRpgPlugin 每 60 秒 `tick`、进服 `apply`；ember_p1_season、ember_hub「荣誉与排行」左键 |
| 参数源 | ember-v1-runs.yml `season: {anchor, weeks, deep_tier, top}`；运行数据 plugins/CoreRpg/p1-runs/season.yml、season-archive/（运行文件，不入库） |
| 测试 | EmberRunRulesTest `seasonCalendar_D116` + 解析；FreshQ4 发奖 / 装上 / 预览；FreshQ4–Q7 结算日志 not ranked |

### 13.44 周目标（CoreRpg 1.51.0，D117，balance_version 16）

| 项 | 内容 |
|---|---|
| 裁决 | 首通 Q07 后每周 4 个：featured 1（精选挑战版）、abyss 3（深渊层）、raid 1、bounty 3（委托最后一档的天数）；每个 15 余烬徽、全完成 +20；徽只在外观商店用（1 徽 = 1 点） |
| 代码 | EmberSeason（`addGoal`、`goalLine`、`goalsCommand`、PAPI `goals` / `goal_<g>` / `badges`，计数 `p3_goal_<g>@w<周>`、`p3_goalpay_*`、`p3_badge@all`）；EmberRunService `settleFor`；EmberCosmetics `buy <id> badge` |
| 参数源 | ember-v1-runs.yml `weekly_goals: {targets, reward, bonus}`；模型 p2econ `--goals`（out-p2econ-d116-goals-*.md） |
| 测试 | FreshQ4：精选 Q01 挑战通关计入；管理员补齐 → 80 徽；40 徽买素白 |

### 13.45 团本最后阶段复活（CoreRpg 1.51.0，D118，balance_version 16）

| 项 | 内容 |
|---|---|
| 裁决 | 首领所有转阶段都过、生命 ≤20%、有人倒下 → 20 秒后复活一次（每局一次） |
| 代码 | EmberRunDirector（`lastPhase`、`lastRevAt` / `lastRevDone`、`bossTick` 只对团本、`nextRevive` 文案）；EmberRunService `isRaid`、进本提示；ember_hub / ember_p1_adventure / ember_p1_codex 团本 lore |
| 参数源 | ember-v1-runs.yml `raid_revive: {last_phase_hp: 0.2, delay: 20}`；p1party `--last-revive-hp` / `--last-revive-delay`（out-p1party-d118-last-revive.md） |
| 测试 | FreshQ5–Q7 R01：转阶段复活后再倒下 → 20 秒后「最后阶段额外复活」，之后「没有复活点」 |

### 13.46 外观商店页（CoreRpg 1.51.0，D119）

| 项 | 内容 |
|---|---|
| 裁决 | TrMenu 页 ember_p1_shop：16 件图标，左键买 / 换上 / 取下，右键试穿；余烬徽可付 |
| 代码 | EmberCosmetics `pick`、`shopLabel` → `%corerpg_p1_shop_<id>%`；ember_p1_shop.yml |
| 参数源 | 价钱仍在 EmberCosmetics（只做展示） |
| 测试 | FreshQ4 菜单 dump + 三种点击 |


### 13.47 更好的件自动换上（CoreRpg 1.52.0，D120，balance_version 17）

| 项 | 内容 |
|---|---|
| 裁决 | 同部位严格更好（阶更高，或同阶有效值 ≥ ×1.05）：当前件无投入 → 自动换上并说明；有投入 → [换上] [免费互换强化]。「下一次突破」按成本给路线：免费换上 → 印记兑换 → 互换 → 升阶；挑战失败提示第一条 |
| 代码 | EmberRunRules `upgradeVerdict` / `pieceValue` / `UP_*`；EmberRunService `offerUpgrade` / `equipPiece` / `cmdEquip` / `breakthroughRoutes`，`/corerpg p1 equip <uid> [swap [confirm]]`、`/corerpg p1 route`、PAPI `awaken_route`；EmberForgeService `swapUids` / `lackingFor`；EmberLoadoutService `selectCharmUid`；ember_p1_gear N 图标 |
| 参数源 | `UP_SAME_TIER = 1.05`（只决定提示，不改数值） |
| 测试 | 单测 `upgradeVerdict…D120`；FreshQ8 真打 Q04 首通 → T2 精良护符自动换上 |

### 13.48 外观商店一个来源、一套页面（CoreRpg 1.52.0，D121）

| 项 | 内容 |
|---|---|
| 裁决 | 价格 / 付款 / 说法只在 EmberCosmetics；菜单和聊天版共用；新付款页；第 43 格、进服消息、赛季页开 ember_p1_shop；称号 / 排行 / 周目标只留 ember_p1_season；排行只留赛季榜 |
| 代码 | EmberCosmetics `priceText` / `howText` / `openMenu` / `pick` / `buysel` / `ensureTitle` / `papi()`（`shopprice_` `shopname_` `shophow_` `shopmarks` `shopsel_*`）；`cosmetic list`；EmberSeason `weekTop` 供 `topRows` 和主城 PAPI；ember_p1_shop.yml、ember_p1_shop_buy.yml（新）、ember_p1_gear.yml X、ember_hub.yml Y、ember_p1_season.yml、ember_p1_codex_gear.yml [51] |
| 参数源 | 价钱仍在 EmberCosmetics（只做展示） |
| 测试 | 单测 `shopPriceLine…D121`；FreshQ8 页内用币买素白、自动装称号、回商店页；第 43 格 / `top` 开对应页面 |

### 13.49 Q04 封口和掉落传回（CoreRpg 1.52.0，D122）

| 项 | 内容 |
|---|---|
| 裁决 | R3 东墙排水口封口；低于 y 63.5 传回当前房间；R3 浮空刷怪点挪位 |
| 代码 | EmberRunMaps `fallCatchY`；EmberRunDirector fall-catch（玩家传回、怪回家） |
| 参数源 | ember-v1-runs.yml q04 `rails: [[29,63,99,30,64,103]]`、`fall_catch_y: 63.5`、R3 点 [25,64,93] |
| 测试 | `check-dp-spawns.py` 浮空点消失（门铁栏报错是全图老问题）；FreshQ8 生存不 tp 通关 Q04 |

### 13.50 赛季深渊榜并列、深潜者第 5 层（CoreRpg 1.52.0，D123，balance_version 17）

| 项 | 内容 |
|---|---|
| 裁决 | 同层按用时排，无用时最后；赛季深潜者 = 赛季内通关第 5 层；没上榜也显示自己 |
| 代码 | EmberSeason `Row.secs`、`compareRows`、`rowText`、`bestSecs`、`abyssMine`；`onAbyss(…, secs)` |
| 参数源 | ember-v1-runs.yml `season.deep_tier: 5` |
| 测试 | 单测 `abyssBoardBreaksTies…D123`；FreshQ8 `season` 每榜「你：未上榜」 |

### 13.51 深渊层费 T3 印记抵（CoreRpg 1.52.0，D124，balance_version 17）

| 项 | 内容 |
|---|---|
| 裁决 | 币不够时，多于 8 枚的 T3 印记 1 枚 = 200 币抵层费；退费退印记 |
| 代码 | EmberRunService `feeMarks`、账本 `cost_coin = mark:3:n`、`releaseFee` / 重启退还；EmberRunMaps `abyssFeeMarkCoin` |
| 参数源 | ember-v1-runs.yml `abyss.fee_mark_coin: 200`；p2econ `--fee-mark`（out-p2econ-feemark-*.md：0.7 档卡币天数 54.8 → 28.4，两件极品第 12 周 75 → 83%） |
| 测试 | 单测 fee mark 200；p2econ 三档 |

### 13.52 团本倒下提示（CoreRpg 1.52.0，D125）

| 项 | 内容 |
|---|---|
| 裁决 | 拦 `/dp revive` 改回下一次复活说明；关 DP 复活消息；24 格提示 10 秒冷却；团本无药可退不提示 |
| 代码 | EmberRunService（命令拦截、`leashTold`、退药判断）；DP `config.yml` `dungeon-revive-message: false`、lang `dungeon-game-un-revive` |
| 测试 | 代码 + 配置；本轮没有实机团本复测 |

### 13.53 聊天世界名和原版成就（CoreRpg 1.52.0，D126）

| 项 | 内容 |
|---|---|
| 裁决 | 聊天不带世界名；不广播原版成就 |
| 代码 | CoreRpgPlugin `quietAdvancements`（启用时 + WorldLoadEvent）；Multiverse `prefixchat: 'false'`（在线用 `mv config prefixchat false`） |
| 测试 | FreshQ8 聊天「[庭院余火] <FreshQ8> …」；`gamerule announceAdvancements` = false |

### 13.54 旧物品名重写（CoreRpg 1.52.0，D127）

| 项 | 内容 |
|---|---|
| 裁决 | 进服重写签名有效的 P1 物品名字和 lore，只改显示 |
| 代码 | EmberItems `relabel`；EmberLoadoutService `onJoin` 40 tick 后 |
| 测试 | P1Fox 进服「10 old item label(s) rewritten」 |

### 13.55 挑战失败退一半体力（CoreRpg 1.53.0，D128，balance_version 18）

| 项 | 内容 |
|---|---|
| 裁决 | 每个体力日第一次失败的挑战版或深渊层退还 50% 体力；不给掉落和币，层费不退；挑战和深渊共用每天一次 |
| 代码 | EmberRunService `fail`（先读 fightStarted）/ `failRefund` / `failRefundLabel`（PAPI `p1_failrefund`）；EmberRunRules `failRefundAmount` / `failRefundRun` / `FAIL_REFUND_KEY`；EmberRunMaps `failRefund`；ember_p1_challenge / ember_p1_abyss 各一行 |
| 参数源 | ember-v1-runs.yml `fail_refund: 0.5`；p2econ `--fail-refund`（out-p2econ-d128-fail-refund-*.md） |
| 测试 | 单测 `failRefundIsHalfOncePerDayD128`；FreshQ13 两次 Q01 挑战失败：第一次 +15，第二次不退 |

### 13.56 刷怪点检查（D129）

| 项 | 内容 |
|---|---|
| 裁决 | 门格空气（存档时开着）或铁栏都算对，别的方块报错；团本 r01 / r02 单独成块 |
| 代码 | scripts/check-dp-spawns.py |
| 测试 | live 图 9 个副本全 ok |

### 13.57 起步刃不挡自动换上、起步刃进背包（CoreRpg 1.54.0，D130 / D131）

| 项 | 内容 |
|---|---|
| 裁决 | D85 顶替起步刃之后总是再走 D120 自动换上；起步刃挪到背包第一个空格（9–35），背包满才和新刃对调 |
| 代码 | EmberRunService `giveItem`（不再提前 return）；EmberRunRules `starterTarget` |
| 参数源 | 无（不改数） |
| 测试 | 单测 `starterBladeGoesToTheBackpackNotTheHotbar_D131`、`starterSwapStillLetsTheBetterBladeAutoEquip_D130`；`tools/p1map/starter-equip-check.sh` FreshQ18 / FreshQ19 全 PASS |

### 13.58 背包里更好的同阶件（CoreRpg 1.54.0，D132）

| 项 | 内容 |
|---|---|
| 裁决 | 自动换上 / 询问在新件 + 背包同部位件里挑最好的（D120 的 5% 规则，开套装时要同族）；「下一次突破」有更好的同族背包件时先给「换上（免费）」+ [换上刃] / [换上护符] |
| 代码 | EmberRunRules `betterThan` / `bestCandidate`；EmberRunService `offerUpgrade` / `bagPieces` / `breakthroughRoutes` |
| 参数源 | 无 |
| 测试 | 单测 `betterSameTierPieceInTheBagIsPreferred_D132`；FreshQ19 手拿 T1 打 `/corerpg p1 route` 出现 [换上刃] |

### 13.59 团本失败说明周次数（CoreRpg 1.54.0，D133）

| 项 | 内容 |
|---|---|
| 裁决 | 团本周次数只在通关结算时加；失败消息多一句「本周团本次数没有扣：还是 n/3…」 |
| 代码 | EmberRunService `fail`（团本分支）、`raidWeek` |
| 参数源 | 无 |
| 测试 | FreshQ24–Q26 R01 开战后全倒，三人都看到「还是 0/3（团本合计）」 |

### 13.60 毕业那周委托目标按剩下的天数（CoreRpg 1.54.0，D134，balance_version 19）

| 项 | 内容 |
|---|---|
| 裁决 | Q07 真首通记日期；毕业那周「委托 3 天」目标 = max(1, min(3, 8 − 星期几))，下周起恢复 3；进服 [外观商店] 按钮补齐 D121 |
| 代码 | EmberSeason `target` / `proratedTarget` / `markGraduated`（`p3_grad`）；EmberRunService `endOfP1`、`joinButtons`；ember_p1_season 一行 |
| 参数源 | ember-v1-runs.yml（两份）balance_version 19、`weekly_goals:` 注释 |
| 测试 | 单测 `bountyGoalIsProratedInTheGraduationWeek_D134`；FreshQ20 进服按钮有 [外观商店] |

### 13.61 深渊层费不调（D135）

| 项 | 内容 |
|---|---|
| 裁决 | 第 8～10 层费 400 / 440 / 480、每阶保留 8 枚印记、`fee_mark_coin: 200` 都不动；第 10 层反复打是后期花币出口（用户 10-03 09:51） |
| 代码 | 无 |
| 参数源 | ember-v1-runs.yml 不变（balance_version 19） |
| 测试 | p2econ `out-p2econ-recheck5-*.md`：0.7 档第 12 周 100% 到第 10 层 |

### 13.62 外观商店返回键（CoreRpg 1.55.0，D136）

| 项 | 内容 |
|---|---|
| 裁决 | 返回键回到打开商店的那一页（装备页 / 赛季页），从聊天栏或命令打开就关闭商店 |
| 代码 | EmberCosmetics `shop`（`from` / `back`）、`FROM` / `FROM_MENUS` / `backLabel`、PAPI `p1_shopback`；EmberSeason 称号列表按钮；ember_p1_gear / ember_p1_season [43]、ember_p1_shop 返回键 |
| 参数源 | 无 |
| 测试 | 单测 `shopBackGoesToTheOpeningPage_D136`；FreshQ27 三个入口都回对了 |

### 13.63 团本 R03 断塔回廊·团（CoreRpg 1.56.0，D137，balance_version 20）

| 项 | 内容 |
|---|---|
| 裁决 | Q05 地图的 3～5 人团本，和 r01 / r02 合计每周 3 次；招牌机制烬核（分摊）；偏向族焚烬；结算同 r01 |
| 代码 | EmberRunMaps `Skill.share`；EmberRunDirector `execute`（分摊）/ `shareDamage` / 绿圈；EmberSeason `time_r03`；EmberCosmetics `r03` / `trail_r03`；DP `EmberQ0R3` + config；菜单 hub / adventure / codex / codex_gear / season |
| 参数源 | ember-v1-runs.yml（两份）`raids.r03`（烬核 `dmg: 100, warn: 3.0, every: 16, share: true`），balance_version 20；p1party `--share-dmg`（out-p1party-d137-*.md） |
| 测试 | 单测 `thirdRaidStacksForItsShareMoveAndCoversTheLastFamily_D137`；check-dp-spawns 10 个 ok；FreshQ28–Q30 R03 通关 + 烬核 n=1 / n=2 日志 |

### 13.64 重复刷图花样（CoreRpg 1.57.0，D138，balance_version 21）

| 项 | 内容 |
|---|---|
| 来源 | 用户 10-03 13:51：新内容批第 2 项（重复局的变化：随机精英词缀 + 可选房间事件，只用现有奖励种类） |
| 规则 | 已首通的普通版（全队都首通），开本按种子定：1 只词缀精英（炽热火圈 1 秒预警 / 分裂 2 个 50% 近战 / 护盾生命 ×1.6）击败 +3 余烬碎片；约 50% 的局 30 秒限时清房 +1 余烬核心；首通 / 挑战 / 深渊 / 团本没有 |
| 配置 | runs yml `variety:`；balance_version 21 |
| 模型 | p1sim Q07 首通中位 26→25 / 8→8 / 3→3 天；p2econ 两件极品最快 7.75→7.80 / 5.50→5.50 / 4.33→4.44 周（+6 碎片时 0.3 档 −2 天，超限，改 +3） |
| 测试 | 单测 `repeatRunVarietyIsSeededAndPaysOnlyExistingTypesOffFirstClears_D138`（39 个全过）；selfcheck 0 failed；随 1.58.0 上线（10-03 14:38 CST 部署），实测词缀局还没打 |

### 13.65 国庆 2026 限时活动「烟火庙会」（CoreRpg 1.58.0，D139，balance_version 22）

| 项 | 内容 |
|---|---|
| 来源 | 用户 10-03 新内容批：国庆限时活动，10-07 前能玩、10-08 0 点（CST）结束，日期全部来自配置 |
| 时间窗 | `ember-v1-festival.yml` `start: 2026-10-03T00:00:00+08:00` / `end: 2026-10-08T00:00:00+08:00`（start ≤ 现在 < end 开放；`enabled: false` 整个关掉）；改日期改这两行再 `/corerpg p1 fest reload` |
| 活动本 | gq26「烟火庙会」：DP `EmberQ0F1`，地图 `ember_fest_gq26_v1` = Q04 潮蚀水道模板的红金换色副本（`/corerpg p1 fest mapbuild` 只换方块，几何 / 门 / 刷怪点同 Q04；原模板不动）。三房（灯会平台 / 爆竹坊 / 舞狮场）+ 首领年兽 · 焰狮（1800 生命，礼花圈 / 狮首扑击 / 半血后爆竹连环 + 余响）；强度在 Q04 和 Q05 之间。1～3 人，不耗体力，每天 3 次（进实例才算），需本人首通 Q04；单人倒下即失败。代码 `EmberRunMaps.events`（`MapDef.event`，不进主线顺序 / 深渊 / 团本），`EmberRunService` enter / verifyEntry / onBossKilled / onMobDeath 的事件分支 |
| 奖励 | 只发国庆币（NI `ember_fest_coin_gq26`，CoreRpg 在活动本里发：小怪 25% 1 枚、精英 2、福袋兔 / 宝箱 3、通关每人 10，约 20 / 通关）和首次通关的限时称号「盛世烟火」；**不发余烬币、装备、印记、碎片、委托 / 周目标进度**，不走主线结算 → p2econ 不变（没有新币来源；活动后常驻价是币 / 徽的出口） |
| 活动护符 | 盛世烟火符：独立的活动护符位（不占主手和已选护符、不算套装），H / D 走统一公式（`EmberLoadout.compute(..., festHp, festDef)` 加进 H0 / D，夹到标准 T3 护符 h 165 / D 14）。上线值 **hp 0 / def 0**；效果烟火迸发：你击杀的敌人炸开，3 格内最多 3 个其他敌人吃 0.2×B（不暴击、不算套装触发、迸发击杀不再迸发），每人 6 秒一次，只在 P1 副本里。活动期间 60 国庆币；活动后常驻 15000 余烬币或 300 余烬徽；都要本人首通 Q04 |
| 模型 | `tools/p1sim/festsim.py` → `out-festsim-d139.md`：书参考区间平均（表 1b）Q01–Q07 戴 / 不戴差 ≤ 2.4 点（Q02 躲避 0.7）；团本 1000 局 R01 +2.9 / R02 +0.5 / R03 +0.1。调参过程：hp 10 让 Q01 / Q03 +30 点，hp 3 或 def 1 仍 +5～11（主线图对 H 很敏感），所以不加属性；coef 0.4 → +6.4，0.25 → +3.6，0.2 达标（`out-festsim-d139-coef025.md`）。活动本：刚首通 Q04 的推进玩家躲避 0.3 / 0.5 / 0.7 = 54% / 66% / 98% |
| 限时外观 | 称号 gq26「盛世烟火」（活动期间通关，计数 `p2_title_gq26`）、足迹 trail_gq26「红金烟火」（活动商店 30 国庆币，计数 `p3_fest_trail_trail_gq26`）；活动后都拿不到，已有的保留 |
| 菜单 | `ember_p1_fest`（信息 / 进本 / 护符 / 足迹 / 称号 / 返回主界面），主界面左上角图标（活动期间才亮，`%corerpg_p1_fest_open%`）；PAPI `%corerpg_p1_fest_*%` |
| 测试 | 单测 `EmberFestivalTest`（事件本解析与校验、时间窗、T3 封顶）；实测 FestQ01（1.57.0 测试包：进本门槛、掉币、买护符、迸发命中日志 `[P1 fest] burst … hit 1`、通关得称号、买足迹装上）、FestQ02（1.58.0：进本 → 通关 → 称号 + 15 国庆币）；活动结束后的常驻价没有实测（要改日期） |

### 13.66 七个主线首领各加一招（CoreRpg 1.59.0，D140，balance_version 23）

| 项 | 内容 |
|---|---|
| 来源 | 用户 10-03 13:51 / 14:37：新内容批第 3 项，每个 Q01–Q07 首领一招有预警的新招，通关率 ±3 以内；Q01 要改形状或错开时机 |
| 规则 | 踏地（Q01，接重斩后 2 秒、半血后）/ 骨刺 / 誓印圈 / 潮涌 / 落石 / 霜环 / 矿锤横扫，预警 ≥ 1.2 秒，挑战版轻值；主招放慢（10.5 / 12.5 / 13.5 / 13.5 / 12 / 12.5 / 12 秒） |
| 模型 | `tools/p1sim/bossmoves.py`（5 种子 × 4000 局）：6 格通关率最大变化 1.7 点 |
| 测试 | 单测 `everyMainBossHasOneNewTelegraphedLightMove_D140`（183 个全过）；selfcheck 0 failed；FreshQ32 Q01 踏地、Q07 矿锤横扫实测出招；check-dp-spawns 11 ok |

### 13.67 天赋专精（D141，P2 草案 §5za 第 1 部分）

| 项 | 内容 |
|---|---|
| 来源 | 用户 10-03 15:42：服主说成长太单线 → 有上限的横向成长、装备仍是主线；三样全开到顶时 Q01–Q07 / 团本通关率 ±3 以内、Q07 首通中位和两件极品最快路线 ±0.5 周 |
| 规则 | 3 排 × 3 个天赋，每排一个、从上往下点；专精点 6（首通 Q03 / Q05 / Q07、任一挑战、任一团本、深渊 5 层），排价 1 / 2 / 3 点；学会付一次币 800 / 2000 / 4000；重置第一次免费，之后 2000 币；只在城里改 |
| 参数源 | `ember-v1-growth.yml` `talents:`（两份一致）；修饰键表见 P2 草案 §5za |
| 模型 | `tools/p1sim/growth.py` + `growthtune.py` / `growthcheck.py` / `growthraid.py` / `growthrun.py`；终验 `tools/p1sim/out-growth-d141-d143.md`：Q01–Q07 / R01–R03 通关率 ±3（Q03/Q04 普通 0.5 档 +3.3～3.6 接受），Q07 首通中位 ≤ +0.29 周，两件极品最快路线 −0.42～+0.06 周 |
| 上线 | CoreRpg 1.60.0（10-03 18:23 CST），balance_version 24，三样一起上线 |

### 13.68 余烬勋记（D142，§5za 第 2 部分）

| 项 | 内容 |
|---|---|
| 规则 | 7 个账号永久勋记，条件全是已有一次性成就（深渊 5 / 10 层、挑战首通 3 / 7 张、团本首通 1 / 3 个、图录全满）；效果经济 / 便利为主，战斗类只有深渊里受伤 −2%；图录那个不给战斗属性（书 §19.5） |
| 封顶 | 币 ×1.04、碎片 +1 / 次、深渊层费 ×0.95、深渊受伤 ×0.98、洗练币 ×0.85（代码硬封顶） |

### 13.69 词条洗练（D143，§5za 第 3 部分）

| 项 | 内容 |
|---|---|
| 规则 | T1+ 刃 / 护符一个词条槽（初始空）；一次 = 同部位同阶重复件（或碎片 40 / 80 / 120）+ 币 300 / 600 / 1000；档位 50 / 30 / 15 / 5% 截在成色上限（标准 1 / 精良 2 / 卓越 3 / 极品 4）；连续 5 次没出上限档第 6 次必出；保留新 / 旧 |
| 存档 | 词条记在玩家存档、按物品 uid（`p4_af_<uid>` = 编号 × 10 + 档，`p4_afp_<uid>` = 保底计数），不改物品签名 |
| 词条池 | 刃：破缀 +4/8/12/16%、余烬（套装事件）+0.25/0.5/0.75/1%（冻结时 ×0.5）、拆分 +5/10/15/20%；护符：稳桩 −0.5/1/1.5/2%、分核 −1/2/3/4%、抗缀 −4/8/12/16% |

### 13.70 花样委托 / 烬核同心 / 余烬连战（CoreRpg 1.61.0，D144，balance_version 25，P2 草案 §5zb）

| 项 | 内容 |
|---|---|
| 来源 | 新内容批 10-04：新的委托目标、团本配合目标、每周首领连战；只用现有奖励种类，两件极品最快路线变化 ≤ 0.5 周，超了先砍连战 / 委托奖励 |
| 规则 | 花样委托（每日）：重打已首通普通版时击败 2 只词缀精英 20 币、1 次限时清房达标 20 币，每局只算一次；烬核同心（可选周目标）：R03 烬核落下时全队站着的人都在圈里（≥ 2 人）并通关，2 次 → 15 余烬徽，不算进全部完成；余烬连战：Q05 → Q06 → Q07 首领在 Q05 首领厅连打（DP `EmberQ0B1`），×1.7 生命 ×1.45 伤害，休息 10 秒回 30%，没有复活，需本人首通 Q07，1～3 人，不耗体力，每周 1 次（第一个首领现身时计入），奖励 T3 印记 +1、余烬徽 +20、首次称号「连战不息」，不给币 / 装备 / 碎片 / 委托；`time_rush` 周 / 赛季榜 |
| 参数源 | `ember-v1.yml` `bounty.variety:`；runs yml `weekly_goals.targets.core`、`rush:`（两份一致） |
| 模型 | `rushsim.py`（T3 两件 +6 躲避 0.5 单人约 9 成）；p2econ 600 人 12 周两件极品最快 关 / 开 7.50→7.75 / 5.44→5.38 / 4.18→4.15 周（+0.25 / −0.06 / −0.03，`tools/p1sim/out-p2econ-d144.md`）；p1sim Q07 首通 26 / 8 / 3 天不变 |
| 测试 | 单测 `EmberRunRulesTest`（花样委托档位）；线上冒烟 FreshQ35 / FreshQ36（§5zb）：Q01 普通版词缀精英 → 花样委托 1/2；连战通关 27 秒、称号 + 印记 + 20 徽；check-dp-spawns 25 ok；套件 9/9 |

### 13.71 评审 10-04 整改 Part D（CoreRpg 1.61.0，D145–D149，balance_version 25）

| 项 | 内容 |
|---|---|
| 来源 | `docs/reviews/review-2026-10-04-new-content.md` Top 10 |
| D145 #1 | 活动后主菜单「国庆纪念」入口；装备页常驻烟火符格 |
| D146 #2 | 剩余国庆币：纪念称号 120；5:1 换余烬徽、每人封顶 40；足迹活动后用剩下的国庆币买（`ember-v1-festival.yml`）。实测 130 → 称号 + 2 徽，再 300 → 38 徽封顶 |
| D147 #3 #5 #6 #8 #10 | 天赋 / 洗练 lore 写量级与适合 / 不适合；词条名带「纹」（code 不变）；锁定圈预警带半径；结算行来源前缀；厚甲 / 余烬核心碎片 |
| D148 #4 | 洗练锁定词条，另加 `lock_shard` 0 / 40 / 80 / 120；`rerollsim.py` 指定词条满档 约 16 → 约 6 次。实测 T1 精良猎缀纹 1 → 2 档 |
| D149 #7 #9 | 主菜单团本合一格、天赋 / 勋记 / 洗练合成「成长」；活动首领两招预警 1.0 → 1.2 秒（`out-festsim-d149.md` 差 0.0 点） |
| p2econ | 不改输入（锁定只影响洗档位，模型档位固定；余烬徽只买外观） |

### 13.72 连战失败行、刷怪点检查切块（D150，源码已修，等 1.61.1 部署）

| 项 | 内容 |
|---|---|
| 来源 | 1.61.0 冒烟（10-04 03:33 CST，FreshQ35 第三个首领倒下） |
| 问题 | `fail()` 先把状态改成 FAILED 再拼连战失败那一行，`fightStarted()` 读成 false → 已计入本周的局也说「还没开打，本周次数没有扣」（次数其实扣了，日志 `rush entry counted`） |
| 修改 | 改状态前读 `started`（和挑战失败 `chFail` 同样处理）；`check-dp-spawns.py` 按任何两格缩进的图键切块，`rush:` 段不再混进 q07（之前 EmberQ0B1 误报门格 FAIL）；套件主菜单检查认合并图标 |
| 上线 | 源码 main 9af8556；线上仍是 1.61.0，下次部署升 1.61.1 |

### 13.73 P2-8 第 4 条周规则「卸甲」（CoreRpg 1.63.0，D152，balance_version 26）

| 项 | 内容 |
|---|---|
| 来源 | handoff 候选：精选周规则可再加几条；计时/黑暗在 P2 草案 §5e 明确否决；卸甲复用已有 `Modifier.remap` |
| 规则 | `disarm` / 卸甲：`remap: {heavy: melee}`；挑战专用（不设 `normal: true`，与 `casters` 相同）；不改奖励、不加倍率（§23.3） |
| 参数源 | `ember-v1-runs.yml` `rotation.modifiers` 第四条（两份）；balance_version 26；轮换 4×7=28 周组合 |
| 模型 | `p2econ.py --mods` → `tools/p1sim/out-p2econ-mods-d152.md`：disarm 通关率差 +0 点（躲避 0.5 已顶 100%）；第 8 周印记/币相对轮换噪声内 |
| 测试 | 单测 `EmberRunRulesTest`：4 modifiers、disarm heavy→melee on q01、`!normal`、28 pairs；线上冒烟 FreshQ44：`modifier forced disarm` + 聊天「卸甲」+ `r1 rule disarm: melee×4` |
| 上线 | 10-04 07:48 CST，日志 `Enabling CoreRpg v1.63.0` + `[storage] MySQL connected`；STATUS `docs/status/STATUS-ember-modifier-disarm-d152.md` |

### 13.74 深渊层费复查：不卡穷、不调（D153，2026-10-04，balance_version 仍 26）

| 项 | 内容 |
|---|---|
| 来源 | D151 后 p2econ 第 8 周（0.5 档）币中位：不打深渊约 3.3 万、打深渊约 1.4k；D135 定过第 8～10 层费不调 |
| 检查 | `tools/p1sim/abysscoin.py` 200 人 8 周 × 0.3 / 0.5 / 0.7 × 层费优先 / 先精工（`p2econ.py --abyss-forge-first`）；60 人试降层费 ×0.75 / ×0.6 / ×0.5 / 只砍 5～10 层 |
| 数 | 两件极品深渊 56～74% 对 38～40%；先精工：精工成色做满周 4 / 3 / 4 对 4 / 3 / 1，卡精工周数中位 3 / 3 / 4 对 3 / 3 / 1，B 134.6 相同；降层费后余币 1380→1387 / 1545 / 1900 / 1490 |
| 裁决 | 不卡（自愿花余币），`abyss.tiers[].fee` / `fee_mark_coin` 不动，不加结算币；STATUS `docs/status/STATUS-ember-abyss-coin-2026-10-04.md`，P2 草案 §5zd |

### 13.75 P2-8 第 5 条周规则「卫士潮」（CoreRpg 1.63.1，D154，balance_version 27）

| 项 | 内容 |
|---|---|
| 来源 | handoff 候选：精选周规则可再加几条；计时/黑暗在 P2 草案 §5e 明确否决；卫士潮复用已有 `Modifier.remap` |
| 规则 | `guards` / 卫士潮：`remap: {ranged: heavy}`；挑战专用（不设 `normal: true`，与 `casters` / `disarm` 相同）；不改奖励、不加倍率（§23.3） |
| 参数源 | `ember-v1-runs.yml` `rotation.modifiers` 第五条（两份）；balance_version 27；轮换 5×7=35 周组合 |
| 模型 | `p2econ.py --mods` → `tools/p1sim/out-p2econ-mods-d154.md`：guards 通关率差 +0 点（躲避 0.5 已顶 100%）；第 8 周印记/币相对轮换噪声内；未触发 −12 点否决线 |
| 测试 | 单测 `EmberRunRulesTest`：5 modifiers、guards ranged→heavy on q01/q3、`!normal`、35 pairs；线上冒烟 FreshQ45：`modifier forced guards` + 聊天「卫士潮」+ `r1/r2/r3 rule guards` |
| 上线 | 10-04 08:21 CST，日志 `Enabling CoreRpg v1.63.1` + `[storage] MySQL connected`；STATUS `docs/status/STATUS-ember-modifier-guards-d154.md` |

### 13.76 P2-8 第 6 条周规则「铁卫」（CoreRpg 1.63.2，D155，balance_version 28）

| 项 | 内容 |
|---|---|
| 来源 | handoff 候选：精选周规则可再加几条；计时/黑暗在 P2 草案 §5e 明确否决；铁卫复用已有 `Modifier.remap`（与 disarm/guards 不同：melee→heavy） |
| 规则 | `wall` / 铁卫：`remap: {melee: heavy}`；挑战专用（不设 `normal: true`，与 `casters` / `disarm` / `guards` 相同）；不改奖励、不加倍率（§23.3） |
| 参数源 | `ember-v1-runs.yml` `rotation.modifiers` 第六条（两份）；balance_version 28；轮换 6×7=42 周组合 |
| 模型 | `p2econ.py --mods` → `tools/p1sim/out-p2econ-mods-d155.md`：wall 通关率差 +0 点（躲避 0.5 已顶 100%）；第 8 周印记/币相对轮换噪声内；未触发 −12 点否决线 |
| 测试 | 单测 `EmberRunRulesTest`：6 modifiers、wall melee→heavy on q01、ranged unchanged、`!normal`、42 pairs；线上冒烟 FreshQ46：`modifier forced wall` + 聊天「铁卫」+ `rN rule wall` |
| 上线 | 10-04 08:52 CST，日志 `Enabling CoreRpg v1.63.2` + `[storage] MySQL connected`；STATUS `docs/status/STATUS-ember-modifier-wall-d155.md` |

### D156（2026-10-04）国庆活动结束后路径冒烟

临时改 `ember-v1-festival.yml` end→`fest reload`→FreshQ47：D145 主菜单「国庆纪念」+ 装备页常驻格、D146 常驻价 15000 币购符 / 结束后足迹 / 剩币换徽 / 徽价门闩均 PASS；立刻还原 10-08。不改数值。STATUS `docs/status/STATUS-ember-fest-after-end-smoke.md`。

| D157 | CoreRpg 1.64.0 | InvSnap strip vault/ticket on restore；bulk dismantle skip invested；hub L after-end actions；文案批次（周规则/繁花烟火/宠物光环/余烬徽）；FreshQ49 PASS；推迟火花折算与批量撤销 |
| D159 | CoreRpg 1.64.2 | 洗练可用装备库重复件（优先背包，否则装备库；kind=reroll）；hub 扭蛋 ender pearl；扭蛋页#9文案；FreshQ51 PASS |
| D158 | CoreRpg 1.64.1 + CoreGacha 1.0.1 | InvSnap 按账本扣回（cr_vault_log + gacha_ledger redeem，取代 1.64.0 全部扣回）+ `invsnap preview`；批量分解确认按成色计数 + 红字不分解清单 + [投入] 标记；整批撤销 `p1 undo batch`；gq26 火花 30 只换锦鲤灵/繁花烟火、剩余 1:1 转 standard；周规则转化怪各自加成 balance_version 29；FreshQ50 PASS 14/14、FreshG05/G06 PASS |

### 13.77 洗练可用装备库重复件（CoreRpg 1.64.2，D159，2026-10-04）

| 项 | 内容 |
|---|---|
| 问题 | 自动入库后洗练页只说「背包里没有重复件」，装备库里合格掉落重复件用不了 |
| 裁决 | 洗练可消耗装备库重复件（未锁定/未收藏 + duplicateOk）；优先背包；ledger kind=reroll（不可撤销） |
| 旁注 | hub 扭蛋图标改 ender pearl；扭蛋页补商店件币买/已购返光屑文案；不加周规则 |
| 版本 | CoreRpg 1.64.2；冒烟 FreshQ51 PASS；STATUS `docs/status/STATUS-ember-reroll-gearlib-d159.md` |
| D160 | CoreRpg 1.65.0 | 余烬连战不限次数重试、每周首次通关领奖（p4_rush_claim）、之后为练习；其它周内容逐项复查不改 |
| D161 | CoreRpg 1.65.0 | 存仓 / 存库 / 取出 / 快照恢复后同 tick 保存 .dat；取出途中下线补偿（D162 起改为投递） |
| D162 | CoreRpg 1.65.1 | review §03 A01–A04：cr_p1_delivery 同事务投递、自动入库同步单事务、工坊扣费 hold + 对账、玩家行 + 仓库一个事务、资产冻结 / 写失败暂停、离线恢复成功后才出队；测试专用故障注入 |
| D163 | CoreRpg 1.65.2（未单独部署，随 1.65.3 上线） | review B01：拆分改为分身净 +50% / 本体 −20%（`dmg_affix_body` 0.80）；开局药格文案改「快捷栏第 5–9 格」。见 `docs/status/STATUS-ember-b01-split-d163.md`；拆分部分已被 D164 取代 |
| D164 | CoreRpg 1.65.3 | 天赋第二排 `t2c` 拆分 → **破甲**：`dmg_affix_shield` 1.60、`dmg_affix_blazing` 0.80、`dmg_affix_split` 0.80（分身也算分裂）；只作用于主线重打的词缀精英 |
| D165 | CoreRpg 1.65.3 | 刃词条 裂身纹 `b_split` 移出洗练池（`rollable: false`）：洗不出来、不能锁定；已有的照常生效；洗练页 / 词条池 / 图鉴列为「已移出洗练池」 |
| D166 | CoreRpg 1.65.3 | 文案：天赋第二排与 猎缀纹 / 裂身纹 / 抗缀纹 加「仅主线重打生效」；Q01 第一房提示退到门口打；首领前「留 2 瓶药」；R01 招募 / 开本 / 冒险页「建议队伍里有炽愈」。不改数值 |
| D169 | 文档定稿（无版本 bump） | 6 槽装备结构分阶段落地计划 SETTLED：`docs/design/DESIGN-ember-gear-staged-plan-2026-10-04.md`。目标刃+护符+4甲；每图掉全部位、甲另掉不稀释；2+4 件套；Stage 0–4 门禁。CoreRpg 结构代码仍 HOLD。不占 D167/D168 |
| D170 | CoreRpg 1.65.4 | Q02–Q07 第一房战术提示（短中文）；Q04 r3 可选落差提醒；balance_version 仍 29；不改战斗数值 |

### 13.78 余烬连战：失败不限次数重试，每周首通领奖（CoreRpg 1.65.0，D160，2026-10-04）

| 项 | 内容 |
|---|---|
| 来源 | 玩家反馈：连战一周只能进一次，打输了这周就没了，太伤 |
| 裁决 | 去掉每周进入次数；`p4_rush` 改为只统计尝试次数，`p4_rush_claim` 记每周领奖；本周第一次结算通关发 T3 印记 + 余烬徽，之后本周再通关是练习（不发奖，仍上 `time_rush` 周榜）；本周已有旧版通关（旧计数 > 0 且在周榜上）算已领。称号仍是首次通关（只在领奖局）。免费，没有每次尝试成本。失败提示「不扣任何东西，随时可以再来」+ 本周奖励状态 + [再来一次] |
| 其它周内容 | 团本：周上限只数通关（D133），每次进入扣体力不变，达到上限后挡进入 → 不改。精选周挑战（C_ROTATION）：按通关计 → 不改。深渊：每次层费 + 体力，D128 半价退还，不是「每周机会」→ 不改。国庆 gq26：每天 3 次按进入计 → 不改（怪物掉币，进入上限就是奖励上限；按天；10-08 结束）。旧精英 / 天灾：P1 大厅进不去 → 不改 |
| 模型 | `p2econ.py --rush-tries N`（`RUSH_STATS`）150 人 12 周：领奖周 d0.3 9848 → 9852（91.2%），d0.5 9900 → 9906；每周印记 ≤ 1（上限成立）；最快两件极品周数 d0.3 6.17 → 5.91、d0.5 4.69 → 4.75（±0.5 内）。`tools/p1sim/out-p2econ-d160-*.md` |
| 测试 | `EmberRunRulesTest` +2（只领一次、20k 周上限不变式）；实机见 `docs/tests/TEST-ember-realplay-2026-10-04.md` 的 rush 一行 |
| 上线 | 1.65.0 10-04 10:54 CST；随 1.65.1 一起提交（7b1bd1c） |

### 13.79 存档同步（CoreRpg 1.65.0，D161，2026-10-04）

| 项 | 内容 |
|---|---|
| 问题 | persist-roundtrip run 1：取出与断线同 tick → 行 active、无实物；kill -9 后背包（.dat）比 MySQL 旧 → 存进仓库的材料又在背包里（复制） |
| 裁决 | `EmberVault.flushSoon`、存库成功、取出成功、快照恢复后同 tick `p.saveData()`；取出途中下线先做补偿事务（D162 起改为投递行） |
| 测试 | run 2 a–f 48 / 0 |

### 13.80 资产交付闭环（CoreRpg 1.65.1，D162，review §03 A01–A04）

| 项 | 内容 |
|---|---|
| A01 | `cr_p1_delivery`（唯一 owner + request + idx）：分解胚料、取出的装备、撤销要扣回的胚料与装备状态同事务写入；在线回调投递，离线下次进服；`p1dlv_<id>` 标记 / uid 保证恰好一次；不重新随机 |
| A02 | 自动入库 = 物品行 + 创建流水 + 库标记一个同步事务，COMMIT 后才报已入库；失败走背包 / 奖励账本 pending |
| A03 | 权威存储表见 review 附录；工坊扣费：hold → 扣费并同步保存（`p1paid_`）→ 装备事务（作废 hold）；失败释放 → 退款；崩溃 → 进服对账；`cr_players` + `cr_warehouse` 一个事务；P1 事务 / 存档连续 3 次失败暂停资产变更（`EmberAssetGuard`） |
| A04 | 离线恢复只在应用后出队，失败记 attempts / last_error；恢复期间冻结仓库、装备库、工坊、投递、拾取进仓 |
| 测试钩子 | `CORERPG_TEST_FAULTS=1` + `/corerpg p1 fault <玩家> before_commit|after_commit|after_deliver|clear`；线上不带该变量 |
| 测试 | `docs/tests/TEST-ember-persist-roundtrip-2026-10-04.md`：a–f 48 / 0，g 52 / 0；单测 226 全过 |
| 上线 | 10-04 11:31 CST（最终 jar），提交 64a0593；状态 `docs/status/STATUS-ember-rush-persist-d160-d162.md` |

### 13.81 拆分 → 破甲（CoreRpg 1.65.3，D164；取代 D163 的拆分部分）

| 项 | 内容 |
|---|---|
| 来源 | review B01 / B02；`docs/design/DESIGN-ember-build-diversity-2026-10-04.md` §3.4、§7 提案 P7。D163（1.65.2）修好了文案与实现不一致，但模拟显示拆分仍是死选项（分裂精英击杀 0 ～ −2.3%，分身 = 半只近战怪；本体 −20% 让厚甲 / 炽热精英更慢） |
| 裁决 | `t2c` 改名「破甲」：对厚甲精英 ×1.60（正好抵掉它的 1.6 倍生命），对炽热 / 分裂精英（含分身）×0.80。新键 `dmg_affix_shield` / `dmg_affix_blazing` / `dmg_affix_split`，`EmberGrowthService.classMult(m, cls, affix)` 按目标词缀类型相乘；`dmg_affix_body` 不再被任何节点使用（代码保留，读到时仍生效）；`dmg_split` 仍是裂身纹的键。growth.yml `version: 3` |
| 范围 | 只有主线重打的词缀精英（每局 1 只）；首通、挑战、团本、深渊没有词缀精英 → 不受影响 |
| 模拟 | `tools/p1sim/check_d164.py` → `out-d164-armorbreak-check.md`（rules sha256 f23d20ef0ce2779e，n=3000，d0.5，种子 4243 配对；容差 14 情境 × 3 躲避 × 3 种子 × 600 局）。破甲 vs 1.65.2 拆分：厚甲精英击杀用时 Q04 −34.8 / −34.0 / −36.2%（焚烬 / 烬爆 / 炽愈），Q07 −31.7 / −30.5 / −34.6%；分裂精英慢 +1.8 ～ +10.6%；炽热精英与拆分相同（比无成长慢 +14 ～ +20%）；重打通关率 +0.2 ～ +3.2pp，不超过无成长；Q07 挑战不变；42 个首通 / 挑战格三组逐局相同 |
| 测试 | `EmberGrowthTest.t2cIsArmorBreak_D164`（替换 `b01SplitMeansNetPlus50OnClones_D163`）；实机 FreshQ101 强制厚甲精英：伤害日志 `[厚甲] … D141 天赋 ×1.600`，普通怪无倍率 |
| 上线 | 10-04 13:41 CST，状态 `docs/status/STATUS-ember-armorbreak-1.65.3.md`，发布凭证 `docs/status/RELEASE-ember-1.65.3.md` |

### 13.82 裂身纹移出洗练池 + 范围 / 新手提示文案（CoreRpg 1.65.3，D165 / D166）

| 项 | 内容 |
|---|---|
| D165 | `b_split` 加 `rollable: false`。`EmberAffix.pool()` 只返回可洗词条，`roll()` 忽略对已退役词条的锁定，洗练页不给锁定按钮，PAPI 锁定行说明原因；词条池最后一行列「裂身纹：已移出洗练池（洗不出来；已有的照常生效）」。刃池 3 → 2：洗出指定极品期望次数（rerollsim，刃，极品）不锁 15.9 → 10.5、锁定 6.2 → 5.6。`tools/p1sim/realcost.py` 池同样过滤 `rollable: false` |
| D166 | 第二排主题与 t2a / t2b / t2c 正面文案、猎缀纹 / 裂身纹 / 抗缀纹（`note`）加「仅主线重打生效」；图鉴词缀精英条目说明。Q01 r1 `hint`「进门后退到门口打，别站进怪堆」；清完最后一房、等首领时 `PRE_BOSS_HINT`「首领前留 2 瓶药」（所有主线）；R01 `party_hint`「建议队伍里有炽愈」：招募广播、开本、冒险页 R01 图标 |
| 不改 | 数值、奖励、`balance_version`（29） |
| 测试 | `EmberGrowthTest.splitAffixRetiredFromPool_D165`、`EmberRunRulesTest.textHints_D166`；单测 229 / 0。实机：FreshQ100 Q01 kite 首通看到两条提示；FreshQ101 洗练页 / 天赋页文案正确；FreshQ102 收到 R01 招募带「建议队伍里有炽愈」 |

### 13.83 装备结构分阶段计划定稿（D169，纯文档，2026-10-04）

| 项 | 内容 |
|---|---|
| 来源 | RESEARCH `92f77fb`；服主否决按图锁部位；化妆品暂停；成长 sidegrades 待 6 槽复核 |
| 裁决 | 目标 6 槽（刃+护符+4 原版甲）；护甲无攻击、分走护符 H/D（示意 40%/15%×4，Stage 0 定）；每图掉全部位、甲另掉不稀释刃/护符；2 件被动不变 + 4 件改行为；锻造后期选族选部位+可选钉词条类型，保留 8 印记；刃型 Stage 3 定 F 形状。Stage 0 离线模拟；Stage 1+ 各 balance_version + 冒烟 + loss/dup；失败停。yml-only sidegrades 包若 Stage 0+1 久拖可先发，本轮不发 |
| 文档 | `docs/design/DESIGN-ember-gear-staged-plan-2026-10-04.md`；`COORD-gear-structure-hold.txt` → SETTLED PLAN / 结构代码仍 HOLD |
| 不占 | D167（forge-random）、D168（gear8） |

### 13.84 Q02–Q07 战术房提示（CoreRpg 1.65.4，D170，2026-10-04）

| 项 | 内容 |
|---|---|
| 裁决 | 为 Q02–Q07 第一房补短中文 `hint`（语气同 D166）；Q04 r3 补落差提醒；Q01 r1 门口提示保留 |
| 不改 | 战斗数值、variety、技能、`balance_version`（29）、非 hint 键 |
| 版本 | CoreRpg 1.65.4；状态 `docs/status/STATUS-ember-room-hints-1.65.4.md`；发布凭证 `docs/status/RELEASE-ember-1.65.4.md` |
