# RESEARCH · 每周转化保留/代价菜单诚实（离开技能页形缓 · 非升阶保留再叠）· 2026-10-13

> **状态：** **CLOSED live** · TIP-BQ · items `a5c3f49d7` · TrMenu reload 77 · jar 未改 `1.65.800-d13143` · 2026-10-13 00:31 Asia/Shanghai  
> **起因：** TIP-BP CLOSED live 后总控「离开技能页形缓 / 升阶保留链 · 开下一可感变强途径」。prefer **非**幅度半行叠层；扫 AH…BP / power-paths：强化率 / 升阶保留 / 形缓 Icon A 已齐；**每周转化**聊天预览已写保留/代价，转化页目标格 **无 lore**、工坊入口只写代价半句——跨族变强途径在点选前静默。  
> **上游：** TIP-BP CLOSED live items `ae8ad1c70` · TrMenu reload 77 · docs `078926fe8` · jar 未改 `1.65.800-d13143` · `EmberConvertRules.plan` · `EmberGrowthService.convertHeld` 预览 · `ember_p1_convert.yml` · forge Icon `V` · power-paths · TIP-AI CLOSED `STAGE1_L13_OFFLINE=FAIL`（**L13 施工 HOLD**）  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零改 MARK_TICKS · 零抬 RATE/upgradeCost · 零抬 talent/gem/副手 flat · 零 soft·开孔再叠 · 零 Anvil densify · 零 topUpTo · 零 SF4 U2U3 · 零改 skill_mult · 零灰印/聚火 · 零签名 lore / 套装 blurb / 宝石短效果 / 天赋悬停 / 烙纹档幅度 / 副手微量 / 强化率 / 升阶保留 / 形缓 Icon A 再叠 · 零调律 live bad · 零并轨 B · 零 SetRules/Formula 抬 · 零扩 stampable · 零再扫甲闸 · 零 SetChase 六槽 densify · 零 ForgeChoice densify · 零 ConvertCap densify · 零 sx20 · 零开 L13 施工 tip · 零效果长墙 · 零 lore>3**

---

## 0. 一句话

转化页点选目标族前写出 **既有** 保留（精工/词条/…）与代价（强化归零 · 成色截卓越）；不改周帽、不改价、不 densify CapSoft。

---

## 1. 选题扫描（离开技能页形缓 + 升阶保留 + 幅度/预览半行族 + soft/开孔 + SF4）

| 候选 | 裁决 | 理由 |
|------|------|------|
| **每周转化保留/代价菜单诚实** | **采纳** | 离开形缓/升阶保留；**非**幅度半行叠；聊天已有保留/代价、菜单目标格空；跨族 = 真变强途径；**零改** WEEKLY_CAP/价/plan |
| 技能页形缓 Icon A / 形状格 D/E/F 再叠 | **否** | 刚 CLOSED / 硬禁离链 |
| 升阶保留 / 强化率再叠 | **否** | 刚 CLOSED / 硬禁 |
| 成色/精工「确定成功」再压 | **否** | hope 已「下次确定升」；弱于转化缺口 |
| 升阶「印记另一条路」短指针 | **否** | 升阶邻链 · prefer 再离 |
| cook / AFK remain 再叠 | **否** | 已齐轴再叠冻 |
| mainline unlock / 甲闸 | **否** | 甲闸再扫硬禁 |
| 签名/套装/宝石/天赋/烙纹/副手幅度再叠 | **否** | 硬禁 |
| SF4 U2U3 / 接线再叠 | **否** | 硬禁 |
| Anvil soft densify / topUpTo | **否** | 硬禁仍冻 |
| soft / 开孔 / xp-boost / GemHole 再叠 | **否** | 链已齐 |
| ConvertCap densify / 抬 WEEKLY_CAP | **否** | 硬禁 · 本窗只展示 |
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
| E1 | `plan()`：quality 截 `QUALITY_CAP=2`（卓越）；enhance→0；**craft/affix/pity/sig/rerollN/origin/sock 保留**；uid 不变 | `EmberConvertRules.java` | **保留/代价 SoT 已锁** |
| E2 | 聊天预览：成色→截 · 强化→+0；**「保留：精工/词条/保底/锻造次数 · uid 不变」**；花费胚+币；可先互换强化 | `EmberGrowthService.convertHeld` !confirm | **聊天已诚实** |
| E3 | 转化页 Icon `1`/`2`/`3`（焚烬/烬爆/承烬）：**仅 name · 无 lore** | `ember_p1_convert.yml` | **点选前看不见保留/代价** |
| E4 | Open tell：`成色截卓越 · 强化归零 · uid 不变` — **无保留半句** | 同上 | **入口半诚实** |
| E5 | 说明格 `N`：还差 gap +「转到另一族」— **无保留/代价表** | 同上 | **说明仍缺 SoT** |
| E6 | 工坊 Icon `V`：跨族 · 成色截卓越 · 强化归零 — **无保留** | `ember_p1_forge.yml` | **进页前只见代价** |
| E7 | TIP-AW CapSoft / CostSoft 已对齐 glance | TIP-AW / AT CLOSED | **本窗禁 ConvertCap/CostSoft densify** |
| E8 | power-paths：展示对齐 > densify；WS2 邻域已齐强化率/升阶保留 | DESIGN power-paths · BN/BO | **换轴到转化环** |

### SoT 菜单短写（只读对齐 · 禁改 Java）

| 键 | 口径 |
|----|------|
| 保留 | 精工 / 词条 / 保底 / 签名 / 孔 / uid（聊天写精工/词条/保底/锻造次数；plan 另保 sig/sock — 菜单合写 **精工/词条/签名/孔**） |
| 代价 | 强化归零 · 成色截卓越（极品→卓越） |
| 不变 | 阶 · 部位 · 每周 1 次（CapSoft 已另对齐，本窗不写 densify） |
| 禁 | 改 WEEKLY_CAP / blanks/coins / QUALITY_CAP / plan / CapSoft MULT |

---

## 3. 方案方向（荐 M · 详 DESIGN）

1. **TrMenu only**：转化页 `1`/`2`/`3` + 说明 `N`（及可选 forge `V`）写入保留/代价半行；守 lore≤3。  
2. **零新 PAPI**：文案钉死 SoT；花费仍靠聊天预览 / 既有 gap。  
3. **施工岗**：批后 **余烬-物品**；**不派插件**（零 jar）。  
4. **零改** ConvertRules / CapSoft / CostSoft / soft / 开孔 / Anvil / SF4 / 升阶保留·强化率·形缓再叠。

---

## 4. 明确不做

- 改 `WEEKLY_CAP` / blanks / coins / QUALITY_CAP / `plan()` / CapSoft / CostSoft MULT  
- ConvertCap densify · Affix densify · 伤乘 · skill_mult · Anvil densify · soft·开孔再叠 · L13 施工  
- 形缓 Icon A / 升阶保留 / 强化率 / 签名 lore / 套装 / 宝石 / 天赋 / 烙纹 / 副手再叠  
- 效果长墙 · lore>3 · 新 PAPI · 并轨方案 B

## STATUS: CLOSED live items `a5c3f49d7` · TIP-BQ · TrMenu reload 77 · jar 未改 · 2026-10-13 00:31 Asia/Shanghai
