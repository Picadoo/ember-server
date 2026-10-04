# STATUS · Weekly Modifier Pack 3 → CoreRpg 1.65.25（D186，2026-10-05）

## 结论

Pack 3 落地为 **CoreRpg 1.65.25** / 裁决 **D186** / `balance_version` **48**。Stage C `p2econ.py --mods --weeks 24` **within range**（2026-10-05 合批）。冒烟 **PASS 12/0**（合批，`docs/tests/smoke-2026-10-05-batch-1.65.23-25.md`）。

## 改动摘要

| 区域 | 内容 |
|---|---|
| 周规则池 | 9→**12**（ISO 周 mod 12；7 图 × 12 规则 = **84** 对） |
| `skirmish` 散兵 | `remap: {melee: ranged}` + `converted: {interval: 0.85, hp: 0.9}`；challenge-only |
| `hexers` 咒潮 | `remap: {melee: caster}` + `converted: {interval: 1.1}`；challenge-only |
| `ballista` 重弩 | `remap: {heavy: ranged}` + `converted: {atk: 1.25, interval: 1.35, speed: 0.8}`；challenge-only |
| 实现面 | 只改双写 `ember-v1-runs.yml` + 单测 + 版本 bump；**无新 Java 字段** |
| 奖励 | 不变（§23.3） |
| 留给 Pack 4 | `heavy→caster`、`caster→melee` |

## 测试

- `mvn test package`（JDK8）：见 RELEASE
- 冒烟：合批 PASS 12/0（2026-10-05 04:57–04:59）
- 资产路径：未改 → 跳过 persist-roundtrip
- MySQL：CoreRpg + CoreGacha connected；SEVERE 0

## 协调

- `COORD-routine-0346` → DONE
- DEPLOY LOCK 与 afk-p1 / mainline-unlocks / signin-online 串行
- 未触碰 gear / AFK / sign-in / cosmetics / `config.yml`

## 备份

- `/workspace/backup/CoreRpg-1.65.24-pre-1.65.25.jar`
