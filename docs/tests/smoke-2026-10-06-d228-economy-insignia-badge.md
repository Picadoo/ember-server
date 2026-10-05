# Smoke · CoreRpg 1.65.54 / D228（ARCH S2-8）— 2026-10-06 Asia/Shanghai

Script: `tools/p1map/d228-economy-insignia-badge-smoke.sh` · bot **FreshQ790** · play PID 2158083 · bv58

| 检查 | 结果 |
|---|---|
| 启动：CoreRpg 1.65.54、MySQL×2（CoreRpg + CoreGacha）、economy SoT bv58、无 FAIL-CLOSED | PASS |
| S07 首通徽记：Q01 首通 → `fc_sigmark` 经 grantInsignia | PASS q01 0→3 |
| S08 重打徽记：Q01 普通重打 → `sig_mark` | PASS 3→4 |
| S19 周目标徽：`runs season goal … bounty 3`（EmberSeason.addGoal → grantBadge） | PASS 0→15 |
| S27 国庆兑换徽：10 国庆币 → 2 徽 | PASS 15→17（玩家消息「共 17；兑换额度 2/40」） |
| S18 残响徽记：echo_q01 绑定 + 结算 claim → `rush_sig_q01` | PASS 4→6（`rush settle … rows+2 … claim`） |
| 日志无 `economy grant(Insignia|Badge|Mark) refused` / `untagged mark|insignia row` | PASS |
| SEVERE | 0（整份 latest.log） |

**RESULT PASS=25 FAIL=0.**

前序尝试：FreshQ788 第一跑被我误杀（不计）；FreshQ789 跑出 S07 / S19 / S27 PASS，但脚本在第一局后没 `/dp leave`，第二局进本被释放（「未进入实例」）——修脚本后用 FreshQ790 整跑通过。
没改任何资产存取路径（vault / 断线 / 重启 / 分解 / 撤销 / 快照 / 扭蛋 / 投递语义不变；账本行仍走原 deliver，只换写入入口）→ 不需要 persist-roundtrip。
