# STATUS · TIP-EQ W1a 集火路径选打菜单化 · D13374

> 2026-10-15 08:30 Asia/Shanghai · 总控续作（Periodic） · **W1a 已交** · tip **勿勾 CLOSED** · jar `1.65.867-d13374.local`

## 交付
- `EmberFocusPath.offerPick` →开 `/ember_p1_focus_path`（`openPathPick` / `PATH_PICK_*`）；**禁** ConfirmTokens 教 `focuspath` 为主 UI
- 无参 `/corerpg p1 focuspath` + 周要约 `maybeOfferWeekly` / `scheduleOfferAfterQ01` →同薄页（经 offerPick）
- `applyAndReply` 下一步→同薄页 `/ember_p1_focus_path` / 冒险 `/ember_p1_adventure`
- glance / 用法：清手打 focuspath 主教 →「点集火路径页」
- 局内「集火」战斗钮语义 **零改**（仍 `/corerpg p1 focus`）；「改路径」开薄页
- PAPI：`%corerpg_p1_focus_path%`/`_label`/`_tip`/`_line`
- 单测：`EmberFocusPathPickTest` · `EmberRunPapiTest` 绿
- jar：`1.65.867-d13374.local` · ECONOMY_BV **123** 未动
- **零改**伤害 / CD 表 / 掉落 / 体力价 / D597 集火语义 / doom_path / nest_path 及其他已齐 path / TrMenu yml（W1b 物品岗并行）
- tip **勿勾 CLOSED** · 可派测 / 总控收口

## 证据
- `/workspace/tmp/tip-eq-focus-path/` · jar sha256 见 `00-jar.sha256`
