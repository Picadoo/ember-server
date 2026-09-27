# STATUS — LuckPerms + PAPI Player（玩法服）

**日期：** 2026-09-27 20:16 CST（Asia/Shanghai）  
**范围：** Paper 1.12.2 玩法服 `/workspace/minecraft`（`server-runtime/plugins` → `../plugins`）  
**未改：** MM / 主线 quest YAML / 战斗数值 / Paper 核心

---

## 1. LuckPerms jar

| 项 | 值 |
|----|----|
| 文件 | `plugins/LuckPerms-Bukkit-5.4.145.jar` |
| 版本 | **5.4.145**（Bukkit） |
| 来源 | Modrinth `luckperms` → `v5.4.145-bukkit`（CDN 直链）；备份 `plugins/_downloads/LuckPerms-Bukkit-5.4.145.jar` |
| 为何非最新 5.5.x | 5.5.x 部分构建需 Java 11+（class 55）；本服 JDK8。GitHub issue #4274：1.12/Java8 最后已知可用为 **5.4.145** |
| api-version | plugin.yml 标 `1.13`（LP 说明：仅减 remap，**仍兼容 1.12**） |
| 字节码 | loader / 主类 major=52 → Java 8 |
| 存储 | **H2**（默认，`plugins/LuckPerms/luckperms-h2-v2.mv.db`）；未上 MySQL |
| 加载日志 | `[LuckPerms] Successfully enabled` + Vault permission & chat hook |

组数据镜像（人工快照，便于 diff）：`plugins/LuckPerms/ember-lp-setup-snapshot.json`  
（在线以 H2 为准；可用 OP 执行 `/lp export <name>`）

---

## 2. 权限组

### default（weight 0；组名 `default` = LP 自动默认组）

| 类别 | 节点 |
|------|------|
| Multiverse | `multiverse.access.*` · `multiverse.teleport.self.*` · `multiverse.portal.access.*` · `multiverse.core.spawn.self` · `multiverse.core.list.worlds` · `multiverse.core.coord` |
| TrMenu | `trmenu.use` · `trmenu.command.open`（`/ember` 绑定打开主菜单） |
| DungeonPlus | `dungeon.user` · `dungeon.start` · `dungeon.teleport`（jar 内字符串；无独立 plugin.yml permissions） |
| CoreRpg | `corerpg.use` / enhance / socket / covenant / talent / skill / cash / shop / monthly / scrap / reforge / mail / friend / settings / pet / guild / arena / auction / warehouse |
| 显式拒绝 | `corerpg.admin` = **false**（绝不给玩家管理） |
| PAPI / Vault | 无玩家节点；占位符由服务端 parse，经济读走 Vault API |

### admin（weight 100）

- `*` · `luckperms.*`（`*` 已覆盖 `corerpg.admin`）
- **未**自动把任何人塞进 admin；需 `/lp user <名> parent add admin`

### 节点来源核对

- CoreRpg：`plugin.yml` permissions（default:true 子节点在装 LP 后仍显式 set true）
- TrMenu / DungeonPlus：TabooLib 包内字符串（无完整 plugin.yml permissions）
- Multiverse-Core 2.5.0-b727：`multiverse.access.*` / `teleport.self.*` 等

---

## 3. PAPI Player 复核

| 检查 | 结果 |
|------|------|
| jar | `plugins/PlaceholderAPI/expansions/Expansion-player.jar` 存在（22 463 B） |
| 启动日志 | `Successfully registered expansion: player` |
| `/papi list`（OP） | 含 **player**（另有 corerpg / dungeon / ember / localtime / ni / progress / server / spark / team / trmenu / vault） |
| `/papi parse RpgBot %player_name%` | → `RpgBot` |
| `/papi parse RpgBot %player_world%` | → `ember_hub` |

无需 ecloud 补装。

---

## 4. 重启与 ops

```bash
# 玩法服短重启
/workspace/minecraft/server-runtime/./stop.sh
/workspace/minecraft/server-runtime/./start.sh custom
# 或整栈
/workspace/minecraft/scripts/ember-down.sh
/workspace/minecraft/scripts/ember-up.sh
```

- 配 LP 时：停服 → 写 `ops.json`（RpgBot UUID `148ec8b6-253f-36f4-bcd7-bf2395b8df80`）→ 启动 → `mineflayer-tests/op-cmd.js` 跑 `/lp …` → **测后 ops=[]**
- 无 rcon / 无 stdin；控制台命令靠临时 OP 机器人
- **最终 `server-runtime/ops.json` = `[]`**（已确认）
- Bungee 热接过快时偶发 LP pre-login 拒登（「data not pre-loaded」）；隔几秒重连即可。装 LP 后短窗内勿秒切 play。

---

## 5. 非 OP 自检（RpgBot，ops 已空）

| 操作 | 结果 |
|------|------|
| `/gamemode 1` | 无权限（确认非 OP） |
| `/ember` | 打开主菜单（「欢迎回来…」） |
| `/mvtp ember_afk` | 坐标变更至挂机庭 |
| `/mvtp ember_hub` | 回到枢纽（补 `multiverse.core.spawn.self` 后通过） |
| `/hub` | CoreRpg 回城正常 |
| `/corerpg help` | 有权限输出 |

---

## 6. 已知缺口 / 风险

1. **DP 节点未 100% 文档化**：`dungeon.*` 来自 jar 字符串；若进本仍拒，用 `/lp verbose on` 抓真实节点再补。
2. **菜单路径 vs 玩家 mvtp**：灾厄等菜单仍可能走 `console: mvtp`（历史 workaround）；玩家侧现已有 MV 传送权限，可择机改回玩家命令，非本任务范围。
3. **日/周/深渊实例世界**：若 DP 动态世界不在 MV `access.*` 覆盖语义内，进本失败时再按世界名加 `multiverse.access.<世界>`。
4. **admin 组无人在册**：管理号需手动 `parent add admin`（或临时 ops.json）。
5. **LP 5.4.145 非最新**：安全/功能补丁需跟 Java8 兼容构建；勿盲升 5.5+。
6. **代理秒传 play**：见上 pre-login 警告；生产上可在 AuthMe 发送后略延迟，或观察是否复现。

---

## 7. 禁止项遵守

- 未改 MM / quest / 战斗数值 / Paper  
- 未 commit / push  
- default **无** `corerpg.admin=true`
