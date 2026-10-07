# 状态 · D252：S0-10 旧仓库只读（可看不可存取/扩格）

**日期：** 2026-10-07（上海时间）  
**上游：** D251 规格（`b135ab53`）· CoreRpg **1.65.71** · `balance_version` **58**（数值不动）

## 这扇窗做什么

按 D251 方案 A：P1 下普通玩家旧 `/corerpg warehouse` **只能查看**；存入 / 取出 / 花钱扩格一律拒绝。存取请走枢纽「仓库」（`EmberVault`）。

## 改了什么

| 处 | 改动 |
|---|---|
| `LegacyGate.DEFAULT_ALLOW` | `warehouse` 从 `*` → `''/list/overview/info` |
| `ember-v1.yml`（线上 + 打包） | 同上 |
| `WarehouseService` | 纵深：写子命令在 P1 非 OP 时拒绝，并提示用枢纽仓库 |
| 单测 | `LegacyGateTest.s010WarehouseViewOnlyForPlayers` |

## 验收

- `mvn -o test -Dtest=LegacyGateTest`（JDK8）通过
- 实服：非 OP `warehouse deposit/withdraw/unlock` 被拒；`warehouse` / `list` 仍可看
- 不停 login/proxy；play 仅换 jar 后温和重启

## 不变

- 不开六槽；不改养成数值；S0-9 仍 HOLD

## 下一扇候选

- 其它文案/注释薄扫，或 S0-9 若出现新传送证据再开
