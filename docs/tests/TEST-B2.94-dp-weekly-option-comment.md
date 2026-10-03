# B2.94 · DP `EmberWeekly/option.yml` 注释改体力口径 · 纯静态薄验收

- **总评：PASS**
- **测报时间：**2026-10-01 02:12 Asia/Shanghai
- **岗别：**余烬-测试岗（B2.94 纯静态薄验收）
- **工作区：**`/workspace/minecraft`（main，与 `origin/main` 同步；三个 commit 本地均存在且为 HEAD 祖先，无需 fetch）
- **施工 tip SHA：**`ef7e350`（2026-10-01 02:07:07 CST）
- **设计 / 批准：**`9d20149`（02:05:47 CST）/ `4c7e7eb`（02:06:30 CST）
- **tip：**`docs/design/design-ember-dp-weekly-option-ticket-comment-copy.md`（§1 行为核对、§2 荐案、§2.1 锁定口径、§3 验收）
- **是否已 push：否**（本岗仅本地 commit，禁止 push）
- **报告路径：**`docs/tests/TEST-B2.94-dp-weekly-option-comment.md`
- **ahead：**相对 `origin/main` ahead 1（本测报本地 commit 后，未 push）
- **`server-runtime/ops.json`：**`[]`（未改动）

## 各点

| # | 条件 | 结果 |
|---|------|------|
| 1 | 施工 commit 只改 `plugins/DungeonPlus/dungeon/EmberWeekly/option.yml`，numstat `4 4`，变化行恰为 L4/L5/L16/L18，逐字等于 §2 荐案；无行尾空白/CR；L16/L18 行首 4 空格 | **PASS** · numstat `4	4`；hunk 头 `@@ -4,2 +4,2 @@`、`@@ -16 +16 @@`、`@@ -18 +18 @@`，`diff` 为 `4,5c4,5` / `16c16` / `18c18`；程序提取 §2 第二个 yaml 块（4 行），与 live、`ef7e350` 的 L4/5/16/18 及施工新增行 `cmp` 一致，sha256 相同；删除行也与 §2 旧行块一致；CR 字节 0、行尾空白 0；L4/L5 顶格，L16/L18 恰 4 空格 |
| 2 | tip node 命令原样输出 `same`；键值 / `text=` / 脚本 / L20 不在 diff；HEAD 与 `ef7e350` 一致 | **PASS** · 原样命令（HEAD=`ef7e350`）输出 `same`，改用 `ef7e350~1`/`ef7e350` 显式再跑亦 `same`；去掉注释行后前后逐行相同；L3/L15/L17/L19/L20 与父版本相同；HEAD、工作区与 `ef7e350` 字节一致，之后无 commit 触及 |
| 3 | 违禁词 | **PASS** · 文件内「扣票」「B0.1」「余烬周票」「B0.1 已清」「票已废」计数均 0（`rg -n "扣票|B0\.1|余烬周票"` 无命中）；tip §2.1 额外禁写「已停用 NI 票」亦 0 |
| 4 | 新注释与 CoreRpg 源码现行行为一致 | **PASS（附两处边界备注，不构成不符）** · 首免、45 体力、退还、`ticket_convert`、扣次位置均与源码 / 配置一致，见下「行为核对」 |
| 5 | HANDOFF §8 查密码命令输出 0；`ops.json` 为 `[]` | **PASS** · 原样执行（`grep -c` 只出计数）输出 `0`，变量非空 `true`（未打印值）；`ops.json` 为 `[]` |

## 目标行原文（live）

```
3:# 启动：/dp start EmberWeekly
4:# 次数：维护备忘：遗留票物/体力口径；现行进本扣体力（本周首次免费，之后按 cash.yml stamina.costs.weekly，现行 45）
5:# 维护备忘：扣次由 CoreRpg TicketEntryService/StaminaService 在 corerpg enter 时处理，本文件不扣次；遗留 NI 票物 ticket_ember_weekly 按 stamina.ticket_convert 折体力
15:    - "$team-condition{team=true;min=1;max=3;message=§c周常本人数 1～3，当前 (<size>)} @system"
16:    # 2026-09-26 等级门槛（CoreRpg progress.yml level_gates.weekly=20；DP 逐队员判定；被拒未进本时 CoreRpg 退还本次体力/首免；OP 豁免）
17:    - "$js-condition{text='%corerpg_gate_weekly%'=='yes'||'%player_is_op%'=='yes';message=§c周常本需要余烬等级 §eLv.20§c · 队伍中有人等级不足 · 打开枢纽 → 角色查看等级} @system"
18:    # 维护备忘：体力/首免已在 corerpg enter 扣除（未进本自动退还）；此处仅保留人数/等级门
19:  action-script:
20:    - "$message{type=text;text=§c深核·周 开始！已消耗体力 ×1} @dungeon"
```

## 命令与输出

```
$ git log --oneline -8
ef7e350 feat(dp): B2.94 EmberWeekly option.yml 注释改体力口径
4c7e7eb docs: approve B2.94 dp weekly option ticket comment (A)
9d20149 docs: B2.94 tip dp weekly option ticket comment pending A
da34b8f docs: close B2.93 cash elite free_tickets comment (PASS)
869fd38 docs: B2.93 cash elite free_tickets comment test PASS
6203a87 feat(corerpg): B2.93 cash elite free_tickets 行内旁注
df742c8 docs: approve B2.93 cash elite free_tickets comment (A)
b53fa71 docs: B2.93 tip cash elite free_tickets comment pending A
$ git merge-base --is-ancestor <9d20149|4c7e7eb|ef7e350> HEAD   → 均为祖先
$ git show --stat b53fa71… 9d20149 / 4c7e7eb   → 均只含本 tip 与 docs/design/design-ember-content-backlog.md

# 1
$ git show --stat --numstat --format= ef7e350
4	4	plugins/DungeonPlus/dungeon/EmberWeekly/option.yml
 plugins/DungeonPlus/dungeon/EmberWeekly/option.yml | 8 ++++----
 1 file changed, 4 insertions(+), 4 deletions(-)

$ git diff -U0 ef7e350^ ef7e350
@@ -4,2 +4,2 @@
-# 次数：扣 NI id ticket_ember_weekly ×1（显示名「余烬周票」；周发 1）
-# B0.1: 扣票改由 CoreRpg TicketEntryService（NI id）；本文件不再 <item:显示名> 扣次
+# 次数：维护备忘：遗留票物/体力口径；现行进本扣体力（本周首次免费，之后按 cash.yml stamina.costs.weekly，现行 45）
+# 维护备忘：扣次由 CoreRpg TicketEntryService/StaminaService 在 corerpg enter 时处理，本文件不扣次；遗留 NI 票物 ticket_ember_weekly 按 stamina.ticket_convert 折体力
@@ -16 +16 @@ dungeon-start:
-    # 2026-09-26 等级门槛（…；放在扣票条件前 → 被拒不扣票；OP 豁免）
+    # 2026-09-26 等级门槛（CoreRpg progress.yml level_gates.weekly=20；DP 逐队员判定；被拒未进本时 CoreRpg 退还本次体力/首免；OP 豁免）
@@ -18 +18 @@ dungeon-start:
-    # B0.1: 票已在 /corerpg enter 按 NI id 扣除；此处仅保留人数/等级门
+    # 维护备忘：体力/首免已在 corerpg enter 扣除（未进本自动退还）；此处仅保留人数/等级门
（旧行为历史原文，仅出现在删除侧）

$ diff <(git show ef7e350^:F) <(git show ef7e350:F) | grep -E '^[0-9]'
4,5c4,5
16c16
18c18

# 荐案行：从 tip §2 第二个 ```yaml 块程序提取（4 行）→ /tmp/b294_expected.txt
$ diff expected <(sed -n '4p;5p;16p;18p' F)        → IDENTICAL
$ cmp  expected <(git show ef7e350:F | sed -n …)   → CMP_OK
$ git diff -U0 … | grep '^+[^+]' | sed 's/^+//' | cmp - expected   → added==expected
$ §2 第一个 yaml 块（旧行） cmp 删除侧                              → removed==design old block
sha256（荐案 / live / 施工 tip 三者相同）：a3c9ce6fdc2bf99c6ee5a4a2009b36a0c920e7ca743e8009048b621763b40d43

# 行尾 / 缩进
CR 字节（L4/5/16/18）：0；全文件 CR：0；行尾空白行数：0
cat -A 行尾：四行均以 `$` 结束，前一字符为中文字节（`M-^I$` / `M-^[$` 等），无 ` $`、无 `^M$`
（注：`cat -A | grep '\^M'` 会把中文 UTF-8 字节 0x8D 显示的 `M-^M` 误计，已改用 `tr -cd '\r' | wc -c` 精确计数为 0）
4  leading_spaces 0  tab False  len 75
5  leading_spaces 0  tab False  len 137
16 leading_spaces 4  tab False  len 105
18 leading_spaces 4  tab False  len 56

# 2（tip §3 原样，仓库根目录，HEAD=ef7e350）
$ node -e 'const y=require("js-yaml"),{execSync:x}=require("child_process"),f="plugins/DungeonPlus/dungeon/EmberWeekly/option.yml",g=r=>JSON.stringify(y.load(x("git show "+r+":"+f).toString()));if(g("HEAD~1")!==g("HEAD"))process.exit(1);console.log("same")'
same
exit=0
（同命令把 HEAD~1/HEAD 换成 ef7e350~1/ef7e350：same，exit=0）
$ diff <(git show ef7e350^:F | grep -vE '^\s*#') <(git show ef7e350:F | grep -vE '^\s*#')   → NONCOMMENT_IDENTICAL
L3 same / L15 same / L17 same / L19 same / L20 same
HEAD==ef7e350（cmp）；worktree==HEAD（cmp）；git log ef7e350..HEAD -- F → 空

# 3
扣票 0 · B0.1 0 · 余烬周票 0 · B0.1 已清 0 · 票已废 0 · 已停用（NI 票）0（全文件与新增行均为 0）
$ rg -n "扣票|B0\.1|余烬周票" F   → 无命中（rc=1）

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
| 进本入口为 `corerpg enter` | `CoreRpgPlugin.java:842-843` `enter`/`进本` → `ticketEntryService.cmdEnter`；`TicketEntryService.java:235-241` 解析种类后 `tryEnter`；TrMenu `plugins/TrMenu/menus/ember_weekly.yml:77` `command: corerpg enter weekly` | 一致 |
| 扣次在 CoreRpg TicketEntryService/StaminaService，本文件不扣次 | `TicketEntryService.java:124-136` 非 OP 调 `stamina.consumeForEnter`，`:159-162` 之后才 `dp start-console`；option.yml `dungeon-start.condition` 只有人数（L15）与等级 PAPI（L17），无物品/体力条件 | 一致 |
| 本周首次免费 | `StaminaService.java:205-214` `ensureWeekCredits`：`DailyService.weekId()` 变化时 `weeklyGrantCreditWeekly=1`；`StaminaService.java:300-305` WEEKLY 有额度先扣额度、`usedCredit=true`、cost 0（账户字段，非 NI 物品）；周界 `DailyService.java:9,11,24-28`（Asia/Shanghai、ISO 周，即周一起算） | 一致 |
| 之后按 `stamina.costs.weekly`，现行 45 | `StaminaService.java:119-124` 读 `stamina.costs`，`:142-150` `costOf`，`:299,319-326` 体力不足拒绝、足则扣 `cost`；`plugins/CoreRpg/cash.yml:25` `weekly: 45`；src 模板 `CoreRpg/src/main/resources/cash.yml:25` 同为 45，两份 `stamina` 段解析后深度相等 | 一致（live 与 src 无差异） |
| 被拒 / 未进本时退还本次体力或首免 | `StaminaService.java:329-345` `refundEnter`：`usedCredit` 则 WEEKLY 额度 +1，否则退回 `prior.cost`；调用点 `TicketEntryService.java:167-169`（dispatch 失败）与 `:186-201`（`ENTER_VERIFY_TICKS=40`，约 2 秒后 `looksInDungeon` 为假则退还，`:82`） | 一致 |
| OP 豁免 | `TicketEntryService.java:103,106,125` OP/`corerpg.admin` 跳过等级门与扣体力（`:139-140` 提示「管理免扣」）；option.yml L17 `%player_is_op%` | 一致 |
| 遗留 NI 票物 `ticket_ember_weekly` 按 `stamina.ticket_convert` 折体力 | `ticket_convert` 在 `plugins/CoreRpg/cash.yml:30,32`（`ticket_ember_weekly: 45`；src 模板 `:30,32` 同值）；`StaminaService.java:125-131` 读取，`:348-373` `convertInventoryTickets` 按张 × 比例加体力；触发点 `/corerpg stamina convert`（`StaminaService.java:515-521`）及迁移期满后登录自动折算（`:385-396`，`migration_t0 2026-09-28` + 7 天，cash.yml:15-16） | 一致（注释未声称「自动」，不夸大） |
| `ticket_ember_weekly` 仅为遗留 id | `TicketEntryService.java:27,34` `Kind.WEEKLY` 的 `ticketNiId` 注释「legacy id (migration only)」；`/corerpg ticket consume` 为管理测扣口（`TicketEntryService.java:245`） | 一致 |

**边界备注（不构成不符，不影响 PASS，供后续写玩家文案时参考）：**

1. 「未进本自动退还」：约 2 秒校验时若玩家已离线，`TicketEntryService.java:188-189` 直接 return，不退还。属罕见边界，维护注释不必展开。
2. 扣费与退还只作用于执行 `corerpg enter` 的发起人（`consumeForEnter(player, kind)` 只扣该玩家）；DP 拉入的队友不经 CoreRpg 扣体力。L16 的「DP 逐队员判定」指等级门判定，与此不矛盾；注释也没有声称队友各扣一次。
3. L20 玩家可见 `text=…已消耗体力 ×1` 仍是票时代遗留（现行扣 45 或首免），tip §4/§5 已明确不在本窗、记为后续窗；本窗确认其未被改动。

## 旁证

1. **施工范围：**`ef7e350` 只改 `EmberWeekly/option.yml` 四行注释；解析结果前后 `same`，非注释行逐行相同；L3 管理备注、L17 等级门脚本、L20 开本提示、奖励脚本均未动。
2. **精确行文：**live、施工 tip 与 tip §2 荐案三者 sha256 相同；L16/L18 行首 4 空格与原行一致。
3. **口径：**删除了「B0.1:」前缀与「扣票」「余烬周票」旧说法；未写「票已废」「B0.1 已清」「已停用 NI 票」；不代表 B0.1 已清（tip §1 说明）。
4. **范围外未动：**本窗 `9d20149^..HEAD` 期间无 commit 触及 `plugins/CoreRpg`、`CoreRpg/`、`plugins/TrMenu`；其它 option.yml 未动。
5. **做法纪律：**仅静态 git / sed / diff / cmp / node js-yaml / python / 读源码；未起服、未 `/dp reload`、未长测、未杀进程；未改任何文件（仅新增本报告）；机密未回显。
6. **时间戳：**tip 写「已批 A（总控 · 2026-10-01 02:06）」，批准 commit `4c7e7eb` 为 02:06:30 CST，一致；顺序设计 → 批准 → 施工正确。

## 阻塞点

无。

## 回报摘要

- 总评：PASS
- 各点：1 PASS / 2 PASS / 3 PASS / 4 PASS（附两处边界备注）/ 5 PASS
- 施工 tip SHA：`ef7e350`
- 测报 tip short SHA：待本地 commit 后回报
- 是否已 push：否
- 报告路径：`docs/tests/TEST-B2.94-dp-weekly-option-comment.md`
- 阻塞点：无
- ahead：相对 `origin/main` ahead 1（未 push）
- `ops.json`：`[]`
