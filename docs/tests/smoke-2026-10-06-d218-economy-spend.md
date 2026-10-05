# Smoke · CoreRpg 1.65.48 / D218（ARCH S2-4 economy spends）

| 项 | 值 |
|---|---|
| 时间 | 2026-10-06 ~03:02–03:05 Asia/Shanghai |
| 服务器 | play PID 2079344；Enabling CoreRpg v1.65.48；CoreRpg + CoreGacha MySQL connected；SEVERE 0 |
| 脚本 | `tools/p1map/d218-economy-spend-smoke.sh` |
| 机器人 | FreshQ779 真实 Q01 首通（creative + weaken 三房 + 首领结算），然后城里 C07 印记兑换 + C03 强化 |
| 结果 | 14 PASS / 4 FAIL — 4 个 FAIL 全是首会话 papi 已知 quirk（Failed to find player）；**二会话 papi 4/4**（q01_state 已首通 / q01_fc 已领取 / q02_open yes / q01_cleared yes）→ PASS |
| C07 | admin +8 T1 印记（1→9）→ 「已用 8 枚 T1 印记兑换 焚烬刃（剩余 1）」+ 到账；log `mark redeem … T1 scorch blade`；9→1 |
| C03 | 快捷栏 0 号 P1 件「强化预览」→ confirm → log `forge enhance … ok: 强化成功 +0 → +1` cost shard 4 / coin 40（数量与 EmberUpgradeRules 一致） |
| 登记拒绝 | log 无 `[P1 pay] registry refused` |
| 资产 | 未改存取路径 → 不跑 persist-roundtrip |
| jar sha256 | `2bc32e21a44e9d1fadd501024e4af533ebc77683f0aea6ce9267cb16dce9f38e` |
