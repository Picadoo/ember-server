# 状态 · 下一档硬债选定 · 需策划（拟 D13306 · 挂机庭「顺手升阶」· docs-only · ≠抬日表 · ≠sx20 · ≠菜单诚实空转）

> **STATUS:** OPEN · 待批 A · 荐方案 M · 2026-10-13 21:39 Asia/Shanghai

> **旁注（打开 · 待批 A · 荐 M · 拟 D13306）：** tip 交总控批 · DESIGN [`DESIGN-ember-afk-courtyard-quick-upgrade-2026-10-13.md`](../design/DESIGN-ember-afk-courtyard-quick-upgrade-2026-10-13.md) · RESEARCH [`RESEARCH-ember-afk-courtyard-quick-upgrade-2026-10-13.md`](../design/RESEARCH-ember-afk-courtyard-quick-upgrade-2026-10-13.md) · backlog `B-afk-courtyard-quick-upgrade` · **TIP-CA** · **≠关观察 ≠抬日表 ≠sx20 ≠开 R/K3 ≠假开旧日常 ≠菜单诚实/observe 空转 ≠改 UpgradeRules/afk.tiers ≠复开 D423 ≠stage 脏 runtime**。

> **上游结案：** **TIP-BZ CLOSED** live MM `d39c32bb6`（D13305 短征 R2 EliteCast · tip `d688ed5af`）。总控连开：挂机庭第二件可感（荐升阶/精工其一·零抬产）；硬禁延续 sx20·L13·K3·Pack6/天赋灰印/R·抬 AFK 日表/伤乘·Affix densify·sure/pity/soft 诚实半行·菜单诚实/observe 空转。

**日期：** 2026-10-13（上海时间）  
**本窗性质：** tip **打开 · 待批 A · 荐方案 M · 拟 D13306** · **docs-only** · **批准前禁改 YAML/Java**  
**硬规格（待批 A · 荐 M · 拟 D13306）：** [`DESIGN-ember-afk-courtyard-quick-upgrade-2026-10-13.md`](../design/DESIGN-ember-afk-courtyard-quick-upgrade-2026-10-13.md) · backlog `B-afk-courtyard-quick-upgrade`  
**打开理由：** D13304 庭内强化已 live，升阶仍逼跳工坊；D13304§6#1 / D13305§6#1 后置明文；择升阶（非精工）同形扩，**零抬产**。

---

## 0. 局势一句话

庭里能强化，升阶还要去工坊——补第二件可点花料。

---

## 1. 选题

| # | 候选 | 裁决 | 理由（证据） |
|---|------|------|--------------|
| **1** | **挂机庭「顺手升阶」** | **采纳 · 需策划** | 开窗挂机庭第二件；E1–E6；见 §1.1 |
| 2 | 挂机庭顺手精工 | **后置** | 同形；本窗只钉升阶一条 |
| 3 | 今日推短征轮换 | **后置** | 非庭内多一事 |
| 4 | 再叠 AFK/菜单诚实半行 | **否** | 空转硬禁 |
| 5 | sx20 新本硬开 | **否** | 硬禁 |
| 6 | 抬日表 / 开 R·K3 / Pack6 | **否** | 硬禁 |
| 7 | 复开 D423 / exhausted 空声明 | **否** | 有真债 |

### 1.1 证据核验（实扫 · 2026-10-13）

| # | 证据 | 出处 | 含义 |
|---|------|------|------|
| E1 | E/e 顺手强化 live | `ember_p1_afk.yml` · `e214b0399` | 第一件齐；第二件空 |
| E2 | 工坊 upgrade/confirm live · U/u 护甲灰 | `ember_p1_forge.yml` | 可复用 |
| E3 | held_upgrade_cost/lack；gap_upgrade_t2=拼接串 | EmberRunPapi | lore 可挂；禁假 `<=0` |
| E4 | D13304§6#1 / D13305§6#1 后置升阶 | DESIGN | 合法接力 |
| E5 | TIP-BZ live · 连开禁 sx20/诚实空转 | `d39c32bb6` · 派单 | 边界 |

**玩家价值：** 挂机庭点两下就能升阶手持——第二件产→花不断跳。

**荐方案 M：** `ember_p1_afk` 增 U/u「顺手升阶」对齐工坊 upgrade→confirm；lore 挂 cost/lack/gap 串；护甲灰；**零改**产量/价；TrMenu-only；精工后置。

---

## 2. 派单句

```
【派单·内容真债待批A·拟D13306】挂机庭「顺手升阶」（≠抬日表 · ≠sx20 · ≠菜单诚实空转）
优先级：总控 TIP-BZ 后连开 · 挂机庭第二件可感 · docs-only
禁：sx20·抬daily_kills/afk.tiers·改UpgradeRules·开gate_daily·样本R·Pack6·天赋/灰印·关观察·开K3·L13·伤乘/skill_mult·Affix densify·sure/pity/soft诚实半行再叠·复开D423·本窗双开精工·假用gap_upgrade_t2<=0

请出 DESIGN-ember-afk-courtyard-quick-upgrade-2026-10-13.md，STATUS=待批 A · 荐 M。
交付：只 docs；批准前禁改 YAML/Java；勿 stage 脏 runtime。
```

---

## 3. 本窗不做

不关观察；不开样本 R；不开 K3 live；**不抬**挂机日表/层收益；不 Pack6/天赋/灰印；假开旧日常；**不交 sx20**；不开 L13 施工；不叠菜单诚实/sure·pity·soft 半行；不以口号当主交付；**不复开 D423**；不教裸指令；本窗不双开精工；**本 tip 零代码、且不得 stage 脏 runtime**。

---

## 4. 文件指针

| 层 | 路径 |
|----|------|
| RESEARCH | `docs/design/RESEARCH-ember-afk-courtyard-quick-upgrade-2026-10-13.md` |
| DESIGN | `docs/design/DESIGN-ember-afk-courtyard-quick-upgrade-2026-10-13.md` |
| tip（本文件） | `docs/status/STATUS-ember-next-hard-debt-afk-courtyard-quick-upgrade-need-design-2026-10-13.md` |
| 薄 tip | `docs/tips/TIP-ember-afk-courtyard-quick-upgrade-thin-d13306-2026-10-13.md` |
| backlog | `B-afk-courtyard-quick-upgrade` |
| 号段 | **拟 D13306**（TIP-BZ/D13305 后下一未用号）· 字母窗 **TIP-CA** |

---

*选题挂机庭顺手升阶 · tip 打开 · 待批 A · 荐 M · 拟 D13306 · ≠关观察 ≠抬日表 ≠sx20 ≠复开 D423。*

---

## 变更记录

| 日 | 事 |
|----|-----|
| 2026-10-13 | 策划 · tip 打开 · TIP-BZ 后连开 · 选题挂机庭顺手升阶 · DESIGN/RESEARCH 待批 A·荐 M · 拟 D13306 |
