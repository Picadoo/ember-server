# 状态 · TIP-EI 闪线路径选打菜单化抽测

> **抽测 PASS · tip 未勾 CLOSED（待总控收口）** · feat `cb970b73c` · tip批 `6d6be8c54` · menu `8638c526f` · jar `1.65.859-d13366.local` · 2026-10-14 Asia/Shanghai · ancestor TIP-EH CLOSED live `e4bccf93b`

**裁决：** **PASS** · 2026-10-14 Asia/Shanghai · feat `cb970b73c` · menu tip `8638c526f` · tip/批 `6d6be8c54` · jar `1.65.859-d13366.local` · sha12 `deac77e06775` · 证据 `/workspace/tmp/tip-ei-bolt-path/`  
**范围：** offerPick / 无参 boltpath / 周要约 → 开 `ember_p1_bolt_path`（**禁** ConfirmTokens 教 boltpath 为主 UI）· 薄页自动/提醒/静默/清空/技能组/冒险/工坊真开 · applyAndReply 下一步走选打薄页 · PAPI `bolt_path_*` 诚实 · Q01 灰态+亮态 · **零改**伤害/CD表/掉落/体力价 · **零改** `ember_p1_shift_path` / gap / phase / wave / soak / shell / clone / press / cut / rush / clean / streak / blaze / spin / snap / veil / free 及其他已齐 path · **零改** D589 闪线「闪线」战斗钮语义 · **零**新建 `ember_p1_pace_path` · **≠**已齐选打同题复读 · **tip 勿勾 CLOSED**（总控收口）

## 必验

| # | 项 | 结果 | 证据 |
|---|----|------|------|
| 1 | 静态：菜单文件存在；零改 `ember_p1_shift_path` / gap / phase / wave / soak / shell / clone / press / cut / rush / clean / streak / blaze / spin / snap / veil / free 及其他已齐 path | **PASS** | bolt_path md5=`0ff6058fccfd32d0f48c628bca74c557` · shift_path md5=`eec992760b0892ec9d2df3958420185e`（TIP-EH 基线）· gap=`9602bae7717dcf218dc651f67a528a24` · phase=`b37b4dec11bfa8217d84403f9b13d4cd` · wave/soak/shell/clone/press/cut/rush/clean/streak/blaze/spin/snap/veil/free/dodge/sidestep/step/flex/stride/counter/pledge/trial 基线未变 · `10-menu-static` · `00-md5` · `V0_shift_path_untouched` · `V5_*` |
| 2 | jar/bv：live 1.65.859-d13366 · ECONOMY_BV 123 未抬 | **PASS** | jar sha256 `deac77e06775…` plugins=server-runtime · bv123 · `00-jar` · `V0_jar` · `V0_switches_pre` |
| 3 | 单测：`EmberBoltPathPickTest` 等绿 | **PASS** | BoltPathPick **8/8** · BoltPath · ShiftPathPick · ShiftPath · GapPathPick · GapPath · Wave/Soak/Shell/Clone/Press/Cut/Rush/Clean/Streak/Blaze/Spin/Snap/Veil/Free/Dodge/Sidestep/Step/Flex/Stride/Counter/Pledge/Trial/RunPapi · `12-unit-*` · `V4_unit_path_pick_bolt` · `V0_unit_mvn` |
| 4 | PAPI：q01 未通灰态 / 已通亮；`%corerpg_p1_bolt_path%` / `_label` / `_tip` | **PASS** | path/label/tip/line 诚实 · Q01 锁→自动/提醒/静默/清空灰「需首通 Q01」· 通关亮态 · `20/21/30` · `V3_q01_gray` · `V2_papi_*` |
| 5 | offerPick / 无参 boltpath / 周要约 → 真开 `/ember_p1_bolt_path`（非 ConfirmTokens 教裸 boltpath） | **PASS** | `offerPick`→`openPathPick` · 无参 boltpath live 开「闪线 · 路径」· nudge/周要约同开薄页 · glance「点闪线路径页」· **零** ConfirmTokens 教裸 boltpath（硬红门绿）· `12-unit-path-pick*` · `32-offer-noarg` · `33-weekly-offer` · `58-no-teach` · `V1_hard_red_*` |
| 6 | 点选 auto/ask/mute/clear + 技能组/冒险真开 | **PASS** | 1→auto · 2→ask · 3→mute · C clear · K→技能组 · A→冒险 · F→工坊 · `40–47` · `57-bright-reopen` |
| 7 | 局内「闪线」战斗钮仍 `/corerpg p1 bolt`；「改路径」开薄页 | **PASS** | `[闪线]`仍→`/corerpg p1 bolt` · 「改路径」→`/ember_p1_bolt_path` · `V4_d589_combat_btn_intact` · `12-unit-path-pick` |
| 8 | 零抬伤害/CD表/掉落/体力价；零改 D589 闪线语义 | **PASS** | feat 仅 BoltPath/Command/Papi/薄页/单测 · 零 yml/Mythic 触碰 · top=30 · weekly_cap=3 · event_rate=0.85 · bv123 · `00-feat-zero-raise` · `70-intact` · `V5_*` |

## 旁注

- Stage2 三开 · daily_kills=2400 · ×0.97 · gate_daily=no · TrMenu **114**（含 `ember_p1_bolt_path`）· jar sha256 `deac77e06775…` plugins=server-runtime · MySQL connected · bv123
- 单测 `EmberBoltPathPickTest` 8/8 / `EmberBoltPathTest` / ShiftPathPick 等绿
- **86/86 PASS** · **tip 勿勾 CLOSED**（总控收口）
- 选打主 UI = `ember_p1_bolt_path`；未教裸 boltpath；D589 局内「闪线」战斗钮语义零改；未复读侧安/收招等选打；未建 pace_path

## 纪律确认

six_slot enabled/migrate/set_bonus 仍 true · daily_kills=2400 · weekly_cap=3 · rot weekly_cap=3 · top cost=30 · SOFT_TARGET=3 · ×0.97 未改 · bv123 · 零改伤害/CD表/掉落/体力价/shift_path/gap_path/phase_path/wave_path/soak_path/shell_path/clone_path/press_path/cut_path/rush_path/clean_path/streak_path/blaze_path/spin_path/snap_path/veil_path/free_path/dodge_path/sidestep_path/step_path/flex_path/stride_path/counter_path/pledge_path/trial_path/abyss·short·raid·featured·challenge path/variety/soft-reward · 零改 D589 闪线战斗钮 · 零新建 pace_path · 零 sx20 · 未改玩法数值 · 未切分支/stash/reset/force-push · 未碰 login/proxy/MariaDB
