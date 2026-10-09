> **旁注（已关 · 2026-10-10）：** 已批 A · 批 M · **D379 docs 占位 · 施工交怪物岗** · STATUS [`STATUS-ember-daily-crypt-telegraph-d379-2026-10-10.md`](STATUS-ember-daily-crypt-telegraph-d379-2026-10-10.md) · tip `b3c025f1` · **≠本号改 MM ≠改体力掉落 ≠关观察 ≠开 R**。下文为选题当时正文，保留作史。

# 状态 · 下一档硬债选定 · 需策划（日更残誓地窖第三拍预警环 · D374 E8 后置升主）

> **上游结案：** 总控 D378 已落 @`87991c6b`（使魔→生活兑尘跳转）→「请交下一内容真债 tip（提高在线/趣味；禁抬挂机日表、样本R、Pack6、天赋灰印、观察运维变体、改×0.97）」。Stage2 观察照续（≥**2026-10-10 17:40 CST**，**≠关窗 ≠改×0.97**）。禁复述刚落：使魔出战 D373 / 日更四线预警 D374 / 挂机去哪花 D375 / 生活 Layout D376 / 枢纽旁轨 D377 / 使魔→生活 D378。

**日期：** 2026-10-10（上海时间）  
**本窗性质：** tip **已关 · 已批 A · 批 M · D379** · **docs 占位 · 施工交怪物岗** · **≠本号改 MM** · **≠关观察** · **≠抬 daily_kills / afk.tiers** · **≠开样本 R** · **≠开 K3** · **≠改体力掉落**  
**硬规格（已批 A · 批 M · D379）：** [`DESIGN-ember-daily-crypt-telegraph-2026-10-10.md`](../design/DESIGN-ember-daily-crypt-telegraph-2026-10-10.md) · backlog `B-ember-daily-crypt-telegraph`  
**打开理由：** D374 已把庭院/焦骨/潮蚀/断塔四线修成可读第三拍，并**明文后置**残誓地窖（E8）。现态七线里只剩 `EmberDailyCryptWarden` 仍挂无预警 `damage 0.4 r6 ~onTimer:50`——日刷地窖终厅仍是「站着挨烫」，与六线可读节奏落差最大，重刷趣味被拖短。

---

## 0. 局势一句话

六线终厅已「看提示→拉开可躲」；残誓地窖仍是静默环伤——同构一拍即可，零动体力掉落。

---

## 1. 选题

| # | 候选 | 裁决 | 理由（证据） |
|---|------|------|--------------|
| **1** | **残誓地窖第三拍预警环补齐** | **采纳 · 需策划** | 见 §1.1；D374 E8 后置升主；趣味真缺口 |
| 2 | 日更菜单六线补「终厅读条可躲」半行 | **后置** | 六线 MM 已 PASS；菜单未写「可躲」=欠宣传非假平面（未空许愿）；薄于地窖战斗债；D378 tip 曾作次选 |
| 3 | 工坊页「材料哪来」→挂机反向跳 | **后置** | D375 已 afk→forge；forge 盘密、空位仅底栏；边际薄于终厅节奏 |
| 4 | 使魔等级/进度同屏 | **本轮不写** | 无现成 pet level PAPI；新 Java 过厚 |
| 5 | 复述 D373–D378 主交付 | **否决** | 硬禁 |
| 6 | 观察运维 / 附录 / 关窗 / 抬日表 | **否决** | 硬禁 |

### 1.1 证据核验（实扫 · 2026-10-10）

| # | 证据 | 出处 | 含义 |
|---|------|------|------|
| E1 | D374 已落：`EmberYardPulseCast` / `AshBurst` / `TideCrash` / `SpireSlam` + 霜/锈范式；STATUS 抽测 PASS | `EmberDungeonDiffSkills.yml` · `STATUS-ember-daily-short-wave3-telegraph-spot-d374` · git `761b0678`/`ecd16bbc` | **六线可读第三拍 live** |
| E2 | D374 DESIGN §2.1 / tip E7：**残誓地窖同构后置**（「四线 PASS 后再开独立扩线号」） | `DESIGN-ember-daily-short-wave3-telegraph-2026-10-10.md` | **本债 = 后置升主，≠复述四线施工** |
| E3 | `EmberDailyCryptWarden`：`damage{amount=0.4} @PlayersInRadius{r=6} ~onTimer:50` · **无** message/delay 绑伤 | `plugins/MythicMobs/Mobs/EmberDailyCrypt.yml` | **七线唯一仍静默环伤的 Boss** |
| E4 | 同 Boss：点名 message + SLOW `~onTimer:160`（「守墓者点名」）**不绑**环伤 | 同文件 | **保留**（对齐断塔逼近 message 可留） |
| E5 | SoftHit / mmxp / mmgive 掉落行仍在 | 同文件 | **本窗零改**掉落/SoftHit |
| E6 | TrMenu `ember_daily` 残誓格 lore 仅地形句，**未**写「可躲」 | `ember_daily.yml` C 格 | **无假平面**；可选半行另号或同批可选 |
| E7 | 入口仍 `corerpg enter daily_crypt` · 体力 30 同池 | 同菜单 · 进本 tell | UX：TrMenu 点选，玩家不打指令 |
| E8 | Stage2 / 硬禁 | 观察续；禁抬日表/样本R/Pack6/天赋灰印/关观察/改×0.97/开K3 | docs-only |

**玩家价值：** 地窖终厅与其它日更线同档「可读可躲」——七线重刷节奏齐平，在线时长/趣味↑，不靠抬体力奖励。

**荐方案 M：** 删 CryptWarden 无预警 onTimer 环；新建 1 个主题 `EmberCryptOathCast`（同构 FrostNova/RailSlam）；挂 `~onTimer:80`；**保留**点名+SLOW `~160`；零动体力/掉落/DP/地图。

---

## 2. 派单句

```
【派单·内容真债待批A】日更残誓地窖第三拍预警环（D374 E8 后置升主 · ≠复述四线）
优先级：总控 D378 后内容真债 · docs-only · 对齐 FrostNova/RailSlam / D374 范式
硬规格：docs/design/DESIGN-ember-daily-crypt-telegraph-2026-10-10.md
荐方案：M（CryptOathCast 同构读条可躲 · 保留点名）
禁：抬体力/掉落 · 假平面 · 样本R · Pack6 · 天赋灰印 · 关观察 · 改×0.97 · 开K3 · 抬挂机日表 · 复述D373–D378主交付
入口：TrMenu ember_daily 不变（玩家不打命令）
批A ≠ 施工 ≠ 关观察
```

---

## 3. 排除摘要（写给总控）

| 排除项 | 为何不选 |
|--------|----------|
| 六线菜单「可躲」半行 | 非假平面（未空许愿）；可另号薄窗；本窗优先七线战斗齐平 |
| 工坊←挂机反向 | D375 单向够用；痛点弱于静默环伤 |
| 使魔等级同屏 | 缺 PAPI，易升 Java |
| D374 四线再扫/附录 | 硬禁复述 |
| 观察运维 | 硬禁 |

**若驳回本债：** 次选「日更菜单六线终厅读条可躲半行」（诚实宣传 · 禁写残誓未施工可躲），仍禁抬表。

---

## 4. 硬禁自检

- [x] ≠抬挂机日表 / daily_kills / afk.tiers  
- [x] ≠样本 R ≠ Pack6 ≠天赋灰印 ≠关观察 ≠改 ×0.97 ≠开 K3  
- [x] ≠观察运维变体  
- [x] ≠复述 D373 出战 / D374 四线主交付 / D375 去哪花 / D376 Layout / D377 旁轨 / D378 跳转  
- [x] UX：TrMenu 进本；玩家不打指令；零假平面  
- [x] docs-only；勿 stage 脏 runtime  

---

## 5. 变更记录

| 日 | 事 |
|----|-----|
| 2026-10-10 | 策划 · tip 打开 · 选题残誓地窖第三拍预警 · DESIGN 待批 A·荐 M |
| 2026-10-10 | 总控 · tip **已关** · 批 A·M · **D379** docs 占位 · 施工交怪物岗 · STATUS `STATUS-ember-daily-crypt-telegraph-d379-2026-10-10.md` |

---

*选题日更残誓地窖第三拍 · tip 已关 · 已批 A·M · D379 docs 占位 · 施工交怪物岗 · ≠本号改 MM ≠改体力掉落 ≠关观察。*
