# STATUS · B2.47 拆解 Open tell 管理口吻 · TrMenu · 薄验收

**日期：** 2026-09-29 04:55 → 04:56 Asia/Shanghai（CST）  
**岗：** 余烬-测试岗执行器  
**依据：** 施工 tip `8c09391` Clarify ember disassemble open prompt  
**范围：** `ember_disassemble.yml` Open Events tell（L22）去「逻辑待 CoreRpg」管理口吻 + 含「点左侧即可」  
**禁项：** **禁改配置** · 禁改 scrap/reforge · 禁长测/挑刺 · **不宣称 B0.1 已清** · 勿带 dirty runtime  
**Verdict：** ✅ **PASS**（验收点全过 · ops=`[]`）

---

## 一句话

Open tell L22=`请手持 余烬之刃 或 余烬护符，点左侧即可。`；`rg '逻辑待 CoreRpg' ember_disassemble.yml` → **0**；L89 / scrap / reforge 本窗未漂；hub→拆解 chat 见持装+点左侧、无管理口吻；ops=`[]`。

---

## 总评

| # | 验收点 | 结果 |
|---|--------|------|
| 1 | 静态：Open tell 无「逻辑待 CoreRpg」；含「点左侧即可」；L89 / scrap / reforge 本窗未漂 | **PASS** |
| 2 | 开菜单轻测：hub→拆解 → chat 见持装+点左侧提示、无管理口吻 | **PASS** |
| 3 | 本岗未改配置；ops=`[]`；写测报 · commit+push | **PASS** |

---

## 环境

| 项 | 值 |
|----|-----|
| 工作区 | `/workspace/minecraft` |
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |
| 口 | proxy 25565 / login 25566 / play 25567 |
| 施工 tip | `8c09391` Clarify ember disassemble open prompt |
| 设计 / 批准 | `e9370a8` / `78c815e` |
| TrMenu | tip 后自动热更 `ember_disassemble.yml`（04:53:11 CST · 2ms）· live=`server-runtime` 与 `plugins/` diff 空 |
| 账号 | 验收 `OtPq8onq`（非 OP · Lv.10）· 辅助 `OtOpq8onq`（临时 OP，已 deop + LP unset） |
| 探针 | `/tmp/b247-disassemble-open-tell-copy-test.js` · JSON `/tmp/b247-disassemble-open-tell-copy-test.json`（未入 git） |
| 坐标 | hub (-18.5, 58, 110.5) 全程未变 |

---

## 各点证据

### 1 · 静态

```
L22 - 'tell: §7[分解] §7请手持 §c余烬之刃 §7或 §e余烬护符 §7，点左侧即可。'
L89 - '§8点左侧即可分解或重铸'
L56 - 'command: corerpg scrap'
L75 - 'command: corerpg reforge'
```

- `rg -n '逻辑待 CoreRpg' plugins/TrMenu/menus/ember_disassemble.yml` → **0**
- `rg -n '点左侧即可' …/ember_disassemble.yml` → L22 Open tell · L89 规则速览 lore
- tip `8c09391`：仅 `ember_disassemble.yml` `1 file changed, 1 insertion(+), 1 deletion(-)`  
  （旧含「逻辑待 CoreRpg 接线」→「点左侧即可。」）
- 相对 tip 父：L89 / scrap / reforge 相关行 **零 diff**（本窗未漂）
- live sync：`plugins/` == `server-runtime/plugins/`（diff 空）

### 2 · UX 菜单路径 hub→拆解 → Open tell chat

| 步骤 | 结果 |
|------|------|
| `/ember` → hub | 标题「余烬 · 冒险枢纽」 |
| 点「分解」 | 标题「余烬 · 分解」· 槽见「分解装备 / 重铸词缀 / 规则速览 / 返回主菜单」 |
| 开菜单瞬间 chat | **`分解 请手持 余烬之刃 或 余烬护符 ，点左侧即可。`** · **无**「逻辑待 CoreRpg」 |

（clickWindow 可能报 transaction timeout，属既有 TrMenu/mineflayer 吞事务；**菜单标题已切到「余烬 · 分解」且 Open tell 已入 chat**，以窗口 dump + chat 为准。）

### 3 · 禁项

| 项 | 证据 | 判定 |
|----|------|------|
| tip 仅 Open tell 1 行 | `git show 8c09391 --stat` → 仅 `ember_disassemble.yml` ±1 | **PASS** |
| 未改 scrap/reforge | tip name-only 无 scrap/reforge/ItemsAdder/体力；现网 command 仍在 | **PASS** |
| L89 未漂 | 父/现同为 `§8点左侧即可分解或重铸` | **PASS** |
| 本岗未改配置 | 仅写测报 | **PASS** |
| 未进本 / 坐标 hub | (-18.5, 58, 110.5) 前后同 | **PASS** |
| 未宣称 B0.1 | 本报不涉及 B0.1 | **PASS** |
| 不叫挑刺 / 禁长测 | 仅开菜单轻点 | **PASS** |
| 未带 dirty runtime | commit 仅 docs 测报 | **PASS** |
| ops=`[]` | play=`[]` · login=`[]`；临时 OP 已 deop | **PASS** |

---

## 阻塞点

无。

---

## 交付

- 报告：`docs/STATUS-ember-disassemble-open-tell-copy-test.md`
- 施工 tip SHA：`8c09391`
- ops：`[]`
- push：见本 commit 是否已 push（失败则交总控代推）
