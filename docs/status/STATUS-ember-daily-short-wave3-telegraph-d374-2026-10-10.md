# 状态 · D374：日更短本第三拍四线预警（批 A·M · docs 占位 · 等 MM 落地）

**日期：** 2026-10-10（上海时间）  
**上游：** tip [`STATUS-ember-next-hard-debt-daily-short-wave3-telegraph-need-design-2026-10-10.md`](STATUS-ember-next-hard-debt-daily-short-wave3-telegraph-need-design-2026-10-10.md) @ `a944ed30` · DESIGN [`DESIGN-ember-daily-short-wave3-telegraph-2026-10-10.md`](../design/DESIGN-ember-daily-short-wave3-telegraph-2026-10-10.md)  
**裁决：** **已批 A · 批 M · D374** · 总控采纳方案 M · tip 旁注已关 · backlog→**已批 A·施工中 · 怪物岗** · **本号 docs-only 占位** · **≠本号改 MM** · **≠改体力/掉落** · **≠关观察** · **≠开 R** · **≠开 K3** · **≠改 ×0.97 / 三开关 / bv**  
**版本：** **docs-only**（DESIGN 勾批 · tip 关 · backlog · 本 STATUS）· MythicMobs / DP / 体力 / 掉落 / jar / set_bonus / bv **未动**  
**toplevel（落字时）：** `/workspace/minecraft` @ `main`（C0 合格）

## 人话

霜/锈第三拍已过；庭院·焦骨·潮蚀·断塔终厅还缺「看提示→拉开可躲」。本号只批方案并占位——**MM 改文件交怪物岗另号**；体力掉落开关不动。

## 已批摘要（方案 M）

| 线 | Boss | 新 Skill（名可微调须唯一） | 动作 |
|----|------|---------------------------|------|
| 庭院 | `EmberDailyBrute` | `EmberYardPulseCast` | 删无预警环 → 挂 `~onTimer:80` |
| 焦骨 | `EmberAshBrute` | `EmberAshBurstCast` | 同上 |
| 潮蚀 | `EmberDailyTideBrute` | `EmberTideCrashCast` | 删环伤+独立 SLOW → 读条可带轻 SLOW |
| 断塔 | `EmberDailySpireWarden` | `EmberSpireSlamCast` | 删环；保留逼近 message |

**钉：** r=5 · amount=1.2 · delay 25 · Cooldown 8 · onTimer 80 · 范式对齐 FrostNova/RailSlam。  
**后置：** 残誓地窖同构。  
**禁：** 抬体力/掉落 · 假平面 · 本号改 MM · 关观察 · 开 R · 开 K3 · 改 ×0.97。

## 本号范围

| 做 | 不做 |
|----|------|
| DESIGN 勾批 A·M · tip 关 · backlog 施工中·怪物岗 · 本 STATUS 占位 | 改 `EmberDungeonDiffSkills.yml` / 四份 `EmberDaily*.yml` |
| 显式 add docs push（D365） | 改 DP / TrMenu（可选 lore 另号）/ 体力 / 掉落 / 开关 / jar |

## 验收自检（本号 docs）

| ID | 结果 | 备注 |
|----|------|------|
| D1 | **PASS** | DESIGN STATUS→已批 A·批 M·D374；勾批 M；总控批注 |
| D2 | **PASS** | tip 旁注已关 · 硬规格→已批 |
| D3 | **PASS** | backlog `B-ember-daily-short-wave3-telegraph` → 已批 A·施工中·怪物岗 |
| D4 | **PASS** | 本号未碰 MM / 体力 / 掉落 / 三开关 / bv / jar |

## 改动清单（本号）

| 文件 | 改动 |
|------|------|
| `DESIGN-ember-daily-short-wave3-telegraph-2026-10-10.md` | STATUS→已批；勾批；总控批注；变更记录 |
| tip `…-daily-short-wave3-telegraph-need-design-…` | 旁注已关 |
| `design-ember-content-backlog.md` | 新增/对齐 `B-ember-daily-short-wave3-telegraph` → **已批 A·施工中 · 怪物岗** |
| 本 STATUS | 占位 · 等 MM 落地 |
| MythicMobs / DP / 体力 / 掉落 / ember-v1 / jar | **未动** |
| ladder / calamity-state / p1-six / MM SavedData | **未 stage** |

## 下一号（怪物岗）

按 DESIGN §2.1–§2.2 / §2.7：落四条 Cast + Boss 挂载；测法复用霜/锈五条；**仍禁**体力掉落/DP/假平面。

## 本号 commit 自检（按 D365）

| ID | 结果 | 备注 |
|----|------|------|
| C1 | **PASS** | 工作区有脏 runtime 常态 |
| C2 | **待 commit 时核** | cached 仅授权 docs |
| C3 | **待 commit 时核** | 显式 `git add` 路径；未用 `add -A` |
| C4 | **PASS** | 未碰 live ember-v1；未切分支；toplevel=`/workspace/minecraft` |
| C5 | **待 commit 时核** | 脏 runtime 未入暂存 |

## 不动

本号改 MM · 改体力/掉落 · 关观察 · 开 R · 开 K3 · 改 ×0.97 / 三开关 / bv · Pack6 / 天赋 / 灰印 · stage 脏 runtime · 假平面

---

*D374 批 A·M · tip `a944ed30` · docs 占位 · 施工交怪物岗 · ≠本号改 MM ≠改体力掉落 ≠关观察。*
