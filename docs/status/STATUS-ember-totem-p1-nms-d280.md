# 状态 · D280：P1 图腾不被 NMS 吃掉（证据 + 纵深）

**日期：** 2026-10-07（上海时间）  
**上游：** ARCH R6「CoreCombat 图腾是否绕过 / 是否仍耗图腾」· CoreCombat **1.0.1** · CoreRpg 仍 **1.65.76**（仅注释）

## 人话

有人担心：P1 副本里拿着「余烬续命图腾」，死的时候 Paper 底层会不会先把图腾扣掉、再被 Ember 取消复活？  
**结论：不会。** Paper 只有在 `EntityResurrectEvent` **没被取消** 时才 `subtract(1)`。P1 里 Ember 会取消该事件（B08）→ 图腾留在手里，人照常死。  
本窗再加一层：CoreCombat 在 P1 世界直接判图腾无效，NMS 根本选不中这张图腾。

## 代码证据（Paper `EntityLiving#e`）

| 行（约） | 行为 |
|---|---|
| 1028–1035 | `TotemNmsHooks.isValidTotem` 选中候选栈 |
| 1043–1045 | 抛 `EntityResurrectEvent`（无候选则事先 cancelled） |
| **1047–1049** | **`if (!event.isCancelled()) { itemstack1.subtract(1); }`** |
| 1066 | `return !event.isCancelled()`（false → 继续死亡） |

Ember：`EmberCombatListener.onResurrect` HIGH + `EmberMode.isP1` → `setCancelled(true)`（B08）。

## 改了什么

| 处 | 改动 |
|---|---|
| `CoreCombatPlugin` validator | P1 世界 `emberP1World`（反射 `EmberMode.isP1`）→ `isValid=false` |
| `CoreCombatPlugin.emberP1World` | 软依赖 CoreRpg，无编译耦合 |
| `EmberCombatListener` | 注释标明「取消 ⇒ 不 subtract」合同 |
| 版本 | CoreCombat **1.0.0 → 1.0.1** |

## 验收

- `mvn -o -pl CoreCombat -am package -DskipTests`（JDK8）成功
- play：`Enabling CoreCombat v1.0.1`；login/proxy 不停
- `/corecombat check`：TotemNmsHooks.active=true

## 不变

- 不开六槽；不改养成数值；不换 CoreRpg 版本号
- 枢纽 / 非 P1 世界图腾行为不变（仍可复活并消耗）

## 下一扇候选

- ~~月卡进服发币 / vip·pass~~ → **D281**
- Flex 冲刺为 P1 技能组设计内（非 bug）
- 或有实机证据的玩法问题
