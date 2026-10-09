# 状态 · D404：仓内还差 PAPI（批 A·M · W1a+W1b）

**日期：** 2026-10-10（上海时间）  
**上游：** 总控派单 · DESIGN [`DESIGN-ember-afk-forge-mat-gap-visible-2026-10-10.md`](../design/DESIGN-ember-afk-forge-mat-gap-visible-2026-10-10.md) **§2.1–2.2**  
**裁决：** **本号交** 只读 Placeholder + 单测 + 装 play · **TrMenu W1c/d 另号** · **未动** afk.tiers / daily_kills / UpgradeRules / gate_daily / 观察三开关 / vault 白名单容量

## 本号范围

| 项 | 做了 | 另号 |
|----|------|------|
| 仓余额四键 | EmberRunPapi **VAULT** 段；真源 `EmberVault.count`（仓内合计，含绑定） | — |
| 代表档还差 | 镜像 `EmberUpgradeRules.enhanceCost(0)` / `upgradeCost(1)` / `refineCost(0)`；`gap=max(0,need−have)` | — |
| 路由 | `isVaultKey` 在 MAP 尾之前；不碰 SHORT / LOADOUT | — |
| 菜单挂键 | — | W1c/d TrMenu 另号 |

## 键清单

| Placeholder | 含义 | 空/异常 | 形态钉死 |
|-------------|------|---------|----------|
| `%corerpg_p1_vault_shard%` | 仓内碎片（`mat_ember_shard`） | `0` | 数字 |
| `%corerpg_p1_vault_bone%` | 仓内骨尘 | `0` | 数字 |
| `%corerpg_p1_vault_core%` | 仓内核心碎片 | `0` | 数字 |
| `%corerpg_p1_vault_blank%` | 仓内胚料 | `0` | 数字 |
| `%corerpg_p1_recipe_gap_enhance1%` | 强化+1 碎片 gap（need=4） | `0`（已够） | **单数字** |
| `%corerpg_p1_recipe_gap_upgrade_t2%` | T1→T2 多料还差（need 碎60·核12·胚6） | `碎差0·核差0·胚差0` | **拼接半行** `碎差N·核差M·胚差K`（**非**拆三键） |
| `%corerpg_p1_recipe_gap_refine1%` | 精工0→1 胚/骨还差（need 胚3·骨5）（可选同批） | `胚差0·骨差0` | **拼接半行** `胚差N·骨差M` |

## 不动

afk.tiers / daily_kills · UpgradeRules 费用数组 · gate_daily · 观察三开关 / ×0.97 · K3 · vault 白名单/容量 · 主仓切分支 · 用缺 six_slot 默认盖 live ember-v1.yml

## 验收（本号）

| # | 项 | 结果 |
|---|----|------|
| V-route | VAULT 路由；vault/gap 键不落 MAP | EmberRunPapiTest |
| V-gap | enhance1 / upgrade_t2 拼接 / refine1 与 UpgradeRules 镜像一致 | EmberRunPapiTest |
| V-rules | UpgradeRules / afk / gate_daily git diff 空 | 本号 |
| V-play | jar 装 play · Enabling | 见装服 tip |

## tip / jar

| 项 | 值 |
|----|----|
| tip | `a6385f12` |
| jar | `CoreRpg-1.65.113-d404.local.jar` |
| sha256 | `b62819ed0844503396036c6ffb4fb3ec58b89377c0a10a8d7c4e705861e28978` |
| Enabling | `CoreRpg v1.65.113-d404.local` · play PID 1151991 |

## 下一号

菜单岗 W1c/d：`ember_p1_forge_recipes` 挂仓内现有 + 还差行；挂机半行指速览。
