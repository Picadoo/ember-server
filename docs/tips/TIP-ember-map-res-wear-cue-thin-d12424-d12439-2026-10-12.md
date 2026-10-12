# tip · TIP-R 图装身份 · 局内换装共鸣升沿（非加宽 · 非 glance）· D12424–D12439

> **状态：** CLOSED · live 1.65.771-d12439.local（2026-10-12 17:02 Asia/Shanghai）  
> **上游：** [`RESEARCH-ember-map-res-wear-cue-thin-2026-10-12.md`](../design/RESEARCH-ember-map-res-wear-cue-thin-2026-10-12.md) · [`DESIGN-ember-map-res-wear-cue-thin-2026-10-12.md`](../design/DESIGN-ember-map-res-wear-cue-thin-2026-10-12.md) · `EmberMapResonance` · TIP-J 停 glance  
> **前置：** TIP-Q CLOSED PASS live `1.65.770-d12423`；power-glance densify **仍停**  
> **施工岗：** 插件岗（升沿 cue + latch；**零改加宽数值**）  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零 glance twin** · 零 skill_mult · 禁 MG3 soft densify（禁加 WearBone 量）

---

## 0. 一句话

局内换上本图掉落件时说一声「图源共鸣亮了」——图装途径摸得到，不改加宽。

---

## 1. 选题裁决

| 候选 | 裁决 | 理由 |
|------|------|------|
| 工坊选择感再一眼 / 稳赌 densify | **否** | TIP-B/H/forgegoal 已齐；再动易 glance densify |
| 掉落 giveItem 出处墙 | **否** | 结算已挤；LootMapFeel 粒子已有 |
| **图装身份 · 局内换装共鸣升沿** | **采纳** | enter cue 盖不住「局内才穿」；G1 已生效只缺白话；非 glance |

---

## 2. 方案 M（定稿）

| ID | 项 | 验收 |
|----|-----|------|
| D12424 | 本 tip + RESEARCH + DESIGN | 路径可点 |
| D12425 | DESIGN power-paths **指针** TIP-R（图装身份深度 · 非 glance） | 进度戳 |
| D12426 | `EmberMapResonance.maybeWearCue(p, d, mapKey)`：in-run 且 `active` 升沿 → enter 同口径半行 + AB | 单测升沿 |
| D12427 | Latch：每 runId（或等价）一次；非 active→active 不喊；离图丢快照 | 抖背包无刷 |
| D12428 | 挂点：loadout `refresh` / 换装后路径（仅有活跃 run 时） | 可核 |
| D12429 | 可选：升沿且 `MapOriginWearBone.ready` → 既有 `maybeGift`（**BONUS 不变**） | once/day |
| D12430 | 热关：`map_res.wear_cue` 缺省 true | 可热关 |
| D12431 | 单测：假→真喊；真→真静；真→假→真 同 run 仍静（latch） | PASS |
| D12432 | 验收：加宽/掉率/`git show` 共鸣几何 **无抬**；无新 hub 格 | 可核 |
| D12433 | HOLD：枢纽 mapid densify；改加宽；WearBone+；Affix；工坊 glance | 本包不开 |
| D12434–D12439 | 缓冲热修（文案过长压行）；禁借号抬表 | 超额新 tip |

### 玩家口径

- 本里换上本图掉的刃/符，会听到共鸣亮了。  
- 加宽规则没变；只是以前局内换装不说话。

---

## 3. 明确不做

- 改共鸣加宽幅度 / skill_mult / 掉率 / Affix  
- 枢纽 glance twin / 新 hope·map PAPI  
- WearBone BONUS+ / 新每日礼（MG3）  
- 工坊稳赌 / forgegoal densify  

---

## 4. 验收清单

- [x] 总控批 **A · 方案 M**  
- [x] D12426–D12430 升沿+latch+热关  
- [ ] 加宽表 `git show` 无抬  
- [ ] 不跑 p1sim（文案级）

---

## 5. 批注

- [x] 总控批 **A · 方案 M** · 2026-10-12 16:59 CST  
- 选题：变强途径 · 图装身份深度（局内换装共鸣升沿）· 非 glance  
- **批注：** 既有 active 升沿表面化；每 run 一次 latch；WearBone 仅复用零加量；零改加宽；硬禁 Affix/伤乘/抬表/glance twin/soft densify

## STATUS: CLOSED · live 1.65.771-d12439.local · 2026-10-12 17:02 CST
