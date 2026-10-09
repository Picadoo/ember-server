# 状态 · D392 抽测：短征 sx02 锈灯栈道

**日期：** 2026-10-10（上海时间）  
**执行：** 余烬-测试岗  
**裁决：** **PASS**（旁注 MM ranged id）  
**证据：** `/workspace/tmp/d392-sx02-spot/` · summary `99-summary.json`  
**对照：** D391 复测 @`2375c206` · `/workspace/tmp/d391-sx01-retest/`

## tip / jar

| 项 | 值 |
|----|----|
| 键 tip | `878b9938` |
| MM tip | `caab6e25` |
| 菜单 tip | `24ac21e8` |
| jar | `1.65.106-d392.local`（play Enabling 已确认；sha256 `82de57177477cef3e3ce593bc1b5606cb2bf666e5dde388b964706968415ac3d`） |
| bv | **65** |
| runId | `sx02-mv1haa2z-iyh` |

## 分项

| # | 项 | 结果 | 证据要点 |
|---|----|------|----------|
| 1 | 双本菜单 | **PASS** | `ember_p1_short`：烬门+锈灯 · `enter sx01`/`enter sx02` |
| 2 | Q01 门闩 | **PASS** | `D392SxNo` →「未解锁短征（需本人首通 Q01）」 |
| 2b | `p1 enter sx02` | **PASS** | 预留体力 30 · 今日有奖 0/3 · bound |
| 3 | 体力净扣 30 | **PASS** | 90→60 |
| 3b | Director FIGHTING | **PASS** | r1/r2/r3 started+cleared |
| 4 | 三房 | **PASS** | 锈灯栈台 → 分叉灯桥 → 灯塔甬道 均已清空 |
| 4b | BeaconCast | **PASS** | 「【锈灯】灯塔扫线落点——侧移拉开！」+ 蓄力「灯塔扫线」「延时砸地」 |
| 5 | S41 结算 | **PASS** | 施工钉：有奖 **币80+碎4+骨3**；首通另加 **币180+碎6+胚1**；到账币 **260** / 仓碎 **10** / 骨 **3** / 胚 **1**；`short settle … reward day=1/3` |
| 6 | 日帽与 sx01 分开 | **PASS** | sx02 settle day=1/3；随后进 sx01 仍「今日有奖 **0/3**」；claim=`p1_sx02_day` ≠ `p1_sx01_day` |
| 7 | gate_daily 仍拒 | **PASS** | `%corerpg_gate_daily%` → `no`（首跑曾因进服竞态假红，进服后复核） |
| 7b | 旧 EmberDaily | **PASS（纪律）** | 未走 EmberDaily 路径；未知主线本 daily_frost |
| 8 | afk / 三开关 | **PASS** | six_slot enabled/migrate/set_bonus 仍 true；`daily_kills=2400` 未拧 |

## 旁注（非硬红）

| 项 | 说明 |
|----|------|
| MM ranged id | live `ember-v1-runs.yml` `sx02.mobs.ranged.mm=EmberSx02Crossbow`，MM 仅有 `EmberSx02Archer`。三房仍清（近战/精英够），**建议键号改对齐或补 Crossbow** |
| S41 金额 | 以施工 STATUS 钉为准（80/4/3 + 首通 180/6/1）；≠ DESIGN 草案 75/3/5 |
| 地图主题 WE | map_version `ember_short_sx02@d392-pending`；本抽测按白盒坐标通关，**不**验锈灯主题换皮 |

## 不动（本号抽测）

关观察 · 切分支 · login/proxy/MariaDB · 改 live 开关 / afk · 改配置（只读扫）

## 人话

锈灯栈道能从短征双本页进，Q01 门闩真，打完一局扣 30 体力、三房+灯塔 Cast、S41 首通包到账；sx01 日帽没串；旧日常闸和挂机/观察开关没动。远程怪 id 写错一个名字，旁注给键号修。

---

*D392 sx02 spot PASS · tip 878b9938 / caab6e25 / 24ac21e8 · jar 1.65.106-d392.local · ≠关观察。*
