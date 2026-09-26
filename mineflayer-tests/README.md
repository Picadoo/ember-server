# mineflayer-tests（Paper 1.12.2 本地冒烟）

用于连接本机 Paper 1.12.2 服务端（`localhost:25565`）的小型 Mineflayer 测试工程。

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
| `MC_USER` | `Tester` | 机器人用户名 |
| `MC_TIMEOUT_MS` | `30000` | 连接超时（毫秒） |

脚本会：连接 → 等待 spawn → 聊天 `hello from mineflayer smoke test` → 退出。

## 分工说明

- **核心改造（Paper fork）**：酿造台 / 工作台 / 熔炉配方与钓鱼机制
- **自定义物品**：仅通过 **NeigeItems** 插件提供（不要在核心里硬编码物品）
- 插件目录：`/workspace/minecraft/plugins/`（由 server-runtime 挂载）

## 说明

- 使用 `auth: 'offline'`，需服务端 `online-mode=false`
- 协议版本固定为 `1.12.2`
