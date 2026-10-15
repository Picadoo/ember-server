# 状态 · 下一档硬债选定 · 需策划（拟 D13385 · RallyPath.offerPick / applyAndReply 突进路径选打菜单化 · 选路径聊天钮去指令化 · docs-only · ≠破绽爆发/砸晶/护兔/占点路径选打同题 · ≠已齐选打同题 · ≠半行 · ≠抬伤害/CD表/掉落/体力价 · ≠全族 *Path.offerPick 大扫 · ≠Pace同窗 · ≠其它 *Path 同窗 · ≠复开 D562 玩法本体）

> **上游结案：** **TIP-FA CLOSED live**（D13384 破绽爆发路径选打 · APPROVED `d876a2dfe` · feat `fd9bcf3d5` · menu `d8b9676df` · jar `1.65.877-d13384.local` · PASS `c9047b97a` · spot 104/104 · TrMenu 132 · bv123 · **破绽爆发路径选打同题勿再交**）。

**日期：** 2026-10-15（上海时间）
**本窗性质：** need-design · docs-only · 拟 D13385 · TIP-FB · **APPROVED A·M（W1a 插件 + W1b 菜单）· L不批 · 勿勾 CLOSED**
**总控裁定（2026-10-15 16:25 Asia/Shanghai）：APPROVED A·M**（W1a+W1b · L不批 · 勿勾 CLOSED）· D13385 · TIP-FB。W1a 将 Rally 自有的**全部**玩家可见「突进」改「清房突进」：`EmberRallyPath` label L71–73、glance L85–87（并去手打 rallypath）、局内钮「[突进]」L133(maybeAfterClear)/L153(maybeProbe)→「[清房突进]」，以及复核补列同类 Rally 自有字 L126/L148「突进·自动 短加速」、L202、L209、L218；`EmberCommand` Rally 函数内 L4924/L4931/L4937/L4944/L4956–4957/L4966（L4937/L4944 改指向薄页 `ember_p1_rally_path`）。**仅改字**，按钮行为/命令目标/D562 语义零改；Stride/Posture/Step 文案一字不动；类名 `EmberRallyPath`/命令 id `rallypath`·`rally`/别名「突进路径」/菜单 id `ember_p1_rally_path` 不变。复核：Rally only · 无 Pace · 无大扫 · 祖先 FA CLOSED `46e138454` / PASS `c9047b97a` · 行号于 `bf56af838` 无偏移 · 旧名残留零。

**策划旁注（2026-10-15 16:20 Asia/Shanghai）：** 已交 [`DESIGN`](../design/DESIGN-ember-rally-path-offer-pick-visible-2026-10-15.md) · [`RESEARCH`](../design/RESEARCH-ember-rally-path-offer-pick-visible-2026-10-15.md) · 薄 tip [`TIP-ember-rally-path-offer-pick-visible-thin-d13385-2026-10-15.md`](../tips/TIP-ember-rally-path-offer-pick-visible-thin-d13385-2026-10-15.md) · backlog `B-rally-path-offer-pick-visible` · STATUS=待批 A · 荐 M（W1a+W1b）· L不批 · 勿勾 CLOSED · 玩家文案定名「清房突进」（W1a 改 label L71–73 + `EmberCommand` L4937/L4944 教手打，行号复核无偏移）· ancestor TIP-FA CLOSED `46e138454` / PASS `c9047b97a` · 半行 REJECT `d00ed84bc` 保持
**打开理由：** D563 Burst 刚 CLOSED；其下最高残留 D# = **EmberRallyPath D562**（突进），仍 ConfirmTokens 教裸 `rallypath`，TrMenu **无** `ember_p1_rally_path.yml`。次位 Thaw D561 / Duck D560 后置另窗。

## 1. 选题

| # | 候选 | 结论 | 理由 |
|---|------|------|------|
| 1 | Burst D563 / Smash D564 / Guard D565 / Hold D566 及以上 | **否** | 已菜单化 / CLOSED |
| 2 | PacePath | **禁** | 本仓无 · 禁默念 |
| 3 | 全族剩支（Thaw D561 及以下无菜单者）| **后置** | 禁全族大扫 · 另窗逐支 |
| 4 | **RallyPath.offerPick 选打菜单化（D562）** | **是** | 残留最高 D#；硬债 |

## 2. 证据（EmberRallyPath.java）

- `offerPick` L157–165：ConfirmTokens → `/corerpg p1 rallypath auto|ask|mute|clear`
- 周要约 / Q01 后要约同入口（L174）
- `applyAndReply` 下一步「[换一条]」→ `/corerpg p1 rallypath`（L222）
- glance L86：未选教手打 `/corerpg p1 rallypath`
- 局内「[改路径]」→ `/corerpg p1 rallypath`（L134/L154）
- 命令入口 `EmberCommand` L338 `rallypath` / 别名「突进路径」；L4937/L4944 亦教手打
- label：`突进·自动/提醒/静默`；tip：清房后短加速冲下一房
- TrMenu **无** `ember_p1_rally_path.yml`

## 3. ⚠ 玩家可见撞名（批时定名）

「突进」已被下列已齐路径/菜单使用：
- StridePath「突进踏」— `ember_p1_stride_path`（薄页标题/按钮）
- PosturePath「突进点名」（别名「突进」→ STRIKE）
- StepPath FORWARD lore「潜行+Q 向前突进」（`ember_p1_step_path`）

建议：RallyPath 玩家可见名改带限定（荐「清房突进」），薄页标题 + PAPI `_label`/`_line` 统一；Java 类/命令 id `rallypath`/菜单 id `ember_p1_rally_path` 不改。别名「突进路径」当前直达 rallypath，保留；勿给 Stride/Posture 改字。

## 4. 派单摘要（待批 A · 荐 M）

```
【派单·内容真债待批A·拟D13385·TIP-FB】RallyPath.offerPick/applyAndReply突进路径选打菜单化 → TrMenu ember_p1_rally_path（镜像 FA burst_path · 只钉 Rally 一支 · 禁 Pace · 禁全族大扫 · ≠复开D562 · 撞名「突进踏/突进点名」须区分）
请出 DESIGN-ember-rally-path-offer-pick-visible-2026-10-15.md + RESEARCH + 薄 tip，STATUS=待批 A · 荐 M（W1a 插件 + W1b 菜单）· L不批。
```

## 5. 交付索引

| 项 | 路径 |
|----|------|
| 本 STATUS | `docs/status/STATUS-ember-next-hard-debt-rally-path-offer-pick-need-design-2026-10-15.md` |
| DESIGN | `docs/design/DESIGN-ember-rally-path-offer-pick-visible-2026-10-15.md`（已交） |
| RESEARCH | `docs/design/RESEARCH-ember-rally-path-offer-pick-visible-2026-10-15.md`（已交） |
| 薄 tip | `docs/tips/TIP-ember-rally-path-offer-pick-visible-thin-d13385-2026-10-15.md`（已交） |
| backlog | `B-rally-path-offer-pick-visible` |
| 号段 | D13385 · TIP-FB · 玩法本体 D562 · menu id `ember_p1_rally_path` |
| 上游 | TIP-FA CLOSED |

## STATUS: **待批 A** · 荐 M · 拟 D13385 · TIP-FB · 2026-10-15 16:20 Asia/Shanghai · 勿勾 CLOSED · L不批 · 只钉 Rally 一支 · 禁 Pace 默念 · 禁全族大扫 · ancestor TIP-FA CLOSED
