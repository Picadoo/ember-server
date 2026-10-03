# STATUS · PAPI 体力 parse 软债复测

**日期：** 2026-09-28 11:08（Asia/Shanghai）  
**岗：** 余烬-测试岗执行器  
**基线 tip：** `114b7bc`  
**债源：** `docs/status/STATUS-ember-stamina-s0.md`  
**结论：** ✅ **PASS；PAPI parse 软债可勾销**

## 复测条件

- 未修改插件或配置；未 commit/push。
- 真实在线玩家：`PapiRt7393`，经 proxy → login → play 后保持在线执行命令。
- 复测期间临时 OP `PapiRt7393`，结束后 deop；最终 `server-runtime/ops.json`、`login-runtime/ops.json` 均为 `[]`。
- Expansion 检查：`/papi list` 返回 12 个 hook，包含 `corerpg`。

## PAPI 结果

采用等价在线自解析：`/papi parse me <placeholder>`（`me` 即当前真实在线玩家，避免旧脚本的跨玩家查找时序问题）。全部返回可读值，均非 `Failed to find player`。

> 复测备注：首轮由独立 OP bot 对另一在线 bot 逐名执行 `parse PapiRt7391 ...` 仍复现 `Failed to find player`；改用派工允许的同一在线玩家 `me` 等价路径后 12 项全通过。因此本次按“或等价”判 PASS；若后续强制要求跨玩家逐名语法，需另开债。

| Stub | 返回 |
|---|---:|
| `%corerpg_stamina%` | `90` |
| `%corerpg_stamina_max%` | `90` |
| `%corerpg_stamina_bank%` | `0` |
| `%corerpg_stamina_cost_daily%` | `30` |
| `%corerpg_stamina_cost_weekly%` | `45` |
| `%corerpg_stamina_cost_abyss%` | `30` |
| `%corerpg_stamina_cost_elite%` | `40` |
| `%corerpg_stamina_cost_raid%` | `50` |
| `%corerpg_stamina_reset%` | `今日 0:00` |
| `%corerpg_stamina_credit_weekly%` | `1` |
| `%corerpg_stamina_credit_elite%` | `1` |
| `%corerpg_stamina_credit_raid%` | `1` |

## 菜单/TrMenu

按派工可采信既有 S0 PASS：进日常扣体力并显示“已消耗体力”，复测记录为 `menu_trmenu_baseline=PASS`。

## 产物与回执

- 证据：`/tmp/papi-stamina-parse-retest.json`
- 总评：**PASS**
- 硬条：Expansion 已注册；12 个 S0 相关 stub 全部在线解析通过；TrMenu 体力显示基线 PASS；ops 终态 `[]`。
- `ops=[]`：✅
- 阻塞点：无
- ops：`[]`
