# STATUS · TIP-ES W1a 备药路径选打菜单化 · D13376

> 2026-10-15 09:15 Asia/Shanghai · 总控续作（Periodic） · **W1a 已交** · tip **勿勾 CLOSED** · jar `1.65.869-d13376.local`

## 交付
- `EmberStockPath.offerPick` →开 `/ember_p1_stock_path`（`openPathPick` / `PATH_PICK_*`）；**禁** ConfirmTokens 教 `stockpath` 为主 UI
- 无参 `/corerpg p1 stockpath` + 周要约 `maybeOfferWeekly` / `scheduleOfferAfterQ01` →同薄页（经 offerPick）
- `applyAndReply` 下一步→同薄页 `/ember_p1_stock_path` / 冒险 `/ember_p1_adventure`
- glance / 用法：清手打 stockpath 主教 →「点备药路径页」
- 局内「核对」战斗钮语义 **零改**（仍 `/corerpg p1 stock`）；「改路径」开薄页
- PAPI：`%corerpg_p1_stock_path%`/`_label`/`_tip`/`_line`
- 单测：`EmberStockPathPickTest` · `EmberRunPapiTest` 绿
- jar：`1.65.869-d13376.local` · ECONOMY_BV **123** 未动
- **零改**伤害 / CD 表 / 掉落 / 体力价 / D599 备药语义 / hurry_path / focus_path 及其他已齐 path / TrMenu yml（W1b 物品岗并行）
- tip **勿勾 CLOSED** · 可派测 / 总控收口

## 证据
- `/workspace/tmp/tip-es-stock-path/` · jar sha256 见 `00-jar.sha256`
