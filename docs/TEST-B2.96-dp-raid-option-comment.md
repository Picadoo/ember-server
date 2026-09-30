# B2.96 · DP `EmberRaid/option.yml` 注释改体力口径（含 L30 团戒注释）· 纯静态薄验收

- **总评：PASS**
- **测报时间：**2026-10-01 02:27 Asia/Shanghai
- **岗别：**余烬-测试岗（B2.96 纯静态薄验收）
- **工作区：**`/workspace/minecraft`（main，与 `origin/main` 同步；三个 commit 本地均存在且为 HEAD 祖先，无需 fetch）
- **施工 tip SHA：**`179a7a9`（2026-10-01 02:21:57 CST）
- **设计 / 批准：**`5552822`（02:20:49 CST）/ `273a4f6`（02:21:27 CST）
- **tip：**`docs/design-ember-dp-raid-option-ticket-comment-copy.md`（§1 行为核对、§2 荐案、§2.1 锁定口径、§3 验收、§4 明确不做）
- **是否已 push：否**（本岗仅本地 commit，禁止 push）
- **报告路径：**`docs/TEST-B2.96-dp-raid-option-comment.md`
- **ahead：**相对 `origin/main` ahead 1（本测报本地 commit 后，未 push）
- **`server-runtime/ops.json`：**`[]`（未改动）

## 各点

| # | 条件 | 结果 |
|---|------|------|
| 1 | 施工 commit 只改 `plugins/DungeonPlus/dungeon/EmberRaid/option.yml`，numstat `5 5`，变化行恰为 L3/L4/L17/L19/L30，逐字等于 §2 荐案；无 CR / 行尾空白；L17/L19 行首 4 空格，L30 缩进对照设计；删除行对 §2 旧行块 | **PASS** · numstat `5	5`；hunk 头 `@@ -3,2 +3,2 @@`、`@@ -17 +17 @@`、`@@ -19 +19 @@`、`@@ -30 +30 @@`，`diff` 为 `3,4c3,4` / `17c17` / `19c19` / `30c30`；§2 第二个 yaml 块（5 行）与 live、`179a7a9` 对应行及施工新增行 `cmp` 一致，sha256 相同；删除行与 §2 旧行块 `cmp` 一致；CR 字节 0、行尾空白 0；L3/L4/L30 顶格（0 空格，符合设计「L3、L4、L30 顶格」），L17/L19 恰 4 空格 |
| 2 | tip node 命令原样输出 `same`；L31、键值、L22/L45 `text=`、脚本不在 diff；HEAD 与 `179a7a9` 一致 | **PASS** · 原样命令（HEAD=`179a7a9`）输出 `same`，显式 `179a7a9~1`/`179a7a9` 再跑亦 `same`；去掉注释行后前后逐行相同；L1/2/5/6/16/18/21/22/28/29/31/32/43/44/45 与父版本相同；HEAD、工作区与 `179a7a9` 字节一致，之后无 commit 触及 |
| 3 | 违禁词 | **PASS** · 文件内「扣票」「B0.1」「团本票」「团票」「额外票」「B0.1 已清」「票已废」计数均 0（`rg -n "扣票|B0\.1|团本票|团票|额外票"` 无命中）；设计额外口径：「余烬团本票」0、「周发 1」0、「实践保底」0、「已停用」0；§2.1「不写测试岗边界情况」：新增行「离线」「2 秒」「时序」均 0 |
| 4 | 新注释与 CoreRpg 源码现行行为一致、不夸大 | **PASS（附一处设计文档事实更正 + 边界备注，均不影响注释正确性）** · 发起者付费、首免每周 1 次、50 体力、未进本退还、同周再通扣体力、团戒每人每周 1 枚、`ticket_convert` 均与源码 / 配置一致，见下「行为核对」 |
| 5 | HANDOFF §8 查密码命令输出 0；`ops.json` 为 `[]` | **PASS** · 原样执行（`grep -c` 只出计数）输出 `0`，变量非空 `true`（未打印值）；`ops.json` 为 `[]` |

## 目标行原文（live）

```
3:# 次数：维护备忘：遗留票物/体力口径；现行进本扣发起者体力（本周首次免费，之后按 cash.yml stamina.costs.raid，现行 50）
4:# 维护备忘：扣次由 CoreRpg TicketEntryService/StaminaService 在 corerpg enter 时处理，本文件不扣次；遗留 NI 票物 ticket_ember_raid 按 stamina.ticket_convert 折体力
16:    - "$team-condition{team=true;min=3;max=5;message=§c团本人数 3～5，当前 (<size>)} @system"
17:    # 2026-09-26 等级门槛（CoreRpg progress.yml level_gates.raid=35；DP 逐队员判定；被拒未进本时 CoreRpg 退还本次体力/首免；OP 豁免）
18:    - "$js-condition{text='%corerpg_gate_raid%'=='yes'||'%player_is_op%'=='yes';message=§c团本需要余烬等级 §eLv.35§c · 队伍中有人等级不足 · 打开枢纽 → 角色查看等级} @system"
19:    # 维护备忘：体力/首免已在 corerpg enter 扣除（未进本自动退还）；此处仅保留人数/等级门
22:    - "$message{type=text;text=§8已消耗体力 ×1} @dungeon"
30:# 团戒：周首通保底（每人每周 1 枚）。进本首免按发起者每周 1 次，用完后同周再通扣发起者体力；团戒不重复发；
31:#   周首通去重：corerpg raid grant-ring（raidRingWeek=DailyService.weekId）
43:  - "$command{text=corerpg raid grant-ring %player_name%;console=true} @player"
45:  - "$message{type=text;text=§8同周再通不重复发戒（周首通一次）· T3 不保底} @dungeon"
```

## 命令与输出

```
$ git log --oneline -8
179a7a9 feat(dp): B2.96 EmberRaid option.yml 注释改体力口径
273a4f6 docs: approve B2.96 dp raid option ticket comment (A)
5552822 docs: B2.96 tip dp raid option ticket comment pending A
61785ad docs: close B2.95 dp abyss option ticket comment (PASS)
3c82e34 docs: B2.95 dp abyss option comment test PASS
50a946c feat(dp): B2.95 EmberAbyss option.yml 注释改体力口径
e384dab docs: approve B2.95 dp abyss option ticket comment (A)
37a01ca docs: B2.95 tip dp abyss option ticket comment pending A
$ git merge-base --is-ancestor <5552822|273a4f6|179a7a9> HEAD   → 均为祖先
$ git show --stat 5552822 / 273a4f6   → 均只含本 tip 与 docs/design-ember-content-backlog.md

# 1
$ git show --stat --numstat --format= 179a7a9
5	5	plugins/DungeonPlus/dungeon/EmberRaid/option.yml
 plugins/DungeonPlus/dungeon/EmberRaid/option.yml | 10 +++++-----
 1 file changed, 5 insertions(+), 5 deletions(-)
$ git diff -U0 179a7a9^ 179a7a9 | grep '^@@'
@@ -3,2 +3,2 @@
@@ -17 +17 @@ dungeon-start:
@@ -19 +19 @@ dungeon-start:
@@ -30 +30 @@ dungeon-area: []
$ diff <(git show 179a7a9^:F) <(git show 179a7a9:F) | grep -E '^[0-9]'
3,4c3,4
17c17
19c19
30c30

# 荐案 / 旧行：从 tip §2 第二、第一个 ```yaml 块程序提取（各 5 行）
cmp expected live(L3/4/17/19/30)      → 一致
cmp expected 179a7a9 同行              → 一致
cmp expected 施工新增行(+)              → 一致
cmp design_old 施工删除行(-)            → 一致
cmp design_old 父版本同行               → 一致
sha256（荐案 / live / 施工 tip / 新增行 四者相同）：f253c6de00f0dad78b62a2cb2ccbceb8ed4a5f004000de30ae2ba57f6ae589ad

# 行尾 / 缩进
CR 字节（5 行）：0；全文件 CR：0；行尾空白行数：0
cat -A 行尾：五行均以中文字节 + `$` 结束，无 ` $`、无 `^M$`
3  leading_spaces 0  len 76   末字符「）」
4  leading_spaces 0  len 135  末字符「力」
17 leading_spaces 4  len 103  末字符「）」
19 leading_spaces 4  len 56   末字符「门」
30 leading_spaces 0  len 57   末字符「；」（全角分号，属荐案原文；旧 L30 同样以「；」结尾）
31 leading_spaces 0  len 67   （未改）

# 2（tip §3 原样，仓库根目录，HEAD=179a7a9）
$ node -e 'const y=require("js-yaml"),{execSync:x}=require("child_process"),f="plugins/DungeonPlus/dungeon/EmberRaid/option.yml",g=r=>JSON.stringify(y.load(x("git show "+r+":"+f).toString()));if(g("HEAD~1")!==g("HEAD"))process.exit(1);console.log("same")'
same
exit=0
（HEAD~1/HEAD 换成 179a7a9~1/179a7a9：same，exit=0）
$ diff <(git show 179a7a9^:F | grep -vE '^\s*#') <(git show 179a7a9:F | grep -vE '^\s*#')   → NONCOMMENT_IDENTICAL
L1 L2 L5 L6 L16 L18 L21 L22 L28 L29 L31 L32 L43 L44 L45 same
HEAD==179a7a9（cmp）；worktree==HEAD（cmp）；git log 179a7a9..HEAD -- F → 空

# 3（全文件 / 新增行）
扣票 0/0 · B0.1 0/0 · 团本票 0/0 · 团票 0/0 · 额外票 0/0 · 余烬团本票 0/0 · B0.1 已清 0/0 · 票已废 0/0
周发 1 0/0 · 实践保底 0/0 · 已停用 0/0 · 离线 0/0 · 2 秒 0/0 · 时序 0/0
$ rg -n "扣票|B0\.1|团本票|团票|额外票" F   → 无命中（rc=1）

# 5（HANDOFF §8 原样；grep -c 只出计数）
$ set -a; source secrets/mysql-ember.env; set +a
$ (git diff; git ls-files -o --exclude-standard | xargs -r cat) | grep -c -- "$MYSQL_PASSWORD"
0
[ -n "$MYSQL_PASSWORD" ] → true（未打印值）
$ cat server-runtime/ops.json
[]
```

## 行为核对（第 4 点，只读源码 / 配置；路径前缀 `CoreRpg/src/main/java/town/sunshine/corerpg/`）

| 注释说法 | 源码 / 配置依据 | 判定 |
|---|---|---|
| 进本入口 `corerpg enter`，本文件不扣次 | TrMenu `plugins/TrMenu/menus/ember_raid.yml:79` `command: corerpg enter raid`（文件头 `:2`「S0 体力 50 / 本周首次免费 · 3～5 人」，`:109`「本周首次免费 · 其后 50 体力」）；`CoreRpgPlugin.java:842-843` → `TicketEntryService.java:235-241` → `tryEnter`；option.yml 条件仅人数（L16）与等级 PAPI（L18） | 一致 |
| 扣的是**发起者**体力 | `TicketEntryService.java:130` `stamina.consumeForEnter(player, kind)` 只对执行命令的 `player`；`:159` `dp start-console <player.getName()> EmberRaid`；其余队员无任何 CoreRpg 扣费路径 | 一致 |
| 本周首次免费（按发起者每周 1 次） | `StaminaService.java:205-214` `ensureWeekCredits`：`DailyService.weekId()` 变化时 `setWeeklyGrantCreditRaid(1)`；`StaminaService.java:311-315` RAID 有额度先扣额度（`usedCredit=true`，cost 0），额度为账户字段 `PlayerData.java:720,746-747`，非 NI 物品；周界 `DailyService.java:9,11,23-29`：`ZoneId Asia/Shanghai` + `WeekFields.ISO`（周一为一周首日、最少 4 天），键 `weekBasedYear-Www`，即每周一 00:00 CST 换周 | 一致 |
| 之后按 `stamina.costs.raid`，现行 50 | `plugins/CoreRpg/cash.yml:28` `raid: 50`；src 模板 `CoreRpg/src/main/resources/cash.yml:28` 同为 50，两份 `stamina` 段解析后深度相等；代码缺省 `StaminaService.java:86` `costs.put("raid", 50)`；扣减 `StaminaService.java:316-326` | 一致 |
| 等级门 35；DP 逐队员判定；OP 豁免 | `plugins/CoreRpg/progress.yml:145` `raid: 35`（代码缺省 `ProgressService.java:133`）；发起者 `TicketEntryService.java:106-113` 不足直接拒、不扣；OP `:103,106,125`；option.yml L18 逐队员 PAPI | 一致 |
| 被拒 / 未进本时退还本次体力/首免 | `StaminaService.java:329-345` `refundEnter`：`usedCredit` 且 RAID → `setWeeklyGrantCreditRaid(+1)`（`:338-339`），否则退 `prior.cost`（`:341-342`）；调用点 `TicketEntryService.java:167-169`（dispatch 失败）与 `:186-201`（约 2 秒 `ENTER_VERIFY_TICKS=40`，`:82`） | 一致 |
| 用完首免后同周再通扣发起者体力，无票物参与 | 同周第二次 `consumeForEnter`：额度已 0 → 走 `StaminaService.java:316-326` 扣体力；进本路径 `TicketEntryService.java:100-203` 对 RAID 无任何 NI 票检查（`Kind.RAID` 的 `ticket_ember_raid` 为 legacy id，`TicketEntryService.java:29,34`）；`RaidService` / `RaidHallService` 中 `stamina|refund|ticket` 计数均 0 | 一致 |
| 团戒：周首通保底，每人每周 1 枚，不重复发 | option.yml L43 每名队员 `corerpg raid grant-ring %player_name%`（console）；路由 `CoreRpgPlugin.java:896-897` → `RaidService.cmdRoot` `:55-71`（需 `corerpg.admin`，控制台具备）→ `grantRing` `:93-123`：`weeklyFirst && week.equals(raidRingWeek)` 则提示「本周团戒已领取」不发（`:100-106`），否则发 `acc_ember_raid_ring` ×1 并 `setRaidRingWeek(week)`（`:108-114`）；`raidRingWeek` 存取 `PlayerData.java:756-759`、`PlayerDataStore.java:168,348` | 一致（与发起者/首免/体力无关） |
| 遗留 NI 票物 `ticket_ember_raid` 按 `stamina.ticket_convert` 折体力 | `plugins/CoreRpg/cash.yml:30,34` `ticket_ember_raid: 50`（src 模板 `:30,34` 同值；代码缺省 `StaminaService.java:92`）；`StaminaService.java:125-131` 读取、`:348-373` `convertInventoryTickets` 按张折体力 | 一致（未声称自动，不夸大） |

**设计文档事实更正（不影响本窗注释；注释本身未提此点）：**

- tip §1 与本次任务写「live 配置未设 `raid_ring`，走代码缺省 `weekly_first: true`」——**不准确**。`RaidService.reload()` 读的是 `set.yml`（`RaidService.java:36-50`），而 live `plugins/CoreRpg/set.yml:29-32` **显式配置了** `raid_ring: {ni_id: acc_ember_raid_ring, weekly_first: true, week_key: raidRingWeek}`；src 模板 `CoreRpg/src/main/resources/set.yml` 与 live 字节相同。`cash.yml` 里确实没有 `raid_ring`（`raid` 段只有 `free_tickets` / `ticket_ni_id`）。代码缺省（`RaidService.java:26-27,48-50`：`acc_ember_raid_ring` / `true`）与配置值相同，**行为不变**，故 L30/L31 注释结论成立。`week_key` 键代码未读取，仅文档意义。

**边界备注（不构成不符；tip §2.1 明确不写进注释，此处仅记录）：**

1. 约 2 秒校验时发起者已离线则不退还（`TicketEntryService.java:188-189`）；若 DP 超过约 2 秒才传入，理论上会先退还再进本（时序边界）。与 B2.94/B2.95 同。
2. 首免 / 体力只看发起者：同周换一位首免未用的队员发起，会用该队员自己的首免（tip §1 已述，注释「按发起者每周 1 次」与之一致）。
3. `grantRing` 在目标玩家不在线时直接返回、不发戒也不记周（`RaidService.java:94-96`）：通关结算瞬间掉线的队员当次拿不到团戒，但周标记未写，下次通关仍可领。注释「每人每周 1 枚」为上限描述，不构成承诺问题。

**范围外旧票时代文案（不在本窗，供排窗）：**

- `plugins/CoreRpg/set.yml:27`（及 src 模板同行）：「团票每周 1 张 ⇒ 实践上每角色每周至多 1 次 COMPLETE。」——已不符（现行同周可多次通关，团戒靠 `raidRingWeek` 去重）。
- `CoreRpg/src/main/resources/cash.yml:86` 旧英文旁注「menu promises 每周团本票×1」（live 已为 B2.85 维护备忘口径）；live `plugins/CoreRpg/cash.yml:86` 括注「菜单承诺每周团本票×1」为 B2.85 留档，现 TrMenu 已改为首免口径，可另议。
- option.yml L22 玩家提示「已消耗体力 ×1」（tip §4，排在 `text=` 窗）；`CoreRpgPlugin.java:1356` 兜底文案、深渊状态页背包票数（tip §4 已由总控排期）。
- 旧文档 / 测试脚本（`DESIGN-ember-raid.md:9,33`、`STATUS-ember-abyss-raid-smoke.md`、`mineflayer-tests/raid-combat-smoke.js:155`、`killany-live-retest.js:122` 仍匹配「已扣除余烬团本票」）属历史记录，仅记。

## 旁证

1. **施工范围：**`179a7a9` 只改 `EmberRaid/option.yml` 五行注释；解析结果前后 `same`，非注释行逐行相同；L31 去重注释、L22/L45 玩家提示、条件 / 动作 / 奖励脚本均未动。
2. **精确行文：**live、施工 tip、新增行与 tip §2 荐案四者 sha256 相同；L17/L19 行首 4 空格、L3/L4/L30 顶格，与原行一致；删除行与 tip §2 旧行块逐字一致。
3. **口径：**删除「B0.1:」前缀、「扣票」「余烬团本票」「周发 1 = 周首通实践保底」「团票每周 1 张 ⇒ 同周再通需额外票」旧说法；未写「票已废」「B0.1 已清」；不代表 B0.1 已清（tip §1）。
4. **范围外未动：**`5552822^..HEAD` 期间无 commit 触及 `plugins/CoreRpg`、`CoreRpg/`、`plugins/TrMenu`；其它 option.yml 未动。
5. **做法纪律：**仅静态 git / sed / diff / cmp / node js-yaml / python / rg 读源码；未起服、未 `/dp reload`、未长测、未杀进程；未改任何文件（仅新增本报告）；机密未回显。
6. **时间戳：**tip 写「已批 A（总控 · 2026-10-01 02:21）」，批准 commit `273a4f6` 为 02:21:27 CST，一致；顺序设计 → 批准 → 施工正确。

## 阻塞点

无。

## 回报摘要

- 总评：PASS
- 各点：1 PASS / 2 PASS / 3 PASS / 4 PASS（附设计文档 `raid_ring` 事实更正与边界备注）/ 5 PASS
- 施工 tip SHA：`179a7a9`
- 测报 tip short SHA：待本地 commit 后回报
- 是否已 push：否
- 报告路径：`docs/TEST-B2.96-dp-raid-option-comment.md`
- 阻塞点：无
- ahead：相对 `origin/main` ahead 1（未 push）
- `ops.json`：`[]`
