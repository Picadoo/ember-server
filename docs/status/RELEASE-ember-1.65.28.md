# 发布凭证 · CoreRpg 1.65.28（2026-10-05）

D189 Affix Pack 4：词缀精英池 8 → 10，新增「毒十字」`venom` 与「禁锢」`jailer`。设计：`docs/design/DESIGN-ember-affix-pack4-2026-10-05.md`。

## 线上状态（部署后核对于 2026-10-05 06:19 Asia/Shanghai）

| 项 | 值 |
|---|---|
| CoreRpg 版本 | **1.65.28** |
| class major | **52**（JDK 8，tools/jdk8u504-b01）|
| CoreRpg JAR sha256 | `9cfdf869d483381afaa76169e041f82fc66004d34a93739a379522afa02732eb` |
| balance_version | **51** |
| 回滚包 | `/workspace/backup/CoreRpg-1.65.27-pre-1.65.28.jar` |
| 服务器 PID | **1339093**（was 1326994）|
| MySQL | `[CoreRpg] [storage] MySQL connected` · `[CoreGacha] [db] MySQL connected` |
| SEVERE | 0 |

## 内容

- `ember-v1-runs.yml`：`variety.affixes` + `venom, jailer`；`venom: {every 6, warn 1.3, arm 4, width 1.5, dmg 1.0}`、`jailer: {every 7, warn 1.2, radius 1.6, root 1.0, dmg 0.5}`；balance_version 50 → 51
- `EmberRunMaps.Variety`：KNOWN 10 项、参数解析与钳位（root ≤ 1.5 秒、warn ≥ 1.2、arm ≤ 6）、中文名 毒十字 / 禁锢
- `EmberRunDirector`：毒十字（精英脚下十字 +/× 轮换，交叉点只算一次，kb 0，日志 `venom +|x hit=N`）；禁锢（最近玩家脚下双圈，落下圈内 0.5×atk + 定身，日志 `jailer rooted=N`）；进房提示各一句
- 图录 / 冒险菜单词缀列表同步（八选一 → 十选一）
- p1sim `affix_mob` 建模两条新缀；门禁 `tools/p1sim/affixpack4.py` → `tools/p1sim/out-affixpack4-d189.md`（within range）
- 奖励 / 掉落 / 闸门 / 装备结构 / AFK / 签到 / 化妆品：**不动**

## 验收

| 项 | 结果 |
|---|---|
| 单测 `mvn package`（JDK 8）| **285 / 0**（+1：`varietyPack4VenomJailer_D189` 参数、钳位、十字命中 / 空隙、kb 0、奖励不变、roll 能出两条新缀）|
| p1sim 门禁 | p2econ 120 人 × 12 周，新池 vs 旧池各列 ±1 pp 级、币中位 +0.5%：within range |
| 冒烟 | 合批（1.65.26 D187 + 1.65.27 D188 + 1.65.28 D189），报告见 `docs/tests/smoke-2026-10-05-batch-1.65.26-28.md` |
| persist-roundtrip | 不需要（无资产路径变化）|
