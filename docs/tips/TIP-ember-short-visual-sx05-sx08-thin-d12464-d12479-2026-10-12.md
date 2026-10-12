# tip · TIP-U 短征轻视觉续（sx05–sx08 叠饰）· D12464–D12479

> **状态：** OPEN · 待批 A · 荐方案 M（2026-10-12 Asia/Shanghai）  
> **上游：** [`RESEARCH-ember-short-visual-sx05-sx08-thin-2026-10-12.md`](../design/RESEARCH-ember-short-visual-sx05-sx08-thin-2026-10-12.md) · [`DESIGN-ember-short-visual-sx05-sx08-thin-2026-10-12.md`](../design/DESIGN-ember-short-visual-sx05-sx08-thin-2026-10-12.md) · TIP-O sx01–sx04 · TIP-T 深化停窗  
> **前置：** TIP-T CLOSED（观察停窗 · 无码）；深化 densify **仍冻**；glance densify **仍停**  
> **施工岗：** 地图/脚本岗（`tools/p1map` 叠饰；**零改 jar 数值表**；runs 仅 `map_version` 戳）  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零 glance twin** · **零 ForgeChoice/wear/pity/awkDelta densify** · 禁重铺地板/改房序 · 禁改 S44–S47/Cast/afk · 禁 sx20

---

## 0. 一句话

短征 sx05–sx08 叠主题柱/灯/饰带，进本一眼有差，不改奖不改伤。

---

## 1. 选题裁决

| 候选 | 裁决 | 理由 |
|------|------|------|
| 烬斩手感再研究/cue | **否（本窗）** | TIP-K 已 CLOSED；再开易 densify；禁 skill_mult |
| 变强途径再 densify | **否** | TIP-T 冻 |
| **短征视觉续 sx05–sx08** | **采纳** | TIP-O HOLD 续批；真地形已有、缺叠饰；内容可感 |

---

## 2. 方案 M（定稿）

| ID | 项 | 验收 |
|----|-----|------|
| D12464 | 本 tip + RESEARCH + DESIGN | 路径可点 |
| D12465 | 审计：sx05–sx08 DESIGN 主材 vs 现网 builder（对齐 RESEARCH） | 可核 |
| D12466 | 脚本 `tools/p1map/d12464_sx05_sx08_visual_pass.py`：范式对齐 d12376/d450（采实心、占位 skip、**禁**重铺地板） | 可跑 |
| D12467 | **sx05** 叠饰：安山/圆石柱 + 铁栅栏杆；桥侧不堵 | 进本可感 |
| D12468 | **sx06** 叠饰：石砖/安山柱 + 灰·红羊毛饰带；不封环走 | 进本可感 |
| D12469 | **sx07** 叠饰：石砖柱 + 荧石/灯点缀窗廊；塔冠少量火盆 | 进本可感 |
| D12470 | **sx08** 叠饰：石砖柱 + 层间铁栅；错层阶侧饰带 | 进本可感 |
| D12471 | `ember-v1-runs.yml` 四本 `map_version` → `@d12464` | git 可核 |
| D12472 | 脚下实心复验（各本 room/Boss safe） | 无坠虚空 |
| D12473 | 冒烟：sx05+sx08 enter→三房+Boss；Director 房序不变 | 记待恢复服 |
| D12474 | 静态：S44–S47 / Cast / afk / STAMP `git show` **无抬** | PASS |
| D12475 | HOLD：sx09–sx19；粒子 densify；Affix；glance；变强 densify | 本包不开 |
| D12476–D12479 | 缓冲热修（堵路回滚）；禁借号抬表 | 超额新 tip |

### 玩家口径

- 裂谷 / 环廊 / 烬塔 / 错层庭边上有各自主题柱和灯，不像同一套灰盒子。  
- 路和奖都没变。

---

## 3. 明确不做

- 重铺地板 / 改 R2 / 改 rooms·boss 坐标  
- 改 S44–S47 / 日帽 / 体力 / Cast 数值  
- Affix · skill_mult · glance twin · ForgeChoice/wear/pity/awkDelta densify  
- sx09–sx19 同窗硬铺 · sx20 · 挂机抬产  

---

## 4. 验收清单

- [ ] 总控批 **A · 方案 M**  
- [ ] D12466–D12472 脚本+四本叠饰+map_version+脚下实心  
- [ ] 奖励 / Cast / afk `git show` 无抬  
- [ ] 不跑 p1sim（叠饰级）

---

## 5. 批注

- [ ] 总控批 **A · 方案 M** · （待）  
- 选题：短征白模叠饰续批 sx05–sx08（TIP-O 范式）  
- **批注预填：** 叠饰禁重铺；对齐 d12376；sx09+ 另 tip；仅 map_version 戳；零 jar 数值；硬禁含变强 densify 冻

## STATUS: OPEN · 待批 A · 荐 M · D12464–D12479
