# STATUS · ARCH S4-2（CoreRpg 1.65.68 / D243）

**日期：** 2026-10-06 Asia/Shanghai · **上游：** live 1.65.67 / D241 + D242 文档（a24fc17）/ bv58

D242 审计留下的缺口 G1–G11 里，能直接修的这一刀全修了：**只登记 / 打标签 / 同步 / 改注释，任何数量都不变**（`balance_version` 仍 58）。

## 1 · 登记（G1 / G2 / G3）+ S09 键（G8）

| 行 | 名称 | 账户 / 周期 | 金样（= 现行数） | 接线 |
|---|---|---|---|---|
| **S33** | 图录阶段奖励 | COIN · 一次性（`p1_codex_` / `p1_codex_stage_`） | `at5/at10/at15/at20.coin` = 200 / 400 / 600 / 1000（= `EmberCodex.STAGE_AT` / `STAGE_COIN`） | 账本 `codex/stage<n>` 币行 `sourceForGrant` → S33，走带标签的 `grantCoin`（原来是无标签 `addCoin`） |
| **S34** | 宝箱额外装备 | GEAR · 按局 | `chest_weight` 5（= `EmberRunRules.EXTRA_WEIGHTS[3]`） | `sourceForGrantKey("extra_chest_item")` → S34（原混在 S01） |
| **S35** | 起步包 | GEAR + POT · 一次性（`p1_starter`） | `pieces` 2、`potions` 5（= `ember-v1.yml starter.heal_potions`） | 账本 `starter/starter_<slot>` → S35 |
| S09 | 自选誓约 | — | 新键 `per_rule` 1 | `EmberPledgeService.settleGrant` = 誓约条数 × `amount("S09","per_rule")`（原来写死 ×1） |

`ember-v1-economy.yml` 加 S09 / S33 / S34 / S35 块（线上与打包相同）；`EmberEconomyTest` 改成 S01–S35（35 行），金样分别钉在 `EmberCodex` 常量、`EXTRA_WEIGHTS[3]`、`starter.heal_potions` 上，外加路由测试 `d243RoutesCodexChestStarter`。

## 2 · G4：OP `givedup` 不再生成可分解件

`/corerpg p1 givedup` 生成的件 `source` 从 `drop` 改为 `admin`：不可分解（`dismantleCheck` 只放行 drop）、不进图录、不进批量分解。为了 OP 洗练测试照常能用，`EmberAffix.duplicateOk` 现在接受 `drop` **或** `admin` 当重复件——**小的行为变化**：OP `/corerpg p1 give` 发的 admin 件（标准、+0、无精工）也能被洗练吃掉。只有 OP 能产出 admin 件，玩家路径不变。单测 `adminDuplicateIsNotDismantlable`。

## 3 · G5：打包配置 = 线上

5 个 `ember-v1*.yml` 的 `src/main/resources` 与 `plugins/CoreRpg/` 现在逐字节相同（`ember-v1.yml` 打包版补了 `legacy_gate` 块和 MapV2 前缀，线上值全保留，无密钥）。新单测 `EmberSourceMapTest.bundledConfigsMatchLive` 防止再漂。

## 4 · 注释（G6 / G7 / G11）

- G6 `ember-v1.yml` 挂机庭：「不发材料」→ rev 2 实际发的绑定碎片 / 骨尘 / 核心 / 胚料。顺手把第 1 行文档路径修正为 `docs/design/DESIGN-ember-v1.0-P1-source-table.md`。
- G7 `ember-v1-runs.yml` raids：「r01 + r02 共用」→「r01 + r02 + r03 共用」。**顺带（玩家可见文案）**：r01 / r02 的 `purpose` 改成「与 r02 / r03 合计」「与 r01 / r03 合计」（原来漏了 r03；只改文字）。
- G11 bv41 历史注释：「7 件调律版」→「6 件调律版 L01/L02/L06/L08/L10/L12，L11b 不开放」。

## 5 · p1sim 读 `ember-source-map.yml`（G9）

- `rules.py` 把 source map 纳入规则快照；`validate()` 要求每个 `sim:` 来源在 economy yml 有块。
- 新 `tools/p1sim/sourcemap.py`：`codex_stages()`（S33 数从 yml 取，和 Java `STAGE_AT` / `STAGE_COIN` / `FAMILIES` 双重断言）、`starter_kit()`（S35 药数 = `starter.heal_potions`）；`show` / `dyn` 两个工具。
- `p1sim.py`：`codex_see()` 在 T0 起步件和每件新到手的件上登记图录种类，到 5 / 10 / 15 / 20 种立刻发阶段币；不抽随机数。`P1SIM_NO_CODEX=1` = 改前模拟，**输出与改前逐位相同**（只有 rules 戳不同）。

### Gate 前 / 后（不调参）

| Gate | 改前（a24fc17） | 改后 | 结论 |
|---|---|---|---|
| 21 格 A/B（`sourcemap.py dyn --players 800`，A = 不发图录币，B = S33） | — | **21/21 在 ±2pp 内**，最大 \|差\| 0.6 pp（dodge 0.3 Q06 19.6 → 19.1）；首通中位天 / P90 / 等级 21 格全部相同 | 不翻 |
| p1sim 默认（60 人 × dodge 0.3 / 0.5 / 0.7） | Q07 首通中位 23 / 8 / 3 天，P90 33 / 11 / 4，60 天内 100 % | 中位 **24** / 8 / 3，P90 33 / 11 / 4，100 % | dodge 0.3 中位 +1 天（60 人样本噪声：800 人 A/B 同格 26 = 26）；其余格 ±1 级强化 / ±3 pp 前沿率（强化随机流位移） |
| selfcheck | 2 个已知 FAIL（图录菜单「预警 3.0 秒」文案、`signin.py:35` 直读规则） | 同样 2 个，无新增 | 不变 |
| W30（p2econ 300 人 × 12 周 dodge 0.5 + 深渊 + 团本 + 周目标） | **5.42** 周（P2-2 深渊；P50 7.60） | **5.50** 周（P2-2 深渊 / 深渊 + 周目标；P50 7.33） | 标题 W30 +0.08 周，最快路线不变，不翻。**要报的一处状态变化**：无轮换 / P2-1 轮换的 P50 由「12 周内未达」变为 11.00 / 11.33 周（第 12 周仍无双极品 51 → 47 %、53 → 48 %），路线排序里「无轮换」移到「轮换 + 团本」前面；P90 全部仍未达。不调参 |

每角色图录币（800 人 × 60 天）：dodge 0.3 / 0.5 / 0.7 = 734 / 541 / 319 币。

## 测试

全量 **533/0**（JDK8；+3：`d243RoutesCodexChestStarter`、`adminDuplicateIsNotDismantlable`、`bundledConfigsMatchLive`；`EmberEconomyTest` 金样 +4 组）。

## 冒烟

见 `docs/tests/smoke-2026-10-06-d243-arch-s4-2.md`：**47/0/0**（run1 20/0/0 + run2 27/0/0），FreshQ831 / FreshQ832。起步包 5/5 药 + 2 件 T0；Q01 回归结算；图录领取 +200 整、无拒绝、第二次不发；givedup → admin、分解被拒、洗练照常吃掉；8 次背包 ↔ DB 一一对应（无丢件 / 复制）；MySQL×2；SEVERE 0。

## 不变

`balance_version` **58**；所有发放 / 消耗数量；图录 / 宝箱 / 起步包 / 誓约实际发的东西；玩家路径的分解与洗练规则；C15；内容包。

## 部署

jar `b885c11f88c14fd7…`（sha256 `b885c11f88c14fd7d44fe261b2754052d03316657abcce5f5e51761bf74ad3c0`）· play PID 2389433 · 08:37 Asia/Shanghai · 回滚 `/workspace/backup/CoreRpg-1.65.67-pre-1.65.68.jar`（1.65.67 jar 读新 yml 没问题：漂移检查只看自己的金样）。

## 下一刀

p1sim 读词缀原语导出表（D241 记下的，需重跑 gate）；6 槽 Stage 1 仍等服主开工（独立 bv）。G10（REG §5 物品级缺口）仍在。
