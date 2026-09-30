# B2.102 · TrMenu `ember_hub.yml` L249-L250 精英试炼 lore：限通关口径 + 体力改占位符 · 纯静态薄验收

- **总评：PASS**（reload / 实服观察项记 SKIP：待恢复服后实测，不挡 PASS）
- **测报时间：**2026-10-01 03:27 Asia/Shanghai
- **岗别：**余烬-测试岗（B2.102 纯静态薄验收）
- **工作区：**`/workspace/minecraft`（main，与 `origin/main` 同步；三个 commit 本地均存在且为 HEAD 祖先，无需 fetch）
- **施工 tip SHA：**`862d21c`（2026-10-01 03:23:28 CST）
- **设计 / 批准：**`4045245`（03:21:52 CST）/ `6c2913a`（03:22:50 CST）
- **tip：**`docs/design-ember-hub-elite-lore-copy.md`（§1 完整旧行、§2 核对、§3 荐案、§4 验收与脚本、§5 明确不做、§6 顺带发现、文末总控批注）
- **改动性质：**玩家可见菜单 lore（精英试炼图标 `Icons.'2'.display.lore[1]`、`[2]`）
- **是否已 push：否**（本岗仅本地 commit，禁止 push）
- **报告路径：**`docs/TEST-B2.102-hub-elite-lore.md`
- **ahead：**相对 `origin/main` ahead 1（本测报本地 commit 后，未 push）
- **`server-runtime/ops.json`：**`[]`（未改动）
- **本机服状态：**25565/25566/25567/3306 均未监听（`ss -ltn` 计数 0），按总控批注不起服

## 各点

| # | 条件 | 结果 |
|---|------|------|
| 1 | 施工 commit 只改 `plugins/TrMenu/menus/ember_hub.yml`，numstat `2 2`，hunk `@@ -249,2 +249,2 @@`，仅 L249、L250；旧行 = §1；新行 = §3；8 空格、无 tab/CR/行尾空白 | **PASS** · numstat `2	2`；hunk `@@ -249,2 +249,2 @@ Icons:`，`diff` 仅 `249,250c249,250`；除 L249-250 外逐行相同；两行旧值与 tip §1 块、施工删除行逐字节一致，两行新值与 tip §3 块、live、HEAD、施工新增行逐字节一致（sha256 见下）；四行缩进均 8 空格，tab 0、CR 0、行尾空白 0（全文件同）；`cat -A` 行尾均 `'$` |
| 2 | §4 脚本：`/tmp/chk-b2102.js` 是否即设计脚本；运行得 `only-L249-L250`；能识别未改；独立深比对仅 `/Icons/2/display/lore/1`、`/2`；HEAD = `862d21c` | **PASS** · `/tmp/chk-b2102.js` 已存在（1047 字节，mtime 03:23:28，box 用户），与 tip §4 ```js 块去掉 markdown 统一 4 空格缩进后**逐字节相同**（sha256 `74343bd5…d36bef2`），未重写；`node /tmp/chk-b2102.js 862d21c` → `only-L249-L250 /Icons/2/display/lore/1 /Icons/2/display/lore/2`（exit 0）；旧对旧 `6c2913a`、`4045245` → 打印 `[]`、exit 1；新对新 `WT`（HEAD=`862d21c` 对工作区）→ `[]`、exit 1；本测报 commit 后 `B=HEAD`（HEAD^=`862d21c`）→ 见命令区；独立递归深比对（含键序）变化路径恰为 `/Icons/2/display/lore/1`、`/Icons/2/display/lore/2`；图标键确为字符串 `'2'`（`ember_hub.yml:224`），lore 5 项（下标 0 空行、1、2、3 空行、4「➥ 点击进入」），长度 5→5；HEAD、工作区与 `862d21c` 字节一致 |
| 3 | 两条原样 `rg` 0 命中；`stamina_cost_elite` 恰为 L235/L240/L250；「B0.1 已清」「票已废」0 | **PASS** · `rg "每周 1 次|消耗 §e40" …` 无输出（rc=1；施工前命中 L249、L250）；`rg "已消耗体力|体力 -|扣票|精英票" …` 无输出（rc=1；施工前亦 0）；`rg -n stamina_cost_elite …` 恰为 `235`、`240`、`250`；全文件「B0.1 已清」0、「票已废」0、「§e40」0、「只有一次」0；tip §4 L72 预期属实 |
| 4 | 占位符解析链、同图标已用、update/refresh、字符串一致；「每人每周限通关 1 次」口径 | **PASS** · 见下「占位符解析链」与「口径核对」 |
| 5 | 全仓对两行旧文的依赖 | **PASS（附范围外 1 条玩家可见同类旧文）** · mineflayer-tests 0 依赖；非 docs 仅 `plugins/CoreRpg/quest.yml:389`（及 src 模板同行）同口径「精英试炼（每周 1 次）」，见下 |
| 6 | HANDOFF §8 查密码命令 0；`ops.json` `[]` | **PASS** · 原样执行（`HANDOFF.md:140-141`，`grep -c` 只出计数）输出 `0`，变量非空 `true`（未打印值）；`ops.json` 为 `[]` |
| 7 | reload 后实测：`trm reload` 后枢纽精英 lore 显示「每人每周限通关 1 次 …」「消耗 40 体力（本周首次免费）」，数字由占位符解析；（可选）改 cash.yml 后 lore 跟随 | **SKIP · 待恢复服后实测**（本机无运行中的服；总控批注明确不挡 PASS） |

## 目标行原文

```
# 父版本 862d21c^ L249-L250（旧；= tip §1）
        - '§7每周 1 次 · 词缀精英 · 周首通稳定符'
        - '§8需余烬 Lv.40 · 消耗 §e40 §8体力（本周首次免费）'
# live / 862d21c L249-L250（新；= tip §3 荐案）
        - '§7每人每周限通关 1 次 · 词缀精英 · 周首通稳定符'
        - '§8需余烬 Lv.40 · 消耗 §e%corerpg_stamina_cost_elite% §8体力（本周首次免费）'
# 新行玩家可见文本（去 § 色码；占位符按现行 cash.yml 解析为 40）
每人每周限通关 1 次 · 词缀精英 · 周首通稳定符
需余烬 Lv.40 · 消耗 40 体力（本周首次免费）

# live L224 / L241–L257 上下文（精英试炼图标 '2'）
224:  '2':
242:    update: 20
243:    refresh: 20
244:    display:
245:      material: golden sword
246:      name: '§e精英试炼'
247:      lore:
248:        - ''
249:        - '§7每人每周限通关 1 次 · 词缀精英 · 周首通稳定符'
250:        - '§8需余烬 Lv.40 · 消耗 §e%corerpg_stamina_cost_elite% §8体力（本周首次免费）'
251:        - ''
252:        - '§e➥ 点击进入'
253:    actions:
254:      all:
255:        - 'sound: BLOCK_NOTE_PLING-1-2'
256:        - 'command: corerpg elite start'
257:        - close
```

## 命令与输出

```
$ git log --oneline -6
862d21c feat(trmenu): B2.102 hub 精英 lore 改限通关口径并用体力占位符
6c2913a docs: approve B2.102 hub elite lore (A)
4045245 docs: B2.102 design tip — ember_hub.yml L249 限通关口径 + L250 体力改占位符（待批 A）
1a4d298 docs: close B2.101 dp elite start text (PASS)
713b407 docs: B2.101 dp elite start text test PASS
e55540a feat(dp): B2.101 EmberEliteWeekly 开本提示改每人每周限通关一次
$ git merge-base --is-ancestor <4045245|6c2913a|862d21c> HEAD   → 均为祖先
$ git show --stat 4045245 / 6c2913a   → 均只含本 tip 与 docs/design-ember-content-backlog.md

# 1
$ git show --numstat --format= 862d21c
2	2	plugins/TrMenu/menus/ember_hub.yml
$ git diff -U0 862d21c^ 862d21c | grep '^@@'
@@ -249,2 +249,2 @@ Icons:
$ diff <(git show 862d21c^:F) <(git show 862d21c:F) | grep -E '^[0-9]'
249,250c249,250
$ diff <(git show 862d21c^:F | sed '249,250d') <(git show 862d21c:F | sed '249,250d')   → ALL_OTHER_LINES_IDENTICAL

# tip 两个 ```yaml 块程序提取（各 2 行）
old == tip §1 块 True；new == tip §3 块 True；删除行(-) == old True；新增行(+) == new True；live == new True；HEAD == new True
L249 旧 sha256 5a26a9f46f4369f434ed3f2c94ab7df594435d2f967070715fdb96fcee2b090c（= §1 第 1 行）
L249 新 sha256 dd350a135a6ae17a8396ee34b4ee6d17017dcf0187244ae5c2d15289191b7d7c（= §3 第 1 行）
L250 旧 sha256 3656d9782bd03de6cbc3319f48f03f64a99cbb71fb2a0b2ea96656009417c0e7（= §1 第 2 行）
L250 新 sha256 b7c5184541ca9857ba855713691183963fd549fb59a8167b5d1c269229fd13d4（= §3 第 2 行）
L249 old len 65 / new len 80 · L250 old len 80 / new len 106 · 四行 indent 8 · tab 0 · CR 0 · 行尾空白 无
全文件：CR 0 · 行尾空白行 0 · tab 0
cat -A：L249 `        - 'M-BM-'7M-fM-/M-^OM-dM-:M-:…M-gM-,M-&'$`；L250 `        - 'M-BM-'8M-iM-^\M-^@…M-oM-<M-^I'$`

# 2（/tmp 脚本身份）
$ ls -la /tmp/chk-b2102.js
-rw-r--r-- 1 box box 1047 Oct  1 03:23 /tmp/chk-b2102.js（mtime 03:23:28.88，与施工 commit 同秒）
tip §4 ```js 块原文 1091 字节（每行带 markdown 4 空格缩进）；textwrap.dedent 后 1047 字节
sha256(/tmp/chk-b2102.js) = sha256(dedent(§4 块)) = 74343bd58a06c4bacb487f4e89679fd26a1680f1fc2aaef968f4c4190d36bef2
diff dedent(§4) /tmp/chk-b2102.js → IDENTICAL（未改写 /tmp 文件；不入库）

$ node /tmp/chk-b2102.js 862d21c
only-L249-L250 /Icons/2/display/lore/1 /Icons/2/display/lore/2
exit=0
# 对照实验（验证能识别「未改」）
node /tmp/chk-b2102.js 6c2913a → []  exit=1（旧对旧）
node /tmp/chk-b2102.js 4045245 → []  exit=1（旧对旧）
node /tmp/chk-b2102.js WT      → []  exit=1（新对新：HEAD=862d21c 对工作区）
（本测报 commit 后 B=HEAD 结果见「回报摘要」下方附记）

# 独立深比对（递归 walk，含对象键序；js-yaml）
changed: ["/Icons/2/display/lore/1","/Icons/2/display/lore/2"]
Icons 含键 "2"：true；lore len 5 -> 5
0 ""                                                          same
1 "§7每人每周限通关 1 次 · 词缀精英 · 周首通稳定符"               CHANGED
2 "§8需余烬 Lv.40 · 消耗 §e%corerpg_stamina_cost_elite% §8体力（本周首次免费）"  CHANGED
3 ""                                                          same
4 "§e➥ 点击进入"                                               same
icon '2' 键：icons,update,refresh,display,actions · update 20 · refresh 20 · 条件图标 1 个
actions.all：["sound: BLOCK_NOTE_PLING-1-2","command: corerpg elite start","close"]（未变）
HEAD==862d21c==worktree（cmp）；git log 862d21c..HEAD -- F → 空

# 3
$ rg "每周 1 次|消耗 §e40" plugins/TrMenu/menus/ember_hub.yml
（无输出，rc=1）
$ rg "已消耗体力|体力 -|扣票|精英票" plugins/TrMenu/menus/ember_hub.yml
（无输出，rc=1）
$ rg -n stamina_cost_elite plugins/TrMenu/menus/ember_hub.yml
235:            - '§7需要 §e%corerpg_stamina_cost_elite% §7体力 · 当前 §f%corerpg_stamina%§7/§f%corerpg_stamina_max%'
240:            - 'tell: §c体力不足，需 %corerpg_stamina_cost_elite% 点体力（本周免费已用完）；每日 0:00 回满。'
250:        - '§8需余烬 Lv.40 · 消耗 §e%corerpg_stamina_cost_elite% §8体力（本周首次免费）'
$ git show 862d21c^:F | rg -n "每周 1 次|消耗 §e40"
249:        - '§7每周 1 次 · 词缀精英 · 周首通稳定符'
250:        - '§8需余烬 Lv.40 · 消耗 §e40 §8体力（本周首次免费）'
全文件：B0.1 已清 0 · 票已废 0 · §e40 0 · 精英票 0 · 只有一次 0 · 限通关 1（L249）
$ rg -n '消耗 §e[0-9]' F     # tip §6 预跑项，范围外
215: '§8进本消耗 §e45 §8体力 · 通关回枢纽'
266: '§8消耗 §e30 §8体力 · 进本扣体力'
280: '§8消耗 §e50 §8体力（本周首次免费）· 3～5 人'

# 6（HANDOFF §8 原样；grep -c 只出计数）
$ set -a; source secrets/mysql-ember.env; set +a
$ (git diff; git ls-files -o --exclude-standard | xargs -r cat) | grep -c -- "$MYSQL_PASSWORD"
0
[ -n "$MYSQL_PASSWORD" ] → true（未打印值）
$ cat server-runtime/ops.json
[]
```

## 占位符解析链（第 4 点；Java 路径前缀 `CoreRpg/src/main/java/town/sunshine/corerpg/`）

| 环节 | 依据 | 判定 |
|---|---|---|
| PAPI 注册 | `CoreRpgPlugin.java:441-446` `registerPapi`：检测 PlaceholderAPI 后 `new CoreRpgExpansion(this).register()`；`plugins/PlaceholderAPI/` 在仓 | 已注册 |
| 标识符 | `CoreRpgExpansion.java:10` `getIdentifier() → "corerpg"`；`:13` `persist() true`；`:16-19` `onPlaceholderRequest` 取 `params.toLowerCase()` 为 key | `%corerpg_<key>%` |
| 分支 | `CoreRpgExpansion.java:94-95` 取 `StaminaService`；`:103` `"stamina_cost_elite"` → `String.valueOf(st.costOf("elite"))`（同组 `:100-104` daily/weekly/abyss/elite/raid） | key = `stamina_cost_elite` |
| 取值 | `StaminaService.java:142-150` `costOf(String)` 查 `costs` 表；表由 `reload()`（`:96`）先 `defaults()`（`:97` → `:74-87`，`:84` `elite=40`）再读 cash.yml `stamina`（`:110-111`）→ `costs` 段逐键覆盖（`:119-124`） | 配置优先，缺省 40 |
| 配置值 | live `plugins/CoreRpg/cash.yml:26` `elite: 40`；src 模板 `CoreRpg/src/main/resources/cash.yml:26` `elite: 40`；两份 `stamina` 段 js-yaml 解析后深度相等 | 现行解析为 40，与旧硬编码同值 |
| 字符串格式 | L235、L240、L250 三处均为逐字 `%corerpg_stamina_cost_elite%`（`rg -n "%corerpg_stamina_cost_elite%"` → 235/240/250，完整带百分号）；L250 前后色码 `§e`…` §8` 与旧行一致 | 一致 |
| 同图标已在用 | 条件分支 `ember_hub.yml:227-240`（`condition: 'check papi %corerpg_stamina_blocked_elite% > 0'`，`priority: 2`，`inherit: true`）lore `:235`、tell `:240` 已用同一占位符；其它菜单：`ember_abyss.yml:63`、`ember_hub.yml:197`（daily）、`ember_daily.yml` 23 处 | TrMenu lore 内 PAPI 已是现行写法 |
| 刷新 | 图标 `'2'` 顶层 `ember_hub.yml:242` `update: 20`、`:243` `refresh: 20` | lore 定时重解析，改价后 reload 可跟随 |

## 口径核对（第 4 点续）

- 「每人每周限通关 1 次」与 B2.101 新 L20「每人每周限通关一次，没过可以再来」同一口径（lore 用阿拉伯数字、省去「没过可以再来」，tip §3 L42 已说明）。
- 源码依据同 B2.101：门槛 `EliteService.java:40-47`（`isClearedThisWeek` `:49-53`）；标记 COMPLETE 时逐人写 `ProgressService.java:471-481`（reward-script `EmberEliteWeekly/option.yml:36` `@player`）；发起者预检 `TicketEntryService.java:115-121`；失败不写标记（`EmberEliteWeekly/task/timeout.yml:7` `reward=false`）。
- 「（本周首次免费）」：`StaminaService.java:205-214` 换周 `setWeeklyGrantCreditElite(1)`；`:306-310` ELITE 先扣额度；首免用完且体力不足时由 `:227-240` 灰显分支接管（「本周免费已用完」）。与 tip §2 L26 一致。
- 「1」为通关次数，非费用；「Lv.40」为等级门（`plugins/CoreRpg/progress.yml:147` `elite: 40`），不在本窗。
- 无过度承诺。边界同 B2.101（OP 豁免可重复通关；结算瞬间离线的队员不计通关），与 lore 两行无直接关系，不重复展开。
- **OP 显示冲突：**OP 私聊为「管理免扣」（`TicketEntryService.java:139-140`），lore 仍显示价格；tip §2 L27-31 已记录并决定不处理，总控批注「OP 冲突只记不改」，本报告照记。

## 依赖检索（第 5 点）

- **mineflayer-tests（排除 node_modules）：**`rg '每周 1 次|消耗 §e40|§e40 §8体力|词缀精英|精英试炼（|周首通稳定符|stamina_cost_elite'` → **0 命中**；唯一相关的「本周首次免费」在 `stamina-s0-smoke.js:111`（周本私聊判定，与本 lore 无关）。**无脚本依赖两行旧文，也无脚本匹配新文。**
- **非 docs 其它位置：**
  - `plugins/CoreRpg/quest.yml:389` 与 `CoreRpg/src/main/resources/quest.yml:389`（逐字相同）：任务步 `event: elite_weekly_clear` 的 `hint: 打开枢纽菜单 → 精英试炼（每周 1 次）`——**玩家可见**（`QuestService.java:168` 读入，`:293`、`:471`、`:569` 发给玩家），与新 L249 / B2.101 L20 口径不一致。见前提更正 1。
  - `plugins/TrMenu/menus/ember_life.yml:66,90,98,107,115`「每周 1 次」为生活商店限购，`plugins/NeigeItems/Items/ember-craft-fish-combat.yml:64` 为稳固符限制，`EmberRaid/option.yml:30`「每人每周 1 枚 / 每周 1 次」为团戒/首免注释——均无关。
- **docs（只列，不改）：**16 个文件含「每周 1 次」或「消耗 §e40」，多为历史设计 / 测报引用；与本 lore 直接相关的原始出处为 `docs/design-stage4-elite-weekly.md:44`（精英 lore 原始设计 `§7每周 1 次 · 词缀精英 · 周首通稳定符`）、`docs/design-stage4-quest-vol2.md:157`（quest hint 原始设计）、`docs/design-stage4-mainline-vol2.md:161,282`（票时代「每周 1 次」）。

**B2.109 清单（本窗新增）：**

1. 无 mineflayer 脚本覆盖枢纽精英 lore；可在菜单扫描类脚本（如 `menu-cmd-sweep.js`）补读 `ember_hub` 图标 `'2'` lore：断言含「每人每周限通关 1 次」、「消耗 40 体力」（占位符已解析，不含 `%`）。
2. 既有（B2.99～B2.101 列）照旧，含 EliteWeekly 覆盖缺口与 `quest-vol2-4.5-smoke.js:78` 写标记副作用。

## 前提更正 / 补充

1. **（范围外，玩家可见，新发现）任务提示同类旧口径：**`plugins/CoreRpg/quest.yml:389`（src 模板 `CoreRpg/src/main/resources/quest.yml:389` 同）`hint: 打开枢纽菜单 → 精英试炼（每周 1 次）`，玩家在任务追踪 / 提示中可见；tip §6 与总控批注未列。建议新开一窗（live + src 两文件同行，改为「每人每周限通关 1 次」），编号由总控定。
2. **tip §2 L22「`StaminaService.reload` … 装入的表（L110-119）」行号偏移：**`reload()` 起于 `StaminaService.java:96`，`stamina` 段读取在 `:110-111`，`costs` 段逐键装入在 `:119-124`（L110-119 只覆盖到 `getConfigurationSection("costs")` 那一行）。结论不受影响。
3. **tip §4 脚本存放方式：**脚本在 tip 中带 markdown 4 空格缩进，「存为 `/tmp/chk-b2102.js`」需先去缩进；本机 `/tmp` 已有文件恰为去缩进版本（逐字节相同），mtime 与施工 commit 同秒，推断为施工岗所存。`/tmp` 不入版本控制，本次以 sha256 对 tip 为准。
4. **tip §4 脚本的覆盖边界（不影响本次）：**`flat` 只记录叶子值，空对象 / 空数组的增删不会计入差异；本次独立深比对（含键序与长度）已补足，确认无此类变化。
5. 其余核对无误：`Icons.2.display.lore[1]/[2]`（tip L5）、`CoreRpgExpansion` L103 与 L101-104、灰显分支 L227-240、L235/L240 已用占位符、`update: 20`/`refresh: 20`、`ember_abyss.yml:63`、`TicketEntryService` L140、L197（daily 同样用占位符）、tip §6 L215/L266/L280（已排 B2.113）。

## 范围外备注（不在本窗）

- `ember_hub.yml:215`（周本 45）、`:266`（深渊 30）、`:280`（团本 50）硬编码 → 总控已排 **B2.113**。
- `plugins/CoreRpg/quest.yml:389` 任务 hint「每周 1 次」（见前提更正 1）。
- `ember_hub.yml:250`「Lv.40」为等级门，硬编码与 `progress.yml:147` 同值；如需跟随可另议（非本窗）。
- OP 看 lore 与私聊不一致：只记不改（总控批注）。

## 旁证

1. **施工范围：**`862d21c` 只改 `ember_hub.yml` L249、L250；解析后仅 `/Icons/2/display/lore/1`、`/2` 变化，lore 长度、图标结构、条件分支、actions 全同。
2. **精确行文：**两行新值与 tip §3 逐字节相同，两行旧值与 tip §1 逐字节相同。
3. **口径：**未写「票已废」「B0.1 已清」；未改数值（占位符现行解析仍为 40）；NI 票物、cash.yml、CoreRpg、DP 未动。
4. **范围外未动：**`4045245^..HEAD` 期间触及 `plugins/`、`CoreRpg/`、`mineflayer-tests/` 的只有 `862d21c`（仅本文件）。
5. **做法纪律：**仅静态 git / sed / diff / cmp / node js-yaml / python / rg；未起服、未 `trm reload`、未长测、未杀进程；未改任何仓库文件（仅新增本报告）；未改写 `/tmp/chk-b2102.js`；机密未回显。
6. **时间戳：**tip STATUS 与总控批注均写「2026-10-01 03:22」批 A；设计 commit `4045245` 03:21:52、批准 commit `6c2913a` 03:22:50 CST，一致；施工 `862d21c` 03:23:28 在批准之后，顺序正确。tip L6 称「总控 03:20 定：L250 并入本窗」，早于设计 commit（03:21:52），与 B2.101 总控批注（03:11「并入 B2.102」）口径相符。

## 阻塞点

无（第 7 项实服观察待恢复服后实测）。

## 回报摘要

- 总评：PASS
- 各点：1 PASS / 2 PASS / 3 PASS / 4 PASS / 5 PASS（附 quest.yml:389 同类旧文）/ 6 PASS / 7 SKIP（待恢复服后实测）
- 前提更正：`quest.yml:389` 任务 hint「每周 1 次」未列入；tip L22 行号偏移（costs 装入在 `StaminaService.java:119-124`）；/tmp 脚本需去缩进（本机文件已核等同）
- 施工 tip SHA：`862d21c`
- 测报 tip short SHA：待本地 commit 后回报
- 是否已 push：否
- 报告路径：`docs/TEST-B2.102-hub-elite-lore.md`
- 阻塞点：无
- ahead：相对 `origin/main` ahead 1（未 push）
- `ops.json`：`[]`

附记（本测报 commit 后）：`node /tmp/chk-b2102.js HEAD`（HEAD^ = `862d21c`，新对新）→ 打印 `[]`，exit 1；脚本新对新 / 旧对旧均失败，不会假 PASS。
