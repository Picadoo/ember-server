# 状态 · 下一档硬债选定 · 需策划（拟 D13332 · 挑战本结算下一环菜单化 · settle 聊天钮去指令化 · docs-only · ≠团本settle下一环同题 · ≠短征settle下一环·回看同题 · ≠variety再拧 · ≠soft-reward同题 · ≠路径追同题 · ≠日差同题 · ≠今日推/周目标复读 · ≠半行 · ≠sx20 · ≠抬掉落/体力价 · ≠庭内旁路 · ≠Cast/Aid/todayRot再拧 · ≠深渊同窗大扫）

> **旁注（已批 A · 方案 M · APPROVED A·M · 2026-10-14 09:02 Asia/Shanghai · 可施工 W1a插件+W1b物品 · L不批 · 勿勾 CLOSED · 零改挑战掉落/体力价/成色 · 深渊后置）：** tip **已批 A · 方案 M** · DESIGN [`DESIGN-ember-challenge-settle-next-loop-visible-2026-10-14.md`](../design/DESIGN-ember-challenge-settle-next-loop-visible-2026-10-14.md) · RESEARCH [`RESEARCH-ember-challenge-settle-next-loop-visible-2026-10-14.md`](../design/RESEARCH-ember-challenge-settle-next-loop-visible-2026-10-14.md) · backlog `B-challenge-settle-next-loop-visible` · **TIP-DA** · **≠关观察 ≠抬日表 ≠sx20 ≠半行独批 ≠Cast/Aid/variety再拧 ≠团本settle下一环同题 ≠短征settle下一环同题 ≠短征settle回看同题 ≠soft-reward同题 ≠庭内旁路再拧 ≠教裸指令 ≠densify ≠抬挑战掉落/体力价 ≠深渊同窗大扫**。

> **上游结案：** **TIP-CZ CLOSED** live `dceae5440`（D13331 团本结算下一环齐 · feat `166586585` · tip `f7a3b824c` · jar `1.65.824-d13331.local` · PASS `64bfa4265` · 批 sha `2e12d099e` · **团本结算下一环菜单化同题勿再交**）。总控派 TIP-DA：内容优先拉长在线；已齐同题勿复读；庭内旁路近饱和默认勿交；宝石/Cast·Aid·variety完整经济/节·灾厄/sx20仍缓；§6 挑战/深渊 settle 去指令化可施工。Cast+BossAid+EliteAid+todayRot+variety(W1a–d)+庭内全弧+今日推/冲一下/周短征/三冲+日差本可冲+路径追+短征settle下一环+soft-reward+settle花样回看+团本settle下一环 **已齐勿复读**。半行 REJECT `d00ed84bc` **保持否决**。

**日期：** 2026-10-14（上海时间）
**本窗性质：** tip **已批 A · 方案 M** · APPROVED A·M · 拟 D13332 · TIP-DA · 可施工 W1a插件+W1b物品 · L不批 · 勿勾 CLOSED · 零改挑战掉落/体力价/成色 · 深渊后置 · 2026-10-14 09:02 Asia/Shanghai
**硬规格：** [`DESIGN-ember-challenge-settle-next-loop-visible-2026-10-14.md`](../design/DESIGN-ember-challenge-settle-next-loop-visible-2026-10-14.md) · backlog `B-challenge-settle-next-loop-visible`
**打开理由：** 短征+团本 settle 下一环已菜单化，但挑战结算仍 ConfirmTokens 教指令——D13331§6① 明文后置「挑战本 settle 下一环」；内容闭环断在挑战结算。

---

## 0. 局势一句话

短征+团本结算下一环已齐；补 **挑战结算下一环薄页真可点**——**不是**短征/团本同题复读，**不是**半行；深渊另窗。

---

## 1. 选题

| # | 候选 | 裁决 | 理由（证据） |
|---|------|------|--------------|
| **1** | **挑战本结算下一环菜单化（荐）** | **采纳 · 需策划** | E1–E10；见 §1.1 |
| 2 | 挂机区资源环加厚 / 庭内旁路 / 宝石 | **否** | 庭内近饱和；gem gap 未挂庭面；无新硬债 |
| 3 | soft-reward / 短征settle下一环·回看 / 团本settle下一环 | **否** | 派单禁；刚 CLOSED |
| 4 | Cast/Aid/todayRot/W1e/variety 再拧 / 完整 variety 经济 | **否** | 硬禁；已齐 / 仍缓 |
| 5 | 路径追 / 日差本 / 今日推 / 周目标 / 三冲 | **否** | 已齐勿再交 |
| 6 | 深渊 settle 同窗大扫 | **后置** | §6②；本窗只钉挑战一支 |
| 7 | 只 Open「挑战下一环」半行 | **否** | REJECT 保持 |
| 8 | sx20 / 抬掉落 / densify / 灾厄 / Pack6 | **否** | 硬禁或仍缓 |

### 1.1 证据核验（实扫 · 2026-10-14）

| # | 证据 | 出处 | 含义 |
|---|------|------|------|
| E1 | 总控派 TIP-DA；内容优先；已齐同题勿再交；庭内默认勿交；§6 明确候选挑战/深渊 | 派单 | 选题边界 |
| E2 | D13331 DESIGN §6① 明文后置「挑战本 settle 下一环」；短征 TIP-CW + 团本 TIP-CZ 已齐 | DESIGN-CZ §6 | **合法未 CLOSED 后置** |
| E3 | `EmberChallengePath.afterSettle` ConfirmTokens → `/corerpg p1 enter … challenge` / `forge` / `mapfarm` | ChallengePath.java L222–229 | **缺菜单冲钉** |
| E4 | 挑战首通 D671 ConfirmTokens 工坊仍 `/corerpg p1 forge` | SettleService.java L665–668 | 首通旁路亦教指令 |
| E5 | TrMenu **无** `ember_p1_challenge_settle`；`ember_p1_challenge` 仅选图进本 | menus/ | **缺结算薄页** |
| E6 | 短征/团本 settle 范式可镜像；**不**复开同题 | TIP-CW / TIP-CZ | 可施工 |
| E7 | ≠ 团本settle下一环：不碰 raid_settle；≠ 短征settle：不碰 short_settle | 本 STATUS | 边界 |
| E8 | **为何不是半行：** 主交付=插件改入口 + 薄页真态；半行不可独批 | 本 STATUS | 过硬禁 |
| E9 | 半行 REJECT `d00ed84bc` 保持 | backlog | 否决只口号 |
| E10 | 深渊已有 `/ember_p1_abyss` 钮但仍教 `abysstarget`——另窗 | SettleService L784–796 | 本窗不扫深渊 |

---

## 2. 荐方案

| 方案 | 一行 | 本窗 |
|------|------|------|
| **M（荐）** | 插件 ChallengePath.afterSettle 下一环改开菜单 + 薄页 `ember_p1_challenge_settle` 真可点；零改掉落/体力价 | **批准** |
| L | 仅改一条聊天钮目标无薄页 | **不批** |
| A | 只半行 | **否决**（REJECT） |
| W | 抬掉落/sx20/短征·团本同题复读/variety再拧/庭内/深渊大扫 | **否决** |

---

## 3. 派单草案（批 A 后）

```
【派单·内容真债待批A·拟D13332·TIP-DA】挑战本结算下一环菜单化（真可点 · ≠团本/短征settle同题 · ≠半行空转）
优先级：总控 TIP-CZ CLOSED 后连开 · 内容乐趣 · docs-only→批后施工
请出 DESIGN-ember-challenge-settle-next-loop-visible-2026-10-14.md，STATUS=待批 A · 荐 M。
```

---

## 4. 链接

| 项 | 路径 |
|----|------|
| DESIGN | [`DESIGN-ember-challenge-settle-next-loop-visible-2026-10-14.md`](../design/DESIGN-ember-challenge-settle-next-loop-visible-2026-10-14.md) |
| RESEARCH | [`RESEARCH-ember-challenge-settle-next-loop-visible-2026-10-14.md`](../design/RESEARCH-ember-challenge-settle-next-loop-visible-2026-10-14.md) |
| 薄 tip | [`TIP-ember-challenge-settle-next-loop-visible-thin-d13332-2026-10-14.md`](../tips/TIP-ember-challenge-settle-next-loop-visible-thin-d13332-2026-10-14.md) |
| backlog | `B-challenge-settle-next-loop-visible` |
| 上游 | TIP-CZ CLOSED live `dceae5440` · feat `166586585` · tip `f7a3b824c` · PASS `64bfa4265` · jar `1.65.824-d13331.local` · 批 sha `2e12d099e` · D13331；半行 REJECT `d00ed84bc` 保持否决；团本结算下一环同题勿再交 |

---

## STATUS: **已批 A · 方案 M** · APPROVED A·M · 可施工 W1a插件+W1b物品 · 勿勾 CLOSED · L不批 · D13332 · TIP-DA · 2026-10-14 09:02 Asia/Shanghai
