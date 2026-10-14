# 余烬 · 精选周帽满 D794 / FeaturedPath.afterSettle「精选下一环」菜单化（settle 聊天钮去指令化 · ≠图身份首通settle下一环同题 · ≠深渊settle下一环同题 · ≠挑战settle下一环同题 · ≠团本settle下一环同题 · ≠短征settle下一环·回看同题 · ≠variety再拧 · ≠半行 · ≠庭内旁路 · ≠sx20 · ≠抬掉落/体力价/周帽 · ≠D673同题复读）

| 日 | 事 |
|----|-----|
| 2026-10-14 10:22 Asia/Shanghai | 总控批 A·M · 可施工 W1a插件+W1b物品 · L不批 · 勿勾 CLOSED · afterSettle+D794→开 ember_p1_featured_settle · 清 ConfirmTokens 教 enter/featurepath/forge 主 UI · 零改掉落/体力价/周帽 · |
| 2026-10-14 10:18 Asia/Shanghai | 初稿 STATUS=**待批 A** · 荐 M · TIP-DC CLOSED 后连开 · 拟 D13335 · TIP-DD |

STATUS=**已批 A · 方案 M** · APPROVED A·M · D13335 · TIP-DD · 可施工 W1a插件+W1b物品 · L不批 · 勿勾 CLOSED · 零改掉落/体力价/周帽 · 2026-10-14 10:22 Asia/Shanghai
> tip [`STATUS-ember-next-hard-debt-featured-settle-next-loop-need-design-2026-10-14.md`](../status/STATUS-ember-next-hard-debt-featured-settle-next-loop-need-design-2026-10-14.md) · RESEARCH [`RESEARCH-ember-featured-settle-next-loop-visible-2026-10-14.md`](RESEARCH-ember-featured-settle-next-loop-visible-2026-10-14.md) · backlog `B-featured-settle-next-loop-visible` · **≠关观察 ≠抬日表 ≠开 R ≠开 K3 ≠假开旧日常 ≠sx20 ≠菜单诚实/observe 空转 ≠半行独批 ≠Cast粒子主交付 ≠EliteAid/BossAid再拧 ≠todayRot/W1e独批 ≠抬掉落/体力价/周帽 ≠图身份settle下一环同题 ≠深渊settle下一环同题 ≠挑战settle下一环同题 ≠团本settle下一环同题 ≠短征settle下一环同题 ≠短征settle回看同题 ≠D673同题复读 ≠variety再拧 ≠完整 variety 经济 ≠第五件花料 ≠推荐花复读 ≠洗练/烙纹/转化/随机锻再拧 ≠庭内旁路再拧 ≠今日推复读 ≠路径追同题 ≠日差同题 ≠P格兼确认 ≠复开 D423 ≠教裸指令 ≠灾厄/fest重开 ≠Pack6 ≠densify ≠legacy abyss evacuate 大扫**

> **一句话玩家价值：** 精选图打完 / 周帽满后一点开「精选下一环」——再进同图、换精选/硬本路径、回冒险、去工坊、图装追都能点；**不**抬掉落/体力价/周帽、**不**教手打指令、**不**复读已齐 settle 同题、**不**复开 D673。

> **批 A = 采纳方案（荐 M）。批 A ≠ 施工 ≠ 关观察 ≠ 抬日表 ≠ 开 R ≠ 开 K3 ≠ 交 sx20。**

---

## 0. 证据表

| # | 来源 | 口径 |
|---|------|------|
| E1 | 总控 TIP-DC CLOSED `d5e5185a3` · 连开 TIP-DD | 内容优先；禁已齐同题；选题优先级挂机>短征>其余 settle 聊天钮 |
| E2 | 实扫挂机庭 | 旁路全弧 CLOSED；afk **零** gem gap → **无新硬债** |
| E3 | 实扫短征乐趣 | Cast/Aid/todayRot/variety/日差/路径追/settle/soft-reward/回看已齐；sx20 禁 → **无薄窗新硬债** |
| E4 | DESIGN-DC §6⑤ | 明文后置「精选周帽满 D794 / FeaturedPath.afterSettle「精选下一环」去指令化（另窗）」 |
| E5 | `EmberFeaturedPath.afterSettle` L229–232 | ConfirmTokens「精选下一环」→ enterCmd（裸 enter）/ `/corerpg p1 forge` / `/corerpg p1 mapfarm` |
| E6 | `EmberSettleService` L821–824 D794 | 周帽满 ConfirmTokens「下一步」→ `/corerpg p1 featurepath` / `/corerpg p1 challengepath` / `/ember_p1_adventure` |
| E7 | TrMenu **无** `ember_p1_featured_settle`；短征/团本/挑战/深渊/图身份 settle 范式可镜像 | TIP-CW/CZ/DA/DB/DC |
| E8 | **为何不是半行** | 主交付=薄页+改入口；见 §3.5 |
| E9 | 半行 REJECT `d00ed84bc` 保持 | backlog |

**一句话问题：** 挂机/短征无新薄债、图身份首通 settle 已齐后，精选结算仍钉在 ConfirmTokens（enter/forge/mapfarm + 周帽满 featurepath/challengepath），**没有**结算薄页聚合下一环。

**定位拍板：**

| 选项 | 结论 | 理由 |
|------|------|------|
| 挂机庭加厚 / 宝石 | **否** | 近饱和；gem gap 未亮 |
| 短征乐趣薄窗 / sx20 | **否** | 已齐 / 硬禁 |
| 图身份/深渊/短征/团本/挑战 settle 同题 | **否** | 派单禁；已 CLOSED |
| D673 同题复读 | **否** | TIP-DC 刚齐 |
| 只半行 | **否** | REJECT |
| **精选周帽满 D794 / FeaturedPath.afterSettle 下一环菜单化（荐）** | **采纳** | E2–E7；真可点；≠已齐 settle 同题 |

---

## 1. 玩家感知目标（≤3）

1. **一眼下一环：** 精选图结算或周帽满后自动出现/可开「精选下一环」薄页，见精选图名与路径/周帽态。
2. **一点去向：** 再进精选 / 换精选路径 / 换硬本路径 / 冒险 / 工坊 / 图装追 **全是点击**；**零**以 ConfirmTokens 教裸 `enter`/`featurepath`/`forge` 为主 UI。
3. **诚实可读：** lore 挂精选图+路径；周帽已满时再进钮灰态诚实。

---

## 2. 方案表

| 方案 | 内容 | 评价 |
|------|------|------|
| **A · 只改 Open/结算半行口号** | 口号 | **否决独批** — 仍空转 |
| **M · 插件改入口 + 薄 settle 页（荐）** | 真开菜单；清裸指令主路径；**零改**掉落/体力价/周帽 | **荐** — 真可点乐趣 |
| **L · 仅把一条聊天钮改 `/ember_p1_*`；无薄页聚合** | 减面 | **默认不批 L** |
| **W · 抬掉落/体力价/周帽 / sx20 / 已齐 settle 同题 / 庭内 / D673复读** | — | **否决** |

### 批 A 勾选（总控填）

- [x] **方案 M**（W1a 插件改 FeaturedPath.afterSettle + D794 下一环入口必做；W1b 薄 settle 页必做；W1c 清裸指令主路径同批；见 §3）
- [ ] 或勾 **方案 L**（仅改单钮命令；薄页后置）· **本窗不批 L**
- [x] 否决 A（只半行）/ W（抬表/sx20/已齐同题/庭内/D673复读）
- [x] 批注：总控批 A·M · 可施工 W1a插件+W1b物品 · L不批 · 勿勾 CLOSED · afterSettle+D794→开 ember_p1_featured_settle · 清 ConfirmTokens 教 enter/featurepath/forge 主 UI · 零改掉落/体力价/周帽 · 2026-10-14 10:22 Asia/Shanghai

---

## 3. 可落地（方案 M）

### 3.1 条件与键（钉死 · 只读）

| 项 | 口径 | 纪律 |
|----|------|------|
| 薄页 id | `ember_p1_featured_settle`（OpenCommand 建议 `/ember_p1_featured_settle`） | 新页；**不**复开图身份/深渊/短征/团本/挑战 settle 同题 |
| 触发 A | `EmberFeaturedPath.afterSettle`（本周精选图结算） | 禁改精选入账/周规则语义 |
| 触发 B | `EmberSettleService` D794（rotation 周帽满） | 禁改周帽数值 |
| 再进精选 | 菜单 actions 调既有 `enter`（challenge/普通） | **禁**主 UI 仍以裸 `/corerpg p1 enter …` ConfirmTokens 唯一交付 |
| 换精选路径 | 菜单 actions `corerpg p1 featurepath …` 或薄格 | 禁教手打 featurepath 为主 UI |
| 换硬本路径 | 菜单 actions `corerpg p1 challengepath …` 或既有页 | 禁教手打 challengepath 为主 UI |
| 工坊 | 开 `ember_p1_forge` | 禁改 forge 价/配方 |
| 冒险 | 开 `ember_p1_adventure` | 禁改冒险 Layout 主交 |
| 图装追 | 菜单 actions `corerpg p1 mapfarm` 或已有图装页 | 禁改 mapfarm 掉落/语义 |
| settle 入口 | afterSettle + D794：优先 `open` 薄页 **或** ConfirmTokens 主钮改为 `/ember_p1_featured_settle` | **禁**教指令三钮仍为唯一主 UI |
| 文案 | glance/下一步若保留辅钮，须菜单优先 | 禁保留教指令主路径 |
| 图身份/深渊/短征/团本/挑战/Cast/Aid/variety | **零改** | 本债≠同题复读 |

### 3.2 窗拆分

| 窗 | 做什么 | 不动 |
|----|--------|------|
| **W1a · 插件冲钉（必做）** | afterSettle + D794 下一环→开薄页；记住 settle 图/路径/周帽态；清 ConfirmTokens 教指令主路径；可选 `featured_settle_*` PAPI；单测 | 不改掉落/体力价/周帽 |
| **W1b · 薄 settle 页（必做 · TrMenu）** | `ember_p1_featured_settle`：再进 / 换精选路径 / 换硬本路径 / 冒险 / 工坊 / 图装追；lore 挂图名+路径+周帽 | 不改图身份/深渊/短征/团本/挑战薄页 / 庭内 |
| **W1c · 辅指针（同批可选）** | 若需 ActionBar/半行辅指 → **不可独批** | 禁半行独档 |

### 3.3 文案示例（短 · 可感）

```
§b精选 · 下一环
§7本周：§f锈轨精选 · 路径 §e精选·挑战 · 周帽 §f3/3
§8点选 · 再进 / 换路径 / 冒险 / 工坊 / 图装追
```

### 3.4 经济幅度

| 项 | 钉死 | 禁 |
|----|------|-----|
| 掉落 / 体力价 / 周帽 / 精选语义 | **零改** | 除非总控另批 |
| 奖励 | **零** 新 NI 结算奖 | 禁借下一环发经济 |
| 图身份/深渊/短征/团本/挑战/variety/soft-reward | **不碰** | 同题勿再交 |

### 3.5 为何不是半行

主交付 = **薄 featured settle 页真开菜单去向** + **插件改 afterSettle + D794 下一环入口** + 清 ConfirmTokens 教指令主路径。任何半行仅同批辅指针；**不可**半行独档过批（REJECT `d00ed84bc` 保持）。

---

## 4. 验收（施工号用 · 本号只定规格）

| # | 场景 | 期望 |
|---|------|------|
| V1 | 精选图结算（路径已选） | 下一环薄页可开；再进/工坊/冒险/图装追真开；路径 lore 诚实 |
| V2 | 周帽满 D794 | 薄页可开；换精选路径/换硬本路径/冒险真开；再进灰态诚实 |
| V3 | 未选精选路径 | 薄页可开换路径；不误开再进为主交付 |
| V4 | 结算文案 | 聊天 **不以** ConfirmTokens 教裸 enter/featurepath/forge/mapfarm 为主交付；主路径为菜单 |
| V5 | 零改抽测 | 掉落/体力价/周帽/Cast/Aid/variety/图身份·深渊·短征·团本·挑战薄页 不变；零 sx20；零半行独档 |

---

## 5. 禁区

- 半行 lore-only 独批（REJECT `d00ed84bc`）
- 图身份 settle下一环 / 深渊 settle下一环 / 短征 settle下一环·回看 / 团本 settle下一环 / 挑战 settle下一环 / variety再拧 / soft-reward / 路径追 / 日差同题复读
- D673 同题复读
- 抬掉落 / 体力价 / 周帽 / 日帽 / S表 / 伤表
- sx20 · Pack6 · 开R/K3 · 关观察 · densify · 灾厄/fest重开
- 庭内旁路再拧 · 教裸指令主 UI · stage 脏 runtime
- legacy deep-abyss evacuate 大扫

---

## 6. 后置（明文 · 本窗不做）

1. 庭内顺手宝石（须庭面先亮 gem gap）
2. variety 完整经济 / W1e todayRot 偏置
3. fest 继任 / 灾厄枢纽可见（须总控另批）
4. sx20（仍禁，除非总控另开窗）
5. FeaturedPath.offerPick / glance 全链教指令大扫（本窗只钉 settle 下一环；offerPick 若仅同批辅清可做，不可扩成路径系统重做）
6. legacy AbyssSettleService evacuate 大扫（须另窗证据）

---

## 变更记录

| 日 | 事 |
|----|-----|
| 2026-10-14 10:22 Asia/Shanghai | 总控 · **批 A·M** · 可施工 W1a插件+W1b物品 · L不批 · 勿勾 CLOSED · afterSettle+D794→开 ember_p1_featured_settle · 清 ConfirmTokens 教 enter/featurepath/forge 主 UI · 零改掉落/体力价/周帽 · 半行 REJECT `d00ed84bc` 保持 |
| 2026-10-14 10:18 Asia/Shanghai | 策划 · 初稿 STATUS=**待批 A** · 荐 M · TIP-DC CLOSED 后连开 · 选题精选周帽满 D794 / FeaturedPath.afterSettle 下一环菜单化 · 拟 D13335 · TIP-DD · ≠半行 ≠图身份/深渊/短征/团本/挑战settle同题 ≠D673同题 ≠variety再拧 ≠庭内旁路 · **改题有证据**（挂机近饱和·gem gap未亮；短征薄债已齐；DESIGN-DC§6⑤；afterSettle ConfirmTokens；D794 ConfirmTokens；无薄页；见 RESEARCH） |

*D13335 **已批 A · 方案 M** · APPROVED A·M · 精选周帽满 D794 / FeaturedPath.afterSettle 下一环菜单化 · TIP-DD。**可施工 W1a插件+W1b物品** · L不批 · 勿勾 CLOSED · afterSettle+D794→开 ember_p1_featured_settle · 清 ConfirmTokens 教 enter/featurepath/forge 主 UI · 零改掉落/体力价/周帽 · ≠半行 ≠已齐 settle 同题复读 ≠sx20 · 2026-10-14 10:22 Asia/Shanghai*
