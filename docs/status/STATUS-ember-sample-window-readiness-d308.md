# 状态 · D308：样本窗就绪清单 / 何时再开 R（方案 M · docs-only）

**日期：** 2026-10-08（上海时间）  
**上游：** 总控批 A · [`DESIGN-ember-sample-window-readiness-2026-10-08.md`](../design/DESIGN-ember-sample-window-readiness-2026-10-08.md) 方案 M（`878cf667`）  
**版本：** **docs-only** · CoreRpg **未升**（仍 **1.65.97**）· `balance_version` **60**（未抬）· **零部署**

## 人话

D298 遥测管已有，但各 R「样本够不够、读哪行、谁签字」仍散落。本窗**只落 docs 门禁表**：统一就绪硬表 = 后续任一战斗/经济 R 的**唯一开闸对照**；**当前全表不得开**——**不跑 p1sim · 不换 jar · 不改 yml**。

## 改了什么

| 窗 | 改动 |
|---|---|
| **M 硬表落地** | 设计 §2.1 就绪清单 / 合格样本定义 / 红线 / OP 读数口径 → 本 STATUS + OP 半页为仓内施工真源指针（**门槛口径仍以 DESIGN 为准，勿改设计主交付**） |
| **链入** | backlog `B-sample-window-readiness` → **已上线 D308**；tip 硬规格 → 已上线；加固稿 §6.1 旁注链 D298→D308 |
| **OP 半页** | [`STATUS-ember-sample-window-op-cheatsheet-d308.md`](STATUS-ember-sample-window-op-cheatsheet-d308.md)（禁玩家面） |
| jar / 玩法 yml | **未动** |

### 当前态摘要（2026-10-08 · 钉死「不得开」）

| 债 | 当前态 |
|----|--------|
| 事件 R/W · 调律 R | **未满**（D298 仅日级 · 缺完整 P 周 / `runs`≥30）· **不得开** |
| 工坊 R（forge 价） | **未满** · **不得开** · **禁**借 D307 再拧价 |
| 走廊 W2 · 深渊 R · 周本 R · Boss 预警 R · 挂机 R | **未满/等人感** · **不得开** |

完整门槛 / 关键指标 / 红线 / 签字人 → 见 DESIGN §2.1；**单局人感一句不算开闸**。

## 不动

- 任一 R 窗施工本体（事件 R/W · 调律 R · 工坊 R · 走廊 W2 · 深渊 R · 周本 R · Boss 预警 R · 挂机 R）  
- `refineCost` / `qualityCost` / `enhanceCost` / `upgradeCost` · 掉落 / `event_rate` / ALTS · 体力  
- Pack6 / 六槽 / 新模式图包 · 天赋 HOLD · 灰印 HOLD · 守招再调  
- D307 工坊菜单主交付 · 玩家面遥测 KPI / 排行榜 / 成就  
- 周报模板（方案 **R**）**后置**（未勾 M+R）  
- CoreRpg jar · TrMenu / `ember-v1*.yml` 玩法键 · bv **60** · login/proxy/play **未停未换**

## 验收

- 静态：DESIGN 文首 **已批 A · 批 M · D308**；本 STATUS + OP 半页入库；backlog / tip / 加固旁注已链；`git diff` **无** `CoreRpg/src/**` · **无** `plugins/**/*.yml` 玩法改 · **无** jar  
- **不跑** p1sim · **不部署**  
- 门禁自检：当前全表「不得开」；禁借本号开 R / 改价 / 重开 D307  
- 回滚：删本 STATUS / OP 半页 + 还原 backlog·tip·加固旁注（设计批注 commit `878cf667` 保留）

## 下一窗

- **等** ≥1 完整 P 周真人样本（荐 2）且全服 `p1_pf_runs`≥30（排除测试号）后再议事件 R/W · 调律 R  
- 编排/演出/挂机类：人感 ≥3 非测试 + 距对应 M ≥3 日 → 再派**该债**硬设计待批 A（须先把表内「当前态」升「满」）  
- 方案 **R**（周报模板）后置；脚本另号 · **禁**与战斗/经济 R 同号  
- **禁** 偷开任一 R · 改价(=forge R) · Pack6/六槽 · 天赋/灰印续跑 · 重开 D307 · 玩家面遥测 · 薄 UX 抬假硬债挡窗  
- **下一档硬债 tip（已写 · 非开 R）：** [`STATUS-ember-next-hard-debt-offhand-merge-decision-need-design-2026-10-08.md`](STATUS-ember-next-hard-debt-offhand-merge-decision-need-design-2026-10-08.md) · 加固§8-5 副手并轨/不并轨决策页 · docs-only · **不施工物品** · **仍禁**在样本未满时改派开任一 R
