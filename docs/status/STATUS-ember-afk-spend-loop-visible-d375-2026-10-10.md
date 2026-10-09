# 状态 · D375：挂机消费闭环可见化（产→花可点 · 已施工）

**日期：** 2026-10-10（上海时间）  
**上游：** tip [`STATUS-ember-next-hard-debt-afk-spend-loop-visible-need-design-2026-10-10.md`](STATUS-ember-next-hard-debt-afk-spend-loop-visible-need-design-2026-10-10.md) @ `819c1a33` · DESIGN [`DESIGN-ember-afk-spend-loop-visible-2026-10-10.md`](../design/DESIGN-ember-afk-spend-loop-visible-2026-10-10.md)  
**裁决：** **已批 A · 批 M · D375** · 总控采纳方案 M（W1a/W1b/W1c/W1e）· tip 旁注已关 · backlog 已对齐 · **≠关观察** · **≠改 afk.tiers / daily_kills** · **≠开 R** · **≠改 ×0.97 / 三开关 / bv**  
**版本：** TrMenu + docs · jar / afk / set_bonus / bv **未动**  
**toplevel（落字时）：** `/workspace/minecraft` @ `main`（C0 合格）

## 人话

挂机页现在能点「战利品去哪花」进工坊，旁格进材料仓库；战况/开页写明材料进仓去花；枢纽挂机格半行互指。产量表零改。

## 落地摘要（方案 M）

| 窗 | 本号 |
|----|------|
| W1a 消费跳转格 | `ember_p1_afk` 格 `S`「§6战利品去哪花」→ `menu: ember_p1_forge`；lore=币/xp/碎片/骨尘/核心/胚料对照 +「魂尘≠挂机」 |
| W1b 战况/Open 半行 | I lore +1 行「材料进仓库 · 点去哪花」；Open tell 改同口径 |
| W1c 仓库轻跳 | 邻格 `V`「§3材料仓库」→ `menu: ember_storage` |
| W1e 枢纽半行 | `ember_hub` 格 `Z` lore +「庭内可去工坊花材料」 |
| F 满额 | **未动** · 仍「体力还在 · 去冒险」 |

## 验收自检 V1–V6

| ID | 结果 | 备注 |
|----|------|------|
| V1 | **PASS（静态）** | Layout `# S 4 V #` · Icons `S`/`V` 存在 |
| V2 | **PASS（静态）** | `S` actions → `menu: ember_p1_forge` |
| V3 | **PASS（静态）** | lore 含六键短签；**无**「挂机产魂尘」；有「魂尘≠挂机」 |
| V4 | **PASS（静态）** | F 满额条件/文案未改；S/V 不占 F 位 |
| V5 | **PASS** | 本号未改 `ember-v1.yml`；skip-worktree 仍在 |
| V6 | **PASS（静态）** | `V` → `menu: ember_storage` |

## 改动清单（本号）

| 文件 | 改动 |
|------|------|
| `plugins/TrMenu/menus/ember_p1_afk.yml` | Layout 加 S/V；S→工坊；V→仓库；I/Open 半行 |
| `plugins/TrMenu/menus/ember_hub.yml` | Z 挂机格 lore +产→花半行 |
| `DESIGN-ember-afk-spend-loop-visible-2026-10-10.md` | 勾批 M；STATUS→已批·D375；变更记录 |
| tip `…-afk-spend-loop-visible-need-design-…` | 旁注已关 |
| `design-ember-content-backlog.md` | `B-afk-spend-loop-visible` → **已关 · D375 已施工** |
| 本 STATUS | 落地摘要 · V1–V6 |
| afk.tiers / ×0.97 / 三开关 / bv / jar / ladder / calamity / p1-six | **未动 / 未 stage** |

## 热更

- play **已起** · 已热更：`scripts/console.sh play "trmenu reload"` → 日志 `良好 | 70 个菜单已加载 (138 ms)`（**03:01:06 CST**）；afk/hub 亦曾被 TConfigWatcher 自动载入
- **未**执行 `/corerpg reload`（零 afk / 零开关变更）。

## 本号 commit 自检（按 D365）

| ID | 结果 | 备注 |
|----|------|------|
| C1 | **PASS** | 工作区有 ladder/calamity-state/p1-six/MM SavedData 脏迹（常态） |
| C2 | **PASS** | cached 仅授权路径（TrMenu/docs） |
| C3 | **PASS** | 显式 `git add` 各路径；未用 `add -A` |
| C4 | **PASS** | 未碰 live ember-v1；未切分支；toplevel=`/workspace/minecraft` |
| C5 | **PASS** | 脏 runtime 未入暂存 · 可 commit |

## 不动

关观察 · 改 afk.tiers / daily_kills · 改 ×0.97 / set_bonus / 三开关 / bv · 开 R · 开 K3 · Pack6 / 天赋 / 灰印 · stage 脏 runtime · 玩家面教裸 `/corerpg …`

---

*D375 批 A·M · tip `819c1a33` · 挂机产→花可见可点 · ≠关观察 ≠抬日表。*
