# STATUS · TIP-EP W1a 灾临路径选打菜单化 · D13373

> 2026-10-15 07:48 Asia/Shanghai · 总控续作（Periodic） · **W1a 已交** · tip **勿勾 CLOSED** · jar `1.65.866-d13373.local`

## 交付
- `EmberDoomPath.offerPick` →开 `/ember_p1_doom_path`（`openPathPick` / `PATH_PICK_*`）；**禁** ConfirmTokens 教 `doompath` 为主 UI
- 无参 `/corerpg p1 doompath` + 周要约 `maybeOfferWeekly` / `scheduleOfferAfterQ01` →同薄页（经 offerPick）
- `applyAndReply` 下一步→同薄页 `/ember_p1_doom_path` / 冒险 `/ember_p1_adventure`
- glance / 用法：清手打 doompath 主教 →「点灾临路径页」
- 局内「奔赴」战斗钮语义 **零改**（仍 `/corerpg p1 doom`）；「改路径」开薄页
- PAPI：`%corerpg_p1_doom_path%`/`_label`/`_tip`/`_line`
- 单测：`EmberDoomPathPickTest` · `EmberRunPapiTest` 绿
- jar：`1.65.866-d13373.local` · ECONOMY_BV **123** 未动
- **零改**伤害 / CD 表 / 掉落 / 体力价 / D596 灾临奔赴语义 / nest_path 及其他已齐 path / TrMenu yml（W1b 已在 `635d5b670`）
- tip **勿勾 CLOSED** · 可派测 / 总控收口

## 证据
- `/workspace/tmp/tip-ep-doom-path/` · jar sha256 见 `00-jar.sha256`
