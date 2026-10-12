# RESEARCH · 烙纹合成/定向「预览直办」菜单诚实（离开洗练直留链 · SUR1）· 2026-10-13

> **状态：** **OPEN** · TIP-BV · 荐批 A·M · 2026-10-13 00:58 Asia/Shanghai  
> **起因：** TIP-BU CLOSED live 后总控「离开洗练直留链 · 开下一可感变强途径」。硬禁含洗练直留再叠 · forge EQRUS/互换 sure 半行族 · Affix densify · forgeroll 权重 densify。扫 AH…BU / power-paths / BU 旁债：**BrandSure 已 live（聊天「碎片齐·预览直办」「料齐·预览直办」「合成后自动定向」）**，烙纹菜单合成/类型格仍写「聊天栏确认 / 点击预览定向」——**无直办字面**。  
> **上游：** TIP-BU CLOSED live items `f59b9beae` · docs `b7756f94e` · TrMenu reload 77 · jar 未改 `1.65.801-d13207` · `EmberBrandSure` · `EmberGrowthService` brandCraft/brandPin · `ember_p1_brand.yml` N/C/1–5 · power-paths SUR1 · TIP-AI CLOSED `STAGE1_L13_OFFLINE=FAIL`（**L13 施工 HOLD**）  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零改解锁数学 · 零 ConvertCap densify · 零改 WEEKLY_CAP/MARK_TICKS · 零抬 RATE/upgradeCost · 零抬成色/精工 cost · 零抬 swap 收费 · 零抬 talent/gem/副手 flat · 零改 brand craftShards/pin 价/MAX_CHARGES · 零 Anvil densify / soft·开孔再叠 / topUpTo · 零 SF4 U2U3 · 零灰印·聚火 · 零签名 lore / 套装 blurb / 宝石短效果 / 天赋悬停 / 烙纹档幅度 / 副手微量 / 强化率 / 升阶保留 / 形缓 Icon A / 转化保留 / access_* / 成色·精工确定成功 / 互换免费确定 / 洗练直留再叠 · 零调律 live bad · 零并轨 B · 零 SetRules/Formula 抬 · 零扩 stampable · 零再扫甲闸 · 零 SetChase 六槽 densify · 零 ForgeChoice densify · 零 forgeroll 权重 densify · 零 sx20 · 零 skill_mult · 零开 L13 施工 tip · 零效果长墙 · 零 lore>3 · 零改 pity/pool**

---

## 0. 一句话

烙纹页补上「**料齐 · 预览直办** / **路径齐 · 合成后自动定向**」半行，对齐 BrandSure 聊天 SoT；不改价、不改次数、不改 Affix。

---

## 1. 选题扫描（离开洗练直留 + AH…BU 已齐诚实族）

| 候选 | 裁决 | 理由 |
|------|------|------|
| **烙纹合成/定向「预览直办」菜单诚实** | **采纳** | 离开 forge/洗练 sure·keep 半行族；聊天已有直办 · 菜单无；SUR1；零新 PAPI；**非** Affix densify / 烙纹档幅度再叠 |
| 洗练直留 / a–d pity 一眼再叠 | **否** | 刚 CLOSED / BU 旁债 prefer 再离 |
| forge EQRUS / 互换免费确定再叠 | **否** | 硬禁离链 |
| forgeroll 普通格成色权重半行 | **否** | 赌线 · 禁 ForgeChoice densify 边 |
| 分解菜单「确定」字面 | **否** | 弱 · 已有 yield 预览 |
| 升阶「印记另一条路」短指针 | **否** | 升阶邻链 |
| hub/raid/abyss access 扫尾 | **否** | access_* 再叠硬禁 |
| 主线 Q methodLine / hope glance 再叠 | **否** | MG/HD 已齐或 TIP-J 停窗 |
| cook/AFK/attune soft 再叠 | **否** | 链已齐或硬禁 |
| 签名/套装/宝石/天赋/烙纹档幅度/副手再叠 | **否** | 硬禁（档幅度=BL） |
| 成色/精工确定成功 / 强化率 / 升阶保留 / 转化 / 形缓再叠 | **否** | 硬禁 |
| Anvil densify / soft·开孔 / SF4 U2U3 | **否** | 硬禁 |
| 灰印 / 聚火 / L13 施工 | **否** | 仍冻 / HOLD |
| Affix densify / ConvertCap densify / 伤乘 | **否** | 硬禁 |
| 调律 live bad 进持件 | **否** | 旁债仍冻 |
| 效果长墙 / lore>3 | **否** | densify / 硬禁 |
| 观察停窗 only | **否** | 有可施工诚实薄片 |

---

## 2. 证据（现网只读）

| # | 事实 | 路径 | 含义 |
|---|------|------|------|
| E1 | `EmberBrandSure.craftReady`：碎片够 → craft 预览直办 | `EmberBrandSure.java` | **合成直办 SoT 已有 · 零改合成比** |
| E2 | `EmberBrandSure.pinReady`：烙纹+币够 → pin 预览直办 | 同文件 | **定向直办 SoT 已有 · 零改价/次数** |
| E3 | `maybeAutoPinAfterCraft`：路径已选且料够 → 合成后自动定向（每路径一次 latch） | 同文件 + Growth ~D2783 | **路径直办 SoT · 聊天「合成后自动定向」** |
| E4 | 聊天：`烙纹合成 · 碎片齐 · 预览直办` / `烙纹定向 · 料齐 · 预览直办` / `烙纹路径直办 · 合成后自动定向` | `EmberGrowthService` brandCraft/Pin | **玩家面已喊直办** |
| E5 | 菜单 C：`点击：聊天栏确认合成` — **无直办字面** | `ember_p1_brand.yml` C | **合成格缺口** |
| E6 | 类型 1–5：`点击预览定向` — **无料齐直办字面**（档幅度已 BL） | 同文件 1–5 | **定向格缺口（SUR1）** |
| E7 | 说明 N：仓差/定向还差/档随机 — **无直办/自动定向** | 同文件 N | **说明缺口** |
| E8 | Open tell：`先合成…聊天栏确认` — 与料齐直办口径偏旧 | 同文件 Events | **可随本窗半行对齐（可选）** |
| E9 | power-paths SUR1：结果页「已自动保留/直办」表面化；STOP densify twin | DESIGN power-paths §3.4 | **本窗 = 表面化 · 非再叠 sure 数学** |
| E10 | TIP-BL 已写档幅度区间；本窗**不改** values/weights | BL CLOSED | **离烙纹档幅度再叠** |

### SoT 直办（只读 · 禁改价/次数/Affix）

| 键 | 口径 |
|----|------|
| 合成 | 碎片 ≥ need → **预览直办**（免二次确认） |
| 定向 | 烙纹+币够 → **预览直办**（档仍随机 · 成色上限截断） |
| 路径 | 已选 brandpath 且料够 → **合成后自动定向**（每路径 latch 一次） |
| 禁 | 改 craftShards / pin 价 / MAX_CHARGES · Affix densify · 抬表 |

---

## 3. 方案方向（荐 M · 详 DESIGN）

1. **TrMenu only**：`ember_p1_brand` Icon `N`/`C`/`1`–`5` lore 写入直办半行；守 lore≤3；档幅度行保留。  
2. **零新 PAPI**；既有 `forge_gap_brand` / `forge_gap_pin` / `brand_path` **不动**。  
3. **施工岗**：批后 **余烬-物品**；**不派插件**。  
4. **零改** Affix / soft / 开孔 / Anvil / SF4 / access / forge EQRUS / 洗练 / 互换 / 升阶 / 强化率 / 转化 / 形缓 / 烙纹档幅度 values。

---

## 4. 明确不做

- 改 craftShards / pin 价 / MAX_CHARGES / Affix values·weights·pool · Affix densify · ForgeChoice densify · forgeroll 权重 densify  
- 伤乘 · 抬表 · soft·开孔·Anvil densify · SF4 U2U3 · 灰印/聚火 · L13 施工  
- 洗练直留再叠 · forge EQRUS/互换 sure 再叠 · 成色/精工确定成功 · access_* · 升阶保留 / 强化率 / 转化 / 形缓 / 签名·套装·宝石·天赋·烙纹档幅度·副手再叠  
- 效果长墙 · lore>3 · 并轨 B · 新 PAPI · 改 BrandSure Java

## STATUS: OPEN · TIP-BV · 荐批 A·M · 2026-10-13 00:58 Asia/Shanghai
