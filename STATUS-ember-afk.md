# STATUS · 挂机庭 ember_afk 内容加厚

**日期：** 2026-09-13（Asia/Shanghai）  
**范围：** MythicMobs 专用 AFK mob + Spawner 指向；NI 材料掉落；不改方块/全局机制  
**Verdict：** **PASS**

## Mob IDs

| MM ID | Display | HP/Dmg | NI drop（`ni give` @Death） |
|-------|---------|--------|------------------------------|
| `EmberAfkZombie` | 挂机庭僵尸 | 16 / 2 | `mat_ember_shard` ×1 @ **100%** |
| `EmberAfkSkeleton` | 挂机庭骷髅 | 14 / 3 | `mat_ember_bone_dust` ×1 @ **100%** |

- 弱于 `EmberDaily*`（日：24/3、20/4）；无蛮兵/核心/装备  
- 「低于日本」靠弱体 + Spawner CD（8/10/12s），非菜单发奖  
- 单路径：MM `~onDeath` + `@Trigger` → `ni give`（MythicMobs + NeigeItems）  
- 文件：`plugins/MythicMobs/Mobs/EmberAfk.yml`

## Spawners（`ember_afk` ≈ -46,70,250）

| Spawner | MobName | 坐标 | 实况 |
|---------|---------|------|------|
| `EmberAfk_Z1` | EmberAfkZombie | -55,70,250 | MobSpawn 已指向 |
| `EmberAfk_Z2` | EmberAfkZombie | -40,70,255 | MobSpawn 已指向 |
| `EmberAfk_S1` | EmberAfkSkeleton | -48,70,240 | MobSpawn 已指向 |

注：本 MM 4.11 `/mm s set … MobName` 报 invalid；**yml + `/mm reload` 已生效**。

## Flavor

- HD：`ember_afk_hub` @ ember_afk -46,70,250（挂机庭 | 碎片·骨尘 | MM→NI）  
- Hub 菜单 → `mvtp … ember_afk`

## NI 物品（既有）

- `mat_ember_shard` / `mat_ember_bone_dust` ← `NeigeItems/Items/ember-dungeon.yml`

## Smoke（2026-09-13 CST）

| 检查 | 结果 |
|------|------|
| `/mm reload` | PASS · 28 mobs 含 EmberAfk* |
| Spawner MobSpawn | PASS · Zombie/Skeleton 新 ID |
| 名牌 /kill | PASS · `挂机庭僵尸` / `挂机庭骷髅` |
| 击杀碎片 | PASS · `NeigeItems > 你得到了 1 个 余烬碎片` + 背包 redstone lore |
| 击杀骨尘 | PASS · `余烬骨尘` + gunpowder lore `mat_ember_bone_dust` |
| HD hub | PASS · `ember_afk_hub` 3 lines |
