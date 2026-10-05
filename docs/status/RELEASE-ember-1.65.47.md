# RELEASE · CoreRpg 1.65.47（D216，2026-10-06）

| 项 | 值 |
|---|---|
| 决策 | D216 · ARCH S2-3：签到/在线金额读登记表；mat/mark/XP grant*；未登记发放扫描 |
| 内容 | EmberEconomy.grantMark/grantXp/grantMat/sourceForGrant；EmberSignService S23 amount + S24 合计校验；deliver 路由 sign/online + MAT/MARK/XP；S04/S05 键前缀；p1 addCoin 白名单扫描 |
| 不变 | bv57；所有数量；未改 p1sim；后撤步仍仅 §4b 备忘 |
| 测试 | unit **380 / 0**（+4：S23/S24、grantMark/Mat、settle 键映射、addCoin 扫描） |
| 配置 | 无数值改动（signin/online yml 与金样一致） |
| jar sha256 | `0e1c7e3d93f09a3462edbfe1f64208d2c2a02debbfc74130d0e0cec82fa03cd8`（JDK 8u504，class 52） |
| 备份 | `/workspace/backup/CoreRpg-1.65.46-pre-1.65.47.jar` |
| 服务器 | 02:50 重启；Enabling CoreRpg v1.65.47；CoreRpg / CoreGacha MySQL connected；SEVERE 0；play PID 2068615 |
| 冒烟 | FreshQ778 PASS（docs/tests/smoke-2026-10-06-d216-economy-s23.md）；二会话 papi 4/4 |
| 下一步 | S2-4 工坊/深渊/天赋 spend；徽记进 p1sim；yaml 真源 |
