# 状态 · D326：六槽 Stage2 观察关窗检查单批 A · 方案 M（docs-only）

**日期：** 2026-10-08（上海时间）  
**上游：** 总控批 A · 批 M · [`DESIGN-ember-six-slot-stage2-observe-close-2026-10-08.md`](../design/DESIGN-ember-six-slot-stage2-observe-close-2026-10-08.md)（交稿 `e8d2ca8e`）· tip [`STATUS-ember-next-hard-debt-six-slot-stage2-observe-close-need-design-2026-10-08.md`](STATUS-ember-next-hard-debt-six-slot-stage2-observe-close-need-design-2026-10-08.md)（`b2f9b744`）· live [`STATUS-ember-six-slot-stage2-set-bonus-live-d325-bv62-2026-10-08.md`](STATUS-ember-six-slot-stage2-set-bonus-live-d325-bv62-2026-10-08.md)（`6e734dc7`）  
**版本：** **docs-only** · jar / 线上 yml / bv **未动**（仍 bv62 · `set_bonus=true` · `1.65.99-d325.local`）· **零部署**

## 人话

总控采纳 **方案 M**：Stage2 观察自 live 起点 **2026-10-08 17:40 CST** 起满 **48h**（不早于 **2026-10-10 17:40 CST**），且 §2 必抽整轮 ≥1 PASS，才可另签绿出口「观察结束 · 维持 bv62 / set_bonus=true」。本号**不**关观察、**不**改 ×0.97、**不**升 L、**不**勾 M+R 门闩。线上玩家本窗**无新感知**。

## 改了什么

| 项 | 改动 |
|---|---|
| 检查单 DESIGN | STATUS **待批 A → 已批 A · 批 M · D326** · §6 勾「批 A：采纳方案 M」+ 总控批注 · 变更记录 |
| tip | 加结案旁注：已关 · 批 A · 批 M · D326；硬规格 STATUS=已批 A · 批 M |
| backlog | `B-six-slot-stage2-observe-close` → **已批 A · 批 M · D326 · 观察执行中 · 绿出口另签** |
| jar / 玩法 yml / 开关 / bv | **未动** |

## 不动

- 关观察签字（§4 绿出口另签；不早于 2026-10-10 17:40 CST）  
- 升级 L · M+R 门闩本号 · 样本 R 全表 · K3 · Pack6 · 天赋 · 灰印 · 档 S · 再改 ×0.97  
- 线上 `set_bonus` / enabled / migrate / jar / bv · Stage1 观察开关  
- p1sim · 任一 R 窗施工

## 验收

- 静态：DESIGN 文首 **已批 A · 批 M · D326**；本 STATUS 入库；tip / backlog 对齐；`git diff` 无 `CoreRpg/src/**`、无玩法 yml、无 jar  
- **本号不部署** · **不关观察**  
- 回滚：删本 STATUS + 还原 DESIGN 批注 / tip / backlog（交稿 `e8d2ca8e` 保留）

## 下一窗

- **观察执行中**：测试按 DESIGN §2 必抽；红线走 §3；满窗后总控**另签**绿出口或继续观察 / 回滚  
- **禁** 借号开 R / K3 / Pack6 / 档 S / 改 ×0.97 / 关 set_bonus 不记 STATUS / 顺手关 Stage1
