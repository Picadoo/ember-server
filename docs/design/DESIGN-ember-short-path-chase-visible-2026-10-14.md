# 余烬 · 短征选页「路径追」可点（ShortPath 菜单化 + enter short-path · ≠日差同题 · ≠今日推/周目标复读 · ≠D406 首通同屏复读 · ≠半行 · ≠庭内旁路 · ≠sx20 · ≠抬表）
| 2026-10-14 04:22 Asia/Shanghai | 总控批 A·M · 全弧非 L · 派插件+物品 · 勿勾 CLOSED · 零改日帽/S/resolve 算法 |
| 2026-10-14 04:18 Asia/Shanghai | 初稿 STATUS=**待批 A** · 荐 M · TIP-CU CLOSED 后连开 · 拟 D13327 · TIP-CV |

STATUS=**已批 A · 方案 M** · 拟 D13327 · TIP-CV · 全弧非 L · 派插件+物品 · 勿勾 CLOSED · 零改日帽/S/resolve · 2026-10-14 04:22 Asia/Shanghai
> tip [`STATUS-ember-next-hard-debt-short-path-chase-need-design-2026-10-14.md`](../status/STATUS-ember-next-hard-debt-short-path-chase-need-design-2026-10-14.md) · RESEARCH [`RESEARCH-ember-short-path-chase-visible-2026-10-14.md`](RESEARCH-ember-short-path-chase-visible-2026-10-14.md) · backlog `B-short-path-chase-visible` · **≠关观察 ≠抬日表 ≠开 R ≠开 K3 ≠假开旧日常 ≠sx20 ≠菜单诚实/observe 空转 ≠半行独批 ≠Cast粒子主交付 ≠EliteAid/BossAid再拧 ≠todayRot/W1e独批 ≠抬 event_core ≠新 variety kind ≠完整 variety 经济 ≠第五件花料 ≠推荐花复读 ≠洗练/烙纹/转化/随机锻再拧 ≠庭内旁路再拧 ≠今日推复读 ≠本周短征可冲复读 ≠日差同题 ≠D406 首通同屏复读 ≠P格兼确认 ≠复开 D423 ≠教裸指令 ≠撤分格 ≠灾厄/fest重开 ≠Pack6 ≠densify ≠改 ShortPath 算法权重/日帽**

> **一句话玩家价值：** 打开短征选本就能点「路径追」——用点击切换首通追/有奖追/后段追，并一键进当前偏好 resolve 的下一本；**不**抬日帽、**不**改奖励、**不**再交日差同题。

> **批 A = 采纳方案（荐 M）。批 A ≠ 施工 ≠ 关观察 ≠ 抬日表 ≠ 开 R ≠ 开 K3 ≠ 交 sx20。**

---

## 0. 证据表

| # | 来源 | 口径 |
|---|------|------|
| E1 | 总控 TIP-CU CLOSED `3e3b14fd1` · 连开 TIP-CV | 强烈偏向短征日更/乐趣；庭内默认勿再交；**日差同题勿再交**；禁半行/Cast·Aid·variety再拧 |
| E2 | 今日推/周目标/日差/三冲已齐 | ≠再交同轨 |
| E3 | D406/D422 首通同屏已齐 | ≠本债主交 |
| E4 | ShortPath「Chat buttons only」+ glance 教裸指令 | EmberShortPath.java |
| E5 | 无 short_path* PAPI / 无 enter short-path / 选页无路径格 | RunPapi · short.yml |
| E6 | resolve(FC/DAY/LATE) live；Layout 行2空位可落 `0` | ShortPath · short.yml |
| E7 | **为何不是半行** | 主交付=真切偏好+enter；见 §3.5 |
| E8 | 半行 REJECT `d00ed84bc` 保持 | backlog |

**一句话问题：** ShortPath 粘性追猎已 live，但主 UI 仍是聊天——选页缺「切偏好 + 进下一本」真可点。

**定位拍板：**

| 选项 | 结论 | 理由 |
|------|------|------|
| 日差同题 / 软目标发奖 | **否** | 派单禁；经济另批 |
| 今日推 / 周目标 / 三冲 / D406 复读 | **否** | 已齐 |
| Cast/Aid/variety/todayRot 再拧 | **否** | 硬禁 |
| 庭内旁路 / 宝石 | **否** | 饱和 / gap 未亮 |
| 只 Open 半行 | **否** | REJECT |
| **路径追可点（荐）** | **采纳** | E4–E6；真可点；≠已齐冲口 |

---

## 1. 玩家感知目标（≤3）

1. **一眼偏好：** 选页入口见当前路径标签（首通追/有奖追/后段追/未选）与 resolve 下一本名。  
2. **一点切换：** 薄页点击切换三偏好或清空；**零**教玩家手打 `/corerpg p1 shortpath`。  
3. **一点进本：** 「进下一本」真 `enter short-path`；无 resolve / 体力不足时灰态诚实。

---

## 2. 方案表

| 方案 | 内容 | 评价 |
|------|------|------|
| **A · 只改 Open「去聊天选路径」半行** | 口号 | **否决独批** — 仍教指令=空转 |
| **M · PAPI + enter + 选页入口 + 薄路径页（荐）** | 切偏好 + 真进本；**零改**日帽/S/算法权重 | **荐** — 真可点乐趣 |
| **L · 仅选页入口打开聊天 shortpath；无 enter / 无薄页切偏好** | 减面 | **默认不批 L** |
| **W · 抬日帽/S/targets / sx20 / 日差同题 / 庭内 / 改 resolve 算法** | — | **否决** |

### 批 A 勾选（总控填）

- [x] **方案 M**（W1a PAPI+enter 必做；W1b 薄路径页+选页 `0` 必做；W1c Open 辅半行同批；见 §3）
- [ ] 或勾 **方案 L**（仅入口刷聊天；enter/薄页后置）· **本窗默认不批 L**
- [x] 否决 A（只半行）/ W（抬表/sx20/日差同题/庭内）
- [x] 批注：总控批 A·M · 全弧非 L · 派插件+物品 · 勿勾 CLOSED · 零改日帽/S/resolve 算法 · 2026-10-14 04:22 Asia/Shanghai

---

## 3. 可落地（方案 M）

### 3.1 条件与键（钉死 · 只读）

| 项 | 口径 | 纪律 |
|----|------|------|
| path id | 复用 `EmberShortPath`：FC=1 / DAY=2 / LATE=3 / NONE=0 | **禁**改 resolve 排序/日帽/奖励 |
| label/tip | 复用 `label()` / `tip()` 文案 | 禁另造经济承诺 |
| resolve | `EmberShortPath.resolve(runs,d,path)`；path=NONE 时：**灰拒进本**（或薄页强制先选——钉死：未选→`_ok=0`，进本拒绝并提示先选） | 禁静默改偏好 |
| `_ok` | path 有效 ∧ resolve 非空 ∧（体力门禁由菜单灰）→1 | 单条件灰 |
| enter | `corerpg p1 enter short-path`（别名 `short-chase-path`）→ 进 resolve 本；门禁同该本 | **禁**教裸指令当主 UI |
| 切偏好 | 薄页 actions：`corerpg p1 shortpath fc\|day\|late\|clear`（已有命令；菜单点） | 不新发明指令语义 |
| Cast/Aid/variety/日差 | **零改** | 本债≠打磨/同题 |

### 3.2 窗拆分

| 窗 | 做什么 | 不动 |
|----|--------|------|
| **W1a · 插件冲钉（必做）** | `short_path` / `_label` / `_tip` / `_resolve` / `_resolve_name` / `_ok` / `_line` + `enter short-path` + 单测 | 不改 resolve 算法/日帽/S/targets |
| **W1b · 选页入口 + 薄路径页（必做 · TrMenu）** | Layout `'#I H B Z#'`→`'#I0H B Z#'`（或等价空位落 **`0`**）；`0`=路径追入口→`menu: ember_p1_short_path`；薄页：三偏好+清空+「进下一本」真 enter；lore 挂 label+resolve；灰态诚实 | 不改十九本格 / D/F/L / B/Z |
| **W1c · Open 辅半行（同批）** | Open +「路径追见本页 0」 | **不可独批**；半行 REJECT 保持 |

### 3.3 文案示例（短 · 可感）

```
§e路径追
§f当前：§e短征·有奖追
§7下一本：§fSX07 烬塔回升
§8点开 · 切换偏好 / 一键进本
```

薄页「进下一本」：

```
§a进下一本
§7按当前路径 resolve
§8点进 · 真进本
```

### 3.4 与邻域边界

| 邻域 | 边界 |
|------|------|
| 今日推 D | 单本轮换标记；本债=粘性偏好+resolve |
| 本周短征 F | 周计数 short:5；本债≠抬 targets |
| 日差 L | 今日未领日奖另一本；**本窗不做同题** |
| D406 首通同屏 | 只读可见；本债=可点切偏好+enter |
| 庭内旁路 | **不交** |
| variety 结算回看 | 后置；本窗不做 |

### 3.5 为何不是半行

主交付 = **薄路径页真切偏好** + **`enter short-path` 真进本** + 选页入口。Open 半行仅同批辅指针；**不可**半行独档过批（REJECT `d00ed84bc` 保持）。

---

## 4. 验收（施工号用 · 本号只定规格）

| # | 场景 | 期望 |
|---|------|------|
| V1 | 已选 DAY 且存在 dayLeft 本 | 入口/薄页见有奖追 + resolve 名；进下一本可进该本 |
| V2 | 未选路径 | `_ok=0`；进本灰/拒并提示先选；可点三偏好切换 |
| V3 | FC 路径且无未领首通 | resolve 空；进本灰；可改 DAY/LATE |
| V4 | 零改抽测 | 日帽/S/targets/Cast/Aid/variety/日差 L 文案与行为不变；零 sx20；零半行独档 |

---

## 5. 禁区

- 半行独批 · 日差同题 · 今日推/周目标/三冲/D406 复读主交  
- Cast 粒子主交付 · Aid/variety再拧 · todayRot/W1e独批  
- 庭内旁路再拧 · 宝石（须庭面 gap）· densify  
- sx20 · 抬日帽/S/targets · Pack6 · 开R/K3 · 关观察 · 灾厄/fest重开 · 教裸指令 · stage脏runtime  
- 改 ShortPath resolve 排序/权重当「优化」主交付  

---

## 6. 后置（明文 · 本窗不做）

1. 日差软目标发奖 / 写入 `weekly_goals`（须总控另批经济）  
2. 庭内顺手宝石（须庭面先亮 gem gap）  
3. 短征花样结算回看 / variety 经济发核心 / W1e  
4. fest 继任 / 灾厄枢纽可见（须总控另批）  
5. sx20（仍禁，除非总控另开窗）  
6. settle 聊天钮去指令化大扫（可另窗；本窗以选页主 UI 为准）  

---

## 变更记录

| 日 | 事 |
|----|-----|
| 2026-10-14 04:22 Asia/Shanghai | 总控 · **批 A·M** · 全弧非 L · 派插件+物品 · 勿勾 CLOSED · 半行 REJECT `d00ed84bc` 保持 · 日差同题勿再交 |
| 2026-10-14 04:18 Asia/Shanghai | 策划 · 初稿 STATUS=**待批 A** · 荐 M · TIP-CU CLOSED 后连开 · 选题短征路径追可点 · 拟 D13327 · TIP-CV · ≠半行 ≠日差同题 ≠今日推/周目标/D406复读 ≠Cast/Aid/variety再拧 ≠庭内旁路 · **改题有证据**（ShortPath chat-only；无 enter；Layout 空位；见 RESEARCH） |

*D13327 **已批 A · 方案 M** · 短征路径追可点 · TIP-CV。**全弧非 L** · ≠半行 ≠日差同题 ≠今日推/周目标/D406复读 ≠Cast/Aid/variety再拧 ≠庭内旁路 · ≠sx20 · ≠改 resolve 算法 · 2026-10-14 04:22 Asia/Shanghai*
