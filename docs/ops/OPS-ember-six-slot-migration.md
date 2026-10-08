# 运维手册 · 余烬六槽护甲迁移（gear.six_slot）

- 适用对象：运维 / OP（权限 `corerpg.admin`）。本手册只列游戏内或控制台可用的命令，均以代码为准。
- 代码基准：CoreRpg `origin/main`，含 D319 的 invsnap 守卫、孤儿标签清理和收尾存档。线上目前仍跑 1.65.97，**这里所有功能线上都没有**。开关默认关，未部署。
- 本手册**不授权**任何上线、切开关或迁移动作。T2 演练、T3 上线、部署都要各自另签（见 `docs/status/STATUS-ember-six-slot-t1-accept-test-2026-10-08.md` 的总控签字）。
- 相关文档：规格 `docs/design/DESIGN-ember-six-slot-t1-spec-revision-2026-10-08.md`；T2 演练计划 `docs/status/PLAN-ember-six-slot-t2-drill.md`。

---

## 1. 两个开关

| 键（`plugins/CoreRpg/ember-v1.yml`） | 代码默认 | 作用 |
|---|---|---|
| `gear.six_slot.enabled` | `false` | 总开关。关：装备计算走 2 槽公式，逐位不变（T1-1 golden）。甲位上的东西一律忽略，护甲页、待领领取、结算掉甲、护甲 PAPI 都不生效。还要求 P1 模式本身开着（同一文件顶层的 `enabled: true`）。 |
| `gear.six_slot.migrate` | `false` | 老角色一次性迁移。只在总开关也开着时才有效（代码：`migrateEnabled() = enabled() && migrate`）。 |

线上的 `ember-v1.yml` 和 jar 内自带的 yml **都没有这两个键**，缺省就是关。

### 1.1 写法

在 `plugins/CoreRpg/ember-v1.yml` 顶层新增一段（文件里原本没有顶层 `gear:`）：

```yaml
gear:
  six_slot:
    enabled: true
    migrate: true
```

- 不要写到 `storage.gearlib` 下面，那是装备库，跟这里无关。
- 改完在控制台执行 `/corerpg reload`，它会重读 `config.yml` 和 `ember-v1.yml`。
- 拿一个在线的号核对：`/corerpg p1 armor status <玩家>`，输出末尾的 `enabled=… migrate=…` 是当前实际生效的值。

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

---

## 2. 禁止事项

- **禁止手删 `ember_six_m_*` 标签**，包括 `/scoreboard players tag … remove` 或任何插件，也禁止手加。
  - 这个标签是「这次换装已经落到玩家身上」的唯一证据。journal 未决时如果删掉它，续上时会判定「换装没生效」，丢弃 journal，**原物丢失**（QA 已复现）。
  - 迁移完成后残留的标签由插件自己清理（§5），也不需要手删。
- **禁止离线改玩家 NBT**（`world/playerdata/<uuid>.dat`），包括甲位、背包、Tags。原因同上：标签和甲位一旦不一致，结果就是原物翻倍或丢失。
- **禁止手改或删除 `plugins/CoreRpg/p1-six/<uuid>.yml`**。待领物品只存在这个文件里，迁移完成标记也在这里；删掉后再开迁移会重复发甲，待领也没了。
- `ember_six_c_<id>` 是待领领取时的发放标记，同样不要动。

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

---

## 4. 作废旧甲的残留与审计（属于预期）

- 迁移续上时，如果某件已发的新甲在提交前离开了玩家（扔到地上、放进箱子、死亡掉落），会用新 uid 重发一件，旧 uid 记入 `voided` 并在 DB 置为 `retired`。
- **旧件实体不会被回收**，会以无效物品的形式一直留在世界里（地上、箱子），装备计算不认它。
- 玩家把它捡回来后，`/corerpg p1 audit <玩家>` 会报 `HELD_NOT_ACTIVE <uid> state=retired`。这是**预期结果**，不是复制。处理方式是让玩家丢弃或忽略它。
- 不要对这类 uid 用 `/corerpg p1 audit restore`。那条命令只用于 DB 为 active 但身上没有的件（`ACTIVE_NOT_HELD`）。

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

- 在线玩家可以用 `/corerpg p1 armor status <玩家>` 查看 `p1_six_mig`、`journal`、`待领` 数量。
- 离线玩家只能只读查看这个文件。查看「谁还有未决 journal」时，看文件里是否有 `journal:` 段。

### 7.2 关闭开关后的状态

| 项 | `migrate` 关、`enabled` 开 | 两个都关 |
|---|---|---|
| 新迁移 | 不再发生 | 不再发生 |
| 已迁移玩家的四件甲 | 照常计入六槽公式 | 成为惰性物品：装备计算走 2 槽公式，逐位等于开关从未打开时 |
| 待领领取 | 可领 | **不可领**（护甲页和领取都回复「护甲功能尚未开放」）。物品仍完好存在记录文件里，重新打开 `enabled` 后可领 |
| 进服重发作废 uid | 照常 | 停止。已写入的 retired 仍然有效，DB 校验与开关无关 |
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
- 原甲位物品已从甲位移到待领。只能由玩家在 `enabled` 开着时自己领回，**没有 OP 发放命令**，也不会自动放回甲位。
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

`/corerpg p1 armor claim | equip | all` 是给玩家用的，由菜单代发，不属于运维命令。

---

## 9. 告警速查（日志 + 在线 OP）

| 文本（前缀 `[P1 six] <玩家>`） | 含义 / 处理 |
|---|---|
| `六槽迁移中断（下次回到枢纽时自动续上或作废）` | 迁移中抛异常。journal 未决，invsnap 会被暂时拒绝。让玩家回枢纽续上 |
| `六槽迁移未完成：player file save failed …` | 玩家存档失败（SAVE_FAILED），什么都没有标记。下次回枢纽续上。连续出现时检查磁盘 |
| `六槽迁移未执行：护甲物品模板缺失 …` | NI 模板没加载。停 `migrate`，修好模板后再开 |
| `六槽迁移已完成，但去掉换装标签后的玩家存档失败…` | (c) 失败。迁移已完成，残留标签会由 (b) 下次清理 |
| `六槽孤儿换装标签已在内存清除，但玩家存档失败…` | (b) 存档失败，下次再清 |
| `六槽作废 uid 重发失败：…` | 进服重发 retired 出错。查 DB（§3.1） |
| `待领领取中断：…` | 领取中途出错。物品仍在待领里，让玩家稍后再领 |

---

## 附：代码位置（@ dc69ee23）

| 规则 | 位置 | 离线测试 |
|---|---|---|
| 开关 | `p1/EmberSixSlot.enabled()` / `migrateEnabled()` | `EmberSixSlotOffGoldenTest` |
| (c) 收尾去标签后立即 Checked 存档 | `p1/EmberSixMigration.commit()`（`port.persistInventory()` 紧跟 `apply(null, mark, false)`），`LivePort.persistInventory` → `EmberVault.savePlayerFileChecked` | `EmberSixMigrationTest.orphanSwapMarkKillAfterCommitClearedOnRejoin_D319`、`saveFailureNeverMarksAndResumes` |
| (b) 已完成时清孤儿标签并存档 | `p1/EmberSixMigration.run()` → `tidyOrphanMarks()`；`LivePort.dropSwapMarks()` | 同上；`EmberSixRestoreGuardTest.orphanTagDoesNotBlockPostMigrationRestore` |
| `done_at` 与完成标记同次写入 | `EmberSixMigration.commit()`；`EmberSixSlotService.load/save`（键 `done_at`） | `EmberSixRestoreGuardTest.doneAtOnlyOnCommit` |
| (a)(d) invsnap 守卫 | `p1/EmberSixRestoreGuard.check()`；`EmberSixSlotService.restoreGuard()`；`InvSnapService.restore()` / `applyPending()` / `preview()` / `refuseSix()` | `EmberSixRestoreGuardTest`（11 个，含 `queuedRestoreAtTick40BeforeMigrationAtTick120`） |
