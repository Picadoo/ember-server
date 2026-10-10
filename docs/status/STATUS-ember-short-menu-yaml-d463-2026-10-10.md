# 状态 · D463：短征选页 YAML 修复 · PASS

**日期：** 2026-10-10  
**裁决：** **已批 A · 批 M · D463**  
**版本：** TrMenu-only（live jar **1.65.154-d462** 未改）· bv **未抬**  
**性质：** 修 `ember_p1_short.yml`；**零 jar**

## 交付

| 项 | 状态 | 证据 |
|----|------|------|
| sx14–sx19 lore 同缩进（去金字塔） | ✅ | `yaml.safe_load` OK |
| 「十九本并列」补烬裂/旋/影/潮/晶/弦 | ✅ | 3 处 |
| 布局压到 **6 行**（箱体上限） | ✅ | 去第 7 行 `#NPRSJ` 金字塔行 |
| 体力键 **S→H**（避 sx18 烬晶 `S` 撞键） | ✅ | TrMenu `duplicate keys: S` 消失 |
| `trmenu reload` | ✅ | **18:16:06 CST** · `良好 \| 76 个菜单已加载 (149 ms)` · 其后无 `ember_p1_short` / YAML / Rows 错 |
| 冒烟开页 FreshQ993 | ✅ | title `短征 · 选本` · **raidHits=19** · 体力格在 · Open tell 十九本句 |
| tip | 开→关 · 本号 | |
| backlog | `B-short-menu-yaml` D463 PASS | |

## 根因链

1. sx14–19 lore 错缩进 → NightConfig `YAML parsing failed`（整页挂）  
2. 修 YAML 后暴露：Layout **7 行** → `Rows for chest must be [1,6]`  
3. 体力 Icon `S` 与 sx18 Icon `S` 撞键 → `duplicate keys found : S`

## 禁夹带

sx20 · 抬 AFK · K3 · 聚火 · bv · EmberSetRules · skill_mult/CD · CoreRpg 源码/deploy

## 编号

派单原文 D462；撞 live mode-path **1.65.154-d462** → 本债 **D463**。COORD-routine-1810。

## 冒烟

- Bot：**FreshQ993**（`botd` join → `/ember_p1_short` → 关窗 → quit）  
- MySQL connected 行仍健康（本号未触库）· 本变更无 SEVERE  
- 已知无害：`Player.updateCommands()` NoSuchMethodError（1.12 TrMenu 旁注）
