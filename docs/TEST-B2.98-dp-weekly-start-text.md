# B2.98 · DP `EmberWeekly/option.yml` L20 开本提示去扣费字样 · 纯静态薄验收

- **总评：PASS**（reload / 实服观察项记 SKIP：待恢复服后实测，不挡 PASS）
- **测报时间：**2026-10-01 02:45 Asia/Shanghai
- **岗别：**余烬-测试岗（B2.98 纯静态薄验收）
- **工作区：**`/workspace/minecraft`（main，与 `origin/main` 同步；三个 commit 本地均存在且为 HEAD 祖先，无需 fetch）
- **施工 tip SHA：**`94d8759`（2026-10-01 02:40:33 CST）
- **设计 / 批准：**`58103e9`（02:38:34 CST）/ `baccdf4`（02:39:55 CST）
- **tip：**`docs/design-ember-dp-weekly-start-text-copy.md`（§1 现行行为、§2 荐案、§3 验收、§4 明确不做、§6 余项排序、文末总控批注）
- **改动性质：**玩家可见 `text=`（非注释）
- **是否已 push：否**（本岗仅本地 commit，禁止 push）
- **报告路径：**`docs/TEST-B2.98-dp-weekly-start-text.md`
- **ahead：**相对 `origin/main` ahead 1（本测报本地 commit 后，未 push）
- **`server-runtime/ops.json`：**`[]`（未改动）
- **本机服状态：**25565/25566/25567/3306 均未监听（`ss -ltn` 计数 0），按总控批注不起服

## 各点

| # | 条件 | 结果 |
|---|------|------|
| 1 | 施工 commit 只改 `plugins/DungeonPlus/dungeon/EmberWeekly/option.yml`，numstat `1 1`，只改 L20；新 L20 逐字等于 §2 荐案；无 CR / 行尾空白；缩进与旧 L20 相同；旧行对设计旧行 | **PASS** · numstat `1	1`；hunk 头 `@@ -20 +20 @@ dungeon-start:`，`diff` 仅 `20c20`；§2 yaml 块（1 行）与 live L20、`94d8759` L20、施工新增行 `cmp` 一致，sha256 相同；CR 字节 0、行尾空白 0；新旧 L20 均 4 空格缩进、无 tab；删除行 = 父版本 L20，去缩进后与 §1 L12 引用的旧行逐字一致 |
| 2 | 设计 js-yaml 命令原样输出 `only-L20`；独立深比对仅 `dungeon-start.action-script[0]` 变化；HEAD 与 `94d8759` 一致 | **PASS** · 原样命令（HEAD=`94d8759`）输出 `only-L20`，exit 0；独立递归深比对 `94d8759^` vs `94d8759` 变化路径仅 `[".dungeon-start.action-script[0]"]`，`action-script` 长度 3→3；除 L20 外全文逐行相同；HEAD、工作区与 `94d8759` 字节一致，之后无 commit 触及 |
| 3 | 违禁词 / 扣费字样 | **PASS** · 全文件「×1」0、「已消耗体力」0（`rg -n "×1|已消耗体力"` 无命中）；设计额外口径：「已消耗」0、「消耗」0、「B0.1 已清」0、「票已废」0；新增行中「体力」「扣」「票」「45」「首次免费」均 0（文件内这些词只在 B2.94 已验的 L4/L5/L16/L18 注释里） |
| 4 | 前提核对：发起者私聊「正在进入……（costHint）」；新 L20 与之一致、不重复不矛盾不夸大；测试脚本不依赖旧文案 | **PASS（附一处前提补充）** · 见下「行为核对」与「测试脚本依赖」 |
| 5 | HANDOFF §8 查密码命令输出 0；`ops.json` 为 `[]` | **PASS** · 原样执行（`grep -c` 只出计数）输出 `0`，变量非空 `true`（未打印值）；`ops.json` 为 `[]` |
| 6 | `/dp reload` 后实测：本内只见「深核·周 开始！」，发起者仍收「正在进入……」 | **SKIP · 待恢复服后实测**（本机无运行中的服；总控批注明确不挡本窗 PASS） |

## 目标行原文

```
# 父版本 94d8759^ L20（旧）
    - "$message{type=text;text=§c深核·周 开始！已消耗体力 ×1} @dungeon"
# live / 94d8759 L20（新）
    - "$message{type=text;text=§c深核·周 开始！} @dungeon"

# live L19–L22 上下文
19:  action-script:
20:    - "$message{type=text;text=§c深核·周 开始！} @dungeon"
21:    - "$teleport{location=-40,65,270;defspawn=true} @player"
22:    - "$monstergroup{group=wave1;repeat=false;delay=2} @dungeon"
```

## 命令与输出

```
$ git log --oneline -8
94d8759 feat(dp): B2.98 EmberWeekly 开本提示去扣费字样
baccdf4 docs: approve B2.98 dp weekly start text copy (A)
58103e9 docs: B2.98 tip dp weekly start text copy + ticket-era leftovers order pending A
473376c docs: close B2.97 dp elite option ticket comment (PASS)
8e12ca3 docs: B2.97 dp elite option comment test PASS
79257fd feat(dp): B2.97 EmberEliteWeekly option.yml 注释改体力口径
810f0cd docs: approve B2.97 dp elite option ticket comment (A)
aeb2bc1 docs: B2.97 tip dp elite option ticket comment pending A
$ git merge-base --is-ancestor <58103e9|baccdf4|94d8759> HEAD   → 均为祖先
$ git show --stat 58103e9 / baccdf4   → 均只含本 tip 与 docs/design-ember-content-backlog.md

# 1
$ git show --stat --numstat --format= 94d8759
1	1	plugins/DungeonPlus/dungeon/EmberWeekly/option.yml
 plugins/DungeonPlus/dungeon/EmberWeekly/option.yml | 2 +-
 1 file changed, 1 insertion(+), 1 deletion(-)
$ git diff -U0 94d8759^ 94d8759 | grep '^@@'
@@ -20 +20 @@ dungeon-start:
$ diff <(git show 94d8759^:F) <(git show 94d8759:F) | grep -E '^[0-9]'
20c20

# 荐案行：tip §2 首个 ```yaml 块程序提取（1 行）；旧行：tip §1 L12 反引号内原文程序提取
cmp expected live L20            → 一致
cmp expected 94d8759 L20         → 一致
cmp expected 施工新增行(+)        → 一致
cmp 父版本 L20 施工删除行(-)      → 一致
diff <(删除行去行首缩进) 设计 §1 旧行 → 一致
sha256（荐案 / live / 施工 tip / 新增行 四者相同）：e73a71eb13f2cf1907c6f71827b6c08f3ac01b20e81d4264fc2465c55dab2a09

# 行尾 / 缩进
CR 字节（L20）：0；全文件 CR：0；行尾空白：0
cat -A：行首 `    - "$message{type=text;text=M-BM-'c…`（§ 为 UTF-8 C2 A7），行尾 `} @dungeon"$`
94d8759^ L20：indent 4，len 60，无 tab
94d8759  L20：indent 4，len 52，无 tab

# 2（tip §3 原样，仓库根目录，HEAD=94d8759）
$ node -e 'const y=require("js-yaml"),{execSync:x}=require("child_process"),f="plugins/DungeonPlus/dungeon/EmberWeekly/option.yml",g=r=>y.load(x("git show "+r+":"+f).toString());const a=g("HEAD~1"),b=g("HEAD");a["dungeon-start"]["action-script"][0]="$message{type=text;text=§c深核·周 开始！} @dungeon";if(JSON.stringify(a)!==JSON.stringify(b))process.exit(1);console.log("only-L20")'
only-L20
exit=0

# 独立深比对（递归 walk，列出所有变化路径；/usr/share/nodejs/js-yaml）
changed paths: [".dungeon-start.action-script[0]"]
action-script length: 3 -> 3
$ diff <(git show 94d8759^:F | sed '20d') <(git show 94d8759:F | sed '20d')   → ALL_OTHER_LINES_IDENTICAL
HEAD==94d8759（cmp）；worktree==HEAD（cmp）；git log 94d8759..HEAD -- F → 空

# 3（全文件 / 新增行）
×1 0/0 · 已消耗体力 0/0 · 已消耗 0/0 · 消耗 0/0 · B0.1 已清 0/0 · 票已废 0/0
体力 4/0 · 扣 3/0 · 票 2/0 · 45 1/0 · 首次免费 1/0   （全文件命中均在 L4/L5/L16/L18 注释，B2.94 已验）
$ rg -n "×1|已消耗体力" F   → 无命中（rc=1）

# 5（HANDOFF §8 原样；grep -c 只出计数）
$ set -a; source secrets/mysql-ember.env; set +a
$ (git diff; git ls-files -o --exclude-standard | xargs -r cat) | grep -c -- "$MYSQL_PASSWORD"
0
[ -n "$MYSQL_PASSWORD" ] → true（未打印值）
$ cat server-runtime/ops.json
[]
```

## 行为核对（第 4 点；路径前缀 `CoreRpg/src/main/java/town/sunshine/corerpg/`）

**私聊提示位置与 costHint 原文（前提核对：`TicketEntryService.java:149` 正确）：**

```
TicketEntryService.java:138        String costHint;
TicketEntryService.java:139-140    if (op) { costHint = "管理免扣";
TicketEntryService.java:141-142    } else if (consumed != null && consumed.usedCredit) { costHint = "本周首次免费";
TicketEntryService.java:143-144    } else if (consumed != null && consumed.cost > 0) { costHint = "体力 -" + consumed.cost;
TicketEntryService.java:145-146    } else { costHint = "无消耗"; }
TicketEntryService.java:148-149    player.sendMessage(ChatColor.YELLOW + "[" + kind.shortLabel + "] "
                                           + ChatColor.GRAY + "正在进入……（" + costHint + "）");
```

- `sendMessage` 语句起于 L148，「正在进入……（costHint）」字符串在 **L149**，总控批注行号正确。
- 周本 `kind.shortLabel` = 「周本」（`TicketEntryService.java:27`），即发起者看到「[周本] 正在进入……（本周首次免费｜体力 -45｜管理免扣）」，与 tip §1 L13 一致。
- 覆盖三种情况：首免 → 实际字符串是「本周首次免费」（简报写的「首免」是简称，代码里不出现「首免」二字）；体力 → 「体力 -N」（周本 N=45，来自 `consumed.cost`，即 `cash.yml` `stamina.costs.weekly`）；OP/`corerpg.admin` → 「管理免扣」（`op` 定义于 `TicketEntryService.java:103`）。
- **前提补充：**还有第 4 个分支「无消耗」（`:145-146`，cost ≤ 0 且非首免时）。周本现行 cost 45，正常不会走到；tip §1 / 总控批注只列了三种，不影响结论。
- 私聊只发给发起者（`player.sendMessage`，`player` 即执行 `corerpg enter weekly` 的人）；发送时机在扣费之后、`dp start-console`（`:159-162`）之前。若随后未进本，退还走 `refundEnter`（`:167-169`、`:186-201`），并另发「体力已退还，若已扣」（`:210`）。

**新 L20 与私聊的一致性判断：**

- L20 是 `@dungeon` 广播，发给本内全体队员（周本 1～3 人，L15 `team-condition`）；新文案「§c深核·周 开始！」不提扣费、不写数字，既不与发起者私聊重复，也不会对首免 / 队员 / OP 产生错误信息（旧文案「已消耗体力 ×1」对这三类都不成立，且易读成扣 1 点）。
- 无夸大：新文案只剩原标题，没有新增任何玩法或数值承诺；颜色码 `§c`、全角「！」、引号与 `@dungeon` 原样保留。
- 结论：一致，无矛盾、无重复、无过度承诺。

## 测试脚本依赖（第 4 点）

- `mineflayer-tests/stamina-s0-smoke.js`（161 行）：周本判定 L111 `includes('本周首次免费') || includes('正在进入')`、L120 `includes('体力 -45') || includes('正在进入')`，只依赖 CoreRpg 私聊，**不依赖 L20**；「已消耗体力」只在日常判定 L64、L65 的备选条件里，日常七线 option.yml 仍含「已消耗体力」（各 1 处），不受本窗影响。脚本内无 `×1`、`深核`、`开始！` 匹配。tip §3 所引 L111 / L120 / L64-65 行号正确。
- 其余 `mineflayer-tests/`（排除 node_modules）：
  - `gates-smoke.js:30` `/开始/.test(r.weekly_ok)`（`/dp start EmberWeekly` 输出）——新 L20 仍含「开始」，**不受影响**。（同一判定还要求 `ticket_ember_weekly` 数量 −1，属票时代断言，现行不扣票，与本窗无关，见范围外备注。）
  - `dungeon-clear-smoke.js:95` `/开始|已开启|点燃|降临|第 .?1.? 层/`——仍能匹配「开始」，不受影响。
  - `dungeon-balance.js:15` 周本只匹配 `/深核·周 通关/` 与各室名，不依赖开本句。
  - 其它 `×1` 命中（`ticket-grant-smoke.js:5-6` 注释、`new-player-smoke.js:55`「日票 ×1」菜单名、`ash-brace-contention-copy-light.js` / `guild-boss-smoke.js` / `pet-feed-smoke.js` 的物品数量）均与周本开本句无关。
  - **结论：无任何测试脚本依赖旧 L20 的「已消耗体力 ×1」。**

## 前提核对小结

| 前提 | 核对 | 结论 |
|---|---|---|
| 总控批注：私聊在 `TicketEntryService.java:149` | 语句 L148–149，字符串在 L149 | 正确 |
| tip §1：私聊内容「[周本] 正在进入……（本周首次免费 / 体力 -45 / 管理免扣）」 | `:27,138-149` | 正确；另有「无消耗」第 4 分支（周本现行不会触发） |
| 简报：costHint 含「首免」 | 代码字符串为「本周首次免费」 | 措辞差异，语义一致 |
| tip §1：L20 `@dungeon` 发给本内所有队员（1～3 人） | option.yml L15 `min=1;max=3`、L20 `@dungeon` | 正确 |
| tip §3：`stamina-s0-smoke.js` L111、L120、L64-65 | 行号与内容一致 | 正确 |
| tip §5：日常七线仍为「已消耗体力 ·」 | 7 个 EmberDaily* option.yml 各 1 处「已消耗体力」 | 正确（Raid / Abyss 各 1 处，属 B2.99 / B2.100） |

## 范围外备注（不在本窗）

- `mineflayer-tests/gates-smoke.js:26-30`：注释「weekly starts, ticket consumed」及断言 `count(b, 'ticket_ember_weekly') === wt0 - 1` 仍按票时代写；现行进本不扣票，该断言在实服上会 FAIL。与 tip §6 B2.109（mineflayer 旧正则）同类，建议并入。
- `mineflayer-tests/stamina-s0-smoke.js:120`：`weeklyPaid` 条件含 `|| includes('正在进入')`，第二次进本即使走了首免也会判 PASS，断言偏松；仅记录。
- `task/timeout.yml` 的「周常超时失败（体力不返还）」按 tip §4 不改。

## 旁证

1. **施工范围：**`94d8759` 只改 `EmberWeekly/option.yml` L20 一行 `text=`；解析后仅 `dungeon-start.action-script[0]` 变化，列表长度不变，其余键、注释、脚本逐行相同。
2. **精确行文：**live、施工 tip、新增行与 tip §2 荐案四者 sha256 相同；缩进 4 空格与旧行一致。
3. **口径：**删去「已消耗体力 ×1」，未新增数字或玩法描述；未写「票已废」「B0.1 已清」；NI 票物、数值未动。
4. **范围外未动：**`58103e9^..HEAD` 期间无 commit 触及 `plugins/CoreRpg`、`CoreRpg/`、`plugins/TrMenu`、`mineflayer-tests`；其它 option.yml 未动。
5. **做法纪律：**仅静态 git / sed / diff / cmp / node js-yaml / python / rg；未起服、未 `/dp reload`、未长测、未杀进程；未改任何文件（仅新增本报告）；机密未回显。
6. **时间戳：**tip 与总控批注写「2026-10-01 02:39」批 A，批准 commit `baccdf4` 为 02:39:55 CST，一致；顺序设计 → 批准 → 施工正确。

## 阻塞点

无（第 6 项实服观察待恢复服后实测）。

## 回报摘要

- 总评：PASS
- 各点：1 PASS / 2 PASS / 3 PASS / 4 PASS（附「无消耗」第 4 分支前提补充）/ 5 PASS / 6 SKIP（待恢复服后实测）
- 施工 tip SHA：`94d8759`
- 测报 tip short SHA：待本地 commit 后回报
- 是否已 push：否
- 报告路径：`docs/TEST-B2.98-dp-weekly-start-text.md`
- 阻塞点：无
- ahead：相对 `origin/main` ahead 1（未 push）
- `ops.json`：`[]`
