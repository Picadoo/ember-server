# STATUS · TIP-FA / D13384 · spot LIVE · 破绽爆发路径选打（BurstPath.offerPick → ember_p1_burst_path）

**裁决：PASS · 104/104** · tip 未勾 CLOSED（总控收口）

| 锚 | sha |
|---|---|
| APPROVED | `d876a2dfe` |
| W1a feat | `fd9bcf3d5` |
| W1b menu | `d8b9676df` · `ember_p1_burst_path.yml` md5 `fbecd39b7a22edd6abc8e32a2d932696`（repo=live） |
| jar | `1.65.877-d13384.local` sha256 `1795d8fdcdd958594da829b55b42e0a7d22b7297460aefd810d954de881ff758`（target=live） |
| ECONOMY_BV | 123 未抬 · storage mysql |
| TrMenu | reload 后 132 menus · duplicate keys WARN 0（c259724f8 修复生效） |

## 必验
1. play live jar 877 · bv123 · MySQL — 绿
2. offerPick / 无参 burstpath / 周要约 / Q01 后 / 「换一条」→ 真开 `ember_p1_burst_path`；ConfirmTokens/glance 不教裸 burstpath（硬红项）— 绿
3. 1/2/3/C → PAPI burst_path 1/2/3/0，`_label`/`_tip`/`_line` 随之变化（破绽爆发·自动 等）— 绿
4. q01done<1 灰态；局内「爆发」仍 `/corerpg p1 burst`，「改路径」开薄页 — 绿
5. counter_path「破绽反击」md5 `6a21af38…`、smash `1878fc50…`、guard `093df921…`、hold `90acf254…` 零改；伤害/CD/掉落/体力价零变 — 绿
6. 单测 EmberBurstPath(Pick)Test · Smash/Guard/Hold 等 PickTest 全绿

注：spot 由 TIP-EZ 103 项平移，新增 smash_path 不变量 → 104。改名对齐真实方法：maybeAfterStagger / pathBurst / burstPathCmd / burstGoCmd；counter 误开判定收紧为「破绽」且非「破绽爆发」。
证据 `/workspace/tmp/tip-fa-burst-path/`（spot-run.log · 99-results.json · 00-trmenu-reload-raw.txt）
