# 状态 · D388：生活等级/次数同屏（批 A·M · docs 占位 · 插件补键中 · 菜单挂键另号）

**日期：** 2026-10-10（上海时间）  
**上游：** tip [`STATUS-ember-next-hard-debt-life-level-papi-need-design-2026-10-10.md`](STATUS-ember-next-hard-debt-life-level-papi-need-design-2026-10-10.md) @ `8305129d` · DESIGN [`DESIGN-ember-life-level-papi-2026-10-10.md`](../design/DESIGN-ember-life-level-papi-2026-10-10.md)  
**裁决：** **已批 A · 批 M · D388** · 总控采纳方案 M · tip 旁注已关 · backlog→**已批·插件施工中** · **本号 docs-only 占位** · **插件补键中** · **菜单挂键另号** · **≠本号写 Java** · **≠改 live 菜单** · **≠改 life.yml 价/日周顶/level_xp** · **≠关观察** · **≠抬日表** · **≠开 R** · **≠开 K3** · **≠改 ×0.97 / 三开关 / bv** · **≠假写固定 Lv** · **≠回盘 E/F** · **≠空跳转** · **≠复述 D373–D386**  
**版本：** **docs-only**（DESIGN 勾批 · tip 关 · backlog · 本 STATUS）· jar / CorePapi / TrMenu `ember_life` / life.yml / set_bonus / bv **未动**  
**toplevel（落字时）：** `/workspace/minecraft` @ `main`（C0 合格）

## 人话

生活等级与兑尘/孵化次数在 LifeService，菜单要关页刷聊天才看得见——缺 `%corerpg_life_*%`。本号只批方案并占位：**插件岗另号补键**；**菜单挂键另号**（键就绪后）；生活经济与观察不动。

## 已批摘要（方案 M）

| 阶 | 岗 | 交付 |
|----|-----|------|
| **P1** | 插件岗（另号 · **补键中**） | 注册 DESIGN §2.1 最少集（`life_level` / `life_xp` / `life_xp_next` / `life_level_line` / `life_soul_daily_left` / `life_soul_daily_line` / `life_hatch_weekly_left` / `life_hatch_weekly_line` 等）；单测；发 jar |
| **P2** | 菜单岗（另号 · **键就绪后**） | `ember_life` W1a–W1b（I 同屏 + G/K 半行；H/J/Open 可选）；TrMenu reload 验收 |
| **本号** | docs | 勾批 · tip 关 · backlog 施工中 · 本 STATUS |

**钉：** 只读 `LifeService.lifeLevel` / `lifeXp` / `levelXp` / `periodCount`；计数约定魂尘日顶合计 4、孵化周顶合计 2（见 DESIGN §2.1）；**禁**改 `life.yml` 价/daily/weekly/`level_xp`；**禁**菜单假写固定个人 Lv；**禁**挪用 `%corerpg_ember_level%` 冒充生活。  
**禁：** 同号写 Java · 改 live 菜单 · 关观察 · 抬日表 · 开 R · 开 K3 · 改 ×0.97 · 回盘 E/F · 空跳转 · 复述 D373–D386。

## 本号范围

| 做 | 不做 |
|----|------|
| DESIGN 勾批 A·M · tip 关 · backlog→已批·插件施工中 · 本 STATUS 占位 | 改 `CorePapi` / Expansion / `LifeService` / jar |
| 显式 add docs push（D365） | 改 `ember_life.yml` live · 改 `life.yml` 经济 · 开关 / bv |

## 验收自检（本号 docs）

| ID | 结果 | 备注 |
|----|------|------|
| D1 | **PASS** | DESIGN STATUS→已批 A·批 M·D388；勾批 M；总控批注 |
| D2 | **PASS** | tip 旁注已关 · 硬规格→已批 |
| D3 | **PASS** | backlog `B-life-level-papi` → **已批·插件施工中** |
| D4 | **PASS** | 本号未碰 Java / 菜单 / life.yml / 三开关 / bv / jar |

## 改动清单（本号）

| 文件 | 改动 |
|------|------|
| `DESIGN-ember-life-level-papi-2026-10-10.md` | STATUS→已批；勾批；总控批注；变更记录 |
| tip `…-life-level-papi-need-design-…` | 旁注已关 |
| `design-ember-content-backlog.md` | `B-life-level-papi` → **已批·插件施工中** |
| 本 STATUS | 占位 · 插件补键中 · 菜单挂键另号 |
| CorePapi / jar / `ember_life.yml` / `life.yml` / ember-v1 | **未动** |
| ladder / calamity-state / p1-six / MM SavedData | **未 stage** |

## 下一号

| 号 | 岗 | 指针 |
|----|-----|------|
| **P1** | 插件岗 | DESIGN §2.1 / §2.3 P1；最少集键；单测；发 jar；**仍禁**改 life.yml 经济/曲线 |
| **P2** | 菜单岗 | DESIGN §2.2 W1a–W1b；**键在 live 可解析后再挂**；禁提前写字面量冒充；禁回盘 E/F |

## 本号 commit 自检（按 D365）

| ID | 结果 | 备注 |
|----|------|------|
| C1 | **PASS** | 工作区有脏 runtime 常态 |
| C2 | **待 commit 时核** | cached 仅授权 docs |
| C3 | **待 commit 时核** | 显式 `git add` 路径；未用 `add -A` |
| C4 | **PASS** | 未碰 live ember-v1；未切分支；toplevel=`/workspace/minecraft` |
| C5 | **待 commit 时核** | 脏 runtime 未入暂存 |

## 不动

本号写 Java · 改 live 菜单 · 改 life.yml 价/日周顶/level_xp · 关观察 · 抬日表 · 开 R · 开 K3 · 改 ×0.97 / 三开关 / bv · Pack6 / 天赋 / 灰印 · 假写固定 Lv · 回盘 E/F · 空跳转 · stage 脏 runtime · 复述 D373–D386

---

*D388 批 A·M · tip `8305129d` · docs 占位 · 插件补键中 · 菜单挂键另号 · ≠本号写 Java ≠改菜单 ≠关观察。*
