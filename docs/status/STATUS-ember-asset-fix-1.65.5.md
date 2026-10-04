# 状态 · CoreRpg 1.65.5 资产安全修复（D172，2026-10-04）

只修漏洞：价格、概率、保底、印记数量、装备结构（`COORD-gear-structure-hold`）、`balance_version`（29）都不变。来源：D167 烬砧设计 §5 漏洞审查（X5 / X1 / X4 / X15）列的前置。协调：`/workspace/COORD-asset-fix-1655.txt`。

## 修了什么

| # | 漏洞（1.65.4） | 1.65.5 |
|---|---|---|
| X5 | `EmberGearLib.undo` / `undoBatch`：主线程查胚料 → 异步 commit → **之后**才用负数送货行扣。中间把胚料花掉（锻造），扣款只记 `debit short`，玩家同时留下撤销回来的件和花掉的胚料 | 统一付款 `EmberPay`：`refund:undo:<uid>:<rev>` hold 行 → **先扣胚料**（+ `p1paid_` 标记，同步存档）→ `commitTxn` 作废 hold。扣不到 → 什么都不做。批量撤销一次插所有 hold（一个 DB 事务），每件一个 commit，失败件单独退。不再写负数送货行（旧 pending 行照旧处理） |
| X1 / X4 | 印记兑换：PlayerData 扣印记计数、ledger 待发货行两次独立写入，中间崩服可丢件或白拿 | rid `redeem:<uuid8>:<时间>:<随机>`；`EmberPay` 扣 8 枚印记（与 hold 标记同存档）→ `EmberItemStore.commitCreate` **一个事务**写物品行 + txn（`mark_redeem`）+ gear 待发货行 + 作废 hold。没提交 → 退一次（新 `mark` 欠款类型，`p1_mark_tN` 与 `p1dlv_` 标记同存档）；崩服 → 进服 `reconcileHolds` / 送货 |
| X15 | 洗练：`takeCoin` + `ni.consume` → `ThreadLocalRandom` → 改 PlayerData，分开保存；断线 / kill 可白洗或付了钱结果丢 | rid `afx:<uid>:<n>`。付款（币 / 碎片 / 锁定碎片）+ `p4_rrn_<uid>`（已付款次数）+ `p4_rro_<uid>`（欠一次结果）同一次存档 → `commitPlain` ledger 行（或重复件退役 `consumeForReroll(rid)`）→ **才抽**，种子 `SHA-256(盐, 玩家, rid)`，写结果的那次存档清欠款。断线 / 重启：进服 `recoverRolls` 按 txn 状态补抽同一次 / 等 / 丢弃（已退款） |

测试钩子：新故障点 `after_pay`（付款落盘后结算事务晚 10 s 开始）。所有故障点仍只在进程环境 `CORERPG_TEST_FAULTS=1` 时可 arm；线上已关（见下）。

## 单测

`mvn -o package`（JDK8）：**238 / 0**。新增 `EmberPayTest`（8 个）：rid ≤ 64 且互不相同；价目 → 退款行；批量合计 / 不同阶印记拒绝；印记物品校验；欠款编码；恢复判定（committed → 补抽，hold / 未知 → 等，none → 丢）；种子确定性（同 rid 同结果）；`after_pay` 在测试环境外无效。

## persist-roundtrip（`tools/p1map/persist-roundtrip.sh`，`ONLY=g,h`）

最终一轮：16:53–17:10 CST，机器人 FreshQ115–119，**PASS 203 / FAIL 0**（日志 `/workspace/scratch/d1655/gh-run3.log`）。前两轮（FreshQ109–114）只因脚本断言问题失败（kill -9 后没带故障钩子重启、按前缀数行），产品断言全部通过；脚本修正后重跑全绿。

| 用例 | 场景 | 断言 | 结果 |
|---|---|---|---|
| h1a | 批量撤销 + 锻造精工同 tick 发出（撤销在前），胚料只够一个 | 恰好一个成功；胚料 / 骨尘 / 币 = 起始 − 赢家价；无 hold / pending | PASS 6（精工赢） |
| h1b | 同上，精工在前 | 同上 | PASS 6（精工赢） |
| h1x | 撤销付款后 commit 晚 10 s（`after_pay`），窗口内精工花同一批胚料（1.65.4 漏洞的原样复现） | 撤销成功、精工因不够被拒；守恒 | PASS 7（撤销赢） |
| h1 | 全程 | 日志 0 条 `debit short`；0 条 `undo:` 负数行 | PASS 2 |
| h1c | 撤销 `before_commit` | 件仍分解；胚料退回一次；无 hold；无该 rid 的 undo txn | PASS 4 |
| h1d | 撤销 `after_pay` + **kill -9** | 杀前已扣；重启后件仍分解、胚料退一次、hold → delivered | PASS 4 |
| h2a | 兑换正常 | 印记 −8；1 条 `mark_redeem`；背包 1 件；gear 行 delivered；无 hold | PASS 6 |
| h2b | 兑换 `before_commit` | 印记不变（`mark` 退款行 delivered 一次）；无 txn；无物品 | PASS 4 |
| h2c | 兑换 `after_commit` + **kill -9** | 杀前已扣 + 已提交、背包无件；重启进服：印记 −8 一次、件到账一次、hold void | PASS 5 |
| h2d | 兑换 `after_pay` + **kill -9** | 杀前已扣无 txn；重启：印记退回一次、无兑换、无 gear 行 | PASS 4 |
| h3a | 洗练（碎片）正常 | 付一次抽一次；空槽直接装上 | PASS 8 |
| h3b | 洗练 `before_commit` | 退款一次；词条不变；无欠款 | PASS 8 |
| h3c | 洗练 `after_commit` + 断线（掐 socket） | 欠款随付款落盘；进服 “recovered at join” 抽一次 | PASS 9 |
| h3d | 发命令同 tick 掐 socket | 要么什么都没发生，要么完整一次 | PASS 7 |
| h3e | 洗练 `after_pay` + 断线 | 离线时提交；进服抽一次 | PASS 8 |
| h3f | 装备库重复件洗练 `after_commit` + 断线 | 重复件退役一次、不扣碎片、进服抽一次 | PASS 9 |
| h3g | 洗练 `after_commit` + **kill -9** | 重启进服补抽一次 | PASS 9 |
| h3h | 洗练 `after_pay` + **kill -9** | 无提交；币 / 碎片退回一次；无欠款 | PASS 9 |
| h-final | 再次重进 | 全部守恒：币 = 起始 − 300 × 已提交洗练、碎片 = 起始 − 40 × 已提交碎片洗练、每个已提交 rid 恰好一次结果、印记 = 40 − 8 × 兑换数、每件兑换件恰好 1 份、三个号 0 hold / pending、0 `debit short` | PASS 14 |
| h | 关钩子重启 | `fault` 命令被拒；play JVM 环境无 `CORERPG_TEST_FAULTS` | PASS 2 |
| g1–g6、g3k、g5a/b | D162 原有故障注入（g4 改成新语义：撤销 after_commit + 断线 → 胚料先扣一次、hold void、无负数行） | 原断言 | PASS 33（另有 arm 确认行） |
| restart / after kill -9 | 每次启动 | 两条 MySQL connected；0 SEVERE | PASS 14 |

所有用例：0 丢失、0 重复。

## 冒烟（关钩子，17:11–17:15 CST）

| 项 | 结果 |
|---|---|
| FreshQ120 Q01 kite（`MOVE=kite DRINK=1 realgear-run.sh … q01 0 0 0 0 10`） | CLEAR，140 s，0 死，1 瓶药 |
| 手动洗练（T1 刃，碎片） | `afx:<uid>:1`，300 币 + 40 碎片，空槽直接装上 |
| 手动兑换（8 枚 T1 印记） | 背包到账 1 件，剩余 1 枚 |
| 分解 3 件 + 整批撤销 | 「已整批撤销 3 件 … 胚料 ×3 已扣」；0 pending |
| 机器人 | 全部退出（botd `/list` = `[]`） |

## 已知限制

- 背包里的材料：`DataStore.save` 与 `EmberVault.savePlayerFile` 之间有极小窗口（同 D162 锻造付款）。
- YAML 存储（非 MySQL）仍走旧的内存路径（没有 txn 表可提交）。
- 洗练种子盐在 `plugins/CoreRpg/p1-runs/reroll-salt`（运行时文件，不入库）；丢了会换盐，但结果仍是 exactly-once（只是重启后不可重放）。
