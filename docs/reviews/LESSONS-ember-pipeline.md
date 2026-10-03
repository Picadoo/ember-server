# 余烬服产线经验总结

## 总控
1. 老插件（AP/MM）优先用户拖 jar，别赌论坛直链。
2. 多 bot 必须冻结「唯一文件路径」，否则互相覆盖、复测对错文件。
3. 冒烟进服 ≠ 玩法验收；击杀掉落必须玩家击杀链路。
4. 改 AP lore 前先读服上 attributes 默认键。

## MythicMobs（余烬-怪物）
1. DropTables 里 `command{ni give}` 击杀时可能完全不派发。
2. 背包给物写在 Skills：`command{…} @Trigger ~onDeath`。
3. MM 4.11 有 `<trigger.name>`，**无** `<killer.name>`。
4. 概率写成 `~onDeath >0 0.08` 更稳。
5. `NeigeItems.Drops` 可地面掉；与 `ni give` 双开可能叠双份。
6. Mobs/ 禁止放 `.md`。
7. 同 mob ID 不要拆多文件。
8. 文件名需总控冻结全员同一口径。
9. 验收看背包 + 日志 `ni give <玩家名>`。
10. 材质用 1.12 名。

## NeigeItems × AP（余烬-物品）
1. 键名以 AP `attributes.yml` 为准：`物理伤害/生命力/物理防御`。
2. 格式：`物理伤害: +8`。
3. 先装/先查服再写 lore。
4. 装备只写该件属性，全局替换易串行。
5. ID（`mat_*`/`gear_*`）稳定勿改。
6. YAML 风格与现有 Items 对齐；1.12 材质。
7. runtime 与 plugins 可能同 inode，改一份即可。
8. 多岗改同一 yml 先读后写。
9. 属性行单独成段，方便对接。

## 测试（余烬-测试）
1. nogui 无 stdin：用 mineflayer OP 聊天代发命令。
2. AP 首次起服慢，等 latest.log 的 Done。
3. TabooLib WARN ≠ AP 未加载。
4. Mobs/ 勿放 md。
5. 刷怪带完整世界坐标；HUSK 显示可能像 zombie。
6. 手动 `/ni give` PASS ≠ 击杀掉落 PASS。
7. lore 必须对齐 AP 默认键。
8. 配置文件名单事实源；以磁盘+reload 为准。
9. 只记 PASS/FAIL/SKIP 与复现，不停服干净不结案。

## Paper / Core 插件
（专岗回执后补；总控侧：PERF 补丁打标记+changelog；漏斗勿盲目关 InventoryMoveItemEvent；经验球/刷怪关闭与掉落表 experience 冲突要清。）

## Core* 插件（余烬-插件）
1. Paper 永不依赖 NI；钩子只收 resolver/规则表。
2. 1.12 熔炉 Material 匹配 → FurnaceSmeltEvent + isNiItem。
3. cook/brew 时长数量要 paper-custom 钩子。
4. reload 必须重推 setRule/setResolver。
5. NI：ItemManager.INSTANCE；Items/ 目录；/ni give 验收。
6. 堆叠 ≤127；客户端显示可能仍卡 64。
7. 附魔触媒 INK_SACK:4 + NI ID 拒原版 lapis。
8. MM：~onDeath+@Trigger；无 killer.name；经验球已关勿依赖 experience 掉落。
9. AP lore 键对齐；jar 常需用户拖。
10. 材质锁 1.12；盾/图腾/合成都要事件再验 NI。

## Paper/NMS（余烬-Paper）
1. XP orb 必须 NMS 拒绝，Bukkit 事件不够。
2. 勿开 hopper.disable-move-event。
3. max-tick-time 勿压 50ms（伤 MM）。
4. activation-range 勿过小。
5. PERF 与玩法钩子隔离，标 CoreSystem-PERF。
6. 先配置后 NMS（如 chunk sends）。
7. idle skip ≠ 去掉经验球。
8. 配置备份 + PERF-CHANGELOG。
9. 碰撞预算代码硬化。
10. 内容 YAML 不必重建 paper-custom。
