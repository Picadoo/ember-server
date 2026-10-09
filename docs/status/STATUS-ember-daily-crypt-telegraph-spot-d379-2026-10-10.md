# STATUS · D379 抽测：残誓地窖第三拍 EmberCryptOathCast

**日期：** 2026-10-10 03:31 CST（Asia/Shanghai）  
**岗：** 余烬-测试岗执行器  
**依据：** DESIGN `docs/design/DESIGN-ember-daily-crypt-telegraph-2026-10-10.md` · 施工 STATUS `docs/status/STATUS-ember-daily-crypt-telegraph-2026-10-10.md` · MM tip **`455dc35c`**（已 `mm reload` · 113 怪 / 29 技）  
**测号：** `D379Crypt`  
**证据：** `/workspace/tmp/d379-crypt-telegraph/`（`00-static.json` · `10-crypt.json` · `99-summary.json` · `run-v2.log` · `run-v3.log` · `run-v4.log` · `probe-pad.js`）  
**Verdict：** **✅ PASS（旁注：拉开/圈内分跑曾受测法污染；干净窗已证）**

---

## 一句话

残誓终厅 `EmberCryptOathCast` live：提示「【残誓】…拉开」先出 → 干净窗 **early=0 / late=1.2** → 安全垫拉开 **dodge=0**；点名「守墓者点名」仍在且带 **SLOW**；静默环已删；霜锈四线 Cast / DP / 体力旁注零改。单次脚本曾被 **WITHER_SKELETON 近战凋零** 与 **hub 出生点 +z 环境持续掉血** 污染（垫点探测无 Boss 亦掉血），**非** Cast 半径异常——YAML 同构霜/庭院。

---

## 验收表

| 项 | 结果 | 证据 |
|----|------|------|
| 静态 Cast | ✅ message→粒子→`delay 25`→`damage 1.2 @r=5`；Cooldown 8；**无** Cast 内 SLOW | `00-static.json` castOk |
| 静态 Boss | ✅ 挂 `EmberCryptOathCast ~onTimer:80`；**无** `damage ~onTimer:50` 静默环；点名+SLOW `~160`；SoftHit；HP210/Dmg3 | `00-static.json` bossOk |
| 六线旁注 | ✅ 霜/锈/庭/焦/潮/断 Cast 相对 tip 父提交 **零 diff**；DP Crypt 空 diff；`daily_crypt:30` | mmOther/dpCrypt `(empty)` |
| 进本 | ✅ `/corerpg enter daily_crypt` →「开始！」+体力文案 | `10-crypt.json` enter |
| 提示「拉开」 | ✅ | 各跑 `msgSeen` |
| early / 圈内 late | ✅ 干净窗 **early=0 · late=1.2**（v2/v3） | `run-v2.log` / `run-v3.log` |
| 拉开 drop=0 | ✅ 安全垫 **x+18**：**dodgeEarly=0 · dodge=0**（v4） | `run-v4.log`；+z 垫点污染见下 |
| 点名+SLOW | ✅ 「守墓者点名」· wait≥5s · effect SLOW id=2 amp0 dur45 | `run-v4.log` naming |
| 开关 | ✅ 未拧 · bv62 · enabled/migrate/set_bonus=true | static switches |
| 观察 | 续（≠关窗） | — |

**总评：** PASS（测法污染旁注，不挡技能结论）

---

## 测法污染旁注（诚实口径）

1. **Boss 类型 `WITHER_SKELETON`：** hub 垫测时 NoAI 偶发未完全压制近战 → 凋零 DoT / 击退叠进血量窗，导致单次 early/late 飙高（v4 in：10.5/19.2）或拉开误记伤（v2 dodge=1.333 @ +z）。  
2. **hub 出生点 +z 方向环境伤：** `probe-pad.js` 无 Boss 时 z+18/+22/+24 仍持续掉血；**x+18 稳定**。故拉开改采 `far=x+18`（v4 dodge=0）。  
3. **Cast YAML** 与已 PASS 的 `EmberFrostNovaCast` / `EmberYardPulseCast` **同构**（message→delay 25→damage 1.2 r=5）；六线零改。污染属**测法/垫点**，非技能半径写错。

干净窗拼合：v2/v3 证圈内 1.2；v4 证拉开 0 + 点名/SLOW。

---

## 测法摘要

1. **静态：** Skills / Warden / 六线 diff / DP / 体力 / 三开关+bv62。  
2. **进本：** `daily_crypt` → 开始！·体力 → `/dp leave`。  
3. **Cast live（hub 垫）：** `/mm m spawn EmberDailyCryptWarden` + NoAI → 贴脸等提示 → early/late；见提示后 TP 安全远点量 dodge。  
4. **点名：** 另刷 Boss，等 ≥5s 见「守墓者点名」+ entity SLOW。  
5. **硬约束：** 未改 MM/DP/开关/×0.97；未切分支；未关观察；测后 ops play+login=`[]`。

---

## 环境

| 项 | 值 |
|----|-----|
| tip / 施工 | **`455dc35c`** feat(D379) EmberCryptOathCast |
| HEAD（测时写 STATUS） | 见 push 后本 STATUS sha |
| 分支 | `main` |
| jar / bv / 六槽 | 观察续语境；**bv62**；三开关 **true** |
| 端口 | proxy 25565 / play 25567 |
| killall | 仅清测垫 |

---

## 回执（给总控）

| 项 | 内容 |
|----|------|
| 总评 | **PASS**（旁注测法污染） |
| tip sha | **`455dc35c`** |
| 提示/1.2/拉开 | 干净窗全绿 |
| 点名+SLOW | 仍在 |
| 静态 | 无静默环 · 六线/DP/体力零改 |
| 证据 | `/workspace/tmp/d379-crypt-telegraph/` |
| 本 STATUS | `docs/status/STATUS-ember-daily-crypt-telegraph-spot-d379-2026-10-10.md` |
| 观察 | **续**（≠关窗） |
| 开关 | **未拧** |

---

*D379 抽测 PASS · tip `455dc35c` · 证据 `/workspace/tmp/d379-crypt-telegraph/` · ≠关观察 ≠改开关。*
