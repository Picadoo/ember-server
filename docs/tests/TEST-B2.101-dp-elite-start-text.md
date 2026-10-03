# B2.101 · DP `EmberEliteWeekly/option.yml` L20 开本提示「本周只有一次」→「每人每周限通关一次」· 纯静态薄验收

- **总评：PASS**（reload / 实服观察项记 SKIP：待恢复服后实测，不挡 PASS）
- **测报时间：**2026-10-01 03:15 Asia/Shanghai
- **岗别：**余烬-测试岗（B2.101 纯静态薄验收）
- **工作区：**`/workspace/minecraft`（main，与 `origin/main` 同步；三个 commit 本地均存在且为 HEAD 祖先，无需 fetch）
- **施工 tip SHA：**`e55540a`（2026-10-01 03:11:44 CST）
- **设计 / 批准：**`86d83c1`（03:10:06 CST）/ `910d284`（03:11:06 CST）
- **tip：**`docs/design/design-ember-dp-elite-start-text-copy.md`（§1 完整旧行、§2 现行规则、§3 荐案、§4 验收、§5 明确不做、§6 顺带发现、文末总控批注）
- **改动性质：**玩家可见 `text=`（非注释）
- **是否已 push：否**（本岗仅本地 commit，禁止 push）
- **报告路径：**`docs/tests/TEST-B2.101-dp-elite-start-text.md`
- **ahead：**相对 `origin/main` ahead 1（本测报本地 commit 后，未 push）
- **`server-runtime/ops.json`：**`[]`（未改动）
- **本机服状态：**25565/25566/25567/3306 均未监听（`ss -ltn` 计数 0），按总控批注不起服

## 各点

| # | 条件 | 结果 |
|---|------|------|
| 1 | 施工 commit 只改 `plugins/DungeonPlus/dungeon/EmberEliteWeekly/option.yml`，numstat `1 1`，hunk `@@ -20 +20 @@`；旧行 = §1；新行 = 荐案；4 空格、无 tab/CR/行尾空白 | **PASS** · numstat `1	1`；hunk `@@ -20 +20 @@ dungeon-start:`，`diff` 仅 `20c20`；旧行与 tip §1 块、施工删除行一致（sha256 `a71e396c…d1b0a7`）；新行与 tip §3 块、live、HEAD、施工新增行一致（sha256 `84fde841…40fb576`）；公共前缀「…§e精英试炼开启。§7」与公共后缀「——词缀会咬人。} @dungeon"」不变，仅中段「本周只有一次」→「每人每周限通关一次，没过可以再来」；长度 111→141；缩进 4 空格，tab 0、CR 0、行尾空白 0（全文件同） |
| 2 | 设计 js-yaml 命令原样 `only-L20`，且确能识别「未改」；独立深比对只 `[0]` 变、长度 3；HEAD = `e55540a` | **PASS** · 原样命令 `B=e55540a` 输出 `only-L20`（exit 0）；命令确为 `B^` 对 `B`，并断言旧值 = O（exit 3）、新值 = N（exit 4）、两侧长度 3（exit 2）；对照实验：原样命令 `B=910d284`、`B=86d83c1`（旧对旧）均 exit 4，改写副本两侧同取 `e55540a`（新对新）exit 3，均不会误报；独立深比对变化路径仅 `[".dungeon-start.action-script[0]"]`，长度 3→3，下标 1/2 相同，顶层 / `dungeon-start` 键序相同，reward-script 10 项全同；除 L20 外逐行相同；HEAD、工作区与 `e55540a` 字节一致 |
| 3 | 原样 `rg` 0 命中；「B0.1 已清」「票已废」0；设计附加项 | **PASS** · 原样 `rg "已消耗体力|体力 ×|体力 -|扣票|精英票|只有一次" …/EmberEliteWeekly/option.yml` 无输出（rc=1）；施工前同命令仅命中 L20（tip §4 L59 属实）；「B0.1 已清」0、「票已废」0、「已消耗」0、「消耗」0、「×」0、「只有一次」0；新 L20 中「体力 / 扣 / 票 / 首免 / 免费 / 40」均 0；设计收窄口径无范围外误中 |
| 4 | 新文案与现行行为一致，边界不夸大 | **PASS（附边界备注 + 范围外冲突 1 条）** · 见下「文案与行为核对」 |
| 5 | 两个入口均到 `tryEnter` → 私聊 costHint；mineflayer 依赖；hub L249 口径 | **PASS** · 见下「入口与私聊」 |
| 6 | HANDOFF §8 查密码命令 0；`ops.json` `[]` | **PASS** · 原样执行（`docs/handoff/HANDOFF.md:140-141`，`grep -c` 只出计数）输出 `0`，变量非空 `true`（未打印值）；`ops.json` 为 `[]` |
| 7 | reload 后实测：本内见新 L20；私聊「[精英] 正在进入……（本周首次免费 / 体力 -40）」；失败后同周可再进；通关后再点提示「本周已通关精英试炼，下周再来」 | **SKIP · 待恢复服后实测**（本机无运行中的服；总控批注明确不挡 PASS） |

## 目标行原文

```
# 父版本 e55540a^ L20（旧；= tip §1）
    - "$message{type=text;text=§e精英试炼开启。§7本周只有一次——词缀会咬人。} @dungeon"
# live / e55540a L20（新；= tip §3 荐案）
    - "$message{type=text;text=§e精英试炼开启。§7每人每周限通关一次，没过可以再来——词缀会咬人。} @dungeon"
# 新行玩家可见文本（去 § 色码）
精英试炼开启。每人每周限通关一次，没过可以再来——词缀会咬人。

# live L19–L22 上下文
19:  action-script:
20:    - "$message{type=text;text=§e精英试炼开启。§7每人每周限通关一次，没过可以再来——词缀会咬人。} @dungeon"
21:    - "$teleport{location=-35,70,270;defspawn=true} @player"
22:    - "$monstergroup{group=wave1;repeat=false;delay=2} @dungeon"
```

## 命令与输出

```
$ git log --oneline -6
e55540a feat(dp): B2.101 EmberEliteWeekly 开本提示改每人每周限通关一次
910d284 docs: approve B2.101 dp elite start text (A)
86d83c1 docs: B2.101 design tip — DP EmberEliteWeekly L20「本周只有一次」→ 每人每周限通关一次（待批 A）
00a2952 docs: close B2.100 dp abyss start text (PASS)
c3d662c docs: B2.100 dp abyss start text test PASS
252fe8e feat(dp): B2.100 EmberAbyss 开本提示去扣费字样
$ git merge-base --is-ancestor <86d83c1|910d284|e55540a> HEAD   → 均为祖先
$ git show --stat 86d83c1 / 910d284   → 均只含本 tip 与 docs/design/design-ember-content-backlog.md

# 1
$ git show --numstat --format= e55540a
1	1	plugins/DungeonPlus/dungeon/EmberEliteWeekly/option.yml
$ git diff -U0 e55540a^ e55540a | grep '^@@'
@@ -20 +20 @@ dungeon-start:
$ diff <(git show e55540a^:F) <(git show e55540a:F) | grep -E '^[0-9]'
20c20

# tip 两个 ```yaml 块程序提取（各 1 行）
old == tip §1 块 True；new == tip §3 块 True；删除行(-) == old True；新增行(+) == new True；live == new True；HEAD == new True
sha256 new（§3 / new / live / 新增行 四者相同）：84fde84135561224469f6f76dd645df684dade25890429dfcbe31c75340fb576
sha256 old（§1 / old / 删除行 三者相同）：a71e396cbbf1eb07216e4f6f99cb858b281ac02b0043ea5ca1c4a05713d1b0a7
old: len 111 · indent 4 · tab 0 · CR 0 · 行尾空白 无
new: len 141 · indent 4 · tab 0 · CR 0 · 行尾空白 无
全文件：CR 0 · 行尾空白行 0 · tab 0
公共前缀 `    - "$message{type=text;text=§e精英试炼开启。§7` · 旧中段「本周只有一次」→ 新中段「每人每周限通关一次，没过可以再来」 · 公共后缀 `——词缀会咬人。} @dungeon"`
cat -A（新）：行首 `    - "$message{type=text;text=M-BM-'eM-gM-2M->…`（§ 为 C2 A7），行尾 `…M-^@M-^B} @dungeon"$`

# 2（tip §4 L55 原样，仓库根目录）
$ B=e55540a; node -e '…（tip §4 L55 原文，略）…' "$B"
only-L20
exit=0
# 对照实验（验证命令能识别「未改」/「比错提交」）
原样 B=910d284（910d284^ 与 910d284 均为旧行）→ exit 4（新值 ≠ 荐案）
原样 B=86d83c1（同为旧对旧）→ exit 4
改写副本：g(B+"^") 换成 g(B)，B=e55540a（新对新）→ exit 3（旧值 ≠ 改前原文）
→ 与 B2.99/B2.100 的 HEAD~1 版不同，本命令新对新 / 旧对旧都会失败，不会假 PASS

# 独立深比对（递归 walk；js-yaml）
changed paths: [".dungeon-start.action-script[0]"]
len 3 -> 3
idx 0 CHANGED / idx 1 same / idx 2 same
top keys same: true（dungeon-init-script,dungeon-start,dungeon-area,dungeon-reward-script）
dungeon-start keys same: true
reward-script len 10 same: true
$ diff <(git show e55540a^:F | sed '20d') <(git show e55540a:F | sed '20d')   → ALL_OTHER_LINES_IDENTICAL
HEAD==e55540a==worktree（cmp）；git log e55540a..HEAD -- F → 空

# 3
$ rg "已消耗体力|体力 ×|体力 -|扣票|精英票|只有一次" plugins/DungeonPlus/dungeon/EmberEliteWeekly/option.yml
（无输出，rc=1）
$ git show e55540a^:F | rg -n "已消耗体力|体力 ×|体力 -|扣票|精英票|只有一次"
20:    - "$message{type=text;text=§e精英试炼开启。§7本周只有一次——词缀会咬人。} @dungeon"
全文件 / 新 L20：B0.1 已清 0/0 · 票已废 0/0 · 已消耗 0/0 · 消耗 0/0 · × 0/0 · 只有一次 0/0
（「体力」4、「扣」4、「票」3、「首免」2、「免费」1、「40」2 全部在 L3/L4/L16/L18 维护注释，B2.97 已验；新 L20 均 0）

# 6（HANDOFF §8 原样；grep -c 只出计数）
$ set -a; source secrets/mysql-ember.env; set +a
$ (git diff; git ls-files -o --exclude-standard | xargs -r cat) | grep -c -- "$MYSQL_PASSWORD"
0
[ -n "$MYSQL_PASSWORD" ] → true（未打印值）
$ cat server-runtime/ops.json
[]
```

## 文案与行为核对（第 4 点；Java 路径前缀 `CoreRpg/src/main/java/town/sunshine/corerpg/`）

| 新文案 | 源码 / 配置依据 | 判定 |
|---|---|---|
| 「每人每周限**通关**一次」——门槛 | `EliteService.java:40-47` `passesGate`：等级 ≥ `gateLevel("elite")`（:43-44）且 `!isClearedThisWeek`（:45）；`isClearedThisWeek` `:49-53` 查 `lootWeekMarks` 含 `elite_weekly_clear=<weekId>`；PAPI `%corerpg_gate_elite%` → `CoreRpgExpansion.java:49-55` 走 `EliteService.passesGate`；DP 条件 `option.yml:17` | 限的是「本周已通关」，未限进本次数 |
| 发起者预检 | `TicketEntryService.java:115-121` ELITE 判 `isClearedThisWeek`，已通关提示「本周已通关精英试炼，下周再来」并在扣费前 `return`（扣费在 `:130`） | 一致 |
| 「每人」——标记按人写 | COMPLETE 唯一出口 `monster.yml:57` `$end{…reward=true;…;end-type=COMPLETE}` → reward-script `option.yml:36` `corerpg progress %player_name% elite_weekly;console=true} @player`（逐人）→ `ProgressService.java:471-481`：未有标记则 `addLootWeekMark(CLEAR_MARK, week)` 并落盘（:479-480），已有则报 already 并 `:490` 跳过双倍经验；周界 `DailyService.weekId()`（ISO 周，Asia/Shanghai 周一 0:00） | 一致 |
| 「没过可以再来」——失败不写标记 | 全目录 `rg progress|elite_weekly|weekly-first|end-type` 仅 `option.yml:35-37`（reward-script）、`monster.yml:57`（COMPLETE）与 `task/timeout.yml:7`（`reward=false;end-type=FAILURE`，:6-7 只发消息与 `$end`，不跑任何 `$command`）；`obstacle.yml` 为空脚本；团灭 / `/dp leave` 由 DP 结束，不经 reward-script（`reward=true` 仅 COMPLETE） | 失败 / 超时 / 团灭 / 撤出均不写标记，同周可再进 |
| 再进的代价 | 首免：`StaminaService.java:205-214` 换周 `setWeeklyGrantCreditElite(1)`（:211），`:306-310` 先扣额度；之后 `:316-326` 按 `cash.yml:26` `elite: 40` 扣体力；失败不退（timeout 无退还），进本前被拒才退（`TicketEntryService.java:169,186-201` → `StaminaService.java:329-345`） | 与「没过可以再来」一致；新行不写代价，交私聊 |
| 通关箱与稳定符 | 通关箱 `option.yml:30-34` 每次 COMPLETE 逐人发；稳定符 `option.yml:35` → `EliteService.java:76-107`，`elite_weekly_first=<周>` 已有则 already（:93-96） | 每人每周首通 1 枚 |

**边界核对（简报点名的 B2.97 边界）：**

1. **结算瞬间离线的队员：**`ProgressService.java:466` 目标不在线直接返回，不写通关标记；`ni give` / `corerpg loot` / `weekly-first`（`EliteService.java:85-89`）同样不发。此人本周没拿到任何通关奖励、也没被记「通关」，可再进再通——与「限通关一次」不冲突（他这次不计通关）。
2. **非发起者队员：**不付体力 / 首免（`TicketEntryService.java:130` 只扣发起者），但 COMPLETE 时同样被写标记（`@player` 逐人）。新句「每人」正好说清「队友各算各的」；不付费却被计次是既有规则，新句未写费用，不构成承诺偏差。
3. **门槛是否对全队判定：**DP `$js-condition` 对**每名队员**分别解析 `%corerpg_gate_elite%`——仓库无 DP jar 可读源码，旁证为实服烟测 `docs/tests/smoke-raid-combat-20260926.md:40`「DP 的 js-condition 对**每个队员**都查票，缺一个全队拒进」（同一机制，当时条件为查票）。因此本周已通关的队员被拉入时，DP 整队拒进（提示 `option.yml:17`「…且本周尚未通关」），发起者约 2 秒后被退还体力 / 首免（`TicketEntryService.java:186-201`）；已通关队员**无法被再次带进**，拿不到第二份奖励。若门槛只判发起者，则会出现「已通关队员再通一次、再拿一次通关箱」（`option.yml:30-34` 无去重，仅 `:35` 稳定符与 `:36` 经验去重）——该风险被 DP 逐队员判定挡住，但此结论依赖实服旁证，非源码可证，待恢复服后可顺带实测（B 已通关、A 未通关，A 发起组 B → 应整队拒进并退还）。
4. **稳定符：**`EliteService.java:97-98` 先写 `elite_weekly_first` 标记再发物品；`giveNiItem` 失败时只记 warning（:103），本周不会补发。属极端边界，新句未提稳定符，不涉及。
5. **OP：**`TicketEntryService.java:103-106` 与 `option.yml:17` `%player_is_op%` 双重豁免，OP 可同周重复通关并重复拿通关箱（经验与稳定符去重）。测试便利，非玩家口径。
6. **结论：**新可见文本「精英试炼开启。每人每周限通关一次，没过可以再来——词缀会咬人。」与现行行为一致：「每人」「限通关」「没过可以再来」三点都有源码依据；不含费用与数字，不暗示扣费；句读通顺，原收尾「——词缀会咬人。」保留。无过度承诺。

## 入口与私聊（第 5 点）

- **菜单入口：**`plugins/TrMenu/menus/ember_hub.yml:256` `command: corerpg elite start` → `CoreRpgPlugin.java:829-830` → `EliteService.cmdRoot`（`EliteService.java:117-122`）→ `cmdStart`（`:63-69`）→ `TicketEntryService.tryEnter(player, Kind.ELITE)`。
- **通用入口：**`corerpg enter elite` → `CoreRpgPlugin.java:842-843` → `TicketEntryService.java:235-241` `Kind.parse` → 同一 `tryEnter`（`:100`）。两条入口汇合。
- **私聊：**`Kind.ELITE` `TicketEntryService.java:30`，shortLabel「精英」；`:148-149` →「[精英] 正在进入……（costHint）」。精英可出现 `:140`「管理免扣」、`:142`「本周首次免费」（`StaminaService.java:306-310`）、`:144`「体力 -40」；`:146`「无消耗」仅 `cost ≤ 0` 兜底（现行 40 不会触发）。与 tip §2 L28 一致。
- **新 L20 与私聊分工：**L20（`@dungeon` 全队）讲规则，费用只在私聊。

**hub L249 口径（一致性备注）：**`ember_hub.yml:249`「§7每周 1 次 · 词缀精英 · 周首通稳定符」仍是旧口径，与新 L20「每人每周限通关一次，没过可以再来」不一致（读作只能进 1 次）；`:250`「消耗 §e40 §8体力（本周首次免费）」硬编码 40。总控批注已把 L249+L250 并入 B2.102，tip §3 L49 已给 B2.102 同句式「每人每周限通关 1 次」，方向一致。另：同文件 `:239-240` 灰显图标已用 `%corerpg_stamina_cost_elite%` 占位符，可作为 B2.102 L250 的写法参照。

**mineflayer 依赖（全 `mineflayer-tests/`，排除 node_modules）：**

- `rg '只有一次|本周只有|精英试炼开启|词缀会咬人|限通关|没过可以再来|每人每周|本周已通关|EmberEliteWeekly|elite start|enter elite|ticket_ember_elite|精英票|试炼失败|下周再来'` → **0 命中**：无脚本依赖旧 L20，也无脚本匹配新 L20。
- 相关但不依赖：`quest-vol2-4.5-smoke.js:74-85` 直接 `/corerpg progress <PLAYER> elite_weekly` 触发任务，会给该测试号写本周通关标记（测试副作用：同周该号进不了精英本）。

**B2.109 清单（精英相关）：**

1. **覆盖缺口：**mineflayer 无任何 EmberEliteWeekly 进本 / 通关 / 失败再进脚本；建议 B2.109 补：新 L20 文本、「[精英] 正在进入……（本周首次免费 / 体力 -40）」、通关后再点「本周已通关精英试炼，下周再来」、已通关队员被拉入整队拒进并退还（上文边界 3）。
2. `quest-vol2-4.5-smoke.js:78` 写通关标记的副作用需在脚本注释或用例顺序中说明（与上条共用测试号时会互相干扰）。
3. 既有（B2.99/B2.100 列）照旧：`raid-combat-smoke.js:153-155,170`、`killany-live-retest.js:122`、`abyss-followup.js:58`、`abyss-calamity-supplement.js:58,62,111`、`dungeon-balance.js:16-17,21`、`abyss-reload-smoke.js:17-18`、`gates-smoke.js:22-23,26-30`、`ticket-grant-smoke.js`、`stamina-s0-smoke.js:120`。

## 前提更正 / 补充

1. **（范围外，玩家可见，新发现）超时失败提示与新 L20 冲突：**`plugins/DungeonPlus/dungeon/EmberEliteWeekly/task/timeout.yml:6`「§c试炼失败。§7体力已扣，下周再来。」——「下周再来」与新 L20「没过可以再来」及源码（失败不写标记、同周可再进）直接矛盾；「体力已扣」在首免 / OP 时也不准。tip §5/§6 与总控批注均未列此行，建议新开一窗（与 B2.102 同属精英文案，可排在其后），本窗不改。
2. **tip §2 L26「失败、团灭或撤出都不写通关标记」**属实，但依据只有「唯一写标记的 `progress` 在 reward-script」；团灭 / 撤出的 DP 内部结束路径无法读源码，本报告以「`reward=true` 仅在 `monster.yml:57` COMPLETE」旁证。
3. **tip §2 L23「队员由 DP L17 判同样两项」**：结论成立，但「DP 逐队员解析 PAPI」只有实服旁证（`docs/tests/smoke-raid-combat-20260926.md:40`），非源码可证。
4. **tip §2 L27「首免 `weeklyGrantCreditElite`，现行 40」**：「40」是 `stamina.costs.elite`（`plugins/CoreRpg/cash.yml:26`），不是首免额度（首免每周重置为 1 次，`StaminaService.java:211`）；括号归属易误读，结论不受影响。
5. **tip §2 L32「实际上每人每周最多拿一次通关奖励」**：对普通玩家成立；OP 例外（上文边界 5），离线队员属「未拿到」而非「多拿」。
6. 其余行号核对无误：`EliteService` L40-47 / L49-53 / L85-105、`TicketEntryService` L115-121 / L140-149、`ProgressService` L471-481、option.yml L17 / L30-36、`ember_hub.yml` L249 / L250 / L256。

## 范围外备注（不在本窗）

- `task/timeout.yml:6`「体力已扣，下周再来」（见前提更正 1）。
- `ember_hub.yml:249-250` 已并入 B2.102（总控批注）。
- `EliteService.java:136-139` `/corerpg elite status` 仍显示「精英票」计数（B2.97 已列）。
- 七个日常本「已消耗体力」只记不排。

## 旁证

1. **施工范围：**`e55540a` 只改 `EmberEliteWeekly/option.yml` L20 一行 `text=` 中段；解析后仅 `dungeon-start.action-script[0]` 变化，列表 3 项、顺序与 reward-script 全同。
2. **精确行文：**live、施工 tip、新增行与 tip §3 荐案 sha256 相同；旧行与 tip §1 相同。
3. **口径：**未写费用与数字；未写「票已废」「B0.1 已清」；NI 票物、数值、`monster.yml`、`task/` 未动。
4. **范围外未动：**`86d83c1^..HEAD` 期间触及 `plugins/`、`CoreRpg/`、`mineflayer-tests/` 的只有 `e55540a`（仅本文件）；`task/timeout.yml` 最后改动为 `9311a72`（2026-09-28 23:39 CST）。
5. **做法纪律：**仅静态 git / sed / diff / cmp / node js-yaml / python / rg；未起服、未 `/dp reload`、未长测、未杀进程；未改任何文件（仅新增本报告）；机密未回显。
6. **时间戳：**tip STATUS 与总控批注均写「2026-10-01 03:11」批 A；设计 commit `86d83c1` 03:10:06、批准 commit `910d284` 03:11:06 CST，一致；施工 `e55540a` 03:11:44 在批准之后，顺序正确。

## 阻塞点

无（第 7 项实服观察待恢复服后实测）。

## 回报摘要

- 总评：PASS
- 各点：1 PASS / 2 PASS / 3 PASS / 4 PASS（附边界备注）/ 5 PASS / 6 PASS / 7 SKIP（待恢复服后实测）
- 前提更正：`task/timeout.yml:6`「下周再来」与新 L20 冲突（新窗）；DP 逐队员判定仅实服旁证；tip L27「现行 40」归属
- 施工 tip SHA：`e55540a`
- 测报 tip short SHA：待本地 commit 后回报
- 是否已 push：否
- 报告路径：`docs/tests/TEST-B2.101-dp-elite-start-text.md`
- 阻塞点：无
- ahead：相对 `origin/main` ahead 1（未 push）
- `ops.json`：`[]`
