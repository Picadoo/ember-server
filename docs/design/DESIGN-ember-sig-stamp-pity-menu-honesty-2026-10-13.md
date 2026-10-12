# DESIGN · 签名盖章软保底「连空 n/N」菜单诚实（签名 TrMenu · 薄窗 · 可施工）· 2026-10-13

> **状态：** OPEN · TIP-BW · 荐批 A·M · 2026-10-13 01:04 Asia/Shanghai  
> **上游 RESEARCH：** [`RESEARCH-ember-sig-stamp-pity-menu-honesty-2026-10-13.md`](RESEARCH-ember-sig-stamp-pity-menu-honesty-2026-10-13.md)  
> **对接 live：** TIP-BV CLOSED live items `9bac5aef8` · `EmberSigStampPity` · `EmberSigStampDry.glance` · `ember_p1_sig.yml` / `ember_p1_sig_imprint.yml` · `EmberGrowthService.sigPapi` · power-paths SUR3  
> **HOLD 旁注：** **L13 施工 HOLD**（TIP-AI FAIL · PASS≠开工）；Anvil soft densify / topUpTo / 灰印·聚火 / SF4 U2U3 / 调律 live bad 进持件 / 签名效果 lore / 套装 blurb / 宝石短效果 / 天赋悬停 / 烙纹档幅度 / 副手微量 / 强化率 / 升阶保留 / 形缓 / 转化 / access_* / 成色·精工确定成功 / 互换免费确定 / 洗练直留 / 烙纹直办再叠 / soft·开孔再叠 / **抬 STAMP_RATE · 扩 stampable · SigStamp densify** **仍冻**  
> **硬禁：** 零抬 STAMP_RATE/C12 · 零扩 stampable · 零改 SigStampPity need/force · 零 Affix densify · 零伤乘 · 零抬表 · 零抬 RATE/upgradeCost · 零抬 talent/gem/副手 flat · 零 soft·开孔再叠 · 零 Anvil densify · 零 topUpTo · 零 SF4 U2U3 · 零灰印/聚火 · 零签名效果 lore / 套装/宝石/天赋/烙纹档幅度/副手/强化率/升阶保留/形缓/转化/access_*/成色精工确定成功/互换免费确定/洗练直留/烙纹直办再叠 · 零再扫甲闸 · 零 SetChase 六槽 densify · 零 ForgeChoice densify · 零 ConvertCap densify · 零 forgeroll 权重 densify · 零 skill_mult/sx20 · 零效果长墙 · 零 lore>3 · 零并轨方案 B · 零发明第二 dry 计数 / celebration

---

## 0. 一句话

签名/烙印页用 **现网 `EmberSigStampDry.glance`** 写出连空 n/N（或软保底已满）；与结算同口径，不改掉率。

---

## 1. 问题陈述

| 层 | 现状 | 应然 |
|----|------|------|
| Pity / Dry | need / ready / glance 已 live | **不动数学** |
| 结算 / test show | 已喊 n/N · 软保底满 | **不动** |
| 签名/烙印菜单 | 「见结算提示」· 无 n/N | **一眼 glance** |
| PAPI | chase_hope / imprint_hope · **无 stamp_hope** | **新 `sig_stamp_hope` → glance** |
| sure/直办半行族 / 烙纹直办 | 已齐 | **零再叠** |

---

## 2. 方案 M（定稿荐）

### 2.1 SoT 锁（只读 · 禁改 STAMP_RATE / need）

| 键 | 规则 |
|----|------|
| 展示句 | **必须** = `EmberSigStampDry.glance(d)`（同结算/test show） |
| need | `EmberSigStampPity.need()` = `ceil(1/stampRate())` · **禁改** |
| ready | dry ≥ need → 软保底满句 · **禁改 force 逻辑** |
| 禁 | 手写漂移表 · 抬 stampRate · 扩 stampable · 第二套 dry 计数 · celebration |

### 2.2 PAPI（插件）

| id | 返回例 | 备注 |
|----|--------|------|
| `corerpg_p1_sig_stamp_hope` | `EmberSigStampDry.glance` 原文 | 新键；`sigPapi` 分支 `stamp_hope`（可选别名 `stamp_dry`/`dry`→同实现）；登记路径同 `chase_hope`；**零改**既有 chase/imprint_hope |

单测：dry=0 → 含「未连空」；dry=need-1 → 含 `n/need`；ready → 含「软保底已满」或「下次…盖章」。

### 2.3 TrMenu lore / Open 定稿（≤3）

| 点 | 定稿 |
|----|------|
| **sig Icon I** | lore：`%corerpg_p1_sig_head%` / `%corerpg_p1_sig_stamp_hope%` / `§7来源：重打得徽记 · 烙印可保底（不改掉率）`（≤3；来源行压短） |
| **sig Open tell** | 将「连空局数见结算提示」改为 `§e签名连空：§f%corerpg_p1_sig_stamp_hope%`（或等价短 tell）；保留其它 Open 行勿墙 |
| **imprint Open tell** | 同构替换「掉落连空见结算提示」→ 活 PAPI 半行 |
| **Icon M** | **本窗可不改**（已有 chase_hope；勿第四行挤墙） |

- 每格 lore **≤3**；禁教学墙 · 禁效果长墙。  
- **禁**改签名条目 n_i 效果/代价行（≠ BH 效果 lore 再叠）。  
- **禁**改 attune 页 / forge sure 族 / brand 直办本窗扩面。

### 2.4 调用方接线

| 点 | 动作 |
|----|------|
| `EmberGrowthService.sigPapi` | `stamp_hope`（+可选别名）→ `EmberSigStampDry.glance(d)` |
| 单测 | dry 三态（见 §2.2） |
| `ember_p1_sig.yml` I + Open | lore/tell → §2.3 |
| `ember_p1_sig_imprint.yml` Open | tell → §2.3 |
| `EmberSigStampPity` / `STAMP_RATE` / stampable / Soft / Affix | **禁改** |
| sure/keep/直办半行 · 烙纹直办 · 签名效果 lore | **本窗不改** |

### 2.5 验收核（插件+物品）

| 核 | 期望 |
|----|------|
| 签名说明格可见连空 n/N 或软保底满句 | 可见 |
| 与 `glance` / 结算同口径 | 可核 |
| Open tell 不再只写「见结算」 | 可核 |
| 烙印 Open 同构可见 | 可核 |
| lore ≤3；无第四行墙 | 可核 |
| `git show` 无 STAMP_RATE/need/stampable/Affix/soft·开孔·Anvil·SF4 / 签名效果 lore / 烙纹直办·forge sure 再叠 / 伤乘 / skill_mult | PASS |

### 2.6 玩家口径（固定）

- 打开签名页一眼看见 **连空进度 n/N**（或软保底已满 · 下次重打必出盖章候选）。  
- 本窗 **不**改签名掉率、不改软保底局数、不扩可盖章部位。  
- 仍守 lore≤3。

---

## 3. 验收

- tip + RESEARCH + 本 DESIGN + power-paths 戳可点  
- 批后：**余烬-插件** + **余烬-物品**；策划 docs-only  
- L13 施工仍 HOLD；Anvil / soft / 开孔再叠 / SF4 U2U3 / 灰印 / 调律 live bad / 签名效果 lore·套装·宝石·天赋·烙纹档幅度·副手·强化率·升阶保留·形缓·转化·access_* / 成色精工确定成功 / 互换 / 洗练直留 / **烙纹直办再叠** / **抬 STAMP · 扩 stampable** 仍冻

## 4. 旁债（下一 prefer · 非本窗）

| # | 债 | 态 |
|---|----|----|
| 1 | Anvil soft densify / topUpTo | **仍冻** |
| 2 | 灰印 / 聚火 | **仍冻** |
| 3 | SF4 U2 / U3 | **仍冻** |
| 4 | L13 施工 | **HOLD** |
| 5 | 调律 live bad 进持件 | **仍冻** |
| 6 | 签名效果 lore / 套装 blurb / 宝石 / 天赋 / 烙纹档幅度 / 副手 / 强化率 / 升阶保留 / 形缓 / 转化 / access_* / 成色·精工确定成功 / 互换 / 洗练直留 / 烙纹直办再叠 | **仍冻** |
| 7 | soft·开孔·GemHole / enhance+5 再叠 | **仍冻** |
| 8 | hub/raid/abyss「通关才开」扫尾 | **仍冻**（access_* 再叠） |
| 9 | 升阶「印记另一条路」短指针 | **另 tip · 可选** |
| 10 | forgeroll 普通格成色权重半行 | **另 tip · 可选**（禁 ForgeChoice densify） |
| 11 | Affix densify / ConvertCap densify / 抬表 / forgeroll 权重 densify / 抬 STAMP / 扩 stampable | **仍冻** |
| 12 | 分解菜单「确定」字面再压 | **另 tip · 可选**（弱） |
| 13 | SigStampPity 菜单再叠 / dry twin celebration | **本窗后冻再叠** |
| 14 | AFK 层差「还差/下一层更好」dwell cue | **另 tip · 可选**（非抬日表） |

## STATUS: OPEN · TIP-BW · 荐批 A·M · 2026-10-13 01:04 Asia/Shanghai
