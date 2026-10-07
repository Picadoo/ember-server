# 状态 · D310：样本窗周报模板（方案 M · docs-only）

**日期：** 2026-10-08（上海时间）  
**上游：** 总控批 A · [`DESIGN-ember-sample-week-report-2026-10-08.md`](../design/DESIGN-ember-sample-week-report-2026-10-08.md) 方案 M（待批稿 `627f2f6d`）  
**版本：** **docs-only** · CoreRpg **未升**（仍 **1.65.97**）· `balance_version` **60**（未抬）· **零部署**

## 人话

D308 手查硬表已有；OP 仍要手抄 `p1-telemetry/<week>.yml`。本窗**只落 docs 周报检查单**：可复用 markdown（周滚 / `runs`≥30 / 事件两率 / 调律两率 / 排除脏表 / 各 R 红线 / 签字栏）+ 空周/半满周示例指针；**门槛数字钉 D308 未改**；**勾满 ≠ 自动开 R**——**不跑 p1sim · 不换 jar · 不改 yml · 不开任一 R**。

## 改了什么

| 窗 | 改动 |
|---|---|
| **M 检查单落地** | DESIGN §2.1.1 → [`STATUS-ember-sample-week-report-checklist-d310.md`](STATUS-ember-sample-week-report-checklist-d310.md)（OP-only · 禁玩家面） |
| **链入** | backlog `B-sample-week-report` → **已上线 D310**；tip 硬规格 → 已上线；D308 STATUS 旁注「方案 R 模板已落 D310 · 脚本仍后置」 |
| jar / 玩法 yml / 脚本 | **未动**（`tools/p1-telemetry-week-report.py` **未写** · 方案 R 仍后置） |

### 门禁摘要（本号仍钉）

| 项 | 态 |
|----|----|
| 自动开闸 / 「建议立即开 X R」无签字 | **禁** |
| 玩家面 KPI / 排行 / 成就 | **禁** |
| 任一战斗/经济 R 施工 | **不开**（对照 D308：**当前全表不得开**） |
| D308 门槛数字 | **未改** |
| 脚本方案 R / M+R | **后置**（另号） |

> **脚本契约指针（2026-10-08 · 不改本号已批 M）：** D310 方案 R 后置 → 待批稿 [`DESIGN-ember-sample-week-report-script-2026-10-08.md`](../design/DESIGN-ember-sample-week-report-script-2026-10-08.md) · tip [`STATUS-ember-next-hard-debt-sample-week-report-script-need-design-2026-10-08.md`](STATUS-ember-next-hard-debt-sample-week-report-script-need-design-2026-10-08.md) · `B-sample-week-report-script` · **仍未写** `tools/p1-telemetry-week-report.py`。

## 不动

- 任一 R 窗施工本体（事件 R/W · 调律 R · 工坊 R · 走廊 W2 · 深渊 R · 周本 R · Boss 预警 R · 挂机 R）  
- `refineCost` / `qualityCost` / `enhanceCost` / `upgradeCost` · 掉落 / `event_rate` / ALTS · 体力  
- Pack6 / 六槽 / 新模式图包 · 天赋 HOLD · 灰印 HOLD · 守招再调  
- D307 / D308 / D309 主交付 · D308 §2.1 门槛数字  
- 玩家面遥测 KPI / 排行榜 / 成就  
- CoreRpg jar · TrMenu / `ember-v1*.yml` 玩法键 · bv **60** · login/proxy/play **未停未换**  
- `tools/p1-telemetry-week-report.py`（方案 R · **未落**）

## 验收

- 静态：DESIGN 文首 **已批 A · 批 M · D310**；本 STATUS + 检查单入库；backlog / tip / D308 STATUS 已链；`git diff` **无** `CoreRpg/src/**` · **无** `plugins/**/*.yml` 玩法改 · **无** jar · **无** 新 tools 脚本  
- **不跑** p1sim · **不部署**  
- 门禁自检：检查单含签字栏与「禁自动开闸」；门槛对照 D308+D298；禁借本号开 R / 改门槛 / 玩家面 KPI  
- 回滚：删本 STATUS / 检查单 + 还原 backlog·tip·D308 旁注·主稿批注（设计待批稿 `627f2f6d` 保留）

## 下一窗

- **脚本方案 R** → tip 已升档：[`STATUS-ember-next-hard-debt-sample-week-report-script-need-design-2026-10-08.md`](STATUS-ember-next-hard-debt-sample-week-report-script-need-design-2026-10-08.md) · backlog `B-sample-week-report-script` · 荐 docs 脚本契约（可修订本 DESIGN §2.2）；落脚本须总控批 A 后 docs/tools only；输出只得门槛勾选+当前态摘要；**禁**无签字「建议开 R」；**禁**与战斗/经济 R 同号  
- 真开某 R：先周报勾到「可讨论」+ 总控签字进入该债待批 A → 再派该债硬设计（不得跳过）· 对照 [`STATUS-ember-sample-window-readiness-d308.md`](STATUS-ember-sample-window-readiness-d308.md) + D298  
- **禁** 偷开任一 R · 改价(=forge R) · Pack6/六槽 · 天赋/灰印续跑 · 重开 D307–D310 · 玩家面遥测 · 改 D308 门槛数 · 薄 UX 抬假硬债挡窗
