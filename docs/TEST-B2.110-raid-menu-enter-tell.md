# B2.110 · TrMenu `plugins/TrMenu/menus/ember_raid.yml` L76-L77 进本按钮 tell「团本已点燃 / 消耗 §e50 §8体力」→「[团本] 尝试进入…… / 人数 3～5」· 纯静态薄验收

- **总评：PASS**（reload / 实服观察项记 SKIP：待恢复服后实测，不挡 PASS）
- **测报时间：**2026-10-01 13:21 CST（`date`）
- **岗别：**余烬-测试岗（B2.110 纯静态薄验收）
- **开工前：**`git status --porcelain` 空（干净）；`git pull --ff-only` → `Already up to date.`（rc 0；本地未超前、未分叉；上一批测报 `13771d1` 已在 `origin/main` 中，HEAD = origin/main = `22b1084`）；复读 `HANDOFF.md`（§8 查密码、`ops.json` 必须 `[]`、提交身份、杀进程用 PID）与 `docs/LESSONS-ember-pipeline.md`（以磁盘 + reload 为准；只记 PASS/FAIL/SKIP），两文件自 `2e5649f`（2026-09-27 21:37 CST）后未改。
- **工作区：**`/workspace/minecraft`（main）
- **施工 tip SHA：**`22b1084`（2026-10-01 13:16:06 CST）
- **设计 / 批准：**`4aff2ee`（13:14:43 CST）/ `a63f4ac`（13:15:31 CST）
- **tip：**`docs/design-ember-raid-menu-enter-copy.md`（§1 完整旧行、§2 代码核对表、§3 DP 重复核对、§4 荐案、§5 验收与 heredoc、§6 明确不做、§7 同文件写死「50」、文末总控批注）
- **改动性质：**玩家可见菜单点击 tell（聊天栏，仅发给点按钮的人）
- **是否已 push：否**（本岗仅本地 commit，禁止 push）
- **报告路径：**`docs/TEST-B2.110-raid-menu-enter-tell.md`
- **ahead：**相对 `origin/main` ahead 1（本测报本地 commit 后，未 push）
- **`server-runtime/ops.json`：**`[]`（未改动）
- **本机服状态：**25565/25566/25567/3306 均未监听（`ss -ltn` 计数 0）；未起服、未结束任何进程

## 各点

| # | 条件 | 结果 |
|---|------|------|
| 0 | 三个 commit 时间顺序；设计 / 批准只改文档 | **PASS** · 设计 `4aff2ee` 13:14:43 → 批准 `a63f4ac` 13:15:31 → 施工 `22b1084` 13:16:06（committer = author 时间，CST）；三者均为 HEAD 祖先；`4aff2ee`、`a63f4ac` 均只改 `docs/design-ember-raid-menu-enter-copy.md` 与 `docs/design-ember-content-backlog.md`，对 `plugins/` 的 stat 为空；`4aff2ee^..HEAD` 触及 `plugins/`、`CoreRpg/`、`mineflayer-tests/` 的只有 `22b1084` |
| 1 | `22b1084` 只改 L76、L77，numstat `2 2`；旧行 = §1；新行 = §4；8 空格、单引号；整文件字节级无 tab/CR/行尾空格；L78–L80、L22、L109、L118 未动 | **PASS** · numstat `2	2	plugins/TrMenu/menus/ember_raid.yml`；hunk `@@ -76,2 +76,2 @@ Icons:`，`diff` 仅 `76,77c76,77`，删去 L76-77 后其余行逐字节相同；旧两行 = tip §1 块 = 施工删除行，新两行 = tip §4 块 = 施工新增行 = live = HEAD（sha256 见下）；四行缩进均 8 空格，均以 `- '` 起、`'` 止；整文件 3559 字节 118 行、末尾有换行，字节级 0x09 = 0、0x0D = 0、以 ASCII 空白结尾的行 0、以任意 Unicode 空白结尾的行 0（`cat -A` 每行都以 `$` 收尾；多字节字符显示为 `M-` 序列，不作判据）；L22/L78/L79/L80/L109/L118 在父版本、施工版、live 三处逐字节相同 |
| 2 | `/tmp/chk-b2110.js` 与稿内 heredoc 一致；`22b1084` → `only-L76-L77 …` exit 0；旧对旧 / 新对新 exit 1；临时仓变异；独立 deep diff | **PASS（附 exit 5 语义更正，见「前提更正 1」）** · `/tmp/chk-b2110.js` 已存在（1475 字节，mtime 13:16:05.96，box 用户，比施工 commit 早 0.04 秒），与 tip §5 ```sh 块 heredoc 正文逐字节相同（sha256 `e2e27db0…d154f633f4`；块内缩进行 0，无需去 markdown 缩进）；按稿重跑 heredoc（只改落盘路径）产物 `cmp` 一致；**未覆盖**。`22b1084` → `only-L76-L77 /Icons/S/actions/all/1 /Icons/S/actions/all/2` exit 0；旧对旧 `a63f4ac`、`4aff2ee` → `[]` exit 1；新对新 `WT` → `[]` exit 1；无参 exit 9。临时仓变异 10 例全部被拦或按预期通过（见命令区）；独立 deep diff 仅 `["/Icons/S/actions/all/1","/Icons/S/actions/all/2"]` |
| 3 | `rg 已点燃\|消耗 §e50\|体力 -\|已消耗体力\|扣票\|团本票` 0；「B0.1 已清」「票已废」0 | **PASS** · 原样 rg 无输出 rc=1（父版本只命中 L76、L77，与稿 §5「施工前只命中 L76、L77」一致）；`rg -F "B0.1 已清"`、`rg -F "票已废"` 均 0；稿 §7 `rg -n 50` 施工前 L2/22/77/91/109/118，施工后 L2/22/91/109/118（只少了 L77） |
| 4 | tryEnter / consumeForEnter 各情况都有私聊；灰显子图标接管；不与 DP L21/L22 重复；无过度承诺 | **PASS（附边界标注）** · 见下「私聊核对」「灰显接管」「重复与承诺」 |
| 5 | 无 mineflayer 脚本 / 非 docs 文件依赖旧「团本已点燃」「消耗 50」 | **PASS** · 没有任何 mineflayer 脚本点击 `ember_raid` 菜单（`rg ember_raid mineflayer-tests` 0 命中）；脚本里的「团本已点燃」等的是**旧 DP L21**（`553efc4` 2026-09-27 已改为「团本大厅已集结」），与本窗菜单 tell 无关，但已过期，列入 B2.109；非 docs 的「消耗 50 / 消耗 §e50」只剩 `ember_raid.yml:22` 与 `ember_hub.yml:280` 两处配置本身（已分别排 B2.118 / B2.113），不是依赖 |
| 6 | HANDOFF §8 命令计数 0；报告自身 0；`ops.json` `[]` | **PASS** · 原样执行（`HANDOFF.md:140-141`）输出 `0`，变量非空 `true`（未打印值）；commit 前（本报告为未跟踪文件，已被该命令纳入）再跑仍为 `0`，另对本报告 `grep -cF` 为 `0`；`ops.json` 为 `[]` |
| 7 | reload / live：Lv.35 以下非 OP、周首免、OP 各点一次「开始协作」，聊天只见新两行 + L78 + 对应插件私聊 | **SKIP · 待恢复服后实测**（本机无运行中的服；总控批注明确不挡 PASS） |

## 目标行原文

```
# 父版本 22b1084^ L76-L77（旧；= tip §1）
        - 'tell: §9团本已点燃。§7分路推进——左卫兵、右射手，汇合后再闯终厅。'   sha256 e6b1dac56609a8909fc390fe2da26f26b0bc4d32fdfcdd2f46c688591dc0eb20（108 字节）
        - 'tell: §8消耗 §e50 §8体力 · 人数 3～5'                                  sha256 96e6ddbad0fde4579ed7fbfefee592e199842bb154f27dba9f466529f51515c0（59 字节）
# live / 22b1084 L76-L77（新；= tip §4 荐案）
        - 'tell: §9[团本] §7尝试进入……'                                           sha256 7c349bc05a3ac5aa84426e0bcdc0023742213ef01e1b4f849e1062c6607f7fe1（51 字节）
        - 'tell: §8人数 3～5'                                                      sha256 1cad34b895e3477033db65b2801c77f1cbade3e6d2dbfc0b9f4ef52e26e3dca5（33 字节）
（sha256 针对不含换行的整行字节，含行首 8 空格）

# live Icons.S.actions.all（L74-L80，解析后）
["sound: BLOCK_NOTE_PLING-1-2",
 "tell: §9[团本] §7尝试进入……",            ← /Icons/S/actions/all/1（L76）
 "tell: §8人数 3～5",                      ← /Icons/S/actions/all/2（L77）
 "tell: §8需要余烬等级 §eLv.35§8（见菜单等级要求）",   ← L78 未动
 "command: corerpg enter raid",            ← /Icons/S/actions/all/4（L79）未动
 "close"]                                  ← L80 未动

# 未动的同文件行
L22  : - 'tell: §9[团本] §7需 3～5 人 · 消耗 50 体力（本周首次免费）'
L109 : - '§7本周首次免费 · 其后 50 体力'
L118 : - 'tell: §9团本本周首次免费，其后 50 体力；需 3～5 人。'
```

## 命令与输出

```
# 0
git pull --ff-only                       → Already up to date.（rc 0）
git merge-base --is-ancestor 13771d1 origin/main → 是
git show --stat 4aff2ee → docs/design-ember-content-backlog.md | 8 +-, docs/design-ember-raid-menu-enter-copy.md | 121 +
git show --stat a63f4ac → docs/design-ember-content-backlog.md | 10 +-, docs/design-ember-raid-menu-enter-copy.md | 9 +-
git show --stat 22b1084 → plugins/TrMenu/menus/ember_raid.yml | 4 ++--

# 1
git show --numstat --format= 22b1084     → 2	2	plugins/TrMenu/menus/ember_raid.yml
git diff -U0 22b1084^ 22b1084 | grep @@   → @@ -76,2 +76,2 @@ Icons:
diff <(父 | sed 76,77d) <(施工 | sed 76,77d) → 无差异
cmp HEAD:file  live  且  HEAD:file  22b1084:file → 相同
python 字节统计：old==§1 True / new==§4 True / 删除行==旧 True / 新增行==新 True / live、HEAD==新 True
                 bytes 3559 · lines 118 · 0x09 0 · 0x0D 0 · 行尾空白（ASCII / Unicode）均 []

# 2（/tmp 脚本身份）
稿 §5 ```sh 块：1 个；块内以空白开头的行 0
heredoc 正文 1475 字节 sha256 e2e27db01bb7244092a27c8e8e4be88fadc847354d29d14f6104dad154f633f4
/tmp/chk-b2110.js 1475 字节 sha256 e2e27db01bb7244092a27c8e8e4be88fadc847354d29d14f6104dad154f633f4 → 逐字节相同
按稿重跑 heredoc 到 /tmp/b2110-regen/chk.js → cmp 与 /tmp/chk-b2110.js 一致（未覆盖原文件）

node /tmp/chk-b2110.js 22b1084   → only-L76-L77 /Icons/S/actions/all/1 /Icons/S/actions/all/2    exit 0
node /tmp/chk-b2110.js a63f4ac   → []   exit 1      （旧对旧）
node /tmp/chk-b2110.js 4aff2ee   → []   exit 1      （旧对旧）
node /tmp/chk-b2110.js WT        → []   exit 1      （新对新：HEAD 对工作区）
node /tmp/chk-b2110.js           → usage             exit 9

# 变异（/tmp/b2110-mut 一次性临时仓，逐提交构造父/子；与主仓无关）
| 例 | 构造（父 → 子） | 实际 exit | 稿 §5 预期 | 判定 |
|---|---|---|---|---|
| 新值错 L76 | 旧 → 新但 L76「尝试进入……」改「尝试进入。」 | 4 | 4 | 拦住 |
| 新值错 L77 | 旧 → 新但 L77「3～5」改「3-5」 | 4 | 4 | 拦住 |
| 旧值错 | 父 L76「团本已点燃」改「团本已开启」→ 新 | 3 | 3 | 拦住 |
| 多改一行 | 旧 → 新 + L78 Lv.35→Lv.36（diff 打印 all/1、all/2、all/3） | 1 | 1 | 拦住 |
| 漏改一行 | 旧 → 只改 L76（diff 打印 all/1） | 1 | 1 | 拦住 |
| all/4 子侧改 | 旧 → 新 + L79 `enter raid`→`enter weekly`（diff 打印 all/1、all/2、all/4） | **1** | 稿写 5（「不是 enter raid 或前后不一致」） | 拦住，但退出码是 1 不是 5 |
| all/4 两侧都错 | 父、子 L79 均为 `enter weekly`，其余为正常施工 | 5 | 5 | 拦住 |
| 动作插入一项 | 旧 → 新 + L78 前插入一条 tell（all/3…all/6 全变） | 1 | 1 | 拦住 |
| 两侧都插入同一项 | 父、子都在 L78 前插入同一条 tell（all/4 两侧均为该 tell） | 5 | 5 | 拦住 |
| 对照 | 旧 → 新（正确施工） | 0 | 0 | 通过 |

# 独立 deep diff（js-yaml；递归比较类型、数组长度、对象键序与叶子值）
changed: ["/Icons/S/actions/all/1","/Icons/S/actions/all/2"]
新文件空对象 / 空数组：0 个（无 `{}` / `[]` 叶）
Icons.S 键序：icons,update,refresh,display,actions（父子相同）

# 3
rg "已点燃|消耗 §e50|体力 -|已消耗体力|扣票|团本票" plugins/TrMenu/menus/ember_raid.yml → 无输出 rc=1
（父版本同命令 → 仅 76:、77:）
rg -F "B0.1 已清" → 0 ； rg -F "票已废" → 0
rg -n 50：父 2 22 77 91 109 118 → 施工后 2 22 91 109 118

# 6（HANDOFF §8 原样；grep -c 只出计数）
set -a; source secrets/mysql-ember.env; set +a
(git diff; git ls-files -o --exclude-standard | xargs -r cat) | grep -c -- "$MYSQL_PASSWORD"   → 0
（在子 shell 中执行，变量不留在会话里；变量非空 true；commit 前含本报告再跑仍为 0）
cat server-runtime/ops.json → []
```

## 私聊核对（第 4 点；Java 路径前缀 `CoreRpg/src/main/java/town/sunshine/corerpg/`）

调用链：菜单 `ember_raid.yml:79` `command: corerpg enter raid` → `CoreRpgPlugin.java:842-843` → `TicketEntryService.cmdEnter`（`TicketEntryService.java:226-242`，`Kind.parse` 后 L241 `tryEnter`）→ `tryEnter`（L100-203）。`Kind.RAID` = `("raid","EmberRaid","ticket_ember_raid","余烬团本","raid","团本")`（L29）。只校验、只扣**发起者**；队员人数与等级由 DP `option.yml:16`、`:18` 判定。

| 情况 | 发送语句（file:line） | 玩家看到的原文 | 扣 / 退 |
|---|---|---|---|
| 等级不足（非 OP） | `TicketEntryService.java:109-113`（`need` 来自 `ProgressService.gateLevel`，`progress.yml:145 raid: 35`；`ProgressService.java:133` 默认同值） | `§c余烬团本需要余烬 Lv.35§7（当前 Lv.x）` | 不扣，直接 return |
| 体力不足（非 OP，无周免） | `StaminaService.java:319-322` 组字串，`TicketEntryService.java:131-134` 发送 | `§c体力不足（需 50，当前 x/上限）· 明日 0 点恢复` | 不扣 |
| 体力服务未就绪（非 OP） | `TicketEntryService.java:126-129` | `§c体力服务未就绪` | 不扣 |
| 周首免 | `StaminaService.java:311-315` 抵扣 `weeklyGrantCreditRaid`（每周由 `:205-214` `ensureWeekCredits` 置 1）；`TicketEntryService.java:141-142` + `:148-149` | `§e[团本] §7正在进入……（本周首次免费）` | 抵 1 次周免 |
| 付费 | `StaminaService.java:324-326`（`costOf(raid)`：`:86` 默认 50，live/src `cash.yml:28 raid: 50`）；`TicketEntryService.java:143-144` + `:148-149` | `§e[团本] §7正在进入……（体力 -50）` | 扣 50 |
| OP / `corerpg.admin` | `TicketEntryService.java:103`、`:139-140` + `:148-149`（跳过 L106-122 等级门与 L125-136 扣费） | `§e[团本] §7正在进入……（管理免扣）` | 不扣 |
| cost ≤ 0（仅配置改成 0 时） | `StaminaService.java:316-317`；`TicketEntryService.java:145-146` | `§e[团本] §7正在进入……（无消耗）` | 不扣（现配置 50，不触发） |
| `dp start-console` 返回 false | `TicketEntryService.java:167-173` → `refundEnter`（`StaminaService.java:329-345`，周免加回或体力加回）→ `tellEnterFailed`（L205-211） | `§e出本后再进约等 5 秒（缓存冷却），不是进本坏了`；非 5.5 秒内重试时再加 `§7若仍进不去，请稍后再试（体力已退还，若已扣）` | 退还 |
| dispatch 成功但 40 tick（约 2 秒）后不在副本（含 DP 人数 3～5 拒、队员等级拒、DP 冷却） | `TicketEntryService.java:186-201`（`ENTER_VERIFY_TICKS = 40L`，L82）→ 同上 | DP 先发自己的红字（`option.yml:16` `§c团本人数 3～5，当前 (<size>)` / `:18` `§c团本需要余烬等级 §eLv.35§c · 队伍中有人等级不足 · …`，DP 源码不在仓库，属配置层核对）+ 同上两句 | 退还 |

结论：等级不足、体力不足、周首免、付费、OP、DP 启动失败退还六种情况，插件都有私聊，菜单不写扣费不会让玩家漏掉扣费结果。

**私聊有缺口的情形（标注，均非本窗引入，不挡 PASS）：**

1. **5.5 秒内重试被退还：**`recent` 为真时（L153-156、L209）只发「出本后再进约等 5 秒……」，**不发**「体力已退还」那句——实际已退，但玩家不会被告知。
2. **40 tick 校验时发起者已下线：**L189 `if (p == null || !p.isOnline()) return;` 在 L194-195 `refundEnter` **之前**返回——既不退还也不提示（下线无法私聊；但"不退还"是行为缺口，与 tip §2「DP 启动失败 → refundEnter 退还」的笼统说法不符）。
3. **周免被退还时措辞：**`tellEnterFailed` 只说「体力已退还，若已扣」，周首免情形退的是周免次数（`StaminaService.java:333-340`），措辞不精确。
4. **人数 / 等级被 DP 拒时措辞：**CoreRpg 在 DP 红字之后仍补「出本后再进约等 5 秒（缓存冷却），不是进本坏了」，把人数不足说成缓存冷却，可能误导（`STATUS-ember-enter-stamina-gray-test.md:69` 实测记「人数 1<3 拒开本并退还」）。
5. **成功进本：**发起者只见 L148-149 一句，随后全队见 DP L21/L22，无缺口。

## 灰显接管（第 4 点续）

- `ember_raid.yml:39-58`：`S.icons[0]` = `condition: 'check papi %corerpg_stamina_blocked_raid% > 0'`（L42）、`priority: 2`（L43）、`inherit: true`（L44），自带 `display`（L45-51 灰玻璃板「开始协作 · 体力不足」）和 `actions.all`（L52-55，只有 sound + tell「§c体力不足，需 %corerpg_stamina_cost_raid% 点体力（本周免费已用完）；每日 0:00 回满。」，**无** `corerpg enter raid`）；`update: 20` / `refresh: 20`（L57-58）。默认 `S.actions.all` 在 L74-80。
- `CoreRpgExpansion.java:115-119`：`stamina_blocked_raid` 有周免 → `"0"`；否则 `stamina < costOf("raid")` → `"1"`。即「无周免且体力 < 50」才灰。
- TrMenu 源码不在仓库，子图标接管点击以实测为据：`docs/STATUS-ember-enter-stamina-gray-test.md:64-71`（测 `6bda99e`，施工 `23a816e`，CoreRpg 1.15.23）团本行「`开始协作 · 体力不足` gray pane · tell 需 50 · 无 enter」PASS。
- 所以走到 L76-L79 的人是：等级不足、周首免、付费、OP、人数 / 队员等级不对，以及 20 tick 刷新间隙里刚变成体力不足的人（由 L131-134 私聊兜底）。与 tip §2 L34 一致。
- **边界（标注）：**`blocked_raid` 不判 OP。OP 无周免且体力 < 50 时也被灰显拦下，从菜单拿不到「管理免扣」，需用 `/corerpg enter raid`。与本窗无关。

## 重复与承诺（第 4 点续）

- 新 L76「§9[团本] §7尝试进入……」：不说已开、不说扣费、不说路线；前缀 `§9[团本]` 与 `ember_raid.yml:22` Open tell 同写法；句式同 `ember_abyss.yml:77`「§5[深渊] §7尝试下潜……」。紧接着插件私聊「§e[团本] §7正在进入……（…）」，两句意思接近但不矛盾（尝试 → 正在），深渊菜单是同样的模式。
- 新 L77「§8人数 3～5」：对应 DP `option.yml:16` `min=3;max=5`；与 L22 Open tell「需 3～5 人」、lore L70「人数 §f3～5」一致。
- 与 DP `EmberRaid/option.yml:21`「§9团本大厅已集结。§7左道卫兵 · 右道射手 · 汇合后终厅使徒。」、`:22`「§8通关箱 · 全队每人结算 · 周首通保底团戒」**不重复**：新句不含路线、不含奖励、不含「已集结 / 已点燃」。旧 L76 的路线提示与 L21、lore L68「大厅集结 → 左道卫兵 → 右道射手 → 汇合 → 终厅」重复，已去掉。
- 过度承诺：新两行只有「尝试进入」和「人数 3～5」两个说法，都能在 `TicketEntryService.java:159-162`（发 `dp start-console`）和 `option.yml:16` 找到依据；没有费用、首免、奖励、成功的说法。**PASS**。
- 范围外：L78「需要余烬等级 Lv.35（见菜单等级要求）」= `progress.yml:145`，对 OP 也照发（OP 实际免等级），无害，稿已明确只记不动。

## 依赖检索（第 5 点）

- `rg ember_raid mineflayer-tests` 0 命中：没有脚本点开团本菜单或点击 `S`，所以没有脚本依赖 L76/L77（无论新旧）。会点菜单的脚本（`mainline-smoke.js`、`audit-fix-smoke.js` 用 `clickWindow`）里和 raid 有关的只有 `corerpg progress … raid_clear` / `raid claim-ring`，与本窗无关。
- 「团本已点燃」非 docs 命中（都是旧 **DP L21**，不是菜单 tell；`git log -S` 显示 `553efc4`（2026-09-27 22:16 CST）把 DP L21 从「§9团本已点燃。§7分路推进……」改成「§9团本大厅已集结。…」，菜单 L76 当时保留了旧句，直到本窗）：
  - `mineflayer-tests/raid-combat-smoke.js:153-154`（等进本标志）、`:170`（`enter:` 判定）——用 `/dp start EmberRaid` 直开（L132、L150），绕过 CoreRpg `tryEnter` 与体力，还发票（L131、L144）、查「已扣除余烬团本票」（L155）。**已过期**。
  - `mineflayer-tests/abyss-followup.js:58`：`/团本已点燃|创建完毕/`，同样 `/dp start EmberRaid`（L53）+ 发票（L47）。**已过期**。
  - `mineflayer-tests/killany-live-retest.js:122`：正则备选里含「团本已点燃」「已扣除余烬团本票」，另有「团本大厅已集结 / 正在进入」兜底，不会误判，但备选过期。
  - 根目录 `STATUS-ember-raid.md:28` 引用旧 DP L21 原文（状态文档，不是依赖）。
- 其它「已点燃」都属盟约周 Boss（`plugins/DungeonPlus/dungeon/EmberGuildBoss/option.yml:20`「§6盟约周 Boss 已点燃。」）：`gate-drops-smoke.js:45`、`guildboss-refund-smoke.js:32,34`、`guild-boss-smoke.js:88`、`guildboss-gate-smoke.js:62,67,76,112`、`killany-live-retest.js:332`——与团本无关，不受影响。
- 「消耗 50 / 消耗 §e50」非 docs 命中只有配置本身：`plugins/TrMenu/menus/ember_raid.yml:22`（B2.118）、`plugins/TrMenu/menus/ember_hub.yml:280`（B2.113）；无脚本依赖。
- docs 中「团本已点燃」出现在 5 个文件里，都是历史引用，不算依赖。

## 前提更正 / 补充

1. **（稿 §5、总控批注、简报）exit 5 的语义：**稿写「`/Icons/S/actions/all/4` 不是 `command: corerpg enter raid` **或前后不一致** exit 5」，简报写「/Icons/S/actions/all/4 不再是 enter raid（应 exit 5）」。实际脚本在 L85 先判「差异路径集合必须恰为两条」，子提交一改 L79，`all/4` 就进了差异集合，**先 exit 1**；L87 的 exit 5 只在父子两侧 `all/4` **相同且都不是** enter raid 时才会触发（例如父版本本来就错，或两侧都插入了同一条动作）。变异实测：子侧改 L79 → exit 1；两侧都改 → exit 5。改动都被拦住，脚本结论可靠，只是「前后不一致 → 5」这一分支不可达，不挡 PASS。
2. **（tip §2 表末行）「DP 启动失败 → `refundEnter` 退还体力/周免」偏笼统：**发起者在 40 tick 内下线时不退还（`TicketEntryService.java:189`）；5.5 秒内重试时虽退还但不提示「已退还」（`:209`）。见「私聊核对」缺口 1、2。
3. **（tip §4）「句式参照 `ember_abyss.yml` L77「§5[深渊] §7尝试下潜……」」：**L77 全文是「§5[深渊] §7尝试下潜……（需余烬 Lv.25 · 体力 30；等级不足不扣）」，后半仍写死体力 30（B2.100 已记）。稿只引了前缀，作为句式参照没问题。
4. **（tip §2 L34、总控批注）灰显接管：**结论正确；补充一点：`blocked_raid` 不判 OP（`CoreRpgExpansion.java:115-119`），所以 OP 在「无周免 + 体力 < 50」时从菜单拿不到「管理免扣」。
5. **旧句来源：**`raid-combat-smoke.js` 等脚本里的「团本已点燃」来自旧 DP L21（`553efc4` 前），不是菜单 L76；B2.99 记的脚本过期与本窗无关，本窗也不会让它们更坏。
6. **tip 其余行号核对：**`tryEnter` L100-203、`consumeForEnter` L293-327、等级 L109-113、体力 L131-134 / Stamina L319-322、周免 Stamina L311-315、L140/L142/L144/L148-149、`tellEnterFailed` L205-211、灰显子图标 L42-55、lore 路线 L68、L66 / L111「失败不返还」、§7 `rg -n 50` 命中行——都与现文件一致；等级私聊原文 `§c余烬团本需要余烬 Lv.35§7（当前 Lv.x）` 与 L110-112 拼接结果一致；`§e[团本]` 前缀（`ChatColor.YELLOW`，L148）一致。
7. **时间戳：**tip L6「总控 13:12 派单」→ 设计 `4aff2ee` 13:14:43 → tip STATUS 与总控批注写「13:15 批 A」，批准 `a63f4ac` 13:15:31 → 施工 `22b1084` 13:16:06（批准后 35 秒）；`/tmp/chk-b2110.js` mtime 13:16:05.96（施工岗落盘，比施工 commit 早不到 0.1 秒）。顺序正确。tip §5「策划已干跑：以 `WT` 为参数」在设计 commit 前，合理。

## 范围外备注（不在本窗）

- **B2.118**（总控已排，B2.113 之后）：同文件 L22、L109、L118 写死「50」→ `%corerpg_stamina_cost_raid%`；本窗已核逐字节未动。
- L66「失败不返还」、L111「进本即扣 · 失败不返还」：稿判口径准确、不动；补一句，L111「进本即扣」在周首免时不扣，严格说不精确，可随 B2.118 一起看。
- 「私聊核对」缺口 1-4（退还提示、下线不退、周免措辞、人数拒被说成冷却）属 CoreRpg `TicketEntryService`，不在 TrMenu 文案窗口，建议总控另行评估。

## 新发现的 B2.109 项

1. `mineflayer-tests/abyss-followup.js:58`：团本开本判定等旧 DP L21「团本已点燃」，并用 `/dp start EmberRaid`（L53）+ 发票（L47）——改成走 `/corerpg enter raid`，匹配「正在进入」/「团本大厅已集结」，去掉发票。
2. 补一个团本菜单点击用例：打开 `ember_raid`、点 `S`，断言聊天有「[团本] 尝试进入……」「人数 3～5」，没有「已点燃」「消耗 50」，并按等级不足 / 周首免 / 付费 / OP 检查对应私聊（即本报告第 7 项实测的自动化）。
3. （B2.99 已记，重申）`raid-combat-smoke.js:153-155,170` 等「团本已点燃」「已扣除余烬团本票」、`/dp start` 直开与发票；`killany-live-retest.js:122` 过期备选。

## 旁证

1. **施工范围：**`22b1084` 只改 `ember_raid.yml` 两行 tell 的值；解析后只有 `/Icons/S/actions/all/1`、`/all/2` 变化；动作数（6）、顺序、L78-L80、灰显子图标、lore、`P`、`R`、Open 事件全同。
2. **精确行文：**live、施工 tip、新增行与 tip §4 sha256 相同；旧行与 tip §1 相同。
3. **口径：**未写费用与数字；未写「票已废」「B0.1 已清」；NI 票物、数值、DP 配置、CoreRpg 代码、cash.yml 未动。
4. **做法纪律：**仅静态 git / sed / diff / cmp / node js-yaml / python / rg；未起服、未 reload、未长测、未结束任何进程；主仓未改任何文件（只新增本报告）；未覆盖 `/tmp/chk-b2110.js`（另生成的 `/tmp/b2110-regen/`、`/tmp/b2110-mut/` 为一次性临时文件，不入库）；机密未回显。

## 阻塞点

无（第 7 项实服观察待恢复服后实测）。

## 回报摘要

- 总评：PASS
- 各点：0 PASS / 1 PASS / 2 PASS（附 exit 5 语义更正）/ 3 PASS / 4 PASS（附私聊缺口与 OP 灰显标注）/ 5 PASS / 6 PASS / 7 SKIP（待恢复服后实测）
- 前提更正：子侧改 `all/4` 由 exit 1 拦下，exit 5 只在两侧同错时触发；`refundEnter` 在发起者下线时不执行、5.5 秒内重试不提示已退还；`blocked_raid` 不判 OP；脚本中的「团本已点燃」来自旧 DP L21
- 施工 tip SHA：`22b1084`
- 测报 tip short SHA：待本地 commit 后回报
- 是否已 push：否
- 报告路径：`docs/TEST-B2.110-raid-menu-enter-tell.md`
- 阻塞点：无
- ahead：相对 `origin/main` ahead 1（未 push）
- `ops.json`：`[]`
