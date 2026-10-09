# 余烬 · 日更残誓地窖第三拍预警环（D374 E8 后置升主 · ≠复述四线）

STATUS=**已批 A · 批 M · D379 · 施工交怪物岗** · 2026-10-10 · tip [`STATUS-ember-next-hard-debt-daily-crypt-telegraph-need-design-2026-10-10.md`](../status/STATUS-ember-next-hard-debt-daily-crypt-telegraph-need-design-2026-10-10.md)（已关）· backlog `B-ember-daily-crypt-telegraph` · STATUS [`STATUS-ember-daily-crypt-telegraph-d379-2026-10-10.md`](../status/STATUS-ember-daily-crypt-telegraph-d379-2026-10-10.md) · 总控 D378 后内容真债（D374 E8 后置升主）

> **一句话玩家价值：** 残誓地窖终厅从「站着挨无预警环」变成与其它六线同档「看提示→拉开可躲」——七线日刷节奏齐平，重刷更有趣，不靠抬体力奖励。

> **批 A ≠ 施工 ≠ 关观察 ≠ 抬日表 ≠ 开 R ≠ 开 K3 ≠ 复述 D374 四线主交付。**

---

## 0. 证据表

| # | 来源 | 口径 |
|---|------|------|
| E1 | D374 已落六线 Cast（霜/锈/庭/焦/潮/断）· 抽测 PASS · `EmberDungeonDiffSkills.yml` · spot `ecd16bbc` | **范式 live** |
| E2 | D374 DESIGN §2.1 | 「后置：残誓地窖 `EmberDailyCryptWarden` 同构（E8）——四线 PASS 后再开独立扩线号」 | **本债合法升主** |
| E3 | `EmberDailyCrypt.yml` `EmberDailyCryptWarden` | `damage{amount=0.4} @PlayersInRadius{r=6} ~onTimer:50` · **无** message/delay | **七线唯一静默环** |
| E4 | 同 Boss | 点名 `message` + `potion SLOW` `~onTimer:160`；SoftHit；mmxp/mmgive | **点名保留；掉落零改** |
| E5 | 范式 `EmberFrostNovaCast` / `EmberRailSlamCast` / D374 四 Cast | message→粒子→`delay 25`→`damage 1.2` r5；CD8；Boss `~onTimer:80` | **施工对齐铁栅** |
| E6 | `ember_daily.yml` 残誓格 | 地形 lore；**未**写「可躲」 | 无假平面；半行可选 |
| E7 | Stage2 / 硬禁 | 观察续；禁抬日表 / 样本 R / Pack6 / 天赋灰印 / 关观察 / 改 ×0.97 / 开 K3 | docs-only |

**一句话问题：** 七线日更只剩残誓地窖终厅无可读第三拍——与 D374 后六线落差最大。

**为何选本债、排除其它：**  
- **六线菜单「可躲」半行：** 欠宣传非假平面；可另号。  
- **工坊←挂机：** D375 单向够用。  
- **使魔等级同屏：** 无 pet level PAPI。  
- **本债：** D374 明文后置 + 实扫仍静默环；趣味真缺口；≠复述四线施工。

---

## 1. 玩家感知目标（≤3）

1. **地窖终厅出现短提示（含「拉开」）后再出半径伤——圈内可感、拉开可躲。**  
2. **点名「守墓者点名」节奏仍在（不等于读条环）。**  
3. **体力/掉落/进本路径与今日一致——玩家不感到「又被砍奖励」。**

---

## 2. 方案表

| 方案 | 内容 | 评价 |
|------|------|------|
| **A · 只改 lore / 菜单半行** | 残誓菜单写「终厅可躲」但 MM 仍静默环 | 空许愿；**不荐** |
| **M · 同构一拍 Crypt Cast（荐）** | 删无预警 `~onTimer:50` 环；新建 **`EmberCryptOathCast`**；Boss 挂 `~onTimer:80`；**保留**点名+SLOW `~160`；零体力零掉落 | **荐** — 范式已证；七线齐平；趣味真落地 |
| **L · 新系统 / 假平面 / 抬表** | 新地窖图包、抬体力/掉落「补节奏」、叠团本多招 | **否决** — 硬禁；过厚 |

**批 A = 采纳方案（荐 M）。批 A ≠ 施工 ≠ 关观察 ≠ 改掉落/体力 ≠ 开 R ≠ 开 K3。**

---

## 3. 可落地（方案 M）

### 3.1 范围钉死

| 项 | 本窗动作 | 不动 |
|----|----------|------|
| Boss | `EmberDailyCryptWarden`（显示名残誓守墓） | HP / Damage / Equipment / SoftHit 概率 |
| 删 | `damage{amount=0.4} @PlayersInRadius{r=6} ~onTimer:50` | — |
| 增 Skill | **`EmberCryptOathCast`**（名可微调，须唯一）写入 `EmberDungeonDiffSkills.yml` | 霜/锈/庭/焦/潮/断既有 Cast **零改** |
| 挂载 | `- skill{s=EmberCryptOathCast} @self ~onTimer:80` | — |
| 保留 | 点名 message + SLOW `~onTimer:160`；死亡 mmxp/mmgive | 不把点名改成读条环 |
| 地图 / DP | **零** | `monster.yml` / 房1/房2 / boss_prep / 门坐标 |
| 体力 / 掉落 | **零** | cost 30 · 通关箱 · mmgive 行概率 |

### 3.2 预警表现边界（对齐已 PASS 模板）

| 项 | 钉 | 禁 |
|----|----|-----|
| 序列 | `message`（短、含「拉开」、主题【残誓】）→ 蓄力粒子（1.12 有效：cloud/crit 等）→ 可选音效 → **`delay 25`** → `damage` `@PlayersInRadius` → 可选击中粒子 | 无 delay 直接伤；无 message 静默环 |
| 半径 / 伤 | 荐 **r=5 · amount=1.2**（与六线一致，测法复用） | 抬伤到团本级；改 Boss `Health`/`Damage` |
| Timer | Boss `~onTimer:80`；Skill `Cooldown: 8` | 叠回 `~onTimer:50` 无预警环 |
| SLOW | **默认无**（学锈轨/断塔）；点名自带 SLOW 已保留，Cast **勿**再叠长减速 | 定身/击飞；双 SLOW 叠满 |
| 主题差 | 文案「残誓/守墓」+ 暗色粒子；**禁**抄「霜核」用词 | 与霜厅撞词 |
| 并存 | SoftHit / 点名 / mmxp / mmgive **零改** | 借窗改掉落行 |

**示意（施工稿骨架 · 非本窗落盘）：**

```yaml
# EmberDungeonDiffSkills.yml（批 A 后施工号落）
EmberCryptOathCast:
  Cooldown: 8
  Skills:
  - message{m="&8【残誓】&f守墓蓄力——&7拉开！"} @PlayersInRadius{r=24}
  - effect:particles{p=cloud;amount=36;hSpread=4;ySpread=0.8;speed=0} @Self
  - effect:particles{p=crit;amount=18;hSpread=3;ySpread=0.5;speed=0.02} @Self
  - effect:sound{s=minecart.base;volume=0.6;pitch=0.65} @Self
  - delay 25
  - damage{amount=1.2} @PlayersInRadius{r=5}
  - effect:particles{p=crit;amount=28;hSpread=5;ySpread=0.6;speed=0.02} @Self
```

Boss：删 `damage … ~onTimer:50` → 增 `skill{s=EmberCryptOathCast} @self ~onTimer:80`；点名块不动。

### 3.3 TrMenu / UX

| 项 | 本窗 M |
|----|--------|
| 进本 | **不变**：枢纽 → 日常 → `ember_daily` 点选残誓（禁教 `/dp`） |
| 文案 | **可选** 残誓格 lore 半行「终厅读条可躲」（施工后）；**禁**在批 A 前/未施工时写可躲 |
| 六线菜单半行 | **本窗不做**（另号薄窗）；禁借窗空转扫六线 |
| NI | **不**新自定义物 |

### 3.4 本窗不做（docs-only 边界）

- **不**施工 MM / DP / TrMenu / Java  
- **不**关 Stage2 观察、**不**改 ×0.97、**不**开 K3、**不**开样本 R  
- **不**抬体力 / 掉落 / event_rate / 挂机日表  
- **不**重开 D374 四线数字/文案  
- **不** Pack6 / 天赋 / 灰印 HOLD  
- **不** stage 脏 runtime（ladder / calamity-state / p1-six 等）  
- **不**假平面 / 新地窖图包 / 房2 链式再变体  

### 3.5 与 Stage2 观察边界

| 项 | 口径 |
|----|------|
| 本窗 | **docs-only 已批 A · 批 M · D379** |
| 批 A 后 | **MM 施工交怪物岗**（另号）；测法复用霜/锈/D374 五条 |
| 观察 | **续**；绿出口仍 ≥**2026-10-10 17:40 CST**；本债**不**勾关窗 |

### 3.6 验收指针（批 A · 施工后 · 非本窗）

1. 提示文案先于该次读条伤（delay 窗 earlyDrop 无该次半径伤）  
2. 圈内结算 ≈1.2  
3. 提示后拉开 → 该次 drop=0  
4. 点名 `~160` 仍触发；SoftHit / 掉落 / DP 相对施工 tip 零 diff；体力 -30  
5. 无 `$kill-any` / 无 MCA / 无脏 runtime stage  
6. 六线既有 Cast **零回归**（本号禁改）

---

## 4. 否决摘要

| 否决 | 理由 |
|------|------|
| 只改菜单不改 MM | 空许愿 |
| 改体力 / 掉落「补节奏」 | 硬禁 |
| 假平面 / 新图包 | 地图纪律 |
| 删点名或把点名改成环 | 破坏残誓身份差；点名≠读条 |
| 借窗重扫 D374 四线 / 附录空转 | 硬禁复述 |
| 开样本 R / 关观察 / 改 ×0.97 / 开 K3 / 抬挂机 | 硬禁 |

---

## 5. 回总控一句话

D374 四线已齐；七线只剩残誓地窖静默环——荐 **M** 同构 `EmberCryptOathCast`，保留点名，零动体力掉落；批 A ≠ 施工 ≠ 关观察。

---

## 6. 批注勾选（总控）

- [x] **批 M**（CryptOathCast 同构读条可躲 · 保留点名）→ **D379 已批 · 施工交怪物岗** · **策划荐 · 总控已批**
- [ ] **批 A**（只改 lore/菜单半行）· **不荐独批**
- [ ] **批 L**（新系统/假平面/抬体力掉落）· **否决**
- [ ] 驳回改派（理由：________）

**策划荐勾：** **批 M**。

### 总控批注（D379）

- **已批 A · 批 M** · 本号 **docs-only** 落字；**MM 施工交怪物岗**（另号）。
- **≠本号改 MM** · **≠改体力/掉落** · **≠关观察** · **≠开 R** · **≠开 K3** · **≠改 ×0.97 / 三开关 / bv** · **≠假平面** · **≠复述 D374 四线**。

**说明：** 批 A ≠ 施工 ≠ 关观察 ≠ 改掉落/体力 ≠ 开 R ≠ 抬日表。

---

## 7. 变更记录

| 日 | 事 |
|----|-----|
| 2026-10-10 | 策划 · 初稿 STATUS **待批 A** · 荐 M；tip `b3c025f1` |
| 2026-10-10 | 总控批 A·M · **D379** docs 占位 · tip 关 · backlog→已批 A·施工中·怪物岗 · **≠本号改 MM ≠改体力掉落 ≠关观察** |

---

## 8. 参考

- tip · STATUS D379 · D374 DESIGN / STATUS / spot  
- `EmberDailyCrypt.yml` · `EmberDungeonDiffSkills.yml`  
- 范式：`EmberFrostNovaCast` / `EmberRailSlamCast` / D374 四 Cast  
- 入口：`plugins/TrMenu/menus/ember_daily.yml`

---

*已批 A · 批 M · D379 docs 占位 · 施工交怪物岗 · ≠本号改 MM ≠改体力掉落 ≠关观察 ≠开 R。*
