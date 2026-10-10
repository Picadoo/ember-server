# Stage0 · EM1 图本赋能/词缀手感 · 2026-10-11

> **状态：** Stage0 **COMPLETE** · 已批 A · 方案 M · **D1814–**  
> **上游：** `DESIGN-ember-empowerment-scan-next-stage0` EM1 研究笔记（PoE / D4 / MH）  
> **硬禁：** 无永久伤乘 / skill_mult / gem flat / clear-gate / drop-rate / hope PAPI twin / K3 / SF4 / AFK raise / sx20 / legacy socket / sticky

## 1. 参考对照（落地约束）

| 参考 | 机制本质 | 余烬对位 | 本窗可否发明 |
|------|----------|----------|--------------|
| **PoE league** | 内容自带**额外获取路径**（非全局掉率乘） | 每图已有 lootFamily/图鉴身份 + 宝石通关保底 + 成套焦点 | **否**发明新玩法钩；**可**把已有路径合成「本图方法」半行 |
| **D4 seasonal** | 主题环 + **可见进度** | goals/featured/awaken remain 已近饱和 | **否**再叠 PAPI/希望 twin |
| **MH 素材表** | 部位/素材可见 | vault/forge 缺料 AB 已近饱和 | **否**再 twin；**可**在图本侧点名「缺件→哪条获取」 |

## 2. 拆窗

| ID | 项 | 风险 | 态 |
|----|----|------|----|
| **EM1-A** | methodLine → mapfarm/enter/mapcard | 低 · 纯展示 | **D1814–D1841 SHIP** · near-sat |
| **EM1-B** | 每图一条**新**农场方法（新怪/新房间/新掉落池） | 高 · 易发明 · 须对照表+owner+可能 p1sim | **BLOCKED** 设计歧义 |
| **EM1-C** | 词缀精英手感伤乘/新行为 | 高 · 永久伤乘/variety 表 | **BANNED** 本窗 |
| **EM1-D** | SF4 形微疗缓 | 已 **BANNED** | 禁 |

## 3. 方案 M（EM1-A）

1. `methodLine(MapDef, PlayerData)` 纯 Java：  
   - 有 lootFamily → `nextMissing` 短句；齐则「身份齐·刷分解/成套」  
   - 玩家成套焦点族 == 本图 lootFamily → 「对焦点」  
   - `EmberGemAcquire.countsForPity` 适用的主线 Q → 附宝石保底 remain（已有 `remainLine`，非 hope）  
2. 接线：`EmberMapFarmCue` / `EmberMapEnterFeel` / mapfarm 展示用 methodLine（可并列 remainLine，勿重复刷屏时合并一条）。  
3. **零改** 掉率、通关闸、skill_mult、套装战力、AFK、宝石 flat。  
4. **无需 p1sim**（无战斗/经济数值变更）。

## 4. EM1-B 阻塞说明

「每图一条方法」缺业主选定的 Q01–Q07 方法表；在未对照+签字前写码 = 发明。下批若开：先补「参考对照表 + 每图一条」再 tip。

## 5. 批注

- [x] 总控批 A · 方案 M · 2026-10-11 · 只开 EM1-A
