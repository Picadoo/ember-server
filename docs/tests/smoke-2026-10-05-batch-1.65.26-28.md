# 合批冒烟 · CoreRpg 1.65.26 / 1.65.27 / 1.65.28（2026-10-05 06:19–06:27 CST）

POLICY 01:53（业主：测试攒一堆后再测）。服务器：play PID **1339093**，CoreRpg **1.65.28**（06:18:49 Enabling，CoreRpg + CoreGacha MySQL connected，SEVERE 0）。机器人 FreshQ500–505，结束后全部经 botd 退出（list=[]）。

## 结果

| 包 | 脚本（gitignored，本地）| 机器人 | 结果 | 关键证据 |
|---|---|---|---|---|
| 1.65.28 D189 词缀 Pack 4 | `tools/p1map/d189-affix4-smoke.sh` | FreshQ500（Q01 `venom:r1`）· FreshQ501（Q01 `jailer:r1`）| **PASS 14 / 0** | `r1 affix venom on melee` → `venom + hit=0` → `venom x hit=1`（+/× 轮换生效）→ `affix venom done`；`r1 affix jailer on melee` → `jailer rooted=1` ×2 → `affix jailer done`；进房提示「毒十字 / 禁锢」|
| 1.65.26 D187 周规则 Pack 4 | `tools/p1map/d187-weekly4-smoke.sh` | FreshQ502（Q07 挑战 hexplate）· FreshQ503（Q03 挑战 blades）| **PASS 8 / 0** | `q07c r1 rule hexplate: …,caster (converted x1 hp*1.25 … interval*1.2)`；`q03c r1 rule blades: melee,melee,melee,heavy` |
| 1.65.27 D188 撞墙破绽（+ D173 半血回归）| `tools/p1map/d173-boss-smoke.sh` | FreshQ504（Q01）· FreshQ505（Q07）| **PASS 20 / 0** | 两图三房清空 → 首领现身（Q07 boss hp 3120）→ `boss half-HP phase at 40%`；首领战路径无 SEVERE |

合计 **42 PASS / 0 FAIL**；全程 SEVERE 0。

## 未覆盖 / 说明

- D188 `wall stun` 日志本轮**没有触发**：冒烟脚本把首领削到 40% 后很快结束，期间没有一次冲撞撞到实心墙。撞墙判定（墙 / 跑满 / 悬崖 / 空地边界）由单测覆盖；实机触发留给下一批带站位的冒烟（站在首领区墙边等冲撞）。
- persist-roundtrip：三包均无资产路径变化，不需要。
- Stage C：D187 p2econ `--mods --weeks 98` within range（见 1.65.26 发布）；D188 `out-wallstun-d188.md`；D189 `out-affixpack4-d189.md`。
