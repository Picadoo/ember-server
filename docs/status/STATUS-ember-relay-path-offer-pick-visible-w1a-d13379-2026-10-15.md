# STATUS · TIP-EV W1a 传火路径选打菜单化 · D13379

> 2026-10-15 Asia/Shanghai · **W1a 已交** · tip **勿勾 CLOSED** · 等 W1b 物品联验

## 交付
- `EmberRelayPath.offerPick` →开 `/ember_p1_relay_path`（`openPathPick` / `PATH_PICK_*`）；**禁** ConfirmTokens 教 `relaypath` 为主 UI
- 无参 `/corerpg p1 relaypath` + 周要约 `maybeOfferWeekly` / `scheduleOfferAfterQ01` →同薄页（经 offerPick）
- `applyAndReply` 下一步→同薄页 `/ember_p1_relay_path` / 冒险 `/ember_p1_adventure`
- glance / 用法：清手打 relaypath 主教 →「点传火路径页」
- 局内「传火」战斗钮语义 **零改**（仍 `/corerpg p1 relay`）；「改路径」开薄页
- PAPI：`%corerpg_p1_relay_path%`/`_label`/`_tip`/`_line`
- 单测：`EmberRelayPathPickTest` · `EmberRunPapiTest` 绿
- jar：`1.65.872-d13379.local` · ECONOMY_BV **123** 未动
- **零改**伤害 / CD 表 / 掉落 / 体力价 / D568 传火语义 / breach_path / alert_path 及其他已齐 path / TrMenu yml（W1b 物品岗并行）
- tip **勿勾 CLOSED** · 等物品 W1b 再联验

## 证据
- `/workspace/tmp/tip-ev-relay-path/` · jar sha256 见 `00-jar.sha256`
