# AFK Tier T=3 / T=4 测试报告

- 时间：2026-09-27 15:32–15:42（Asia/Shanghai）
- 执行器：余烬-测试岗
- 总评：**FAIL**（T=3 最低 HP 不达标；T=4 PASS）
- 未改任何玩法数值 / YAML

## 验收标准

| 项 | 目标 |
|---|---|
| deaths | == 0 |
| minHpPct | ≥ 30%（相对 `maxHp`；脚本取 `min(40, 属性「生命」)`，即客户端 hearts_display_cap） |
| ttkMed | ≈ 3s（本报告判 2.0–4.5s 为接近 PASS） |

## T=3（焦土 Lv.30）— **FAIL**

- 账号：`Afk3x7639`（OP 辅助：`RpgBot`）
- 完整 stdout：`/tmp/afk-t3-out.txt`

### AFK_RESULT 关键字段

| 字段 | 值 |
|---|---|
| T / lv / cov | 3 / 30 / blaze |
| deaths | **0** |
| ttkMed | **3.6** s |
| maxHp | 40（属性生命 65.0，cap 到 40） |
| minHpPct | **1**（相对 maxHp=40） |
| endHpPct | 3 |
| avgHpPct | 47 |
| kills / killsPerMin | 35 / 11.7 |
| fightSec | 180 |
| hits / casts / eats | 99 / 18 / 13 |
| mobsSeen / mobInBlockSamples | 36 / 0 |
| gain | shard 31 · bone_dust 15 · core_fragment 1 · soul_dust 1 · calamity 0 |
| padDist / keptInventory / resistAfterRespawn | 0 / true / true |
| gateBelow | 未达 Lv.30 时正确拒绝（当前 Lv.10） |

### 判定

| 项 | 结果 |
|---|---|
| deaths==0 | PASS |
| minHpPct≥30 | **FAIL（1%）** |
| ttkMed≈3s | PASS（3.6，落在 2–4.5） |
| **本场** | **FAIL** |

备注：战斗中曾出现「was shot by 余烬霜骸 Lv30」聊天片段，但 `deaths==0`（可能是濒死回血/未真正死亡计数）。最低血量几乎贴地（1%），生存余量不足。

## T=4（烬原深处 Lv.40）— **PASS**

- 账号：`Afk4x2675`（OP 辅助：`RpgBot`）
- 完整 stdout：`/tmp/afk-t4-out.txt`

### AFK_RESULT 关键字段

| 字段 | 值 |
|---|---|
| T / lv / cov | 4 / 40 / blaze |
| deaths | **0** |
| ttkMed | **3.2** s |
| maxHp | 40（属性生命 96.0，cap 到 40） |
| minHpPct | **33**（相对 maxHp=40） |
| endHpPct | 38 |
| avgHpPct | 53 |
| kills / killsPerMin | 40 / 13.3 |
| fightSec | 180 |
| hits / casts / eats | 143 / 19 / 13 |
| mobsSeen / mobInBlockSamples | 43 / 0 |
| gain | shard 41 · bone_dust 23 · core_fragment 5 · soul_dust 1 · calamity 0 |
| padDist / keptInventory / resistAfterRespawn | 0 / true / true |
| gateBelow | 未达 Lv.40 时正确拒绝（当前 Lv.10） |

### 判定

| 项 | 结果 |
|---|---|
| deaths==0 | PASS |
| minHpPct≥30 | PASS（33%） |
| ttkMed≈3s | PASS（3.2，落在 2–4.5） |
| **本场** | **PASS** |

## 复现命令

```bash
# 前置：RpgBot 需 op（测完必须清回 []）
cd /workspace/minecraft/server-runtime && ./stop.sh
# 写入 ops.json（RpgBot 离线 UUID）后：
export JAVA_HOME=/workspace/minecraft/tools/jdk8u504-b01
./start.sh custom
# 等 25567 + latest.log Done

cd /workspace/minecraft/mineflayer-tests
npm i --no-save mineflayer-pathfinder   # 若缺
T=3 DUR=180 node afk-tier-test.js | tee /tmp/afk-t3-out.txt
T=4 DUR=180 node afk-tier-test.js | tee /tmp/afk-t4-out.txt
```

## ops 恢复说明

1. `node op-cmd.js "/deop RpgBot"` → 聊天确认 `De-opped RpgBot`
2. `./stop.sh` 停游玩服，写 `ops.json` = `[]`，再 `./start.sh custom`
3. 最终确认：`server-runtime/ops.json` 为 `[]`
4. 端口：25565 / 25566 / 25567 均在监听（login/proxy 全程未停）

## 调参建议（仅数据建议，本次未改文件）

T=3 唯一硬伤是 **minHpPct=1%**（avg 47%、0 死亡、TTK 正常）：
- T3 属性对比：攻 +34.0 · 生命 65 · 减伤 33.3%；T4：攻 +30.0 · 生命 96 · 减伤 48.7%。T3 血防明显更薄，且 dmg 串里霜骸（射击）压力大。
- 建议方向（择一或组合，需策划拍板后再改）：
  1. **提高 T3 板甲/符文生命或物防**，目标把实战 minHpPct 抬到 ≥35–40%（留余量）。
  2. **略降 T3 霜骸远程伤害或射速**（聊天有「was shot by 余烬霜骸」）。
  3. 若希望维持威胁感：加强 T3 治疗/吸（誓约 life_steal）或面包回复窗口，而不是只砍 mob 伤害。
- T4 三项均达标，TTK 3.2 / minHp 33% 贴近目标，**暂不建议动 T4**。

## 2026-09-27 怪物岗调参（T=3 焦土，不动 T4）

- 执行：余烬-怪物 · 因 T=3 `minHpPct=1`（聊天「was shot by 余烬霜骸」），TTK 3.6 已合格
- 文件：`plugins/MythicMobs/Mobs/EmberAfk.yml`（`server-runtime/plugins` → 同目录软链）
- 未改：T4 灼尸/凋骸、刷怪点、Health、掉落、Paper/CoreRpg/ops

| Mob | 字段 | 改前 | 改后 | 理由 |
|---|---|---|---|---|
| EmberAfk3Stray 余烬霜骸 | Damage | 4 | **2** | 主因远程射击；腰斩箭压，抬 minHp 余量 |
| EmberAfk3Zombie 余烬焦兵 | Damage | 5 | **4** | 略降近战附带压力 |
| 两者 | Health | 100 / 85 | **不变** | 保 ttkMed≈2.0–4.5 |

- 生效：游玩服短停后 `./start.sh custom`（MM 启动加载；ops 始终 `[]`）。日志：成功加载 35 怪物 / 12 刷怪点。
- **请测试岗复测**：`T=3 DUR=180 node afk-tier-test.js`，目标 deaths=0、minHpPct≥30、ttkMed 仍在 2.0–4.5。勿动 T4。

## 复测 T=3（焦土 Lv.30）— **PASS**

- 时间：2026-09-27 15:46–15:50（Asia/Shanghai）
- 执行器：余烬-测试岗
- 改动说明（怪物岗已热加载，本岗未改 YAML/数值）：EmberAfk3Stray Damage 4→2；EmberAfk3Zombie Damage 5→4
- 账号：`Afk3x3410`（OP 辅助：`RpgBot`，测后已 `/deop` 且 ops 恢复 `[]`）
- 完整 stdout：`/tmp/afk-t3-retest.txt`

### AFK_RESULT 关键字段

| 字段 | 值 |
|---|---|
| T / lv / cov | 3 / 30 / blaze |
| deaths | **0** |
| ttkMed | **2.8** s |
| maxHp | 40（属性生命 65.0，cap 到 40） |
| minHpPct | **70**（相对 maxHp=40） |
| endHpPct | 81 |
| avgHpPct | 84 |
| kills / killsPerMin | 36 / 12 |
| fightSec | 180 |
| hits / casts / eats | 107 / 18 / 13 |
| mobsSeen / mobInBlockSamples | 38 / 0 |
| gain | shard 30 · bone_dust 22 · core_fragment 0 · soul_dust 1 · calamity 0 |
| padDist / keptInventory / resistAfterRespawn | 0 / true / true |
| gateBelow | 未达 Lv.30 时正确拒绝（当前 Lv.10） |

### 判定

| 项 | 结果 |
|---|---|
| deaths==0 | PASS |
| minHpPct≥30 | PASS（70%） |
| ttkMed≈3s | PASS（2.8，落在 2–4.5） |
| **本场** | **PASS** |

### 与上次 T=3 对比

上次 minHpPct=1（FAIL）→ 本次 minHpPct=70（PASS）；deaths 仍为 0；ttkMed 3.6→2.8（仍在合格带）。霜骸/焦兵降伤后生存余量显著抬升，TTK 略加快但仍在目标区间。

### ops / 端口收尾

- `node op-cmd.js "/deop RpgBot"` → 聊天确认 `De-opped RpgBot`
- 停游玩服 → `ops.json` 写回 `[]` → `./start.sh custom`
- 最终：`ops.json` = `[]`；25565 / 25566 / 25567 均在监听

