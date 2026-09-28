# STATUS · B2.15 天赋菜单裸属性键人话化 A · 测试验收

| 字段 | 值 |
|------|-----|
| 时间 | 2026-09-29 00:32 Asia/Shanghai |
| 岗 | 余烬-测试岗执行器 |
| 施工 tip | `da17196`（设计 `26d7895` · 批准 `f4e2bcd`） |
| 范围 | 仅 `plugins/TrMenu/menus/ember_talent.yml` 九行效果 lore；方案 B（passive/skill）**未做** |
| Verdict | **✅ PASS · 勾销** |
| JSON | `/tmp/b215-talent-attr-copy-smoke.json`（未入 git） |

---

## 一句话

**PASS：** `ember_talent` 二层九节点效果 lore 已无裸属性英键，改为物攻/暴伤/暴击率/移速/攻速/击中减速/受伤/生命/物防且数值原样；相对施工 tip 仅目标菜单九行 + 施工 STATUS；`talent.yml` / unlock 实参 / 其它菜单 **零 diff**；枢纽→天赋冒烟绿；**ops=[]**；本 commit **未 push**。**不**宣称 B0.1 / passive·skill / NI id 已清。

---

## 硬条总表

| # | 验收点 | 结果 | 证据 |
|---|--------|------|------|
| 1 | 静态：`rg 'phys_damage\|crit_damage_pct\|crit_chance_pct\|move_speed_pct\|attack_speed_pct\|on_hit_slow_pct\|damage_taken_pct\|max_health\|phys_defense'` = 0；九行人话+数值原样 | ✅ **PASS** | 见下静态明细 |
| 2 | 禁项：相对 `da17196`，`talent.yml` stats/unlock / 其它菜单 **零 diff**（tip 仅九行 lore） | ✅ **PASS** | tip `name-only` 仅 `ember_talent.yml` + 施工 STATUS；hunk 仅 9×`§7…` 替换 |
| 3 | 冒烟：开 `ember_talent`，二层九节点效果行无人话前英键 | ✅ **PASS** | `B215tgsznf` · JSON |

禁项自检：本岗 **未改** YAML；**不**宣称 B0.1 / passive·skill / NI id 已清；**未**带 dirty runtime 进 commit；测后 **ops=[]**；临时 OP 已 `deop`（`00:32:27` De-opped `B215opgsznf`）。

---

## 静态明细

```text
$ rg -n 'phys_damage|crit_damage_pct|crit_chance_pct|move_speed_pct|attack_speed_pct|on_hit_slow_pct|damage_taken_pct|max_health|phys_defense' plugins/TrMenu/menus/ember_talent.yml
# 无输出 · exit 1 · 0 命中

$ rg -n '物攻|暴伤|暴击率|移速|攻速|击中减速|受伤|生命|物防' plugins/TrMenu/menus/ember_talent.yml
180:        - '§7物攻 +1'
198:        - '§7暴伤 +0.03'
216:        - '§7暴击率 +0.005 · 物攻 +1'
234:        - '§7移速 +0.01'
252:        - '§7攻速 +0.01'
270:        - '§7击中减速 +0.03 · 攻速 +0.01'
288:        - '§7受伤 -0.01'
306:        - '§7生命 +2'
324:        - '§7物防 +1 · 受伤 -0.01'
```

热更旁证（施工 STATUS · Asia/Shanghai）：`00:28:47` 自动重载 `ember_talent.yml`（5ms）。live=`server-runtime/plugins/TrMenu/menus/ember_talent.yml` 与 `plugins/` 同内容（mtime `00:28` · size 8371 · `diff -q` 一致）。

方案 B 旁证（**不得宣称已清**）：同文件仍有 `passive` / `skill` 类型词（如「二层 · passive · cost 3」）。

---

## 结构零 diff

相对施工 tip `da17196`（父 `f4e2bcd`）：

| 路径 | 变更 |
|------|------|
| `plugins/TrMenu/menus/ember_talent.yml` | **仅** 9 行效果 lore 裸键→短中文（9−/9+）；**无** `command:` / cost / requires / stats / passive·skill 进入替换 hunk |
| `docs/STATUS-ember-talent-attr-copy.md` | 施工 STATUS |

未变（禁项核对）：

- `plugins/CoreRpg/talent.yml`：**零 diff**（tip 窗口未触及）
- unlock：`command: corerpg talent unlock …` — tip diff **无** unlock/command 行
- 其它 TrMenu 菜单：**零 diff**（tip `name-only` 仅 `ember_talent.yml` + 施工 STATUS）
- tip 相对父提交仅上述九对 `§7phys_damage…` 等 → 人话

---

## 冒烟

环境：proxy 25565 / login 25566 / play 25567；`JAVA_HOME=…/jdk8u504-b01`；账号 `B215tgsznf`（非 OP）；辅助 `B215opgsznf`（console FIFO `op` → `mvtp` 枢纽 → LP unset + 测后 `deop`；ops 终态 `[]`）。

**UX 路径（玩家口径）：** 枢纽菜单 → 天赋（禁教玩家斜杠；bot 侧仅用 `/ember` 开枢纽）。

| 抽检 | 结果 | 玩家可见（去色） |
|------|------|------------------|
| 开窗 title | ✅ | `余烬 · 天赋` |
| 烬刃 · 余烬脉 | ✅ | `物攻 +1`；无英键 |
| 烬刃 · 炽脉 | ✅ | `暴伤 +0.03`；无英键 |
| 烬刃 · 再燃 | ✅ | `暴击率 +0.005 · 物攻 +1`；无英键 |
| 灰行 · 灰纱 | ✅ | `移速 +0.01`；无英键 |
| 灰行 · 细尘 | ✅ | `攻速 +0.01`；无英键 |
| 灰行 · 掩息 | ✅ | `击中减速 +0.03 · 攻速 +0.01`；无英键 |
| 守墓 · 叠壁 | ✅ | `受伤 -0.01`；无英键 |
| 守墓 · 护冢 | ✅ | `生命 +2`；无英键 |
| 守墓 · 誓碑 | ✅ | `物防 +1 · 受伤 -0.01`；无英键 |

旁证：二层 lore 仍可见 `passive · cost …`（方案 B 范围外 · **不得宣称已清**）。

---

## ops 终态

- `server-runtime/ops.json` = **`[]`**
- `login-runtime/ops.json` = **`[]`**
- 临时辅助 `B215opgsznf`：LP `corerpg.admin` unset；console `De-opped B215opgsznf`（`00:32:27` Asia/Shanghai）；终态空数组

---

## 债 / 范围外

| 项 | 说明 |
|----|------|
| 方案 B · `passive` / `skill` 类型词 | **未做**；同文件仍有；不得宣称已清 |
| B0.1 | **未测、未宣称** |
| NI id / 套装 id 灰字 | **未测、未宣称**（另窗） |
| dirty runtime | 工作区仍有现网脏改；本 commit **仅**本 STATUS，未纳入 |

---

## 回报主代理 / 总控

- **总评：** **PASS**
- **各点：** 1 静态 PASS · 2 禁项零 diff PASS · 3 冒烟 PASS
- **diff：** tip `da17196` = `ember_talent.yml` 九行人话 + 施工 STATUS；本验收 commit = 本 STATUS
- **报告：** `docs/STATUS-ember-talent-attr-copy-test.md`
- **ops：** `[]`
- **阻塞点：** 无
- **UX：** 枢纽菜单 → 天赋；禁教玩家斜杠
