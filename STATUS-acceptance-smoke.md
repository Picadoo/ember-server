# 余烬 · mineflayer 冒烟验收报告

- **时间**：2026-09-27 15:26–15:28 CST (UTC+8)
- **执行器**：余烬-测试岗
- **工作区**：`/workspace/minecraft`
- **范围**：代理/登录/进游玩服/基础指令与菜单（不含 new-player/newbie 长战斗）
- **约束遵守**：未改 YAML / 玩法数值 / 插件配置 / Paper；临时脚本仅写在 `/tmp`，未提交 git

---

## 1. 总评

**整体：PASS（核心代理链路）**，附带脚本按预期 SKIP/FAIL。

| 计数 | 数量 |
|------|------|
| PASS | 4 |
| FAIL | 1 |
| SKIP | 2 |
| 备注项（非阻塞） | 2（`/hub` 文案不一致；测中后期代理/游玩服掉线） |

核心必做项（端口/JDK、`proxy-smoke.js`、joinPlay 进 play + `/ember` 菜单）均通过。  
`npm run smoke` / `npm run gameplay` 仍无 AuthMe/`joinPlay`，按总控预期记 SKIP/FAIL，**未为变绿而改服务端**。

---

## 2. 逐条结果

| # | 名称 | 状态 | 证据 / 复现命令 |
|---|------|------|-----------------|
| 1 | 环境：端口监听 + JDK + 依赖 | **PASS**（开跑时） | `ss`：`*:25565`、`127.0.0.1:25566`、`127.0.0.1:25567` 均 LISTEN；`JAVA_HOME=/workspace/minecraft/tools/jdk8u504-b01` 可执行；`mineflayer-tests/node_modules/mineflayer` 已存在（未再 `npm install`）。**测中后期**代理踢出文案 `The proxy server is restarting`，随后 25565/25567（及最终 25566）无监听——见第 5 节。 |
| 2 | `node proxy-smoke.js` | **PASS** | `cd /workspace/minecraft/mineflayer-tests && node proxy-smoke.js` → exit 0。`PROXY_RESULT`：`check_direct=PASS`，`check_noauth=PASS`，`check_authed=PASS`。认证号 `ProxyT1` 进 play 后 `/ember` 窗口标题 `余烬 · 冒险枢纽`；UUID 与 offline UUID 一致。直连后端本地被踢（需 Bungee IP forwarding）；外网 IP 直连 ECONNREFUSED（后端仅绑 127.0.0.1，符合预期）。完整 stdout：`/tmp/proxy-smoke-out.txt` |
| 3 | 进游玩服 + 基础指令/菜单（joinPlay） | **PASS** | 现成 `menu-cmd-sweep.js` **未走 AuthMe**，改用临时脚本 `/tmp/menu-cmd-joinplay.js`（未入 git）。`node /tmp/menu-cmd-joinplay.js` → 账号 **`AccT1635`** 注册+登录进 play（坐标约 -18.5,58,110）。`/ember` → 窗口 `{"text":"余烬 · 冒险枢纽"}`。`/corerpg stats`/`level` 回 help（无崩）；补测 `/corerpg status` 有合理状态行；`/corerpg spawn` 回 `spawn EmberCryptZombie x1 OK`。日志：`/tmp/menu-cmd-out.txt`、`/tmp/cmd-extra-out.txt` |
| 3b | 现成 `menu-cmd-sweep.js` | **SKIP** | 脚本直连 `25565` 且无 `joinPlay`，会卡在 AuthMe；本次改用临时 joinPlay 脚本覆盖同目标。复现（预期卡登录）：`node menu-cmd-sweep.js`（需 `CMDS_FILE`） |
| 4 | `npm run smoke`（`scripts/smoke.js`） | **SKIP** | `cd /workspace/minecraft/mineflayer-tests && MC_TIMEOUT_MS=25000 npm run smoke` → **exit 0**，但 bot `Tester` 仅在**登录服** spawn（坐标约 -1.5, **-2**, 17.5），无 `/login`/`/register`，**未进 play**。属假绿，故记 SKIP（非 play 验收）。日志：`/tmp/npm-smoke-out.txt` |
| 5 | `npm run gameplay`（`gameplay-suite`） | **FAIL** | 脚本无 `joinPlay`。开跑时代理已掉：`Error: connect ECONNREFUSED 127.0.0.1:25565`。即便代理在，也会因 AuthMe 卡在登录服。已用 `MC_SUITE_TIMEOUT_MS=90000` + `timeout 180`，未空等超 3 分钟。日志：`/tmp/npm-gameplay-out.txt`。**未改服务端**。 |

### PROXY_RESULT 摘要（条目 2）

```
check_direct = PASS
check_noauth = PASS   # 未认证指令均 "must be authenticated"；respawns=0；无菜单窗
check_authed = PASS   # ProxyT1 登录成功；menu=余烬 · 冒险枢纽；uuid==offline_uuid
```

### 菜单/指令摘要（条目 3，账号 AccT1635）

| 指令 | 结果 |
|------|------|
| `/ember` | PASS — 打开「余烬 · 冒险枢纽」 |
| `/corerpg status` | PASS — 币/签到/活跃等状态行 |
| `/corerpg spawn` | PASS — 刷怪 OK（玩家指令可用） |
| `/corerpg stats` / `level` | 回 help 列表（无子命令），不崩 |
| `/hub` / `/spawn` | **Unknown command**（见备注） |

---

## 3. 使用过的测试账号

| 账号 | 用途 |
|------|------|
| **ProxyT1** | `proxy-smoke.js` 已认证进 play |
| **AccT1635** | 临时 joinPlay 菜单/指令扫（自动 `/register`，密码写入 `secrets/bot-passwords.json`） |
| NoAuth\**** | proxy-smoke 未认证对照（随机名） |
| Tester | `npm run smoke`（仅登录服 spawn，未认证） |
| GpSkip1 | gameplay 尝试连接（ECONNREFUSED，未成功进服） |

---

## 4. 报告路径

`/workspace/minecraft/STATUS-acceptance-smoke.md`

---

## 5. FAIL / 严重与备注复现

### FAIL-1：`npm run gameplay` 无法验收（环境 + 脚本缺口）

1. 确保代理应监听 `127.0.0.1:25565`（本次开跑时已掉线）。
2. `cd /workspace/minecraft/mineflayer-tests && MC_SUITE_TIMEOUT_MS=90000 MC_USER=GpSkip1 npm run gameplay`
3. 观察：`ECONNREFUSED` 或卡在 AuthMe 登录提示，无法完成 furnace/enchant 等套件。
4. **原因**：`scripts/gameplay-suite.js` 无 `joinPlay`；且测中代理/play 进程已退出。  
5. **建议（供总控，本次未改）**：套件改为 `require('../lib/proxy-login').joinPlay`；运维恢复 `proxy-runtime` / `server-runtime` / `login-runtime` 监听。

### 备注-A（非阻塞）：欢迎文案写 `/hub 回城`，但指令 Unknown

1. joinPlay 进 play 后看欢迎消息含「`/hub` 回城」。
2. 发送 `/hub`（及 `/spawn`、`/lobby`、`/server hub`）→ `Unknown command. Type "/help" for help.`
3. 回城可用路径待产品确认（例如仅菜单传送 / `/corerpg` 某子命令）。**本次未改配置。**

### 备注-B：测中后期进程掉线

- 补测过程中 bot 被踢：`The proxy server is restarting`
- 之后 `ss` 无 25565/25566/25567；`proxy-runtime`/`login-runtime`/`server-runtime` 的 java 进程不在。
- **核心 PASS 项均在掉线前完成**；gameplay 受其影响记 FAIL。
- 启动脚本位置（未在本轮执行）：  
  `/workspace/minecraft/proxy-runtime/start.sh`  
  `/workspace/minecraft/login-runtime/start.sh`  
  `/workspace/minecraft/server-runtime/start.sh`

---

## 6. 原始产物

| 文件 | 内容 |
|------|------|
| `/tmp/proxy-smoke-out.txt` | proxy-smoke 完整输出含 PROXY_RESULT |
| `/tmp/menu-cmd-out.txt` | joinPlay 菜单扫 MENU_CMD_RESULT |
| `/tmp/cmd-extra-out.txt` | status/spawn/ember 补测 |
| `/tmp/npm-smoke-out.txt` | npm smoke |
| `/tmp/npm-gameplay-out.txt` | npm gameplay（ECONNREFUSED） |
| `/tmp/menu-cmd-joinplay.js` | 临时脚本（勿提交） |
