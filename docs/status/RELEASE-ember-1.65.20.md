# 发布凭证 · CoreRpg 1.65.20（2026-10-05）

D182 奖励精英变招 Pack 1：Extra.ELITE 每图 1 条固定轻招。设计：`docs/design/DESIGN-ember-reward-elite-twists-2026-10-04.md`。

## 线上状态（核对于 TBD Asia/Shanghai）

| 项 | 值 |
|---|---|
| CoreRpg 版本 | **1.65.20** |
| class major | **52**（JDK 8）|
| CoreRpg JAR sha256 | `9be6bbb04640cd391c528c2f7912f9ba0709dc30c15d4ace8c6e241052315a5f`（build）|
| balance_version | **43** |
| 代码提交 | `70556e9` |
| 回滚包 | `/workspace/backup/CoreRpg-1.65.19-pre-1.65.20.jar` |
| MySQL | 部署后核 |
| SEVERE | 部署后核 |

## 内容

- `elite_twists:` 开启：Q01 门廊推 / Q02 焦焰踏 / Q03 誓印扫 / Q04 闸冲（kb=0）/ Q05 落尘 / Q06 霜息 / Q07 矿渣劈
- 伤害 = 精英 atk × 倍率；warn ≥ 1.2；open_delay 3s；奖励仍 10 碎片 + 1 核心；Extra 权重不变
- p1sim Extra.ELITE 仍是木桩模型 → §7 通关率门禁延后（设计已注明）

## 验收

| 项 | 结果 |
|---|---|
| 单测 `mvn -o test`（JDK 8）| 272 / 0 |
| 冒烟 | TBD FreshQ310+ |
| 资产回归 | 无资产路径变化 → 未跑 |
