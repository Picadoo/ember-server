# 余烬服（Ember）接手说明 · HANDOFF

> 写于 2026-09-27 13:00（CST）。原账号额度将尽，由新的 Grok Bot 账号通过 GitHub 接手。
> 仓库：`Picadoo/ember-server`（main）。二进制与存档：Release **`v2026.09.27`**，资产 `ember-binaries-20260927.tar.xz`（sha256 见同名 `.sha256`）。
> **先读 `docs/ember-master-plan.md`**（总体规划与资源总账），再读本文。

---

## 1. 在新机器上恢复

1. **克隆到固定路径**（`env.sh` / `start.sh` / 测试脚本都写死了 `/workspace/minecraft`）：
   ```bash
   git clone https://github.com/Picadoo/ember-server.git /workspace/minecraft
   cd /workspace/minecraft
   ```
2. **下载并解压 Release**（世界、全部 jar、插件 sqlite、运行库、SQL 转储）：
   ```bash
   gh release download v2026.09.27 --repo Picadoo/ember-server
   sha256sum -c ember-binaries-20260927.tar.xz.sha256
   tar -xJf ember-binaries-20260927.tar.xz -C .
   ```
   如果以后资产分卷（`*.part-aa` …），先 `cat ember-binaries-*.part-* > ember-binaries.tar.xz` 再解压。
3. **JDK**（不入库，放 `tools/`）：
   - JDK 8（Temurin `jdk8u504-b01`）→ `tools/jdk8u504-b01`：登录服 + 游玩服（Paper 1.12.2），以及用 Maven 构建插件。
   - JDK 21（`jdk-21.0.12.1+1`）→ `tools/jdk-21.0.12.1+1`：Waterfall 代理需要 Java 11 以上；也可以用 `PROXY_JAVA=/path/to/java` 覆盖。
   - Maven 3.9.x → `tools/apache-maven-3.9.16`（`mvn -o` 离线构建需要先联网跑一次）。
4. **插件目录软链**：`ln -sfn ../plugins server-runtime/plugins`（start.sh 缺失时也会自动建）。
5. **机密**（全部由 `.example` 生成，真实文件被 .gitignore 忽略）：
   ```bash
   cp secrets/mysql-ember.env.example secrets/mysql-ember.env && chmod 600 secrets/mysql-ember.env   # 填密码
   ```
   然后把同一密码填进：`plugins/CoreRpg/config.yml` 的 `mysql.password`（仓库里是 `CHANGE_ME`）、登录服 AuthMe 配置（`login-runtime/plugins/AuthMe/config.yml`）。`secrets/bot-passwords.json` 由测试库首次运行自动生成。
6. **MariaDB**（10.x/11.x）安装与恢复：
   ```bash
   sudo apt-get install -y mariadb-server && sudo service mariadb start
   set -a; source secrets/mysql-ember.env; set +a
   sudo mariadb -e "CREATE DATABASE IF NOT EXISTS ember CHARACTER SET utf8mb4; CREATE DATABASE IF NOT EXISTS authme CHARACTER SET utf8mb4;
     CREATE USER IF NOT EXISTS '$MYSQL_USER'@'127.0.0.1' IDENTIFIED BY '$MYSQL_PASSWORD'; CREATE USER IF NOT EXISTS '$MYSQL_USER'@'localhost' IDENTIFIED BY '$MYSQL_PASSWORD';
     GRANT ALL ON ember.* TO '$MYSQL_USER'@'127.0.0.1'; GRANT ALL ON authme.* TO '$MYSQL_USER'@'127.0.0.1';
     GRANT ALL ON ember.* TO '$MYSQL_USER'@'localhost'; GRANT ALL ON authme.* TO '$MYSQL_USER'@'localhost'; FLUSH PRIVILEGES;"
   mariadb -h 127.0.0.1 -u "$MYSQL_USER" -p"$MYSQL_PASSWORD" ember  < sql/ember.sql
   mariadb -h 127.0.0.1 -u "$MYSQL_USER" -p"$MYSQL_PASSWORD" authme < sql/authme.sql
   ```
7. **启动 / 停止**：`scripts/ember-up.sh`（登录服 → 游玩服 → 代理）/ `scripts/ember-down.sh`。
   单独重启游玩服：`./server-runtime/stop.sh` → 等 `:25567` 不再监听 → `sleep 3` → `(setsid nohup ./server-runtime/start.sh > /tmp/play-start.log 2>&1 < /dev/null &)` → 等 `server-runtime/logs/latest.log` 出现 `Done (`。
   控制台命令（无 RCON / 无需 op）：`scripts/console.sh <play|login|proxy> "cmd"`，经各 runtime 的 `console.fifo`（start.sh 自动建立，`stop.sh` 经它正常 `stop`/`end`）。
8. **构建 CoreRpg**：
   ```bash
   export JAVA_HOME=/workspace/minecraft/tools/jdk8u504-b01 PATH=$JAVA_HOME/bin:/workspace/minecraft/tools/apache-maven-3.9.16/bin:$PATH
   cd CoreRpg && mvn -o -q -DskipTests package && cp target/CoreRpg.jar ../plugins/CoreRpg.jar   # 停服时复制
   ```

## 2. 架构

| 层 | 目录 | 端口 | 说明 |
|---|---|---|---|
| 代理 | `proxy-runtime/` | **25565**（公网） | Waterfall（Java 21）；新连接先进登录服 |
| 登录服 | `login-runtime/` | 25566（127.0.0.1） | Paper 1.12.2 + AuthMe（MySQL 库 `authme`），登录后送往游玩服 |
| 游玩服 | `server-runtime/` | 25567（127.0.0.1） | Paper 1.12.2 自定义核心 `paper-custom.jar`；插件配置在 `plugins/`（软链） |
| 数据库 | MariaDB | 3306 | 库 `ember`（CoreRpg 玩家/公会/寄售/邮件）+ 库 `authme`，用户见 `secrets/mysql-ember.env` |

世界：`world`（原版）、`ember_hub`（枢纽/主城）、`ember_afk`（分层挂机）、`ember_event`（灾厄公共窗），副本由 DungeonPlus 按 `plugins/DungeonPlus/map` 实例化。

## 3. 插件版本（游玩服）

CoreRpg **1.15.1**（阶段 4 已验收）· CoreEnchant 1.2.0 · CoreWorldRules 1.2.0 · CoreAnvil / CoreBrew / CoreCombat / CoreCraft / CoreFish / CoreSmelt 1.0.0 · MythicMobs 4.11.0 · NeigeItems 1.21.151 · DungeonPlus 1.4.5 · TrMenu 3.12.5 · Adyeshach 2.1.1 · HolographicDisplays 2.4.9 · Multiverse-Core 2.5.0-b727 · Multiverse-Portals 2.5.0-b751 · LuckPerms **5.4.145** · PlaceholderAPI 2.10.9（PAPI Player expansion 已存在）· ProtocolLib 4.4.0 · Vault 1.7.3 · spark 1.10.185。AttributePlus 已停用（`plugins/_parked`，属性层由 CoreRpg StatService 接管）。
登录服：AuthMe 5.4.0-b1877、ProtocolLib 4.4.0、Vault 1.7.3。代理：Waterfall 26.1 快照。

**LuckPerms + PAPI Player 基建已完成：** `default` 组含 MV / TrMenu / DP user/start / CoreRpg 玩家节点，并显式拒绝 `corerpg.admin`；`admin` 组为 `*` + `luckperms.*`，无人自动加入。`%player_name%` / `%player_world%` 已复核正常。详见 `docs/STATUS-ember-luckperms-papi.md`。

## 4. 文档索引（按优先级）

1. `docs/ember-master-plan.md` —— **总体规划**：阶段表、资源总账（产出/消耗/判断）、战力与怪物数值、各阶段落地记录。
2. `docs/critic-fixes-20260927.md` —— 逐条修复与重启记录（§1–§21）。
3. `docs/ember-skills-passives.md` —— 阶段 2 被动 / 誓约属性 / Boss 实测表。
4. `docs/ember-mainline-spec.md`（主线）、`docs/ember-gear-stats.md`、`docs/ember-gear-drop-t0-t3.md`、`docs/ember-abyss-calamity.md`、`docs/ember-raid-channel-spec.md`、`docs/progression-20260926.md`、`docs/level-gates-bounty-20260926.md`。
5. `docs/proxy-20260926.md`（代理/登录拓扑）、`DESIGN-ember-mysql-network.md`、`README.md`、`STATUS.md`。
6. `docs/LESSONS-ember-pipeline.md`（踩坑）、`docs/如何创建-Grok-Bot专岗.md`（bot 团队）。

## 5. 阶段状态

| 阶段 | 内容 | 状态 |
|---|---|---|
| 0 止血 | 挂机日顶、币消耗口、死物资接线、生活玩法第一版、主线修复 | ✅ CoreRpg 1.11.0 |
| 1 保底与补源 | 锻造 T1→T2→T3、凝核/断供补源、付费轨收紧 | ✅ 1.12.0 |
| 2 手感 | 装备被动（引燃/炽愈/烬爆）、同袍共鸣、誓约属性开启+上限、吸血只算技能、Boss 重调 | ✅ 1.13.0 |
| **3 分层挂机** | Lv10/20/30/40 四层，只掉材料，共用日顶 | ✅ **1.14.0 验收通过**（见下） |
| **4** | **后期内容：** I 主线 ch7～10 + J 天赋上限 20→30 + N 深渊 9～12 / `EmberEliteWeekly`；按 `docs/design-stage4-mainline-vol2.md` 冻结顺序实现 | ✅（有条件：7.2.4 TTK 估测未做）；CoreRpg **1.15.1**；4.1～4.6 已关闭 |
| **下一步（queued）** | 多世界地图（LuckPerms + PAPI Player 基建已完成） | 待排队 |
| 5 | 外观系统（外观碎片去处）等，见 master plan §5「L」 | 未开始 |
| 后续 · TODO | **地图美化**：现有地图（枢纽、挂机层空中平台、灾厄祭坛、副本模板）都是能用的占位图；以后换成有授权的预制地图（prefab）或用 WorldEdit 搭建。俯视预览图渲染脚本见 `scripts/render_topdown.py` | 未开始 |

### 阶段 3 现状（CoreRpg 1.14.0，commit `67c0cc3`）

**已完成并上线：**
- `AfkTierService`：`/corerpg afk [n]`（等级门槛 + 传送）；层区域内等级不足每 2 秒送回；死亡保留物品/等级并复活在本层入口（5 秒抗性 V）；怪物窒息保护（抬到空处）；升级跨过门槛时提示解锁；`/corerpg afk build <n>` 按 `config.yml afk_tiers` 重建空中平台（世界存档不进 git，平台可复现）。
- 四层：① 灰坡 Lv10（原挂机庭草坡）；② 荒原 Lv20 @ (200,110,250)；③ 焦土 Lv30 @ (400,110,250)；④ 烬原深处 Lv40 @ (600,110,250)，平台 37×37、屏障围墙。
- MM 怪（`Mobs/EmberAfk.yml`）与刷怪点（`Spawners/EmberAfk{2,3,4}_{A,B,C}.yml`，每层最多 2+1+1 只）。掉落全部走 `corerpg mmgive` → 受 `afk_caps` 约束。**日顶按「玩家 + 物品」计，不分层**（键 `afk_<item>`，世界名单 `ember_afk, world`），四层合计共用碎片 150 / 骨尘 80 / 核心碎片 10 / 魂尘 2 / 击杀币 150，超过后 25%；击杀经验 100/日也是全局。
- 菜单：`/ember → 挂机庭` 打开 `ember_afk.yml` 选层；主线第 2/4/6 章结束语与等级步骤提示已加挂机层。
- 灾厄余烬**没有**加进 Lv40 层：master plan 里它已经盈余，锻造只一次性消耗，加产出与流向不符。Lv40 层的特色改为核心碎片 4%（仍受日顶 10）。

**已测（非 op bot，`mineflayer-tests/afk-tier-test.js`）：**
- 门槛拒绝（Lv10 进 ②）、传送到入口、区域踢回（Lv24 进 ④ → 送回 ②）、死亡保留物品 + 入口复活 + 抗性，都通过。怪物卡方块采样 0。
- ② 荒原 Lv20（精炼刃 +2 / 护符 +1，烬刃）调参后：3 分钟 0 死亡，最低 HP 32%、平均 76%，14 杀/分钟，TTK 中位 2.8 秒。
- ① 灰坡：测试 bot 不会躲箭，150 秒被骷髅射死 1 次（老数值，未改）。
- ③ 焦土 Lv30：首跑 0 死亡、TTK 中位 3.6 秒，但最低 HP 1%（被霜骸射击，FAIL）；调伤后复测 0 死亡、最低 HP 70%、TTK 中位 2.8 秒、36 杀（PASS）。
- ④ 烬原深处 Lv40：首跑 PASS，0 死亡、最低 HP 33%、TTK 中位 3.2 秒、40 杀。
- ③/④ 战斗验收已完成；调参仅为 `EmberAfk3Stray Damage 4→2`、`EmberAfk3Zombie Damage 5→4`，Health/掉落/刷怪点未改，T4 未改。当前数值：焦兵 100/4、霜骸 85/2、灼尸 120/6、凋骸 110/6。详见 `STATUS-afk-tier-t3-t4.md`。

**阶段 3 验收补充：**
- `afk_caps` 日顶验收已通过：`CapM4518` 在 ① 结束为 12/15，切到 ② 仍为 12/15 并继续到 28/15；出现上限提示；达顶后 40 次碎片击杀实测 +12（约 30%，样本波动可接受）。详见 `STATUS-afk-caps.md`。配置已恢复 `shard/kill_coin=150/150`，`ops.json=[]`。

**未完成 / 收尾：**
- 已知设计债务（不阻塞阶段 3 验收）：日顶后的 25% 递减目前没有第二档，长时间挂机仍有线性产出（每小时约为未封顶时的 1/4）。

## 6. 已知问题 / 待复核

- 阶段 2 留下的偏高剩余生命（等真实玩家数据再调）：深渊单人守墓 91%；灾厄 3 人 76–83%；团本取决于守墓嘲讽能否拉住使徒，波动大。T3 烬爆被动没测到。
- 测试 bot 带饱和 / 吃面包，真人剩余生命应更低。
- 挂机日顶后 25% 递减没有第二档，长时间挂机仍有线性产出（每小时约为未封顶时的 1/4）。
- 外观碎片仍是死物资（阶段 5）；附魔晶盈余。
- `pkill -f` 会匹配到自己的 shell，杀进程用 PID。
- MM 4.11 `/mm s set … MobName` 报 invalid，改刷怪点要改 yml 再 `/mm reload`。

## 7. 测试脚本（`mineflayer-tests/`）

- 登录方式：`lib/proxy-login.js`（走代理 → AuthMe 注册/登录 → 游玩服），bot 密码自动写到 `secrets/bot-passwords.json`。
- op 命令：`node op-cmd.js "/corerpg reload" "/mm reload"`（需要 RpgBot 是 op）。
- 给 RpgBot op：**停服时**把 `{"uuid":…,"name":"RpgBot","level":4,"bypassesPlayerLimit":false}` 写进 `server-runtime/ops.json`（原环境备份在 `/tmp/ops-rpgbot.json`，新机器要自己写；RpgBot 离线 UUID 可从 `usercache.json` 查）。测试完在线执行 `node op-cmd.js "/deop RpgBot"`，**最终 ops.json 必须是 `[]`**。
- `dungeon-balance.js`：真实战斗平衡。`D=weekly|abyss|calamity|guild|raid`、`COV=blaze,ash,warden`、`CALN=3`（灾厄人数）、`RING=1`，结果行 `BALANCE_RESULT`，汇总可写个小脚本解析 JSON。
- `afk-tier-test.js`：分层挂机。`T=1..4 DUR=180 [KICK=1] [NODEATH=1]`，结果行 `AFK_RESULT`。
- 其它：`mainline-smoke.js`、`life-e2e.js`、`forge-test.js`、`critic-20260927-*.js`（试玩复测）、`gates-smoke.js` 等。

## 8. 硬规则

- **不改方块机制**（硬度、掉落等），**不改 Paper 核心**（除非用户明确要求；性能补丁归 Paper 岗）。
- NPC 用 **Adyeshach**，不用 Citizens。
- **怪物掉落只走 MythicMobs → NeigeItems**（`corerpg mmgive` / `ni give`），不走菜单发奖、不用原版掉落。
- **仓库里不放机密、jar、世界存档、日志**；二进制走 Release。提交前查密码：
  ```bash
  set -a; source secrets/mysql-ember.env; set +a
  (git diff; git ls-files -o --exclude-standard | xargs -r cat) | grep -c -- "$MYSQL_PASSWORD"   # 必须为 0
  ```
  `plugins/CoreRpg/config.yml` 在仓库里原地改，保持 `password: "CHANGE_ME"`，不要把线上那份复制进来。
- **`server-runtime/ops.json` 最终必须是 `[]`**；手改只在停服时。
- 重启尽量少、尽量快；重启前 `ss -tn state established | grep ":25565 " | wc -l` 看在线人数；commit 信息里写重启时间（CST）。
- 每块单独 commit + push；提交身份 `git -c user.name=Picadoo -c user.email=Picadoo@users.noreply.github.com commit`。
- 对用户汇报：中文、简洁、时间用 CST。Ember 内容（YAML、CoreRpg、MM、菜单、主线）可以全权改。

## 9. Bot 团队

完整提示词在 `docs/如何创建-Grok-Bot专岗.md`（§3 角色表、§4 可直接粘贴的 description）。角色：

| 角色 | 职责 |
|---|---|
| 总控（test） | 定优先级、派活、串验收，按 master plan 推进阶段 |
| 余烬-Paper | paper-custom.jar、NMS 钩子、性能 |
| 余烬-插件 | Core* 插件与 YAML（含 CoreRpg） |
| 余烬-物品 | NeigeItems 物品 YAML |
| 余烬-怪物 | MythicMobs 怪、技能、掉落 |
| 余烬-测试 | 起停服、mineflayer 冒烟、PASS/FAIL 报告 |
| 余烬-策划 | 玩法/副本/菜单文案/经济节奏设计稿 |
| 挑刺岗（critic） | 有内容里程碑时做新手试玩，出问题清单（见 `docs/critic-*.md`） |

新账号：先建总控，把本文和 master plan 给它，再按上表建各专岗。
