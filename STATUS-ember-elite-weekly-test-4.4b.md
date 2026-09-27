# STATUS · EmberEliteWeekly 阶段 4.4b 粗平衡复测（测试岗）

- 时间：2026-09-27（Asia/Shanghai，约 18:22～18:30）
- 执行：余烬-测试岗
- 依据：`STATUS-ember-elite-mobs-4.4b.md`（怪物岗抬威胁）；对照 `STATUS-ember-elite-weekly-test-4.4.md`
- **未改**玩法数值 / 怪 HP·伤害 / 配置数值
- 脚本：`/tmp/elite-weekly-4.4b-clear.js`（由 4.4 clear 改编，**强制无 buff**）；结果 `/tmp/elite44b-out.json`

## 环境

| 项 | 值 |
|----|-----|
| 游玩服 | `server-runtime` custom Paper · 端口 **25567**（代理 **25565**） |
| MM 内存 | 短重启后加载 41 怪 / 22 技能；yml 为 4.4b：Zombie1400/9、Skeleton900/8、Mix700/9、Brute2800/10、Boss5200/12、BossPulse ~onTimer:80 |
| 测中 OP | RpgBot（uuid `148ec8b6-253f-36f4-bcd7-bf2395b8df80`）临时写入 `ops.json` |
| 测后 | `/deop` 全清、`ops.json=[]`、再 `./stop.sh` → `./start.sh custom` → Done |

## 总评

**FAIL（粗平衡未达标；功能抽查进本+扣票 PASS）**

无 op 战斗 buff、烬刃 T2+7 参照档下可通关：Boss TTK **65s** 落入 45～75；但全本仅 **193s**（目标 480～720）仍偏短，结束剩血 **16%**（目标 25～55）偏低且战中两死。相对 4.4（有 resistance I+strength I：全本 66s / Boss 37s / 近满血）威胁已明显抬升，尚未压进三档同时 PASS。测试岗**不改怪**。

## 分项

| # | 分项 | 结果 | 证据摘要 |
|---|------|------|----------|
| 1 | hub 进本（抽查） | **PASS** | `ElB182437`：`/ember`→「精英试炼」→`正在进入`→`精英试炼开启`/`试炼一` |
| 2 | 扣票×1（抽查） | **PASS** | NI `ticket_ember_elite`：进本前 1 → 后 0 |
| 3 | 三波可清 | **PASS** | 波1→波2（~75s）→Boss（~128s）→`试炼通过`（193s）；顺记孔石稳固石、周首通稳定符×1 |
| 4 | 全本时长 8～12 分（480～720s） | **FAIL** | 实测 **193 s**（约 3.2 分） |
| 5 | Boss TTK 45～75s | **PASS** | 实测 **65 s** |
| 6 | 结束剩血 25～55% | **FAIL** | 结束 **16%**（6.2 / max40）；minHp≈0.55 |
| 7 | 死亡/复活 | 记录 | deaths=**2**；revives=**2**（炽尸×1、Boss×1） |
| 8 | 是否用过任何 buff | **无** | 强制 `FORCE_BUFF=false`；测前 `/effect clear`；**未**给予 resistance / strength / 任何 op 战斗 buff |

## 账号与装备

| 角色 | 账号 |
|------|------|
| OP（测中） | RpgBot |
| 首跑（误判中止） | ElB182262（波1 死后脚本误匹配「则挑战失败」警告提前退出；已修脚本） |
| 正式通关主号 | **ElB182437**（Lv.42，烬刃 T2 **+7**，护符 T2 **+4**，sharpness **3**，誓约 blaze + 天赋 blaze_root/crit1/leech1） |

| 指标 | 4.4b 实测 | 目标 | 4.4 对照（有 buff） |
|------|-----------|------|---------------------|
| 全本时长 | **193 s** | 480～720 s | 66 s |
| Boss TTK | **65 s** | 45～75 s | 37 s |
| 结束 HP% | **16%**（6.2/40） | 25～55% | ≈满血（client≈39） |
| minHp | ≈0.55 | — | ≈36.5 |
| 死亡 / 复活 | 2 / 2 | — | 0 / 0 |
| hits / skill casts | 244 / 22 | — | 78 / 8 |
| buff | **无** | 禁止 | resistance I + strength I |

波段：w1@0s → w2@75s → boss@128s → 通关@193s。

## 与 4.4 对照一句

4.4 有 resistance+strength 时全本 66s/Boss37s/近满血；4.4b 无 buff 同档+7 刃下全本拉到 193s、Boss TTK 已进窗（65s），但全本仍短约 2.5×、结束血与两死说明前两波/脉冲爆发仍需再调（加时长血池、略收爆发）。

## FAIL 建议（勿由测试改数值）

1. **怪物岗**：在保持 Boss TTK≈45～75 的前提下，**加长波1/波2 有效血池或数量**（全本从 ~3 分拉到 8～12 分）；同时略收炽尸/蛮纹/BossPulse 爆发或频率，使结束剩血回到 25～55%、减少「可通但两死压线」。
2. Boss **5200/12 + Pulse80** 对本参照档 TTK 已合适，优先动前两波总威胁与节奏，勿再大幅砍 Boss 血导致 TTK 掉出窗。
3. 功能侧进本/扣票无需为平衡回滚。

## 测后状态

- `ops.json` = `[]`
- 游玩服已 `./stop.sh` → `./start.sh custom` 再起（Done）
- 端口：play **25567**，proxy **25565**

## 报告路径

`/workspace/minecraft/STATUS-ember-elite-weekly-test-4.4b.md`
