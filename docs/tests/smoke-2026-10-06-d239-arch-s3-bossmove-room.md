# Smoke · D239 ARCH S3-10 BossMove / RoomObjective（CoreRpg 1.65.65）

**日期：** 2026-10-06 Asia/Shanghai · **脚本：** `tools/p1map/d239-arch-s3-bossmove-room-smoke.sh` · **bots：** FreshQ823

**PASS 15 / FAIL 0 / SOFT 1** · MySQL×2 · SEVERE 0 · jar `d358cedccb476aad…` · play PID 2292281

行为不变回归：Q01 进本 + 三房 + 结算。无资产路径 → 无 persist-roundtrip。

SOFT：failrefund PAPI（玩家已 quit，parse 找不到玩家；可选）。
