# 发布凭证 · CoreRpg 1.65.21（2026-10-05）

D181 Variety Affix Pack 3：词缀池 6→8（+mortar 投弹 / molten 亡爆）。设计：`docs/design/DESIGN-ember-variety-affix-pack3-2026-10-04.md`。

## 线上状态（部署后核对于 2026-10-05 Asia/Shanghai）

| 项 | 值 |
|---|---|
| CoreRpg 版本 | **1.65.21** |
| class major | **52**（JDK 8，tools/jdk8u504-b01）|
| CoreRpg JAR sha256 | `390fc0033a36311e74f7f9a3a89c192e98578d2f3268620f417036df1c6ca77e` |
| balance_version | **44** |
| 回滚包 | `/workspace/backup/CoreRpg-1.65.20-pre-1.65.21.jar` |
| 服务器 PID | **1162839** |
| MySQL | `[CoreRpg] [storage] MySQL connected` · `[CoreGacha] [db] MySQL connected` |
| SEVERE | 0 |
| Stage C p1sim | **deferred**（与 D176 同策略）|

## 内容

- `variety.affixes` +`mortar` / `molten`（池 8）；事件池不动
- mortar：周期在最近玩家脚下亮圈（warn≥1.2，kb=0，dmg×atk）
- molten：原词缀精英死亡后尸体延迟预警圈；splitAdds 不触发；退房/卸载取消
- 强制钩子：`/corerpg p1 runs variety mortar|molten`
- 图录/冒险页一行扩到八选一；奖励 `affix_shard` 不变

## 验收

| 项 | 结果 |
|---|---|
| 单测 `mvn -o test`（JDK 8）| 273 / 0 |
| 冒烟 FreshQ330 | PASS：force mortar + 聊天「投弹」+ affix mortar done |
| 冒烟 FreshQ331 | PASS：force molten + 「亡爆」+ 「尸体要炸」chat；SEVERE 0；机器人已退出 |
| 资产回归 | 无物品资产路径变化 → 未跑 persist-roundtrip |
