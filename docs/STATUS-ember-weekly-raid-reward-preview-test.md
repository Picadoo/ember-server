# STATUS · 周本/团本奖励预览真分页 A · 测岗

**日期：** 2026-09-28 22:29 → 22:31 Asia/Shanghai（CST）  
**岗：** 余烬-测试岗执行器  
**依据：** `docs/design-ember-weekly-raid-reward-preview.md`（批 A）；施工 tip `991e863` · `docs/STATUS-ember-weekly-raid-reward-preview.md`  
**范围：** TrMenu 真预览子页 `ember_weekly_rewards` / `ember_raid_rewards`（P 入口 · 底栏返回 · 文案抽查 · 禁项 diff）  
**禁项：** **禁改配置** · **未 push**（交总控代推）  
**Verdict：** ✅ **PASS**（验收点全过 · ops=`[]`）

---

## 一句话

hub「周常 / 团本」→ 点 **P** 分别打开 **`周本 · 奖励预览`** / **`团本 · 奖励预览`**（非空壳 tell）；底栏 **B** 回各自父页且坐标未变、背包未增物；周首通护符保底、锋利石约 50% 非必给、T3 不保底文案齐；DP/MM/`loot.yml` diff 空；ops=`[]`。

---

## 总评

| # | 验收点 | 结果 |
|---|--------|------|
| 1 | 周本：开 P → `ember_weekly_rewards` 真页 → 底栏返回 `ember_weekly`；未进本、未发物 | **PASS** |
| 2 | 团本：开 P → `ember_raid_rewards` → 返回 `ember_raid`；未进本、未发物 | **PASS** |
| 3 | 文案：周首通护符保底；锋利石约 50% **非**必给；T3 **不保底** | **PASS** |
| 4 | 禁项：DP/MM/`loot.yml` / 体力 / `over_chance*` diff 空；父页 P 无空壳 tell | **PASS** |

---

## 环境

| 项 | 值 |
|----|-----|
| 工作区 | `/workspace/minecraft` |
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |
| 口 | proxy 25565 / login 25566 / play 25567 |
| 施工 tip | `991e863` feat: weekly/raid reward preview pages (TrMenu) |
| TrMenu | **34** 个菜单已加载（22:26:30 CST 热更） |
| 账号 | 验收 `WrPcft5e`（非 OP · Lv.37）· 辅助 `WrOpcft5e`（临时 OP，已 deop） |
| 探针 | `/tmp/weekly-raid-reward-preview-test.js` · JSON `/tmp/weekly-raid-reward-preview-test.json`（未入 git） |
| 坐标 | hub (-18.5, 58, 110.5) 全程未变 |

---

## 各点证据

### 1 · 周本动线

```
/ember → 点「周常 · 深核」→ title「周常 · 深核」
  → 点 P「奖励预览」(slot 22) → title「周本 · 奖励预览」
  → 点 B「返回周本」(slot 49) → title「周常 · 深核」（开始挑战/奖励预览/次数说明仍在）
```

| 检查 | 实测 | 判定 |
|------|------|------|
| 真页标题 | `周本 · 奖励预览` | **PASS** |
| 分区可见 | 通关箱×3/×2/×8/×4、+6 级、周首通护符、再通 10%/8%、终厅摘要、中核一句 | **PASS** |
| 返回父页 | title=`周常 · 深核`；S/P/R 在 | **PASS** |
| 未进本 | 坐标始终 (-18.5,58,110.5) overworld | **PASS** |
| 未发物 | 背包前后同：刃/护符/面包×16 | **PASS** |
| 无空壳 tell | 点 P 后 chat **无** `[周奖励]` | **PASS** |

（clickWindow 可能报 transaction timeout，属既有 TrMenu/mineflayer 吞事务现象；**菜单标题与槽位已切换**，以窗口 dump 为准。）

### 2 · 团本动线

```
/ember → 点「团本」→ title「团本 · 大厅通道」
  → 点 P「奖励预览」(slot 22) → title「团本 · 奖励预览」
  → 点 B「返回团本」(slot 49) → title「团本 · 大厅通道」
```

| 检查 | 实测 | 判定 |
|------|------|------|
| 真页标题 | `团本 · 奖励预览` | **PASS** |
| 分区可见 | 核心×4 / 晶×2 / 碎片×10 / 骨尘×6、锋利石约 50%、周首通团戒、终厅 T2/T3 | **PASS** |
| 返回父页 | title=`团本 · 大厅通道`；开始协作/奖励预览在 | **PASS** |
| 未进本 / 未发物 | 坐标与背包同上，未变 | **PASS** |
| 无空壳 tell | 点 P 后 chat **无** `[团本奖励]` | **PASS** |

### 3 · 文案抽查（live lore）

| 项 | live | 判定 |
|----|------|------|
| 周首通护符保底 | `周首通 · 精炼护符 ×1` · lore「每周首次通关保底」· NI `gear_ember_t1_talisman` | **PASS** |
| 锋利石约 50% 非必给 | `锋利石 约 50%` · lore「非必给 · 不是固定 ×1」· `raid_gem` | **PASS** |
| T3 不保底 | `终厅使徒 · T2/T3` · lore「T3 刃 约 8% / T3 符 约 6%」「以上均不保底」 | **PASS** |

### 4 · 禁项 / 静态

| 项 | 证据 | 判定 |
|----|------|------|
| `git diff HEAD -- plugins/DungeonPlus plugins/MythicMobs plugins/CoreRpg/loot.yml` | **空**（0 字节） | **PASS** |
| 体力 / `over_chance*` | 本测未改 `config.yml`；施工 tip 仅 TrMenu+STATUS | **PASS** |
| 父页 P | YAML：`sound: BLOCK_CHEST_OPEN-1-1` + `menu: ember_*_rewards`；**无** tell | **PASS** |
| 预览页 | **无** `corerpg enter` / `command:` | **PASS** |
| 施工范围 | `991e863` 仅 4 TrMenu YAML + 施工 STATUS | **PASS** |

---

## UX

- 路径：`/ember` → 周常 / 团本 → **P 奖励预览** → **B 返回**（菜单点选）  
- **未**教玩家手打 `/trmenu` / `/corerpg enter` / `/ni`；预览页纯展示  
- OP 斜杠仅测号等级/体力/mvtp/deop，不入玩家可见文案  

---

## ops

| 项 | 结果 |
|----|------|
| 测后 `server-runtime/ops.json` | `[]` |
| 测后 `login-runtime/ops.json` | `[]` |
| 临时 OP | `WrOpcft5e` · 已 `deop` |

---

## Blocker

无。

---

## 交付

- [x] 报告：`docs/STATUS-ember-weekly-raid-reward-preview-test.md`
- [x] JSON：`/tmp/weekly-raid-reward-preview-test.json`
- [x] commit **未 push**（交总控代推）
- [x] 总评 PASS · ops 清空
