# 运维手册 · 余烬六槽护甲迁移（gear.six_slot）

- 适用对象：运维 / OP（权限 `corerpg.admin`）。本手册只列游戏内或控制台可用的命令，均以代码为准。
- **线上状态（D350 钉盘 · 观察期）：** jar **`1.65.101-d335.local`**（进程真源；D335 tip 起 · 薄抽/预检多次确认）· `balance_version` **62** · Stage1 **F** + Stage2 档 **C** · 三开关 **`enabled` / `migrate` / `set_bonus` 均为 true** · **观察中**（绿出口另签，不早于 **2026-10-10 17:40 CST**）。真源：**代码 / 线上配置 / 进程 > 本文**；若再漂移，以进程为准并旁注（§1.5）。**本行改 jar tip ≠ 授权换装或回滚。** D325 备份路径见 §1.4。
- 代码基准：CoreRpg `origin/main`（含 D319–D325：invsnap 守卫、孤儿标签清理、收尾存档、完成前等 DB 确认、pieces 自愈、待领与开关脱钩、audit 待领计入 held、Stage2 `set_bonus` 四件套减伤）。代码默认三键仍为 `false`；**线上观察中为 true**——以线上 `ember-v1.yml` 与 `/corerpg p1 armor status` 为准。
- 本手册**不擅自授权**任何上线、切开关、改 ×0.97 或迁移动作；须总控另签。T2 演练、T3 上线、部署、观察关窗、回滚都要各自另签。
- 相关文档：规格 `docs/design/DESIGN-ember-six-slot-t1-spec-revision-2026-10-08.md`；Stage2 `docs/design/DESIGN-ember-six-slot-stage2-set-bonus-2026-10-08.md`；观察结案 `docs/design/DESIGN-ember-six-slot-stage2-observe-close-2026-10-08.md`；T2 演练计划 `docs/status/PLAN-ember-six-slot-t2-drill.md`；权威勘误 STATUS `docs/status/STATUS-ember-six-slot-authority-ops-align-d330-2026-10-09.md`。

---

## 1. 三个开关

| 键（`plugins/CoreRpg/ember-v1.yml`） | 代码默认 | 作用 |
|---|---|---|
| `gear.six_slot.enabled` | `false` | 总开关。关：装备计算走 2 槽公式，逐位不变（T1-1 golden）。甲位上的东西一律忽略，护甲页（只显示「护甲功能尚未开放」）、结算掉甲、护甲 PAPI 都不生效。**例外：待领领取与开关无关**（D320 ④），有待领时开关关也能领，护甲 PAPI 的待领三个键也照常有值。还要求 P1 模式本身开着（同一文件顶层的 `enabled: true`）。 |
| `gear.six_slot.migrate` | `false` | 老角色一次性迁移。只在总开关也开着时才有效（代码：`migrateEnabled() = enabled() && migrate`）。 |
| `gear.six_slot.set_bonus` | `false` | Stage2 四件套减伤（档 C：激活时受伤 ×0.97 / −3%，**不进 B/H**）。须 `enabled`。关：无四件套减伤；迁移 / 甲属性仍按 `enabled`。 |

代码缺省为关。**线上观察中三键均为 `true`**（以线上 yml / `armor status` 为准）。缺键时按代码默认 `false`。

### 1.1 写法

在 `plugins/CoreRpg/ember-v1.yml` 顶层 `gear.six_slot` 段（观察期线上示例）：

```yaml
gear:
  six_slot:
    enabled: true
    migrate: true
    set_bonus: true
```

- 不要写到 `storage.gearlib` 下面，那是装备库，跟这里无关。
- 改完在控制台执行 `/corerpg reload`，它会重读 `config.yml` 和 `ember-v1.yml`。**观察期禁擅自改真值**（须总控签）。
- 拿一个在线的号核对：`/corerpg p1 armor status <玩家>`，输出末尾的 `enabled=… migrate=…`（及面板/日志中的 set_bonus 生效态）是当前实际生效的值。

### 1.2 开启顺序

1. **前置检查**：
   - 部署的 jar 包含本手册的代码基准（invsnap 守卫、孤儿标签清理）；
   - NI 护甲模板已加载，否则迁移结果是 `NO_TEMPLATE`，并提示 OP；
   - 菜单 `ember_p1_armor` 已上线；
   - `storage: mysql`（见 §3）；
   - DB 健康（见 §3.1）。
2. **两个键在同一次编辑里写成 `true`，然后执行一次 `/corerpg reload`。**
   - 原因：总开关一开，所有人立刻按六槽公式计算。还没迁移的玩家甲位视为空（品质 0、精工 0），H/D 会低于迁移前（护符品质和精工都为 0 时持平）。迁移会给他们补上同值的四件甲，H/D 逐位回到原值。
   - 所以不要只开 `enabled`、不开 `migrate`。
3. **迁移触发时机**（代码）：
   - 玩家进服，第 120 tick；
   - 从副本等世界回来，第 40 tick；
   - 只在「枢纽」执行：不在副本里、不在挂机庭、活着、不是旁观模式。不满足就延后到下次。
   - OP 可以对在线玩家手动触发：`/corerpg p1 armor mig <玩家>`。要求 `migrate` 开着；不在枢纽时显示「延后」。
4. **观察**：
   - 日志里的 `[P1 six] <玩家> migration MIGRATED / ROLLED_FORWARD …`；
   - 异常会以 `[P1 six] <玩家> …` 的形式同时写警告日志并发给在线 OP（见 §6）；
   - 抽查 `/corerpg p1 audit <玩家>`（见 §4）。

### 1.3 关闭顺序

- **只停迁移**：把 `migrate` 改成 `false`，然后 `/corerpg reload`。
  - 新的迁移停止（进服、换世界和 `mig` 命令都不再执行）；
  - 已迁移的玩家不受影响，待领照常可领；
  - 进服时重发作废 uid 这一步照常进行（它只要求总开关开着）。
  - 注意：只要 `enabled` 还开着，还没迁移的玩家就一直按空甲位计算（见 1.2 第 2 步）。所以迁移窗口结束后是否保留 `enabled`，要由总控定。
- **全部回滚**：先确认没有未决 journal（见 §7.1），再把两个键都改成 `false`，`/corerpg reload`。完成后的状态见 §7。

### 1.4 观察期（Stage2 · D326 / D330）

- **红线 / 绿出口：** 见 [`DESIGN-ember-six-slot-stage2-observe-close-2026-10-08.md`](../design/DESIGN-ember-six-slot-stage2-observe-close-2026-10-08.md) 与 [`STATUS-ember-six-slot-stage2-observe-close-d326-2026-10-08.md`](../status/STATUS-ember-six-slot-stage2-observe-close-d326-2026-10-08.md)。绿出口须另签；满窗不早于 **2026-10-10 17:40 CST**；必抽整轮 ≥1 PASS。
- **禁默改 ×0.97：** 档 C 减伤倍率钉死；观察期不得拧倍率、不得把减伤改写进 B/H。
- **回滚解耦：** 回滚 / 关 `set_bonus` **与** Stage1 的 `enabled` / `migrate` **解耦**——可只关四件套减伤而保留六槽甲路径与迁移；全关仍按 §1.3 / §7（先清未决 journal）。
- **备份路径：** `/workspace/tmp/d325-bv62-backup-20261008173047/`（D325 live 备份）。触发红线（双计减伤、B/H 漂移、迁移回归、跨服复制等）时按观察结案回滚，勿手删 `p1-six` / 标签。
- **K3：** T0‴ 已签 PASS，**施工 / 部署等绿出口**；本手册不授权观察期部署 K3。
- **K3 禁误读短语（D362）：** 「已就位 / PASS / 批 A」**≠开闸**——见 DESIGN [`DESIGN-ember-six-slot-k3-offline-live-misread-2026-10-09.md`](../design/DESIGN-ember-six-slot-k3-offline-live-misread-2026-10-09.md) / STATUS [`STATUS-ember-six-slot-k3-offline-live-misread-d362-2026-10-09.md`](../status/STATUS-ember-six-slot-k3-offline-live-misread-d362-2026-10-09.md)。**≠部署 ≠开 `k3_refine`；开闸真源仍 D341 §2.1。**
- **白板经济注记（D361）：** 分解/验收报告须注记「白板经济模型未覆盖」——见本清单 DESIGN [`DESIGN-ember-six-slot-blank-economy-note-2026-10-09.md`](../design/DESIGN-ember-six-slot-blank-economy-note-2026-10-09.md) / STATUS [`STATUS-ember-six-slot-blank-economy-note-d361-2026-10-09.md`](../status/STATUS-ember-six-slot-blank-economy-note-d361-2026-10-09.md)。**≠改 ×0.1 / dismantleYield；≠补 p1sim blank 建模；≠开 R。**

### 1.5 观察期 yml 真源与防冲（D344）

> 规格真源：[`DESIGN-ember-six-slot-live-yml-protect-2026-10-09.md`](../design/DESIGN-ember-six-slot-live-yml-protect-2026-10-09.md)（**已批 A·M·D344**）。**批 A ≠ 改代码默认 true ≠ 关观察 ≠ 开 K3。**

#### 真源

| 路径 | 观察期角色 |
|------|------------|
| `plugins/CoreRpg/ember-v1.yml` · `ember-v1-runs.yml`（及同族 live） | **运行真源** |
| `CoreRpg/src/main/resources/ember-v1*.yml` | 打包/单测默认；**不得**反向覆盖 live |
| jar 内 resources | 仅当插件目录**缺文件**时由 `saveResource(..., false)` 抽出——**已存在 live 禁止当「对齐」覆写** |

#### 保护键（缺一即事故）

| 键 | 观察钉死（当前） | 冲丢表现 |
|----|------------------|----------|
| `gear.six_slot.enabled` | **true** | 护甲路径关 |
| `gear.six_slot.migrate` | **true** | 迁移停 |
| `gear.six_slot.set_bonus` | **true** | Stage2 减伤关 |
| `balance_version`（runs） | **62** | bv 回落（曾见 60） |
| `gear.six_slot.k3_refine` | **关/缺省** | 若被误开 → 违 D341 |

#### 禁止 / 允许

| 禁止（观察期） | 允许 |
|----------------|------|
| `cp` / `rsync` / `git checkout --` **resources → plugins/CoreRpg/ember-v1*.yml** | 改 live 后 **同提交** 把 live **拷回** resources（满足 `bundledConfigsMatchLive`） |
| 以「单测红了」为由整份用 src 盖 live | 单测红：先查是否 live 有意超前；再 live→src，**勿** src→live |
| **主工作区** `git checkout` / `switch` / 会改写已跟踪 live yml 的操作 | 观察期**禁主仓切分支**；K3 离线只用**独立 worktree** |
| 部署脚本静默覆盖保护键 | 部署前显式 `diff`/`rg` 保护键 |

#### skip-worktree（观察期必做）

live 两文件已被 git 跟踪；主仓 `checkout`/`switch` 会整份盖工作区（D343：21:00、21:03）。观察期须：

```bash
git update-index --skip-worktree -- plugins/CoreRpg/ember-v1.yml plugins/CoreRpg/ember-v1-runs.yml
```

- 要改并 **commit** 保护键时：先 `git update-index --no-skip-worktree -- <同上>` → add/commit → **再设回 skip-worktree**。
- `skip-worktree` **≠** 取消跟踪；只防本机工作区被切分支静默冲掉。钉仓真值仍以 `origin/main` 上的保护键为准（D343 钉仓 `c9888b3a`）。

#### 前车与核对

- D342 预检 **R1+R2 红**：21:00:03 live 被替换为与 git resources 同文 → 六槽段消失、`balance_version`→60。  
- D343 已恢复三 true + bv62；pin `/workspace/tmp/d343-pin/`；备份 `/workspace/tmp/d335-backup-20261009195542/`。  
- 发现 live 已被冲：**停**；对照 pin/备份；**另签**后再恢复；记 STATUS。  
- 文首 jar tip 若与进程漂移：以进程为准，旁注即可（D342 R4），**勿**用旧 jar 名当覆盖借口。

---

## 2. 禁止事项

- **禁止手删 `ember_six_m_*` 标签**，包括 `/scoreboard players tag … remove` 或任何插件，也禁止手加。
  - 这个标签是「这次换装已经落到玩家身上」的唯一证据。journal 未决时如果删掉它，续上时会判定「换装没生效」，丢弃 journal，**原物丢失**（QA 已复现）。
  - 迁移完成后残留的标签由插件自己清理（§5），也不需要手删。
- **禁止离线改玩家 NBT**（`world/playerdata/<uuid>.dat`），包括甲位、背包、Tags。原因同上：标签和甲位一旦不一致，结果就是原物翻倍或丢失。
- **禁止手改或删除 `plugins/CoreRpg/p1-six/<uuid>.yml`**。待领物品只存在这个文件里，迁移完成标记也在这里；删掉后再开迁移会重复发甲，待领也没了。
- `ember_six_c_<id>` 是待领领取时的发放标记，同样不要动。
- **禁止按材质 clear 玩家**，例如 `/clear <玩家> minecraft:iron_helmet`、`/minecraft:clear <玩家> <材质>`，或任何按物品类型批量删的插件命令。
  - 原因：迁移发的四件甲和玩家原来的甲用的是同一批原版材质。按材质清会把迁移甲（DB 里仍是 active）和原物（身上的、刚从待领领回的）一起删掉。前者让护甲不再计入、H 下降，`audit` 报 `ACTIVE_NOT_HELD`；后者直接丢原物。两者都破坏守恒（「迁移前有的，迁移后在身上或待领里一件不少」），而且 DB 和记录文件都无法自动补回。T2 第一段已经出现过一次：操作员清 `iron_helmet` 时把 P1 头盔一起删了。
  - 需要腾背包时，让玩家自己挪或丢。测试服确实要用 `clear` 时，只清明确不是装备的东西（例如圆石），并在测试记录里写明；巡检报的「② 丢失」属于预期。

---

## 3. storage 必须是 mysql

- `plugins/CoreRpg/config.yml` 里必须是 `storage: mysql`。线上当前就是 mysql。
- 原因：迁移过程中被换掉、需要作废的旧件，只靠 DB 里 `cr_p1_item.state = retired` 加上加载装备时的 DB 校验来失效。
  - 在 YAML 模式或 DB 不可用时，校验会退回只看物品签名，作废件会被当作有效件，等于复制。
  - 所以 YAML 模式下作废无效，**不能开迁移**。

### 3.1 迁移前的 DB 健康检查（每次开 migrate 之前，以及迁移期间定时检查）

| 检查 | 命令 / 位置 | 合格 |
|---|---|---|
| 存储模式和连通性 | 控制台 `/corerpg storage` | 显示 `storage=mysql (config=mysql)`，`MySQL ping:` 是绿色毫秒数。出现 `FAIL` 或 `MySQL configured but inactive (fell back to yaml)` 都不合格 |
| 启动时 DB 守卫 | `/corerpg storage guard` | `未拦截`。**不要用 `release` 放行**，放行后玩家会以 YAML 模式进服 |
| 写入错误 | 服务器日志 | 近期没有 `[ember-v1.0-P1] upsert item <uid> failed:`，也没有 `[ember-v1.0-P1] schema init failed` |
| 抽查对账 | `/corerpg p1 audit <在线测试号>` | 结果与预期一致（见 §4） |

### 3.2 DB 宕机时

- **立即把 `gear.six_slot.migrate` 改成 `false` 并执行 `/corerpg reload`。**
- DB 恢复、§3.1 全部合格后，再按 1.2 重新打开。
- 宕机期间丢失的 retired 写入会自动补发：玩家进服时，以及迁移判定为已完成或续上时，都会重发作废 uid（幂等）。DB 恢复后让相关玩家重登一次即可。
- 宕机期间正在迁移的玩家不会被标记完成（见 3.3），四件甲的 DB 行也会在 DB 恢复后补上（见 3.3、3.4）。

### 3.3 迁移完成前等 DB 确认（D320 ①）

- **成因（T2 A1-6/A1-7）**：新发四件甲的 DB 行（`cr_p1_item` active）是异步写的。以前完成标记先落盘，异步写还没落库时被 kill -9，部分件的 DB 行丢失。之后判定「已完成」不会再写，这些件不计入，H 下降。
- **现在的做法**：
  - 收尾时，插件先对四件甲逐件补发 DB 写入（upsert active），然后**等 DB 回执**。回执在 DB 线程完成后回到主线程，**不阻塞主线程**。
  - 四件都确认之前，结果是 `DB_PENDING`：journal 和换装标签都保留，不写完成标记、不写 `done_at`。这段时间内四件甲照常计入（内存校验缓存已认它们）。
  - 四件都确认后，插件回到主线程自动重跑一次迁移检查（续上 → 收尾）。这时才在**同一次记录写入**里写完成标记、`done_at`、`pieces` 和待领。
  - 有一件写失败（DB 断开、SQL 错误）：不写完成标记，发告警 `六槽迁移等待 DB 确认失败（<uid>），未置完成标记；下次回到枢纽重试`。journal 保留，下次回枢纽（或 `/corerpg p1 armor mig`）时重来，可以重复进入。
  - 日志：每次检查都写 `[P1 six] <玩家> migration DB_PENDING … · waiting for the DB to confirm n of 4 pieces (journal kept)`；确认后紧跟一行 `MIGRATED` 或 `ROLLED_FORWARD`。
- `DB_PENDING` 期间 `status` 显示 `journal=true`，invsnap 恢复被暂时拒绝（§6 规则 2），属于预期。DB 正常时这段时间通常只有几十毫秒。
- 长时间停在 `DB_PENDING`，说明 DB 写不进去，按 3.1 / 3.2 处理。
- **与 invsnap 的关系（E4 放大路径已消）**：旧 tip 在「完成标记已写、部分 remember 未落库」时，恢复迁移后快照会把无 DB 行的迁移甲当「DB 无记录」跳过，甲直接丢失。现在完成标记只在 4 件确认之后才写，未决 journal 期间 invsnap 守卫会暂时拒绝恢复；进服 `onJoin` 的 pieces 自愈也在排队恢复（第 40 tick）之前跑。T2 在 tip f6868515 上复跑 NA1-6 / NA1-7 / NA1-5b 均 PASS。
- 只有 MySQL 存储处于激活状态（`/corerpg storage` 显示 `storage=mysql`）时才等待。YAML 模式下没有 DB 可等，直接完成，但 YAML 模式本来就禁止开迁移（见本节开头）。运行中 DB 断开时，MySQL 仍是激活状态，写入会失败，按上面的「写失败」处理，不会误标完成。

### 3.4 已完成玩家的 DB 行自愈（D320 ②）

- 记录文件新增 `pieces.<n>`，记下迁移发出的四件甲（与完成标记同次写入）。
- 下面两个时机，插件会对这四件里**还在玩家身上、没有作废、本次进程还没确认过**的件，逐件做一次「DB 里没有这行才插入 active」（`INSERT IGNORE`）：
  - 玩家进服（要求 `enabled` 开着），和重发作废 uid 同一步；
  - 迁移检查判定「已完成」（ALREADY，要求 `migrate` 开着），和孤儿标签清理同一步。
- 幂等：已有的行**一律不改**。retired 的不会复活，在装备库里（stored）或属于别人的也不动，只记一行警告 `self-heal <uid>: DB row is <state> / owner <uuid> — left as it is`。`voided` 里的 uid 根本不发。
- 插入后按 DB 实际状态刷新校验缓存并重算属性，所以护甲重新计入，H/D 回到原值，不需要重登第二次。
- DB 不可用时不做任何事，下次进服或回枢纽再试（日志 `self-heal <uid>: DB query failed …`）。
- **限制**：D320 之前写的完成记录没有 `pieces`，无法从记录自愈。这类记录只存在于测试服（线上从未开过开关）；测试服如有 A1-6/A1-7 遗留的号，用 `/corerpg p1 audit` 核对后按测试岗的流程处理。

---

## 4. 作废旧甲的残留与审计（属于预期）

- 迁移续上时，如果某件已发的新甲在提交前离开了玩家（扔到地上、放进箱子、死亡掉落），会用新 uid 重发一件，旧 uid 记入 `voided` 并在 DB 置为 `retired`。
- **旧件实体不会被回收**，会以无效物品的形式一直留在世界里（地上、箱子），装备计算不认它。
- 玩家把它捡回来后，`/corerpg p1 audit <玩家>` 会报 `HELD_NOT_ACTIVE <uid> state=retired`。这是**预期结果**，不是复制。处理方式是让玩家丢弃或忽略它。
- 不要对这类 uid 用 `/corerpg p1 audit restore`。那条命令只用于 DB 为 active、身上和待领都没有的件（`ACTIVE_NOT_HELD`）。

### 4.1 audit 与六槽待领（D321 H7）

- `/corerpg p1 audit` 会扫 `plugins/CoreRpg/p1-six/<uuid>.yml` 的待领（任意键：迁移原物、重发进待领的 `r`+uid 等）。待领里的 P1 uid 算 held。
- 若某 uid 的 DB 行是 active、只在待领里、不在背包/甲位/末影箱：finding 是 **`ACTIVE_IN_STASH <uid> · 在六槽待领，勿 audit restore（让玩家去 装备→护甲 领取）`**，**不是** `ACTIVE_NOT_HELD`。
- `/corerpg p1 audit restore` 若该 uid 已在待领里，会拒绝并提示「该 uid 在六槽待领里，勿补发；让玩家去 装备→护甲 领取」。照旧 restore 会再补一份，玩家再领待领就两份有效，违反同 uid 有效 ≤1。
- 真正的 `ACTIVE_NOT_HELD`（身上和待领都没有）才考虑 restore，且仍要过「在线副本」检查。

---

## 5. 孤儿 `ember_six_m_*` 标签（D319 (b)(c)）

- **成因**：迁移收尾时，插件会先把「完成标记 `p1_six_mig: 1` 和完成时间 `done_at`」写进记录文件，再去掉换装标签。去标签只发生在内存里，如果之后、自动存档之前被 kill -9，玩家文件里就会残留这个标签。
- **(c) 收尾立即存档**：去标签后，插件马上做一次带结果的玩家存档（`savePlayerFileChecked`）。
  - 存档失败只会告警：日志和在线 OP 会收到 `[P1 six] <玩家> 六槽迁移已完成，但去掉换装标签后的玩家存档失败…`；
  - 迁移结果不受影响，结果仍是 MIGRATED / ROLLED_FORWARD，日志里的明细末尾带 `post-commit player save failed`。
- **(b) 已完成时顺手清理**：迁移已完成（有 `p1_six_mig: 1`、没有未决 journal）的玩家，每次在枢纽触发迁移检查时，插件都会去掉他身上残留的所有 `ember_six_m_*` 标签，并再做一次带结果的存档。
  - 日志：`[P1 six] <玩家> migration ALREADY orphan swap mark cleared (n), saved`；
  - 存档失败时明细为 `… player file save failed (cleared again next time)`，并告警，下次再清。
  - 不重新迁移，不发甲，不动待领。
- 清理只在 `migrate` 开着时才会被触发。`migrate` 关掉以后，残留标签会留在玩家身上。这本身无害：invsnap 守卫不看标签（§6），装备计算也不读它。**仍然不要手删。**

---

## 6. invsnap 恢复守卫（D319 (a)(d)）

**适用范围：**

- 守卫作用在 `/corerpg invsnap restore` 的**即时恢复**和**排队恢复**上。排队恢复是指玩家离线时排队，下次进服第 40 tick 自动执行；它在真正执行时会再检查一次，这时迁移（第 120 tick）还没跑。
- `/corerpg invsnap preview` 会提前显示守卫的判定。

**判定依据：**

- 只看记录文件 `plugins/CoreRpg/p1-six/<uuid>.yml`，**不看玩家身上的标签**。
- 与开关状态无关：只要记录文件存在就会检查，回滚关掉开关后也一样，用来防止复制。

**规则（从上往下，命中即停）：**

| # | 情况 | 结果 | 排队恢复 |
|---|---|---|---|
| 1 | 没有 `p1-six/<uuid>.yml`（从未迁移，包括开关一直关着） | **放行**，与没有守卫时逐位相同 | 照常执行 |
| 2 | 有未决 journal（迁移中断，或存档失败还没续上） | **拒绝（暂时）**：原因是「六槽迁移未完成（journal 未决）」 | 保留在队列里，记一次失败，下次进服再试 |
| 3 | 已迁移（`p1_six_mig: 1`），且快照时间早于 `done_at + 1 秒` | **拒绝（永久）**：恢复会让原甲位物品同时出现在身上和待领里，造成复制 | **从队列移除**，日志写 `dropped (six-slot guard, permanent)` |
| 4 | 已迁移，但记录里没有 `done_at`（D319 之前写的老记录） | **拒绝（永久）**：保守规则，因为分不清快照在迁移前还是迁移后 | 从队列移除 |
| 5 | 其余情况：已迁移且快照晚于完成时间；或者有记录但既没有标记也没有 journal（journal 已丢弃或自检撤回，玩家身上已回到迁移前的状态） | **放行** | 照常执行 |

**补充说明：**

- `done_at` 是秒级时间戳，与「完成标记」在**同一次记录写入**里落盘。快照与完成时间落在同一秒时，按「早于完成」处理。
- 被拒绝时：
  - 执行命令的 OP 会看到原因，以及「这张快照以后也不能恢复，请选迁移完成之后的快照」或「稍后可以再试」，外加本手册的路径；
  - 排队恢复被拒时，所有在线 OP 也会收到提示；
  - 日志写 `[invsnap] six-slot guard refused restore <id> for <玩家> …`。
- 快照被永久拒绝后的操作：
  - 用 `/corerpg invsnap list <玩家>` 找一张时间晚于迁移完成时间的快照（list 显示 CST 时间），先 `preview` 再 `restore`；
  - 迁移完成时间可以在记录文件的 `done_at` 里看到（只读）。
- 暂时拒绝（journal 未决）的处理：
  - 让玩家回到枢纽，`migrate` 必须开着，迁移会自动续上；
  - 也可以用 `/corerpg p1 armor mig <玩家>` 手动续上；
  - `/corerpg p1 armor status <玩家>` 显示 `journal=false` 后再恢复。
  - 如果 `migrate` 已经关了，journal 永远不会被处理，恢复就会一直被拒，需要先按 1.2 重新打开。
- **jar 回退到不含守卫的版本**（例如 1.65.97）后就没有这道守卫了。这时 OP 必须自己遵守上面的规则：不恢复到迁移完成之前的快照，journal 未决的玩家不恢复。

---

## 7. 记录文件与回滚

### 7.1 记录文件 `plugins/CoreRpg/p1-six/<uuid>.yml`（只读查看，不要改）

| 键 | 含义 |
|---|---|
| `p1_six_mig` | 1 表示已迁移完成，0 或缺失表示未完成 |
| `done_at` | 迁移完成时间，epoch 秒。D319 起和完成标记一起写入 |
| `seq` | 待领条目序号 |
| `journal.{at, reverting, mark, originals.0-3, issued.0-3}` | **未决 journal**：迁移进行中或中断时存在。`originals` 是换下的原物，`issued` 是新发的甲，`mark` 对应换装标签 `ember_six_m_<mark>` |
| `stash.<id>.{slot, item}` | **待领**：原甲位物品，以及重发后没有空位的甲。玩家在 装备 → 护甲 页「领取」后，对应条目会被移除 |
| `voided.<n>` | 作废的旧 uid，每次进服都会重发 DB retired |
| `pieces.<n>` | D320 起：迁移发出的四件甲（uid 等），进服和「已完成」检查时据此自愈 DB 行（§3.4）。与完成标记同次写入 |

- 在线玩家可以用 `/corerpg p1 armor status <玩家>` 查看 `p1_six_mig`、`journal`、`待领` 数量。
- 离线玩家只能只读查看这个文件。查看「谁还有未决 journal」时，看文件里是否有 `journal:` 段。

### 7.2 关闭开关后的状态

| 项 | `migrate` 关、`enabled` 开 | 两个都关 |
|---|---|---|
| 新迁移 | 不再发生 | 不再发生 |
| 已迁移玩家的四件甲 | 照常计入六槽公式 | 成为惰性物品：装备计算走 2 槽公式，逐位等于开关从未打开时 |
| 待领领取 | 可领 | **可领**（D320 ④ 起与开关脱钩）。护甲页只剩「护甲功能尚未开放」和待领入口，装备页的护甲格在有待领时显示「待领物品」。领取的防连点、幂等和存档顺序与开关开时完全相同 |
| 进服重发作废 uid、DB 行自愈 | 照常 | 停止。已写入的 retired 仍然有效，DB 校验与开关无关 |
| invsnap 守卫 | 生效 | **仍然生效**：只要有记录文件就检查 |
| 孤儿标签清理 | 停止（无害，见 §5） | 停止 |

### 7.3 已迁移玩家怎么处理

- **什么都不要动**：
  - 不删记录文件，不改 `p1_six_mig` 或 `done_at`；
  - 不删标签；
  - 不把玩家恢复到迁移前的快照（守卫会拦；jar 回退后要人工遵守）。
- **全部回滚前先清空未决 journal**：
  - 有 journal 时，原物可能只存在于 journal 里，还没进待领；
  - 这时关掉开关，journal 就不会再被处理，原物会一直拿不到；
  - 所以先在 `migrate` 开着的情况下，让这些玩家回到枢纽，或用 `/corerpg p1 armor mig <玩家>` 续上，直到 `status` 显示 `journal=false`，再关开关。
- 回滚后如果要重新打开：已迁移的玩家不会再迁移一次（`p1_six_mig: 1` 表示已完成），待领物品原样可领。

### 7.4 不可逆的部分

- 新发的四件甲在 DB 里是 `cr_p1_item` 的 active 行（`source=migrate`）。按 D245 安全路径**不删 DB 行**。物品本身留在玩家身上，开关关时是惰性物品。
- 作废的旧 uid 在 DB 里是 `retired`，**不会恢复成 active**，实体残留见 §4。
- 原甲位物品已从甲位移到待领。只能由玩家自己领回（开关开或关都可以），**没有 OP 发放命令**，也不会自动放回甲位。
- `p1_six_mig: 1` 和 `done_at` 是永久记录。
- 迁移完成后，迁移完成之前的 invsnap 快照**永远不能再恢复**到这个玩家身上（规则 3、4）。

---

## 8. 命令速查（均需 `corerpg.admin`）

| 命令 | 用途 |
|---|---|
| `/corerpg reload` | 重读 `config.yml` 和 `ember-v1.yml`，开关改动用它生效 |
| `/corerpg storage` | 存储模式和 MySQL ping |
| `/corerpg storage guard` | 启动时的 DB 守卫状态（不要 `release`） |
| `/corerpg p1 armor status [玩家]` | `p1_six_mig`、`journal`、待领数量、`enabled`、`migrate` |
| `/corerpg p1 armor mig [玩家]` | 对在线玩家手动触发或续上迁移（要求 `migrate` 开着且在枢纽） |
| `/corerpg p1 audit [玩家]` | P1 物品与 DB 对账（`HELD_NOT_ACTIVE` 见 §4） |
| `/corerpg invsnap list <玩家> [条数]` | 列出快照（CST 时间） |
| `/corerpg invsnap preview <玩家> <id>` | 预演恢复，含六槽守卫判定 |
| `/corerpg invsnap restore <玩家> <id>` | 恢复；离线玩家则排队，进服第 40 tick 执行（执行时再过守卫） |

`/corerpg p1 armor claim | equip | all | worn | set` 是给玩家用的，由菜单代发，不属于运维命令。每个都会回一句话；开关关时除有待领的 `claim` 外一律回「护甲功能尚未开放。」

---

## 9. 告警速查（日志 + 在线 OP）

| 文本（前缀 `[P1 six] <玩家>`） | 含义 / 处理 |
|---|---|
| `六槽迁移中断（下次回到枢纽时自动续上或作废）` | 迁移中抛异常。journal 未决，invsnap 会被暂时拒绝。让玩家回枢纽续上 |
| `六槽迁移未完成：player file save failed …` | 玩家存档失败（SAVE_FAILED），什么都没有标记。下次回枢纽续上。连续出现时检查磁盘 |
| `六槽迁移未执行：护甲物品模板缺失 …` | NI 模板没加载。停 `migrate`，修好模板后再开 |
| `六槽迁移已完成，但去掉换装标签后的玩家存档失败…` | (c) 失败。迁移已完成，残留标签会由 (b) 下次清理 |
| `六槽孤儿换装标签已在内存清除，但玩家存档失败…` | (b) 存档失败，下次再清 |
| `六槽作废 uid 重发 / 自愈失败：…` | 进服重发 retired 或自愈出错。查 DB（§3.1） |
| `六槽迁移等待 DB 确认失败（<uid>），未置完成标记…` | §3.3：四件甲有一件 DB 写失败。没有标记完成，journal 保留，下次回枢纽重试。查 DB |
| （仅日志）`self-heal <uid>: DB row is … — left as it is` | §3.4：DB 里这行不是本人的 active（retired / stored / 别人），按设计不改。retired 属预期；其余情况用 `audit` 核对 |
| （仅日志）`self-heal <uid>: DB query failed …` | §3.4：DB 不可用，下次进服 / 回枢纽再试 |
| `待领领取中断：…` | 领取中途出错。物品仍在待领里，让玩家稍后再领 |

---

## 附：代码位置（@ D320，见 git log）

| 规则 | 位置 | 离线测试 |
|---|---|---|
| 开关 | `p1/EmberSixSlot.enabled()` / `migrateEnabled()` | `EmberSixSlotOffGoldenTest` |
| (c) 收尾去标签后立即 Checked 存档 | `p1/EmberSixMigration.commit()`（`port.persistInventory()` 紧跟 `apply(null, mark, false)`），`LivePort.persistInventory` → `EmberVault.savePlayerFileChecked` | `EmberSixMigrationTest.orphanSwapMarkKillAfterCommitClearedOnRejoin_D319`、`saveFailureNeverMarksAndResumes` |
| (b) 已完成时清孤儿标签并存档 | `p1/EmberSixMigration.run()` → `tidyOrphanMarks()`；`LivePort.dropSwapMarks()` | 同上；`EmberSixRestoreGuardTest.orphanTagDoesNotBlockPostMigrationRestore` |
| `done_at` 与完成标记同次写入 | `EmberSixMigration.commit()`；`EmberSixSlotService.load/save`（键 `done_at`） | `EmberSixRestoreGuardTest.doneAtOnlyOnCommit` |
| (a)(d) invsnap 守卫 | `p1/EmberSixRestoreGuard.check()`；`EmberSixSlotService.restoreGuard()`；`InvSnapService.restore()` / `applyPending()` / `preview()` / `refuseSix()` | `EmberSixRestoreGuardTest`（11 个，含 `queuedRestoreAtTick40BeforeMigrationAtTick120`） |
| ① 完成标记前等 4 件 DB 确认 | `p1/EmberSixMigration.commit()`（`return Outcome.DB_PENDING;` 在 `next.flag = true;` 之前）；`LivePort.remember/confirmed`、`EmberSixSlotService.dbAnswer()`；`EmberItemStore.upsertItem(d, owner, state, cb)` | `EmberSixMigrationTest.rememberLossAtEveryWritePointTimesRestart_D320`、`killAfterConfirmAtEveryWritePoint_D320`、`conservationFuzzWithHardKillsAndSaveFailures`、`dbConfirmAndSelfHealWiring_D320` |
| ② 已完成玩家 DB 行自愈 | `p1/EmberSixMigration.heal()`，调用点 `run()` 的 ALREADY 分支和 `onJoin()`；`LivePort.heal` → `EmberItemStore.insertItemIfAbsent`（`INSERT IGNORE`） | `EmberSixMigrationTest.selfHealRestoresLostRowsNeverRevivesRetired_D320` |
| ④ 待领与开关脱钩 / ⑤ 每个按钮都回话 | `p1/EmberSixSlotService.cmd()` → `EmberSixPapi.route()`；`EmberRunPapi.armor()`（开关关时待领键仍取真实件数）；菜单稿 `docs/design/staged/d318-six-slot/trmenu/` | `EmberSixRankTest.claimWorksOffAndEveryClickHasAReply_D320`、`serviceCmdRoutesEveryClick_D320`、`stagedMenuEveryButtonReplies_D320` |
| ⑥ 全部换上：超时与方案变化分开提示 | `p1/EmberSixRank.confirm()` / `confirmText()` | `EmberSixRankTest.timeoutAndPlanChangeAreDifferentTexts_D320` |
