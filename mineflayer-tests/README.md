# mineflayer-tests（Paper 1.12.2 本地冒烟）

用于连接本机 Paper 1.12.2 服务端的小型 Mineflayer 测试工程。自 2026-09-26 起经代理 `localhost:25565` → 登录服 → 游玩服，用 `lib/proxy-login.js` 的 `joinPlay()` 自动登录。

## 前置条件

- Node.js ≥ 18（本机已装 v20）
- Paper 服务端以 **离线模式** 运行：`online-mode=false`
- 服务端已接受 EULA：`eula=true`

## 安装依赖

```bash
cd /workspace/minecraft/mineflayer-tests
npm install
```

## 运行冒烟测试

先启动服务端（见 `/workspace/minecraft/server-runtime`），再执行：

```bash
npm run smoke
# 或
node scripts/smoke.js
```

可选环境变量：

| 变量 | 默认值 | 说明 |
|------|--------|------|
| `MC_HOST` | `127.0.0.1` | 服务器地址 |
| `MC_PORT` | `25565` | 端口 |
| `MC_USER` | `EmberTestOp` | 机器人用户名（AuthMe 密码自动存入 `secrets/bot-passwords.json`） |
| `MC_TIMEOUT_MS` | `45000` | 登录 + 转服超时（毫秒） |

脚本会：经代理连接 → AuthMe /register 或 /login → 等待转到游玩服 → 聊天 `hello from mineflayer smoke test` → 退出。

`npm run check`（`check-furnace.js`、`check-enchant.js`、`check-craft-fish-combat.js`）：在游玩服控制台执行 `coresmelt/coreenchant/corecraft/corefish/corecombat check`，按当前配置断言输出（如 `NI_registered=5`、`tableEntryCount=7`、`registeredTables=8`），再把样例 NI 物品给机器人并核对中文名。需要下文「控制台 FIFO」。

## 分工说明

- **核心改造（Paper fork）**：酿造台 / 工作台 / 熔炉配方与钓鱼机制
- **自定义物品**：仅通过 **NeigeItems** 插件提供（不要在核心里硬编码物品）
- 插件目录：`/workspace/minecraft/plugins/`（由 server-runtime 挂载）

## 说明

- 使用 `auth: 'offline'`，需服务端 `online-mode=false`
- 协议版本固定为 `1.12.2`

## 玩法套件 `npm run gameplay`

`scripts/gameplay-suite.js`：机器人 `EmberTestOp` 经代理 25565 → AuthMe（`joinPlay()`，密码自动生成并存入已忽略的 `secrets/bot-passwords.json`）→ 游玩服，依次测：

Join → 枢纽菜单 `/ember`（非 op，查标题/关键图标/无未替换 `%占位符%`）→ 熔炉（CoreSmelt）→ 附魔（CoreEnchant，NI `gear_ember_blade` + `crystal_ember_enchant`）→ 钓鱼（CoreFish NI 战利品）→ 图腾（`totem_ember_life`）→ 盾（`shield_ember_guard`）→ 合成（2×2 余烬铁锭→余烬铁板）→ 世界规则 / MythicMobs（`EmberZombie`）。

- 机器人 **不是 op**、本服 **无 LuckPerms**：所有特权步骤（`ni give`、`gamemode`、`mvtp`、`fill`、`mm mobs spawn/kill`）都由 `lib/console.js` 写入游玩服控制台执行，`ops.json` 始终保持 `[]`。
- 测试场地：`world` (3000,200,3000) 高空临时平台，结束时在 `finally` 中清空背包、拆平台、`gamemode 0`、送回 `ember_hub`。不改 gamerule。
- 钓鱼池故意 1 格深且紧贴脚下：2 格深时上钩会把浮漂拖到水下，战利品在水中被岸边方块挡住，捡不到（几何问题，不是 CoreFish bug）。
- 结果写到 `STATUS_PATH`（默认 `/workspace/minecraft/STATUS-gameplay-suite.md`）；调试变量 `FISH_DEBUG=1`、`REEL_MS`（上钩后多久收杆，默认 400）。

### 控制台 FIFO

没有 RCON。自 2026-10-01 起 `server-runtime/start.sh`（以及登录服、代理的 start.sh）自动把服务端 stdin 接到 `server-runtime/console.fifo`，由常驻 `sleep infinity` 持有写端（见 `scripts/console-fifo.sh`），正常 `scripts/ember-up.sh` 或 `server-runtime/start.sh` 启动即可跑本套件。手动下命令：

```bash
/workspace/minecraft/scripts/console.sh play "list"
```

`lib/console.js` 以 O_NONBLOCK 打开 FIFO：服务端没在读时直接报错，不会卡住。可用 `EMBER_CONSOLE_FIFO`、`EMBER_PLAY_LOG` 覆盖路径。

### 其他脚本现状（2026-10-01）

- `trmenu-ember-smoke.js`：已改为 `joinPlay` + 断言（B2.122 回归：团本/深渊点击提示的原始聊天包须含 `【团本】`/`【深渊】` 且保留颜色）。
- 顶层其余 62 个 `*.js` 是历史一次性脚本：仍直连 25565（未走 AuthMe）且靠机器人聊天发 op 命令（本服 `ops.json` 为 `[]`），在当前拓扑下不能直接用，尚未迁移。新测试请用 `lib/proxy-login.js` 的 `joinPlay()` + `lib/console.js`。另外 37 个已用 `joinPlay` 的脚本里，凡发 op 命令的步骤同样需改成控制台。
