# B2.100 · DP `EmberAbyss/option.yml` L22 开本提示去「已消耗体力 ×1 —— 」· 纯静态薄验收

- **总评：PASS**（reload / 实服观察项记 SKIP：待恢复服后实测，不挡 PASS）
- **测报时间：**2026-10-01 03:05 Asia/Shanghai
- **岗别：**余烬-测试岗（B2.100 纯静态薄验收）
- **工作区：**`/workspace/minecraft`（main，与 `origin/main` 同步；三个 commit 本地均存在且为 HEAD 祖先，无需 fetch）
- **施工 tip SHA：**`252fe8e`（2026-10-01 03:01:24 CST）
- **设计 / 批准：**`0ef491e`（03:00:15 CST）/ `2033207`（03:00:52 CST）
- **tip：**`docs/design/design-ember-dp-abyss-start-text-copy.md`（§1 完整旧行、§2「12」核对、§3 荐案、§4 验收、§5 明确不做、§6 后续排序、文末总控批注）
- **改动性质：**玩家可见 `text=`（非注释）
- **是否已 push：否**（本岗仅本地 commit，禁止 push）
- **报告路径：**`docs/tests/TEST-B2.100-dp-abyss-start-text.md`
- **ahead：**相对 `origin/main` ahead 1（本测报本地 commit 后，未 push）
- **`server-runtime/ops.json`：**`[]`（未改动）
- **本机服状态：**25565/25566/25567/3306 均未监听（`ss -ltn` 计数 0），按总控批注不起服

## 各点

| # | 条件 | 结果 |
|---|------|------|
| 1 | 施工 commit 只改 `plugins/DungeonPlus/dungeon/EmberAbyss/option.yml`，numstat `1 1`，hunk `@@ -22 +22 @@`；新行 = 旧行恰删「已消耗体力 ×1 —— 」；新行 = 荐案；旧行 = §1 完整旧行；4 空格、无 tab/CR/行尾空白 | **PASS** · numstat `1	1`；hunk `@@ -22 +22 @@ dungeon-start:`，`diff` 仅 `22c22`；删除子串在旧行中恰出现 1 次（字节偏移 55，紧接 `§7`），27 字节；`new == old.replace(sub,"",1)` 且 `new == old[:55]+old[82:]`，长度 160→133（差 27），其余字节不变；新行与 tip §3 荐案块、live、HEAD、施工新增行 `cmp` 一致（sha256 `892ce9c0…aec513d`）；旧行与 tip §1 完整旧行块、施工删除行一致（sha256 `ccd83754…c36c4ec`）；新旧行缩进均 4 空格，tab 0、CR 0、行尾空白 0；全文件 CR 0、tab 0、行尾空白 0 |
| 2 | js-yaml 深比对：只 `dungeon-start.action-script[0]` 变化，长度仍 5，顺序与其它项不变；HEAD 该文件 = `252fe8e` | **PASS** · tip §4 原样命令（HEAD=`252fe8e`，HEAD~1=`2033207`，其该文件与 `252fe8e^` 字节一致）输出 `only-L22`，exit 0；独立递归深比对变化路径仅 `[".dungeon-start.action-script[0]"]`；长度 5→5；下标 1～4 逐项相同；顶层键与 `dungeon-start` 子键顺序相同；除 L22 外全文逐行相同；HEAD、工作区与 `252fe8e` 字节一致，其后无 commit 触及该文件 |
| 3 | 扣费字样 `rg` 0 命中；全文件「B0.1 已清」「票已废」0；设计附加项 | **PASS** · 原样 `rg "已消耗体力|体力 ×|体力 -|扣票|深渊票" …/EmberAbyss/option.yml` 无输出（rc=1）；施工前（`252fe8e^`）同命令仅命中 L22（tip §4 L47 所述属实）；「B0.1 已清」0、「票已废」0、「已消耗」0、「消耗」0、「×」0、「——」0；设计收窄口径已去掉 `×1`，本次无范围外误中（B2.99 同类问题已修正） |
| 4 | 「12」为现行下潜上限；删段后句子通顺 | **PASS** · 见下「12 层上限核对」 |
| 5 | 入口与私聊 costHint；深渊只可能「管理免扣 / 体力 -30」；菜单点击 tell；mineflayer 依赖 | **PASS（附菜单 L77 硬编码备注）** · 见下「入口与私聊」 |
| 6 | HANDOFF §8 查密码命令输出 0；`ops.json` 为 `[]` | **PASS** · 原样执行（`docs/handoff/HANDOFF.md:140-141`，`grep -c` 只出计数）输出 `0`，变量非空 `true`（未打印值）；`ops.json` 为 `[]` |
| 7 | reload 后实测：本内见新 L22 + L23 撤离句；发起者收「[深渊] 正在进入……（体力 -30）」 | **SKIP · 待恢复服后实测**（本机无运行中的服；总控批注明确不挡 PASS） |

## 目标行原文

```
# 父版本 252fe8e^ L22（旧；= tip §1 完整旧行）
    - "$message{type=text;text=§5深渊已开启。§7已消耗体力 ×1 —— 能走多深，就走多深。本期可下潜至第 §f12 §7层。} @dungeon"
# live / 252fe8e L22（新；= tip §3 荐案）
    - "$message{type=text;text=§5深渊已开启。§7能走多深，就走多深。本期可下潜至第 §f12 §7层。} @dungeon"
# 新行玩家可见文本（去 § 色码）
深渊已开启。能走多深，就走多深。本期可下潜至第 12 层。

# live L21–L26 上下文
21:  action-script:
22:    - "$message{type=text;text=§5深渊已开启。§7能走多深，就走多深。本期可下潜至第 §f12 §7层。} @dungeon"
23:    - "$message{type=text;text=§8需要撤离：打开菜单 → 深渊 → 撤离 · 按最高层结算 · 体力不退} @dungeon"
24:    - "$teleport{location=-40,90,274;defspawn=true} @player"
25:    - "$command{text=corerpg abyss progress %player_name% 0;console=true} @player"
26:    - "$monstergroup{group=floor1;repeat=false;delay=2} @dungeon"
```

## 命令与输出

```
$ git log --oneline -6
252fe8e feat(dp): B2.100 EmberAbyss 开本提示去扣费字样
2033207 docs: approve B2.100 dp abyss start text (A)
0ef491e docs: B2.100 design tip — DP EmberAbyss L22 开本提示去扣费句（待批 A）；测岗新候选排为 B2.110
9c3f6b8 docs: close B2.99 dp raid start text (PASS)
16d74cb docs: B2.99 dp raid start text test PASS
b90e999 feat(dp): B2.99 EmberRaid 开本提示去扣费字样
$ git merge-base --is-ancestor <0ef491e|2033207|252fe8e> HEAD   → 均为祖先
$ git show --stat 0ef491e / 2033207   → 均只含本 tip 与 docs/design/design-ember-content-backlog.md

# 1
$ git show --numstat --format= 252fe8e
1	1	plugins/DungeonPlus/dungeon/EmberAbyss/option.yml
$ git diff -U0 252fe8e^ 252fe8e | grep '^@@'
@@ -22 +22 @@ dungeon-start:
$ diff <(git show 252fe8e^:F) <(git show 252fe8e:F) | grep -E '^[0-9]'
22c22

# 删除子串逐字节（python，按旧行实际字节核）
sub = 「已消耗体力 ×1 —— 」
bytes: e5 b7 b2 e6 b6 88 e8 80 97 e4 bd 93 e5 8a 9b 20 c3 97 31 20 e2 80 94 e2 80 94 20   (27 bytes)
       已 消 耗 体 力 ␠ ×(U+00D7) 1 ␠ —(U+2014) —(U+2014) ␠   （三处空格均为 ASCII 0x20）
old.count(sub) = 1；位置：byte 55（前为 `§7`，后为「能走」）
new == old.replace(sub,"",1)  → True
new == old[:55] + old[82:]    → True
len old/new/diff = 160 / 133 / 27

# tip 两个 ```yaml 块程序提取（各 1 行）
old == tip §1 块 → True；new == tip §3 块 → True
施工删除行(-) == old → True；施工新增行(+) == new → True；live == new → True；HEAD == new → True
sha256 new（荐案 / new / live / 新增行 四者相同）：892ce9c055d04d441e22b090a0a02b20df47c2db3ef21123def360b61aec513d
sha256 old：ccd83754922ff30a95d94be962979b09c95cffaaadbfd795e5860c589c36c4ec
old: indent 4 · tab 0 · CR 0 · 行尾空白 无
new: indent 4 · tab 0 · CR 0 · 行尾空白 无
全文件：CR 0 · 行尾空白行 0 · tab 0
cat -A（新）：行首 `    - "$message{type=text;text=M-BM-'5…`（§ 为 C2 A7），行尾 `} @dungeon"$`
cat -A（旧）：同上，中段可见 `M-CM-^W1 M-bM-^@M-^TM-bM-^@M-^T `（× = C3 97，— = E2 80 94）

# 2（tip §4 原样，仓库根目录，HEAD=252fe8e）
$ node -e '…（tip §4 L44 原文，略）…'
only-L22
exit=0
HEAD~1 = 2033207；cmp HEAD~1:F 252fe8e^:F → 一致

# 独立深比对（递归 walk；js-yaml）
changed paths: [".dungeon-start.action-script[0]"]
len 5 -> 5
idx 0 CHANGED / idx 1 same / idx 2 same / idx 3 same / idx 4 same
top keys same order: true（dungeon-init-script,dungeon-start,dungeon-area,dungeon-reward-script）
dungeon-start keys same: true
$ diff <(git show 252fe8e^:F | sed '22d') <(git show 252fe8e:F | sed '22d')   → ALL_OTHER_LINES_IDENTICAL
HEAD==252fe8e==worktree（cmp）；git log 252fe8e..HEAD -- F → 空

# 3
$ rg "已消耗体力|体力 ×|体力 -|扣票|深渊票" plugins/DungeonPlus/dungeon/EmberAbyss/option.yml
（无输出，rc=1）
$ git show 252fe8e^:F | rg -n "已消耗体力|体力 ×|体力 -|扣票|深渊票"
22:    - "$message{type=text;text=§5深渊已开启。§7已消耗体力 ×1 —— 能走多深，…} @dungeon"
全文件 / 新 L22：B0.1 已清 0/0 · 票已废 0/0 · 已消耗 0/0 · 消耗 0/0 · × 0/0 · —— 0/0
（「体力」6、「扣」3、「票」2、「首免」1、「30」1 全部在 L3/L4/L18/L20 维护注释与 L23/L35「体力不退」，新 L22 均 0）

# 6（HANDOFF §8 原样；grep -c 只出计数）
$ set -a; source secrets/mysql-ember.env; set +a
$ (git diff; git ls-files -o --exclude-standard | xargs -r cat) | grep -c -- "$MYSQL_PASSWORD"
0
[ -n "$MYSQL_PASSWORD" ] → true（未打印值）
$ cat server-runtime/ops.json
[]
```

## 「12」层上限核对（第 4 点）

| 依据 | 位置 | 结论 |
|---|---|---|
| DP 层组 | `plugins/DungeonPlus/dungeon/EmberAbyss/monster.yml` 层组依次 `floor1`(:11) … `floor11`(:192)、`floor12`(:208)，共 12 组，floor12 为最后一组（文件共 223 行） | 上限 12 |
| 顶层结束 | `monster.yml:219` `corerpg abyss progress %player_name% 12` → `:220`「第 12 层通过 · 本期顶层通关结算」→ `:222` `$end{…顶层通关！…;reward=true;…;end-type=COMPLETE}`；floor12 end 段无 `$monstergroup` 续层（floor1～11 的 end 段均续下一组，如 `:205`） | 12 即 COMPLETE |
| 唯一 COMPLETE | `rg end-type plugins/DungeonPlus/dungeon/EmberAbyss/` 仅 `monster.yml:222`（COMPLETE）与 `task/timeout.yml:10`（FAILURE） | 无其它终点 |
| option 注释 | `option.yml:7`「COMPLETE 顶层 = floor12（非 floor8）」；`:30`「COMPLETE（本期顶层 floor12）触发…」 | 一致 |
| 结算代码 | `AbyssSettleService.java:33` `WEEKLY12_MIN_FLOOR = 12`；`:350` `floor < WEEKLY12_MIN_FLOOR` 不发；`:359`「本周首次抵达第 12 层，获得稳定符 ×1」 | 一致 |
| 地图 / 井道 | `AbyssShaftService.java:42-47` `FLOOR_Y` 注释「floor index 1..12」，数组 0 + 12 层；`:73` `floor > 12` 回井顶；`:95`、`:247` 建层循环止于 12 | 地图只有 12 层平台 |
| 档位表 | `plugins/CoreRpg/abyss.yml:7` `{ min: 10, max: 999, … }` 开放区间 | 不构成上限（tip §2 所述属实） |
| 菜单 | `plugins/TrMenu/menus/ember_abyss.yml:68`「本期可下潜至第 §f12 §7层」、`:69`「共 12 层」、`:113`「本期顶12」、`:116` | 口径一致 |
| >12 痕迹 | 全仓（除 docs、node_modules）`rg 'floor1[3-9]|floor2[0-9]|第 1[3-9] 层'` 0 命中 | 无 >12 层 |

**语句通顺：**新行可见文本「深渊已开启。能走多深，就走多深。本期可下潜至第 12 层。」为三个完整句，均以句号收尾；原破折号「——」连接的是扣费短语，随之整体删除，无悬空标点、无双空格（`§7` 后直接接「能走多深」）。颜色层次 `§5` 标题 / `§7` 正文 / `§f` 层数不变。「12」为层数、非费用，保留正确。

## 入口与私聊（第 5 点）

- **链路：**菜单 `plugins/TrMenu/menus/ember_abyss.yml:79` `command: corerpg enter abyss` → `CoreRpgPlugin.java:842-843` → `TicketEntryService.java:235-241` `Kind.parse("abyss")` → `tryEnter(player, Kind.ABYSS)`（`:100`）。`Kind.ABYSS` 定义 `TicketEntryService.java:28`，shortLabel「深渊」；私聊 `:148-149` → 「[深渊] 正在进入……（costHint）」。
- **深渊 costHint 只可能两种：**
  - `:139-140` OP / `corerpg.admin` →「管理免扣」；
  - `StaminaService.consumeForEnter`（`StaminaService.java:293-327`）的周免抵扣只对 `WEEKLY`（:301）、`ELITE`（:306）、`RAID`（:311）三种，**ABYSS 永不 `usedCredit`**，故 `:142`「本周首次免费」不会出现；
  - 非 OP：`cost = costOf(abyss)` = `plugins/CoreRpg/cash.yml:27` `abyss: 30`（代码缺省 `StaminaService.java:85` 同 30）→ `:144`「体力 -30」；
  - `:146`「无消耗」仅当 `StaminaService.java:316-317` `cost <= 0`，即 cash.yml 将 `costs.abyss` 改为 ≤0 时才会出现，现行配置不会触发；
  - 体力不足时 `:319-322` 直接返回失败提示，不走 costHint。
- 所以 tip §1（设计 L18）「深渊实际能出现的是 L140 管理免扣和 L144 体力 -N，L142 不适用，L146 只在 cost ≤ 0 时兜底」完全正确。
- **菜单点击 tell（备注，范围外）：**`ember_abyss.yml:77` `tell: §5[深渊] §7尝试下潜……（需余烬 Lv.25 · 体力 30；等级不足不扣）`，在 `corerpg enter` 之前无条件发出。
  - 措辞为「尝试下潜」「需……体力 30」，属进本前的门槛说明，不说「已扣」，与私聊「体力 -30」不矛盾；「需余烬 Lv.25」对应 `plugins/CoreRpg/progress.yml:143` `abyss: 25`；「等级不足不扣」对应 `TicketEntryService.java:109-113`，等级校验先于扣体力（扣体力在 `:130`），DP 逐队员门槛被拒时退还（`option.yml:18`）。
  - 但「30」是硬编码：OP 免扣时 tell 写「体力 30」、私聊却是「管理免扣」；若日后改 `cash.yml costs.abyss`，此处不会随动（`:63` lore 已用 `%corerpg_stamina_cost_abyss%` 占位符，是正确写法）。冲突程度远轻于 `ember_raid.yml:76-77`（那边写「消耗 50」且含「已点燃」），建议与 B2.110 同窗或其后评估，本窗不改。
- **新 L22 与私聊分工：**L22（`@dungeon` 全队）只讲开本与层数；扣费只由私聊（仅发起者）告知，1～2 人队伍中非发起者不再误以为自己被扣。

**mineflayer 依赖（全 `mineflayer-tests/`，排除 node_modules）：**

- 旧 L22 的「已消耗体力 ×1 —— 」段：**无脚本依赖**。「已消耗体力」仅 `stamina-s0-smoke.js:64-65`（日常本判定，与深渊无关）。
- `abyss-calamity-supplement.js:66` `/灰烬潮|深渊已开启|第 1 层/`：新 L22 仍含「深渊已开启」，**不受影响**。
- 「能走多深」「可下潜至」无脚本引用。

**B2.109 清单（深渊相关旧匹配 / 票时代步骤，本窗不改）：**

1. `mineflayer-tests/dungeon-balance.js:16-17`：abyss 配置仍带 `ticket: 'ticket_ember_abyss'`；`boss: /第 8 层 ——/`、`waves: [1..8]`，按 floor8 为顶层写，与现行 floor12 COMPLETE（`monster.yml:208-222`，stage4.1）不符。
2. `mineflayer-tests/abyss-reload-smoke.js:17-18`：`/ni give RpgBot ticket_ember_abyss 3` 后 `/dp start EmberAbyss`，票时代步骤。
3. `mineflayer-tests/abyss-calamity-supplement.js:58,62`：发深渊票 + `/dp start EmberAbyss`，票时代步骤（`:66` 判定本身仍有效）。
4. `mineflayer-tests/gates-smoke.js:22-23`：`check_abyss_refused` 以 `ticket_ember_abyss` 数量 >0 判「未扣票」，票时代断言（与既有 `gates-smoke.js:26-30` 同条）。
5. `mineflayer-tests/ticket-grant-smoke.js:6,24,72,144-145`：整支按「深渊票每日发放」写，票时代脚本。
6. 既有（B2.99 列）：`raid-combat-smoke.js:153-155,170`、`killany-live-retest.js:122`、`abyss-followup.js:58`、`abyss-calamity-supplement.js:111`、`dungeon-balance.js:21`、`stamina-s0-smoke.js:120`。

## 前提更正 / 补充

1. **设计 §4 js-yaml 命令只证「HEAD 等于期望」，不证「HEAD~1 确为旧行」：**对照实验以 `252fe8e` 对 `252fe8e`（新对新）运行同一命令仍输出 `only-L22`（exit 0），因为它把 a 的 `[0]` 覆盖成荐案后比较，没有断言 a 原值为旧行。本次另以独立深比对（变化路径恰为 `[0]`）与字节级「旧行 = §1 块」补足，结论不受影响。另：命令用 `HEAD~1`，本次 HEAD~1 为批准 commit `2033207`（该文件与 `252fe8e^` 字节一致），故结果有效；本测报 commit 后再跑将变成新对新（仍会输出 `only-L22`），复测应改用 `252fe8e^` / `252fe8e`。（B2.99 tip 的同款命令有同样局限。）
2. **设计 §6（`docs/design/design-ember-dp-abyss-start-text-copy.md:67`）称「参照 `ember_abyss.yml` L77『尝试下潜……』的写法，改成不写扣费的『尝试进入』提示」：**L77 本身写有硬编码「体力 30」（`plugins/TrMenu/menus/ember_abyss.yml:77`），并非「不写扣费」的范本；B2.110 出稿时应只借鉴「尝试…」的语气，费用改用占位符或交私聊。
3. 其余前提核对无误：tip §1 旧行、`dungeon-start.action-script[0]`、列表 5 项；`cash.yml` `stamina.costs.abyss` 30（`plugins/CoreRpg/cash.yml:27`）；人数 1～2（`option.yml:17`）；`ember_abyss.yml` L79；`TicketEntryService` L140/L142/L144/L146/L148-149；`monster.yml` L208 floor12；`option.yml` L7/L30；`AbyssSettleService.java` L33；`abyss.yml` 最高档 `min: 10, max: 999`（`plugins/CoreRpg/abyss.yml:7`）；tip §4 L47「施工前仅命中 L22 一处」属实。

## 范围外备注（不在本窗）

- `ember_abyss.yml:77` 点击 tell 硬编码「体力 30」，OP 免扣时与私聊不一致（见上）；建议并入 B2.110 或紧随其后。
- `option.yml:23`、`:35` 玩家可见「体力不退」为撤离 / 结算规则说明，非扣费提示，保持。
- `option.yml:3-4`、`:18`、`:20` 维护注释含「体力 / 扣 / 票」，B2.95 已验，保持。
- 七个日常本「已消耗体力」只记不排（tip §5）。

## 旁证

1. **施工范围：**`252fe8e` 只改 `EmberAbyss/option.yml` L22 一行 `text=`，且只删 27 字节子串；解析后仅 `dungeon-start.action-script[0]` 变化，列表长度与顺序不变，其余键、注释、脚本逐行相同。
2. **精确行文：**live、施工 tip、新增行与 tip §3 荐案四者 sha256 相同；旧行与 tip §1 完整旧行一致。
3. **口径：**删去「已消耗体力 ×1 —— 」，未新增数字与扣费；未写「票已废」「B0.1 已清」；NI 票物、数值、`monster.yml` 未动。
4. **范围外未动：**`0ef491e^..HEAD` 期间触及 `plugins/CoreRpg`、`CoreRpg/`、`plugins/TrMenu`、`plugins/DungeonPlus`、`mineflayer-tests` 的只有 `252fe8e`（仅本文件）。
5. **做法纪律：**仅静态 git / sed / diff / cmp / node js-yaml / python / rg；未起服、未 `/dp reload`、未长测、未杀进程；未改任何文件（仅新增本报告）；机密未回显。
6. **时间戳：**tip STATUS 与总控批注均写「2026-10-01 03:00」批 A；设计 commit `0ef491e` 03:00:15、批准 commit `2033207` 03:00:52 CST，均落在 03:00 这一分钟内，一致；施工 `252fe8e` 03:01:24 在批准之后，顺序设计 → 批准 → 施工正确。

## 阻塞点

无（第 7 项实服观察待恢复服后实测）。

## 回报摘要

- 总评：PASS
- 各点：1 PASS / 2 PASS / 3 PASS / 4 PASS / 5 PASS（附菜单 L77 硬编码备注）/ 6 PASS / 7 SKIP（待恢复服后实测）
- 前提更正：tip §4 js-yaml 命令新对新也会输出 `only-L22`（需配合深比对）；tip §6 称 `ember_abyss.yml:77` 不写扣费，实则硬编码「体力 30」
- 施工 tip SHA：`252fe8e`
- 测报 tip short SHA：待本地 commit 后回报
- 是否已 push：否
- 报告路径：`docs/tests/TEST-B2.100-dp-abyss-start-text.md`
- 阻塞点：无
- ahead：相对 `origin/main` ahead 1（未 push）
- `ops.json`：`[]`
