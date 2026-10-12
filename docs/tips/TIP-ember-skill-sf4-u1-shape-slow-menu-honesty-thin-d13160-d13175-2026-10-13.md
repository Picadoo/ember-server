# tip · TIP-BP 烬斩 SF4 U1 技能页形缓展示诚实（技能 TrMenu · 可施工薄 · 可感变强途径）· D13160–D13175

> **状态：** **CLOSED live** items `ae8ad1c70` · 批 A·M · TrMenu reload 77 · jar 未改 `1.65.800-d13143` · 2026-10-13 00:25 Asia/Shanghai  
> **上游门禁：** TIP-BO CLOSED live items `64fe9f46e` · TrMenu reload 77 · docs `bab7f4a7c` · jar 未改 `1.65.800-d13143` · TIP-BG CLOSED live `1.65.796-d13031` · RESEARCH [`RESEARCH-ember-skill-sf4-u1-shape-slow-menu-honesty-2026-10-13.md`](../design/RESEARCH-ember-skill-sf4-u1-shape-slow-menu-honesty-2026-10-13.md) · DESIGN [`DESIGN-ember-skill-sf4-u1-shape-slow-menu-honesty-2026-10-13.md`](../design/DESIGN-ember-skill-sf4-u1-shape-slow-menu-honesty-2026-10-13.md) · TIP-AI CLOSED `STAGE1_L13_OFFLINE=FAIL`（**L13 施工 HOLD**）  
> **选题：** 离开升阶保留链 → **烬斩 SF4 U1 技能页形缓展示诚实**（**非工坊 TrMenu 半行 · 非抬 skill_mult · 非改 MARK_TICKS · 非 U2U3 · 非 soft/开孔再叠 · 非 Anvil · 非签名/套装/宝石/天赋/烙纹/副手/强化率/升阶保留再叠 · 非效果长墙 · 非 lore>3 · 非并轨 B**）  
> **施工岗（批后派单）：** **余烬-物品**（`ember_skill_kit` Icon `A` lore）；**不派插件**（零新 PAPI · 零 jar）  
> **策划本窗：** docs-only commit（零业务 jar）  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零改 skill_mult/CD/几何/MARK_TICKS/SLOW_AMP/HIT_LIM · 零抬 RATE/upgradeCost · 零抬 talent/gem/副手 flat · 零 soft·开孔再叠 · 零 Anvil densify · 零 topUpTo · 零 SF4 U2U3 · 零灰印/聚火 · 零签名 lore 再叠 · 零调律 live bad 进持件 · 零套装 blurb 再叠 · 零宝石短效果再叠 · 零天赋悬停再叠 · 零烙纹档幅度再叠 · 零副手微量数字再叠 · 零强化率再叠 · 零升阶保留再叠 · 零 SetRules/Formula 抬 · 零扩 stampable · 零再扫甲闸 · 零 SetChase 六槽 densify · 零 ForgeChoice densify · 零 ConvertCap densify · 零 sx20 · 零开 L13 施工 tip · 零效果长墙 densify · 零 lore>3 · 零并轨方案 B**

---

## 0. 一句话

技能页烬斩格写出荐形命中缓速I·1秒；战斗码已齐，只补展示。

---

## 1. 选题裁决

| 候选 | 裁决 | 理由 |
|------|------|------|
| **烬斩 SF4 U1 技能页形缓展示诚实** | **采纳** | 离开升阶保留；**非**工坊半行；BO #12；BG live 菜单空；**零改**战斗常量 |
| 升阶保留 / 强化率再叠 | **否** | 刚 CLOSED / 硬禁 |
| 印记另一条路短指针 | **否** | 工坊邻链 · prefer 再离 |
| cook / AFK / mainline 甲闸 | **否** | 已齐或硬禁 |
| 签名/套装/宝石/天赋/烙纹/副手幅度再叠 | **否** | 硬禁 |
| SF4 U2U3 / 接线再叠 | **否** | 硬禁 / 离接线链 |
| Anvil soft densify / topUpTo | **否** | 硬禁仍冻 |
| soft / 开孔 / xp-boost / GemHole 再叠 | **否** | 链已齐 |
| 灰印 / 聚火 | **否** | 旁债仍冻 |
| L13 施工 tip | **否** | TIP-AI FAIL；HOLD |
| 甲闸再扫 / 扩 stampable / SetChase 六槽 | **否** | 硬禁 |
| Affix densify / 伤乘 / skill_mult | **否** | 硬禁 |
| 调律 live bad 进持件 | **否** | 旁债仍冻 |
| 效果长墙 / lore>3 | **否** | densify / 硬禁 |
| 观察停窗 only | **否** | 有可施工诚实薄片 |

---

## 2. 方案 M（定稿荐 · docs 本窗 · 批后物品）

| ID | 项 | 验收 |
|----|-----|------|
| D13160 | 本 tip + RESEARCH + DESIGN（上文链） | 路径可点 |
| D13161 | power-paths 进度戳：**TIP-BO CLOSED live**；**TIP-BP OPEN**；旁注 **L13 施工 HOLD** | 戳可见 |
| D13162 | 文首钉死：离开升阶保留；技能页形缓；**非工坊半行**；**非改 MARK_TICKS**；零新 PAPI | 可核 |
| D13163 | 证据链：EmberSkillSf4 常量 · shapeOk · AB 形缓 · Icon A 无形缓 · S≠D438 并表 · BG live | RESEARCH §2 |
| D13164 | SoT 锁：菜单半行 = Slow I·1s + 荐形命中；禁改 Java 常量/skill_mult | DESIGN §2.1 |
| D13165 | **物品：** skill_kit `A` lore → 主招行 / 形缓行 / 充能+约束；≤3 | 可核 |
| D13166 | S/D/E/F/B 本窗不改；形状格形缓另 tip | 可核 |
| D13167 | 玩家口径固定（§2 下） | 可核 |
| D13168 | NON 核：`git show` 无 EmberSkillSf4/skill_mult/CD/几何改 / soft/开孔/Anvil/U2U3 / 升阶保留·强化率·签名·套装·宝石·天赋·烙纹·副手再叠 / Affix densify/伤乘 / CoreRpg 业务 jar | PASS |
| D13169 | HOLD：L13；Anvil densify；topUpTo；soft/开孔再叠；灰印/聚火；U2/U3；调律 live bad；签名 lore；套装 blurb；宝石；天赋；烙纹；副手；强化率；升阶保留再叠；甲闸再扫；扩 stampable；SetChase 六槽；效果长墙；Affix densify/伤乘/抬表/sx20/ForgeChoice densify；抬 skill_mult | 文内硬句 |
| D13170 | 批后派单句：**余烬-物品**；**不派插件**；策划 docs-only | 派单句 |
| D13171 | 验收：烬斩格可见荐形形缓；充能/约束仍在；lore≤3 | PASS |
| D13172 | 冒烟记：开技能页见「荐形命中 → 缓速I·1秒」（待恢复服） | 记 |
| D13173 | D438 Icon S 对照行 / 招架 / 身法 / 烬突：**本窗不改** | 可核 |
| D13174 | Open tell 墙 / playstyle 入口半行：**本窗不扩**（旁债） | 可核 |
| D13175 | 缓冲：错链/错戳/文案漂移；禁借号改 MARK_TICKS / 开 L13 / Anvil densify / soft 再叠 / Affix densify / U2U3 / 升阶保留再叠 | 超额新 tip |

### 玩家口径（固定）

- 打开技能页，烬斩格能看见 **推荐形状命中 → 缓速 I · 约 1 秒**（没套装时只有直线）。  
- 本窗不改伤害、充能时间、形状大小、招架。  
- 仍守 lore≤3。

---

## 3. 对接文件（真 ID · 禁发明表）

| 层 | 路径 |
|----|------|
| RESEARCH | `docs/design/RESEARCH-ember-skill-sf4-u1-shape-slow-menu-honesty-2026-10-13.md` |
| DESIGN | `docs/design/DESIGN-ember-skill-sf4-u1-shape-slow-menu-honesty-2026-10-13.md` |
| SoT 形缓 | `CoreRpg/.../p1/EmberSkillSf4.java`（**只读**） |
| SoT 形状门 | `EmberSkillKit.setShapeSynced` / `SHAPE_*`（**只读**） |
| 菜单 | `plugins/TrMenu/menus/ember_skill_kit.yml` Icon `A` |
| 旁轨 HOLD | L13 · Anvil soft densify · 灰印/聚火 · SF4 U2U3 · 调律 live bad · 升阶保留再叠 · 强化率再叠 · 签名 lore · 套装 blurb · 宝石 · 天赋 · 烙纹 · 副手 · soft/开孔再叠 · Affix densify · skill_mult |

---

## 4. 明确不做

- 任何 `EmberSkillSf4` / `slashLandSlowTicks` / skill_mult / CD / FAN·LINE·RING 命中盒改值  
- Affix densify · 伤乘 · sx20 · ForgeChoice densify · 抬表  
- lore>3 · 效果长墙 · Open tell 教学墙 · D438/SF4 并表  
- SF4 U2/U3 · 灰印/聚火 · soft·开孔·Anvil densify · topUpTo  
- 升阶保留 / 强化率 / 天赋悬停 / 宝石短效果 / 套装 blurb / 签名 lore / 烙纹档幅度 / 副手微量再叠  
- 开 L13 施工 tip · 形状格 D/E/F 本窗扩面 · 新 PAPI

---

## 5. 验收清单

- [x] 总控批 **A · 方案 M**  
- [x] tip + RESEARCH + DESIGN + power-paths 戳可点  
- [x] 文首 HOLD / 选题否决表可核  
- [x] 批后才派 **余烬-物品**；**不派插件**；策划本窗 **无**业务 jar  
- [x] `git show` docs-only（本 commit）；L13 施工仍 HOLD  

---

## 6. 批注（荐）

- **批：** **A · 方案 M** · 已批 · 已派余烬-物品 · 不派插件  
- **选题：** TIP-BO 后 · **离开升阶保留** · **烬斩 SF4 U1 技能页形缓展示诚实**（可感变强途径 · **非工坊半行** · **非改战斗码** · 对位 BO 旁债 #12 / BG live）  
- **批后派岗：** 余烬-物品；不派插件  
- **HOLD：** L13 施工；Anvil densify；topUpTo；soft·开孔再叠；灰印·聚火；SF4 U2U3；调律 live bad 进持件；签名 lore 再叠；套装 blurb 再叠；宝石短效果再叠；天赋悬停再叠；烙纹档幅度再叠；副手微量数字再叠；强化率再叠；升阶保留再叠；甲闸再扫；扩 stampable；SetChase 六槽 densify；效果长墙；**Affix densify**；**skill_mult / MARK_TICKS**  

## STATUS: CLOSED live items `ae8ad1c70` · 批 A·M · TrMenu reload 77 · jar 未改 · 2026-10-13 00:25 Asia/Shanghai
