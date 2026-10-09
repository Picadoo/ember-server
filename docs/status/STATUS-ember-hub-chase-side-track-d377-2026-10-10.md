# 状态 · D377：枢纽「今日可追」旁轨（生活·使魔·工坊 · 已施工）

**日期：** 2026-10-10（上海时间）  
**上游：** tip [`STATUS-ember-next-hard-debt-hub-chase-side-track-need-design-2026-10-10.md`](STATUS-ember-next-hard-debt-hub-chase-side-track-need-design-2026-10-10.md) @ `5c0ab5cd` · DESIGN [`DESIGN-ember-hub-chase-side-track-2026-10-10.md`](../design/DESIGN-ember-hub-chase-side-track-2026-10-10.md)  
**裁决：** **已批 A · 批 M · D377** · 总控采纳方案 M（W1a/W1b/W1c/W1d）· tip 旁注已关 · backlog 已对齐 · **≠关观察** · **≠改 route_pri 计算** · **≠改 afk 日表 / 体力** · **≠开 R** · **≠改 ×0.97 / 三开关 / bv**  
**版本：** TrMenu + docs · jar / afk / set_bonus / bv / route_* PAPI **未动**  
**toplevel（落字时）：** `/workspace/minecraft` @ `main`（C0 合格）

## 人话

打开枢纽「今天该打哪」，战斗四态下面多了一行「今日可追」：生活兑魂尘养使魔、材料去工坊花。左键仍走挂机/冒险主推荐；右键可轻跳补给或工坊。产量与体力零改。

## 落地摘要（方案 M）

| 窗 | 本号 |
|----|------|
| W1a 决策卡旁轨行 | H 三分支（体力不足 / 挂机满 / 默认）各 +「今日可追」+ 真入口互指 |
| W1b 假平面禁与互指 | 只指 `ember_life` / `ember_p1_forge`；Open tell「旁轨见今天该打哪」；**无**旧日常窟/重铸 |
| W1c 轻跳（同批） | 体力不足：左→挂机·右→生活；挂机满：左→冒险·右→工坊；默认：左→冒险·右→生活 |
| W1d 冒险半指 | `ember_p1_adventure` 短目标格 +「旁轨：生活·工坊见枢纽今日卡」 |

## 验收自检

| ID | 结果 | 备注 |
|----|------|------|
| V1 | **PASS（静态）** | H 三分支 lore 均含「今日可追」且提生活/使魔或工坊 |
| V2 | **PASS（静态）** | 体力不足：旁轨句在；左键 `ember_p1_afk`；右键 `ember_life` |
| V3 | **PASS（静态）** | 挂机满：旁轨句在；左键 `ember_p1_adventure`；右键 `ember_p1_forge` |
| V4 | **PASS（静态）** | 右键轻跳目标为真菜单；与 D376/D375 入口一致 |
| V5 | **PASS（静态）** | **无**「挂机产魂尘」；**无**旧日常窟入口 |
| V6 | **PASS** | 本号未改 daily_kills / tiers / stamina / legacy_gate / route_* 计算 |

## 改动清单（本号）

| 文件 | 改动 |
|------|------|
| `plugins/TrMenu/menus/ember_hub.yml` | H 三分支旁轨行 + 左/右键；Open tell 半行 |
| `plugins/TrMenu/menus/ember_p1_adventure.yml` | 短目标格旁轨半行 |
| `DESIGN-ember-hub-chase-side-track-2026-10-10.md` | 勾批 M；STATUS→已批·D377；变更记录 |
| tip `…-hub-chase-side-track-need-design-…` | 旁注已关 |
| `design-ember-content-backlog.md` | `B-hub-chase-side-track` → **已关 · D377 已施工** |
| 本 STATUS | 落地摘要 · 验收 |
| afk.tiers / stamina / ×0.97 / 三开关 / bv / jar / route_* Java / ladder / calamity / p1-six | **未动 / 未 stage** |

## 热更

- play 热更：`scripts/console.sh play "trmenu reload"`（见落字后执行日志）
- **未**执行 `/corerpg reload`（零 afk / 零开关 / 零 route 计算变更）

## 本号 commit 自检（按 D365）

| ID | 结果 | 备注 |
|----|------|------|
| C1 | **PASS** | 工作区有 ladder/calamity-state/p1-six/MM SavedData 脏迹（常态） |
| C2 | **PASS** | cached 仅授权路径（TrMenu/docs） |
| C3 | **PASS** | 显式 `git add` 各路径；未用 `add -A` |
| C4 | **PASS** | 未碰 live ember-v1；未切分支；toplevel=`/workspace/minecraft` |
| C5 | **PASS** | 脏 runtime 未入暂存 · 可 commit |

## 不动

关观察 · 改 route_pri/route_sec 计算 · 改 afk.tiers / daily_kills / 体力 · 改 ×0.97 / set_bonus / 三开关 / bv · 开 R · 开 K3 · Pack6 / 天赋 / 灰印 · stage 脏 runtime · 回盘 E/F · 复述 D373–D376 主交付

---

*D377 批 A·M · tip `5c0ab5cd` · 枢纽今日卡旁轨可追 · ≠关观察 ≠抬挂机表 ≠改 route_pri。*
