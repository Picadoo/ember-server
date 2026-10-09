# STATUS · D387 使魔菜单等级格补 update 刷新 · 2026-10-10

## 债
测岗 D385 PASS 旁注：投喂后同会话菜单重开 lore 仍旧一档（重进服才对）。疑 `ember_pet.yml` I/S/E 缺 `update:`。

## 修
- 文件：`plugins/TrMenu/menus/ember_pet.yml`
- 图标：`E`（出战/收回）、`S`（魂尘投喂）、`I`（使魔状态）——凡挂 `%corerpg_pet_*%` 的格
- 写法：同仓惯例 `update: 20`（与 `ember_p1_afk` / gear / sig 等 PAPI 格一致）
- **未改**：actions、feed 公式、Java/jar、其它菜单、Stage2 开关/bv62

## 运维
- play 热更：`scripts/console.sh play "trmenu reload"` → 日志 `良好 | 70 个菜单已加载 (120 ms)`（**04:02:52 CST**）
- 写盘时 TConfigWatcher 已自动重载 `ember_pet.yml`（04:02:19 CST，6ms）；显式 reload 再钉一次

## 范围外
≠关观察 ≠改 feed ≠开 R ≠抬日表 ≠K3

## 验收建议（测岗）
同会话：开使魔页 → 投喂 → 不重进服、关页再开（或等 ≤1s 格刷新）→ `%corerpg_pet_level_line%` / `feed_hint` 应变新。
