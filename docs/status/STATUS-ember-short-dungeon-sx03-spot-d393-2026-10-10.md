# 状态 · D393 抽测：短征 sx03 霜雾闸廊

**日期：** 2026-10-10（上海时间）  
**执行：** 余烬-测试岗  
**裁决：** **PASS**（旁注：菜单草案金额 ≠ 施工钉）  
**证据：** `/workspace/tmp/d393-sx03-spot/` · summary `99-summary.json`  
**对照：** D392 spot @`fe15885d` · `/workspace/tmp/d392-sx02-spot/`

## tip / jar

| 项 | 值 |
|----|----|
| 键 tip | `ba308bfe` |
| MM tip | `71e03569` |
| 菜单 tip | `61652e2f` |
| jar | `1.65.107-d393.local`（play Enabling 已确认；sha256 `6c0096afe029a943540db1c2a7fdc7879650c43ba5959d535df976d717324f15`） |
| bv | **66** |
| runId | `sx03-mv1ht7ot-xwv` |

## 分项

| # | 项 | 结果 | 证据要点 |
|---|----|------|----------|
| 1 | 三本菜单 | **PASS** | `ember_p1_short`：烬门+锈灯+霜雾 · `enter sx01`/`sx02`/`sx03` · 日帽分开文案 |
| 2 | Q01 门闩 | **PASS** | `D393SxNo` →「未解锁短征（需本人首通 Q01）」 |
| 2b | `p1 enter sx03` | **PASS** | 预留体力 30 · 今日有奖 0/3 · bound |
| 3 | 体力净扣 30 | **PASS** | 90→60 |
| 3b | Director FIGHTING + 三房 | **PASS** | r1 雾廊入口 / r2 双闸室 / r3 霜窖甬道 started+cleared |
| 4 | FrostCast | **PASS** | MM「【霜雾】霜雾楔冻圈——侧移拉开！」+ CoreRpg 蓄力「霜雾楔」「冻圈」 |
| 5 | S42 结算 | **PASS** | 施工钉：有奖 **币80+碎4+骨3**；首通另加 **币160+碎8+胚1**；到账币 **240** / 仓碎 **+12** / 骨 **+3** / 胚 **+1**；`short settle … reward day=1/3` |
| 6 | 日帽独立 | **PASS** | sx03 settle day=1/3；随后进 sx01 仍「今日有奖 **0/3**」；claim=`p1_sx03_day` |
| 7 | gate_daily 仍拒 | **PASS** | `%corerpg_gate_daily%` → `no`（首扫曾因进服竞态 Failed to find player 假红；hub 补测） |
| 7b | 旧 EmberDaily | **PASS（纪律）** | 未走 EmberDaily 路径 |
| 8 | afk / 三开关 | **PASS** | six_slot enabled/migrate/set_bonus 仍 true；`daily_kills=2400` 未拧；bv66 |

## 旁注（非硬红）

| 项 | 说明 |
|----|------|
| 菜单草案 vs 施工钉 | 菜单 lore 仍写「币70+碎片5+骨尘3（S42 · 结算另号）」；live 经济/实发为施工钉 **80/4/3 + 首通 160/8/1**。**建议菜单岗对齐文案** |
| 地图主题 WE | map_version `ember_short_sx03@d393-pending`；本抽测按白盒坐标通关，**不**验霜雾闸廊主题换皮 / 真双闸形体 |

## 不动（本号抽测）

关观察 · 切分支 · login/proxy/MariaDB · 改 live 开关 / afk · 改配置（只读扫）

## 人话

霜雾闸廊能从短征三本页进，Q01 门闩真，打完一局扣 30 体力、三房+FrostCast（霜雾楔/冻圈）、S42 首通包到账；sx01 日帽没串；旧日常闸和挂机/观察开关没动。菜单奖励预告还写着草案 70/5/3，旁注给菜单岗对齐施工钉。

---

*D393 sx03 spot PASS · tip ba308bfe / 71e03569 / 61652e2f · jar 1.65.107-d393.local · ≠关观察。*
