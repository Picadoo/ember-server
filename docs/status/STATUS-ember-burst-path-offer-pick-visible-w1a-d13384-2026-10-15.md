# STATUS · TIP-FA W1a 破绽爆发路径选打菜单化 · D13384

> **CLOSED live（2026-10-15 16:20 Asia/Shanghai）：** APPROVED `d876a2dfe` · feat `fd9bcf3d5` · menu `d8b9676df` · jar `1.65.877-d13384.local` · PASS `c9047b97a` · spot **104/104** · TrMenu **132** · bv123 · 玩家可见名「破绽爆发」（≠ CounterPath「破绽反击」）· 证据 `/workspace/tmp/tip-fa-burst-path/` · 破绽爆发路径选打同题勿再交 · reload duplicate keys WARN=0（B-trmenu-short-dup-keys `c259724f8` 生效）· 下一支 TIP-FB / D13385 RallyPath（突进）need-design 待批

> 2026-10-15 Asia/Shanghai · **W1a 已交** · tip **勿勾 CLOSED** · APPROVED A·M `d876a2dfe` · jar `1.65.877-d13384.local`

## 交付
- `EmberBurstPath.offerPick` →开 `/ember_p1_burst_path`（`openPathPick` / `PATH_PICK_*`）；**禁** ConfirmTokens 教 `burstpath` 为主 UI
- 无参 `/corerpg p1 burstpath` + 周要约 / Q01 后要约 →同薄页；`applyAndReply`「换一条」→薄页 + 冒险
- 局内「改路径」×2 →薄页；glance / 用法清手打 burstpath
- 玩家文案「破绽爆发·自动/提醒/静默」「破绽爆发路径」；类名/命令 id 不改；CounterPath「破绽反击」未动（另加别名 `破绽爆发路径`，旧别名保留）
- 局内「[爆发]」`/corerpg p1 burst`、maybeAfterStagger / runs.pathBurst 语义 **零改**
- PAPI：`%corerpg_p1_burst_path%`/`_label`/`_tip`/`_line`
- 单测：BurstPathPick 8 · BurstPath 1 · RunPapi 5 · SmashPathPick 8 · CounterPath* 11 绿
- ECONOMY_BV **123**；零改伤害/CD/掉落/体力价/D563/其它 *Path；TrMenu yml 未动（W1b 物品岗）

## 证据
- `/workspace/tmp/tip-fa-burst-path/`
