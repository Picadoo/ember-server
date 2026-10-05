# STATUS · S0-1 + S0-2 旧副本闸门（D198 / CoreRpg 1.65.35）

- **线上：** 1.65.35 / bv57（未变），PID 1686032，2026-10-05 15:49 起；发布凭证 `docs/status/RELEASE-ember-1.65.35.md`。
- **内容：** P1 开着时旧副本对普通玩家关门：`/dp start <旧本>` 由 `%corerpg_gate_*%` / `%corerpg_guildboss_pass%` 回 `no` 拒绝（S0-1）；`/corerpg enter <旧>` 与 `/corerpg elite` 回「P1 模式下旧副本已关闭」（S0-2）。OP / 管理员 / 控制台放行；P1 关时不变。封 AUDIT L1 / L2。
- **测试：** 单测 306/0；冒烟 22/0（`docs/tests/smoke-2026-10-05-1.65.35-s0gate.md`）；不需要 p1sim / persist-roundtrip。
- **回滚：** `/workspace/backup/CoreRpg-1.65.34-pre-1.65.35.jar`（只换 jar，无配置改动）。
- **下一步：** S0-3 路由级默认拒绝白名单。
