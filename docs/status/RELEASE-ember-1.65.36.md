# RELEASE · CoreRpg 1.65.36（D199，2026-10-05 16:27 CST）

| 项 | 值 |
|---|---|
| 决策 | D199 · ARCH S0-3（`docs/design/AUDIT-ember-legacy-reachability-2026-10-05.md` §5.2），随附 S0-6 / S0-7 |
| 内容 | `/corerpg` 路由级默认拒绝：P1 开着、普通玩家只能用 `plugins/CoreRpg/ember-v1.yml legacy_gate.allow` 里的子命令；封 L3 竞技场币 / L4 战令免费补给 / L5 竞技场日箱 / L7 灾厄传送 / L8 战令等级奖 / L9 拆解重铸 / L10 勋阶日领 / L11 盟约 的玩家入口，并显式关旧宝石 socket、晶钻 shop / monthly |
| 不变 | 控制台（DP / MM 发奖脚本）、OP、`corerpg.admin`、P1 关闭时的旧模式；P1 数值（balance_version 57 不动，p1sim 不动） |
| 代码 | `CoreRpg/src/main/java/town/sunshine/corerpg/LegacyGate.java`（canonical / refuseRoute / DEFAULT_ALLOW）、`CoreRpgPlugin.java onCommand → legacyRouteRefused`；`LegacyGateTest` +5 |
| 提交 | `3fac3a4`（代码 + 配置 + 测试 + 源表 D199 + AUDIT 更新） |
| 构建 | JDK 8（`tools/jdk8u504-b01`，class major 52），`mvn -o package` 单测 **311 / 0** |
| jar | `plugins/CoreRpg.jar` sha256 `d07d9fbdf5fcdd3495b11b6a4491428f89b752d64117a66ffe23f45fcdbc87c3`（工作树在 `3fac3a4` 上无改动时构建） |
| 备份 | `/workspace/backup/CoreRpg-1.65.35-pre-1.65.36.jar`（sha256 `91e6069b…` = 上一版线上） |
| 服务器 | PID **1707851**（was 1686032；`server-runtime/stop.sh` 优雅停 → `start.sh`），Enabling CoreRpg v1.65.36 16:27:11，两条 MySQL connected，Done 6.9 s，SEVERE 0 |
| 冒烟 | `docs/tests/smoke-2026-10-05-1.65.36-s0route.md`：**50 PASS / 0 FAIL**（FreshQ721–723），Q01 首通打通 |
| 资产回归 | 不需要（没碰仓库 / 断线 / 重启 / 拆解 / 撤销 / 快照 / 扭蛋 / 投递路径） |
| 回退 | 把 `ember-v1.yml legacy_gate.route_whitelist` 改 `false` 后 `/corerpg reload`；或换回备份 jar |
| 下一窗 | S0-4（旧经验 / 旧击杀 / 灾厄发奖的纵深防御），以及把 `pass` 查看页里指向 `pass free` 的文案在 P1 下隐藏 |
