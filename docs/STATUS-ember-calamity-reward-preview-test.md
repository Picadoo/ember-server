# STATUS · 灾厄奖励预览真分页 A · 测岗

**日期：** 2026-09-28 22:45 → 22:46 Asia/Shanghai（CST）  
**岗：** 余烬-测试岗执行器  
**依据：** `docs/design-ember-calamity-reward-preview.md`（批 A）；施工 tip `62f444a` · `docs/STATUS-ember-calamity-reward-preview.md`  
**范围：** TrMenu 真预览子页 `ember_calamity_rewards`（P 入口 · 底栏返回 · 日箱/尾刀文案 · 禁项 diff）  
**禁项：** **禁改配置** · **未 push**（交总控代推）  
**Verdict：** ✅ **PASS**（验收点全过 · ops=`[]`）

---

## 一句话

hub「灾厄」→ 点 **P** 打开 **`灾厄 · 奖励预览`**（非空壳 tell）；底栏 **B** 回 `灾厄 · 余烬灾厄使` 且坐标未变、背包未增物；日箱限 1 / 保护券约 10% / 周首次凝核 / 尾刀必给（非全员）/ T3 各约 1% 不保底文案齐；`calamity.yml`/MM/loot/`over_chance*` diff 空；ops=`[]`。

---

## 总评

| # | 验收点 | 结果 |
|---|--------|------|
| 1 | 灾厄：开 P → `ember_calamity_rewards` 真页 → 底栏返回 `ember_calamity`；未奔赴、未开 DP 测本、未发物 | **PASS** |
| 2 | 文案：日箱限 1；保护卷~10%；周首次凝核；尾刀必给（非全员）；T3 刃/护符各约 1% 不保底 | **PASS** |
| 3 | 禁项：`calamity.yml`/MM/loot/窗期/`over_chance*` diff 空；父 P 无空壳 tell；无全员参战保底 T3 | **PASS** |

---

## 环境

| 项 | 值 |
|----|-----|
| 工作区 | `/workspace/minecraft` |
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |
| 口 | proxy 25565 / login 25566 / play 25567 |
| 施工 tip | `62f444a` feat: calamity reward preview page (TrMenu) |
| TrMenu | **36** 个菜单已加载（22:41:59 CST 热更；含 `ember_calamity_rewards`） |
| 账号 | 验收 `WrPczzt4`（非 OP · Lv.31）· 辅助 `WrOpczzt4`（临时 OP，已 deop） |
| 探针 | `/tmp/calamity-reward-preview-test.js` · JSON `/tmp/calamity-reward-preview-test.json`（未入 git） |
| 坐标 | hub (-18.5, 58, 110.5) 全程未变 |

---

## 各点证据

### 1 · 灾厄动线

```
/ember → 点「灾厄」→ title「灾厄 · 余烬灾厄使」
  → 点 P「奖励预览」(slot 13/22) → title「灾厄 · 奖励预览」
  → 点 B「返回灾厄」(slot 49) → title「灾厄 · 余烬灾厄使」（奔赴/奖励预览/窗口说明仍在）
```

| 检查 | 实测 | 判定 |
|------|------|------|
| 真页标题 | `灾厄 · 奖励预览` | **PASS** |
| 分区可见 | 日箱限1 · 保护券约10% · 周首次凝核 · 尾刀必给 · 尾刀概率 · T3 各约1% · 返回灾厄 | **PASS** |
| 返回父页 | title=`灾厄 · 余烬灾厄使`；S/P/R/T 在 | **PASS** |
| 未奔赴 | 坐标始终 (-18.5,58,110.5) overworld；未点「奔赴灾厄」 | **PASS** |
| 未开 DP | 未点「测试实例（OP）」；无 DP 进本 | **PASS** |
| 未发物 | 背包前后同：刃/护符/面包×16 | **PASS** |
| 无空壳 tell | 点 P 后 chat **无** 旧空壳「公共窗打灾厄使…正式入口是 ember_event」 | **PASS** |

（clickWindow 可能报 transaction timeout，属既有 TrMenu/mineflayer 吞事务现象；**菜单标题与槽位已切换**，以窗口 dump 为准。）

### 2 · 文案抽查（live lore）

| 项 | live | 判定 |
|----|------|------|
| 日箱限 1 | `日箱 · 每日限 1 次` · 核心碎片×2 · 附魔晶×1 · 灾厄余烬×2 · 每日限领 1 次 | **PASS** |
| 保护卷~10% | `保护券 · 约 10%` · 余烬保护券约 10% · 非必给 | **PASS** |
| 周首次凝核 | `周首次日箱 · 凝核 ×1` · 本周首次领取 · 同周不重复 | **PASS** |
| 尾刀必给（非全员） | `尾刀必给 · 最后一击` · 余烬×1 · 凝核×1 · 碎片×2 · **仅最后一击玩家，非全员参战奖励** | **PASS** |
| 尾刀概率 | 锋利石约 30% · 孔石各约 2% · 稳定符约 5% · 外观碎片约 20% | **PASS** |
| T3 不保底 | `T3 刃 / 护符 · 各约 1%` · 均不保底 · 不是全员参战奖励，也不是必掉 | **PASS** |
| 与 calamity.yml | `limit_per_day:1` · 碎片2/晶1/余烬2 · `protect_scroll_chance:0.10` · `weekly_first` 凝核1 | **PASS** |
| 与 MM | 尾刀必给 3 行无概率；T3 blade/talisman `~onDeath 0.01`；锋利 0.3 等 | **PASS** |

### 3 · 禁项 / 静态

| 项 | 证据 | 判定 |
|----|------|------|
| `git diff HEAD -- plugins/CoreRpg/calamity.yml plugins/MythicMobs plugins/CoreRpg/loot.yml` | **空**（0 字节） | **PASS** |
| 窗期 / 奔赴 / `over_chance*` | 工作区 diff 无 `over_chance`；施工 tip 仅 TrMenu+STATUS | **PASS** |
| 父页 P | YAML：`sound: BLOCK_CHEST_OPEN-1-1` + `menu: ember_calamity_rewards`；**无** tell；lore「参战」→「尾刀结算」 | **PASS** |
| 预览页 | **无** `calamity join` / `dp start` / `command:` / `tell:` / 发物 | **PASS** |
| 全员参战保底 T3 | 页内明确「不保底」「非全员参战奖励」；无保底 T3 假写 | **PASS** |
| 施工范围 | `62f444a` 仅 `ember_calamity.yml` + `ember_calamity_rewards.yml` + 施工 STATUS | **PASS** |

---

## UX

- 路径：`/ember` → **灾厄** → **P 奖励预览** → **B 返回灾厄**（菜单点选）  
- **未**教玩家手打 `/trmenu` / `/corerpg calamity join` / `/dp start` / `/ni`；预览页纯展示  
- OP 斜杠仅测号等级/体力/mvtp/deop，不入玩家可见文案  

---

## ops

| 项 | 结果 |
|----|------|
| 测后 `server-runtime/ops.json` | `[]` |
| 测后 `login-runtime/ops.json` | `[]` |
| 临时 OP | `WrOpczzt4` · 已 `deop` |

---

## Blocker

无。

---

## 交付

- [x] 报告：`docs/STATUS-ember-calamity-reward-preview-test.md`
- [x] JSON：`/tmp/calamity-reward-preview-test.json`
- [x] commit **未 push**（交总控代推）
- [x] 总评 PASS · ops 清空
