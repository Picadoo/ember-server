# B2.99 · DP `EmberRaid/option.yml` L22 开本提示去扣费字样（荐案 b）· 纯静态薄验收

- **总评：PASS**（reload / 实服观察项记 SKIP：待恢复服后实测，不挡 PASS）
- **测报时间：**2026-10-01 02:58 Asia/Shanghai
- **岗别：**余烬-测试岗（B2.99 纯静态薄验收）
- **工作区：**`/workspace/minecraft`（main，与 `origin/main` 同步；三个 commit 本地均存在且为 HEAD 祖先，无需 fetch）
- **施工 tip SHA：**`b90e999`（2026-10-01 02:50:56 CST）
- **设计 / 批准：**`4007fb3`（02:47:21 CST）/ `27633ca`（02:50:16 CST）
- **tip：**`docs/design/design-ember-dp-raid-start-text-copy.md`（§1 入口与费用提示、§2 (a)/(b) 对比、§3 荐案 (b)、§4 验收、§5 明确不做、文末总控批注）
- **改动性质：**玩家可见 `text=`（非注释）
- **是否已 push：否**（本岗仅本地 commit，禁止 push）
- **报告路径：**`docs/tests/TEST-B2.99-dp-raid-start-text.md`
- **ahead：**相对 `origin/main` ahead 1（本测报本地 commit 后，未 push）
- **`server-runtime/ops.json`：**`[]`（未改动）
- **本机服状态：**25565/25566/25567/3306 均未监听（`ss -ltn` 计数 0），按总控批注不起服

## 各点

| # | 条件 | 结果 |
|---|------|------|
| 1 | 施工 commit 只改 `plugins/DungeonPlus/dungeon/EmberRaid/option.yml`，numstat `1 1`，hunk `@@ -22 +22 @@`；新 L22 逐字等于荐案 (b)；4 空格缩进、无 CR / 行尾空白；旧行对设计引用 | **PASS** · numstat `1	1`；hunk `@@ -22 +22 @@ dungeon-start:`，`diff` 仅 `22c22`；§3 yaml 块（1 行）与 live L22、`b90e999` L22、施工新增行 `cmp` 一致，sha256 相同；新旧 L22 均 4 空格、无 tab，CR 0、行尾空白 0；删除行 = 父版本 L22 `    - "$message{type=text;text=§8已消耗体力 ×1} @dungeon"`，含设计标题引用的「已消耗体力 ×1」，并与 B2.96 测报所录 L22 原文逐字一致（设计正文未给整行旧文，见前提更正 2） |
| 2 | 设计 js-yaml 命令原样输出 `only-L22`；独立确认只 `action-script[1]` 变化、长度仍 4、顺序与其它项不变；HEAD 与 `b90e999` 一致 | **PASS** · 原样命令（HEAD=`b90e999`）输出 `only-L22`，exit 0；独立递归深比对变化路径仅 `[".dungeon-start.action-script[1]"]`；长度 4→4；下标 0/2/3 逐项相同；顶层键顺序相同；除 L22 外全文逐行相同；HEAD、工作区与 `b90e999` 字节一致 |
| 3 | 扣费字样 `rg` 0 命中；全文件「B0.1 已清」「票已废」0 | **PASS** · 原样 `rg "已消耗体力|体力 ×|体力 -|扣票|团本票" …/EmberRaid/option.yml` 无输出（rc=1）；「B0.1 已清」0、「票已废」0、「已消耗」0、「消耗」0；全文件「×1」1 处在 L29 注释「孔石锋利×1」（奖励数量，范围外，不算失败） |
| 4 | 新文案出处与结算一致，不暗示扣费、不夸大 | **PASS（附离线 / 缺席边界备注）** · 见下「文案出处与结算核对」 |
| 5 | 入口与私聊前缀；mineflayer 依赖 | **PASS** · `[团本] 正在进入……（costHint）` 前缀正确；无测试脚本依赖旧 L22，也无脚本匹配新 L22；B2.109 清单见下 |
| 6 | HANDOFF §8 查密码命令输出 0；`ops.json` 为 `[]` | **PASS** · 原样执行（`grep -c` 只出计数）输出 `0`，变量非空 `true`（未打印值）；`ops.json` 为 `[]` |
| 7 | reload 后实测：本内见 L21 + 新 L22，发起者收「[团本] 正在进入……（本周首次免费 / 体力 -50）」 | **SKIP · 待恢复服后实测**（本机无运行中的服；总控批注明确不挡 PASS） |

## 目标行原文

```
# 父版本 b90e999^ L22（旧）
    - "$message{type=text;text=§8已消耗体力 ×1} @dungeon"
# live / b90e999 L22（新）
    - "$message{type=text;text=§8通关箱 · 全队每人结算 · 周首通保底团戒} @dungeon"

# live L20–L24 上下文
20:  action-script:
21:    - "$message{type=text;text=§9团本大厅已集结。§7左道卫兵 · 右道射手 · 汇合后终厅使徒。} @dungeon"
22:    - "$message{type=text;text=§8通关箱 · 全队每人结算 · 周首通保底团戒} @dungeon"
23:    - "$teleport{location=0,65,0;defspawn=true} @player"
24:    - "$monstergroup{group=wave1;repeat=false;delay=2} @dungeon"
```

## 命令与输出

```
$ git log --oneline -8
b90e999 feat(dp): B2.99 EmberRaid 开本提示去扣费字样
27633ca docs: approve B2.99 dp raid start text (A)
4007fb3 docs: B2.99 tip dp raid start text copy pending A
f0cae66 docs: close B2.98 dp weekly start text (PASS)
b4cec8c docs: B2.98 dp weekly start text test PASS
94d8759 feat(dp): B2.98 EmberWeekly 开本提示去扣费字样
baccdf4 docs: approve B2.98 dp weekly start text copy (A)
58103e9 docs: B2.98 tip dp weekly start text copy + ticket-era leftovers order pending A
$ git merge-base --is-ancestor <4007fb3|27633ca|b90e999> HEAD   → 均为祖先
$ git show --stat 4007fb3 / 27633ca   → 均只含本 tip 与 docs/design/design-ember-content-backlog.md

# 1
$ git show --stat --numstat --format= b90e999
1	1	plugins/DungeonPlus/dungeon/EmberRaid/option.yml
 plugins/DungeonPlus/dungeon/EmberRaid/option.yml | 2 +-
 1 file changed, 1 insertion(+), 1 deletion(-)
$ git diff -U0 b90e999^ b90e999 | grep '^@@'
@@ -22 +22 @@ dungeon-start:
$ diff <(git show b90e999^:F) <(git show b90e999:F) | grep -E '^[0-9]'
22c22

# 荐案行：tip §3 首个 ```yaml 块程序提取（1 行）
cmp expected live L22        → 一致
cmp expected b90e999 L22     → 一致
cmp expected 施工新增行(+)    → 一致
cmp 父版本 L22 施工删除行(-)  → 一致
cmp 删除行 与 docs/tests/TEST-B2.96-dp-raid-option-comment.md 所录 L22 → 一致
sha256（荐案 / live / 施工 tip / 新增行 四者相同）：c1788a4b10f7591cca2005b61ef6b3b44c721094e40b1c8f95362e98fa9681f0

# 行尾 / 缩进
CR 字节（L22）：0；全文件 CR：0；行尾空白：0
cat -A：行首 `    - "$message{type=text;text=M-BM-'8…`（§ 为 C2 A7），行尾 `} @dungeon"$`
b90e999^ L22：indent 4，len 52，无 tab
b90e999  L22：indent 4，len 66，无 tab

# 2（tip §4 原样，仓库根目录，HEAD=b90e999）
$ node -e 'const y=require("js-yaml"),{execSync:x}=require("child_process"),f="plugins/DungeonPlus/dungeon/EmberRaid/option.yml",g=r=>y.load(x("git show "+r+":"+f).toString());const a=g("HEAD~1"),b=g("HEAD");if(b["dungeon-start"]["action-script"].length!==4)process.exit(2);a["dungeon-start"]["action-script"][1]="$message{type=text;text=§8通关箱 · 全队每人结算 · 周首通保底团戒} @dungeon";if(JSON.stringify(a)!==JSON.stringify(b))process.exit(1);console.log("only-L22")'
only-L22
exit=0

# 独立深比对（递归 walk；/usr/share/nodejs/js-yaml）
changed paths: [".dungeon-start.action-script[1]"]
len 4 -> 4
idx 0 same: true / idx 2 same: true / idx 3 same: true
top-level key order same: true
$ diff <(git show b90e999^:F | sed '22d') <(git show b90e999:F | sed '22d')   → ALL_OTHER_LINES_IDENTICAL
HEAD==b90e999（cmp）；worktree==HEAD（cmp）；git log b90e999..HEAD -- F → 空

# 3
$ rg "已消耗体力|体力 ×|体力 -|扣票|团本票" plugins/DungeonPlus/dungeon/EmberRaid/option.yml
（无输出，rc=1）
$ rg -n "×1|已消耗体力|体力 -|扣票|团本票" F      # 设计 §4 原文写法（含 ×1）
29:# 材料保底；孔石锋利×1（二选一取锐）；不发灾厄余烬（灾厄主产）
全文件 / 新增行：B0.1 已清 0/0 · 票已废 0/0 · 已消耗 0/0 · 消耗 0/0 · ×1 1/0（L29）
（「体力」「扣」「票」「首免」「50」「首次免费」全文件命中均在 L3/L4/L17/L19/L30 注释，B2.96 已验；新增行均 0）

# 6（HANDOFF §8 原样；grep -c 只出计数）
$ set -a; source secrets/mysql-ember.env; set +a
$ (git diff; git ls-files -o --exclude-standard | xargs -r cat) | grep -c -- "$MYSQL_PASSWORD"
0
[ -n "$MYSQL_PASSWORD" ] → true（未打印值）
$ cat server-runtime/ops.json
[]
```

## 文案出处与结算核对（第 4 点）

| 新文案片段 | 出处 / 结算依据 | 判定 |
|---|---|---|
| 「通关箱 · 全队每人结算」 | 菜单 `plugins/TrMenu/menus/ember_raid_rewards.yml:46`（同句另见 `:54,62,70`；`:37`「全队每人结算 · 通关箱与终厅掉落一览」；`:1` 文件头）；`plugins/TrMenu/menus/ember_raid.yml:88`「通关箱（全队每人）」 | 出处一致，逐字复用 |
| 通关箱实际按人发放 | `EmberRaid/option.yml:28`「通关箱（每次 COMPLETE · 全队）」；reward-script `:35-45` 每条 `$command{…%player_name%…;console=true} @player`：`ni give` 核心碎片 4 / 附魔晶 2 / 碎片 10 / 骨尘 6（`:37-40`）+ `corerpg loot %player_name% raid_gem`（`:41`，`plugins/CoreRpg/loot.yml:14-16`「团本通关箱」锋利石 50%）；实服旁证 `docs/tests/smoke-raid-combat-20260926.md:31`「复测每人都拿到通关箱和团戒」、`:43` 通关箱全队 PASS、`:49` 非参与者背包增量 `{}` | 一致 |
| 「周首通保底团戒」 | `plugins/TrMenu/menus/ember_raid.yml:69`「§8周首通保底团戒 · 戒+刃=同袍套装」、`:93`；`EmberRaid/option.yml:30`「团戒：周首通保底（每人每周 1 枚）…」、`:42` 注释「周首通保底团戒（NI acc_ember_raid_ring）」、`:43` `corerpg raid grant-ring %player_name%`、`:44` 广播「周首通保底 · 余烬团戒…」 | 出处一致 |
| 团戒实际发 `acc_ember_raid_ring`、每人每周 1 枚 | 路由 `CoreRpgPlugin.java:896-897` → `RaidService.java:55-71`（需 `corerpg.admin`，控制台具备）→ `grantRing` `:93-123`：同周 `raidRingWeek` 已领则提示「本周团戒已领取」不发（`:100-106`），否则发 `ringNiId` ×1 并写周（`:108-114`）；`ringNiId` 取 `plugins/CoreRpg/set.yml:29-30` `raid_ring.ni_id: acc_ember_raid_ring`（`RaidService.java:48-50`，缺省同值 `:26`）；实服旁证 `docs/tests/smoke-raid-combat-20260926.md:44-46`（首通 3 人各 1 枚；同周再通不发；跨周可再发） | 一致 |
| 不暗示扣费 | 新 L22 无「体力 / 消耗 / 扣 / 票 / 数字」任何字样（见第 3 点计数）；扣费只由发起者私聊 costHint 告知 | 一致 |

路径前缀：`CoreRpgPlugin.java` / `RaidService.java` / `LootService.java` / `TicketEntryService.java` 均在 `CoreRpg/src/main/java/town/sunshine/corerpg/`。

**「保底」是否过度承诺（边界备注，不构成不符）：**

1. **离线 / 结算瞬间不在线的队员拿不到：**`grantRing` 对不在线目标直接返回（`RaidService.java:94-96`）；`corerpg loot` 对不在线玩家报「玩家不在线或无此 loot key」（`LootService.java:37-39`）；`ni give` 对不在线玩家同样失败。但团戒的周标记也不会写入，下次在线通关仍可领本周那枚——「保底」按「每人每周首次在线通关必得 1 枚」理解成立。
2. **中途离本 / 未到 COMPLETE 的队员：**reward-script 以 `@player` 执行，实服旁证显示非参与者拿不到（`docs/tests/smoke-raid-combat-20260926.md:49`）；DP 对「中途 `/dp leave` 后是否仍算 `@player`」的判定无法在本机读源码确认（仓库不含 DungeonPlus jar，按硬规则不入库），仅记。
3. **同周再通：**通关箱照发、团戒不再发（`RaidService.java:100-106`；旁证 `:45`），新 L22「周首通保底团戒」只承诺首通，与之相符；L45 广播「同周再通不重复发戒」仍在，二者不冲突。
4. 结论：新 L22 是对现行结算的准确摘要，用词全部来自已上线的菜单与本文件现有广播，未新增口径；「保底」不构成过度承诺。

## 入口与私聊（第 5 点）

- 菜单 `plugins/TrMenu/menus/ember_raid.yml:79` `command: corerpg enter raid` → `CoreRpgPlugin.java:842-843` → `TicketEntryService.java:235-241` `Kind.parse("raid")` → `tryEnter(player, Kind.RAID)`（`:100`）；`:130` 只对发起者 `consumeForEnter`；`:159` `dp start-console <发起者> EmberRaid`。
- `Kind.RAID` 定义 `TicketEntryService.java:29`：`RAID("raid", "EmberRaid", "ticket_ember_raid", "余烬团本", "raid", "团本")`，`shortLabel`=「团本」；私聊 `:148-149` `"[" + kind.shortLabel + "] " + "正在进入……（" + costHint + "）"` → 「[团本] 正在进入……（…）」。costHint `:140`「管理免扣」/ `:142`「本周首次免费」/ `:144`「体力 -N」（团本 N=50）/ `:146`「无消耗」，与 tip §1 所列 4 种一致。
- 新 L22 与私聊分工清楚：私聊（仅发起者）讲扣费，L22（`@dungeon` 全队）讲奖励，无重复、无矛盾。

**mineflayer 依赖（全 `mineflayer-tests/`，排除 node_modules）：**

- 旧 L22「已消耗体力 ×1」：**无任何脚本匹配**（「已消耗体力」仅在 `stamina-s0-smoke.js:64-65` 日常判定，与团本无关）。
- 新 L22「通关箱 · 全队每人结算 · 周首通保底团戒」：**无脚本匹配**（`audit-fix-smoke.js:24` 的 `/通关箱自动结算/` 匹配的是 `RaidService.java:84`「团戒由团本通关箱自动结算…」，与 L22 无关）。
- `killany-live-retest.js:122` 进本判定 `/团本大厅已集结|【左道】|左道卫兵|已扣除余烬团本票|团本已点燃|正在进入/`：命中 L21「团本大厅已集结」与私聊「正在进入」，不受本窗影响（tip §2 表格结论正确）。

**B2.109 清单（团本相关旧匹配 / 票时代断言，本窗不改）：**

1. `mineflayer-tests/raid-combat-smoke.js:153-154,170`：进本判定只认 `/团本已点燃/`。该句已不在 `EmberRaid/option.yml`（`git log -S` 显示最后出现于 `553efc4`），现仅存于菜单点击 tell `plugins/TrMenu/menus/ember_raid.yml:76`；脚本走 `/dp start EmberRaid`（`:132,150`）不经菜单，**实服上进本判定会假 FAIL**。应改为 L21「团本大厅已集结」或私聊「正在进入」。
2. `mineflayer-tests/raid-combat-smoke.js:155`：`R.ticketMsg = /已扣除余烬团本票/` 票时代断言。
3. `mineflayer-tests/killany-live-retest.js:122`：正则里「已扣除余烬团本票」「团本已点燃」两个备选已失效（整体仍能命中，不致 FAIL），建议清理。
4. `mineflayer-tests/abyss-followup.js:58`：`/团本已点燃|创建完毕/`，同第 1 条（走 `/dp start EmberRaid`，`:53`）。
5. `mineflayer-tests/abyss-calamity-supplement.js:107-125`：`raid_ticket` 发票判定（`:111`）属票时代步骤。
6. `mineflayer-tests/dungeon-balance.js:21`：raid 配置仍带 `ticket: 'ticket_ember_raid'` 字段，是否仍发票需出稿时核对。
7. 既有：`gates-smoke.js:26-30`、`stamina-s0-smoke.js:120`（tip §5 已列）。

## 前提更正 / 补充

1. **设计 §4 扣费字样 `rg` 命令会命中 1 行：**tip §4 写 `rg -n "×1|已消耗体力|体力 -|扣票|团本票" …` 「无命中」，实际命中 L29 注释「孔石锋利×1」（奖励数量，非扣费，范围外）。本次按简报原样命令（不含 `×1`，改为 `体力 ×`）为 0 命中；设计里的检查项写法需修正，结论不受影响。
2. **旧行引用：**设计正文未给出整行旧文（只在标题写「已消耗体力 ×1」），「旧行对设计引用」只能按片段核对；整行已与父版本 L22 及 B2.96 测报所录原文逐字比对一致。
3. **总控批注出处 `EmberRaid/option.yml:30/44`：**正确；另 `:42` 注释亦写「周首通保底团戒」。简报引用的 `:42-44` 也成立。
4. `TicketEntryService.java:148-149`、`:29` shortLabel、菜单 `ember_raid.yml:79`、`ember_raid_rewards.yml:46`、`ember_raid.yml:69`：行号与内容均与现文件一致。

## 范围外备注（不在本窗）

- **菜单点击 tell 与首免不符（新发现，玩家可见）：**`plugins/TrMenu/menus/ember_raid.yml:76-77` 点击即 `tell`「团本已点燃…」+「§8消耗 §e50 §8体力 · 人数 3～5」，不区分首免，也在 CoreRpg 预检前发出（等级不足被拒时也会看到）。与私聊 costHint「本周首次免费」可能矛盾；属 TrMenu 文案，建议在 B2.102 同类窗或另开一窗评估（`:64` lore 已写「（本周首次免费）」，可对齐）。
- L29「孔石锋利×1」为奖励数量注释，不属扣费字样，保持。
- 七个日常本「已消耗体力」只记不排（tip §5）。

## 旁证

1. **施工范围：**`b90e999` 只改 `EmberRaid/option.yml` L22 一行 `text=`；解析后仅 `dungeon-start.action-script[1]` 变化，列表长度与顺序不变，其余键、注释、脚本逐行相同。
2. **精确行文：**live、施工 tip、新增行与 tip §3 荐案四者 sha256 相同；颜色 `§8`、引号、`@dungeon` 与旧行一致。
3. **口径：**删去「已消耗体力 ×1」，未写数字与扣费；未写「票已废」「B0.1 已清」；NI 票物、数值未动。
4. **范围外未动：**`4007fb3^..HEAD` 期间无 commit 触及 `plugins/CoreRpg`、`CoreRpg/`、`plugins/TrMenu`、`mineflayer-tests`；其它 option.yml 未动。
5. **做法纪律：**仅静态 git / sed / diff / cmp / node js-yaml / python / rg；未起服、未 `/dp reload`、未长测、未杀进程；未改任何文件（仅新增本报告）；机密未回显。
6. **时间戳：**tip 与总控批注写「2026-10-01 02:50」批 A，批准 commit `27633ca` 为 02:50:16 CST，一致；顺序设计 → 批准 → 施工正确。

## 阻塞点

无（第 7 项实服观察待恢复服后实测）。

## 回报摘要

- 总评：PASS
- 各点：1 PASS / 2 PASS / 3 PASS / 4 PASS（附离线边界备注）/ 5 PASS / 6 PASS / 7 SKIP（待恢复服后实测）
- 前提更正：设计 §4 含 `×1` 的 rg 会命中 L29「孔石锋利×1」
- 施工 tip SHA：`b90e999`
- 测报 tip short SHA：待本地 commit 后回报
- 是否已 push：否
- 报告路径：`docs/tests/TEST-B2.99-dp-raid-start-text.md`
- 阻塞点：无
- ahead：相对 `origin/main` ahead 1（未 push）
- `ops.json`：`[]`
