# STATUS · TIP-EY / D13382 · spot LIVE · 护兔路径选打（GuardPath.offerPick → ember_p1_guard_path）

**裁决：PASS · 102/102** · tip 未勾 CLOSED（总控收口）

| 锚 | sha |
|---|---|
| APPROVED | `29ea1be01` |
| W1a feat | `22dc24411` |
| W1b menu | `a118e2ad6` · `ember_p1_guard_path.yml` md5 `093df9212ece4e2d613ab7251491cb3c`（repo=live） |
| jar | `1.65.875-d13382.local` sha256 `c16a7e272ac85ba0722a7d33e899c998626c4b671407425fb38e501c7520774b`（target=live） |
| ECONOMY_BV | 123 未抬 · storage mysql |
| TrMenu | 130 menus |

## 必验
1. play live jar 875 · bv123 · MySQL — 绿
2. offerPick / 无参 guardpath / 周要约 / Q01 后 → 真开 `ember_p1_guard_path`；ConfirmTokens 不教裸 guardpath（硬红项）— 绿
3. 1/2/3/C → PAPI guard_path 1/2/3/0，`_label`/`_tip`/`_line` 随之变化（护兔·自动 等）— 绿
4. q01done<1 灰态；局内「护兔」仍 `/corerpg p1 guard`，「改路径」开薄页 — 绿
5. hold_path md5 `90acf254…` 零改；其它已齐 *path 零改；伤害/CD/掉落/体力价零变 — 绿
6. 单测 EmberGuardPathPickTest 8/8 · EmberGuardPathTest · Hold/Alert 等 PickTest 全绿

注：spot 套件由 TIP-EX 102 项平移（新增 hold_path 不变量），总数 102，不是派单里预期的 103。
证据 `/workspace/tmp/tip-ey-guard-path/`（spot-run.log · 99-results.json）
