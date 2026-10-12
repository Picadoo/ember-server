# RESEARCH · 副手「微量」守势数字诚实（TrMenu 购/炼入口 · 离开烙纹档幅度）· 2026-10-12

> **状态：** CLOSED live items `91300ad65` · TIP-BM · TrMenu reload 77 · jar 未改 · 2026-10-13 00:03 Asia/Shanghai  
> **起因：** TIP-BL CLOSED live 后总控「离开烙纹档幅度链 · 开下一可感变强途径」。BL DESIGN 旁债 #9 已点名「副手「微量」守势数字诚实」。扫 AH…BL / power-paths：NI 持件已写 `物理防御: +2` / `生命力: +8` / `物理防御: +1`；**购/炼入口菜单仍「微量」无数字**——D12199/D12200 lore≤3 压缩把 D289/D294 诚实行（含灰箍 `物理防御: +1`）整段删掉。  
> **上游：** TIP-BL CLOSED live items `2f0507813` · TrMenu reload · docs `65b5250fc` · jar 未改 `1.65.799-d13079` · NI `ember-gear-offhand.yml` / `ember-gear-parts.yml` · `ember_life.yml` O/W · `ember_set.yml` O · `ember_part.yml` A · `ember_forge.yml` P · D289/D294 · power-paths [`DESIGN-ember-power-paths-variety-2026-10-12.md`](DESIGN-ember-power-paths-variety-2026-10-12.md) · TIP-AI CLOSED `STAGE1_L13_OFFLINE=FAIL`（**L13 施工 HOLD**）  
> **硬禁：** **零 Affix densify（禁改 values/weights/pool）· 零伤乘 · 零抬表 · 零抬 talent/gem flat · 零 soft·开孔再叠 · 零 Anvil densify · 零 topUpTo · 零 SF4 U2U3 · 零灰印/聚火 · 零签名 lore 再叠 · 零调律 live bad 进持件 · 零套装 blurb 再叠 · 零宝石短效果再叠 · 零天赋悬停再叠 · 零烙纹档幅度再叠 · 零 SetRules/Formula 抬 · 零扩 stampable · 零再扫甲闸 · 零 SetChase 六槽 densify · 零 ForgeChoice densify · 零 ConvertCap densify · 零 sx20 · 零 skill_mult · 零开 L13 施工 tip · 零效果长墙 densify · 零 lore>3 · 零抬副手 Stat 数值 / 零并轨方案 B**

---

## 0. 一句话

购守腕/生坠、炼灰箍入口用 **既有 NI SoT 数字** 写回 ≤3 行；不改 Stat、不抬功率。

---

## 1. 选题扫描（离开烙纹档幅度 + soft/开孔 + SF4 + 签名/套装/宝石/天赋）

| 候选 | 裁决 | 理由 |
|------|------|------|
| **副手「微量」守势数字诚实** | **采纳** | BL 旁债 #9；SoT 数字已有；D12199/D12200 误删；购前不知 +2/+8/+1；**非抬 flat**；离开烙纹链 |
| 烙纹档幅度 / pin 预览气泡再叠 | **否** | 刚 CLOSED / 离链（pin 气泡仍另 tip） |
| 天赋悬停 / 宝石短效果 / 套装 blurb / 签名 lore 再叠 | **否** | 硬禁离链 |
| SF4 U2U3 / U1 cue twin | **否** | 硬禁 / 离链 |
| Anvil soft densify / topUpTo | **否** | 硬禁仍冻 |
| soft / 开孔 / xp-boost / GemHole 再叠 | **否** | 链已齐 / 硬禁 |
| 灰印 / 聚火 | **否** | 旁债仍冻 |
| L13 施工 | **否** | TIP-AI FAIL；HOLD |
| 甲闸再扫 / 扩 stampable / SetChase 六槽 | **否** | 硬禁 |
| Affix densify / 抬 talent/gem flat | **否** | 硬禁 |
| 调律 live bad 进持件 | **否** | 旁债仍冻 |
| 副手并轨方案 B / 抬 Stat 数值 | **否** | D309 关窗；禁抬 |
| 效果长墙 / lore>3 | **否** | densify / 硬禁 |
| 观察停窗 only | **否** | 有可施工诚实薄片 |

---

## 2. 证据（现网只读）

| # | 事实 | 路径 | 含义 |
|---|------|------|------|
| E1 | `acc_ember_offhand_ward` lore 含 `物理防御: +2` | `ember-gear-offhand.yml` | **持件 SoT 已有** |
| E2 | `acc_ember_offhand_vita` lore 含 `生命力: +8` | 同文件 | **持件 SoT 已有** |
| E3 | `part_ember_ash_brace` lore 含 `物理防御: +1` +「不计入主线…」 | `ember-gear-parts.yml` | **持件 SoT 已有** |
| E4 | life O/W 现网 lore 仅「80 币 / 每周 1 / 购买」——**无效果数字、无「不进 P1」** | `ember_life.yml` | **购前不知变强多少** |
| E5 | D386 前回盘曾写「微量守势 · 不计入主线…」；**D12199 lore≤3 压缩删掉** | `413a1bd7a` vs `ce64fbdd2` | **误删可感信息（同构 BK）** |
| E6 | set O 仅「副手微量守势 · 永不进 P1 B/H」——**无 +2/+8** | `ember_set.yml` | **入口仍糊弄** |
| E7 | part A 现网仅材料×；D294 曾写 `物理防御: +1` + 诚实半行；**D12200 压缩删掉** | `ember_part.yml` · `c66ecdf3b` | **炼前不知 +1** |
| E8 | forge P「副手微量物防 · 不计主线战力」——**无 +1** | `ember_forge.yml` | **入口糊弄** |
| E9 | D289/D294/D309：永不进 P1 B/H · 展示/微量生存 · **禁抬数值 · 禁方案 B** | STATUS / DESIGN offhand | **本窗只展示对齐** |
| E10 | life.yml `offhand_ward/vita` 仅 coin/weekly/give——数值在 NI | `life.yml` | **SoT=NI；菜单对齐** |

### SoT 数字（只读 · 禁改）

| id | 名 | 入口 | 数字 |
|----|----|------|------|
| `acc_ember_offhand_ward` | 余烬守腕 | life O · set 入口 | 物防 **+2** |
| `acc_ember_offhand_vita` | 余烬生坠 | life W · set 入口 | 生命 **+8** |
| `part_ember_ash_brace` | 余烬灰箍 | part A · forge P | 物防 **+1**（薄于守腕） |

---

## 3. 方案方向（荐 M · 详 DESIGN）

1. **定稿短数字半行**：购/炼入口合进价/材料行或独立一行；每格 lore≤3；保留「不进 P1 B/H」。  
2. **施工岗**：批后 **余烬-物品**（只改 TrMenu：`ember_life` O/W · `ember_set` O · `ember_part` A ± 说明 · `ember_forge` P）；**不派插件**（零 jar · 禁改 NI 数值 / StatService / life.yml coin）。  
3. **NI 持件**默认不动（数字已在）；若物品岗发现旧栈 lore 漂移，另记，不本窗扩面。  
4. **零改** Affix/soft/开孔/Anvil/SF4/签名/套装/宝石/天赋/烙纹幅度再叠；零抬副手 flat。

---

## 4. 明确不做

- 改 NI `物理防御`/`生命力` 数字 · StatService 白名单量 · life.yml coin/weekly  
- 副手并轨方案 B · 进 P1 B/H · 物伤/暴击/skill_mult  
- Affix densify · 伤乘 · 抬表 · soft·开孔·Anvil densify · SF4 U2U3 · 灰印/聚火 · L13 施工  
- 签名 lore / 套装 blurb / 宝石短效果 / 天赋悬停 / 烙纹档幅度 / pin 预览气泡再叠  
- 效果长墙 · lore>3  

---

## 5. 对照

| 成熟做法 | 余烬本窗 |
|----------|----------|
| 商店悬停写属性数字再买 | 灰粮购格写 +2/+8 |
| 合成预览写产物属性 | 炼灰箍格写 +1 |
| 禁止「微量」无数字糊弄选择 | 禁购/炼入口定性-only |
| BK 压回 D12200 误删效果 | 同构压回副手数字 |

## STATUS: CLOSED live items `91300ad65` · TIP-BM · TrMenu reload 77 · jar 未改 · 2026-10-13 00:03 Asia/Shanghai
