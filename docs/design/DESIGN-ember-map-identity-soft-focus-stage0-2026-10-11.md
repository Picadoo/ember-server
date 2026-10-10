# Stage0 · 图身份齐 → 软定套装焦点 · 2026-10-11

> **状态：** COMPLETE · 已批 A · 方案 M · D2205–  
> **禁：** 通关门 / 掉率 / ConfirmTokens / 伤乘 / AFK 表

## 方案 M

| ID | 项 | 态 |
|----|----|----|
| MI1 | `EmberMapIdentity.maybeSoftFocusAfterCodex` | SHIP |
| MI2 | GearLib + LoadoutService 新图录接线 | SHIP |
| MI3 | 单测 + tip/docs/jar | SHIP |

## 规格

1. 仅当 `EmberCodex.register` 新登记后调用。
2. `SetFocus` 已 valid → return false。
3. 存在主线 Q 图：`lootFamily==family` 且 `have>=need>0`。
4. Latch `p1_map_id_soft_<family>@all`；已软推过该族 → skip。
5. `EmberSetFocus.set(d, parse(family))`；聊天 + ActionBar 一行；**零按钮**。

## 批注

- [x] 总控批 A · 方案 M
