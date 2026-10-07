# 状态 · D311：样本窗周报脚本（方案 M+R · docs+tools 同号）

**日期：** 2026-10-08（上海时间）  
**上游：** 总控批 A · [`DESIGN-ember-sample-week-report-script-2026-10-08.md`](../design/DESIGN-ember-sample-week-report-script-2026-10-08.md) **批 M+R · 同号**（待批稿 `b0d98b0a`）  
**版本：** **docs + tools only** · CoreRpg **未升**（仍 **1.65.97**）· `balance_version` **60**（未抬）· **零部署玩法**

## 人话

D310 手填检查单已有；OP 仍要手抄 `p1-telemetry/<week>.yml`。本窗**落脚本契约批注 + 只读脚本**：读周遥测 yml → 吐同结构检查单 md（预填 A–C 数字；D/E/G 人感与签字留空）；**门槛钉 D308 未改**；**勾满 ≠ 自动开 R**——**不跑 p1sim · 不换 jar · 不改玩法 yml · 不开任一战斗/经济 R**。

## 改了什么

| 窗 | 改动 |
|---|---|
| **M+R 契约落地** | DESIGN 文首 **已批 A · 批 M+R · D311**；勾选同号 |
| **脚本** | [`tools/p1-telemetry-week-report.py`](../../tools/p1-telemetry-week-report.py)（stdlib + PyYAML；只读；禁自动开闸） |
| **示例 fixture** | `tools/fixtures/p1-telemetry-example-empty.yml` · `tools/fixtures/p1-telemetry-example-half.yml`（EXAMPLE ONLY · 无真玩家 UUID · 非 live） |
| **链入** | backlog `B-sample-week-report-script` → **已批 M+R · D311**；tip 关窗；D310 STATUS/DESIGN 旁注「脚本已落 D311」（**不改** D310 已批 M 检查单结论） |
| jar / 玩法 yml / NI | **未动** |

### 门禁摘要（本号仍钉）

| 项 | 态 |
|----|----|
| 自动开闸 / 「建议立即开 X R」无签字 | **禁**（脚本禁句清单硬验收） |
| 玩家面 KPI / 排行 / 成就 | **禁** |
| 任一战斗/经济 R 施工 | **不开**（对照 D308：**当前全表不得开**） |
| D308 门槛数字 | **未改**（脚本只引用 runs≥30） |
| 与战斗/经济 R 同号兼开 | **禁**（本号仅 docs+tools） |
| D/E/G 人感与签字 | **脚本留空** |

## CLI 示例

```bash
python3 tools/p1-telemetry-week-report.py --yml plugins/CoreRpg/p1-telemetry/<week>.yml --out docs/status/reports/sample-week-<week>.md
python3 tools/p1-telemetry-week-report.py --yml tools/fixtures/p1-telemetry-example-empty.yml --out /tmp/week-empty.md
```

可选：`--json-log <path>`（仅对账备注，**不**覆盖 yml `counts`）；`--yml` / `--out` **必填**（对照 DESIGN §2.1.1）。

退出码：`0` 可读成功 · `2` 缺文件 · `3` 缺 `week`/`counts`/`p1_pf_runs` · `4` `--out` 无法写入。

## 不动

- 任一 R 窗施工本体（事件 R/W · 调律 R · 工坊 R · 走廊 W2 · 深渊 R · 周本 R · Boss 预警 R · 挂机 R）  
- `refineCost` / `qualityCost` / `enhanceCost` / `upgradeCost` · 掉落 / `event_rate` / ALTS · 体力  
- Pack6 / 六槽 / 新模式图包 · 天赋 HOLD · 灰印 HOLD · 守招再调  
- D307 / D308 / D309 / D310 主交付 · D308 §2.1 门槛数字 · D310 检查单栏目结构  
- 玩家面遥测 KPI / 排行榜 / 成就  
- CoreRpg jar · TrMenu / `ember-v1*.yml` 玩法键 · bv **60** · login/proxy/play **未停未换**

## 验收

- 静态：DESIGN 文首 **已批 A · 批 M+R · D311**；本 STATUS + 脚本入库；backlog / tip / D310 旁注已链；`git diff` **无** `CoreRpg/src/**` · **无** `plugins/**/*.yml` 玩法改 · **无** jar  
- 脚本自测：空周 / 半满周 fixture → md；含签字栏空位；禁句清单零命中；`runs<30` 标「未满 · 不得开」；E 表默认「未满 · 不得开」  
- **不跑** p1sim · **不部署**玩法  
- 回滚：删本 STATUS / 脚本 / fixture + 还原 backlog·tip·D310 旁注·主稿批注（设计待批稿 `b0d98b0a` 保留）

## 下一窗

- 真开某 R：先周报（手填检查单或本脚本预填）勾到「可讨论」+ 总控签字进入该债待批 A → 再派该债硬设计（不得跳过）· 对照 [`STATUS-ember-sample-window-readiness-d308.md`](STATUS-ember-sample-window-readiness-d308.md) + D298 + D310 检查单  
- **禁** 偷开任一 R · 改价(=forge R) · Pack6/六槽 · 天赋/灰印续跑 · 重开 D307–D311 · 玩家面遥测 · 改 D308 门槛数 · 薄 UX 抬假硬债挡窗 · 无签字自动开闸
