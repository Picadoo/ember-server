# B2.92 · CoreRpg `config.yml` `mysql.password` 复位为 `"CHANGE_ME"`

- **STATUS：tip · pending A（策划 · 2026-10-01 01:55 Asia/Shanghai）** · 待总控批 A
- **tip 路径：**`docs/design-ember-corerpg-config-mysql-password-reset.md`
- **目标范围：**仅 `plugins/CoreRpg/config.yml` L76 `mysql.password` 的值；其它键零改。
- **起因：**自 `2e5649f`（Ember sync 2026-09-27）起，该行在仓库里是真实样式密码，违反 `HANDOFF.md` §8 硬规则（仓库内保持 `password: "CHANGE_ME"`）。本稿及 backlog **不写旧值原文**。
- **前序对齐：**B2.91 已 PASS · 勾销（设计 `759c7d7` · 批准 `fde150f` · 插件 `93724a4` · 测 `96baff6` · close `47f2c77`）。
- **施工岗：**批 A 后交 **插件岗（CoreRpg config）**。
- **本窗纪律：**只改该行值；勿 git push；任何命令、日志、报告、commit message 都不回显旧值。

## 1. 现况（只读核对，已脱敏）

`plugins/CoreRpg/config.yml` L71–L78：

```yaml
mysql:
  host: 127.0.0.1
  port: 3306
  database: ember
  username: ember
  password: <真实样式密码，24 字符，此处脱敏>
  pool-size: 10
  jdbc-params: useSSL=false&allowPublicKeyRetrieval=true&characterEncoding=utf8&serverTimezone=UTC
```

策划核对中发现三件会影响施工和验收的事，需总控在批 A 时知悉：

1. **这是 live 文件。**`server-runtime/plugins` 软链到 `../plugins`，游玩服启动时直接读这一行；`MysqlStorage.java` L90 只读 `mysql.password`（缺省 `changeme`），**没有环境变量覆盖**。值复位后若不在本地回填，下次起游玩服 CoreRpg 连库会失败。当前 25565/25567/3306 均未监听，本窗不涉及重启。
2. **`secrets/mysql-ember.env` 在这台机器上不存在**（只有 `.example`）。所以 HANDOFF §8 的提交前查密码命令（`grep -c -- "$MYSQL_PASSWORD"`）此刻变量为空，形同失效，这很可能就是真值被提交进仓库的原因。
3. **同一旧值在当前树里还出现在另外 4 个受跟踪文件**（只列位置，不列值）：
   - `login-runtime/plugins/AuthMe/config.yml` L18（登录服 AuthMe 连库，也是 live 文件）
   - `plugins/CoreRpg/config-mysql.example.yml` L11
   - `CoreRpg/config-mysql.example.yml` L11
   - `CoreRpg/src/main/resources/config-mysql.example.yml` L11

   因此总控建议的验收口径「`git grep` 当前树里不再出现旧值」**只改 config.yml 做不到**。本稿按一窗一件事，把该条收窄为「`plugins/CoreRpg/config.yml` 中不再出现旧值」，另 4 处见 §5 备选与后续。

## 2. 荐案（批 A 后替换该行）

新行精确文本（行首 2 空格缩进，值带双引号）：

```yaml
  password: "CHANGE_ME"
```

与 HANDOFF §1 第 5 步、§8 的仓库口径一致。

## 3. 施工步骤（插件岗）

0. **先保住真值（不入 git）。**按 `secrets/mysql-ember.env.example` 生成 `secrets/mysql-ember.env`（`chmod 600`，已被 `.gitignore` 的 `*.env` 忽略），把 L76 当前值写进 `MYSQL_PASSWORD`，其余字段照 config.yml 的 host/port/database/username 填。用变量搬运，不 `echo`、不 `cat` 真值。这一步同时让 HANDOFF §8 的查密码命令重新生效。
1. 用只匹配键名的替换把 L76 改成荐案（例如 `sed -i -E '76s/^  password: .*/  password: "CHANGE_ME"/' plugins/CoreRpg/config.yml`），不在命令里写旧值。
2. 按 §4 验收自查后，只 `git add plugins/CoreRpg/config.yml`，本地 commit（`fix(corerpg): B2.92 config mysql.password 复位 CHANGE_ME`），不 push。
3. **commit 之后本地回填（默认，不入 git）。**把 L76 从 `secrets/mysql-ember.env` 回填为真值，让 live 游玩服下次启动仍能连库；此后该行在工作区是已知脏行，任何岗位提交 config.yml 前都要跑 HANDOFF §8 查密码命令（结果必须为 0），用 `git add -p` 排除这一处。回报时写明「已回填、脏行在」。
   - 若总控不希望留脏行，可改为不回填，由测试岗每次起服前回填、停服后还原；本稿默认前者，因为起停服频繁。

## 4. 验收口径（测试岗静态）

- [ ] 施工 commit 只含 `plugins/CoreRpg/config.yml`，`git show --stat` 为 `1 insertion(+), 1 deletion(-)`，`+` 行逐字为 `  password: "CHANGE_ME"`。
- [ ] 按施工 commit 的文件内容解析 YAML（不是工作区，工作区已回填）：
      `git show HEAD:plugins/CoreRpg/config.yml | node -e 'const d=require("js-yaml").load(require("fs").readFileSync(0,"utf8")).mysql;if(d.password!=="CHANGE_ME"||d.host!=="127.0.0.1"||d.port!==3306||d.database!=="ember"||d.username!=="ember")process.exit(1);console.log("ok")'`
      在仓库根目录输出 `ok`（盒内无 PyYAML，用已装的 js-yaml）。`HEAD` 按实际施工 commit 替换。
- [ ] 施工 commit 的 `plugins/CoreRpg/config.yml` 中不再出现旧值：用 `secrets/mysql-ember.env` 里的变量做 `git grep -cF -- "$MYSQL_PASSWORD" <施工 commit> -- plugins/CoreRpg/config.yml`，结果为空（不打印值）。
- [ ] `secrets/mysql-ember.env` 存在、权限 600、`git check-ignore` 命中、`git ls-files` 不含。
- [ ] 报告与测报里不出现旧值原文；不重启、不长测。

## 5. 明确不做

- **线上库密码轮换**：不在本窗（总控已告知用户）。旧值已进公开仓库历史，只复位当前树不等于泄露已止，真正止损要轮换 MariaDB `ember` 用户密码并同步各 live 配置。
- **git 历史清理**（filter-repo / BFG / force push）：不在本窗。
- 另 4 处同值文件（§1 第 3 条）不在本窗；`CoreRpg/src/main/resources/config.yml` 的 password 行（不是旧值）不动。
- 不改 `MysqlStorage.java`，不加环境变量覆盖；不改 host/port/database/username/pool-size/jdbc-params 及 config.yml 其它任何键。
- 不碰 `plugins/CoreRpg/calamity-state.yml`、`ladder.yml`、`players/**`、jar、世界存档、日志。

## 6. 备选与后续（供总控排窗，未立项）

- **备选 B（批 A 时可选）：**本窗扩为「当前树所有旧值复位」，一次改 5 个文件：3 个 `config-mysql.example.yml` 的 password 改 `"CHANGE_ME"`（纯样例，无运行风险），`login-runtime/plugins/AuthMe/config.yml` L18 同样复位并按 §3 第 0、3 步本地回填。这样总控原先的「`git grep` 全树无旧值」验收才成立。代价是一窗改 5 文件、多一个 live 服，偏厚。策划倾向 A 先落、B 紧接一窗。
- **后续 B2.93 候选：**3 个 example 文件去真值（零运行风险，薄窗）。
- **后续 B2.94 候选：**AuthMe config L18 去真值 + 本地回填（登录服 live）。
- **后续（代码窗，交插件岗评估）：**`MysqlStorage` 在值为 `CHANGE_ME` 时读 `MYSQL_PASSWORD` 环境变量，从根上消掉 live 文件里的脏行和再次误提交的风险。
