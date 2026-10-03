# 阶段 4.5 · 主线第二卷 ch7～10 YAML 草案 + 新 event 约定

**日期：** 2026-09-27（Asia/Shanghai）  
**作者岗：** 余烬-策划  
**状态：** 已落地 / **PASS（阶段 4.5）**（插件实现与测试验收通过）  
**承接：** `docs/design/design-stage4-mainline-vol2.md` §3.1；现网 `quest.yml` ch1～6；`docs/ember-mainline-spec.md`  
**依赖：** 4.1 深渊可到 9+；4.2 天赋二层可点；4.4 `EmberEliteWeekly` 可通关  
**给谁：** **余烬-插件**（QuestService 扩 event + 合并 YAML）；测试扩 mainline-smoke

**玩家体验（修订）：** 所有 `step.hint` 统一「打开 /ember（或枢纽菜单）→ …」；**禁止**在 hint 写 `/corerpg`、`/dp`、`/dungeon`、`/hub`（见主稿 §0.1 与 §4 菜单补丁）。

---

## 1. 新 event 约定（先于 YAML 落地）

现有 event（保持）：`sign` · `bounty` · `daily_clear` · `weekly_clear` · `abyss_clear` · `raid_clear` · `guild_boss_clear` · `enchant` · `anvil` · `craft` · `calamity_join` · `calamity_boss` · `covenant` · `talent` · `enhance`

### 1.1 新增

| event | YAML 字段 | 完成条件 | 谁触发 |
|-------|-----------|----------|--------|
| `abyss_floor` | `floor: N`（必填） | 玩家**历史最高**深渊层 ≥ N（读 `recordAbyssFloor` / 同等字段） | 进步时 state 检查；或 settle/progress 后刷新 |
| `elite_weekly_clear` | （无额外字段） | 本周已通关 `EmberEliteWeekly` ≥1 | DP reward：`corerpg progress <p> elite_weekly` **且** `corerpg quest event <p> elite_weekly_clear`（或 progress 内兼发 quest） |
| `forge` | （无） | 生涯成功锻造 ≥1（T1→T2 或 T2→T3 皆可） | ForgeService 成功回调；已锻过的进步时 state 完成 |

### 1.2 语义细则

- **state 型**（与 sign/talent 同类）：步骤开始时若已满足，立即完成（防卡老玩家）。  
- `abyss_floor`：**历史最高**，不是「本局刚打到」；主线不要求同一局连打。  
- `abyss_clear` 仍表示「任意一次深渊结算」；与 `abyss_floor` 不同。  
- `talent`：已有；ch9 表示「曾花费 ≥1 天赋点」。若需「点过二层节点」，本期**不要求**（二层可能尚未点满）。  
- 多选：`event: raid_clear|calamity_boss`（1.11.0 已支持 `a|b`）。

### 1.3 管理 / 测试命令（不对玩家展示）

- 现有：`/corerpg quest set|reset|event|npc`  
- 测试：`quest event <p> abyss_floor` 不够（缺层数）→ 建议 `quest event <p> abyss_floor <N>` 或直接改玩家最高层字段。  
- `quest set <p> 7 0` 起第二卷。  
- 玩家查进度：`/ember` → 主线（或已有主线按钮），**不**要求打 `/corerpg quest`。

---

## 2. 卷衔接

- ch6 最后 talk 已有：「灰烬之下还有更深的东西……下回再说。」  
- **ch6 完成后自动开 ch7**（与现卷内章间衔一致：`auto_start` / 章末进下一章）。  
- 卷标题展示：侧边栏可用 `第二卷 · <章名>`（插件可选；最低要求章名正确）。

**定额 XP 合计：** 900+1100+1200+1400 = **4600**（与总稿一致）。

---

## 3. YAML 草案（粘贴到 `chapters:` 下；仅文档）

```yaml
  # ========== 第二卷「烬火未灭」==========
  7:
    name: 烬原之下
    intro:
      - '§6灰烛：§f第一卷的火，停在团本门口。底下还有四层挂机，你还没真正站过烬原深处。'
      - '§6灰烛：§f去 ④ 层走走，再回工坊锻一次——火要会用，才算你的。'
    steps:
      - type: talk
        desc: 向 灰烛 打听更深的烬原
        hint: 枢纽出生点北边、工坊旁找灰烛（右键交谈）
        xp: 80
        done:
          - '§6灰烛：§fLv40 才能在 ④ 层站稳。材料日顶四层共用，别贪。'
      - type: kill
        mobs: ['EmberAfk4*']
        count: 12
        desc: 在挂机庭 ④ 烬原深处击败 12 只怪
        hint: 打开 /ember（或枢纽菜单）→ 挂机庭 → ④ 烬原深处（需 Lv40）；危险就回枢纽找灰烛
        xp: 200
        done: ['§7深处的灰是热的。']
      - type: event
        event: forge
        count: 1
        desc: 在余烬锻炉完成一次锻造
        hint: 打开 /ember（或枢纽菜单）→ 余烬锻炉
        xp: 220
        done: ['§6灰烛：§f刃认新主了。']
      - type: level
        level: 40
        desc: 达到余烬等级 Lv.40
        hint: 打开 /ember → 日常本 / 深渊 / 挂机庭 / 悬赏
        xp: 400
        levels: 3
        items: { mat_ember_stable_charm: 1, mat_ember_shard: 20 }
        done:
          - '§6灰烛：§f稳定符收好。接下来的井，比挂机庭深得多。'
          - '§a[深渊] 可以试着下潜到第 9 层以外了。'

  8:
    name: 更深的井
    intro:
      - '§6灰烛：§f深渊以前停在第 8 层。现在井又开了——第 9 层起，看守换了脾气。'
    steps:
      - type: talk
        desc: 听 灰烛 说深渊加层
        hint: 枢纽工坊旁找灰烛（右键交谈）
        xp: 80
        done: ['§6灰烛：§f票还是一天一张。能走多深，就走多深。']
      - type: event
        event: abyss_clear
        count: 1
        desc: 完成一次深渊结算（任意层）
        hint: 打开 /ember（或枢纽菜单）→ 深渊 → 开始下潜
        xp: 200
        done: ['§7井壁在往下退。']
      - type: event
        event: abyss_floor
        floor: 9
        count: 1
        desc: 深渊历史最高层达到第 9 层
        hint: 打开 /ember → 深渊，继续下潜（可多日累计到第 9 层）
        xp: 400
        done: ['§6灰烛：§f第 9 层的蛮压，你顶住了。']
      - type: event
        event: daily_clear
        count: 2
        desc: 通关 余烬窟·日 两次
        hint: 打开 /ember → 日常本（每日免费日票）
        xp: 200
        done: ['§7日常仍是火种。']
      - type: level
        level: 45
        desc: 达到余烬等级 Lv.45
        hint: 打开 /ember → 深渊 / 日常本 / 灾厄 / 挂机庭
        xp: 220
        levels: 3
        items: { gem_ember_steady: 1, mat_ember_core_fragment: 5 }
        done:
          - '§6灰烛：§f稳石给你。天赋若已点满一层，去看看第二层的脉。'

  9:
    name: 第二层誓火
    intro:
      - '§6灰烛：§f三十级以后，誓火还有一层。点之前，先去精英试炼证明你还站得住。'
    steps:
      - type: talk
        desc: 向 灰烛 打听天赋二层与精英试炼
        hint: 枢纽工坊旁找灰烛（右键交谈）
        xp: 80
        done: ['§6灰烛：§f试炼每周一次。稳定符在周首通里。']
      - type: event
        event: talent
        count: 1
        desc: 花费至少 1 点天赋（含二层）
        hint: 打开 /ember（或枢纽菜单）→ 天赋（需已选誓约）
        xp: 200
        done: ['§7脉里又多了一点热。']
      - type: event
        event: elite_weekly_clear
        count: 1
        desc: 通关 余烬·精英试炼 一次
        hint: 打开 /ember（或枢纽菜单）→ 精英试炼（每周 1 次）
        xp: 500
        done: ['§6灰烛：§f词缀怪比普通周本难缠。你过了。']
      - type: level
        level: 50
        desc: 达到余烬等级 Lv.50
        hint: 打开 /ember → 精英试炼 / 深渊 / 日常本
        xp: 420
        levels: 3
        items: { core_ember_compact: 1, mat_ember_talent_reset: 1 }
        done:
          - '§6灰烛：§f凝核与洗点券——一攻一守。'
          - '§6灰烛：§f最后一章，不必一个人走完。'

  10:
    name: 未灭之火
    intro:
      - '§6灰烛：§f满级不是熄火。是学会让火一直亮着。'
    steps:
      - type: talk
        desc: 听 灰烛 说第二卷的结尾
        hint: 枢纽工坊旁找灰烛（右键交谈）
        xp: 100
        done: ['§6灰烛：§f团本或灾厄，选你熟悉的那扇门。']
      - type: event
        event: raid_clear|calamity_boss
        count: 1
        desc: 通关团本 或 参与击退灾厄使
        hint: 打开 /ember（或枢纽菜单）→ 团本 或 灾厄
        xp: 400
        done: ['§7同袍或祭坛——火都还在。']
      - type: event
        event: abyss_floor|elite_weekly_clear
        floor: 10
        count: 1
        desc: 深渊历史最高达到第 10 层 或 再通关精英试炼
        hint: 打开 /ember → 深渊（冲到第 10 层）或 → 精英试炼（本周未打过时）
        xp: 400
        done: ['§7井与试炼，你都碰过了。']
      - type: talk
        desc: 回到 灰烛 身边
        hint: 枢纽工坊旁找灰烛（右键交谈）
        xp: 200
        done: ['§6灰烛：§f把火交到你手上的时候，我以为会灭。']
      - type: level
        level: 60
        desc: 达到余烬等级 Lv.60（第二卷终）
        hint: 打开 /ember → 日常本 / 深渊 / 精英试炼 / 团本 / 灾厄
        xp: 300
        levels: 5
        items: { core_ember_compact: 1, mat_ember_stable_charm: 1, cosmetic_calamity_shard: 1 }
        done:
          - '§6灰烛：§f第二卷，到此为止。外观碎片先收着——以后有地方花。'
          - '§a[第二卷完成] 烬火未灭。日常、深渊与试炼仍在。'
```

### 3.1 `abyss_floor|elite_weekly_clear` 说明

多选时：若选 `abyss_floor` 分支，读同一步的 `floor: 10`；若选 `elite_weekly_clear` 则忽略 floor。插件需支持「复合 event + 共享附加字段」。若实现成本高，**可拆成两步二选一**（总控拍板）；草案优先省步数。

---

## 4. 菜单文案补丁（TrMenu · 玩家唯一入口）

**原则：** `step.hint` 与菜单 lore **禁止**出现 `/corerpg`、`/dp`、`/dungeon`、`/hub`。底层可以 `command: corerpg …`，玩家只看见按钮。

| 位置 | 现状 | 需求 |
|------|------|------|
| `/ember` → 挂机庭 → ④ | `ember_afk.yml` **已有**④ 按钮（底层 `corerpg afk 4`） | **保持点击进层**；勿改回让玩家打指令。lore 可写「需 Lv40」 |
| `/ember` → 余烬锻炉 | `ember_forge.yml` **已有**；hub lore 仍写「命令：/corerpg forge」 | **删掉玩家可见的命令行 lore**；只留「打开锻炉」按钮 |
| `/ember` → 深渊 | `ember_abyss.yml` 已有「开始下潜」 | lore 加「可下潜至第 12 层 · 第12层周首通稳定符」；撤离用菜单按钮 |
| `/ember` → **精英试炼** | **尚无**独立项 | **新增** hub 按钮（建议放周常旁）：一点进本；底层 DP，玩家不可见指令 |
| 主线按钮 | 已有 | lore：进行中 ch≥7 时 `§7第二卷 · %章名%` |

---

## 5. 物品依赖（均应已存在）

| NI ID | 用途 |
|-------|------|
| `mat_ember_stable_charm` | ch7 / ch10 |
| `mat_ember_shard` | ch7 |
| `gem_ember_steady` | ch8 |
| `mat_ember_core_fragment` | ch8 |
| `core_ember_compact` | ch9 / ch10 |
| `mat_ember_talent_reset` | ch9（若无此 ID，改发晶钻说明或删奖，**落地前物品岗确认**） |
| `cosmetic_calamity_shard` | ch10 预埋 |

---

## 6. 验收

1. [ ] ch6 完成后进入 ch7；侧边栏/命令显示正确。  
2. [ ] `forge` / `abyss_floor` / `elite_weekly_clear` state 与触发均通。  
3. [ ] 未达 level 门不跳步；达标一次完成并发奖。  
4. [ ] 主线 XP 不占击杀日顶。  
5. [ ] ch10+Lv60 有第二卷完成提示。  
6. [ ] `mainline-smoke` 扩到 ch10（可用 quest set + event 加速非战斗步）。

---

## 7. 专岗

| 岗 | 任务 |
|----|------|
| 插件 | 三 event + YAML 合并 + 复合 event 策略 |
| 物品 | 确认 `mat_ember_talent_reset` |
| 测试 | 扩 smoke；卡关回归 |
| 策划 | 本稿；与 4.1/4.4 文案对齐 |

