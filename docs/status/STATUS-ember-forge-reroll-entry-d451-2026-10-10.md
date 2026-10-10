# 状态 · D451：工坊挂词条洗练入口 · PASS

**日期：** 2026-10-10  
**裁决：** **已批 A · 批 M · D451**  
**版本：** jar **1.65.145-d451.local** · bv **未抬**  
**性质：** 工坊第四选择可见化 + `from forge` 返回；**零改**洗练价表 / 词条池 / lock_shard

## 交付

| 项 | 状态 |
|----|------|
| `ember_p1_forge` 底栏 W「词条洗练」 | lore 明示普通洗 / 锁定保类型（D148） |
| `REROLL_FROM.forge` → `ember_p1_forge` | 返回「返回工坊」 |
| 单测 `rerollFromForge_mapsToForgeMenu_D451` | OK |
| 冒烟 FreshQ991 | `/ember_p1_forge` 见洗练 tell；`from forge`→洗练；`back`→锻造；NBT「返回工坊」 |
| MySQL/SEVERE | connected ×2 · SEVERE0 |
| 误 tip「洗练保类型另开」 | **关闭**（D148 已覆盖） |
| EmberSetRules / AFK / skill_mult | **未改** |

## 禁夹带

sx20 · 聚火 · 抬日表 · Stage2＜17:40 关观察 · K3 · 改 lock 价

## 下债

≥17:40 CST Stage2 绿出准备（既有 tip）；≠本号关观察。
