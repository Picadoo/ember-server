# RESEARCH · Boss 预警方案 R 薄热修（持续蓄力条 · 半血前提示）· 2026-10-12

> **状态：** 调研定稿 · 对接 tip `TIP-ember-boss-telegraph-r-thin-d12360-d12375-2026-10-12.md`  
> **上游：** [`DESIGN-ember-boss-telegraph-honesty-2026-10-08.md`](DESIGN-ember-boss-telegraph-honesty-2026-10-08.md) §2.2 方案 R（**后置**）· D304 M 已 live · tip-J 后内容薄窗  
> **硬禁：** 零 Pack6 / 新招 · 零抬 every/warn/dmg/HP · 零 Affix densify · 零 glance twin · 零抬表

---

## 0. 为何挑这条

总控要「内容薄窗 · 可感玩法」；例举 Boss 可读性热修。  
D304 已落起手短闪 + 相位短签 + whiff 诚实；**方案 R 明确后置**：ActionBar 只闪一下、半血只在跨线时喊——高压时仍易丢读。  
本窗 = 开 R 的**演出级**子集，不动战斗压。

---

## 1. 现网缺口（码）

| 项 | 现网 | 缺口 |
|----|------|------|
| 起手 | `startWarn` → `flashActionBar(castStartBar…)` **一次** | warn 窗 1～3s 内条被其它 AB 盖掉就没了 |
| 半血 | `storyHalfHpCue` / raid `halfHpCue` 在 **跨 50%** 时发 | 无「即将半血」缓冲读秒 |
| 粒子预示 | D3281+ `EmberTelegraph` 等 | **≠** HUD 持续文案；本窗不 densify 粒子 twin |
| 增援门延迟 | 方案 R 候选 | 动门压 → 易触 p1sim；**本窗不做** |

---

## 2. 薄施工边界

| 做 | 不做 |
|----|------|
| 蓄力 ActionBar **按 tick 续写**至 warn 结束（同 `castStartBar` 文案） | 改 warn 秒 / every / dmg / HP |
| HP≤55% 且未半血：一行「即将半血」（每首领每局一次） | adds 前延迟停门 |
| 热关键（yml feel 开关，缺省开） | Pack6 / 新招 / Affix / glance |

门禁：仅演出 → **不跑 p1sim**（对齐 DESIGN §3.2 R 演出级）。

---

## 3. 对照

D304 M = 通道+诚实揭示；本 R = 同文案的**时间伸展** + 半血前一口气。不学新 Pack。
