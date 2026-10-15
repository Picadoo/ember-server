# STATUS · TIP-EZ W1a 砸晶路径选打菜单化 · D13383

> 2026-10-15 Asia/Shanghai · **W1a 已交** · tip **勿勾 CLOSED** · APPROVED A·M `156e3572a` · jar `1.65.876-d13383.local`

## 交付
- `EmberSmashPath.offerPick` →开 `/ember_p1_smash_path`（`openPathPick` / `PATH_PICK_*`）；**禁** ConfirmTokens 教 `smashpath` 为主 UI
- 无参 `/corerpg p1 smashpath` + 周要约 `maybeOfferWeekly` / `scheduleOfferAfterQ01` →同薄页（经 offerPick）
- `applyAndReply` 下一步→薄页 `/ember_p1_smash_path` / 冒险 `/ember_p1_adventure`
- glance / 用法：清手打 smashpath 主教 →「点砸晶路径页」
- 局内「砸晶」钮语义 **零改**（仍 `/corerpg p1 smash`，maybeAfterPlace / pathSmashCrystal 未动）；「改路径」开薄页
- PAPI：`%corerpg_p1_smash_path%`/`_label`/`_tip`/`_line`
- 单测：`EmberSmashPathPickTest`(8) · `EmberSmashPathTest`(1) · `EmberRunPapiTest`(5) · `EmberGuardPathPickTest`(8) 绿
- ECONOMY_BV **123** 未动；零改伤害/CD/掉落/体力价/D564/其它 *Path；TrMenu yml 归 W1b 物品岗（本窗未动）

## 证据
- `/workspace/tmp/tip-ez-smash-path/`


## STATUS: **CLOSED live** · TIP-EZ / D13383 · SmashPath.offerPick → `ember_p1_smash_path` · 2026-10-15 16:10 Asia/Shanghai

> **CLOSED live：** APPROVED `156e3572a` · feat `26423759a` · menu `caa9e282a` · jar `1.65.876-d13383.local` · PASS `b6b79b7a1` · spot **103/103** · TrMenu **131** · bv123 · 证据 `/workspace/tmp/tip-ez-smash-path/` · 砸晶路径选打同题勿再交 · reload WARN duplicate keys priority/display 源自 `ember_p1_short.yml`（非 smash/guard/hold，见 backlog B-trmenu-short-dup-keys）· 下一支 TIP-FA / D13384 BurstPath（破绽）待批
