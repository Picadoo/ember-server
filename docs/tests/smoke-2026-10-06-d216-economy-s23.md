# Smoke · CoreRpg 1.65.47 / D216（ARCH S2-3 economy route）

| 项 | 值 |
|---|---|
| 时间 | 2026-10-06 ~02:53 Asia/Shanghai |
| 服务器 | play PID 2068615；Enabling CoreRpg v1.65.47；CoreRpg + CoreGacha MySQL connected；SEVERE 0 |
| 脚本 | `tools/p1map/d216-economy-s23-smoke.sh` |
| 机器人 | FreshQ778 真实 Q01 首通（creative + weaken 三房 + 首领结算） |
| 结果 | 结算 `q01-… settle … (first clear)` PASS；首会话 papi 已知 quirk（Failed to find player）；**二会话 papi 4/4**（已首通 / yes / 已领取 / q02 yes） |
| 资产 | 未改存取路径 → 不跑 persist-roundtrip |
| jar sha256 | `0e1c7e3d93f09a3462edbfe1f64208d2c2a02debbfc74130d0e0cec82fa03cd8` |

## 覆盖

- 插件加载与双 MySQL
- S01 settle 仍经登记表发放（本窗未改 settle 数量路径，回归）
- 无 SEVERE / claim-error
