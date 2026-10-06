# STATUS · ARCH S4-1 · D242（文档 + 测试，无发版）· 2026-10-06

| 项 | 内容 |
|---|---|
| 交付 | D242 / ARCH S4-1 |
| 版本 | **无**：CoreRpg 仍 **1.65.67**（`fa92a49`）/ `balance_version` 58；未改主代码、线上配置、服务器；无部署 / 重启 / 冒烟需求 |
| 权威文档 | `docs/design/DESIGN-ember-gear-structure-2026-10-06.md`（槽位、家族 / 阶级 / 成色 / 精工、两件套觉醒、签名 L01–L15 与徽记 / 烙印 / 双签名 / 调律、强化 + 保底、升阶、8 印记兑换、分解、洗练词条、物品 v2 键、来源表说明、旧文档索引、不一致清单、未来计划状态） |
| 来源表 | `docs/design/ember-source-map.yml`：accounts 19 · item_sources 5 · content 27 · signatures 15 · item_affixes 6 · sources S01–S32 + X01–X03 · legacy LS1–LS5 · sinks C01–C18 |
| 测试 | `CoreRpg/src/test/java/town/sunshine/corerpg/p1/EmberSourceMapTest.java` ×12；全套 **530 / 0**（JDK8 `jdk8u504-b01`，`mvn -o test`）。变异自检：删一个 grant 键 / 经济键 / econ_row → 对应测试失败 |
| 指针头 | P1 书（04–06、09 章）、D169 分阶段、6 槽 Stage 0、D167 烬砧、成长 sidegrade、D174 主线解锁、旧镶嵌、旧掉落表、旧 StatService、构筑多样性、装备调研 |
| 文档修正 | F1 P1 书 §6.4 T2→T3 60/15/6/1800 · F2 §5.2 洗练注 · F3 REG S06 Q02 = 自选族 × 部位 · F4 REG S13 无无尽层 · F5 REG S08 12% 仅适配 · F6 REG C13 调律 6 件 · F7 Stage 0 头注 · F8 D174 签名存物品 v2 · F9 P1 书 §3.2 / §9.4 首通自选 |
| 缺口（未修） | G1 图录阶段币未登记 · G2 宝箱额外件无独立行 · G3 起步包未登记 · G4 `give dup` 产 source=drop · G5 打包 ember-v1.yml 漂移 · G6 挂机庭注释 · G7 团本共用次数注释 · G8 S09 无 yml 键 · G9 p1sim 未建模图录 / 起步 · G10 REG §5 物品级缺口 · G11 bv41 注释「7 件调律版」 |
| 协调 | `/workspace/COORD-arch-s4-1.txt`；`COORD-gear-structure-hold` 仍有效（未改装备结构 Java）；未触碰未入库的 `DESIGN-ember-v1.2-gear8.md` |
| 下一刀 | S4-2：G1–G3 进 `EmberEconomy`（CoreRpg 1.65.68，金额不变，仅打标签）+ p1sim 读 source map；6 槽 Stage 1 仍等服主开工 |
