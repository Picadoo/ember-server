# tip · TIP-X 短征轻视觉收尾（sx17–sx19 叠饰）· D12512–D12527

> **状态：** 待批 A · 方案 M · 荐交施工（2026-10-12 17:23 Asia/Shanghai）  
> **上游：** [`RESEARCH-ember-short-visual-sx17-sx19-thin-2026-10-12.md`](../design/RESEARCH-ember-short-visual-sx17-sx19-thin-2026-10-12.md) · [`DESIGN-ember-short-visual-sx17-sx19-thin-2026-10-12.md`](../design/DESIGN-ember-short-visual-sx17-sx19-thin-2026-10-12.md) · TIP-W sx13–sx16 CLOSED `fb4debc2` · TIP-V · TIP-U · TIP-O · TIP-T 深化停窗  
> **前置：** TIP-W CLOSED PASS live `fb4debc2`（sx13–16 `@d12496`）；深化 densify **仍冻**；glance densify **仍停**  
> **施工岗：** 地图/脚本岗（`tools/p1map` 叠饰；**零改 jar 数值表**；runs 仅 `map_version` 戳）  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零 glance twin** · **零 ForgeChoice/wear/pity/awkDelta densify** · 禁重铺地板/改房序 · 禁改 S56–S58/Cast/afk · 禁 sx20 · 禁 skill_mult · 禁粒子 densify

---

## 0. 一句话

短征 sx17–sx19 叠主题柱/灯/饰带，进本一眼有差，不改奖不改伤；**闭合** sx01–19 视觉叠饰线。

---

## 1. 选题裁决

| 候选 | 裁决 | 理由 |
|------|------|------|
| 烬斩手感再研究/cue | **否（本窗）** | TIP-K 已 CLOSED；再开易 densify；禁 skill_mult |
| 变强途径再 densify | **否** | TIP-T 冻 |
| 主线内容 | **否（本窗）** | 不抢薄窗；视觉收尾债更贴 TIP-W HOLD |
| **短征视觉收尾 sx17–sx19** | **采纳** | TIP-W D12507 HOLD；三本收尾（非四本）；真地形已有、缺叠饰；闭合 O→U→V→W→X |

---

## 2. 方案 M（定稿荐）

| ID | 项 | 验收 |
|----|-----|------|
| D12512 | 本 tip + RESEARCH + DESIGN | 路径可点 |
| D12513 | 审计：sx17–sx19 DESIGN 主材 vs 现网 builder（对齐 RESEARCH / D442） | 可核 |
| D12514 | 脚本 `tools/p1map/d12512_sx17_sx19_visual_pass.py`：范式对齐 d12496/d12480/d12464/d12376（采实心、占位 skip、**禁**重铺地板） | 可跑 |
| D12515 | **sx17** 叠饰：棱柱岩柱 + 海晶灯帽 + 青羊毛饰带；闸侧铁栅；落潮通道/水饰面不堵 | 进本可感 |
| D12516 | **sx18** 叠饰：石英/安山柱 + 折角侧铁栅 + 紫·青染色玻璃饰带；折角走道不封 | 进本可感 |
| D12517 | **sx19** 叠饰：石砖/安山柱 + 节点侧铁栅 + 紫羊毛；共鸣侧穿口不封、音符不盖 | 进本可感 |
| D12518 | `ember-v1-runs.yml` 三本 `map_version` → `@d12512` | git 可核 |
| D12519 | 脚下实心复验（各本 room/Boss safe） | 无坠虚空 |
| D12520 | 冒烟：sx17+sx19 enter→三房+Boss；Director 房序不变 | 记待恢复服 |
| D12521 | 静态：S56–S58 / Cast / afk / STAMP `git show` **无抬** | PASS |
| D12522 | HOLD：sx20；粒子 densify；Affix；glance；变强 densify；skill densify | 本包不开 |
| D12523–D12527 | 缓冲热修（堵路回滚）；禁借号抬表 | 超额新 tip |

### 玩家口径

- 潮廊 / 晶棱 / 弦廊边上有各自主题柱和灯，不像同一套灰盒子。  
- 路和奖都没变。  
- 短征十九本视觉叠饰线至此收尾。

---

## 3. 明确不做

- 重铺地板 / 改 R2 / 改 rooms·boss 坐标  
- 改 S56–S58 / 日帽 / 体力 / Cast 数值  
- Affix · skill_mult · glance twin · ForgeChoice/wear/pity/awkDelta densify  
- sx20 · 粒子 densify · 挂机抬产  

---

## 4. 验收清单

- [ ] 总控批 **A · 方案 M**  
- [ ] D12514–D12519 脚本+三本叠饰+map_version+脚下实心  
- [ ] 奖励 / Cast / afk `git show` 无抬  
- [ ] 不跑 p1sim（叠饰级）

---

## 5. 批注

- [ ] 总控批 **A · 方案 M** · （待填）  
- 选题：短征白模叠饰收尾 sx17–sx19（TIP-W 范式 · 闭合 O→U→V→W→X）  
- **批注（荐）：** 叠饰禁重铺；对齐 d12496/d12480/d12464/d12376；sx20/粒子 densify HOLD；仅 map_version `@d12512`；零 jar 数值；硬禁含变强 densify 冻

## STATUS: 待批 A · 方案 M · 2026-10-12 17:23 CST
