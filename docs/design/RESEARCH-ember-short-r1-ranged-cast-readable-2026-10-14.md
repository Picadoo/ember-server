# RESEARCH · 短征 R1 远程卫可读轻 Cast（短本玩法花样续 · ≠sx20 · ≠抬表）· 2026-10-14

> **状态：** **打开 · 待批 A · 荐 M · 拟 D13314 · TIP-CI** · docs-only · 2026-10-14 Asia/Shanghai  
> **起因：** 总控【连开·下一档内容真债】TIP-CH CLOSED live `69cc1580c` · PASS `ce9cd56bb` · jar `1.65.810-d13313.local`。请出下一窗 tip+DESIGN（拟 D13314 · TIP-CI），STATUS=待批 A · 荐 M · docs-only。优先可感：挂机庭资源环新乐趣（≠第五件花料）/ 短征日更花样 / 乐趣系统；D13313§6 后置可参考须证据表。  
> **上游：** TIP-CH CLOSED · D13313 冒险今日三冲 · D13311 R1 近战 GuardCast · D13305 R2 EliteCast · R3 BossCast · 挂机庭四件齐 · 今日推/冲一下/周短征/三冲齐  
> **硬禁：** **零** sx20 · **零** L13 · **零** K3 · **零** Pack6/天赋灰印/R · **零抬** AFK 日表/`daily_kills`/`afk.tiers`/伤乘/`skill_mult`/`short:5` · **零** Affix densify · **零** sure/pity/soft 诚实半行再叠 · **零** 菜单诚实/observe 空转 · **零** 假开 `gate_daily` · **零** 重开 calamity · **零** 新地图硬开 · **零** 挂机庭第五件花料 · **零** 复开 D423 · **零** 教裸指令 · **零** 撤分格

---

## 0. 一句话

短征 R1 **近战卫已有 GuardCast，远程 Skeleton/Archer/Crossbow 仍只会 SoftHit**——开场房半侧仍是站桩平砍；在已有 sx01–19 上给远程卫挂**更轻更慢**主题 Cast，补齐 R1 双侧可读，**不**开 sx20、**不**抬伤表。

---

## 1. 选题扫描（TIP-CH 后 · 可感内容真债）

| 候选 | 裁决 | 理由 |
|------|------|------|
| **短征 R1 远程卫可读轻 Cast（sx01–19）** | **采纳** | D13313§6① / D13311§6③ 明文后置；现网远程仅 SoftHit；近战 GuardCast 已 live；MM-only 薄 |
| 挂机庭资源环「第五件花料」复读 | **否** | 四件齐；总控硬禁第五件 |
| 挂机庭「顺手推荐花」聚合 | **后置备选** | 可感但非本窗最直债；四花分格已齐，聚合非硬缺口 |
| GuardCast/Elite 粒子二次打磨（无新挂接） | **后置** | 打磨≠新可感；远程 SoftHit 债更硬 |
| fest 继任 / 灾厄枢纽可见 | **否** | 须总控另批；灾厄重开硬禁 |
| 菜单诚实 / sure·pity·soft / observe | **否** | 空转硬禁 |
| sx20 / 抬日表 / 抬伤表 / Pack6 / L13 / K3 | **否** | 硬禁 |

**择优结论：** 导航链（今日推→冲一下→周短征→三冲）与挂机庭四花、Cast 三房（近战 Guard/Elite/Boss）已齐；**债清在 R1 远程仍 SoftHit**——合法接力 D13313§6①。故 **写死荐 M = 短征 R1 远程卫轻 Cast**。**未改题**（采纳 §6①，非另开无关题）。

---

## 2. 证据（现网只读 · 2026-10-14）

| # | 事实 | 路径 | 含义 |
|---|------|------|------|
| E1 | D13313§6① / D13311§6③ 明文「R1 远程轻 Cast」后置 | DESIGN D13313 / D13311 | 合法接力 |
| E2 | sx01 `Skeleton`、sx02 `Archer`、sx03–19 `Crossbow` Skills **仅** SoftHit（无 Cast） | `plugins/MythicMobs/Mobs/EmberSx*.yml` | 远程半侧无花样 |
| E3 | 同房近战 GuardCast live（timer140 · dmg0.32） | DiffSkills + Mobs · D13311 | 远程须**更慢更轻**防双喊刷屏 |
| E4 | EliteCast timer100·0.52 / BossCast timer70·0.85 live | 同上 · D13305 | 梯队可再降档 |
| E5 | 挂机庭四件 / 今日推 / 冲一下 / 周短征 / 三冲 / Cast 三房近战侧已齐 | backlog · tip live | 勿再叠花料/导航/近战复读 |
| E6 | 短征链停 sx19 · **禁 sx20**；禁抬伤表 | 派单 | 花样=旧本加深 |

**一句话缺口：** 进短征第一房，近战已会蓄力，远程仍平砍——R1 可读只做了一半。

---

## 3. 方案方向（荐 M · 详 DESIGN）

1. **MM Skills：** 每本新增 `EmberSxNNArcherCast`——同主题粒子/喊话，**伤害≈0.22–0.28**、**delay 可读**、`onTimer:180`（慢于 Guard 140）、文案「弓卫/弩卫蓄力——侧移」  
2. **MM Mobs：** 仅 R1 **远程**挂接（sx01=`Skeleton`；sx02=`Archer`；sx03–19=`Crossbow`）；**不**改近战 GuardCast；**不改** HP/Damage/掉落/日帽  
3. **防刷屏：** Timer/CD 与 GuardCast **错开**；禁把 Guard/Elite/Boss 原伤原 Timer 贴到远程  
4. **范围：** 荐 **sx01–19 全线同形**；若总控要更薄可先 sx01–06（方案 L）  
5. **施工岗：** 批后 **余烬-怪物 / MM**；**不派** TrMenu/jar 主交付

---

## 4. 明确不做

- sx20 · 新地图 · 抬 AFK 日表/tiers · 抬 Guard/Elite/Boss/Archer 基础 HP·Damage  
- 菜单诚实半行族 · observe 空转 · Pack6/天赋/灰印/R/L13/K3 · 伤乘/skill_mult · Affix densify  
- 只做粒子打磨不挂远程 · 远程与近战同 Timer 同伤双压 · 再叠挂机庭第五件花料  
- 重开 calamity · 新 fest · 复开 D423 · 教裸指令 · 撤分格  

## STATUS: 打开 · 待批 A · 荐 M · 拟 D13314 · TIP-CI · 2026-10-14 Asia/Shanghai
