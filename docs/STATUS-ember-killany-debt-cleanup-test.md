# STATUS · 消 kill-any 剩债短抽

**日期：** 2026-09-28 10:03～10:22 CST（Asia/Shanghai）  
**岗：** 余烬-测试岗执行器  
**依据：** `docs/design-ember-killany-debt-cleanup.md` §3 · `docs/STATUS-ember-killany-debt-cleanup.md` · commit `3194355`（tip `120be0b`）  
**脚本：** `/tmp/killany-debt-cleanup-test.js` · 日志 `/tmp/killany-debt-cleanup-test.log` · JSON `/tmp/killany-debt-cleanup-test.json`  
**账号：** 主测 **`KadAb3454`**；临时 OP **`KadDebtOp`**；团本 filler `KadRa7006` / `KadRb1949`（测后已 kick + deop）  
**热更：** 测前 FIFO `dp reload` ✅ **10:03:13～10:03:14** CST（EmberAbyss / EmberRaid / EmberGuildBoss 初始化完毕）  
**硬禁遵守：** 禁 killall · 禁改配置 · 测岗不 commit/push · 测后 **ops=[]**

---

## 总评

# **PASS**

静态四条全过；Live **EmberAbyss** 真击杀过 F2 且顺过 F7（混潮+骨潮双 `$kill` 升层）；**EmberRaid** / **EmberGuildBoss** 按门控 **SKIP**（不挡本轮）。

| 项 | 结果 | 摘要 |
|----|------|------|
| ① 三文件实杀 `$kill-any{` 归零 | **PASS** | EmberAbyss / EmberRaid / EmberGuildBoss 有效行 0；全库仅剩模板 `AAA,BBB,CCC` |
| ② 同波双 `$kill` AND · amount | **PASS** | 2+2 / 3+4 / 5+3；无新组、无链式拆 wave |
| ③ option/HP/刷点/日常·周本·精英零误伤 | **PASS** | `3194355` 仅动三份 `monster.yml` + STATUS；他线/option 零 diff |
| ④ plugins ↔ server-runtime identical | **PASS** | 三文件同 inode · `cmp` identical |
| Live EmberAbyss F2 | **PASS** | 进本→F2「混潮+骨潮」@7s → F3 @15s；服 `本局层 1 → 2` |
| Live EmberAbyss F7 | **PASS** | F7「混潮+骨潮加压」@59s → F8 @70s；服 `本局层 6 → 7` |
| Live EmberRaid wave2 | **SKIP** | 3 bot 已组+给票；未真正进本（见下） |
| Live EmberGuildBoss wave1 | **SKIP** | 无盟约（不挡） |
| ops | **PASS** | play=`[]` login=`[]` |

---

## 静态证据

### ① `$kill-any{` 归零（三文件实杀）

`rg '$kill-any{' plugins/DungeonPlus/dungeon/**/monster.yml`：有效实杀 **0**；仅小写空壳模板 `AAA,BBB,CCC`（不计）。  
STATUS 点名三文件（EmberAbyss / EmberRaid / EmberGuildBoss）内 **无** `$kill-any{` 有效条件行（注释「禁 kill-any」保留不计）。

### ② 同波双 `$kill` AND

| 组 | condition（现） | amount |
|----|-----------------|--------|
| EmberAbyss floor2 / floor7 | `$kill{余烬深渊·混潮;2}` + `$kill{余烬深渊·骨潮;2}` | **2+2** |
| EmberRaid wave2 | `$kill{团本·通道卫兵;3}` + `$kill{团本·通道射手;4}` | **3+4** |
| EmberGuildBoss wave1 | `$kill{余烬深渊·潮尸;5}` + `$kill{余烬深渊·骨潮;3}` | **5+3** |

组名仍为 `floor2`/`floor7`/`wave2`/`wave1`；`end` 仍 `$monstergroup` 串下一原组 —— **无** `floor2a`/`wave2a` 新组链式。

### ③ 零误伤

`git diff 3194355^..3194355 --name-only`：

- `docs/STATUS-ember-killany-debt-cleanup.md`
- `plugins/DungeonPlus/dungeon/EmberAbyss/monster.yml`
- `plugins/DungeonPlus/dungeon/EmberRaid/monster.yml`
- `plugins/DungeonPlus/dungeon/EmberGuildBoss/monster.yml`

日常七线 / 周本 / 精英 / `option.yml` / HP / 刷点 location·amount · **零碰**（diff 仅 condition 行）。

### ④ 双树 identical

| 文件 | inode | cmp |
|------|-------|-----|
| EmberAbyss/monster.yml | 同 | identical |
| EmberRaid/monster.yml | 同 | identical |
| EmberGuildBoss/monster.yml | 同 | identical |

---

## Live · EmberAbyss（必做）

| 字段 | 值 |
|------|----|
| 进本 | **PASS** · `/ember` → 深渊 → 开始下潜（扣体力） |
| F1→F2 | **7s** · 横幅 `—— 第 2 层 —— 混潮+骨潮` |
| F2 双 `$kill` | **PASS** · 升层至 F3 @15s · 服 `[10:10:23] 本局层 1 → 2` |
| F7 双 `$kill` | **PASS** · 横幅 `—— 第 7 层 —— 混潮+骨潮加压` @59s → F8 @70s · 服 `[10:11:18] 本局层 6 → 7` |
| 层时序 (s) | 1:0 · 2:7 · 3:15 · 4:23 · 5:33 · 6:54 · 7:59 · 8:70 |
| 战斗 | ~70s · hits≈59（OP `/tp` 贴怪助攻） |

**结论：** 混潮+骨潮同波双 `$kill` AND 在 F2 / F7 均成立（清完两类才升层）。

---

## Live · EmberRaid wave2

**SKIP**（不挡本轮）

- 已拉起 3 bot + 团本票；菜单可见「团本 需 3～5 人」
- **未**出现【左道】/【右道】射手就位等 wave 横幅
- 脚本误判进本后 bot **卡墙窒息**；聊天多次「该命令只能在地牢内操作使用」→ 实际未进 EmberRaid 实例
- wave2 双 `$kill`（3+4）以 **静态 YAML** 验收；live 进本门/组队工具链本窗未打通

---

## Live · EmberGuildBoss wave1

**SKIP**（不挡本轮）

- `/corerpg guild info` →「你不在任何盟约中。」
- 无盟/不可进 → 按派工 SKIP

---

## 时序（CST）

| 时间 | 事件 |
|------|------|
| 10:01:52 | 插件岗 STATUS：`dp reload`（前次） |
| 10:03:13 | 测岗 FIFO `dp reload` · 插件重载完毕 · 三本初始化 |
| 10:08:23 | 脚本启动 · 临时 OP `KadDebtOp` |
| 10:10:01 | EmberAbyss 进本 · `本局层 0 → 0` · 第 1 层 |
| 10:10:23 | `本局层 1 → 2` · F2 双 kill 过 |
| 10:11:18 | `本局层 6 → 7` · F7 双 kill 过 · 随后 F8 @70s 停 |
| 10:11+ | Raid 尝试 · 未进本 · SKIP |
| 10:21 | GuildBoss probe · 无盟 · SKIP |
| 10:21:56 | 强制收尾 · kick/deop · **ops=[]** |

---

## 产物

| 路径 | 说明 |
|------|------|
| `docs/STATUS-ember-killany-debt-cleanup-test.md` | 本报告 |
| `/tmp/killany-debt-cleanup-test.json` | 结构化结果 |
| `/tmp/killany-debt-cleanup-test.log` | bot 全文 |
| `server-runtime/logs/latest.log` | 服侧 `本局层` / `dp reload` |

---

## 回报摘要（告总控代推）

- **总评 PASS**
- 四条静态：**PASS / PASS / PASS / PASS**
- Live：Abyss F2 **PASS** · F7 **PASS** · Raid **SKIP** · GuildBoss **SKIP**
- 报告：`docs/STATUS-ember-killany-debt-cleanup-test.md`
- ops：play=`[]` · login=`[]`
- 阻塞点：Raid live 进本/组队未打通（本窗 SKIP）；GuildBoss 无盟（预期 SKIP）；测岗不 push
