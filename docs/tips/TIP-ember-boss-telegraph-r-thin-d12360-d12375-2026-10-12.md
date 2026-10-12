# tip · TIP-N Boss 预警 R 薄热修（持续蓄力条 · 半血前提示）· D12360–D12375

> **状态：** 待总控批 **A · 方案 M**（本 tip 的施工方案名；对应 DESIGN 原「方案 R」演出子集）  
> **上游：** [`RESEARCH-ember-boss-telegraph-r-thin-2026-10-12.md`](../design/RESEARCH-ember-boss-telegraph-r-thin-2026-10-12.md) · [`DESIGN-ember-boss-telegraph-honesty-2026-10-08.md`](../design/DESIGN-ember-boss-telegraph-honesty-2026-10-08.md) §2.2 · D304 STATUS  
> **前置：** D304 M CLOSED live；TIP-M 停窗观察 CLOSED；glance densify **仍停**  
> **施工岗：** 插件岗（Director / Counterplay 演出；**零改技能数值表**）  
> **硬禁：** **零 Pack6 / 新招 · 零抬 every/warn/dmg/HP/体力/掉率 · 零 Affix densify · 零 glance twin · 零抬表** · 禁 adds 门延迟（本窗）· 禁粒子 densify twin

---

## 0. 一句话

蓄力条在整段预警里仍能看见；半血前给一口气短提示——不扩 Pack、不改伤。

---

## 1. 选题裁决

硬债池里 Boss 预警 **R 后置**仍开着；挂机/视觉主线点缀多已收。本窗开 R 的演出级薄片。

---

## 2. 方案 M（定稿 · = DESIGN 方案 R 演出子集）

| ID | 项 | 验收 |
|----|-----|------|
| D12360 | 本 tip + RESEARCH | 路径可点 |
| D12361 | DESIGN honesty 进度戳：R 薄窗开工指针 | 可核 |
| D12362 | **蓄力条持续：** `startWarn` 后按 ≤5 tick 或每 0.25s 续写 `castStartBar` 至 warn 结束或招取消；被其它 flash 打断则下一 tick 抢回（仅蓄力窗内） | 冒烟见条不单闪；文案=既有 castStartBar |
| D12363 | 热关：`ember-v1-runs.yml`（或 feel 段）`boss_feel.warn_bar_hold: true` 缺省 true；false=退回 D304 单闪 | 可热关 |
| D12364 | **半血前提示：** 首领 HP 首次 ≤55% 且尚未发半血短签 → ActionBar/subtitle 一行 `§e即将半血`（可附本图相位半词，与 `storyHalfHpCue` 同族词根）；**每首领每局一次** | 不改 50% 钩；不提前加怪 |
| D12365 | 热关：`boss_feel.pre_half_cue: true` 缺省 true | 可热关 |
| D12366 | 单测：续写在 warn 窗内；pre_half 只触发一次；关键后无续写/无前提示 | PASS |
| D12367 | 冒烟：Q01 + Q07（或 Q06）+ 一团本：见持续条；压血过 55% 见前提示再过 50% 见既有半血签 | 记待恢复服 |
| D12368 | HOLD：adds 门延迟、改 warn 秒、粒子 densify、Pack6、glance | 本包不开 |
| D12369–D12375 | 缓冲热修（刷屏/抢条优先级）；禁借号抬表 | 超额新 tip |

### 玩家口径

- 首领蓄力时，动作条上的「躲什么」会留到落地。  
- 快半血时先喊一声「即将半血」，招式变强仍以半血短签为准。

---

## 3. 明确不做

- Pack6 / 新 Boss 招 / 重铺 skills  
- 改 every / warn / dmg / HP / adds 组成  
- Affix densify / power-glance twin  
- 增援门 0.5–1s 停（另 tip + 门禁）  
- 纯 lore 菜单宣称、局内不对齐  

---

## 4. 验收清单

- [ ] 总控批 **A · 方案 M**  
- [ ] D12362–D12365 行为 + 热关  
- [ ] 数值表 / STAMP / 体力 / 掉率 `git show` 无抬  
- [ ] 不跑 p1sim（演出级）  

---

## 5. 批注

- [ ] 总控批 **A · 方案 M** · _待填_  
- 对应 DESIGN **方案 R 演出子集**；非 Pack、非 glance  
- 选题：硬债 Boss 可读性热修（D304 R 后置升主）

## STATUS: OPEN · 待批 A·M · 2026-10-12 Asia/Shanghai
