# 状态 · 下一档硬债选定 · 需策划（拟 D13384 · BurstPath.offerPick / applyAndReply 破绽路径选打菜单化 · 选路径聊天钮去指令化 · docs-only · ≠砸晶/护兔/占点路径选打同题 · ≠已齐选打同题 · ≠半行 · ≠抬伤害/CD表/掉落/体力价 · ≠全族 *Path.offerPick 大扫 · ≠Pace同窗 · ≠其它 *Path 同窗 · ≠复开 D563 玩法本体）

> **CLOSED live（2026-10-15 16:20 Asia/Shanghai）：** APPROVED `d876a2dfe` · feat `fd9bcf3d5` · menu `d8b9676df` · jar `1.65.877-d13384.local` · PASS `c9047b97a` · spot **104/104** · TrMenu **132** · bv123 · 玩家可见名「破绽爆发」（≠ CounterPath「破绽反击」）· 证据 `/workspace/tmp/tip-fa-burst-path/` · 破绽爆发路径选打同题勿再交 · reload duplicate keys WARN=0（B-trmenu-short-dup-keys `c259724f8` 生效）· 下一支 TIP-FB / D13385 RallyPath（突进）need-design 待批

> **上游结案：** **TIP-EZ CLOSED live**（D13383 砸晶路径选打 · APPROVED `156e3572a` · feat `26423759a` · menu `caa9e282a` · jar `1.65.876-d13383.local` · PASS `b6b79b7a1` · spot 103/103 · TrMenu 131 · bv123 · **砸晶路径选打同题勿再交**）。

**日期：** 2026-10-15（上海时间）
**本窗性质：** need-design · docs-only · 拟 D13384 · TIP-FA · **待批 A · 荐 M（W1a 插件 + W1b 菜单）· L不批 · 勿勾 CLOSED**
**总控批注（2026-10-15 16:12 Asia/Shanghai）：** **APPROVED A·M**（W1a+W1b · L不批 · 勿勾 CLOSED）· tip `2a1678317` · 命名：BurstPath 玩家可见名「破绽爆发」（薄页标题 + PAPI _label），CounterPath 保持「破绽反击」；Java 类/命令 id 不改 · 派 余烬-插件 W1a + 余烬-物品 W1b

**策划旁注（2026-10-15 16:04 Asia/Shanghai）：** 已交 [`DESIGN`](../design/DESIGN-ember-burst-path-offer-pick-visible-2026-10-15.md) · [`RESEARCH`](../design/RESEARCH-ember-burst-path-offer-pick-visible-2026-10-15.md) · 薄 tip [`TIP-ember-burst-path-offer-pick-visible-thin-d13384-2026-10-15.md`](../tips/TIP-ember-burst-path-offer-pick-visible-thin-d13384-2026-10-15.md) · backlog `B-burst-path-offer-pick-visible` · STATUS=待批 A · 荐 M（W1a+W1b）· L不批 · 勿勾 CLOSED · ancestor TIP-EZ CLOSED `5be145227` / PASS `b6b79b7a1` · 半行 REJECT `d00ed84bc` 保持 · 命名注：≠ TIP-DL CounterPath「破绽反击」
**打开理由：** 扫 `Ember*Path.offerPick` 无 TrMenu `ember_p1_*_path.yml` 者：D564 Smash 刚 CLOSED；其下最高残留 D# = **EmberBurstPath D563**（破绽），仍 ConfirmTokens 教裸 `burstpath`。次位 Rally D562 / Thaw D561 / Duck D560 后置另窗。

## 1. 选题

| # | 候选 | 结论 | 理由 |
|---|------|------|------|
| 1 | Smash D564 / Guard D565 / Hold D566 及 D567–D600 | **否** | 已菜单化 / CLOSED |
| 2 | PacePath | **禁** | 本仓无 · 禁默念 |
| 3 | 全族剩支（Rally D562 及以下无菜单者）| **后置** | 禁全族大扫 · 另窗逐支 |
| 4 | **BurstPath.offerPick 选打菜单化（D563）** | **是** | 残留最高 D#；硬债 |

## 2. 证据（EmberBurstPath.java）

- `offerPick` L146–154：ConfirmTokens → `/corerpg p1 burstpath auto|ask|mute|clear`
- `maybeOfferWeekly` / `scheduleOfferAfterQ01` 同入口（L157–176）
- `applyAndReply` L180+：下一步 ConfirmTokens，「[换一条]」→ `/corerpg p1 burstpath`（L211）
- glance L86：未选教手打 `/corerpg p1 burstpath`
- 局内破绽钮 `maybeAfterStagger`（L100）/ `runs.pathBurst` 带「[改路径]」→ `/corerpg p1 burstpath`（L123/L143，≠选打页）
- 命令入口 `EmberCommand` L336 `burstpath`；TrMenu **无** `ember_p1_burst_path.yml`

## 3. 派单摘要（待批 A · 荐 M）

```
【派单·内容真债待批A·拟D13384·TIP-FA】BurstPath.offerPick/applyAndReply破绽路径选打菜单化 → TrMenu ember_p1_burst_path（镜像 EZ smash_path · 只钉 Burst 一支 · 禁 Pace · 禁全族大扫 · ≠复开D563）
请出 DESIGN-ember-burst-path-offer-pick-visible-2026-10-15.md + RESEARCH + 薄 tip，STATUS=待批 A · 荐 M（W1a 插件 + W1b 菜单）· L不批。
```

## 4. 交付索引

| 项 | 路径 |
|----|------|
| 本 STATUS | `docs/status/STATUS-ember-next-hard-debt-burst-path-offer-pick-need-design-2026-10-15.md` |
| DESIGN | `docs/design/DESIGN-ember-burst-path-offer-pick-visible-2026-10-15.md` |
| RESEARCH | `docs/design/RESEARCH-ember-burst-path-offer-pick-visible-2026-10-15.md` |
| 薄 tip | `docs/tips/TIP-ember-burst-path-offer-pick-visible-thin-d13384-2026-10-15.md` |
| backlog | `B-burst-path-offer-pick-visible` |
| 号段 | D13384 · TIP-FA · 玩法本体 D563 · menu id `ember_p1_burst_path` |
| 上游 | TIP-EZ CLOSED |

## STATUS: **待批 A** · 荐 M · 拟 D13384 · TIP-FA · 2026-10-15 16:10 Asia/Shanghai · 勿勾 CLOSED · L不批 · 只钉 Burst 一支 · 禁 Pace 默念 · 禁全族大扫 · ancestor TIP-EZ CLOSED
