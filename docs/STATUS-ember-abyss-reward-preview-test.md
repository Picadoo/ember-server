# STATUS · 深渊奖励预览真分页 A · 测岗

**日期：** 2026-09-28 22:36 → 22:37 Asia/Shanghai（CST）  
**岗：** 余烬-测试岗执行器  
**依据：** `docs/design-ember-abyss-reward-preview.md`（批 A）；施工 tip `4805c11` · `docs/STATUS-ember-abyss-reward-preview.md`  
**范围：** TrMenu 真预览子页 `ember_abyss_rewards`（P 入口 · 底栏返回 · 三档/COMPLETE/L12 文案 · 禁项 diff）  
**禁项：** **禁改配置** · **未 push**（交总控代推）  
**Verdict：** ✅ **PASS**（验收点全过 · ops=`[]`）

---

## 一句话

hub「深渊」→ 点 **P** 打开 **`深渊 · 奖励预览`**（非空壳 tell）；底栏 **B** 回 `深渊 · 无尽层` 且坐标未变、背包未增物；三档数量 / 孔石约 10% 非必给 / COMPLETE T2 周首通 / L12 稳定符周首通文案齐；`abyss.yml`/MM/loot/DP/`over_chance*` diff 空；ops=`[]`。

---

## 总评

| # | 验收点 | 结果 |
|---|--------|------|
| 1 | 深渊：开 P → `ember_abyss_rewards` 真页 → 底栏返回 `ember_abyss`；未进本、未撤离、未发物 | **PASS** |
| 2 | 文案：三档数量（1～4 / 5～9 / 10+）；孔石约 10% 非必给；COMPLETE T2 周首通；L12 稳定符周首通 | **PASS** |
| 3 | 禁项：无一层一页；`abyss.yml`/MM/loot/DP/体力/`over_chance*` diff 空；父 P 无空壳 tell | **PASS** |

---

## 环境

| 项 | 值 |
|----|-----|
| 工作区 | `/workspace/minecraft` |
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |
| 口 | proxy 25565 / login 25566 / play 25567 |
| 施工 tip | `4805c11` feat: abyss reward preview page (TrMenu) |
| TrMenu | **35** 个菜单已加载（22:34:40 CST 热更） |
| 账号 | 验收 `WrPcp5au`（非 OP · Lv.31）· 辅助 `WrOpcp5au`（临时 OP，已 deop） |
| 探针 | `/tmp/abyss-reward-preview-test.js` · JSON `/tmp/abyss-reward-preview-test.json`（未入 git） |
| 坐标 | hub (-18.5, 58, 110.5) 全程未变 |

---

## 各点证据

### 1 · 深渊动线

```
/ember → 点「深渊」→ title「深渊 · 无尽层」
  → 点 P「奖励预览」(slot 13) → title「深渊 · 奖励预览」
  → 点 B「返回深渊」(slot 49) → title「深渊 · 无尽层」（开始下潜/奖励预览/上浮撤离仍在）
```

| 检查 | 实测 | 判定 |
|------|------|------|
| 真页标题 | `深渊 · 奖励预览` | **PASS** |
| 分区可见 | 档1～4 / 5～9 / 10+ · COMPLETE T2 · L12 稳定符 · 战斗摘要 · 返回深渊 | **PASS** |
| 返回父页 | title=`深渊 · 无尽层`；S/E/P/R 在 | **PASS** |
| 未进本 | 坐标始终 (-18.5,58,110.5) overworld | **PASS** |
| 未撤离 | 未点 E；坐标未变；无撤离结算 chat | **PASS** |
| 未发物 | 背包前后同：刃/护符/面包×16 | **PASS** |
| 无空壳 tell | 点 P 后 chat **无** 空壳 tell | **PASS** |

（clickWindow 可能报 transaction timeout，属既有 TrMenu/mineflayer 吞事务现象；**菜单标题与槽位已切换**，以窗口 dump 为准。）

### 2 · 文案抽查（live lore）

| 项 | live | 判定 |
|----|------|------|
| 档 1～4 | `档 1～4 · 基础结算` · 碎片×8 · 骨尘×2 · 核心×0 · 锋利石×0 | **PASS** |
| 档 5～9 | `档 5～9 · 进阶结算` · 碎片×12 · 骨尘×4 · 核心×1 · 锋利石约 10%（非必给） | **PASS** |
| 档 10+ | `档 10+ · 本期顶 12` · 碎片×16 · 骨尘×6 · 核心×2 · 锋利石约 10%（非必给） | **PASS** |
| COMPLETE T2 | `COMPLETE · T2 周首通` · 本周首次 T2 刃+护符×1 · 再通刃约 4% / 符约 3% | **PASS** |
| L12 稳定符 | `L12 · 稳定符周首通` · 本周首次达第 12 层稳定符×1 · 同周再达静默 | **PASS** |
| 与 abyss.yml | settle 1～4/5～9/10+ 与 `gem_chance: 0.1` 一致 | **PASS** |

### 3 · 禁项 / 静态

| 项 | 证据 | 判定 |
|----|------|------|
| `git diff HEAD -- plugins/CoreRpg/abyss.yml plugins/MythicMobs plugins/CoreRpg/loot.yml plugins/DungeonPlus` | **空**（0 字节） | **PASS** |
| 体力 / `over_chance*` | 工作区 diff 无 `over_chance`；施工 tip 仅 TrMenu+STATUS | **PASS** |
| 父页 P | YAML：`sound: BLOCK_CHEST_OPEN-1-1` + `menu: ember_abyss_rewards`；**无** tell | **PASS** |
| 预览页 | **无** `corerpg enter` / `abyss evacuate` / `command:` / `tell:` | **PASS** |
| 一层一页 | 三档同屏；无 L1～L12 翻页 | **PASS** |
| 施工范围 | `4805c11` 仅 `ember_abyss.yml` + `ember_abyss_rewards.yml` + 施工 STATUS | **PASS** |

---

## UX

- 路径：`/ember` → **深渊** → **P 奖励预览** → **B 返回深渊**（菜单点选）  
- **未**教玩家手打 `/trmenu` / `/corerpg enter` / `/corerpg abyss evacuate` / `/ni`；预览页纯展示  
- OP 斜杠仅测号等级/体力/mvtp/deop，不入玩家可见文案  

---

## ops

| 项 | 结果 |
|----|------|
| 测后 `server-runtime/ops.json` | `[]` |
| 测后 `login-runtime/ops.json` | `[]` |
| 临时 OP | `WrOpcp5au` · 已 `deop` |

---

## Blocker

无。

---

## 交付

- [x] 报告：`docs/STATUS-ember-abyss-reward-preview-test.md`
- [x] JSON：`/tmp/abyss-reward-preview-test.json`
- [x] commit **未 push**（交总控代推）
- [x] 总评 PASS · ops 清空
