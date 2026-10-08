# 余烬 · 六槽护甲 T2 演练 · 第一段报告（余烬-测试 · 2026-10-08）

- 计划：`docs/status/PLAN-ember-six-slot-t2-drill.md`（已批 D320）。本段 = 计划步 0–5：预检 / 构建全测 / 合成迁移前档 / H1 H2 H5 H6 / F G（菜单、reload）/ D（事件级 T1-6）。
- GUARD_SHA = `f3f1a065823a8c3f449dec1ab01b77f8ea8473e5`（守卫代码 `dc69ee23`，tip 另含 `docs/ops/OPS-ember-six-slot-migration.md`）。
- 执行时间：2026-10-08 10:27–11:08 UTC+8。测试服 `/workspace/tmp/t2-drill/`，证据都在 `/workspace/tmp/t2-drill/evidence/`（下文简写为 `ev/`）。
- **结论：本段 28 例，PASS 26，FAIL 1（G8，菜单层，不影响资产，归入「需修，可带条件过」），部分 1（D12：拒绝分解迁移件 PASS，掉落甲分解路径 SKIP）。不变量 ① ② 全程无违例**（唯一 ② 报警来自测试者用控制台 `clear` 删掉的圆石，属预期，见 §4）。没有阻塞项。

## 1 预检

| 项 | 结果 | 证据 |
|---|---|---|
| 构建 + 全测（worktree @f3f1a065，nice / 单线程） | 658 例 / 2 失败，与基线一致：只有 `EmberGrowthTest` D164、D165 两例已知失败 | `ev/build-test.log` |
| 产物 | CoreRpg.jar `801812d41461420a…`，其余插件、paper 的 sha256 见 `ev/artifacts.sha256` | |
| (a) invsnap 守卫只读记录 | `EmberSixSlotService.restoreGuard`（:144–150）只读 `p1-six/<uuid>.yml` 里的 flag、journal、done_at，不看 NBT 标签；调用点 `InvSnapService` :434（restore）、:478（preview）、:726（排队恢复，判定为永久拒绝时出队） | 代码 |
| (b) 孤儿换装标签 | `EmberSixMigration.run()` :216–217 `if (rec.flag) { if (rec.journal == null) tidyOrphanMarks(); … ALREADY }`。`tidyOrphanMarks` 在 :193–197 去掉标签，再做 checked 存档，日志写 `orphan swap mark cleared (n), saved` | 代码 |
| (c) 提交后补存档 | `commit()` 先 :412 `port.save(next)`（flag、待领、journal 一次写完），再 :414 `apply(null, mark, false)` 去掉标签，然后 :415 `persistInventory()`（`EmberVault.savePlayerFileChecked`）；存档失败时发告警 | 代码 |
| (d) 快照时间守卫 | `EmberSixRestoreGuard.check` :50 `doneAt <= 0` 永久拒绝；:51 `snapAtMs < (doneAt+1)*1000` 永久拒绝 | 代码 |

(a)–(d) 四项都在代码里，行为与计划断言一致。运行时行为（E4–E7、A 组 kill 点）留到第二段实测。

## 2 环境

- Paper 1.12.2（线上同款 jar），端口 25577；jdb 25578；临时 mariadbd 3317（独立 datadir 和 socket）。三者都只绑 127.0.0.1。
- `online-mode=false`，`bungeecord=false`，gamemode 0。不接 Waterfall。
- 配置取自 git f3f1a065。`ember-v1-item.key` 由测试服自行生成，没有拷线上密钥；没有拷真实玩家档，玩家都是合成号 t2a–t2e、t2op、t2peer。
- 只在测试服的 `ember-v1.yml` 里打开 `gear.six_slot.enabled` 和 `migrate` 两个开关。G8 那一轮临时关掉 enabled，测完已改回 true。
- 测试服堆 `-Xmx1536M`，SerialGC。后台 `memmon.sh` 每 5 秒记一次可用内存，写到 `ev/mem.log`。**本段可用内存最低 5459 MB**，从未触及 1.5 G 暂停线。
- 已知环境处理：
  - TrMenu 首次启动会去 Mojang 下载语言文件，网络不通会卡住。处理：把线上 `assets/` 只读拷进测试目录。
  - 平坦世界出生点改到 y=4。

## 3 结果

### H 组（迁移主路径）

| 例 | 结果 | 关键证据 |
|---|---|---|
| H1 迁移等价 | **PASS** | 5/5 MIGRATED（`ev/H1-log.txt`）。迁移前后 B、H、D、M、EHP 逐号完全相同（`ev/H1-pre-hd.jsonl` vs `ev/H1-post-hd.jsonl`，例如 t2a 前后都是 `B=51.66 H=139.70 D=10 EHP=174.6`）。flag=1，无 `ember_six_*` 标签，done_at 已写，原物全部在待领，DB 新增 20 行 migrate 且都是 active，① ② 无违例（`ev/check-H1.json`） |
| H2 幂等 | **PASS** | 重登 3 次，再执行 `armor mig`：5 个号都是 ALREADY ×5（`ev/H2-log.txt`），DB 行数不变（28），`ev/check-H2.json` 无违例 |
| H5 migrate 关 | **PASS** | 只开 enabled、关 migrate：不迁移，不发件，不打标签（`ev/check-H5.json`） |
| H6 模板缺失 | **PASS** | 拿掉模板后结果为 NO_TEMPLATE，没有发出任何件。恢复模板并 `ni reload` 后重登，结果 MIGRATED（`ev/check-H6a.json`、`ev/check-H6b.json`） |

### F 组（属性 / reload）

| 例 | 结果 | 关键证据 |
|---|---|---|
| F1 护甲值为 0、属性不外露 | **PASS** | 穿着的和背包里的 P1 甲：generic.armor 与 toughness 都是 0，Unbreakable 1，HideFlags 7；客户端 generic.armor = 0。`/ni reload` 后不变（`ev/F1-before.json`、`ev/F-after-reload.json`）。D1–D3 过程中客户端 armor 属性也始终为 0（`ev/D1-3.json`） |
| F2 reload 后发件 | **PASS（部分）** | reload 后新 give 的件正常。「reload 后补发（reissue）」部分按计划留到第二段 A2 |

### G 组（菜单 `ember_p1_armor`，mineflayer 真点击）

| 例 | 结果 | 关键证据 |
|---|---|---|
| G1 打开 | **PASS** | 4 格穿着、4 格候选、S、A、L、R 全部渲染，没有未解析的 `%corerpg_p1_*%`，玩家文案里没有命令教学（`ev/G1-t2a.json`） |
| G2 排序 | **PASS（只验证了第一键）** | 背包里放 3 件同部位头盔（q1c1、q3c3、q0c0），候选格显示 q3c3：`成色 精良 → 极品 · 精工 4% → 6% · 生命 +0.5`，符合第一键「生命差」（`ev/G-t2a.json`）。并列时的后续键（同族、护符同族、掉落阶、时间、uid）没有专门造并列数据，第二段若有余量再补 |
| G3 单件换上 | **PASS** | 点候选：头盔 9eda8f22 → 6cc833b9；换下的 9eda8f22 回到候选原来所在的背包格（41）。`.dat` 已存（check 读的是 `.dat`），① ② 无违例（`ev/G-t2a.json`、`ev/check-G3G4G6.json`） |
| G4 全部换上 | **PASS** | 第一次点只提示「将换上：胸甲（合计生命 +0.5）。30 秒内再点一次…确认」，甲位不变。等 31 秒再点，又回到提示（重新计时），甲位仍不变。30 秒内第三次点才执行：「已换上 1 件护甲。」（`ev/G4-t2a.json`）。文案观察：纯超时也显示「背包有变化，已刷新方案」，措辞不准，不影响资产 |
| G5 待领领取（含背包满） | **PASS** | t2d 背包满：领取得到「已领取 0 件；背包满了，还有 4 件留在待领里」。腾出 2 格后领到 2 件，余 2 件；再腾格后全部领完。`ember_six_c_*` 清空，待领显示「没有待领物品」（`ev/G5-t2d.json`、`ev/check-G5G6.json`） |
| G6 防连点 | **PASS** | 不等回包连点「换上」10 次：只换 1 次，换下的件 1 份。连点「领取」10 次：只领 1 次（2 件），没有复制。① 无违例 |
| G7 关闭再打开 | **PASS** | 换上和领取之后重开菜单，显示与实际一致：穿着格、候选格、「四个部位都已是最好的一件」、待领件数都对得上 |
| G8 开关关 | **FAIL（菜单层，不影响资产 → 需修，可带条件过）** | 见 §3.1 |

### 3.1 G8 FAIL 详情

计划断言：「护甲格显示普通玻璃板；任何点击只回『护甲功能尚未开放』」。实测（`ev/G8-t2a.json`、`ev/G8b-direct.json`）：

- ✅ 装备页的「护甲」入口（ember_p1_gear 第 12 格）显示为灰玻璃板，点击没有任何动作，正常路径进不去。
- ✅ 服务端全部拒绝：`armor equip head`、`armor all confirm`、`armor claim` 都只回「护甲功能尚未开放」，甲位不变。
- ❌ 护甲页仍能直接打开：TrMenu 的 Bindings 命令 `ember_p1_armor`，玩家输入 `/ember_p1_armor` 就能打开。
- ❌ 页面里 4 个穿着格（10/12/14/16）仍显示皮革甲图标「穿着 · 头盔」等，不是普通玻璃板。
- ❌ 点穿着格、候选格（已是灰玻璃板）、「全部换上」（煤炭）、四件套，都没有任何回复；只有「待领物品」会回「护甲功能尚未开放」。

建议（菜单岗）二选一：

- 开关关时让 Bindings 不生效，或者打开时直接拦截；
- 给各格加 `armor_on == 0` 的分支，显示玻璃板并统一回「尚未开放」。

FAIL 只留证据，没有改代码或菜单。

### D 组（事件级 T1-6，t2a / t2d）

| 例 | 结果 | 关键证据 |
|---|---|---|
| D1 拖放 | **PASS** | 头盔拖到背包：H 140.84 → 139.98。迁移头盔拖上身：H 变为 140.37（与菜单里 -0.5 的差值一致）。D 一直是 10（按设计 `EmberLoadout`「B and D never change」，D 只由护符决定）。① ② 无违例（`ev/D1-3.json`、`ev/check-D1-3.json`） |
| D2 shift 点击 | **PASS** | 穿和脱各一次，H 都随之刷新（139.98 ↔ 140.84）。mineflayer 客户端视图有滞后，以服务端 status 和 `.dat` 为准 |
| D3 数字键 | **PASS** | 甲位 ↔ 快捷栏 9 来回交换，H 都正确刷新 |
| D4 发射器 | **PASS** | 头部留空，站在发射器前，红石触发后原版铁头盔上身，其余三格 P1 甲不变（`ev/D-A.json`）。注：发射器由控制台 setblock 放置，与计划写的「t2op 放置」效果等价 |
| D5 盔甲架 | **PASS** | 手持 P1 甲右键盔甲架被拦，提示「不能放到盔甲架上」，P1 件仍在手里。空手从盔甲架取下原版金头盔正常 |
| D6 死亡掉落 | **PASS** | `keepInventory false` 下执行 kill，10 件 P1（含 4 件穿着）掉落后自动捡回，uid 全部不变，地上没有残留，DB 全部 active，无复制（`ev/D-B.json`）。插件同时记了 death 快照 #49 |
| D7 换世界 | **PASS** | world → t2_alt → world，甲位 4 件 uid 不变（`ev/D-B.json`） |
| D8 Q 丢出 | **PASS** | 被拦，提示「余烬材料和装备账号绑定，不能丢出…」，物品仍在手里，地上没有掉落物 |
| D9 箱子 / 漏斗 | **PASS** | 箱子、漏斗各试三种：点放、shift、数字键。每次都提示「不能放进箱子 / 容器」，重开后服务端容器为空，物品仍在手里 |
| D10 展示框 | **PASS** | 被拦，提示「不能放进展示框」，物品仍在手里，展示框为空 |
| D11 装备库往返 | **PASS** | `p1 stash` 后 DB 状态 active → stored，owner 不变；`gearlib take` 后 stored → active，owner 不变，uid 不变，背包里只有 1 份（`ev/D13-D11.json`） |
| D12 分解 | **部分：拒绝 PASS / 掉落路径 SKIP** | 手持迁移件分解被拒：「这件是迁移（不可分解）…」，DB 仍是 active（`ev/D-B.json`）。src=drop 的护甲只能从副本掉落获得（`EmberRunService`），而本次不装 DungeonPlus，测试服拿不到掉落甲，所以「件作废 + 白板 ×0.1 记账」这条路径 SKIP。单测 `EmberSixSlotRulesTest` 覆盖了 `armorDismantleTenths` / `addTenths`；实机验证与 H8 一起留到 T3 |
| D13 invsnap 防复制 | **PASS** | 拍快照 #52（X 在背包）→ 把 X 存入装备库 → restore #52：日志 `skipped P1 1`，「…@背包1（已在装备库）」，X 没有回到背包，DB 仍是 stored；取回后只有 1 份（`ev/D13-D11.json`）。变体：快照后把 X 穿上身再 restore #57，restore 连甲位一起覆盖成快照状态，X 只有 1 份，`skipped P1 0`，因为不存在复制来源（`ev/D13-worn.json`、`ev/check-D13.json`） |
| D14 末影箱 | **PASS** | 放入允许，存档后 `.dat` 的末影箱里有该件；取回到快捷栏 1 后末影箱为空，① ② 无违例（`ev/D14.json`） |

全员终检 `ev/check-D-end.json`：t2a、t2b、t2d 都是 flag=1，没有标签，四格甲都在。t2c、t2e 是迁移前档，保留给第二段。① 无违例。

## 4 测试者操作事件（不是插件缺陷，如实记录）

1. **误删 4 件 P1 头盔。** 11:00 UTC+8 清理 D4、D5 留下的原版头盔时，用了控制台 `clear t2a minecraft:iron_helmet`。P1 头盔的物品材质也是 iron_helmet，结果连同 4 件 P1 头盔一起删掉了：6475738c、6cc833b9、a6fbee68 是测试中 give 的，9eda8f22 是 t2a 的迁移头盔。DB 里这 4 行仍是 active，成了没有实物的孤儿行。这是测试者操作失误，原版 `/clear` 本来就绕过插件。第二段开始前会按计划用 `restore-pre.sh` 回到迁移前档。旁证：运维手册可以补一句「不要按材质 clear 玩家，P1 甲与原版甲同材质」。
2. **G5 的 ② 报警是预期的。** 为了腾出背包格，用控制台清掉了 t2d 的 4 组圆石，所以 check 报 ② 丢失 4 组圆石。这是预期，不是违例。
3. **工具修正。**
   - `check.py` 增加「基线之后 give 的单份件」归类，不再误报成翻倍（真翻倍时同 uid 计数 >1，仍会报）。
   - `lib.logSince` 原来按字符切按字节记的偏移，已修正。复核 H1、H2 的日志证据完整：MIGRATED ×5、ALREADY ×5。

## 5 剩余场景（第二段）

- **A 组 43 例**：kill -9 / jdb 断点，含关键 kill 点「done flag 已写、提交后存档前」以及 (b)(c) 必测。
- B 组 5 例，C 组 3 例。
- E 组 7 例：E4 迁移完成 + 孤儿标签，重启后标签被清、守卫放行；E5 永久拒绝并出队；排队恢复在 tick 40 复核。
- H3、H4、H7。
- F2 的 reissue 部分（随 A2）。
- 合计 61 例。计划总数 89（另 H8 SKIP）= 本段 28 + 余 61。
- **SKIP**：H8（不装 DungeonPlus，留到 T3）；A 组里「异步写已落、主线程未推进」一侧（D320 ②，总控已确认，列入残余风险）；D12 掉落甲分解路径（同 H8 原因）。

## 6 线上与收尾

- 线上 4 个服务全程未动。pid 与启动时间和开测前一致：mariadbd 47100（05:38:04），登录服 47542（05:38:18），代理 47617（05:38:18），游玩服 101651（06:55:31）。
- 线上快照 128 个文件 `sha256sum -c` 全部 OK。线上 DB 只读查询的结果与开测前逐字一致（`snap/live-db-before.txt`）。
- 没有写线上库，没有动线上 bv 和配置，没有给线上任何号 op。临时 op 只在测试服用来读 status，用完立即 deop。
- 本段结束：测试服已 stop，临时 mariadbd 已 shutdown，memmon 和控制台 fifo holder 已停。25577、25578、3317 端口已释放，没有残留的 t2-drill 进程。
- 测试目录和 worktree 保留给第二段；`ember-v1.yml` 已恢复为 enabled=true、migrate=true。

---

# 第二段（f3f1a065 部分完成 · 2026-10-08 13:15 UTC+8）

- 同 tip `f3f1a065`，约束同第一段。第二段开始于 11:09 UTC+8。
- 第一步先起临时库，再跑 `restore-pre.sh`。测试库回到迁移前：8 行 active，逐行与 `.dat` 实物对上（`ev/check-pre2.json`，`active_without_item = []`），第一段误删头盔留下的 4 行孤儿记录已随之消失。之后每个用例开头都会再跑一次 `restore-pre.sh`，所以各用例的前置互相独立。
- 驱动方式：jdb attach 127.0.0.1:25578，用 `stop at EmberSixMigration:<行>`（行号按 f3f1a065 用 javap 核过）下断点。断点一命中就 kill -9；「后侧」先 `next` 单步再 kill。R 组在 261 行执行 `set post = "drill"` 强制自检失败。没有加任何测试钩子。
- 每个用例走五步：
  1. kill，然后马上解析 `.dat` 和 `p1-six` 做 C 检查（标签 ⇔ 甲位）；
  2. 重启，bot 进服，用 `armor mig` 一路续到 ALREADY；
  3. 再 kill -9 一次，重启复核；
  4. 跑不变量 ① ②；
  5. 核对：每部位恰好 1 件迁移甲有效（临时库 migrate·active 每部位 = 1）、作废 uid 在库里为 retired、`/corerpg p1 status` 的 B/H/D/M/EHP 与迁移前逐位相等。
  证据在 `ev/A/<编号>/`（k1、k2、final 下的 `.dat` JSON、yml，以及 `result.json`）。
- 工具说明：
  - `check.py` 增加了 `T2_GRANTED` 白名单，只用于 A3-2、A3-7：用控制台发的那顶原版皮帽不算翻倍。
  - A2/A3 中玩家动过甲位之后的 C 检查用宽松规则：只判「带标签时甲位出现原物」。
  - A5 中 kill 后那一刻的 ② 只作瞬态记录（已发出、待下次 claim 结算的条目），判定以结算后的严格检查为准。

## A 组结果（43/43 完成：41 PASS / 2 FAIL）

| 例 | 写点 | 结果 | 要点 |
|---|---|---|---|
| A1-1 | M2 前 | PASS | 无 journal，重启后 MIGRATED |
| A1-2 | M2 后 | PASS | journal 未带标签，`.dat` 仍是原物（C 一致）→ JOURNAL_DROPPED → 4 个 uid 都 retired → MIGRATED |
| A1-3 | M3 后 / M4 前 | PASS | 同上，内存里的换甲没有落盘 |
| A1-4 | M4 后 | PASS | 带标签且甲位是 issued（C 一致）→ ROLLED_FORWARD |
| A1-5 | M5 后 / M6 前 | PASS | ROLLED_FORWARD，重新 remember |
| **A1-6** | **M6 后（完成标记已写）/ M7 前**（关键 kill 点） | **FAIL** | (b) 生效：重启后 ALREADY，日志 `orphan swap mark cleared (1), saved`，`.dat` 不再带标签。但 4 件迁移甲只有 head 在临时库有记录，chest/legs/boots 的异步 remember（upsert active）随 kill 丢失，而 ALREADY 路径不会补写。dbCheck 判这 3 件「DB 记录未找到」，不计入护甲，t2a 的 H 从 139.70 降到 138.56，EHP 从 174.6 降到 173.2。两次独立运行都复现 |
| **A1-7** | **M7 后 / M8 收尾存档前**（孤儿标签窗口） | **FAIL** | 同 A1-6：(b) 日志与清标签都正确，但 3 件迁移甲没有 DB 记录，H/D 与迁移前不等。复现 2 次 |
| A1-8 | M8 后 | PASS | (c) 生效：收尾 Checked 存档后 `.dat` 不带标签。`next` 期间异步写已落，4 件都有记录 |
| A2-1…5 | F2 前、F2 后、F3 后、F4 后、F5 后（先把头盔放进末影箱，让它不在身上，再进入续上/重发路径） | 5 PASS | ROLLED_FORWARD「re-issued 1」。旧 uid（含末影箱里的那件）记进 voided 并在库里为 retired。F2 后到 F4 后之间被 kill 的，重启会再重发一次，前一个新 uid 也会作废，最终每部位只有 1 件有效 |
| A3-1…6 | R1 后、R2 后（玩家先在头部穿上自己的皮帽）、R4 前、R5 前、R6 前、R7 前 | 6 PASS | CHECK_FAILED「revert finished」→ 重新 MIGRATED。A3-2 中原铁盔被划出 journal、进了待领，玩家的皮帽没被动。A3-6 的 DB retire 丢失，进服后 resendVoids 补写为 retired |
| A3-7 | 撤回中途玩家往空槽穿自己的东西（不 kill） | PASS | 原物进待领，玩家物品保留 |
| A4-1…4 | D1 take 前、D2 persist 前、D3 save 前、D4 retire 前（A1-3 状态） | 4 PASS | JOURNAL_DROPPED。A4-4 的 retire 未发出，重启后补写为 4 行 retired |
| A5-1…6 | C4 give 前、C4 后 / C5 前、C5 后 / C6 前、C6 后 / C7 前、C7 后 / C8 前、C8 后（t2a 待领 1 件铁盔） | 6 PASS | 每个 kill 点重启后都恰好领到 1 次，没有重复领取，也没有丢失。A5-3（已 persist、条目没删）kill 后那一刻 `.dat` 里有铁盔、待领里也还有条目，② 判「翻倍」。这是设计内的瞬态：这一件已发出，靠 `ember_six_c` 认领标记在下次 claim 的 settle 结算；结算后严格检查通过，待领条目已删，身上只有 1 件。A5-4/5 的残留认领标签由 untagExcept 清掉 |
| A5-7、A5-8 | 从 A5-3 状态重领：settle C1 persist 前、C1 后 / C2 save 前 | 2 PASS | settle 中途再次被 kill，重启后仍只结算 1 次，待领条目最终删除，铁盔 1 件 |
| A6-1 | E1 swapIn：内存已换、`savePlayerFile` 前 | PASS（第 2 次） | 第 1 次 FAIL `awi accd65e9`：这是测试布置的问题，不是插件问题。布置时用控制台 give 的物品在 kill -9 前还没存档，DB 里已写 active，物品却随 kill 丢了。改为下断点前先 `save-all` 后重跑，通过。第 1 次的证据保留在 `ev/A/A6-1-run1-setup-artifact/`。顺带的观察：admin give 先写 DB 行、后存背包，kill -9 落在这中间会留下「有记录无实物」的 active 行。这是 give 命令本身的窗口，不属于六槽迁移 |
| A6-2 | E1 swapIn：`savePlayerFile` 后 | PASS | |
| A7-1/4/7 | M4 后、F2 后、R2 后 × 不动（关迁移进出一次） | 3 PASS | 关迁移时进出不触发任何写；开迁移后续上，① ② 和 H/D 都通过 |
| A7-2/5/8 | 同上三个点 × 死亡掉落（keepInventory=false，原地重生后捡回） | 3 PASS（第 2 次） | 第 1 次判 FAIL 是因为布局，不是因为丢件。捡回后迁移甲和刀/护符落进了背包或别的快捷栏位：A7-2 是 4 件全在背包，H=138.18；A7-5 是 3 件在背包，H=138.56；A7-8 甲已穿回但刀不在原快捷栏，B=1.00。H/D 只算身上的件，所以读数不等。这 3 例当时 ①②、C、DB 每部位 1 件 active、awi、settle3 ALREADY 都通过，见 `ev/A/A7-2,5,8/result.json`。第 2 次（`A7-*r`）在读 H/D 前加了一步布局还原 `rewear.js`：按 DB 里 active 的迁移甲把它们穿回对应甲位，刀/护符放回迁移前的快捷栏位 1/0。断言不变，3 例通过。还原日志写在 `result.json.rewear` |
| A7-3/6/9 | 同上三个点 × 穿到背包后重登 | 3 PASS（A7-3/6 是第 2 次） | 原因同上：A7-3/6 第 1 次的甲还在背包里，H 偏低。加布局还原后通过。A7-9 是 R2 后的撤回路径，会把甲重新穿上，第 1 次就通过了，第 2 次加还原复跑也通过 |

**A 组小结：**

- 43 例中 41 PASS，2 FAIL（A1-6、A1-7，同一缺陷）。
- 全组没有违反 ①（同 uid 两份有效）或 ②（原物丢失或翻倍）的情况。
- 每例的 C 检查都一致。
- 「完成标记已写、收尾存档前」这个点由 A1-6 和 A1-7 覆盖。(b) 日志 `orphan swap mark cleared (1), saved` 和 (c) 的 Checked 存档（A1-8）都已核实。

**A1-6 / A1-7 的性质（待总控定级）：**

- 不违反 ① ②，原物都在待领。
- 但违反 A 组断言「身上加待领的迁移甲每个部位恰好 1 件有效」，并造成玩家 H/D 下降，而且没有自愈路径：ALREADY 只清孤儿标签，不会补 remember。
- 这是 jdb 能覆盖的「异步写未落」一侧，现实中的窗口是 remember（异步）到 DB 落库之间的几十毫秒。
- 修法交插件岗，例如：
  - ALREADY 或进服时，对身上和待领里缺 DB 记录的 src=migrate 件补 upsert；
  - 或 commit 在写 flag 之前等 DB 确认。


## B 组结果（5/5 PASS）

前置：A1-4 状态，即 M4 后 kill，journal 未决，`.dat` 带 `ember_six_m_*` 且甲位是 issued。先关 migrate，把迁移挂起，再依次做事件；每一步之后都解析 `.dat` 看标签。证据见 `ev/B/chainA`、`ev/B/chainB` 和 `ev/B/result.json`。

| 例 | 事件 | 结果 | 要点 |
|---|---|---|---|
| B2 | 退出→重连 ×2 | PASS | 标签 `ember_six_m_511dc6b8…` 在 |
| B4 | 正常 `stop` → 起服 | PASS | 标签在 |
| B5 | `save-all` 后 kill -9 → 起服 | PASS | kill 后、重启进服后标签都在 |
| B3 | `mv tp t2_alt` 再回 `world`；之后开 migrate，`corerpg reload`，进服约 1 秒就换到 t2_alt | PASS | 标签在。续上发生在换世界约 2–3 秒后：12:46:53 进服，12:46:57 ROLLED_FORWARD，早于进服 120 tick（6 秒）的触发点，说明是 `onWorld` 第 40 tick 续上的。续上后 `p1_six_mig=1 journal=false 待领=3` |
| B1 | 死亡（`/kill`，keepInventory=false）→ 重生，原地捡回 | PASS | 重生后标签仍在（MC-85730 不复现）。4 件迁移甲掉落后被捡回背包。回枢纽后 ROLLED_FORWARD，① ② 成立，每部位恰好 1 件 active，awi 为空 |

两条链最后都是：违反 0，`active_without_item` 为空，migrate·active 每部位 1 件，flag=1，无 journal，无标签。

## C 组结果（3/3 PASS，15 次 kill 全部一致）

R6：标签和甲位在同一份 `.dat` 里，同一次存档写入。每个点从干净前置连续 kill 5 次，每次 kill 后立即解析 `.dat` 和 yml。证据在 `ev/C/<点>-<n>/result.json` 和 `ev/C/summary-all.json`。

| 例 | kill 点 | 5 次结果 | 要点 |
|---|---|---|---|
| C1（M4） | run() 248 persist 后 | 5/5 一致 | 每次都是带标签且 4 个甲位都是 issued（新 uid），journal marked=true |
| C2（R5） | revert() 332 persist 后（261 处强制自检失败） | 5/5 一致 | 每次都无标签，甲位按 journal originals 逐位是原物（铁盔 / 空 / 钻石护腿 / 同伴 P1 靴 df5cb4bb），marked=false |
| C3（F5） | reconcile() 383 前（头盔先放末影箱，F 路径 persist 后） | 5/5 一致 | 每次都带标签，4 个甲位都是 issued（头部是重发件） |

**落盘方式观察：** 15 次 kill 后都没有看到 `.dat_old` 或 tmp 残留，`dat_old_seen=0`、`tmp_seen=0`。1.12.2 的玩家存档是整文件替换，不会出现「标签在、甲位不在」或反过来的半写状态。

## E 组结果（invsnap 守卫：7/7 按断言 PASS；E4 附带复现 A1-6/A1-7，且后果被放大）

证据在 `ev/E/<例>/result.json`，含恢复命令的整段日志、排队文件前后内容和 `.dat` 解析。

| 例 | 场景 | 结果 | 要点 |
|---|---|---|---|
| E1 | journal 未决、带标签（M4 后 kill，关 migrate 进服），在线恢复到迁移前快照 #13 | PASS | 拒绝，判 `temporary: 六槽迁移未完成（journal 未决）`；标签、甲位前后不变；之后续上 ROLLED_FORWARD |
| E2 | 同 E1，号离线排队恢复 | PASS | 第一次进服：12:57:42 拒绝并保留排队（`attempts: 1`，`last_error` 记了原因），没有先恢复；12:57:46 迁移续上。第二次进服：快照早于完成时间（12:57:46），判 permanent，日志 `queued restore 13 for t2a dropped (six-slot guard, permanent)`，已出队 |
| E3 | 已迁移、无孤儿标签，恢复到迁移**后**的快照（manual #14） | PASS | 正常恢复（pre-restore #15）。被移动的头盔回到甲位，`skipped P1 0` |
| E4 | 孤儿标签：commit 414 unmark 后、415 persist(c) 前 kill → 重启回枢纽 → 恢复到迁移**后**的 join 快照 #14 | PASS（④ 断言） | kill 后 `.dat` 有 1 个标签。重启后日志 `ALREADY orphan swap mark cleared (1), saved`，下一次存档的 `.dat` 无标签，`p1_six_mig=1 journal=false`，invsnap 没有误拒，恢复已执行。**附带发现见下** |
| E5 | 已迁移，在线恢复到迁移前快照 | PASS | 拒绝，判 `permanent: 快照早于六槽迁移完成`，提示「这张快照以后也不能恢复」；甲位、待领不变 |
| E6 | 已迁移，恢复到 journal 未决期间拍的死亡快照（13:00:28，早于完成时间 13:00:56） | PASS | 拒绝（permanent） |
| E7 | 已迁移、号离线，排队恢复迁移前快照 | PASS | 进服时拒绝，没有先恢复。日志 `dropped (six-slot guard, permanent)`，排队文件已清空。总控本轮要求「永久拒绝并出队」，实现与此一致。计划原文写的是「排队保留并记录原因」，那是旧口径；按原因保留排队的行为只出现在 temporary（journal 未决）这一类，E2 已验证 |

**E4 附带发现（A1-6/A1-7 第 3 次复现，后果放大）：**

- E4 的 kill 点就是 A1-7 的窗口。重启后临时库 migrate·active 只有 head 一件（`migSlots={head:1}`），chest、legs、boots 的 remember 又一次丢失。
- 之后恢复到迁移后的快照时，invsnap 的 P1 信任检查把这 3 件判为「DB 无记录」并跳过（防复制）：`skipped P1 3`，日志列出 T2 烬爆胸甲、靴子、护腿。
- 恢复是整背包替换，所以玩家身上这 3 件迁移甲**直接消失**。最终只剩头盔，`final.armor = {103}`。
- pre-restore 快照 #16 里虽然有这 3 件，但再恢复它同样会被跳过；迁移前的快照又被守卫永久拒绝。玩家没有自助找回的路径，只能管理员补发。
- ① ② 仍成立：原物都在待领，②只管原物。但 A1-6/A1-7 的后果因此从「H/D 下降」升级为「invsnap 恢复后迁移甲丢失」。**建议总控按此重新定级**：A1-6/A1-7 必修，并加回归用例「E4 状态 → 恢复后每部位 1 件 active」。

## H3 / H4 / H7 与 F2 重发部分

| 例 | 结果 | 要点（证据 `ev/H/<例>/result.json`） |
|---|---|---|
| F2 重发部分 | PASS | 先 `/ni reload`，再走 F 路径（头盔在末影箱）→ ROLLED_FORWARD `re-issued 1`。重发件 `30b71f00`：`generic.armor=0`、`armorToughness=0`、Unbreakable=1、HideFlags=7、src=migrate，临时库为 active。H/D 与迁移前逐位相等（H=139.70、EHP=174.6） |
| H3 | PASS | 旧 uid `d903e9cb` 在 `voided`，临时库为 retired。把它从末影箱取出穿上后：H 从 139.70 降到 139.32，等于头部空着按标准成色计算；护甲页头部显示「头盔：空」，背包里的有效新件显示为「可换上」；audit 报 `HELD_NOT_ACTIVE d903e9cb… state=retired`，符合手册 §4 的预期，无 DUPLICATE |
| H4 | PASS（第 2 次设计） | 第 1 次设计无效：先停库再让号进服，进服被同步加载卡住约 24 秒（主线程 8 秒超时 ×3，看门狗 thread dump），F 路径实际在起库后才跑。保留为观察，见下。第 2 次（H4b）：号进服、加载完成后立刻停库，13:08:57 F 路径在库宕时完成（`re-issued 1`，flag=1，voided `91cf336e`）。起库时 91cf336e 没有行，migrate·active 为 0 行（宕机期间的写都没落库）。重登：这一次会话的加载在连接池重连期间超时，存档按「本会话加载失败」拒绝写（保护生效）。之后临时库里 `91cf336e` 为 **retired**，4 件 migrate·active 每部位各 1 行，H/D 与迁移前相等，违反 0 |
| H7 | **FAIL**（审计口径） | 5 个号同时在线、迁移完成后执行 `corerpg p1 audit`：t2b–t2e findings 0；t2a 报 `ACTIVE_NOT_HELD df5cb4bb`（同伴 P1 靴子）。这件靴子在**待领**里，没有丢，但 audit 只扫背包、甲位、副手、末影箱和光标，不扫 `p1-six` 待领。更大的风险：手册说 `ACTIVE_NOT_HELD` 用 `/corerpg p1 audit restore`，而 restore 的防复制检查 `heldAnywhere` 同样不看待领。照做会补发一份，玩家再领待领就有两份有效副本，违反 ①。代码核对：f6868515 的 EmberAudit 也没有待领处理。建议把待领计入 held，或在 restore 前拒绝待领里已有的 uid |

**H4 第 1 次的观察（不属于六槽迁移，记录备查）：**

- 库宕时进服，P1 的 kinds、loadout、items、stored 四次加载都在主线程上各等 8 秒超时，进服被卡约 24 秒，触发看门狗 thread dump。
- 起库后同一个进程里重登，`/corerpg p1 status` 显示 `H=20.00 D=0`（迁移甲和护符都不计入），这个状态一直持续到那次停服。原因没有查清，只留证据。
- 新 tip 的「DB 宕机迁移」场景会再看一次。

---

# f3f1a065 两段汇总与结论（这一版）

| 段 / 组 | 例数 | PASS | FAIL | SKIP / 部分 |
|---|---|---|---|---|
| 第一段 H/F/G/D | 28 | 26 | 1（G8 菜单层） | D12 部分（掉落路径归 T3） |
| A（kill -9 / jdb 写点） | 43 | 41 | 2（A1-6、A1-7） | — |
| B（标签跨事件） | 5 | 5 | 0 | — |
| C（标签⇔甲位，15 次 kill） | 3 | 3 | 0 | — |
| E（invsnap 守卫） | 7 | 7（E4 附带 A1-6/7 复现） | 0 | — |
| H3 / H4 / H7 / F2 重发 | 4 | 3 | 1（H7 审计不看待领） | — |
| 合计 | 90 | 85 | 4 | H8、「已落盘、主线程未前进」侧、D12 掉落路径 |

**不变量：**

- 全部用例中，① 同 uid 两份有效副本、② 原物丢失或翻倍，**都是 0 次**。
- C 组和 A 组每次 kill 后，标签⇔甲位都一致。
- B 组标签从未丢失。

**问题与遗留风险：**

1. **A1-6 / A1-7（T2 阻塞，总控已定）**：完成标记先于迁移甲的 DB 行落库，kill -9 后 3 件没有 DB 行，H/D 下降且无法自愈。E4 显示 invsnap 恢复会把这 3 件当可疑件跳过，玩家直接失去迁移甲。f6868515 已加 DB_PENDING 门和 pieces 自愈，待复跑。
2. **G8**：护甲页开关关闭时的菜单层问题（第一段）。f6868515 已改，待复跑。
3. **H7**：audit 与 audit restore 不看待领，照手册操作有复制风险。f6868515 未见修改，需修或在手册中禁止。
4. **G4**：第一段的复制、超时文案。f6868515 已把超时和方案变化分开提示，待复跑。
5. 「已落盘、主线程未前进」一侧：jdb 无法构造，未测。
6. **D12** 掉落路径：归 T3。
7. **H8**：跨服或代理，本轮 SKIP。
8. admin give 先写 DB、后存背包的窗口（A6-1 布置时发现，不属于六槽）；库宕时进服主线程被卡、之后 H=20（H4 第 1 次）。

**结论：不建议签「T2 过线」。** A1-6/A1-7 是阻塞项，待新 tip f6868515 复跑。复跑结果写在下面。


---

# 新 tip f6868515 复跑（收束 · 中途存档 2 · 2026-10-08 14:06 UTC+8；后续在 tip 4d82b518 续跑）

- tip `f6868515`（含 remember 落库门 DB_PENDING、pieces 自愈、待领与开关解耦、G8 菜单、超时文案、手册 §3.3/§3.4）。
- worktree 已切到该 tip。全量测试：`Tests run: 666, Failures: 2`，失败仅 `EmberGrowthTest` D164/D165（预期）。`BUILD SUCCESS`，jar 已换到测试服（`server/plugins/CoreRpg.jar` sha256 前缀 `ac17b0b709d83b9c`，旧 jar 留在 `parked/CoreRpg-f3f1a065.jar`）。
- 证据根：`/workspace/tmp/t2-drill/evidence/N/`（旧 tip 证据仍在 `evidence/{A,B,C,E,H}/`，未覆盖）。
- A1-6/A1-7 遗留号一律 `restore-pre.sh` 重来（旧完成记录无 `pieces`，无法自愈）。

## N-H12（H1/H2 初跑）——判 FAIL，口径需澄清后重跑

证据：`ev/N/H12/result.json`。

| 项 | 结果 |
|---|---|
| 迁移路径 | 5 个号都是 `DB_PENDING` → 紧跟 `ROLLED_FORWARD`（异步 DB 确认后主线程重跑，属预期） |
| H2 | **PASS**：每人再重登 3 次 + `armor mig` → 都是 ALREADY；`cr_p1_item` 总行 28→28，migrate·active 20→20 |
| 不变量 | 违反 0，awi 空 |
| 迁移后 H/D | 5 个号都与第一段基线 `H1-pre-hd.jsonl` **逐位相等**（t2a H=139.70 / t2c 313.70 / t2d 95.50 / t2b 20.00 / t2e 40.00） |
| 本 tip 会话内「迁移前」读数 | t2a/t2c/t2d **低于**基线（t2a 138.47、t2c 307.76、t2d 94.10）；t2b/t2e 相等 |

**为何记 FAIL：** 计划 H1 断言是「与迁移前逐位相等」。本脚本在 `enabled=true migrate=false` 下读的迁移前 H，对混有原版甲的号（t2a/t2c/t2d）低于迁移后；按字面断言 FAIL。

**倾向判断（待 H1 重跑核实，不凭此过线）：**

- 迁移**产出**的 H/D 与第一段基线完全一致，H2 幂等，不像发甲算错。
- 更像是：六槽 `enabled=true` 后、尚未迁移前，原版甲位件的计入方式与第一段基线测量时不一致（或基线测量口径不同），导致「会话内 pre」被压低，迁移完成后回到设计值。
- 重跑方案：① 关六槽读 2 槽 H/D 作 pre；② 开 `enabled+migrate` 完成迁移后读 post，断言 post==①；③ 同时再读一组 `enabled=true migrate=false` 的过渡态，单独记录。结果写在后续存档。

## A 组 kill 点复跑（25/25 PASS，含原阻塞点）

证据：`ev/N/A/<id>/result.json`，跑完于 13:57:48。行号按 f6868515 的 `EmberSixMigration`。

| 例 | 写点 | 结果 | 要点 |
|---|---|---|---|
| NA1-1…4 | M2 前/后、M3/M4（273） | 4 PASS | 与旧 tip 行为一致 |
| NA1-5 | commit 437 remember 未发 | PASS | 带标签；重启后 DB_PENDING→ROLLED_FORWARD；4 件 active，H=139.70 |
| **NA1-5b** | **442 DB_PENDING（remember 已发、等 DB 回执）** | **PASS** | kill 时 flag=0、journal 在、标签在；重启续上完成，每部位 1 件，H/D 等于基线 |
| NA1-6a | 451 前（4 件已确认、完成标记未写） | PASS | |
| **NA1-6** | **451 后（完成标记+pieces 已写）/ 去标签前** | **PASS** | 旧阻塞点。kill 后 flag=1；重启 ALREADY，`(b) orphan swap mark cleared (1), saved`；**4 件 migrate·active**，H=139.70（不再丢 DB 行） |
| **NA1-7** | **455 前（内存去标签、收尾存档前）** | **PASS** | 旧阻塞点。同上，4 件齐全，H/D 恢复 |
| NA1-8 | 455 后 | PASS | |
| NA2-1…5 | F 路径 408/410/411/413/414 | 5 PASS | 重发路径在 DB_PENDING 门后仍正确 |
| **NA2-6** | 槽被占：头盔进末影箱 + 头部穿皮帽 → 自然续上（不 kill） | **PASS** | `re-issued 1`；待领里出现 `r`+新 uid（如 `rb575d41a…`）；领取后布局还原，H=139.70；旧 uid retired |
| **NA2-7** | 同布置，在 442 DB_PENDING kill | **PASS** | kill 时 stash 已有 `r3a304f68…`（重发件进待领）；重启不再二次重发，最终每部位 1 件 |
| NA3-1/2/5/6 | R 路径抽查 | 4 PASS | |
| NA4-3/4 | D 路径抽查 | 2 PASS | |
| NA7-2/8 | M4 后 / R2 后 × 死亡掉落 | 2 PASS | 读 H/D 前布局还原 |

**A1-6/A1-7 阻塞项：本 tip 已过。** DB_PENDING 期间 kill 与完成标记后 kill 均不再丢迁移甲 DB 行。

A5（待领领取 kill 点）正在跑；其后 G、DB 宕机/自愈、E、H1 重跑。

## A5 待领领取（f6868515，6/8 完成）

| 例 | 写点 | 结果 |
|---|---|---|
| NA5-1…6 | claim give 前 → 最后 persist 后（行号 526–536） | 6 PASS，恰好领 1 次 |
| NA5-7、NA5-8 | settle 重领 kill 点 | **未跑**（总控改切 tip 4d82b518，改在新 tip 续） |

未在本 tip 跑完的：A6 抽查、G1–G8、G5 开关全关、DB 宕机、自愈、E 复核、N-H12 口径重跑、H7。总控正式指令：口径以 **4d82b518** 为准，下面切 tip 续跑。

**f6868515 已确认的阻塞项消除证据（可保留）：**

- NA1-6 / NA1-7 / NA1-5b：PASS（完成标记后 kill、DB_PENDING 期间 kill 均不丢迁移甲 DB 行，H=139.70）
- NA2-6/7：`r`+uid 进待领、DB_PENDING 不二次重发
- 全量测试 666/2（仅 D164/D165）
