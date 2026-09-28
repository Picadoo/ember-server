# STATUS · B2.11 主线 quest「深渊票」两句 A · 验收

**日期：** 2026-09-28 23:52 CST（Asia/Shanghai）  
**岗：** 余烬-测试岗  
**依据：** 施工 tip `8605d10` · 批准 `ca68b64` · 设计 `4a2a671` · 方案 A  
**口径：** 静态 rg + YAML 结构比对 + 卷4/卷8 抽检；**禁改**玩法 YAML；**不宣称** B0.1 / 灾厄 OP #6 已清  
**Verdict：** ✅ **PASS** · 报告仅本地 commit · **未 push**（交总控代推）

---

## 一句话

live+src 已无「深渊票|票还是一天一张」；Q1 hint「耗体力」、Q2 done「体力日回，能走多深就走多深。」双路径一致；步骤结构/items/cost/进本/TrMenu/灾厄 OP 相对 tip 零 diff；ops=[]。

---

## 总评

| 验收点 | 结果 |
|--------|------|
| 1. live+src `rg '深渊票\|票还是一天一张'` → EMPTY | **PASS** |
| 2. Q1 hint「耗体力」、Q2 done「体力日回，能走多深就走多深。」双路径一致 | **PASS** |
| 3. 步骤结构/items/cost/进本/TrMenu/灾厄 OP 相对 tip 零 diff | **PASS** |
| 4. 可选：抽卷4 hint / 卷8 灰烛 done | **PASS**（静态抽检） |
| ops=[] · 临时 OP deop | **PASS**（全程未抬 OP） |
| B0.1 / 灾厄 OP #6 | **不计本轮**（仍残留，未宣称已清） |

---

## 环境

| 项 | 值 |
|----|-----|
| 工作区 | `/workspace/minecraft` |
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |
| 口 | proxy 25565 / login 25566 / play 25567（均 LISTEN） |
| 施工 tip | `8605d10` fix(quest): B2.11 abyss ticket copy→stamina (dual-path) |
| 热更 | 施工 STATUS：`corerpg reload` @ **23:49:26–27 CST**（`Quest: 10 chapters loaded`；`配置已重载 (storage=mysql)`） |
| 验收方式 | 只读；未改任何 YAML |

---

## 各点证据

### 1 · 旧假票串 EMPTY

```
rg '深渊票|票还是一天一张' plugins/CoreRpg/quest.yml CoreRpg/src/main/resources/quest.yml
→ EXIT 1 · 无命中
```

### 2 · Q1 / Q2 新文案 · 双路径一致

| # | 位置 | 现网 | live==src |
|---|------|------|-----------|
| Q1 | 卷4 kill hint · L180 | `打开枢纽菜单 → 深渊（耗体力）· 本次结算也算完成` | **是**（`diff` EXIT 0） |
| Q2 | 卷8 talk done · L335 | `§6灰烛：§f体力日回，能走多深就走多深。` | **是** |

与批准表 `ca68b64` / 设计 `4a2a671` 全文一致。`python` 全文件 `yaml.safe_load`：**live == src**。

### 3 · 禁项相对 tip 零 diff

- tip `8605d10` 相对 parent 仅 3 文件：双路径 `quest.yml` 各 2 行字符串替换 + 施工 STATUS；hunk 无非 `hint`/`done` 行。
- 工作树 `plugins/CoreRpg/quest.yml` · `CoreRpg/src/.../quest.yml` **相对 tip `8605d10` 零 diff**。
- 步骤结构（`type`/`event`/`count`/`mobs`/`complete_on`/`floor`/`xp`/`items`）python 比对 tip parent：**相等**；hint/done/desc 置空后结构亦相等。
- `plugins/TrMenu` · `plugins/CoreRpg/cash.yml` · `plugins/DungeonPlus/**`（含 EmberCalamity）**相对 tip 零 diff**（本验收未触碰）。
- tip patch **无** 新增斜杠教学。

### 4 · 可选抽检（卷4 / 卷8）

- 卷4「深渊回响」首步 kill：hint 已为「深渊（耗体力）」；`type/mobs/count/complete_on/xp/items` 未变。
- 卷8「更深的井」首步 talk（灰烛）：done 已为「体力日回，能走多深就走多深。」；`type/hint/xp` 未变。
- 未做进本/NPC 实机冒烟（可选；静态抽检已覆盖批准句）。

### ops

测前/测后 `server-runtime/ops.json` = `[]` · `login-runtime/ops.json` = `[]` · 全程未临时 OP。

### 残余（本轮不宣称已清）

| 项 | 证据 |
|----|------|
| 灾厄 OP #6 | `plugins/DungeonPlus/dungeon/EmberCalamity/option.yml` L17 仍 `…公共窗口：/ember → 灾厄`（方案 B，未批本轮） |
| B0.1 / NI 票显示名 | 未动、未验清 |
| `cash.yml` `ticket_*` / `free_tickets` 键名 | 配置键保留属 S0 既有；本窗仅 quest 玩家可见文案 |

---

## Diff 摘要（相对 tip parent `ca68b64` 的施工 tip）

| 文件 | 变更性质 |
|------|----------|
| `plugins/CoreRpg/quest.yml` | **仅** Q1 hint + Q2 done 两处字符串 |
| `CoreRpg/src/main/resources/quest.yml` | 同上（双路径同 diff） |
| `docs/STATUS-ember-quest-abyss-ticket-copy.md` | 施工 STATUS（既有 tip） |

本验收 **未** 改任何玩法 YAML。

---

## 版本控制

- 本验收 STATUS：`docs/STATUS-ember-quest-abyss-ticket-copy-test.md`
- 测试 commit：**本地** · **未 push** · tip 交总控代推  
- 建议 message：`test(quest): B2.11 abyss ticket copy A PASS`
