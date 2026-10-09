# 余烬 · 日更短本第三拍扩线（预警环伤 · 庭院/焦骨/潮蚀/断塔）

STATUS=**已批 A · 批 M · D374 · 施工交怪物岗** · 2026-10-10 · tip [`STATUS-ember-next-hard-debt-daily-short-wave3-telegraph-need-design-2026-10-10.md`](../status/STATUS-ember-next-hard-debt-daily-short-wave3-telegraph-need-design-2026-10-10.md)（已关）· backlog `B-ember-daily-short-wave3-telegraph` · STATUS [`STATUS-ember-daily-short-wave3-telegraph-d374-2026-10-10.md`](../status/STATUS-ember-daily-short-wave3-telegraph-d374-2026-10-10.md) · 总控排队内容真债 2（使魔 `1d938a07` 之后）

> **一句话玩家价值：** 日刷终厅从「站着挨无预警环」变成「看提示→拉开可躲」的可读战斗节奏——四线重刷更有趣，不靠抬体力奖励。

---

## 0. 证据表

| # | 来源 | 口径 |
|---|------|------|
| E1 | `EmberDungeonDiffSkills.yml` `EmberFrostNovaCast` | 范式：`message` → 粒子 → `delay 25` → `damage 1.2` `@r=5` + SLOW；CD8 |
| E2 | 同文件 `EmberRailSlamCast` | 同构工业风（**无** SLOW）；锈轨 PASS 测报五条全过 |
| E3 | `EmberDailyFrost.yml` / `EmberDailyRail.yml` | Boss 挂 `skill{s=…Cast} @self ~onTimer:80`；已删无预警 onTimer 环 |
| E4 | `EmberDaily.yml` `EmberDailyBrute` | `damage 0.5 r6 ~onTimer:40` · **无预警** |
| E5 | `EmberDailyAsh.yml` `EmberAshBrute` | `damage 0.5 r5 ~onTimer:40` · **无预警** |
| E6 | `EmberDailyTide.yml` `EmberDailyTideBrute` | `damage 0.4 r5 ~onTimer:40` + SLOW `~onTimer:50` · **无读条绑伤** |
| E7 | `EmberDailySpire.yml` `EmberDailySpireWarden` | `damage 0.45 r5 ~onTimer:45`；逼近 message `~onTimer:180` **≠** 读条预警 |
| E8 | `EmberDailyCrypt.yml` `EmberDailyCryptWarden` | 同族无预警环（`damage 0.4 r6 ~onTimer:50`）· **本窗后置** |
| E9 | `design-ember-daily-third-beat.md` / `design-ember-daily-rail-third-beat.md` | 第三拍铁栅：仅 MM；禁体力/掉落/DP/prep/房2；霜→锈复制窗已走完 |
| E10 | `STATUS-ember-daily-third-beat-test.md` / `…-rail-third-beat-test.md` | 两线 live PASS：early 无该次伤 · 圈内 1.2 · 拉开 drop=0 · DP 零 diff |
| E11 | `ember_daily.yml` | TrMenu 七线点选进本；玩家不打命令 |
| E12 | Stage2 / 硬禁 | 观察续；禁样本 R / Pack6 / 天赋灰印 / 关观察 / 改 ×0.97 / 开 K3 / 抬体力掉落 |

**一句话问题：** 七线里只有霜/锈有可读第三拍；庭院·焦骨·潮蚀·断塔终厅仍是亲戚铁栅 + 无预警环伤。

---

## 1. 方案表

| 方案 | 内容 | 评价 |
|------|------|------|
| **A · 只改 lore / 菜单半行** | 日菜单写「终厅可躲」但 MM 仍无预警环 | 空许愿；**不荐** |
| **M · 范式扩四线（荐）** | 删四线 Boss 无预警 onTimer 环；各新建 1 个主题 `…Cast`（同构 FrostNova/RailSlam）；挂 `~onTimer:80`；逼近 message 可留 | **荐** — 现栈已证；零体力零掉落；趣味节奏真落地 |
| **L · 新系统 / 假平面 / 抬表** | 新限时旁支 API、新图板、抬体力或掉落「补偿节奏」 | **否决** — 硬禁；过厚；与第三拍债无关 |

**批 A = 采纳方案（荐 M）。批 A ≠ 施工 ≠ 关观察 ≠ 改掉落/体力 ≠ 开 R ≠ 开 K3。**

---

## 2. 可落地（方案 M）

### 2.1 范围钉死（本 / Boss / Skill）

| 线（日菜单名） | DP / 世界 | Boss MM ID · Display | 现状环 | 本窗动作 |
|----------------|-----------|----------------------|--------|----------|
| 余烬窟·**庭院** | `EmberDaily` | `EmberDailyBrute` · 灰烬庭院蛮兵 | `dmg 0.5 r6 ~40` | 删环 → 挂 **`EmberYardPulseCast`**（名可微调，须唯一） |
| 余烬窟·**焦骨甬道** | `EmberDailyAsh` | `EmberAshBrute` · 焦核蛮兵 | `dmg 0.5 r5 ~40` | 删环 → 挂 **`EmberAshBurstCast`** |
| 余烬窟·**潮蚀水道** | `EmberDailyTide` | `EmberDailyTideBrute` · 潮闸蛮兵 | `dmg 0.4 r5 ~40` + SLOW `~50` | 删环伤 **及** 独立 SLOW 光环 → 挂 **`EmberTideCrashCast`**（读条结算可带轻 SLOW，对齐霜晶差一档） |
| 余烬窟·**断塔回廊** | `EmberDailySpire` | `EmberDailySpireWarden` · 断塔守望 | `dmg 0.45 r5 ~45` | 删环 → 挂 **`EmberSpireSlamCast`**；**保留**逼近 message（同锈轨） |

**后置（本窗不做）：** 残誓地窖 `EmberDailyCryptWarden` 同构（E8）——四线 PASS 后再开独立扩线号。

**已有、不动：** 霜晶 `EmberFrostNovaCast` · 锈轨 `EmberRailSlamCast`（数字/文案本窗零改）。

### 2.2 预警表现边界（对齐已 PASS 模板）

每条新 Skill **必须**满足：

| 项 | 钉 | 禁 |
|----|----|----|
| 序列 | `message`（短、含「拉开」）→ 蓄力粒子（1.12 有效粒子）→ 可选音效 → **`delay 25`** → `damage` `@PlayersInRadius` → 可选击中粒子 / 轻 SLOW | 无 delay 直接伤；无 message 的静默环 |
| 半径 / 伤 | 荐 **r=5 · amount=1.2**（与霜/锈一致，便于测法复用） | 抬伤到团本级；改 Boss `Health`/`Damage` |
| Timer | Boss 挂 `~onTimer:80`；Skill `Cooldown: 8` | 叠回 `~onTimer:40` 无预警环 |
| 主题差 | 文案色/厅名 + 粒子族不同（庭院余烬 / 焦骨焰 / 潮蚀水沫 / 断塔石屑） | 四线复制同一 message 串；与霜厅撞「霜核」用词 |
| SLOW | 庭院·焦骨·断塔：**默认无 SLOW**（学锈轨）；潮蚀：**允许**结算带 `SLOW duration≤40 level0`（替掉原独立光环） | 全线叠多重减速；定身/击飞 |
| 并存 | SoftHit / 焦骨 Ignite / 断塔逼近 message / mmxp / mmgive **零改** | 借窗改掉落行概率 |

**示意（施工稿骨架 · 非本窗落盘）：**

```yaml
# EmberDungeonDiffSkills.yml（批 A 后施工号落）
EmberYardPulseCast:   # 庭院
  Cooldown: 8
  Skills:
  - message{m="&a【庭院】&f蛮兵蓄力——&7拉开！"} @PlayersInRadius{r=24}
  - effect:particles{p=flame;amount=36;hSpread=4;ySpread=0.8;speed=0} @Self
  - delay 25
  - damage{amount=1.2} @PlayersInRadius{r=5}
# EmberAshBurstCast / EmberTideCrashCast / EmberSpireSlamCast 同构；潮蚀可 + potion SLOW
```

Boss 挂载：删对应 `damage{…} ~onTimer:…`（潮蚀另删 SLOW onTimer）→ 增 `- skill{s=EmberXxxCast} @self ~onTimer:80`。

### 2.3 TrMenu / UX

| 项 | 本窗 M |
|----|--------|
| 进本 | **不变**：枢纽 → 日常 → `ember_daily` 点选（禁教玩家打 `/dp` 等） |
| 文案 | **可选** 四线 lore 半行「终厅读条可躲」（非必做）；**禁**空许愿写未施工机制 |
| NI | 本债**不**新自定义物；既有掉落物不动 |

### 2.4 地图 / DP / 铁栅

| 可动（批 A 后施工） | **本窗与施工均禁** |
|---------------------|-------------------|
| 仅 `EmberDungeonDiffSkills.yml` + 四份 `EmberDaily*.yml` Boss Skills | 七线 `DungeonPlus/.../monster.yml` 房1/房2/`boss_prep`/门坐标 |
| | **体力 30** / 通关箱 / `dungeon-reward-script` / mmgive 掉落行 |
| | Boss HP / Display 去色名 / SoftHit 概率 |
| | MCA 地形、假平面 AFK 板、断塔环廊防坠再改（≠本债） |
| | `$kill-any`、跨组合并 `$kill` |
| | 霜晶/锈轨再加 prep；房2 链式再变体 |

**地图约束重申：** 日更世界已是真实地形/结构；本债**零**新图、**零**假平面。

### 2.5 本窗不做（docs-only 边界）

- **不**施工 MM / DP / TrMenu / Java
- **不**关 Stage2 观察、**不**改 ×0.97、**不**开 K3、**不**开样本 R
- **不**抬体力 / 掉落 / event_rate
- **不**交残誓地窖同构（后置）
- **不** stage 脏 runtime（ladder / calamity-state / p1-six 等）
- **不** Pack6 / 天赋 / 灰印 HOLD 重开

### 2.6 与 Stage2 观察边界

| 项 | 口径 |
|----|------|
| 本窗 | **docs-only 已批 A** · MM 施工交怪物岗另号 |
| 批 A 后 | 另开施工号（怪物岗 MM）；测法复用霜/锈五条（early / 圈内 1.2 / 拉开 miss / DP 零 diff / 禁 kill-any·掉落零改） |
| 观察 | **续**；绿出口仍 ≥**2026-10-10 17:40 CST**；本债**不**勾关窗 |

### 2.7 验收指针（批 A · 施工后 · 非本窗）

每线短抽对齐霜/锈：

1. 提示文案先于该次读条伤（delay 窗 earlyDrop 无该次半径伤）  
2. 圈内结算 ≈1.2（潮蚀若带 SLOW 须静态声明）  
3. 提示后拉开 → 该次 drop=0  
4. DP 相对施工 tip 零 diff；体力 -30；掉落/SoftHit/逼近（若有）零改  
5. 无 `$kill-any` / 无 MCA / 无脏 runtime stage  

---

## 3. 否决摘要

| 否决 | 理由 |
|------|------|
| 改体力 / 掉落「补节奏」 | 硬禁 · 零体力零掉落改 |
| 假平面 / 新图包 | 地图纪律；非第三拍债 |
| 只改菜单不改 MM | 空许愿 |
| 断塔环廊再折腾 | 旧硬条；本债只 Boss 战内 |
| 四线一次叠团本级多招 | 过厚；范式是单读条可躲 |
| 借窗开样本 R / 关观察 / 改 ×0.97 / 开 K3 | 硬禁 |

---

## 4. 回总控一句话

霜/锈第三拍已 PASS；庭院·焦骨·潮蚀·断塔仍无预警环——荐 **M** 同构四条主题读条，零动体力掉落地图；批 A ≠ 施工 ≠ 关观察。

---

## 5. 批注勾选（总控）

- [x] **批 M**（四线同构读条可躲 · YardPulse/AshBurst/TideCrash/SpireSlam）→ **D374 已批 · 施工交怪物岗** · **策划荐 · 总控已批**
- [ ] **批 A**（只改 lore/菜单半行）· **不荐独批**
- [ ] **批 L**（新系统/假平面/抬体力掉落）· **否决**
- [ ] 驳回改派（理由：________）

**策划荐勾：** **批 M**。

### 总控批注（D374）

- **已批 A · 批 M** · 本号 **docs-only** 落字；**MM 施工交怪物岗**（另号）。
- **≠本号改 MM** · **≠改体力/掉落** · **≠关观察** · **≠开 R** · **≠开 K3** · **≠改 ×0.97 / 三开关 / bv** · **≠假平面**。

**说明：** 批 A ≠ 施工 ≠ 关观察 ≠ 改掉落/体力 ≠ 开 R。

---

## 6. 变更记录

| 日 | 事 |
|----|-----|
| 2026-10-10 | 策划 · 初稿 STATUS **待批 A** · 荐 M；tip `a944ed30` |
| 2026-10-10 | 总控批 A·M · **D374** docs 占位 · tip 关 · backlog→已批 A·施工中·怪物岗 · **≠本号改 MM ≠改体力掉落 ≠关观察** |

---

## 7. 参考

- tip · STATUS D374
- `EmberDungeonDiffSkills.yml` · `EmberDaily.yml` / `EmberDailyAsh.yml` / `EmberDailyTide.yml` / `EmberDailySpire.yml`
- 范式：`EmberFrostNovaCast` / `EmberRailSlamCast` · 测报 `*-third-beat-test`
- 入口：`plugins/TrMenu/menus/ember_daily.yml`

---

*已批 A · 批 M · D374 docs 占位 · 施工交怪物岗 · ≠本号改 MM ≠改体力掉落 ≠关观察 ≠开 R。*
