# STATUS · 日常奖励预览真分页 · 短抽

**日期：** 2026-09-28 08:03 → 08:04 Asia/Shanghai（CST）  
**岗：** 余烬-测试岗执行器  
**依据：** `docs/design/design-ember-daily-reward-preview.md` §7；`docs/status/STATUS-ember-daily-reward-preview.md`；commit `4517021`  
**范围：** TrMenu 真预览子菜单 `ember_daily_rewards`（点 P / 三类分区 / 返回 B / 零 slash / diff 范围）  
**禁项：** **禁改配置** · **未** commit/push  
**Verdict：** **✅ PASS**（五条硬条全过 · ops=`[]`）

---

## 一句话

hub「日常」→ `ember_daily` → 点 **P** 打开 **`日常 · 奖励预览`**（非无反馈）；通关箱 **A/C/I**、Boss **D/E/F**、装备 **X/Y/Z**（约 12%/8%/3%）均可见；点底栏 **B** 回选线页且未进本；玩家路径零业务 slash；`4517021` diff 仅 TrMenu+STATUS；测后 ops=`[]`。

---

## 五条硬条

| # | 硬条 | 结果 | 证据 |
|---|------|------|------|
| 1 | 开 `ember_daily`，点 **P「奖励预览」** → 打开 `ember_daily_rewards`（有声），非无反馈 | **✅ PASS** | live title=`日常 · 奖励预览`；P slot=40；YAML P actions=`sound: BLOCK_CHEST_OPEN-1-1` + `menu: ember_daily_rewards`；预览 Events Open 同声 |
| 2 | 可见三类：通关箱 **A/C/I** · Boss **D/E/F** · 装备 **X/Y/Z**（刃约12% / 符约8% / T1约3%） | **✅ PASS** | 见下「三类 live 槽」；NI id 与概率 lore 齐 |
| 3 | 点底栏 **B** → 回 `ember_daily`，仍可选线；**预览页无进本** | **✅ PASS** | B→title=`日常 · 余烬窟`；线键庭院/焦骨/地窖/潮蚀/断塔仍在；点刃无 enter；预览 YAML **无** `corerpg enter`；坐标未变 (-18.5,110.5) |
| 4 | 全程零手打 slash | **✅ PASS** | 玩家路径：hub 点「日常」→ 点 P → 点 B（`via=hub_click_daily`）；无手打 `/corerpg`/`/dp`/`/trmenu`/`/hub` |
| 5 | 配置 diff 仅 TrMenu（核对落地即可，不改文件） | **✅ PASS** | `4517021` 文件：`ember_daily.yml` · `ember_daily_rewards.yml` · `docs/status/STATUS-ember-daily-reward-preview.md`；无 DP/MM/CoreRpg |

**测后 ops：** play=`[]` · login=`[]` → **✅ PASS**

---

## 环境

| 项 | 值 |
|----|-----|
| 工作区 | `/workspace/minecraft` |
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |
| 端口 | proxy 25565 / login 25566 / play 25567 |
| TrMenu | 现网已 reload · **32** 菜单（含新页；STATUS 记 08:01:38 CST） |
| commit | `4517021` `feat(trmenu): 日常奖励预览真分页 ember_daily_rewards` |
| 登录 | `mineflayer-tests/lib/proxy-login.js` |
| 临时 OP | `DrpOp*`（测后 deop + ops 写空） |
| 玩家 | `DrpP9526` · Lv.10 · hub (-18.5, …, 110.5) |
| JSON | `/tmp/daily-reward-preview-test.json` |
| 日志 | `/tmp/daily-reward-preview-test.log` |

---

## 静态核对（未改文件）

| 检查 | 结果 |
|------|------|
| `ember_daily` **P**：悬停摘要 +「点击打开预览页」+ sound + `menu: ember_daily_rewards` | ✅ |
| `ember_daily_rewards` Layout：`# A C I #` / `# D E F #` / `# X Y Z #` / `####B####` | ✅ |
| A 核心×1 · C 附魔晶×1 · I 碎片×5 + xp+3 · NI id | ✅ |
| D 必掉 · E 风味分线（焦骨/潮蚀/霜晶→碎片；地窖/断塔/锈轨→骨尘；庭院无此行）· F 精英经验 | ✅ |
| X 约12% · Y 约8% · Z 约3% · NI id | ✅ |
| B → `menu: ember_daily`（不回 hub）；全文无 `corerpg enter` | ✅ |
| commit 范围仅上述 TrMenu + STATUS | ✅ |

---

## Live 证据摘要

### 动线

```
OP 开 hub → 玩家点「日常」→ title「日常 · 余烬窟」
  → 点 P「奖励预览」(slot 40) → title「日常 · 奖励预览」
  → 点 B「返回日常选线」(slot 49) → title「日常 · 余烬窟」（五线仍在）
```

（clickWindow 可能报 transaction timeout，属既有 TrMenu/mineflayer 吞事务现象；**菜单标题与槽位已切换**，以窗口 dump 为准。）

### 三类 live 槽（strip 后）

| 区 | slot | name | lore 要点 |
|----|------|------|-----------|
| 通关箱 A | 20 | 余烬核心碎片 ×1 | 通关箱 · NI `mat_ember_core_fragment` |
| 通关箱 C | 22 | 余烬附魔晶 ×1 | NI `crystal_ember_enchant` |
| 通关箱 I | 24 | 余烬碎片 ×5 | 另附原版经验 **+3 级** · NI `mat_ember_shard` |
| Boss D | 29 | Boss 核心碎片 ×1（必掉） | NI `mat_ember_core_fragment` |
| Boss E | 31 | **风味小料 50%** | 焦骨/潮蚀/霜晶→碎片；地窖/断塔/锈轨→骨尘；**庭院无此行** |
| Boss F | 33 | 精英击杀经验 | daily_clear 另计 |
| 装备 X | 38 | 余烬之刃 | **约 12%** · NI `gear_ember_blade` |
| 装备 Y | 40 | 余烬护符 | **约 8%** · NI `gear_ember_charm` |
| 装备 Z | 42 | 余烬精炼之刃 | **约 3%** · NI `gear_ember_t1_blade` |
| 返回 B | 49 | 返回日常选线 | → `ember_daily` |

页头 H(slot13) lore 含「风味小料分线」——初跑断言曾误把 H 当 E；**live 槽31 确有风味图标**，已按 name-first 重判为 PASS（见 JSON `hard2_note`）。

### 有声

客户端音效无法由 bot 直接采样；以 YAML **P actions** + 预览 **Events.Open** 均含 `BLOCK_CHEST_OPEN-1-1`，且点击后菜单**确已打开**为证（符合设计「有声即可」）。

---

## ops / 禁项

| 项 | 结果 |
|----|------|
| 测后 `server-runtime/ops.json` | `[]` |
| 测后 `login-runtime/ops.json` | `[]` |
| 改配置 | **无** |
| commit / push | **无**（报告落盘交总控） |

---

## Blocker

无。

---

## 交付

- [x] 报告：`docs/status/STATUS-ember-daily-reward-preview-test.md`
- [x] JSON：`/tmp/daily-reward-preview-test.json`
- [x] 五条硬条结论明确 · ops 清空
