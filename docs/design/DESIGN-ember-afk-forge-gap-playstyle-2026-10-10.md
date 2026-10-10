# 余烬 · 成长花样 · 挂机→烬砧仓差诚实环（D432）

STATUS=**已批 A · 方案 M · D432 · 施工中** · 2026-10-10 · tip [`STATUS-ember-next-hard-debt-afk-forge-gap-need-design-2026-10-10.md`](../status/STATUS-ember-next-hard-debt-afk-forge-gap-need-design-2026-10-10.md) · backlog `B-afk-forge-gap`（**已批·施工中**）· STATUS [`STATUS-ember-afk-forge-gap-d432-2026-10-10.md`](../status/STATUS-ember-afk-forge-gap-d432-2026-10-10.md) · **≠关观察 ≠抬日表 ≠sx20 ≠天赋/灰印 ≠改价**

**上游：** D431 每周转化 PASS（jar 1.65.133-d431）· Stage2 观察至 **≥17:40 CST**。

## 0. 玩法

仓内挂机材料对照烬砧价，显示「还差多少」才能：合成烙纹 / 随机锻一次 / 转化一次。**零改** `afk.tiers` / `daily_kills`。

## 1. 方案对照

| 方案 | 内容 | 裁定 |
|------|------|------|
| **M（荐）** | PAPI `forge_gap_*` + 挂机战况/配方速览/烬砧菜单互指 | **主推** |
| A | 只口号 | **否决** |
| L | 抬产量表 | **否决** |
| W | sx20 | **否决** |

### 批 A 勾选

- [x] **方案 M**
- [x] 否决 A / L / W
- [x] 批注：总控 **已批 A · 方案 M · D432** · 2026-10-10 12:04 CST · 只读镜像 D429/D430/D431 价 · **禁止抬 afk 日表**

## 2. PAPI 键（%corerpg_p1_*%）

| 键 | 含义 |
|----|------|
| `forge_gap_brand` | 烙纹合成碎差（需 40，镜像 EmberBrandRules.CRAFT_SHARDS） |
| `forge_gap_roll` | 随机锻：`胚差N·币差M`（4 胚 + 500 币） |
| `forge_gap_convert` | 转化：手持阶价，无持件按 T1；`胚差N·币差M` |

仓量读 EmberVault（挂机绑定仓）；币读 PlayerData。

## 3. 验收

1. 空仓：brand=40、roll 胚差4·币差500、convert T1 胚差2·币差300。  
2. 存入材料后差变小。  
3. 零改 afk yml 产量键。  

*D432 · 已批 A·M · 施工中。*
