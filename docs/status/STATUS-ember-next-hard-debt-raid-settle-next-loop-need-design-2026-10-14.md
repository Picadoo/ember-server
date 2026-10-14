# 状态 · 下一档硬债选定 · 需策划（拟 D13331 · 团本结算下一环菜单化 · settle 聊天钮去指令化 · docs-only · ≠短征settle下一环同题 · ≠短征settle回看同题 · ≠variety再拧 · ≠soft-reward同题 · ≠路径追同题 · ≠日差同题 · ≠今日推/周目标复读 · ≠半行 · ≠sx20 · ≠抬周帽 · ≠庭内旁路 · ≠Cast/Aid/todayRot再拧）

> **旁注（待批 A · 方案 M · docs-only · 2026-10-14 08:40 Asia/Shanghai · 全弧荐 M · L 默认不批 · 零改周帽/loot · 挑战/深渊后置）：** tip **待批 A · 方案 M** · DESIGN [`DESIGN-ember-raid-settle-next-loop-visible-2026-10-14.md`](../design/DESIGN-ember-raid-settle-next-loop-visible-2026-10-14.md) · RESEARCH [`RESEARCH-ember-raid-settle-next-loop-visible-2026-10-14.md`](../design/RESEARCH-ember-raid-settle-next-loop-visible-2026-10-14.md) · backlog `B-raid-settle-next-loop-visible` · **TIP-CZ** · **≠关观察 ≠抬日表 ≠sx20 ≠半行独批 ≠Cast/Aid/variety再拧 ≠短征settle下一环同题 ≠短征settle回看同题 ≠soft-reward同题 ≠庭内旁路再拧 ≠教裸指令 ≠densify ≠抬 weeklyCap/loot ≠挑战/深渊同窗大扫**。

> **上游结案：** **TIP-CY CLOSED** live `bdf88e272`（D13330 短征花样结算回看齐 · feat `e5cb2ddd6` · tip `a1f5a2537` · jar `1.65.823-d13330.local` · PASS `176e2fe03` · 批 sha `afeb9a210` · **settle回看/variety再拧同题勿再交**）。总控派 TIP-CZ：内容优先拉长在线；短征同题勿复读；庭内旁路近饱和默认勿交；宝石/Cast·Aid·variety完整经济/节·灾厄/sx20仍缓；§6 全服 settle 去指令化可施工。Cast+BossAid+EliteAid+todayRot+variety(W1a–d)+庭内全弧+今日推/冲一下/周短征/三冲+日差本可冲+路径追+settle下一环+soft-reward+settle花样回看 **已齐勿复读**。半行 REJECT `d00ed84bc` **保持否决**。

**日期：** 2026-10-14（上海时间）
**本窗性质：** tip **待批 A · 方案 M** · 拟 D13331 · TIP-CZ · docs-only · L 默认不批 · 零改周帽/loot · 挑战/深渊后置 · 2026-10-14 08:40 Asia/Shanghai
**硬规格：** [`DESIGN-ember-raid-settle-next-loop-visible-2026-10-14.md`](../design/DESIGN-ember-raid-settle-next-loop-visible-2026-10-14.md) · backlog `B-raid-settle-next-loop-visible`
**打开理由：** 短征 settle 下一环已菜单化（D13328），但团本周清仍 ConfirmTokens 教指令——D13330/29/28§6⑥ 明文后置「全服 settle 去指令化」；内容闭环断在团本结算。

---

## 0. 局势一句话

短征结算下一环已齐；补 **团本周清下一环薄页真可点**——**不是**短征同题复读，**不是**半行。

---

## 1. 选题

| # | 候选 | 裁决 | 理由（证据） |
|---|------|------|--------------|
| **1** | **团本结算下一环菜单化（荐）** | **采纳 · 需策划** | E1–E9；见 §1.1 |
| 2 | 挂机区资源环加厚 / 庭内旁路 / 宝石 | **否** | 庭内近饱和；gem gap 未挂庭面；无新硬债 |
| 3 | soft-reward / settle回看 / 短征settle下一环 | **否** | 派单禁；刚 CLOSED |
| 4 | Cast/Aid/todayRot/W1e/variety 再拧 / 完整 variety 经济 | **否** | 硬禁；已齐 / 仍缓 |
| 5 | 路径追 / 日差本 / 今日推 / 周目标 / 三冲 | **否** | 已齐勿再交 |
| 6 | 挑战/深渊 settle 同窗大扫 | **后置** | §6；本窗只钉团本一支 |
| 7 | 只 Open「团本下一环」半行 | **否** | REJECT 保持 |
| 8 | sx20 / 抬周帽 / densify / 灾厄 / Pack6 | **否** | 硬禁或仍缓 |

### 1.1 证据核验（实扫 · 2026-10-14）

| # | 证据 | 出处 | 含义 |
|---|------|------|------|
| E1 | 总控派 TIP-CZ；内容优先；短征同题勿再交；庭内默认勿交；§6 可施工 | 派单 | 选题边界 |
| E2 | D13330/29/28 DESIGN §6⑥ 明文后置「全服 settle 去指令化」；短征支 TIP-CW 已齐 | 三份 DESIGN §6 | **合法未 CLOSED 后置** |
| E3 | `EmberRaidLoop.afterWeeklyClear` ConfirmTokens → `/corerpg p1 raidpath|recruit|enter` | RaidLoop.java L31–58 | **缺菜单冲钉** |
| E4 | `EmberRaidPath` glance/offerPick 教 `/corerpg p1 raidpath` | RaidPath.java | 路径亦 chat 轨 |
| E5 | TrMenu **无** `ember_p1_raid_settle`；`ember_raid` 仅大厅开本 | menus/ | **缺结算薄页** |
| E6 | 短征 `ember_p1_short_settle` 范式可镜像；**不**复开短征同题 | TIP-CW | 可施工 |
| E7 | ≠ 短征settle下一环：不碰 short_settle；≠ settle回看：不碰 short_var_* | 本 STATUS | 边界 |
| E8 | **为何不是半行：** 主交付=插件改入口 + 薄页真态；半行不可独批 | 本 STATUS | 过硬禁 |
| E9 | 半行 REJECT `d00ed84bc` 保持 | backlog | 否决只口号 |

---

## 2. 荐方案

| 方案 | 一行 | 本窗 |
|------|------|------|
| **M（荐）** | 插件 RaidLoop 下一环改开菜单 + 薄页 `ember_p1_raid_settle` 真可点；零改周帽/loot | **荐批** |
| L | 仅改一条聊天钮目标无薄页 | **不批** |
| A | 只半行 | **否决**（REJECT） |
| W | 抬周帽/sx20/短征同题复读/variety再拧/庭内/挑战深渊大扫 | **否决** |

---

## 3. 派单草案（批 A 后）

```
【派单·内容真债待批A·拟D13331·TIP-CZ】团本结算下一环菜单化（真可点 · ≠短征settle同题 · ≠半行空转）
优先级：总控 TIP-CY CLOSED 后连开 · 内容乐趣 · docs-only→批后施工
请出 DESIGN-ember-raid-settle-next-loop-visible-2026-10-14.md，STATUS=待批 A · 荐 M。
```

---

## 4. 链接

| 项 | 路径 |
|----|------|
| DESIGN | [`DESIGN-ember-raid-settle-next-loop-visible-2026-10-14.md`](../design/DESIGN-ember-raid-settle-next-loop-visible-2026-10-14.md) |
| RESEARCH | [`RESEARCH-ember-raid-settle-next-loop-visible-2026-10-14.md`](../design/RESEARCH-ember-raid-settle-next-loop-visible-2026-10-14.md) |
| 薄 tip | [`TIP-ember-raid-settle-next-loop-visible-thin-d13331-2026-10-14.md`](../tips/TIP-ember-raid-settle-next-loop-visible-thin-d13331-2026-10-14.md) |
| backlog | `B-raid-settle-next-loop-visible` |
| 上游 | TIP-CY CLOSED live `bdf88e272` · feat `e5cb2ddd6` · tip `a1f5a2537` · PASS `176e2fe03` · jar `1.65.823-d13330.local` · 批 sha `afeb9a210` · D13330；半行 REJECT `d00ed84bc` 保持否决；settle回看/variety再拧同题勿再交 |

---

## STATUS: **待批 A** · 荐 **M** · 拟 D13331 · TIP-CZ · docs-only · 2026-10-14 08:40 Asia/Shanghai
