# DESIGN · 烬斩 SF4 U1 技能页形缓展示诚实（技能 TrMenu · 薄窗 · 可施工）· 2026-10-13

> **状态：** APPROVED · TIP-BP · 批 A·M · 已派余烬-物品 · 不派插件 · 2026-10-13 00:24 Asia/Shanghai  
> **上游 RESEARCH：** [`RESEARCH-ember-skill-sf4-u1-shape-slow-menu-honesty-2026-10-13.md`](RESEARCH-ember-skill-sf4-u1-shape-slow-menu-honesty-2026-10-13.md)  
> **对接 live：** TIP-BO CLOSED live items `64fe9f46e` · TIP-BG CLOSED live `1.65.796-d13031` · `EmberSkillSf4`（MARK_TICKS=20 · SLOW_AMP=0 · HIT_LIM=3）· `ember_skill_kit.yml` Icon `A` · BO 旁债 #12  
> **HOLD 旁注：** **L13 施工 HOLD**（TIP-AI FAIL · PASS≠开工）；Anvil soft densify / topUpTo / 灰印·聚火 / SF4 U2U3 / 调律 live bad 进持件 / 签名 lore / 套装 blurb / 宝石短效果 / 天赋悬停 / 烙纹档幅度 / 副手微量 / 强化率 / 升阶保留再叠 / soft·开孔再叠 **仍冻**  
> **硬禁：** 零 Affix densify · 零伤乘 · 零抬表 · **零改 skill_mult/CD/几何/MARK_TICKS** · 零抬 RATE/upgradeCost · 零抬 talent/gem/副手 flat · 零 soft·开孔再叠 · 零 Anvil densify · 零 topUpTo · 零 SF4 U2U3 · 零灰印/聚火 · 零签名/套装/宝石/天赋/烙纹/副手/强化率/升阶保留展示再叠 · 零扩 stampable · 零再扫甲闸 · 零 SetChase 六槽 densify · 零 ForgeChoice densify · 零 ConvertCap densify · 零 sx20 · 零效果长墙 · 零 lore>3 · 零并轨方案 B

---

## 0. 一句话

技能页烬斩格补 **荐形命中 → 缓速I·1秒**；与 live `EmberSkillSf4` 同口径，不改战斗码。

---

## 1. 问题陈述

| 层 | 现状 | 应然 |
|----|------|------|
| `EmberSkillSf4` / AB | Slow I 1s 已接线；局内「烬斩·形缓」 | **不动数值** |
| 技能页 Icon `A` | 1.5B · 充能 · 不暴击… · **无形缓** | **写出荐形形缓**（≤3） |
| Icon `S` D438 对照 | 「烬爆直·缓速」= 套装落点 | **本窗不改**（禁并表） |
| U2/U3 / soft / 升阶保留 | 冻 / 刚齐 | **零再叠** |

---

## 2. 方案 M（定稿荐）

### 2.1 SoT 锁（只读 · 禁改 Java）

| 键 | 规则 |
|----|------|
| 形缓半行 | 固定文案对齐 **Slow I · 1 秒** + **荐形命中**（无套装·直线） |
| 常量 | `MARK_TICKS=20` · `SLOW_AMP=0` · `HIT_LIM=3`（只读） |
| 形状门 | 文案短写「荐形」；细节不展开套装族名墙 |
| 禁 | 改 EmberSkillSf4 / skill_mult / CD / 几何 / 新 PAPI twin |

### 2.2 PAPI

**本窗零新 PAPI**；不引入动态 fam/shape 键（禁 glance twin）。

### 2.3 TrMenu lore 定稿（≤3）

| 格 | lore 定稿 |
|----|-----------|
| **A** 烬斩 · F | `§a主招 · F · 1.5B · 形状 §e%corerpg_kit_shape%` / `§e荐形命中 → 缓速I·1秒 §8（无套装·直线）` / `§7充能 §f%corerpg_kit_charge% §8· 不暴击/吸血/套计` |

- **S / D / E / F / B / 招架 / 身法：本窗不改**（形状格形缓半行 = 旁债可选）。  
- **禁**改 Open 多行 tell 墙；**禁**把 D438「烬爆直·缓速」改写成 SF4。  
- 每格 lore **≤3**；禁教学墙。

### 2.4 调用方接线

| 点 | 动作 |
|----|------|
| `ember_skill_kit.yml` Icon `A` | lore → §2.3 |
| `EmberSkillSf4` / SkillService / EmberSkillKit | **禁改** |
| 工坊升阶保留 / soft / 开孔 / Affix / U2U3 | **本窗不改** |

### 2.5 验收核（物品）

| 核 | 期望 |
|----|------|
| 烬斩格可见「荐形命中 → 缓速I·1秒」 | 可见 |
| 仍见形状 PAPI + 充能；不暴击/吸血/套计保留短写 | 可见 |
| lore ≤3；无第四行 | 可核 |
| `git show` 无 EmberSkillSf4/skill_mult/CD/几何改 / soft·开孔·Anvil·U2U3 / 升阶保留·强化率·签名·套装·宝石·天赋·烙纹·副手再叠 / Affix densify / 伤乘 / CoreRpg 业务 jar | PASS |

### 2.6 玩家口径（固定）

- 打开技能页，烬斩格一眼看见：**推荐形状打中敌人会挂约 1 秒缓速 I**；没套装时只有直线穿刺才有。  
- 本窗 **不**改伤害、充能、形状命中盒、招架。  
- 仍守 lore≤3。

---

## 3. 验收

- tip + RESEARCH + 本 DESIGN + power-paths 戳可点  
- 批后：**余烬-物品**；**不派插件**；策划 docs-only  
- L13 施工仍 HOLD；Anvil / soft / 开孔再叠 / SF4 U2U3 / 灰印 / 调律 live bad / 升阶保留·强化率·签名·套装·宝石·天赋·烙纹·副手再叠仍冻  

## 4. 旁债（下一 prefer · 非本窗）

| # | 债 | 态 |
|---|----|----|
| 1 | Anvil soft densify / topUpTo | **仍冻** |
| 2 | 灰印 / 聚火 | **仍冻** |
| 3 | SF4 U2 / U3 | **仍冻** |
| 4 | L13 施工 | **HOLD** |
| 5 | 调律 live bad 进持件 | **仍冻** |
| 6 | 签名 lore / 套装 blurb / 宝石 / 天赋 / 烙纹 / 副手 / 强化率 / 升阶保留再叠 | **仍冻** |
| 7 | soft·开孔·GemHole / enhance+5 再叠 | **仍冻** |
| 8 | 形状格 D/E/F 形缓半行（本窗只 A） | **另 tip · 可选** |
| 9 | 升阶「印记另一条路」短指针 | **另 tip · 可选**（工坊邻链 · prefer 再离） |
| 10 | playstyle 入口半行点名形缓 | **另 tip · 可选** |
| 11 | Affix densify / 抬 skill_mult | **仍冻** |
| 12 | cook/AFK 已齐轴再叠 | **仍冻再叠** |

## STATUS: APPROVED · TIP-BP · 批 A·M · 已派余烬-物品 · 不派插件 · 2026-10-13 00:24 Asia/Shanghai
