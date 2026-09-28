# STATUS · B2.45 烬砧灰箍抢口文案 · TrMenu · 薄验收

**日期：** 2026-09-29 04:30 CST（Asia/Shanghai）  
**岗：** 余烬-测试岗执行器  
**依据：** 施工 tip `3066746` · 批准 `docs/STATUS-ember-ash-brace-forge-copy-approve.md`  
**口径：** 仅静态 rg + 菜单悬停轻测；**禁**长测/挑刺；**禁改**配方/NI/四件甲/玩法数值；**勿宣称 B0.1 已清**；本岗零改配置  
**Verdict：** ✅ **PASS**  
**JSON：** `/tmp/ash-brace-contention-copy-light.json`（未入 git）  
**脚本：** `mineflayer-tests/ash-brace-contention-copy-light.js`

---

## 一句话

主句「优先沉铁锭」命中 forge P + part A 共 **2**；旁句「有铁锭先炼灰箍沉底」命中 part I **1**；消耗 ×12/×2 仍在；`part.yml` 相对 tip **ZERO**；菜单悬停可见；ops=[]。

---

## 总评

| 验收点 | 结果 |
|--------|------|
| 1a. rg `优先沉铁锭` = 2（forge P + part A） | **PASS** |
| 1b. rg `有铁锭先炼灰箍沉底` = 1（part I） | **PASS** |
| 1c. 消耗 ×12/×2 行仍在 | **PASS** |
| 1d. `plugins/CoreRpg/part.yml` 本窗无 diff | **PASS**（相对 `3066746` ZERO） |
| 2. 菜单：锻炉「炼部件」悬停主句 → part 灰箍主句 + 说明旁句 | **PASS** |
| ops=[] · 临时 OP deop | **PASS** |
| 硬禁（零改配置 / 勿宣称 B0.1 / 不长测） | **PASS** |

---

## 环境

| 项 | 值 |
|----|-----|
| 工作区 | `/workspace/minecraft` |
| 施工 tip | `3066746` feat(trmenu): B2.45 ash-brace forge copy — prioritize iron sink lore |
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |
| 口 | proxy 25565 / login 25566 / play 25567 |
| 服 | 已起；TrMenu 04:27:28 CST 自动热载 `ember_forge.yml` / `ember_part.yml` |
| 账号 | 验收 `AvLtB245`（非 OP）· 辅助 `AvOpB245`（临时 OP，已 deop） |
| UX | `/ember` → 锻炉 → **炼部件**（不依赖手打命令教学句） |

---

## 各点证据

### 1 · 静态 rg

```
优先沉铁锭 → 2
  plugins/TrMenu/menus/ember_forge.yml:72  （P 炼部件 lore）
  plugins/TrMenu/menus/ember_part.yml:30   （A 余烬灰箍 lore）

有铁锭先炼灰箍沉底 → 1
  plugins/TrMenu/menus/ember_part.yml:60   （I 说明 lore）

消耗
  forge P: 「灰箍：碎片×12 + 余烬铁锭×2」
  part A:  「余烬碎片 ×12」·「余烬铁锭 ×2」

part.yml
  git diff 3066746 -- plugins/CoreRpg/part.yml → 空（ZERO）
```

**PASS**

### 2 · 菜单轻测所见摘要

1. `/ember` →「余烬 · 冒险枢纽」→ 点锻炉 →「余烬锻炉 · 锻造」
2. 槽 22「**炼部件**」悬停 lore：
   - `有铁锭时：碎片优先沉铁锭炼灰箍`（主句）
   - `灰箍：碎片×12 + 余烬铁锭×2`（消耗）
3. 点炼部件 →「烬砧 · 炼部件」
4. 「**余烬灰箍**」悬停：主句同左 + 消耗 ×12/×2
5. 「**说明**」（槽 31）悬停：`强化另用碎片；有铁锭先炼灰箍沉底`（旁句）

未手打 `/corerpg` 教学路径；未做炼制/副手长测。

**PASS**

### ops

`server-runtime/ops.json` = `[]` · `login-runtime/ops.json` = `[]` · `deop AvOpB245` 已执行。

---

## 阻塞点

无。

---

## 交总控

- **总评：** PASS
- **施工 tip SHA：** `3066746`
- **测报 tip short SHA：** `d6768d9`
- **是否已 push：** 是（`d6768d9` → origin/main）
- **报告路径：** `docs/STATUS-ember-ash-brace-contention-copy-test.md`
- **ops：** `[]`
- **rg 计数证据：** 优先沉铁锭=2 · 有铁锭先炼灰箍沉底=1 · part.yml ZERO · ×12/×2 保留
- **菜单所见：** forge P / part A 主句可见；part I 旁句可见
