# STATUS · 周本菜单 YAML 修复短冒烟

- **复测时间：** 2026-09-28 04:39:23–04:39:37（Asia/Shanghai）
- **Verdict：** **✅ PASS**（三项硬条全绿）
- **JSON：** `/tmp/weekly-menu-yaml-retest.json`

| 硬条 | 结果 | 证据 |
|---|---|---|
| `/trmenu reload` 后无 `ember_weekly` YAML parsing failed | ✅ | `server-runtime/logs/latest.log`：reload 为 **04:39:26**（行 632）；其后至本轮结束（行 651）无 `ember_weekly` / `YAML parsing failed`。reload chat 回执：`[TrMenu] FINE \| 31 menus were loaded (90 ms)` |
| `/trmenu open ember_weekly` 成功 | ✅ | 命令 **04:39:30**（行 647）；mineflayer 收到窗口标题 `{"text":"§c周常 · 深核"}`，81 slots、30 non-empty，含 Diamond Sword / Ender Chest / Clock；无 `Unkown menu` |
| 测后 ops play+login = `[]` | ✅ | `server-runtime/ops.json=[]`；`login-runtime/ops.json=[]`；临时 `WkY2Retest` 已于 **04:39:35** `/deop`（日志行 648–649） |
| 勿改 YAML/jar | ✅ | `ember_weekly.yml` SHA-256=`2a9cb05a…eaaa03f`，mtime **04:35:51**；本轮未改 YAML 或 jar |

## 复测过程

1. 临时 OP `WkY2Retest`（仅为执行管理命令）并短重启 play；启动载入 **31 个菜单**（04:39:14），未报 YAML parsing failed。
2. 经 proxy → login → play 登录后执行 `/trmenu reload`，再执行 `/trmenu open ember_weekly`。
3. 确认窗口后 `/deop WkY2Retest`；最终 play/login 两侧 ops 均为空。

> 非硬条旁证：TrMenu reload 同时产生一次既有兼容性 `NoSuchMethodError: Player.updateCommands()` 警告，但 reload 回执为 31 menus loaded，且周本窗口实际打开成功；本轮未出现 YAML parsing failed。

## 上轮 FAIL 痕迹（保留对比）

- **时间：** 2026-09-28 04:34–04:35（Asia/Shanghai）
- **Verdict：** **FAIL**
- 启动阶段 04:34:00、reload 后 04:34:40 均报 `ember_weekly` `YAML parsing failed`（旧日志行 477–479、708–710）。
- `/trmenu open ember_weekly`（04:34:43）返回 `Unkown trplugins.menu ember_weekly .`。
- 上轮 ops 已清空；当时未改 YAML/jar。修复提交为 `08ec598`，本页为该提交后的复测结果。
