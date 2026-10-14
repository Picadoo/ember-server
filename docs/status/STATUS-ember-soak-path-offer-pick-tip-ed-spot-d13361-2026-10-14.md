# 状态 · TIP-ED 分摊路径选打菜单化抽测

> **抽测 PASS · tip 未勾 CLOSED（待总控收口）** · feat `4f9f34928` · tip批 `48f2c8226` · menu `653cfded7` · jar `1.65.854-d13361.local` · 2026-10-14 Asia/Shanghai · ancestor TIP-EC CLOSED live

**裁决：** **PASS** · 2026-10-14 18:41 Asia/Shanghai · feat `4f9f34928` · menu tip `653cfded7` · tip/批 `48f2c8226` · jar `1.65.854-d13361.local` · sha12 `756b536733dc` · 证据 `/workspace/tmp/tip-ed-soak-path/`  
**范围：** offerPick / 无参 soakpath / 周要约 → 开 `ember_p1_soak_path`（**禁** ConfirmTokens 教 soakpath 为主 UI）· 薄页自动/提醒/静默/清空/技能组/冒险/工坊真开 · applyAndReply 下一步走选打薄页 · PAPI `soak_path_*` 诚实 · Q01 灰态+亮态 · **零改**伤害/CD表/掉落/体力价 · **零改** `ember_p1_shell_path` / `ember_p1_clone_path` / press / cut / rush / clean / streak / blaze / spin / snap / veil / free 及其他已齐 path · **零改** D584 分摊「进圈」战斗钮语义 · **零**新建 `ember_p1_pace_path` · **≠**已齐选打同题复读 · **tip 勿勾 CLOSED**（总控收口）

## 必验

| # | 项 | 结果 | 证据 |
|---|----|------|------|
| 1 | 静态：菜单文件存在；零改 `ember_p1_shell_path` / clone / press / cut / rush / clean / streak / blaze / spin / snap / veil / free 及其他已齐 path | **PASS** | soak_path md5=`bd05aac511e9cc63739c8d7c41085bb4` · shell_path md5=`b651dc85ee91b81c5c916385795ee3cc`（TIP-EC 基线）· clone=`3f18eb69448d473777b380a66db24ad9` · press=`bf96dc003de213736e3bc11bbdf3d267` · cut=`10bf6f11a237df710d4c75fea48c845c` · rush=`aa203898e30ea7c8fd03fee2736baca9` · clean=`742d92edca840efbfaed1b6b7810a58e` · streak=`a4dee98b1f74a791db239cc80ecd1140` · blaze=`cf037a5fe2ea4a0cd422734dc13cfed9` · spin=`6e8484c868d57b23547b54d07aa7d0c9` · snap=`0eb745db720ac51c264c8a405b290312` · veil=`0f700173156e1608d5045a563edfec1f` · free=`8674c12a94f5ae9c3300647790155110` · dodge/sidestep/step/flex/stride/counter/pledge/trial 基线未变 · `10-menu-static` · `00-md5` · `V0_shell_path_untouched` · `V5_*` |
| 2 | jar/bv：live 1.65.854-d13361 · ECONOMY_BV 123 未抬 | **PASS** | jar sha256 `756b536733dc…` plugins=server-runtime · bv123 · `00-jar` · `V0_jar` · `V0_switches_pre` |
| 3 | 单测：`EmberSoakPathPickTest` 等绿 | **PASS** | SoakPathPick **8/8** · SoakPath 1/1 · ShellPathPick 8/8 · ClonePathPick 8/8 · Press/Cut/Rush/Clean/Streak/Blaze/Spin/Snap/Veil/Free/Dodge/Sidestep/Step/Flex/Stride/Counter/Pledge/Trial/RunPapi · `12-unit-*` · `V4_unit_path_pick_soak` · `V0_unit_mvn` |
| 4 | PAPI：q01 未通灰态 / 已通亮；`%corerpg_p1_soak_path%` / `_label` / `_tip` | **PASS** | path/label/tip/line 诚实 · Q01 锁→自动/提醒/静默/清空灰「需首通 Q01」· 通关亮态 · `20/21/30` · `V3_q01_gray` · `V2_papi_*` |
| 5 | offerPick / 无参 soakpath / 周要约 → 真开 `/ember_p1_soak_path`（非 ConfirmTokens 教裸 soakpath） | **PASS** | `offerPick`→`openPathPick` · 无参 soakpath live 开「分摊 · 路径」· nudge/周要约同开薄页 · glance「点分摊路径页」· **零** ConfirmTokens 教裸 soakpath（硬红门绿）· `12-unit-path-pick*` · `32-offer-noarg` · `33-weekly-offer` · `58-no-teach` · `V1_hard_red_*` |
| 6 | 点选 auto/ask/mute/clear + 技能组/冒险真开 | **PASS** | 1→auto · 2→ask · 3→mute · C clear · K→技能组 · A→冒险 · F→工坊 · `40–47` · `57-bright-reopen` |
| 7 | 局内「进圈」战斗钮仍 `/corerpg p1 soak`；「改路径」开薄页 | **PASS** | `[进圈]`仍→`/corerpg p1 soak` · 「改路径」→`/ember_p1_soak_path` · `V4_d584_combat_btn_intact` · `12-unit-path-pick` |
| 8 | 零抬伤害/CD表/掉落/体力价；零改 D584 分摊语义 | **PASS** | feat 仅 SoakPath/Command/Papi/薄页/单测 · 零 yml/Mythic 触碰 · top=30 · weekly_cap=3 · event_rate=0.85 · bv123 · `00-feat-zero-raise` · `70-intact` · `V5_*` |

## 旁注

- Stage2 三开 · daily_kills=2400 · ×0.97 · gate_daily=no · TrMenu **109**（含 `ember_p1_soak_path`）· jar sha256 `756b536733dc…` plugins=server-runtime · MySQL connected · bv123
- 单测 `EmberSoakPathPickTest` 8/8 / `EmberSoakPathTest` / ShellPathPick / ClonePathPick / PressPathPick / Cut/Rush/Clean/Streak/Blaze/Spin/Snap/Veil/Free/Dodge/Sidestep/Step/Flex/Stride/Counter/Pledge/Trial/RunPapi 绿
- **75/75 PASS** · **tip 勿勾 CLOSED**（总控收口）
- 选打主 UI = `ember_p1_soak_path`；未教裸 soakpath；D584 局内「进圈」战斗钮语义零改；未复读铁壁/分身/压伤等选打；未建 pace_path

## 纪律确认

six_slot enabled/migrate/set_bonus 仍 true · daily_kills=2400 · weekly_cap=3 · rot weekly_cap=3 · top cost=30 · SOFT_TARGET=3 · ×0.97 未改 · bv123 · 零改伤害/CD表/掉落/体力价/shell_path/clone_path/press_path/cut_path/rush_path/clean_path/streak_path/blaze_path/spin_path/snap_path/veil_path/free_path/dodge_path/sidestep_path/step_path/flex_path/stride_path/counter_path/pledge_path/trial_path/abyss·short·raid·featured·challenge path/variety/soft-reward · 零改 D584 分摊战斗钮 · 零新建 pace_path · 零 sx20 · 未改玩法数值 · 未切分支/stash/reset/force-push · 未碰 login/proxy/MariaDB

> **测岗抽测：** PASS live · feat `4f9f34928` · menu tip `653cfded7` · tip `48f2c8226` · jar `1.65.854-d13361.local` · **勿勾 CLOSED**（总控收口）
