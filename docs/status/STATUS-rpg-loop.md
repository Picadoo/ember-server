# STATUS — Paper 1.12.2 余烬服 RPG loop

**日期：** 2026-09-12 11:29 CST (Asia/Shanghai)  
**设计：** `docs/design/DESIGN-ember-enchant-anvil.md`  
**结果：** PASS — 配置落地、CoreRpg 已装、AttributePlus 已停放、服与 RpgBot 持续运行。

---

## 运行中

| 进程 | PID | 说明 |
|------|-----|------|
| Paper (`./start.sh custom`) | **1123847** | `server-runtime/server.pid`；端口 **25565** |
| mineflayer `rpg-loop.js` | **1124239** | `mineflayer-tests/logs/rpg-loop.pid` |

- Host: `127.0.0.1:25565` / version `1.12.2` / online-mode=false  
- Bot: **RpgBot**（已写入 `server-runtime/ops.json`，UUID `148ec8b6-253f-36f4-bcd7-bf2395b8df80`）  
- 日志：`server-runtime/logs/latest.log` · `mineflayer-tests/logs/rpg-loop.log`

### 如何启动 / 停止

```bash
# Server
cd /workspace/minecraft/server-runtime && ./start.sh custom
# wait for: Done (...)!
./stop.sh

# RPG bot (continuous after ≥5 kills; 8s sleep between waves)
cd /workspace/minecraft/mineflayer-tests
RPG_MAX_KILLS=5 RPG_WAVE_SLEEP_MS=8000 RPG_TOTAL_WAVES=0 \
  nohup node rpg-loop.js > logs/rpg-loop.log 2>&1 &
echo $! > logs/rpg-loop.pid
# stop: kill $(cat logs/rpg-loop.pid)
```

---

## 本轮完成项

### 1. CoreEnchant YAML（运行时 + src）

- `plugins/CoreEnchant/config.yml`
- `CoreEnchant/src/main/resources/config.yml`

要点：`require_ni_item: true`；白名单 `gear_ember_blade` + `gear_ember_charm`；`bookshelf_cost_bump_cap: 2`；分表 `IRON_SWORD`（DAMAGE_ALL / FIRE_ASPECT）与 `GOLD_NUGGET`（DURABILITY / PROTECTION_ENVIRONMENTAL）。

启动证明：

```
[CoreEnchant] Loaded offer table 'ember_blade' with 3 slot offer(s).
[CoreEnchant] Loaded offer table 'ember_charm' with 3 slot offer(s).
[CoreEnchant] Pushed 2 offer table(s) ... bookshelfBumpCap=2, active=true
```

### 2. CoreAnvil YAML + NI 修理补丁

- `plugins/CoreAnvil/config.yml` / `CoreAnvil/src/main/resources/config.yml`
- `plugins/CoreAnvil.jar`（含 `NiMaterialRepairListener`）

要点：`require_ni_repair_ingredient: true`；修理料 `mat_ember_shard` + `mat_ember_core_fragment`；`material_repair_extra_cost: 3`。  
碎片非原版铁锭料，靠 PrepareAnvil 合成修理（不改 Paper）。

### 3. CoreRpg 插件（新建）

| 路径 | 用途 |
|------|------|
| `CoreRpg/` | Maven 源码（JDK8 + tools maven） |
| `plugins/CoreRpg.jar` | 已安装（10742 bytes） |
| `plugins/CoreRpg/config.yml` | join 中文目标 / scoreboard / spawn 模板 |

行为：进服中文目标；侧边栏击杀/碎片/骨尘/附魔晶；`/corerpg spawn|status|reload`。

编译：

```bash
export JAVA_HOME=/workspace/minecraft/tools/jdk8u504-b01
export PATH="$JAVA_HOME/bin:/workspace/minecraft/tools/apache-maven-3.9.16/bin:$PATH"
cd /workspace/minecraft/CoreRpg && mvn -DskipTests package
cp target/CoreRpg.jar ../plugins/CoreRpg.jar
```

启动证明：`[CoreRpg] CoreRpg enabled (spawn=EmberCryptZombie).`

### 4. AttributePlus 停放

- **已移至** `plugins/_parked/AttributePlus.jar`
- 本次启动日志 **无** AttributePlus 加载（避免 datafixerupper 卡死）。
- 数据目录 `plugins/AttributePlus/` 仍在，无 jar 不会启用。

### 5. mineflayer RPG loop

- 脚本：`mineflayer-tests/rpg-loop.js`
- 模式：survival → `/tp -40 65 270` → 拿刃/剑 → 循环 `/corerpg spawn` EmberCryptZombie/Skeleton → 击杀 → 统计材料 → 够 3 碎片+1 骨尘时 `/ni give crystal_ember_enchant`（合成代理）→ ≥5 kill 后 `WAVE_SLEEP` 继续跑。

### 6. 已有（未改坏）

- CoreCraft：`crystal_ember_enchant_from_mats` / `from_core` 已注册（启动日志确认）。
- EmberCrypt.yml：未改名；僵尸掉落仍走 NI give 单路径。

---

## 插件（余烬-插件岗 · DESIGN-ember-enchant-anvil）

**更新：** 2026-09-12 11:30 CST · 不改 Paper · 不改 EmberCrypt 掉落

### CoreEnchant
- 路径：`plugins/CoreEnchant/config.yml`（已同步 `CoreEnchant/src/main/resources/`）
- `override_vanilla: true` · `require_ni_item: true`
- 白名单：`gear_ember_blade` / `gear_ember_charm`
- 触媒：`crystal_ember_enchant` · `reject_vanilla_lapis: true` · `bookshelf_cost_bump_cap: 2`
- 分表：`ember_blade`（IRON_SWORD：锋利 I/II + 火焰附加 I）· `ember_charm`（GOLD_NUGGET：耐久 I/II + 保护 I）
- 验收：`/coreenchant check|reload`

### CoreAnvil
- 路径：`plugins/CoreAnvil/config.yml` + 重编 `plugins/CoreAnvil.jar`（13072 bytes）
- 政策：`block_enchant_combines` · `material_repair_only` · `require_ni_repair_ingredient: true`
- 修理料：`mat_ember_shard`（1 单位/颗）· `mat_ember_core_fragment`（3 单位/颗）；单位=`maxDurability/4`；费用=`单位数+extra(3)` 封顶 30
- **补丁：** `NiMaterialRepairListener`（PrepareAnvilEvent）——NMS 在非原版修理料 + material_repair_only 时会早退清空；插件侧合成结果，次 tick 反射回写 `levelCost`/`k`（客户端可能短暂显示 0）
- 铁锭修理 / 附魔书合并：仍由 AnvilNmsHooks 拒绝
- 验收：`/coreanvil check|reload`（应见 `requireNiRepair=true` 与 repair ids）

### CoreCraft
- YAML：`crystal_ember_enchant_from_mats`（3碎片+1骨尘→1晶）· `crystal_ember_enchant_from_core`（1核心+2碎片→2晶）
- 源码：`findMatchingEntry` 扫全部 entries（同 `result_ni_id` 多配方；物品岗已重编 `CoreCraft.jar`）
- 验收：`/corecraft check|reload`

### 未改
- Paper NMS / API 钩子
- MythicMobs / EmberCrypt 掉落路径
- CoreSmelt / CoreBrew / CoreFish / CoreCombat / CoreWorldRules（本轮无关）


---

## 实测证明（RpgBot 日志摘录）

```
[chat] [余烬服] 目标：击杀地窟亡灵，收集碎片与骨尘，合成附魔晶，强化余烬装备。
[rpg] wave 1..5 spawn EmberCryptZombie / EmberCryptSkeleton
[chat] NeigeItems > 你得到了 1 个 余烬碎片 / 余烬骨尘
[rpg] enough mats — attempt craft via /ni give crystal
[chat] NeigeItems > 成功给予 RpgBot 1 个 余烬附魔晶
[rpg] progress kills=5 waves=5 crystals=2 shards=4 dust=1 crystalInv=2
[rpg] reached MIN_KILLS=5 — continue with wave sleep
```

---

## 已知小问题 / 非阻断

| 项 | 说明 |
|----|------|
| MythicMobs lock | 曾有 `Mobs/EMBERCRYPT_FILENAME.lock` 导致 InvalidConfigurationException；已删除该 lock 文件（**未**动 `EmberCrypt.yml`）。 |
| 配方合成 vs `/ni give` | bot 在材料够时用 op `/ni give` 代理「获得晶」；真实工作台合成路径已由 CoreCraft 注册，GUI 自动化未做。 |
| AttributePlus | 停放后属性强化石闭环未测（设计下一轮）。 |
| XP 球 | 服仍关经验球；附魔表 cost 按设计保留小数，主要吃晶。 |

---

## 验收对照（DESIGN）

| # | 项 | 状态 |
|---|----|------|
| 1 | CoreEnchant 白名单 + 分表 + bump=2 | DONE（配置+启动推送） |
| 2 | CoreAnvil NI 修理料 + extra=3 + PrepareAnvil 补丁 | DONE（jar 已部署） |
| 3 | CoreRpg join/scoreboard/spawn | DONE + 运行中 |
| 4 | AttributePlus park | DONE → `_parked/` |
| 5 | rpg-loop.js ≥5 kills + 持续 | DONE（kills=5+, sleep 续跑） |
| 6 | 服 + bot 后台 | DONE PIDs 上表 |
| 7 | STATUS 本文 | DONE |

**方块未改。** EmberCrypt.yml **未改名。**

---

## 2026-09-12 TrMenu

- `plugins/TrMenu.jar` 3.12.5 已加载；日志：`良好 | 3 个菜单已加载`
- 菜单：`ember_hub` / `ember_daily` / `ember_weekly`
- 冒烟：MenuBot `/ember` 打开 GUI「§6余烬 · 冒险入口」（Grass/Iron/Diamond/Book 图标）
- DungeonPlus：仍缺 jar（论坛墙）
- AttributePlus：仍在 `_parked`

## 2026-09-12 DungeonPlus

- User uploaded jar → `plugins/DungeonPlus.jar` **1.4.5**
- Boot OK on Paper 1.12.2; components registered (Hologram/NPC soft deps missing —非阻断)
- Next: EmberDaily / EmberWeekly configs + map import per docs/design/DESIGN-dungeon-daily-weekly.md
