# 状态 · 下一档硬债选定 · 需策划（拟 D13328 · 短征结算「下一环」菜单化 · settle 聊天钮去指令化 · docs-only · ≠路径追同题 · ≠日差同题 · ≠今日推/周目标复读 · ≠半行 · ≠sx20 · ≠抬日表 · ≠庭内旁路 · ≠Cast/Aid/variety再拧）

> **旁注（勾毕 · CLOSED live · feat `8c2dc7d95` · tip `374a142fd` · PASS `72e8e6a4e` · jar `1.65.821-d13328b.local` · 批 sha `e73f5847e` · 2026-10-14 07:47 Asia/Shanghai · 结算下一环同题勿再交）：** tip **CLOSED live** · DESIGN [`DESIGN-ember-short-settle-next-loop-visible-2026-10-14.md`](../design/DESIGN-ember-short-settle-next-loop-visible-2026-10-14.md) · RESEARCH [`RESEARCH-ember-short-settle-next-loop-visible-2026-10-14.md`](../design/RESEARCH-ember-short-settle-next-loop-visible-2026-10-14.md) · backlog `B-short-settle-next-loop-visible` · **TIP-CW** · **≠关观察 ≠抬日表 ≠sx20 ≠半行独批 ≠Cast/Aid/variety再拧 ≠路径追同题 ≠日差同题 ≠庭内旁路再拧 ≠教裸指令 ≠densify ≠改 resolve 算法**。

> **上游结案：** **TIP-CV CLOSED** live `f3defd0bb`（D13327 短征选页「路径追」齐 · feat `c31c780a9` · tip `a206b9893` · jar `1.65.819-d13327.local` · PASS `f9e4ca402` · 批 sha `ac2889160` · **路径追同题勿再交**）。总控连开：优先**强烈偏向短征日更/乐趣**（真可点或房内可感）；庭内工坊旁路已近饱和（默认勿再交）；宝石须庭面 gap；日差软目标发奖须另批。Cast+BossAid+EliteAid+todayRot+variety(W1a–d)+庭内全弧+今日推/冲一下/周短征/三冲+日差本可冲+路径追 **已齐勿复读**。半行 REJECT `d00ed84bc` **保持否决**。

**日期：** 2026-10-14（上海时间）
**本窗性质：** tip **CLOSED live** · feat `8c2dc7d95` · tip `374a142fd` · PASS `72e8e6a4e` · jar `1.65.821-d13328b.local` · D13328 · TIP-CW · 2026-10-14 07:47 Asia/Shanghai
**硬规格：** [`DESIGN-ember-short-settle-next-loop-visible-2026-10-14.md`](../design/DESIGN-ember-short-settle-next-loop-visible-2026-10-14.md) · backlog `B-short-settle-next-loop-visible`
**打开理由：** 路径追已菜单化后，短征有奖 settle 仍用 ConfirmTokens 教 `/corerpg p1 shortpath` / `/corerpg p1 forge`，并有裸指令文案；日更「通关→下一环」乐趣断在聊天钮去指令化。

---

## 0. 局势一句话

短征通关后补「下一环」薄菜单——再进/回选页/路径追/工坊一点开；**不**再交路径追同题，**不**抬日帽。

---

## 1. 选题

| # | 候选 | 裁决 | 理由（证据） |
|---|------|------|--------------|
| **1** | **短征结算下一环菜单化** | **采纳 · 需策划** | E1–E9；见 §1.1 |
| 2 | Cast/Aid/todayRot/W1e/variety 再拧 / 完整 variety 经济 | **否** | 硬禁；刚 CLOSED |
| 3 | 庭内旁路再拧 / 宝石 | **否** | 硬禁 / gap 未亮 |
| 4 | 路径追同题 / 改 resolve | **否** | 派单禁；已 CLOSED |
| 5 | 日差同题 / 软目标发奖 | **否** | 派单禁；经济另批 |
| 6 | 今日推 / 周目标 / 三冲 / D406 复读 | **否** | 已齐勿复读 |
| 7 | 只 Open「结算见聊天」半行 | **否** | REJECT 保持 |
| 8 | sx20 / 抬表 / densify / 灾厄 / Pack6 | **否** | 硬禁 |

### 1.1 证据核验（实扫 · 2026-10-14）

| # | 证据 | 出处 | 含义 |
|---|------|------|------|
| E1 | 总控 TIP-CV CLOSED 后连开；强烈偏向短征日更/乐趣；路径追同题勿再交；庭内默认勿再交 | 派单 · live `f3defd0bb` | 选题边界 |
| E2 | D13327§6⑥ 明文后置「settle 聊天钮去指令化大扫」 | DESIGN-path-chase §6 | 合法未 CLOSED 后置 |
| E3 | `EmberShortService.settle` 有奖支：ConfirmTokens `[再进]`→`/corerpg p1 enter sx` · `[工坊]`→`/corerpg p1 forge` · `[短征路径]`→`/corerpg p1 shortpath` | ShortService.java L135–144 | **结算仍教 chat 指令轨** |
| E4 | 首通下一本文案教 `/corerpg p1 shortpath day`；`spendChaseButtonCmd=/corerpg p1 forge` | ShortService L160 · ShortRules L228 | **裸指令文案硬证** |
| E5 | 路径追已有 `ember_p1_short_path` + `enter short-path`；结算「短征路径」钮仍开 chat shortpath | D13327 live · ShortService | **菜单齐但结算未接** |
| E6 | 无 `ember_p1_short_settle` 薄页；todayRot settleCue 仍 chat 钮 `enter short-today` | TrMenu ls · TodayRotFeel L89–96 | 缺结算聚合页 |
| E7 | Cast+Aid+todayRot+variety+庭内+日差+路径追齐 | D13304–27 | 勿复读主交 |
| E8 | **为何不是半行：** 主交付=薄 settle 页真开菜单 + 插件改下一环入口 | 本 STATUS | 过硬禁 |
| E9 | 半行 REJECT `d00ed84bc` 保持 | backlog | 否决只口号 |

---

## 2. 荐方案

| 方案 | 一行 | 本窗 |
|------|------|------|
| **M（荐）** | 插件 settle 下一环改开薄页/菜单命令 + 薄页再进/选页/路径/工坊 + 清裸指令文案；零抬日帽/S/算法 | **荐** |
| L | 仅改一条聊天钮文案/目标命令；无薄页聚合 | **默认不批** |
| A | 只半行 | **否决**（REJECT） |
| W | 抬表/sx20/路径追同题/日差同题/庭内/Cast·Aid·variety再拧 | **否决** |

---

## 3. 派单草案（批 A 后）

```
【派单·内容真债待批A·拟D13328·TIP-CW】短征结算「下一环」菜单化（settle 聊天钮去指令化 · ≠路径追同题 · ≠日差同题 · ≠半行空转 · ≠庭内旁路 · ≠抬表）
优先级：总控 TIP-CV CLOSED 后连开 · 强烈偏向短征日更 · docs-only
请出 DESIGN-ember-short-settle-next-loop-visible-2026-10-14.md，STATUS=待批 A · 荐 M。
```

---

## 4. 链接

| 项 | 路径 |
|----|------|
| DESIGN | [`DESIGN-ember-short-settle-next-loop-visible-2026-10-14.md`](../design/DESIGN-ember-short-settle-next-loop-visible-2026-10-14.md) |
| RESEARCH | [`RESEARCH-ember-short-settle-next-loop-visible-2026-10-14.md`](../design/RESEARCH-ember-short-settle-next-loop-visible-2026-10-14.md) |
| 薄 tip | [`TIP-ember-short-settle-next-loop-visible-thin-d13328-2026-10-14.md`](../tips/TIP-ember-short-settle-next-loop-visible-thin-d13328-2026-10-14.md) |
| backlog | `B-short-settle-next-loop-visible` |
| 上游 | TIP-CV CLOSED live `f3defd0bb` · feat `c31c780a9` · tip `a206b9893` · PASS `f9e4ca402` · 批 sha `ac2889160` · D13327；半行 REJECT `d00ed84bc` 保持否决 |

---

*选题短征结算下一环菜单化 · tip **CLOSED live** · `8c2dc7d95` / `374a142fd` · PASS `72e8e6a4e` · D13328 · TIP-CW · 半行 REJECT `d00ed84bc` **保持否决** · 结算下一环同题勿再交。*
