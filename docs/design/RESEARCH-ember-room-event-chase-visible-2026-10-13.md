# RESEARCH · 房间事件「冲一下」可感（事件窗小乐趣 · 零抬 event_rate/Pack）· 2026-10-13

> **状态：** 待批 A · 荐方案 M · TIP-CE / 拟 **D13310** · docs-only · 2026-10-13 22:18 Asia/Shanghai  
> **起因：** 总控【TIP-CD CLOSED】短征今日推 live · menu `5611a4337` · jar `1.65.805-d13309` · PASS `8c4b8ce2d`（今日 sx11）。下一波交薄 tip：荐 **事件窗小乐趣**（此前两后置）；或其它日征/挂机庭可感。硬禁 sx20 / 抬日帽·AFK表 / L13 / K3 live / 诚实空转 / Pack6。  
> **上游：** TIP-CD CLOSED · D13309 今日推 · D13304–08 挂机庭四件齐 · D13305 R2 EliteCast · 多窗 §6 明文后置「事件窗小乐趣」  
> **硬禁：** **零抬** `daily_kills`/`afk.tiers` · **零** sx20 · **零** L13/K3/Pack6/天赋/灰印/R · **零** 伤乘/skill_mult · **零** Affix densify · **零** sure/pity/soft 诚实半行再叠 · **零** 菜单诚实空转 / observe 空转 · **零** 假开 `gate_daily` · **零** 抬 `event_rate`/`event_core`/软保底 NEED · **零** 重开 P1 已关 `calamity join`

---

## 0. 一句话

房间事件软保底与出率已齐，但冒险页只有 Open tell——没有「一点进本刷事件」；限时 fest 已纪念、灾厄 join 在 P1 legacy 闸拒。债清：冲图键 + 可点格。

---

## 1. 选题扫描（TIP-CD 后 · 事件窗优先）

| 候选 | 裁决 | 理由 |
|------|------|------|
| **房间事件「冲一下」可感** | **采纳** | 总控两后置现点名；soft_hope 只 tell 无动作；chase 图可复用 featured/已通最高 Q；薄 jar+TrMenu；可感挂时长 |
| 国庆 fest 再开限时 | **否** | fest 已纪念面齐；新限时=大窗，非薄 tip |
| 灾厄窗主枢纽奔赴 | **否** | P1 `legacy_gate` 拒 `calamity join`；重开=LEAK L7 政策窗，非本薄 tip |
| 再叠挂机庭第五件/诚实半行 | **否** | 四件齐；空转硬禁 |
| sx20 / 抬日表·日帽 / Pack6 | **否** | 硬禁 |
| 观察 exhausted 空声明 | **否** | 有可施工真债 |

**择优结论：** 「冲一下」=事件窗小乐趣的薄可施工面——不新开活动本、不重开灾厄闸；把已 live 的 soft_hope 从口号变成一点进本。故 **写死荐 M = 房间事件冲一下可感**。

---

## 2. 证据（现网只读 · 2026-10-13）

| # | 事实 | 路径 | 含义 |
|---|------|------|------|
| E1 | `event_soft_hope` 仅冒险 Open tell；无独立可点格 | `ember_p1_adventure.yml` | 见得到，冲不到 |
| E2 | SoftPity NEED=1 · event_rate 0.85 · 池 9 · W2b/W3 live | `EmberEventSoftPity` / runs.yml | **禁**再抬率/扩 Pack；债在导航动作 |
| E3 | `featured_key` live；花样仅已通普通重打出 | `EmberRunPapi` / Variety 闸 | 冲图须落「已首通」键 |
| E4 | 冒险 Layout 第三行 `'#Ws b u n'` 有空位可落 **E** | 菜单 | 薄可施工 |
| E5 | fest `fest_window` 纪念面齐；活动本已下架逻辑 live | `ember_p1_fest` / Festival | 非本窗主债 |
| E6 | P1 `calamity: ['', status, info]` · join 拒 | `ember-v1.yml` legacy_gate | 灾厄奔赴不可当薄交付 |
| E7 | 多窗 §6 / 总控 TIP-CD 点名荐事件窗 | DESIGN D13308–09 · 派单 | 合法接力 |
| E8 | 挂机庭四件齐；短征今日推已 live | D13304–09 | 勿再叠花料族 |

**一句话缺口：** 知道「下次必有房间事件」，仍要自己翻七图猜哪本已通——冲事件乐趣断在一键。

---

## 3. 方案方向（荐 M · 详 DESIGN）

1. **薄 jar：** `%corerpg_p1_event_chase%` = 冲图标（本周精选若已首通，否则已通最高 Q01–Q07；皆无则空）；`_name`；`%corerpg_p1_event_soft_ready%` = dry≥NEED ? 1 : 0（复用 SoftPity，**零改** NEED）  
2. **进本：** `corerpg enter %event_chase%` 或别名 `enter event-chase` · **同**现网体力/门禁；花样规则服务端原样  
3. **TrMenu：** 冒险空位 **E**「房间事件 · 冲一下」· lore 挂 soft_hope + chase_name +「仅已通普通重打」· ready=1 可亮态 · 无 chase/未解锁灰  
4. **Open 半行（同批薄）：** 保留/收束 soft_hope 指针，避免只靠刷屏 tell  
5. **施工岗：** 批后 **余烬-插件（薄 chase/ready PAPI + 可选别名）** + **余烬-物品（TrMenu）**；不派怪物/地图；**不**动 calamity legacy

---

## 4. 明确不做

- 抬 `event_rate` / `event_core` / SoftPity NEED · Pack6 新 kind · Affix densify  
- 重开 `calamity join` · 新国庆限时本 · sx20 · 抬日帽/AFK 表  
- 只加 Open/S 半行无进本（诚实空转）· 菜单 sure/pity 半行再叠  
- Pack6 / 天赋 / 灰印 / R / L13 / K3 · 复开 D423 · 假开 `gate_daily` · stage 脏 runtime · 再叠挂机庭花料第五件  

## STATUS: 待批 A · 荐 M · 拟 D13310 · 2026-10-13 22:18 Asia/Shanghai
