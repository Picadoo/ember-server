# STATUS — 余烬地窟 + AttributePlus 3.3.3.0 起服验收

**Updated:** 2026-09-11 18:50 Asia/Shanghai (UTC+8)  
**执行岗:** 余烬服测试专岗  
**总体 verdict: FAIL**（AP 加载与三只怪刷怪 PASS；击杀掉落未触发；装备 lore 属性键与 AP 默认键不一致）

---

## 1. 环境

| 项 | 值 |
|----|-----|
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |
| 服目录 | `/workspace/minecraft/server-runtime` |
| Jar / 模式 | `paper-custom.jar`（`./start.sh custom`） |
| 端口 / online-mode | 25565 / false |
| 插件目录 | `server-runtime/plugins` → `/workspace/minecraft/plugins` |
| 控制台 | `start.sh` 使用 `nohup … nogui > logs/stdout.log`，**无 stdin / 无 rcon**（`enable-rcon=false`）。本验收用 **mineflayer OP 玩家 `Tester`** 发聊天命令代替控制台。 |
| 起服日志 | `logs/latest.log` / `logs/stdout.log` |
| Done | `[10:44:06] Done (1.060s)!`（UTC；约 CST 18:44） |
| 停服 | `./stop.sh` 已执行；进程不在、无 pidfile |

本次验收使用已挂上 `AttributePlus.jar` 后完成首次依赖下载并到达 Done 的 custom 实例（到场时进程已在拉 AP 库）。

---

## 2. 插件 enable 表

| 插件 | 版本 | Loading | Enabling | 结论 |
|------|------|---------|----------|------|
| AttributePlus | **v3.3.3.0** | PASS | PASS | **PASS**（已生成 `plugins/AttributePlus/`） |
| PlaceholderAPI | v2.10.9 | PASS | PASS | **PASS** |
| NeigeItems | v1.21.151 | PASS | PASS | **PASS** |
| MythicMobs | v4.11.0 | PASS | PASS | **PASS**（成功加载 9 怪物 / 5 掉落表） |

### AP 启动异常摘要（非阻断 enable）

- 大量 `ClassVisitException` / `ClassNotFoundException: com.mojang.datafixers.kinds.App`（TabooLib Kether 动作在 1.12.2 上缺 datafixer 类）。**插件仍 Enabling 并完成属性注册。**
- 语言包下载曾 `Connection timed out`；随后仍继续启动。
- 运行中 AP 战斗提示正常（击杀测试可见「对 余烬地窟僵尸 造成 x.xx 点伤害」）。

### 其它日志噪音

- `Cannot load plugins/MythicMobs/Mobs/EmberCrypt-DROPS.md` — Markdown 文档被 MM 当 YAML 扫到。**建议移出 `Mobs/`**（未改配置，仅记录）。
- 示例配置：`ExampleItems.yml` GOLDEN_HELMET、`ExampleDropTables.yml` heroesexp — 与余烬无关。

---

## 3. 重载

| 命令 | 结果 |
|------|------|
| `/mm reload` | PASS — `MythicMobs 重载完毕! 耗时 20 ms`；再次「成功加载 9 个怪物 / 5 个掉落表」 |
| `/ni reload` | PASS — `NeigeItems > 重载完毕` |

---

## 4. 刷怪结果

MM 配置 ID（`plugins/MythicMobs/Mobs/EmberCryptMobs.yml`）：

| Mob ID | Type | 控制台/聊天刷怪 | 实体观察 | 结论 |
|--------|------|-----------------|----------|------|
| `EmberCryptZombie` | ZOMBIE | `[MythicMobs] Spawned 1x EmberCryptZombie!` | zombie @ (-41,65,269)，显示名「余烬地窟僵尸」 | **PASS** |
| `EmberCryptSkeleton` | SKELETON | `Spawned 1x EmberCryptSkeleton!` | skeleton @ (-40,65,271)，「余烬地窟骷髅」 | **PASS** |
| `EmberCryptBrute` | HUSK | `Spawned 1x EmberCryptBrute!` | 客户端显示为 zombie/husk @ (-39,65,270)，「余烬地窟蛮兵」 | **PASS** |

命令格式（MM 4.11）：

```
/mm m spawn EmberCryptZombie 1 world,-41,65,269
/mm m spawn EmberCryptSkeleton 1 world,-40,65,271
/mm m spawn EmberCryptBrute 1 world,-39,65,270
```

终局同时存在 3 只 mythic 怪（mineflayer 计数 3）。  
脚本参考：`/workspace/minecraft/scripts/ember-crypt-setup.txt`（场地 setblock 未全量粘贴；坐标刷怪已覆盖验收）。

---

## 5. 掉落结论

### 配置侧

- DropTables：`plugins/MythicMobs/DropTables/EmberCryptDrops.yml` 已加载（总 5 表含本文件 3 表）。
- 掉落方式：`command{c="ni give <trigger.name> …";asop=true}`（无原生 NI drop type）。
- 映射：Zombie→`mat_ember_shard`(+8% blade)；Skeleton→`mat_ember_bone_dust`(+8% charm)；Brute→`mat_ember_core_fragment`(+25% blade / +20% charm)。

### 实测

| 检查 | 结果 |
|------|------|
| `/ni give Tester <id> 1` 五件物品 | **PASS**（碎片/骨尘/核心/刃/护符均成功给予，lore ID 正确） |
| 玩家近战击杀 `EmberCryptZombie`（生存+钻剑，AP 伤害字滚动，怪死亡） | **FAIL** — 击杀后背包无 NI 物品；`latest.log` **无** 掉落触发的 `ni give` |
| `/minecraft:kill @e` 清怪 | 不期望触发玩家击杀掉落（符合预期） |

**掉落结论：配置与 NI give 通路 OK；击杀 → command drop → `ni give <trigger.name>` 链路未在实战触发。**  
怀疑：` <trigger.name>` 在 MM 4.11 击杀上下文未解析 / 命令静默失败。未改配置。建议后续试 `<killer.name>` / `<player.name>` 或地面 ItemDrop 兜底（见既有 STATUS 备注）。

**复现步骤（掉落 FAIL）：**

1. `./start.sh custom`，等 Done。  
2. mineflayer/`Tester` OP：`/mm m spawn EmberCryptZombie 1 world,-40,65,270`  
3. 生存模式近战击杀至死亡（可见 AP 伤害提示与自定义名）。  
4. 查背包与 `latest.log` 是否出现 `ni give … mat_ember_shard` → 当前无。

---

## 6. AttributePlus 属性键结论

配置：`plugins/AttributePlus/attribute.yml` → `attribute.key`  
日志亦打印「属性 [物理伤害] 注册完毕」等。

### 默认键名（中文标签，摘自生成配置）

**攻击/防御类 (`attackOrDefense`)**  
物理伤害、物理防御、真实伤害、暴击几率、闪避几率、反弹几率、吸血几率、护甲值、箭伤免疫率、雷击几率、冰冻几率、燃烧几率  

**其它 (`other`)**（节选）  
PVP伤害、PVE伤害、PVP防御、PVE防御、生命恢复、暴伤倍率、暴击抵抗、暴击闪避、暴伤抵抗、吸血倍率、吸血抵抗、吸血闪避、护甲穿透、破甲几率、命中几率、反弹倍率、经验加成、蓄力加成、蓄力干扰、燃烧伤害、雷击伤害、冰冻强度、盾牌格挡率、破盾几率、箭矢速度、箭术精准、箭矢穿透率、召唤强度  

**更新类 (`update`)**  
生命力、移速加成、百分比恢复  

常用映射：`attack` → **物理伤害**；`defense` → **物理防御**；`health` → **生命力**。

### 与余烬装备 lore 对比（重要）

| 装备 lore（`ember-dungeon.yml`） | AP 默认键 | 匹配 |
|----------------------------------|-----------|------|
| `攻击力: +8` | **物理伤害** | **不一致** |
| `生命值: +20` | **生命力** | **不一致** |
| `防御力: +3` | **物理防御** | **不一致** |

→ 当前默认 AP **不会**按装备 lore 读到攻击/生命/防御；需改 NI lore 或给 AP 增加别名键（验收未改平衡/配置）。

---

## 7. 总体 PASS/FAIL/SKIP

| 项 | 结论 |
|----|------|
| AP 是否加载 | **PASS**（3.3.3.0 Enabling + 属性注册 + 战斗字） |
| EmberCryptZombie 刷怪 | **PASS** |
| EmberCryptSkeleton 刷怪 | **PASS** |
| EmberCryptBrute 刷怪 | **PASS** |
| 掉落（击杀→NI） | **FAIL** |
| 属性键与装备 lore | **FAIL（键名不匹配）** |
| **总体** | **FAIL** |

服已停。报告路径见下。

---

## 8. 产物与复现命令

- 本报告：`/workspace/minecraft/docs/status/STATUS-ember-dungeon-verify.md`
- 测试脚本：`mineflayer-tests/ember-crypt-verify.js`、`ember-kill-drop.js`
- 起停：

```bash
export JAVA_HOME=/workspace/minecraft/tools/jdk8u504-b01
cd /workspace/minecraft/server-runtime
./stop.sh 2>/dev/null; ./start.sh custom
# 等 logs/latest.log 出现 Done
# 用 OP 玩家执行 /mm reload; /ni reload; /mm m spawn …
./stop.sh
```
