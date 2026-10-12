# RESEARCH · 烬斩 SF4 U1 技能页形缓展示诚实（离开升阶保留 · 非工坊半行）· 2026-10-13

> **状态：** OPEN · TIP-BP · 待批 A·M · 2026-10-13 00:21 Asia/Shanghai  
> **起因：** TIP-BO CLOSED live 后总控「离开升阶保留链 · 开下一可感变强途径」。prefer **非**工坊 TrMenu lore 半行；扫 AH…BO / power-paths / BO 旁债 #12：SF4 U1_sl10 **已 live**（TIP-BG），技能页仍不写形缓。  
> **上游：** TIP-BO CLOSED live items `64fe9f46e` · jar 未改 `1.65.800-d13143` · TIP-BG CLOSED live `1.65.796-d13031` · `EmberSkillSf4` · `ember_skill_kit.yml` · power-paths · TIP-AI CLOSED `STAGE1_L13_OFFLINE=FAIL`（**L13 施工 HOLD**）  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零抬 RATE/upgradeCost · 零抬 talent/gem/副手 flat · 零 soft·开孔再叠 · 零 Anvil densify · 零 topUpTo · 零 SF4 U2U3 · 零改 skill_mult/CD/几何/MARK_TICKS · 零灰印/聚火 · 零签名 lore / 套装 blurb / 宝石短效果 / 天赋悬停 / 烙纹档幅度 / 副手微量 / 强化率 / 升阶保留再叠 · 零调律 live bad · 零并轨 B · 零 SetRules/Formula 抬 · 零扩 stampable · 零再扫甲闸 · 零 SetChase 六槽 densify · 零 ForgeChoice densify · 零 ConvertCap densify · 零 sx20 · 零开 L13 施工 tip · 零效果长墙 · 零 lore>3**

---

## 0. 一句话

技能页写出 **既有** SF4 U1_sl10：荐形命中 → Slow I · 1s；不改战斗码、不抬伤。

---

## 1. 选题扫描（离开升阶保留 + 工坊幅度/预览半行族 + soft/开孔 + SF4 接线）

| 候选 | 裁决 | 理由 |
|------|------|------|
| **烬斩 SF4 U1 技能页形缓展示诚实** | **采纳** | 离开升阶保留；**非**工坊 TrMenu 半行；BO 旁债 #12；BG live 效用菜单不可见；**零改** MARK_TICKS/skill_mult |
| 升阶保留 / 强化率 / 成色精工再叠 | **否** | 刚 CLOSED / 硬禁离链 |
| 升阶「印记另一条路」短指针 | **否** | 仍工坊升阶邻链 · prefer 再离 |
| 签名/套装/宝石/天赋/烙纹/副手幅度再叠 | **否** | 硬禁 |
| cook cost peek / AFK remain | **否** | life/AFK 花费·remain 已齐；cook soft 再叠冻 |
| mainline unlock cue / 甲闸 | **否** | 2c 真债甲签名；甲闸再扫硬禁 |
| SF4 U2/U3 / 接线再叠 | **否** | 硬禁 / 离接线链 |
| Anvil soft densify / topUpTo | **否** | 硬禁仍冻 |
| soft / 开孔 / xp-boost / GemHole 再叠 | **否** | 链已齐 |
| 灰印 / 聚火 | **否** | 旁债仍冻 |
| L13 施工 | **否** | TIP-AI FAIL；HOLD |
| Affix densify / 伤乘 / skill_mult | **否** | 硬禁 |
| 调律 live bad 进持件 | **否** | 旁债仍冻 |
| 效果长墙 / lore>3 | **否** | densify / 硬禁 |
| 观察停窗 only | **否** | 有可施工诚实薄片 |

---

## 2. 证据（现网只读）

| # | 事实 | 路径 | 含义 |
|---|------|------|------|
| E1 | `MARK_TICKS=20` · `SLOW_AMP=0` · `HIT_LIM=3` | `EmberSkillSf4.java` | **U1_sl10 SoT 已锁** |
| E2 | `shapeOk`：fam 空/none → LINE；否则 `setShapeSynced` | 同上 | **荐形门 = 套装推荐 / 无套装直线** |
| E3 | 成功上缓 → ActionBar `烬斩·形缓` | 同上 | **局内已有短 cue；菜单无** |
| E4 | TIP-BG CLOSED live `1.65.796-d13031` | power-paths / tip | **接线已上线 · 非本窗再改码** |
| E5 | 技能页 Icon `A` 烬斩 lore：1.5B · 充能 · **「不暴击·不吸血·不触发套装计数」— 无形缓** | `ember_skill_kit.yml` | **点技能页不见可感变强** |
| E6 | Icon `S` 对照行写「烬爆直·缓速」= D438 套装落点，**≠** SF4 U1 | 同上 | **禁把两套缓并成一条谎表** |
| E7 | 形状格 D/E/F：几何 + 当前形状 · **无形缓半行** | 同上 | 本窗可只改 `A`（薄）；形状格另 tip |
| E8 | `slashLandSlowTicks` / skill_mult / CD / 几何 · **本窗只读** | `EmberSkillKit` / `SkillService` | **零改战斗数学** |
| E9 | playstyle `S` 入口仅「形状预览」· 无形缓 | `ember_p1_playstyle.yml` | 深链入口；本窗主改 skill_kit |
| E10 | power-paths：展示对齐 > densify；BO 旁债 #12 点名本轴 | DESIGN power-paths · BO DESIGN §4 | **对位可感变强** |

### SoT 形缓口径（只读 · 禁改 Java）

| 键 | 口径 |
|----|------|
| 效用 | 荐形命中 → **Slow I · 1 秒**（20t）· 最多 3 目标 |
| 形状门 | 有套装：当前形状 = 套装推荐；无套装/`none`：仅 **直线** |
| 空挥 | 无命中 → 无缓 |
| 禁 | 改 MARK_TICKS / SLOW_AMP / HIT_LIM / skill_mult / CD / 几何 / 打包 U2U3 |

---

## 3. 方案方向（荐 M · 详 DESIGN）

1. **TrMenu only**：`ember_skill_kit` Icon `A` lore 写入形缓半行（对齐 E1–E3）；守 lore≤3。  
2. **零新 PAPI**：文案钉死 SoT 常量；不读动态 fam（避免 glance twin）。  
3. **施工岗**：批后 **余烬-物品**；**不派插件**（零 jar）。  
4. **零改** SF4 Java / soft / 开孔 / Anvil / 工坊升阶保留再叠 / 签名·套装·宝石·天赋·烙纹·副手·强化率。

---

## 4. 明确不做

- 改 `EmberSkillSf4` / `slashLandSlowTicks` / skill_mult / CD / FAN·LINE·RING 命中盒  
- SF4 U2/U3 · 灰印/聚火 · Anvil densify · soft·开孔再叠 · L13 施工  
- 升阶保留 / 强化率 / 签名 lore / 套装 blurb / 宝石 / 天赋 / 烙纹 / 副手再叠  
- 把 D438 套装缓与 SF4 形缓写成同一「缓速表」 · 效果长墙 · lore>3 · 新 PAPI

## STATUS: OPEN · TIP-BP · 待批 A·M · 2026-10-13 00:21 Asia/Shanghai
