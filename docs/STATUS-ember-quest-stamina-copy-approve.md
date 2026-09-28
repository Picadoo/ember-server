# STATUS · B2.7 quest 体力台词 · 批准 B（总控）

| 字段 | 值 |
|------|-----|
| 时间 | 2026-09-28 22:59 Asia/Shanghai |
| 岗 | 余烬-总控 |
| 批准 | **B** |
| 设计 tip | `b7ccbb8` · `docs/design-ember-quest-stamina-copy.md`（已 push `origin/main`） |
| 下一 | ~~插件施工~~ → 已结 |
| 状态 | **PASS · 勾销**（施工 `4eb9af8` · 测 `f2edc25` · backlog close） |

---

## 一句话

**已批 B：** live `plugins/CoreRpg/quest.yml` **与** `CoreRpg/src/main/resources/quest.yml` 同 6 处玩家可见字符串（票→体力/体力药）；禁改步骤 type/event/count/items 与任何 cost。理由：同薄窗 + 防 jar 重打包盖回 live。

---

## 范围（6 替换 · 双路径同 diff）

| # | 键位（摘要） | 旧 → 新 |
|---|-------------|---------|
| 1 | done | 日票拿去 → **体力药拿去** |
| 2 | intro | 每日 3 张免费日票，主线再送 1 张 → **日回体力约可刷 3 次日常，主线再送 1 瓶体力药** |
| 3 | hint | 需日票 → **耗体力** |
| 4 | done | 带上周票 → **留够体力** |
| 5 | hint | 每日 3 张免费日票 → **日回体力约可刷 3 次日常** |
| 6 | hint | 每日免费日票 → **日回体力** |

路径：

1. `plugins/CoreRpg/quest.yml`（live）
2. `CoreRpg/src/main/resources/quest.yml`（src 镜像 · 同 6 处）

完整句见设计稿命中表（`b7ccbb8`）。

---

## 禁项

- **不**改步骤 `type` / `event` / `count` / `mobs` / `complete_on` / `items` 键与数量
- **不**改体力 cost / 进本 / `cash.yml` 数值 / TrMenu / DP / MM / loot
- 总控 **不**亲改 `quest.yml`（插件施工）
- 插件 **未 push**；测岗 **本窗不派**

---

## 下一

1. 派 **余烬-插件** 按上表双路径施工 + `corerpg reload`（或服约定 quest 重载）
2. 插件回报 tip 后总控再派测 / 代推
