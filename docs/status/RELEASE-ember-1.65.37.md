# RELEASE · CoreRpg 1.65.37（D200，2026-10-05 17:18 CST）

| 项 | 值 |
|---|---|
| 决策 | D200 · ARCH S0-4（`docs/design/AUDIT-ember-legacy-reachability-2026-10-05.md` §5.2）：旧发奖端纵深防御 |
| 内容 | P1 开着时：① `ProgressService.grantEmberXp / grantPassXp(source)` 一律 0（全部调用点都是旧来源；P1 经验只走 `grantFlatEmberXp`：主线结算 + 主线任务）；② P1 本（`dungeon_EmberQ0*`）和挂机庭以外的世界不发旧击杀币 / 活跃 / 击杀经验 / 悬赏进度；③ 同样的世界里 `corerpg mmgive / mmxp` 不发（latest.log `[legacy_gate] payout skip …`）；④ 公共灾厄使被杀不结算。封 AUDIT L6（旧经验抬 P1 等级）/ L12（旧击杀不封顶）及灾厄发奖 |
| 不变 | P1 关闭时的旧模式；P1 自己的经验 / 掉落 / 挂机庭；`questService.onKill`、`setService.onKill`；P1 数值（balance_version 57 不动，p1sim 不动） |
| 配置 | `plugins/CoreRpg/ember-v1.yml legacy_gate.payout_guard: true`（false = 整段关）、`xp_sources_allow: []`、`kill_payout_worlds: []` |
| 代码 | `LegacyGate.java`（blocksLegacyXp / blocksLegacyKillPayout / blocksCalamitySettle）、`CoreRpgPlugin.java`（legacyPayoutGuard / legacyXpBlocked / legacyKillPayoutBlocked；onDeath、cmdMmCredit）、`ProgressService.java`；`LegacyGateTest` +3 |
| 提交 | `3bac6ec`（代码 + 配置 + 测试 + 源表 D200 + AUDIT 更新） |
| 构建 | JDK 8（`tools/jdk8u504-b01`，class major 52），`mvn -o package` 单测 **314 / 0** |
| jar | `plugins/CoreRpg.jar` sha256 `649cf125a79de3207a458d7ed8b5ef94b65fa358dd366ac918ed6700226f870b`（工作树内容 = `3bac6ec`） |
| 备份 | `/workspace/backup/CoreRpg-1.65.36-pre-1.65.37.jar`（sha256 `d07d9fbd…` = 上一版线上） |
| 服务器 | PID **1733729**（was 1707851；`stop.sh` 优雅停 → `start.sh`），Enabling CoreRpg v1.65.37 17:18:16，两条 MySQL connected，Done 6.3 s，SEVERE 0 |
| 冒烟 | `docs/tests/smoke-2026-10-05-1.65.37-s0payout.md`：合计 **PASS**（旧经验 7 源全 0、mmgive / mmxp / 旧本小怪击杀不发、Q01 首通 + 主线经验 +120 照发）；失败项全是脚本 / 机器人问题，见冒烟文档 |
| 资产回归 | 不需要（没碰仓库 / 断线 / 重启 / 拆解 / 撤销 / 快照 / 扭蛋 / 投递路径） |
| 回退 | `ember-v1.yml legacy_gate.payout_guard: false` 后 `/corerpg reload`；或换回备份 jar |
| 下一窗 | S0-5（竞技场每日计币上限，防日后重开）；`pass` 查看页在 P1 下隐藏 `pass free` 提示；N2 计数器登记 / N3 货币来源-去处-上限表（纯文档） |
