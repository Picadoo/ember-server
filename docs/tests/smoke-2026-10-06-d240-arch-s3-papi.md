# Smoke · D240 ARCH S3-11 Papi 分节（CoreRpg 1.65.66）

**日期：** 2026-10-06 Asia/Shanghai · **脚本：** `tools/p1map/d240-arch-s3-papi-smoke.sh`（快照：`tools/p1map/papi-snapshot.sh`）· **bots：** FreshQ824、FreshQ827（FreshQ825 / 826 为脚本调试时用掉）

**PASS 17 / FAIL 0 / SOFT 0** · MySQL×2 · SEVERE 0 · jar `76da71bd44243762…` · play PID 2304067

1. 部署前（1.65.65）：FreshQ824 先跑 D239 Q01 回归（15/0/SOFT1，进本 + 三房 + 结算，给它首通状态），再重进做 756 键快照 `pre.tsv`。
2. 部署后（1.65.66）：FreshQ824 新会话快照 → 与 `pre.tsv` 逐字相同（751 键；剔除按在线分钟跳动的 `p1_online_min` / `p1_online_next` / `activity` 与技能冷却 `kit_charge` / `skill_charge_ready`）。部署后第一次快照（07:04）连剔除项都只差在线分钟 2→3。
3. 新 bot FreshQ827：第 1 会话注册；第 2 会话 PAPI 可解析 + `failrefund` 文案 / `q01_state` map 尾 / `stamina_max` / `gate_elite` / `ember_power_score` 抽查；第 2 ↔ 第 3 会话 751 键逐字相同。
4. 返回 null（PAPI 原样显示字面量）的键集合不变：`corerpg_nosuch`、`ember_nosuch`、`ember_daily_left`、`ember_weekly_left`（后两个是既有缺口，见 STATUS）。
