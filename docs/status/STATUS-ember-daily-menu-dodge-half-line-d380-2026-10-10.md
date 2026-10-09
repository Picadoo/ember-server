# 状态 · D380：日更七线菜单「终厅读条可躲」半行统一（已施工）

**日期：** 2026-10-10（上海时间）  
**上游：** tip [`STATUS-ember-next-hard-debt-daily-menu-dodge-half-line-need-design-2026-10-10.md`](STATUS-ember-next-hard-debt-daily-menu-dodge-half-line-need-design-2026-10-10.md) @ `c9395035` · DESIGN [`DESIGN-ember-daily-menu-dodge-half-line-2026-10-10.md`](../design/DESIGN-ember-daily-menu-dodge-half-line-2026-10-10.md)  
**裁决：** **已批 A · 批 M · D380** · 总控采纳方案 M（七键主+灰态半行必做；T 总述同批）· tip 已关 · backlog `B-daily-menu-dodge-half-line` 已关 · **≠关观察** · **≠改 MM / 体力 / 掉落** · **≠抬日表** · **≠开 R** · **≠改 ×0.97 / 三开关 / bv**  
**版本：** TrMenu + docs · jar / MM / DP / afk / set_bonus / bv **未动**  
**toplevel（落字时）：** `/workspace/minecraft` @ `main`（C0 合格）

## 人话

打开日常选线页，七线悬停都能看见「终厅 · 读条提示后环伤 · 拉开可躲」；体力不够的灰态也看得到。进本路径、体力 30、掉落零改——只多了进门前预期。残誓测未回不挡宣传（禁写「测岗已签」）。

## 落地摘要（方案 M）

| 窗 | 本号 |
|----|------|
| 七键主 lore | S/A/C/D/E/F/G 地形句与体力块之间各插主题色半行「终厅 · 读条提示后环伤 · 拉开可躲」 |
| 七灰态子 lore | 同位置同语义半行（保留主题色） |
| 顶栏 T | +1 行「§e七线终厅：读条提示后环伤 · 拉开可躲」 |
| enter / condition / 材料 / P / B | **未动** |

## 验收自检

| ID | 结果 | 备注 |
|----|------|------|
| V1 | **PASS（静态）** | `rg '可躲'`：七主 + 七灰 = 14；T 总述 1 |
| V2 | **PASS（静态）** | enter 七命令 / 体力 condition `< 30` / P / B **未改** |
| V3 | **PASS** | 全文无「测岗已签 / PASS / 已签字」玩家面 |
| V4 | **PASS** | 本号未改 MM / DP / 体力 cost / 掉落 / ×0.97 / 三开关 / bv / jar |
| V5 | **PASS（热更）** | `trmenu reload` 见下 |

## 改动清单（本号）

| 文件 | 改动 |
|------|------|
| `plugins/TrMenu/menus/ember_daily.yml` | 七主+七灰+ T 半行；enter/condition/P/B 不动 |
| `DESIGN-ember-daily-menu-dodge-half-line-2026-10-10.md` | 勾批 M；STATUS→已批·D380；变更记录 |
| tip `…-daily-menu-dodge-half-line-need-design-…` | 旁注已关 |
| `design-ember-content-backlog.md` | `B-daily-menu-dodge-half-line` → **已关 · D380 已施工** |
| 本 STATUS | 落地摘要 · 验收 |
| MM / DP / 体力 / 掉落 / ×0.97 / 三开关 / bv / jar / ladder / calamity / p1-six | **未动 / 未 stage** |

## 热更

- play 热更：`scripts/console.sh play "trmenu reload"`（见落字后执行日志）
- **未**执行 `/mm reload` / `/corerpg reload`（零 MM/CoreRpg 变更）

## 本号 commit 自检（按 D365）

| ID | 结果 | 备注 |
|----|------|------|
| C1 | **PASS** | 工作区有 ladder/calamity-state/p1-six/MM SavedData 脏迹（常态） |
| C2 | **PASS** | cached 仅授权路径（TrMenu/docs） |
| C3 | **PASS** | 显式 `git add` 各路径；未用 `add -A` |
| C4 | **PASS** | 未碰 live ember-v1；未切分支；toplevel=`/workspace/minecraft` |
| C5 | **PASS** | 脏 runtime 未入暂存 · 可 commit |

## 不动

关观察 · 改 MM Cast / Boss / 掉落 · 改体力 30 · 抬 afk.tiers / daily_kills · 改 ×0.97 / set_bonus / 三开关 / bv · 开 R · 开 K3 · Pack6 / 天赋 / 灰印 · stage 脏 runtime · 复述 D373–D379 主交付 · 写「测岗已签」

---

*D380 批 A·M · tip `c9395035` · 七线菜单终厅可读可躲 · ≠关观察 ≠改 MM ≠抬日表。*
