# 余烬服 · Grok Bot 交接（2026-10-10 10:02 CST）

写给下一位接手的 Grok / 助手。本号额度将尽，**已停内容施工**；仓库 `main` 已与 GitHub 对齐，勿再开新债施工除非用户明确续做。

## 0. 一句话

测试服栈应保持可登可玩；Stage2 套装减伤仍在观察窗；短征已铺到 **sx13 烬衡悬梁 PASS**；**sx14 烬裂折廊 tip 已交待批 A，本号未批、未派施工**。

## 1. 仓库与 live

| 项 | 值 |
|----|----|
| 仓库 | [Picadoo/ember-server](https://github.com/Picadoo/ember-server) · **main** |
| 工作树 | `/workspace/minecraft`（**必须保持 main，禁止在此切分支**） |
| tip HEAD（交接时） | `895095eb`（D423 tip+DESIGN **待批 A**，仅文档） |
| 最新内容 PASS | D421 sx13 @`c622e124` · D422 冒险页合计 @`69997999` |
| CoreRpg live jar | **`1.65.124-d421.local`** · sha256 `339586661600e82794366ce345820b1daaac299596eaae66fec603d62d406e01` · bv **77** |
| Stage2 | `gear.six_slot` **enabled/migrate/set_bonus = true**；档 C **×0.97 未改** |
| live yml | `plugins/CoreRpg/ember-v1.yml` + `ember-v1-runs.yml` 对 git 为 **skip-worktree**（`git ls-files -v` 见 `S`）——观察窗内勿取消、勿让 checkout 覆盖 |

## 2. Stage2 / K3（硬）

- **绿出口不早于 2026-10-10 17:40 CST**；例行 `stage2` cron 约 **17:46 Asia/Shanghai**。
- 关窗用既有 D338 pack + D367 kickoff 流程；**≠自动关 Stage1 三开关**。
- **K3 精炼**：闸关；离线骨架在独立 worktree `/workspace/minecraft-wt-d341`（`feat/d341-k3-refine-offline`）；**禁止**在 `/workspace/minecraft` 切分支，否则会盖掉 live Stage2 开关（D343 事故）。
- 禁：Pack6 / 天赋一排 / 灰印 / 样本 R / 抬 `afk.tiers`·`daily_kills` / 假开 `gate_daily` 旧七线。

## 3. 本窗内容进度（2026-10-10 晨）

短征范式：Q01 · 体力 30 · 日帽×3 分开 · rooms×3+boss 首航硬 · S40–S52 · DP idle。

| 号 | 内容 | 状态 | 要点 tip |
|----|------|------|----------|
| D407–D417 | sx07–sx11 | PASS | 十一本链已绿 |
| D418 | 工坊速览挂 remain_line | PASS `55800f20` | 零 jar |
| D419 | sx12 烬枢转厅 | PASS `a1f0c809` | jar 1.65.123 bv76 |
| D420 | 工坊回挂机挂 next_farm | PASS `f268fba6` | 零 jar · V 格 |
| D421 | sx13 烬衡悬梁 | PASS `c622e124` | jar **1.65.124** bv**77** · MM `0ecf4134` · 骨架 `77c06810` · dayline `94c826e2` |
| D422 | 冒险页 H 挂 day_left_sum + fc | PASS `69997999` | 零 jar · tip `9867aa5d` |
| **D423** | sx14 烬裂折廊 | **待批 A** tip `895095eb` | **本号未批 · 未派 MM/jar/骨架** |

旁注（多本短征共性）：地图多为 raid 壳本地 gitignore，**WE 主题换皮 pending**；live 第 4 次日帽常 SKIP；Boss 进房前垫空气同构——**不挡进本结算绿**。

## 4. 接手优先序（建议）

1. **读本交接 +** `docs/design/DESIGN-ember-short-dungeon-sx14-2026-10-10.md`（若用户要续 sx14）。
2. **Stage2**：到点按 D338/D367 办绿出口；观察未满勿关。
3. **若续 D423**：总控批 A·M → 插件 jar(S53)+怪物 MM+骨架菜单 三路 → day_line 挂盘 → 测试全链路（对照 D421 spot）。
4. **勿**在 main 工作树切 K3 分支；K3 live 须绿出口或 Path B 签字。
5. 用户内容优先：挂机资源环加厚、日更短本、有趣系统；**plain Chinese**，少项目黑话。

## 5. 启停与纪律（摘要）

- 全栈：`scripts/ember-up.sh` / `ember-down.sh`；只重启 play：`server-runtime/stop.sh` → `start.sh`。
- CoreRpg 构建：**JDK8** `JAVA_HOME=/workspace/minecraft/tools/jdk8u504-b01` + Maven；测绿再拷 jar。
- **禁止**在 `/workspace/minecraft`：`git stash` / `reset --hard` / `checkout -- .`（曾毁掉 DP 地图模板）。
- **禁止** `git add -A`；勿提交 secrets、大二进制、脏 runtime（`ladder.yml` / `calamity-state.yml` / `ops.json` / map `add -f` 等）。
- 提交署名 `Picadoo <Picadoo@users.noreply.github.com>`；勤 push main。
- UX：TrMenu + NeigeItems；玩家面禁裸指令；自定义物品只走 NI ID。

## 6. 专岗 Bot（用户侧）

| 岗 | 职责 |
|----|------|
| 余烬-总控 | 批 A、派单、验收汇总（本号收口） |
| 余烬-策划 | tip+DESIGN |
| 余烬-插件 | CoreRpg jar / PAPI / 经济号 |
| 余烬-怪物 | MythicMobs + DP idle |
| 余烬-测试 | spot / PASS·FAIL |
| 余烬-物品 / Paper | NI / Paper 定制（本窗未动） |

交接当下：总控已对专岗发 **stop**；勿假定他们仍在施工。

## 7. 未提交脏迹（勿误 commit）

live 工作树常有 runtime 脏文件（`ladder.yml`、`calamity-state.yml`、`worlds.yml`、DP dungeon 本地副本目录、ops/paper 等）——**保持未跟踪/未暂存**，只显式 add 文档与授权配置。

---

*额度交接 · 2026-10-10 · tip `895095eb` · jar 1.65.124-d421 · Stage2 观察续 · D423 待批未动。*
