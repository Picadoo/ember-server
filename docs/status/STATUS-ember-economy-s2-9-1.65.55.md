# STATUS · ARCH S2-9（CoreRpg 1.65.55 / D229）

**日期：** 2026-10-06 Asia/Shanghai · **上游：** live 1.65.54 / D228 / bv58（b3b4f96）

## 为什么是这一刀

D228 收口时留下两件 S2 leftover：

1. **S13 深渊层**仍经 `sourceForGrantKey("base_*")` 落到 **S01**，与 REG 表「S13 = 深渊层结算」不一致（数量本就同基线，只是标签错）。
2. **vault 写入扫描**（REG §6.4 最后一项）未做 — `autoDeposit` / `creditBound` / `credit` / `give` 调用方应先过 `grantMat`（或显式 `econ-ok:`）。

## 做了什么

1. `EmberEconomy.isAbyssHead` + `sourceForGrant`：`base_*` + run-id head `<map>a<n>` → **S13**；普通 / 挑战仍 S01。settle 数量继续读 S01 BASE_*（不变）。
2. S01 登记名改为「主线 / 挑战结算基线」（去掉深渊）。
3. 行级 vault 扫描（`EmberEconomyTest.p1VaultWritesAreEconomyOrTagged`）：`p1/` 内上述写入必须在 `EmberVault` 或带 `econ-ok:`。
4. 例外标注：`EmberRunService` deliver MAT/BMAT（grantMat 已校验）×2、`EmberDelivery` 持久投递 ×1、`EmberForgeService` S29 分解胚料（legacy YAML 路径）×1。
5. 单测：`abyssFloorSettleTagsAsS13NotS01` + vault 扫描；既有 S01 键 alone 断言不变。

## 已知边界

- S13 无独立 golden / yml 数量行 — 有意：数量真源仍是 S01；S13 只做 SourceId 标签。若日后深渊要独立数量，再给 S13 加 golden + yml。
- 扫描是**行级**：与 D228 同边界（局部变量中转的写入需人工复核）。
- Pickup / `EmberVault` 内部 `add` 不扫（玩家搬仓，不是来源发放）。

## 下一刀候选

1. S3 第一步：从 `EmberRunService` 拆出 `EmberRushService`（行为不变）。
2. S14/S15 经 grant*（体力 / 退药）。
3. 可选：S13 独立 golden 镜像 S01（防标签行日后漂移）。

## 冒烟

FreshQ797 PASS 17/0（深渊第 1 层 `q01a1` settle + vault 进仓库 live；无 grant refuse；SEVERE 0）— `docs/tests/smoke-2026-10-06-d229-economy-s13-vault.md`。
