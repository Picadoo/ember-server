# 状态 · D278：扭蛋兑券跨库缝 — 评估 + 薄修（先 hold 再落盘）

**日期：** 2026-10-07（上海时间）  
**上游：** ARCH R5 ① · D277 下一档候选  
**插件：** CoreGacha **1.0.2**（源码）；play **当前未挂** `CoreGacha.jar`（目录里只有 yml）— 本窗**未**往线上丢 jar、**未**重启，避免误开扭蛋。

## 人话：缝在哪

用余烬币/徽换扭蛋券时：

1. CoreRpg 内存扣币，并**马上**写进 CoreRpg 库（`flushMutation`）
2. 再异步写 gacha 库加券
3. 若第 2 步失败，再 `addCoin` 退款

两套库没有共同事务。进程若在「已扣币落盘、券还没加上」之间挂掉 → **钱没了、券也没有**；退款也依赖进程还活着。

证据：`CoreGacha/.../GachaService.java` `exchange` · `CoreRpgBridge.takeCoin`（旧：`take` 后立刻 `flush`）。

## 本窗薄修（已写进源码）

| 步 | 行为 |
|---|---|
| 扣款 | `takeCoinHold` / `takeBadgesHold` — **只改内存，不 flush** |
| 加券 | 原 gacha MySQL 事务不变 |
| 成功 | `flushPlayer` — 这时才把扣款写入 CoreRpg |
| 失败 | 内存加回 + flush（与原来退款等价，且多数情况从未落过扣款） |

效果：kill-9 卡在「已扣内存、券未提交」时，重载会从 CoreRpg 库读回**扣款前**余额 → **玩家不丢钱**。

## 仍未闭环（下一窗候选）

| 残留 | 说明 |
|---|---|
| 券已提交、flush 前崩溃 | 可能白嫖券（经济向玩家倾斜，不丢币） |
| 窗口内 CoreRpg 定时 `saveAll`（约 5 分钟） | 极窄：若兑换跨越整点存档，仍可能提前落盘扣款 |
| 真原子 | 需 `exchange_intent` 账本 + 进服对账退款（大窗） |

## 验收

- 源码 diff：`CoreRpgBridge` + `GachaService.exchange`；版本 1.0.2
- `mvn -o -DskipTests package`（JDK8）通过
- **未**部署 jar / **未**重启 play（线上本来就没挂 CoreGacha）

## 不变

- 六槽 / S0-9 不动；CoreRpg 数值不动
