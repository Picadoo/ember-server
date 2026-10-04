# TEST · 资产持久化往返 + 故障注入（2026-10-04，D161 / D162）

**元数据（review Q01）**

| 项 | 值 |
|---|---|
| 版本 | CoreRpg 1.65.1（提交 64a0593；线上 jar 与该提交重新编译的 jar 内容逐文件相同）· CoreGacha 1.0.1 · Paper 1.12.2 |
| 管理员辅助 | 是：`ni give`、`corerpg p1 give / givedup`、`corerpg coin give`、`corerpg invsnap take / restore`、`gacha admin give-tickets` 造条件；故障点用 `/corerpg p1 fault`（只在 `CORERPG_TEST_FAULTS=1` 启动时可用，测完已不带该变量重启，命令拒绝 = 用例 PASS）。没有削弱怪物（这一套不打怪） |
| 覆盖 | `tools/p1map/persist-roundtrip.sh`：真实 bot 客户端、真实 MySQL、正常重启 ×3、kill -9 ×2（只杀 `server.pid` 且 cwd = server-runtime 的 Paper JVM）；见下表 |
| 仍未覆盖 | 真实断库（停 MySQL）下的「写失败暂停 / 自动恢复」；自动入库（需整局结算掉落）+ 故障；快照恢复中途失败的重试分支；多人同时操作同一件；扭蛋只测断线 / 重启（e），没测故障注入 |

## 结果

| 轮 | jar | bot | 结果 |
|---|---|---|---|
| run 1 | 1.64.2 线上前 | FreshQ52 / Q53 / G07 / Q54 | 40 PASS / 3 FAIL：b 取出与断线同 tick → 行 active 无物品（**真 bug**）；e 两条为脚本 bug；f 假 PASS（bot 留在 fallback），手动重登确认 **kill -9 后复制**（.dat 比 DB 旧） |
| run 2 | 1.65.0（D161） | FreshQ55 / Q56 / G08 / Q57 | a–f **48 / 0** |
| run 3 | 1.65.1 首版（D162） | FreshQ58 / Q59 / G09 / Q60 + Q61 / Q62 | a–f **48 / 0**；g 11 FAIL —— 都是用例设计：mkdups 没有「正在用的 T1 刃」、未消费的故障点串到下一步；以及 **真 bug**：同一工坊请求失败退款后再试被 hold 唯一键挡住 |
| run 4 | 1.65.1（hold 分段 + after_commit 改为回调延后 10 s） | Q63 / Q64（只跑 g） | 42 / 4（辅助函数定义在 a–f 块里、两条断言写法） |
| run 5 | 同上（= 线上） | FreshQ65 / Q66（只跑 g） | **g 52 / 0** |

原始日志：`/workspace/scratch/d160/persist-run{1..5}*.log`（box，本地）。

## 用例（g = 故障注入，守恒：期末 = 期初 + 已确认收益 − 已确认消耗）

| 用例 | 故障点 × 中断 | 断言 | run 5 |
|---|---|---|---|
| a | 存入仓库后 0.3 s 断开 socket → 重启 → 取出 | 仓库 / 背包数目精确 | PASS（run 3） |
| b | 装备库取出与断线同 tick | 行 active 时背包必有 1 件（D162 起进服投递），跨重启稳定 | PASS（run 3） |
| c / c2 | 批量分解 → 整批撤销；分解 → 重启 → 撤销 | 件数、胚料回到期初 | PASS（run 3） |
| d | 离线排队快照恢复 → 重启 → 进服 | 恰好应用一次，队列清除，再重启不重复 | PASS（run 3） |
| e | 扭蛋十连后 30 ms 断线 → 重启 | 钱包 = 账本最后一行，10 券 10 抽，拥有 + 非重复数，CoreRpg 外观标记 | PASS（run 3） |
| f | 存 / 取 / 存装备后 kill -9 | 材料总数不变，取出的在背包，存入的装备无残留实物 | PASS（run 3） |
| g1 | 批量分解 · after_commit（第一笔回调延后 10 s）· 窗口内断线 | 3 件 3 胚料：每件一行投递；进服胚料 = 期初 + 3；再重登不变 | PASS |
| g2 | 取出 · before_commit · 断线 | 仍 stored、无实物、无投递行 | PASS |
| g3 | 取出 · after_commit · 窗口内断线 | 提交后 active、无实物、行 pending → 进服 1 件、delivered → 再重登 1 件 | PASS |
| g4 | 撤销分解 · after_deliver（已扣已存、跳过 ack）· 断线 | 回到装备库；胚料 = 期初 − 1；行 pending → 重登只 ack，仍 = 期初 − 1 | PASS |
| g5a | 工坊强化 · before_commit | 物品不变；碎片 4 + 金币 40 退回，期末 = 期初；退款行 delivered | PASS |
| g6 | 取出 · after_deliver · kill -9 | 实物 1；重启进服只 ack，仍 1 | PASS |
| g3k | 取出 · after_commit · 窗口内 kill -9 | 重启进服 1 件、delivered | PASS |
| g5b | 强化 · after_commit · 窗口内 kill -9 | 已提交（rev 0→1）→ 期末 = 期初 − 费用（碎片 4、金币 40），恰好一次；hold 0 / void 2 | PASS |
| g 收尾 | — | 两 bot 无 pending / hold 行；再重登实物稳定；关掉故障变量后命令拒绝 | PASS |

## 修掉的 bug

1. 取出途中下线：行 active、谁手里都没有（run 1 b）→ D161 先补偿放回，D162 改为同事务投递行、进服到账。
2. kill -9 后复制 / 丢失：仓库在 MySQL、背包在 .dat，时间点不同（run 1 f）→ D161 存仓 / 存库 / 取出 / 恢复后同 tick `saveData`。
3. 离线时分解的胚料、整批撤销的退款、工坊分解的胚料「not given」→ 投递行（A01）。
4. 存入失败且已下线：实物丢 → gear 投递行，下次进服退回。
5. 自动入库先报成功、SQL 还在排队 → 同步单事务（A02）。
6. 玩家行和仓库行两次独立写 → 一个事务。
7. 工坊扣费与装备事务之间崩溃 → hold + 对账（A03）；同请求重试被挡（run 3 发现）→ idx 分段。
8. 离线恢复请求先删后恢复 → 成功后才删（A04）。
