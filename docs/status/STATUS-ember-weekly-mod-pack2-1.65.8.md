# STATUS · Weekly Modifier Pack 2 → CoreRpg 1.65.8（D178，2026-10-04）

## 结论

D175 设计落地为 **CoreRpg 1.65.8** / 裁决 **D178** / `balance_version` **32**。Stage C `p2econ.py --mods` 门禁 **DEFERRED**（`tools/p1sim/**` 仍被 afk-p1 / mainline W30 占用，与 Variety Pack 2 同处理）。

## 改动摘要

| 区域 | 内容 |
|---|---|
| 周规则池 | 6→**9**（ISO 周 mod 9；7 图 × 9 规则 = **63** 对） |
| `bolters` 弓潮 | `remap: {caster: ranged}` + `converted: {interval: 0.9}`；challenge-only |
| `shell` 龟甲 | `remap: {caster: heavy}` + `converted: {hp: 1.2, atk: 0.7, interval: 1.35}`；challenge-only |
| `press` 压阵 | `remap: {ranged: melee}` + `converted: {speed: 1.3, hp: 0.85}`；challenge-only |
| 实现面 | 只改双写 `ember-v1-runs.yml` + 单测 + 版本 bump；**无新 Java 字段**（复用现有 remap / converted） |
| 奖励 | 不变（§23.3） |

## 测试

- `mvn -o package`（JDK8 `tools/jdk8u504-b01`）：**249 / 0**
- 冒烟 FreshQ139+（几分钟）：
  - FreshQ139 `bolters` @ q03 challenge force：PASS（聊天「弓潮」+ `modifier forced bolters`）
  - FreshQ140 `shell` @ q03：PASS（「龟甲」+ forced shell）
  - FreshQ141 `press` @ q01：PASS（「压阵」+ forced press）
- 资产路径：未改 vault 交付 → 跳过 full persist-roundtrip
- MySQL：CoreRpg + CoreGacha connected；SEVERE 0

## 协调

- `COORD-routine-1812` → DONE
- DEPLOY LOCK 与 `COORD-afk-p1` 串行：claim → 构建/重启/冒烟 → RELEASED
- 未触碰 `tools/p1sim/**` / AFK / 主线签名 Java / cosmetics / `config.yml`
- Stage C p1sim 门禁 deferred

## 备份

- `/workspace/backup/CoreRpg-1.65.7-pre-1658.jar`
- `/workspace/backup/CoreRpg-1.65.8.jar`

下次测试号：**FreshQ142**+。
