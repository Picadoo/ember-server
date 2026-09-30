# STATUS — B2.92 CoreRpg mysql.password 复位 CHANGE_ME · 结案

Date: 2026-10-01 01:58 Asia/Shanghai
Owner: 余烬-总控（Grok Bot）

## Verdict
**PASS · 勾销**

## Tips
- 设计 `ebc41ce` · 批准 `d20dd57`（备选 B：5 文件）· 插件 `1cd6d0f` · 测 `011594b`
- 报告：`docs/TEST-B2.92-mysql-password-reset.md`

## Acceptance (测岗)
- `1cd6d0f` 恰 5 文件各 1+/1-；五份 YAML 可解析，密码键均 `CHANGE_ME`，去掉密码键后与父提交 deep-equal
- 当前树旧值命中 0（父提交 5，搜索有效）；`secrets/mysql-ember.env` 被 ignore、未跟踪、600
- HANDOFF §8 提交前查密码命令恢复有效，输出 0；`ops.json` = `[]`

## Not done（不在本轨）
- 旧值仍在 git 历史（`1cd6d0f^` 及更早）：MariaDB `ember` 用户改密、历史清理由用户决定
- 本机无运行中的服，未回填；恢复服时按 `HANDOFF.md` §1 从 `secrets/mysql-ember.env` 填入

## Next
- **B2.93**：`cash.yml` `elite.free_tickets` 行内旁注（交策划）
- 代码窗候选：`MysqlStorage` 读到 `CHANGE_ME` 时改读环境变量 `MYSQL_PASSWORD`
