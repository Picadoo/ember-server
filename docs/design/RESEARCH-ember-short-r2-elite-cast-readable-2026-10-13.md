# RESEARCH · 短征 R2 精英可读 Cast（短本玩法花样 · ≠sx20 · ≠抬表）· 2026-10-13

> **状态：** 待批 A · 荐方案 M · TIP-BZ / 拟 **D13305** · docs-only · 2026-10-13 21:31 Asia/Shanghai  
> **起因：** 总控【连开下一窗】TIP-BY 菜单已 live `e214b0399`。再交一薄 tip：短本玩法花样或挂机庭第二件可感乐趣；仍禁 sx20/抬日表/诚实空转/L13/K3。  
> **上游：** TIP-BY CLOSED live menu `e214b0399`（D13304 顺手花料）· D13304§6 后置明文「短征 R2 精英可读 Cast」· 短征链 sx01–sx19 PASS 暂停于 sx19（**禁 sx20**）  
> **硬禁：** **零** sx20 · **零** L13 施工 · **零** K3 · **零** Pack6/天赋灰印/R · **零抬** AFK 日表/`daily_kills`/`afk.tiers`/伤乘/`skill_mult` · **零** Affix densify · **零** sure/pity/soft 诚实半行再叠 · **零** 菜单诚实/observe 空转 · **零** 假开 `gate_daily` · **零** 新地图硬开

---

## 0. 一句话

短征三房里 **R3 Boss 有可读 Cast，R2 精英只会 SoftHit**——第二房「平砍无花样」；在 **已有 sx01–19** 上给精英挂轻压可读 Cast，立刻可感，**不**开 sx20。

---

## 1. 选题扫描（TIP-BY 后 · 可感内容真债）

| 候选 | 裁决 | 理由 |
|------|------|------|
| **短征 R2 精英可读 Cast（sx01–19 同形轻挂）** | **采纳** | 开窗点名短本玩法花样；D13304 后置#2；全线 Elite 实扫仅 SoftHit；Boss Cast 范式已 live 可降伤复用 |
| 挂机庭第二件：顺手升阶/精工 | **后置备选** | D13304§6#1；真多一事但刚交顺手强化，本窗优先短本花样 |
| 今日推短征轮换 | **后置** | 选本导航；非房内玩法花样 |
| 再叠 AFK remain/仓差/半行 | **否** | 展示已厚；禁空转 |
| 菜单诚实 / sure·pity·soft | **否** | 硬禁 |
| sx20 新本硬开 | **否** | 硬禁；链停 sx19 |
| 抬日表 / 抬 Elite HP·伤表 / 新地图 | **否** | 硬禁 / 非薄 |
| 观察 exhausted 空声明 | **否** | 有可施工真债 |

---

## 2. 证据（现网只读 · 2026-10-13）

| # | 事实 | 路径 | 含义 |
|---|------|------|------|
| E1 | sx01–19 **全部** `EmberSxNNElite` Skills 仅 `EmberDailySoftHit` + 掉落；**无** Cast | `plugins/MythicMobs/Mobs/EmberSx*.yml` | R2 无花样 |
| E2 | 同本 R3 Boss 均挂主题 Cast（`SentryCast`/`BeaconCast`/…/`StringCast`）`~onTimer:70` · 蓄力粒子+喊话+delay30+r5 伤害 | 同上 + `EmberDungeonDiffSkills.yml` | 范式可复用 |
| E3 | Boss Cast 典型：message 拉开 · flame/crit/cloud · damage 0.85 · CD8 | `EmberSx01SentryCast` 等 | 精英应 **更慢更轻**（非同伤复读） |
| E4 | D13304§6 后置：R2 Cast / 顺手升阶 / 今日推 | DESIGN-afk-courtyard-quick-spend | 本窗合法接力 |
| E5 | TIP-BY live；开窗禁诚实半行/sx20/抬表 | tip `e214b0399` · 总控连开 | 选题边界 |
| E6 | 短征链 sx19 PASS · **禁 sx20** | backlog / STATUS sx19 | 花样=旧本加深，非新本 |

**一句话缺口：** 进短征打到精英房，仍是「站桩平砍」——Boss 才有第一段可躲演出；R2 白白浪费主题 Cast 资产。

---

## 3. 方案方向（荐 M · 详 DESIGN）

1. **MM Skills：** 每本（或共享模板）新增 `EmberSxNNEliteCast`——同主题粒子/喊话，**伤害≈0.50–0.55**、**delay 可读**、`onTimer:100`（慢于 Boss 70）、文案「伍长/精英蓄力——拉开」  
2. **MM Mobs：** `EmberSxNNElite` Skills **追加一行**挂 EliteCast；**不改** HP/Damage/掉落/日帽  
3. **范围：** 荐 **sx01–19 全线同形**（只 YAML；薄可施工）；若总控要更薄可先 sx01–06 试点（方案 L）  
4. **施工岗：** 批后 **余烬-战斗 / MM**；**不派** TrMenu 主交付；零 jar 优先（纯 Mythic YAML）

---

## 4. 明确不做

- sx20 · 新地图 · 抬 AFK 日表/tiers · 抬 Elite/Boss 基础 HP·Damage 表  
- 菜单诚实半行族 · observe 空转 · Pack6/天赋/灰印/R/L13/K3 · 伤乘/skill_mult · Affix densify  
- 把 Boss Cast **原伤原 Timer** 原样贴到 Elite（变「两个 Boss」）  
- 复开 D423 · 假开旧日常 · 教裸指令  

## STATUS: 待批 A · 荐 M · 拟 D13305 · 2026-10-13 21:31 Asia/Shanghai
