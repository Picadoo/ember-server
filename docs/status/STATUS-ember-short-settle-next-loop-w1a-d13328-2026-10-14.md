# STATUS · TIP-CW W1a 短征结算「下一环」菜单化 · D13328

> 2026-10-14 Asia/Shanghai · 余烬-插件岗 · **W1a 已交 · 勿勾 CLOSED** · 等物品 W1b 薄页 + 测岗

## 交付
- 有奖 settle 下一环主钮 → `/ember_p1_short_settle`（`SETTLE_NEXT_LOOP_*`）
- 可保留「再进本图」直达 `enter <key>`；路径追 → `/ember_p1_short_path`；工坊 → `/ember_p1_forge`
- 日奖满分支：下一环薄页 + 冒险页 + 路径追（去 shortpath/forge 裸命令）
- 清裸指令：`FC_CLEARED_HINT`（点路径追）；`EmberShortPath` glance / 换一条同批对齐
- `spendChaseButtonCmd` → `/ember_p1_forge`
- todayRot `settleCue`：保持 `enter short-today` 别名（不教手打）
- 单测：`EmberShortSettleNextLoopTest`（常量 / settle 源 / ShortPath 源 / todayRot）
- jar：`1.65.820-d13328.local` · sha256 `a4eccd4b1413…` · ECONOMY_BV **123** 未动
- play：live `1.65.820-d13328.local` · storage=mysql · bv123 · MySQL connected
- feat：`dc23f7104` → origin/main
- **零改** resolve 算法 / 日帽 / S40–S58 / targets.short / Cast / Aid / variety / short_div* / 路径追 PAPI·enter / TrMenu yml
- **未做** 菜单 W1b（物品岗 `ember_p1_short_settle`）

## 落点
- `EmberShortRules`：SETTLE_* / FC_CLEARED_HINT；spendChaseButtonCmd→forge 菜单
- `EmberShortService`：settle ConfirmTokens 下一环开薄页
- `EmberShortPath`：FC 清提示 + glance + 换一条开路径页
- `pom.xml` / `plugin.yml`：1.65.820-d13328.local

## 零改边界
- ShortPath resolve 排序/权重 · 日帽 · S40–S58 · weekly_goals.targets.short
- Cast / Aid / variety / short_div* / 路径追 PAPI·enter · TrMenu yml
- balance_version 123 · sx20

## 下步
- 物品 W1b 薄 settle 页 `ember_p1_short_settle`（再进/回选/路径/工坊）
- 测岗抽测后总控收口（本号勿 CLOSED）
