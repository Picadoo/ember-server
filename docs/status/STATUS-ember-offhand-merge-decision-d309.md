# 状态 · D309：副手并轨 / 不并轨决策页关窗（方案 A · docs-only）

**日期：** 2026-10-08（上海时间）  
**上游：** 总控批 A · [`DESIGN-ember-offhand-merge-decision-2026-10-08.md`](../design/DESIGN-ember-offhand-merge-decision-2026-10-08.md) 方案 A（待批稿 `f4840478` · 批注 `bc6ca94f`）  
**版本：** **docs-only** · CoreRpg **未升**（仍 **1.65.97**）· `balance_version` **60**（未抬）· **零部署**

## 人话

**已上线（docs-only 关窗）。** 灰粮副手（StatService 守腕/生坠/灰箍）与 P1 刃+护符双轨并行；10-07 已批「永不进 B/H」，但加固§8-5 仍写待派。本窗**只落 docs 关窗**：正式确认 **方案 A**——副手是展示/微量生存，**永不进** P1 B/H；方案 B 搁置；**不写新物品、不并轨插件、不开任一 R**。

## 改了什么

| 窗 | 改动 |
|---|---|
| **方案 A 关窗** | 主稿 STATUS **已批 A** · 批注勾选方案 A · 施工号 **D309** |
| **加固§8-5** | 文首窗 5 + §8 第 5 条 → **已关窗 · 方案 A · D309**（永不进 P1 B/H；六槽仍 HOLD） |
| **旧决策页** | 10-07 文首接替指针 → 指 10-08 **已批 A · D309** |
| **链入** | backlog `B-offhand-merge-decision` / `B-flex-5` → **已批 A · 方案 A · D309**；tip 硬规格 → 已关窗 |
| jar / 玩法 yml / NI | **未动** |

## 不动

- 任一 R 窗施工本体（事件 R/W · 调律 R · 工坊 R · 走廊 W2 · 深渊 R · 周本 R · Boss 预警 R · 挂机 R）  
- 新副手 NI · 改编 ward / vita / 灰箍**数值** · Stat→EmberFormula 并轨插件  
- 六槽 Stage1 · Pack6 · 天赋 HOLD · 灰印 HOLD · 守招再调  
- D307 / D308 主交付 · 价表 / 体力 / 掉率 / event_rate / ALTS  
- CoreRpg jar · TrMenu / `ember-v1*.yml` 玩法键 · bv **60** · login/proxy/play **未停未换**

## 验收

- 静态：DESIGN 文首 **已批 A · 方案 A · D309**；本 STATUS 入库；加固§8-5 / 旧决策页 / backlog / tip 已关窗对齐；`git diff` **无** `CoreRpg/src/**` · **无** `plugins/**/*.yml` 玩法改 · **无** jar / NI  
- **不跑** p1sim · **不部署**  
- 门禁自检：禁借本号开 R / 写新副手物品 / 并轨插件 / 重开 D307/D308  
- 回滚：删本 STATUS + 还原加固§8-5·旧决策页·backlog·tip·主稿批注（设计待批稿 `f4840478` 保留）

## 下一窗

- 方案 B（替换预算并轨）**搁置**；若总控改批须另派数值+p1sim+插件号（本号不解禁）  
- 诚实半行 D289/D294 **已够**；再拧菜单半行 = 薄 UX，不另开  
- 样本门禁 R 仍按 D308 硬表：**当前全表不得开**  
- **下一档硬债 tip（已关 · D310 · 非开 R）：** [`STATUS-ember-sample-week-report-d310.md`](STATUS-ember-sample-week-report-d310.md) · tip [`STATUS-ember-next-hard-debt-sample-week-report-need-design-2026-10-08.md`](STATUS-ember-next-hard-debt-sample-week-report-need-design-2026-10-08.md) · 批 M · backlog `B-sample-week-report` · **禁**无签字自动开闸 · **禁**玩家面 KPI  
- **禁** 偷开任一 R · 改价(=forge R) · Pack6/六槽 · 天赋/灰印续跑 · 新副手 DPS NI · 默默叠 Stat→EmberFormula · 重开 D307/D308 · 薄 UX 抬假硬债  
