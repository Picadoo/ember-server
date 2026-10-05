# 冒烟 · CoreRpg 1.65.45（D214 火痕步）· 2026-10-06 02:34–02:38 CST

- 脚本：`tools/p1map/d214-huohen-smoke.sh` + 二会话 papi/CD 复查
- 机器人：FreshQ774（锁）、FreshQ775（Q05+套装切换）、FreshQ776（Q01 首通回归）；结束后 `list=[]`
- 启动：`Enabling CoreRpg v1.65.45`；两条 MySQL connected；SEVERE 0

## 结果

| 检查 | 结果 |
|---|---|
| FreshQ774 kit 文案含「火痕步」「Q05」 | PASS |
| FreshQ774 flex cast「释放 踏步」 | PASS |
| FreshQ774 二会话 papi `kit_step_unlock=no` `kit_step=踏步` | PASS |
| FreshQ775 admin firstclear q01–q05 + 焚烬 2pc → kit「火痕步 · 落点点燃 1」 | PASS |
| FreshQ775 flex cast「释放 火痕步 · 落点点燃」 | PASS |
| FreshQ775 二会话 papi scorch → `unlock=yes` `step=火痕步`；burst → `step=踏步` | PASS |
| FreshQ776 真实 Q01 首通 settle first clear · 无 SEVERE | PASS |
| 首会话 `papi parse` Failed to find player | 已知限制（同 D213），不计 FAIL |

结论：**PASS**。后撤步未做（暂缓）。无资产路径改动 → 不跑 persist-roundtrip。
