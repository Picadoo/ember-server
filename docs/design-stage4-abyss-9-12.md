# 阶段 4.1 · 深渊 9～12 层（N1）— 波次 / 看守 / 层奖

**日期：** 2026-09-27（Asia/Shanghai）  
**作者岗：** 余烬-策划  
**状态：** 仅文档（不改线上 YAML / jar）  
**批准：** 总控全量 I+J+N；落地顺序 §5 → **先做本文件**  
**承接：** `design-stage4-mainline-vol2.md` §3.3.1；现网 `DungeonPlus/dungeon/EmberAbyss/monster.yml`（floor1～8）；`MythicMobs/Mobs/EmberAbyss.yml`；`docs/ember-abyss-calamity.md` §1.4；`docs/ember-abyss-evacuate-spec.md`  
**给谁：** **余烬-怪物**（数值/Display）+ **余烬-插件**（DP 层组、progress、settle、周首通）

**玩家体验：** 进本/撤离走 `/ember` → 深渊菜单按钮；聊天指令仅管理/测试（主稿 §0.1）。

---

## 0. 目标

把现网「第 8 层 = 本期顶层 COMPLETE」改为 **可下潜到第 12 层**；第 12 层通关才 COMPLETE。  
主线 ch8 的 `abyss_floor: 9` 依赖本条可达成。

**不改：** 日票 1 张、撤离/超时 settle 语义、1～8 层刷怪点坐标与 kill Display（除非怪物岗统一改名）。  
**可改：** floor8 的 `$end COMPLETE` → 续 floor9；新增 floor9～12；settle 档与周首通稳定符。

---

## 1. 现网基准（勿回滚）

| MM ID | Display（kill） | Health / Damage（1.13.0） |
|-------|-----------------|---------------------------|
| `EmberAbyssZombie` | 余烬深渊·潮尸 | 110 / 5 |
| `EmberAbyssSkeleton` | 余烬深渊·骨潮 | 80 / 4 |
| `EmberAbyssMix` | 余烬深渊·混潮 | 130 / 5 |
| `EmberAbyssBrute` | 余烬深渊·蛮层 | 450 / 4 |
| `EmberAbyssWatcher` | 余烬深渊·看守 | **1500 / 3** |

实测：单人烬刃冲第 8 层看守约 35s，剩生命约 38%（守墓可偏高）。  
坐标：开放室 `x -43..-37, z 267..273`；刷点沿用 `location=-41,65,273` 等（见现 monster.yml 头注）。

清层 end 模式（每层照抄）：

1. `effect … instant_health 1 2`（波末奶）  
2. `corerpg abyss progress %player_name% N`  
3. 通过文案 + 撤离提示  
4. `$monstergroup{group=floorN+1…}` 或顶层 `$end … COMPLETE`

波末提示里的撤离：**改为** `§8需要撤离：打开菜单 → 深渊 → 撤离`（或行动栏短提示），与现网若仍写 `/corerpg abyss evacuate` 的层一并改掉（插件 4.1 顺手）。

---

## 2. 层循环与 9～12 设计

层类型（`N % 5`）：1 灰烬潮 / 2 骨鸣 / 3 混响 / 4 蛮压 / 0 看守。

| 层 | mod | 标签 | 刷怪（意图） | kill 条件 | 开场文案 |
|----|-----|------|--------------|-----------|----------|
| **9** | 4 | 蛮压·深 | `EmberAbyssBrute`×1 + `EmberAbyssZombie`×2 | 蛮层×1（小怪可随通关抹，条件只卡蛮层；或 kill-any 3——**推荐只卡蛮层×1**，与 floor4 一致） | `§c—— 第 §f9 §5层 —— §7深处蛮压` |
| **10** | 0 | 看守·二 | **`EmberAbyssWatcherDeep`**×1（新）+ `EmberAbyssMix`×1 | 看守·深×1 | `§4—— 第 §f10 §5层 —— §c第二看守！视线更毒` |
| **11** | 1 | 灰烬潮·深 | `EmberAbyssZombie`×4（或 Zombie×3 + Mix×1） | 潮尸×4（若混潮则 kill-any 对齐数量） | `§5—— 第 §f11 §5层 —— §7潮尸加压×4` |
| **12** | 2 | 顶层·合拢 | **`EmberAbyssWatcherDeep`**×1 + `EmberAbyssSkeleton`×2 | 看守·深×1 | `§4—— 第 §f12 §5层 —— §c深渊合拢·最终看守！` |

### 2.1 对 floor8 的改动（插件）

- **删除** floor8 end 里的 `$end … COMPLETE`。  
- floor8 end 末尾改为：`progress 8` → 文案「继续下潜或撤离」→ `$monstergroup{group=floor9;…}`。  
- **floor12** end：`progress 12` → `$end{… reward=true; end-type=COMPLETE}`（本期新顶层）。  
- 顶层 COMPLETE 仍走现有 `option.yml` reward：`settle` +（若有）T2 周首逻辑；**另加**周首通稳定符（见 §4）。

### 2.2 新怪 `EmberAbyssWatcherDeep`（怪物岗）

| 项 | 规格 |
|----|------|
| Display | `余烬深渊·看守·深`（kill 名必须与 DP `$kill` 一致） |
| Type | 与 Watcher 同骨架（1.12.2 材质，勿用 1.13+ 名） |
| Health / Damage（初值） | **2200 / 4**（估；4.3 按 bot 调） |
| 技能 | 复用 `EmberAbyssWatcherGaze`；计时可略加快（如 100 tick，可选） |
| 掉落 | 见 §3；走 `corerpg mmgive` 或现 Watcher 同路径，**世界不要**进 `afk_caps` 名单 |

小怪 9～12 **优先复用**现 ID，靠数量加压；若 4.3 实测 TTK 过短，再给 Deep 小怪变体（本稿不强制）。

### 2.3 平衡目标（验收用）

参照档：**单人 · 烬刃 · T2 刃+6～+8 / T2 符 · 天赋一层（≤20 点）· Lv40+**

| 层 | Boss/压力点 | 目标 TTK | 结束剩余生命（烬刃） |
|----|-------------|----------|----------------------|
| 9 蛮层 | 精英 | 20～40s | 40～70% |
| 10 看守·深 | Boss | **50～80s** | **25～50%** |
| 11 潮尸潮 | 清杂 | 整层 40～70s | 不团灭即可 |
| 12 最终看守 | Boss | **55～90s** | **25～55%** |

守墓可偏高；若烬刃轻易 <30s 清 10/12 → 4.3 抬血或抬伤。  
天赋二层（4.2）上线后 **必须**复测 10/12（阶段稿 §5 的 4.3）。

> **4.3 验收口径：** 复活或即时治疗抬高结束生命%时，若战中 `minHp` 已显示致死压力并伴随复活，结束%不单独判 FAIL；主要看 TTK 入窗与 `minHp`/复活次数。

---

## 3. 战斗中掉落（9～12 增量）

1～8 维持现网 MM 表。9～12 **叠加**下表（或 Deep 看守专用）：

| 来源 | NI | 数量/概率 | 备注 |
|------|-----|-----------|------|
| 层末蛮层（9） | `mat_ember_core_fragment` | 12%（略高于普通精英 8%） | |
| 层末蛮层（9） | 随机孔石 | 8% | 池：sharp/steady/drain/gale |
| 看守·深（10 / 12） | `mat_ember_core_fragment` | **1 必掉** | 与现看守一致 |
| 看守·深 | 随机孔石 | **45%**（高于现 35%） | |
| 看守·深 | `mat_ember_protect_scroll` | 12% | |
| 看守·深（仅 12） | — | — | **稳定符不走战斗掉落**，走周首通（§4），防重复刷 |

---

## 4. 结算箱与周首通

### 4.1 按最高层 settle（对齐 calamity 表，补 9～12 体感）

现档已覆盖：5～9 / 10～14。冲到 9 仍属 5～9；冲到 10～12 属 **10～14**：

| 最高层 | 碎片 | 骨尘 | 核心 | 随机孔石 | 其它 |
|--------|------|------|------|----------|------|
| 1～4 | 8 | 2 | 0 | 0 | — |
| 5～9 | 12 | 4 | 1 | 1 | — |
| **10～14** | 16 | 6 | 2 | 1 | 保护券 15%；T2 刃 4%（若现网已接则保留） |
| 15～19 | 20 | 8 | 3 | 2 | （本期达不到，表保留） |
| 20+ | … | … | … | … | （保留） |

**本期无需新建 9～12 专属 settle 档**；靠战斗掉落 + 周首通拉开与「只打到 8」的差距。

### 4.2 第 12 层周首通（新 · 插件）

| 项 | 规格 |
|----|------|
| 条件 | 本局 `abyss_floor ≥ 12` 且触发 COMPLETE 或 settle 时最高层 ≥12 |
| 奖励 | `mat_ember_stable_charm` ×**1** |
| 频率 | **每玩家每自然周 1 次**（Asia/Shanghai 周历，与周本重置对齐） |
| 提示 | `§a[深渊] 本周首次抵达第 12 层，获得稳定符 ×1` / 已领过则静默 |
| 命令建议 | `corerpg abyss weekly12 <player>` 由 COMPLETE/settle 调用；或并入 settle 查表 |

**不要**给第 10 层也发稳定符（稿约定只 12）。

### 4.3 COMPLETE 与 T2

现网 floor8 COMPLETE 曾绑 T2 刃/符逻辑——加层后：

- **建议：** T2 周首通/概率仍按 settle 档（10～14），**不要**「每打到 12 再送一把 T2」。  
- 若现 option.yml 在 COMPLETE 时**固定**发 T2：改为仅「本周首次 COMPLETE」或并入 settle，避免 12 层每周多一把 T2。具体以现网 `option.yml` 为准，插件岗改前先贴现逻辑给总控一眼。

---

## 5. DP 伪代码（插件落地清单）

```
floor8 end:  progress 8 → 文案「继续下潜」→ monstergroup floor9   # 去掉 COMPLETE
floor9:      Brute+Zombie×2 → progress 9 → floor10
floor10:     WatcherDeep+Mix → progress 10 → floor11
floor11:     Zombie×4 → progress 11 → floor12
floor12:     WatcherDeep+Skeleton×2 → progress 12 → $end COMPLETE
COMPLETE / settle: 既有 settle 箱 + weekly12 稳定符检查
```

菜单 `ember_abyss.yml`：
- lore 补一行：`§7本期可下潜至第 §f12 §7层 · 第12层周首通稳定符`
- **撤离**必须是菜单按钮（一点结算离本）；波末提示写「打开深渊菜单可撤离」，**不要**要求玩家打 `/corerpg abyss evacuate`
- 「开始下潜」保持一点进本；文案不出现 `/dp start`

主线 event：`abyss_floor` 读历史或本周最高层 ≥ N（见 quest 草案文）。

---

## 6. 专岗拆分

| 岗 | 任务 |
|----|------|
| **怪物** | 新建 `EmberAbyssWatcherDeep`（Display/血伤/技能）；确认 9～12 复用小怪 Display 与 `$kill` 一致 |
| **插件** | monster.yml floor8 续层 + floor9～12；COMPLETE 顶层改 12；`weekly12` 稳定符；菜单 lore |
| **物品** | 确认 `mat_ember_stable_charm` 已存在（应已有）；无需新票 |
| **测试** | 单人冲 12：progress 1→12；撤离在 9 settle 档正确；周首通稳定符只 1 次；回归 1～8 |
| **策划** | 本稿；主线 ch8 依赖本条可打到 9 |

---

## 7. 验收勾选

1. [ ] 无票不能进；有票扣 1；1～8 行为与改前一致（除 8 不再完结）。  
2. [ ] 可连续打到 12；`progress` 到 12；COMPLETE 只在 12。  
3. [ ] 中途撤离：最高层 9 → 5～9 档箱；最高层 11 → 10～14 档箱。  
4. [ ] 本周首次 12：稳定符 ×1；同周再打 12：不再发。  
5. [ ] 单人烬刃 T2+6～+8：第 10/12 层 TTK / 剩余生命落在 §2.3；不进 afk 日顶。  
6. [ ] Display / `$kill` 名称一致，无卡波。

---

## 8. 风险

| 风险 | 缓解 |
|------|------|
| 8→9 续层后老玩家「顶层习惯」 | 菜单+开场提示「可继续下潜至 12」 |
| WatcherDeep 过强/过弱 | 初值 2200/4；4.3 bot 调 |
| 稳定符过多 | 仅周首×1；战斗掉落不给符 |
| COMPLETE 仍发 T2 导致通胀 | 插件改前核对 option.yml |

---

## 旁注 · 怪物岗落地（2026-09-27）

- 已新增 `EmberAbyssWatcherDeep` → `plugins/MythicMobs/Mobs/EmberAbyss.yml`
- Display：`&5余烬深渊·看守·深`（kill 明文 **余烬深渊·看守·深**）
- 2200/4；Gaze `~onTimer:100`；掉落按 §3（无稳定符）
- 详见 `STATUS-ember-abyss-watcher-deep.md`
