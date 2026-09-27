# STATUS · 挂机庭 ember_afk 内容加厚

**日期：** 2026-09-27（Asia/Shanghai）
**范围：** MythicMobs 专用 AFK mob + Spawner 指向；NI 材料掉落；阶段 3 T3/T4 战斗与 `afk_caps` 日顶验收；不改方块/全局机制
**Verdict：** **✅ 阶段 3（1.14.0）验收通过**

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

## 阶段 3 实战验收（2026-09-27，Asia/Shanghai）

报告：`STATUS-afk-tier-t3-t4.md`。验收标准为 deaths=0、minHpPct≥30%、ttkMed≈3 秒。

| 层 | 首跑 / 复测 | 结果 |
|---|---|---|
| T=4 烬原深处 Lv40 | 首跑：deaths=0、ttkMed=3.2、minHpPct=33、kills=40 | **PASS** |
| T=3 焦土 Lv30 | 首跑：deaths=0、ttkMed=3.6、minHpPct=1（被霜骸射击） | **FAIL** |
| T=3 焦土 Lv30 | 调伤后复测：deaths=0、minHpPct=70、ttkMed=2.8、kills=36 | **PASS** |

### T=3 平衡调节

仅改 `plugins/MythicMobs/Mobs/EmberAfk.yml`：

- `EmberAfk3Stray` Damage **4→2**（余烬霜骸）。
- `EmberAfk3Zombie` Damage **5→4**（余烬焦兵）。
- Health、掉落、刷怪点保持不变；T4 保持不变。

调节后当前实况数值：焦兵 **100/4**、霜骸 **85/2**、灼尸 **120/6**、凋骸 **110/6**（Health/Damage）。`ops.json` 保持为 `[]`。

## `afk_caps` 日顶合并验收（2026-09-27，Asia/Shanghai）

报告：`STATUS-afk-caps.md`。三项均 **PASS**：跨层计数合并、上限提示文案、达顶后约 25% 掉率（40 次碎片击杀实测 +12，约 30%，样本波动可接受）。`CapM4518` 在 T1 结束为 `12/15`，切换 T2 后仍为 `12/15` 并继续到 `28/15`；配置已恢复 `shard/kill_coin=150/150`，`ops.json=[]`。

**阶段状态：** T3/T4 战斗与 `afk_caps` 跨层日顶合并均已通过；阶段 3（1.14.0）验收完成。
