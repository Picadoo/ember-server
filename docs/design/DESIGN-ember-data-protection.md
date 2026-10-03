# 余烬 · 玩家数据保护（CoreRpg 1.62.0）

日期：2026-10-04（时间都是 CST）。这份文档讲玩家数据存在哪里、怎么备份和恢复、数据库断线时怎么处理、装备流水，以及背包快照。

玩家数据存在两类地方，两类都要保护：

| 数据 | 存在哪里 | 怎么保护 |
|---|---|---|
| **原版背包、护甲、副手、末影箱**（包括 NI 材料、P1 装备实物） | 每个世界的 `world*/playerdata/<uuid>.dat`（游玩服和登录服 login-runtime 各有一份），**不在 MySQL** | ① 背包快照 InvSnap（§5）② 每小时打包 playerdata（§1.2） |
| CoreRpg 档案（等级、货币、计数、仓库）、P1 装备账本（`cr_p1_item` / `cr_p1_txn`）、跑图记录、邮件、公会、拍卖 | MySQL `ember` 库 | 每小时整库备份（§1.1）、恢复脚本（§2）、启动守卫（§3） |
| AuthMe 账号 | MySQL `authme` 库，加上登录服的 `AuthMe/` 文件 | 和上面一起备份 |

## 1. 每小时备份：`scripts/ember-backup.sh` 和 `scripts/ember-backup-loop.sh`

- 机器上没有 cron，所以用一个常驻循环：`scripts/ember-backup-loop.sh start|stop|status|now`。
  - 它用 setsid/nohup 在后台跑，pid 记在 `db-auto/loop.pid`，日志写到 `db-auto/loop.log`。
  - 每小时跑一次。失败了 10 分钟后重试。机器重启后，它会把错过的那次补上。
  - `server-runtime/start.sh` 和 `scripts/ember-up.sh` 都会顺手把它拉起来；它已经在跑时不会再起第二个。
- 备份目录是 `/workspace/backup/db-auto/`。保留规则：每小时的留 48 份；每天第一份校验通过的另存一份，留 30 天，同时复制到 `/home/box/ember-db-backups/`。

### 1.1 数据库备份

- 导出 `ember` 和 `authme` 两个库，gzip 压缩，存成 `hourly/db-<ts>.sql.gz`。
- 三项校验都要过：`gzip -t` 能解开；dump 里 `CREATE TABLE` 的数量等于线上基表的数量；`cr_p1_item` 在 dump 里的行数和线上相差不超过 20。
- 连库用 `sudo mysql` 的 root，不行就从 CoreRpg 配置里读账号密码，写进一个 0600 权限的临时 defaults-file 给 mysql 用。全程不打印密码。

### 1.2 玩家文件备份

- 打包成 `files-hourly/files-<ts>.tar.gz`，内容如下：
  - server-runtime 和 login-runtime 下所有世界的 `playerdata/`、`stats/`、`advancements/`
  - `plugins/CoreRpg/players/`、`p1-runs/`、`snapshots/`、`invsnap-pending.yml`
  - DungeonPlus、TrMenu 的 sqlite 文件
  - 登录服的 AuthMe 数据（`authme.db`、`playerdata/`）
  - 所有 `*/config.yml` 都排除在外。
- 校验：`tar -tzf` 能完整列出内容，而且里面至少有一个 playerdata `.dat` 文件。
- 每次结果追加到 `db-auto/backup.log`，最后一次成功写进 `last-ok`。

## 2. 恢复：`scripts/db-restore.sh <dump|latest> [--live] [--db ember|authme|both] [--yes]`

- **默认是演练**：把 dump 导进临时库 `ember_restore_chk` / `authme_restore_chk`，逐表对比线上和 dump 的行数（COUNT(*)），然后删掉临时库。线上数据不动。
- **`--live` 才会真的恢复**，流程是：
  1. 游玩服在跑就拒绝执行（先 `server-runtime/stop.sh`）。
  2. 要求手动输入 `RESTORE` 确认。
  3. 先跑 `db-dump.sh pre-restore`，给当前数据留一份安全备份。
  4. 导入 dump，然后重新对比行数。
- **玩家文件恢复**：先停服，再从 `files-*.tar.gz` 里解出那个玩家的 `world*/playerdata/<uuid>.dat`，覆盖回去。如果只是某个玩家的背包出了问题，优先用 §5 的背包快照，不用停服。

## 3. 启动守卫 DbGuard（MySQL 连不上时）

- **以前的问题**：配置是 `storage: mysql`，但启动时连不上库，插件就静默改用 YAML。玩家照样能进服，进来后读到的是旧的或空的档案。等库恢复，这些假数据会写回 MySQL，盖掉真数据。
- **现在**：同时满足以下几条时，守卫生效：
  - 配置是 `storage: mysql`
  - 启动时连接失败
  - P1 开启
  - `storage_guard.enabled` 为 true（默认就是 true）
- 守卫生效后：
  - 日志打出醒目的 `[DB GUARD]` 横幅。
  - 在 AsyncPlayerPreLogin 和 PlayerLogin 两处都拒绝进服，提示「余烬服数据库暂时连不上，为保护你的存档，服务器暂停进入」（白名单式踢出）。
  - 已在线的玩家全部踢出。
  - 每 30 秒用 TCP 探测一次 MySQL 端口。库恢复后，日志提示「重启游玩服即可放行」。
- **故意不热切换回 MySQL**：公会、拍卖、玩家缓存这时已经从旧的 YAML 读进内存，热切换会把它们写进 MySQL。必须重启。
- 命令 `/corerpg storage guard [status|release]`。`release` 需要管理员权限，作用是强行放行、退回旧的 YAML 模式。不推荐用。
- **单个玩家读库失败**：某个玩家进服时 MySQL 读取抛异常，这个玩家的 PlayerData 会被标记 `loadFailed`。本次会话内永远不保存它（否则会覆盖库里的真数据），并把他踢出、提示稍后再进。
- **测试方法**：设环境变量 `CORERPG_TEST_MYSQL_PORT=<一个错误端口>` 再启动，插件会用这个端口代替配置里的 `mysql.port`，日志里会大声提示 TEST OVERRIDE。这样测试不用改带密码的配置文件。
  - 10-04 04:00 实测：设 3399 启动后横幅出现，FreshQ41 进服被拒。去掉变量重启后日志出现 `MySQL connected`。

## 4. P1 装备流水（`cr_p1_txn`）

- P1 装备每次改变都会在同一个事务里改 `cr_p1_item`，并记一条流水。流水种类：
  - create：发放，1.62 起会补记
  - enhance、upgrade、refine、quality、swap：锻造类
  - dismantle：分解
  - reroll：洗练时被吃掉的重复件
  - stash：存入装备库（active → stored）
  - unstash：从装备库取出（stored → active，rev+1，重新签名）
  - glibdis：在装备库里分解（stored → dismantled）
  - undo：撤销分解（dismantled → stored，先把退还的胚料收回）
- **没有交易**：D73 规定装备不能交易。别人手里的装备过不了「主人 + rev + 签名」校验，不计入战斗。
- 洗练吃掉的重复件不能撤销。
- 管理员查流水：`/corerpg p1 itemlog <玩家>|uid <前缀> [条数]`，时间显示为 CST。

## 5. 背包快照 InvSnap（原版背包、护甲、副手、末影箱）

- **什么时候拍**：
  - 进服（40 tick 后）
  - 退服
  - 死亡（LOWEST 优先级，掉落之前）
  - 跨世界传送进出 P1 世界或副本实例世界，原因记为 enter / exit / hop
  - 在线玩家每 10 分钟一次（periodic）
  - 插件关闭时，给所有在线玩家拍一次（shutdown）。停服时插件先关闭、玩家后被踢，退服事件来不及触发，所以要靠这一次。
- **内容和格式**：
  - 背包按 `getContents()` 共 41 格：0–8 快捷栏，9–35 背包，36–39 靴、腿、胸、头，40 副手。末影箱 27 格。
  - 每件物品用 Bukkit 的 YAML 序列化。1.12 的 CraftMetaItem 会把不认识的 NBT 放进 `internal` 字段一起保存，所以 NI 的 id、ember 签名、附魔、成书内容、皮革颜色都能原样还原。
  - 整份 gzip 压缩，存进 MySQL 表 `cr_inv_snapshot`，字段有 id、player_uuid、name、reason、world、created_at、item_count、hash、data（MEDIUMBLOB）。
  - **库连不上时**，改写文件 `plugins/CoreRpg/snapshots/<uuid>/<毫秒时间戳>-<原因>.yml.gz`，id 记作 `f<时间戳>`。
- **去重**：内容 hash 和这个玩家上一次一样时，自动快照（进服、10 分钟、进出副本、退服）直接跳过；manual、pre-restore、death 三种不管有没有变化都拍。
- **保留**：每个玩家留最近 50 份，再加上近 30 天里每个自然日（按 CST）最新的一份。每写一次就按这个规则清理一次（`InvSnapRules.prune`，有单测）。
- **配置**（`config.yml`，都可省略，代码里有默认值）：`invsnap.enabled`、`interval_minutes: 10`、`keep_last: 50`、`keep_days: 30`。

### 5.1 管理员命令（需要 `corerpg.admin`）

| 命令 | 作用 |
|---|---|
| `/corerpg invsnap list <玩家> [条数]` | 列出快照：id、CST 时间、原因、世界、格数 |
| `/corerpg invsnap view <玩家> <id>` | 打开只读箱子界面。第 1 页是背包：第 1–3 行背包、第 4 行快捷栏、第 5 行是护甲（36–39 格）和副手（40 格）。左下角按钮翻到第 2 页末影箱。界面里的点击和拖动全部取消 |
| `/corerpg invsnap restore <玩家> <id>` | 恢复，规则见下 |
| `/corerpg invsnap diff <玩家> <id>` | 把当前背包加末影箱和快照逐格比对（`ItemStack.equals`，包括 NBT），输出「完全一致 MATCH」或不一致的格子列表 |
| `/corerpg invsnap take <玩家>` | 立即拍一份 manual 快照 |

**恢复规则**：

- **先拍 pre-restore 快照**。它写不进去就不恢复。恢复完会提示一条撤销命令，形如 `restore <玩家> <pre-restore 的 id>`。
- **P1 装备防复制检查**：快照里每件带签名的 P1 装备，必须同时满足以下条件才会还原，否则那一格留空，并逐件报给管理员：
  - `cr_p1_item` 里状态是 active
  - 主人是这个玩家
  - 当前没有别的在线玩家拿着它
  - 同一份快照里没有重复出现

  举例：已经存进装备库（stored）、已分解、已被洗练吃掉、属于别人的装备，都会被跳过。
- 恢复后自动跑一次锻造同步：库里 rev 更新的装备按库里的数据重写，再刷新套装和装备栏。
- **玩家不在线**时，恢复请求写进 `plugins/CoreRpg/invsnap-pending.yml`，等他下次进服时执行（同样先拍 pre-restore）。

### 5.2 冒烟（10-04，FreshQ40）

1. 往背包放：附魔并命名的钻石剑、石头×64、附魔金苹果×5、成书、NI 余烬核心碎片×3；护甲位放附魔铁头盔、染色皮靴，副手放盾牌；末影箱第 0、5、26 格放钻石×10、命名纸×3、附魔书。
2. 拍快照 #4（18 格，包括新号自带的 T0 刃、T0 护符和 5 瓶回复药）。
3. `clear` 加清空末影箱后，`diff` 显示 18 格不一致。`restore 4` 跳过的 P1 装备为 0 件，恢复前状态存为 #5。再 `diff`，结果是**完全一致 MATCH**。
4. 离线恢复：机器人下线后执行 `restore FreshQ40 5`，请求排进队列。机器人上线后自动恢复（恢复前状态存为 #7），`diff 5` 一致。最后 `restore 4` 恢复回去，再 `diff 4` 一致。
5. 死亡快照：FreshQ41 在余烬连战里倒下，`cr_inv_snapshot` 里出现一条 `death` 记录（世界 `dungeon_EmberQ0B1_…`）。

## 6. 还没做 / 注意

- view 只读界面需要管理员本人在游戏里打开（机器人没有 OP，没有用机器人测）；控制台请用 diff。
- **快照不替代整服回档**。整服回档用 §1.2 的打包文件。
- 拍快照的机制挡不住「玩家用原版方式复制物品」。P1 装备另有 uid、rev、签名三重校验。
