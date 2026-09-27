# STATUS · S2 焦骨/地窖独立验收

**日期：** 2026-09-28 04:12（Asia/Shanghai）  
**岗：** 余烬-测试岗执行器  
**依据：** `STATUS-ember-daily-s2-dp.md`（插件结案）；派工 · S2 焦骨/地窖独立验收  
**CoreRpg：** **1.15.15**（jar `plugin.yml` + 日志 `CoreRpg 1.15.15 enabled` + `/version CoreRpg`）  
**Verdict：** **✅ PASS**（A 焦骨 · C 残誓 · 菜单 全绿）

---

## 一句话

独立抽检：`daily_ash` / `daily_crypt` 各扣体力 30 → 真击杀开门 AIR×9 → Boss 通关播报实锤；TrMenu B/C 无「筹备中」、无玩家可见 `/dp`；未用 `mm mobs killall`；测后 ops play+login=`[]`。

---

## 分项总表

| 分项 | 结果 | 要点 |
|------|------|------|
| **A 焦骨甬道** | **PASS** | 进本 -30 · 门1/门2 AIR×9 · Boss 通关「焦骨甬道 通关！」 |
| **C 残誓地窖** | **PASS** | 进本 -30 · 井口 (0,72,0) · 门1/门2 AIR×9 · Boss 通关「残誓地窖 通关！」 |
| **菜单** | **PASS** | 无「筹备中」· `corerpg enter daily_ash\|daily_crypt` · teachHits(`/dp`)=0 |
| **总评** | **PASS** | 关键项全绿 |

---

## A · daily_ash（EmberDailyAsh · spawn 0,65,0）

| # | 检查 | 结果 | 证据 |
|---|------|------|------|
| 1 | 进本扣体力 30 | ✅ | `[焦骨] 正在进入……（体力 -30）` · spawn (0,65,0) · `余烬窟·焦骨甬道 开始！` |
| 2 | 真击杀 wave1 → 门1 AIR | ✅ | 「前段已清 · 门开了」· z=16 x=-1..1 y=65..67 **iron=0 air=9**（关闭时 iron=9） |
| 3 | wave2a→2b → 门2 AIR | ✅ | 「弯折前段已清 · 燃矢骷压上」→「弯折已清 · 鼓室门开了」· z=36 **air=9** |
| 4 | Boss 通关 | ✅ | 「【鼓室】焦核蛮兵！」· 真挥击 →「余烬窟·焦骨甬道 通关！奖励发放中…」 |

账号：OP `S2tOp82` · 玩家 `S2tAc3977`  
禁 killall：✅ 脚本仅 `bot.attack` / swingArm

---

## C · daily_crypt（EmberDailyCrypt · spawn 0,72,0）

| # | 检查 | 结果 | 证据 |
|---|------|------|------|
| 1 | 进本扣体力 30 · 井口 | ✅ | `[残誓] 正在进入……（体力 -30）` · pos **(0,72,0)** · `残誓地窖 开始！` |
| 2 | 真击杀 → 门1 AIR | ✅ | 关闭时 z=18@y72 **iron_bars×9** →「上层已清 · 下阶门开了」· **air=9** |
| 3 | → 门2 AIR | ✅ | 「中层前段已清 · 誓印骷压上」→「中层已清 · 底层门开了」· z=40@y66 **air=9** |
| 4 | Boss 通关 | ✅ | 「【圆厅】残誓守墓！」· 真挥击 →「余烬窟·残誓地窖 通关！奖励发放中…」 |

账号：同 OP · **独立玩家** `S2tAc1379`（与 ash 分号，避开 `start-interval` 冷却）  
誓印骷 MM 配 BOW：贴脸追击；本轮无弓箭死亡阻断。

---

## 菜单

| 检查 | 结果 |
|------|------|
| B/C 无「筹备中」 | ✅（plugins + server-runtime 双路径） |
| 点击 `corerpg enter daily_ash` / `daily_crypt` | ✅ |
| 玩家可见行 `/dp`（teachHits） | ✅ **0**（注释行不计） |

路径：`plugins/TrMenu/menus/ember_daily.yml` · `server-runtime/plugins/TrMenu/menus/ember_daily.yml`

---

## 环境与约束

| 项 | 状态 |
|----|------|
| CoreRpg jar / 日志 / chat | **1.15.15** |
| 本岗改 jar / YAML / MM / 数值 | **未改** |
| `mm mobs killall` | **未用** |
| 临时 OP 短名 | `S2tOp82`（S2tOp*） |
| 测后 ops play+login | **`[]`** |
| 短重启 play | ✅（首轮热写 ops 未进内存 → 预置 OP 后短启；未删 dungeon-caches） |
| 证据 JSON / 日志 | `/tmp/s2-daily-test.json` · `/tmp/s2-daily-test.log` |

---

## 债 / 备注

- **无阻塞债。** 首轮（热写 ops 未重启）因 OP 未加载导致 wave2 远距无法 `/tp` 而 FAIL，属测试基建；短重启后复测全绿，不记产品债。
- 插件岗曾记「联合跑 crypt 撞 start-interval」——本岗 ash/crypt **分号** + leave 后间隔，未复现冷却 FAIL。
- EmberAshSkeleton=木剑；EmberDailyCryptSkeleton=**BOW**（贴脸即可，非产品缺陷）。

---

## 给总控的结案转发

```
【结案 · S2 焦骨/地窖独立验收】priority=true
岗：余烬-测试 · CoreRpg 1.15.15
总评：PASS
A 焦骨：进本-30 · 门1/门2 AIR×9 · Boss 通关实锤 PASS
C 残誓：进本-30 · 井口 · 门1/门2 AIR×9 · Boss 通关实锤 PASS
菜单：去灰 · enter daily_ash|crypt · 无/dp PASS
禁 killall · ops=[] · 报告 STATUS-ember-daily-s2-test.md · JSON /tmp/s2-daily-test.json
债：无
```
