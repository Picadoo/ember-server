# 余烬 · 房间事件「冲一下」可感（事件窗小乐趣 · 零抬率/Pack · ≠sx20）

STATUS=**CLOSED live · 批 A · 方案 M · D13310** · jar `1.65.807-d13310b.local` · menu `1b4a9d78e` · PASS `abc14a205` · 总控勾毕 2026-10-13 23:05 Asia/Shanghai · TIP-CE · 2026-10-13 22:18 Asia/Shanghai · tip [`STATUS-ember-next-hard-debt-room-event-chase-need-design-2026-10-13.md`](../status/STATUS-ember-next-hard-debt-room-event-chase-need-design-2026-10-13.md) · RESEARCH [`RESEARCH-ember-room-event-chase-visible-2026-10-13.md`](RESEARCH-ember-room-event-chase-visible-2026-10-13.md) · backlog `B-room-event-chase-visible` · **≠关观察 ≠抬日表 ≠开 R ≠开 K3 ≠假开旧日常 ≠sx20 ≠菜单诚实/observe 空转 ≠抬 event_rate/event_core/NEED ≠Pack6 ≠重开 calamity join ≠复开 D423 ≠教裸指令**

> **一句话玩家价值：** 打开冒险就能看见房间事件保底，一点进「该冲的已通本」——保底就绪时更有冲劲；**不**抬出率、**不**扩 Pack、**不**重开灾厄闸。

> **批 A = 采纳方案（荐 M）。批 A ≠ 施工 ≠ 关观察 ≠ 抬日表 ≠ 开 R ≠ 开 K3 ≠ 交 sx20。**

---

## 0. 证据表

| # | 来源 | 口径 |
|---|------|------|
| E1 | 总控 TIP-CD CLOSED · 连开 | 荐事件窗小乐趣；禁 sx20/抬表/诚实空转/L13/K3/Pack6 |
| E2 | adventure Open 仅 soft_hope tell | 见得到无冲口 |
| E3 | SoftPity / event_rate 0.85 / 池 9 live | 禁抬率扩 Pack；债=导航动作 |
| E4 | featured_key live；花样闸=已通普通 | 冲图标须已首通 |
| E5 | Layout 第三行空位可落 E | 薄可施工 |
| E6 | fest 纪念齐；calamity join P1 拒 | 非本薄 tip 主交付 |
| E7 | 多窗 §6 后置事件窗 | D13308–09 合法接力 |

**一句话问题：** 房间事件系统已可感在局内，城内缺「一点冲」——事件窗小乐趣断在菜单动作。

**定位拍板：**

| 选项 | 结论 | 理由 |
|------|------|------|
| 再叠 Open/S 诚实半行 | **否** | 空转硬禁 |
| 抬 event_rate / Pack6 / NEED | **否** | 硬禁 / 已齐 |
| 新 fest / 重开 calamity join | **否** | 非薄；P1 闸政策窗 |
| 再叠挂机庭花料 | **否** | 四件齐 |
| **房间事件冲一下（荐）** | **采纳** | E2–E5；薄 jar+菜单；可感挂时长 |

---

## 1. 玩家感知目标（≤3）

1. **一眼保底：** 冒险页见「房间事件」格，挂 soft_hope（就绪时更醒目）。  
2. **一点进本：** 点格进冲图标（已通普通），门禁/体力同该本；**不**教指令。  
3. **零抬经济：** 不改出率、核心张数、软保底 NEED、日帽、AFK 表。

---

## 2. 方案表

| 方案 | 内容 | 评价 |
|------|------|------|
| **A · 只加 Open/S 半行复读 soft_hope** | 口号 | **否决独批** — 仍无冲口 |
| **M · chase 键 + 冒险 E 格进本（荐）** | jar `event_chase*`/`event_soft_ready` + 菜单 E 真 enter + Open 半行指针 · **零改**率/Pack/NEED | **荐** — 债清；薄可施工；可感 |
| **L · 本窗改交新 fest / 重开灾厄 / 抬率·Pack6** | 换题或动表 | **否决独批** — 硬禁或非薄 |
| **W · 诚实空转 / 假写固定 q01 不验首通 / 无键硬编码** | — | **否决** — 假写或不薄 |

### 批 A 勾选（总控填）

- [x] **方案 M**（W1a chase/ready 必做；W1b 冒险 E 格必做；W1c Open 半行同批；见 §3）
- [x] 否决 A（只口号）/ L（新 fest·灾厄·抬率 Pack）/ W（假写/空转）
- [x] 批注：总控批 A·M · D13310 · TIP-CE · 先插件 W1a chase/ready 再物品 W1b/c · 零抬率/Pack/NEED · fest/灾厄后置


---

## 3. 可落地（方案 M）

### 3.1 冲图钉死（只读 · 零经济）

| 项 | 口径 | 纪律 |
|----|------|------|
| 冲图标 | 若本周 `featured` 已首通 → 用 featured；否则取已通最高 `q01`…`q07`；皆无 → 空 | **禁**指未通图（花样不出） |
| 键 | `%corerpg_p1_event_chase%` / `_name` / `%corerpg_p1_event_soft_ready%`（0/1←SoftPity.get≥NEED） | ready **只读**；禁改 NEED |
| 进本 | `corerpg enter <chase>` 或 `enter event-chase` | **同**现网体力/解锁；禁第二奖励轨 |
| 奖励 | **不变** | 禁抬 event_core / 日帽 / 印记 |

### 3.2 窗拆分

| 窗 | 做什么 | 不动 |
|----|--------|------|
| **W1a · chase/ready PAPI（必做 · 插件）** | `EmberRunPapi`（或并列）增 `event_chase`/`_name`/`event_soft_ready`；单测：精选已通优先、未通回落最高已通、全未通空、ready 边界 | 不改 SoftPity.onSettle；不改 event_rate/weights |
| **W1b · 冒险 E 格（必做 · TrMenu）** | Layout 第三行空位落 **E**（例 `'#WsEb u n'` 或等价，保留 W/s/b/u/n）；名 `§e房间事件 · 冲一下`；lore：`event_soft_hope` + chase_name +「仅已通普通重打出事件」；ready=1 可用亮材/名；actions：`enter %event_chase%`（或 event-chase）；无 chase / 体力不足 / 未解锁灰态诚实 | 不改 Q01–Q07 格；不改 H 短征今日推；不改 S/W 其它语义为主交付 |
| **W1c · Open 半行（必做同批）** | Open 保留短 soft_hope 或收束为「冲一下见本页 E」；避免只刷屏 | 不盖 mapid_board / 今日推半行 |
| **W1d · fest/灾厄（本窗不做）** | — | 不重开 join；不动 fest 纪念 |
| **W1e · 经济/地图（默认不做）** | — | 禁动 event_core / Pack / 地图 / MM |

### 3.3 与邻域边界

| 邻域 | 边界 |
|------|------|
| D296/D623/D12206/D12208 事件必感链 | **保留**；本债=城内冲口 |
| D728 soft_hope PAPI | **复用**；不另造 hope 文案体系 |
| D13309 今日推 | **保留**；E 与短征 H 分流 |
| D13304–08 挂机庭花料 | **不叠**第五件 |
| fest / calamity | **本窗不做** |
| sx20 / D423 | **不做 / 不复开** |

---

## 4. 验收（施工号用 · 本号只定规格）

| ID | 项 |
|----|-----|
| V1 | 冒险见 E 格；lore 含 soft_hope；有 chase 时名与 `event_chase_name` 一致 |
| V2 | 点 E 进本与直接点该 Q 格同门禁/扣体力；花样闸仍要求已通普通 |
| V3 | 精选已通 → chase=featured；精选未通有其它已通 → 回落最高已通；全未通 → 灰、无假成功 |
| V4 | ready=1 时亮态可辨；ready 翻转不改 NEED/出率 |
| V5 | `git show`：**无**改 event_rate / event_core / NEED / afk.tiers / daily_kills / Pack 池 |
| V6 | 玩家面无裸指令教学；H 今日推 / 十九短征 / 花料庭行为不变 |

---

## 5. 禁区

- 抬 event_rate / event_core / SoftPity NEED · Pack6 新 kind · Affix densify  
- 重开 calamity join · 新国庆限时本 · sx20 · 抬日帽/AFK 产量  
- 只半行无进本 · 假写固定 q01 不验首通 · 静默改其它本奖励  
- 菜单诚实/sure·pity·soft 半行空转 · observe 空转 · 假开旧日常  
- Pack6 / 天赋 / 灰印 / R / L13 / K3 · 复开 D423 · stage 脏 runtime  
- 再叠挂机庭顺手花料第五件  

---

## 6. 后置（明文 · 本窗不做）

1. 灾厄窗主枢纽可见（须总控另批 legacy 政策）  
2. 新限时活动本（fest 继任）  
3. 冲一下「本局已抽 kind」预告（禁剧透种子）  
4. sx20（仍禁，除非总控另开窗）  

---

## 变更记录

| 日 | 事 |
|----|-----|
| 2026-10-13 | 策划 · 初稿 STATUS=**待批 A** · 荐 M · TIP-CD 后连开 · 选题房间事件冲一下（事件窗小乐趣）· 拟 D13310 · ≠sx20 ≠抬率 ≠Pack6 ≠重开 calamity ≠复开 D423 |
| 2026-10-13 | 总控【TIP-CE CLOSED】· jar `1.65.807-d13310b` · menu `1b4a9d78e` · PASS `abc14a205` · STATUS=**已批 A · 方案 M · CLOSED** · tip/STATUS/backlog 勾毕旁注 · fest/灾厄后置 |

*D13310 · 房间事件冲一下 · **已批 A·方案 M** · CLOSED live menu `1b4a9d78e` · PASS `abc14a205` · ≠关观察 ≠抬日表 ≠sx20。*
