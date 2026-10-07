# 状态 · D261：S0-10 轻量实服验收（非 OP）

**日期：** 2026-10-07（上海时间）  
**上游：** D252 实现 · D260（`6a0b4c6b`）· CoreRpg **1.65.73** 在线  
**脚本：** `mineflayer-tests/s0-10-warehouse-smoke.js`（FreshW10，经代理登录，**未**重启服）

## 结果总览

**6 / 6 PASS**

| # | 检查 | 结果 | 证据摘要 |
|---|---|---|---|
| A1 | 旧 `/corerpg warehouse` 可查看 | **PASS** | `[仓库] 材料仓 0/8…` / 空仓提示用枢纽 |
| A2 | `warehouse deposit` 拒绝 | **PASS** | `P1 模式下这个旧功能已关闭，请用 /ember 主菜单。` |
| A3 | `warehouse withdraw` 拒绝 | **PASS** | 同上 |
| A4 | `warehouse unlock` 拒绝 | **PASS** | 同上 |
| B1 | 枢纽一键存入 `p1 stash` | **PASS** | `一键存入：材料 余烬碎片 ×8` |
| B2 | 枢纽取出 `p1 vault take` | **PASS** | `取出 余烬碎片 ×2（库存 6）` |

## 说明

- 测号 **非 OP**（`ops.json` 空 + 脚本 `deop`）
- 未跑完整 persist-roundtrip（本窗只要轻量可达性/存取）
- 票类文案填充窗已停（D260）

## 下一建议（非填充）

见父代理报告。
