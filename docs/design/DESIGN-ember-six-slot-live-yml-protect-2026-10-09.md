# 余烬 · 观察期 live yml 防源码默认冲掉（纪律页）

STATUS=**已批 A · 批 M · D344**（防冲纪律 + OPS 已落 · **批 A ≠ 改默认 true ≠ 关窗 ≠ 开 K3** · 观察续）· 2026-10-09 · 总控批注 · 上游 tip `7c9b8ddd`

> **一句话：** 观察真源在 **`plugins/CoreRpg/ember-v1*.yml`**；`src/main/resources` 是打包默认。荐 **方案 M**：钉死**禁止 src→live 整份覆盖**、保护键、同步方向只许 live→resources、部署前核对。**批 A ≠ 改默认 true。**

### 0. 证据

| # | 来源 | 口径 |
|---|------|------|
| E1 | D342 预检实跑 STATUS | 21:00:03 live 与 resources 同文；六槽段消失；bv→60；R1+R2 红停报 |
| E2 | D343 恢复（总控口述 + 现网） | 三开关 true + bv62 已回；broken/pin 在 `/workspace/tmp/d343-*` |
| E3 | `EmberSourceMapTest#bundledConfigsMatchLive` | 要求 bundled == live；文案：**把 live 拷进 resources**（同提交） |
| E4 | OPS / D330 | 观察期真源=线上配置；禁擅自改真值 |
| E5 | 代码默认 | `six_slot` 缺省/false；resources 若无观察段 = 「关」 |

**事故链（简）：** 为对齐单测/分支/热更，有人把 **resources（无观察真值）拷到 plugins** → 观察静默解体风险（reload/重启后生效）。

---

## §1 方案表（荐 M）

| 方案 | 内容 | 评价 |
|------|------|------|
| **M（荐）** | §2 纪律 + OPS 小段规格；批 A=采纳；OPS 落字另号；零改默认开关语义 | **荐** |
| L | M + 立刻改 Java/`saveResource` 永不覆盖已存在文件的强代码闸 | 要插件号；可后置；本窗不授权 |
| W | 把 resources 默认改成三 true / 默默关观察「省事」 | **否决**（污染新环境 / 偷关窗） |

---

## §2 纪律正文（方案 M）

### 2.1 真源与角色

| 路径 | 角色（观察期） |
|------|----------------|
| `plugins/CoreRpg/ember-v1.yml` · `ember-v1-runs.yml`（及同族 live） | **运行真源** |
| `CoreRpg/src/main/resources/ember-v1*.yml` | 打包/单测用默认；**不得**反向覆盖 live |
| jar 内 resources | 仅当插件目录**缺文件**时由 `saveResource(..., false)` 抽出——**已存在 live 禁止当「对齐」覆写** |

### 2.2 观察期保护键（缺一即事故）

| 键 | 观察钉死（当前） | 冲丢表现 |
|----|------------------|----------|
| `gear.six_slot.enabled` | **true** | 护甲路径关 |
| `gear.six_slot.migrate` | **true** | 迁移停 |
| `gear.six_slot.set_bonus` | **true** | Stage2 减伤关 |
| `balance_version`（runs） | **62** | bv 回落（曾见 60） |
| `gear.six_slot.k3_refine` | **关/缺省** | 若被误开 → 违 D341 |

**销账前**任何「同步 yml」须先 `rg`/`diff` 确认上表仍在 live。

### 2.3 禁止 / 允许

| 禁止（观察期） | 允许 |
|----------------|------|
| `cp`/`rsync`/`git checkout --` **resources → plugins/CoreRpg/ember-v1*.yml** | 改 live 后 **同提交** 把 live **拷回** resources（满足 `bundledConfigsMatchLive`） |
| 以「单测红了」为由整份用 src 盖 live | 单测红：先查是否 live 有意超前；再 live→src，**勿** src→live |
| 部署脚本静默覆盖上述保护键 | 部署 checklist 显式 diff 保护键 |
| **主工作区** `git checkout` / `switch` / `pull` 会改写已跟踪的 live yml（D343） | 观察期**禁主仓切分支**；K3 离线用**独立 worktree**；live 两文件观察期 `skip-worktree` |
| 本页授权改 ×0.97 / 关观察 / 开 K3 | — |

### 2.4 同步与部署前核对（可执行）

| # | 步骤 | 合格 |
|---|------|------|
| C1 | `diff` live vs 将要写入的文件：保护键段 | live 仍三 true + bv62 |
| C2 | 若只需过 `bundledConfigsMatchLive` | **只** live→`src/main/resources`，同 commit |
| C3 | 热更/换 jar 后 | 只读复检保护键（可套 D342 R1/R2）；**勿**为「对齐」再盖 live |
| C4 | 发现 live 已被冲 | **停**；对照 `/workspace/tmp/d343-pin/` 或 D335/D325 备份；**另签**后再恢复；记 STATUS（范式 D342/D343） |

### 2.5 OPS 小对齐规格（D344 已落字）

写入 [`OPS-ember-six-slot-migration.md`](../ops/OPS-ember-six-slot-migration.md) 小节 **「观察期 yml 真源与防冲」**（D344）：

1. 重申真源=plugins；resources≠可覆盖源。  
2. 粘贴 §2.2 保护键表 + 当前钉死值。  
3. 明文禁止 resources→plugins；允许方向 live→resources。  
4. 指向 D342 红停报 / D343 恢复 / pin 路径作前车。  
5. 文首 jar tip 若与进程漂移：以进程为准，旁注即可（对齐 D342 R4），**勿**用旧 jar 名当覆盖借口。  
6. **总控加注（必须）：** 除 resources→plugins 外，主工作区 `git checkout` / `switch` 因 `plugins/CoreRpg/ember-v1*.yml` **被 git 跟踪**也会整份盖 live（D343：21:00、21:03）。观察期：**主工作区禁切分支**；K3 离线只用**独立 worktree**。  
7. **总控加注（必须）：** 观察期对 live `plugins/CoreRpg/ember-v1.yml` / `ember-v1-runs.yml` 设 `git update-index --skip-worktree`（钉仓后设回）；要改并 commit 时先 `--no-skip-worktree`，提交后再设回。**skip-worktree ≠ 取消跟踪**，只防本机工作区被 checkout 静默冲掉。

### 2.6 验收（docs 落字号）

| ID | 预期 |
|----|------|
| A1 | DESIGN 批 A；tip 关 |
| A2 | OPS 出现防冲小节（另号） |
| A3 | backlog 指针；**无**关观察签字交付 |
| A4 | 本债号 `git` **无**误 stage 脏 runtime / 未授权拧开关 |

### 2.7 岗位

| 角色 | 职责 |
|------|------|
| 策划 | 本页批 A |
| 运维/插件 | 遵守 §2.3–2.4；OPS 另号落字 |
| 总控 | 恢复/覆盖类操作须另签；本页≠关窗 |

---

## §3 不动与否决

| 项 | 状态 |
|----|------|
| 观察开关 / ×0.97 / bv 真值（本窗） | **不动**（已恢复则保持） |
| 关观察签字包交付 | **否决**（未满窗） |
| K3 live / 样本 R / Pack6 | 否决 |
| resources 默认改成观察 true 当「一劳永逸」 | **否决**（污染新装） |
| 默默改 `bundledConfigsMatchLive` 放宽 | 须另号设计；本窗不授权 |

---

## §4 批注栏

- [x] **批 A：采纳方案 M（防冲纪律 + OPS 规格）** → OPS D344 已落字（总控已批 · D344）  
- [ ] 升级 L（授权插件号做覆盖守卫代码）  
- [ ] 否决 / 改派  

**总控批注（2026-10-09 · D344）：** **批 A · 采纳方案 M**。真源=`plugins/CoreRpg`；禁 resources→live；保护键三 true + bv62；同步只许 live→resources。**加注：** 主工作区因 live yml **被 git 跟踪**，`checkout`/`switch` 亦可整份盖 live（D343 21:00/21:03）——观察期**禁主仓切分支**；K3 离线只用独立 worktree；live 两文件观察期 **`skip-worktree`**（改仓时先解再钉回）。**批 A ≠ 改默认 true ≠ 关观察 ≠ 开 K3**；×0.97 / 三开关真值本号不拧。OPS 防冲小节同号落字。

---

## 变更记录

| 日期 | 事件 | 谁 |
|------|------|-----|
| 2026-10-09 | 初稿 · 待批 A · 荐 M · 上游 D342 红 / D343 恢复 | 策划执行手 |
| 2026-10-09 | **D344** · 总控批 A·M；OPS 防冲小节落字；加注禁主仓切分支 + skip-worktree；**批 A ≠ 改默认 true ≠ 关窗** | 总控 |

*live yml 防冲 · 已批 A·M·D344 · 禁 src→live · 禁主仓切分支 · skip-worktree · 只许 live→resources · 勿关窗 · 零改默认 true。*
