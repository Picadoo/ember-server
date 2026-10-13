# RESEARCH · 挂机庭「顺手烙纹」（产→烙纹轨可点 · ≠第五件花料 · ≠推荐花复读 · ≠洗练再拧 · ≠半行 · ≠抬表）· 2026-10-14

> **状态：** **CLOSED live** · feat `cf35d9948` · tip `ce2f8c95f` · PASS `d9f70ed9a` · jar `1.65.815-d13323.local` · TIP-CR · 2026-10-14 03:11 Asia/Shanghai
> **起因：** 总控【连开·TIP-CR / 拟 D13323】上游 TIP-CQ CLOSED live `e099c02b7`（feat `a342f34cf` · tip `6804ed53a` · jar `1.65.814-d13322.local` · PASS `16998530c` · 挂机庭顺手洗练齐）。请立刻交下一档硬债 docs-only 待批 A · 荐 M。**优先短征日更/乐趣**（真可点或房内可感）；或挂机庭资源环**新轨**真入口；D13322§6 后置「庭内顺手宝石/烙纹」须另证真债才可交。Cast+BossAid+EliteAid+todayRot+variety(W1a–d)+庭内四花+推荐+拆+换+洗练 **已齐勿复读**。半行 REJECT `d00ed84bc` **保持否决**。  
> **上游：** TIP-CQ CLOSED live `e099c02b7` · feat `a342f34cf` · tip `6804ed53a` · PASS `16998530c` · 批 sha `33d94eb40` · D13322  
> **硬禁：** **零** 半行独批 · **零** 第五花/推荐花复读 · **零** 洗练再拧 · **零** Cast粒子打磨 · **零** Aid再拧 · **零** todayRot/W1e独批 · **零** 抬 event_rate/event_core/affix_shard · **零** 新 variety kind · **零** 完整 variety 经济 · **零** sx20 · **零** 抬日表/伤表/S表 · **零** Pack6 · **零** 开R/K3 · **零** 关观察 · **零** 灾厄重开 · **零** 教裸指令 · **零** stage脏runtime

---

## 0. 一句话

短征日更玩法层（Cast/Aid/todayRot/variety）与庭内花/拆/换/洗 **已饱和**；挂机日满/`forgeGoal=BRAND` 已会喊「碎够合成烙纹 · /corerpg p1 brand craft」，庭内 Open 也亮 `%forge_gap_brand%`，但 **烙纹页无庭内可点入口**——玩家须跳工坊再点「烙纹定向」。本债=庭内 **G「顺手烙纹」真入口** 进既有烙纹页（返回挂机庭），**不是**第五花料，**不是**推荐花复读，**不是**洗练再拧，**不是**半行口号。

---

## 1. 选题扫描（TIP-CQ 后 · 必须真动作）

| 候选 | 裁决 | 理由 |
|------|------|------|
| Cast 粒子打磨 / Aid再拧 / todayRot/W1e / variety 再拧 / 完整 variety 经济 | **否** | 硬禁；刚 CLOSED |
| 半行独批（选页/Open 口号） | **否** | REJECT `d00ed84bc` 保持 |
| 第五花 / 推荐花复读 / 洗练再拧 / 拆换复读 | **否** | 硬禁 / 已齐 |
| 导航三冲 / 今日推 / 冲一下复读 | **否** | 已 CLOSED |
| fest / 灾厄 / sx20 | **否** | 硬禁或另批 |
| 庭内顺手宝石（同 §6） | **后置** | 同构真债可证，但本窗先交烙纹（庭面已亮 brand 仓差；宝石后置） |
| **挂机庭「顺手烙纹」真入口（荐）** | **采纳** | D13322§6②；AfkForgePath BRAND 已喊；afk 无入口；forge G live；=产→烙纹轨真扩 |

**择优结论：** 短征 §6 余项多触硬禁（半行/经济/打磨/sx20/fest）。扫线发现庭资源环 **烙纹轨断链**——与 D13322§6②「须另证真债」对得上：系统已认定消耗点、菜单无闭环。故交庭内顺手烙纹；**证据表见 §2**；**为何不是半行见 E8**；**改题有证据**（短征饱和 + §6② + 现网缺口）。

---

## 2. 证据（现网只读 · 2026-10-14）

| # | 事实 | 路径 | 含义 |
|---|------|------|------|
| E1 | 总控 TIP-CQ CLOSED · 连开 TIP-CR · 优先短征/乐趣或庭**新轨**真入口；D13322§6 宝石/烙纹须另证 | live `e099c02b7` · 派单 | 选题边界 |
| E2 | D13322§6② 明文后置「庭内顺手宝石/烙纹入口（若另证真债）」 | DESIGN-reroll | 合法接力 |
| E3 | `ember_p1_afk`：四花+P+D+W/w+L… **无** 烙纹 Icon / 无 `p1 brand` / 无 `menu: ember_p1_brand` | TrMenu live | 庭内无烙纹入口 |
| E4 | `ember_p1_forge` Icon **G**：`menu: ember_p1_brand`（烙纹定向）live；返回硬编码 `menu: ember_p1_forge` | forge.yml / brand.yml | 真动作在盘；返回无 afk 源 |
| E5 | `brandCommand`：`menu` 仅 `openMenu(ember_p1_brand)` · **无** `from`/`back`/`BRAND_FROM`（对比 `REROLL_FROM` 已有 afk） | `EmberGrowthService` | 薄插件可补返回源（对位 D13322） |
| E6 | `EmberAfkForgePath`：`forgeGoal==BRAND` 喊「碎够合成烙纹 · /corerpg p1 brand craft」；`EmberForgeGoal.BRAND` 文案「挂机攒碎片 → 合成烙纹」 | Java live | 系统已认定烙纹是挂机产料消耗点，入口未闭环 |
| E7 | 庭内 Open/战况已亮 `%corerpg_p1_forge_gap_brand%` / `%forge_gap_pin%` —— **看得见仓差，点不进烙纹页** | afk.yml | 面债=入口，非半行补文案 |
| E8 | **为何不是半行：** 主交付=新菜单格 **真 `command: corerpg p1 brand from afk`** → 打开既有烙纹页可合成/定向；Open 半行仅同批辅，**不可独批** | 本 RESEARCH | 过硬禁 |
| E9 | 短征 Cast/Aid/todayRot/variety(W1a–d) 齐；§6 半行/经济/打磨/sx20 不可主交 | D13318–21 | 勿复读短征主交 |

**一句话缺口：** 挂机产碎已可喂烙纹，庭内却点不到——资源环烙纹侧断在入口（类比 D13322 洗练）。

---

## 3. 方案方向（荐 M · 详 DESIGN）

1. **插件 W1a（薄）：** `BRAND_FROM.put("afk","ember_p1_afk")`（兼 `forge` 等既有入口）+ `corerpg p1 brand from <src>` / `brand back` + 返回文案「返回挂机庭」  
2. **TrMenu W1b：** `ember_p1_afk` 空位落 **G**「顺手烙纹」· lore 挂 `%forge_gap_brand%` / `%forge_gap_pin%` · `command: corerpg p1 brand from afk`  
3. **TrMenu W1c：** `ember_p1_brand` 返回改 `p1 brand back`；工坊 G 改 `p1 brand from forge`（保返回工坊）  
4. **TrMenu W1d（同批辅）：** Open / 去哪花 +半行「本页也可顺手烙纹」——**不可独批**  
5. **零改** brand 价/次数/craftShards · afk.tiers · daily_kills · 伤表 · Cast/Aid/variety · 洗练  
6. **施工岗：** 批后 **余烬-插件**（W1a）+ **余烬-物品**（W1b/c/d）；不派怪物/地图

## 4. 明确不做

- 只 Open/lore 半行无 G 格 · 第五件花料 · 推荐花复读 · 洗练再拧 · 庭内顺手宝石（§6 后置）· 抬产/改烙纹价  
- Cast/Aid/todayRot/variety 再拧 · sx20 · Pack6/灰印/R/L13/K3 · 灾厄 · 教裸指令 · stage 脏 runtime

## STATUS: **CLOSED live** · `cf35d9948` / `ce2f8c95f` · PASS `d9f70ed9a` · D13323 · TIP-CR · 2026-10-14 03:11 Asia/Shanghai
