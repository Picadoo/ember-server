# DESIGN · 宝石 Stage4 UI（D609）

> **状态：** 已批 A · 方案 M · 2026-10-11
> **禁：** legacy `ember_socket` / 抬 flat / K3 / AFK / sx20

## 方案 M

菜单 `ember_p1_gem.yml`：
- 希望保底行 `%corerpg_p1_gem_pity_hope%`
- 合成四石 → `corerpg p1 gem craft …`
- 孔0/孔1 × 四石镶入 → `insert`
- 拆孔 → `remove`
- 刷新 list / 返回工坊

入口：旧锻炉 S、P1 工坊镶嵌位 → `menu: ember_p1_gem`（不再只 tell+list）。
