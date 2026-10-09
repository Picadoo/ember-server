# 状态 · 下一档硬债选定 · 需策划（拟 D402 · 使魔选定盘面化 · **已关 · 已批 A·M1 · 已落地** · ≠关观察 · ≠抬日表 · ≠开 R · ≠sx06）

> **旁注（已关 · 已落地）· 2026-10-10：** 总控 **已批 A · 方案 M1 · D402 · 已落地** · tip `e12c548c` · 落地 STATUS [`STATUS-ember-pet-select-menu-d402-2026-10-10.md`](STATUS-ember-pet-select-menu-d402-2026-10-10.md) · 薄页 `ember_pet_select` + `ember_pet` L · **≠关观察 ≠抬日表 ≠sx06 ≠开 R ≠开 K3 ≠改 Java/afk/Stage2**。下文为交稿原文，保留备查。
>
> **【D402 · 已关 · 已批 A · 批 M1 · 已落地】** tip+DESIGN **关闭** · **零 jar** · **≠关观察** · **≠抬挂机日表** · **≠开样本 R** · **≠开 K3** · **≠假开旧日常** · **≠交 sx06** · **≠教玩家手打 `/corerpg pet`**

> **上游结案：** 总控批 A·M1 · 同号施工 TrMenu 已落地 · tip 关。

**日期：** 2026-10-10（上海时间）  
**本窗性质：** tip **已关 · 已批 A·M1 · 已落地**  
**硬规格：** [`DESIGN-ember-pet-select-menu-2026-10-10.md`](../design/DESIGN-ember-pet-select-menu-2026-10-10.md) · backlog `B-pet-select-menu`（**已关 · 已落地**）  
**打开理由（历史）：** 使魔页「图鉴」曾 `corerpg pet list` **聊天灌指令**；换宠须手打 `summon <id>`——违「玩家不打指令」UX。

---

## 0. 局势一句话

使魔养成闭环已可兑尘/投喂/出战，但**换哪只好看聊天、靠斜杠**——选定面仍停在 admin 味 list，趣味断在第二只使魔。

---

## 1. 选题

| # | 候选 | 裁决 | 理由（证据） |
|---|------|------|--------------|
| A | 复述 D373–D401（周目标/五本短征/配方速览/枢纽旁注/等级同屏/互指）当主交付 | **否** | 禁复述；刚落/施工中 |
| B | sx06 第六本短征 | **否** | **等 D400 PASS**；本催硬闸 |
| C | 挂机体验再加厚（D383/D398 remain·还差变体） | **否本窗主交付** | 禁复述 D383/D398；边际薄于本 UX 硬证 |
| D | 日更假开旧七线 / 空跳转 / 空转菜单复扫 | **否** | 硬禁；展示波已 exhausted |
| E | 假开 `gate_daily` / 抬日表 / 开 R / Pack6 / 天赋灰印 / 关观察 / ×0.97 / 开 K3 | **否** | 硬禁 |
| **F** | **有趣系统 · 使魔选定盘面化（真缺口）** | **采纳 · 需策划 · 拟 D402** | E1–E8；见 DESIGN |

### 1.1 扫证（本机 · 2026-10-10）

| # | 证据 | 出处 | 含义 |
|---|------|------|------|
| E1 | `ember_pet` 格 L「使魔图鉴」actions = `command: corerpg pet list` | `plugins/TrMenu/menus/ember_pet.yml` | 点开会**刷聊天**，不开选宠盘 |
| E2 | `PetService.cmdList` 末行教 `/corerpg pet summon [id] · dismiss · unlock · feed` | `PetService.java` ~L318–345 | **教玩家手打指令**；违 UX |
| E3 | 出战格 E 仅 `summon` / `dismiss` **无 id** | 同菜单 | 只能动**当前选定**；换宠无盘面入口 |
| E4 | `summon <id>` 已存在且会 `setActivePet` + 出战 | `PetService.cmdSummon` | **零新经济 API**即可盘面化 |
| E5 | 现网仅两只：`pet_ember_ashling` / `pet_ember_cinder`（灰灵/烬火） | `plugins/CoreRpg/pet.yml` | 薄页两格即可，非大图鉴工程 |
| E6 | 已有 `%corerpg_pet_active_name%` / `unlocked_count` 等；**无**按 id 解锁态键 | `CorePapiPet` | 未解锁格用诚实灰态/插件拒门即可；**禁**假写「已拥有」 |
| E7 | D373/D378/D385 已齐出战·兑尘·等级同屏 · **未**改 list→选定 | DESIGN/STATUS | 本债=旁轨真缺口，≠复述同屏 |
| E8 | 总控催 D402：有趣/挂机/日更择优；**sx06 等 D400 PASS**；禁抬日表/假开旧日常/开R·K3/关观察 | 总控消息 | 选题闸对齐 |

**荐方案 M：** docs-only **使魔选定盘面化**——把 L「图鉴」从聊天 list 改为 **可点选定盘**（薄子页或本页两格）：点灰灵/烬火 → `corerpg pet summon <id>`（现网已 setActive+出战）· 未解锁诚实拒门/灰态 · Open/状态格互指「点选定换宠」· **零改** feed/价表/战力公式 · **零抬**挂机日表 · **不交** sx06。**批 A ≠ 施工 ≠ 关观察 ≠ 抬日表 ≠ 开 R ≠ 交 sx06。**

**为何不选挂机/短征本窗：** D401 周钩施工中、五本/配方/枢纽旁注禁复述；sx06 闸关；挂机 remain/还差为 D383/D398 后置变体；本债是「有趣系统」下 **UX 硬证压过** 的使魔旁轨真缺口。

---

## 2. 派单句

```
【派单·内容真债待批A·拟D402】使魔选定盘面化（有趣系统 · list→可点换宠 · ≠抬日表 · ≠sx06）
优先级：总控催 D402 · 有趣系统择优 · docs-only
禁：抬daily_kills/afk.tiers·开gate_daily旧七线·样本R·Pack6·天赋/灰印·关观察·改×0.97·开K3·交sx06·复述D373–D401·空跳转当主交付·教玩家手打/corerpg pet

请出 DESIGN-ember-pet-select-menu-2026-10-10.md，STATUS=待批 A · 荐 M。
交付：只 docs；批准前禁改 YAML/Java；勿 stage 脏 runtime。
```

---

## 3. 本窗不做

不关观察；不开样本 R；不开 K3 live；不改 ×0.97 / set_bonus；**不抬** `daily_kills` / 离线% / 各层每日量；不 Pack6 / 天赋 / 灰印；不假开 `gate_daily` 旧七线；**不交 sx06**；不以空跳转/纯 PAPI 同屏复述/周目标复述当主交付；不复述 D373–D401（含五本短征/D398/D399/D401 周钩）为主债；不改 feed 公式/life 价；**本 tip 零代码、且不得 stage 脏 runtime**。

---

*选题使魔选定盘面化 · tip 旁注已关 · 已批 A·方案 M1 · D402 · 设计待批关闭 · 施工另派 TrMenu · ≠关观察 ≠抬挂机表 ≠sx06。*

---

## 变更记录

| 日 | 事 |
|----|-----|
| 2026-10-10 | 策划 · tip 打开 · 选题使魔选定盘面化 · DESIGN 待批 A·荐 M（拟 D402）· 上游总控催 D402 · D401 施工中 · ≠sx06 |
| 2026-10-10 | **旁注已关** · 总控批 A·M1 · D402 · 设计待批关闭 · **施工另派 TrMenu · 交菜单岗** · 钉死薄页 `ember_pet_select`（非 M2）· ≠sx06 |
| 2026-10-10 | 总控批 A·M1 · M1 落地 · tip 关 · backlog 已关 · tip `e12c548c` |
