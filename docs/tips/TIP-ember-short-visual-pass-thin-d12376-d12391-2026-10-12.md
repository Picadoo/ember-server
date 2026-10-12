# tip · TIP-O 短征轻视觉点缀薄窗（sx01–sx04 叠饰）· D12376–D12391

> **状态：** 批 **A · 方案 M**（总控 2026-10-12 16:41 Asia/Shanghai）· 交地图/脚本岗  
> **上游：** [`RESEARCH-ember-short-visual-pass-thin-2026-10-12.md`](../design/RESEARCH-ember-short-visual-pass-thin-2026-10-12.md) · [`DESIGN-ember-short-visual-pass-thin-2026-10-12.md`](../design/DESIGN-ember-short-visual-pass-thin-2026-10-12.md) · D443/D444 真地形 · D449/D450 主线范式  
> **前置：** TIP-N Boss 预警 R 薄热修 CLOSED live 1.65.768-d12375；glance densify **仍停**  
> **施工岗：** 地图/脚本岗（`tools/p1map` 叠饰；**零改 jar 数值表**；runs 仅 `map_version` 戳）  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零 glance twin** · 禁重铺地板/改房序 · 禁改 S4x/体力/Cast HP·dmg · 禁 sx20 · 禁挂机抬产

---

## 0. 一句话

短征首四本有真地形仍偏结构白模——叠一层主题柱/灯/饰带，进本一眼有差，不改奖不改伤。

---

## 1. 选题裁决

| 候选 | 裁决 | 理由 |
|------|------|------|
| 挂机诚实漏口（remain/仓差/名片） | **否** | D305/D405–D415 已齐；挂机 R 提示密度可感弱于进本白模 |
| 主线白模点缀 | **否** | D449/D450 已收 |
| **短征轻视觉点缀（sx01–sx04）** | **采纳** | 真地形后仍缺主线级叠饰；内容可感、零经济战斗压 |
| Boss adds 门延迟 / 走廊 W2 | **后置** | TIP-N 已 HOLD；易触门压 |

---

## 2. 方案 M（定稿）

| ID | 项 | 验收 |
|----|-----|------|
| D12376 | 本 tip + RESEARCH + DESIGN | 路径可点 |
| D12377 | 审计表：sx01–sx04 DESIGN 主材 vs 现网 builder 缺口（RESEARCH §1） | 可核 |
| D12378 | 脚本 `tools/p1map/d12376_sx01_sx04_visual_pass.py`（或施工号名）：范式对齐 d450（采脚下实心、占位 skip、**禁**重铺地板） | 可跑 |
| D12379 | **sx01** 叠饰：石砖/橡木柱+荧石帽；灰·黄羊毛饰带；少量火盆位 | 进本可感 |
| D12380 | **sx02** 叠饰：木向柱+铁栅栏杆；荧石/红石灯链；少量海晶潮气点缀 | 进本可感 |
| D12381 | **sx03** 叠饰：深板岩柱；碎冰·雪墙根；闸侧铁栅加厚；禁整图冰 | 进本可感 |
| D12382 | **sx04** 叠饰：井口柱；井底赤灰饰带+岩浆块点缀（不堵走位） | 进本可感 |
| D12383 | `ember-v1-runs.yml` 四本 `map_version` → `@d12376`（落盘改施工号） | git 可核 |
| D12384 | 脚下实心复验（各本 room/Boss safe 点） | 无坠虚空 |
| D12385 | 冒烟：sx01+sx04 enter→三房+Boss 通；Director 房序不变 | 记待恢复服 |
| D12386 | 静态：奖励表 / Cast HP·delay·dmg / afk.tiers / STAMP `git show` **无抬** | PASS |
| D12387 | HOLD：sx05–sx19 叠饰；粒子 densify；挂机 R；Affix；glance | 本包不开 |
| D12388–D12391 | 缓冲热修（堵路回滚、错放拆块）；禁借号抬表 | 超额新 tip |

### 玩家口径

- 短征前四本走廊/井/闸边上有主题柱和灯，不像同一套灰盒子换色。  
- 路还是原来的路，奖还是原来的奖。

---

## 3. 明确不做

- 重铺地板 / 改 R2 点 / 改 rooms·boss 坐标  
- 改 S41–S43（及四本结算行）/ 日帽 / 体力  
- Cast 数值 · Affix densify · power-glance twin  
- sx05–sx19 同窗硬铺 · sx20 · 挂机 `daily_kills`/tiers  
- 假平面空板大修

---

## 4. 验收清单

- [x] 总控批 **A · 方案 M**  
- [ ] D12378–D12384 脚本+四本叠饰+map_version+脚下实心  
- [ ] 奖励 / Cast / afk / STAMP `git show` 无抬  
- [ ] 不跑 p1sim（叠饰级）

---

## 5. 批注

- [x] 总控批 **A · 方案 M** · 2026-10-12 16:41 CST  
- 选题：短征白模叠饰（主线 D449/D450 范式迁 sx）  
- **批注：** 首批 sx01–sx04；叠饰禁重铺；对齐 d450 采实心+占位 skip；sx05+ 另 tip；仅 map_version 戳；零 jar 数值

## STATUS: 批 A·M · 交施工 · 2026-10-12 16:41 CST
