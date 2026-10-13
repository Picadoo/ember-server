# 余烬 · 挂机庭「顺手转化」（产→每周转化轨可点 · ≠第五件花料 · ≠推荐花复读 · ≠洗练/烙纹再拧 · ≠半行 · ≠抬日表 · ≠宝石无同构）

STATUS=**CLOSED live** · feat `0eb85b785` · tip `b5aea9bc5` · PASS `372360684` · jar `1.65.816-d13324.local` · D13324 · TIP-CS · 2026-10-14 03:30 Asia/Shanghai
> tip [`STATUS-ember-next-hard-debt-afk-courtyard-quick-convert-need-design-2026-10-14.md`](../status/STATUS-ember-next-hard-debt-afk-courtyard-quick-convert-need-design-2026-10-14.md) · RESEARCH [`RESEARCH-ember-afk-courtyard-quick-convert-2026-10-14.md`](RESEARCH-ember-afk-courtyard-quick-convert-2026-10-14.md) · backlog `B-afk-courtyard-quick-convert` · **≠关观察 ≠抬日表 ≠开 R ≠开 K3 ≠假开旧日常 ≠sx20 ≠菜单诚实/observe 空转 ≠半行独批 ≠Cast粒子主交付 ≠Aid再拧 ≠todayRot再拧 ≠W1e独批 ≠抬 event_rate/event_core/affix_shard ≠新 variety kind ≠第五件花料 ≠推荐花复读 ≠洗练再拧 ≠烙纹再拧 ≠P格兼确认 ≠复开 D423 ≠教裸指令 ≠撤分格 ≠改convert周帽/价 ≠庭内宝石本窗（同构未过）≠顺手随机锻本窗主交**

> **一句话玩家价值：** 挂机庭一点「顺手转化」进既有每周转化页——产胚/币直接喂改族，返回仍回庭；**不**新开第五花料、**不**抬日产、**不**改转化价/周帽。

> **批 A = 采纳方案（荐 M）。批 A ≠ 施工 ≠ 关观察 ≠ 抬日表 ≠ 开 R ≠ 开 K3 ≠ 交 sx20。**

---

## 0. 证据表

| # | 来源 | 口径 |
|---|------|------|
| E1 | 总控 TIP-CR CLOSED `fed2cf6aa` · 连开 TIP-CS | 短征+烙纹已齐；可扩庭**新轨**真入口；宝石须同构否则改题；禁半行 |
| E2 | D13323§6② 宝石同构核验 | 路径已喊✓ · **庭面 gap✗**（afk 无 gem gap）→ **改题** |
| E3 | `ember_p1_afk` | 四花+P+D+W/w+L+G live；**无** 转化格 / 无 `p1 convert` |
| E4 | `ember_p1_forge.yml` V | `menu: ember_p1_convert` live；convert 返回硬编码工坊 |
| E5 | `convertCommand` | 无 `from`/`back`/`CONVERT_FROM`（对比 REROLL/BRAND_FROM 已有 afk） |
| E6 | 庭面已亮 `%forge_gap_convert%` | 看得见仓差，点不进页 |
| E7 | `EmberForgeGoal.CONVERT` | 周转化独立轨；tip 已有 [去转化] |
| E8 | 半行 REJECT `d00ed84bc` 保持否决 | backlog |
| E9 | **为何不是半行** | 主交付=真 command 进转化页；见 §3.5 |

**一句话问题：** 庭内能花能拆能换能洗能烙不能转化——每周转化轨断在入口。

**定位拍板：**

| 选项 | 结论 | 理由 |
|------|------|------|
| Cast/Aid/todayRot/variety 再拧 | **否** | 硬禁；刚 CLOSED |
| 第五花 / 推荐花复读 / 洗练·烙纹再拧 | **否** | 硬禁 / 已齐 |
| 只 Open「去工坊转化」半行 | **否** | REJECT 保持 |
| 庭内顺手宝石 | **否** | 同构缺庭面 gap；§6 仍后置 |
| 庭内顺手随机锻（同窗） | **后置** | §6 后置；本窗先转化 |
| **庭内顺手转化真入口（荐）** | **采纳** | E2–E7；真可点；≠花料/洗练/烙纹 |

---

## 1. 玩家感知目标（≤3）

1. **庭内可转：** 打开挂机庭见「顺手转化」，一点进既有每周转化页（三族改族全保留）。
2. **返回闭环：** 转化页「返回」回到挂机庭（非误落工坊）。
3. **零抬产/零改周帽：** 不改 convert 价/周帽/族表、不改 afk.tiers/daily_kills。

---

## 2. 方案表

| 方案 | 内容 | 评价 |
|------|------|------|
| **A · 只加 Open「记得去工坊转化」半行** | 口号 | **否决独批** — 无新动作=诚实空转 |
| **M · 庭内 C 顺手转化 + afk 返回源（荐）** | 插件补 `CONVERT_FROM[afk]` + `convert from/back`；TrMenu **C**→`corerpg p1 convert from afk`；工坊 V/convert 返回改动态；Open 半行仅同批辅 · **零改**价/周帽/产量 | **荐** — 真可点；庭内闭环；≠第五花/洗练/烙纹再拧 |
| **L · 仅 TrMenu `menu: ember_p1_convert`（返回落工坊）** | 减面 | 可批但弱于闭环；**本窗默认不批 L** |
| **W · 抬周帽/价 / 双开宝石或随机锻同窗 / sx20 / 半行独批 / Cast·Aid再拧 / 第五花** | — | **否决** — 硬禁 |

### 批 A 勾选（总控填）

- [x] **方案 M**（W1a 插件 afk 返回源必做；W1b 庭内 C 格必做；W1c convert/forge 返回动态必做；W1d Open 辅半行同批；见 §3）
- [ ] 或勾 **方案 L**（仅 menu 开 convert；返回落工坊）· **本窗默认不批 L**
- [x] 否决 A（只半行）/ W（抬表/sx20/宝石·随机锻主交/再拧/第五花）
- [x] 批注：总控批 A·M · 全弧非 L · 派插件+物品 · 勿勾 CLOSED · 宝石/随机锻仍后置 · 2026-10-14 03:20 Asia/Shanghai

---

## 3. 可落地（方案 M）

### 3.1 条件与键（钉死 · 只读）

| 项 | 口径 | 纪律 |
|----|------|------|
| 入口命令 | `corerpg p1 convert from afk` | **与工坊同页** `ember_p1_convert`；禁发明第二转化经济 |
| 返回源 | `CONVERT_FROM["afk"]="ember_p1_afk"`（兼 forge→`ember_p1_forge`） | 薄插件；`back` → 挂机庭 |
| 展示 | `%corerpg_p1_forge_gap_convert%`（既有）；周次余量用既有 convert 周帽展示键（有则挂、无则静态「本周有次」） | **零新 PAPI**（默认） |
| 花料/拆/换/洗/烙 | **不撤** E/e/U/u/R/r/Q/q/P/D/W/w/L/G/S | 禁占其格 |

### 3.2 窗拆分

| 窗 | 做什么 | 不动 |
|----|--------|------|
| **W1a · afk 返回源（必做 · 插件）** | `CONVERT_FROM` + `convert from <src>` / `convert back`；返回文案「返回挂机庭」 | 不改周帽/价/族表 |
| **W1b · 顺手转化格（必做 · TrMenu）** | `ember_p1_afk` 用 **C** 替 digit `3`（布局建议 `'#PLWGwCD#'`；施工可微调）名 `§6顺手转化` · lore≤3：转化仓差 + 周次提示 +「点进转化页」· `command: corerpg p1 convert from afk` | 不撤四花+P+D+W/w+L+G；不改层传送（`3` 定义可留死键） |
| **W1c · convert/forge 返回动态（必做 · TrMenu）** | `ember_p1_convert` 返回→`p1 convert back`；工坊 V→`p1 convert from forge` | 不改转化成交逻辑 |
| **W1d · Open / 去哪花半行（必做同批 · 辅）** | Open +半行「本页也可顺手转化」；S 去哪花 +半行同指针 | **不可**无 W1b 独交半行 |
| **W1e · 宝石/随机锻/经济（不做）** | — | 禁庭内宝石/随机锻本窗主交；禁改价/周帽/抬日表 |

### 3.3 玩家路径

| 步 | 路径 | 说明 |
|----|------|------|
| 挂 | 庭内打怪产胚/币 | 现网 |
| **转（本债）** | 点 C → 转化页 · 选族改族 | W1a/b |
| 回 | 转化页返回 → 挂机庭 | W1a/c |
| 花/拆/换/洗/烙 | 仍点 E/U/R/Q/P/D/W/L/G | 保留 |

**文案钉死示例（施工可微调色码，语义不得漂）：**

```
§6顺手转化
§7仓差：%corerpg_p1_forge_gap_convert%
§7每周转化 · 手持刃/符改族
§e➥ §f点进转化页
```

### 3.4 与邻域边界

| 邻域 | 边界 |
|------|------|
| 四花+推荐花+拆+换+洗+烙 | **保留不动**；本债=**每周转化轨入口**，≠再叠花/洗练/烙纹再拧 |
| 工坊转化 V | **同页同经济**；庭内只补入口+返回源 |
| D13322/23 洗练/烙纹 | **保留**；本窗≠再拧 |
| 宝石 / 随机锻 | **后置**（宝石同构未过；随机锻另窗） |
| 半行 REJECT | **保持否决** |

### 3.5 为何不是半行（验收必读）

| 半行空转（禁） | 本窗 M（过） |
|----------------|--------------|
| 只改 Open「去工坊转化」口号 | **真 command 进转化页**（可改族） |
| 玩家无新可点结果 | 庭内 **多一事可花**（每周转化轨） |
| 对位 sure/pity/observe | 对位工坊 V 转化入口 → 庭内可达 |

---

## 4. 验收（施工号用 · 本号只定规格）

| ID | 项 |
|----|-----|
| V1 | 庭内出现 C「顺手转化」；左键打开 `ember_p1_convert` |
| V2 | 转化页返回落到 `ember_p1_afk`（非误落 forge，除非玩家本从 forge 进） |
| V3 | 转化成交路径与工坊入口一致（周帽/价/族表不变） |
| V4 | `git diff`：无改日帽/`afk.tiers`/伤乘/S 表/`event_*`/convert 价与周帽、无 sx20、无半行独档、无 Cast/Aid、无第五花/推荐花/洗练·烙纹再拧、无宝石主交 |
| V5 | 玩家面无裸指令要求（菜单点击优先） |

---

## 5. 禁区

- sx20 · 新地图硬开 · 抬挂机产量/日顶 · 抬伤乘 · Affix densify · forgeroll 权重 densify
- Pack6 · 天赋 · 灰印 · R · L13 · K3 · 假开旧日常 · 重开 calamity · 新 fest
- 菜单诚实/sure·pity·soft 半行空转 · observe 空转 · **只半行无 C 格**
- **Cast 粒子打磨 / Aid再拧 / todayRot cue再拧 / W1e 独批** · **抬 event_core** · **新 variety kind**
- **第五件花料** · **推荐花复读** · **洗练再拧** · **烙纹再拧** · **P 格兼确认**
- 复开 D423 · stage 脏 runtime · 教裸指令 · 改 convert 周帽/价
- **本窗不以「庭内顺手宝石 / 顺手随机锻」当主交付**（§6 后置）

---

## 6. 后置（明文 · 本窗不做）

1. P 格兼确认（须另批）
2. 庭内顺手宝石入口（须庭面先亮 gem gap 再证同构）
3. 庭内顺手随机锻入口（若另证 · 面已亮 `forge_gap_roll`）
4. Cast 粒子二次打磨 / Aid 再拧 / variety 经济发核心
5. fest 继任 / 灾厄枢纽可见（须总控另批）
6. sx20（仍禁，除非总控另开窗）

---

## 变更记录

| 日 | 事 |
|----|-----|
| 2026-10-14 03:30 Asia/Shanghai | **CLOSED live** · feat `0eb85b785` · tip `b5aea9bc5` · PASS `372360684` · jar `1.65.816-d13324.local` · 证据 `/workspace/tmp/tip-cs-afk-convert/` · W1a–d 齐 · 宝石/随机锻仍后置 |
| 2026-10-14 03:20 Asia/Shanghai | 总控批 A·M · 全弧非 L · 派插件+物品 · 勿勾 CLOSED · 宝石/随机锻仍后置 |
| 2026-10-14 03:17 Asia/Shanghai | 策划 · 初稿 STATUS=**待批 A** · 荐 M · TIP-CR CLOSED 后连开 · 选题挂机庭顺手转化 · 拟 D13324 · TIP-CS · ≠半行 ≠第五花 ≠洗练/烙纹再拧 ≠Cast/Aid/variety再拧 · **改题有证据**（短征饱和；GEM 同构缺庭面 gap；面亮 forge_gap_convert；afk 无入口；forge V live；见 RESEARCH） |

*D13324 **CLOSED live** · `0eb85b785` / `b5aea9bc5` · PASS `372360684` · 挂机庭顺手转化 · TIP-CS。**已批 A · 方案 M · 全弧非 L** · ≠半行 ≠第五花 ≠洗练/烙纹再拧 ≠宝石本窗 · ≠sx20。*
