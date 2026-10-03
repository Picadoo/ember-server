# STATUS · B0 进本门控验收（测试岗）

**日期：** 2026-09-27 22:16–22:19（Asia/Shanghai）  
**岗：** 余烬-测试岗执行器  
**依据：** `docs/status/STATUS-ember-b0.md`（交卷基线 CoreRpg **1.15.8**；磁盘/日志实测 **1.15.9** + `TicketEntryService`）  
**脚本：** `/tmp/b0-accept-slim.js` · 结果 `/tmp/b0-accept-slim-result.json`  
**OP：** `B0OpBot`（临时；测完 deop）· 玩家 `B0s_7095`（非 OP）  
**Verdict：** **✅ B0 进本门控验收通过**（daily 全链路实测 + 静态全量；weekly/深/团/精英 live enter **SKIP** 见下）

---

## 总评

| # | 验收项 | 结果 |
|---|--------|------|
| 1 | 菜单/`corerpg enter` 扣 **NI id** 票 ×1；无票拒绝 | ✅ daily 实测；weekly+ 无票/进本因等级卡 Lv.10 **SKIP**（见备注） |
| 2 | 裸 `/dp start <Dungeon>` 进不去（`dungeon.start=false`） | ✅ daily + weekly 抽检均未进本 |
| 3 | 改显示名仍按 NI id 扣；不双扣 | ✅ 改名日票进本 −1；给 2 进本剩 1；option 无 live `<item:>` |
| 4 | TrMenu 玩家 lore/tell 无教打 `/dp` `/mvtp` | ✅ 抽查日/周/深/团/hub（及全 ember_*）teachHits=[] |
| 5 | ops=[] | ✅ play + login 均为 `[]` |

**CoreRpg：** jar **1.15.9** · 日志 `CoreRpg 1.15.9 enabled` · 含 `TicketEntryService.class`  
**未改：** 数值 / 票价 / MM（仅临时改 NI 日票 `name` 做改名抽检，测完已还原为 `余烬日票`）

---

## 1) 扣票 / 无票（live）

### daily（完整）

| 步骤 | 证据 |
|------|------|
| 无票 `/corerpg enter daily` | 聊天：`缺少余烬日票×1（进本即扣，不返还）`；未进本 |
| 给票 ×2 → enter | `[日票] 正在进入……` → 落地 **(0,65,0)**；`灰烬庭院·日 开始！已扣除日票 ×1` |
| 扣次（按显示名计，lore 已无裸 id） | 进本前 **2** → 后 **1**（Δ=1） |
| 不双扣 | 再给 ×2 → 进本后 **1**；五日 option.yml **无** 非注释 `<item:…>` 扣次 |

### weekly / abyss / raid / elite

| 本 | 无票 | 有票 enter | 说明 |
|----|------|------------|------|
| weekly | SKIP | SKIP | `ensureLevel` / `/corerpg progress … raid_clear` 未抬经验（玩家卡 **Lv.10 · 经验 0/60**）；等级门先于缺票提示 |
| abyss | SKIP | SKIP | 同上（需 Lv.25） |
| raid | SKIP | SKIP | 需 Lv.35 + 3～5 人；本轮未组队实测 |
| elite | SKIP | SKIP | 需 Lv.40；可能另有周通关冷却 |

> 静态侧：五日 `option.yml` 均已去掉 live `<item:显示名>` 扣次（仅注释提及）；TrMenu `command:` 为 `corerpg enter …` / `corerpg elite start`（菜单执行，非玩家教学句）。

---

## 2) 裸 `/dp start` 拒绝

非 OP + 持票：

| 命令 | entered | 聊天摘要 | 坐标 |
|------|---------|----------|------|
| `/dp start EmberDaily` | **false** | `Incorrect argument for command` / `dp start<--[HERE]` | 仍在枢纽 ≈(-18.5,58,110.5) |
| `/dp start EmberWeekly` | **false** | 同上 | 同上 |

与交卷冒烟 `/tmp/b0-ticket-enter-result.json` 一致（LP `dungeon.start=false` 路径）。

---

## 3) 改显示名仍按 NI id 扣

1. 临时改 `plugins/NeigeItems/Items/ember-dungeon-tickets.yml`：`ticket_ember_daily.name` → `改名验收日票`  
2. `/ni reload` → `/ni give … ticket_ember_daily 1`  
3. 背包显示名：**改名验收日票**（lore 仍无裸 id）  
4. `/corerpg enter daily` → 进入 (0,65,0)；票 **1→0**  
5. **已还原** yaml 为 `余烬日票` 并 `/ni reload`（仓库无永久改名残留）

→ 证明扣次走 **NI id**（`consumeExact`），不依赖显示名。

---

## 4) TrMenu lore / tell 抽查

对 `ember_daily/weekly/abyss/raid/hub` 及全部 `ember_*.yml`（排除 `.bak`）：

- 玩家可见 lore/tell：**无**「进本命令：/dp」「请打 /dp|/mvtp」「请执行 /dp|/mvtp」等教学句  
- `command:` 底层保留（菜单点按执行，不教手打）— 符合 B0.3  

灾厄测本 `ember_calamity.yml` 管理测句不在本项必测范围（总控 B0 交卷已声明）。

---

## 5) ops

| 文件 | 测后 |
|------|------|
| `server-runtime/ops.json` | `[]` |
| `login-runtime/ops.json` | `[]` |

测中临时 OP：`B0OpBot`（及 ops 文件中的 RpgBot 条目）；测完 `/deop` + 写空。

---

## 环境备注

- 代理 25565 / login 25566 / play 25567；play 本轮有过多次重启（他岗并行），验收以 **22:15:55+** 实例日志为准。  
- 票玩家面 lore **已清裸 `ticket_ember_*`**，客户端计票改按**显示名**；服务端仍按 NI id。  
- 等级抬升失败原因未深挖（疑似 progress 日顶/权限/存储）；**不阻塞** daily 门控结论。若需补 weekly+ live，建议另开测窗：先确认 `/corerpg progress` 对测试号生效或 admin 置等级后再跑。

---

## 痕迹

- `/tmp/b0-accept-slim.js` · `/tmp/b0-accept-slim-result.json` · `/tmp/b0-accept-slim-run.log`  
- 前序全量稿 `/tmp/b0-accept.js`（等级问题导致大量假阴；以 slim 为准）  
- 交卷冒烟对照：`/tmp/b0-ticket-enter-result.json`  
- 本报告：`/workspace/minecraft/docs/status/STATUS-ember-b0-test.md`

**下游：** 总控可据此开 **P5**（灾厄/团本地图）；B0.2 天赋 TTK / B0.4 挂机二档仍按交卷 STATUS 挂测/标债，不在本项。
