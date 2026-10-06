# RELEASE · CoreRpg 1.65.70（D245 / ARCH S4-4）

| 项 | 内容 |
|---|---|
| 版本 | CoreRpg **1.65.70** · deliverable **D245** · `balance_version` **58**（数量全不变） |
| 玩家可见 | **无**（lore 不变；只有 OP 在 `/corerpg p1 inspect` 看到「来源记录」） |
| 来源键 | NBT `om` 图 / 模式 · `os` 来源行 S## / X## · `or` run id · `ot` unix 秒；DB `cr_p1_item.origin`（`map\|src\|run\|at`，旧件 `''`，写入永不清空）。映射：base_item → 图 / S01（深渊 S13）、宝箱 S34、团本 S12、首通自选 S06、起步包 `starter` / S35、8 印记 `forge` / S28、OP `admin` / X03、未登记 X00 |
| 兼容 | 旧件没有来源、照常有效（v2 规范串 / HMAC 逐字节不变）；回滚到 1.65.69 安全（旧 jar 忽略这列） |
| 机器可读 | `ember-source-map.yml` `item_provenance:` + `EmberSourceMapTest.itemProvenanceMatchesCode` |
| 单测 | **547/0/0**（JDK8；+ `EmberProvenanceTest` 7、`itemProvenanceMatchesCode` 1） |
| 冒烟 | `d245-arch-s4-4-smoke.sh`，FreshQ835–842 + 老号 FreshQ834：每条来源检查都有干净 PASS（含装备库往返、分解 → 撤销 → 取出、loss/dup、重启往返），SEVERE 0；FAIL 只出在脚本问题（已修） |
| 部署 | jar `f1e65f0f61ad9f6f…`（= 源码树）· 10:07 首启 · 现 PID 2487583（冒烟做过两次重启往返） |
| 回滚 | `/workspace/backup/CoreRpg-1.65.69-pre-1.65.70.jar` |
| 余部 | D246 离线 6 槽重跑未完成（部分结果 `tools/p1sim/out-d246-partial.md`，未入库）；Stage 1 仍关闭 |
