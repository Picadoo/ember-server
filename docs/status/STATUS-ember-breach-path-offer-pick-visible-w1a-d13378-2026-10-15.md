# STATUS · TIP-EU W1a 裂隙路径选打菜单化 · D13378

> 2026-10-15 Asia/Shanghai · 总控续作 · **W1a 已交** · tip **勿勾 CLOSED** · jar `1.65.871-d13378.local`

## 交付
- `EmberBreachPath.offerPick` →开 `/ember_p1_breach_path`（`openPathPick` / `PATH_PICK_*`）；**禁** ConfirmTokens 教 `breachpath` 为主 UI
- 无参 `/corerpg p1 breachpath` + 周要约 `maybeOfferWeekly` / `scheduleOfferAfterQ01` →同薄页（经 offerPick）
- `applyAndReply` 下一步→同薄页 `/ember_p1_breach_path` / 冒险 `/ember_p1_adventure`
- glance / 用法：清手打 breachpath 主教 →「点裂隙路径页」
- 局内「裂隙」战斗钮语义 **零改**（仍 `/corerpg p1 breach`）；「改路径」开薄页
- PAPI：`%corerpg_p1_breach_path%`/`_label`/`_tip`/`_line`
- 单测：`EmberBreachPathPickTest` · `EmberRunPapiTest` 绿
- jar：`1.65.871-d13378.local` · ECONOMY_BV **123** 未动
- **零改**伤害 / CD 表 / 掉落 / 体力价 / D569 裂隙语义 / alert_path / stock_path 及其他已齐 path / TrMenu yml（W1b 物品岗并行）
- tip **勿勾 CLOSED** · 可派测 / 总控收口

## 证据
- `/workspace/tmp/tip-eu-breach-path/` · jar sha256 见 `00-jar.sha256`
