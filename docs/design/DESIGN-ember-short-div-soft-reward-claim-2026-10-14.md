# DESIGN · 短征日差软目标极薄有奖（真可点领 NI · ≠日差本同题 · ≠路径追同题 · ≠settle下一环同题 · ≠半行 · ≠抬 targets · ≠写 weekly_goals）· 拟 D13329 · TIP-CX

| 日 | 事 |
|----|-----|
| 2026-10-14 07:53 Asia/Shanghai | 总控批 A·M · 全弧非 L · 可施工 W1a插件+W1b物品 · 勿勾 CLOSED · 零抬日帽/S/targets · 零写 weekly_goals · L 不批 |
| 2026-10-14 07:51 Asia/Shanghai | 策划 · ancestor 对齐 TIP-CW CLOSED live `71db92dfb` |

STATUS=**已批 A · 方案 M** · APPROVED A·M · D13329 · TIP-CX · 全弧非 L · 可施工 W1a插件+W1b物品 · 勿勾 CLOSED · 零抬日帽/S/targets · 零写 weekly_goals · 2026-10-14 07:53 Asia/Shanghai

> tip [`STATUS-ember-next-hard-debt-short-div-soft-reward-need-design-2026-10-14.md`](../status/STATUS-ember-next-hard-debt-short-div-soft-reward-need-design-2026-10-14.md) · RESEARCH [`RESEARCH-ember-short-div-soft-reward-claim-2026-10-14.md`](RESEARCH-ember-short-div-soft-reward-claim-2026-10-14.md) · backlog `B-short-div-soft-reward-claim` · **≠关观察 ≠抬日表 ≠开 R ≠开 K3 ≠假开旧日常 ≠sx20 ≠菜单诚实/observe 空转 ≠半行独批 ≠Cast粒子主交付 ≠EliteAid/BossAid再拧 ≠todayRot/W1e独批 ≠抬 event_core ≠新 variety kind ≠完整 variety 经济 ≠第五件花料 ≠推荐花复读 ≠洗练/烙纹/转化/随机锻再拧 ≠庭内旁路再拧 ≠今日推复读 ≠本周短征可冲复读 ≠日差本同题 ≠路径追同题 ≠settle下一环同题 ≠抬 targets.short ≠写入 weekly_goals ≠P格兼确认 ≠复开 D423 ≠教裸指令 ≠撤分格 ≠灾厄重开 ≠Pack6 ≠densify**

---

## 0. 一句话

今日有奖不同本满软目标 3 → 一日一次骨尘×2，选页 L 一点领；**不**抬日帽/targets，**不**写 weekly_goals，**不**再交日差进本同题。

---

## 1. 证据与边界

| # | 证据 | 含义 |
|---|------|------|
| E1 | 总控 TIP-CX 开 soft-target 极薄有奖可选；短征日循环优先 | 合法选题 |
| E2 | D13326–28§6① 后置软目标发奖 | 后置兑现 |
| E3 | `SOFT_TARGET=3` 零经济；无 claim PAPI/命令 | 缺口 |
| E4 | L 仅 enter short-div | 缺领奖点 |
| E5 | OnlineClaimWinBone 骨尘日一次范式 | 薄奖可抄 |
| E6 | ≠ 日差本同题：不改 chase/enter 谓词 | 边界 |

| 候选 | 裁决 | 理由 |
|------|------|------|
| **软目标极薄有奖** | **采纳** | E1–E6 |
| 日差本同题 / 路径追 / settle 下一环 | **否** | 已齐勿再交 |
| 庭内旁路 / Cast·Aid·variety再拧 | **否** | 硬禁 |
| 只半行 | **否** | REJECT |

### 玩家体验钉

1. **一眼进度：** L 仍见 `今日有奖不同本 n/3`。  
2. **满 3 可领：** lore 出「可领 · 骨尘×2」；点 L → 真入包。  
3. **已领/未满诚实：** 已领灰；未满仍可点进 chase 本（既有 enter）。  
4. **零手打指令：** 仅菜单 actions；玩家不教 `/corerpg …` 手打。

---

## 2. 方案

| 方案 | 一行 | 本窗 |
|------|------|------|
| **M · claim NI + L 可领（荐）** | 插件日一次骨尘×2 + claim PAPI/命令 + L 可领态；零抬日帽/S/targets；零写 weekly_goals | **荐** |
| L · 仅 lore 口号 | 写「满 3 有奖」无真发 | **默认不批** |
| A · 只半行 | Open 空转 | **否决** |
| W · 抬表/写周目标/同题复读 | 硬禁 | **否决** |

### 批注勾选（总控）

- [x] 勾 **方案 M**（全弧）· STATUS→已批 A  
- [ ] 或勾 **方案 L**（仅 lore；claim 后置）· **本窗不批 L**  
- [x] 批注：总控批 A·M · D13329 · TIP-CX · 全弧非 L · 可施工 W1a插件+W1b物品 · 勿勾 CLOSED · 零抬日帽/S/targets · 零写 weekly_goals · 2026-10-14 07:53 Asia/Shanghai

---

## 3. 经济幅度（供总控批 · 明示）

| 项 | 钉死 | 禁 |
|----|------|-----|
| 触发 | `distinctRewarded n ≥ SOFT_TARGET(3)` 且今日未领 | 禁按本数叠发；禁每周累计改写 |
| 奖励 | NI `mat_ember_bone_dust`（骨尘）×**2** | 禁徽 / event_core / affix_shard / 花料 / 抬日帽 |
| 频次 | 一日一次 · period key `p1_short_div_soft_claim` | 禁无 period 可刷 |
| 对照 | ≈ 2× `EmberOnlineClaimWinBone`（骨尘×1） | 远薄于单次短征有奖通关 |
| 周目标 | **零写入** `weekly_goals` / **零改** `targets.short` | 禁把软目标并进周目标经济 |
| 日帽/S | **零改** daycap / S40–S58 | 除非总控另批（本 tip **不**明示抬） |

---

## 4. 施工规格（批 A 后）

| 轨 | 内容 | 禁 |
|----|------|-----|
| **W1a · 插件（必做）** | `EmberShortDayDivSoftClaim`（或等价）：`claimable(n, claimedToday)` / `tryClaim`→Vault/NI 骨尘×2 + period++；PAPI `short_div_claim_ok` / `_claimed` / `_claim_line`；命令 `corerpg p1 claim short-div-soft`（菜单调）；单测 | 不改 SOFT_TARGET 数；不改 chase/enter；不写 weekly_goals；不抬日帽/S/targets |
| **W1b · 选页 L（必做 · TrMenu）** | L 增优先态：`claim_ok≥1`→金/绿染料「日差软奖可领」lore 骨尘×2 + 进度；actions→`claim short-div-soft`；已领/未满/体力态诚实；**未满时保留** enter short-div | 不改十九本格 / D / F / 0 路径追 / settle 页 |
| **W1c · 辅指针（可选同批）** | settle 下一环页或 Open 半行提一句「日差软奖见选页 L」→ **不可独批** | 半行不可独档 |
| **不改** | 日帽 / S / targets.short / weekly_goals / Cast / Aid / variety / 庭内 / enter short-div 谓词 / ShortPath resolve / sx20 | — |

### 玩家零命令

- TrMenu actions 调 claim；聊天 ConfirmTokens **不做**主 UI。  
- 禁教玩家手打 `/corerpg p1 claim short-div-soft`。

---

## 5. 验收（批后施工）

| # | 条 |
|---|-----|
| V1 | n&lt;3：L 无「可领」；claim 拒发；仍可 enter short-div（有 chase） |
| V2 | n≥3 未领：L「可领」；一点→骨尘+2；period 日一次；再点拒发 |
| V3 | PAPI claim_ok/claimed/claim_line 诚实；line 仍 `n/3` |
| V4 | `git diff`：无改日帽/`afk.tiers`/伤乘/S/`weekly_goals`/`event_rate`/`event_core`、无 sx20、无半行独档、无 Cast/Aid/variety yml、无庭内菜单、无改 enter short-div 谓词 |
| V5 | 玩家路径无裸指令主 UI；TrMenu+NI |

### ≠ 对照（防复读）

| 易混 | 本窗 |
|------|------|
| 日差本可冲（D13326） | **不改** chase/enter；只加领奖 |
| 路径追 / settle 下一环 | **不碰** |
| 周目标 F / 今日推 D | **不碰** |
| 半行 lore-only | **主交付=真发 NI** |

---

## 6. 后置（明文 · 本窗不做）

1. 写入 `weekly_goals` / 抬 `targets.short` / 余烬徽并轨（须总控另批更大经济）  
2. 庭内顺手宝石（须庭面先亮 gem gap）  
3. 短征花样结算回看 / variety 经济发核心 / W1e  
4. fest 继任 / 灾厄枢纽可见（须总控另批）  
5. sx20（仍禁，除非总控另开窗）  
6. 全服 settle 去指令化大扫（TIP-CW 短征支已齐；其余可另窗）  

---

## 变更记录

| 日 | 事 |
|----|-----|
| 2026-10-14 07:53 Asia/Shanghai | 总控 · **批 A·M** · 全弧非 L · 可施工 W1a插件+W1b物品 · 勿勾 CLOSED · L 不批 · 半行 REJECT `d00ed84bc` 保持 · 零抬日帽/S/targets · 零写 weekly_goals |
| 2026-10-14 07:48 Asia/Shanghai | 策划 · 初稿 STATUS=**待批 A** · 荐 M · TIP-CW PASS 后连开 · 选题短征日差软目标极薄有奖 · 拟 D13329 · TIP-CX · ≠半行 ≠日差本同题 ≠路径追/settle同题 ≠Cast/Aid/variety再拧 ≠庭内旁路 · **改题有证据**（§6①；SOFT_TARGET 零经济；L 无领态；见 RESEARCH） |

*D13329 **已批 A · 方案 M** · APPROVED A·M · 短征日差软目标极薄有奖 · TIP-CX。**全弧非 L** · 可施工 W1a插件+W1b物品 · ≠半行 ≠日差本同题 ≠路径追/settle同题 ≠Cast/Aid/variety再拧 ≠庭内旁路 · ≠sx20 · ≠写 weekly_goals · ≠抬 targets · 2026-10-14 07:53 Asia/Shanghai*
