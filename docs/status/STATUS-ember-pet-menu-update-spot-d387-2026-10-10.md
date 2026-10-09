# 余烬 · D387：使魔菜单 update 同会话复测 · 线上抽测

- **性质：观察期薄复测 · `ember_pet` I/S/E `update:20` · 投喂后同会话 lore 即时变 · 未改配置 / 未动开关 / 未改 feed.* / 未关观察**
- 上游施工：[`STATUS-ember-pet-menu-update-d387-2026-10-10.md`](STATUS-ember-pet-menu-update-d387-2026-10-10.md) tip **`72d90063`**
- 旁注债：D384+D385 spot [`STATUS-ember-pet-level-papi-spot-d384-d385-2026-10-10.md`](STATUS-ember-pet-level-papi-spot-d384-d385-2026-10-10.md) @`61cce6c7` · V3 同会话 lore 旧一档
- 现态：bv**62** · `enabled=true` · `migrate=true` · `set_bonus=true` · play PID **906214** · jar **`1.65.102-d384.local`**
- 测号：`D384Pet`（有宠）；经代理 joinPlay；**未动真人档**
- 证据：`/workspace/tmp/d387-pet-update/`（`00-precheck.json` · `10-same-session.json` · `90-endstate.json` · `99-results.json` · `run.log`）
- 执行：2026-10-10 04:04–04:05 CST
- **结论：PASS** — 同会话投喂后 stay-open ≤0.5s 与关页重开 / hub 回跳，I/S/E `level_line` 均即时变；D384 V3 旁注消

## 人话

施工给 I/S/E 补了 `update: 20` 并 `trmenu reload`（70 菜单）。有宠号开使魔页见 `Lv.4/10`，点魂尘投喂聊天 `Lv.4→5`；**不重进服**，菜单还开着 ≤0.5s 已刷成 `Lv.5/10`，关页再开与经枢纽回跳同为 `Lv.5/10`。对照 D384 同会话仍卡在旧档，旁注已消。`pet.yml` / 三开关 / bv62 / jar 未动。观察不关。

## 验收表

| ID | 项 | 结果 | 证据 |
|----|----|------|------|
| **T0** | tip `72d90063` + I/S/E `update:20` + reload | **PASS** | tip in history；菜单 md5 `0eb07e73…`；`update: 20`×3；reload「70 个菜单已加载 (110 ms)」@04:04:23 CST · `00-precheck.json` |
| **T1** | 投喂前 I/S/E 见真等级行 | **PASS** | I/S=`Lv.4/10` · hint=`下一级需要魂尘×4` · E=`选定：余烬灰灵 · Lv.4/10` · `10-same-session.json` before |
| **T2** | 投喂 chat + PAPI 即时变 | **PASS** | chat `[使魔] 消耗魂尘×4 · 余烬灰灵 Lv.4→5`；`/papi parse me` → `Lv.5/10` / `下一级需要魂尘×5` |
| **T3** | **同会话不重进服** stay-open 刷新 | **PASS** | feed 后 ≤500ms I 已 `Lv.5/10`（战力加值 8→9）· stayPolls · stayOpenFinal |
| **T4** | 同会话关页重开 + hub 回跳 | **PASS** | reopen / hubBounce I/S/E 均 `Lv.5/10` · hint×5 |
| **T5** | 对照 D384 V3 旁注消 | **PASS** | 旧 `61-same-session.json`：before=afterA=`Lv.3/10`（stale）；新 before=`Lv.4` → stay/reopen=`Lv.5` |
| **T6** | 零改 feed.* / 开关 / bv / jar | **PASS** | `pet.yml` md5 仍 `ee8bef6e…`；ember-v1 `48b96322…`；三 true · bv62 · jar d384 · PID 906214 |

## tip / 产物

| 项 | 值 |
|----|-----|
| 施工 tip（D387） | **`72d90063`** |
| `ember_pet.yml` md5 | `0eb07e739dff57d7654ada8e6bd01d46` |
| `pet.yml` md5 | `ee8bef6eef1ee8e11fff336c79185a02`（未变） |
| jar | `1.65.102-d384.local` |
| 分支 | main |

## 手法与注记

- mineflayer `D384Pet` + `lib/proxy-login`；脚本 `/workspace/tmp/d387-pet-update/d387-same-session.js`
- 开菜单：console `trmenu open ember_pet D384Pet`；投喂：菜单 S 格点击（=`corerpg pet feed`）
- 造条件：`ni give` 魂尘×16；**未**改 feed 公式 / 开关 / bv / jar / 菜单（本号只读验）
- **未**切分支 / stash / reset / `checkout -- .`；login/proxy/MariaDB **未碰**
- **≠关观察**（满窗仍须 ≥2026-10-10 17:40 CST）

## 结束态

| 项 | 值 |
|----|-----|
| set_bonus / enabled / migrate | true / true / true |
| balance_version | 62 |
| play PID | 906214 |
| jar | 1.65.102-d384.local |
| tip | 72d90063 |

## 总控签字栏（待签）

| 项 | 签认 |
|----|------|
| **D387 薄复测 PASS** | [ ] |
| tip `72d90063` | [ ] |
| 同会话 level_line 即时变（V3 旁注消） | [ ] |
| 零改 feed.*·开关·bv·jar·配置 | [ ] |
| ≠关观察（满窗仍须 ≥2026-10-10 17:40 CST） | [ ] |
| 签字 / 日期 | |

---

*D387 薄复测 · PASS · tip `72d90063` · jar `1.65.102-d384.local` · 证据 `/workspace/tmp/d387-pet-update/` · ≠关观察。*
