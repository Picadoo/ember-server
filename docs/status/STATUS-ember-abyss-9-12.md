# STATUS · 深渊 stage4.1（9～12 层）验收

**日期：** 2026-09-27 16:08～16:20（Asia/Shanghai / CST）  
**执行岗：** 余烬-测试  
**账号：** 主测 `Ab9x3744`（非 op）；管理 `RpgBot`；撤离补测 `AbEv9339` / `AbE22430`  
**脚本：** `/tmp/abyss-9-12-accept.js`（勿提交）· stdout `/tmp/abyss-9-12-out.txt` · `ABYSS912_RESULT`  
**未改：** 平衡 YAML / 怪物 / DP kill 名（仅读写验收）

---

## 总评

**内容层（9～12 / COMPLETE / 周首通稳定符）：PASS**  
**本内菜单撤离：FAIL（卡点，见下）**  
整体按阶段 4.1 目标：**有条件 PASS**（冲层与奖励通过；撤离玩家路径当前不可达）。

---

## 逐条

| # | 项 | 结果 | 证据摘要 |
|---|----|------|----------|
| 1 | 菜单进本 | **PASS** | `/ember` → 点「深渊」→ 点「开始下潜」；窗口先后「余烬 · 冒险枢纽」「深渊 · 无尽层」；聊天「深渊已开启…本期可下潜至第 12 层」+「—— 第 1 层 ——」 |
| 2 | floor8→9 续层 | **PASS** | 第 8 层通过后「继续下潜或撤离」→「—— 第 9 层 —— 深处蛮压」（**无** COMPLETE@8） |
| 3 | kill 名 · 第9 | **PASS** | floor9 开场「深处蛮压」；条件配置 `$kill{余烬深渊·蛮层}`；本局 progress→9 |
| 4 | kill 名 · 第10/12 | **PASS** | 「第二看守！视线更毒」「深渊合拢·最终看守！」；progress 10 / 12；配置 `$kill{余烬深渊·看守·深}` |
| 5 | 进度 ≥9 | **PASS** | 真实战斗最高层 **12**（`[深渊] 本局最高层更新为 9…12`） |
| 6 | COMPLETE=12 | **PASS（自然）** | 「余烬深渊 顶层通关！」；模式 `natural`（未用管理 progress 12） |
| 7 | 周首通稳定符 | **PASS** | settle 聊天：`[深渊] 本周首次抵达第 12 层，获得稳定符 ×1`；NI `mat_ember_stable_charm` 计数≥1 |
| 8 | 同周静默 | **PASS** | 再 `/corerpg loot <p> abyss_weekly12` →「本周首通保底已领；本次无额外掉落」；再 settle →「本局已结算，不再重复发放」；charm 计数不变 |
| 9 | 菜单撤离 | **FAIL** | 本内 `/ember` / `/trmenu open ember_abyss` / `/corerpg abyss evacuate` 均被 CoreRpg 副本命令白名单拦截；见「卡点」 |
| 10 | ops 恢复 | **PASS** | 测中 RpgBot op4 → `/deop` → 停服 `ops.json=[]` → 再起 |
| 11 | 三端端口 | **PASS** | `:25565` 代理 · `:25566` 登录 · `:25567` 游玩 均 LISTEN |

---

## 战斗摘要（Ab9x3744）

| 项 | 值 |
|----|-----|
| 等级 | Lv.28（ensureLevel≥25） |
| 装备 | T2 刃+5 / T2 符+1 · Sharpness III · 誓约烬刃 · 面包 |
| 防卡死 buff | op `resistance II` + `strength I`（**进本仍菜单**） |
| 层时刻（进本后秒） | 1:0 · 5:36 · 8:70 · **9:92** · **10:104** · **11:112** · **12:116** |
| 战斗时长 | ~119 s · 死亡 0 · minHp≈31 · hits 126 |
| COMPLETE | 自然顶层通关 + settle 档 12（碎片×16 骨尘×6 核心×2）+ abyss_t2 周首 T2 刃/符 |

> 注：有 buff 时 TTK 偏短，**不作** 4.3 平衡主证据；仅证层组/kill/COMPLETE/周首通语义。

---

## 玩家复现（菜单优先）

### 进本（PASS）

1. 持有 `ticket_ember_abyss`×1，余烬 Lv≥25  
2. 聊天执行 `/ember`  
3. 点 **深渊**  
4. 点 **开始下潜**  
5. 应见扣票 +「深渊已开启…第 12 层」+「—— 第 1 层 ——」

### 冲至 9～12（PASS）

1. 清层至第 8 层通过 → 应续「第 9 层 · 深处蛮压」（不应在第 8 层 COMPLETE）  
2. 第 9：蛮层；第 10/12：看守·深文案；第 12 通过后「余烬深渊 顶层通关」

### 周首通稳定符（PASS）

1. 本局最高层≥12 且 settle（COMPLETE 奖励脚本或撤离 settle）  
2. 首周：聊天「本周首次抵达第 12 层，获得稳定符 ×1」  
3. 同账号同周再 loot/`abyss_weekly12` 或再 settle：不二发

### 撤离（当前 FAIL）

**设计路径：** 本内 `/ember` → 深渊 → **上浮撤离**  
**实测：** 本内无法开菜单 / 无法执行 `corerpg abyss evacuate`（见卡点）。  
超时仍可由 DP timeout 控制台 `settle`（未在本轮单独计时验收）。

---

## 卡点 / Bug 复现

### Bug：本内无法走「菜单撤离」

**现象：** 进入 EmberAbyss 后：

| 玩家动作 | 服务端回复 |
|----------|------------|
| `/ember` | `[余烬] 副本中不能回城或开菜单，离开请用 /dp leave（可用：/corerpg skill · quest · stats）` |
| `/trmenu open ember_abyss` | `[余烬] 副本中该命令不可用。可用：/dp leave · /corerpg skill · quest · stats` |
| `/corerpg abyss evacuate` | 同上「该命令不可用」 |

**影响：** `ember_abyss.yml` 的「上浮撤离」按钮在本内不可达；设计文案「打开菜单 → 深渊 → 撤离」与现网白名单冲突。  
**建议（插件岗，本岗未改）：** 白名单放行 `/ember`（或仅 `ember_abyss`）及 `corerpg abyss evacuate`；或本内热键直接开 `ember_abyss`。  
**证据账号：** `AbE22430` · `/tmp/abyss-evac2-out.txt` · `EVAC2_RESULT`

### 其它备注

- mineflayer `clickWindow` 常报 transaction timeout，但 TrMenu action 仍执行（进本成功）；真人点击不受影响。  
- 强化材料不足时刃停在 +5（设计档 +6～8）；有 buff 仍可通关，平衡复测需补材料。  
- 主线 `abyss_floor≥9` 认层：本局真实打到 12 已写 `recordAbyssFloor`；主线 ch8 若未接线则另测（本轮仅注记 admin `progress abyss_clear` 作 XP）。

---

## ops / 端口（收尾）

| 项 | 状态 |
|----|------|
| 测前 | 停游玩服 → `ops.json` = RpgBot `148ec8b6-253f-36f4-bcd7-bf2395b8df80` level **4** → `./start.sh custom` |
| 测后 | 在线 `/deop RpgBot` → 停服 → `ops.json=[]` → 再起 custom |
| 当前 ops | **`[]`** |
| 端口 | **25565**（代理）· **25566**（登录 127.0.0.1）· **25567**（游玩 127.0.0.1）均在听 · Done `@16:19:27 CST` |

---

## 配置核对（只读）

- DP `EmberAbyss/monster.yml`：floor8→floor9（无 COMPLETE）；floor12 COMPLETE「余烬深渊 顶层通关」  
- MM `EmberAbyssWatcherDeep` Display=`余烬深渊·看守·深`（加载 36 怪）  
- TrMenu `ember_abyss.yml`：「开始下潜」「上浮撤离」  
- CoreRpg loot `abyss_weekly12`：`first_per_week: mat_ember_stable_charm×1`；settle(floor≥12) 发符  

---

## 结论给总控

- **9～12 层组 + kill 名 + 自然 COMPLETE@12 + 周首通稳定符 + 同周静默：PASS**  
- **菜单进本：PASS**  
- **菜单撤离：FAIL（白名单挡本内菜单/evacuate）——建议插件岗修后再点验撤离一条**  
- ops 已空，三端端口正常；未改平衡 YAML/怪物  

---

## 菜单撤离复测

| 项 | 值 |
|----|-----|
| 时间 | 2026-09-27 16:26～16:28 CST |
| jar | `CoreRpg.jar` mtime **2026-09-27 16:23:11 CST**（`plugins/` 与 `server-runtime/plugins/` 同路径同戳）· Enabling **CoreRpg v1.14.0** |
| 结果 | **PASS** |
| 账号 | 玩家 `AbEvR5909`（非 op）· op 辅助 `RpgBot`（测后已 `/deop`） |
| 证据文件 | `/tmp/abyss-evac-retest-out.txt` · `ABYSS_EVAC_RESULT` |

### 复现菜单步骤

1. **进本（枢纽）：** `/ember` → 点「深渊」→ 点「开始下潜」（扣 `ticket_ember_abyss`×1）  
2. **本内确认：** 聊天见「深渊已开启…本期可下潜至第 12 层」「—— 第 1 层 ——」  
3. **撤离：** 本内再 `/ember` → 插件提示 `[深渊] 本内菜单 · 点「上浮撤离」`，直接打开「深渊 · 无尽层」→ 点「上浮撤离」  
4. **PASS 证据：**  
   - 未被「副本中该命令不可用 / 不能回城或开菜单」拦住（`blocked=false`）  
   - `[深渊] AbEvR5909 结算完成 · 层 2 → 碎片×8 骨尘×2`  
   - `[深渊] 上浮撤离 · 前往 hub`  

### 备注

- 本内 `/ember` 经白名单修复改为控制台 `trmenu open ember_abyss`（无需再点枢纽「深渊」子入口）。  
- DP `config.yml` 已放行 `corerpg abyss evacuate`；菜单按钮 action 即该指令。  
- 进本后潮尸秒杀导致阵亡提示，但菜单撤离与 settle 仍正常触发（本测用 admin `abyss progress … 2` 抬层以便结算箱有层数；非平衡改动）。  
- 未改 YAML/平衡；未要求打到 12 层。  

### ops / 端口（本复测收尾）

| 项 | 状态 |
|----|------|
| 测后 | `/deop RpgBot` → 停游玩服 → `ops.json=[]` → `./start.sh custom` |
| 当前 ops | **`[]`** |
| 端口 | **25565**（代理）· **25566**（登录）· **25567**（游玩）均在听 · Done `@16:28:17 CST` |

