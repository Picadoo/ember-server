# tip · TIP-V 短征轻视觉续（sx09–sx12 叠饰）· D12480–D12495

> **状态：** CLOSED · PASS · live `bae53046`（2026-10-12 17:15 Asia/Shanghai）  
> **上游：** [`RESEARCH-ember-short-visual-sx09-sx12-thin-2026-10-12.md`](../design/RESEARCH-ember-short-visual-sx09-sx12-thin-2026-10-12.md) · [`DESIGN-ember-short-visual-sx09-sx12-thin-2026-10-12.md`](../design/DESIGN-ember-short-visual-sx09-sx12-thin-2026-10-12.md) · TIP-U sx05–sx08 CLOSED `a3590b41` · TIP-O sx01–sx04 · TIP-T 深化停窗  
> **前置：** TIP-U CLOSED PASS live `a3590b41`（sx05–08 `@d12464`）；深化 densify **仍冻**；glance densify **仍停**  
> **施工岗：** 地图/脚本岗（`tools/p1map` 叠饰；**零改 jar 数值表**；runs 仅 `map_version` 戳）  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零 glance twin** · **零 ForgeChoice/wear/pity/awkDelta densify** · 禁重铺地板/改房序 · 禁改 S48–S51/Cast/afk · 禁 sx20 · 禁 skill_mult

---

## 0. 一句话

短征 sx09–sx12 叠主题柱/灯/饰带，进本一眼有差，不改奖不改伤。

---

## 1. 选题裁决

| 候选 | 裁决 | 理由 |
|------|------|------|
| 烬斩手感再研究/cue | **否（本窗）** | TIP-K 已 CLOSED；再开易 densify；禁 skill_mult |
| 变强途径再 densify | **否** | TIP-T 冻 |
| 主线内容 | **否（本窗）** | 不抢薄窗；视觉续债更贴 TIP-U HOLD |
| **短征视觉续 sx09–sx12** | **采纳** | TIP-U D12475 HOLD 续批；真地形已有、缺叠饰；四本批对齐 TIP-O/U |

---

## 2. 方案 M（定稿荐）

| ID | 项 | 验收 |
|----|-----|------|
| D12480 | 本 tip + RESEARCH + DESIGN | 路径可点 |
| D12481 | 审计：sx09–sx12 DESIGN 主材 vs 现网 builder（对齐 RESEARCH） | 可核 |
| D12482 | 脚本 `tools/p1map/d12480_sx09_sx12_visual_pass.py`：范式对齐 d12464/d12376（采实心、占位 skip、**禁**重铺地板） | 可跑 |
| D12483 | **sx09** 叠饰：石砖/安山/圆石柱 + 岸缘铁栅；跳石顶不堵 | 进本可感 |
| D12484 | **sx10** 叠饰：石砖/安山柱 + 闸口铁栅 + 灰·红羊毛饰带；不封闸道 | 进本可感 |
| D12485 | **sx11** 叠饰：石砖柱对称双廊 + 荧石/灯点缀合镜门侧 | 进本可感 |
| D12486 | **sx12** 叠饰：石砖/安山柱环枢 + 侧厢铁栅饰带；转心少量火盆 | 进本可感 |
| D12487 | `ember-v1-runs.yml` 四本 `map_version` → `@d12480` | git 可核 |
| D12488 | 脚下实心复验（各本 room/Boss safe） | 无坠虚空 |
| D12489 | 冒烟：sx09+sx12 enter→三房+Boss；Director 房序不变 | 记待恢复服 |
| D12490 | 静态：S48–S51 / Cast / afk / STAMP `git show` **无抬** | PASS |
| D12491 | HOLD：sx13–sx19；粒子 densify；Affix；glance；变强 densify | 本包不开 |
| D12492–D12495 | 缓冲热修（堵路回滚）；禁借号抬表 | 超额新 tip |

### 玩家口径

- 跳石渠岸 / 递闸室 / 镜廊 / 枢厅边上有各自主题柱和灯，不像同一套灰盒子。  
- 路和奖都没变。

---

## 3. 明确不做

- 重铺地板 / 改 R2 / 改 rooms·boss 坐标  
- 改 S48–S51 / 日帽 / 体力 / Cast 数值  
- Affix · skill_mult · glance twin · ForgeChoice/wear/pity/awkDelta densify  
- sx13–sx19 同窗硬铺 · sx20 · 挂机抬产  

---

## 4. 验收清单

- [x] 总控批 **A · 方案 M**  
- [x] D12482–D12488 脚本+四本叠饰+map_version+脚下实心  
- [ ] 奖励 / Cast / afk `git show` 无抬  
- [ ] 不跑 p1sim（叠饰级）

---

## 5. 批注

- [x] 总控批 **A · 方案 M** · 2026-10-12 17:13 CST  
- 选题：短征白模叠饰续批 sx09–sx12（TIP-U 范式）  
- **批注：** 叠饰禁重铺；对齐 d12464/d12376；sx13+ 另 tip；仅 map_version `@d12480`；零 jar 数值；硬禁含变强 densify 冻

## STATUS: CLOSED · PASS · live `bae53046` · 2026-10-12 17:15 CST
