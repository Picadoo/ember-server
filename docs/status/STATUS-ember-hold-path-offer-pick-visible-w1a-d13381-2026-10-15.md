# STATUS · TIP-EX W1a 占点路径选打菜单化 · D13381


> **CLOSED live** · tip TIP-EX · feat `ad2720cfe` · jar `1.65.874-d13381.local` · 2026-10-15 15:05 Asia/Shanghai · 证据 `/workspace/tmp/tip-ex-hold-path/`

> 2026-10-15 11:55 Asia/Shanghai · 总控收口 · **W1a 已交** · tip **勿勾 CLOSED** · jar `1.65.874-d13381.local` · menu `ddb1f6ce6` · tip批 `6b5357baf`

## 交付
- `EmberHoldPath.offerPick` →开 `/ember_p1_hold_path`（`openPathPick` / `PATH_PICK_*`）；**禁** ConfirmTokens 教 `holdpath` 为主 UI
- 无参 `/corerpg p1 holdpath` + 周要约 `maybeOfferWeekly` / `scheduleOfferAfterQ01` →同薄页（经 offerPick）
- `applyAndReply` 下一步→同薄页 `/ember_p1_hold_path` / 冒险 `/ember_p1_adventure`
- glance / 用法：清手打 holdpath 主教 →「点占点路径页」
- 局内「占点」战斗钮语义 **零改**（仍 `/corerpg p1 hold`）；「改路径」开薄页
- PAPI：`%corerpg_p1_hold_path%`/`_label`/`_tip`/`_line`
- 单测：`EmberHoldPathPickTest` · `EmberRunPapiTest` 绿
- jar：`1.65.874-d13381.local` · ECONOMY_BV **123** 未动
- **零改**伤害 / CD 表 / 掉落 / 体力价 / D566 占点语义 / beacon_path / relay_path 及其他已齐 path / TrMenu yml（W1b 物品岗已交 `ddb1f6ce6`）
- tip **勿勾 CLOSED** · 待测岗 PASS live 后再 CLOSED · Guard 等另窗证据逐支（禁全族大扫）· 勿默念 Pace（本仓无）

## 证据
- `/workspace/tmp/tip-ex-hold-path/` · jar sha256 见 `00-jar.sha256`
