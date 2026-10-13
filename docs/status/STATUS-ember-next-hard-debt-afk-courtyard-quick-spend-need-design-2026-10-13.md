# 状态 · 下一档硬债选定 · 需策划（拟 D13304 · 挂机庭「顺手花料」· docs-only · ≠抬日表 · ≠sx20 · ≠菜单诚实空转）

> **旁注（打开 · 待批 A · 2026-10-13）：** tip 交总控批 · DESIGN [`DESIGN-ember-afk-courtyard-quick-spend-2026-10-13.md`](../design/DESIGN-ember-afk-courtyard-quick-spend-2026-10-13.md) · RESEARCH [`RESEARCH-ember-afk-courtyard-quick-spend-2026-10-13.md`](../design/RESEARCH-ember-afk-courtyard-quick-spend-2026-10-13.md) · backlog `B-afk-courtyard-quick-spend` · **TIP-BY** · **≠关观察 ≠抬日表 ≠sx20 ≠开 R/K3 ≠假开旧日常 ≠菜单诚实/observe 空转 ≠复开 D423 ≠stage 脏 runtime**。

> **上游结案：** 总控开窗 · **TIP-BX CLOSED** live `1.65.804-d13303` · tip `be7a461ec`（purgeLegacy Fight-only + YieldBoost 菜单诚实收口）。下一选题必须「可感内容/挂时长」——挂机庭资源环、日短本乐趣、或新乐趣系统；**禁止**再开菜单诚实/observe 空转。硬禁：sx20 · L13 施工 · K3 · Pack6/天赋灰印/R · 抬 AFK 日表/伤乘/skill_mult · Affix densify · 再叠 sure/pity/soft 诚实半行族。handoff `7fc55366`：**本单不勾/复开 D423**。

**日期：** 2026-10-13（上海时间）  
**本窗性质：** tip **打开 · 待批 A · 荐方案 M · 拟 D13304** · **docs-only** · **批准前禁改 YAML/Java**  
**硬规格（待批 A · 荐 M · 拟 D13304）：** [`DESIGN-ember-afk-courtyard-quick-spend-2026-10-13.md`](../design/DESIGN-ember-afk-courtyard-quick-spend-2026-10-13.md) · backlog `B-afk-courtyard-quick-spend`  
**打开理由：** 挂机庭「去哪花 / 仓差 / remain / next_farm / 在养手持」展示链已齐，但**庭内仍不能顺手把料花掉**——产→花断在跳工坊；开窗点名「多一事可做」真缺口。sx20 禁；短征 R2 Cast 后置；诚实半行族禁。

---

## 0. 局势一句话

料看得见、去向看得见，挂机庭里还是没事可点——补「仓够→顺手强化+1」真动作。

---

## 1. 选题

| # | 候选 | 裁决 | 理由（证据） |
|---|------|------|--------------|
| **1** | **挂机庭「顺手花料」· 庭内强化+1** | **采纳 · 需策划** | 开窗优先挂机多一事；E1–E5；命令/缺口键已 live；见 §1.1 |
| 2 | 再叠 remain/仓差/next_farm/层名片半行 | **否** | 展示已厚；开窗禁空转 |
| 3 | 菜单诚实 / sure·pity·soft 半行再叠 | **否** | TIP-BX 后硬禁 |
| 4 | sx20 新本硬开 | **否** | 硬禁；短征链暂停于 sx19 |
| 5 | 短征 R2 精英 Cast 扩线 | **后置** | 真玩法花样；本窗优先挂机 |
| 6 | 今日推短征轮换 | **后置** | 选本导航；不解决庭内多一事 |
| 7 | 抬日表 / 新挂机店 / 假开旧日常 / 开 R·K3 / Pack6 / 天赋灰印 | **否** | 硬禁 |
| 8 | 复开 D423 / 观察 exhausted 空声明 | **否** | 派单禁；有真债 |

### 1.1 证据核验（实扫 · 2026-10-13）

| # | 证据 | 出处 | 含义 |
|---|------|------|------|
| E1 | 挂机「去哪花」→工坊；I/Open 挂 vault·recipe_gap·remain·next_farm·held hope | `ember_p1_afk.yml` | 可见齐；**无庭内花费格** |
| E2 | 工坊 `corerpg p1 enhance` / `confirm` | `ember_p1_forge.yml` | 真花费可复用 |
| E3 | `recipe_gap_enhance1` 返回数字缺口（0=够） | `EmberRunPapi` | TrMenu 条件可用 |
| E4 | D375–D420 / D612 展示·互指链 | backlog / DESIGN | 再叠半行=空转；本债=动作 |
| E5 | TIP-BX CLOSED · 开窗硬禁表 | tip `be7a461ec` · 派单 | 选题边界 |
| E6 | 短征 sx01–19 Cast 在 Boss；R2 Elite 仅 SoftHit | MM EmberSx* | 短本花样后置合法 |
| E7 | handoff 勿复开 D423 | `HANDOFF-ember-grokbot-2026-10-10.md` · 派单 | 本单不动旧号 |

**玩家价值：** 挂机坐满一段，仓够时在庭里点一下强化手持——多一事、产花闭环；不抬产量。

**荐方案 M：** 挂机庭新格「顺手强化+1」· `recipe_gap_enhance1<=0` 亮态走工坊同款 enhance→confirm · 不够灰态进工坊 · Open/去哪花半行 · **零改** `daily_kills`/`afk.tiers`/UpgradeRules。

---

## 2. 派单句

```
【派单·内容真债待批A·拟D13304】挂机庭「顺手花料」（≠抬日表 · ≠sx20 · ≠菜单诚实空转）
优先级：总控 TIP-BX 后开窗 · 可感内容/挂时长 · docs-only
禁：抬daily_kills/afk.tiers·开gate_daily·样本R·Pack6·天赋/灰印·关观察·开K3·sx20·L13·伤乘/skill_mult·Affix densify·sure/pity/soft诚实半行再叠·复开D423·复述D375–D420展示债为主交付

请出 DESIGN-ember-afk-courtyard-quick-spend-2026-10-13.md，STATUS=待批 A · 荐 M。
交付：只 docs；批准前禁改 YAML/Java；勿 stage 脏 runtime。
```

---

## 3. 本窗不做

不关观察；不开样本 R；不开 K3 live；不改 ×0.97；**不抬**挂机日表/层收益；不 Pack6/天赋/灰印；不假开旧日常；**不交 sx20**；不开 L13 施工；不叠菜单诚实/sure·pity·soft 半行；不以展示复述当主交付；**不复开 D423**；不教裸指令；**本 tip 零代码、且不得 stage 脏 runtime**。

---

## 4. 文件指针

| 层 | 路径 |
|----|------|
| RESEARCH | `docs/design/RESEARCH-ember-afk-courtyard-quick-spend-2026-10-13.md` |
| DESIGN | `docs/design/DESIGN-ember-afk-courtyard-quick-spend-2026-10-13.md` |
| tip（本文件） | `docs/status/STATUS-ember-next-hard-debt-afk-courtyard-quick-spend-need-design-2026-10-13.md` |
| backlog | `B-afk-courtyard-quick-spend` |
| 号段 | **拟 D13304**（TIP-BX 收于 D13303 后下一未用号）· 字母窗 **TIP-BY** |

---

*选题挂机庭顺手花料 · tip 打开 · 待批 A · 荐 M · 拟 D13304 · ≠关观察 ≠抬日表 ≠sx20 ≠复开 D423。*

---

## 变更记录

| 日 | 事 |
|----|-----|
| 2026-10-13 | 策划 · tip 打开 · TIP-BX 后开窗 · 选题挂机庭顺手花料 · DESIGN/RESEARCH 待批 A·荐 M · 拟 D13304 |
