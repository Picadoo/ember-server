# STATUS · S4 霜晶/锈轨独立验收

**日期：** 2026-09-28 05:33（Asia/Shanghai）  
**岗：** 余烬-测试岗执行器  
**依据：** `docs/status/STATUS-ember-daily-s4.md` · `docs/status/STATUS-ember-daily-s4-dp.md` · `docs/status/STATUS-ember-daily-s4-mobs.md`；派工 · S4 霜晶/锈轨独立验收  
**CoreRpg：** **1.15.18**（jar `plugin.yml` + 日志 `CoreRpg 1.15.18 enabled` + `/version CoreRpg`）  
**commit（功能基线）：** **f802a7a**（`feat(s4): frost/rail maps + DP/menu`）  
**Verdict：** **✅ PASS**（F 霜晶 · G 锈轨 · 菜单 · Display · 地图 全绿）

---

## 一句话

独立抽检：`daily_frost` / `daily_rail` 各扣体力 **30** → 真击杀开门 **AIR×9** → Boss 通关播报实锤；TrMenu F/G 无「筹备中」、无玩家可见 slash 教学；`$kill` 去色 Display 六怪对齐、有效行无 `$kill-any`；地图可辨（冰蓝裂隙 ≠ 锈轨矿道）；未用 `killall`；测后 ops play+login=`[]`。

---

## 分项总表

| 分项 | 结果 | 要点 |
|------|------|------|
| **F 霜晶裂隙** | **PASS** | 进本 -30 · 门1@z16 / 门2@z34 AIR×9 · Boss「霜晶裂隙 通关！」 |
| **G 锈轨矿道** | **PASS** | 进本 -30 · 门1@z15 / 门2@z33 AIR×9 · Boss「锈轨矿道 通关！」 |
| **菜单** | **PASS** | 无「筹备中」· `corerpg enter daily_frost\|daily_rail` · teachHits=0 |
| **体力灰显** | **记债不整项 FAIL** | YAML 有灰态；CoreRpg 拒进实锤；live 菜单灰切换见已知 bug（mustfix-test） |
| **Display/$kill** | **PASS** | 霜晶尸/霜矢骷/霜核蛮兵 · 锈轨尸/矿矢骷/锈轨矿监 去色对齐；有效行无 kill-any |
| **地图可辨** | **PASS** | frost=浮冰/石英/雪+裂隙底；rail=粗石/木梁/铁轨+1支洞；≠潮蚀/焦骨 |
| **总评** | **PASS** | 关键项全绿 · ops=[] |

---

## F · daily_frost（EmberDailyFrost · spawn 0,70,0）

| # | 检查 | 结果 | 证据 |
|---|------|------|------|
| 1 | 进本扣体力 30 | ✅ | `[霜晶] 正在进入……（体力 -30）` · spawn **(0,70,0)** · `余烬窟·霜晶裂隙 开始！` |
| 2 | 体力不足拒进 | ✅ | 体力 10 → `体力不足（需 30，当前 10/90）· 明日 0 点恢复`（CoreRpg 门控） |
| 3 | 真击杀 wave1 → 门1 AIR | ✅ | 关闭时 z=16 **iron_bars×9** →「冻台已清 · 冰闸开了」· **air=9** · hits=22 · **05:30:43 CST** |
| 4 | wave2a→2b → 门2 AIR | ✅ | 「折台前段已清 · 霜矢骷压上」→「折台已清 · 霜厅门开了」· z=34 **air=9** · hits≈14+10 · **05:31:13 CST** |
| 5 | Boss 通关 | ✅ | 「【霜厅】霜核蛮兵！」· 真挥击 hits=17 →「余烬窟·霜晶裂隙 通关！奖励发放中…」· **05:31:31 CST** |
| 6 | 裂隙有底无虚空 | ✅ | live L/R@y64=`packed_ice` · y65=`snow`；anvil `no_void_rift` · `no_water` |

账号：OP `S4tOp88` · 玩家 `S4tFr5938`（非 OP，非管理免扣）  
禁 killall：✅ 脚本仅 `bot.attack` / swingArm

---

## G · daily_rail（EmberDailyRail · spawn 0,64,0）

| # | 检查 | 结果 | 证据 |
|---|------|------|------|
| 1 | 进本扣体力 30 | ✅ | `[锈轨] 正在进入……（体力 -30）` · spawn **(0,64,0)** · `余烬窟·锈轨矿道 开始！` |
| 2 | 真击杀 → 门1 AIR | ✅ | 关闭时 z=15 **iron×9** →「主巷前段已清 · 轨闸开了」· **air=9** · hits=26 · **05:32:17 CST** |
| 3 | → 门2 AIR | ✅ | 「后段前排已清 · 矿矢骷压上」→「后段已清 · 机房门开了」· z=33 **air=9** · hits≈12+12 · **05:32:45 CST** |
| 4 | Boss 通关 | ✅ | 「【机房】锈轨矿监！」· 真挥击 hits=16 →「余烬窟·锈轨矿道 通关！奖励发放中…」· **05:33:02 CST** |
| 5 | 1 短支洞 + 巷道可辨 | ✅ | live spur mid=`rail` · endWall=`cobblestone`；主材 cobble/log/rail；anvil `gallery_wide` · `no_netherrack` |

账号：同 OP · **独立玩家** `S4tRl1631`（与 frost 分号，避开 `start-interval` 冷却）  
矿矢骷 MM 配弓：贴脸追击；本轮无弓箭死亡阻断。

---

## 菜单

| 检查 | 结果 |
|------|------|
| F/G 无「筹备中」 | ✅（plugins + server-runtime 双路径） |
| 点击 `corerpg enter daily_frost` / `daily_rail` | ✅ |
| 玩家可见 slash 教学（lore/tell 中 `/hub` `/dp` `/mvtp` `/corerpg`） | ✅ **0**（`command:` 进本动作不计；注释不计） |
| F/G YAML 体力不足灰态模板 | ✅（gray pane +「0:00 回满」tell，无 enter） |
| live 灰显切换 | ⚠ 已知债（见下）；**不**因该 bug 整项 FAIL |

路径：`plugins/TrMenu/menus/ember_daily.yml` · `server-runtime/plugins/TrMenu/menus/ember_daily.yml`

---

## Display / $kill 去色对齐

| MM ID | Display 去色 | `$kill{mobname=…}` | 对齐 |
|-------|--------------|---------------------|------|
| EmberDailyFrostZombie | 霜晶尸 | 霜晶尸 | ✅ |
| EmberDailyFrostSkeleton | 霜矢骷 | 霜矢骷 | ✅ |
| EmberDailyFrostBrute | 霜核蛮兵 | 霜核蛮兵 | ✅ |
| EmberDailyRailZombie | 锈轨尸 | 锈轨尸 | ✅ |
| EmberDailyRailSkeleton | 矿矢骷 | 矿矢骷 | ✅ |
| EmberDailyRailWarden | 锈轨矿监 | 锈轨矿监 | ✅ |

有效 YAML 行 **无** `$kill-any`（仅注释写「禁 $kill-any」；本岗仅扫有效行）。

---

## 地图可辨

| 线 | 主材/特征（实测） | ≠ 他线 |
|----|-------------------|--------|
| 霜晶 | packed_ice×100 · quartz · snow · ice · 裂隙底 packed_ice@y64 | ≠ 潮蚀棱柱/水面；无大片流水 |
| 锈轨 | cobble×262 · log · mossy_cobble · **rail×13** · 恰 1 支洞（x→12 封墙） | ≠ 焦骨 netherrack 窄筒；巷宽 7（anvil gallery_wide） |

anvil：`/tmp/s4-anvil-verify.json`（门挡 BFS · 裂隙底 · 支洞 · 无水/无地狱岩）。

---

## 环境与约束

| 项 | 状态 |
|----|------|
| CoreRpg jar / 日志 / chat | **1.15.18** |
| 功能基线 commit | **f802a7a** |
| 本岗改 jar / YAML / MM / 体力数值 | **未改** |
| `mm mobs killall` / `$kill-any` 清房 | **未用** |
| 临时 OP 短名 | `S4tOp88`（S4tOp*） |
| 测后 ops play+login | **`[]`** |
| 短重启 play | ✅（预置 OP 后 `./start.sh custom`） |
| 证据 JSON / 日志 | `/tmp/s4-daily-test.json` · `/tmp/s4-daily-test.log` |
| commit/push | **未做** |

---

## 门开/通关时刻（CST）

| 事件 | 时刻 |
|------|------|
| frost 门1 | 2026-09-28 05:30:43 CST |
| frost 门2 | 2026-09-28 05:31:13 CST |
| frost Boss | 2026-09-28 05:31:31 CST |
| rail 门1 | 2026-09-28 05:32:17 CST |
| rail 门2 | 2026-09-28 05:32:45 CST |
| rail Boss | 2026-09-28 05:33:02 CST |

---

## 债 / 备注

- **产品阻塞债：无。**
- **菜单灰显 live bug（已知，不整项 FAIL）：** `STATUS-ember-daily-menu-mustfix-test` 记 YAML 有灰态但 live 不切换；本单 F/G YAML 灰态齐全，**进本门控以 CoreRpg 拒进为准**（体力 10 → 拒进文案实锤）。测后仓库另有 `227b9c1 fix(menu): stamina gray icons…`，live 灰切换是否已愈 **本岗未复验**。
- 首轮跑测曾被他岗（GrayFix*）关服打断（`Server closed`）；清场后短重启重跑全绿。
- 插件岗曾记 frost 门1 真击杀闭环——本岗 **两线完整清房+Boss** 复验通过。

---

## 给总控的结案转发

```
【结案 · S4 霜晶/锈轨独立验收】priority=true
岗：余烬-测试 · CoreRpg 1.15.18 · 基线 f802a7a
总评：PASS
F 霜晶：进本-30 · 门1@z16/门2@z34 AIR×9 · Boss 通关实锤 PASS
G 锈轨：进本-30 · 门1@z15/门2@z33 AIR×9 · Boss 通关实锤 PASS
菜单：无筹备中 · enter daily_frost|rail · 无slash教学 PASS
体力门：CoreRpg 拒进实锤；菜单 live 灰显已知债（不整项 FAIL）
Display：$kill 去色六怪对齐 · 有效行无 kill-any PASS
地图：冰蓝裂隙≠锈轨矿道 · 裂隙底/1支洞 PASS
禁 killall · ops=[] · 报告 docs/status/STATUS-ember-daily-s4-test.md · JSON /tmp/s4-daily-test.json
债：菜单灰显 live（已知；227b9c1 后未复验）
```
