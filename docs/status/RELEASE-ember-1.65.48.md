# RELEASE · CoreRpg 1.65.48（D218，2026-10-06）

| 项 | 值 |
|---|---|
| 决策 | D218 · ARCH S2-4：工坊 / 印记兑换 / 深渊层费 / 天赋 / 洗练 / 烙印 / 调律 消耗走 EmberEconomy（C03–C13） |
| 内容 | `EmberEconomy.spendMark` / `spendInsignia` / `spendMat`（只校验）/ `takes` / `sinkForForge`；`EmberPay.Price.at(sink)` + `sinkRefusal()`（扣之前整单校验，登记不符 = 什么都不扣）；工坊 C03–C06、C07 兑换（数量读 `amount("C07","marks")`）、C08 层费（币 / T3 印记）、C09/C10 天赋币、C11 洗练、C12 烙印、C13 调律解锁；C09/C10 金样（行币 800/2000/4000、respec 2000）钉 growth yml；p1 takeCoin 白名单扫描 |
| 不变 | bv57；所有数量；未改 p1sim / 技能代码；撤销分解（undo）仍是不打标签的直接路径（它不是消耗） |
| 测试 | unit **386 / 0**（+6：天赋金样、工坊 kind→C、每档工坊价被对应 C 全收、Price 标签合并、spendMark/Insignia/Mat、p1 takeCoin 扫描） |
| jar sha256 | `2bc32e21a44e9d1fadd501024e4af533ebc77683f0aea6ce9267cb16dce9f38e`（JDK 8u504，class 52） |
| 备份 | `/workspace/backup/CoreRpg-1.65.47-pre-1.65.48.jar` |
| 服务器 | 03:00 重启；Enabling CoreRpg v1.65.48；CoreRpg / CoreGacha MySQL connected；SEVERE 0；play PID 2079344 |
| 冒烟 | FreshQ779 PASS（docs/tests/smoke-2026-10-06-d218-economy-spend.md）：Q01 首通 + C07 兑换 8 印记 + C03 强化；二会话 papi 4/4 |
| 下一步 | C18 国庆商店 / C15（暂停）/ 投递扣币；S22 挂机庭发放入口；yaml 真源 `ember-v1-economy.yml`；徽记进 p1sim |
