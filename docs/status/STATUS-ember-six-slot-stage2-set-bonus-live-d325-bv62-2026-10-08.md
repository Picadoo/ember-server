# 余烬 · 六槽 Stage2 档 C · D325 线上部署 bv62（余烬-测试 · 2026-10-08）

- 上游：总控签部署 bv62（依据隔离 STATUS `baa6306a`）；用户确认测服可直接部署
- tip / jar：`b02ca9f3` · `CoreRpg.jar` sha256 **`4c273c5a509dbbe81d771ff6e02cdaac89ec3459919e957479b6acba5fddc8cc`** · plugin.yml **`1.65.99-d325.local`**
- 线上配置：`gear.six_slot.enabled=true` · `migrate=true` · **`set_bonus=true`** · `ember-v1-runs.yml` **`balance_version: 62`**
- 环境：play PID **149359**（旧 107524 已停）· login **47542** · proxy **47617** · MariaDB **47100**（后三者全程未变）
- 备份：`/workspace/tmp/d325-bv62-backup-20261008173047/`（旧 jar `abc96aca…` / `1.65.98-d322.local` + yml + pids）
- 测号：`D325Live`（代理 25565 → play；**未动真人档**）
- 证据：`/workspace/tmp/d325-bv62-smoke/`（含 `SUMMARY.json` · `poison2-*.log` · menu/papi/status）
- 执行：2026-10-08 17:30–17:40 CST
- **结论：部署 + 线上烟测 PASS。不变量 ①② 无违例。login/proxy/MariaDB 未动。最终 `set_bonus=true`、bv=62。**
- **建议：维持线上观察（S3 口径）；Stage2 已随 bv62 开服。**

## 1 部署步骤

| 步 | 动作 | 结果 |
|---|---|---|
| 1 | 备份 jar+`ember-v1.yml`+`ember-v1-runs.yml`+pids | ✅ `/workspace/tmp/d325-bv62-backup-20261008173047/` |
| 2 | `list`=0 → `server-runtime/stop.sh` | ✅ 旧 play 107524 停 |
| 3 | 换 jar → sha `4c273c5a…` | ✅ |
| 4 | yml：`set_bonus: true`；runs：`balance_version: 62`（注释 +61/62 史） | ✅ 仅线上文件；**未**进 git（同 D323 惯例） |
| 5 | `server-runtime/start.sh custom` | ✅ PID **149359**；日志 `Enabling CoreRpg v1.65.99-d325.local` · `Done (7.270s)` |
| 6 | 核 login/proxy/mariadb PID | ✅ 47542 / 47617 / 47100 不变 |

## 2 烟测结果（必验）

| # | 必验 | 结果 | 关键证据 |
|---|------|------|----------|
| 1 | 菜单/PAPI D169；无「后续开放」；三态文案 | **PASS** | `ember_p1_armor.yml` 无「后续开放」；`/corerpg p1 armor set` →「四件套已激活（焚烬族护甲 3/2）：受伤略减（−3%）」/ 拆甲后「进行中 … 1/2」；PAPI `%corerpg_p1_armor_set%`=`焚烬族 护甲 3/2 · 受伤 −3%` · `active=1`。`poison2-run.log` / `fixup-papi.txt` / `09-menu.json` |
| 2 | set_bonus 开+激活 poison ×0.97；关/未激活 ×1.0 | **PASS** | 在 **ember_afk**（P1 世界）采毒：关开关 `final 0.80`（仅 ×M，无 D325 行）；开+激活 `… D325 四件套受伤 ×0.97: 1.00 → 0.78` · `final 0.78`；开+拆至 1/2 `final 0.80` 无 D325。`poison2-A-off.log` / `poison2-B-on-active.log` / `poison2-C-on-broken.log` |
| 3 | 不进 B/H 面板 | **PASS** | 同装开关关/开：`攻击 47 · 生命 125 · 防御 10` 逐位不变。`fixup-status-off/on.txt` |
| 4 | Stage1 迁移/待领不冲突 | **PASS** | `armor status` → `p1_six_mig=1 journal=false 待领=0 enabled=true migrate=true`；`claim`「没有待领物品」。`11-armor-status.txt` / `12-claim.txt` |

### 门禁对照

| # | 门禁 | 结果 |
|---|------|------|
| G-S0 | 开关关=基线（无四件套减伤） | **PASS**（final 0.80） |
| G-S1 | 仅掉落阶≥T2 同族甲计件 | **PASS**（3×T2→3/2；拆至 1→进行中） |
| G-S2 | 激活 ×0.97；B/H 不变 | **PASS** |
| G-S3 | 菜单进度 / 三态；无「后续开放」 | **PASS** |
| G-S4 | 资产 loss/dup 不回归（迁/待领） | **PASS**（抽查） |
| G-S6 | 不改 F/护符×1.0/K0 | **PASS**（面板不变 + 施工单测依赖） |

## 3 约束与安全

- **已**换 jar / 升 bv62 / 开 `set_bonus`；**未**动 proxy / login / MariaDB 配置或 PID
- enabled+migrate **保持 true**；烟测中短暂关 `set_bonus` 做对照后**已恢复 true**
- **未** `git stash` / `reset --hard` / force-push；运行时 yml 变更**不提交**（同 D323 `ee73480c` 惯例：本号只 docs）
- 测号临时 op，用完 deop；真人档未碰
- 首次烟测在 hub/`world` 采毒无 P1 管道属预期；补跑改 `mv tp ember_afk` + latch `debug console` 后矩阵全绿

## 4 残余 / 观察

| 级 | 项 | 说明 |
|---|---|---|
| 观察 | poison 须在 P1 世界 | 线上 `scope.worlds=[ember_afk]`；hub 不走 `EmberCombatListener` 套装钩子 |
| 观察 | `corerpg p1 debug console` 为开关 | 每次命令翻转；采伤前须确认「控制台伤害日志: true」 |
| 观察 | TrMenu displayName | mineflayer 对自定义名多为材质名；以 lore / PAPI / `armor set` 回复为准 |
| 非阻 | 挂机庭脱战回复刷屏 | 测号在 ember_afk 时 AFK 回血日志多，不影响 final 采样 |

## 5 建议

**线上 bv62 / Stage2 部署 PASS，建议维持观察。**

- jar / set_bonus / bv 与隔离验收一致；减伤矩阵与 B/H / Stage1 抽查全绿
- 失败回滚路径仍可用：停 play → 恢复备份 jar（`abc96aca…`）+ yml（无 set_bonus 或 false、bv=61）→ 启 play（本窗**未**触发回滚）

## 6 清场

- 测号已下线；`list`=0
- 证据保留 `/workspace/tmp/d325-bv62-smoke/` + 备份目录
- 线上保持：`1.65.99-d325.local` · bv62 · enabled+migrate+**set_bonus** 全 true · play **149359**

---
*D325 测试岗 · 线上部署 bv62 · PASS*
