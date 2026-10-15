# 状态 · 下一档硬债选定 · 需策划（拟 D13383 · SmashPath.offerPick / applyAndReply 砸晶路径选打菜单化 · 选路径聊天钮去指令化 · docs-only · ≠护兔/占点路径选打同题 · ≠已齐选打同题 · ≠半行 · ≠sx20 · ≠抬伤害/CD表/掉落/体力价 · ≠全族 *Path.offerPick 大扫 · ≠Pace同窗 · ≠其它 *Path 同窗 · ≠复开 D564 玩法本体）

> **上游结案：** **TIP-EY CLOSED live `8ac9b2ebc`**（D13382 护兔路径选打 · APPROVED `29ea1be01` · feat `22dc24411` · menu `a118e2ad6` · jar `1.65.875-d13382.local` · PASS `5ecfb6686` · spot 102/102 · TrMenu 130 · bv123 · **护兔路径选打同题勿再交**）。

**日期：** 2026-10-15（上海时间）
**本窗性质：** need-design · **APPROVED A · M（W1a 插件 + W1b 菜单）· L不批 · 勿勾 CLOSED** · 总控 2026-10-15 16:00 Asia/Shanghai · docs-only · 拟 D13383 · TIP-EZ · 勿勾 CLOSED
**策划旁注（2026-10-15 15:58 Asia/Shanghai）：** 已交 [`DESIGN`](../design/DESIGN-ember-smash-path-offer-pick-visible-2026-10-15.md) · [`RESEARCH`](../design/RESEARCH-ember-smash-path-offer-pick-visible-2026-10-15.md) · 薄 tip [`TIP-ember-smash-path-offer-pick-visible-thin-d13383-2026-10-15.md`](../tips/TIP-ember-smash-path-offer-pick-visible-thin-d13383-2026-10-15.md) · backlog `B-smash-path-offer-pick-visible` · STATUS=待批 A · 荐 M（W1a+W1b）· L不批 · 勿勾 CLOSED · ancestor TIP-EY CLOSED `8ac9b2ebc` / PASS `5ecfb6686` · 半行 REJECT `d00ed84bc` 保持
**打开理由：** 扫全部 `Ember*Path.offerPick`：D565–D600 已全部有 TrMenu `ember_p1_*_path`（Guard 刚 CLOSED）；Guard 之下最高残留 D# = **EmberSmashPath D564**（砸晶），仍 ConfirmTokens 教裸 `smashpath`。

## 1. 选题

| # | 候选 | 结论 | 理由 |
|---|------|------|------|
| 1 | Guard D565 / Hold D566 及 D567–D600 | **否** | 已菜单化 / CLOSED |
| 2 | PacePath | **禁** | 本仓无 · 禁默念 |
| 3 | 全族剩支（D467–D563 无菜单者）| **后置** | 禁全族大扫 · 另窗逐支 |
| 4 | **SmashPath.offerPick 选打菜单化（D564）** | **是** | 残留最高 D#；硬债 |

## 2. 证据（EmberSmashPath.java）

- `offerPick` L144–148：ConfirmTokens → `/corerpg p1 smashpath auto|ask|mute|clear`
- `applyAndReply` L203–205：下一步 ConfirmTokens，「[换一条]」→ `/corerpg p1 smashpath`
- glance L84：未选教手打 `/corerpg p1 smashpath`
- 局内砸晶钮 L113/L135 带「[改路径]」→ `/corerpg p1 smashpath`（≠选打页）
- `scheduleOfferAfterQ01` 同入口；TrMenu **无** `ember_p1_smash_path.yml`

## 3. 派单摘要（待批 A · 荐 M）

```
【派单·内容真债待批A·拟D13383·TIP-EZ】SmashPath.offerPick/applyAndReply砸晶路径选打菜单化 → TrMenu ember_p1_smash_path（镜像 EY guard_path · 只钉 Smash 一支 · 禁 Pace · 禁全族大扫 · ≠复开D564）
请出 DESIGN-ember-smash-path-offer-pick-visible-2026-10-15.md + RESEARCH + 薄 tip，STATUS=待批 A · 荐 M（W1a 插件 + W1b 菜单）· L不批。
```

## 4. 交付索引

| 项 | 路径 |
|----|------|
| 本 STATUS | `docs/status/STATUS-ember-next-hard-debt-smash-path-offer-pick-need-design-2026-10-15.md` |
| DESIGN | `docs/design/DESIGN-ember-smash-path-offer-pick-visible-2026-10-15.md` |
| RESEARCH | `docs/design/RESEARCH-ember-smash-path-offer-pick-visible-2026-10-15.md` |
| 薄 tip | `docs/tips/TIP-ember-smash-path-offer-pick-visible-thin-d13383-2026-10-15.md` |
| backlog | `B-smash-path-offer-pick-visible` |
| 号段 | D13383 · TIP-EZ · 玩法本体 D564 · menu id `ember_p1_smash_path` |
| 上游 | TIP-EY CLOSED `8ac9b2ebc` |

## STATUS: **APPROVED A · M（W1a 插件 + W1b 菜单）· L不批 · 勿勾 CLOSED** · 总控 2026-10-15 16:00 Asia/Shanghai · 拟 D13383 · TIP-EZ · 2026-10-15 15:58 Asia/Shanghai · 勿勾 CLOSED · 只钉 Smash 一支 · 禁 Pace · 禁全族大扫
