# STATUS · P1 每日签到 + 在线时长（D180，CoreRpg 1.65.16）— 2026-10-04

**状态：Stage 1 已上线**（见 `docs/status/RELEASE-ember-1.65.16.md`）。研究 `docs/design/RESEARCH-ember-signin-online-2026-10-04.md` ·
设计 `docs/design/DESIGN-ember-signin-online-2026-10-04.md` · 协调 `/workspace/COORD-signin-online.txt`。

## 玩家看到什么（零命令）
- 进服 5 秒后（今天没签时）：「[签到] 今天还没签到 · 本月第 n 次：…… [去签到]」，点按钮打开签到页。
- 主菜单 `/ember` 第 4 行第 1 格「签到 · 在线」（绿宝石，今天没签时闪光）→ `ember_p1_sign`：
  28 格月历（按本月第 n 次签到发奖，漏签不清零；今天那格闪光，点了就签）、补签按钮、在线 15 / 30 / 60 / 120 分钟四档、一键领取、返回。
- 有效在线满一档时聊天栏「可领 …… [去领取]」；没领的档过 0 点自动补发。

## 发什么（`ember-v1.yml signin:` / `online:`，两份一致）
| 来源 | 奖励 |
|---|---|
| 签到第 1–6 / 8–13 / 15–20 / 22–27 次 | 余烬币 20 + 经验 5 |
| 第 7 / 21 次 | 余烬币 40 + 首领徽记 ×1（已首通最靠后的签名图；都没首通 → 60 币） |
| 第 14 / 28 次 | 余烬币 40 / 80 + 锻造印记 ×1（已首通最高阶） |
| 第 29–31 次 | 余烬币 10 + 经验 5 |
| 在线 15 / 30 / 60 / 120 分钟 | 10 币 / 15 币 + 5 经验 / 20 币 + 5 经验 / 25 币 + 10 经验 |
| 上限 | 签满一月 700 币 + 130 经验 + 2 徽记 + 2 印记；在线每天 70 币 + 20 经验 |

补签：每月 3 次、每天 1 次，要先签今天并且今天有效在线满 60 分钟，自动补最早漏签的一天。

## 改了哪些
- 新 `p1/EmberSignService.java`（计数、计时、领取、补签、进服提示、PAPI、旧命令 P1 改道）；`EmberCommand`（`/corerpg p1 sign|online`）；
  `EmberRunService`（PAPI `sign_*` / `online_*`）；`CoreRpgPlugin`（挂接；P1 下旧 `/corerpg sign` → P1 签到，`activity` / `bounty` 不再发旧币）。
- `ember-v1.yml`（两份）末尾 `signin:` + `online:`；`balance_version` → 39。
- TrMenu：新 `ember_p1_sign.yml`（生成器 `tools/menus/gen_ember_p1_sign.py`）；`ember_hub.yml` 新 `Q` 格（hub slot 27）。
- `tools/p1sim/signin.py`（table / dyn / p2econ，可叠加挂机庭 `afk.py`）；冒烟 `tools/p1map/d180-signin-smoke.sh`。

## 有效在线怎么算
在线、不在挂机庭（`ember_afk` 的分钟归挂机庭自己结算）、最近 5 分钟有真实输入（转视角 / 点击 / 界面 / 攻击 / 聊天 / 命令 / 切栏 / 潜行；只是位置变化不算）→ 每 60 秒记 1 分钟。

## 后续
- 挂机庭自动战斗（D177 rev 2，1.65.15）定稿后用它的 `afk.py` 复跑设计 §7.3（本版用 19:18 的冻结副本）。
- Stage 2 候选（各自先过 p1sim）：节日月历替换大格内容；新人 7 格。
