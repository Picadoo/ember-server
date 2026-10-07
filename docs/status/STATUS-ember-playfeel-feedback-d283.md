# 状态 · D283：P0 反馈 — 破绽首次提示 + 本局摘要 + 七图名片

**日期：** 2026-10-07（上海时间）  
**上游：** 总控批 A · `DESIGN-ember-playfeel-hardening-2026-10-07.md` §8-1 · CoreRpg **1.65.79** · `balance_version` **58**（未动数值）

## 人话

机制早就有（撞墙 / 落空 / 破招 + 房间事件），玩家看不见等于没有。本窗只加**反馈与名片文案**：破绽每招种每局首次 ActionBar/标题闪一下；结算多一行本局摘要；冒险/挑战页七图各加一句招牌。

## 改了什么

| 处 | 改动 |
|---|---|
| `EmberRunSession` | 瞬态计数 `wallHits/whiffHits/breakHits` + `noteCounterplay`（每招种首次 true） |
| `EmberCounterplay.firstFlash` | 短动词：撞墙破绽 / 落空破绽 / 破招成功 |
| `EmberRunDirector` | wall/whiff/break 成功后：计数；仅首次 `flashCounterplayTip`（ActionBar + subtitle）；chat `tellRun` 仍每次 |
| `EmberSettleService.playfeelSummary` | 结算行「本局：撞墙 ×N · … · 限时清房 ✔/✘」；交付前发给有资格玩家 |
| `EmberRunMaps.MapDef.hint` | 读图级 `hint:`（纯文案；房间 hint 不动） |
| `ember-v1-runs.yml`（两份） | Q01–Q07 名片 hint（§4.1） |
| TrMenu `ember_p1_adventure` / `_challenge` | 七图 lore「§d名片：…」 |
| 版本 | **1.65.78 → 1.65.79** |

## 验收

- 静态：`rg firstFlash|playfeelSummary|§d名片|D283 名片` 有键
- 单测：`EmberCounterplayTest` / `EmberSettleServiceTest` D283 用例
- p1sim：**不变**（纯反馈）
- play Enabling **1.65.79** @ **19:49:16** Asia/Shanghai；Done 5.9s；TrMenu 67 菜单；login 不停（proxy 进程未在本机 ps 列出则略）

## 不变

- 招式伤害 / 冷却 / 掉落 / 体力 / 周 cap / 六槽
- Pack6、VIP/战令暗开、Director 大分支
- `balance_version` 仍 58
