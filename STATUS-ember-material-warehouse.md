# STATUS · 材料仓库

**日期：** 2026-09-13（Asia/Shanghai）  
**插件：** CoreRpg **1.4.5**（4726067）· storage=mysql  
**规格：** `DESIGN-ember-material-warehouse.md`

## 已可玩

| 命令 | 结果 |
|------|------|
| `/corerpg warehouse` | 总览 已用/总格 |
| `deposit` | 主手白名单 NI 合并入格 |
| `withdraw <slot\|id> [n]` | 取回 NI |
| `unlock` / `unlock cash` | 币阶梯 / 晶钻 28 |
| `info <slot>` | 单格详情 · cap 2e9 |
| `/corerpg storage` | **仍为** MySQL ping（未占用） |

## 冒烟（WareBot）

- 存入 `mat_ember_shard` ×16 PASS  
- `gear_ember_t3_blade` 拒存 PASS  
- 取出 ×5 → 余 11 PASS  
- 币解锁 8→9（5000）· 晶钻解锁 →10（28）PASS  

## 菜单

`ember_storage.yml`：总览 / 存入 / 解锁 / 末影箱
