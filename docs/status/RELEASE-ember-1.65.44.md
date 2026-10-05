# RELEASE · CoreRpg 1.65.44（D213，2026-10-06）

| 项 | 值 |
|---|---|
| 决策 | D213 · ARCH S2-1：经济来源 / 消耗登记表进代码（REG-ember-source-sink-cap §6.1/§6.2 第一步） |
| 内容 | 新纯数据类 `EmberEconomy`：P1 来源 S01–S32、旧来源 L-S1～L-S5、消耗 C01–C18（账户、周期、计数器键族、账本行、p1sim 覆盖、金样数量）。运行时无调用方 |
| 不变 | bv57；所有数值、发放与扣除路径；p1sim / p2econ |
| 测试 | unit **371 / 0**（+`EmberEconomyTest` 8 条：编号、键族在 `EmberCounters`、资产键族有来源 / 消耗、金样 = Java 常量 / 部署的 `ember-v1-runs.yml` / `ember-v1.yml`、未建模清单钉死、旧来源模型外） |
| 配置 | 无改动 |
| jar sha256 | `6890b2f8033bbb253635459b65781399d120891b887da3b729f9c9de0c3abedf`（JDK 8u504，class 52） |
| 备份 | `/workspace/backup/CoreRpg-1.65.43-pre-1.65.44.jar` |
| 服务器 | 02:24 重启；Enabling CoreRpg v1.65.44；CoreRpg / CoreGacha MySQL connected；SEVERE 0 |
| 冒烟 | FreshQ773 真实 Q01 首通 PASS（`docs/tests/smoke-2026-10-06-d213-economy.md`）；资产路径未改 → 不跑 persist-roundtrip |
| 下一步 | S2-2：把签到 / 在线 / 委托等发放改走 `EmberEconomy.grant(SourceId…)`（金样单测保证数量不变）；补 p1sim 徽记账户（REG §5 缺口 1–2，需 tools/p1sim 空闲） |
