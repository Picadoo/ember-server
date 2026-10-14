# 状态 · TIP-EB 分身路径选打菜单化抽测

> **抽测 PASS · tip 未勾 CLOSED（待总控收口）** · feat `fdd409b97` · tip批 `3e31ae7c8` · menu `938e20faa` · jar `1.65.852-d13359.local` · 2026-10-14 Asia/Shanghai · ancestor TIP-EA CLOSED live

**裁决：** **PASS** · 2026-10-14 18:02 Asia/Shanghai · feat `fdd409b97` · menu tip `938e20faa` · tip/批 `3e31ae7c8` · jar `1.65.852-d13359.local` · sha12 `f36f3128b8b6` · 证据 `/workspace/tmp/tip-eb-clone-path/`  
**范围：** offerPick / 无参 clonepath / 周要约 → 开 `ember_p1_clone_path`（**禁** ConfirmTokens 教 clonepath 为主 UI）· 薄页自动/提醒/静默/清空/技能组/冒险/工坊真开 · applyAndReply 下一步走选打薄页 · PAPI `clone_path_*` 诚实 · Q01 灰态+亮态 · **零改**伤害/CD表/掉落/体力价 · **零改** `ember_p1_press_path` / `ember_p1_cut_path` / rush / clean / streak / blaze / spin / snap / veil / free 及其他已齐 path · **零改** D582 分身「清分身」战斗钮语义 · **零**新建 `ember_p1_pace_path` · **≠**已齐选打同题复读 · **tip 勿勾 CLOSED**（总控收口）

## 必验

| # | 项 | 结果 | 证据 |
|---|----|------|------|
| 1 | 静态：菜单文件存在；零改 `ember_p1_press_path` / cut / rush / clean / streak / blaze / spin / snap / veil / free 及其他已齐 path | **PASS** | clone_path md5=`3f18eb69448d473777b380a66db24ad9` · press_path md5=`bf96dc003de213736e3bc11bbdf3d267`（TIP-EA 基线）· cut=`10bf6f11a237df710d4c75fea48c845c` · rush=`aa203898e30ea7c8fd03fee2736baca9` · clean=`742d92edca840efbfaed1b6b7810a58e` · streak=`a4dee98b1f74a791db239cc80ecd1140` · blaze=`cf037a5fe2ea4a0cd422734dc13cfed9` · spin=`6e8484c868d57b23547b54d07aa7d0c9` · snap=`0eb745db720ac51c264c8a405b290312` · veil=`0f700173156e1608d5045a563edfec1f` · free=`8674c12a94f5ae9c3300647790155110` · dodge/sidestep/step/flex/stride/counter/pledge/trial 基线未变 · `10-menu-static` · `00-md5` · `V0_press_path_untouched` · `V5_*` |
| 2 | jar/bv：live 1.65.852-d13359 · ECONOMY_BV 123 未抬 | **PASS** | jar sha256 `f36f3128b8b6…` plugins=server-runtime · bv123 · `00-jar` · `V0_jar` · `V0_switches_pre` |
| 3 | 单测：`EmberClonePathPickTest` 等绿 | **PASS** | ClonePathPick **8/8** · ClonePath 1/1 · PressPathPick 8/8 · CutPathPick · Rush/Clean/Streak/Blaze/Spin/Snap/Veil/Free/Dodge/Sidestep/Step/Flex/Stride/Counter/Pledge/Trial/RunPapi · `12-unit-*` · `V4_unit_path_pick_clone` |
| 4 | PAPI：q01 未通灰态 / 已通亮；`%corerpg_p1_clone_path%` / `_label` / `_tip` | **PASS** | path/label/tip/line 诚实 · Q01 锁→自动/提醒/静默/清空灰「需首通 Q01」· 通关亮态 · `20/21/30` · `V3_q01_gray` · `V2_papi_*` |
| 5 | offerPick / 无参 clonepath / 周要约 → 真开 `/ember_p1_clone_path`（非 ConfirmTokens 教裸 clonepath） | **PASS** | `offerPick`→`openPathPick` · 无参 clonepath live 开「分身 · 路径」· nudge/周要约同开薄页 · glance「点分身路径页」· **零** ConfirmTokens 教裸 clonepath（硬红门绿）· `12-unit-path-pick*` · `32-offer-noarg` · `33-weekly-offer` · `58-no-teach` · `V1_hard_red_*` |
| 6 | 点选 auto/ask/mute/clear + 技能组/冒险真开 | **PASS** | 1→auto · 2→ask · 3→mute · C clear · K→技能组 · A→冒险 · F→工坊 · `40–47` · `57-bright-reopen` |
| 7 | 局内「清分身」战斗钮仍 `/corerpg p1 clone`；「改路径」开薄页 | **PASS** | `[清分身]`仍→`/corerpg p1 clone` · 「改路径」→`/ember_p1_clone_path` · `V4_d582_combat_btn_intact` · `12-unit-path-pick` |
| 8 | 零抬伤害/CD表/掉落/体力价；零改 D582 分身语义 | **PASS** | feat 仅 ClonePath/Command/Papi/薄页/单测 · 零 yml/Mythic 触碰 · top=30 · weekly_cap=3 · event_rate=0.85 · bv123 · `00-feat-zero-raise` · `70-intact` · `V5_*` |

## 旁注

- Stage2 三开 · daily_kills=2400 · ×0.97 · gate_daily=no · TrMenu **107**（含 `ember_p1_clone_path`）· jar sha256 `f36f3128b8b6…` plugins=server-runtime · MySQL connected · bv123
- 单测 `EmberClonePathPickTest` 8/8 / `EmberClonePathTest` / PressPathPick / CutPathPick / RushPathPick / CleanPathPick / Streak/Blaze/Spin/Snap/Veil/Free/Dodge/Sidestep/Step/Flex/Stride/Counter/Pledge/Trial/RunPapi 绿
- **73/73 PASS** · **tip 勿勾 CLOSED**（总控收口）
- 选打主 UI = `ember_p1_clone_path`；未教裸 clonepath；D582 局内「清分身」战斗钮语义零改；未复读压伤/打断/限时等选打；未建 pace_path

## 纪律确认

six_slot enabled/migrate/set_bonus 仍 true · daily_kills=2400 · weekly_cap=3 · rot weekly_cap=3 · top cost=30 · SOFT_TARGET=3 · ×0.97 未改 · bv123 · 零改伤害/CD表/掉落/体力价/press_path/cut_path/rush_path/clean_path/streak_path/blaze_path/spin_path/snap_path/veil_path/free_path/dodge_path/sidestep_path/step_path/flex_path/stride_path/counter_path/pledge_path/trial_path/abyss·short·raid·featured·challenge path/variety/soft-reward · 零改 D582 分身战斗钮 · 零新建 pace_path · 零 sx20 · 未改玩法数值 · 未切分支/stash/reset/force-push · 未碰 login/proxy/MariaDB

> **测岗抽测：** PASS live · feat `fdd409b97` · menu tip `938e20faa` · tip `3e31ae7c8` · jar `1.65.852-d13359.local` · **勿勾 CLOSED**（总控收口）
