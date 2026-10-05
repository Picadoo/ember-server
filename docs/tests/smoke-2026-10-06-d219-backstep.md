# 冒烟 · CoreRpg 1.65.49（D219 后撤步）· 2026-10-06 03:09–03:14 CST

- 脚本：`tools/p1map/d219-backstep-smoke.sh` + 二会话 papi 复查
- 机器人：FreshQ780（方向开关 + 后撤步 cast）、FreshQ781（火痕·后撤 + 起跳点燃文案）、FreshQ782（Q01 首通回归）；结束后 `list=[]`
- 启动：`Enabling CoreRpg v1.65.49`；两条 MySQL connected；SEVERE 0；play PID 2087958

## 结果

| 检查 | 结果 |
|---|---|
| FreshQ780 kit 文案含「身法方向」「前冲」 | PASS |
| FreshQ780 `/skill dir back` → 后撤 + 身法冷却转满 14s | PASS |
| FreshQ780 flex cast「释放 后撤步」 | PASS |
| FreshQ780 二会话 papi `kit_step_dir` 前冲/后撤 · `kit_step` 踏步/后撤步 | PASS 4/4 |
| FreshQ781 Q05+焚烬 → flex「火痕·后撤 · 起跳点燃」 | PASS |
| FreshQ781 二会话 papi unlock=yes · 火痕步 / 火痕·后撤 | PASS 4/4 |
| FreshQ782 Q01 三房 + 首领 settle · 二会话 dir=前冲 | PASS |
| 首会话 `papi parse` Failed to find player | 已知限制（同 D213/D218），不计 FAIL |

结论：**PASS**。无资产路径改动 → 不跑 persist-roundtrip。
