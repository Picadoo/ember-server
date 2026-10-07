# 状态 · D276：账号绑定守卫拆出挂机庭类（O9 结构）

**日期：** 2026-10-07（上海时间）  
**上游：** D275（`67adf497` / 1.65.74）· CoreRpg **1.65.75** · `balance_version` **58**

## 这扇窗做什么

D275 已把门闩改成跟 P1；本窗把丢出 / 箱子 / 展示框 / 盔甲架监听从 `EmberAfkService` **拆到**独立类 `EmberBindGuard`，结构上也不再挂在挂机庭上。

## 改了什么

| 处 | 改动 |
|---|---|
| `EmberBindGuard`（新） | 五个 handler + `bindGuardsActive` / `blockContainer` / `BOUND_MATS`；构造时自注册；提示前缀 `[余烬]` |
| `EmberAfkService` | 删除绑定整段与相关 import；类注释指向 BindGuard |
| `CoreRpgPlugin` | `new EmberBindGuard(this)`（紧挨挂机庭构造） |
| 单测 | `EmberBindGuardTest`；AFK 测不再碰 bind API |

## 验收

- `mvn -o test -Dtest=EmberAfkServiceTest,EmberBindGuardTest` 通过
- play Enabling **1.65.75**；login/proxy 不停

## 不变

- 不开六槽；不改数值；行为与 D275 相同（仍只看 P1）

## 下一扇候选

- R3（词缀/签名上物品行）或其它有证据真债；S0-9 仍 HOLD
