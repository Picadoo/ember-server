> **旁注（已关 · 2026-10-10）：** 已批 A · 批 M · **D388 docs 占位 · 插件补键中 · 菜单挂键另号** · STATUS [`STATUS-ember-life-level-papi-d388-2026-10-10.md`](STATUS-ember-life-level-papi-d388-2026-10-10.md) · tip `8305129d` · **≠本号写 Java ≠改菜单 ≠关观察 ≠抬日表 ≠开 R**。下文为选题当时正文，保留作史。

# 状态 · 下一档硬债选定 · 需策划（生活等级/次数同屏 · 先证插件缺口 · ≠抬日表）
> **【待批 A · 荐 M】** tip+DESIGN docs-only · **≠关观察 ≠抬日表 ≠开 R ≠开 K3 ≠空跳转 ≠回盘 E/F ≠复述 D373–D386**


> **上游结案：** 总控 D386 生活副手回盘已落 @`ce64fbdd` →「请交下一内容真债 tip（挂机/日更/有趣系统；禁复述 D373–D386；禁抬日表/开R/Pack6/天赋灰印/关观察/改×0.97/开K3/空跳转/回盘E/F）。若扫空则诚实 exhausted。」Stage2 观察照续（≥**2026-10-10 17:40 CST**，**≠关窗 ≠改×0.97**）。

**日期：** 2026-10-10（上海时间）  
**本窗性质：** tip **已关 · 已批 A · 批 M · D388** · **docs 占位 · 插件补键中 · 菜单挂键另号** · **≠本号写 Java** · **≠关观察** · **≠抬日表** · **≠开样本 R** · **≠开 K3** · **≠空跳转** · **≠回盘 E/F**  
**硬规格（已批 A · 批 M · D388）：** [`DESIGN-ember-life-level-papi-2026-10-10.md`](../design/DESIGN-ember-life-level-papi-2026-10-10.md) · backlog `B-life-level-papi` · STATUS [`STATUS-ember-life-level-papi-d388-2026-10-10.md`](STATUS-ember-life-level-papi-d388-2026-10-10.md)  
**打开理由：** 生活页配方多处写「需生活 Lv.N」、魂尘/孵化有日周顶，但盘面**零**等级/剩余次数；唯一「生活等级 / 次数」格 **I** 是 `close` + `corerpg life`（关菜单刷聊天）。数据在 `LifeService`，`CorePapi` **无** `life_*` 段——与使魔 D384 前同构缺口，但标的是**生活轨**（钓鱼→魂尘→孵化），**≠**复述使魔 PAPI 正文。

---

## 0. 局势一句话

补给·生活页看不见自己几级、今日还剩几次兑尘——点兑换常撞「需 Lv.2 / 今日已用完」，却要关菜单才知道；补只读 PAPI + 同屏，拉长钓鱼/兑尘停留。

---

## 1. 选题

| # | 候选 | 裁决 | 理由（证据） |
|---|------|------|--------------|
| **1** | **生活等级 / 日周次数同屏（先证 PAPI 缺口）** | **采纳 · 需策划** | 见 §1.1；有趣系统·养成可见；非空跳转、非又回盘 |
| 2 | 图录 `ember_bestiary` 使魔键 P 回 Layout | **否** | Icons 有 P、Layout 无 —— **又回盘同类**（对齐 D376/D386 模式）；派单优先非又回盘 |
| 3 | 生活 E/F 重铸/稳固回盘 | **否** | 硬禁回盘 E/F；P1 拒旧轨 → 兑得到用不上 |
| 4 | 再交 A→B 空跳转 / 互指半行 | **否** | D373–D381 互指已齐；派单禁 |
| 5 | 挂机层展示钩 / 使魔等级同屏 / 副手回盘 | **否** | D383 / D384–D385 / D386 **刚落**；禁复述 |
| 6 | 日更 Cast / 可躲半行 / 残誓 | **否** | D374/D379/D380 已齐；入口仍 LegacyGate 不可达 |
| 7 | 新短本房 / 新挂机层产量 | **否本窗** | 须经济可达或抬表/新图；荐总控另点名 |
| 8 | 抬日表 / 开 R / Pack6 / 天赋灰印 / 关观察 / ×0.97 / 开 K3 | **否** | 硬禁 |
| 9 | 本轮 exhausted | **不采纳** | 有生活同屏硬证据，非扫空 |

### 1.1 证据核验（实扫 · 2026-10-10）

| # | 证据 | 出处 | 含义 |
|---|------|------|------|
| E1 | `ember_life` Icons **I**：「生活等级 / 次数」→ `close` + `command: corerpg life` | `plugins/TrMenu/menus/ember_life.yml` | **关菜单才看得到**等级/次数 |
| E2 | Open tell 仅魂尘/副手半行；**零** `%corerpg_…life…%` | 同文件 | 盘面无同屏进度 |
| E3 | 配方 lore：魂尘/孵化/熬药写「需生活 Lv.2/4」；G/K「每日 2 次」；O/W「周购」——**无**个人剩余 | 同文件 · `life.yml` | 宣称门槛与顶，看不见自己到没到 |
| E4 | `LifeService.lifeLevel` / `lifeXp` / `level_xp`；`show()` 聊天输出 `Lv.N（经验 x/next）` + 各 offer 日周计数 | `LifeService.java` · `life.yml` | **数据层有**；仅 chat |
| E5 | `CorePapi` 段：ACCOUNT/CASH/KIT/PROGRESS/GATE/P1/STAMINA/**PET**；键集**无** `life_*` | `CorePapi.java` · D384 只加了 pet | **无键** → 须插件补键（类比 D384，标的不同） |
| E6 | 魂尘 `life_level: 2` · `daily: 2`；孵化 `life_level: 4` · `weekly: 1` | `life.yml` | 钓鱼闭环卡在「看不见门槛」 |
| E7 | `git log` D373–D386 | 使魔/日更/挂机互指·展示·副手回盘 **均已落** | 本债**非**复述那些主交付 |
| E8 | 图录 P 未入 Layout | `ember_bestiary.yml` | 真缺口但属**又回盘**；本窗排除 |
| E9 | Stage2 / 硬禁 | 观察续；禁抬日表/样本R/Pack6/天赋灰印/关观察/改×0.97/开K3/空跳转/回盘E/F | 本窗只 docs 规格 |

**玩家价值：** 打开生活页一眼看见「生活 Lv.N · 经验 · 今日兑尘剩次 / 本周孵化剩次」——钓鱼与兑尘有目标感，少关菜单刷聊天；对齐「挂机获取资源 / 有趣系统」旁轨厚度。

**荐方案 M：** 插件补 `%corerpg_life_*%` 只读键规格 + `ember_life` I 格（及 G/K/H/J 可选半行）挂真 Placeholder；施工拆号；**零改** `life.yml` 价/日周顶/等级曲线；**禁**菜单假写固定 Lv。

---

## 2. 派单句

```
【派单·内容真债待批A】生活等级/次数同屏（先证 life_* PAPI 缺口 · ≠抬日表）
优先级：总控 D386 后内容真债 · docs-only · 对齐 ember_life / LifeService / CorePapi / life.yml
禁：抬daily_kills/层表·样本R·Pack6·天赋/灰印·关观察·改×0.97·开K3·空跳转·回盘E/F·复述D373–D386·假写固定Lv

请出 DESIGN-ember-life-level-papi-2026-10-10.md，STATUS=待批 A · 荐 M。
交付：只 docs；批准前禁改 YAML/Java；勿 stage 脏 runtime。
批 A ≠ 施工 ≠ 关观察 ≠ 抬日表。
```

---

## 3. 本窗不做

不关观察；不开样本 R；不开 K3；不改 ×0.97 / set_bonus；**不抬** `daily_kills` / afk.tiers；不 Pack6 / 天赋 / 灰印；不空跳转；不回盘 E/F/V；不复述 D373–D386 主交付；不改 `life.yml` 价与日周顶与 `level_xp`；不菜单假写固定等级；**本 tip 零代码、且不得 stage 脏 runtime**。

---

*选题生活等级/次数同屏 · tip 已关 · 已批 A·M · D388 docs 占位 · 插件补键中 · 菜单挂键另号 · ≠本号写 Java ≠关观察 ≠抬日表。*

---

## 变更记录

| 日 | 事 |
|----|-----|
| 2026-10-10 | 策划 · tip 打开 · 生活同屏 · 先证无 PAPI · DESIGN 待批 A·荐 M |
| 2026-10-10 | 总控 · tip **已关** · 批 A·M · **D388** docs 占位 · 插件补键中 · 菜单挂键另号 · STATUS `STATUS-ember-life-level-papi-d388-2026-10-10.md` |
