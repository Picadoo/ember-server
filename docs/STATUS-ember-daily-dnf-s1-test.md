# STATUS · S1 日常分房实战验收

**日期：** 2026-09-28 01:40（Asia/Shanghai）  
**岗：** 余烬-测试岗执行器  
**依据：** `docs/STATUS-ember-daily-dnf-s1.md`；设计分房（门廊→房1→铁门1→房2→铁门2→Boss）；总控必验  
**CoreRpg：** **1.15.13**（jar `plugins/CoreRpg.jar`；play 日志 `CoreRpg 1.15.13 enabled`）  
**数值：** **未改**  
**真击杀：** **是**（全程禁止 `mm mobs killall` / 控制台清怪）  
**Verdict：** **❌ FAIL**

---

## 一句话

门廊落地、门1 铁栅挡路、**真击杀**清前厅→「前厅已清 · 门开了」、体力 **-30**、菜单无玩家可见 `/dp`、模板 **门1/门2 各 12 格铁门**均已实锤；wave2 会刷混编并播报「回廊·混编来袭」，但同一次实战 **未能**打出「回廊已清 · Boss 门开了」，故 **≥2 道先清后开**未在单次跑通中闭环 → 总评 FAIL。

---

## 环境

| 项 | 值 |
|----|-----|
| 路径 | `/workspace/minecraft` |
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |
| 端口 | 25565 proxy / 25566 login / 25567 play |
| 登录 | `mineflayer-tests/lib/proxy-login.js` |
| 验收手段 | mineflayer 真攻击；`/tp` 仅用于贴近刷怪点（非 killall） |
| 临时 OP | 多轮 `S1*Op*`；测后 `deop` + `ops.json=[]` |
| JSON | `/tmp/s1-daily-test.json` |

---

## 总控对照

| # | 条 | 结果 | 证据要点 |
|---|----|------|----------|
| 1 | `corerpg enter daily` 落门廊 **(0,65,0)**，朝向第一道铁门；进本后越不过门1（z=13） | ✅ **PASS** | 多次落地 `(0,65,0)`；门1 `iron_bars` **12/12**；前进 `maxZ=0 < 13.2` |
| 2 | **真打**清房1→门开+「前厅已清 · 门开了」；清房2→「回廊已清 · Boss 门开了」 | ⚠️ **半过** | 房1：**PASS**（例：hits=25 · 17s · 播报齐全）。房2：组已启动「【回廊】混编来袭」+ 僵尸×3/骷髅×3 可见，但 **未**出现「回廊已清」 |
| 3 | Boss 独立终厅/垫 (0,66,31)；通关或至少两道门机制 | ❌ **未闭环** | 门2 机制未在实战中验证开门；Boss 未打 |
| 4 | 菜单无玩家可见 `/dp` 教学；体力仍扣 **30** | ✅ **PASS** | `ember_daily.yml` 玩家可见行 teachHits=**0**；B/C「筹备中」；进本前后体力 **90→60** |
| 5 | 证明存在 **≥2** 道先清后开（非单院三波） | ⚠️ **设计/模板 PASS · 实战闭环 FAIL** | 模板 MCA 门1z=13 / 门2z=25 铁门各 **12**；实战只打通第 1 道开门链 |

---

## 关键坐标（与插件 STATUS 一致）

| 角色 | 坐标 |
|------|------|
| spawn / 门廊 | **(0, 65, 0)** |
| 门1 | **z=13**，x=-2..1，y=65..67（IRON_FENCE / iron_bars ×12） |
| 门2 | **z=25**，同上 ×12 |
| Boss 垫 | **(0, 66, 31)** |

---

## 最佳连续跑证据（S1xAc9831）

- 进本：`[日常] 正在进入……（体力 -30）` + `门廊安全 · 清前厅开门`
- 门1：iron 12 · `cannot_pass=true`
- wave1：**真击杀** hits=25 → `前厅已清 · 门开了`（ms≈17020）
- wave2 start：`【回廊】混编来袭 —— 先清远程再推 Boss 门`
- wave2 clear：**未达成**（≈313 次挥击仍无「回廊已清」；门2 抽样仍为 iron_bars）
- 体力：`90/90` → `60/90`

补充探针（S1w2Ac769）：wave1 清后 8s 内见回廊播报，实体列表 3 僵尸 + 3 骷髅落在 z≈17..22（与 `monster.yml` 一致）。

---

## 失败归因（测试侧 / 可能产品侧）

1. **回廊真击杀不稳定**：mineflayer 在实例内实体坐标偶发漂移；wave1 的 `$kill` 已多次打通，wave2 的 `$kill-any{灰烬庭院僵尸,灰烬庭院骷髅;amount=6}` 未在自动化中记账成功。  
2. **禁止** `mm mobs killall`（不计 DP `$kill`）——无法用控制台捷径冒烟门2。  
3. 曾误 `rm` 活跃 `dungeon-caches` 导致 8KB 空 region（落地掉虚空）；已重启 play 并改为只清空壳缓存。  
4. `ni give gear_ember_t2_blade` 需 NeigeItems 权限；本轮多用新手「余烬之刃」（lore 含 `物理伤害: +8`）。

**建议插件岗：** 人工进本抽检回廊击杀→门2；或确认 `$kill-any` 对混编 Display 名是否与 `$kill` 同等可靠。

---

## ops 终态

- `server-runtime/ops.json` = **`[]`**
- `login-runtime/ops.json` = **`[]`**

---

## 回报主代理 / 总控

- **总评：** **FAIL**
- **落地 (0,65,0)：** PASS · **门挡 z=13：** PASS · **两道门实战闭环：** FAIL（仅门1） · **Boss：** 未验 · **体力-30：** PASS · **菜单无/dp：** PASS
- **CoreRpg：** **1.15.13**
- **ops：** `[]`
- **报告：** `docs/STATUS-ember-daily-dnf-s1-test.md`
- **JSON：** `/tmp/s1-daily-test.json`
