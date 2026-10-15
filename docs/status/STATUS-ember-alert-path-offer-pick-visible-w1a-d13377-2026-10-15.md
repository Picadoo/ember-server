# STATUS · TIP-ET W1a 预警路径选打菜单化 · D13377

> 2026-10-15 09:30 Asia/Shanghai · 总控续作（Periodic） · **W1a 已交** · tip **勿勾 CLOSED** · jar `1.65.870-d13377.local`

## 交付
- `EmberAlertPath.offerPick` →开 `/ember_p1_alert_path`（`openPathPick` / `PATH_PICK_*`）；**禁** ConfirmTokens 教 `alertpath` 为主 UI
- 无参 `/corerpg p1 alertpath` + 周要约 `maybeOfferWeekly` / `scheduleOfferAfterQ01` →同薄页（经 offerPick）
- `applyAndReply` 下一步→同薄页 `/ember_p1_alert_path` / 冒险 `/ember_p1_adventure`
- glance / 用法：清手打 alertpath 主教 →「点预警路径页」
- 局内「就位」战斗钮语义 **零改**（仍 `/corerpg p1 alert`）；「改路径」开薄页
- PAPI：`%corerpg_p1_alert_path%`/`_label`/`_tip`/`_line`
- 单测：`EmberAlertPathPickTest` · `EmberRunPapiTest` 绿
- jar：`1.65.870-d13377.local` · ECONOMY_BV **123** 未动
- **零改**伤害 / CD 表 / 掉落 / 体力价 / D600 预警语义 / stock_path / hurry_path 及其他已齐 path / TrMenu yml（W1b 物品岗并行）
- tip **勿勾 CLOSED** · 可派测 / 总控收口

## 证据
- `/workspace/tmp/tip-et-alert-path/` · jar sha256 见 `00-jar.sha256`
