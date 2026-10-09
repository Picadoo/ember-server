# 状态 · 下一档硬债选定 · 需策划（挂机层展示钩 · 多坐一会儿趣味）

> **上游结案：** 总控批 A·M @`92359b65` 内容互指/日更可读面 exhausted（D373–D381）确认后**否决静默**，点名主题 A「新挂机层展示钩 / 挂机庭多坐一会儿趣味」。Stage2 观察照续（≥**2026-10-10 17:40 CST**，**≠关窗 ≠改×0.97**）。主题 B 使魔等级 PAPI **本号不写**（可另号）。

**日期：** 2026-10-10（上海时间）  
**本窗性质：** tip **待批 A · 荐 M** · **≠关观察** · **≠抬 daily_kills / afk.tiers** · **≠开样本 R** · **≠开 K3** · **≠新假平面** · **≠空跳转**  
**硬规格（待批 A · 荐 M）：** [`DESIGN-ember-afk-layer-dwell-hook-2026-10-10.md`](../design/DESIGN-ember-afk-layer-dwell-hook-2026-10-10.md) · backlog `B-afk-layer-dwell-hook`  
**打开理由：** D285/D305/D375 后，挂机页能看见今日条与层名片、也能去工坊花材料，但**缺「今天还差什么 / 下一层更好在哪」软目标展示**——停留动机与回城动机仍薄，玩家容易看一眼就关。

---

## 0. 局势一句话

产侧与产→花已齐；先把「多坐一会儿 / 换层值不值」做進战况与层差展示，不抬产量。

---

## 1. 选题

| # | 候选 | 裁决 | 理由（证据） |
|---|------|------|--------------|
| **A** | **挂机层展示钩 · 多坐一会儿** | **采纳 · 需策划** | 总控点名主题 A；见 §1.1 |
| B | 使魔等级 PAPI 同屏 | **本号不写** | 总控标次题；缺占位则另号插件缺口 |
| — | 再交空跳转 / 复述 D373–D381 | **否决** | exhausted 已确认；禁硬凑 |

### 1.1 证据核验（实扫）

| # | 证据 | 出处 | 含义 |
|---|------|------|------|
| E1 | 战况 I：`state/here/today/session/kph/loot/cap/offline`；**无**「还差」软目标句、**无**层差一览、「下一层更好」聚合行 | `plugins/TrMenu/menus/ember_p1_afk.yml` Icons `I` | 今日数字在，**动机文案薄** |
| E2 | 四层格已有 D305 `§d名片` + `§b养` + 打满资源表 + 推荐装；升层 cue 仅站场 chat 一次（`upgrade_hint`） | 同文件 1–4 · `EmberAfkService.upgradeHintText` · D305 STATUS | 层身份齐；**菜单无「相对下层多养」聚合** |
| E3 | D375/D381：`S` 去哪花→工坊、`V` 仓库；产→花可点 | DESIGN afk-spend-loop / forge-mat-source | **不重做**跳转主交付；本债可旁文案加厚 |
| E4 | D285：满额 F→去冒险；`afk_full` | STATUS D285 · F 条件格 | 满额路径保留；软目标不得盖过 |
| E5 | 现网 PAPI（只展示已有键）：`today/session/kph/loot/cap/here/state/offline/full/t1..t4`；**无** `remain` / `eta` / `next_farm` | `EmberAfkService.papi` | 本窗**仅用现有键**；新键标「须插件另号」 |
| E6 | `ember-v1.yml` `afk.daily_kills: 2400` · tiers 币/碎片/核心/胚料表 · `feel.upgrade_hint` | 只读 | **禁改产量数字**；现网表可展示 |
| E7 | 内容 exhausted @`92359b65` · 总控否决静默并点名本主题 | tip content-true-debt-exhausted | 本债=点名新主题，**≠**复述互指波 |

**玩家价值：** 打开挂机庭一眼知道「今天还差多少才满、下一层多养什么」——愿意多坐一会儿，满了有回城/去花动机；不靠抬日表。

**荐方案 M：** 战况软目标行 + 层差一览/相对多养短签（静态对齐真表）+ 去哪花旁文案加厚；零新跳转格、零改 `daily_kills`/tiers。

---

## 2. 派单句

```
【派单·内容真债待批A】挂机层展示钩·多坐一会儿趣味（≠抬日表）
优先级：总控点名主题 A · docs-only · 对齐 ember_p1_afk / D285/D305/D375
禁：抬daily_kills/层表·样本R·Pack6·天赋/灰印·关观察·改×0.97·开K3·假平面·空跳转·主题B同号

请出 DESIGN-ember-afk-layer-dwell-hook-2026-10-10.md，STATUS=待批 A · 荐 M。
交付：只 docs；批准前禁改 YAML/Java；勿 stage 脏 runtime。
```

---

## 3. 本窗不做

不关观察；不开样本 R；不开 K3；不改 ×0.97 / set_bonus；**不抬** `daily_kills` / 离线% / 各层每日量；不 Pack6 / 天赋 / 灰印；不新假平面 AFK 世界；不新造无目的跳转格；不写主题 B 使魔 PAPI；不把 D373–D381 当本号主交付；**本 tip 零代码、且不得 stage 脏 runtime**。

---

*选题挂机层展示钩 · tip 待批 A · 荐 M · ≠关观察 ≠抬挂机表。*
