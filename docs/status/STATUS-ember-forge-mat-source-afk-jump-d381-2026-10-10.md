# 状态 · D381：工坊「材料哪来」→挂机庭反向跳（产花双向互指 · 已施工）

**日期：** 2026-10-10（上海时间）  
**上游：** tip [`STATUS-ember-next-hard-debt-forge-mat-source-afk-jump-need-design-2026-10-10.md`](STATUS-ember-next-hard-debt-forge-mat-source-afk-jump-need-design-2026-10-10.md) @ `62d5aef2` · DESIGN [`DESIGN-ember-forge-mat-source-afk-jump-2026-10-10.md`](../design/DESIGN-ember-forge-mat-source-afk-jump-2026-10-10.md)  
**裁决：** **已批 A · 批 M · D381** · 总控采纳方案 M（W1a+W1b 必做；W1c+W1d 同批）· tip 旁注已关 · backlog 已对齐 · **≠关观察** · **≠改 afk.tiers / daily_kills** · **≠改工坊价表** · **≠开 R** · **≠改 ×0.97 / 三开关 / bv**  
**版本：** TrMenu + docs · jar / afk / set_bonus / bv **未动**  
**toplevel（落字时）：** `/workspace/minecraft` @ `main`（C0 合格）

## 人话

站在工坊缺料时，能点底栏「材料哪来·挂机庭」回到挂机庭看今日产/层名片；打开页和强化预览也会提示这条路。挂机「去哪花」仍可回工坊。仓库半行+轻跳同源。产量与价表零改。

## 落地摘要（方案 M）

| 窗 | 本号 |
|----|------|
| W1a 来源跳转格 | `ember_p1_forge` Layout `# A b  B#`；A=`§a材料哪来 · 挂机庭` → `menu: ember_p1_afk` |
| W1b Open/半行 | Open tell「缺料？点材料哪来回挂机庭」；强化预览 lore +「材料多从挂机进仓」 |
| W1c 仓库半对齐 | `ember_storage` V lore +「挂机战利品进本仓」；邻格 A → `menu: ember_p1_afk` |
| W1d 挂机回指 | `ember_p1_afk` S「去哪花」lore +「工坊内可回本庭」——**未重做** D375 主格 |
| E/U/R/Q/S/D | **未挤** · 费用命令与 PAPI **未改** |

## 验收自检 V1–V7

| ID | 结果 | 备注 |
|----|------|------|
| V1 | **PASS（静态）** | Layout 含 A；Icons.A 短 «材料哪来·挂机庭» |
| V2 | **PASS（静态）** | A actions → `menu: ember_p1_afk` |
| V3 | **PASS（静态）** | afk S 仍 → `menu: ember_p1_forge`；+回指半行 |
| V4 | **PASS（静态）** | lore 含产→仓→花；有「魂尘≠挂机」；**无**抬产暗示 |
| V5 | **PASS（静态）** | E/U/R/Q/S/D 确认列仍在；甲灰显未动 |
| V6 | **PASS** | 本号未改 `ember-v1.yml`；skip-worktree 仍在 |
| V7 | **PASS（静态）** | storage V 半行 + A 跳挂机 |

## 改动清单（本号）

| 文件 | 改动 |
|------|------|
| `plugins/TrMenu/menus/ember_p1_forge.yml` | Layout 加 A；A→挂机；Open/强化半行互指 |
| `plugins/TrMenu/menus/ember_storage.yml` | V 半行；A→挂机 |
| `plugins/TrMenu/menus/ember_p1_afk.yml` | S lore +「工坊内可回本庭」 |
| `DESIGN-ember-forge-mat-source-afk-jump-2026-10-10.md` | 勾批 M；STATUS→已批·D381；变更记录 |
| tip `…-forge-mat-source-afk-jump-need-design-…` | 旁注已关 |
| `design-ember-content-backlog.md` | `B-forge-mat-source-afk-jump` → **已关 · D381 已施工** |
| 本 STATUS | 落地摘要 · V1–V7 |
| afk.tiers / 价表 / ×0.97 / 三开关 / bv / jar / ladder / calamity / p1-six / MM | **未动 / 未 stage** |

## 热更

- play **已起** · 已热更：`scripts/console.sh play "trmenu reload"` → 日志 `良好 | 70 个菜单已加载 (84 ms)`（**03:32:21 CST**）
- **未**执行 `/corerpg reload`（零 afk / 零价表 / 零开关变更）

## 本号 commit 自检（按 D365）

| ID | 结果 | 备注 |
|----|------|------|
| C1 | **PASS** | 工作区有 ladder/calamity-state/p1-six/MM SavedData 脏迹（常态） |
| C2 | **PASS** | cached 仅授权路径（TrMenu/docs） |
| C3 | **PASS** | 显式 `git add` 各路径；未用 `add -A` |
| C4 | **PASS** | 未碰 live ember-v1；未切分支；toplevel=`/workspace/minecraft` |
| C5 | **PASS** | 脏 runtime 未入暂存 · 可 commit |

## 不动

关观察 · 改 afk.tiers / daily_kills · 改工坊价表 · 改 ×0.97 / set_bonus / 三开关 / bv · 开 R · 开 K3 · Pack6 / 天赋 / 灰印 · stage 脏 runtime · 复述 D373–D380 主交付 · 重做 D375「去哪花」主格

---

*D381 批 A·M · tip `62d5aef2` · 工坊↔挂机产花双向可点 · ≠关观察 ≠抬日表 ≠改价。*
