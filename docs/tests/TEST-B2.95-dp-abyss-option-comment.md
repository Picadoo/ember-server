# B2.95 · DP `EmberAbyss/option.yml` 注释改体力口径 · 纯静态薄验收

- **总评：PASS**
- **测报时间：**2026-10-01 02:19 Asia/Shanghai
- **岗别：**余烬-测试岗（B2.95 纯静态薄验收）
- **工作区：**`/workspace/minecraft`（main，与 `origin/main` 同步；三个 commit 本地均存在且为 HEAD 祖先，无需 fetch）
- **施工 tip SHA：**`50a946c`（2026-10-01 02:14:37 CST）
- **设计 / 批准：**`37a01ca`（02:13:38 CST）/ `e384dab`（02:14:05 CST）
- **tip：**`docs/design/design-ember-dp-abyss-option-ticket-comment-copy.md`（§1 行为核对、§2 荐案、§3 验收、§4 明确不做）
- **是否已 push：否**（本岗仅本地 commit，禁止 push）
- **报告路径：**`docs/tests/TEST-B2.95-dp-abyss-option-comment.md`
- **ahead：**相对 `origin/main` ahead 1（本测报本地 commit 后，未 push）
- **`server-runtime/ops.json`：**`[]`（未改动）

## 各点

| # | 条件 | 结果 |
|---|------|------|
| 1 | 施工 commit 只改 `plugins/DungeonPlus/dungeon/EmberAbyss/option.yml`，numstat `4 4`，变化行恰为 L3/L4/L18/L20，逐字等于 §2 荐案；无 CR / 行尾空白；L18/L20 行首 4 空格；删除行对 §2 旧行块 | **PASS** · numstat `4	4`；hunk 头 `@@ -3,2 +3,2 @@`、`@@ -18 +18 @@`、`@@ -20 +20 @@`，`diff` 为 `3,4c3,4` / `18c18` / `20c20`；§2 第二个 yaml 块（4 行）与 live、`50a946c` 的 L3/4/18/20 及施工新增行 `cmp` 一致，sha256 相同；删除行与 §2 旧行块 `cmp` 一致；CR 字节 0、行尾空白 0；L3/L4 顶格，L18/L20 恰 4 空格 |
| 2 | tip node 命令原样输出 `same`；键值 / L22、L23 `text=` / 脚本不在 diff；HEAD 与 `50a946c` 一致 | **PASS** · 原样命令（HEAD=`50a946c`）输出 `same`，显式 `50a946c~1`/`50a946c` 再跑亦 `same`；去掉注释行后前后逐行相同；L1/L2/L5/L17/L19/L21–L25 与父版本相同；HEAD、工作区与 `50a946c` 字节一致，之后无 commit 触及 |
| 3 | 违禁词 | **PASS** · 文件内「扣票」「B0.1」「深渊票」「B0.1 已清」「票已废」计数均 0（`rg -n "扣票|B0\.1|深渊票"` 无命中）；tip 额外口径：「余烬深渊票」0、「日 1 张」0、「已停用」0；L18 不含「首免」（tip §2 L18 只写体力）；tip §4「不写测试岗边界情况」：新增行「离线」「发起人」均 0 |
| 4 | 新注释与 CoreRpg 源码现行行为一致、不夸大 | **PASS（附边界备注，不构成不符）** · 无首免、30 体力、未进本退还、撤离结算不碰体力、无每日次数门、`ticket_convert` 均与源码 / 配置一致，见下「行为核对」 |
| 5 | HANDOFF §8 查密码命令输出 0；`ops.json` 为 `[]` | **PASS** · 原样执行（`grep -c` 只出计数）输出 `0`，变量非空 `true`（未打印值）；`ops.json` 为 `[]` |

## 目标行原文（live）

```
2:# 启动：/dp start EmberAbyss（玩家走菜单按钮，勿在 tell 里强调指令）
3:# 次数：维护备忘：遗留票物/体力口径；现行进本扣体力（深渊无首免，按 cash.yml stamina.costs.abyss，现行 30）
4:# 维护备忘：扣次由 CoreRpg TicketEntryService/StaminaService 在 corerpg enter 时处理，本文件不扣次；遗留 NI 票物 ticket_ember_abyss 按 stamina.ticket_convert 折体力
17:    - "$team-condition{team=true;min=1;max=2;message=§c深渊人数 1～2，当前 (<size>)} @system"
18:    # 2026-09-26 等级门槛（CoreRpg progress.yml level_gates.abyss=25；DP 逐队员判定；被拒未进本时 CoreRpg 退还本次体力；OP 豁免）
19:    - "$js-condition{text='%corerpg_gate_abyss%'=='yes'||'%player_is_op%'=='yes';message=§c深渊需要余烬等级 §eLv.25§c · 队伍中有人等级不足 · 打开枢纽 → 角色查看等级} @system"
20:    # 维护备忘：体力已在 corerpg enter 扣除；未进本自动退还，进本后撤离按最高层结算、体力不退；此处仅保留人数/等级门
21:  action-script:
22:    - "$message{type=text;text=§5深渊已开启。§7已消耗体力 ×1 —— 能走多深，就走多深。本期可下潜至第 §f12 §7层。} @dungeon"
23:    - "$message{type=text;text=§8需要撤离：打开菜单 → 深渊 → 撤离 · 按最高层结算 · 体力不退} @dungeon"
```

## 命令与输出

```
$ git log --oneline -8
50a946c feat(dp): B2.95 EmberAbyss option.yml 注释改体力口径
e384dab docs: approve B2.95 dp abyss option ticket comment (A)
37a01ca docs: B2.95 tip dp abyss option ticket comment pending A
7a5aff2 docs: close B2.94 dp weekly option ticket comment (PASS)
b6501af docs: B2.94 dp weekly option comment test PASS
ef7e350 feat(dp): B2.94 EmberWeekly option.yml 注释改体力口径
4c7e7eb docs: approve B2.94 dp weekly option ticket comment (A)
9d20149 docs: B2.94 tip dp weekly option ticket comment pending A
$ git merge-base --is-ancestor <37a01ca|e384dab|50a946c> HEAD   → 均为祖先
$ git show --stat 37a01ca / e384dab   → 均只含本 tip 与 docs/design/design-ember-content-backlog.md

# 1
$ git show --stat --numstat --format= 50a946c
4	4	plugins/DungeonPlus/dungeon/EmberAbyss/option.yml
 plugins/DungeonPlus/dungeon/EmberAbyss/option.yml | 8 ++++----
 1 file changed, 4 insertions(+), 4 deletions(-)
$ git diff -U0 50a946c^ 50a946c | grep '^@@'
@@ -3,2 +3,2 @@
@@ -18 +18 @@ dungeon-start:
@@ -20 +20 @@ dungeon-start:
$ diff <(git show 50a946c^:F) <(git show 50a946c:F) | grep -E '^[0-9]'
3,4c3,4
18c18
20c20

# 荐案 / 旧行：从 tip §2 第二、第一个 ```yaml 块程序提取（各 4 行）
cmp expected live(L3/4/18/20)          → 一致
cmp expected 50a946c(L3/4/18/20)       → 一致
cmp expected 施工新增行(+)              → 一致
cmp design_old 施工删除行(-)            → 一致
cmp design_old 父版本 L3/4/18/20        → 一致
sha256（荐案 / live / 施工 tip / 新增行 四者相同）：55b41206875d89f0f76b55a279881d19db5602dab3cf48717bf6720c39878d1e

# 行尾 / 缩进
CR 字节（L3/4/18/20）：0；全文件 CR：0；行尾空白行数：0
cat -A 行尾：四行均以中文字节 + `$` 结束（`M-^I$` / `M-^[$` / `M-^I$` / `M-($`），无 ` $`、无 `^M$`
3  leading_spaces 0  tab False  len 71
4  leading_spaces 0  tab False  len 136
18 leading_spaces 4  tab False  len 101
20 leading_spaces 4  tab False  len 69

# 2（tip §3 原样，仓库根目录，HEAD=50a946c）
$ node -e 'const y=require("js-yaml"),{execSync:x}=require("child_process"),f="plugins/DungeonPlus/dungeon/EmberAbyss/option.yml",g=r=>JSON.stringify(y.load(x("git show "+r+":"+f).toString()));if(g("HEAD~1")!==g("HEAD"))process.exit(1);console.log("same")'
same
exit=0
（HEAD~1/HEAD 换成 50a946c~1/50a946c：same，exit=0）
$ diff <(git show 50a946c^:F | grep -vE '^\s*#') <(git show 50a946c:F | grep -vE '^\s*#')   → NONCOMMENT_IDENTICAL
L1 L2 L5 L17 L19 L21 L22 L23 L24 L25 same
HEAD==50a946c（cmp）；worktree==HEAD（cmp）；git log 50a946c..HEAD -- F → 空

# 3（全文件 / 新增行）
扣票 0/0 · B0.1 0/0 · 深渊票 0/0 · 余烬深渊票 0/0 · B0.1 已清 0/0 · 票已废 0/0 · 日 1 张 0/0 · 已停用 0/0 · 离线 0/0 · 发起人 0/0
L18 含「首免」：0
$ rg -n "扣票|B0\.1|深渊票" F   → 无命中（rc=1）

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
| 进本入口为 `corerpg enter`，本文件不扣次 | TrMenu `plugins/TrMenu/menus/ember_abyss.yml:79` `command: corerpg enter abyss`；`CoreRpgPlugin.java:842-843` → `TicketEntryService.java:235-241` → `tryEnter`；`TicketEntryService.java:124-136` 先 `consumeForEnter`，`:159-162` 再 `dp start-console`；option.yml 条件只有人数（L17）与等级 PAPI（L19） | 一致 |
| 深渊无首免 | `StaminaService.java:300-315` 首免额度只判 `Kind.WEEKLY` / `ELITE` / `RAID`，ABYSS 直接落到 `:316-326` 扣 `cost`；`ensureWeekCredits`（`:205-214`）也只刷新 weekly/elite/raid 三项；菜单 `ember_abyss.yml:40`「深渊无周免费」 | 一致 |
| 按 `stamina.costs.abyss`，现行 30 | `plugins/CoreRpg/cash.yml:27` `abyss: 30`；src 模板 `CoreRpg/src/main/resources/cash.yml:27` 同为 30，两份 `stamina` 段解析后深度相等；代码缺省 `StaminaService.java:85` `costs.put("abyss", 30)`；读取 `:119-124`，`costOf` `:142-150`；菜单 lore `ember_abyss.yml:63` 用 `%corerpg_stamina_cost_abyss%` | 一致 |
| 等级门 25；DP 逐队员判定；OP 豁免 | `plugins/CoreRpg/progress.yml:143` `abyss: 25`（代码缺省 `ProgressService.java:132`）；`TicketEntryService.java:106-113` 发起人等级不足直接拒、不扣；OP 跳过门与扣费 `:103,106,125`；option.yml L19 `%corerpg_gate_abyss%`/`%player_is_op%` | 一致 |
| 被拒 / 未进本时退还本次体力 | `StaminaService.java:329-345` `refundEnter`：非首免时退回 `prior.cost`；调用点 `TicketEntryService.java:167-169`（dispatch 失败）与 `:186-201`（`ENTER_VERIFY_TICKS=40` 约 2 秒，`:82`；`looksInDungeon` 为假则退还） | 一致 |
| 进本后撤离按最高层结算、体力不退 | `AbyssSettleService.java:173-174` `evacuate`/`leave`/`上浮` → `cmdEvacuate`（`:262-281`：`settle(p)` → 提示 → `dp leave`）；`settle` `:304` 起按本局会话层数结算（`:191` 状态显示「本局最高层」）；`AbyssSettleService` / `AbyssShaftService` / `AbyssSession` 三文件 `rg -c -i 'stamina|refund|consumeForEnter|refundEnter|addStamina|setStamina'` 均为 0；菜单 `ember_abyss.yml:66,90`「撤离不退体力」，option.yml L23 同 | 一致 |
| 无每日次数门（旧「日 1 张」已删） | 进本路径 `TicketEntryService.java:100-203` 对 ABYSS 无次数判断（仅 ELITE 有周通关门 `:115-121`）；`PlayerData.java:24,107-109` 有 `abyssUsedToday` / `markAbyssUsed()`，但全源码无 `markAbyssUsed` 调用方，`getAbyssUsedToday` 只用于显示（`CoreRpgExpansion.java:31`、`CoreRpgPlugin.java:1358`）与存取（`PlayerDataStore.java:166,346`），不参与拦截 | 一致（注释未写日限，正确） |
| 遗留 NI 票物 `ticket_ember_abyss` 按 `stamina.ticket_convert` 折体力 | `plugins/CoreRpg/cash.yml:30,33` `ticket_ember_abyss: 30`（src 模板 `:30,33` 同值；代码缺省 `StaminaService.java:91`）；`StaminaService.java:125-131` 读取，`:348-373` `convertInventoryTickets` 按张折体力；触发 `/corerpg stamina convert`（`:515-521`）及迁移期满登录自动折算（`:385-396`）；`TicketEntryService.java:28,34` `ticket_ember_abyss` 为 legacy id | 一致（未声称自动，不夸大） |

**边界备注（不构成不符，不影响 PASS；tip §4 明确不写进注释，此处仅记录）：**

1. 「未进本自动退还」：约 2 秒校验时玩家已离线则 `TicketEntryService.java:188-189` 直接 return，不退还（与 B2.94 同）。
2. 扣费 / 退还只作用于执行 `corerpg enter` 的发起人（`consumeForEnter(player, kind)`）；DP 拉入的队友（深渊最多 2 人）不经 CoreRpg 扣体力。注释没有声称队友各扣一次，不矛盾（与 B2.94 同）。
3. 退还依据是约 2 秒后 `looksInDungeon`：若 DP 在 2 秒后才把玩家传入本内，理论上会先退还再进本。属实现层面的时序边界，注释「未进本自动退还」表述本身无误。
4. **范围外的旧文案（不在本窗，供排窗）：**`CoreRpgPlugin.java:1356` `cmdAbyss` 仍提示「日限 1 次 · 进本扣余烬深渊票」及 `:1359-1361` 旧票说明；但它只在 `abyssSettleService == null` 时作为回退（`CoreRpgPlugin.java:825-827`），正常路径走 `AbyssSettleService.cmdRoot`。`AbyssSettleService.java:189,196` 状态页仍显示「背包深渊票」计数。L22 玩家提示「已消耗体力 ×1」按 tip §4 另窗处理，本窗确认未改。

## 旁证

1. **施工范围：**`50a946c` 只改 `EmberAbyss/option.yml` 四行注释；解析结果前后 `same`，非注释行逐行相同；L22/L23 玩家提示、条件脚本、奖励脚本均未动。
2. **精确行文：**live、施工 tip、新增行与 tip §2 荐案四者 sha256 相同；L18/L20 行首 4 空格与原行一致；删除行与 tip §2 旧行块逐字一致。
3. **口径：**删除「B0.1:」前缀、「扣票」「余烬深渊票」「日 1 张」旧说法；未写「票已废」「B0.1 已清」；不代表 B0.1 已清（tip §1）。L20 把「未进本退还」与「进本后撤离不退」分开写，与 L23、菜单 lore 一致。
4. **范围外未动：**`37a01ca^..HEAD` 期间无 commit 触及 `plugins/CoreRpg`、`CoreRpg/`、`plugins/TrMenu`；其它 option.yml 未动。
5. **做法纪律：**仅静态 git / sed / diff / cmp / node js-yaml / python / rg 读源码；未起服、未 `/dp reload`、未长测、未杀进程；未改任何文件（仅新增本报告）；机密未回显。
6. **时间戳：**tip 写「已批 A（总控 · 2026-10-01 02:14）」，批准 commit `e384dab` 为 02:14:05 CST，一致；顺序设计 → 批准 → 施工正确。

## 阻塞点

无。

## 回报摘要

- 总评：PASS
- 各点：1 PASS / 2 PASS / 3 PASS / 4 PASS（附边界备注）/ 5 PASS
- 施工 tip SHA：`50a946c`
- 测报 tip short SHA：待本地 commit 后回报
- 是否已 push：否
- 报告路径：`docs/tests/TEST-B2.95-dp-abyss-option-comment.md`
- 阻塞点：无
- ahead：相对 `origin/main` ahead 1（未 push）
- `ops.json`：`[]`
