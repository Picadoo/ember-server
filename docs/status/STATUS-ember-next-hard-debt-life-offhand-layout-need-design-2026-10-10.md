# 状态 · 下一档硬债选定 · 需策划（生活页副手守腕/生坠回 Layout · 灰粮可购成真）

> **上游结案：** 总控 D385 使魔等级同屏已挂 @`1e9a57b4`（jar d384）→「请交下一内容真债 tip（挂机/日更/有趣系统；禁复述 D373–D385；禁抬日表/开R/Pack6/天赋灰印/关观察/改×0.97/开K3/空跳转）。若扫空则诚实 exhausted。」Stage2 观察照续（≥**2026-10-10 17:40 CST**，**≠关窗 ≠改×0.97**）。

**日期：** 2026-10-10（上海时间）  
**本窗性质：** tip **待批 A** · **docs-only** · **≠关观察** · **≠抬日表** · **≠开样本 R** · **≠开 K3** · **≠空跳转** · **≠复述 D373–D385** · **≠回盘 E/F（重铸/稳固·P1 用不上）**  
**硬规格（待批 A · 荐 M）：** [`DESIGN-ember-life-offhand-layout-2026-10-10.md`](../design/DESIGN-ember-life-offhand-layout-2026-10-10.md) · backlog `B-life-offhand-layout`  
**打开理由：** `ember_set` 副手格写「守腕/生坠 · **灰粮可购**」，生活页 Icons **O/W 已定义**且接真 `life buy`，但 **Layout 仍收起**（D99→D376 明示另债）——玩家面「说得到买不到」= 假平面。副手在 P1 下**可佩戴生效**（StatService 微量守势，永不进 B/H），回盘不会造「兑得到用不上」。

---

## 0. 局势一句话

套装页说灰粮能买守腕/生坠，生活盘上却没有键——把 O/W 回 Layout，轻量周购旁轨才诚实可点。

---

## 1. 选题

| # | 候选 | 裁决 | 理由（证据） |
|---|------|------|--------------|
| **1** | **生活页副手 O/W（守腕/生坠）回 Layout** | **采纳 · 需策划** | 配置假平面 + P1 可用；见 §1.1 |
| 2 | 生活 E/F 重铸石·稳固符回盘 | **否** | `legacy_gate` 拒 `reforge`/`enhance`；回盘易「兑得到用不上」；D377 已排除；NI 遗物/宝珠假兑另号诚实订正 |
| 3 | 再交 A→B 空跳转 / 互指半行 | **否** | 派单禁；D373–D381 互指已齐 |
| 4 | 挂机层展示钩 / 使魔 PAPI 同屏 | **否** | D383 / D384–D385 **刚落**；禁复述 |
| 5 | 日更 Cast / 可躲半行 / 残誓 | **否** | D374/D379/D380 已齐；入口仍 LegacyGate 不可达 |
| 6 | 新短本房 / 新挂机层产量内容 | **否本窗** | 须经济可达建模或抬表/新图；荐总控另点名，非薄 Layout |
| 7 | 抬日表 / 开 R / Pack6 / 天赋灰印 / 关观察 / ×0.97 / 开 K3 | **否** | 硬禁 |
| 8 | 本轮 exhausted | **不采纳** | 有 O/W 假平面硬证据，非扫空 |

### 1.1 证据核验（实扫 · 2026-10-10）

| # | 证据 | 出处 | 含义 |
|---|------|------|------|
| E1 | `ember_life` Layout：`# A B C #` / `# G D K #` / `# H   J #` / `###I#P#Z#` — **无 O/W** | `plugins/TrMenu/menus/ember_life.yml` | 副手购键不在盘 |
| E2 | Icons **O** 守腕 / **W** 生坠：actions=`corerpg life buy offhand_ward` / `offhand_vita`；D289 诚实 lore 已写 | 同文件 | **键在文件、不在盘** |
| E3 | 注释：「E/F/O/W/V 仍 stowed」；D376 DESIGN 明示 O·W **另债** | 同文件 · `DESIGN-ember-life-soul-dust-layout` §3.3 | **官方后置债升主**；≠复述 D376 魂尘主交付 |
| E4 | `ember_set` 副手格 lore：「试点：守腕/生坠 · **灰粮可购**」；actions 仅 tell，**无** `menu: ember_life` | `plugins/TrMenu/menus/ember_set.yml` O | **宣称可购、灰粮无键** = 假平面 |
| E5 | `life.yml`：`offhand_ward`/`offhand_vita` 各 80 币 · **weekly: 1** · 给 NI 饰品 | `plugins/CoreRpg/life.yml` | 真价表已在；本窗不改数值 |
| E6 | `legacy_gate.allow.life: '*'`；拒表含 `reforge`/`enhance`/`part` — **life 购副手放行** | `ember-v1.yml` | P1 下购得到且可戴（StatService）；≠ E/F |
| E7 | D289/D309：副手永不进 P1 B/H；微量守势 · 展示选择感 | DESIGN offhand-budget / merge | 回盘对齐已批口径，零战斗乘区 |
| E8 | `git log` D373–D385 | 使魔/日更/挂机互指·展示·PAPI **均已落** | 本债**非**复述那些主交付 |
| E9 | Stage2 / 硬禁 | 观察续；禁抬日表/样本R/Pack6/天赋灰印/关观察/改×0.97/开K3/空跳转 | 本窗只 docs 规格 |

**玩家价值：** 每周可点买一次副手饰品（守腕/生坠），灰粮页多一档轻消费与装扮选择；套装说明与盘面诚实，拉长生活页停留。

**荐方案 M：** Layout 回盘 **O+W**；订正 D99/D376 stowed 注释；`ember_set` O 可点进生活（或半行指补给）；**零改** `life.yml` 价/周顶；**不**回盘 E/F/V；**不**开旧 enhance/reforge。

---

## 2. 派单句

```
【派单·内容真债待批A】生活页副手守腕/生坠回 Layout（灰粮可购成真 · ≠抬日表）
优先级：总控 D385 后内容真债 · docs-only · 对齐 ember_life / ember_set / life.yml / D289
禁：抬daily_kills/层表·样本R·Pack6·天赋/灰印·关观察·改×0.97·开K3·空跳转·回盘E/F·复述D373–D385

请出 DESIGN-ember-life-offhand-layout-2026-10-10.md，STATUS=待批 A · 荐 M。
交付：只 docs；批准前禁改 YAML/Java；勿 stage 脏 runtime。
批 A ≠ 施工 ≠ 关观察 ≠ 抬日表。
```

---

## 3. 本窗不做

不关观察；不开样本 R；不开 K3；不改 ×0.97 / set_bonus；**不抬** `daily_kills` / afk.tiers；不 Pack6 / 天赋 / 灰印；不空跳转；不复述 D373–D385 互指/日更 Cast/挂机层钩/使魔 PAPI 主交付；不回盘 E/F（重铸/稳固）/ V（旧票）；不放开 legacy `reforge`/`enhance`；不改 `life.yml` 价与周顶；**本 tip 零代码、且不得 stage 脏 runtime**。

---

*选题生活页副手 O/W 回 Layout · tip 待批 A · 荐 M · ≠关观察 ≠抬日表 ≠复述 D373–D385 ≠回盘 E/F。*
