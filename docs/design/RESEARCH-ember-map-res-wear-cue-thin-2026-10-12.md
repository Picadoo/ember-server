# RESEARCH · 图装身份 · 局内换装共鸣升沿薄窗 · 2026-10-12

> **状态：** 调研定稿 · 对接 tip `TIP-ember-map-res-wear-cue-thin-d12424-d12439-2026-10-12.md`  
> **上游：** [`DESIGN-ember-power-paths-variety-2026-10-12.md`](DESIGN-ember-power-paths-variety-2026-10-12.md) §3.3 图装 · G1 `EmberMapResonance` · TIP-J 停 glance · TIP-Q CLOSED  
> **硬禁：** 零 Affix densify · 零伤乘 / 抬表 / 加宽数值 · 零 glance twin · 零 MG3 soft gift densify

---

## 0. 为何挑这条

总控 TIP-Q 后要变强途径薄窗：**工坊选择感 / 图装身份**，非加难；glance / pity densify 仍停。  
工坊稳赌/commit/forgegoal/「确定成功」文案 **已齐**。  
图装：methodLine / 农场闭环 / 入场共鸣 cue / LootMapFeel 粒子 **已有**；缺口是——**局内才换上本图掉落件** 时，`maybeEnterCue` 已过（或入场时未穿），玩家听不见「现在共鸣亮了」——图装变强途径断在穿戴瞬间。

---

## 1. 现网缺口（码）

| 项 | 现网 | 缺口 |
|----|------|------|
| 入场 | `EmberMapResonance.maybeEnterCue`：仅 enter 且 `active(lo)` 时 latch+喊 | 入场未穿本图件 → 不 latch；**局内换上** → **无 cue** |
| 加宽 | `active(lo, map)` 几何加宽已连续生效 | 手感真有，**无升沿白话** |
| 穿戴骨尘 | `MapOriginWearBone` 挂在 enter cue 上 | 局内首穿够条件也摸不到（本窗 **可** 复用 once/day，**禁**加量） |
| 工坊选择 | TIP-B/H 已齐 | 本窗不抢 |

DESIGN §3.4 STOP 名单含 MapResonance = **禁再叠功率/新 twin**；本窗 = **既有 active 升沿表面化**（对照 TIP-P 读表不改表），非新乘区。

---

## 2. 薄施工边界

| 做 | 不做 |
|----|------|
| 局内 loadout 刷新：`active` 假→真 升沿 → 半行共鸣白话（同 enter 口径） | 改加宽数值 / skill_mult / 掉率 |
| 每 run 或 debounce 一次（禁背包抖刷屏） | 枢纽 glance / 新 hope PAPI |
| 可选：升沿时若 `WearBone.ready` 则走既有 once/day（零加量） | MG3 soft densify（新礼/加 BONUS） |

门禁：文案+升沿 latch → **不跑 p1sim**。

---

## 3. 对照

| 号 | 性质 |
|----|------|
| D6305 | 图源共鸣几何（零伤乘） |
| D3753 | 掉落粒子身份 |
| TIP-C/I/L | 图装一眼 / 农场闭环 |
| **本窗** | 穿戴升沿 → 身份变强可感（非 glance） |
