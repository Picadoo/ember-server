# STATUS · 周本菜单 YAML 修复短冒烟

- **时间：** 2026-09-28 04:34–04:35（Asia/Shanghai）
- **Verdict：** **FAIL**

| 硬条 | 结果 | 证据 |
|---|---|---|
| `/trmenu reload` 后无 `ember_weekly` YAML parsing failed | ❌ | `server-runtime/logs/latest.log`：04:34:40，行 708–710 仍报 `ember_weekly` / `YAML parsing failed` |
| 周本子菜单可打开 | ❌ | `/trmenu open ember_weekly`（04:34:43）返回 `Unkown trplugins.menu ember_weekly .` |
| 测后 ops=[]（play+login） | ✅ | `server-runtime/ops.json=[]`；`login-runtime/ops.json=[]`；临时 `WkYOp928` 已于 04:35:00 `/deop` |
| 勿改 YAML/jar | ✅ | `ember_weekly.yml` 前后 SHA-256 均 `d1dbc48e…29dad1ee`；第 57 行仍为多缩进，未修文件 |

补证：启动阶段 04:34:00 同样出现 `ember_weekly` YAML parsing failed（latest.log 行 477–479）。本轮只做重载/打开验证，未改 YAML 或 jar。
