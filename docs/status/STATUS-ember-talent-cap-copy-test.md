# STATUS · B2.13 天赋菜单 `*_cap` 人话化 A · 测试验收

| 字段 | 值 |
|------|-----|
| 时间 | 2026-09-29 00:12 Asia/Shanghai |
| 岗 | 余烬-测试岗执行器 |
| 施工 tip | `89b7432`（设计 `c86ebd6` · 批准 `aa4a7bb`） |
| 范围 | 仅 `plugins/TrMenu/menus/ember_talent.yml` 十四处玩家可见字符串；§8 方案 B 未做 |
| Verdict | **✅ PASS · 勾销** |
| JSON | `/tmp/b213-talent-cap-copy-smoke.json`（未入 git） |

---

## 一句话

**PASS：** `ember_talent` 玩家可见串已无人话禁词 `*_cap`，改为「燎原 / 烟幕 / 永护」及二层「一层顶「…」」；相对施工 tip 仅目标菜单文案 + 施工 STATUS；`talent.yml` / unlock 实参 / §8 `nodeId:`·前置 / 其它菜单 **零 diff**；枢纽→天赋冒烟绿；**ops=[]**；本 commit **未 push**。**不**宣称 B0.1 或 §8 已清。

---

## 硬条总表

| # | 验收点 | 结果 | 证据 |
|---|--------|------|------|
| 1 | 静态：`rg '\*_cap'` = 0；出现「燎原 / 烟幕 / 永护」及二层「一层顶「…」」 | ✅ **PASS** | 见下静态明细 |
| 2 | 禁项：相对 `89b7432`，talent.yml 键/cost/requires/stats、unlock 实参、§8 nodeId/前置、其它菜单 **零 diff** | ✅ **PASS** | tip 仅 ember_talent 文案 14↔14 + 施工 STATUS |
| 3 | 冒烟：开 `ember_talent`（枢纽→天赋），Open tell / 解锁说明 / 规则速览 / 烬刃·灰行·守墓二层 lore 无 `*_cap` | ✅ **PASS** | `B213tg39u5` · JSON |

禁项自检：本岗 **未改** YAML；**不**宣称 B0.1 或 §8 已清；**未**带 dirty runtime 进 commit；测后 **ops=[]**；临时 OP 已 deop。

---

## 静态明细

### `*_cap` 扫描

```text
$ rg -n '\*_cap' plugins/TrMenu/menus/ember_talent.yml
# 无输出 · exit 1 · 0 命中
```

### 目标口径

| 口径 | 命中（节选） |
|------|-------------|
| 燎原 / 烟幕 / 永护 | Open L23 · U lore L64 · U tell L70 · 规则 L166 · 规则 tell L173 |
| 一层顶「燎原」 | 二层 a/b/c（烬刃 · 余烬脉/炽脉/再燃） |
| 一层顶「烟幕」 | 二层 d/e/f（灰行 · 灰纱/细尘/掩息） |
| 一层顶「永护」 | 二层 g/h/i（守墓 · 叠壁/护冢/誓碑） |

热更旁证（施工 STATUS · Asia/Shanghai）：`00:09:24` 自动重载 `ember_talent.yml`；`00:09:34` `36 个菜单已加载`。live=`server-runtime/plugins/TrMenu/menus/ember_talent.yml` 与 `plugins/` 同内容（mtime `00:09`）。

§8 仍在（方案 A 未清，**不得宣称已清**）：`§8nodeId:` / `§8前置：` 行相对 tip **字节级相同**。

---

## 结构零 diff

相对 `aa4a7bb..89b7432`（施工 tip 相对批准）：

| 路径 | 变更 |
|------|------|
| `plugins/TrMenu/menus/ember_talent.yml` | **仅** 14 处玩家可见 lore/tell：`*_cap` → 人话（14−/14+）；**无** `command:` / cost / requires / stats / `§8nodeId` / `§8前置` 行进入 hunk |
| `docs/status/STATUS-ember-talent-cap-copy.md` | 施工 STATUS |

未变（禁项核对）：

- `plugins/CoreRpg/talent.yml`：**零 diff**（相对 tip 窗口）
- unlock：`command: corerpg talent unlock …` 十三行 **与 tip 父提交完全一致**
- §8 `nodeId:` / `前置` 行 **与 tip 父提交完全一致**
- 其它 TrMenu 菜单：**零 diff**（tip `name-only` 仅 `ember_talent.yml` + 施工 STATUS）

---

## 冒烟

环境：proxy 25565 / login 25566 / play 25567；`JAVA_HOME=…/jdk8u504-b01`；账号 `B213tg39u5`（非 OP）；临时 OP `B213opg39u5`（console FIFO `op` → `mvtp` 枢纽 → 测后 `deop` + LP unset）。

**UX 路径（玩家口径）：** 枢纽菜单 → 天赋（禁教玩家斜杠；bot 侧仅用 `/ember` 开枢纽）。

| 抽检 | 结果 | 玩家可见（去色） |
|------|------|------------------|
| 开窗 title | ✅ | `余烬 · 天赋` |
| Open tell | ✅ | `请先选定誓约。点击节点解锁；二层需先点满一层顶（燎原 / 烟幕 / 永护）。` |
| 解锁说明 lore | ✅ | 含 `二层需先点满一层顶（燎原 / 烟幕 / 永护）` |
| 解锁说明 tell | ✅ | `点击节点解锁；二层需先点满一层顶 燎原 / 烟幕 / 永护。` |
| 规则速览 lore | ✅ | 第 4 条同人话口径 |
| 规则速览 tell | ✅ | `二层需一层顶（燎原 / 烟幕 / 永护）` |
| 烬刃 · 余烬脉 | ✅ | `先点满一层顶「燎原」` · 无 `*_cap` |
| 灰行 · 灰纱 | ✅ | `先点满一层顶「烟幕」` · 无 `*_cap` |
| 守墓 · 叠壁 | ✅ | `先点满一层顶「永护」` · 无 `*_cap` |

旁证：二层 lore 仍可见 `nodeId: …`（§8 灰字，方案 A 范围外）。

---

## ops 终态

- `server-runtime/ops.json` = **`[]`**
- `login-runtime/ops.json` = **`[]`**
- 临时 OP `B213opg39u5`：console `deop` + LP `corerpg.admin` unset

---

## 债 / 范围外

| 项 | 说明 |
|----|------|
| §8 `nodeId:` / 前置灰字 | 方案 A **未做**；不得宣称已清 |
| B0.1 | **未测、未宣称** |
| dirty runtime | 工作区仍有现网脏改；本 commit **仅**本 STATUS，未纳入 |

---

## 回报主代理 / 总控

- **总评：** **PASS**
- **各点：** 1 静态 PASS · 2 禁项零 diff PASS · 3 冒烟 PASS
- **diff：** tip `89b7432` = `ember_talent.yml` 14 处人话 + 施工 STATUS；本验收 commit = 本 STATUS
- **报告：** `docs/status/STATUS-ember-talent-cap-copy-test.md`
- **ops：** `[]`
- **阻塞点：** 无
- **UX：** 枢纽菜单 → 天赋；禁教玩家斜杠
