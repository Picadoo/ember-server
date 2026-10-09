# 状态 · D379：日更残誓地窖第三拍预警环（批 A·M · docs 占位 · 等 MM 落地）

**日期：** 2026-10-10（上海时间）  
**上游：** tip [`STATUS-ember-next-hard-debt-daily-crypt-telegraph-need-design-2026-10-10.md`](STATUS-ember-next-hard-debt-daily-crypt-telegraph-need-design-2026-10-10.md) @ `b3c025f1` · DESIGN [`DESIGN-ember-daily-crypt-telegraph-2026-10-10.md`](../design/DESIGN-ember-daily-crypt-telegraph-2026-10-10.md)  
**裁决：** **已批 A · 批 M · D379** · 总控采纳方案 M · tip 旁注已关 · backlog→**已批 A·施工中 · 怪物岗** · **本号 docs-only 占位** · **≠本号改 MM** · **≠改体力/掉落** · **≠关观察** · **≠开 R** · **≠开 K3** · **≠改 ×0.97 / 三开关 / bv** · **≠复述 D374 四线**  
**版本：** **docs-only**（DESIGN 勾批 · tip 关 · backlog · 本 STATUS）· MythicMobs / DP / 体力 / 掉落 / jar / set_bonus / bv **未动**  
**toplevel（落字时）：** `/workspace/minecraft` @ `main`（C0 合格）

## 人话

D374 六线终厅已「看提示→拉开可躲」；七线只剩残誓地窖静默环。本号只批方案并占位——**MM 改文件交怪物岗另号**；体力掉落开关不动。

## 已批摘要（方案 M）

| 线 | Boss | 新 Skill（名可微调须唯一） | 动作 |
|----|------|---------------------------|------|
| 残誓地窖 | `EmberDailyCryptWarden` | `EmberCryptOathCast` | 删无预警 `damage 0.4 r6 ~onTimer:50` → 挂 `~onTimer:80`；**保留**点名+SLOW `~160` |

**钉：** r=5 · amount=1.2 · delay 25 · Cooldown 8 · onTimer 80 · 范式对齐 FrostNova/RailSlam / D374 四 Cast · Cast **默认无** SLOW（点名自带）。  
**主题：** 文案「残誓/守墓」+ 暗色粒子；禁抄「霜核」用词。  
**禁：** 抬体力/掉落 · 假平面 · 本号改 MM · 关观察 · 开 R · 开 K3 · 改 ×0.97 · 复述 D374 四线。

## 本号范围

| 做 | 不做 |
|----|------|
| DESIGN 勾批 A·M · tip 关 · backlog 施工中·怪物岗 · 本 STATUS 占位 | 改 `EmberDungeonDiffSkills.yml` / `EmberDailyCrypt.yml` |
| 显式 add docs push（D365） | 改 DP / TrMenu（可选 lore 另号）/ 体力 / 掉落 / 开关 / jar |

## 验收自检（本号 docs）

| ID | 结果 | 备注 |
|----|------|------|
| D1 | **PASS** | DESIGN STATUS→已批 A·批 M·D379；勾批 M；总控批注 |
| D2 | **PASS** | tip 旁注已关 · 硬规格→已批 |
| D3 | **PASS** | backlog `B-ember-daily-crypt-telegraph` → 已批 A·施工中·怪物岗 |
| D4 | **PASS** | 本号未碰 MM / 体力 / 掉落 / 三开关 / bv / jar |

## 改动清单（本号）

| 文件 | 改动 |
|------|------|
| `DESIGN-ember-daily-crypt-telegraph-2026-10-10.md` | STATUS→已批；勾批；总控批注；变更记录 |
| tip `…-daily-crypt-telegraph-need-design-…` | 旁注已关 |
| `design-ember-content-backlog.md` | 新增 `B-ember-daily-crypt-telegraph` → **已批 A·施工中 · 怪物岗** |
| 本 STATUS | 占位 · 等 MM 落地 |
| MythicMobs / DP / 体力 / 掉落 / ember-v1 / jar | **未动** |
| ladder / calamity-state / p1-six / MM SavedData | **未 stage** |

## 下一号（怪物岗）

按 DESIGN §3.1–§3.2 / §3.6：落 `EmberCryptOathCast` + Boss 挂载；保留点名块；测法复用霜/锈/D374 五条；**仍禁**体力掉落/DP/假平面；**禁**改六线既有 Cast。

## 本号 commit 自检（按 D365）

| ID | 结果 | 备注 |
|----|------|------|
| C1 | **PASS** | 工作区有脏 runtime 常态 |
| C2 | **待 commit 时核** | cached 仅授权 docs |
| C3 | **待 commit 时核** | 显式 `git add` 路径；未用 `add -A` |
| C4 | **PASS** | 未碰 live ember-v1；未切分支；toplevel=`/workspace/minecraft` |
| C5 | **待 commit 时核** | 脏 runtime 未入暂存 |

## 不动

本号改 MM · 改体力/掉落 · 关观察 · 开 R · 开 K3 · 改 ×0.97 / 三开关 / bv · Pack6 / 天赋 / 灰印 · stage 脏 runtime · 假平面 · 复述 D374 四线

---

*D379 批 A·M · tip `b3c025f1` · docs 占位 · 施工交怪物岗 · ≠本号改 MM ≠改体力掉落 ≠关观察。*
