# STATUS · TIP-EW W1a 护灯路径选打菜单化 · D13380

> 2026-10-15 10:53 Asia/Shanghai · 总控收口 · **W1a 已交** · tip **CLOSED live** · feat `113e9f7f0` · tip `ead4c7ee4` · menu `b4a3273eb` · jar `1.65.873-d13380.local` · PASS `02dcf7f0f` · 101/101

## 交付
- `EmberBeaconPath.offerPick` →开 `/ember_p1_beacon_path`（`openPathPick` / `PATH_PICK_*`）；**禁** ConfirmTokens 教 `beaconpath` 为主 UI
- 无参 `/corerpg p1 beaconpath` + 周要约 `maybeOfferWeekly` / `scheduleOfferAfterQ01` →同薄页（经 offerPick）
- `applyAndReply` 下一步→同薄页 `/ember_p1_beacon_path` / 冒险 `/ember_p1_adventure`
- glance / 用法：清手打 beaconpath 主教 →「点护灯路径页」
- 局内「护灯」战斗钮语义 **零改**（仍 `/corerpg p1 beacon`）；「改路径」开薄页
- PAPI：`%corerpg_p1_beacon_path%`/`_label`/`_tip`/`_line`
- 单测：`EmberBeaconPathPickTest` · `EmberRunPapiTest` 绿
- jar：`1.65.873-d13380.local` · ECONOMY_BV **123** 未动
- **零改**伤害 / CD 表 / 掉落 / 体力价 / D567 护灯语义 / relay_path / breach_path 及其他已齐 path / TrMenu yml（W1b 物品岗并行）
- tip **CLOSED live** · **CLOSED live** · feat `113e9f7f0` · tip `ead4c7ee4` · menu `b4a3273eb` · jar `1.65.873-d13380.local` · PASS `02dcf7f0f` · 2026-10-15 10:53 Asia/Shanghai · 护灯路径选打菜单化同题勿再交 · Hold D566+ 另窗证据逐支（禁全族大扫 · 禁与 Hold/Guard 等同窗）· 勿默念 Pace（本仓无）· 挂机若新硬债 / legacy evacuate 后置

## 证据
- `/workspace/tmp/tip-ew-beacon-path/` · jar sha256 见 `00-jar.sha256`
