# STATUS · EmberEliteWeekly 阶段 4.4 验收（测试岗）

- 时间：2026-09-27（Asia/Shanghai，约 17:55～18:17）
- 执行：余烬-测试岗
- 依据：`docs/design-stage4-elite-weekly.md` §7/§4；`docs/STATUS-ember-elite-weekly-4.4.md`
- 口径补丁：怪物掉落仅小额 NI `mmgive`；周首通稳定符**不走 MM**、只验插件 `weekly-first`；自定义奖励/掉落一律按 **NeigeItems ID** 核对（禁止只认显示名/原版）
- **未改**玩法数值 / 怪 HP·伤害 / 配置数值
- 脚本：`/tmp/elite-weekly-4.4-accept.js`（门控）、`/tmp/elite-weekly-4.4-clear.js`（通关+NI）、结果 `/tmp/elite44-out.json`

## 环境

| 项 | 值 |
|----|-----|
| CoreRpg | **1.15.0**（jar mtime≈17:53） |
| DP | 已加载 **EmberEliteWeekly** |
| 游玩服 | `server-runtime` custom Paper · 端口 **25567**（代理 **25565**） |
| 短重启 | `./stop.sh` → ops 写 RpgBot → `JAVA_HOME=.../jdk8u504-b01 ./start.sh custom` → Done |
| 测后 | `/deop`、`ops.json=[]`、再起 |

## 总评

**CONDITIONAL PASS（功能门控/进本/COMPLETE/NI 发奖/日顶豁免/锻炉文案 PASS；粗平衡 FAIL）**

功能链路可用：hub「精英试炼」一点进本、扣票、三波可清、箱物料+孔石按 NI ID、周首通稳定符仅插件一次/周、progress `elite_weekly`（+80）、quest `elite_weekly_clear` 有日志证据、挂机 afk_caps 世界名单不含本本地图。  
**粗平衡未达标**（全本过快、Boss TTK 偏低、结束剩血过高）→ 交怪物岗调数值；测试岗不改怪。

## 分项

| # | 分项 | 结果 | 证据摘要 |
|---|------|------|----------|
| 1 | 拒绝 Lv&lt;40 | **PASS** | `ElLow3610`：`精英试炼需要余烬 Lv.40（当前 Lv.10）`；无 `/dp start` |
| 1 | 拒绝无票 | **PASS** | `ElNoT3610`：`缺少余烬精英票…`；无命令提示 |
| 1 | 拒绝本周已通关 | **PASS** | `ElClr3610` 预标 `progress elite_weekly` 后拒；通关号 `ElOk1344` 再点亦「本周已通关」 |
| 1 | 拒绝无命令提示 | **PASS** | 拒绝短句均无 `/dp start` / 教玩家指令 |
| 2 | hub 一点进本 | **PASS** | `/ember` →「精英试炼」→ `正在进入` → `精英试炼开启` / `试炼一`（玩家路径 TrMenu→`corerpg elite start`） |
| 2 | 扣票×1 | **PASS** | NI `ticket_ember_elite`：进本前 1 → 后 0 |
| 2 | 三波可清 / 无窒息 | **PASS** | 波1→波2→波3→`试炼通过`；窒息命中 0；复活次数 0（本通关未死） |
| 3 | COMPLETE 箱物料（NI） | **PASS** | 增量（NI ID）：`mat_ember_core_fragment` +5、`mat_ember_shard` +17、`mat_ember_bone_dust` +7、`crystal_ember_enchant` +1（箱基线 4/12/6/1 + 战斗 mmgive 小额） |
| 3 | 孔石（NI） | **PASS** | `gem_ember_gale` ×1（`corerpg loot elite_gem` pick_one） |
| 3 | 周首通稳定符（插件·NI） | **PASS** | `mat_ember_stable_charm` ×1；MM yml **无** `mat_ember_stable_charm`；再跑 `elite weekly-first` → `already`，NI 计数不二发 |
| 3 | progress `elite_weekly` | **PASS** | 日志：`progress ElOk1344 elite_weekly → … ember +80`；`elite status` 本周已通关：是 |
| 3 | quest `elite_weekly_clear` | **PASS** | 日志：`quest event ElOk1344 elite_weekly_clear → …` |
| 4 | 不进 afk_caps | **PASS** | `afk_caps.worlds=[ember_afk, world]`；本本地图 `ember_weekly` 实例；MM 仅 `mmgive` NI 材料；测中无挂机日顶提示（存储为 mysql，未用 yml 计数文件） |
| 5 | 粗平衡全本 8～12 分 | **FAIL** | 实测全本 **66s**（目标 480～720s） |
| 5 | Boss TTK 45～75s | **FAIL**（记 SOFT/偏低） | 实测 Boss TTK **37s**（目标 45～75；差一档偏低） |
| 5 | 结束剩血 25～55% | **FAIL** | 结束 client HP≈39、战中 minHp≈36.5（近似满血）；远高于 25～55% |
| 5 | minHp / 复活 | 记录 | minHp≈36.5；deaths=0；revives=0 |
| 6 | 锻炉/分解文案无「§8命令：/corerpg …」 | **PASS** | 静态 `ember_forge.yml` / `ember_disassemble.yml`；菜单抽查无命令 lore |

### NI 定义抽查（均存在）

`ticket_ember_elite`、`mat_ember_core_fragment`、`mat_ember_shard`、`mat_ember_bone_dust`、`crystal_ember_enchant`、`mat_ember_stable_charm`、`gem_ember_sharp|steady|drain|gale`

### MM 掉落口径

`EmberEliteWeekly.yml`：`PreventOtherDrops` + `corerpg mmgive … mat_ember_shard|bone_dust|core_fragment` 仅小额 NI；**无**稳定符。

## 账号与关键数字

| 角色 | 账号 |
|------|------|
| OP（测中） | RpgBot（uuid `148ec8b6-253f-36f4-bcd7-bf2395b8df80`） |
| 拒绝-低级 | ElLow3610 |
| 拒绝-无票 | ElNoT3610 |
| 拒绝-已通关 | ElClr3610 |
| 通关主号 | **ElOk1344**（Lv.42，烬刃 T2 **+7**，护符 +4，sharpness 3，誓约 blaze） |
| 前次失败对照 | ElRun0620（强化不足 +4 刃，Boss 阵亡未通） |

| 指标 | 值 |
|------|-----|
| 全本时长 | **66 s** |
| Boss TTK | **37 s** |
| 结束 HP / minHp | ≈39 / ≈36.5 |
| 死亡 / 复活 | 0 / 0 |
| hits / skill casts | 78 / 8 |
| NI 增量 | core+5 shard+17 bone+7 crystal+1 charm+1 gem_gale+1 |
| 票 | 1→0 |
| 周 | 2026-W39 |

**测通关时 op buff：** resistance I + strength I（标注；因无 buff 的 +4 档曾在 Boss 团灭）。平衡 FAIL 在有 buff 下仍过快 → 更说明威胁偏低。

## FAIL 建议（勿由测试改数值）

1. **怪物岗**：上调 `EmberEliteBoss`（及必要时波1/波2）有效威胁，使单人烬刃 T2+6～+8 参照档下：全本 **8～12 分**、Boss TTK **45～75s**、结束剩血 **25～55%**。当前 2800/4 在 +7+技能下 TTK≈37s、全本≈1 分。  
2. **测后再验**：无 op 伤害/抗性 buff、仅参照档装备，重跑 TTK/时长。  
3. 功能侧无需为平衡回滚门控/发奖。

## 测后状态

- `ops.json` = `[]`
- 游玩服已 `./stop.sh` → `./start.sh custom` 再起（见当次日志 Done）
- 端口：play **25567**，proxy **25565**

## 报告路径

`/workspace/minecraft/STATUS-ember-elite-weekly-test-4.4.md`
