# STATUS · 挂机二档菜单 UX 冒烟 A

**日期：** 2026-09-28 21:56 CST（Asia/Shanghai）  
**岗：** 余烬-测试岗  
**依据：** 施工 tip `0deda12` · `docs/STATUS-ember-afk-ux-tier2-menu.md` · `docs/design-ember-afk-ux-tier2-menu.md`（批 A）  
**口径：** 菜单 UX 短冒烟；**勿长挂**；**禁改** `afk_caps` / MM / 体力数值  
**Verdict：** ✅ **PASS** · STATUS **未 push**（交总控代推）

---

## 一句话

枢纽挂机 lore 含「达顶后双软顶」；规则 I 可读达顶后再降一档，日顶 150/80/10/2/150/100，无「超过后掉率 25%」单档唯一句 / 裸 0.08 / 封死通胀 / 新增斜杠教学；点规则可见今日进度；`over_chance=0.25` / `over_chance_2=0.08` 未动；进层瞬进瞬出 OK；ops=[]。

---

## 总评

| 验收点 | 结果 |
|--------|------|
| 1. 枢纽挂机入口 lore 含「达顶后双软顶」 | **PASS** |
| 2. 规则 I：双软顶语义 + 日顶数字 + 禁项 | **PASS** |
| 3. 点规则仍能看今日进度 | **PASS** |
| 4. config `over_chance` / `over_chance_2` 未动 | **PASS**（0.25 / 0.08） |
| 5. 可选：任一层瞬进瞬出 | **PASS** |
| ops=[] · 临时 OP deop | **PASS** |
| 禁改 afk_caps / MM / 体力 | **PASS**（本窗零改） |

---

## 环境

| 项 | 值 |
|----|-----|
| 工作区 | `/workspace/minecraft` |
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |
| 口 | proxy 25565 / login 25566 / play 25567 |
| 施工 tip | `0deda12` fix: AFK tier-2 dual softcap menu UX (TrMenu lore) |
| TrMenu 热更 | 日志 `[21:53:10]` 自动重载 `ember_afk.yml` / `ember_hub.yml`；`32 个菜单已加载` |
| 账号 | 验收 `UxAfkb8len`（非 OP）· 辅助 `UxOpb8len`（临时 OP，已 deop） |
| JSON | `/tmp/afk-ux-tier2-menu-smoke.json`（未入 git） |

---

## 各点证据

### 1 · 枢纽挂机入口 lore

进服 `/ember` → 窗标题「余烬 · 冒险枢纽」· 槽 10「挂机庭」lore：

> 刷碎片 / 骨尘（高层有核心碎片），四层共用每日上限 · **达顶后双软顶**

**PASS**

### 2 · 挂机庭 · 规则 I

点「挂机庭」→ 窗「挂机庭 · 分层」· 槽 30「规则与今日上限」live lore：

| 检查 | 实测 | 判定 |
|------|------|------|
| 达顶后再降一档 | `达顶后掉率约两成五；再挂会再降一档` | **PASS** |
| 日顶数字 | `碎片 150 · 骨尘 80 · 核心碎片 10` / `魂尘 2 · 击杀币 150 · 击杀经验 100` | **PASS** |
| 无「超过后掉率 25%」单档唯一句 | live + 静态 YAML 均无 | **PASS** |
| 无裸 `0.08` | live + 静态均无 | **PASS** |
| 无封死通胀 | live + 静态均无；有「长挂收益显著低于满速」 | **PASS** |
| 无新增斜杠教学 | lore 无 `/corerpg`/`/dp`/`/mvtp`；底层 `command: corerpg afk` 保留（与 B0.3 同） | **PASS** |

### 3 · 点规则看今日进度

点「规则与今日上限」→ 触发 `corerpg afk`，chat 含：

> 今日：碎片 **0/150** · 骨尘 **0/80** · 核心碎片 **0/10** · 魂尘 **0/2** · 击杀币 **0/150**

**PASS**

### 4 · config 未动

| 键 | 值 | 证据 |
|----|-----|------|
| `afk_caps.over_chance` | **0.25** | `plugins/CoreRpg/config.yml` 测前=测后 |
| `afk_caps.over_chance_2` | **0.08** | 同上 · 注释 1.15.22 |
| 本 commit / 本窗 | **零改** config / MM / 体力 / jar | 测岗仅读 + 临时 OP + 点按 |

### 5 · 可选进层瞬进瞬出

`mvtp ember_afk` 入（约 -4.5,73,250.5）→ `/hub` 出（约 -18.5,58,110.5）。**PASS**（短样，非长挂）

---

## 禁项核对

| 禁项 | 本窗 |
|------|------|
| 长挂 / 墙钟 ≥2h | **未做**（菜单 UX ~1 min） |
| 改 `over_chance*` / cap | **未改** |
| 改 MM / 体力 / 玩法数 | **未改** |
| 宣称封死通胀 | **无** |
| ops 残留 | **ops=[]** · `De-opped UxOpb8len` |
| git push | **未 push** |

---

## Git

- 仅本 STATUS  
- **未 push** → tip 交总控代推后归档

## ops

`[]`（play + login）
