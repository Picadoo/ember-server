# STATUS · ARCH S2-3 经济路由（CoreRpg 1.65.47 / D216）

## 本窗新路由

| REG | 路径 | 怎么走 |
|---|---|---|
| S23 | 每日签到 | `daily.coin` / `daily.xp` / `makeup_per_month` / `sigmark_fallback_coin` 读 `EmberEconomy.amount`；发放经 `sourceForGrant(…, p1sign-*)` → grantCoin / grantXp / grantMark |
| S24 | 在线时长档 | 里程碑格仍读 yml；合计钉 S24 `coin_total`/`xp_total`/`top_min`（漂移打 warning）；发放经 `p1online-*` → 同上 |
| S01/S03/S04/S05/S06/S20/S21/S25 | 材料 / 印记 / XP | deliver 里 MAT/MARK/XP 有 REG 源时先走 `grantMat` / `grantMark` / `grantXp`（键前缀含 `var_affix_`/`var_event_`） |
| — | 扫描 | `EmberEconomyTest.p1AddCoinIsEconomyOrAllowlisted`：`p1/` 内新 `.addCoin(` 必须进登记表或退款白名单 |

## 已路由（累计）

S01–S03 settle 数量；S06/S20/S21/S25 币；C14 药价+扣币；上表 S23/S24 + mat/mark/XP 校验入口。

## 未路由（下一切）

- 工坊消耗 C03–C06、深渊层费 C08、天赋 C09/C10、印记兑换 C07、徽记烙印/调律 C12/C13
- 挂机庭 S22 发放入口统一
- yaml 真源 `ember-v1-economy.yml`（REG §6.3）
- 徽记账户进 p1sim（见 `docs/design/TODO-ember-p1sim-insignia-2026-10-06.md`）

## 后撤步

仅设计备忘（DESIGN-ember-skill-kit §4b）：无套装亲和 / 菜单选型定稿前不写代码，不发明亲和。
