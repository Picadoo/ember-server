# STATUS · TIP-EY W1a 护兔路径选打菜单化 · D13382

> 2026-10-15 Asia/Shanghai · **W1a 已交** · tip **勿勾 CLOSED** · APPROVED A·M `29ea1be01` · jar `1.65.875-d13382.local`

## 交付
- `EmberGuardPath.offerPick` →开 `/ember_p1_guard_path`（`openPathPick` / `PATH_PICK_*`）；**禁** ConfirmTokens 教 `guardpath` 为主 UI
- 无参 `/corerpg p1 guardpath` + 周要约 `maybeOfferWeekly` / `scheduleOfferAfterQ01` →同薄页（经 offerPick）
- `applyAndReply` 下一步→薄页 `/ember_p1_guard_path` / 冒险 `/ember_p1_adventure`
- glance / 用法：清手打 guardpath 主教 →「点护兔路径页」
- 局内「护兔」战斗钮语义 **零改**（仍 `/corerpg p1 guard`）；「改路径」开薄页
- PAPI：`%corerpg_p1_guard_path%`/`_label`/`_tip`/`_line`
- 单测：`EmberGuardPathPickTest`(8) · `EmberGuardPathTest`(1) · `EmberRunPapiTest`(5) · `EmberHoldPathPickTest`(8) 绿
- ECONOMY_BV **123** 未动；零改伤害/CD/掉落/体力价/D565/其它 *Path；TrMenu yml 归 W1b 物品岗

## 证据
- `/workspace/tmp/tip-ey-guard-path/`


## STATUS: **CLOSED live** · TIP-EY / D13382 · GuardPath.offerPick → `ember_p1_guard_path` · 2026-10-15 15:55 Asia/Shanghai

> **CLOSED live：** APPROVED `29ea1be01` · feat `22dc24411` · menu `a118e2ad6` · jar `1.65.875-d13382.local` · PASS `5ecfb6686` · spot **102/102** · TrMenu **130** · bv123 · 证据 `/workspace/tmp/tip-ey-guard-path/` · 护兔路径选打同题勿再交 · 下一支 TIP-EZ / D13383 SmashPath（砸晶）待批
