# Smoke · D229 / CoreRpg 1.65.55 — ARCH S2-9 S13 abyss SourceId + vault scan

**何时：** 2026-10-06 ~04:58 Asia/Shanghai · **脚本：** `tools/p1map/d229-economy-s13-vault-smoke.sh` · **Bot：** FreshQ797

## 结果

**PASS 17 / FAIL 0** · MySQL×2 · SEVERE 0 · jar `20c0e4577d1c23cb…`

| 项 | 结果 |
|---|---|
| CoreRpg 1.65.55 enable | PASS |
| CoreRpg + CoreGacha MySQL | PASS |
| ember-v1-economy.yml SoT bv58 | PASS |
| 深渊第 1 层 bound `q01a1-…` | PASS |
| 清房 + 首领结算 `(abyss 1)` | PASS |
| settle run-id `q01a1`（S13 head） | PASS |
| 无 `economy grant* refused` | PASS |
| vault `进仓库` / 自动入库（bot chat） | PASS |
| SEVERE | 0 |

## 说明

- 数量仍为 S01 BASE_*（币 300 / 碎片 24 / …）；本刀只把 SourceId 从 S01 换成 S13。
- FreshQ794 亦曾完整通关深渊并打出 `[仓库] +24 … 自动入库`；FreshQ791–796 为脚本迭代（错图 tp / Q07 walk STUCK）。
- 无资产路径变更 → 不做 persist-roundtrip。
