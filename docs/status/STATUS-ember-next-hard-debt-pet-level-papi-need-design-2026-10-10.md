> **旁注（已关 · 2026-10-10）：** 已批 A · 批 M · **D384 docs 占位 · 插件补键中 · 菜单挂键另号** · STATUS [`STATUS-ember-pet-level-papi-d384-2026-10-10.md`](STATUS-ember-pet-level-papi-d384-2026-10-10.md) · tip `a869c892` · **≠本号写 Java ≠改菜单 ≠关观察 ≠抬日表 ≠开 R**。下文为选题当时正文，保留作史。

# 状态 · 下一档硬债选定 · 需策划（使魔等级 PAPI 同屏 · 主题 B）

> **上游结案：** 总控 D383 挂机层钩已落 @`137058ee` 后点名主题 B「使魔等级 PAPI 同屏（先证插件缺口）」。Stage2 观察照续（≥**2026-10-10 17:40 CST**，**≠关窗 ≠改×0.97**）。**≠复述 D373–D383 主交付。**

**日期：** 2026-10-10（上海时间）  
**本窗性质：** tip **已关 · 已批 A · 批 M · D384** · **docs 占位 · 插件补键中 · 菜单挂键另号** · **≠本号写 Java** · **≠关观察** · **≠抬日表** · **≠开样本 R** · **≠开 K3** · **≠空跳转**  
**硬规格（已批 A · 批 M · D384）：** [`DESIGN-ember-pet-level-papi-2026-10-10.md`](../design/DESIGN-ember-pet-level-papi-2026-10-10.md) · backlog `B-pet-level-papi` · STATUS [`STATUS-ember-pet-level-papi-d384-2026-10-10.md`](STATUS-ember-pet-level-papi-d384-2026-10-10.md)  
**打开理由：** D373/D376/D378 使魔闭环与互指已齐，但使魔页**仍看不见等级/下一级魂尘**——养成反馈只靠 `pet list`/`feed` 聊天，菜单同屏缺真源。

---

## 0. 局势一句话

数据层有等级；PAPI **无** `corerpg_pet_*` → 先证缺口、荐「插件补键 → 菜单挂键」拆号；禁菜单假写固定字。

---

## 1. 选题

| # | 候选 | 裁决 | 理由（证据） |
|---|------|------|--------------|
| **B** | **使魔等级 PAPI 同屏** | **采纳 · 需策划** | 总控点名主题 B；见 §1.1 |
| — | 再交空跳转 / 复述 D373–D383 | **否决** | 硬禁；互指波已齐 |
| — | 仅 TrMenu 写「Lv.?」假字 | **否决** | 无真源 = 假平面 |

### 1.1 证据核验（实扫 · 2026-10-10）

| # | 证据 | 出处 | 含义 |
|---|------|------|------|
| E1 | `PlayerData`：`petsUnlocked` / `activePet` / `petLevels`；`getPetLevel`/`setPetLevel`（默认 1） | `PlayerData.java` ~453–509 | **有**等级字段 |
| E2 | `PetService`：`feed.max_level=10`；`feedCostFor(L)=base+(L-1)*per`；`cmdList` 打 `Lv.N`；`cmdFeed` 升一级 | `PetService.java` · `pet.yml` | **有**养成逻辑；聊天可见 |
| E3 | `CorePapi.route` 段：ACCOUNT / CASH / KIT / PROGRESS / GATE / P1 / STAMINA；**无 PET**；键集**无**任何 `pet_*` | `CorePapi.java` | **无**使魔占位路由 |
| E4 | `CoreRpgExpansion` / `CorePapiAccount` / `CorePapiProgress`：**无** pet 分支；`getPetService()` 存在但未接 PAPI | `CoreRpgExpansion.java` · Plugin getter | **插件缺口**确认 |
| E5 | `ember_pet.yml`：F 兑尘 / L 图鉴(`pet list`) / E 出战 / S 投喂；lore **无** `%corerpg_…%` 等级行 | `plugins/TrMenu/menus/ember_pet.yml` | 菜单**缺同屏** |
| E6 | 既有 tip/DESIGN 多次旁注「无 pet level PAPI」后置 | D378/D380/D381/D383 等 | 本号**升主**；非复述互指 |

**PAPI 结论：无**（缺失 `%corerpg_pet_*%` 全系）。数据层**有**等级；展示层**无**键。

**玩家价值：** 打开使魔页一眼见当前等级 / 是否满级 / 下一级魂尘——投喂有反馈，养成可感。

**荐方案 M：** 插件另号补 Minimal Placeholder 规格 + 菜单同屏草案（施工拆号：插件→菜单）；**否决 A**=仅菜单假写；**否决 L**=改 feed 公式 / 抬日表 / 挂机加尘。

---

## 2. 派单句

```
【派单·内容真债待批A】使魔等级PAPI同屏（主题B·先证缺口）
优先级：总控点名主题 B · docs-only · 对齐 PetService/PlayerData / ember_pet
禁：抬日表·样本R·Pack6·天赋/灰印·关观察·改×0.97·开K3·空跳转·菜单假写固定Lv·同号写Java·改feed公式·复述D373–D383

请出 DESIGN-ember-pet-level-papi-2026-10-10.md，STATUS=待批 A · 荐 M。
交付：只 docs；批准前禁改 YAML/Java；勿 stage 脏 runtime。
批 A ≠ 施工 ≠ 关观察 ≠ 同号装 jar。
```

---

## 3. 本窗不做

不关观察；不开样本 R；不开 K3；不改 ×0.97 / set_bonus；不抬 `daily_kills` / afk.tiers；不 Pack6 / 天赋 / 灰印；不空跳转；不复述 D373–D383 互指/日更/挂机层钩主交付；不改 `feed.*` 数值公式；**本 tip 零代码、且不得 stage 脏 runtime**；**批 A ≠ 同号写 Java**（插件规格可写，施工另号）。

---

*选题使魔等级 PAPI 同屏 · tip 已关 · 已批 A·M · D384 docs 占位 · 插件补键中 · 菜单挂键另号 · ≠本号写 Java ≠关观察 ≠抬日表。*

---

## 变更记录

| 日 | 事 |
|----|-----|
| 2026-10-10 | 策划 · tip 打开 · 主题 B · 先证无 PAPI · DESIGN 待批 A·荐 M |
| 2026-10-10 | 总控 · tip **已关** · 批 A·M · **D384** docs 占位 · 插件补键中 · 菜单挂键另号 · STATUS `STATUS-ember-pet-level-papi-d384-2026-10-10.md` |
