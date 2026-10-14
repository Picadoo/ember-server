# 余烬 · 图身份首通结算「下一环」菜单化（settle 聊天钮去指令化 · ≠深渊settle下一环同题 · ≠挑战settle下一环同题 · ≠团本settle下一环同题 · ≠短征settle下一环·回看同题 · ≠精选周帽满D794同窗 · ≠variety再拧 · ≠半行 · ≠庭内旁路 · ≠sx20 · ≠抬掉落/体力价）

| 日 | 事 |
|----|-----|
| 2026-10-14 09:55 Asia/Shanghai | 总控批 A·M · 可施工 W1a插件+W1b物品 · L不批 · 勿勾 CLOSED · EmberSettleService D673→开 ember_p1_identity_settle · 清 ConfirmTokens 教 forge 主 UI · 零改掉落/体力价/首通包 · D794 后置 · |
| 2026-10-14 09:53 Asia/Shanghai | 初稿 STATUS=**待批 A** · 荐 M · TIP-DB CLOSED 后连开 · 拟 D13334 · TIP-DC |

STATUS=**已批 A · 方案 M** · APPROVED A·M · D13334 · TIP-DC · 可施工 W1a插件+W1b物品 · L不批 · 勿勾 CLOSED · 零改掉落/体力价/首通包 · 2026-10-14 09:55 Asia/Shanghai
> tip [`STATUS-ember-next-hard-debt-identity-fc-settle-next-loop-need-design-2026-10-14.md`](../status/STATUS-ember-next-hard-debt-identity-fc-settle-next-loop-need-design-2026-10-14.md) · RESEARCH [`RESEARCH-ember-identity-fc-settle-next-loop-visible-2026-10-14.md`](RESEARCH-ember-identity-fc-settle-next-loop-visible-2026-10-14.md) · backlog `B-identity-fc-settle-next-loop-visible` · **≠关观察 ≠抬日表 ≠开 R ≠开 K3 ≠假开旧日常 ≠sx20 ≠菜单诚实/observe 空转 ≠半行独批 ≠Cast粒子主交付 ≠EliteAid/BossAid再拧 ≠todayRot/W1e独批 ≠抬掉落/体力价/首通包 ≠深渊settle下一环同题 ≠挑战settle下一环同题 ≠团本settle下一环同题 ≠短征settle下一环同题 ≠短征settle回看同题 ≠精选周帽满D794同窗 ≠variety再拧 ≠完整 variety 经济 ≠第五件花料 ≠推荐花复读 ≠洗练/烙纹/转化/随机锻再拧 ≠庭内旁路再拧 ≠今日推复读 ≠路径追同题 ≠日差同题 ≠P格兼确认 ≠复开 D423 ≠教裸指令 ≠灾厄/fest重开 ≠Pack6 ≠densify ≠legacy abyss evacuate 大扫**

> **一句话玩家价值：** 图身份首通保底后一点开「下一环」——去工坊养特色件、穿装备、回冒险下一张图都能点；**不**抬掉落/体力价、**不**教手打指令、**不**复读深渊/短征/团本/挑战 settle 同题、**不**同窗扫 D794。

> **批 A = 采纳方案（荐 M）。批 A ≠ 施工 ≠ 关观察 ≠ 抬日表 ≠ 开 R ≠ 开 K3 ≠ 交 sx20。**

---

## 0. 证据表

| # | 来源 | 口径 |
|---|------|------|
| E1 | 总控 TIP-DB CLOSED `fd1152e1f` · 连开 TIP-DC | 内容优先；禁已齐同题；选题优先级挂机>短征>其余 settle 聊天钮 |
| E2 | 实扫挂机庭 | 旁路全弧 CLOSED；afk **零** gem gap → **无新硬债** |
| E3 | 实扫短征乐趣 | Cast/Aid/todayRot/variety/日差/路径追/settle/soft-reward/回看已齐；sx20 禁 → **无薄窗新硬债** |
| E4 | D13333 DESIGN §6⑥ | 明文后置「全服其余 settle 聊天钮（图身份首通 D673 / 精选路径 D794 等）逐支另窗」 |
| E5 | EmberSettleService L697–700 D673 | ConfirmTokens「下一步」→ `/corerpg p1 forge` / `/ember_p1_gear` / `/ember_p1_adventure` |
| E6 | TrMenu **无** `ember_p1_identity_settle`；短征/团本/挑战/深渊 settle 范式可镜像 | TIP-CW/CZ/DA/DB |
| E7 | **为何不是半行** | 主交付=薄页+改入口；见 §3.5 |
| E8 | 半行 REJECT `d00ed84bc` 保持 | backlog |

**一句话问题：** 挂机/短征无新薄债后，图身份首通庆祝仍钉在 ConfirmTokens 三钮（工坊仍走 `/corerpg p1 forge`），**没有**结算薄页聚合下一环。

**定位拍板：**

| 选项 | 结论 | 理由 |
|------|------|------|
| 挂机庭加厚 / 宝石 | **否** | 近饱和；gem gap 未亮 |
| 短征乐趣薄窗 / sx20 | **否** | 已齐 / 硬禁 |
| 深渊/短征/团本/挑战 settle 同题 | **否** | 派单禁；已 CLOSED |
| D794 精选周帽满同窗 | **否 · 本窗** | §6 逐支另窗；本窗钉 D673 |
| 只半行 | **否** | REJECT |
| **图身份首通结算下一环菜单化（荐）** | **采纳** | E2–E6；真可点；≠已齐 settle 同题 |

---

## 1. 玩家感知目标（≤3）

1. **一眼下一环：** 图身份首通保底庆祝后自动出现/可开「图身份下一环」薄页，见图名与族/槽。
2. **一点去向：** 工坊 / 装备 / 冒险（可选图装追）**全是点击**；**零**以 ConfirmTokens 教 `/corerpg p1 forge` 为主 UI。
3. **诚实可读：** lore 挂本图特色族+槽；无首通时本页不误开。

---

## 2. 方案表

| 方案 | 内容 | 评价 |
|------|------|------|
| **A · 只改 Open/结算半行口号** | 口号 | **否决独批** — 仍空转 |
| **M · 插件改入口 + 薄 settle 页（荐）** | 真开菜单；清裸指令主路径；**零改**掉落/体力价/首通包 | **荐** — 真可点乐趣 |
| **L · 仅把一条聊天钮改 `/ember_p1_*`；无薄页聚合** | 减面 | **默认不批 L** |
| **W · 抬掉落/体力价 / sx20 / 已齐 settle 同题 / 庭内 / D794同窗** | — | **否决** |

### 批 A 勾选（总控填）

- [x] **方案 M**（W1a 插件改图身份首通 settle 下一环入口必做；W1b 薄 settle 页必做；W1c 清裸指令主路径同批；见 §3）
- [ ] 或勾 **方案 L**（仅改单钮命令；薄页后置）· **本窗不批 L**
- [x] 否决 A（只半行）/ W（抬表/sx20/已齐同题/庭内/D794同窗）
- [x] 批注：总控批 A·M · D13334 · TIP-DC · 可施工 W1a插件+W1b物品 · L不批 · 勿勾 CLOSED · EmberSettleService D673→开 ember_p1_identity_settle · 清 ConfirmTokens 教 forge 主 UI · 零改掉落/体力价/首通包 · D794 后置 · 2026-10-14 09:55 Asia/Shanghai

---

## 3. 可落地（方案 M）

### 3.1 条件与键（钉死 · 只读）

| 项 | 口径 | 纪律 |
|----|------|------|
| 薄页 id | `ember_p1_identity_settle`（OpenCommand 建议 `/ember_p1_identity_settle`） | 新页；**不**复开深渊/短征/团本/挑战 settle 同题 |
| 触发 | `in.firstClear != null` 且 `validFamily(loot.family)`（对齐现 D673） | 禁改首通/图身份语义 |
| 工坊 | 开 `ember_p1_forge` | 禁改 forge 价/配方；**禁**主 UI 仍以 `/corerpg p1 forge` ConfirmTokens 唯一交付 |
| 装备 | 开 `ember_p1_gear` | 禁改装备 Layout 主交 |
| 冒险 | 开 `ember_p1_adventure` | 禁改冒险 Layout 主交 |
| 图装追（可选） | 菜单 actions `corerpg p1 mapfarm` 或已有图装页 | 禁改 mapfarm 掉落/语义 |
| settle 入口 | `EmberSettleService` D673：优先 `open` 薄页 **或** ConfirmTokens 主钮改为 `/ember_p1_identity_settle` | **禁**三钮仍为唯一主 UI |
| 文案 | glance/下一步若保留辅钮，须菜单优先 | 禁保留教指令主路径 |
| 深渊/短征/团本/挑战/Cast/Aid/variety/D794 | **零改** | 本债≠同题复读 |

### 3.2 窗拆分

| 窗 | 做什么 | 不动 |
|----|--------|------|
| **W1a · 插件冲钉（必做）** | 图身份首通下一环→开薄页；记住 settle 图/族/槽；清 ConfirmTokens 三钮主路径；可选 `identity_settle_*` PAPI；单测 | 不改掉落/体力价/首通包 |
| **W1b · 薄 settle 页（必做 · TrMenu）** | `ember_p1_identity_settle`：工坊 / 装备 / 冒险 / 可选图装追；lore 挂图名+族+槽 | 不改深渊/短征/团本/挑战薄页 / 庭内 |
| **W1c · 辅指针（同批可选）** | 若需 ActionBar/半行辅指 → **不可独批** | 禁半行独档 |

### 3.3 文案示例（短 · 可感）

```
§6图身份下一环
§7本图：§f烬门庭 · 保底 §e刃握 · 刃
§8点选 · 工坊养件 / 穿装备 / 下一张图
```

### 3.4 经济幅度

| 项 | 钉死 | 禁 |
|----|------|-----|
| 掉落 / 体力价 / 首通包 / 图身份语义 | **零改** | 除非总控另批 |
| 奖励 | **零** 新 NI 结算奖 | 禁借下一环发经济 |
| 深渊/短征/团本/挑战/variety/soft-reward/D794 | **不碰** | 同题勿再交 |

### 3.5 为何不是半行

主交付 = **薄 identity settle 页真开菜单去向** + **插件改图身份首通 settle 下一环入口** + 清 ConfirmTokens 教指令主路径。任何半行仅同批辅指针；**不可**半行独档过批（REJECT `d00ed84bc` 保持）。

---

## 4. 验收（施工号用 · 本号只定规格）

| # | 场景 | 期望 |
|---|------|------|
| V1 | 图身份首通结算 | 下一环薄页可开；工坊/装备/冒险真开；族/槽 lore 诚实 |
| V2 | 非首通 / 无 family | 本页不误开；既有 settle 路径不动 |
| V3 | 结算文案 | 聊天 **不以** ConfirmTokens 三钮（含 `/corerpg p1 forge`）为主交付；主路径为菜单 |
| V4 | 零改抽测 | 掉落/体力价/首通包/Cast/Aid/variety/深渊·短征·团本·挑战薄页/D794 不变；零 sx20；零半行独档 |

---

## 5. 禁区

- 半行 lore-only 独批（REJECT `d00ed84bc`）
- 深渊 settle下一环 / 短征 settle下一环·回看 / 团本 settle下一环 / 挑战 settle下一环 / variety再拧 / soft-reward / 路径追 / 日差同题复读
- 精选周帽满 D794 / FeaturedPath.afterSettle 同窗大扫
- 抬掉落 / 体力价 / 首通包 / 日帽 / S表 / 伤表
- sx20 · Pack6 · 开R/K3 · 关观察 · densify · 灾厄/fest重开
- 庭内旁路再拧 · 教裸指令主 UI · stage 脏 runtime
- legacy deep-abyss evacuate 大扫

---

## 6. 后置（明文 · 本窗不做）

1. 庭内顺手宝石（须庭面先亮 gem gap）
2. variety 完整经济 / W1e todayRot 偏置
3. fest 继任 / 灾厄枢纽可见（须总控另批）
4. sx20（仍禁，除非总控另开窗）
5. 精选周帽满 D794 / FeaturedPath.afterSettle「精选下一环」去指令化（另窗）
6. legacy AbyssSettleService evacuate 大扫（须另窗证据）

---

## 变更记录

| 日 | 事 |
|----|-----|
| 2026-10-14 09:55 Asia/Shanghai | 总控 · **批 A·M** · 可施工 W1a插件+W1b物品 · L不批 · 勿勾 CLOSED · EmberSettleService D673→开 ember_p1_identity_settle · 清 ConfirmTokens 教 forge 主 UI · 零改掉落/体力价/首通包 · D794 后置 · 半行 REJECT `d00ed84bc` 保持 |
| 2026-10-14 09:53 Asia/Shanghai | 策划 · 初稿 STATUS=**待批 A** · 荐 M · TIP-DB CLOSED 后连开 · 选题图身份首通结算下一环菜单化 · 拟 D13334 · TIP-DC · ≠半行 ≠深渊/短征/团本/挑战settle同题 ≠D794同窗 ≠variety再拧 ≠庭内旁路 · **改题有证据**（挂机近饱和·gem gap未亮；短征薄债已齐；D13333§6⑥；Settle D673 ConfirmTokens；无薄页；见 RESEARCH） |

*D13334 **已批 A · 方案 M** · APPROVED A·M · 图身份首通结算下一环菜单化 · TIP-DC。**可施工 W1a插件+W1b物品** · L不批 · 勿勾 CLOSED · EmberSettleService D673→开 ember_p1_identity_settle · 清 ConfirmTokens 教 forge 主 UI · 零改掉落/体力价/首通包 · D794 后置 · ≠半行 ≠已齐 settle 同题复读 ≠sx20 · 2026-10-14 09:55 Asia/Shanghai*
