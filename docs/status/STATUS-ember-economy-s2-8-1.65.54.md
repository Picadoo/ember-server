# STATUS · ARCH S2-8（CoreRpg 1.65.54 / D228）

**日期：** 2026-10-06 Asia/Shanghai · **上游：** live 1.65.53 / D227 / bv58（d2c5d33）

## 为什么是这一刀

REG §6.4「未登记即拒」只扫了 `addCoin`（D216）和 `takeCoin`（D218）。审计发现以下发放仍**绕过登记表**直接写计数：

| 账户 | 绕过路径（1.65.53） | 现在 |
|---|---|---|
| 首领徽记 `p1_sigmark_` | `deliver()` SIGMARK 分支直写（S07 首通 / S08 重打 / S09 誓约 / S17 前哨 / S18 残响 / S23 签到全部） | `creditInsigniaLedger` → `grantInsignia`（按键 / run id 解析 SourceId） |
| 锻造印记 `p1_mark_t` | `rush_mark` / `raid_mark` / `rot_mark` 解析不到来源 → 直写 | S16/S17 · S12 · S10/S11 解析成功 → `grantMark`；未知键 untagged 照发 |
| 余烬徽 `p3_badge` | 连战（S16）、周目标（S19）、国庆兑换（S27）直写 | `grantBadge` |

`sourceForGrantKey("fc_sigmark")` 原本落入 `fc_` 前缀 → S06（首通包，不发徽记）；本次显式排在前面 → S07。

## 做了什么

1. `EmberEconomy`：`grantInsignia(d, src, map, n)`、`grantBadge(d, src, n)`（拒 sink / legacy / 不发该账户的行 / 非正数）；`Credit {TAGGED, UNTAGGED, REFUSED}` + `creditMarkLedger` / `creditInsigniaLedger`（账本行唯一写入口；未知键 = 1.65.53 之前的直写行为，**不丢资产**）；`runHead(runId)`；`sourceForRushMode`。
2. 调用点：`EmberRunService`（deliver MARK / SIGMARK、rushSettle 徽）、`EmberSeason.addGoal`（S19）、`EmberFestival` 兑换（S27）。
3. 例外标注 `// econ-ok: <理由>`：管理测试钩子 ×4、EmberPay 回滚 ×3、深渊层费退还 ×1、C15 外观扣徽 / 扣印 ×2（暂停，不动逻辑）、XP 发放（S2-3 已校验）×1。
4. 单测（+6）：新键 → REG 行且该行发对应账户；p1/ 里所有 MARK/SIGMARK 字面账本键必须在已知集合（新键不映射 → CI 失败）；grantInsignia/Badge 正反例；credit 三态；部署 runs yml 每个连战厅的奖励（marks / badges / sigmarks）都由其模式行支付（9 厅）；行级扫描。

## 已知边界

- 扫描是**行级**：计数器先放进局部变量再写（`EmberPay` 无标签撤销、`EmberDelivery` 持久投递）不会被抓到——已人工复核，属于退款 / 投递而非新来源。
- S13 深渊层结算仍记在 S01 基线行（数量同表）；S14 体力 / S15 退药不经 grant*。
- vault 写入扫描（REG §6.4 最后一项）未做。

## Echotune 顺带核对（Q05–Q07，只读，不改）

离线 `rushsim.run_rush`（rules bv58，n=600，单人，躲避 0.5）：

| 厅 | hp/dmg | T2+4 | T2+8 |
|---|---|---|---|
| echo_q01 | 12.0/3.0 | 100% 31 s | 100% 25 s |
| echo_q02 | 3.5/2.2 | 93% 41 s | 100% 33 s |
| echo_q03 | 3.2/2.0 | 69% 39 s | 99% 32 s |
| echo_q04 | 2.0/1.8 | 89% 35 s | 99% 29 s |
| echo_q05 | 1.3/1.4 | 88% 37 s | 99% 31 s |
| echo_q06 | 1.15/1.2 | 80% 39 s | 98% 32 s |
| echo_q07 | 1.0/1.1 | 73% 40 s | 97% 33 s |

Q05–Q07 落在旧厅 69–100% 区间内，有效首领生命 2964 / 3059 / 3120（旧厅 2400–3150），且进 Q05–Q07 厅要求本图首通（实际装备高于 T2+4）→ **没有「明显错」，不改**。如要压平 Q07（73%），候选是 `echo_q07.boss_dmg 1.1 → 1.0`，留真人数据再定。

## 下一刀候选

1. vault 写入扫描（`EmberVault.autoDeposit` / `creditBound` 调用方必须经 `grantMat` 校验）+ S13 深渊层 SourceId。
2. S3 第一步：`EmberRunService` 按分节拆出 `EmberRushService`（rushSettle / rush 命令 / PAPI，行为不变，固定种子单测）。
3. S1 维护：计数器注册表 ↔ 源码字面量覆盖率测试纳入新键。

## 冒烟

FreshQ790 PASS 25/0（S07 +3、S08 +1、S19 +15、S27 +2、S18 +2；无 refused / untagged；SEVERE 0）— `docs/tests/smoke-2026-10-06-d228-economy-insignia-badge.md`。
