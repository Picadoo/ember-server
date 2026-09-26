# server-runtime（Paper 1.12.2 本地运行）

## 启动 / 停止

```bash
cd /workspace/minecraft/server-runtime
./start.sh          # auto: 有重建 jar 用重建，否则用 stock
./start.sh stock    # 强制官方 paper-1.12.2-1620.jar
./start.sh rebuilt  # 强制使用 Paper 源码构建产物
./stop.sh
```

配置：`eula=true`，`online-mode=false`，`max-players=40`，端口 `25565`。

## 插件

`plugins/` → `/workspace/minecraft/plugins/`（符号链接）。

放置 **NeigeItems** jar 于此目录即可被 Paper 加载。

**分工**：自定义物品只走 NeigeItems；酿造台 / 工作台 / 熔炉 / 钓鱼机制改 Paper 核心（见 `/workspace/minecraft/HOOKS.md`）。

## JDK

`/workspace/minecraft/tools/jdk8u504-b01`（Temurin 8）。
