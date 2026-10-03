# B2.97 · DP `EmberEliteWeekly/option.yml` 注释改体力口径 · 纯静态薄验收

- **总评：PASS**
- **测报时间：**2026-10-01 02:36 Asia/Shanghai
- **岗别：**余烬-测试岗（B2.97 纯静态薄验收）
- **工作区：**`/workspace/minecraft`（main，与 `origin/main` 同步；三个 commit 本地均存在且为 HEAD 祖先，无需 fetch）
- **施工 tip SHA：**`79257fd`（2026-10-01 02:30:38 CST）
- **设计 / 批准：**`aeb2bc1`（02:29:07 CST）/ `810f0cd`（02:30:07 CST）
- **tip：**`docs/design/design-ember-dp-elite-option-ticket-comment-copy.md`（§1 行为核对、§2 荐案、§3 验收、§4 明确不做、§5 顺带发现）
- **是否已 push：否**（本岗仅本地 commit，禁止 push）
- **报告路径：**`docs/tests/TEST-B2.97-dp-elite-option-comment.md`
- **ahead：**相对 `origin/main` ahead 1（本测报本地 commit 后，未 push）
- **`server-runtime/ops.json`：**`[]`（未改动）

## 各点

| # | 条件 | 结果 |
|---|------|------|
| 1 | 施工 commit 只改 `plugins/DungeonPlus/dungeon/EmberEliteWeekly/option.yml`，numstat `4 4`，变化行恰为 L3/L4/L16/L18，逐字等于 §2 荐案；无 CR / 行尾空白；L16/L18 行首 4 空格；删除行对 §2 旧行块 | **PASS** · numstat `4	4`；hunk 头 `@@ -3,2 +3,2 @@`、`@@ -16 +16 @@`、`@@ -18 +18 @@`，`diff` 为 `3,4c3,4` / `16c16` / `18c18`；§2 第二个 yaml 块（4 行）与 live、`79257fd` 对应行及施工新增行 `cmp` 一致，sha256 相同；删除行与 §2 旧行块 `cmp` 一致；CR 字节 0、行尾空白 0；L3/L4 顶格，L16/L18 恰 4 空格 |
| 2 | tip node 命令原样输出 `same`；键值、`text=`（含 L17 message、L20）、脚本不在 diff；HEAD 与 `79257fd` 一致 | **PASS** · 原样命令（HEAD=`79257fd`）输出 `same`，显式 `79257fd~1`/`79257fd` 再跑亦 `same`；去掉注释行后前后逐行相同；L1/2/5/15/17/19/20/21/26/27/29/35/36/37 与父版本相同；HEAD、工作区与 `79257fd` 字节一致，之后无 commit 触及 |
| 3 | 违禁词（全文件计数） | **PASS** · 「扣票」「B0.1」「精英票」「有票」「持有上限」「B0.1 已清」「票已废」「开放」「上线」全文件计数均 0（含未改动的非注释行，均无出现）；设计额外口径：「余烬精英票」0、「周一发 1」0、「硬顶」0、`hard_cap` 0、「已停用」0；§4「不写测试岗边界情况」：新增行「离线」「2 秒」「可进」均 0 |
| 4 | 新注释与 CoreRpg 源码现行行为一致、不夸大 | **PASS（附一处措辞前提更正 + 边界备注，均不影响注释结论）** · PAPI 门只判等级 + 本周未通关、首免每周 1 次、40 体力、发起者预检不扣、未进本退还、每人每周通关标记、`ticket_convert` 均与源码 / 配置一致，见下「行为核对」 |
| 5 | HANDOFF §8 查密码命令输出 0；`ops.json` 为 `[]` | **PASS** · 原样执行（`grep -c` 只出计数）输出 `0`，变量非空 `true`（未打印值）；`ops.json` 为 `[]` |

## 目标行原文（live）

```
2:# 启动：菜单 corerpg elite start → dp start EmberEliteWeekly（玩家不可见 /dp start）
3:# 次数：维护备忘：遗留票物/体力口径；现行进本扣发起者体力（本周首次免费，之后按 cash.yml stamina.costs.elite，现行 40）；每人每周通关 1 次
4:# 维护备忘：扣次由 CoreRpg TicketEntryService/StaminaService 在 corerpg enter 时处理，本文件不扣次；遗留 NI 票物 ticket_ember_elite 按 stamina.ticket_convert 折体力
15:    - "$team-condition{team=true;min=1;max=2;message=§c精英试炼人数 1～2，当前 (<size>)} @system"
16:    # 等级 + 本周未通关（%corerpg_gate_elite%，不查票/首免）；OP 豁免；发起者在 CoreRpg 先判同两项，不符不扣；队员被拒未进本时 CoreRpg 退还本次体力/首免
17:    - "$js-condition{text='%corerpg_gate_elite%'=='yes'||'%player_is_op%'=='yes';message=§c精英试炼需要余烬 Lv.40，且本周尚未通关} @system"
18:    # 维护备忘：体力/首免已在 corerpg enter 扣除（未进本自动退还）；此处仅保留人数/等级门
20:    - "$message{type=text;text=§e精英试炼开启。§7本周只有一次——词缀会咬人。} @dungeon"
36:  - "$command{text=corerpg progress %player_name% elite_weekly;console=true} @player"
```

## 命令与输出

```
$ git log --oneline -8
79257fd feat(dp): B2.97 EmberEliteWeekly option.yml 注释改体力口径
810f0cd docs: approve B2.97 dp elite option ticket comment (A)
aeb2bc1 docs: B2.97 tip dp elite option ticket comment pending A
2dc9b39 docs: close B2.96 dp raid option ticket comment (PASS)
d62e20c docs: B2.96 dp raid option comment test PASS
179a7a9 feat(dp): B2.96 EmberRaid option.yml 注释改体力口径
273a4f6 docs: approve B2.96 dp raid option ticket comment (A)
5552822 docs: B2.96 tip dp raid option ticket comment pending A
$ git merge-base --is-ancestor <aeb2bc1|810f0cd|79257fd> HEAD   → 均为祖先
$ git show --stat aeb2bc1 / 810f0cd   → 均只含本 tip 与 docs/design/design-ember-content-backlog.md

# 1
$ git show --stat --numstat --format= 79257fd
4	4	plugins/DungeonPlus/dungeon/EmberEliteWeekly/option.yml
 plugins/DungeonPlus/dungeon/EmberEliteWeekly/option.yml | 8 ++++----
 1 file changed, 4 insertions(+), 4 deletions(-)
$ git diff -U0 79257fd^ 79257fd | grep '^@@'
@@ -3,2 +3,2 @@
@@ -16 +16 @@ dungeon-start:
@@ -18 +18 @@ dungeon-start:
$ diff <(git show 79257fd^:F) <(git show 79257fd:F) | grep -E '^[0-9]'
3,4c3,4
16c16
18c18

# 荐案 / 旧行：从 tip §2 第二、第一个 ```yaml 块程序提取（各 4 行）
cmp expected live(L3/4/16/18)      → 一致
cmp expected 79257fd 同行           → 一致
cmp expected 施工新增行(+)           → 一致
cmp design_old 施工删除行(-)         → 一致
cmp design_old 父版本同行            → 一致
sha256（荐案 / live / 施工 tip / 新增行 四者相同）：9253a6373cb9a2692e17accefd0ae8d8fcc4be3cdee188d333caf642f5041d88

# 行尾 / 缩进
CR 字节（4 行）：0；全文件 CR：0；行尾空白行数：0
cat -A 行尾：四行均以中文字节 + `$` 结束，无 ` $`、无 `^M$`
3  leading_spaces 0  len 88   末字符「次」
4  leading_spaces 0  len 136  末字符「力」
16 leading_spaces 4  len 102  末字符「免」
18 leading_spaces 4  len 56   末字符「门」

# 2（tip §3 原样，仓库根目录，HEAD=79257fd）
$ node -e 'const y=require("js-yaml"),{execSync:x}=require("child_process"),f="plugins/DungeonPlus/dungeon/EmberEliteWeekly/option.yml",g=r=>JSON.stringify(y.load(x("git show "+r+":"+f).toString()));if(g("HEAD~1")!==g("HEAD"))process.exit(1);console.log("same")'
same
exit=0
（HEAD~1/HEAD 换成 79257fd~1/79257fd：same，exit=0）
$ diff <(git show 79257fd^:F | grep -vE '^\s*#') <(git show 79257fd:F | grep -vE '^\s*#')   → NONCOMMENT_IDENTICAL
L1 L2 L5 L15 L17 L19 L20 L21 L26 L27 L29 L35 L36 L37 same
HEAD==79257fd（cmp）；worktree==HEAD（cmp）；git log 79257fd..HEAD -- F → 空

# 3（全文件 / 新增行）
扣票 0/0 · B0.1 0/0 · 精英票 0/0 · 有票 0/0 · 持有上限 0/0 · B0.1 已清 0/0 · 票已废 0/0 · 开放 0/0 · 上线 0/0
余烬精英票 0/0 · 周一发 1 0/0 · 硬顶 0/0 · hard_cap 0/0 · 已停用 0/0 · 离线 0/0 · 2 秒 0/0 · 可进 0/0
$ rg -n "扣票|B0\.1|精英票|有票|持有上限" F   → 无命中（rc=1）
$ rg -n "开放|上线" F                          → 无命中（rc=1）

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
| `%corerpg_gate_elite%` = 等级 + 本周未通关，不查票 / 首免 | PAPI 注册：`CoreRpgExpansion.java:49-55`，`gate_` 前缀取 `gateId`，`elite` 单独走 `EliteService.passesGate`；`EliteService.java:40-47` 只判 `getEmberLevel() >= ps.gateLevel("elite")` 与 `!isClearedThisWeek`；`isClearedThisWeek` `:49-53` 查 `lootWeekMarks` 含 `elite_weekly_clear=<weekId>`；方法内无 NI / 首免 / 体力调用（`:36-39` 代码注释亦写明不再要求持票） | 一致 |
| 等级门 40 | live `plugins/CoreRpg/progress.yml:147` `elite: 40`；src 模板 `CoreRpg/src/main/resources/progress.yml:147` 同为 40；读取 `ProgressService.java:127-134`；代码缺省两处：`ProgressService.java:132-133`（仅当整个 `level_gates` 段缺失时）与 `EliteService.java:19,43`（`GATE_LEVEL=40`，仅当 `ProgressService` 为 null 时） | 一致 |
| 进本扣**发起者**体力；本文件不扣次 | 菜单 `plugins/TrMenu/menus/ember_hub.yml:256` `command: corerpg elite start` → `CoreRpgPlugin.java:829-830` → `EliteService.cmdRoot` `:117-122` → `cmdStart` `:63-69` → `TicketEntryService.tryEnter(player, ELITE)`；`TicketEntryService.java:130` 只对发起者 `consumeForEnter`，`:159` `dp start-console <发起者> EmberEliteWeekly`；option.yml 条件仅人数（L15）与 PAPI 门（L17） | 一致（措辞见下「前提更正」） |
| 本周首次免费 | `StaminaService.java:205-214` 换周时 `setWeeklyGrantCreditElite(1)`；`StaminaService.java:306-310` ELITE 先扣额度（`usedCredit=true`，cost 0）；账户字段 `PlayerData.java:719,743-744`，非 NI 物品；周界 `DailyService.java:9,11,23-29`（Asia/Shanghai + `WeekFields.ISO`，周一 00:00 CST 换周） | 一致 |
| 之后按 `stamina.costs.elite`，现行 40 | live `plugins/CoreRpg/cash.yml:26` `elite: 40`；src 模板 `CoreRpg/src/main/resources/cash.yml:26` 同为 40，两份 `stamina` 段解析后深度相等；代码缺省 `StaminaService.java:84` `costs.put("elite", 40)`；扣减 `:316-326` | 一致 |
| 发起者在 CoreRpg 先判同两项，不符不扣 | `TicketEntryService.java:106-113` 非 OP 先判 `gateLevel("elite")`，不足 `return` 于扣费前；`:115-121` ELITE 再判 `elite.isClearedThisWeek(data)`，已通关提示「本周已通关精英试炼，下周再来」并 `return`；扣费在其后 `:124-136` | 一致 |
| OP 豁免 | `TicketEntryService.java:103,106,125` OP/`corerpg.admin` 跳过预检与扣费；option.yml L17 `%player_is_op%` | 一致 |
| 队员被拒未进本时退还本次体力/首免 | `StaminaService.java:329-345` `refundEnter`：`usedCredit` 且 ELITE → `setWeeklyGrantCreditElite(+1)`（`:336-337`），否则退 `prior.cost`；调用点 `TicketEntryService.java:167-169`（dispatch 失败）与 `:186-201`（约 2 秒 `ENTER_VERIFY_TICKS=40`，`:82`） | 一致 |
| 每人每周通关 1 次 | 通关标记写入：option.yml L36 对每名队员（`@player`）`corerpg progress %player_name% elite_weekly` → `ProgressService.java:471-481`：已含 `elite_weekly_clear=<weekId>` 则跳过（`:475-477`，且 `:490` 不重复发经验），否则 `addLootWeekMark(CLEAR_MARK, week)` 并落盘（`:479-480`）；`PlayerData.java:258-263` 同键只保留最新周值；标记后该玩家 `passesGate` 为假，DP L17 与发起者预检都会拦下 | 一致（按人计，队员同样被标记） |
| 遗留 NI 票物 `ticket_ember_elite` 按 `stamina.ticket_convert` 折体力 | live `plugins/CoreRpg/cash.yml:30,35` `ticket_ember_elite: 40`（src 模板同值；代码缺省 `StaminaService.java:93`）；`StaminaService.java:125-131` 读取，`:348-373` `convertInventoryTickets` 按张折体力 | 一致（未声称自动，不夸大） |
| 旧 L3「持有上限 1」已删（hard_cap 无消费方） | `TicketGrantService.java:35,69,73` 只写入 `eliteHardCap`，全源码无读取；`grantEliteIfNeeded` 返回 0、`needsEliteGrant` 恒 false（`TicketGrantService.java:113,118`） | 与 tip §1 一致 |

**前提更正（不影响 PASS）：**

1. **入口命令名：**新 L4「扣次…在 corerpg enter 时处理」、L18「已在 corerpg enter 扣除」沿用了系列句式，但精英的玩家入口实际是 **`corerpg elite start`**（`plugins/TrMenu/menus/ember_hub.yml:256`；本文件 L2 也写明），经 `EliteService.cmdStart`（`EliteService.java:63-69`）进入同一个 `TicketEntryService.tryEnter`。`corerpg enter elite` 也能走到同一方法（`CoreRpgPlugin.java:842-843`、`TicketEntryService.java:30,52` 按 key `elite` 解析）。扣费代码路径相同，所以注释的**行为**描述无误，只是命令名不是菜单实际用的那条。如需精确，可在后续窗改为「corerpg elite start / enter」；本窗按荐案逐字施工，不判为不符。
2. **等级门「代码缺省 40」的适用条件：**`ProgressService.java:132-133` 的缺省 40 只在整个 `level_gates` 段缺失时生效；若该段存在但缺 `elite` 键，`gateLevel` 返回 0（`ProgressService.java:59-62`），不会回落到 `EliteService.GATE_LEVEL`（后者只在 `ProgressService` 为 null 时用，`EliteService.java:43`）。live 与 src 模板都显式配置了 `elite: 40`，现行无影响。

**边界备注（不构成不符；tip §4 明确不写进注释，此处仅记录）：**

1. 约 2 秒校验时发起者已离线则不退还（`TicketEntryService.java:188-189`）；DP 超过约 2 秒才传入时理论上会先退还再进本（与 B2.94–B2.96 同）。
2. **离线队员：**`cmdProgress` 对不在线目标直接返回「玩家不在线」（`ProgressService.java:465-466`），结算瞬间掉线的队员不会被写通关标记，本周仍可再进再通；同理 `cmdWeeklyFirst` 对离线玩家不发稳定符也不记标（`EliteService.java:85-89`）。
3. **非发起者队员：**不付体力 / 首免，但 COMPLETE 时同样被写通关标记（reward-script 按 `@player` 逐人执行），之后本周无法再进（DP L17 拦下）；首免只消耗发起者自己的额度，换人发起会用该队员自己的首免。
4. 通关标记只在 COMPLETE 写入；失败 / 中途离本不写标记，可再进（首免用完后扣体力）。

**范围外旧票时代文案（精英相关，不在本窗；已在 tip §5 或 B2.94–B2.96 报告列过的不重复展开）：**

- 代码注释：`EliteService.java:37-38`「B0.1: 票改由 TicketEntryService 在 start 前 consumeExact」、`EliteService.java:61`「B0.1：NI id 扣票后 console start-console」、`CoreRpgExpansion.java:51`「票在 TicketEntryService 扣」——现行 `tryEnter` 不扣票，属过时代码注释（非玩家可见）。
- 菜单 `plugins/TrMenu/menus/ember_hub.yml:249`「每周 1 次」：与 option.yml L20「本周只有一次」同类，现行是每人每周通关一次、未通关可再进；可并入 tip §5 的 `text=` 窗。
- tip §5 已列：`cash.yml` `elite.hard_cap` 与 L92「持有硬顶 1」（src 模板 `CoreRpg/src/main/resources/cash.yml:92` 仍为旧文案「不进商城日票池；持有硬顶 1」）、`/corerpg elite status` 的「精英票」计数（`EliteService.java:136-139`）、L20「本周只有一次」。
- NI 物品 `plugins/NeigeItems/Items/ember-dungeon-tickets.yml:53-55` `ticket_ember_elite`「余烬精英票」仍存在：作为遗留票物定义保留（`ticket_convert` 需要），仅记。

## 旁证

1. **施工范围：**`79257fd` 只改 `EmberEliteWeekly/option.yml` 四行注释；解析结果前后 `same`，非注释行逐行相同；L2 管理备注、L17 PAPI 门与 message、L20 开本提示、奖励脚本均未动。
2. **精确行文：**live、施工 tip、新增行与 tip §2 荐案四者 sha256 相同；L16/L18 行首 4 空格、L3/L4 顶格，与原行一致；删除行与 tip §2 旧行块逐字一致。
3. **口径：**删除「B0.1:」前缀、「扣票」「余烬精英票」「周一发 1 · 持有上限 1」「有票」旧说法；未写「票已废」「B0.1 已清」；全文件无「开放」「上线」，未暗示精英壳已开放。
4. **范围外未动：**`aeb2bc1^..HEAD` 期间无 commit 触及 `plugins/CoreRpg`、`CoreRpg/`、`plugins/TrMenu`；其它 option.yml 未动。
5. **做法纪律：**仅静态 git / sed / diff / cmp / node js-yaml / python / rg 读源码；未起服、未 `/dp reload`、未长测、未杀进程；未改任何文件（仅新增本报告）；机密未回显。
6. **时间戳：**tip 写「已批 A（总控 · 2026-10-01 02:30）」，批准 commit `810f0cd` 为 02:30:07 CST，一致；顺序设计 → 批准 → 施工正确。

## 阻塞点

无。

## 回报摘要

- 总评：PASS
- 各点：1 PASS / 2 PASS / 3 PASS / 4 PASS（附入口命令名与等级门缺省两处前提更正、边界备注）/ 5 PASS
- 施工 tip SHA：`79257fd`
- 测报 tip short SHA：待本地 commit 后回报
- 是否已 push：否
- 报告路径：`docs/tests/TEST-B2.97-dp-elite-option-comment.md`
- 阻塞点：无
- ahead：相对 `origin/main` ahead 1（未 push）
- `ops.json`：`[]`
