# 状态 · D312：六槽 Stage1 决策页批 B（显式开工令 · docs-only）

**日期：** 2026-10-08（上海时间）  
**上游：** 总控批 B · [`DESIGN-ember-six-slot-stage1-decision-2026-10-08.md`](../design/DESIGN-ember-six-slot-stage1-decision-2026-10-08.md) 方案 B（待批稿 `b87d8583`）· tip [`STATUS-ember-next-hard-debt-six-slot-stage1-decision-need-design-2026-10-08.md`](STATUS-ember-next-hard-debt-six-slot-stage1-decision-need-design-2026-10-08.md)（`ab927e5c`）  
**版本：** **docs-only** · CoreRpg **未升**（仍 **1.65.97**）· `balance_version` **60**（未抬）· **零部署**

## 人话

总控对六槽 Stage1 发了**开工令**，但只放行第一步：**T0 离线模拟复验**（另号 **D313**，按 bv60 现行规则复跑 D246 的 w80_cap，过 G0–G9）。插件（T1）、物品+菜单（T2）、上线迁移（T3）**各需总控另行签字**；T0 或 T0' 不过 → 回到 HOLD，线上零变化。线上玩家本窗**看不到任何变化**。

## 改了什么

| 项 | 改动 |
|---|---|
| 决策页 | STATUS **待批 A → 已批 B** · §4 勾「批 B」+ 总控批注 · §7 变更记录 |
| tip | 加结案旁注：已关 · 批 B · D312 · T0 → D313 |
| backlog | `B-six-slot-stage1-decision` → **已批 B · D312**，下一步 T0 = **D313** |
| jar / 玩法 yml / NI / TrMenu | **未动** |

## 不动

- 任一 R 窗（事件 R/W · 调律 R · 工坊 R · 走廊 W2 · 深渊 R · 周本 R · Boss 预警 R · 挂机 R）；D308 表与门槛数**不变**  
- 六槽 Java / NI / TrMenu（T1–T3 未签）· p1sim `SIX` 默认仍 None  
- Pack6 禁 · 天赋 row1 HOLD · 灰印 HOLD · D309 副手方案 A  
- 价表 / 体力 / 掉率 / event_rate / ALTS · bv **60** · login/proxy/play **未停未换**

## 验收

- 静态：决策页文首 **已批 B · D312**；本 STATUS 入库；tip / backlog 对齐；`git diff` 无 `CoreRpg/src/**`、无玩法 yml、无 jar / NI  
- **本号不跑** p1sim（T0 在 D313）· **不部署**  
- 回滚：删本 STATUS + 还原决策页批注 / tip / backlog（待批稿 `b87d8583` 保留）

## 下一窗

- **D313 · T0 离线**：`tools/p1sim` 接线 + bv60 复跑 + `tools/p1sim/out-six-slot-stage1-t0.md`；只 tools/docs；出口 = 总控签「T0 过线」或回 HOLD  
- **禁** 借号开任一 R · 改价(=forge R) · Pack6 · 未签先写 T1 Java · 天赋/灰印续跑 · 与样本 R 同号
