# B2.112 · DP `EmberEliteWeekly/task/timeout.yml` L6 超时失败提示「体力已扣，下周再来」→「这次不算通关，本周还能再来」· 纯静态薄验收

- **总评：PASS**（reload / 实服观察项记 SKIP：待恢复服后实测，不挡 PASS）
- **测报时间：**2026-10-01 03:38 Asia/Shanghai
- **岗别：**余烬-测试岗（B2.112 纯静态薄验收）
- **开工前：**`git status --porcelain` 空（干净）；已读 `HANDOFF.md`（§8 硬规则：查密码、`ops.json` 必须 `[]`；§6 杀进程用 PID）与 `docs/LESSONS-ember-pipeline.md`（测试 §8「以磁盘 + reload 为准」、§9「只记 PASS/FAIL/SKIP」）。
- **工作区：**`/workspace/minecraft`（main，与 `origin/main` 同步，HEAD = origin/main = `684ede2`；三个 commit 本地均已存在且为 HEAD 祖先，**未 fetch**）
- **施工 tip SHA：**`684ede2`（2026-10-01 03:34:56 CST）
- **设计 / 批准：**`e47a7ed`（03:33:05 CST）/ `ed3e16e`（03:34:20 CST）
- **tip：**`docs/design-ember-dp-elite-timeout-text-copy.md`（§1 完整旧行、§2 核对、§3 荐案、§4 验收与 heredoc 脚本、§5 明确不做、§6 其它本同类、文末总控批注）
- **改动性质：**玩家可见 `$message` `text=`（`@dungeon` 全队）
- **是否已 push：否**（本岗仅本地 commit，禁止 push）
- **报告路径：**`docs/TEST-B2.112-dp-elite-timeout-text.md`
- **ahead：**相对 `origin/main` ahead 1（本测报本地 commit 后，未 push）
- **`server-runtime/ops.json`：**`[]`（未改动）
- **本机服状态：**25565/25566/25567/3306 均未监听（`ss -ltn` 计数 0）；未起服、未结束任何进程

## 各点

| # | 条件 | 结果 |
|---|------|------|
| 0 | 三个 commit 时间顺序；设计 / 批准只改文档 | **PASS** · 设计 `e47a7ed` 03:33:05 → 批准 `ed3e16e` 03:34:20 → 施工 `684ede2` 03:34:56（committer 与 author 时间相同，CST）；`e47a7ed`、`ed3e16e` 均只改 `docs/design-ember-dp-elite-timeout-text-copy.md` 与 `docs/design-ember-content-backlog.md` |
| 1 | `684ede2` 只改 `EmberEliteWeekly/task/timeout.yml` L6，numstat `1 1`，hunk `@@ -6 +6 @@`；旧行 = §1；新行 = §3；6 空格缩进；整文件无 tab/CR/行尾空格 | **PASS** · numstat `1	1`；hunk `@@ -6 +6 @@ timeout:`，`diff` 仅 `6c6`，除 L6 外逐行相同；旧行 = tip §1 块 = 施工删除行（sha256 `bbaddd2f…239d289e`）；新行 = tip §3 块 = live = HEAD = 施工新增行（sha256 `f5501d35…3bce926854`）；新旧行缩进均 6 空格；整文件 272 字节 7 行，`cat -A` 全部以 `$` 收尾，tab 0、CR 0、行尾空格 0 |
| 2 | `/tmp/chk-b2112.js` 与稿内 heredoc 一致；输出 `only-L6 /timeout/0/720/0`；旧对旧 / 新对新非 0；独立 deep diff（含键序、空对象/空数组）只此一处 | **PASS** · `/tmp/chk-b2112.js` 已存在（1182 字节，mtime 03:34:56，与施工 commit 同秒）；与 tip §4 ```sh 块 heredoc 正文逐字节相同（sha256 `0565c983…9df97304`；稿内该块 0 行缩进，无需去缩进）；另把 heredoc 只改落盘路径重跑一次，产物与 `/tmp` 文件 `cmp` 一致；未覆写 `/tmp` 文件。`node /tmp/chk-b2112.js 684ede2` → `only-L6 /timeout/0/720/0`（exit 0）；旧对旧 `ed3e16e`、`e47a7ed` → `[]` exit 1；新对新 `WT`（HEAD=684ede2 对工作区）→ `[]` exit 1；本测报 commit 后 `B=HEAD` 见文末附记；无参 exit 9。一次性临时仓（`/tmp/b2112-mut`，与主仓无关）变异测试：错误新值 → exit 4、错误旧值 → exit 3、多改 L7 → 打印 `['/timeout/0/720/1']` exit 1。独立 deep diff（类型 / 数组长度 / 对象键序 / 叶子值逐一比较）→ 仅 `["/timeout/0/720/0"]` |
| 3 | 原样 `rg` 0 命中；「B0.1 已清」「票已废」0 | **PASS** · `rg "体力已扣|下周再来|已消耗体力|体力 -|扣票|精英票" …/task/timeout.yml` 无输出（rc=1；施工前同命令仅命中 L6，tip §4 L76 属实）；「B0.1 已清」0、「票已废」0；另「体力」「扣」「票」「首免」「免费」「下周」均 0 |
| 4 | 「这次不算通关」「本周还能再来」与配置 / CoreRpg 源码一致；无过度承诺；与 B2.101 L20 一致 | **PASS（附边界备注）** · 见下「文案与行为核对」 |
| 5 | 无脚本 / 其它文件依赖旧文「体力已扣」「下周再来」 | **PASS** · mineflayer-tests 0 命中；非 docs 仅 `TicketEntryService.java:118`「本周已通关精英试炼，下周再来」（已通关拒进提示，口径正确，非本行依赖）与根目录 `STATUS-ember-stage4-4.6.md:37,79`（历史测报引用该拒进提示）；docs 13 个文件为历史引用，见下 |
| 6 | HANDOFF §8 查密码命令 0；报告自身 0；`ops.json` `[]` | **PASS** · 原样执行（`HANDOFF.md:140-141`，`grep -c` 只出计数）输出 `0`，变量非空 `true`（未打印值）；commit 前对本报告 `grep -cF` 为 `0`；`ops.json` 为 `[]` |
| 7 | reload / live：超时后本内见新 L6 并 FAILURE 回城；`corerpg elite status` 显示本周未通关；同周可再 `corerpg elite start` | **SKIP · 待恢复服后实测**（本机无运行中的服；总控批注明确不挡 PASS） |

## 目标行原文

```
# 父版本 684ede2^ L6（旧；= tip §1）
      - "$message{type=text;text=§c试炼失败。§7体力已扣，下周再来。} @dungeon"
# live / 684ede2 L6（新；= tip §3 荐案）
      - "$message{type=text;text=§c试炼失败。§7这次不算通关，本周还能再来。} @dungeon"
# 新行玩家可见文本（去 § 色码）
试炼失败。这次不算通关，本周还能再来。
# 差异中段：旧「体力已扣，下周」→ 新「这次不算通关，本周还能」（公共前缀「…§c试炼失败。§7」、公共后缀「再来。} @dungeon"」不变）

# live 整文件（7 行）
1: auto-start:
2:   - "timeout false timing"
3:
4: timeout:
5:   - 720:
6:       - "$message{type=text;text=§c试炼失败。§7这次不算通关，本周还能再来。} @dungeon"
7:       - "$end{type=text;text=§c精英试炼超时失败;reward=false;delay=1;end-type=FAILURE} @dungeon"
```

## 命令与输出

```
$ git status --porcelain | wc -l
0
$ git log --oneline -6
684ede2 feat(dp): B2.112 EmberEliteWeekly 超时失败提示改不算通关、本周可再来
ed3e16e docs: approve B2.112 dp elite timeout text (A)
e47a7ed docs: B2.112 design tip — EliteWeekly timeout L6 不记通关、本周可再来（待批 A）
8c60c00 docs: close B2.102 hub elite lore (PASS)
0884f55 docs: B2.102 hub elite lore test PASS
862d21c feat(trmenu): B2.102 hub 精英 lore 改限通关口径并用体力占位符
$ git cat-file -t <e47a7ed|ed3e16e|684ede2> → commit ×3；merge-base --is-ancestor → 均为 HEAD 祖先；HEAD = origin/main = 684ede2（未 fetch）
$ git show --stat e47a7ed → docs/design-ember-content-backlog.md, docs/design-ember-dp-elite-timeout-text-copy.md
$ git show --stat ed3e16e → 同上两文件

# 1
$ git show --numstat --format= 684ede2
1	1	plugins/DungeonPlus/dungeon/EmberEliteWeekly/task/timeout.yml
$ git diff -U0 684ede2^ 684ede2 | grep '^@@'
@@ -6 +6 @@ timeout:
$ diff <(git show 684ede2^:F) <(git show 684ede2:F) | grep -E '^[0-9]'
6c6
$ diff <(git show 684ede2^:F | sed '6d') <(git show 684ede2:F | sed '6d')   → ALL_OTHER_LINES_IDENTICAL
tip ```yaml 块 2 个（各 1 行），程序提取：
old == §1 True · new == §3 True · 删除行 == old True · 新增行 == new True · live == new True · HEAD == new True
sha256 旧（§1 / old / 删除行）：bbaddd2fd945628b4d3b0db96181a4842e02a5b325c7a36b4b291ed9239d289e
sha256 新（§3 / new / live / 新增行）：f5501d3530f34866e0959b23d59f001a2e0750154e131e8b3f97e83bce926854
old len 95 · new len 107 · indent 6/6 · tab 0 · CR 0 · 行尾空白 无
整文件 272 字节 · 7 行 · 末尾有换行 · CR 0 · tab 0 · 行尾空白行 0
$ cat -A F
auto-start:$
  - "timeout false timing"$
$
timeout:$
  - 720:$
      - "$message{type=text;text=M-BM-'cM-hM-/M-^UM-gM-^BM-<…M-cM-^@M-^B} @dungeon"$
      - "$end{type=text;text=M-BM-'cM-gM-2M->…;reward=false;delay=1;end-type=FAILURE} @dungeon"$
HEAD==684ede2==worktree（cmp）

# 2（/tmp 脚本身份）
$ ls -la /tmp/chk-b2112.js
-rw-r--r-- 1 box box 1182 Oct  1 03:34 /tmp/chk-b2112.js（mtime 03:34:56.83）
tip §4 ```sh 块：首行 `cat > /tmp/chk-b2112.js <<'JS'`，块内缩进行 0
heredoc 正文 sha256 = /tmp 文件 sha256 = 0565c983891160c7322a1a95862d98bf2b6846fb301bdf0a15590f419df97304（逐字节相等）
按稿执行 heredoc（仅首行落盘路径改为 /tmp/b2112-regen/chk.js）→ cmp /tmp/chk-b2112.js 一致

$ node /tmp/chk-b2112.js 684ede2
only-L6 /timeout/0/720/0
exit=0
$ node /tmp/chk-b2112.js ed3e16e → []  exit=1（旧对旧）
$ node /tmp/chk-b2112.js e47a7ed → []  exit=1（旧对旧）
$ node /tmp/chk-b2112.js WT      → []  exit=1（新对新）
$ node /tmp/chk-b2112.js         → usage…  exit=9
# 变异（/tmp/b2112-mut 一次性临时仓，逐提交构造）
旧 → L6 改成「下周再试」       → exit 4（新值 ≠ 荐案）
「下周再试」→ 荐案             → exit 3（旧值 ≠ 改前原文）
荐案 + L7 delay=1→2（多改一处）→ ['/timeout/0/720/1']  exit 1

# 独立 deep diff（js-yaml；递归比较类型、数组长度、对象键序与叶子值）
changed: ["/timeout/0/720/0"]
top keys ["auto-start","timeout"] → ["auto-start","timeout"]
auto-start ["timeout false timing"] · timeout 长度 1 · timeout[0] 键 ["720"] · 720 列表长度 2 · 720[1]（$end）不变
e47a7ed 与 ed3e16e 解析结果相等（旧对旧）；684ede2 与 HEAD 相等（新对新）

# 3
$ rg "体力已扣|下周再来|已消耗体力|体力 -|扣票|精英票" plugins/DungeonPlus/dungeon/EmberEliteWeekly/task/timeout.yml
（无输出，rc=1）
$ git show 684ede2^:F | rg -n "体力已扣|下周再来|已消耗体力|体力 -|扣票|精英票"
6:      - "$message{type=text;text=§c试炼失败。§7体力已扣，下周再来。} @dungeon"
B0.1 已清 0 · 票已废 0 · 体力 0 · 扣 0 · 票 0 · 首免 0 · 免费 0 · 下周 0

# 6（HANDOFF §8 原样；grep -c 只出计数）
$ set -a; source secrets/mysql-ember.env; set +a
$ (git diff; git ls-files -o --exclude-standard | xargs -r cat) | grep -c -- "$MYSQL_PASSWORD"
0
[ -n "$MYSQL_PASSWORD" ] → true（未打印值）
$ cat server-runtime/ops.json
[]
```

## 文案与行为核对（第 4 点；Java 路径前缀 `CoreRpg/src/main/java/town/sunshine/corerpg/`）

| 新文案 / 前提 | 依据 | 判定 |
|---|---|---|
| 触发：进本即计时，720 秒后先发 L6、1 秒后 L7 结束 | `task/timeout.yml:1-2` `auto-start: "timeout false timing"`；`:5` `720`；`:7` `$end{…;reward=false;delay=1;end-type=FAILURE}`；720 为秒的旁证：`EmberDaily/task/timeout.yml:1`「约 12 分钟」对 720、`EmberGuildBoss/task/timeout.yml:1`「约 15 分钟」对 900、`EmberRaid/task/timeout.yml:1`「约 40 分钟」对 2400、`EmberCalamity/task/timeout.yml:1`「约 10 分钟」对 600；本本 `task/` 下仅 `timeout.yml` | 与 tip §2 一致 |
| 「这次不算通关」：超时不写 `elite_weekly_clear` | 通关标记全仓唯一写入链：`EmberEliteWeekly/option.yml:36` reward-script `corerpg progress %player_name% elite_weekly` → `ProgressService.java:471-481`（`:479` `addLootWeekMark(EliteService.CLEAR_MARK, week)`）；`EliteService.markClearedThisWeek`（`EliteService.java:55-58`）全仓无调用方（`rg markClearedThisWeek` 仅定义处）；reward-script 仅由 `reward=true` 的 `$end` 触发——本本唯一 `reward=true` 为 `monster.yml:57`（COMPLETE），超时 `timeout.yml:7` 为 `reward=false`；`option.yml:37` quest 事件同在 reward-script，超时亦不触发 | 配置层成立（DP jar 不在仓，`reward=false` 不跑 reward-script 以配置语义 + `EmberAbyss/task/timeout.yml:1`「reward=false 防双箱」旁证） |
| 「本周还能再来」：同周门槛放行 | `EliteService.java:40-47` `passesGate` 只判等级（:43-44）与 `isClearedThisWeek`（:45，`:49-53` 查 `elite_weekly_clear=<weekId>`）；DP `option.yml:17` `%corerpg_gate_elite%`（`CoreRpgExpansion.java:49-55` → `passesGate`）；发起者预检 `TicketEntryService.java:115-121` 同查 `isClearedThisWeek`——超时未写标记，三处均放行 | 成立 |
| 再来的费用 | `StaminaService.java:205-214` 换周 `setWeeklyGrantCreditElite(1)`（:211）；`:306-310` ELITE 先扣首免；首免已用则 `:316-326` 按 `cash.yml:26` `elite: 40` 扣体力，不足则 `:319-322` 拒进（「体力不足…明日 0 点恢复」）；超时发生在进本约 12 分钟后，早过 `TicketEntryService.java:186-201`（`ENTER_VERIFY_TICKS=40`，`:82`，约 2 秒）的在本校验，不退还 | 新行不写费用，正确；旧「体力已扣」在首免（`TicketEntryService.java:142`）/ OP（`:140`）时不准 |
| 与 B2.101 L20 一致 | `EmberEliteWeekly/option.yml:20`「每人每周限通关一次，没过可以再来——词缀会咬人。」；`ember_hub.yml:249`「每人每周限通关 1 次」（B2.102） | 「这次不算通关，本周还能再来」与「没过可以再来」同口径 |

**边界核对（是否过度承诺）：**

1. **队员已在别队通关：**已通关者无法进入本局——DP `option.yml:17` 对每名队员解析 `%corerpg_gate_elite%`（实服旁证 `docs/smoke-raid-combat-20260926.md:40`「js-condition 对每个队员都查」，DP 源码不在仓），发起者则在 `TicketEntryService.java:115-121` 被拦。故能看到 L6 的人本周必定尚未通关，「这次不算通关，本周还能再来」对每位收件人都成立。OP 豁免门槛（`TicketEntryService.java:103`，`option.yml:17` `%player_is_op%`），与玩家口径无关。
2. **720 秒计时与通关竞态：**`monster.yml:57` COMPLETE 的 `$end` 带 `delay=2`；若最后一击落在约 718～720 秒，超时 `$end` 可能在 COMPLETE 生效前抢先结束本局（此时 reward-script 不跑、无标记，L6 说「不算通关」反而与实际结算一致）。DP 两个 `$end` 的先后判定无法读源码，记入待恢复服后实测，不构成文案问题。
3. **「本周还能再来」的前提：**门槛放行 ≠ 免费。首免若已在本局用掉，同周再进要扣 40 体力，体力不足时需等次日 0:00 回满；另 DP `config.yml:136` `start-interval: true`（出本后约 5 秒才能再开，`TicketEntryService.java:78-79` 已有「缓存冷却」提示且未进本会退还）。新行只说资格、不说免费，不算过度承诺。
4. **周界：**周日 23:48 后开本、跨过周一 0:00 才超时的情况，新周本来就可再进（`DailyService.weekId`，ISO 周 Asia/Shanghai），「本周还能再来」不会误导。
5. **其它失败路径：**全员死亡超 10 秒无人复活由 DP 自动 leave（`plugins/DungeonPlus/config.yml:51` `dungeon-timeout-revive: 10`），离线超 120 秒自动退出（`:60-64`）——这些路径同样不写标记、但不会显示 L6；不在本行范围。
6. **结论：**新可见文本「试炼失败。这次不算通关，本周还能再来。」与现行配置 / 源码一致；不写费用与数字；无过度承诺。

## 依赖检索（第 5 点）

- **mineflayer-tests（排除 node_modules）：**`rg '体力已扣|下周再来|试炼失败|精英试炼超时|不算通关|本周还能再来'` → **0 命中**。
- **非 docs 其它位置：**
  - `CoreRpg/src/main/java/town/sunshine/corerpg/TicketEntryService.java:118`「本周已通关精英试炼，下周再来」——已通关者拒进提示，「下周再来」对其正确，**不改、非依赖**。
  - 根目录 `STATUS-ember-stage4-4.6.md:37,79`：历史测报引用上述拒进提示。
- **docs（只列，历史引用，不改）：**13 个文件；旧文来源链为 `docs/design-stage4-elite-weekly.md:84`（原始「票已扣，下周再来」）→ `docs/design-ember-dp-ticket-stamina-copy.md:47,63` 与 `docs/STATUS-ember-dp-ticket-stamina-copy.md:21`（改为「体力已扣，下周再来」）→ `docs/TEST-B2.101-dp-elite-start-text.md`（测岗报冲突）→ 本 tip。
- **B2.109：**无新增依赖。仅建议在 B2.109 的精英覆盖用例（B2.101 已列「无 EmberEliteWeekly 脚本」）里加一条超时用例：断言新 L6 文本、`corerpg elite status` 本周未通关、同周可再进（需把超时临时调短或走长测，由总控定）。

## 前提更正 / 补充

1. **设计 / 总控前提核对：无实质错误。**逐条核对属实：tip §1「位于 `timeout[0].720[0]`，后跟 L7」；§2 L22 四个 timeout 注释与秒数；L23「本本唯一的 task」；L25 `ProgressService` L471-481；L26 `markClearedThisWeek`（L55）无调用方；L28 `EmberAbyss/task/timeout.yml` L1「reward=false 防双箱」；L29 `TicketEntryService` L115-121；L31 L142 / L140；L32 `refundEnter` 约 2 秒；§4 L50「整段可直接粘贴、无缩进」（块内 0 缩进行，heredoc 重跑产物与 `/tmp` 文件相同）；§6 表六个文件:行与现文全部一致，「七个日常本仅 EmberDaily 有 task 目录」属实（`plugins/DungeonPlus/dungeon/*/task` 仅 7 个本）。
2. **措辞小点（不影响结论）：**tip §2 L22「单位按同目录其它本的注释对照」——对照的是其它副本目录下的 `task/timeout.yml`，并非同一目录；且 `EmberWeekly/task/timeout.yml`（1500）与 `EmberAbyss/task/timeout.yml` 无时长注释。
3. **简报措辞：**「同周 gate 会放行，第一次首免，之后扣体力」——准确说法是「本周第一次进本用首免」；若超时这局就是发起者本周首次进本，首免已在这局用掉，超时后再来要扣 40 体力（换一名尚有首免的队员发起则可用其首免）。
4. **「reward=false 不跑 reward-script」为配置层核对**：DP jar 不在仓（HANDOFF §8 不入库），与总控批注口径一致。

## 范围外备注（不在本窗）

- 总控已排：**B2.115** `EmberWeekly/task/timeout.yml:6`、**B2.116** `EmberRaid/task/timeout.yml:7`（「体力不返还」首免 / OP 时不准）；`EmberDaily/task/timeout.yml:7`、`EmberAbyss/task/timeout.yml:8` 只记不排；`EmberGuildBoss/task/timeout.yml:7` 为贡献体系，不属本类。
- **B2.114** `plugins/CoreRpg/quest.yml:389`（src 模板同行可不排）照总控排序。
- `EmberEliteWeekly/task/timeout.yml:7` `$end` 文案「精英试炼超时失败」未动（tip §5）。

## 旁证

1. **施工范围：**`684ede2` 只改 `task/timeout.yml` L6 一行 `text=` 中段；解析后仅 `/timeout/0/720/0` 变化，`auto-start`、720、L7 `$end`（含 `reward=false`、`FAILURE`）全同。
2. **精确行文：**live、施工 tip、新增行与 tip §3 sha256 相同；旧行与 tip §1 相同。
3. **口径：**未写费用与数字；未写「票已废」「B0.1 已清」；NI 票物、数值、时长未动。
4. **范围外未动：**`e47a7ed^..HEAD` 期间触及 `plugins/`、`CoreRpg/`、`mineflayer-tests/` 的只有 `684ede2`（仅本文件）；本文件上一次改动为 `9311a72`（2026-09-28 23:39 CST）。
5. **做法纪律：**仅静态 git / sed / diff / cmp / node js-yaml / python / rg；未起服、未 reload、未长测、未结束任何进程；主仓未改任何文件（仅新增本报告）；未覆写 `/tmp/chk-b2112.js`（另生成的 `/tmp/b2112-regen/`、`/tmp/b2112-mut/` 为一次性临时文件，不入库）；机密未回显。
6. **时间戳：**tip STATUS 与总控批注均写「2026-10-01 03:34」批 A；设计 commit `e47a7ed` 03:33:05、批准 commit `ed3e16e` 03:34:20 CST，一致；tip L6「总控 03:31 排为 B2.112」早于设计 commit，合理；施工 `684ede2` 03:34:56 在批准之后，顺序正确。

## 阻塞点

无（第 7 项实服观察待恢复服后实测）。

## 回报摘要

- 总评：PASS
- 各点：0 PASS / 1 PASS / 2 PASS / 3 PASS / 4 PASS（附边界备注）/ 5 PASS / 6 PASS / 7 SKIP（待恢复服后实测）
- 前提更正：无实质错误；tip §2 L22「同目录」措辞；简报「第一次首免」需理解为本周第一次进本
- 施工 tip SHA：`684ede2`
- 测报 tip short SHA：待本地 commit 后回报
- 是否已 push：否
- 报告路径：`docs/TEST-B2.112-dp-elite-timeout-text.md`
- 阻塞点：无
- ahead：相对 `origin/main` ahead 1（未 push）
- `ops.json`：`[]`

附记（本测报 commit 后）：`node /tmp/chk-b2112.js HEAD`（HEAD^ = `684ede2`，新对新）→ 打印 `[]`，exit 1；脚本新对新 / 旧对旧均失败，不会假 PASS。
