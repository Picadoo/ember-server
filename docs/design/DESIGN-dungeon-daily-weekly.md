# 日周本 + 挂机大厅 — 策划提纲

**日期：** 2026-09-12（Asia/Shanghai）  
**作者岗：** 余烬-策划  
**依赖现状：** 附魔/修理闭环已通（见 `docs/design/DESIGN-ember-enchant-anvil.md` / `docs/status/STATUS-rpg-loop.md`）；演示地窟 EmberCrypt 三怪 + NI 掉落已就绪。  
**本轮目标栈：** TrMenu（入口菜单）+ DungeonPlus（独立副本，标称 1.12~1.20）+ 既有 MythicMobs / NeigeItems。  
**硬约束：** 不改 Paper / NMS；不叠新核心钩子。落地交给插件 / 物品 / 怪物岗；验收找测试岗。

---

## 1. 目标与定位

### 1.1 核心循环（已定）

```
挂机大厅刷材料（稳态产出）
        │
        ├─→ 合成附魔晶 / 修理装备（现有 CoreCraft / CoreEnchant / CoreAnvil）
        │
        └─→ 日/周独立副本（限次高收益：核心碎片、稀有装备、通关箱）
```

- **挂机区** = 日常材料水龙头，无限次、节奏平缓，服务养成闭环。
- **日/周本** = 限次尖峰产出，独立实例，不污染挂机区刷怪密度。
- 演示地窟 EmberCrypt 的三怪与 NI 掉落，本轮拆成两条线复用：
  - 挂机区：僵尸 / 骷髅为主（碎片、骨尘）。
  - 日/周本：蛮兵 + 波次/Boss 脚本（核心碎片、装备、通关奖励）。

### 1.2 本轮交付（策划侧）

1. TrMenu 入口树文案与跳转约定。  
2. 挂机大厅、日常本、周常本的次数 / 重置 / 奖励接 NI·MM。  
3. DungeonPlus 脚本与菜单对接的 YAML 草案（示意，非最终落地文件名以安装后插件目录为准）。  
4. 分工与验收清单。

**本轮不做：** AttributePlus 起服（仍停放）；经验球重开；中/高阶附魔晶分种；新 Paper 钩子。

---

## 2. 空间与入口总览

| 场所 | 世界/实例 | 进入方式 | 次数 |
|------|-----------|----------|------|
| 主城 / 大厅枢纽 | 主世界固定点（建议 `spawn` 旁或独立大厅坐标） | 进服默认 / `/spawn` | — |
| 挂机大厅 | 主世界固定刷怪区（或单独 world，非 DungeonPlus 实例） | TrMenu「挂机大厅」传送 | 无限 |
| 日常本 · 余烬余烬窟·日 | DungeonPlus 独立实例 | TrMenu「日常副本」 | **3 次/日** |
| 周常本 · 余烬深核·周 | DungeonPlus 独立实例 | TrMenu「周常副本」 | **1 次/周** |

坐标占位（落地时由插件/测试实测填入）：

| 点位 ID | 用途 | 占位 |
|---------|------|------|
| `hub_menu` | 菜单 NPC / 告示牌 | TBD |
| `afk_lobby` | 挂机区出生点 | 可沿用现 RpgBot 测试点附近，或新建平台 |
| `dungeon_daily_start` | 日副本出生 | DungeonPlus 脚本内 |
| `dungeon_weekly_start` | 周副本出生 | DungeonPlus 脚本内 |
| `dungeon_exit` | 通关/失败回大厅 | `hub` 或 `afk_lobby` |

---

## 3. TrMenu 入口菜单

### 3.1 菜单树

```
余烬入口（主菜单）          命令建议：/ember 或 /menu
├─ 挂机大厅                 → 传送 afk_lobby + 提示今日材料目标
├─ 日常副本                 → 子菜单「余烬窟·日」
│   ├─ 查看剩余次数          （PAPI / DungeonPlus 占位）
│   ├─ 开始挑战              → dp 进本命令
│   └─ 奖励预览              → 静态 lore / 打开预览 GUI
├─ 周常副本                 → 子菜单「深核·周」
│   ├─ 查看剩余次数
│   ├─ 开始挑战
│   └─ 奖励预览
├─ 养成指引                 → 文案：碎片→晶→附魔；核心→大修/周本
└─ 关闭
```

### 3.2 文案要点（中文）

- 主标题：`§6余烬 · 冒险入口`
- 挂机：`§a挂机大厅 §7| 无限刷碎片与骨尘`
- 日本：`§e日常 · 余烬窟 §7| 今日剩余 {daily_left}/3`
- 周本：`§c周常 · 深核 §7| 本周剩余 {weekly_left}/1`
- 次数不足：`§c次数已用尽，请等待刷新（日 0:00 / 周一面 0:00，Asia/Shanghai）`
- 养成指引短文：击杀挂机怪收集材料 → 工作台合成附魔晶 → 附魔台强化余烬刃/护符 → 铁砧用碎片或核心修理 → 日/周本冲击核心与稀有装备。

### 3.3 跳转约定（落地命令占位）

| 菜单动作 | 命令占位（以 DungeonPlus / 插件实装为准） |
|----------|------------------------------------------|
| 开主菜单 | `trmenu open ember_hub` |
| 进挂机 | `tp` / `mvtp` 到 `afk_lobby`；或 `warp afk` |
| 开日本 | `dp join EmberDaily`（示意） |
| 开周本 | `dp join EmberWeekly`（示意） |
| 查次数 | DungeonPlus / PAPI 占位符；菜单 lore 刷新 |

**插件岗注意：** TrMenu 只做入口与文案；次数裁定与实例生命周期归 DungeonPlus。菜单禁止直接 `/ni give` 当奖励（奖励只走副本结算与 MM 死亡触发）。

### 3.4 TrMenu YAML 草案（示意）

路径示意：`plugins/TrMenu/menus/ember_hub.yml`（安装后按 TrMenu 版本目录调整）。

```yaml
# 示意 — 余烬主入口
Title: '§6余烬 · 冒险入口'
Size: 27
Items:
  afk:
    Display: '§a挂机大厅'
    Material: GRASS
    Slot: 11
    Lore:
      - '§7无限次 · 刷碎片与骨尘'
      - '§7服务附魔晶合成与修理'
    Actions:
      - 'teleport: afk_lobby'   # 落地改为真实坐标或 warp
      - 'sound: BLOCK_NOTE_PLING'
  daily:
    Display: '§e日常 · 余烬窟'
    Material: IRON_SWORD
    Slot: 13
    Lore:
      - '§7今日剩余 §f%ember_daily_left%§7/3'
      - '§7通关箱 + 核心碎片'
    Actions:
      - 'open: ember_daily'
  weekly:
    Display: '§c周常 · 深核'
    Material: DIAMOND_SWORD
    Slot: 15
    Lore:
      - '§7本周剩余 §f%ember_weekly_left%§7/1'
      - '§7高难波次 · 稀有装备'
    Actions:
      - 'open: ember_weekly'
  guide:
    Display: '§b养成指引'
    Material: BOOK
    Slot: 22
    Lore:
      - '§7挂机材料 → 附魔晶 → 强化装备'
      - '§7日/周本冲击核心与稀有掉落'
```

子菜单 `ember_daily` / `ember_weekly`：各放「开始挑战」「奖励预览」「返回」三键即可。

---

## 4. 挂机大厅

### 4.1 定位

- **不是** DungeonPlus 实例：常驻区域，玩家可随时进出。
- 刷怪：MythicMobs 自定义怪（复用 / 轻改 EmberCrypt 小怪），原版刷怪保持关闭。
- 产出服务现有养成：碎片、骨尘为主；核心碎片 **极低概率或不掉**（核心留给日/周本）。

### 4.2 怪表（挂机）

| MM ID | 角色 | 掉落（NI，死亡 `ni give` 单路径） | 备注 |
|-------|------|----------------------------------|------|
| `AfkEmberZombie`（可先直接用 `EmberCryptZombie`） | 主刷 | `mat_ember_shard` ×1 | 密度高 |
| `AfkEmberSkeleton`（或复用 `EmberCryptSkeleton`） | 副刷 | `mat_ember_bone_dust` ×1 | 略少 |
| 蛮兵 | **挂机区不刷** | — | 避免核心通胀 |

本轮可 **零改名复用** EmberCrypt 僵尸/骷髅，仅调整刷怪点与密度；若需区分统计，再由怪物岗拆 `Afk*` ID。

### 4.3 节奏建议

| 项 | 建议值 | 说明 |
|----|--------|------|
| 目标击杀间隔 | 约 3～6 秒/只（单人） | 可挂、不爆肝 |
| 碎片/小时（单人粗算） | 约 40～80 | 对齐「3 碎片+1 骨尘 → 1 晶」 |
| 骨尘相对碎片 | 约 1 : 3 | 与合成配方一致 |
| 装备掉率 | 挂机 ≤1% 或关闭 | 刃/护符留给副本与蛮兵 |

经济衔接（沿用附魔设计）：

- 1 晶 ≈ 3 碎片 + 1 骨尘 ≈ 数分钟挂机。  
- 刃锋利 II ≈ 2 晶 → 鼓励日刷 + 偶发日/周本。

### 4.4 挂机区规则（文案级，插件落地）

- 禁止放置/破坏（若 CoreWorldRules 已有则复用；否则 TrMenu 提示 + 测试岗圈地）。  
- 死亡回 `afk_lobby` 或床点；不扣日/周次数。  
- 不进 DungeonPlus 次数统计。

---

## 5. 日常本 · 余烬窟·日（DungeonPlus）

### 5.1 体验一句话

单人（或小队 1～2）短本：3～4 波小怪 → 1 只蛮兵 → 通关箱。时限约 **8～12 分钟**。

### 5.2 次数与重置

| 项 | 值 |
|----|-----|
| 每日次数 | **3** |
| 重置 | 每天 **00:00 Asia/Shanghai** |
| 失败是否扣次 | **扣**（进本即扣；避免刷进度；可在菜单写明） |
| 中途退出 | 视为失败，已扣次不返还 |
| 通关再进 | 消耗剩余次数 |

### 5.3 流程（DungeonPlus 脚本意图）

1. 玩家菜单确认 → `join EmberDaily` → 独立世界/副本实例。  
2. 出生点短暂无敌（3～5s）。  
3. **波次 1：** 僵尸 ×4～6。  
4. **波次 2：** 僵尸 + 骷髅混合 ×5～8。  
5. **波次 3 · Boss：** `EmberCryptBrute` ×1（可微调血量/技能 CD，不改 Paper）。  
6. Boss 死亡 → 刷通关箱 / 执行结算命令 → 倒计时传送回大厅。

### 5.4 掉落与奖励（接 NI / MM）

**战斗中（MM `onDeath` → `ni give`，与现 EmberCrypt 一致）：**

| 来源 | NI | 数量/概率 |
|------|-----|-----------|
| 日波僵尸 | `mat_ember_shard` | 1 / 击杀 |
| 日波骷髅 | `mat_ember_bone_dust` | 1 / 击杀 |
| 日 Boss 蛮兵 | `mat_ember_core_fragment` | 1 必掉 |
| 日 Boss | `gear_ember_blade` | 12%（略低于演示蛮兵 18%，控通胀） |
| 日 Boss | `gear_ember_charm` | 8% |

**通关箱（DungeonPlus 结算，优先 `ni give`，禁止原版钻石堆）：**

| NI / 内容 | 数量 | 备注 |
|-----------|------|------|
| `mat_ember_core_fragment` | 1 | 通关保底再给 1，合计 Boss+箱 ≈2 核心/通关 |
| `crystal_ember_enchant` | 1 | 直接给晶，缩短养成等待 |
| `mat_ember_shard` | 4～6 | 补挂机差 |

**不要在通关箱再高概率出刃/护符**（避免日刷 3 次挤爆装备）；装备以 Boss 低概率 + 周本为主。

### 5.5 DungeonPlus 草案字段（示意）

```yaml
# 示意 EmberDaily — 以 DungeonPlus 实装 schema 为准
name: EmberDaily
display: '余烬窟·日'
maxPlayers: 2
duration: 720        # 秒，超时失败
startActions:
  - 'message: §e余烬窟·日 开始！今日剩余次数已扣除。'
waves:
  - mobs: [EmberCryptZombie, EmberCryptZombie, EmberCryptZombie, EmberCryptZombie]
  - mobs: [EmberCryptZombie, EmberCryptSkeleton, EmberCryptZombie, EmberCryptSkeleton]
  - mobs: [EmberCryptBrute]
clearActions:
  - 'command: ni give {player} mat_ember_core_fragment 1'
  - 'command: ni give {player} crystal_ember_enchant 1'
  - 'command: ni give {player} mat_ember_shard 5'
  - 'message: §a通关！奖励已发放。'
  - 'tp: hub'        # 占位
failActions:
  - 'message: §c挑战失败。'
  - 'tp: hub'
# 次数：dailyLimit: 3，timezone: Asia/Shanghai — 按插件支持项填写
```

---

## 6. 周常本 · 深核·周（DungeonPlus）

### 6.1 体验一句话

更高难、更长：多波 + 强化蛮兵（或蛮兵双生）→ 通关大箱。时限约 **20～25 分钟**。每周一次，作为核心与稀有装备主来源。

### 6.2 次数与重置

| 项 | 值 |
|----|-----|
| 每周次数 | **1** |
| 重置 | **周一 00:00 Asia/Shanghai** |
| 失败扣次 | **扣**（与日本一致，菜单写清） |
| 建议人数 | 1～3（`maxPlayers: 3`） |

### 6.3 流程意图

1. 波次 1～2：小怪加压（数量高于日本）。  
2. 波次 3：蛮兵 ×1（可称「深核守卫」，MM 可新建 `EmberDeepBrute` 或复用 Brute 加技能次数）。  
3. 波次 4（可选）：小怪清理波。  
4. 终局：`EmberDeepBrute` 或第二只 Brute → 通关箱。

怪物岗可选：新建 `EmberDeepBrute`（更高血量、Slam 更频），掉落仍走 `ni give`；若赶工则两只 `EmberCryptBrute` 串联。

### 6.4 奖励

**Boss 战斗掉落：**

| NI | 规则 |
|----|------|
| `mat_ember_core_fragment` | 每只终局蛮兵必掉 1～2 |
| `gear_ember_blade` | 28% |
| `gear_ember_charm` | 20% |

**通关箱（周）：**

| NI | 数量 |
|----|------|
| `mat_ember_core_fragment` | 2～3 |
| `crystal_ember_enchant` | 2 |
| `mat_ember_shard` | 8 |
| `mat_ember_bone_dust` | 4 |
| `gear_ember_blade` 或 `gear_ember_charm` | 二选一保底（可用两条 `ni give` + 随机脚本；若 DungeonPlus 无随机，则固定刃，下期再随机） |

**周本经济锚：** 1 次满通关 ≈ 4～6 核心 + 2 晶 + 可观材料 + 高概率/保底一件装备，明显优于 3 次日本，但不取代挂机日常晶合成。

### 6.5 示意脚本头

```yaml
name: EmberWeekly
display: '深核·周'
maxPlayers: 3
duration: 1500
# weeklyLimit: 1 ，周一 00:00 Asia/Shanghai 重置
```

---

## 7. 次数、权限与防刷

| 规则 | 说明 |
|------|------|
| 时区 | 一律 **Asia/Shanghai** |
| 进本扣次 | 日/周均进本即扣；菜单二次确认 |
| 多开 | 以 DungeonPlus 玩家 UUID 计数；测试服 online-mode=false 时仍按 UUID |
| 管理员 | `dp` / TrMenu 绕过权限仅 OP；验收用 RpgBot 走正常扣次 |
| 奖励唯一路径 | MM 死亡 `ni give` + 副本 `clearActions` 的 `ni give`；禁止菜单直接发核心 |
| 与挂机隔离 | 挂机区不进次数；副本实例内不刷挂机点怪 |

占位符（插件岗接 PAPI 或 DungeonPlus 自带）：

- `%ember_daily_left%` / `%ember_weekly_left%`  
- 若短期无 PAPI 扩展：菜单写死「每日 3 次 / 每周 1 次」+ 进本失败时插件消息提示剩余次数。

---

## 8. 与现有养成闭环的接口

| 产出 | 去处（已有） |
|------|----------------|
| `mat_ember_shard` / `mat_ember_bone_dust` | CoreCraft → `crystal_ember_enchant`；铁砧小修 |
| `mat_ember_core_fragment` | 铁砧大修；日/周本尖峰 |
| `crystal_ember_enchant` | CoreEnchant 触媒（刃/护符白名单） |
| `gear_ember_blade` / `gear_ember_charm` | 附魔台白名单装备 |

挂机保材料水位；日本补核心+晶；周本爆发装备与核心。AttributePlus 仍停放，装备 lore 可保留属性行，本轮不验收 AP。

---

## 9. 分工

| 岗 | 职责 |
|----|------|
| **余烬-策划（本文）** | 循环、次数、菜单文案、奖励节奏、验收标准 |
| **余烬-插件** | 安装/配置 TrMenu、DungeonPlus；菜单 YAML；进本命令；次数与时区；结算 `ni give`；热重载说明 |
| **余烬-物品** | 通关箱若需新 NI（本轮默认复用现有 ID，**可不新增**）；若要「日/周通关凭证」再开 ID |
| **余烬-怪物** | 挂机刷怪点；日/周波次用怪；可选 `EmberDeepBrute`；掉落保持 `ni give` 单路径；**不改** Paper |
| **余烬-Paper** | **默认不动** |
| **余烬-测试** | 起服装齐插件后：菜单跳转、扣次、通关发奖、失败扣次、挂机不扣次、与附魔修理回归抽测 |

总控（test）继续装 TrMenu / DungeonPlus；策划不指定具体下载源，以总控落地版本为准。

---

## 10. 落地顺序（建议）

1. TrMenu 主菜单 + 挂机传送（可先 tp 到现有 EmberCrypt 测试区当挂机）。  
2. DungeonPlus 跑通空本 join/leave + 回大厅。  
3. 挂载日波次三怪 + 通关 `ni give`。  
4. 日次数 3 与 0:00 重置。  
5. 周本脚本 + 周 1 次。  
6. 菜单子页「开始 / 预览 / 返回」接真实命令。  
7. 测试岗按第 11 节验收；通过后写 `docs/status/STATUS-dungeon-daily-weekly.md`。

---

## 11. 验收（PASS 条件）

1. `/ember` 或约定命令打开主菜单；三项入口文案正确。  
2. 挂机大厅可进；击杀僵尸/骷髅仅 NI 材料；**不扣**日/周次数。  
3. 日本：进本后剩余次数 -1；通关获得箱内 NI（核心+晶+碎片）；失败也 -1。  
4. 日次数用尽后菜单拒绝进本并提示。  
5. 周本：每周仅 1 次；通关箱含核心与晶；装备掉落或保底符合第 6.4 节。  
6. 副本内怪死亡掉落仍为 `ni give` 单路径；无原版垃圾装备炸屏。  
7. 附魔/修理/合成抽测仍 PASS（不回归破坏 `STATUS-rpg-loop`）。  
8. 全程无改 Paper/NMS；无新核心钩子。

---

## 12. YAML / 配置清单（落地文件预期）

| 文件（示意） | 负责岗 |
|--------------|--------|
| `plugins/TrMenu/menus/ember_hub.yml` 及 daily/weekly 子菜单 | 插件（文案按本文） |
| DungeonPlus 日/周脚本（`EmberDaily` / `EmberWeekly`） | 插件 + 怪物（波次怪 ID） |
| MM 刷怪点 / 可选 `EmberDeepBrute` | 怪物 |
| 现有 `EmberCrypt.yml` 掉落 | 默认复用；改概率时怪物岗改 |
| 本文 `docs/design/DESIGN-dungeon-daily-weekly.md` | 策划 |

---

## 13. 下一轮（不在本文）

- 通关评分（时间/死亡次数）与额外箱。  
- 日/周本专属 NI（凭证、称号道具）。  
- AttributePlus 恢复后的属性成长与副本词缀。  
- 组队匹配与进度共享规则细化。  
- 挂机区独立世界 + 反挂机脚本（若出现工作室刷法）。

---

## 14. 一句话结论

**挂机无限供养成材料；日 3 次短本补核心与晶；周 1 次长本爆发核心与装备。TrMenu 只做入口，DungeonPlus 管实例与次数，奖励全部走 NI（MM 死亡 + 通关结算），不碰 Paper。**
