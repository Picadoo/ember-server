# RESEARCH · 短征「今日推」本局内可感（日更花样续 · ≠Cast/Aid复读 · ≠半行 · ≠庭内镜像 · ≠sx20 · ≠抬表）· 2026-10-14

> **状态：** **勾毕 CLOSED live** · feat `1a3e1a3de` · PASS `bd7a68ce8` · jar `1.65.812-d13320` · 批 sha `31f06f562` · live `d4ce8d3ed` · 拟 D13320 · TIP-CO · 2026-10-14 02:18 Asia/Shanghai  
> **起因：** 总控【连开·TIP-CO / 拟 D13320】上游 TIP-CN CLOSED live `931274777`（tip `021d8e351` · PASS `17f05e0f1` · EliteAid×19 齐）。请立刻交下一档硬债 docs-only 待批 A。**优先短征日更花样 / 乐趣系统**（真可点或房内可感）；Cast 波+BossAid+EliteAid **已齐** · 勿以 Cast/Aid 粒子二次打磨 / EliteAid·BossAid 再拧当主交付；挂机庭环已齐勿再叠；半行 REJECT `d00ed84bc` **保持否决**。  
> **上游：** TIP-CN CLOSED live `931274777` · D13319；Cast 全波 + BossAid + EliteAid 齐；今日推/冲一下/周短征/三冲齐；四花+推荐花+拆+换齐；半行 REJECT `d00ed84bc` **保持否决**  
> **硬禁：** **零** 半行独批 · **零** sx20 · **零** L13 · **零** K3 live · **零** Pack6/灰印/R · **零抬** AFK 日表/`daily_kills`/`afk.tiers`/`short:5`/伤乘/S40–S58 · **零** Affix densify · **零** Cast 粒子当主交付 · **零** EliteAid/BossAid 再拧 · **零** 假开旧日常 · **零** 教裸指令 · **零** 庭内再叠同类顺手无新证据 · **零** 灾厄重开 · **零** 复开 D423 · **零** stage 脏 runtime

---

## 0. 一句话

短征 **菜单「今日推」已齐**（D13309），但 **进本后与任意短征同构**——开局/开房/结算无「今日」专属可感。Cast+Aid 三房弧已齐；D13319§6 后置均触硬禁。本债=今日推本局 **进本标记 + 分房提示 + 结算完成反馈**（真 session 分支 · 房内可感），**不是**再叠 Aid/Cast，**不是**选页半行，**不是**庭内镜像。

---

## 1. 选题扫描（TIP-CN 后 · 必须真动作 · 优先短征日更）

| 候选 | 裁决 | 理由 |
|------|------|------|
| 庭内再叠镜像顺手 | **否** | 派单明文：庭环已齐，无新硬债勿叠 |
| 选页可躲半行再交 | **否** | REJECT `d00ed84bc` 保持否决 |
| Cast 粒子二次打磨 / EliteAid·BossAid 再拧 / 第二 Cast | **否** | 硬禁；刚 CLOSED Aid 复读 |
| 周目标可冲 / 三冲 / 冲一下 / 今日推菜单 | **否** | D13309–13 已 CLOSED；复读导航 |
| fest / 灾厄 / sx20 | **否** | 硬禁或另批 |
| 短征接入完整 variety（抬 event_core） | **否** | 易触抬表；本窗不做经济扩 |
| **短征「今日推」本局内可感（荐）** | **采纳** | D13309 只到菜单；进本 `EmberMapEnterFeel` 仅标「短征」无 today 分支；日更花样断在门内；真 session 可感 ≠半行 |

**择优结论：** Cast/Aid/庭环/导航链已齐；§6 后置不可主交。扫线发现 **进本路径无 `sx_today`/`todayKey` 分支**（`EmberMapEnterFeel.modeTag` 短征一律「短征」）——与菜单「今日推」落差即日更真债。故交今日推本局内可感；**证据表见 §2**；**为何不是半行见 E8**。

---

## 2. 证据（现网只读 · 2026-10-14）

| # | 事实 | 路径 | 含义 |
|---|------|------|------|
| E1 | 总控 TIP-CN CLOSED · 连开 TIP-CO · **首选短征日更花样** · 禁半行/Cast·Aid打磨主交/庭内再叠 | `931274777` | 选题边界 |
| E2 | D13309「今日推」菜单+`sx_today*` PAPI **已 live**；选页 D / 冒险三冲可进今日本 | tip `5611a4337` · `EmberShortRules.todayKey` | 菜单侧齐 |
| E3 | `EmberMapEnterFeel.modeTag`：短征一律返回「短征」；**无** todayKey 比对 | `EmberMapEnterFeel.java` | 进本无今日差异 |
| E4 | Session/短征 settle **无** `todayRot` 字段或今日完成句 | `EmberSessionService` · `EmberShortService` | 局内/结算无今日分支 |
| E5 | Cast 四档 + BossAid + EliteAid **全线 live** | D13305/11/14/18/19 | 禁 Aid/Cast 再拧 |
| E6 | 挂机庭 E/U/R/Q/P/D/W 齐；导航今日推/冲一下/周短征/三冲齐 | backlog · tip live | 勿回资源环/导航复读 |
| E7 | 半行 REJECT 保持 | `d00ed84bc` | 禁入口口号独批 |
| E8 | **为何不是半行：** 主交付=**插件 session 分支** → 进本/开房/结算 **真不同反馈**（title·AB·完成句·可选再进按钮）；玩家感知=「打的是今日推」；**不是** lore/Open 口号独改 | 本 RESEARCH | 过硬禁 |

**一句话缺口：** 点「今日推」进本后，体感与随手点任意短征无异——日更轮换停在菜单，门内无花样。

---

## 3. 方案方向（荐 M · 详 DESIGN）

1. **插件 W1a：** 短征进本 commit 时若 `m.key.equals(EmberShortRules.todayKey())` → session `todayRot=true`；title/ActionBar/tell「今日推·本」；**零改**体力/日帽  
2. **插件 W1b：** `todayRot` 时各房开门（或进房）ActionBar「今日推 · 第N房」短提示（轻量，可节流）  
3. **插件 W1c：** `todayRot` 且本局有奖结算 → tell「今日推完成」+ chat 按钮「再进今日推」/`enter short-today`（复用既有路由）；满态诚实  
4. **物品 W1d（可选同批辅）：** 选页 D lore 补「进本有今日标记」半行——**不可独批**；主交付仍是局内分支  
5. **零改** Cast/Aid/HP/掉落/S40–S58/日帽/`daily_kills`/`afk.tiers`/地图/MM  
6. **施工岗：** 批后 **余烬-插件**（主）· 物品可选 · 不派怪物/地图

---

## 4. 明确不做

- 只改选页/Open「今日推有标记」半行无局内分支 · Cast 粒子打磨 · EliteAid/BossAid 再拧 · 第二 Cast  
- 庭内再叠顺手 · 第五花料 · P 兼确认 · 抬产/抬伤表 · sx20 · Pack6/灰印/R/L13/K3  
- 重开 calamity · 复开 D423 · 教裸指令 · 假开旧日常 · 抬 event_rate/event_core · 新 variety kind  

## STATUS: **勾毕 CLOSED live** · feat `1a3e1a3de` · PASS `bd7a68ce8` · jar `1.65.812-d13320` · 批 sha `31f06f562` · live `d4ce8d3ed` · 拟 D13320 · TIP-CO · 半行 REJECT `d00ed84bc` **保持否决** · 2026-10-14 02:18 Asia/Shanghai
