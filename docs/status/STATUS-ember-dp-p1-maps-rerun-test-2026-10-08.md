# 状态 · P1 副本地图修复后复核实跑（2026-10-08 · 测试岗 · 零配置变动）

**日期：** 2026-10-08 05:51–06:53（上海时间）  
**上游：** 总控派单「P1 副本地图修复后重跑验收」· 修复提交 `704dcebd` · [`STATUS-ember-dp-p1-maps-restore-2026-10-08.md`](STATUS-ember-dp-p1-maps-restore-2026-10-08.md)  
**版本：** CoreRpg 1.65.97 · bv 60（未动）  
**脚本：** `mineflayer-tests/p1-maps-rerun-smoke.js`（本单新增）

## 总评：部分通过（PARTIAL）· 有 1 个阻塞

| 项 | 结果 |
|---|---|
| 1 · `dp-maps-v2-verify.sh`（只检查） | **FAIL / 阻塞**：7 个 `*_v1` 模板**又是软链**（见下） |
| 现网游玩服实际用图 | **PASS**：12 个 `dungeon-caches/dungeon_EmberQ…`（Q01–Q07、R01–R03、B1、B2）region 与 maps-v2 逐字节一致（测前、测后各查一次，12/12） |
| 2 · Q01–Q07 菜单进本实跑 | **PASS 7/7**：全部通关 + 结算，窒息警告 **0**、anomaly **0** |
| 3 · R01–R03 团本冒烟（D303/D304） | **部分**：地形侧 0 窒息 / 0 anomaly；D303/D304 文案在 R01、R03 全部见到；R02 只到进本 + 房清，首领段 **SKIP**；团本首领 3 队 bot 都被打穿（裸装 bot，非地图问题）；R01/R02 有间歇性「房内剩 1 只远程怪 bot 打不到」软卡 |
| 测试号 OP | 未给任何 OP；`ops.json` 测前测后一致 |
| 服务 | 代理 / 登录 / 游玩 / MariaDB 全程在跑，未重启 |

## 阻塞：修复没留在磁盘上（下次重启游玩服就回到旧图）

`dp-maps-v2-verify.sh` 测前（05:52）、测后（06:53）两次都是：

```
[dp-maps] BROKEN  ember_daily_ash_v1 is a symlink -> ember_daily_ash
[dp-maps] BROKEN  ember_daily_crypt_v1 is a symlink -> ember_daily_crypt
[dp-maps] BROKEN  ember_daily_frost_v1 is a symlink -> ember_daily_frost
[dp-maps] BROKEN  ember_daily_rail_v1 is a symlink -> ember_daily_rail
[dp-maps] BROKEN  ember_daily_spire_v1 is a symlink -> ember_daily_spire
[dp-maps] BROKEN  ember_daily_tide_v1 is a symlink -> ember_daily_tide
[dp-maps] BROKEN  ember_daily_v1 is a symlink -> ember_daily
[dp-maps] 7 template(s) not matching (7 missing/symlink); run with --restore-broken or --restore   (exit 1)
```

- 7 个软链的 mtime 都是 **05:48:08**，和 `704dcebd` 的提交时间同一秒；`git reflog` 同一秒有一条 `reset: moving to HEAD`。
- 原因：这 7 个软链**在 git 里有记录**（`git ls-files -s` 为 mode `120000`）。提交后的那次 reset 把工作区按 HEAD 还原，拷进去的 v2 真目录被换回软链。`/workspace/maps-v2/` 下没有 `replaced-*` 备份；v2 zip 和解压缓存还在（`/workspace/maps-v2/x`），可以重新恢复。
- 当前游玩服（05:46:11 启动）在 05:46:24 已把 v2 拷进 dungeon-caches，所以**本次实跑跑的就是 v2 图**（缓存 12/12 与 v2 一致）。但下次重启游玩服时，DP 会从软链（旧 S2 图）重建缓存，问题会原样回来。
- 本岗**未修**（单上写明「不改地图 / FAIL 只留证据」）。建议运维另开窗：停游玩服 → `--restore-broken` → 处理 git 跟踪（例如 `git rm --cached` 这 7 个软链并加进 ignore，或改为入库真实模板），否则任何 `git reset/stash/checkout` 都会再把软链写回来 → 启服 → 重跑 verify。

## Q01–Q07（单人 · 菜单进本）

做法：新号 `P1MapQ1008`（非 OP）。准备步骤全走控制台 FIFO：体力 120、解锁 + 首通标记、抗性 4 / 力量 20、`corerpg p1 heal`。进本路径是 `/ember_p1_adventure` → 点 Q 图标（槽 19–25）；TrMenu 回的 "transaction" 提示是菜单取消点击的正常回包，日志里都有 `bound to dungeon_…`。每房用 `tp` 进触发区；怪由 `runs weaken` 压到 1 血后，**每一只都由 bot 自己近战击杀**（与 D304 测法同口径，不测 DPS）。

| 本 | runId | 进本（菜单图标） | 三房 | 首领 / 结算 | 窒息警告 | anomaly | 卡关 |
|---|---|---|---|---|---|---|---|
| Q01 | q01-muyndh0o-q1x | Q01 灰烬庭院 | 3/3 | 击杀 42.9 s / settle rows+10 | **0** | 0 | 无 |
| Q02 | q02-muynpgre-2nw | Q02 焦骨甬道 | 3/3 | 6.6 s / rows+10 | **0** | 0 | 无 |
| Q03 | q03-muynrpeu-6o7 | Q03 残誓地窖 | 3/3 | 6.7 s / rows+11 | **0** | 0 | 无 |
| Q04 | q04-muyo24hf-hqy | Q04 潮蚀水道 | 3/3 | 2.2 s / rows+9 | **0** | 0 | 无 |
| Q05 | q05-muyo43kh-p45 | Q05 断塔回廊 | 3/3 | 2.0 s / rows+9 | **0** | 0 | 无 |
| Q06 | q06-muyo6b4x-f7w | Q06 霜封哨所 | 3/3 | 2.0 s / rows+9 | **0** | 0 | 无 |
| Q07 | q07-muyo8bwi-ckz | Q07 锈轨矿道 | 3/3 | 2.2 s / rows+11 | **0** | 0 | 无 |

- fall-catch 0、玩家「suffocated in a wall」0。
- 前几轮失败都是测试方法的问题，与地图无关，已改脚本后重跑：06:01–06:09 Q02/Q03/Q04 裸装 bot 在 r1 开打 3–10 s 内被打死（「本局失败：倒下」）；Q05 因体力被扣到 0 进不去（「体力不足（需 30，当前 0）」）。改为进房即 weaken、每 0.7 s heal、每本前补体力后，Q02–Q07 全过。

## R01–R03（3 bot 团 · 菜单进本 · D303/D304）

做法：`P1RrA1008`（队长）、`P1RrB1008`、`P1RrC1008`，都是非 OP；每本前重新组 `dungeon-team`，队长点冒险菜单的团本图标（G=37 / R=39 / Z=41，左键）。首领出现后先 `weaken 0.45` 触发半血段，再等 `蓄力`（聊天 + ActionBar），最后压到 1 血由 bot 击杀。共 5 轮（06:23–06:50），每轮 ≤3 个 bot、1 个 node 进程。

### 地形侧（本单主目标）

| 团本 | 进本 | 房间结果（全部轮次） | 窒息警告 | anomaly |
|---|---|---|---|---|
| R01 锈轨矿道·团 | 5/5 | 房间清掉 11 次；**r2 侧置货仓**卡住 2 次（06:27 剩 1 只、06:39 剩 2 只，均为「近战×5·远程×2」变体） | **0** | 0 |
| R02 霜封哨所·团 | 3/3 | 房间清掉 3 次；卡住 2 次：**r2 军械仓**（06:31，近战×5·远程×2）、**r1 霜旗外院**（06:46，近战×6·远程×1） | **0** | 0 |
| R03 断塔回廊·团 | 1/1 | 3/3 | **0** | 0 |

- 卡住的情况：director 一直报 `active=rX alive=1`（或 2），120 s 里 weaken 了 55 次，每次都是「削弱 1 只」，bot 却始终打不到这只怪。06:27 那次能看到 `矿道弓手` 一直在射队员，可以确认是远程怪。日志里**没有**窒息或 `anomaly`（不是卡在墙里被系统清掉）。
- 06:44 那轮加了服务端选择器定位（`execute … tp @e[type=…,c=1]`）。R01 / R03 三个房间残留怪的位置脚下、头顶都是 `air`（如 `-7.5,64,25.7`、`41.8,64,63.4`、`-9.3,64,102.6`），不在方块里。
- 结论：同一房间在其他轮次能正常清掉，所以是**间歇性**问题，目前**不能判成地图问题**（可能是远程怪站远 + bot 的 tp/攻击时机不对）。这一项**未定**，建议真人进 R01 r2 / R02 r1–r2 各打一局复现。昨夜 D304 的 R02 房间是用控制台 `kill @e` 清的，没有可比的数据。
- 团本首领 3 次都被打穿（R01 两次、R03 一次，开打约 9–12 s 全员倒下；团本伤害 ×1.40，bot 没穿装备）→ **团本通关 0/3，记 SKIP（bot 装备不足，非地图问题）**；周团本次数没扣（「还是 0/3」）。

### D303 / D304 与昨夜结论对照

昨夜结论来源：[`STATUS-ember-weekly-raid-feel-diff-d303.md`](STATUS-ember-weekly-raid-feel-diff-d303.md)（`152ad6bc`，00:52–00:54 只做了进本）、[`STATUS-ember-boss-telegraph-honesty-d304.md`](STATUS-ember-boss-telegraph-honesty-d304.md)（`6e1ae075`，01:08–01:24）。修前的窒息数取自 `server-runtime/logs/2026-10-08-4/5.log.gz`。

| 检查点 | 昨夜 | 今天（v2 图） | 是否翻转 |
|---|---|---|---|
| D303 R01 进本揭示 | PASS | PASS `本局：锈轨矿道·团 · 名片：冲撞撞墙` | 否（保持） |
| D303 R02 进本揭示 | PASS | PASS `本局：霜封哨所·团 · 名片：半血砸地` | 否 |
| D303 R03 进本揭示 | PASS | PASS `本局：断塔回廊·团 · 名片：烬核分摊` | 否 |
| D303 房清软段 `本间：… 已清 · 三房推进` | 昨夜未跑到（只进本） | R01 装卸场 / 侧置货仓 / 废压机房 ✓；R02 霜旗外院 / 军械仓 ✓（06:25）；R03 第一层环院 / 第二层外台 / 第三层横廊 ✓ | 新增覆盖 · PASS |
| D303 首领厅名片 | 未跑到 | R01 `首领厅 · 名片：冲撞撞墙破绽 · 招式有预警` ✓；R03 `首领厅 · 名片：烬核分摊 · 全队靠拢 · 招式有预警` ✓；R02 未到首领 | 新增 · R01/R03 PASS，R02 SKIP |
| D303 半血 cue | 未跑到 | R01 `半血增援 · 四角加怪` ✓；R02 `半血转阶段` 未到首领 → SKIP；R03 按设计无此行 | 新增 · R01 PASS，R02 SKIP |
| D304 Q01 蓄力 chat + ActionBar + 半血短签 | PASS | PASS：`残门蛮兵 蓄力「重斩」…` + 条 `蓄力 · «重斩» · 扇形 · 1s ·先踩再躲` + `半血 · 招式变强` | 否 |
| D304 Q07 蓄力 | 未稳定收到 | 本轮 Q07 首领进场即压血，没等出招 → **SKIP**（不在本单团本范围内） | 无法判定 |
| D304 R02 | 进本 PASS（首领蓄力靠单测推断） | 进本 PASS；首领段 SKIP（房间软卡 / 未到首领） | 否 |
| D304 R01 / R03 首领蓄力 chat + ActionBar | 未测 | R01 `蓄力「重砸」…` + 条 `蓄力 · «重砸» · 圆形 · 1.3s`，另有 06:38 `冲撞 … ·撞墙` → `撞墙！… 眩晕 1.5 秒` 破绽；R03 `蓄力「斧刃横扫」… 先踩进圈再躲开会踉跄` + 条 `蓄力 · «斧刃横扫» · 扇形 · 1.2s ·先踩再躲` | 新增 · PASS |
| 地形：窒息警告 / anomaly | Q01 **7** 条 + 1（`stuck in block SMOOTH_BRICK`）；Q07 **13** 条 + 4（`fell below the room`）；R02 **3** 条 | 全窗口 05:56–06:53：`was suffocating` **0**、`anomaly` **0**、玩家 `suffocated in a wall` **0** | **翻转（FAIL → PASS）** |

D303 / D304 的文案结论都没有翻转；地形问题从「刷进墙里 / 掉出房间」变成 0，这一项翻转了。

## 未覆盖 / SKIP

- 团本首领击杀与结算（R01–R03）：bot 没穿装备扛不住团本伤害 ×1.40 → SKIP。要补需真人或带 T3 装备的号。
- R02 首领段（D303 半血转阶段 / D304 首领蓄力）：两次被房间软卡挡住，一次是脚本被我中途停掉 → SKIP（按单不无限重试）。
- R01/R02 房间间歇性残留远程怪：未定，需真人复现（见上）。
- Q07 D304 蓄力：本轮没等首领出招 → SKIP。
- B1 / B2 连战、国庆 F1：不在本单范围；F1 模板本来就不在（活动已于 10-08 结束），属已知。

## 约束执行

- 没有改平衡、掉落、配置、MM 或地图；没有执行 `--restore*`；游玩服、登录服、代理、MariaDB 都没有停或重启（进程启动时间分别为 05:46:11 / 05:38:18 / 05:38:18 / 05:38:04，测后不变；06:52 控制台 `list` 正常）。
- OP：测试号一个都没给 OP。play `server-runtime/ops.json` 测前测后 `diff` 一致（17 条历史测试号，测前就在，本单没动）；login `ops.json` 测前测后都是 `[]`。
- 内存：串行跑，每次只有 1 个 node 进程（≤3 bot）。每 5 s 采样一次 `free -m`，共 679 次，可用内存最低 **3408 MB**（06:49:35，used 12604 MB），始终没有低于 1 GB。
- 新注册的测试号：`P1MapQ1008`、`P1RrA1008`、`P1RrB1008`、`P1RrC1008`（AuthMe 密码在已 ignore 的 `secrets/bot-passwords.json`）。它们的运行时数据（players/*.yml、DB 行）没有入库。
- 证据原件（不入库）：`/tmp/p1rerun/*.json|*.out`、`chat-<bot>.log`、`mem.log`、`window.log`（latest.log 05:56 之后的部分）。
