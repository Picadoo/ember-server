# DESIGN · 短征花样结算回看（真可点/菜单可感 · ≠soft-reward同题 · ≠variety再拧 · ≠完整variety经济 · ≠settle下一环同题 · ≠半行 · ≠抬 event_core）· 拟 D13330 · TIP-CY

| 日 | 事 |
|----|-----|
| 2026-10-14 08:14 Asia/Shanghai | 策划 · 初稿 STATUS=**待批 A** · 荐 M · TIP-CX CLOSED 后连开 · 选题短征花样结算回看 · 拟 D13330 · TIP-CY |

STATUS=**待批 A** · 荐方案 M · D13330 · TIP-CY · docs-only · 批准前禁改 YAML/Java · 2026-10-14 08:14 Asia/Shanghai

> tip [`STATUS-ember-next-hard-debt-short-variety-settle-review-need-design-2026-10-14.md`](../status/STATUS-ember-next-hard-debt-short-variety-settle-review-need-design-2026-10-14.md) · RESEARCH [`RESEARCH-ember-short-variety-settle-review-2026-10-14.md`](RESEARCH-ember-short-variety-settle-review-2026-10-14.md) · backlog `B-short-variety-settle-review` · **≠关观察 ≠抬日表 ≠开 R ≠开 K3 ≠假开旧日常 ≠sx20 ≠菜单诚实/observe 空转 ≠半行独批 ≠Cast粒子主交付 ≠EliteAid/BossAid再拧 ≠todayRot/W1e独批 ≠抬 event_core ≠新 variety kind ≠完整 variety 经济 ≠variety再拧 ≠第五件花料 ≠推荐花复读 ≠洗练/烙纹/转化/随机锻再拧 ≠庭内旁路再拧 ≠今日推复读 ≠本周短征可冲复读 ≠日差本同题 ≠路径追同题 ≠settle下一环同题 ≠soft-reward同题 ≠抬 targets.short ≠写入 weekly_goals ≠P格兼确认 ≠复开 D423 ≠教裸指令 ≠撤分格 ≠灾厄重开 ≠Pack6 ≠densify**

---

## 0. 一句话

短征有奖/无奖结算后，本局词缀/事件标签进菜单「本局花样」格一眼可读；**不**发核心/碎片，**不**改率表，**不**再交 soft-reward。

---

## 1. 证据与边界

| # | 证据 | 含义 |
|---|------|------|
| E1 | 总控 TIP-CY 开短征趣味薄窗；soft-reward 同题勿再交 | 合法选题 |
| E2 | D13321/28/29§6③ 后置「短征花样结算回看」 | 后置兑现 |
| E3 | 房内 VarietyFeel cue 齐；settle 零记忆/零菜单 | 缺口 |
| E4 | ShortSettle 只记 map；RunSession affix/event 结算丢 | 缺冲钉 |
| E5 | short_settle 无 V 格 | 缺可点/可读位 |
| E6 | ≠ soft-reward / ≠ settle下一环主弧 / ≠ variety 经济 | 边界 |

| 候选 | 裁决 | 理由 |
|------|------|------|
| **花样结算回看** | **采纳** | E1–E6 |
| soft-reward / settle下一环 / 路径追 / 日差 | **否** | 已齐勿再交 |
| 庭内旁路 / Cast·Aid·variety再拧 / 完整经济 | **否** | 硬禁或仍缓 |
| 只半行 | **否** | REJECT |

### 玩家体验钉

1. **结算后一眼：** 薄页见「本局花样」——词缀名 / 事件名 / 或「本局无花样」。  
2. **真记忆：** 关菜单再开仍见上一局（period 记到下次短征 settle 覆盖）。  
3. **零经济误导：** lore **不**承诺 event_core/affix_shard。  
4. **零手打指令：** 仅菜单可读；玩家不教 `/corerpg …` 手打。

---

## 2. 方案

| 方案 | 一行 | 本窗 |
|------|------|------|
| **M · settle 记忆 + V 格（荐）** | 插件记标签 + PAPI + 薄页 V；零改率；零发核心/碎片 | **荐** |
| L · 仅 lore 口号 | Open 写「有花样」无记忆 | **默认不批** |
| A · 只半行 | Open 空转 | **否决** |
| W · 抬表/发核心/再拧/同题复读 | 硬禁 | **否决** |

### 批注勾选（总控）

- [ ] 勾 **方案 M**（全弧）· STATUS→已批 A  
- [ ] 或勾 **方案 L**（仅 lore；记忆后置）· **本窗默认不批 L**  
- [ ] 批注：…

---

## 3. 经济幅度（供总控批 · 明示）

| 项 | 钉死 | 禁 |
|----|------|-----|
| 奖励 | **零** NI / **零** 徽 / **零** event_core / **零** affix_shard | 禁借回看发经济 |
| 率表 | **零改** `event_rate` / variety weights / 新 kind | 禁再拧 |
| 日帽/S/targets | **零改** | 除非总控另批（本 tip **不**明示抬） |
| soft-reward | **不碰** claim / L 领奖 | 同题勿再交 |

---

## 4. 施工规格（批 A 后）

| 轨 | 内容 | 禁 |
|----|------|-----|
| **W1a · 插件（必做）** | settle 时从 session 取 `affix`/`eventKind`（及 done 态）写入 period（如 `p1_short_var_affix` / `_event` 编码或字符串仓）；PAPI `short_var_hit` / `_affix_label` / `_event_label` / `_line`；无花样诚实「本局无花样」；单测 | 不改 rate；不发 var_* 经济；不改 ShortSettle map 记忆语义 |
| **W1b · 结算薄页 V（必做 · TrMenu）** | Layout 增 V（建议 `##E#S#P##`→`#VE#S#P#` 或同行空位）；图标「本局花样」lore 接 PAPI；无 hit 灰态诚实 | 不改 E/S/P/F 再进/选页/路径/工坊主弧；不改选页 Layout |
| **W1c · 辅指针（可选同批）** | settle Open 半行提一句「本局花样见 V」→ **不可独批** | 半行不可独档 |
| **不改** | 日帽 / S / targets / weekly_goals / Cast / Aid / todayRot / variety yaml 率 / 庭内 / soft-reward / ShortPath resolve / sx20 | — |

### 玩家零命令

- TrMenu 只读展示；聊天 ConfirmTokens **不做**主 UI。  
- 禁教玩家手打查询指令当主路径。

---

## 5. 验收（批后施工）

| # | 条 |
|---|-----|
| V1 | 本局命中词缀或事件：settle 后 V 显示对应中文标签；再开菜单仍在 |
| V2 | 本局无花样：V 灰「本局无花样」；无误标 |
| V3 | PAPI hit/affix/event/line 诚实；**零** event_core/affix_shard 因回看多发 |
| V4 | `git diff`：无改日帽/`afk.tiers`/伤乘/S/`event_rate`/`event_core`、无 sx20、无半行独档、无 Cast/Aid yml、无庭内菜单、无 soft-reward、无新 variety kind |
| V5 | 玩家路径无裸指令主 UI；TrMenu+既有 NI |

### ≠ 对照（防复读）

| 易混 | 本窗 |
|------|------|
| soft-reward（D13329） | **不碰** claim/L |
| settle下一环（D13328） | **不改** E/S/P/F 主弧；只加 V |
| variety 房内（D13321） | **不改** roll/cue；只结算回看 |
| 完整 variety 经济 | **零发** 核心/碎片 |
| 半行 lore-only | **主交付=记忆+PAPI+V** |

---

## 6. 后置（明文 · 本窗不做）

1. 写入 `weekly_goals` / 抬 `targets.short` / 余烬徽并轨（须总控另批更大经济）  
2. 庭内顺手宝石（须庭面先亮 gem gap）  
3. 短征花样结算 **发** 核心/碎片 / variety 完整经济 / W1e todayRot 偏置  
4. fest 继任 / 灾厄枢纽可见（须总控另批）  
5. sx20（仍禁，除非总控另开窗）  
6. 全服 settle（团本/挑战/深渊）聊天钮去指令化大扫（可另窗）  

---

## 变更记录

| 日 | 事 |
|----|-----|
| 2026-10-14 08:14 Asia/Shanghai | 策划 · 初稿 STATUS=**待批 A** · 荐 M · TIP-CX CLOSED 后连开 · 选题短征花样结算回看 · 拟 D13330 · TIP-CY · ≠半行 ≠soft-reward同题 ≠variety再拧 ≠完整经济 ≠settle下一环同题 ≠Cast/Aid/todayRot再拧 ≠庭内旁路 · **改题有证据**（§6③；房内齐结算丢；见 RESEARCH） |

*D13330 **待批 A · 方案 M** · 短征花样结算回看 · TIP-CY。**荐全弧非 L** · ≠半行 ≠soft-reward同题 ≠variety再拧 ≠完整variety经济 ≠sx20 ≠抬 event_core。*
