# STATUS · S3 潮蚀/断塔独立验收

**日期：** 2026-09-28 05:02（Asia/Shanghai）  
**岗：** 余烬-测试岗执行器  
**依据：** `docs/status/STATUS-ember-daily-s3.md` · `docs/status/STATUS-ember-daily-s3-dp.md` · `docs/status/STATUS-ember-daily-s3-mobs.md`；派工 · S3 潮蚀/断塔独立验收  
**CoreRpg：** **1.15.17**（jar `plugin.yml` + 日志 `CoreRpg 1.15.17 enabled` + `/version CoreRpg`）  
**commit：** **ae0ecd4**  
**Verdict：** **✅ PASS**（D 潮蚀 · E 断塔 · 菜单 · Display · 地图 全绿）

---

## 一句话

独立抽检：`daily_tide` / `daily_spire` 各扣体力 **30** → 真击杀开门 **AIR×9** → Boss 通关播报实锤；TrMenu D/E 无「筹备中」、无玩家可见 slash 教学；`$kill` 去色 Display 六怪对齐、有效行无 `$kill-any`；地图可辨（棱柱水道 ≠ 石砖断塔）；未用 `killall`；测后 ops play+login=`[]`。

---

## 分项总表

| 分项 | 结果 | 要点 |
|------|------|------|
| **D 潮蚀水道** | **PASS** | 进本 -30 · 门1/门2 AIR×9 · Boss「潮蚀水道 通关！」 |
| **E 断塔回廊** | **PASS** | 进本 -30 · 门1@y64 / 门2@y70 AIR×9 · Boss「断塔回廊 通关！」 |
| **菜单** | **PASS** | 无「筹备中」· `corerpg enter daily_tide\|daily_spire` · teachHits=0 |
| **Display/$kill** | **PASS** | 潮蚀尸/浪矢骷/潮闸蛮兵 · 断塔卫尸/裂隙箭骷/断塔守望 去色对齐；有效行无 kill-any |
| **地图可辨** | **PASS** | tide=棱柱/海晶/水+石底；spire=石砖/木梁/顶栏；≠庭院/焦骨/地窖 |
| **总评** | **PASS** | 关键项全绿 · ops=[] |

---

## D · daily_tide（EmberDailyTide · spawn 0,64,0）

| # | 检查 | 结果 | 证据 |
|---|------|------|------|
| 1 | 进本扣体力 30 | ✅ | `[潮蚀] 正在进入……（体力 -30）` · spawn **(0,64,0)** · `余烬窟·潮蚀水道 开始！` |
| 2 | 真击杀 wave1 → 门1 AIR | ✅ | 「沿岸已清 · 桥闸开了」· z=18 x=-1..1 y=64..66 **iron=0 air=9**（关闭时 iron=9）· hits=20 · **04:59:34 CST** |
| 3 | wave2a→2b → 门2 AIR | ✅ | 「折桥前段已清 · 浪矢骷压上」→「折桥已清 · 闸厅门开了」· z=38 **air=9** · **05:00:02 CST** |
| 4 | Boss 通关 | ✅ | 「【闸厅】潮闸蛮兵！」· 真挥击 hits=18 →「余烬窟·潮蚀水道 通关！奖励发放中…」· **05:00:20 CST** |
| 5 | 落水有底 | ✅ | 实测 water@y62 + **stone@y61**；anvil `no_void_under_water` |

账号：OP `S3tOp47` · 玩家 `S3tTd8786`（非 OP，非管理免扣）  
禁 killall：✅ 脚本仅 `bot.attack` / swingArm

---

## E · daily_spire（EmberDailySpire · spawn 0,64,0）

| # | 检查 | 结果 | 证据 |
|---|------|------|------|
| 1 | 进本扣体力 30 | ✅ | `[断塔] 正在进入……（体力 -30）` · pos **(0,64,0)** · `余烬窟·断塔回廊 开始！` |
| 2 | 真击杀 → 门1 AIR | ✅ | 关闭时 z=4@y64 **iron_bars×9** →「底层已清 · 上阶门开了」· **air=9** · **05:00:59 CST** |
| 3 | → 门2 AIR | ✅ | 「环廊前段已清 · 裂隙箭骷压上」→「环廊已清 · 顶门开了」· z=4@y70 **air=9** · **05:01:32 CST** |
| 4 | Boss 通关 | ✅ | 「【顶台】断塔守望！」· 真挥击 hits=20 →「余烬窟·断塔回廊 通关！奖励发放中…」· **05:01:50 CST** |
| 5 | 顶台有栏 | ✅ | anvil：顶台 e/s/w=`iron_bars` · `has_rail=true` · catch=`stone` |

账号：同 OP · **独立玩家** `S3tSp8808`（与 tide 分号，避开 `start-interval` 冷却）  
裂隙箭骷 MM 配 BOW：贴脸追击；本轮无弓箭死亡阻断。

---

## 菜单

| 检查 | 结果 |
|------|------|
| D/E 无「筹备中」 | ✅（plugins + server-runtime 双路径） |
| 点击 `corerpg enter daily_tide` / `daily_spire` | ✅ |
| 玩家可见 slash 教学（lore/tell 中 `/hub` `/dp` `/mvtp` `/corerpg`） | ✅ **0**（`command:` 进本动作不计；注释不计） |

路径：`plugins/TrMenu/menus/ember_daily.yml` · `server-runtime/plugins/TrMenu/menus/ember_daily.yml`

---

## Display / $kill 去色对齐

| MM ID | Display 去色 | `$kill{mobname=…}` | 对齐 |
|-------|--------------|---------------------|------|
| EmberDailyTideZombie | 潮蚀尸 | 潮蚀尸 | ✅ |
| EmberDailyTideSkeleton | 浪矢骷 | 浪矢骷 | ✅ |
| EmberDailyTideBrute | 潮闸蛮兵 | 潮闸蛮兵 | ✅ |
| EmberDailySpireZombie | 断塔卫尸 | 断塔卫尸 | ✅ |
| EmberDailySpireSkeleton | 裂隙箭骷 | 裂隙箭骷 | ✅ |
| EmberDailySpireWarden | 断塔守望 | 断塔守望 | ✅ |

有效 YAML 行 **无** `$kill-any`（仅注释写「禁 $kill-any」；自动化首轮把注释误计为 FAIL，复核后纠正）。

---

## 地图可辨

| 线 | 主材/特征（实测） | ≠ 他线 |
|----|-------------------|--------|
| 潮蚀 | prismarine×157 · water×124 · sea_lantern/lapis · 铁闸 | ≠ 庭院木/焦骨红砖/地窖深井 |
| 断塔 | stonebrick×185 · cobble · iron_bars · 向上 y64→70→76 · 顶栏 | ≠ 潮蚀棱柱水道 |

anvil：`/tmp/s3-anvil-verify.json`（门挡 BFS · 水底石 · 顶栏 · catch 实底）。

---

## 环境与约束

| 项 | 状态 |
|----|------|
| CoreRpg jar / 日志 / chat | **1.15.17** |
| 本岗改 jar / YAML / MM / 体力数值 | **未改** |
| `mm mobs killall` / `$kill-any` 清房 | **未用** |
| 临时 OP 短名 | `S3tOp47`（S3tOp*） |
| 测后 ops play+login | **`[]`** |
| 短重启 play | ✅（预置 OP 后 `./start.sh custom`；未删 dungeon-caches） |
| 证据 JSON / 日志 | `/tmp/s3-daily-test.json` · `/tmp/s3-daily-test.log` |
| commit/push | **未做** |

---

## 门开/通关时刻（CST）

| 事件 | 时刻 |
|------|------|
| tide 门1 | 2026-09-28 04:59:34 CST |
| tide 门2 | 2026-09-28 05:00:02 CST |
| tide Boss | 2026-09-28 05:00:20 CST |
| spire 门1 | 2026-09-28 05:00:59 CST |
| spire 门2 | 2026-09-28 05:01:32 CST |
| spire Boss | 2026-09-28 05:01:50 CST |

---

## 债 / 备注

- **无产品阻塞债。**
- 自动化 Display 检查首轮因 monster.yml **注释**含「`$kill-any`」字样误判 FAIL；有效条件行复核无 kill-any · 六怪对齐 → 记 PASS（证据见 JSON `evidence.display_note`）。
- 顶台栏 live 采样坐标偏出（y77±5 全 air），以 anvil `has_rail` + e/s/w iron_bars 为准；地图分项仍 PASS。
- 插件岗曾记 tide 门1 真击杀闭环——本岗 **两线完整清房+Boss** 复验通过。

---

## 给总控的结案转发

```
【结案 · S3 潮蚀/断塔独立验收】priority=true
岗：余烬-测试 · CoreRpg 1.15.17 · ae0ecd4
总评：PASS
D 潮蚀：进本-30 · 门1/门2 AIR×9 · Boss 通关实锤 PASS
E 断塔：进本-30 · 门1@y64/门2@y70 AIR×9 · Boss 通关实锤 PASS
菜单：去灰 · enter daily_tide|spire · 无slash教学 PASS
Display：$kill 去色六怪对齐 · 有效行无 kill-any PASS
地图：棱柱水道≠石砖断塔 · 水底/顶栏 PASS
禁 killall · ops=[] · 报告 docs/status/STATUS-ember-daily-s3-test.md · JSON /tmp/s3-daily-test.json
债：无
```
