# RESEARCH · 短征 R1 近战卫可读轻 Cast（短本玩法花样续 · ≠sx20 · ≠抬表）· 2026-10-13

> **状态：** 待批 A · 荐方案 M · TIP-CF / 拟 **D13311** · docs-only · 2026-10-13 23:10 Asia/Shanghai  
> **起因：** 总控【TIP-CE CLOSED】房间事件冲一下 live · jar `1.65.807-d13310b` · menu `1b4a9d78e` · PASS `abc14a205`（无chase灰绿）。下一波交薄 tip：挂机庭/短征/冒险可感乐趣；禁 sx20/抬表/诚实空转/L13/K3/Pack6/灾厄重开。  
> **上游：** TIP-CE CLOSED · D13310 冲一下 · D13309 今日推 · D13305 R2 EliteCast · D13304–08 挂机庭四件齐 · fest/灾厄仍后置  
> **硬禁：** **零** sx20 · **零** L13 施工 · **零** K3 · **零** Pack6/天赋灰印/R · **零抬** AFK 日表/`daily_kills`/`afk.tiers`/伤乘/`skill_mult` · **零** Affix densify · **零** sure/pity/soft 诚实半行再叠 · **零** 菜单诚实/observe 空转 · **零** 假开 `gate_daily` · **零** 重开 calamity join · **零** 新地图硬开 · **零** 再叠挂机庭花料第五件

---

## 0. 一句话

短征三房里 **R3 Boss / R2 Elite 已有可读 Cast，R1 近战卫仍只会 SoftHit**——进本第一房「平砍开场」；在 **已有 sx01–19** 上给 R1 近战主怪挂更轻更慢的主题 Cast，立刻可感，**不**开 sx20。

---

## 1. 选题扫描（TIP-CE 后 · 可感内容真债）

| 候选 | 裁决 | 理由 |
|------|------|------|
| **短征 R1 近战卫可读轻 Cast（sx01–19）** | **采纳** | 总控点名「短征玩法花样续」；D13305 后自然下一档；R1 实扫仅 SoftHit；Elite/Boss 范式可再降档 |
| 短征周目标选页可冲 | **后置备选** | 导航可感；非房内花样；赛季页已有入口 |
| 冒险「今日三冲」聚合格 | **后置** | 菜单聚合；今日推+冲一下已分格 live |
| 挂机庭第五件花料（拆解等） | **否** | 四件齐；多窗明文勿再叠 |
| 新 fest / 重开灾厄 | **否** | 硬禁/政策后置 |
| 菜单诚实 / sure·pity·soft | **否** | 硬禁 |
| sx20 / 抬日表 / 抬伤表 | **否** | 硬禁 |

**择优结论：** R2 已有 EliteCast、R3 有 BossCast，**债清在 R1 开场房**——短本「三房都会蓄力可躲」差最后一环。故 **写死荐 M = 短征 R1 近战卫轻 Cast**。

---

## 2. 证据（现网只读 · 2026-10-13）

| # | 事实 | 路径 | 含义 |
|---|------|------|------|
| E1 | sx01–19 R1 近战（sx01=`Zombie`；其余=`Guard`）Skills **仅** SoftHit | `plugins/MythicMobs/Mobs/EmberSx*.yml` | 开场无花样 |
| E2 | R1 远程（Skeleton/Crossbow）亦仅 SoftHit | 同上 | 本窗**只挂近战主怪**，防双怪同房刷屏 |
| E3 | R2 EliteCast live（timer100 · dmg≈0.52 · delay35 · r4） | `EmberDungeonDiffSkills.yml` + Mobs | 可再降一档给 R1 |
| E4 | R3 BossCast live（timer70 · dmg0.85） | 同上 | 梯队：Boss > Elite > Guard |
| E5 | TIP-CE CLOSED；挂机庭四件/今日推/冲一下已齐 | 总控 · backlog | 勿再叠花料/导航空转 |
| E6 | 短征链停 sx19 · **禁 sx20** | backlog | 花样=旧本加深 |

**一句话缺口：** 进短征第一房仍是站桩平砍——Elite/Boss 的可读蓄力资产没用在开场。

---

## 3. 方案方向（荐 M · 详 DESIGN）

1. **MM Skills：** 每本新增 `EmberSxNNGuardCast`（sx01 对 `Zombie` 仍用 `GuardCast` 技能名或 `ZombieCast` 别名同一文件）——同主题粒子/喊话，**伤害≈0.30–0.35**、**delay 可读**、`onTimer:140`（慢于 Elite 100）、文案「哨卒/卫士蓄力——侧移」  
2. **MM Mobs：** 仅 R1 **近战主怪** Skills 追加一行挂 GuardCast；**不**挂 Crossbow/Skeleton；**不改** HP/Damage/掉落/日帽  
3. **范围：** 荐 **sx01–19 全线同形**；若总控要更薄可先 sx01–06（方案 L）  
4. **施工岗：** 批后 **余烬-怪物 / MM**；**不派** TrMenu/jar 主交付

---

## 4. 明确不做

- sx20 · 新地图 · 抬 AFK 日表/tiers · 抬 Guard/Elite/Boss 基础 HP·Damage  
- 菜单诚实半行族 · observe 空转 · Pack6/天赋/灰印/R/L13/K3 · 伤乘/skill_mult · Affix densify  
- R1 **远程**同房再挂 Cast（刷屏）· 把 Elite/Boss Cast **原伤原 Timer** 贴到 Guard  
- 再叠挂机庭花料第五件 · 重开 calamity · 新 fest · 复开 D423 · 教裸指令  

## STATUS: 待批 A · 荐方案 M · 拟 D13311 · TIP-CF · 2026-10-13 23:10 Asia/Shanghai
