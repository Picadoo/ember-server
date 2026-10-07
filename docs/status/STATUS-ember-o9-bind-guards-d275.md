# 状态 · D275：账号绑定守卫跟 P1 开关（O9 薄修）

**日期：** 2026-10-07（上海时间）  
**上游：** ARCH §3 O9 / R5 ④ · CoreRpg **1.65.74** · `balance_version` **58**（数值不动）

## 这扇窗做什么

以前「余烬材料/装备不能丢、不能塞箱子」的守卫写在挂机庭类里，并且先看 `afk.enabled`。关掉挂机庭会连带关掉防转移。本窗只改门闩：守卫跟 **P1 总开关**（`EmberMode.active()`），不再跟挂机开关。

## 改了什么

| 处 | 改动 |
|---|---|
| `EmberAfkService` | 新增 `bindGuardsActive()` = `EmberMode.active()`；丢出 / 箱子 / 展示框 / 盔甲架 五个 handler 改用它 |
| 单测 | `bindGuardsFollowP1MasterSwitchNotAfkFlag` |
| 版本 | 1.65.73 → **1.65.74** |

## 验收

- `mvn -o test -Dtest=EmberAfkServiceTest`（JDK8）通过
- 实服：CoreRpg **1.65.74** Enabling；login/proxy 不停，仅 play 换 jar 温和重启
- 人话：关挂机庭也不该再放开丢材料/塞箱（P1 仍开时）

## 不变

- 不开六槽；不改养成数值；S0-9 仍 HOLD
- **未**把监听器拆到独立类（下一扇可选）

## 下一扇候选

- 把绑定守卫抽成独立监听类（仍只看 P1）
- 或其它有证据的真债（R3 词缀上物品行等）
