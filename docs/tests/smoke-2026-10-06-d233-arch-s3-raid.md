# Smoke · D233 / CoreRpg 1.65.59 — ARCH S3-4 EmberRaidService extract

**何时：** 2026-10-06 ~05:39–05:43 Asia/Shanghai · **脚本：** `tools/p1map/d233-arch-s3-raid-smoke.sh` · **Bot：** FreshQ806（团长）+ FreshQ807 / FreshQ808（团员）+ FreshQ809（团本问题行 + Q01 结算）

## 结果

**PASS 32 / FAIL 0 / SOFT 1** · MySQL×2 · SEVERE 0 · jar `c71dc7a06a23a943…` · play PID 2226117

| 项 | Bot | 结果 |
|---|---|---|
| CoreRpg 1.65.59 enable · MySQL×2 · E1 SoT bv58 · 无 FAIL-CLOSED | — | PASS |
| 团本进本问题行「未开放团本（需本人首通 Q07）」（`entryProblem`） | FreshQ809 | PASS |
| `/corerpg p1 runs` 团本行「本周 0/3（团本合计） · 3～5 人 · 50 体力」（`menuButtons` / `label`） | FreshQ806 | PASS |
| `papi parse … %corerpg_p1_raid_r02%` | console | SOFT（console 侧 `Failed to find player`，PAPI 调用问题，非插件；同一 `label()` 已由菜单行覆盖） |
| R01 三人队进本 + 开场行「团本开始 · 3 人 · 掉落 T3 · 敌方生命 ×2.70 伤害 ×1.40」+ 队伍建议（`onStart`） | 3 bots | PASS |
| R01 房 1–3 清房 + 首领现身 | 3 bots | PASS |
| D106：`kill FreshQ808` → 「FreshQ808 倒下 · 下一次复活」（`onFall`） | FreshQ806 视角 | PASS |
| D106：`/corerpg p1 watch` → 「正在观战」（`cmdWatch`） | FreshQ808 | PASS |
| D106：倒下后 `/dp leave` 被拦「倒下后不能离开团本」（`onFallenLeave`） | FreshQ808 | PASS |
| D106：下一房开打 → log `raid revive (新房间开打): [FreshQ808]`（`reviveFallen`） | — | PASS |
| R01 结算（2 人 settle rows+10；1 人 `acted=false` 不合格 = §20.5 原规则，削弱后首领 4.2 s 倒下，该 bot 未出手） | — | PASS |
| 周计数器 0→1：团本行「本周 1/3（团本合计）」（`applyClearCount`） | FreshQ806 | PASS |
| 无 grant refuse | — | PASS |
| Q01 进本 + 三房 + 结算 | FreshQ809 | PASS |
| SEVERE | — | 0 |

## 说明

- 计数器 key / cap_group 共计 / settle grant 形状由 `EmberRaidServiceTest` 钉死；冒烟覆盖菜单 / 问题行 / 开场行 / D106 全链（倒下 → 观战 → 拦离开 → 下一房复活）/ 结算后计数器移动 / Q01 回归。
- 团本失败「次数没扣」行（`tellFailNoBurn`）未在线触发（需全员倒下）；文本由单测 `failNoBurnText` 钉死。
- 无资产路径变更 → 不做 persist-roundtrip。
