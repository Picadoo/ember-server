# 状态 · D255：`/corerpg` 帮助仓库行改为只读说明

**日期：** 2026-10-07（上海时间）  
**上游：** D254（`d44fb106`）· CoreRpg **1.65.72** · `balance_version` 58

## 这扇窗做什么

帮助里还写着 `warehouse deposit|withdraw|unlock`，和 D252 只读矛盾。改成玩家只见 list/info，写操作挪到 admin 行。

## 改了什么

| 处 | 改动 |
|---|---|
| `CoreRpgPlugin.sendHelp` | 玩家行：`warehouse [list|info]` + 指向枢纽仓库；admin 行列出 deposit/withdraw/unlock |
| `WarehouseService` 未知子命令提示 | 同上口径 |
| 版本 | 1.65.71 → **1.65.72** |

## 验收

- 编译部署后 play 日志 `Enabling CoreRpg v1.65.72`
- 控制台 `/corerpg help` 玩家向说明无 deposit 列表（admin 行仍有）

## 不变

- 只读闸门逻辑未改（仍是 D252）；不开六槽；跳过 S0-9

## 下一扇候选

- 其它旁记注释薄扫
