# EmberCrypt 掉落对接（NI）

掉落用 MythicMobs 怪物 YAML 内 `NeigeItems.Drops`（NI 自带 MythicMobsHooker），**不是**原版 MM Drops 表。

| Mob ID | 材料（高概率） | 装备（低/较高概率） |
|--------|----------------|---------------------|
| EmberCryptZombie | `mat_ember_shard` 0.85 | `gear_ember_blade` 0.05 |
| EmberCryptSkeleton | `mat_ember_bone_dust` 0.85 | `gear_ember_charm` 0.05 |
| EmberCryptBrute | `mat_ember_core_fragment` 0.75 | blade 0.18 / charm 0.12 |

依赖：`plugins/NeigeItems/Items/` 中需存在上述 ID（物品岗）。重载：`/mm reload` 后 `/ni reload`（或按服惯例顺序）。

刷怪：
```
/mm m spawn EmberCryptZombie
/mm m spawn EmberCryptSkeleton
/mm m spawn EmberCryptBrute
```
