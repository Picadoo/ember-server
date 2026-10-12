# tip · TIP-W 短征轻视觉续（sx13–sx16 叠饰）· D12496–D12511

> **状态：** OPEN · 待批 A · 方案 M  
> **上游：** [`RESEARCH-ember-short-visual-sx13-sx16-thin-2026-10-12.md`](../design/RESEARCH-ember-short-visual-sx13-sx16-thin-2026-10-12.md) · [`DESIGN-ember-short-visual-sx13-sx16-thin-2026-10-12.md`](../design/DESIGN-ember-short-visual-sx13-sx16-thin-2026-10-12.md) · TIP-V sx09–sx12 CLOSED `bae53046` · TIP-U sx05–sx08 · TIP-O sx01–sx04 · TIP-T 深化停窗  
> **前置：** TIP-V CLOSED PASS live `bae53046`（sx09–12 `@d12480`）；深化 densify **仍冻**；glance densify **仍停**  
> **施工岗：** 地图/脚本岗（`tools/p1map` 叠饰；**零改 jar 数值表**；runs 仅 `map_version` 戳）  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零 glance twin** · **零 ForgeChoice/wear/pity/awkDelta densify** · 禁重铺地板/改房序 · 禁改 S52–S55/Cast/afk · 禁 sx20 · 禁 skill_mult

---

## 0. 一句话

短征 sx13–sx16 叠主题柱/灯/饰带，进本一眼有差，不改奖不改伤。

---

## 1. 选题裁决

| 候选 | 裁决 | 理由 |
|------|------|------|
| 烬斩手感再研究/cue | **否（本窗）** | TIP-K 已 CLOSED；再开易 densify；禁 skill_mult |
| 变强途径再 densify | **否** | TIP-T 冻 |
| 主线内容 | **否（本窗）** | 不抢薄窗；视觉续债更贴 TIP-V HOLD |
| **短征视觉续 sx13–sx16** | **采纳** | TIP-V D12491 HOLD 续批前四；真地形已有、缺叠饰；四本批对齐 TIP-O/U/V |

---

## 2. 方案 M（定稿荐）

| ID | 项 | 验收 |
|----|-----|------|
| D12496 | 本 tip + RESEARCH + DESIGN | 路径可点 |
| D12497 | 审计：sx13–sx16 DESIGN 主材 vs 现网 builder（对齐 RESEARCH） | 可核 |
| D12498 | 脚本 `tools/p1map/d12496_sx13_sx16_visual_pass.py`：范式对齐 d12480/d12464/d12376（采实心、占位 skip、**禁**重铺地板） | 可跑 |
| D12499 | **sx13** 叠饰：石砖/安山/圆石柱 + 梁侧铁栅；衡梁走面不堵 | 进本可感 |
| D12500 | **sx14** 叠饰：石砖/安山柱 + 折角侧铁栅 + 灰·红羊毛饰带；不封折角走道 | 进本可感 |
| D12501 | **sx15** 叠饰：石砖柱沿坡段 + 坡缘铁栅 + 天井侧荧石/灯；螺旋走面不堵 | 进本可感 |
| D12502 | **sx16** 叠饰：石砖/安山柱 + 影墙侧铁栅饰带；侧穿口不封 | 进本可感 |
| D12503 | `ember-v1-runs.yml` 四本 `map_version` → `@d12496` | git 可核 |
| D12504 | 脚下实心复验（各本 room/Boss safe） | 无坠虚空 |
| D12505 | 冒烟：sx13+sx16 enter→三房+Boss；Director 房序不变 | 记待恢复服 |
| D12506 | 静态：S52–S55 / Cast / afk / STAMP `git show` **无抬** | PASS |
| D12507 | HOLD：sx17–sx19；粒子 densify；Affix；glance；变强 densify | 本包不开 |
| D12508–D12511 | 缓冲热修（堵路回滚）；禁借号抬表 | 超额新 tip |

### 玩家口径

- 衡梁 / 折裂 / 旋坡 / 影廊边上有各自主题柱和灯，不像同一套灰盒子。  
- 路和奖都没变。

---

## 3. 明确不做

- 重铺地板 / 改 R2 / 改 rooms·boss 坐标  
- 改 S52–S55 / 日帽 / 体力 / Cast 数值  
- Affix · skill_mult · glance twin · ForgeChoice/wear/pity/awkDelta densify  
- sx17–sx19 同窗硬铺 · sx20 · 挂机抬产  

---

## 4. 验收清单

- [ ] 总控批 **A · 方案 M**  
- [ ] D12498–D12504 脚本+四本叠饰+map_version+脚下实心  
- [ ] 奖励 / Cast / afk `git show` 无抬  
- [ ] 不跑 p1sim（叠饰级）

---

## 5. 批注

- [ ] 总控批 **A · 方案 M** · （待填）  
- 选题：短征白模叠饰续批 sx13–sx16（TIP-V 范式）  
- **荐批注：** 叠饰禁重铺；对齐 d12480/d12464/d12376；sx17+ 另 tip；仅 map_version `@d12496`；零 jar 数值；硬禁含变强 densify 冻

## STATUS: OPEN · 待批 A · 方案 M · 2026-10-12 17:16 CST
