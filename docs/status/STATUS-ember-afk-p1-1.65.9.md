# STATUS · P1 挂机庭（D177，CoreRpg 1.65.9）— 2026-10-04

**状态：Stage 1 已上线**（1.65.9 18:23；18:35 起 1.65.10 接替并包含本功能）。设计 `docs/design/DESIGN-ember-afk-p1-2026-10-04.md`。

## 玩家看到什么（零命令）
- 主菜单 `/ember` 第 3 行第 1 格「挂机庭 · 忙时也有进度」（时钟）→ 挂机庭菜单：四层（未开放显示屏障和条件）、今日挂机进度、回枢纽。
- 首通 Q01 后可进。人在挂机庭待满 10 分钟自动到账一轮余烬币 + 经验；下线时间按 1/4 折算，上线补发（每天最多 6 轮）；每天最多 12 轮。
- 收益层：灰坡（Q01）8 币 + 3 经验 / 轮 → 荒原（Q03）12 + 5 → 焦土（Q05）16 + 6 → 烬原深处（Q07）25 + 10。

## 改了哪些
- 新 `p1/EmberAfkService.java`；`AfkTierService`（P1 下按首通开层、P1 文案、关升级提示）；`CoreRpgPlugin`（挂接、旧掉落 / 击杀奖励在挂机庭关闭、P1 关旧寄售）；
  `EmberRunService`（`grantRow` / `deliverQuiet`，PAPI `afk_*`）。
- `ember-v1.yml`（两份）`afk:` + `legacy_auction: false`；`balance_version` 32 → 33（两份 `ember-v1-runs.yml`）。
- TrMenu：新 `ember_p1_afk.yml`；`ember_hub.yml` 加 `Z` 格。
- `tools/p1sim/afk.py`（dyn / busy / p2econ）。

## 漏洞关闭（顺带）
- P1 下旧挂机怪不再发碎片 / 骨尘 / 核心（之前命令可达、每天 150 碎片，高于主动日约 84）。
- P1 下 `/corerpg ah` 关闭（跨号转币 / 核心的命令路径）。

## 冒烟
FreshQ145：见 `RELEASE-ember-1.65.9.md`。下次测试号：**FreshQ146**+（注意 mainline 用 FreshQ150+）。

## 模拟
- 21 格：21/21（1600 人复测，balance_version 33）。忙碌玩家 + 挂机仍远慢于活跃玩家。
- W30：share 0.5 −0.24 周 ✔；上界 share 1.0 −0.56 周（超 ±0.5 0.06）→ Stage 1.1。

## 后续
- **Stage 1.1（下一步，只改配置）**：每轮币降到 C（每日 60/84/120/180）或 H（48/72/96/144）档，看 `/tmp/afk/p2b-*.md`（设计 §7.3）。
- Stage 2 候选（各自先过 p1sim）：通关休息加成（只加币）、T2–T4 安全台、ActionBar 计时。
- Stage 3：D174 专属装落地后复跑 `afk.py`。
- 「D63 旧命令入口全面审查」：其它隐藏旧系统（旧日常 / 精英等）可能仍发 P1 同名材料。
