# STATUS · 余烬体力 S0 · 独立正式短验收

**日期：** 2026-09-28 00:51（Asia/Shanghai）  
**岗：** 余烬-测试岗执行器  
**依据：** `docs/STATUS-ember-stamina-s0.md`；设计 `docs/design-ember-stamina-dnf-daily.md` §A；总控四条  
**CoreRpg：** **1.15.13**（play 日志 `CoreRpg 1.15.13 enabled`…`stamina/…`；jar `plugins/CoreRpg.jar`。插件 STATUS 写 1.15.12，复验窗口内 jar 已抬到 1.15.13，体力特性仍加载）  
**Verdict：** **✅ PASS**（四条总控全绿）  
**S1：** **未测**（总控：不要测 S1 分房）  
**数值：** **未改**（仅 admin `stamina set` / `progress` 推级，未改 YAML）

---

## 环境

| 项 | 值 |
|----|-----|
| 路径 | `/workspace/minecraft` |
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |
| 端口 | 25565 proxy / 25566 login / 25567 play |
| 登录 | `mineflayer-tests/lib/proxy-login.js` |
| 临时 OP | `St0tOp`（ops.json + `/lp … corerpg.admin`；测后 deop + unset + `ops=[]`） |
| 验收号 | `St0tAc80`（非 OP，推至 Lv.28） |
| JSON | `/tmp/stamina-s0-test.json` |

---

## 四条总控

| # | 条 | 结果 | 证据要点 |
|---|----|------|----------|
| 1 | 菜单显示体力当前/上限；玩家可见 **无** `/dp` `/corerpg stamina` 教学 | ✅ **PASS** | TrMenu `ember_daily/weekly/life/hub/…`：name/lore 含 `%corerpg_stamina%`/`_max`；静态扫描 teachHits=**0**；`/ember` 枢纽 lore 可见「消耗 N 体力」等（无手打指令教学） |
| 2 | 进日常扣 **30**；不足拒且不扣 | ✅ **PASS** | `[日常] 正在进入……（体力 -30）` → 落地 `(0,65,0)` → show **60/90**；set **20** 后再进 → `体力不足（需 30，当前 20/90）` · 仍枢纽 · 仍 **20/90** |
| 3 | 周本本周首次免费文案；再进扣 **45**（遇 DP CD 可只验文案） | ✅ **PASS** | `[周本] …（本周首次免费）`；再进 `[周本] …（体力 -45）`（随后 DP「挑战过快」CD，文案已够） |
| 4 | 生活菜单旧票兑换路径可点（无票时提示即可） | ✅ **PASS** | `ember_life.yml` 按钮「旧票 → 余烬体力」→ `command: corerpg stamina convert`；触发后 `[体力] 背包中没有可兑换的旧票` |

---

## 检查明细（摘要）

- `admin_stamina_set_ok`：set 77 → show 77/90（确认 LP `corerpg.admin`，因 default 组显式拒 admin）
- `enter_daily_deduct_30_msg` / `stamina_after_daily_60`
- `enter_daily_refuse_msg` / `enter_daily_refuse_no_deduct`
- `weekly_first_free_msg` / `weekly_second_cost_45_msg`
- `convert_no_ticket_tip` + life 菜单接线

未覆盖（非本短验收范围）：S1 分房、进本失败退还造失败、PAPI `/papi parse` 冒烟（插件 STATUS 已记软债务）。

---

## ops 终态

- `server-runtime/ops.json` = **`[]`**
- `login-runtime/ops.json` = **`[]`**
- `St0tOp`：`/deop` + LP `parent remove admin` + `permission unset corerpg.admin`

---

## 回报主代理 / 总控

- **总评：** **PASS**
- **四条：** 1 PASS · 2 PASS · 3 PASS · 4 PASS
- **CoreRpg：** **1.15.13**（相对插件 STATUS 的 1.15.12 为同线抬版本；体力已加载）
- **ops：** `[]`
- **报告：** `docs/STATUS-ember-stamina-s0-test.md`
- **JSON：** `/tmp/stamina-s0-test.json`
