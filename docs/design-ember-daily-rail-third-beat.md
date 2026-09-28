# 设计稿 · 日常第三拍扩线（锈轨 · Boss 读条可躲）

> **未批准前不施工。** 玩法 YAML / MythicMobs **零改**直至总控批 A。  
> 交叉引用：霜晶试点规格与 PASS 见 `docs/design-ember-daily-third-beat.md`、`docs/STATUS-ember-daily-third-beat-test.md`（施工 tip `3e8c969` · 五条全过）。  
> 本稿为 **独立扩线稿**，不代替霜晶 appendix；仅锈轨 `EmberDailyRailWarden` 同构评估。

---

## 0. 稿件信息

| 字段 | 填写 |
|------|------|
| 稿件 | 日常 · 第三拍扩线 · 锈轨 Boss「读条可躲」 |
| 负责人 / 日期 | 余烬-策划执行 / 2026-09-28 Asia/Shanghai |
| 关联 | 霜晶 PASS → 锈轨同构窗；live `EmberDailyRail.yml` + `EmberFrostNovaCast` |
| 状态 | **未批准 · 待总控批 A/B** |
| 推荐 | **方案 A · 锈轨同构 MM 读条**（工业风文案/粒子；仅 MythicMobs） |

### 硬约束

| 可动（若批 A） | 不可动（硬禁） |
|----------------|----------------|
| `EmberDailyRailWarden` 技能挂载 + 新建 **1** 个 MM Skill（读条序列） | 七线 `monster.yml` 房1/房2/`boss_prep`/门；链式 delay |
| Boss 战内提示文案（MM `message`） | **体力 / 通关箱 / 掉落池总量口径**；Boss HP / Display |
| | SoftHit 概率、死亡 `mmxp`/`mmgive` 掉落行 |
| | 房2 / 门 / prep / 他线再叠；周本/精英 |
| | 限时旁支 / 假退路 / 暖点（已否，勿空许愿） |

---

## 1. 霜晶 PASS → 扩线前提

| 项 | 结论 |
|----|------|
| 霜晶方案 A | **已批准并施工** tip `3e8c969`；测报 **PASS**（`STATUS-ember-daily-third-beat-test.md` 五条全过） |
| 模板 | `EmberFrostNovaCast`：`message` → 粒子 → `delay 25` → `damage 1.2` + SLOW `@r=5`；Boss 挂 `~onTimer:80`；CD8 |
| 铁栅 | DP `EmberDailyFrost` **零 diff**；无 prep；体力 -30 / 掉落 / SoftHit 零改 |
| 霜晶稿原定 | 「锈轨同构列为 PASS 后复制窗」——**本稿即该窗** |

**一句话：** 霜晶证明「无预警光环 → MM 读条可躲」可测、可躲、不回归铁栅；锈轨是否同构取决于现状是否同族。

---

## 2. 锈轨现状（live MythicMobs）

来源：`plugins/MythicMobs/Mobs/EmberDailyRail.yml` · `EmberDailyRailWarden`。

| 技能 | 现状 | 可读预警？ |
|------|------|------------|
| `damage{amount=0.5} @PlayersInRadius{r=4} ~onTimer:40` | **无预警** 半径伤，约每 2s | **无**（与霜晶改前同族） |
| `message{…矿监逼近…} ~onTimer:170` | 偶发点名逼近文案 | **非**该次光环读条；与伤害不同 timer |
| SoftHit `~onAttack 0.22` | 近战减速感 | 保留 |
| 死亡 mmxp / mmgive×5 | 掉落口径 | **禁改** |
| Health / Damage | 210 / 3 | **禁抬 HP** |

结构对照：

| | 霜晶 **改前** | 锈轨 **现** | 霜晶 **改后（PASS）** |
|--|---------------|-------------|----------------------|
| 光环 | `damage 0.4 r5` + SLOW `~onTimer:45` | `damage 0.5 r4 ~onTimer:40` | 已删；挂 `EmberFrostNovaCast ~onTimer:80` |
| 预警 | 无 | 无（逼近 message 不绑伤） | message→粒子→delay→伤 |
| 终厅 | 无 prep | 无 prep（刻意） | 同 |

**判断：** 锈轨主伤光环与霜晶改前 **同构**（无预警 onTimer 半径伤）；逼近 message **不足以**充当该次伤害的可读预警（间隔更疏、不绑结算）。Boss 节奏同为无 prep 终厅直战，技能结构仅需「删光环 + 挂 1 Skill」，**无需大改**。→ **倾向 A**。

---

## 3. 方案 A / B

### 3.1 方案 A · 同构 MM 读条（荐）

锈轨矿监将「无预警 r4 环伤」改为 **可读条、可走出半径的工业砸地**：提示 → 蓄力粒子（锈/火花） → 短 delay → 半径伤；站圈吃伤，拉开免伤。  
**不动** 房1/房2/门/`boss_prep`（仍无）/体力/掉落/DP/`SoftHit`/逼近 message（可留作风味点名）/Boss HP。

### 3.2 方案 B · 本轮不扩

仅霜晶试点收口；锈轨维持无预警 `onTimer:40` 光环。第三拍扩线顺延。  
适用若：总控认为双线同构过快、或测力不足、或投诉窗口需观察霜晶 live 更久。

### 3.3 对比

| | **A. 锈轨同构读条** | **B. 本轮不扩** |
|--|---------------------|-----------------|
| 还债 | 无 prep 双线均有「第三拍」战内可感机制 | 锈轨仍亲戚铁栅+无预警环 |
| 改动面 | **仅 MM** 1 Skill + Warden 挂载 | 零 |
| 风险 | 文案/粒子与霜厅撞感（用工业差异化解）；消息略密（读条+偶发逼近） | 扩线债挂着 |
| 禁堆对齐 | 不碰房2/prep/HP/掉落 | 同 |

**推荐：A。**  
**一句话理由（交总控）：** 锈轨 `damage 0.5 r4 ~onTimer:40` 与霜晶改前同构无预警光环，霜晶 PASS 已证模板稳，仅 MM 同构工业风读条即可，零动 DP/HP/掉落/prep。

---

## 4. 若批 A · MM 改动清单 / 文案

### 4.1 文件

| 文件 | 改动 |
|------|------|
| `plugins/MythicMobs/Skills/EmberDungeonDiffSkills.yml`（或同目录新建并入） | 新 Skill **`EmberRailSlamCast`**（名可微调，须唯一） |
| `plugins/MythicMobs/Mobs/EmberDailyRail.yml` · `EmberDailyRailWarden` | **删** `damage … r4 ~onTimer:40`；**增** `skill{s=EmberRailSlamCast} @self ~onTimer:80`；其余 Skills **零改** |

### 4.2 Skill 草案（未批准不落盘）

工业风差异 vs 霜晶：橙褐文案、火花/熔岩粒子、矿车/砧声音；**数值同档**（禁抬伤改难度口径）。

```yaml
# 草案（未批准不落盘）
EmberRailSlamCast:
  Cooldown: 8
  Skills:
  - message{m="&6【锈轨】&f矿监蓄力砸地——&7拉开！"} @PlayersInRadius{r=24}
  - effect:particles{p=lava;amount=36;hSpread=3.5;ySpread=0.8;speed=0} @Self
  - effect:particles{p=crit;amount=20;hSpread=3;ySpread=0.6;speed=0.02} @Self
  - effect:sound{s=minecart.base;volume=0.7;pitch=0.55} @Self
  - delay 25
  - damage{amount=1.2} @PlayersInRadius{r=5}
  - effect:particles{p=flame;amount=28;hSpread=5;ySpread=0.6;speed=0.01} @Self
```

Boss 挂载草案：

```yaml
# EmberDailyRailWarden Skills 中：
# 删：- damage{amount=0.5} @PlayersInRadius{r=4} ~onTimer:40
# 增：
- skill{s=EmberRailSlamCast} @self ~onTimer:80
# 保留：SoftHit、矿监逼近 message ~onTimer:170、全部 ~onDeath 命令
```

| 参数 | 建议初值 | vs 霜晶 / vs 锈轨旧 |
|------|----------|---------------------|
| `delay` | **25** tick | 与霜晶同；日常可反应 |
| 结算半径 | **5** | 与霜晶同档；旧光环 r4→略扩 1 以对齐可躲体感（仍短步可出） |
| 单次伤害 | **1.2** | 与霜晶同档；旧 0.5/2s 持续 → 可躲瞬时；**禁**再抬 |
| `~onTimer` | **80** | 与霜晶同；疏于旧 40 |
| Cooldown | **8** | 同霜晶 |
| 减速 | **本 Skill 不加** | 霜晶有 SLOW 贴冻主题；锈轨工业砸地以伤+火花区分，避免双线体感粘连 |
| 逼近 message | **保留** | 非读条预警；~170 疏于读条，可并存 |

**难度口径：** 旧=站桩持续掉血；新=会躲更轻松、贪刀更疼。期望通关时长与掉落 **不变**。

**粒子 1.12：** 优先 `lava`/`crit`/`flame`（霜晶已证 `cloud`/`crit` 可用；禁依赖未测名）。测岗若 `lava` 过密可降 amount。

### 4.3 明确不施工

- DP `EmberDailyRail/monster.yml` / `option.yml` / 门  
- 加 `boss_prep`、改 wave / SoftHit / HP / 掉落  
- 限时旁支、假退路、暖点、整本失败阀  
- 他线再叠读条（本窗仅锈轨）

---

## 5. 验收 ≤5（批准 A 后测岗）

1. **提示先于伤害：** 「矿监蓄力砸地」出现后，`delay` 窗内贴脸 **尚未** 吃到该次读条伤（允许普攻/SoftHit）。  
2. **圈内吃伤：** 结算时位于 r≈5 内 → 受该次 `damage≈1.2`。  
3. **圈外免伤：** 提示后立刻拉开出半径 → **该次** 读条伤不中。  
4. **铁栅零回归：** 锈轨房2/门/无 `boss_prep`/通关箱/体力扣 30 **与基线一致**（DP YAML 本轮应 **零 diff**）。  
5. **禁项：** 无 `$kill-any`；无 MCA 大改；Boss HP、SoftHit、掉落命令行与 `dungeon-reward-script` **零改**；逼近 message 仍在（若按稿保留）。

---

## 6. 禁项（施工复核）

- **未批准前不施工** 任何玩法 YAML / MM  
- 禁抬 Boss HP；禁改体力 / 掉落池总量口径  
- 禁房2 / 门 / prep；禁动他线；禁周本/精英借机改  
- 禁限时旁支 / 假退路 / 暖点空许愿落地  
- 禁 git push（文档 commit 由本岗处理）

---

## 7. 施工岗（批准 A 后）

| 岗 | 职责 |
|----|------|
| **余烬-怪物（MythicMobs）** | 落 `EmberRailSlamCast`；改 `EmberDailyRailWarden`（删无预警 onTimer 光环、挂读条）；`/mm reload`；核对 1.12 粒子 |
| **插件（DungeonPlus）** | **本 A 无改**；回归只读 `monster.yml` 零 diff |
| **地图** | **本 A 无改** |
| **测岗** | 锈轨进本打 Boss：验收 1～5；对照霜晶测法 |
| **策划 / 总控** | 批注 A/B；PASS 后第三拍扩线可收口 |

---

## 8. 回报摘要（给总控）

| 项 | 内容 |
|----|------|
| 路径 | `docs/design-ember-daily-rail-third-beat.md` |
| 推荐 | **A** · 锈轨 Boss 同构 MM 读条（工业砸地） |
| 一句话 | 锈轨无预警 `onTimer:40` 光环与霜晶改前同构，霜晶 PASS 模板已稳，仅 MM 工业风读条即可，零动 DP/HP/掉落/prep。 |
| 否掉 | 限时旁支、假退路、暖点、prep、抬 HP/改掉落 |
| 状态 | **未批准前不施工** |

---

## 9. 总控批注

（待填）
