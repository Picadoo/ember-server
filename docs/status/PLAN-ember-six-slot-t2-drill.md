# 余烬 · 六槽护甲 T2 测试服演练计划（余烬-测试 · 2026-10-08 · **已批 D320**）

## 总控批示 D320（2026-10-08 10:20 UTC+8）

计划 `5329227e` / `c6f885a9` 已批准。§9 待拍板各项定为：

| # | 批示 | 落到正文 |
|---|---|---|
| ① | 用**临时 mariadbd 实例**（独立 datadir、端口 3317、独立 socket），线上库零写入 | §1.2 |
| ② | **不加测试钩子**，用 jdb 断点 + kill -9；「异步 DB 写未落盘」那一侧记 SKIP，并列入残余风险 | §5-A、§6；见下方测试岗注 |
| ③ | **不接 Waterfall** | §1.3 |
| ④ | 孤儿标签按 **a+b** 处理，并顺手补存档 **(c)**，已派插件岗 | §5-A 的 A1、§5-E 的 E4 |
| ⑤ | **由守卫拦**：快照早于迁移完成就拒绝恢复。已派插件岗，场景里要加测 | §5-E 的 E5–E7 |
| ⑥ | **不装 DungeonPlus**，主线结算掉甲记 SKIP，留到 T3 | §3、§5-H 的 H8、§6 |
| 内存 | 可用内存 <1.5 GB 暂停，<1 GB 立停；线上 4 个服务全程不动 | §2、§7 |
| 节奏 | 可以分段跑，每段结束 commit + push 一份报告存档 | §7 |
| 启动 | 插件岗把含守卫的 tip 发来就开始执行，不必再问总控。最终交 STATUS 报告，并给出是否建议签「T2 过线」 | §7、§9 |

> **测试岗注（关于 ②）：** jdb 断点默认挂起**全部线程**，所以 kill -9 时异步 DB 写一定还没执行，jdb 实际覆盖的正是「异步写**未落**」这一侧。jdb 覆盖不到的是「异步写**已落**、主线程还没往下走」这一侧。执行时按机制把**「已落」侧记 SKIP**，并列入残余风险。这一侧在离线 World 模型里已覆盖（模型中 `remember` / `retire` 是同步写入），风险低。如果总控本意确实是另一侧，请更正。



- 依据：D319（T1 过线 @`e558e38f`），T2 硬前置 a–d。本计划覆盖 a（R1/R6 kill -9 演练）、c（事件级 T1-6）、d（`/ni reload` + 菜单 `ember_p1_armor`），并复核 b（invsnap 守卫）。
- 状态：**已批（D320），尚未执行，未起服。** 插件岗推送含 invsnap 守卫、孤儿标签修复和快照时间守卫的提交（下文记作 `<GUARD_SHA>`）后即开始执行，不再另问总控。
- 调研方式全部只读（2026-10-08 10:20 UTC+8，仓库 `a2268e39`）：读仓库、`ss -ltnp`、`free -m`、`ps`、`sha256sum`。本次没有起进程，没有建库，没有拷世界。
- 总不变量（贯穿全部场景）：**① 同一 uid 的有效副本 ≤1（有效 = DB 为 active，且不在作废名单）；② 原物多重集不变。** 原物的统计范围：身上 + 背包 + 末影箱 + 待领 + 地上/容器 + 带 `ember_six_m_*` 标签时 journal 里的原物。

## 1 隔离

### 1.1 目录与进程

| 项 | 值 |
|---|---|
| 根目录 | `/workspace/tmp/t2-drill/`（server/、db/、bots/、evidence/、snap/） |
| 测试服 | `/workspace/tmp/t2-drill/server/`：独立 cwd、独立 `plugins/` 实目录（**不用**指向 `../plugins` 的软链）、独立 `world*`（**新生成的空世界，不拷线上世界**） |
| 运行身份 | 与线上相同的 box 用户。进程统一用 `nice -n 10` 起；pid 文件放 `/workspace/tmp/t2-drill/*.pid` |
| JDK | `/workspace/minecraft/tools/jdk8u504-b01`（只读使用） |
| Paper | 只读拷贝 `server-runtime/paper-custom.jar`（现 sha256 前缀 `97b67a80ce32`） |

### 1.2 DB 方案

**已定（D320 ①）：临时 mariadbd 实例**，线上库零写入。B 方案不采用，下表保留作为记录。

| | **A 临时 mariadbd（已定）** | B 线上 MariaDB 建 schema + 用户（不采用） |
|---|---|---|
| 隔离 | 独立 datadir、端口、socket，与线上进程和数据零交集 | 同一个进程、同一份 `mysql.user`，共享 buffer pool 和连接数 |
| 对线上的写入 | **0** | 要写 `CREATE DATABASE` / `CREATE USER` / `GRANT`，这是对线上 DB 的写操作，和「不写线上 DB」冲突 |
| 误连风险 | 测试服连接串只指向 3317 端口，配错就连不上，不会落进 ember 库 | 连接串只差库名，配错会直接写进线上 `ember` |
| kill -9 演练 | 可以对临时库 kill -9，验证「DB 写丢 → join 重发」 | 不能对线上库做 |
| 内存 | +约 200 MB（buffer pool 64 MB） | +约 0 |
| 清场 | 停进程 + `rm -rf db/` | DROP DATABASE / DROP USER，又是线上写 |

A 的启动方式（拿到 `<GUARD_SHA>` 后在步 1 执行，现在不执行）：
```
mariadb-install-db --no-defaults --datadir=/workspace/tmp/t2-drill/db/data --auth-root-authentication-method=socket
mariadbd --no-defaults --datadir=/workspace/tmp/t2-drill/db/data --port=3317 --bind-address=127.0.0.1 \
  --socket=/workspace/tmp/t2-drill/db/mysqld.sock --pid-file=/workspace/tmp/t2-drill/db/mysqld.pid \
  --innodb-buffer-pool-size=64M --max-connections=40 --skip-name-resolve --log-error=/workspace/tmp/t2-drill/db/err.log &
```
- 库 `ember_t2`；用户 `t2drill@127.0.0.1`，密码随机生成，只写进测试服的 `config.yml`（文件权限 600）。
- 表结构由 CoreRpg 启动时自动建（`plugins/CoreRpg/schema.sql`，注释写明 "Auto-applied on enable when storage: mysql"）；InvSnap 的 `cr_inv_snapshot` 同样自动建。

### 1.3 不接入 Waterfall

**已定（D320 ③）：** 测试服**不接代理**，mineflayer 直连 `127.0.0.1:25577`。理由：
1. 接入要改线上 `proxy-runtime/config.yml` 的 `servers:` 段（现有 login@25566、play@25567），这属于动线上配置；
2. 接入后线上玩家可能被路由到测试服，测试号也可能进到线上；
3. 演练要对测试服反复 kill -9，代理侧会产生断线噪声；
4. 迁移逻辑在游玩服本地执行，与代理无关。换服场景用「退出 → 重连测试服」和「同服换世界」覆盖，跨代理换服留给 T3 的线上冒烟。

### 1.4 端口（2026-10-08 10:20 `ss -ltnp` 实查）

已占用：
- 25565（Waterfall）、25566（登录服）、25567（游玩服）、3306（线上 MariaDB）；
- 1337–1340、2375、6080、6081、8790、8791、26500、50052；
- 5900、5902、5903、5905、5906、5908、5909（x11vnc）；
- 13602–13609、14002–14009（node）。

| 用途 | 端口 | 绑定 | 状态 |
|---|---|---|---|
| 测试服游戏端口 | **25577** | 127.0.0.1 | 空闲 |
| 临时 mariadbd | **3317**，另有 socket `t2-drill/db/mysqld.sock` | 127.0.0.1 | 空闲 |
| JDWP（jdb 定点 kill，§5-A） | **25578** | 127.0.0.1（`address=127.0.0.1:25578`） | 空闲 |
| RCON / query | 关闭 | — | — |

起服前用 `ss -ltn | grep -E ':(25577|25578|3317)\b'` 复查，三个端口都必须为空；有占用就换成 25587/25588/3327，并记录在证据里。

### 1.5 来源于线上配置、必须改掉的项

测试服的配置一律从 **git 的 `<DRILL_SHA>`** 用 `git archive` 导出到 `t2-drill/server/`，不从线上运行目录拷，因为线上目录里有运行时改动和真实玩家档。导出后改以下各项：

| 文件 | 线上值 | 测试服值 | 原因 |
|---|---|---|---|
| `server.properties` `server-port` | 25567 | **25577** | 端口隔离 |
| `server-ip` | 127.0.0.1 | 127.0.0.1 | 保持只监听本机 |
| `online-mode` | false | false | mineflayer 离线号直连 |
| `gamemode` | **1（创造）** | **0**，另加 `force-gamemode=true` | 创造模式的背包包走 creative set-slot，绕过点击事件，会让 T1-6 的结论失真 |
| `level-name` | world | `world`（新生成）；`level-type=FLAT`，`generate-structures=false` | 不拷世界，平地加载快 |
| `enable-rcon` / `enable-query` | false | false | — |
| `motd` | CoreSmelt test | `EMBER T2 DRILL – NOT LIVE` | 防止认错服 |
| `spigot.yml` `settings.bungeecord` | **true** | **false** | 不接代理；为 true 时直连会被拒 |
| `spigot.yml` `settings.restart-on-crash` | — | false | kill -9 后由人工重启，不能自动拉起 |
| `paper.yml` | — | 不改 | — |
| `ops.json` / `whitelist.json` | 线上名单 | **只放测试号**，`white-list=true` | 临时 op 只在测试服上给 |
| `plugins/CoreRpg/config.yml` `storage` | mysql | mysql | 要测 DB 路径（R4 前提是 store 可用） |
| `mysql.host/port/database/username/password` | 127.0.0.1:3306 / ember / ember / *** | **127.0.0.1:3317 / ember_t2 / t2drill / 随机** | 绝不连线上库 |
| `mysql.pool-size` | 10 | 4 | 省内存 |
| `plugins/CoreRpg/ember-v1.yml` | 无 `gear.six_slot` | 加 `gear.six_slot.enabled: true`、`gear.six_slot.migrate: true` | 演练要开开关；线上文件不动 |
| `plugins/CoreRpg/ember-v1-item.key` | 线上签名密钥 | **不拷**，首次启动自动生成新密钥 | 用线上密钥签出来的测试物品在线上会被当成有效件；独立密钥让测试件在线上一律验签失败 |
| `plugins/CoreRpg/players/`、`mail/`、`guilds/`、`p1-runs/`、`p1-six/`、`warehouse*`、`*-state.yml` | 真实玩家数据（git 中也有跟踪） | **全部删除，从空开始** | 不碰真实玩家档 |
| `plugins/CoreGacha/config.yml` | 运行时读 CoreRpg 的 `mysql:` 节 | 跟着 CoreRpg 改到 3317；不装 CoreGacha 时无关 | 防止误连 |
| `plugins/TrMenu/settings.yml` `Database.Method` | SQLITE（本地） | SQLITE，放新目录 | 不带线上 sqlite 文件 |
| LuckPerms `storage-method` | h2（本地文件） | 不装 LuckPerms，用 ops.json | 省内存，也不碰线上 h2 |
| DungeonPlus / Adyeshach / AttributePlus 的 `datasource.yml` | 只是 Hikari 模板，实际用本地 `data.db` | 这些插件不装 | 与演练无关 |
| `plugins/TrMenu/menus/ember_p1_armor.yml` | 线上没有（在 staged 目录） | 从 `docs/design/staged/d318-six-slot/trmenu/` 拷入，并把 `ember_p1_gear.armor-slot.snippet.yml` 合进测试服的 `ember_p1_gear.yml` | 前置 d |

## 2 内存预算

现状（10:20）：总量 16013 MB，available 约 6566 MB，无 swap。线上进程常驻内存：游玩服约 1187 MB，登录服约 534 MB，Waterfall 约 351 MB，MariaDB 约 132 MB。

| 组件 | 上限 / 估算 |
|---|---|
| 测试服 Paper | `-Xms768M -Xmx1536M -XX:+UseSerialGC -XX:MaxMetaspaceSize=192M`，常驻约 **1.9 GB** |
| 临时 mariadbd | 约 **0.2 GB** |
| mineflayer | 同时最多 2 个 bot，各约 0.15–0.25 GB，合计 **≤0.5 GB** |
| jdb（只在 §5-A 期间） | 约 **0.1 GB** |
| node 解析 .dat 的脚本 | 用完即退，约 0.1 GB |
| **合计峰值** | **约 2.8 GB**，演练期间 available 预计保持在约 3.7 GB |

- 监控：`free -m` 每 5 秒记一次，写到 `t2-drill/evidence/mem.log`（只读脚本）。
- **中止阈值：**
  - available **< 1.5 GB**：暂停，不再开新 bot / jdb，当前场景收尾；
  - available **< 1 GB**：立即正常停测试服（`stop`），停不下来就 kill 测试服 pid（只杀 `t2-drill/*.pid` 里的 pid），再停临时库。
  - 任何情况下都不碰线上 4 个进程。

## 3 构件

| 构件 | 来源 | 记录 |
|---|---|---|
| CoreRpg.jar | `git worktree add /workspace/tmp/t2-drill/src <DRILL_SHA>`，执行 `nice -n 19 mvn -o -B package -DskipTests`（单线程、小堆），产物 `CoreRpg/target/CoreRpg-*.jar`。构建前在同一 worktree 跑一次全量测试，应为 646/2 或更新后的基线 | `<DRILL_SHA>` = e558e38f 之后、**包含 `<GUARD_SHA>`（invsnap 守卫）** 的 origin/main tip；jar 的 sha256 写进证据 |
| invsnap 守卫 | 插件岗提交 | **`<GUARD_SHA>` 占位**，执行前填入 |
| Paper | `server-runtime/paper-custom.jar` | 97b67a80ce32… |
| NeigeItems | `plugins/NeigeItems-1.21.151.jar` | 4d91ead56cec… |
| NI 模板 | git 里的 `plugins/NeigeItems/Items/ember-armor-v1.yml`（96201025 引入，40 个模板） | 6bdbb14cd1bb… |
| PlaceholderAPI | `plugins/PlaceholderAPI.jar` | 20a74f102d0f… |
| TrMenu | `plugins/TrMenu.jar`，加菜单 `ember_p1_gear.yml`（git）和 `ember_p1_armor.yml`（staged） | 8ee31e677ac8… |
| ProtocolLib | `plugins/ProtocolLib.jar` | 8932f867d162… |
| Vault | `plugins/Vault.jar` | a6b5ed97f43a… |
| Multiverse-Core | `plugins/Multiverse-Core.jar`（建第二个平地世界 `t2_alt`，用于换世界场景） | f43b8aa54870… |
| 不装 | MythicMobs、Adyeshach、**DungeonPlus（D320 ⑥）**、HolographicDisplays、LuckPerms、Multiverse-Portals、spark、Core* 小插件 | 与演练无关，省内存；CoreRpg 对这些都是 softdepend |

- jar 一律用 `cp --preserve=timestamps` **只读**拷入 `t2-drill/server/plugins/`，拷完核对 sha256。线上 `plugins/` 里不新增、不修改任何文件。
- NI、PAPI、TrMenu 的配置目录：从 git 的 `<DRILL_SHA>` 导出（`plugins/NeigeItems/`、`plugins/TrMenu/`），不导出 `players/`、`*.db`、`data/`。
- 开关：测试服的 `ember-v1.yml` 中 `gear.six_slot.enabled=true`、`migrate=true`。H 组有一项要先在开关关的状态下造「迁移前」档，造完再开。

## 4 测试数据

- 测试号：离线号 `t2a`、`t2b`、`t2c`、`t2d`、`t2e`（UUID 按 `OfflinePlayer:<name>` 生成）、`t2op`（只在测试服 ops.json 里，用来发管理命令），以及 `t2peer`（「他人 P1 甲」的来源）。
- 「迁移前」档**全部合成**，不拷真实玩家档。步骤：开关关（`enabled=false`）起服 → bot 进服 → `t2op` 用 `/give` 和 CoreRpg 管理命令 `corerpg p1 give <族> <部位> <阶> [成色] [精工] [强化] [玩家]` 发刃和护符 → bot 在装备页选定。五个号对应 T1 的五个 case：

| 号 | 对应 case | 「迁移前」状态 |
|---|---|---|
| t2a | case 0 | 有护符；甲位有原版铁盔、钻石护腿，以及 t2peer 的 P1 靴 |
| t2b | case 1 | 无护符；只穿皮胸 |
| t2c | case 2 | T3 q3 f5 护符；甲位空 |
| t2d | case 3 | 背包 36 格满，4 个甲位全占（原版） |
| t2e | case 4 | T0 护符；锁链盔 |

- 造完档后**正常 `stop`**，把 `world/playerdata/*.dat`、`plugins/CoreRpg/players/`、临时库 dump（`mariadb-dump`，只针对 3317）存进 `t2-drill/snap/pre/`。之后每轮场景都从这份快照恢复（只在测试目录内复制），保证初始条件一致。
- 如果必须参考真实档的结构（例如 NBT 字段名），只读一个线上 `.dat` 的**键结构**（不含值），用 node `prismarine-nbt` 打印字段树写进笔记，不拷文件、不拷值。默认不需要。
- 临时 op：只写测试服的 `ops.json`。清场时随目录一起删除。

## 5 场景矩阵

通用证据（每个场景都收）：
- 测试服 `logs/latest.log` 片段（`[P1 six]`、`invsnap`、alert）；
- `corerpg p1 armor status <号>` 输出；
- `p1-six/<uuid>.yml` 副本；
- 用 node `prismarine-nbt` 把 `world/playerdata/<uuid>.dat` 解析成 JSON（Inventory 的 100–103 号槽、背包、EnderItems、Tags 里的 `ember_six_m_*` / `ember_six_c_*`）；
- 临时库 `SELECT item_uid,state,owner FROM cr_p1_item WHERE owner=…`；
- `corerpg p1 audit` 输出；
- 不变量核对脚本 `t2-drill/bots/inv-check.js` 的结果（扫描上述来源和地上掉落物，算 ①②）。

### A · R1：每个写入点 kill -9（前置 a）

写点清单（读码 `EmberSixMigration.java` @e558e38f；Live 指 `EmberSixSlotService.LivePort`）：

| 组 | 写点（按执行顺序） | 代码行 |
|---|---|---|
| M 首次迁移 | M0 `retire(voided)`（只在 voided 非空时）→ M1 `create`×4（NI 建物品，无持久化）→ **M2** `save(journal PREPARED)` 写 `p1-six/<uuid>.yml` → **M3** `apply(换甲 + 标签)`（只改内存）→ **M4** `persistInventory` 写 `.dat` → **M5** `remember`×4（信任缓存 + 异步 DB active）→ **M6** `save(flag + 待领 + 清 journal)` → **M7** `apply(null, 去标签)`（只改内存；@e558e38f 之后不再存档）→ **M8** 补存档（D320 ④ c，以 `<GUARD_SHA>` 为准）；另有 ALREADY 路径清孤儿标签（④ b） | 193、210、220、222、223、372、378、380 |
| F 续上 / 重发 | F1 `create` 新 uid → **F2** `save(voided + journal)` → **F3** `retire(旧 uid)` → **F4** `setArmor(新件)` → **F5** `persistInventory`，然后走 M5–M7 | 342、352、354、355、357 |
| R 自检失败撤回 | **R1** `save(reverting)` → **R2** `save(被占槽原物进待领)` → R3 `take` → **R4** `apply(原物 + 去标签)` → **R5** `persistInventory` → **R6** `save(done, voided)` → **R7** `retire` | 240、298、304、306、307、311、313 |
| D 未换装就丢弃 journal | D1 `take` → **D2** `persistInventory` → **D3** `save(voided)` → **D4** `retire` | 262、263、267、269 |
| C 待领领取 | C1 settle `persist` → C2 settle `save` → C3 `untagExcept` → **C4** `give(物品 + 标签)` → **C5** `persist` → **C6** `save(删条目)` → **C7** `untag` → **C8** 最后一次 `persist` | 429、459（settle）、claim 循环 |
| E 护甲页换上 | **E1** `swapIn`：`setItem` + `setArmorContents` + `savePlayerFile` | Service 479–495 |

**定点触发方式：** 代码里**没有**测试钩子或系统属性（`rg getProperty|getenv` 在两个类里 0 处命中）。两种办法：

**已定（D320 ②）：不加钩子，只用 jdb 断点 + kill -9。** 写点行号以 `<DRILL_SHA>` 为准：④ 的补存档会让 `commit` 附近的行号移动，执行前用 `rg -n` 重新核对上表。

1. **jdb 断点（采用）。**
   - 测试服加 `-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=127.0.0.1:25578` 启动；
   - 执行 `jdb -attach 127.0.0.1:25578`，然后 `stop at town.sunshine.corerpg.p1.EmberSixMigration:<行>`，行号用上表；
   - 断点命中时主线程（默认是全部线程）挂起，**立刻 `kill -9 <测试服 pid>`**，效果等于「在该语句执行之前进程死亡」；
   - 同一断点跑两轮：「命中即杀」和「`next` 单步后再杀」，覆盖写点的前后两侧；
   - R 组需要强制自检失败：在 238 行（`if (post != null)`，post 已算完）断下，执行 `set post = "drill"`，后续即走撤回路径；
   - 风险：Paper watchdog 默认 60 秒报警。断点到 kill 控制在 10 秒内，必要时把 `spigot.yml` `timeout-time` 调大到 300（只改测试服）。
   - 构建默认带行号（maven `-g`），构建后用 `javap -l` 抽查确认。
2. ~~测试钩子（D320 ② 不加，保留作为记录）。~~
   - 系统属性 `-Dember.six.drill.haltAt=<写点名>`，在指定写点之后调用 `Runtime.getRuntime().halt(137)`，等价于 kill -9：不跑 shutdown hook，不存档；
   - 只有属性存在时才生效，线上不设，零影响；
   - 好处是可以脚本化批量跑，还能区分「异步 DB 写已落 / 未落」：只挂起主线程，等异步线程写完再 halt。jdb 默认挂起全部线程，只能覆盖「异步写未落」的情况。

**SKIP（D320 ②）：** jdb 覆盖不到的一侧（异步 DB 写已落、主线程还没往下走）在 A 组里统一记 SKIP，并列入残余风险。对应关系见文件顶部的测试岗注。

**用例**（每例都是：从 `snap/pre/` 恢复 → 起服 → bot 进服（迁移在进服后第 120 tick 触发）→ 断点处 kill -9 → 重启测试服 → bot 重新进服 → 续上直到 ALREADY → 再 kill -9 一次 → 重启并核对）：

| 编号 | 内容 | 例数 |
|---|---|---|
| A1 | M2…M7 每个写点的前、后两侧；**M7 之后、M8 补存档（D320 ④ c）之前**（孤儿标签窗口）；**M8 之后** | 8 |
| A2 | F 组（先让 A1 停在 M4 之后，再让 bot 把迁移甲移到背包或死亡掉落，以此进入 F 路径）：F2…F5 | 5 |
| A3 | R 组（用 `set post` 强制撤回）：R1、R2、R4、R5、R6、R7，另加 1 例「撤回中途玩家往空槽穿自己的东西」 | 7 |
| A4 | D 组（A1 停在 M4 之前，此时 journal 未带标签）：D1…D4 | 4 |
| A5 | C 组：C1…C8 | 8 |
| A6 | E1 前后各一次 | 2 |
| A7 | 待决窗口内玩家动作 × 3 个关键点（M4 后 / F2 后 / R2 后）× {不动、死亡掉落、穿到背包后重登} | 9 |
| | **合计** | **43** |

- **断言：** 不变量 ①②；最终 `flag=1`、journal 为空；身上加待领的迁移甲每个部位恰好 1 件有效；所有作废 uid 在临时库里是 `retired`（异步写丢失的，进服后重发）；`audit` 无 `DUP`，作废件只报 HELD_NOT_ACTIVE（R5，属于预期）。
- **A1 孤儿标签窗口（D320 ④，按 a+b+c 修后的断言）**：
  - 在 M7（内存去标签）与 M8（补存档）之间 kill -9：磁盘上的 `.dat` 可能残留 `ember_six_m_*`，flag=1；
  - 重启后 bot 回枢纽，run 走 ALREADY，(b) 清掉孤儿标签，之后存档的 `.dat` **不再带标签**；
  - invsnap 守卫只看「journal 未决」(a)，所以恢复照常；
  - M8 之后 kill：`.dat` 不带标签。
  - 详见 E4。

### B · 标签 `ember_six_m_*` 能否跨事件保留

做法：让 A1 停在 M4 之后，kill -9 并重启，得到一个「journal 未决 + 带标签」的号。不让它进服续上：把迁移触发推迟，即在 `ember_afk` 世界，或在 `atHub=false` 的状态下进服。然后分别执行：

| 编号 | 事件 | 断言 |
|---|---|---|
| B1 | 死亡（`/kill`）→ 重生 | 重生后标签仍在（MC-85730 已在 1.9 修复，需要实测）；迁移甲按 T1-6 的口径掉落或保留；回枢纽续上后 ①② 成立 |
| B2 | 退出 → 重连 | 标签在 |
| B3 | 换世界（`mv tp t2_alt` 再回来） | 标签在；`onWorld` 第 40 tick 续上 |
| B4 | 测试服正常 `stop` → 起服 | 标签在 |
| B5 | `save-all` 后 kill -9 → 起服 | 标签在 |

证据：每步前后解析 `.dat` 里的 Tags，并截取聊天和日志。共 **5 例**。

### C · R6：标签与甲位落在同一个 `.dat`

- 每次 A 组 kill -9 之后、重启之前，解析 `.dat`，核对：带 `ember_six_m_<X>` ⇔ 100–103 号槽是 journal `issued` 里的 uid；不带 ⇔ 是 `originals`。**任何「标签与甲位不一致」都判阻塞**，这就是 R6 的反例。
- 另外 3 个定点：M4 之后、F5 之后、R5 之后，各连续做 5 次 kill -9（共 15 次），统计不一致次数（应为 0）。同时记录 `.dat` 和 `.dat_old` 的存在情况与 mtime，观察 Paper「写 tmp → 删旧 → 改名」窗口内被 kill 时的表现（这是 Paper 本身的通用风险，单独记录，不算六槽缺陷）。
- 共 **3 例**（15 次 kill）。

### D · 事件级 T1-6（前置 c，规格 T1-6 条目）

用 mineflayer 发真实的窗口点击（`clickWindow` 的 mode：0 拾取/放下 = 拖放，1 = shift，2 = 数字键）。号已迁移，身上 4 件迁移甲，背包里另有 P1 甲和原版甲。

| 编号 | 动作 | 断言 |
|---|---|---|
| D1 | 拖放：甲位 ↔ 背包 | ①②；`markDirty` 后生命和防御按新件刷新 |
| D2 | shift 点击穿 / 脱 | 同上 |
| D3 | 数字键交换甲位与快捷栏 | 同上 |
| D4 | 发射器装甲：`t2op` 放发射器并装原版甲，红石触发，bot 站在发射器前且对应甲位空 | 原版甲上身，P1 件不受影响；①② |
| D5 | 盔甲架：bot 手持 P1 甲右键盔甲架 | 被 `EmberBindGuard.onStand` 拦截，提示「不能放到盔甲架上」；从盔甲架拿原版甲正常 |
| D6 | 死亡掉落（`gamerule keepInventory false`） | 按 CoreRpg 死亡口径；掉落的 P1 件捡回后 uid 不变、仍有效；无复制 |
| D7 | 换世界（`t2_alt` 往返） | 甲位不变；①② |
| D8 | 丢出（Q 键） | 被 `onDrop` 拦截 |
| D9 | 放箱子 / 漏斗 / shift 进箱子 / 数字键进箱子 | 被 `onContainerClick` / `onContainerDrag` 拦截 |
| D10 | 展示框 | 被 `onFrame` 拦截 |
| D11 | 甲 → 装备库 / 仓库（`corerpg p1 gearlib|vault` 菜单）→ 取回 | uid 不变；DB owner 不变；①② |
| D12 | 甲 → 分解（工坊） | 件作废，白板胚按 ×0.1 记账；审计记账 |
| D13 | 两件同 uid 防复制：用 `invsnap` 快照恢复去「复制」一件已在身上的 P1 甲 | 被 P1 信任检查跳过（`skipped P1`） |
| D14 | 末影箱进出 | 允许（不是 world storage）；①② |

最后执行 `corerpg p1 audit`，要求 0 差异（作废件只报 HELD_NOT_ACTIVE，属于预期）。共 **14 例**。

### E · invsnap 守卫（前置 b，`<GUARD_SHA>`）

| 编号 | 前置状态 | 动作 | 断言 |
|---|---|---|---|
| E1 | journal 未决 + 带标签（A1 停在 M4 后，kill，重启，在非枢纽进服） | `corerpg invsnap restore <号> <迁移前快照>` | **拒绝**，并提示原因；背包、甲位、标签都不变 |
| E2 | 同 E1，号离线 | 排队恢复 → 号进服（第 40 tick 执行，早于迁移的第 120 tick） | 排队恢复在进服时**被拒并保留排队**，不会先恢复再续上；不出现「原物翻倍」 |
| E3 | 无 journal（已迁移，flag=1，磁盘上无孤儿标签） | 恢复到迁移**后**的快照 | **正常恢复**；P1 信任检查照常工作 |
| E4 | 孤儿标签（D320 ④）：迁移收尾时在 M7 与 M8 之间 kill -9 → 重启 → bot 回枢纽 | 解析 `.dat`；再恢复到迁移**后**的快照 | 重启后孤儿标签被清（下一次存档的 `.dat` 无 `ember_six_m_*`）；`armor status` 显示 flag=1、journal 为空；invsnap **正常恢复**；①② 成立 |
| E5 | 已迁移（D320 ⑤） | 恢复到迁移**前**的快照（快照时间早于迁移完成） | **拒绝**，并提示原因；背包、甲位、待领都不变；原物不翻倍 |
| E6 | 已迁移（D320 ⑤ 边界） | 恢复到迁移**进行中**拍的快照（journal 未决期间，例如死亡快照，时间早于迁移完成） | **拒绝** |
| E7 | 已迁移、号离线（D320 ⑤） | 排队恢复一份迁移前的快照 → 号进服（第 40 tick） | 进服时**被拒**，排队保留并记录原因，不会先恢复；原物不翻倍 |

共 **7 例**。

### F · `/ni reload`（前置 d）

| 编号 | 动作 | 断言 |
|---|---|---|
| F1 | 身上 4 件 + 背包 2 件迁移甲，执行 `/ni reload` 前后 | `.dat` 里 `AttributeModifiers` 的 armor / armorToughness = 0；`Unbreakable=1`；HideFlags 隐藏属性；客户端（mineflayer 读 `bot.entity.attributes['generic.armor']`）护甲值 = 0；CoreRpg 生命 / 防御不变 |
| F2 | reload 后新发一件（`corerpg p1 give`）以及重发一件（F 路径） | 同上；uid 与签名有效 |

共 **2 例**。

### G · 菜单 `ember_p1_armor` 整套流程（mineflayer，不敲玩家命令）

bot 只通过 GUI 点击操作：`/ember` → 枢纽「装备」→ `ember_p1_gear` 的「护甲」格 → `ember_p1_armor`。

| 编号 | 动作 | 断言 |
|---|---|---|
| G1 | 打开 | 4 格穿着、4 格候选、S/A/L/R 全部渲染；无未解析的 `%corerpg_p1_*%`；玩家文案里没有命令教学 |
| G2 | 排序 | 背包放 3 件同部位候选（不同族 / 阶 / 获得时间）：候选格显示的那件符合 §5.4③ 排序（生命差 → 现穿同族 → 护符同族 → 掉落阶 → 获得时间新者 → uid） |
| G3 | 单件换上 | 换下的件回到背包同一格位；①②；`.dat` 已存 |
| G4 | 全部换上 | 第一次点击只提示；30 秒内再点一次才执行；超时后重新计时 |
| G5 | 待领领取（含背包满） | 领到的件进背包；背包满时余下的留在待领；领完 `ember_six_c_*` 标签清空 |
| G6 | 防连点 | 在 1 tick 内连点「领取」10 次，以及连点「换上」10 次：只执行一次（`claimBusy` / swap 检查）；无复制 |
| G7 | 换上 / 领取后关闭再打开 | 显示与实际一致 |
| G8 | 开关关（另起一轮，`enabled=false`） | 护甲格显示普通玻璃板；任何点击只回「护甲功能尚未开放」 |

证据：bot 记录的窗口快照 JSON 和聊天记录、`.dat` 解析结果、日志。共 **8 例**。

### H · 迁移主流程

| 编号 | 动作 | 断言 |
|---|---|---|
| H1 | t2a…t2e 首次进服（开关开） | 第 120 tick 输出 MIGRATED；H/D（生命、防御）与迁移前**逐位相等**（`corerpg p1` 面板数值和日志）；原物全部进待领；每人 4 件有效 |
| H2 | 幂等：每人再重登 3 次，并执行 `corerpg p1 armor mig <号>` | 都是 ALREADY；DB 行数不增 |
| H3 | 旧 uid 作废：A2 里重发过的号 | 旧 uid 在 `p1-six/<uuid>.yml` 的 `voided` 里；临时库中为 `retired`；旧件穿上后 `dbCheck` 拒绝（护甲页不认、属性不加） |
| H4 | DB 写丢后重发：kill 掉临时 mariadbd → 让号走 F 路径 → 起库 → 号重登 | 进服时 `resendVoids` 补写 retired |
| H5 | 开关关（`migrate=false`） | 进服不迁移，无新行 |
| H6 | 缺 NI 模板（测试服暂时挪走 `ember-armor-v1.yml` 后执行 `/ni reload`） | NO_TEMPLATE，不发不扣；恢复模板后可以正常迁移 |
| H7 | `corerpg p1 audit` 全员 | 0 差异 |
| ~~H8~~ | **SKIP（D320 ⑥）**：主线结算掉甲冒烟，测试服不装 DungeonPlus，留到 T3 | — |

执行 **7 例**，H8 记 SKIP。

**场景总数（D320 后）：执行 A 43 + B 5 + C 3 + D 14 + E 7 + F 2 + G 8 + H 7 = 89；另有 SKIP 1 例（H8）。** A 组每个写点 jdb 覆盖不到的「异步写已落」一侧统一记 SKIP，这是覆盖缺口，不单独计入场景数。

变更说明（相对 87）：
- A1 +1（M8 补存档后增加一个写点）；
- E +2（E4 改为孤儿标签断言；E5 改为拒绝断言；新增 E6、E7）；
- H8 从执行改为 SKIP（−1）。

## 6 通过标准

| 等级 | 判定 |
|---|---|
| **阻塞（T2 不过）** | 任意场景违反总不变量 ①（同一 uid 出现 2 份有效副本）或 ②（原物丢失或翻倍）；C 组出现标签与甲位不一致；B 组标签丢失；E1/E2/E5/E6/E7 守卫没拦住（导致翻倍或恢复了早于迁移完成的快照）；E4 孤儿标签重启后没清、或 invsnap 被误拒；F 组护甲值≠0 或属性外露；G6 连点产生复制；H1 的 H/D 不相等；H2 不幂等；作废 uid 在 DB 恢复并重登后仍是 active；测试服写到了线上路径或线上库（立即中止，见 §7） |
| **需修，但可以带条件过（总控定）** | 菜单文案或排序与 §5.4③ 有出入但不影响资产；D13 的提示文案；`.dat` 改名窗口的 Paper 通用风险（C 组附带记录） |
| **可接受（预期）** | 作废件残留在世界里，`audit` 报 HELD_NOT_ACTIVE（R5）；kill -9 后异步 DB 写丢失、进服时补写（R7）；DB 宕机时迁移暂停或延后（R4，运维手册） |
| **SKIP（需注明，列入残余风险）** | H8 主线结算掉甲（D320 ⑥，不装 DungeonPlus，留到 T3）；A 组「异步 DB 写已落」一侧（D320 ②，jdb 覆盖不到；离线 World 模型已覆盖）；跨代理换服（D320 ③，留给 T3 线上冒烟） |

每条场景都要在 `t2-drill/evidence/<编号>/` 下留证据（日志片段、`.dat` JSON、yml、SQL 输出、bot 记录），报告里逐条写 PASS / FAIL / SKIP。

## 7 执行顺序、估时、中止条件

| 步 | 内容 | 估时 |
|---|---|---|
| 0 | 前置确认（`<GUARD_SHA>` 已推送，含守卫、孤儿标签修复和快照时间守卫），测前快照哈希（§8.1），端口复查，`free -m` | 0.3 h |
| 1 | worktree 构建与全量测试，导出配置，改配置，起临时库 | 0.8 h |
| 2 | 关开关起服，合成 5 个「迁移前」档，存进 `snap/pre` | 0.5 h |
| 3 | H1、H2、H5、H6（主流程，先确认基本面） | 0.5 h |
| 4 | F、G（菜单和 reload） | 1.0 h |
| 5 | D（事件级） | 1.0 h |
| 6 | A + C（jdb 定点 kill，43 例；每例约 4 分钟，含两次重启） | 3.0 h |
| 7 | B、E（7 例）、H3、H4、H7 | 1.0 h |
| 8 | 写报告，清场（§8） | 0.6 h |
| | **合计** | **约 8.7 h**，分段执行（D320）：**段 1** 为步 0–5，**段 2** 为步 6–8；两段之间正常停测试服 |

**分段存档（D320）：** 每段结束时把进度报告 `docs/status/STATUS-ember-six-slot-t2-drill-<日期>.md` commit + push（只 add 报告；推送被拒就 `pull --ff-only`，不强推）。最后一段交完整的 STATUS 报告，逐条写 PASS / FAIL / SKIP，并给出是否建议签「T2 过线」。

**中止条件（任一触发就停）：**
- available < 1.5 GB 暂停、< 1 GB 立停（D320，见 §2）；
- 线上 4 个进程（Waterfall、登录服、游玩服、MariaDB）任一异常（pid 消失、端口不在），或游玩服 TPS 明显下降；
- 发现测试服进程写到了 `/workspace/minecraft/**` 下的任何文件：用 §8.1 的 stamp 文件配合 `find -newer` 检查，只看测试进程打开的文件（`ls -l /proc/<pid>/fd`）；
- 发现测试服连到了 3306；
- 磁盘剩余 < 5 GB；
- 同一阻塞缺陷已复现 2 次：保留证据，不修代码，停下回报。

## 8 清场

### 8.1 测前快照（步 0，只读）

- `sha256sum` 写入 `t2-drill/snap/live-before.sha256`：
  - `server-runtime/{server.properties,spigot.yml,paper.yml,bukkit.yml,ops.json,whitelist.json,paper-custom.jar}`
  - `login-runtime/{server.properties,spigot.yml,paper.yml,ops.json}`
  - `proxy-runtime/{config.yml,waterfall.yml,waterfall.jar}`
  - `plugins/*.jar`
  - `plugins/CoreRpg/{config.yml,ember-v1.yml,ember-v1-item.key}`
  - `plugins/NeigeItems/Items/*.yml`、`plugins/TrMenu/menus/*.yml`
- `touch t2-drill/snap/stamp`。
- 线上 MariaDB 只读快照：
  - `SHOW DATABASES`
  - `SELECT user,host FROM mysql.user`
  - `SELECT COUNT(*) FROM ember.cr_p1_item`，以及各 state 的计数
  - `CHECKSUM TABLE ember.cr_p1_item, ember.cr_inv_snapshot`
  - 会话 `READ ONLY`
- `ps` 记录线上 4 个 pid 和启动时间。

### 8.2 清场步骤

1. 测试服执行 `stop`（console）；超过 60 秒没退出，就 kill `t2-drill/server.pid` 里的 pid（只杀这一个）。
2. bot 和 jdb 退出，核对 `pgrep -f t2-drill` 为空。
3. 停临时库：`mariadb-admin --socket=t2-drill/db/mysqld.sock shutdown`。如果用的是 B 方案：`DROP DATABASE ember_t2; DROP USER 't2drill'@'127.0.0.1'`（需另行授权）。
4. `git worktree remove /workspace/tmp/t2-drill/src`。证据目录 `evidence/` 打包成 `/workspace/tmp/t2-drill-evidence-<日期>.tar.xz` 后保留，其余 `rm -rf /workspace/tmp/t2-drill/`。
5. 核对：
   - `sha256sum -c live-before.sha256` 全部 OK。线上运行时文件（玩家档、状态 yml）本来就会随线上游玩变化，所以只核对上面列出的配置、jar 和密钥；
   - `find /workspace/minecraft -newer stamp` 的结果里没有测试进程造成的改动（对照线上服自身的写入规律，例如 `players/*.yml`、logs）；
   - 线上 DB 只读快照与测前一致（库列表、用户列表一致；`cr_p1_item` 只允许有线上游玩造成的变化，不能出现 `armor`、`migrate` 或测试号 owner）；
   - `ss -ltn` 中 25577、25578、3317 已释放；
   - 线上 4 个 pid 与启动时间未变。
6. 报告写明清场结果和内存最低值。

## 9 前置依赖与已定事项（D320）

**前置依赖（不满足就不执行）：**
1. **`<GUARD_SHA>`：插件岗的提交（已派）。** 需包含：invsnap 守卫（a：只看 journal 未决）、孤儿标签清理（b：ALREADY 时清）、`commit` 补存档（c），以及快照早于迁移完成就拒绝恢复（⑤）。要已推送到 origin/main，并有离线单测；E 组和 A1 以它为准。
2. ~~总控批准本计划~~：**已批（D320）**。拿到 `<GUARD_SHA>` 即执行，不再另问总控。
3. 测试服 jar 的 `<DRILL_SHA>`（含守卫）全量测试结果与基线一致（646/2 或更新后的基线）。

**已定（D320）：**
1. **DB 方案**：已定 A，临时 mariadbd（3317、独立 datadir 和 socket），线上库零写入。
2. **写点测试钩子**：已定不加，用 jdb 断点 + kill -9。jdb 覆盖不到的「异步写已落」一侧记 SKIP，并列入残余风险（见顶部测试岗注）。
3. **Waterfall**：已定不接。
4. **孤儿标签**：已定 a+b+c，已派插件岗。A1 加 M8 写点，E4 断言「重启后标签被清、invsnap 正常恢复」。
5. **迁移后恢复到迁移前的快照**：已定由守卫拦（快照早于迁移完成就拒绝），已派插件岗。E5–E7 加测。
6. **DungeonPlus**：已定不装，H8 主线结算掉甲记 SKIP，留到 T3。
