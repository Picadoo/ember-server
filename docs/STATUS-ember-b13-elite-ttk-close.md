# STATUS · B1.3 精英 Boss TTK 关账采数 A

**日期：** 2026-09-28 19:28～19:38（Asia/Shanghai / CST）  
**岗：** 余烬-测试岗执行器  
**依据：** `docs/design-ember-b13-elite-boss-ttk.md`（总控批 A · tip `1b38ddd` · 设计 `de3287b`）  
**对照：** 4.4f（Boss~70s / 全本~510s）· B13 图修复测（Boss 63s / 全本 594s / 剩血 58%）  
**脚本：** `/tmp/elite-b13-ttk-close.js` · 原始 JSON `/tmp/elite-b13-ttk-close.json` · 日志 `/tmp/elite-b13-ttk-close.log`  
**账号：** 主测 **`B13Tk192741`**（非 op）；临时管理 **`B13TkOp6`**（测后已清）  
**CoreRpg：** **1.15.21**  
**未改：** MM `EmberEliteBoss` 5200/12 · Pulse/Rush · 波次 HP · `EmberEliteWeekly` monster/option · 票/奖励

---

## 总评

# **PASS**（勾销 B1.3 · 图修后关账采数，**非砍血**）

| 项 | 结果 | 摘要 |
|----|------|------|
| 进本 `/corerpg elite start` | **PASS** | 「正在进入……（本周首次免费）」→「精英试炼开启」 |
| Boss TTK 45～75s | **PASS** | **65.5s**（Boss@469.8s → 通关@535.3s） |
| 全本 480～720s | **PASS** | **535.3s** |
| 通关前剩血 25～55 / 软≤60 | **观察 62%** | **24.811/40 ≈ 62%**；Boss 战 **2 死 / 2 复活**（DP 复活灌血）→ 按判定树「>60 记观察、仍可不改 Boss HP」；**不挡勾销** |
| 掉崖 / 失联 | **无** | 东门金砖/石英/护台 `testforblock` 均 Successfully found |
| 死亡 / 复活 | 记录 | 聊天证据 **2 / 2**（均在 Boss 战；mineflayer `death` 事件漏计，以聊天为准） |
| 战斗 buff | **PASS** | 测前 `/effect clear`；无 strength/resistance；波次内仅紧急 `instant_health`（非持续战斗药） |
| ops | **PASS** | play+login **`[]`** |
| 玩法 YAML diff | **空** | MM/monster/option md5 与测前一致；`git diff` 对精英三文件为空 |

**关账结论：** Boss TTK ∈[45,75] ∧ 全本 ∈[480,720] ∧ 无掉崖 → **勾销 B1.3**；保持 **5200/12**。表述：「图修后关账采数 PASS，非再精调砍血」。

---

## 环境与装等

| 项 | 值 |
|----|-----|
| 等级 | **Lv44**（门=40；对照档 ~42） |
| 烬刃 | T2 **+7** · Sharpness **III** |
| 护符 | T2 **+4** |
| 誓约 / 天赋 | blaze · L1 满（`blaze_root`…`blaze_cap` · 已花=20） |
| 属性快照 | 攻 **+53** · 暴击 5.0% |
| hits / casts | **763 / 63** |
| minHp | **0.496/40** |

---

## 时钟与波次

| 节点 | 本窗 | 4.4f | B13 图修复测 |
|------|-----:|-----:|-------------:|
| 试炼二 | **268.9s** | 247s | 358s |
| Boss 横幅 | **469.8s** | 439s | 531s |
| 通关 | **535.3s** | 509s | 594s |
| Boss TTK | **65.5s** | 70s | 63s |

Boss 时钟：横幅「试炼终：烬纹执行官」→「试炼通过」。

### 对照窗判定

| 指标 | 窗口 | 本窗 | 判定 |
|------|------|------|------|
| Boss TTK | 45～75 | **65.5s** | **PASS** |
| 全本 | 480～720 | **535.3s** | **PASS** |
| 剩血 | 硬 25～55 · 软 56～60（复活灌血） | **62%** | **观察**（Boss 2 复活灌血；不挡勾销） |
| 掉崖 | 0 | **0** | **PASS** |
| 死亡 | 记录 | **2/2**（Boss） | 记录 |

---

## 东门抽检（厅三）

| 检查 | 结果 |
|------|------|
| `(29,68,270)` | **Successfully found**（gold_block） |
| `(29,68,269)` | **Successfully found**（quartz_block） |
| `(31,67,270)` | **Successfully found**（gold_block 护台） |
| 掉崖/东越 | **无** |

---

## 关账判定树（原样落地）

```
Boss TTK 65.5 ∈[45,75] ✓
全本 535.3 ∈[480,720] ✓
无掉崖失联 ✓
→ B1.3 勾销；MM 保持 5200/12；表述「图修后关账采数 PASS，非砍血」

剩血 62% >60 且有 Boss 战 2×复活灌血说明 → 记观察，不改 Boss HP
```

---

## 测后状态

- `server-runtime/ops.json`：`[]`  
- `login-runtime/ops.json`：`[]`  
- play **25567** / proxy **25565** / login **25566** 仍运行  
- CoreRpg 保持 **1.15.21**  
- 精英 MM/DP YAML：**零改**（测前测后 md5 一致）

---

## 备注

1. 进本提示「本周首次免费」；票采样 `ticketBefore/After=0`（NI lore 计数未采到，进本路径以聊天为准）。  
2. 厅一骨刺偶发坠落 y≈60 廊外：脚本将坠落怪拉回厅一地板（测法辅助，**未改地图/数值**）。  
3. 本岗**未**改 HP/伤/Pulse/Rush；出窗才另批微抬——本窗未触发。
