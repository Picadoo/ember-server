# STATUS · TIP-EU W1a 裂隙路径选打菜单化 · D13378

> 2026-10-15 10:10 Asia/Shanghai · 总控收口 · **W1a 已交** · tip **CLOSED live** · feat `afa21fbef` · tip `521d55ef1` · menu `dba5004a2` · jar `1.65.871-d13378.local` · PASS `5d7e02ac9` · 99/99

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
- tip **CLOSED live** · **CLOSED live** · feat `afa21fbef` · tip `521d55ef1` · menu `dba5004a2` · jar `1.65.871-d13378.local` · PASS `5d7e02ac9` · 2026-10-15 10:10 Asia/Shanghai · 裂隙路径选打菜单化同题勿再交 · Relay D568+ 另窗证据逐支（禁全族大扫 · 禁与 Relay/Beacon/Hold 等同窗）· 勿默念 Pace（本仓无）· 挂机若新硬债 / legacy evacuate 后置

## 证据
- `/workspace/tmp/tip-eu-breach-path/` · jar sha256 见 `00-jar.sha256`
