# STATUS · CoreRpg 1.65.53 / D227 — 徽记残响 option B

**日期：** 2026-10-06 Asia/Shanghai  
**上游：** D226 R1-sim 推荐 B；业主确认后落地。Live 原 1.65.52 / bv57。

## 做了什么

1. **厅扩：** `rush.echo_q05` / `echo_q06` / `echo_q07`（仍 `EmberQ0B2`、mode echo、`claim: p4_echo_claim`、`weekly: 3`、`reward.sigmarks: 2`）。
2. **门槛：** Q01–Q04 仍需 Q04 首通；Q05–Q07 需本图首通（`requires` + 既有 `chainKeys` 校验）。
3. **总量：** 周发放不变（共用 3×2）；只扩大可领图集合 → 结余最少策略自然喂高图（与 sim B 一致）。
4. **UI：** `ember_p1_modes` 第二排加 Q05–Q07；文案「七个首领合计每周 3 次」。
5. **SoT：** `ember-v1-economy.yml` / runs `balance_version` → 58（金额金样未改）；`insignia.py` 默认 ECHO maps = q01–q07（与 `--whatif B` 体一致）。

## 验收

| 项 | 结果 |
|---|---|
| Unit（JDK8） | 401/0 |
| 启动 | CoreRpg 1.65.53；economy SoT bv58；MySQL×2；SEVERE 0 |
| FreshQ787 | modes E lore Q01–Q07；Q05/Q07 图标；echo_q05/q07 bind；echo_q01 settle claim → papi「本周已领 1/3」 |

## 未做 / 后续

- Q05–Q07 `boss_hp`/`boss_dmg` 为 provisional（对齐有效生命约 2.8–3.1k）；可选独立 echotune 窗。
- R2：若 Q01–Q04 死库存仍刺眼，再扫 option C 兑换比（独立窗）。
- C15 Cosmetics 仍暂停。
