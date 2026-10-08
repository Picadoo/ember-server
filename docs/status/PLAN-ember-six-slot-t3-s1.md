# 余烬 · 六槽 T3 · S1 隔离测服短计划（余烬-测试 · 2026-10-08 · tip `d4ab102c` 已收 · 已交 STATUS · 暂不建议签⑦）

- 依据：设计稿 `docs/design/DESIGN-ember-six-slot-t3-rollout-2026-10-08.md`（**D322 批 A + ⑥ 仅 S1**）；T2 计划 `PLAN-ember-six-slot-t2-drill.md`；OPS `docs/ops/OPS-ember-six-slot-migration.md`；T2 最终 STATUS `ee733764` / tip `4d82b518`（D321 已签 T2）。
- 状态：**已交 STATUS（`1c2adb84`）；D12 结算未完 → 暂不建议签⑦。** tip = `d4ab102c`（全 `d4ab102cf1a49b058459df15ce5477ba434da1e7`，≥ `4d82b518`）；测服 jar = `/workspace/tmp/d322/CoreRpg-1.65.98-d322.local.jar`（sha256 `abc96aca51517216afa25d8ee14649acc55aed5d6b569826dba2308240139661`，plugin.yml `1.65.98-d322.local`）；配置草稿 `/workspace/tmp/d322/`（bv61 + 两开关起步 false）。段末交 STATUS，并建议是否可签 **⑦ 部署**。
- 范围：**冒烟 + H8 + D12 + §1.3 出口标准**。不重跑 T2 全 89 例 kill 矩阵。

## 0 相对 T2 的差异（一览）

| 项 | T2（D320） | S1（D322） |
|---|---|---|
| 目录 | `/workspace/tmp/t2-drill/`（已清场） | **`/workspace/tmp/t3-s1/`** |
| 代理 | 不接（`bungeecord=false`；H8 SKIP） | **独立 Waterfall**（绝不动线上 `proxy-runtime/`）；验 **H8** |
| DungeonPlus | 不装（结算掉甲 SKIP） | **装 DP**（及软依赖）；验 **D12** |
| kill -9 / jdb 矩阵 | A 组 43 例全跑 | **不重跑**；仅在出口失败或 H8/D12 异常时按需定点 |
| 用例量 | 89 执行 + SKIP | **约 15–20 例**（见 §4） |
| tip | 最终签线 `4d82b518` | **`d4ab102c` / jar sha `abc96aca…` 已收** |
| 两开关 | 仅测服同开 | 同左；**线上仍关** |
| 出口 | 建议签「T2 过线」 | 建议是否可签 **⑦ 部署** |

其余隔离口径沿用 T2：独立 cwd / 独立 `plugins/` 实目录 / 新平地世界 / 临时 mariadbd / 不拷签密钥 / 不拷真实玩家档 / 堆 ≤1.5G / 可用 <1.5G 暂停、<1G 立停。

## 1 隔离与端口

### 1.1 目录与进程

| 项 | 值 |
|---|---|
| 根目录 | `/workspace/tmp/t3-s1/`（`proxy/`、`server/`、`db/`、`bots/`、`evidence/`、`snap/`、`src/`） |
| 测服 | Paper 1.12.2，cwd = `t3-s1/server/` |
| 代理 | **独立** Waterfall 副本于 `t3-s1/proxy/`（`cp` 线上 `waterfall.jar` + 自写 `config.yml`；**不改** `proxy-runtime/`） |
| 临时库 | mariadbd，datadir `t3-s1/db/data`，库 `ember_t3`，用户 `t3s1@127.0.0.1` |
| JDK / Paper | 同 T2（`tools/jdk8u504-b01`；`server-runtime/paper-custom.jar` 只读拷） |
| 运行 | `nice -n 10`；pid 只写 `t3-s1/*.pid` |

### 1.2 端口（T2 已清场；2026-10-08 15:03 复查）

线上占用：25565（Waterfall）、**25566（登录）**、25567（游玩）、3306（MariaDB）。**25566 不可作测服代理口。**

| 用途 | 端口 | 绑定 | 说明 |
|---|---|---|---|
| **测服代理** | **25576** | 127.0.0.1 | 错开线上 25565/25566 |
| 测服游戏 | **25577** | 127.0.0.1 | 复用 T2 口；代理 `servers.play` 指向此 |
| 临时 mariadbd | **3317** + socket `t3-s1/db/mysqld.sock` | 127.0.0.1 | 复用 T2 口；库名改 `ember_t3` |
| JDWP（按需） | **25578** | 127.0.0.1 | 默认不开；仅排障定点时开 |
| RCON / query | 关 | — | — |

起服前：`ss -ltn | grep -E ':(25576|25577|25578|3317)\b'` 必须空；占用则改 25586/25587/25588/3327 并记入证据。

### 1.3 相对 T2 必须改的配置

| 文件 | T2 | S1 |
|---|---|---|
| `spigot.yml` `bungeecord` | `false` | **`true`**（只测服；配独立代理 `ip_forward`） |
| 代理 `config.yml` | 无 | `listeners.host: 127.0.0.1:25576`；`servers.play → 127.0.0.1:25577`；**不写**线上 login/play；`online_mode: false`；motd `EMBER T3-S1 – NOT LIVE` |
| `server.properties` | 25577 / gamemode 0 | 同 T2 |
| CoreRpg mysql | 3317 / `ember_t2` | 3317 / **`ember_t3`** / `t3s1` / 随机密（chmod 600） |
| `ember-v1.yml` 两开关 | 同开（仅测服） | 同开（仅测服）；线上文件不动 |
| `ember-v1-item.key` | 测服自生成 | 同左，**不拷线上** |
| DungeonPlus 等 | 不装 | **装**：`DungeonPlus.jar` + 软依赖（Adyeshach / AttributePlus / MythicMobs 按起服缺啥补啥；配置从 git archive 导出，**不拷**线上 `data.db` / 玩家态） |
| 菜单 | `ember_p1_armor` 已合 | 同 T2（从 staged / tip 带入） |
| 玩家档 / ops | 合成号 + 测服-only op | 同左；**绝不**给线上 op |

### 1.4 硬禁（同 T2 / D322）

- 不碰线上 PID（Waterfall / 登录 / 游玩 / MariaDB）；不重启线上服。
- 不写线上 DB（3306）；测服只连 3317。
- 不改线上 `proxy-runtime/`、`server-runtime/`、`login-runtime/`、`plugins/` 下运行时文件。
- 不 `git stash` / `reset --hard` / `checkout -- .` 于 `/workspace/minecraft`；拉代码只用 `git pull --ff-only`。
- 不强制推送；不提交 runtime / secrets（`login-runtime/*`、玩家 yml、worlds、`ops.json`、密钥、`.live.cnf`）。
- 不给线上玩家 / 测试号加 op；临时 op 只写 `t3-s1/server/ops.json`，清场删除。
- 不部署到线上 play、不动 bv、不切线上开关。

## 2 内存（同 T2）

| 组件 | 上限 |
|---|---|
| Paper 测服 | `-Xms768M -Xmx1536M -XX:+UseSerialGC -XX:MaxMetaspaceSize=192M` |
| 临时 mariadbd | ~0.2G（buffer 64M） |
| 独立 Waterfall | ~0.25G（小堆） |
| mineflayer | ≤2 bot，合计 ≤0.5G |
| DP / MM / Ady 额外 | 估 +0.3–0.5G；峰值盯 `free -m` |

- 监控：每 5s 记 `t3-s1/evidence/mem.log`。
- **<1.5G available → 暂停**；**<1G → 立即停**测服/代理/临时库（只杀 `t3-s1/*.pid`）。

## 3 构件与 tip

| 构件 | 要求 |
|---|---|
| CoreRpg.jar | **已收** tip `d4ab102c` 预构建 jar（`/workspace/tmp/d322/CoreRpg-1.65.98-d322.local.jar`，sha256 已核）；不再现场 mvn（省内存）；需要时可 worktree 复核 |
| 其余 | Paper / NI / PAPI / TrMenu / ProtocolLib / Vault / Multiverse-Core 同 T2 只读拷；**另加** DungeonPlus + 软依赖 |
| `<S1_TIP>` | **`d4ab102c`**（已填） |

## 4 场景矩阵（冒烟，非全量）

总不变量同 T2：**① 同 uid 有效 ≤1；② 原物多重集不变。**

### 4.1 必做（D322 / 设计 §1.3 / §4）

| 编号 | 内容 | 断言（摘要） |
|---|---|---|
| **S1-M** | 迁移主流程（≥3 号：有护符 / 无护符 / 原版甲位；`armor mig` + 进枢纽自动各抽） | H/D **逐位**；`p1_six_mig=1`；待领数 = 迁移前甲位件数；journal 空；①② |
| **S1-C** | 待领 · 开关全关可领（两键 false → reload → 护甲页领完） | 领完 stash 空；无复制；文案无命令教学 |
| **S1-A** | `corerpg p1 audit`：待领内 active | finding = **`ACTIVE_IN_STASH`**（非 `ACTIVE_NOT_HELD`）；`audit restore` **拒发** |
| **H8** | **经测服代理**进 play → 枢纽迁移 → 断线重连 / 代理路径再进 → 核对 | `p1_six_mig` 仍 1；四件 migrate·active；待领不复制；**无跨服同 uid 双持**；journal 可续（若中途杀） |
| **D12** | DP 主线通关结算 | ① 结算 **+1 甲**、部位均匀、source/origin 正确；② 掉落甲（`src=drop`）工坊分解 → 作废 + 胚零头 ×0.1；③ **迁移件仍拒分解** |

### 4.2 建议附带（短，≤1h）

| 编号 | 内容 |
|---|---|
| S1-H2 | 幂等：重登 / `armor mig` → ALREADY，DB 行不增 |
| S1-G8 | 开关关：护甲页仅「尚未开放」+ 待领入口（回归 tip 菜单修复） |
| S1-inv | 抽查 invsnap：迁移前快照永久拒；无 journal 的迁移后快照可恢复 |

### 4.3 明确不做

- T2 A 组全量 jdb kill 矩阵、B/C 标签压力、E 全组、F 全组。
- 故意停线上库 / 动线上代理路由。
- 白名单真人、升线上 jar / bv、切线上开关（属 ⑦）。

## 5 出口标准（设计 §1.3 · S1）

全部满足才建议签 ⑦：

1. 管理号 + ≥3 测试号：迁移前后 **H/D 逐位相等**；
2. 记录 **`p1_six_mig=1`**，`done_at` / `pieces` 齐全；
3. **待领数 = 迁移前甲位件数**（刚迁完瞬间）；
4. audit：**无**预期外 `ACTIVE_NOT_HELD`；待领内为 **`ACTIVE_IN_STASH`**；
5. **无未决 journal**；
6. **H8 PASS**（代理路径）；
7. **D12 PASS**（结算掉甲 + 掉落分解 + 迁移件拒拆）；
8. 不变量 ①② 全程 0 违反；线上 4 服务 pid / 哈希未被动。

任一条阻塞 → STATUS 写 **不建议签 ⑦**，只交证据不修（除非总控另派）。

## 6 执行顺序（拿到 tip 后）

| 步 | 内容 | 估时 |
|---|---|---|
| 0 | 填 `<S1_TIP>`；测前线上哈希 / pid / 只读 DB 快照；端口复查；`free -m` | 0.3h |
| 1 | worktree 构建 + 基线测试；导出配置；起临时库；起独立代理 + 测服 | 1.0h |
| 2 | 关开关合成迁移前档 → `snap/pre`；同开两开关 | 0.4h |
| 3 | S1-M / S1-H2 / S1-C / S1-A / S1-G8 / S1-inv | 1.0h |
| 4 | **H8**（代理路径） | 0.5h |
| 5 | **D12**（DP 通关 + 分解） | 1.0–1.5h |
| 6 | STATUS + 清场（停代理/测服/临时库；`rm -rf` 目录；核线上哈希与 pid） | 0.5h |
| | **合计** | **约 5h**，可一段跑完；中途若暂停则 commit 中间 STATUS |

段末产物：`docs/status/STATUS-ember-six-slot-t3-s1-test-YYYY-MM-DD.md`（commit+push，只 add 报告），结论栏写清 **建议 / 不建议签「⑦ 部署」**。

## 7 清场摘要

同 T2 §8：只杀 `t3-s1/*.pid`；`mariadb-admin --socket=… shutdown`；`git worktree remove`；证据打包后删目录；`sha256sum -c live-before.sha256`；确认 25576/25577/25578/3317 释放；线上 4 pid 未变。

## 8 前置与等 tip

| # | 项 | 状态 |
|---|---|---|
| 1 | D322 ⑥ 仅授权隔离测服 S1 | ✅ |
| 2 | 插件 tip ≥ `4d82b518` 测服 jar | **✅ `d4ab102c` / sha `abc96aca…` 已收** |
| 3 | 本短计划 commit+push | 交稿即完成 |
| 4 | ⑦ 部署 | ❌ 等 S1 PASS 后总控另签 |

> tip 已收（`d4ab102c`）。按本计划起 `/workspace/tmp/t3-s1/`，不必再问总控。最终 STATUS + 是否建议签 ⑦。
