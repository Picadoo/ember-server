# 状态 · 下一档硬债选定 · 需策划（拟 D13333 · 深渊结算下一环菜单化 · settle/evacuate 去指令化 · docs-only · ≠挑战settle下一环同题 · ≠团本settle下一环同题 · ≠短征settle下一环·回看同题 · ≠variety再拧 · ≠soft-reward同题 · ≠路径追同题 · ≠日差同题 · ≠今日推/周目标复读 · ≠半行 · ≠sx20 · ≠抬掉落/体力价/层表 · ≠庭内旁路 · ≠Cast/Aid/todayRot再拧）

> **旁注（待批 A · 荐方案 M · 2026-10-14 09:33 Asia/Shanghai · L不荐 · 勿勾 CLOSED · 零改掉落/体力价/层表 · 短征/团本/挑战 settle 同题勿复读）：** tip **待批 A** · DESIGN [`DESIGN-ember-abyss-settle-next-loop-visible-2026-10-14.md`](../design/DESIGN-ember-abyss-settle-next-loop-visible-2026-10-14.md) · RESEARCH [`RESEARCH-ember-abyss-settle-next-loop-visible-2026-10-14.md`](../design/RESEARCH-ember-abyss-settle-next-loop-visible-2026-10-14.md) · backlog `B-abyss-settle-next-loop-visible` · **TIP-DB** · **≠关观察 ≠抬日表 ≠sx20 ≠半行独批 ≠Cast/Aid/variety再拧 ≠挑战settle下一环同题 ≠团本settle下一环同题 ≠短征settle下一环同题 ≠短征settle回看同题 ≠soft-reward同题 ≠庭内旁路再拧 ≠教裸指令 ≠densify ≠抬深渊掉落/体力价/层表 ≠legacy evacuate 大扫**。

> **上游结案：** **TIP-DA CLOSED** live `8e12b6985`（D13332 挑战结算下一环齐 · feat `3d218ee83` · tip `d0f10a344` · jar `1.65.825-d13332.local` · PASS `020a8f2a7` · 批 sha `d0f10a344` · **挑战结算下一环菜单化同题勿再交**）。总控派 TIP-DB：选题钉死深渊 settle/evacuate 去指令化；已齐同题勿复读；庭内旁路近饱和默认勿交；宝石/Cast·Aid·variety完整经济/节·灾厄/sx20仍缓。Cast+BossAid+EliteAid+todayRot+variety(W1a–d)+庭内全弧+今日推/冲一下/周短征/三冲+日差本可冲+路径追+短征settle下一环+soft-reward+settle花样回看+团本settle下一环+挑战settle下一环 **已齐勿复读**。半行 REJECT `d00ed84bc` **保持否决**。

**日期：** 2026-10-14（上海时间）
**本窗性质：** tip **待批 A** · 荐 M · L不荐 · 拟 D13333 · TIP-DB · docs-only · 勿勾 CLOSED · 2026-10-14 09:33 Asia/Shanghai
**硬规格：** [`DESIGN-ember-abyss-settle-next-loop-visible-2026-10-14.md`](../design/DESIGN-ember-abyss-settle-next-loop-visible-2026-10-14.md) · backlog `B-abyss-settle-next-loop-visible`
**打开理由：** 短征+团本+挑战 settle 下一环已菜单化，但深渊结算仍 ConfirmTokens 教指令，且选层页 Open 教 `abysstarget`——D13332§6① 明文后置；内容闭环断在深渊结算。

---

## 0. 局势一句话

短征+团本+挑战结算下一环已齐；补 **深渊结算下一环薄页真可点** + **清 abysstarget 教指令**——**不是**短征/团本/挑战同题复读，**不是**半行。

---

## 1. 选题

| # | 候选 | 裁决 | 理由（证据） |
|---|------|------|--------------|
| **1** | **深渊结算下一环菜单化（荐）** | **采纳 · 需策划** | E1–E10；见 §1.1 |
| 2 | 挂机区资源环加厚 / 庭内旁路 / 宝石 | **否** | 庭内近饱和；gem gap 未挂庭面；无新硬债 |
| 3 | soft-reward / 短征settle下一环·回看 / 团本settle下一环 / 挑战settle下一环 | **否** | 派单禁；刚 CLOSED |
| 4 | Cast/Aid/todayRot/W1e/variety 再拧 / 完整 variety 经济 | **否** | 硬禁；已齐 / 仍缓 |
| 5 | 路径追 / 日差本 / 今日推 / 周目标 / 三冲 | **否** | 已齐勿再交 |
| 6 | legacy abyss evacuate 同窗大扫 | **后置** | LegacyGate；本窗只钉 P1 |
| 7 | 只 Open「深渊下一环」半行 | **否** | REJECT 保持 |
| 8 | sx20 / 抬掉落 / densify / 灾厄 / Pack6 | **否** | 硬禁或仍缓 |

### 1.1 证据核验（实扫 · 2026-10-14）

| # | 证据 | 出处 | 含义 |
|---|------|------|------|
| E1 | 总控派 TIP-DB；选题钉死深渊 settle/evacuate；已齐同题勿再交 | 派单 | 选题边界 |
| E2 | D13332 DESIGN §6① 明文后置深渊 settle；短征+团本+挑战已齐 | DESIGN-DA §6 | **合法未 CLOSED 后置** |
| E3 | `EmberSettleService` 深渊 newBest ConfirmTokens → `/ember_p1_abyss` / `/ember_p1_adventure` / `/corerpg p1 forge`；教 `abysstarget` | SettleService.java L784–797 | **缺菜单冲钉** |
| E4 | `EmberAbyssTarget.afterBest` ConfirmTokens→`abysstarget`；文案教手打 | AbyssTarget.java L63–68 | 层目标旁路教指令 |
| E5 | TrMenu **无** `ember_p1_abyss_settle`；`ember_p1_abyss` Open 仍 `command: corerpg p1 abysstarget` | menus/ | **缺结算薄页；选层页裸指令旁路** |
| E6 | 短征/团本/挑战 settle 范式可镜像；**不**复开同题 | TIP-CW / CZ / DA | 可施工 |
| E7 | ≠ 挑战settle：不碰 challenge_settle；≠ 团本/短征 settle | 本 STATUS | 边界 |
| E8 | **为何不是半行：** 主交付=插件改入口 + 薄页真态；半行不可独批 | 本 STATUS | 过硬禁 |
| E9 | 半行 REJECT `d00ed84bc` 保持 | backlog | 否决只口号 |
| E10 | legacy evacuate 提示仍在但 P1 LegacyGate 拒玩家——另窗 | AbyssSettleService · LegacyGate | 本窗不扫 legacy |

---

## 2. 荐方案

| 方案 | 一行 | 本窗 |
|------|------|------|
| **M（荐）** | 插件深渊 settle 下一环改开菜单 + 薄页 `ember_p1_abyss_settle` 真可点 + 清 abysstarget 教指令；零改掉落/体力价/层表 | **批准** |
| L | 仅改一条聊天钮目标无薄页 | **不批** |
| A | 只半行 | **否决**（REJECT） |
| W | 抬掉落/sx20/短征·团本·挑战同题复读/variety再拧/庭内/legacy evacuate 大扫 | **否决** |

---

## 3. 派单草案（批 A 后）

```
【派单·内容真债待批A·拟D13333·TIP-DB】深渊结算下一环菜单化（真可点 · ≠短征/团本/挑战settle同题 · ≠半行空转）
优先级：总控 TIP-DA CLOSED 后连开 · 内容乐趣 · docs-only→批后施工
请出 DESIGN-ember-abyss-settle-next-loop-visible-2026-10-14.md，STATUS=待批 A · 荐 M。
```

---

## 4. 链接

| 项 | 路径 |
|----|------|
| DESIGN | [`DESIGN-ember-abyss-settle-next-loop-visible-2026-10-14.md`](../design/DESIGN-ember-abyss-settle-next-loop-visible-2026-10-14.md) |
| RESEARCH | [`RESEARCH-ember-abyss-settle-next-loop-visible-2026-10-14.md`](../design/RESEARCH-ember-abyss-settle-next-loop-visible-2026-10-14.md) |
| 薄 tip | [`TIP-ember-abyss-settle-next-loop-visible-thin-d13333-2026-10-14.md`](../tips/TIP-ember-abyss-settle-next-loop-visible-thin-d13333-2026-10-14.md) |
| backlog | `B-abyss-settle-next-loop-visible` |
| 上游 | TIP-DA CLOSED live `8e12b6985` · feat `3d218ee83` · tip `d0f10a344` · PASS `020a8f2a7` · jar `1.65.825-d13332.local` · 批 sha `d0f10a344` · D13332；半行 REJECT `d00ed84bc` 保持否决；挑战结算下一环同题勿再交 |

---

*选题深渊结算下一环菜单化 · tip **待批 A** · 荐 M · L不荐 · 拟 D13333 · TIP-DB · 半行 REJECT `d00ed84bc` **保持否决** · ancestor TIP-DA CLOSED `8e12b6985` · 2026-10-14 09:33 Asia/Shanghai。*
