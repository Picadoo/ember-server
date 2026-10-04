# 发布凭证 · CoreRpg 1.65.25（2026-10-05）

D186 Weekly Modifier Pack 3：精选图周规则 +3（散兵 / 咒潮 / 重弩）。设计：`docs/design/DESIGN-ember-weekly-mod-pack3-2026-10-05.md`。

## 线上状态（部署后核对于 2026-10-05 Asia/Shanghai）

| 项 | 值 |
|---|---|
| CoreRpg 版本 | **1.65.25** |
| class major | **52**（JDK 8，tools/jdk8u504-b01）|
| CoreRpg JAR sha256 | `f58ff765669486b7152cba83bb8f735a5dca3475ac83c0a21d9ee45037ea922c` |
| balance_version | **48** |
| 回滚包 | `/workspace/backup/CoreRpg-1.65.24-pre-1.65.25.jar` |
| 服务器 PID | **1248097** |
| MySQL | `[CoreRpg] [storage] MySQL connected` · `[CoreGacha] [db] MySQL connected` |
| SEVERE | 0 |
| p1sim Stage C | deferred（`--mods`；POLICY within-range）|

## 内容

- `skirmish` 散兵：`remap: {melee: ranged}` + `converted: {interval: 0.85, hp: 0.9}`；challenge-only
- `hexers` 咒潮：`remap: {melee: caster}` + `converted: {interval: 1.1}`；challenge-only
- `ballista` 重弩：`remap: {heavy: ranged}` + `converted: {atk: 1.25, interval: 1.35, speed: 0.8}`；challenge-only
- 池 9→**12**（ISO 周 mod 12；7×12 = **84** 图×规则对）
- 奖励表 / Extra 权重 / 装备结构 / AFK / 签到 / 化妆品：**不动**
- 单测 `_D186` 断言并入 `EmberRunRulesTest` 周规则块；bv48

## 验收

| 项 | 结果 |
|---|---|
| 单测 `mvn test package`（JDK 8）| **281 / 0** |
| p1sim Stage C | deferred |
| 冒烟 | deferred to batch (POLICY 01:53; packs since 1.65.20) |
| 资产回归 | 无物品资产路径变化 → 未跑 persist-roundtrip |
