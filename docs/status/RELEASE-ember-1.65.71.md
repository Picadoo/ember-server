# RELEASE · CoreRpg 1.65.71（D252 / ARCH S0-10）

| 项 | 内容 |
|---|---|
| 版本 | CoreRpg **1.65.71** · D252 · `balance_version` **58**（数量不变） |
| 变更 | P1 下旧 `/corerpg warehouse` 对非 OP **只读**（list/info）；deposit / withdraw / unlock 拒绝；枢纽 `EmberVault` 不变 |
| 配置 | `ember-v1.yml legacy_gate.allow.warehouse: ['', list, overview, info]` |
| 验证 | `LegacyGateTest`（含 s010）· 实服非 OP 拒写 / 可看 |
| 回滚 | 换回 1.65.70 jar，并把 `warehouse` allow 改回 `'*'` |
