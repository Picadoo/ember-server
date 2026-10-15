# STATUS · TIP-EZ / D13383 · spot LIVE · 砸晶路径选打（SmashPath.offerPick → ember_p1_smash_path）

**裁决：PASS · 103/103** · tip 未勾 CLOSED（总控收口）

| 锚 | sha |
|---|---|
| APPROVED | `156e3572a` |
| W1a feat | `26423759a` |
| W1b menu | `caa9e282a` · `ember_p1_smash_path.yml` md5 `1878fc50495c09b8f5229d6d18190f99`（repo=live） |
| jar | `1.65.876-d13383.local` sha256 `9f342c5a738026c67e32d7981c01fad30ca9cb996adde641d0e86339fe3eade4`（target=live） |
| ECONOMY_BV | 123 未抬 · storage mysql |
| TrMenu | reload 后 131 menus |

## 必验
1. play live jar 876 · bv123 · MySQL — 绿
2. offerPick / 无参 smashpath / 周要约 / Q01 后 / 「换一条」→ 真开 `ember_p1_smash_path`；ConfirmTokens 不教裸 smashpath（硬红项）— 绿
3. 1/2/3/C → PAPI smash_path 1/2/3/0，`_label`/`_tip`/`_line` 随之变化 — 绿
4. q01done<1 灰态；局内「砸晶」仍 `/corerpg p1 smash`，「改路径」开薄页 — 绿
5. guard_path md5 `093df921…`、hold_path md5 `90acf254…` 零改；其它已齐 *path 零改；伤害/CD/掉落/体力价零变 — 绿
6. 单测 EmberSmashPath(Pick)Test · EmberGuardPath(Pick)Test · Hold/Alert 等 PickTest 全绿

注：spot 套件由 TIP-EY 102 项平移，新增 guard_path 不变量 → 103。改名对齐真实方法：maybeAfterPlace / pathSmashCrystal / smashPathCmd / smashGoCmd。
证据 `/workspace/tmp/tip-ez-smash-path/`（spot-run.log · 99-results.json）
