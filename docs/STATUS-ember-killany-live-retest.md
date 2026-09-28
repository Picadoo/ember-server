# STATUS · Raid/GuildBoss kill-any live 复测（测法 A）

**日期：** 2026-09-28 18:22～18:27 CST（Asia/Shanghai）  
**岗：** 余烬-测试岗执行器  
**依据：** `docs/design-ember-raid-guildboss-live-harness.md`（tip `f73c0db` · 设计 `a76d248` · **已批准 A**）  
**脚本：** `mineflayer-tests/killany-live-retest.js` · 日志 `/tmp/killany-live-retest.log` · JSON `docs/evidence-killany-live-retest.json`（同源 `/tmp/killany-live-retest.json`）  
**账号：** OP `KaOp928`（测后 deop）；Raid 队 `KaRa928`/`KaRb928`/`KaRc928`（非 OP）；GuildBoss `KaGb928`（非 OP · 植盟「杀债2385」测后 leave 解散）  
**硬禁遵守：** 未降 min、未砍怪、未改 YAML/MM、未重开使徒校准、未裸 `dp start` 当玩家路径；测后 **ops=[]**

---

## 总评

# **PASS**

| 项 | 结果 | 摘要 |
|----|------|------|
| A1 Raid wave2 live | **PASS** | 3 bot dungeon-team · `/corerpg enter raid` 真进本 · wave2 双 `$kill` 过波 + 抽检只清卫兵不过 |
| A2 GuildBoss wave1 live | **PASS** | 植盟+贡献 seed · `/corerpg guild boss` 开本 · wave1 双 `$kill` 过波 + 抽检只清潮尸不过 |
| 人数门测后 | **PASS** | EmberRaid `min=3` 仍在；EmberGuildBoss `min=1` 未改 |
| 玩法树 diff | **PASS** | monster/option/MM **空**（本 commit 仅测脚本+STATUS+证据 JSON） |
| ops | **PASS** | play=`[]` · login=`[]` |

---

## A1 · EmberRaid wave2

| 字段 | 值 |
|------|-----|
| 组队 | **PASS** · `dungeon-team create` + invite/join · 服侧 `队长 KaRa928 人数 3` @ **18:24:29** CST |
| 进本路径 | **`/corerpg enter raid`**（队长；全队持 `ticket_ember_raid` + stamina seed） |
| 进本硬证 | **PASS** · 横幅 **「团本大厅已集结。左道卫兵 · 右道射手 · 汇合后终厅使徒。」**（三 bot 同收）· 禁止仅凭菜单；旁证世界名 **`dungeon_EmberRaid_3798AB47`**（leave 清理日志 @ 18:25:28） |
| wave1 | **PASS** · 「左道肃清」@ **14.4s** |
| wave2 开波 | **PASS** · 「【右道】射手就位——右边先压远程！」@ **18.4s** |
| 抽检（只清卫兵） | **PASS** · guards=0 / archers=**4** 等待 12s → **无**「通道打开」 |
| wave2 双 `$kill` | **PASS** · 续清射手后 **「通道打开 · 精英将至」** @ **56.0s** |
| 早停 | **PASS** · `/dp leave` · 未打使徒 |
| 结论 | 卫兵×3 + 射手×4 同波双 `$kill` AND **成立**（只清一类不过） |

---

## A2 · EmberGuildBoss wave1

| 字段 | 值 |
|------|-----|
| 植盟 | **PASS** · `/corerpg guild create 杀债2385`（币×5000）@ **18:26:02** |
| 贡献 seed | **PASS** · `donate mat_ember_shard 200` → 贡献 **+20**（今日 20/50）· **未改**贡献数值表 |
| 开本路径 | **`/corerpg guild boss`** → 扣贡献 20 · console `dp start-console` · 服 `guild boss started (delayed) by KaGb928` · 实例 `EmberGuildBoss` / **`dungeon_EmberGuildBoss_4FF212A9`** |
| 进本硬证 | **PASS** · 「盟约周 Boss 已点燃」+「盟 Boss 波次 1：深渊潮 · 僵尸与骨鸣」 |
| 抽检（只清潮尸） | **PASS** · zombies=0 / skeletons=**3** 等待 12s → **无**「第一波肃清」 |
| wave1 双 `$kill` | **PASS** · 续清骨潮后 **「第一波肃清 · 蛮兵将至」** @ **34.1s** |
| 早停 | **PASS** · `/dp leave` · 未打蛮兵/灾厄使 |
| 测毕 | 盟约 leave 解散 · kick/deop · **无**残留永久 pass（`guildboss_pass` 为开本 5s 一次性） |
| 结论 | 潮尸×5 + 骨潮×3 同波双 `$kill` AND **成立** |

---

## 时序（CST）

| 时间 | 事件 |
|------|------|
| 18:22:10 | 脚本启动 · OP `KaOp928` |
| 18:22～18:24 | 三 bot 升级≥35 · 票/体力/刃 · dungeon-team |
| 18:24:29 | `/corerpg enter raid` · 人数 3 · 横幅集结 · 【左道】 |
| 18:24:43 | 左道肃清 · 【右道】开 wave2 |
| 18:25:〜 | 抽检只清卫兵不过 → 清射手 → **通道打开** · leave |
| 18:25:40 | `KaGb928` 上线 · 植盟 杀债2385 · 捐献 +20 |
| 18:26:09 | `/corerpg guild boss` · EmberGuildBoss `4FF212A9` |
| 18:26:〜 | 抽检只清潮尸不过 → 清骨潮 → **第一波肃清** · leave · 盟解散 |
| 18:27:01 | 收尾 kick/deop · **ops=[]** · overall PASS |

---

## 产物

| 路径 | 说明 |
|------|------|
| `docs/STATUS-ember-killany-live-retest.md` | 本报告 |
| `mineflayer-tests/killany-live-retest.js` | 测法 A 可复用 harness |
| `docs/evidence-killany-live-retest.json` | 结构化证据 |
| `/tmp/killany-live-retest.log` / `.stdout` | bot 全文 |
| `server-runtime/logs/latest.log` | 服侧人数/世界名/guild boss |

---

## 回报摘要（告总控代推）

- **总评 PASS**
- A1 Raid wave2：**PASS**（进本横幅+`dungeon_EmberRaid_3798AB47` · 双 kill + 抽检）
- A2 GuildBoss wave1：**PASS**（植盟开本+`dungeon_EmberGuildBoss_4FF212A9` · 双 kill + 抽检）
- 报告：`docs/STATUS-ember-killany-live-retest.md`
- 玩法 diff：**空**；Raid min 仍 **3**
- ops：play=`[]` · login=`[]`
- 阻塞点：无；测岗不 push（总控代推）
