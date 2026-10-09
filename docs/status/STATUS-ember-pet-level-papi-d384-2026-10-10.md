# 状态 · D384：使魔等级 PAPI 同屏（批 A·M · docs 占位 · 插件补键中 · 菜单挂键另号）

**日期：** 2026-10-10（上海时间）  
**上游：** tip [`STATUS-ember-next-hard-debt-pet-level-papi-need-design-2026-10-10.md`](STATUS-ember-next-hard-debt-pet-level-papi-need-design-2026-10-10.md) @ `a869c892` · DESIGN [`DESIGN-ember-pet-level-papi-2026-10-10.md`](../design/DESIGN-ember-pet-level-papi-2026-10-10.md)  
**裁决：** **已批 A · 批 M · D384** · 总控采纳方案 M · tip 旁注已关 · backlog→**已批·插件施工中** · **本号 docs-only 占位** · **插件补键中** · **菜单挂键另号** · **≠本号写 Java** · **≠改 live 菜单** · **≠改 feed 公式** · **≠关观察** · **≠抬日表** · **≠开 R** · **≠开 K3** · **≠改 ×0.97 / 三开关 / bv** · **≠假写固定 Lv** · **≠复述 D373–D383**  
**版本：** **docs-only**（DESIGN 勾批 · tip 关 · backlog · 本 STATUS）· jar / CorePapi / TrMenu `ember_pet` / feed.* / set_bonus / bv **未动**  
**toplevel（落字时）：** `/workspace/minecraft` @ `main`（C0 合格）

## 人话

使魔养成数字在 PlayerData，菜单看不见——缺 `%corerpg_pet_*%`。本号只批方案并占位：**插件岗另号补键**；**菜单挂键另号**（键就绪后）；养成公式与观察不动。

## 已批摘要（方案 M）

| 阶 | 岗 | 交付 |
|----|-----|------|
| **P1** | 插件岗（另号 · **补键中**） | 注册 DESIGN §2.1 最少集（`pet_level_line` / `pet_feed_hint` / `pet_level` / `pet_max_level` / `pet_feed_cost` / active 名 id 等）；单测；发 jar |
| **P2** | 菜单岗（另号 · **键就绪后**） | `ember_pet` W1a–W1c 挂 `%corerpg_pet_*%`；TrMenu reload 验收 |
| **本号** | docs | 勾批 · tip 关 · backlog 施工中 · 本 STATUS |

**钉：** 只读 `getPetLevel` / feedCost / max_level；**禁**改 `feed.*`；**禁**菜单假写固定个人 Lv；**禁**挪用 `%corerpg_ember_level%` 冒充使魔。  
**禁：** 同号写 Java · 改 live 菜单 · 关观察 · 抬日表 · 开 R · 开 K3 · 改 ×0.97 · 复述 D373–D383。

## 本号范围

| 做 | 不做 |
|----|------|
| DESIGN 勾批 A·M · tip 关 · backlog→已批·插件施工中 · 本 STATUS 占位 | 改 `CorePapi` / Expansion / `PetService` / jar |
| 显式 add docs push（D365） | 改 `ember_pet.yml` live · 改 `pet.yml` feed 数值 · 开关 / bv |

## 验收自检（本号 docs）

| ID | 结果 | 备注 |
|----|------|------|
| D1 | **PASS** | DESIGN STATUS→已批 A·批 M·D384；勾批 M；总控批注 |
| D2 | **PASS** | tip 旁注已关 · 硬规格→已批 |
| D3 | **PASS** | backlog `B-pet-level-papi` → **已批·插件施工中** |
| D4 | **PASS** | 本号未碰 Java / 菜单 / feed / 三开关 / bv / jar |

## 改动清单（本号）

| 文件 | 改动 |
|------|------|
| `DESIGN-ember-pet-level-papi-2026-10-10.md` | STATUS→已批；勾批；总控批注；变更记录 |
| tip `…-pet-level-papi-need-design-…` | 旁注已关 |
| `design-ember-content-backlog.md` | `B-pet-level-papi` → **已批·插件施工中** |
| 本 STATUS | 占位 · 插件补键中 · 菜单挂键另号 |
| CorePapi / jar / `ember_pet.yml` / `pet.yml` feed / ember-v1 | **未动** |
| ladder / calamity-state / p1-six / MM SavedData | **未 stage** |

## 下一号

| 号 | 岗 | 指针 |
|----|-----|------|
| **P1** | 插件岗 | DESIGN §2.1 / §2.5 V1；最少集键；单测；发 jar；**仍禁**改 feed.* |
| **P2** | 菜单岗 | DESIGN §2.2 W1a–W1c；**键在 live 可解析后再挂**；禁提前写字面量冒充 |

## 本号 commit 自检（按 D365）

| ID | 结果 | 备注 |
|----|------|------|
| C1 | **PASS** | 工作区有脏 runtime 常态 |
| C2 | **待 commit 时核** | cached 仅授权 docs |
| C3 | **待 commit 时核** | 显式 `git add` 路径；未用 `add -A` |
| C4 | **PASS** | 未碰 live ember-v1；未切分支；toplevel=`/workspace/minecraft` |
| C5 | **待 commit 时核** | 脏 runtime 未入暂存 |

## 不动

本号写 Java · 改 live 菜单 · 改 feed 公式 · 关观察 · 抬日表 · 开 R · 开 K3 · 改 ×0.97 / 三开关 / bv · Pack6 / 天赋 / 灰印 · 假写固定 Lv · stage 脏 runtime · 复述 D373–D383

---

*D384 批 A·M · tip `a869c892` · docs 占位 · 插件补键中 · 菜单挂键另号 · ≠本号写 Java ≠改菜单 ≠关观察。*
