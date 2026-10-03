# STATUS · B0 已知债务交卷

**日期：** 2026-09-27 21:53–21:55（Asia/Shanghai）  
**岗：** 余烬-总控执行器（缺人代插件落地）  
**依据：** `docs/design/design-ember-content-backlog.md` §1 B0.1～B0.4；物品岗 `docs/status/STATUS-ember-ticket-ni-audit.md`  
**总控确认：** B0 STATUS 交卷后即可开 P5，**不必等 P4 测回报**；本轮 **未** 开 P5。  
**Verdict：** **✅ B0 交卷**（B0.1+B0.3 落地并冒烟；B0.2 测窗挂起；B0.4 标债不做）

---

## 一句话

进本扣票改走 **NI id**（`TicketEntryService` → `consumeExact` → `dp start-console`）；玩家菜单去指令教学；挂机二档与天赋二层 TTK 分别标债/挂测。

---

## B0.1 票扣次 NI id（✅）

| 项 | 结果 |
|----|------|
| 物品岗 lore | ✅ 已见 `docs/status/STATUS-ember-ticket-ni-audit.md`（裸 `ticket_ember_*` 玩家面已清） |
| CoreRpg | ✅ **1.15.8** 新增 `TicketEntryService`；`/corerpg enter daily\|weekly\|abyss\|raid\|elite`；`/corerpg ticket consume`（admin） |
| 精英 | ✅ `EliteService.cmdStart` 改走同一入口；`passesGate` **不再**要求持票（避免扣票后 `%corerpg_gate_elite%` 假失败） |
| DP `option.yml` | ✅ 五日/周/深/团/精英去掉 `<item:显示名>` 扣次；保留人数/等级门 |
| TrMenu | ✅ 日/周/深/团 `command:` → `corerpg enter …`；精英仍 `corerpg elite start` |
| LP | ✅ `default`：`dungeon.start=false`（防裸 `/dp start` 白嫖）；管理测本用 `dp start-console` / admin |
| 数值 | ✅ 日 3 / 周 1 / 深 1 / 团 1 / 精英 1 **未改** |

### 冒烟（`/tmp/b0-ticket-enter-result.json`）

| 检查 | 结果 |
|------|------|
| 非 OP `/dp start EmberDaily` | ✅ 未进本（argument/无权限路径） |
| `/corerpg enter daily`（有票 Lv≥10） | ✅ 落地 **(0,65,0)**；聊天「正在进入」「已扣除日票」 |
| 无票 enter | ✅ 「缺少余烬日票×1」 |
| `ops.json` | ✅ **`[]`**（测后 `/deop RpgBot`） |

---

## B0.3 菜单去指令（✅ 主路径）

| 范围 | 结果 |
|------|------|
| `ember_*.yml` 玩家 lore「§8命令：/corerpg …」 | ✅ 清掉 **42** 行 |
| 团本 lore 教 `/dp start` | ✅ →「点击进本」 |
| 灾厄测本 lore | ✅ →「仅管理/测试 · 正式请走奔赴」 |
| 誓约「建议命令」 | ✅ →「点击选定」 |
| 底层 `command:` | ✅ **保留**（菜单执行，不教手打） |

抽查：玩家主路径 lore/tell 无 `/dp start` / `/mvtp` / `请执行 /corerpg`。管理测句仅灾厄测钮。

---

## B0.2 天赋二层 TTK（⏳ 挂测窗）

未本轮执行。验收仍按 backlog：一层满 vs 二层满 × 周本 Boss / 团使徒 / 深渊 10·12；ΔTTK **&lt;15%** 或回调复测。**不**借机改掉落/票。  
建议与 B1.3 精英 TTK 同测窗。

---

## B0.4 挂机日顶 25% 无二档（📎 标债）

**本轮不做**（总控未批经济改曲线）。  
声明：超顶后固定 25% 递减，长时间挂机仍近似线性（约未封顶 1/4 产速）——**不宣称「挂机已封死通胀」**。若做：见 backlog B2.5，需曲线表 + 2h 产出对比。

---

## 改动文件（摘要）

- `CoreRpg/.../TicketEntryService.java`（新）· `EliteService.java` · `CoreRpgPlugin.java` · `CoreRpgExpansion.java` · `pom.xml` / `plugin.yml` → **1.15.8**
- `plugins/CoreRpg.jar`
- `plugins/DungeonPlus/dungeon/Ember{Daily,Weekly,Abyss,Raid,EliteWeekly}/option.yml` · `README-ember-dungeons.md`
- `plugins/TrMenu/menus/ember_*.yml`（进本 command + B0.3 lore）
- `plugins/LuckPerms/ember-lp-setup-snapshot.json`（`dungeon.start=false`；线上已 `/lp … set false`）
- 物品侧既有：`docs/status/STATUS-ember-ticket-ni-audit.md` + NI/TrMenu/DP 文案（先前提交）

痕迹：`/tmp/b0-ticket-enter-result.json` · `/tmp/b0-lp-reload.txt`

---

## 请总控 / 下游

1. **P5** 可按已批稿 `docs/design/design-ember-calamity-raid-maps-p5.md` 派插件施工（**不必等 P4 测回报**）。  
2. 测试岗：排 **B0.2 + B1.3** 测窗。  
3. 经济：B0.4 仍挂账，勿对外说挂机通胀已封死。

**本轮未做：** P5 地图施工、B0.2 bot、B0.4 曲线、commit 以外的 Paper/MM 数值。
