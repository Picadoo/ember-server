# B2.114 · live `plugins/CoreRpg/quest.yml` L389 主线 hint「精英试炼（每周 1 次）」→「精英试炼（每人每周限通关 1 次）」· 纯静态薄验收

- **总评：PASS**（reload / 实服观察项记 SKIP：待恢复服后实测，不挡 PASS）
- **测报时间：**2026-10-01 13:07 CST（`date`）
- **岗别：**余烬-测试岗（B2.114 纯静态薄验收）
- **开工前：**`git status --porcelain` 空（干净）；`git pull --ff-only` → `Already up to date.`（rc 0，本地未超前、未分叉；B2.112 测报 `4adadc4` 已在 HEAD 祖先中）；复读 `HANDOFF.md`（§8 查密码、`ops.json` 必须 `[]`、提交身份；§6「`pkill -f` 会匹配到自己的 shell，杀进程用 PID」）与 `docs/LESSONS-ember-pipeline.md`（测试 §8「以磁盘 + reload 为准」、§9「只记 PASS/FAIL/SKIP」），两文件自 `2e5649f`（2026-09-27 21:37 CST）后未改。
- **工作区：**`/workspace/minecraft`（main，HEAD = origin/main = `6817eb9`）
- **施工 tip SHA：**`6817eb9`（2026-10-01 13:03:18 CST）
- **设计 / 批准：**`d7cdcc7`（03:43:20 CST）/ `f6531db`（13:02:37 CST）
- **tip：**`docs/design-ember-quest-elite-hint-copy.md`（§1 完整旧行、§2 展示路径、§3 荐案、§4 验收与 heredoc 脚本、§5 明确不做、§6 同类文案、文末总控批注）
- **改动性质：**玩家可见主线任务 hint（聊天栏）
- **是否已 push：否**（本岗仅本地 commit，禁止 push）
- **报告路径：**`docs/TEST-B2.114-quest-elite-hint.md`
- **ahead：**相对 `origin/main` ahead 1（本测报本地 commit 后，未 push）
- **`server-runtime/ops.json`：**`[]`（未改动）
- **本机服状态：**25565/25566/25567/3306 均未监听（`ss -ltn` 计数 0）；未起服、未结束任何进程

## 各点

| # | 条件 | 结果 |
|---|------|------|
| 0 | 三个 commit 时间顺序；设计 / 批准只改文档 | **PASS** · 设计 `d7cdcc7` 03:43:20 → 批准 `f6531db` 13:02:37 → 施工 `6817eb9` 13:03:18（committer = author 时间，CST）；`d7cdcc7`、`f6531db` 均只改 `docs/design-ember-quest-elite-hint-copy.md` 与 `docs/design-ember-content-backlog.md`，两者对 `plugins/CoreRpg/quest.yml` 的 stat 均为空 |
| 1 | `6817eb9` 只改 `plugins/CoreRpg/quest.yml` L389，numstat `1 1`；旧行 = §1；新行 = §3；8 空格；整文件无 tab/CR/行尾空格；src 模板与 L377 未动 | **PASS（附 live/src 不一致标注）** · numstat `1	1`；hunk `@@ -389 +389 @@ chapters:`，`diff` 仅 `389c389`，除 L389 外逐行相同；旧行 = tip §1 块 = 施工删除行（sha256 `c9c02335…f786b3ca`）；新行 = tip §3 块 = live = HEAD = 施工新增行（sha256 `744c0e47…9dd7a11d`）；新旧行缩进均 8 空格；整文件 18293 字节 442 行，字节级 tab 0、CR 0、以空白结尾的行 0（`cat -A` 每行以 `$` 收尾；`cat -A` 元字符表示有 3 类假阳性，见命令区说明）；`CoreRpg/src/main/resources/quest.yml` 在 `d7cdcc7^..HEAD` 无改动（末次 `8605d10`，2026-09-28 23:50 CST），其 L389 仍为旧文（sha256 与旧行相同）；L377 与施工前逐字节相同 |
| 2 | 稿内 heredoc 与 `/tmp/chk-b2114.js` 一致；`6817eb9` → `only-L389 /chapters/9/steps/2/hint` exit 0；旧对旧 / 新对新非 0；临时仓变异 4/3/1；独立 deep diff 仅此一处且路径对应 | **PASS** · `/tmp/chk-b2114.js` 已存在（1095 字节，mtime 13:03:18，与施工 commit 同秒，box 用户）；与 tip §4 ```sh 块 heredoc 正文逐字节相同（sha256 `b99d4b05…ca854829`；块内缩进行 0，无 markdown 缩进需去）；按稿重跑 heredoc（只改落盘路径）产物 `cmp` 一致；**未覆盖** `/tmp` 文件。`6817eb9` → `only-L389 /chapters/9/steps/2/hint` exit 0；旧对旧 `f6531db`、`d7cdcc7` → `[]` exit 1；新对新 `WT` → `[]` exit 1；无参 exit 9。临时仓（`/tmp/b2114-mut*`，与主仓无关）：错误新值 exit 4、错误旧值 exit 3、旧 → 荐案 + 多改 xp 一行 exit 1（打印两条路径）、旧 → 荐案 + 多改 L377 exit 1。独立 deep diff（类型 / 数组长度 / 对象键序 / 叶子值；新文件空容器 0 个）→ 仅 `["/chapters/9/steps/2/hint"]`。路径对应：`chapters` 是**映射**，键 `"1"…"10"`，`/chapters/9` 是**章节号键 `9`**（`quest.yml:368` `9:`，name「第二层誓火」），`steps` 是数组，下标 2 为第 3 步 `type: event / event: elite_weekly_clear / desc: 通关 余烬·精英试炼 一次`（`quest.yml:385-391`） |
| 3 | `rg「每周 1 次」` 0；「B0.1 已清」「票已废」0 | **PASS** · `rg "每周 1 次" plugins/CoreRpg/quest.yml` 无输出 rc=1（施工前仅命中 L389）；`rg "B0.1 已清"`、`rg "票已废"` 均 rc=1；tip §4 附加 `rg -n "已消耗体力|体力 -|扣票|精英票"` rc=1；「首免」0、「扣」0、「票」0 |
| 4 | hint 展示路径不截断；「每人每周限通关 1 次」与门控及 B2.101 / B2.102 口径一致；无体力 / 首免过度承诺 | **PASS** · 见下「展示路径」「口径核对」 |
| 5 | 无脚本 / 其它位置依赖旧 hint | **PASS** · mineflayer-tests 0 命中；非 docs 唯一旧文为 src 模板 `CoreRpg/src/main/resources/quest.yml:389`（模板本身，非依赖）；docs 6 个文件为历史引用 |
| 6 | HANDOFF §8 命令计数 0；报告自身 0；`ops.json` `[]` | **PASS** · 原样执行（`HANDOFF.md:140-141`）输出 `0`，变量非空 `true`（未打印值）；commit 前对本报告 `grep -cF` 为 `0`；`ops.json` 为 `[]` |
| 7 | reload / live：第二层誓火第 3 步玩家 `/corerpg quest`、与灰烛交谈、步骤切换各见新 hint | **SKIP · 待恢复服后实测**（本机无运行中的服；总控批注明确不挡 PASS） |

## 目标行原文

```
# 父版本 6817eb9^ L389（旧；= tip §1；src 模板 L389 至今同此）
        hint: 打开枢纽菜单 → 精英试炼（每周 1 次）
# live / 6817eb9 L389（新；= tip §3 荐案）
        hint: 打开枢纽菜单 → 精英试炼（每人每周限通关 1 次）

# live L385–L391 上下文（chapters 键 9「第二层誓火」steps[2]）
385:      - type: event
386:        event: elite_weekly_clear
387:        count: 1
388:        desc: 通关 余烬·精英试炼 一次
389:        hint: 打开枢纽菜单 → 精英试炼（每人每周限通关 1 次）
390:        xp: 500
391:        done: ['§6灰烛：§f词缀怪比普通周本难缠。你过了。']
# 同章 steps[0].done[0]（L377，未动，B2.117 另排）
        done: ['§6灰烛：§f试炼每周一次。稳定符在周首通里。']
```

## 命令与输出

```
$ date '+%Y-%m-%d %H:%M:%S %Z'
2026-10-01 13:04:34 CST（开工）
$ git status --porcelain | wc -l
0
$ git pull --ff-only
Already up to date.   rc=0
$ git log --oneline -8
6817eb9 feat(corerpg): B2.114 quest.yml L389 精英试炼 hint 改每人每周限通关 1 次
f6531db docs: approve B2.114 quest.yml L389 elite hint (A)
d7cdcc7 docs: B2.114 design tip — live quest.yml L389 hint 每人每周限通关 1 次（待批 A）
e63340f docs: close B2.112 dp elite timeout text (PASS)
4adadc4 docs: B2.112 dp elite timeout text test PASS
684ede2 feat(dp): B2.112 EmberEliteWeekly 超时失败提示改不算通关、本周可再来
ed3e16e docs: approve B2.112 dp elite timeout text (A)
e47a7ed docs: B2.112 design tip — EliteWeekly timeout L6 不记通关、本周可再来（待批 A）
$ git show --stat d7cdcc7 / f6531db → docs/design-ember-content-backlog.md, docs/design-ember-quest-elite-hint-copy.md（各自仅此两文件）

# 1
$ git show --numstat --format= 6817eb9
1	1	plugins/CoreRpg/quest.yml
$ git diff -U0 6817eb9^ 6817eb9 | grep '^@@'
@@ -389 +389 @@ chapters:
$ diff <(git show 6817eb9^:F) <(git show 6817eb9:F) | grep -E '^[0-9]'
389c389
$ diff <(git show 6817eb9^:F | sed '389d') <(git show 6817eb9:F | sed '389d')   → ALL_OTHER_LINES_IDENTICAL
tip ```yaml 块 2 个（各 1 行），程序提取：
old == §1 True · new == §3 True · 删除行 == old True · 新增行 == new True · live == new True · HEAD == new True
sha256 旧（§1 / old / 删除行 / src 模板 L389）：c9c02335a9d73ee56e5d51278c5a9891e5fb32215a95568ae694ca9bf786b3ca
sha256 新（§3 / new / live / 新增行）：744c0e47e48ee9912dbfd846cb4033bd63e3ff2ed7157ae5d0fe8aff9dd7a11d
old len 67 · new len 82 · indent 8/8 · tab 0 · CR 0 · 行尾空白 无
整文件（字节级）：18293 字节 · 442 行 · 末尾有换行 · 0x09 0 · 0x0D 0 · 以 0x20/0x09 结尾的行 0 · 以 Unicode 空白结尾的行 0 · 合法 UTF-8
$ cat -A F | sed -n 389p
        hint: M-fM-^IM-^SM-eM-<M-^@…M-gM-^BM-<M-oM-<M-^HM-fM-/M-^OM-dM-:M-:M-fM-/M-^OM-eM-^QM-(M-iM-^YM-^PM-iM-^@M-^ZM-eM-^EM-3 1 M-fM-,M-!M-oM-<M-^I$
cat -A 全文件：每行均以 `$` 收尾（不以 `$` 结尾的行 0）。
注：对 cat -A 输出直接 grep `^I` / `^M` / ` $` 会有假阳性——多字节 UTF-8 的元字符表示里含 `M-^I`、`M-^M`，以及 L311 末字「造」（e9 80 a0）末字节 0xA0 显示为 `M- `（看起来像行尾空格）。以字节级统计为准：tab / CR / 行尾空格均为 0。
$ git log --oneline d7cdcc7^..HEAD -- CoreRpg/src/main/resources/quest.yml   → 空（src 模板未动；末次 8605d10 2026-09-28 23:50 CST）
$ diff plugins/CoreRpg/quest.yml CoreRpg/src/main/resources/quest.yml | grep -E '^[0-9]'
389c389   （live 与 src 现仅此一行不同）
L377：施工前后逐字节相同（L377-unchanged）

# 2（/tmp 脚本身份）
$ ls -la /tmp/chk-b2114.js
-rw-r--r-- 1 box box 1095 Oct  1 13:03 /tmp/chk-b2114.js（mtime 13:03:18.65）
tip §4 ```sh 块：首行 `cat > /tmp/chk-b2114.js <<'JS'`，块内缩进行 0
heredoc 正文 sha256 = /tmp 文件 sha256 = b99d4b05bcfbe3b23fbf4537757bcdf73b824d922ec9d46b4681a1bcca854829（逐字节相等）
按稿执行 heredoc（仅首行落盘路径改为 /tmp/b2114-regen/chk.js）→ cmp /tmp/chk-b2114.js 一致（未覆盖原文件）

$ node /tmp/chk-b2114.js 6817eb9
only-L389 /chapters/9/steps/2/hint
exit=0
$ node /tmp/chk-b2114.js f6531db → []  exit=1（旧对旧）
$ node /tmp/chk-b2114.js d7cdcc7 → []  exit=1（旧对旧）
$ node /tmp/chk-b2114.js WT      → []  exit=1（新对新：HEAD=6817eb9 对工作区）
$ node /tmp/chk-b2114.js         → usage…  exit=9
# 变异（/tmp/b2114-mut、/tmp/b2114-mut2 一次性临时仓，逐提交构造；与主仓无关）
旧 → L389 改成「每周限 1 次」                → exit 4（新值 ≠ 荐案）
「每周限 1 次」→ 荐案                         → exit 3（旧值 ≠ 改前原文）
旧 → 荐案 + L390 xp 500→501                  → ['/chapters/9/steps/2/hint','/chapters/9/steps/2/xp']  exit 1
旧 → 荐案 + L377「试炼每周一次」也改          → ['/chapters/9/steps/0/done/0','/chapters/9/steps/2/hint']  exit 1

# 独立 deep diff（js-yaml；递归比较类型、数组长度、对象键序与叶子值）
changed: ["/chapters/9/steps/2/hint"]
新文件空对象 / 空数组：0 个
top keys: enabled,auto_start,title,npc,chapters
chapters：映射（非数组），键 ["1","2","3","4","5","6","7","8","9","10"]
chapters["9"]：keys name,intro,steps · name「第二层誓火」 · steps 4 项
  step 0 talk  「向 灰烛 打听天赋二层与精英试炼」（done[0] 即 L377）
  step 1 event talent 「花费至少 1 点天赋（含二层）」
  step 2 event elite_weekly_clear 「通关 余烬·精英试炼 一次」 ← 目标
  step 3 level 50 「达到余烬等级 Lv.50」
step2 全量：{"type":"event","event":"elite_weekly_clear","count":1,"desc":"通关 余烬·精英试炼 一次","hint":"打开枢纽菜单 → 精英试炼（每人每周限通关 1 次）","xp":500,"done":["§6灰烛：§f词缀怪比普通周本难缠。你过了。"]}

# 3
$ rg "每周 1 次" plugins/CoreRpg/quest.yml   → （无输出）rc=1
$ rg "B0.1 已清" plugins/CoreRpg/quest.yml   → （无输出）rc=1
$ rg "票已废" plugins/CoreRpg/quest.yml      → （无输出）rc=1
$ rg -n "已消耗体力|体力 -|扣票|精英票" plugins/CoreRpg/quest.yml   → rc=1
$ git show 6817eb9^:F | rg -n "每周 1 次"
389:        hint: 打开枢纽菜单 → 精英试炼（每周 1 次）

# 6（HANDOFF §8 原样；grep -c 只出计数）
$ set -a; source secrets/mysql-ember.env; set +a
$ (git diff; git ls-files -o --exclude-standard | xargs -r cat) | grep -c -- "$MYSQL_PASSWORD"
0
[ -n "$MYSQL_PASSWORD" ] → true（未打印值）
$ cat server-runtime/ops.json
[]
```

## 展示路径（第 4 点；Java 路径前缀 `CoreRpg/src/main/java/town/sunshine/corerpg/`）

| 环节 | 依据 | 是否可能截断 / 变形 |
|---|---|---|
| 读取文件 | `QuestService.java:122-124` `reload()`：`new File(getDataFolder(), "quest.yml")`，**仅在文件不存在时** `saveResource("quest.yml", false)`；`:125` 直接 `loadConfiguration(file)`，**无** `setDefaults` / `copyDefaults`；`reload()` 由 `CoreRpgPlugin.java:460-464` `reloadLocal()` 调用（启用时 `:184`、`/corerpg reload` 时 `:773-775`） | 玩家读到的是 live 文件；src 模板不参与合并 |
| 章节 / 步骤装载 | `QuestService.java:139-151`：`chapters` 段逐键 `Integer.parseInt(k)` 作章节号（`:144`），`getMapList("steps")` 逐项 `readStep`（`:148`） | 键 `9` → 第 9 章，步骤按数组顺序 |
| hint 装载 | `QuestService.java:168` `s.hint = color(str(m.get("hint"), ""))`；`str`（`:184`）= `String.valueOf`；`color`（`:189`）= `ChatColor.translateAlternateColorCodes('&', …)` | 不截取、不限长；新 hint 无 `&`/`§`，颜色转换无影响 |
| 展示 ① 步骤播报 | `QuestService.java:289-295` `announceStep`：`:292-293` `title + " 目标：" + desc + progressSuffix + "（" + stripColor(hint) + "）"`，`p.sendMessage` 整串 | 聊天栏整串；外层全角括号与 hint 内括号嵌套（原结构，tip §3 L40 已说明） |
| 展示 ② NPC 交谈 | `QuestService.java:458-471` `talk()`：未完成时 `:470` 发 objective，`:471` `"  " + stripColor(hint)` | 聊天栏整串 |
| 展示 ③ `/corerpg quest` | `QuestService.java:483` `cmd()`（路由 `CoreRpgPlugin.java:798` `quest/mainline/主线`）→ `:569` `"  提示：" + stripColor(hint)` | 聊天栏整串 |
| 不经过的通道 | actionBar `:294`、`:439` 与 `:1220-1222`（`ChatMessageType.ACTION_BAR`）只传 `objective(p)`；`objective()` `:201-209` 只拼 `desc + progressSuffix`；PAPI `%corerpg_quest%` / `quest_objective` → `CoreRpgExpansion.java:21-23` → `objective()`；全仓 `.hint` 仅 `QuestService.java:168,293,471,569` 四处 | hint 不进 actionBar / 侧栏 / PAPI |
| 长度限制排查 | `QuestService.java` 全文 `substring` 仅 `:452`（怪物 ID 通配）、`:1155`（命名空间），与 hint 无关；无 `sendTitle`、BossBar、Scoreboard 文本（`:845-905` 的 `getScoreboardTags` 是实体标签）；无 `length()` 截取 | 无截断。新 hint 26 个可见字符，L293 整行约 50 字符，聊天栏自动换行，不涉 1.12 客户端→服务端 256 字符上限（那是玩家发言方向） |

## 口径核对（第 4 点续）

| 新文案 | 依据 | 判定 |
|---|---|---|
| 「每人每周限**通关** 1 次」 | 门控 `EliteService.java:40-47` `passesGate`（等级 `:43-44` + `!isClearedThisWeek` `:45`），`isClearedThisWeek` `:49-53` 查 `elite_weekly_clear=<weekId>`；标记仅在 COMPLETE reward-script（`EmberEliteWeekly/option.yml:36` `@player` 逐人）→ `ProgressService.java:471-481` 写入（`:479`）；失败 / 超时不写（`EmberEliteWeekly/task/timeout.yml:7` `reward=false`，B2.112） | 限的是通关、按人计，一致 |
| 与 B2.101 / B2.102 同源 | `EmberEliteWeekly/option.yml:20`「每人每周限通关一次，没过可以再来」；`plugins/TrMenu/menus/ember_hub.yml:249`「§7每人每周限通关 1 次 · 词缀精英 · 周首通稳定符」（逐字同源「每人每周限通关 1 次」）、`:250`「消耗 §e%corerpg_stamina_cost_elite% §8体力（本周首次免费）」 | 一致；hint 不重复 L250 的费用信息 |
| 任务步与 hint 的配合 | 本步 `event: elite_weekly_clear`、`count: 1`（`quest.yml:386-387`）；`QuestService.java:358` 对 `elite_weekly_clear` 若玩家本周已有通关标记（`:363-366`）则直接视为完成（`quest.yml:6-7` 注释「already true at step start」） | 玩家本周已通关后再接到此步也会自动完成，「限通关 1 次」不会卡任务 |
| 体力 / 首免说法 | 新 hint 不含「体力」「首免」「免费」「扣」或数字费用（「1」为通关次数） | 无此类承诺可过度 |
| 边界 | OP 豁免门槛（`TicketEntryService.java:103`；DP `option.yml:17` `%player_is_op%`）可同周重复通关——管理通道，非玩家口径；结算瞬间离线的队员不计通关（`ProgressService.java:466`），仍可再通——与「限通关 1 次」不冲突 | 无过度承诺 |

## 依赖检索（第 5 点）

- **mineflayer-tests（排除 node_modules）：**`rg '精英试炼（|每周 1 次|打开枢纽菜单 → 精英|每人每周限通关'` → **0 命中**；读主线的 `quest-vol2-4.5-smoke.js` 只断言 done 文本 / 等级（如 `:85` `/词缀怪比普通周本难缠|你过了/`），不读 hint。
- **非 docs：**`rg '精英试炼（每周 1 次|打开枢纽菜单 → 精英试炼'` → live `plugins/CoreRpg/quest.yml:389`（新）、`:395`（另一步 hint「精英试炼 · 深渊 · 日常本」，无关）；src 模板 `CoreRpg/src/main/resources/quest.yml:389`（旧）、`:395`。无代码 / 菜单 / 脚本引用 hint 文本。
- **docs（历史引用，只列）：**`docs/design-stage4-quest-vol2.md:157`（原始设计「精英试炼（每周 1 次）」）、`docs/TEST-B2.102-hub-elite-lore.md`、`docs/design-ember-content-backlog.md`、本 tip、`docs/STATUS-ember-hub-elite-lore-copy-close.md`、`docs/STATUS-ember-dp-elite-timeout-text-copy-close.md`。
- **B2.109：**无新增依赖项。

## 前提更正 / 补充

1. **（标注，不挡 PASS）live 与 src 模板不一致：**`plugins/CoreRpg/quest.yml:389` 已为新文，`CoreRpg/src/main/resources/quest.yml:389` 仍为旧文「精英试炼（每周 1 次）」（两文件现仅此一行不同）。设计 §5 与总控批注**明确不改 src**（与 B2.107 同列挂起），故按标注处理。后果分析：
   - 正常运行 / `/corerpg reload` / 重启：**不会被覆盖**。`QuestService.java:124` 仅在 live 文件**不存在**时 `saveResource("quest.yml", false)`（`false` = 不覆盖已有文件），且 `reload()` 不做 `setDefaults` 合并（对比 `StaminaService` 读 cash.yml 时有 `setDefaults` / `copyDefaults(false)`，但那也不写回磁盘）。
   - 按 HANDOFF §1 重新部署：live 文件在 git 中受版本控制（`git ls-files` 有 `plugins/CoreRpg/quest.yml`），`server-runtime/plugins` 软链到 `../plugins`，克隆后即为新文，**不会**回落到模板。
   - **会出旧文的情形：**live `quest.yml` 被删除 / 未随部署拷贝（如只装 jar、不带仓库 `plugins/` 目录的新服），首次启用时从 jar 内资源生成——而 jar 由 `CoreRpg/src/main/resources/` 打包（HANDOFF §1.8 `mvn package`），生成的就是旧文「每周 1 次」，同时也会带回 L377 等未同步的内容。建议 B2.107 解挂时一并同步 src L389。
2. **路径表述：**简报「chapter 下标 9」——`chapters` 是 YAML 映射，`9` 是**章节号键**（`quest.yml:368`，`QuestService.java:144` `Integer.parseInt(k)`），不是数组下标；`steps` 才是数组，下标 2 = 第 3 步。tip §1 写 `chapters.9.steps[2]`、总控批注写 `/chapters/9/steps/2/hint`，均正确。
3. **tip §6 扫描表漏列两行：**按 tip 自己的扫描命令 `rg -n "每周 1 次|每周一次|体力|[0-9]+ ?体力"`，还会命中 L68「体力药拿去」、L140「留够体力」；两句都无数字、口径准确，归入「无」类即可，结论不变。
4. **tip §2 行号核对：**L122- `reload()`、L124 `saveResource`、L168、L184 `str`、L189 `color`、L293 / L471 / L569、`substring` 仅 L452 / L1155、actionBar L294 / L439、`objective()` L202-209（方法签名 L202，注释 L201）、`CoreRpgExpansion` L21 —— 均与现文件一致。
5. **时间戳：**设计 `d7cdcc7`（03:43:20）与批准 `f6531db`（13:02:37）相隔约 9 小时 19 分；tip STATUS 与总控批注均写「13:02」批 A，与批准 commit 一致；施工 `6817eb9` 13:03:18 在批准之后 41 秒，顺序正确。tip L6「总控 03:31 排为 B2.114」早于设计 commit，合理。

## 范围外备注（不在本窗）

- **B2.117**：`quest.yml:377`（`chapters.9.steps[0].done[0]`）「试炼每周一次。稳定符在周首通里。」——总控已另排，本窗未动（已核逐字节不变）。
- `quest.yml:134` 周本氛围句「每周一次…」、`:73` / `:243`「约 3 次日常」：总控只记不排。
- src 模板 L389（见前提更正 1）随 B2.107 挂起。

## 旁证

1. **施工范围：**`6817eb9` 只改 live `quest.yml` L389 一行 `hint` 值；解析后仅 `/chapters/9/steps/2/hint` 变化；desc、xp、done、event、count 与其它章节全同。
2. **精确行文：**live、施工 tip、新增行与 tip §3 sha256 相同；旧行与 tip §1、src 模板 L389 相同。
3. **口径：**未写费用与数字；未写「票已废」「B0.1 已清」；NI 票物、数值未动。
4. **范围外未动：**`d7cdcc7^..HEAD` 期间触及 `plugins/`、`CoreRpg/`、`mineflayer-tests/` 的只有 `6817eb9`（仅本文件）；本文件上一次改动为 `8605d10`（2026-09-28 23:50 CST）。
5. **做法纪律：**仅静态 git / sed / diff / cmp / node js-yaml / python / rg；未起服、未 reload、未长测、未结束任何进程；主仓未改任何文件（仅新增本报告）；未覆盖 `/tmp/chk-b2114.js`（另生成的 `/tmp/b2114-regen/`、`/tmp/b2114-mut*/` 为一次性临时文件，不入库）；机密未回显。

## 阻塞点

无（第 7 项实服观察待恢复服后实测）。

## 回报摘要

- 总评：PASS
- 各点：0 PASS / 1 PASS（附 live/src 不一致标注）/ 2 PASS / 3 PASS / 4 PASS / 5 PASS / 6 PASS / 7 SKIP（待恢复服后实测）
- 前提更正：src 模板 L389 仍为旧文（不覆盖 live，仅 live 文件缺失时由 jar 生成旧文）；`/chapters/9` 是章节号键非数组下标；tip §6 扫描表漏列 L68 / L140（无害）
- 施工 tip SHA：`6817eb9`
- 测报 tip short SHA：待本地 commit 后回报
- 是否已 push：否
- 报告路径：`docs/TEST-B2.114-quest-elite-hint.md`
- 阻塞点：无
- ahead：相对 `origin/main` ahead 1（未 push）
- `ops.json`：`[]`
