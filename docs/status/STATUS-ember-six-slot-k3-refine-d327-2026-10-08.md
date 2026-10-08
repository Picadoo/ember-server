# 状态 · D327：六槽 K3 同部位熔炼决策页批 A · 方案 M（docs-only）

**日期：** 2026-10-08（上海时间）  
**上游：** 总控批 A · 批 M · [`DESIGN-ember-six-slot-k3-same-slot-refine-2026-10-08.md`](../design/DESIGN-ember-six-slot-k3-same-slot-refine-2026-10-08.md)（交稿 `126f7d6f`）· tip [`STATUS-ember-next-hard-debt-six-slot-k3-refine-need-design-2026-10-08.md`](STATUS-ember-next-hard-debt-six-slot-k3-refine-need-design-2026-10-08.md)（`8b22bc2b`）· D326 观察批 M · D325 live bv62  
**版本：** **docs-only** · jar / 线上 yml / bv **未动**（仍 bv62 · `set_bonus=true` · `1.65.99-d325.local`）· **零部署**

## 人话

总控采纳 **方案 M**：K3 规则钉死（同部位取高精工、零价、材料销毁、不成色/族/阶）；**观察期可另号离线 T0‴**（p1sim only）；施工/部署默认等 Stage2 绿出口（不早于 **2026-10-10 17:40 CST** + 观察另签）；**不批 L**；**本号不另签并行施工**。线上玩家本窗**无新感知**。

## 改了什么

| 项 | 改动 |
|---|---|
| 决策页 DESIGN | STATUS **待批 A → 已批 A · 批 M · D327** · §7 勾「批 A：采纳方案 M」+ 总控批注 · 变更记录 |
| tip | 加结案旁注：已关 · 批 A · 批 M · D327；硬规格 STATUS=已批 A · 批 M |
| backlog | `B-six-slot-k3-same-slot-refine` → **已批 A · 批 M · D327 · 下一步另号 T0‴** |
| jar / 玩法 yml / 开关 / bv | **未动** |

## 不动

- 施工 / 部署（默认等 Stage2 绿出口；本号不另签并行）  
- 方案 L · 样本 R 全表 · Pack6 · 天赋 · 灰印 · 改 ×0.97 · 拧 set_bonus · 动 F / 护符 ×1.0 · 改价表 · 熔炼改成色/族/阶  
- 线上 `set_bonus` / enabled / migrate / jar / bv  

## 验收

- 静态：DESIGN 文首 **已批 A · 批 M · D327**；本 STATUS 入库；tip / backlog 对齐；`git diff` 无 `CoreRpg/src/**`、无玩法 yml、无 jar  
- **本号不部署** · **不写熔炼 Java/菜单进 live**  
- 回滚：删本 STATUS + 还原 DESIGN 批注 / tip / backlog（交稿 `126f7d6f` 保留）

## 下一窗

- **另号离线 T0‴**（p1sim · G2/G3/G4/G8/G10；PASS 不自动施工）  
- 施工/部署：Stage2 绿出口后另号，或总控另签并行（本号未签）  
- **禁** 借号开 R / Pack6 / 改 ×0.97 / 观察期部署 K3 / 顺手关 Stage1
