# B2.92 · `mysql.password` 复位 `CHANGE_ME`（备选 B，5 文件）· 纯静态薄验收

- **总评：PASS**
- **测报时间：**2026-10-01 01:56 Asia/Shanghai
- **岗别：**余烬-测试岗（B2.92 纯静态薄验收）
- **工作区：**`/workspace/minecraft`（main，已与 `origin/main` 同步；本岗按指示未 `git pull`）
- **施工 tip SHA：**`1cd6d0f419e098f80316dd6f6686e0e40122f803`（short `1cd6d0f`，2026-10-01 01:53 CST）
- **设计 / 批准：**`ebc41ce` / `d20dd57`（批 A · 采备选 B：5 文件）
- **tip：**`docs/design-ember-corerpg-config-mysql-password-reset.md`（按文末「总控批注」验收口径执行）
- **是否已 push：否**（本岗仅本地 commit，禁止 push）
- **报告路径：**`docs/TEST-B2.92-mysql-password-reset.md`
- **ahead：**相对 `origin/main` ahead 1（本测报本地 commit 后，未 push）
- **`server-runtime/ops.json`：**`[]`（未改动）
- **机密处理：**全程不回显旧值 / 现值；未对这 5 个文件跑带 patch 的 `git show` / `git diff`，未打印父版本；比对均在脚本内完成，只输出键名、布尔、计数、长度。本报告不含任何密码原文。

## 各点

| # | 条件 | 结果 |
|---|------|------|
| 1 | 施工 commit 仅 5 个文件，各 1+/1- | **PASS** · `git show --stat/--numstat --format= 1cd6d0f`：5 文件，每个 `1 1`，合计 `5 insertions(+), 5 deletions(-)`；均为 `M` |
| 2 | 5 文件在 `1cd6d0f` 与 `1cd6d0f^` 均可解析；密码键为 `CHANGE_ME`，其它键与父版本深度相等；HEAD 与 `1cd6d0f` 一致 | **PASS** · 每文件仅 1 个键变化且为密码键，新值 `=== "CHANGE_ME"` 为 `true`；屏蔽该键后其余深度相等 `true`，键集合相同；仅 1 行变化；`1cd6d0f..HEAD` 无后续 commit 触及，HEAD / 工作区与 `1cd6d0f` 一致 |
| 3 | `MYSQL_PASSWORD` 非空；`git grep -lF` 旧值在 HEAD 计数 0；env 文件被忽略、未跟踪、权限 600 | **PASS** · 非空 `true`（长度 24）；HEAD 计数 `0`；`git check-ignore` 命中 `.gitignore:4:*.env`；`git ls-files` 为空；`stat -c %a` 为 `600` |
| 4 | HANDOFF §8 提交前查密码命令恢复有效且输出 0 | **PASS** · 原样执行（`grep -c` 只输出计数，不输出匹配行，无需再接 `wc -l`），输出 `0` |
| 附 | 工作区受跟踪文件（非 HEAD）旧值计数；design §4 js-yaml 命令；本报告自查 | **PASS** · 工作区 `git grep -lF` 计数 `0`；design §4 命令（对 `1cd6d0f` 版 `config.yml`）输出 `ok`；本报告旧值计数 `0`（commit 前自查） |

## 施工 commit 文件（stat only）

```
$ git show --stat --format= 1cd6d0f
 CoreRpg/config-mysql.example.yml                    | 2 +-
 CoreRpg/src/main/resources/config-mysql.example.yml | 2 +-
 login-runtime/plugins/AuthMe/config.yml             | 2 +-
 plugins/CoreRpg/config-mysql.example.yml            | 2 +-
 plugins/CoreRpg/config.yml                          | 2 +-
 5 files changed, 5 insertions(+), 5 deletions(-)

$ git show --numstat --format= 1cd6d0f
1	1	CoreRpg/config-mysql.example.yml
1	1	CoreRpg/src/main/resources/config-mysql.example.yml
1	1	login-runtime/plugins/AuthMe/config.yml
1	1	plugins/CoreRpg/config-mysql.example.yml
1	1	plugins/CoreRpg/config.yml

$ git log --oneline 1cd6d0f..HEAD -- <上述 5 文件>
（空：无后续 commit 触及）
```

设计 / 批准 commit 仅改文档：`ebc41ce`、`d20dd57` 均只含 `docs/design-ember-corerpg-config-mysql-password-reset.md` 与 `docs/design-ember-content-backlog.md`。

## YAML 解析与键比对（脱敏脚本 `/tmp/b292_check.js`）

脚本做法：`git show <rev>:<path>` 输出只进 node 进程，不进终端；js-yaml（`/usr/share/nodejs/js-yaml`）解析 `1cd6d0f^` 与 `1cd6d0f`，扁平化后找出值不同的键，只打印键名；新值只打印是否 `=== "CHANGE_ME"`，旧值只打印长度及是否等于 env 变量（布尔）；删除该键后对其余结构 `isDeepStrictEqual`；变化行若含旧值则打 `<MASKED>`（实际新行只含 `CHANGE_ME`）。

| 文件 | 键数 | 变化键 | 新值 == CHANGE_ME | 旧值 == env 变量 | 其余深度相等 | 变化行 | HEAD==1cd6d0f / 工作区==HEAD | 1cd6d0f 含旧值 |
|------|------|--------|------|------|------|------|------|------|
| `plugins/CoreRpg/config.yml` | 140 | `mysql.password` | true | true（长 24） | true | L76 `  password: "CHANGE_ME"` | true / true | false |
| `login-runtime/plugins/AuthMe/config.yml` | 180 | `DataSource.mySQLPassword` | true | true（长 24） | true | L18 `    mySQLPassword: "CHANGE_ME"` | true / true | false |
| `plugins/CoreRpg/config-mysql.example.yml` | 8 | `mysql.password` | true | true（长 24） | true | L11 `  password: "CHANGE_ME"` | true / true | false |
| `CoreRpg/config-mysql.example.yml` | 8 | `mysql.password` | true | true（长 24） | true | L11 `  password: "CHANGE_ME"` | true / true | false |
| `CoreRpg/src/main/resources/config-mysql.example.yml` | 8 | `mysql.password` | true | true（长 24） | true | L11 `  password: "CHANGE_ME"` | true / true | false |

脚本末行：`ALL_OK=true`。AuthMe 文件内其它名含 password 的键（如 `DataSource.mySQLColumnPassword`、`Email.mailPassword`、`Converter.loginSecurity.mySql.password` 等）均未变化。

```
# design §4 命令（HEAD 替换为施工 commit）
$ git show 1cd6d0f:plugins/CoreRpg/config.yml | NODE_PATH=/usr/share/nodejs node -e 'const d=require("js-yaml").load(require("fs").readFileSync(0,"utf8")).mysql;if(d.password!=="CHANGE_ME"||d.host!=="127.0.0.1"||d.port!==3306||d.database!=="ember"||d.username!=="ember")process.exit(1);console.log("ok")'
ok
exit=0
```

## 旧值计数（只打印计数）

```
$ set -a; . secrets/mysql-ember.env; set +a
$ [ -n "$MYSQL_PASSWORD" ]            → true（只打印长度：24）

$ git grep -lF -- "$MYSQL_PASSWORD" HEAD | wc -l
0
$ git grep -lF -- "$MYSQL_PASSWORD" | wc -l            # 可选：工作区受跟踪文件
0
$ git grep -lF -- "$MYSQL_PASSWORD" 1cd6d0f^ | wc -l   # 对照：施工前 5 处，证明变量有效、grep 非空转
5
$ git grep -cF -- "$MYSQL_PASSWORD" 1cd6d0f -- plugins/CoreRpg/config.yml | wc -l   # design §4
0

# HANDOFF §8 原样（grep -c 只出计数）
$ (git diff; git ls-files -o --exclude-standard | xargs -r cat) | grep -c -- "$MYSQL_PASSWORD"
0

# 本报告自查（commit 前）
$ grep -cF -- "$MYSQL_PASSWORD" docs/TEST-B2.92-mysql-password-reset.md
0
```

## `secrets/mysql-ember.env`

```
$ git check-ignore -v secrets/mysql-ember.env
.gitignore:4:*.env	secrets/mysql-ember.env
$ git ls-files secrets/mysql-ember.env
（空）
$ stat -c %a secrets/mysql-ember.env
600
键名（只列名，不列值）：MYSQL_HOST / MYSQL_PORT / MYSQL_USER / MYSQL_PASSWORD / MYSQL_DATABASE_EMBER / MYSQL_DATABASE_AUTHME
```

## 旁证

1. **范围：**施工 commit 恰为总控批注列出的 5 文件，每处只改密码值为 `"CHANGE_ME"`（带双引号，与 HANDOFF §1 第 5 步 / §8 口径一致），其它键零改。
2. **当前树已无旧值：**HEAD 与工作区受跟踪文件计数均为 0；对照施工前为 5，说明 env 变量有效，不是空变量导致的假 0。
3. **§8 查密码命令已恢复有效：**`secrets/mysql-ember.env` 已存在（600，gitignored，未跟踪），`MYSQL_PASSWORD` 非空，命令输出 0。
4. **未回填、无脏行：**按总控批注「不回填、不留脏行」，测前 `git status --porcelain` 为空，5 文件工作区与 HEAD 一致。
5. **做法纪律：**仅静态 git / node 脚本 / stat；未起服、未长测、未杀进程；未改任何文件（仅新增本报告）。
6. **小注（不影响结论）：**tip 头部与「总控批注」标题写 01:58 CST，而批准 commit `d20dd57` 为 01:51:55 +0800、施工 `1cd6d0f` 为 01:53:01 +0800；仅文书时间戳不一致，顺序（设计 → 批准 → 施工）正确。

## 遗留风险（本窗明确不做，照录 tip §5）

- 旧值已进入公开仓库 git 历史（`1cd6d0f^` 及更早），仅复位当前树不等于泄露已止；**线上 MariaDB `ember` 用户密码轮换**与 **git 历史清理**均不在本窗，需总控 / 用户另行决定。
- 日后在本机恢复服时，需按 HANDOFF §1 第 5 步从 `secrets/mysql-ember.env` 回填 live 配置（CoreRpg、AuthMe），且提交前务必跑 §8 查密码命令。

## 阻塞点

无。

## 回报摘要

- 总评：PASS
- 各点：1 PASS / 2 PASS / 3 PASS / 4 PASS / 附 PASS
- 施工 tip SHA：`1cd6d0f`
- 测报 tip short SHA：待本地 commit 后回报
- 是否已 push：否
- 报告路径：`docs/TEST-B2.92-mysql-password-reset.md`
- 阻塞点：无
- ahead：相对 `origin/main` ahead 1（未 push）
- `ops.json`：`[]`
- 旧值计数：HEAD 0 / 工作区 0 / §8 命令 0 / 本报告 0
