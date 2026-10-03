# Ember Stack Display（Forge 1.12.2 客户端模组）

## 作用

自定义 Paper 服务端（CoreBrew）允许炼药相关 NI 物品叠加到 **127**。原版客户端物品栏/快捷栏角标有时看起来仍像按 64 封顶。

本模组在 **客户端** 替换 `RenderItem`，强制 GUI 叠加层绘制 **真实的** `ItemStack.getCount()`（含 65–127），**不会**把显示数字钳制到 64。三位数（如 `127`）会略微缩小字体以塞进格子角落。

## 安装

1. 安装 **Minecraft 1.12.2** + **Forge 1.12.2-14.23.5.2860**（或同系列最后推荐版）。
2. 将成品 jar 复制到客户端 `mods` 文件夹：
   - 仓库产物：`/workspace/minecraft/client-mods/ember-stack-display-1.12.2.jar`
   - 或本工程 `build/libs/ember-stack-display-1.0.0.jar`
3. 启动客户端；日志中应出现：
   `Installed StackAwareRenderItem wrapper (real stack counts in GUI)`
4. 配合服务端 CoreBrew `stack.max_stack: 127` 使用。

本模组声明 `clientSideOnly=true`，**不要**装到服务端。

## 重要说明（显示 ≠ 手感）

| 能力 | 本模组是否保证 |
|------|----------------|
| 物品栏 / 快捷栏 **数字显示** 65–127 | **是**（目标功能）|
| 鼠标中键拆分、拖拽半组、合成格交互 | **否** — 仍可能受原版 `getMaxStackSize()`（常为 64 或药水 1）影响 |
| 服务端真实堆叠上限 | **否** — 由 Paper/CoreBrew NMS hook 负责 |
| 把任意物品 maxStack 改成 127 | **否** — 本模组不改物品上限 |

结论：**只保证显示**；拆分/合成手感仍可能受原版 maxStack 影响。若需要完整客户端交互对齐，需另行 patch `Item.getItemStackLimit` / `ItemStack.getMaxStackSize`（未包含在本版本）。

## 从源码构建

```bash
export JAVA_HOME=/workspace/minecraft/tools/jdk8u504-b01   # Java 8
export PATH="$JAVA_HOME/bin:$PATH"
cd /workspace/minecraft/ember-stack-display
./gradlew build --no-daemon
cp build/libs/ember-stack-display-1.0.0.jar \
   /workspace/minecraft/client-mods/ember-stack-display-1.12.2.jar
```

- ForgeGradle 3 + mappings `stable_39-1.12`
- Forge：`1.12.2-14.23.5.2860`

## 技术实现摘要

1. `FMLInitializationEvent`（CLIENT）用反射把 `Minecraft.renderItem` 换成 `StackAwareRenderItem`。
2. `StackAwareRenderItem` 继承 `RenderItem`，复用原 `ItemModelMesher`，避免模型注册丢失。
3. 覆盖 `renderItemOverlayIntoGUI`：先把 count 临时设为 1 调用 `super`（只画耐久/冷却），再恢复 count，用 `FontRenderer.drawStringWithShadow` 画完整整数；宽数字 `GlStateManager.scale` 缩小。

## 路径

| 内容 | 路径 |
|------|------|
| 工程 | `/workspace/minecraft/ember-stack-display/` |
| 客户端 jar | `/workspace/minecraft/client-mods/ember-stack-display-1.12.2.jar` |
| 构建状态 | `/workspace/minecraft/docs/status/STATUS-stack-display-mod.md` |
