# B2.111 · TrMenu `plugins/TrMenu/menus/ember_abyss.yml` L77 进本按钮 tell 去掉「· 体力 30；等级不足不扣」· 纯静态薄验收

- **总评：PASS**（reload / 实服观察项记 SKIP：待恢复服后实测，不挡 PASS）
- **测报时间：**2026-10-01 13:32 CST（`date`）
- **岗别：**余烬-测试岗（B2.111 纯静态薄验收）
- **开工前：**`git status --porcelain` 空（干净）；`git pull --ff-only` → `Already up to date.`（rc 0；本地未超前、未分叉；上一批测报 `e405013` 已在 `origin/main` 中，HEAD = origin/main = `4cd3be3`）；复读 `docs/handoff/HANDOFF.md`（§8 查密码、`ops.json` 必须 `[]`、提交身份、杀进程用 PID）与 `docs/reviews/LESSONS-ember-pipeline.md`（以磁盘 + reload 为准；只记 PASS/FAIL/SKIP），两文件自 `2e5649f`（2026-09-27 21:37 CST）后未改。
- **工作区：**`/workspace/minecraft`（main）
- **施工 tip SHA：**`4cd3be3`（2026-10-01 13:29:07 CST）
- **设计 / 批准：**`227bec9`（13:27:45 CST）/ `4e0c11a`（13:28:38 CST）
- **tip：**`docs/design/design-ember-abyss-menu-enter-copy.md`（§1 完整旧行、§2 代码核对表、§3 方案对比、§4 荐案（方案 A）、§5 验收与 heredoc、§6 明确不做、§7 同文件写死「30」、文末总控批注）
- **改动性质：**玩家可见菜单点击 tell（聊天栏，仅发给点按钮的人）
- **是否已 push：否**（本岗仅本地 commit，禁止 push）
- **报告路径：**`docs/tests/TEST-B2.111-abyss-menu-enter-tell.md`
- **ahead：**相对 `origin/main` ahead 1（本测报本地 commit 后，未 push）
- **`server-runtime/ops.json`：**`[]`（未改动）
- **本机服状态：**25565/25566/25567/3306 均未监听（`ss -ltn` 计数 0）；未起服、未结束任何进程

## 各点

| # | 条件 | 结果 |
|---|------|------|
| 0 | 三个 commit 时间顺序；设计 / 批准只改文档 | **PASS** · 设计 `227bec9` 13:27:45 → 批准 `4e0c11a` 13:28:38 → 施工 `4cd3be3` 13:29:07（committer = author 时间，CST）；三者均为 HEAD 祖先；`227bec9`、`4e0c11a` 只改 `docs/design/design-ember-abyss-menu-enter-copy.md` 与 `docs/design/design-ember-content-backlog.md`；`227bec9^..HEAD` 触及 `plugins/`、`CoreRpg/`、`mineflayer-tests/` 的只有 `4cd3be3` |
| 1 | `4cd3be3` 只改 L77，numstat `1 1`；旧行 = §1；新行 = 方案 A；8 空格、单引号；整文件字节级无 tab/CR/行尾空格；L41/L49/L54/L78/L79/L130/L132/L139 未动 | **PASS** · numstat `1	1	plugins/TrMenu/menus/ember_abyss.yml`；hunk `@@ -77 +77 @@ Icons:`，`diff` 仅 `77c77`，删 L77 后其余行逐字节相同；旧行 = tip §1 块 = 施工删除行（sha256 `b4b56bfa…09226349`，106 字节），新行 = tip §4 块 = 施工新增行 = live = HEAD（sha256 `b4aa814b…96fbe8f3a`，72 字节）；新行恰为旧行删去子串「 · 体力 30；等级不足不扣」，其余字节不变；两行缩进均 8 空格，以 `- '` 起、`'` 止；整文件 4223 字节 139 行、末尾有换行，字节级 0x09 = 0、0x0D = 0、以 ASCII / Unicode 空白结尾的行 0（`cat -A` 每行以 `$` 收尾，多字节字符不作判据）；指定 8 行在父版本、施工版、live 三处逐字节相同 |
| 2 | `/tmp/chk-b2111.js` 与 heredoc 一致；`4cd3be3` → `only-L77 /Icons/S/actions/all/1` exit 0；旧对旧 / 新对新 exit 1；变异；exit 5 可达条件；独立 deep diff | **PASS（附 exit 5 语义更正，见「前提更正 1」）** · `/tmp/chk-b2111.js` 已存在（1250 字节，mtime 13:29:07.10，box 用户，与施工 commit 同秒），与 tip §5 ```sh 块 heredoc 正文逐字节相同（sha256 `e3a9d9c8…4c9049ac`；块内缩进行 0）；按稿重跑 heredoc（只改落盘路径）产物 `cmp` 一致；**未覆盖**。`4cd3be3` → `only-L77 /Icons/S/actions/all/1` exit 0；旧对旧 `4e0c11a`、`227bec9` → `[]` exit 1；新对新 `WT` → `[]` exit 1；无参 exit 9。临时仓变异 11 例全部按预期拦下或通过（见命令区）；exit 5 仅在两侧 `all/3` 相同且都不是 `command: corerpg enter abyss` 时可达；独立 deep diff 仅 `["/Icons/S/actions/all/1"]` |
| 3 | `rg 体力 30\|等级不足不扣\|已消耗体力\|体力 -\|扣票\|深渊票` 0；「B0.1 已清」「票已废」0 | **PASS** · 原样 rg 无输出 rc=1（父版本只命中 L77，与稿 §5 一致）；`rg -F "B0.1 已清"`、`rg -F "票已废"` 均 0；稿 §7 `rg -n 30` 施工前 L40/41/49/54/77/130/139，施工后 L40/41/49/54/130/139（只少 L77） |
| 4 | 无周首免；等级门（发起者先挡、队员先扣后退）；OP 跳门免扣；各情况私聊；新句每个说法有依据；不与 DP L22-23 重复；灰显接管 | **PASS（附边界标注）** · 见下「私聊核对」「队员等级不足：谁扣谁退」「灰显接管」「依据、重复与承诺」 |
| 5 | 无 mineflayer / 非 docs 文件依赖旧「体力 30」「等级不足不扣」tell | **PASS** · 没有脚本打开 `ember_abyss` 菜单（`rg -P '(?<!ticket_)ember_abyss(?!_)' mineflayer-tests` 0 命中）；四个 `abyss-*` 脚本都用 `/dp start EmberAbyss` 或 `/corerpg abyss evacuate`，不经过菜单；「等级不足不扣」非 docs 0 命中；「体力 30」非 docs 只剩 6 个日常 DP `option.yml:2` 注释（不对玩家，与深渊无关） |
| 6 | HANDOFF §8 命令计数 0；报告自身 0；`ops.json` `[]` | **PASS** · 原样执行（`docs/handoff/HANDOFF.md:140-141`）输出 `0`，变量非空（未打印值）；commit 前（本报告为未跟踪文件，已被该命令纳入）再跑仍为 `0`，另对本报告 `grep -cF` 为 `0`；`ops.json` 为 `[]` |
| 7 | reload / live：Lv.25 以下非 OP、付费、OP 各点一次「开始下潜」，聊天为新句 + L78 + 对应插件私聊 | **SKIP · 待恢复服后实测**（本机无运行中的服；总控批注明确不挡 PASS） |

## 目标行原文

```
# 父版本 4cd3be3^ L77（旧；= tip §1）
        - 'tell: §5[深渊] §7尝试下潜……（需余烬 Lv.25 · 体力 30；等级不足不扣）'
  sha256 b4b56bfad62dd01aaeaa909ef52fe4b8310cf53288c54e9cf7614fc609226349（106 字节）
# live / 4cd3be3 L77（新；= tip §4 方案 A）
        - 'tell: §5[深渊] §7尝试下潜……（需余烬 Lv.25）'
  sha256 b4aa814b208e45e16bd684c70e4a2f060e8ff4125c754dd61b92fb496fbe8f3a（72 字节）
（sha256 针对不含换行的整行字节，含行首 8 空格）

# live Icons.S.actions.all（L74-L80，解析后）
["sound: BLOCK_NOTE_PLING-1-2",
 "tell: §5[深渊] §7尝试下潜……（需余烬 Lv.25）",     ← /Icons/S/actions/all/1（L77）
 "tell: §8需要撤离：打开本菜单 → 上浮撤离",          ← L78 未动
 "command: corerpg enter abyss",                    ← /Icons/S/actions/all/3（L79）未动
 "close"]                                           ← L80 未动

# 未动的同文件行
L41  : - condition: 'check papi %corerpg_stamina% < 30'
L49  : - '§7需要 §e30 §7体力 · 当前 §f%corerpg_stamina%§7/§f%corerpg_stamina_max%'
L54  : - 'tell: §c体力不足，需 30 点体力；每日 0:00 回满。'
L130 : - '§7消耗 §e30 §7体力（与日常同池）'
L132 : - '§7硬顶建议 ≤2/日（免费+购）'
L139 : - 'tell: §5深渊消耗 30 体力；0:00（上海）回满；撤离不退。'
```

## 命令与输出

```
# 0
git pull --ff-only → Already up to date.（rc 0）
git merge-base --is-ancestor e405013 origin/main → 是
git show --stat 227bec9 → docs/design/design-ember-abyss-menu-enter-copy.md | 126 +, docs/design/design-ember-content-backlog.md | 8 +-
git show --stat 4e0c11a → docs/design/design-ember-abyss-menu-enter-copy.md | 9 +-, docs/design/design-ember-content-backlog.md | 12 +-
git show --stat 4cd3be3 → plugins/TrMenu/menus/ember_abyss.yml | 2 +-

# 1
git show --numstat --format= 4cd3be3   → 1	1	plugins/TrMenu/menus/ember_abyss.yml
git diff -U0 4cd3be3^ 4cd3be3 | grep @@ → @@ -77 +77 @@ Icons:
diff <(父 | sed 77d) <(施工 | sed 77d) → 无差异
cmp HEAD:file live、live 4cd3be3:file → 相同
python 字节核对：old==§1 True / new==§4A True / 删除行==旧 True / 新增行==新 True / live==新 True
                 新行 == 旧行.replace(" · 体力 30；等级不足不扣", "") True
                 bytes 4223 · lines 139 · 0x09 0 · 0x0D 0 · 行尾空白（ASCII / Unicode）均 []

# 2（/tmp 脚本身份）
稿 §5 ```sh 块 1 个；块内以空白开头的行 0
heredoc 正文   1250 字节 sha256 e3a9d9c8a3c95b126ffd008e764143703c4ef66cbdc2581d90faa50e4c9049ac
/tmp/chk-b2111.js 1250 字节 sha256 e3a9d9c8a3c95b126ffd008e764143703c4ef66cbdc2581d90faa50e4c9049ac → 逐字节相同
按稿重跑 heredoc 到 /tmp/b2111-regen/chk.js → cmp 一致（未覆盖原文件）

node /tmp/chk-b2111.js 4cd3be3 → only-L77 /Icons/S/actions/all/1    exit 0
node /tmp/chk-b2111.js 4e0c11a → []   exit 1   （旧对旧）
node /tmp/chk-b2111.js 227bec9 → []   exit 1   （旧对旧）
node /tmp/chk-b2111.js WT      → []   exit 1   （新对新：HEAD 对工作区）
node /tmp/chk-b2111.js         → usage          exit 9

# 变异（/tmp/b2111-mut 一次性临时仓，逐提交构造父/子；与主仓无关）
| 例 | 构造（父 → 子） | 实际 exit | 稿 §5 预期 | 判定 |
|---|---|---|---|---|
| 新值错 | 旧 → 新但「Lv.25」写成「Lv.26」 | 4 | 4 | 拦住 |
| 旧值错 | 父 L77「尝试下潜」改「开始下潜」→ 新 | 3 | 3 | 拦住 |
| 多改一行（同数组） | 旧 → 新 + L78「上浮撤离」改「上浮撤出」（打印 all/1、all/2） | 1 | 1 | 拦住 |
| 多改一行（别的图标） | 旧 → 新 + L139 改字（打印 all/1、/Icons/R/actions/all/1） | 1 | 1 | 拦住 |
| 漏改（子 = 父，空提交） | 无变化（打印 []） | 1 | 1 | 拦住 |
| 漏改 L77、改了别行 | 旧 → 只改 L78（打印 all/2） | 1 | 1 | 拦住 |
| all/3 仅子侧改 | 旧 → 新 + L79 `enter abyss`→`enter raid`（打印 all/1、all/3） | **1** | 稿写 5（「不是 enter abyss 或前后不一致」） | 拦住，但退出码是 1 |
| all/3 两侧同错 | 父、子 L79 都是 `enter raid`，其余为正常施工 | 5 | 5 | 拦住 |
| 两侧都插入同一项 | 父、子都在 L79 前插同一条 tell（all/3 两侧均为该 tell） | 5 | 5 | 拦住 |
| 子侧插入一项 | 旧 → 新 + L79 前插一条 tell（打印 all/1、all/3、all/4、all/5） | 1 | 1 | 拦住 |
| 对照 | 旧 → 新（正确施工） | 0 | 0 | 通过 |
（另：变异前一次「空提交」构造失误导致脚本收到非法 ref 抛错 exit 1，已改用 `--allow-empty` 重做，以上表为准）

# exit 5 可达条件（读脚本 L87-L90）
L87：差异路径必须恰为 ["/Icons/S/actions/all/1"]，否则 exit 1。
L90：A[all/3] 不是 enter abyss，或 C[all/3] != A[all/3]，exit 5。
只要子侧改了 all/3，all/3 就进了差异集合，L87 先 exit 1，所以「C[all/3] != A[all/3]」这半句永远走不到；
exit 5 只在父子两侧 all/3 相同、且都不是 enter abyss 时触发（父版本本来就错，或两侧同样插入 / 改动）。与 B2.110 同。

# 独立 deep diff（js-yaml；递归比较类型、数组长度、对象键序与叶子值）
changed: ["/Icons/S/actions/all/1"]
新文件空对象 / 空数组：0 个
Icons.S 键序：icons,update,refresh,display,actions（父子相同）

# 3
rg "体力 30|等级不足不扣|已消耗体力|体力 -|扣票|深渊票" plugins/TrMenu/menus/ember_abyss.yml → 无输出 rc=1
（父版本同命令 → 仅 77:）
rg -F "B0.1 已清" → 0 ； rg -F "票已废" → 0
rg -n 30：父 40 41 49 54 77 130 139 → 施工后 40 41 49 54 130 139

# 6（HANDOFF §8 原样；grep -c 只出计数）
set -a; source secrets/mysql-ember.env; set +a
(git diff; git ls-files -o --exclude-standard | xargs -r cat) | grep -c -- "$MYSQL_PASSWORD"   → 0
（在子 shell 中执行，变量不留在会话里；commit 前含本报告再跑仍为 0）
cat server-runtime/ops.json → []
```

## 私聊核对（第 4 点；Java 路径前缀 `CoreRpg/src/main/java/town/sunshine/corerpg/`）

调用链：菜单 `ember_abyss.yml:79` `command: corerpg enter abyss` → `CoreRpgPlugin.java:842-843` → `TicketEntryService.cmdEnter`（`TicketEntryService.java:226-242`）→ `tryEnter`（L100-203）。`Kind.ABYSS` = `("abyss","EmberAbyss","ticket_ember_abyss","余烬深渊","abyss","深渊")`（L28）。CoreRpg 只校验、只扣**发起者**；队伍人数与每名队员的等级由 DP `EmberAbyss/option.yml:17`、`:19` 判定。

**深渊没有周首免：**`StaminaService.consumeForEnter`（L293-327）的周免抵扣只有三支——WEEKLY（L301-305）、ELITE（L306-310）、RAID（L311-315）；ABYSS 不进任何一支，直接到 L316 起按 `costOf(ABYSS)` 扣。`ensureWeekCredits`（L205-214）也只发 weekly / elite / raid 三种周免。旁证：DP `option.yml:3`「深渊无首免」、菜单 `ember_abyss.yml:40`「深渊无周免费」。所以深渊的 costHint 不会出现「本周首次免费」。

| 情况 | 发送语句（file:line） | 玩家看到的原文 | 扣 / 退 |
|---|---|---|---|
| 发起者等级不足（非 OP） | `TicketEntryService.java:109-113`（`need` 来自 `ProgressService.gateLevel("abyss")`：live/src `progress.yml:143 abyss: 25`，`ProgressService.java:132` 默认 25） | `§c余烬深渊需要余烬 Lv.25§7（当前 Lv.x）` | 不扣：L113 return 在 L130 `consumeForEnter` 之前 |
| 体力不足（非 OP） | `StaminaService.java:319-322` 组字串，`TicketEntryService.java:131-134` 发送 | `§c体力不足（需 30，当前 x/上限）· 明日 0 点恢复` | 不扣 |
| 体力服务未就绪（非 OP） | `TicketEntryService.java:126-129` | `§c体力服务未就绪` | 不扣 |
| 付费成功 | `StaminaService.java:324-326`（`costOf`：`:85` 默认 30，live/src `cash.yml:27 abyss: 30`）；`TicketEntryService.java:143-144` + `:148-149` | `§e[深渊] §7正在进入……（体力 -30）` | 扣 30 |
| OP / `corerpg.admin` | `TicketEntryService.java:103` 判 OP；`:106` `if (!op)` 跳过 L106-122 等级门，`:125` `if (!op)` 跳过 L125-136 扣费；`:139-140` + `:148-149` | `§e[深渊] §7正在进入……（管理免扣）` | 不扣 |
| cost ≤ 0（仅配置改成 0 时） | `StaminaService.java:316-317`；`TicketEntryService.java:145-146` | `§e[深渊] §7正在进入……（无消耗）` | 不扣（现配置 30，不触发） |
| `dp start-console` 返回 false | `TicketEntryService.java:167-173` → `StaminaService.refundEnter`（L329-345，L341-343 加回体力）→ `tellEnterFailed`（L205-211） | `§e出本后再进约等 5 秒（缓存冷却），不是进本坏了`；非 5.5 秒内重试再加 `§7若仍进不去，请稍后再试（体力已退还，若已扣）` | 退还 |
| dispatch 成功但 40 tick（约 2 秒）后不在副本（含人数 1～2 拒、队员等级拒、DP 冷却） | `TicketEntryService.java:186-201`（`ENTER_VERIFY_TICKS = 40L`，L82）→ 同上 | DP 先发自己的红字（`option.yml:17` `§c深渊人数 1～2，当前 (<size>)` / `:19` `§c深渊需要余烬等级 §eLv.25§c · 队伍中有人等级不足 · 打开枢纽 → 角色查看等级`；DP 源码不在仓库，属配置层核对）+ 同上两句 | 退还 |

结论：发起者等级不足、体力不足、付费成功、OP 免扣、DP 启动失败退还都有私聊；深渊没有周首免这一情况（代码里不存在），不需要私聊。菜单不写扣费，玩家也不会漏掉扣费结果。

**没有私聊或私聊不全的情形（标注，均非本窗引入，不挡 PASS）：**

1. **5.5 秒内重试被退还：**`recent` 为真时（L153-156、L209）只发「出本后再进约等 5 秒……」，**不发**「体力已退还」——已退，但玩家不会被告知。
2. **40 tick 校验时发起者已下线：**L189 `return` 在 L194-195 `refundEnter` 之前——**不退还也无提示**。
3. **人数 / 队员等级被 DP 拒：**CoreRpg 在 DP 红字之后补「出本后再进约等 5 秒（缓存冷却），不是进本坏了」，把人数或等级问题说成缓存冷却，可能误导。
4. **成功进本：**发起者只见 L148-149 一句，随后全队见 DP L22-L23，无缺口。
（1-3 与 B2.110 同源，tip §7 排期表里的 B2.119「退还提示例外，需 build」应即此类。）

## 队员等级不足：谁扣谁退（第 4 点续）

1. 非 OP 发起者 A（等级够）点按钮：`tryEnter(A)` 只查 A 的等级（L102 `data` = A 的数据，L109 比较），通过；L130 `consumeForEnter(A, ABYSS)` 扣 **A 的** 30 体力（`StaminaService.java:297` `dataStore.get(player.getUniqueId())`，L324）。队员 B **从未被查等级，也从未被扣**——`tryEnter` 里没有任何遍历队员的代码。
2. L159-162 发 `dp start-console A EmberAbyss`；DP 对队员逐个判 `option.yml:19` `%corerpg_gate_abyss%=='yes'||%player_is_op%=='yes'`（`:18` 注释「DP 逐队员判定；被拒未进本时 CoreRpg 退还本次体力；OP 豁免」），B 等级不足 → 拒开本，DP 发 L19 红字。
3. 40 tick 后 `looksInDungeon(A)` 为假 → `refundEnter(A, ABYSS, consumed)`（L194-195，`p` = `Bukkit.getPlayer(pid)`，`pid` 是 A 的 UUID，L151）→ `StaminaService.java:341-343` 把 30 加回 **A**。若 `dispatch` 直接返回 false，则 L168-169 同样退给 A。
4. 结论：**扣的是发起者 A，退的也是 A；等级不足的队员 B 既不扣也不退**。旧句「等级不足不扣」对 A 本人等级不足是对的（不扣），对队员等级不足是「A 先扣后退」，对 OP 不适用（OP 跳过等级门、不扣）——与 tip §2 L33-37、总控批注一致。
5. 边界：A 为 OP、B 非 OP 且等级不足时，CoreRpg 跳过 A 的等级门且不扣（`consumed` 为 null），DP 仍因 B 拒开本；L168 / L194 的 `!op` 条件使退还跳过（本来就没扣），A 只收到 L208 冷却句和 L210「体力已退还，若已扣」。

## 灰显接管（第 4 点续）

- `ember_abyss.yml:38-57`：`S.icons[0]` = `condition: 'check papi %corerpg_stamina% < 30'`（L41）、`priority: 2`（L42）、`inherit: true`（L43），自带 `display`（L44-50 灰玻璃板「开始下潜 · 体力不足」）和 `actions.all`（L51-54：sound + tell「§c体力不足，需 30 点体力；每日 0:00 回满。」，**无** `corerpg enter abyss`）；`update: 20` / `refresh: 20`（L56-57）；默认 `S.actions.all` 在 L74-80。
- 深渊不用 `stamina_blocked_*`：`CoreRpgExpansion.java:110-124` 只有 `stamina_blocked_weekly / raid / elite`，没有 abyss；深渊没有周免，所以菜单直接用字面量 `< 30`（L40 注释「字面量 30（禁双侧 PAPI）」）。
- TrMenu 源码不在仓库，子图标接管点击以实测为据：`docs/status/STATUS-ember-enter-stamina-gray-test.md:50-51`（测 `6bda99e`）深渊「stamina=10 → S=开始下潜 · 体力不足」「点灰 chat **仅** `体力不足，需 30 点体力；每日 0:00 回满。` · **无**「正在进入」/ 建队」PASS。
- 所以走到 L77-L79 的人是：发起者等级不足、付费、OP（体力 ≥30 时）、人数 / 队员等级不对，以及 20 tick 刷新间隙里刚变成体力不足的人（由 L131-134 私聊兜底）。
- **边界（标注）：**L41 不区分 OP。OP 体力 <30 时也被灰显拦下，从菜单拿不到「管理免扣」，需用 `/corerpg enter abyss`；且条件写死 30，不跟 `cash.yml`。总控批注已「同列只记」，不在本窗。

## 依据、重复与承诺（第 4 点续）

- 新 L77「§5[深渊] §7尝试下潜……（需余烬 Lv.25）」只有两个说法：
  - 「尝试下潜」：菜单接着执行 L79 `corerpg enter abyss` → `TicketEntryService.java:159-162` `dp start-console`，用「尝试」不承诺成功。
  - 「需余烬 Lv.25」：live/src `progress.yml:143 abyss: 25`、`ProgressService.java:132` 默认 25、`TicketEntryService.java:109-112` 发起者门、DP `option.yml:19` 队员门红字「Lv.25」、菜单 lore `ember_abyss.yml:71`「需余烬 Lv.25」。OP 实际豁免（`TicketEntryService.java:106`、`option.yml:19` `player_is_op`），但「需」是对玩家的规则说明，不构成承诺；与 `ember_raid.yml:78` 同类。
- 新句不写人数。人数 1～2 只在 lore L71 和 DP `option.yml:17` 失败红字里，不在本句，无需核对。
- 没有费用、首免、退还、奖励、成功的说法 → 无过度承诺。**PASS**。
- 与 DP `EmberAbyss/option.yml:22`「§5深渊已开启。§7能走多深，就走多深。本期可下潜至第 §f12 §7层。」、`:23`「§8需要撤离：打开菜单 → 深渊 → 撤离 · 按最高层结算 · 体力不退」**不重复**：新句不含「已开启」、层数、撤离、结算、体力。
- 范围外（L78 未动，只记）：L78「§8需要撤离：打开本菜单 → 上浮撤离」与 DP L23「§8需要撤离：打开菜单 → 深渊 → 撤离 …」开头相同、意思重复，玩家成功进本时会先后看到两次；tip §6 已明确不改 L78。

## 依赖检索（第 5 点）

- 菜单本身：`rg -P '(?<!ticket_)ember_abyss(?!_)' mineflayer-tests` 0 命中——没有脚本打开深渊菜单或点 `S`，所以没有脚本依赖 L77（新旧都不依赖）。
- `abyss-*` 脚本逐个看：
  - `abyss-calamity-supplement.js:58,62,66`：发深渊票 + `/dp start EmberAbyss`，判定 `/灰烬潮|深渊已开启|第 1 层/`（DP L22 仍含「深渊已开启」，不受影响）。
  - `abyss-reload-smoke.js:13,17-18`：`/trmenu reload`、发票、`/dp start EmberAbyss`。
  - `abyss-evacuate-reload.js:16,26`：`/trmenu reload`、`/corerpg abyss evacuate`。
  - `abyss-followup.js:23,28,53,67`：只开 `ember_pet` / `ember_bestiary` 菜单，与深渊进本无关。
  - 以上都不经过 `ember_abyss` 菜单，也不匹配「体力 30」「等级不足不扣」。
- 其它：`gates-smoke.js:22-23` 用 `/dp start EmberAbyss`，匹配的是 DP L19 的「需要余烬等级 Lv.25」，不是菜单句。
- 非 docs 全仓：「等级不足不扣」0 命中；「体力 30」只在 6 个日常 DP `option.yml:2` 注释（EmberDailyAsh / Crypt / Tide / Spire / Frost / Rail），不对玩家，与深渊无关；「尝试下潜」只在 L77 本身。
- 票时代 / 过期步骤（`abyss-reload-smoke.js:17-18`、`abyss-calamity-supplement.js:58,62`、`gates-smoke.js:22-23`、`dungeon-balance.js:16-17`、`ticket-grant-smoke.js`）在 B2.100 已列 B2.109，与本窗无关，不重复计新。

## 前提更正 / 补充

1. **（稿 §5、总控批注）exit 5 的语义：**稿写「`/Icons/S/actions/all/3` 不是 `command: corerpg enter abyss` **或前后不一致** exit 5」，总控批注写「all/3 非 enter abyss exit 5」。脚本 L87 先要求差异恰为 `all/1`，所以子侧一改 L79 就先 exit 1（变异实测：exit 1）；L90 的「C[K]!==A[K]」半句不可达。exit 5 只在父子两侧 `all/3` 相同且都不是 enter abyss 时触发（实测两例 exit 5）。改动都被拦住，不挡 PASS；与 B2.110 的问题相同，稿子沿用了同一说法。
2. **（稿 §2 表末行）DP 行号：**稿写「DP L16 人数 1～2、L19 队员等级门」。`EmberAbyss/option.yml:16` 是 `condition:` 键，人数门 `$team-condition{…min=1;max=2…}` 在 **L17**；L19 等级门正确。
3. **（稿 §2 表末行 / 总控批注）「先扣后 `refundEnter` 退还」偏笼统：**发起者在 40 tick 内下线时不退（`TicketEntryService.java:189`），5.5 秒内重试时退了但不提示（`:209`）。见「私聊核对」缺口 1、2。
4. **（稿 §2 L38）灰显：**结论正确（实测见 `docs/status/STATUS-ember-enter-stamina-gray-test.md:50-51`）；补充 L41 不区分 OP，与总控批注一致。
5. **tip 其余行号核对：**`tryEnter` L100-203、`consumeForEnter` L293-327、周免三支 L301 / L306 / L311、等级 L109-113、体力 Stamina L319-322、L140 / L144、`tellEnterFailed` L205-211、`CoreRpgExpansion` L102 `stamina_cost_abyss`、L110-124 `stamina_blocked_*`、菜单 L40 注释、L41 条件、灰显 L40-54、lore L71、DP `option.yml:3` 注释「深渊无首免」、DP L19、L22-23、§7 `rg -n 30` 命中行、L66 / L90 / L98 / L134「不退体力」——都与现文件一致；等级私聊原文 `§c余烬深渊需要余烬 Lv.25§7（当前 Lv.x）` 与 L110-112 拼接结果一致。
6. **时间戳：**tip L6「总控 13:26 派单」→ 设计 `227bec9` 13:27:45 → tip STATUS 与总控批注写「13:28 批 A」，批准 `4e0c11a` 13:28:38 → 施工 `4cd3be3` 13:29:07（批准后 29 秒）；`/tmp/chk-b2111.js` mtime 13:29:07.10，与施工 commit 同秒（施工岗落盘）。顺序正确。

## 范围外备注（不在本窗）

- **B2.120**（总控已排，B2.118 后）：同文件 L49 / L54 / L130 / L139 写死 30 → `%corerpg_stamina_cost_abyss%`；本窗已核逐字节未动。
- **B2.121**（总控已排，B2.120 后）：L132「硬顶建议 ≤2/日（免费+购）」票制旧口径；本窗已核未动。
- L41 灰显条件 `%corerpg_stamina% < 30` 不区分 OP、写死 30：需新增 `stamina_blocked_abyss`（build），总控只记。
- L78 撤离句与 DP L23 重复（见上），tip §6 明确不动，可在后续窗口一并考虑。
- 「私聊核对」缺口 1-3 属 CoreRpg `TicketEntryService`，与 tip 排期中的 B2.119 同类。

## 新发现的 B2.109 项

1. 补一个深渊菜单点击用例：打开 `ember_abyss`、点 `S`，断言聊天有「[深渊] 尝试下潜……（需余烬 Lv.25）」、没有「体力 30」「等级不足不扣」，并分别检查发起者等级不足 / 付费 / OP 三种私聊；再加「队员等级不足」用例，断言发起者体力先扣后退、队员体力不变（即本报告第 7 项的自动化）。
2. 既有过期项（B2.100 已记，不计新）：`abyss-reload-smoke.js:17-18`、`abyss-calamity-supplement.js:58,62`、`gates-smoke.js:22-23`、`dungeon-balance.js:16-17`、`ticket-grant-smoke.js`。

## 旁证

1. **施工范围：**`4cd3be3` 只改 `ember_abyss.yml` 一行 tell 的值；解析后只有 `/Icons/S/actions/all/1` 变化；动作数（5）、顺序、L78-L80、灰显子图标、lore、`E`、`P`、`R` 全同。
2. **精确行文：**live、施工 tip、新增行与 tip §4 sha256 相同；旧行与 tip §1 相同。
3. **口径：**未写费用与数字；未写「票已废」「B0.1 已清」；NI 票物、数值、DP 配置、CoreRpg 代码、cash.yml 未动。
4. **做法纪律：**仅静态 git / sed / diff / cmp / node js-yaml / python / rg；未起服、未 reload、未长测、未结束任何进程；主仓未改任何文件（只新增本报告）；未覆盖 `/tmp/chk-b2111.js`（另生成的 `/tmp/b2111-regen/`、`/tmp/b2111-mut/`、`/tmp/b2111-old.yml`、`/tmp/b2111-new.yml` 为一次性临时文件，不入库）；机密未回显。

## 阻塞点

无（第 7 项实服观察待恢复服后实测）。

## 回报摘要

- 总评：PASS
- 各点：0 PASS / 1 PASS / 2 PASS（附 exit 5 语义更正）/ 3 PASS / 4 PASS（附私聊缺口与 OP 灰显标注）/ 5 PASS / 6 PASS / 7 SKIP（待恢复服后实测）
- 前提更正：子侧改 `all/3` 由 exit 1 拦下，exit 5 只在两侧同错时触发；DP 人数门在 `option.yml:17` 不是 L16；退还在发起者下线时不执行、5.5 秒内重试不提示
- 施工 tip SHA：`4cd3be3`
- 测报 tip short SHA：待本地 commit 后回报
- 是否已 push：否
- 报告路径：`docs/tests/TEST-B2.111-abyss-menu-enter-tell.md`
- 阻塞点：无
- ahead：相对 `origin/main` ahead 1（未 push）
- `ops.json`：`[]`
