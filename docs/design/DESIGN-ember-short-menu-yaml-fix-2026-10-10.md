# 设计 · 短征选页 YAML 修复（ember_p1_short 载入失败 · 方案 M · D463）

**日期：** 2026-10-10（Asia/Shanghai）  
**裁决：** 已批 A · 批 M · D463（总控自批；COORD-routine-1810）  
**性质：** TrMenu-only 热修 · **零 jar** · **零 bv** · **零经济/日帽/技能表**  
**编号说明：** 派单原文写 D462；线上 jar 已占 **1.65.154-d462**（`B-mode-path-pick`）→ 本债落 **D463**。

## 1. 玩法/诚实债

- 现象：play 日志 `YAML parsing failed` · 整页 `ember_p1_short` 载入失败 → 玩家打不开短征十九本选页。
- 根因：sx13 之后追加 sx14–sx19 首通/日帽 lore 时缩进塌成 8 空格，并级联复制出金字塔重复行；NightConfig 在 Icon `I` 条件块映射中解析失败。
- 诚实缺口：三处「十九本并列」文案停在「烬衡悬梁」，未列 sx14–sx19 真名（与 Icons/U/N/P/R/S/J 已挂本不符）。

## 2. 方案 M（本窗唯一落地）

1. 删除级联错缩进金字塔；sx14–sx19 lore 与 sx13 **同缩进**（12 空格条件 lore / 8 空格默认 lore）。
2. 「十九本并列」三处补全：烬裂折廊 / 烬旋坡廊 / 烬影叠廊 / 烬潮回廊 / 烬晶折棱 / 烬弦共鸣。
3. `python3 yaml.safe_load` 必须通过；无 tab。
4. Layout 压到 **≤6 行**（箱体硬顶）；体力 Icon **S→H**，避免与 sx18 烬晶键 `S` 撞键。
5. `trmenu reload` 热更；短冒烟开页；**不**新建 sx20 同构本。

## 3. 否决

| 代号 | 内容 |
|------|------|
| L | 借修菜单抬 AFK/日帽/体力/有奖表 |
| W | sx20+ 同构新本 / 复活聚火 / 开 K3 / 改 EmberSetRules / skill_mult·CD / bv |
| A | 只写 STATUS 不修 YAML（菜单仍挂） |

## 4. 禁夹带

sx20 · 抬 AFK tier/daily_kills · K3 live · 聚火 · bv · EmberSetRules coef/every/ICD · skill_mult/CD · stage 脏 runtime · CoreRpg jar 部署（本债不需要）

## 5. 验收

- YAML safe_load OK  
- reload 后 latest.log **无** `ember_p1_short` + `YAML parsing failed`  
- FreshQ993+ 能打开短征选页（十九本格可见）  
