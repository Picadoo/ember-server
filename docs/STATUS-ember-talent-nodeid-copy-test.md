# STATUS · B2.14 天赋菜单 §8 `nodeId`/前置人话化 A · 测试验收

| 字段 | 值 |
|------|-----|
| 时间 | 2026-09-29 00:22 Asia/Shanghai |
| 岗 | 余烬-测试岗执行器 |
| 施工 tip | `37c4bd6`（设计 `9e129cc` · 批准 `1460fa0`） |
| 范围 | 仅 `plugins/TrMenu/menus/ember_talent.yml`：删 13×`§8nodeId:` + 7×前置人话；方案 B / 裸属性键 **未做** |
| Verdict | **✅ PASS · 勾销** |
| JSON | `/tmp/b214-talent-nodeid-copy-smoke.json`（未入 git） |

---

## 一句话

**PASS：** `ember_talent` 玩家可见 lore 已无 `§8nodeId:` 灰字，7 条前置为人话（燃眼+饮烬 / 余烬脉 / 灰纱 / 叠壁）；相对施工 tip 仅目标菜单文案 + 施工 STATUS；`talent.yml` / unlock 实参 / 其它菜单 **零 diff**；枢纽→天赋冒烟绿；**ops=[]**；本 commit **未 push**。**不**宣称 B0.1 / 裸属性键 / NI id 已清。

---

## 硬条总表

| # | 验收点 | 结果 | 证据 |
|---|--------|------|------|
| 1 | 静态：`rg '§8nodeId:'` = 0；`rg '§8前置：.*(blaze_\|ash_\|warden_)'` = 0；中文前置（燃眼/饮烬/余烬脉/灰纱/叠壁）命中 7 | ✅ **PASS** | 见下静态明细 |
| 2 | 禁项：相对 `37c4bd6`，`talent.yml` / unlock 实参 / 其它菜单 **零 diff**（tip 仅文案） | ✅ **PASS** | tip `name-only` 仅 `ember_talent.yml` + 施工 STATUS；无 unlock/command hunk |
| 3 | 冒烟：开 `ember_talent`，根节点无 nodeId 灰字；烬斩前置「燃眼 + 饮烬」；二层悬停无英前置 | ✅ **PASS** | `B214tggugy` · JSON |

禁项自检：本岗 **未改** YAML；**不**宣称 B0.1 / 裸属性键 / NI id 已清；**未**带 dirty runtime 进 commit；测后 **ops=[]**；临时 OP 已处理（`Could not de-op`＝从未入 ops；LP unset 已发）。

---

## 静态明细

```text
$ rg -n '§8nodeId:' plugins/TrMenu/menus/ember_talent.yml
# 无输出 · exit 1 · 0 命中

$ rg -n '§8前置：.*(blaze_|ash_|warden_)' plugins/TrMenu/menus/ember_talent.yml
# 无输出 · exit 1 · 0 命中

$ rg -n '§8前置：.*(燃眼|饮烬|余烬脉|灰纱|叠壁)' plugins/TrMenu/menus/ember_talent.yml
143:        - '§8前置：燃眼 + 饮烬'     # ×1
197 / 215:  - '§8前置：余烬脉'         # ×2
251 / 269:  - '§8前置：灰纱'           # ×2
305 / 323:  - '§8前置：叠壁'           # ×2
# 合计 7
```

热更旁证（施工 STATUS · Asia/Shanghai）：`00:18:52` 自动重载 `ember_talent.yml`（6ms）；`00:19:36` `36 个菜单已加载`（38ms）。live=`server-runtime/plugins/TrMenu/menus/ember_talent.yml` 与 `plugins/` 同内容（mtime `00:18` · size 8457 · `diff -q` 一致）。

方案 B 旁证（**不得宣称已清**）：同文件仍有裸属性键 lore（`phys_damage` / `crit_*` / `max_health` 等 9 行）。

---

## 结构零 diff

相对施工 tip `37c4bd6`（父 `1460fa0`）：

| 路径 | 变更 |
|------|------|
| `plugins/TrMenu/menus/ember_talent.yml` | **仅** 13 整行删 `§8nodeId:` + 7 前置英→中（27 行 hunk：7+/20−）；**无** `command:` / cost / requires / stats / 裸属性键进入 hunk |
| `docs/STATUS-ember-talent-nodeid-copy.md` | 施工 STATUS |

未变（禁项核对）：

- `plugins/CoreRpg/talent.yml`：**零 diff**（tip 窗口未触及）
- unlock：`command: corerpg talent unlock …` — tip diff **无** unlock/command 行
- 其它 TrMenu 菜单：**零 diff**（tip `name-only` 仅 `ember_talent.yml` + 施工 STATUS）
- 父提交对照：删前曾有 13×`§8nodeId:` + 7×英前置（`git show 37c4bd6^` 计数确认）

---

## 冒烟

环境：proxy 25565 / login 25566 / play 25567；`JAVA_HOME=…/jdk8u504-b01`；账号 `B214tggugy`（非 OP）；辅助 `B214opggugy`（console FIFO `op` 尝试 → `mvtp` 枢纽 → 测后 LP unset + `deop`；ops 终态 `[]`）。

**UX 路径（玩家口径）：** 枢纽菜单 → 天赋（禁教玩家斜杠；bot 侧仅用 `/ember` 开枢纽）。

| 抽检 | 结果 | 玩家可见（去色） |
|------|------|------------------|
| 开窗 title | ✅ | `余烬 · 天赋` |
| 根 · 燃锋 / 灰踪 / 守碑 | ✅ | lore 无 `nodeId:`；仅「根节点 · passive · cost 2」+ 点击解锁 |
| 烬刃 · 烬斩 | ✅ | `前置：燃眼 + 饮烬`；无英前置 / 无 nodeId |
| 二层 · 余烬脉 / 灰纱 / 叠壁 | ✅ | 无 nodeId；无英前置 |
| 二层 · 炽脉 / 再燃 | ✅ | `前置：余烬脉`；无英前置 / 无 nodeId |
| 二层 · 细尘 / 掩息 | ✅ | `前置：灰纱`；无英前置 / 无 nodeId |
| 二层 · 护冢 / 誓碑 | ✅ | `前置：叠壁`；无英前置 / 无 nodeId |

旁证：二层 lore 仍可见裸属性键（`phys_damage` 等，方案 B 范围外 · **不得宣称已清**）。

---

## ops 终态

- `server-runtime/ops.json` = **`[]`**
- `login-runtime/ops.json` = **`[]`**
- 临时辅助 `B214opggugy`：LP `corerpg.admin` unset；console `deop` 回报 `Could not de-op`（从未写入 ops）；终态空数组

---

## 债 / 范围外

| 项 | 说明 |
|----|------|
| 方案 B · 裸属性键 | **未做**；同文件仍有 `phys_damage` 等；不得宣称已清 |
| B0.1 | **未测、未宣称** |
| NI id / 套装 id 灰字 | **未测、未宣称**（另窗） |
| dirty runtime | 工作区仍有现网脏改；本 commit **仅**本 STATUS，未纳入 |

---

## 回报主代理 / 总控

- **总评：** **PASS**
- **各点：** 1 静态 PASS · 2 禁项零 diff PASS · 3 冒烟 PASS
- **diff：** tip `37c4bd6` = `ember_talent.yml` 13 删 + 7 前置人话 + 施工 STATUS；本验收 commit = 本 STATUS
- **报告：** `docs/STATUS-ember-talent-nodeid-copy-test.md`
- **ops：** `[]`
- **阻塞点：** 无
- **UX：** 枢纽菜单 → 天赋；禁教玩家斜杠
