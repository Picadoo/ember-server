# 发布凭证 · CoreRpg 1.65.26（2026-10-05）

D187 Weekly Modifier Pack 4（收尾）：精选图周规则 +2（咒甲 / 断咒），池 12→14，斜移索引。设计：`docs/design/DESIGN-ember-weekly-mod-pack4-2026-10-05.md`。

## 线上状态（部署后核对于 2026-10-05 05:30 Asia/Shanghai）

| 项 | 值 |
|---|---|
| CoreRpg 版本 | **1.65.26** |
| class major | **52**（JDK 8，tools/jdk8u504-b01）|
| CoreRpg JAR sha256 | `0cf19a73b54d5f6f53ccfd4c217487d6855b85ec93e762a910d8e2960fea7d40` |
| balance_version | **49** |
| 回滚包 | `/workspace/backup/CoreRpg-1.65.25-pre-1.65.26.jar` |
| 服务器 PID | **1306094**（was 1261974）|
| MySQL | `[CoreRpg] [storage] MySQL connected` · `[CoreGacha] [db] MySQL connected` |
| SEVERE | 0 |
| p1sim Stage C | **within range**（`tools/p1sim/out-p2econ-mods-d187.md`，`--mods --weeks 98`）|

## 内容

- `hexplate` 咒甲：`remap: {heavy: caster}` + `converted: {hp: 1.25, interval: 1.2}`；challenge-only
- `blades` 断咒：`remap: {caster: melee}` + `converted: {speed: 1.2, atk: 0.9}`；challenge-only
- 池 12→**14**；14 与 7 图不互质 → `EmberRunMaps.modifierIndex` = (w mod 7 + w div 7) mod 14：任意 98 周覆盖全部 98 个图×规则对，相邻两周规则不同；互质池仍为 w mod n
- p1sim / p2econ 同步 `mod_index`
- 奖励表 / Extra 权重 / 装备结构 / AFK / 签到 / 化妆品：**不动**

## 验收

| 项 | 结果 |
|---|---|
| 单测 `mvn test package`（JDK 8）| **281 / 0** |
| p1sim Stage C | `p2econ --mods --weeks 98`：14 条规则各 360–420 玩家-周，通关率差全部 **+0 点** → within range。`--weeks 24` 在斜移索引下新 5 条规则（skirmish…blades）0 样本，不能作门禁，故跑满一个 98 周周期 |
| 开服 | Enabling CoreRpg v1.65.26；双 MySQL；SEVERE 0 |
| 冒烟 | **DEFERRED batch**（POLICY 01:53；新批次第 1 包）|
| 资产回归 | 无物品资产路径变化 → 未跑 persist-roundtrip |

## 协调

- routine-0418 写完并本地提交 42ceba7 后中断；routine-0517 补跑门禁、推送、部署、写凭证。
