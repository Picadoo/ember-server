# STATUS · 希望环 · 2026-10-11

| ID | 项 | 状态 |
|----|----|------|
| D601–D606 | Hope meters + skill hope | PASS |
| D607 | 形状分叉 | PASS 1.65.298 |
| Gem S2 / C1 | 公式 | PASS 1.65.299 |
| D608 / C2 | 宝石获取保底+合成 | SHIP 1.65.300-d608 |
| D609 / C3 | 宝石 TrMenu UI | **SHIP** TrMenu `ember_p1_gem.yml` · 无 jar（tip 禁改 jar） |

## D609 交付

- `plugins/TrMenu/menus/ember_p1_gem.yml`：希望保底 / 合成四石 / 孔0·孔1 镶入 / 拆 / list
- `ember_forge` S → `menu: ember_p1_gem`
- `ember_p1_forge` 底栏 m → `menu: ember_p1_gem`
- 命令只走 `/corerpg p1 gem` · **永不** legacy socket
- tip/DESIGN 已批 A·M

下一：P4 地图装备身份可见 chase（或掉落成色 pity）。
