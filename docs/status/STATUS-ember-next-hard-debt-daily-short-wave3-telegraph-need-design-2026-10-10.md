# 状态 · 下一档硬债选定 · 需策划（日更短本第三拍扩线 · 预警环伤 · 庭院/焦骨/潮蚀/断塔）

> **上游结案：** 使魔闭环诚实 tip `1d938a07` 已交待批；总控排队**内容真债 2** = 日更短本第三拍扩线。Stage2 观察照续（≥**2026-10-10 17:40 CST**，**≠关窗 ≠改×0.97**）。霜晶/锈轨第三拍已 PASS（`EmberFrostNovaCast` / `EmberRailSlamCast`）；庭院·焦骨·潮蚀·断塔 Boss 仍无预警 onTimer 环伤。样本 R / Pack6 / 天赋·灰印 HOLD / 开 K3 live / 关观察 / 抬体力·掉落 **仍禁**。

**日期：** 2026-10-10（上海时间）  
**本窗性质：** tip **待批 A** · **docs-only** · **零 jar / 零改体力 / 零改掉落 / 零改 ×0.97** · **≠关观察** · **≠开闸** · **≠开样本 R** · **≠ stage 脏 runtime** · **≠假平面**  
**硬规格（待批 A · 荐 M）：** [`DESIGN-ember-daily-short-wave3-telegraph-2026-10-10.md`](../design/DESIGN-ember-daily-short-wave3-telegraph-2026-10-10.md) · backlog `B-ember-daily-short-wave3-telegraph`  
**打开理由：** 七线日更短本里霜/锈已有「message→粒子→delay→半径伤」可读第三拍；另四线终厅仍是无预警环伤——打起来像「站着挨烫」，缺预警节奏。

---

## 0. 局势一句话

霜晶/锈轨证明可读条可躲；庭院/焦骨/潮蚀/断塔同族无预警环还在——扩范式即可，零动体力掉落。

---

## 1. 选题（总控排队债 2）

| # | 候选 | 裁决 | 理由（证据） |
|---|------|------|--------------|
| **1** | **日更短本第三拍扩线（预警环伤 → 四缺线）** | **采纳 · 需策划** | 总控内容债 2；见 §1.1 |
| 2 | 抬体力 / 改掉落表 / 新假平面图 | **否** | 硬禁 · 零体力零掉落改 |
| 3 | 残誓地窖同构读条 | **后置** | 同族缺口（E6）；本窗 M 先钉总控点名四线；地窖另号 |
| 4 | 断塔环廊/防坠再折腾 | **否** | 第三拍稿硬条「勿再折腾环廊」；本债只动 Boss MM 战内 |

### 1.1 证据核验（实扫 · 2026-10-10）

| # | 证据 | 出处 | 含义 |
|---|------|------|------|
| E1 | `EmberFrostNovaCast`：message「霜核蓄力」→ cloud 粒子 → `delay 25` → `damage 1.2` r5 + SLOW；Boss `EmberDailyFrostBrute` 挂 `~onTimer:80` | `plugins/MythicMobs/Skills/EmberDungeonDiffSkills.yml` L110–121 · `EmberDailyFrost.yml` | **范式已 live** |
| E2 | `EmberRailSlamCast`：同构工业风（无 SLOW）；`EmberDailyRailWarden` 挂 `~onTimer:80`；逼近 message 保留 | 同 Skills L123–133 · `EmberDailyRail.yml` | **扩线模板已 PASS**（测报 `STATUS-ember-daily-rail-third-beat-test`） |
| E3 | 庭院 `EmberDailyBrute`：`damage 0.5 r6 ~onTimer:40` · **无** message/delay 绑伤 | `EmberDaily.yml` L56–59 | **无预警环伤** |
| E4 | 焦骨 `EmberAshBrute`：`damage 0.5 r5 ~onTimer:40` · **无**读条 | `EmberDailyAsh.yml` L57–61 | **同族缺口** |
| E5 | 潮蚀 `EmberDailyTideBrute`：`damage 0.4 r5 ~onTimer:40` + 独立 SLOW `~onTimer:50` · **无**读条绑结算 | `EmberDailyTide.yml` L61–65 | **同族缺口**（SLOW 光环可并入读条或删后挂读条） |
| E6 | 断塔 `EmberDailySpireWarden`：`damage 0.45 r5 ~onTimer:45`；逼近 message `~onTimer:180` **不绑伤** | `EmberDailySpire.yml` L60–65 | **同族缺口**（逼近≠读条预警） |
| E7 | 残誓 `EmberDailyCryptWarden`：`damage 0.4 r6 ~onTimer:50` + 点名/SLOW `~onTimer:160` | `EmberDailyCrypt.yml` L58–63 | **同族**；本窗后置 |
| E8 | 霜晶/锈轨第三拍设计 + PASS | `design-ember-daily-third-beat.md` · `design-ember-daily-rail-third-beat.md` · 两份 `*-test.md` | 铁栅零回归口径可复用 |
| E9 | 入口 TrMenu `ember_daily.yml` 七线点选进本 | `plugins/TrMenu/menus/ember_daily.yml` | **入口不变**；本窗最多半行文案（可选） |
| E10 | Stage2 / 硬禁 | 观察续；禁样本 R、Pack6、天赋/灰印、关观察、改 ×0.97、开 K3、抬体力/掉落 | **本债 docs-only** |

**玩家价值：** 日刷终厅有「看提示→拉开→可躲」的战斗节奏，四线不再纯站桩挨环，提高趣味与重刷意愿（不靠抬奖励）。

**荐方案 M：** 把 FrostNova/RailSlam 范式扩到庭院/焦骨/潮蚀/断塔四线 Boss（删无预警环 + 各挂 1 个主题读条 Skill）；零动 DP/体力/掉落/地图。

---

## 2. 派单句

```
【派单·内容真债待批A】日更短本第三拍扩线（预警环伤 · 庭院/焦骨/潮蚀/断塔）
优先级：总控排队债2 · docs-only · 对齐 FrostNova/RailSlam 范式
硬规格：docs/design/DESIGN-ember-daily-short-wave3-telegraph-2026-10-10.md
荐方案：M（四线同构读条可躲）
禁：抬体力/掉落 · 假平面 · 样本R · Pack6 · 天赋灰印 · 关观察 · 改×0.97 · 开K3 · 断塔环廊再折腾
入口：TrMenu ember_daily 不变（玩家不打命令）
批A ≠ 施工 ≠ 关观察
```

---

## 3. 本窗不做

- 不施工 MythicMobs / DungeonPlus / TrMenu（等批 A 后另号）
- 不关 Stage2 观察、不改 ×0.97、不开 K3、不开样本 R
- 不抬体力 30、不改 mmgive/通关箱/掉落池
- 不改 MCA / 假平面 / 房2 链式 / boss_prep / 断塔环廊防坠
- 不默默 stage 脏 runtime YAML

