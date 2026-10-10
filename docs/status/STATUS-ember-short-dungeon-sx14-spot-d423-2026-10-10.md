# 状态 · D423 抽测：短征 sx14 烬裂折廊

**日期：** 2026-10-10（上海时间）  
**执行：** 余烬-总控 / 测试  
**裁决：** **PASS**（旁注：地图 raid 壳/WE pending → PARTIAL 不挡绿 · Boss 秒杀导致 RiftCast 消息 SOFT · 首轮 papi 瞬切失败已软复检消）  
**证据：** `/workspace/tmp/d423-sx14-spot/` · summary `99-summary.txt` · soft `20-soft-recheck.txt` · console `d423-sx14-spot-console.log`  
**对照：** D421 sx13 spot · D391 FAIL 退票根因（rooms×3+boss）· DESIGN `DESIGN-ember-short-dungeon-sx14-2026-10-10.md`

## tip / jar

| 项 | 值 |
|----|-----|
| 批 A | `abcf2685` |
| 键 tip | `24dbbcc9`（STATUS 壳 `6567ebf8`） |
| MM tip | `163d9051` |
| 菜单/README | `1c8c8619` |
| jar（通关窗 live） | **`1.65.125-d423.local`** · bv **78** · Enabling 已核 · sha256 `abb66f0d…0f01e8` |
| runId | `sx14-mv1rrnr2-8vu` |
| bot | FreshQ850（通关）· FreshQ851（Q01 门闩） |

## 分项

| # | 项 | 结果 | 证据要点 |
|---|----|------|----------|
| 1 | 十四本菜单 + day_line + fc | **PASS** | `ember_p1_short` **U=烬裂** · `enter sx14` · 挂 `%corerpg_p1_sx14_day_line%` + `%corerpg_p1_sx14_fc%` · I 合计含十四本 |
| 2 | Q01 门闩 | **PASS** | FreshQ851 →「未解锁短征（需本人首通 Q01）」 |
| 2b | `p1 enter sx14` | **PASS** | bound `dungeon_EmberSx14_*` · Director rooms×3+boss |
| 3 | 三房 | **PASS** | r1 裂门庭 / r2 折裂廊 / r3 裂冠甬道 started+cleared |
| 4 | RiftCast | **SOFT** | Boss `spawned hp=180` → `killed after 8.5 s`（秒杀窗短）；MM `EmberSx14RiftCast` + runs circle 裂扫斩/折角压浪 已挂；同构前本 SOFT 不挡绿 |
| 5 | S53 结算 | **PASS** | `short settle … rows+6 reward day=1/3`（有奖3+首通3）；claim=`p1_sx14_day` |
| 6 | 日帽独立 | **PASS** | 软复检 sx14 `今日有奖 1/3`、sx01 `0/3`；`sx_day_left_sum=41`（14×3−1） |
| 6b | 第4次无结算 | **SKIP（旁注）** | 本窗 1 次通关；`daily_reward_cap=3` 同构 |
| 7 | gate_daily 仍拒 | **PASS** | 软复检 `%corerpg_gate_daily%` → `no` |
| 8 | afk / 三开关 / SEVERE | **PASS** | six_slot 三开关仍 true；`daily_kills=2400` 未拧；SEVERE=0；bv78；**≠关观察** |

## 旁注（非硬红）

| 项 | 说明 |
|----|------|
| Boss 秒杀 / Cast 消息 | 8.5s 内击杀；RiftCast 聊天 SOFT；技能表已挂 |
| 地图 | `map_version: ember_short_sx14@d423-pending` · raid 壳 · WE ≥3 强制折角 pending |
| 首轮 papi | 进本/离本瞬切 Failed to find player；hub 软复检全绿 |

## 纪律核对

- Stage2 三开关仍 true · ×0.97 未改 · ≠关观察  
- gate_daily 仍拒 · 未假开旧七线 · afk 未抬  

---

*D423 spot · sx14 烬裂折廊 · PASS · ≠关观察*
