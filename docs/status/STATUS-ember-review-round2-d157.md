# STATUS · D157 第二轮挑刺高/中高修复（CoreRpg 1.64.0）

- **时间**：2026-10-04 09:43–09:55 CST
- **基线评审**：`docs/reviews/review-2026-10-04-round2.md`（commit `17b4797`）
- **线上**：CoreRpg **1.64.0**（balance_version **28** 未改，本单只改周规则文案）+ CoreGacha 1.0.0（yml 文案）+ TrMenu reload
- **测试号**：FreshQ49（invsnap / bulk / hub L）；gacha 改名只核 live yml

## 已修

| # | 严重度 | 项 | 做法 |
|---|---|---|---|
| 1 | 高 | InvSnap 恢复复制材料/扭蛋券 | `InvSnapRules.stripOnRestore` + `InvSnapService.stripCurrency`：恢复前从背包/末影箱数组剔除 vault 白名单材料（含国庆币）与 `ember_gacha_ticket`；help/restore/diff 文案写明快照只管背包+末影箱；diff 附材料仓摘要 |
| 2 | 高 | 批量分解吞投入件 | `EmberStorageRules.invested` / `bulkDismantle(..., withAffix)`：默认跳过 强化>0 / 精工>0 / 成色≥卓越 / 有词条；单件分解不变；预览与 GUI 文案同步 |
| 4 | 中高 | 主菜单 L 活动结束后子图标无动作 | `ember_hub.yml` L 子图标补 `actions`（sound + `menu: ember_p1_fest`）；lore「含仓库」 |
| 5 | 中 | 周规则「重击/伤高」文案 | `ember-v1-runs.yml` disarm/guards/wall + `compositionLabel` → 血厚、出手慢、不怕击退 |
| 6 | 中 | 扭蛋光环与活动同名 | `gq_aura_firework`「盛世烟火」→「繁花烟火」（称号/护符名不动） |
| 8 | 中 | 宠物/光环显示范围未写 | legend pet/aura `desc` + rates 页一行 |
| 10 | 低 | 余烬徽 / 国庆币文案过期 | fest 菜单：20徽=1券、活动后符可用徽；国庆币「含仓库」 |

## 推迟（下一批）

- **#3** 限定池火花折算（1:1 进常驻火花或按光屑价值）
- **#7** 批量撤销分解 / 列表「共 N 显示 20」

## 冒烟（FreshQ49，短）

| 项 | 结果 |
|---|---|
| A InvSnap：stash+redeem 后 restore #42 | **PASS** — 跳过 余烬碎片×30、扭蛋券×2；背包无复制；材料仓/钱包保留 |
| B bulk：2 标准 drop 进清单，极品/卓越跳过 | **PASS** — 「另有 3 件…有投入…跳过」；take 仅成色标准 |
| C 活动结束后 hub 第 0 格点击 | **PASS** — 显示「国庆纪念」→ 打开「国庆 · 烟火庙会」 |
| 部署 | **PASS** — `Enabling CoreRpg v1.64.0` + `[storage] MySQL connected` + CoreGacha MySQL；class major 52（JDK8） |

脚本：`tools/p1map/d157-smoke.sh`。备份 jar：`/workspace/backup/CoreRpg-1.63.2-pre-d157.jar`。
