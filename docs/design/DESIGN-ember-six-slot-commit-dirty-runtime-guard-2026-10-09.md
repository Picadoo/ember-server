# 余烬 · Stage2 观察期 commit 前脏 runtime 防误 stage 薄清单（决策页 · ≠关观察）

STATUS=**已批 A · 批 M · D365**（commit 前脏 runtime 防误 stage 薄清单已采纳 · OPS §1.5 半行已落 · **≠关观察 ≠勾选 §2.4 ≠开闸 ≠开 R ≠改 ×0.97 ≠改 ×0.1 ≠翻 set_bonus ≠改日历门槛 ≠改 `.gitignore` / 取消跟踪** · **不**复述 D360–D364 正文 · **不**重写 D344 防冲政策）· 2026-10-09 · 总控批注 · 上游 tip `a728c2a0` · backlog `B-six-slot-commit-dirty-runtime-guard` · 真源指针 D344 / OPS §1.5 / D364 D6（分工）/ 现网 `git status` 脏迹

> **一句话：** D344 防 live 被冲；缺 commit 前「勿 stage 脏 runtime」黑名单。荐 **方案 M**：统一防误 stage 薄清单；零改 ignore、零改 live。

### 0. 证据

| # | 来源 | 缺口 |
|---|------|------|
| E1 | D344 / OPS §1.5 | 真源、禁 resources→live、禁主仓切分支、skip-worktree——**无** commit 侧路径黑名单 |
| E2 | D344 验收 A4 | 仅写「无误 stage 脏 runtime」——**无**文件名单与 add 节奏 |
| E3 | D364 D6 | 班次查「脏 yml 未盖 live」——运行时覆盖面，**≠** `git add` 黑名单 |
| E4 | 现网工作区 | 常驻 `M ladder.yml` / `M calamity-state.yml` / `?? p1-six/` / MythicMobs `SavedData` 等 |
| E5 | 多份 STATUS 自检 | 「未 stage 脏 runtime」散落复述——无统一勾选真源 |

**本窗不做：** 关观察、勾选 D338 §2.4、开闸、开 R、改 ×0.97/×0.1/set_bonus/日历、改 `.gitignore`/取消跟踪、菜单诚实复扫、附录空转、复述 D360–D364 正文、重写 D344 防冲正文。

---

## §1 方案表（荐 M）

| 方案 | 内容 | 评价 |
|------|------|------|
| **M（荐）** | §2 黑名单 + add 节奏 + 误 stage 回退为运维真源；批 A=采纳；零改 ignore | **荐** |
| L | M + 同号改 `.gitignore` / `assume-unchanged` / 取消跟踪 ladder·calamity-state | 过厚；碰运维策略；另号 |
| W | 借清单授权 `git add -A` / 顺手 commit 脏态「清工作区」/ 关观察 | **否决** |

---

## §2 commit 前脏 runtime 防误 stage（方案 M）

### 2.1 定位（与 D344 / D364 分工）

| 项 | D344 live 防冲 | D364 值班 D6 | **本清单（commit 侧）** |
|----|----------------|--------------|-------------------------|
| 风险 | resources/checkout **盖掉** live 保护键 | 班次发现 live 已被盖 | **误把运行时脏迹 commit 进仓** |
| 时机 | 改 yml / 切分支 / 部署前 | 班次只读 | **每次 `git add` / `commit` 前** |
| 动作 | 禁 src→live；skip-worktree | 红报；不拧 | **显式路径 add；禁宽 add；误 stage 则 unstage** |
| 出口 | live 真源完好 | STATUS 红项 | 提交 diff **不含** §2.3 黑名单 |

**批 A = 采纳本模板。批 A ≠ 关观察 ≠ 开闸 ≠ 改 live ≠ 改 ignore ≠ 开 `k3_refine` ≠ 开 R。**

### 2.2 前置（每次打算提交时）

| # | 项 | 合格 |
|---|----|------|
| P1 | 声明 | docs-only / 功能号首行可写：`本号 commit · 已扫脏 runtime · 未 stage 黑名单` |
| P2 | 先看 | `git status --short`（或等价）——**先读再 add** |
| P3 | 范围 | 观察期默认：**只 add 本债授权路径**（常见=`docs/` 或已批施工文件清单） |
| P4 | 禁宽 | **禁止**无审的 `git add -A` / `git add .` / `git add plugins/CoreRpg` / `git add plugins/` |

### 2.3 黑名单（观察期默认勿 stage）

> 下列为**高发脏 runtime / 进程态**。未获总控「允许入库该文件」另号授权前，**不得** `git add`。

| 类 | 路径（示例） | 为何脏 | 默认 |
|----|--------------|--------|------|
| **R1** 天梯/灾厄态 | `plugins/CoreRpg/ladder.yml` · `plugins/CoreRpg/calamity-state.yml` | 被跟踪且运行时常改 | **勿 stage** |
| **R2** 六槽迁移记录 | `plugins/CoreRpg/p1-six/`（含未跟踪） | 玩家迁移/待领真源；OPS 禁手改 | **勿 stage**；亦禁手改内容 |
| **R3** 玩家/公会实例 | `plugins/CoreRpg/players/*.yml`（除已批的 `_schema-example.yml` 等文档例）· `plugins/CoreRpg/guilds/*.yml` · `mail/<uuid>.yml` | 实例数据 | **勿 stage** |
| **R4** MM 运行态 | `plugins/MythicMobs/SavedData/**` | 进程保存 | **勿 stage** |
| **R5** 日志/临时 | `logs/` · `mineflayer-tests/logs/` · `/workspace/tmp/**` 证据包 | 本机证据 | **勿入仓**（除非总控另签「证据入库」） |
| **R6** 保护键 live（未授权） | `plugins/CoreRpg/ember-v1.yml` · `ember-v1-runs.yml` | D344 真源；观察期 skip-worktree | **勿擅自 stage**；改保护键须总控签 + 解 skip → commit → 钉回 |

**允许（对照）：** 已批 docs（`docs/**`）；已批施工清单内的菜单/源码/resources（且遵守 D344：只许 live→resources 同步，禁 resources→live 盖写）。

### 2.4 推荐 add 节奏（docs-only 号）

```bash
# 1) 先看脏迹
git status --short

# 2) 只加本债 docs（示例）
git add docs/status/STATUS-ember-….md \
        docs/design/DESIGN-ember-….md \
        docs/design/design-ember-content-backlog.md

# 3) 再确认暂存区无黑名单
git diff --cached --name-only
# 期望：仅 docs/…；若出现 ladder.yml / calamity-state.yml / p1-six/ 等 → 立即 unstage

# 4) 误 stage 回退（示例）
git restore --staged -- plugins/CoreRpg/ladder.yml plugins/CoreRpg/calamity-state.yml
# 或：git restore --staged -- <误加路径>
```

功能/插件号：用**施工 STATUS 列明的路径清单**逐条 `git add <path>`；仍跑 §2.5 勾选。

### 2.5 提交前勾选（粘贴 STATUS / 自检）

| ID | 项 | 预期 |
|----|----|------|
| **C1** | `git status --short` 已读 | 已知哪些是脏 runtime |
| **C2** | `git diff --cached --name-only` | **无** §2.3 R1–R6（除非本号总控明文授权入库） |
| **C3** | 未使用禁宽 add | 无未审 `add -A` / `add plugins/CoreRpg` |
| **C4** | 与 D344 | 本提交**未** resources→live 盖写；未主仓危险切分支 |
| **C5** | 结论 | `脏 runtime 未入暂存 · 可 commit` / `已 unstage 黑名单后可 commit` |

### 2.6 含义（给提交的人）

1. **脏 ≠ 必须清仓：** 工作区有 `ladder.yml` 等脏迹是常态；正确动作是**不 stage**，不是「顺手 commit 清掉」。  
2. **本清单 ≠ 改 ignore：** 是否取消跟踪 / 写 `.gitignore` → **另号**（方案 L）；本窗只定操作纪律。  
3. **本清单 ≠ 关观察 / 开闸：** 只防 git 误入脏态。  
4. **与 D364：** 值班查 live 是否被盖；本清单查**即将入库的 diff**。

### 2.7 禁做（误读红线）

| 禁 | 理由 |
|----|------|
| 借「工作区不干净」`git add -A` 清场 | 高概率 stage 黑名单 |
| 把 `p1-six/` / 玩家 yml commit「当备份」 | 违 OPS；另号备份流程 |
| 未授权改并 commit `ember-v1.yml` 保护键 | 违 D344；须总控签 |
| 借本清单改 `.gitignore` / 取消跟踪 / 关观察 / 开 K3 / 开 R | 硬禁或另号 |
| 复述 D360–D364 正文当本债交付 | 硬禁 |

### 2.8 可选同号

| 文件 | 动作 |
|------|------|
| `OPS-ember-six-slot-migration.md` §1.5 | 可选半行指针：「commit 前脏 runtime 防误 stage 见本 DESIGN」 |
| D344 / D364 正文 | **不**空转长复述；防冲与值班步骤不改 |

### 2.9 验收（落字另号）

| ID | 步骤 | 预期 | 中止 |
|----|------|------|------|
| G1 | tip/DESIGN 入库 | 含分工、黑名单、add 节奏、勾选、禁做 | 缺任一项 |
| G2 | 明文 | 批 A ≠ 关观察 ≠ 改 ignore ≠ 开闸；本号自身未 stage 黑名单 | 提交含 ladder/calamity-state/p1-six 等 |
| G3 | 公式/开关/jar/日历 | 本号未改 | 误关窗或改门槛 |

**绿出口：** G1–G3 PASS。  
**回滚：** 还原 docs。

### 2.10 岗位

| 角色 | 职责 |
|------|------|
| 策划 | 批 A · 本清单模板 |
| 各岗（策划/插件/测试/运维） | 每次 commit 前跑 C1–C5 |
| 总控 | 批 A；若需改 ignore/取消跟踪 → **另号**方案 L |

---

## §3 不动与否决

| 项 | 状态 |
|----|------|
| 开关 / bv / ×0.97 / ×0.1 / set_bonus / jar / 日历门槛 | 本窗不动 |
| `.gitignore` / 取消跟踪 / assume-unchanged | 本窗不动（L 另号） |
| 提前关观察 / K3 live / 样本 R / Pack6 | 否决 |
| 附录空转 / D360–D364 复述 / 菜单复扫 | 否决 |

---

## §4 回退与并行

- **回退：** 删/还原本 tip+DESIGN+backlog 行；不影响 D344/D364。  
- **并行：** 观察续至 ≥**2026-10-10 17:40 CST**；K3 闸关；样本 R 全表不得开；菜单诚实波停。

---

## §5 批注栏

- [x] **批 A：采纳方案 M（本清单）** → 本 commit 前脏 runtime 防误 stage 薄清单为运维真源；OPS §1.5 半行指针已落（总控已批 · D365）  
- [ ] 升级 L（另号改 ignore / 取消跟踪）  
- [ ] 否决 / 改派  

**总控批注（2026-10-09 · D365）：** **批 A · 批 M**：采纳本页为 Stage2 观察期 commit 前脏 runtime 防误 stage 薄清单真源；§2.3 R1–R6 黑名单 + §2.4 显式路径 add + §2.5 C1–C5 勾选；与 D344 live 防冲 / D364 值班 D6 分工。OPS §1.5 半行指针已落。**批 A ≠ 关观察 ≠ 改 ignore ≠ 开闸 ≠ 开 R ≠ 勾选 §2.4。**

---

## 变更记录

| 日期 | 事件 | 谁 |
|------|------|-----|
| 2026-10-09 | 初稿 · 待批 A · 荐 M · commit 前脏 runtime 防误 stage 薄清单 | 策划执行手 |
| 2026-10-09 | D365 · 总控批 A·M；清单已采纳；OPS 半行已落；≠关观察≠改 ignore≠开闸 | 总控 / 执行手 |

*已批 A·M·D365 · B-six-slot-commit-dirty-runtime-guard · commit 防误 stage ≠ 关观察 ≠ 改 ignore ≠ 开闸。*
