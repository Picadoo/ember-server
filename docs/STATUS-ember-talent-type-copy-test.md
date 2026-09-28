# STATUS · B2.16 天赋菜单类型文案 A · 测试验收

| 字段 | 值 |
|------|-----|
| 时间 | 2026-09-29 00:43 Asia/Shanghai |
| 岗 | 余烬-测试岗执行器 |
| 施工 tip | `39ca8f6`（设计 `34e61dd` · 批准 `7c1c408`） |
| 范围 | 仅 `plugins/TrMenu/menus/ember_talent.yml` 十三行类型 lore；方案 B（cost→消耗）**未做** |
| Verdict | **✅ PASS · 勾销** |
| JSON | `/tmp/b216-talent-type-copy-smoke.json`（未入 git） |

---

## 一句话

**PASS：** `ember_talent` 类型 lore 已无 `passive`/`skill`：根×3「被动 · cost 2」、烬斩「主动技 · cost 4」（无 skill）、二层×9「被动 · cost 3|4」；cost 数字原样；方案 B（cost→消耗）未做；相对施工 tip 仅目标菜单十三行 + 施工 STATUS；`talent.yml` / unlock 实参 / 其它菜单 **零 diff**；枢纽→天赋冒烟绿；**ops=[]**；本 commit **未 push**（交总控代推若本岗 push 失败）。**不**宣称 B0.1 已清。

---

## 硬条总表

| # | 验收点 | 结果 | 证据 |
|---|--------|------|------|
| 1 | 静态：`rg -n 'passive\|skill' plugins/TrMenu/menus/ember_talent.yml` = 0 | ✅ **PASS** | exit 1 · 0 命中 |
| 2 | 目视/冒烟：根×3「被动 · cost 2」；烬斩「主动技 · cost 4」无 skill；二层×9「被动 · cost 3\|4」 | ✅ **PASS** | 静态计数 + live 槽位 lore；见下 |
| 3 | cost 数字未改；方案 B（cost→消耗）未做 | ✅ **PASS** | tip hunk 仅类型词替换；`rg 消耗` = 0 |

禁项自检：本岗 **未改** YAML；**不**宣称 B0.1 已清；**未**带 dirty runtime 进 commit；测后 **ops=[]**；临时 OP 已 `deop`（`00:43:24` Asia/Shanghai De-opped `B216oph7kmf`）。

---

## 静态明细

```text
$ rg -n 'passive|skill' plugins/TrMenu/menus/ember_talent.yml
# 无输出 · exit 1 · 0 命中

$ rg -n '被动|主动技|cost|消耗' plugins/TrMenu/menus/ember_talent.yml
97:        - '§7根节点 · 被动 · cost 2'
112:        - '§7根节点 · 被动 · cost 2'
127:        - '§7根节点 · 被动 · cost 2'
142:        - '§7主动技 · cost 4'
150:        - 'tell: §8需前置节点 · 主动技烬斩'
178:        - '§7二层 · 被动 · cost 3'
195:        - '§7二层 · 被动 · cost 3'
213:        - '§7二层 · 被动 · cost 4'
232:        - '§7二层 · 被动 · cost 3'
249:        - '§7二层 · 被动 · cost 3'
267:        - '§7二层 · 被动 · cost 4'
286:        - '§7二层 · 被动 · cost 3'
303:        - '§7二层 · 被动 · cost 3'
321:        - '§7二层 · 被动 · cost 4'

计数：根「被动 · cost 2」×3；「主动技 · cost 4」×1（无 skill）；二层 cost3×6 + cost4×3；「消耗」×0。
```

热更旁证（施工 STATUS · Asia/Shanghai）：`00:39:57` 自动重载 `ember_talent.yml`（14ms）；`00:40:00` 36 菜单加载完成。live=`server-runtime/plugins/TrMenu/menus/ember_talent.yml` 与 `plugins/` 同内容（size 8350 · `diff -q` 一致）。

方案 B 旁证（**故意未做 · PASS 条件**）：文件仍保留英文 `cost`，无「消耗」。

---

## 结构零 diff

相对施工 tip `39ca8f6`（父 `7c1c408`）：

| 路径 | 变更 |
|------|------|
| `plugins/TrMenu/menus/ember_talent.yml` | **仅** 13 行类型 lore：`passive`→`被动`；烬斩去掉 ` · skill`（13−/13+）；**无** cost 数字 / unlock / requires / stats / 方案 B |
| `docs/STATUS-ember-talent-type-copy.md` | 施工 STATUS |

未变（禁项核对）：

- `plugins/CoreRpg/talent.yml`：**零 diff**（tip 窗口未触及）
- unlock：`command: corerpg talent unlock …` — tip diff **无** unlock/command 行
- 其它 TrMenu 菜单：**零 diff**（tip `name-only` 仅 `ember_talent.yml` + 施工 STATUS）

---

## 冒烟

环境：proxy 25565 / login 25566 / play 25567；`JAVA_HOME=…/jdk8u504-b01`；账号 `B216th7kmf`（非 OP）；辅助 `B216oph7kmf`（console FIFO `op` → `mvtp` 枢纽 → LP unset + 测后 `deop`；ops 终态 `[]`）。

**UX 路径（玩家口径）：** 枢纽菜单 → 天赋（禁教玩家斜杠；bot 侧仅用 `/ember` 开枢纽）。

| 抽检 | 结果 | 玩家可见（去色）类型行 |
|------|------|------------------------|
| 开窗 title | ✅ | `余烬 · 天赋` |
| 烬刃 · 燃锋 | ✅ | `根节点 · 被动 · cost 2` |
| 灰行 · 灰踪 | ✅ | `根节点 · 被动 · cost 2` |
| 守墓 · 守碑 | ✅ | `根节点 · 被动 · cost 2` |
| 烬刃 · 烬斩 | ✅ | `主动技 · cost 4`（无 skill） |
| 烬刃 · 余烬脉 / 炽脉 / 再燃 | ✅ | `二层 · 被动 · cost 3` / `3` / `4` |
| 灰行 · 灰纱 / 细尘 / 掩息 | ✅ | `二层 · 被动 · cost 3` / `3` / `4` |
| 守墓 · 叠壁 / 护冢 / 誓碑 | ✅ | `二层 · 被动 · cost 3` / `3` / `4` |

JSON：`/tmp/b216-talent-type-copy-smoke.json` · `verdict=PASS` · fails=`[]`。

---

## ops 终态

- `server-runtime/ops.json` = **`[]`**
- `login-runtime/ops.json` = **`[]`**
- stdout：`00:43:24` De-opped `B216oph7kmf`
