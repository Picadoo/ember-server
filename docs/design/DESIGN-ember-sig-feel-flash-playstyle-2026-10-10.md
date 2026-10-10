# 余烬 · 成长花样 · 签名触发可感短闪（D455）

STATUS=**已批 A · 方案 M · D455 · PASS** · 2026-10-10 · tip [`STATUS-ember-next-hard-debt-sig-feel-flash-need-design-2026-10-10.md`](../status/STATUS-ember-next-hard-debt-sig-feel-flash-need-design-2026-10-10.md) · backlog `B-sig-feel-flash` · **≠抬 ALTS/掉率/烙印价 ≠AFK 日表 ≠K3 ≠sx20 ≠局内热换 ≠调律 R**

> **一句话玩家价值：** 带着签名进本抬头能认「这是哪件在干活」——进本一眼 + 触发短闪；**不**加数值强度。

## 0. 证据

| # | 口径 |
|---|------|
| E1 | 套装有 `flashProc`（炽愈/焚烬/烬爆）；技能有 D445 落点短闪；签名躲开回气曾静默，借势/反震/护盾只泛称 |
| E2 | D297 城内调律决策已有；局内仍难感「签名身份」 |
| E3 | 加固债 #2 / D290 深链已做菜单互指；缺战斗触发可读 |

## 1. 方案

| 方案 | 内容 | 裁决 |
|------|------|------|
| **M** | W1a 进本 `announceSigFeel` · W1b `flashSigOwned`（dodge_heal / dodge_burst / hit_burst / skill_ignite / skill_shield / burn_spread）· 让位套装 HUD · debounce 800ms | **主推** |
| A | 只改 lore | 否决独批 |
| R | 改 ALTS / 新 mods / 局内热换 | **禁本号** |

## 2. 钉死

- 归因：仅当**穿着生效签名** mods 含该键时打「签名·名」；天赋独有路径保留薄泛称
- 优先级：套装 `procFlash` / `forceHud` > 签名短闪（复用 `flashSkillConfirm`）
- **禁**改 DEFS.mods / ALTS / STAMP_RATE / 烙印价

## 3. 验收

| V | |
|---|--|
| V1 | 有签名进本：聊天/抬头可见「本局签名：…」 |
| V2 | 单测 `EmberSigFeelTest` PASS |
| V3 | Enabling 1.65.147-d455 · SEVERE0 |
| V4 | 未改 ember-v1-growth ALTS / afk.tiers |

- [x] 批注：总控 **已批 A · 方案 M · D455** · 2026-10-10
