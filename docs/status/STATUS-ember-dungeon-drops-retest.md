# STATUS — 余烬地窟击杀掉落复测

**Updated:** 2026-09-11 18:58 Asia/Shanghai (UTC+8)  
**执行岗:** 余烬服测试专岗执行器  
**总体 verdict: PASS**（击杀后背包获得 `mat_ember_shard` / 余烬碎片）

---

## 1. 环境

| 项 | 值 |
|----|-----|
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |
| 服目录 | `/workspace/minecraft/server-runtime` |
| 模式 | `./start.sh custom` → `paper-custom.jar` |
| 端口 / online-mode | 25565 / false |
| 控制台 | nohup nogui，无 rcon/stdin；用 mineflayer OP `Tester` 发命令 |
| Done | `[10:54:20] Done (1.264s)!`（UTC；约 CST 18:54） |
| 停服 | `./stop.sh` 已执行；无 paper 进程、25565 空闲 |

### 插件 enable（本次复测启动）

| 插件 | 结论 |
|------|------|
| MythicMobs v4.11.0 | **PASS** Enabling；成功加载 **9** 个怪物 / **2** 掉落表 |
| NeigeItems v1.21.151 | **PASS** Enabling |
| PlaceholderAPI v2.10.9 | **PASS** |
| AttributePlus | **SKIP（本轮起服）** — AP 拉库 `datafixerupper` 易卡住；为掉落复测曾临时移出（`_parked`），测后 jar 已恢复在 `plugins/AttributePlus.jar`。本轮 Done 日志中无 Enabling AttributePlus。 |

---

## 2. 配置来源（锁定目标 vs 实际文件）

| 项 | 值 |
|----|-----|
| **锁定目标** | `plugins/MythicMobs/Mobs/EmberCrypt.yml` |
| **磁盘实际** | `EmberCrypt.yml`（存在）；**无** `EmberCryptMobs.yml` |
| lock 文件 | `EMBERCRYPT_FILENAME.lock` → `LOCKED: EmberCrypt.yml only` |
| DropTables | 仅 `ExampleDropTables.yml`（无独立 EmberCrypt drops 文件） |
| 配置一致性 | **PASS**（锁定目标 = 实际文件） |

### 最终版掉落写法（来自 `EmberCrypt.yml`）

```
command{c="ni give <trigger.name> mat_ember_shard 1";asop=true} @Trigger ~onDeath
```

另有 `NeigeItems.Drops: mat_ember_shard 1 0.85` 地面掉落兜底。  
骨架/蛮兵同结构（`mat_ember_bone_dust` / `mat_ember_core_fragment`）。

中途总控曾短暂切换文件名指令（EmberCrypt ↔ EmberCryptMobs）；**本报告以最终锁定 `EmberCrypt.yml` 为准**，实测时磁盘即为该文件。

---

## 3. 复现步骤

```bash
export JAVA_HOME=/workspace/minecraft/tools/jdk8u504-b01
cd /workspace/minecraft/server-runtime
./stop.sh 2>/dev/null; ./start.sh custom
# 等 logs/latest.log 出现 Done；确认 Enabling MythicMobs / NeigeItems
# mineflayer OP Tester：
/mm reload
/ni reload   # 可选
/gamemode 0
/clear
/tp -40 65 270
/give Tester diamond_sword 1
/mm m spawn EmberCryptZombie 1 world,-40,65,270
# 近战击杀至死亡
# 查背包是否有 mat_ember_shard（红石 + lore）或聊天「余烬碎片」
./stop.sh
```

脚本：`mineflayer-tests/ember-drops-retest.js`（本岗）；并行亦有 `ember-kill-drop-retest.js`。  
客户端捕获：`/tmp/ember-drops-retest.out`  
服务端归档：`logs/2026-09-11-3.log.gz`

---

## 4. 结果表

| 项 | 结论 | 证据摘要 |
|----|------|----------|
| MM / NI enable | **PASS** | Enabling + 9 怪物 |
| `/mm reload` | **PASS** | `MythicMobs 重载完毕! 耗时 25 ms`；再加载 9 怪物 |
| `/ni reload` | **PASS** | `NeigeItems > 重载完毕` |
| 刷 `EmberCryptZombie` | **PASS** | `Spawned 1x EmberCryptZombie!` |
| 近战击杀 | **PASS** | 生存模式近战；击杀后 NI 回调 |
| 击杀掉落 `mat_ember_shard` | **PASS** | 聊天：`NeigeItems > 你得到了 1 个 余烬碎片`；日志：`成功给予 Tester 1 个 余烬碎片`（`[10:54:54]` 与再测 `[10:55:03]` UTC） |
| 装备 lore AP 键名 | **PASS（配置）** / **SKIP（运行时背包抽样）** | `NeigeItems/Items/ember-dungeon.yml` 已是 `物理伤害` / `生命力` / `物理防御`（旧键备份在 `.bak-ap-lore`）。本岗 lore 运行时 `ni give` 抽检因 Tester 被顶号踢出未完成；配置侧已对齐。 |
| 配置文件名一致性 | **PASS** | 锁定 = `EmberCrypt.yml` = 磁盘实际 |
| AttributePlus 同启 | **SKIP** | 见上；掉落链路不依赖 AP |

---

## 5. 关键日志摘要

```
[10:54:20] Enabling MythicMobs v4.11.0 — 成功加载 9 个怪物
[10:54:20] Enabling NeigeItems v1.21.151
[10:54:20] Done (1.264s)!
[10:54:41] /mm reload → 成功加载 9 个怪物
[10:54:47] /mm m spawn EmberCryptZombie 1 world,-40,65,270
[10:54:54] NeigeItems > 成功给予 Tester 1 个 余烬碎片
[10:55:01] （并行另一 Tester 会话再刷一只）
[10:55:03] NeigeItems > 成功给予 Tester 1 个 余烬碎片
```

客户端：`[chat] NeigeItems > 你得到了 1 个 余烬碎片` 后被 `You logged in from another location` 踢出（并行验收争用同一 OP 名，不影响掉落已触发结论）。

---

## 6. 结论（给总控）

- **击杀掉落：PASS** — 击杀 `EmberCryptZombie` 后玩家获得 **mat_ember_shard（余烬碎片）**
- **lore 键名：PASS（配置已对齐 AP）** — `物理伤害` / `生命力` / `物理防御`；运行时背包抽检 SKIP
- **配置来源：`Mobs/EmberCrypt.yml`**（`<trigger.name>` + `@Trigger ~onDeath`）
- **总体 verdict：PASS**
- **报告路径：** `/workspace/minecraft/docs/status/STATUS-ember-dungeon-drops-retest.md`
- **异常摘要：** AP 起服偶发卡在 datafixerupper 下载（已 seed libs）；并行 bot 顶号踢出；文件名指令曾反复切换但终态与锁定一致为 EmberCrypt.yml
- **服状态：已停**
