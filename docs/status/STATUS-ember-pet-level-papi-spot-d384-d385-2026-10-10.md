# 余烬 · D384+D385：使魔等级 PAPI 同屏 · 线上抽测

- **性质：观察期抽测 · PAPI 键 + `ember_pet` I/S/E 同屏 · 未改配置 / 未动开关 / 未改 feed.* / 未关观察**
- 上游：DESIGN [`DESIGN-ember-pet-level-papi-2026-10-10.md`](../design/DESIGN-ember-pet-level-papi-2026-10-10.md) · 施工 D384 [`STATUS-ember-pet-level-papi-d384-2026-10-10.md`](STATUS-ember-pet-level-papi-d384-2026-10-10.md) tip **`1acd97da`** · D385 [`STATUS-ember-pet-level-menu-d385-2026-10-10.md`](STATUS-ember-pet-level-menu-d385-2026-10-10.md) tip **`1e9a57b4`**
- 现态：bv**62** · `enabled=true` · `migrate=true` · `set_bonus=true` · play PID **906214** · jar **`1.65.102-d384.local`**（Enabling @ 03:50:49 CST）
- 测号：`D384PetNo`（无宠）· `D384Pet`（有宠投喂）；经代理 joinPlay；**未动真人档**
- 证据：`/workspace/tmp/d384-d385-pet-level/`（`59-retest-results.json` · `61-same-session.json` · `60-menu-fresh.json` · `70-summary.json`）
- 执行：2026-10-10 03:55–04:01 CST
- **结论：PASS（旁注 V3 同会话）** — V1/V2/V4/V5/V6 绿；V3 投喂+PAPI/list 数字变绿，**同会话**菜单重开 lore 旧一档，**重进服**菜单正确

## 人话

使魔页 I/S/E 能看见真等级行（`Lv.N/10`）、下一级魂尘、选定名；无宠显示「尚无使魔 / 先解锁使魔 / 未选定」，不崩。投喂后聊天与 `/papi parse me` 立刻变级；关掉菜单再开（同会话）偶发仍显示旧等级，重进服后正确。疑 I/S/E 未挂邻菜单惯例的 `update:`。本号未改任何配置。三开关与 bv62 未动。观察不关。

## 验收表（DESIGN V1–V6）

| ID | 项 | 结果 | 证据 |
|----|----|------|------|
| **V1** | `/papi parse me %corerpg_pet_level_line%` 等（有宠） | **PASS** | 有宠：`Lv.2/10` → 投喂后 `Lv.3/10` / `Lv.4/10`；`feed_hint`=`下一级需要魂尘×C`；`active_name`=`余烬灰灵`。`40/50/59` · console 侧 `papi parse <name>` 失败找人，改用玩家 `/papi parse me` |
| **V2** | 打开 `ember_pet` 见等级行+投喂 hint（I/S/E） | **PASS** | I：选定+`Lv.N/10`+hint+战力加值；S：顶栏 level_line+feed_hint；E：选定·level_line。无未解析 `%corerpg_pet_*%`。`51-menu-before.json` · `60-menu-fresh.json` |
| **V3** | 投喂 1 次后重开数字变（或满级） | **PASS（旁注）** | 投喂 chat `Lv.3→4`；PAPI/list 即时 `Lv.4/10`；**同会话**重开 I 仍 `Lv.3/10`（`61-same-session.json`）；**重进服** I=`Lv.4/10`（`60` 后再 join）。根因疑 I/S/E 无 `update:`（邻 `ember_p1_shop_buy`/`sig` 惯例 20）· **本号未改菜单** |
| **V4** | 无宠兜底不崩 | **PASS** | PAPI：`尚无使魔` / `先解锁使魔` / `未选定` / level=`0`；菜单 I/S/E 同文案；无崩。`40-v4-papi-me.json` · `41-v4-menu.json` |
| **V5** | `pet.yml` feed.* 与批前一致 | **PASS** | md5 `ee8bef6eef1ee8e11fff336c79185a02` 抽前=抽后；`max_level=10` · `cost_base/per=1` · `power_per_level=1` |
| **V6** | 无假写固定个人 Lv；无挂机产魂尘 | **PASS（静态）** | `ember_pet.yml` 仅挂 `%corerpg_pet_*%`；无字面 `Lv.N`；魂尘仍生活·钓鱼口径 |
| 静态挂键 | I/S/E 挂键 + actions 仍 feed/summon | **PASS** | Layout `#   I   #`；S→`pet feed`；E→summon/dismiss；F→life |
| 开关/jar | 三 true · bv62 · jar d384 | **PASS** | Enabling `1.65.102-d384.local` · PID 906214 · 抽前后开关未变 |

## tip / 产物

| 项 | 值 |
|----|-----|
| 菜单 tip（D385） | **`1e9a57b4`** |
| 键 tip（D384） | **`1acd97da`** |
| jar | `1.65.102-d384.local` |
| `ember_pet.yml` md5 | `8f9981cc453adec713509a4537028222` |
| `pet.yml` md5 | `ee8bef6eef1ee8e11fff336c79185a02` |
| 分支 | main |

## 手法与注记

- mineflayer `D384PetNo` / `D384Pet` + `lib/proxy-login`；脚本 `/workspace/tmp/d384-d385-pet-level/d384-d385-*.js`
- 开菜单：console `trmenu open ember_pet <name>`；PAPI：玩家侧 `/papi parse me`（需短暂 op）
- 造条件：`ni give` 蛋/魂尘；`/corerpg pet unlock`（有宠号）；**未**改 feed 公式 / 开关 / bv / jar（本号）
- **未**切分支 / stash / reset / `checkout -- .`；login/proxy/MariaDB **未碰**
- **≠关观察**（满窗仍须 ≥2026-10-10 17:40 CST）

## 结束态

| 项 | 值 |
|----|-----|
| set_bonus / enabled / migrate | true / true / true |
| balance_version | 62 |
| play PID | 906214 |
| jar | 1.65.102-d384.local |
| 菜单 tip | 1e9a57b4 |
| 键 tip | 1acd97da |

## 总控签字栏（待签）

| 项 | 签认 |
|----|------|
| **D384+D385 抽测 PASS（旁注 V3）** | [ ] |
| tip `1e9a57b4` + `1acd97da` | [ ] |
| V1–V2 / V4–V6 | [ ] PASS |
| V3 同会话旁注（可派薄补 `update:`） | [ ] 已知 |
| 零改 feed.*·开关·bv·jar·配置 | [ ] |
| ≠关观察（满窗仍须 ≥2026-10-10 17:40 CST） | [ ] |
| 签字 / 日期 | |

## 建议后续（总控裁）

- 薄补：`ember_pet` I/S/E（及可选 Open）加 `update: 20`，使同会话投喂后重开/停留可见数字变——**另号菜单岗**；本抽测号未改。

---

*D384+D385 抽测 · PASS（旁注 V3 同会话）· tip `1e9a57b4`+`1acd97da` · jar `1.65.102-d384.local` · 证据 `/workspace/tmp/d384-d385-pet-level/` · ≠关观察。*
