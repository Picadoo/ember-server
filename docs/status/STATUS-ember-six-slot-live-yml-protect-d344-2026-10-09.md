# 状态 · D344：观察期 live yml 防冲（批 A·M · OPS 已落 · ≠关观察）

**日期：** 2026-10-09（上海时间）  
**上游：** tip [`STATUS-ember-next-hard-debt-six-slot-live-yml-protect-need-design-2026-10-09.md`](STATUS-ember-next-hard-debt-six-slot-live-yml-protect-need-design-2026-10-09.md) @ `7c9b8ddd` · DESIGN [`DESIGN-ember-six-slot-live-yml-protect-2026-10-09.md`](../design/DESIGN-ember-six-slot-live-yml-protect-2026-10-09.md)  
**裁决：** **已批 A · 批 M · D344** · 总控采纳防冲纪律 · OPS 「观察期 yml 真源与防冲」已落 · **批 A ≠ 改默认 true ≠ 关观察 ≠ 开 K3**  
**版本：** **docs + 保护键钉仓对齐**（DESIGN · tip 旁注 · backlog · OPS §1.5 · 本 STATUS）· 三开关 / bv 真值 **保持** 三 true + 62（D343 钉仓 `c9888b3a`；本号开跑前复核仍绿）· ×0.97 / K3 / 关窗 **未动**

## 人话

D342 证实 resources→plugins 整份覆盖会抹掉六槽段并把 bv 打回 60；D343 还证实主工作区 `checkout`/`switch` 因 live yml **被 git 跟踪**也会整份盖掉。本号只批防冲纪律并落 OPS：真源、保护键、禁反方向覆盖、**禁主仓切分支**、**skip-worktree**、只许 live→resources。**不关观察、不改代码默认 true、不开 K3。**

## 开跑前 live 核对（本号）

| 项 | 结果 |
|----|------|
| `gear.six_slot.enabled/migrate/set_bonus` | **true / true / true**（未冲；无需从 pin 恢复） |
| `balance_version` | **62** |
| 恢复动作 | **无**（STATUS 记：现盘绿，未动真值） |
| 钉仓 | live+src 保护键已在 `c9888b3a`（D343）；本号 docs 落字 |

## 批注要点

| 项 | 口径 |
|----|------|
| 采纳 | DESIGN 方案 M：防冲纪律 + OPS 规格 |
| 批 A | **≠** 改 resources/代码默认 true · **≠** 关观察 · **≠** 开 K3 · **≠** 拧 ×0.97 |
| 总控加注 | 主工作区禁 `checkout`/`switch`（跟踪 live 会被盖）；K3 离线用独立 worktree；live 两文件观察期 `skip-worktree` |
| 同步方向 | 只许 **live→resources**；禁 resources→plugins |
| 前车 | D342 R1+R2 红 · D343 恢复+钉仓 · pin `/workspace/tmp/d343-pin/` |

## 改动（本号）

| 文件 | 改动 |
|------|------|
| `DESIGN-ember-six-slot-live-yml-protect-2026-10-09.md` | STATUS→已批 A·M·D344；勾批 A；总控批注（含加注）；§2.3/§2.5 补禁切分支 + skip-worktree |
| tip `…-live-yml-protect-need-design-…` | 旁注已关 · 硬规格→已批 A·D344 |
| `OPS-ember-six-slot-migration.md` | 新 §1.5「观察期 yml 真源与防冲」 |
| backlog `B-six-slot-live-yml-protect` | → **已批 A · OPS 已落 · ≠关观察** |
| 本 STATUS | 批 A·M · OPS 落字声明 · live 核对 |
| 三开关 / bv / ×0.97 / K3 / 关窗 | **未拧**（保持观察） |
| `ladder.yml` / 脏 runtime | **未 stage** |

## 不动

×0.97 · 关 Stage2 观察 · 开 K3 live · 改代码默认 true · Pack6 / 天赋 / 灰印 / 样本 R · 主仓切分支（纪律）· 本号不代替绿出口

---

*D344 批 A·M · tip `7c9b8ddd` · OPS §1.5 已落 · 禁主仓切分支 · skip-worktree · ≠关观察 · ≠改默认 true。*
