# STATUS · 锈轨侧袭 + 冷却 chat 短验收

**日期：** 2026/9/28 07:52:47 → 2026/9/28 07:55:21 Asia/Shanghai  
**岗：** 余烬-测试岗执行器  
**依据：** `docs/status/STATUS-ember-daily-rail-flank-cooldown-chat.md`  
**范围：** EmberDailyRail 房2 真侧袭时序 + door2 门控 + 出本冷却人话/体力退还 + CoreRpg 1.15.21  
**禁项：** 禁改配置 · 禁 killall · **未** commit/push  
**Verdict：** **✅ PASS**（三条硬条全过 · ops=`[]`）

---

## 一句话

锈轨房2：主巷开打后 **Δt=1958ms** 支洞矿矢已刷（勿等主巷清完）；door2 在支洞清完前保持铁栅×9、清完后 AIR×9；出本后 **1.2s** 再进日常收到「出本后再进约等 5 秒（缓存冷却），不是进本坏了」，已扣体力退还；CoreRpg **1.15.21**；测后 ops=`[]`。

---

## 三条硬条

| # | 硬条 | 结果 | 证据 |
|---|------|------|------|
| 1 | 锈轨房2：主巷开打后 ≈1s 支洞矿矢已刷；door2 须支洞清完才 AIR；主巷未清完时门仍铁栅亦可 | **✅ PASS** | Δt=1958ms（mob）· 主巷存活时 door2 iron=9 · 主巷清/支洞未清 iron=9 · 支洞清后 air=9 |
| 2 | 出本后 5s 内再进日常 → 人话冷却；若已扣体力须退还 | **✅ PASS** | leave→reenter **1200ms** · chat 命中 · 扣 -30 后 stamFinal=60（退还）· 日志 `start no-effect (likely interval)` |
| 3 | `version`/日志 CoreRpg **1.15.21**；测后 ops=`[]` | **✅ PASS** | jar+live+`version CoreRpg` → 1.15.21 · play=`[]` login=`[]` |

---

## 证据摘要

### 环境

| 项 | 值 |
|----|----|
| CoreRpg jar | **1.15.21** |
| live 日志 | Loading/Enabling CoreRpg v1.15.21 @ 07:49:20/26 CST |
| version 命令 | `CoreRpg version 1.15.21`（07:53:11 CST） |
| 侧袭玩家 | `RfcP1624` · 临时 OP `RfcOp82` |
| 冷却复测玩家 | `CdoP1297` |
| JSON | `/tmp/rail-flank-cooldown-chat-test.json` · `/tmp/rail-flank-cooldown-only.json` |

### 硬条1 · 锈轨侧袭 + door2

**静态 YAML**

- wave2a **start** → `$monstergroup{group=wave2b;repeat=false;delay=1}`（勿等主巷 `$kill`）
- door2 AIR×9 仅在 wave2b **end**

**Live（真击杀 · 禁 killall）**

| 阶段 | 结果 |
|------|------|
| 进本 | `[锈轨] 正在进入……（体力 -30）` · `余烬窟·锈轨矿道 开始！` · spawn (0,64,0) |
| wave1 | hits=22 · 「主巷前段已清 · 轨闸开了」· door1 **air×9** |
| 侧袭时序 | 主巷 mob @ t · 支洞 skeleton @ t+**1958ms** · 文案「锈轨尸压上 —— 当心支洞侧袭」→「矿矢骷侧袭」 |
| door2 @ 主巷仍存活 | **iron×9**（mainMobs=3） |
| door2 @ 主巷清 / 支洞未清 | **iron×9**（flankMobs=0） |
| wave2b | hits=14 · 「后段已清 · 机房门开了」 |
| door2 @ 支洞清完 | **air×9** · iron=0 |

侧袭 timeline（相对 poll）：
- `main_mob` t=1351ms
- `main_msg` t=1503ms · 【矿道·主巷】锈轨尸压上 —— 当心支洞侧袭
- `flank_msg` t=3460ms · 【矿道·支洞】矿矢骷侧袭 —— 清完开机房门
- `flank_mob` t=3461ms

### 硬条2 · 冷却 chat + 体力退还

首轮 leave 后因 stamina show 等导致再进间隔 **>5s**，冷却未触发（庭院误进）。  
**复测**（`CdoP1297`）：庭院进本 → `/dp leave` → **1200ms** 内 `/corerpg enter daily_frost`：

```
[霜晶] 正在进入……（体力 -30）
出本后再进约等 5 秒（缓存冷却），不是进本坏了
```

| 项 | 值 |
|----|----|
| leave→reenter | **1200 ms**（<5s） |
| coolHit | ✅ |
| 曾扣体力 | ✅（文案「体力 -30」） |
| stamFinal | **60/90**（= 进本后水平；若未退还应为 30） |
| 未误进 | ✅ `entered_anyway=false` |
| 服务端 | `[TicketEntry] start no-effect (likely interval): CdoP1297 EmberDailyFrost` @ 07:55:18 CST |

### 硬条3 · 版本 / ops

- jar `plugin.yml`：**1.15.21**
- live：`CoreRpg 1.15.21 enabled`
- `version CoreRpg` → **1.15.21**
- 测后 ops：play=`[]` · login=`[]`（FIFO `deop` + 写空）

---

## 检查明细

- **coreRpg_1_15_21**: ✅ — name: CoreRpg main: town.sunshine.corerpg.CoreRpgPlugin version: 1.15.21 author: sunshine-town | 58:[07:49:20] [Server thread/INFO]: [CoreRpg] Loading CoreRpg v1.15.21 579:[07:49:2
- **yaml_wave2a_starts_wave2b_delay1**: ✅ — $monstergroup{group=wave2b;repeat=false;delay=1} @dungeon"
- **yaml_door2_on_wave2b_end**: ✅ — door2 AIR ops in wave2b end
- **op_ok**: ✅ — [主线] 目标：与枢纽的 引路人·灰烛 交谈（就在出生点北边几步、工坊旁，右键他） | 主线 与枢纽的 引路人·灰烛 交谈 | [Server: Opped RfcOp82] | Opped RfcOp82 | Your game mode has been updated to Creative Mode | You
- **version_cmd_1_15_21**: ✅ — 790:[07:49:47] [Server thread/INFO]: CoreRpg version 1.15.21 914:[07:53:11] [Server thread/INFO]: CoreRpg version 1.15.21
- **rail_enter**: ✅ — {"pos":{"x":0,"y":64,"z":0},"msgs":["[锈轨] 正在进入……（体力 -30）","余烬窟·锈轨矿道 开始！已消耗体力 · 矿口安全 · 清主巷开闸"],"stamina30":true}
- **rail_stamina_30**: ✅ — 体力 -30 on enter
- **wave1_true_kill**: ✅ — {"ok":true,"hits":22,"ms":11674,"msgs":["主巷前段已清 · 轨闸开了"]}
- **door1_air**: ✅ — {"iron":0,"air":9,"raw":{"-1,64,15":"air","-1,65,15":"air","-1,66,15":"air","0,64,15":"air","0,65,15":"air","0,66,15":"air","1,64,15":"air","1,65,15":"air","1,66,15":"air"}}
- **flank_delta_approx_1s**: ✅ — Δt=1958ms src=mob (expect ~1000ms, allow 200-3000)
- **door2_iron_while_main_alive**: ✅ — {"door":{"iron":9,"air":0,"raw":{"-1,64,33":"iron_bars","-1,65,33":"iron_bars","-1,66,33":"iron_bars","0,64,33":"iron_bars","0,65,33":"iron_bars","0,66,33":"iron_bars","1,64,33":"i
- **wave2a_main_clear**: ✅ — {"ok":true,"hits":15,"ms":9593,"msgs":["主巷已清"]}
- **door2_iron_before_flank_clear**: ✅ — {"door":{"iron":9,"air":0,"raw":{"-1,64,33":"iron_bars","-1,65,33":"iron_bars","-1,66,33":"iron_bars","0,64,33":"iron_bars","0,65,33":"iron_bars","0,66,33":"iron_bars","1,64,33":"i
- **wave2b_flank_clear**: ✅ — {"ok":true,"hits":14,"ms":7229,"msgs":["后段已清 · 机房门开了"]}
- **door2_air_after_flank_clear**: ✅ — {"door":{"iron":0,"air":9,"raw":{"-1,64,33":"air","-1,65,33":"air","-1,66,33":"air","0,64,33":"air","0,65,33":"air","0,66,33":"air","1,64,33":"air","1,65,33":"air","1,66,33":"air"}
- **cooldown_chat**: ✅ — {"leave_to_reenter_ms": 1200, "coolHit": true, "msgs": ["[霜晶] 正在进入……（体力 -30）", "出本后再进约等 5 秒（缓存冷却），不是进本坏了"], "deducted": true, "entered_anyway": false, "stamAfterEnter": null, "stam
- **stamina_refund**: ✅ — deducted then refunded: stamFinal={'cur': 60, 'max': 90, 'raw': '[体力] 60/90 · 银行 0 · 药剂今日 0/90'}; leave_to_reenter_ms=1200; msgs=['[霜晶] 正在进入……（体力 -30）', '出本后再进约等 5 秒（缓存冷却），不是进本坏了']
- **ops_empty**: ✅ — {"play":[],"login":[]}

---

## 成功标准核对

| 标准 | 状态 |
|------|------|
| 报告落盘 | ✅ 本文件 |
| 结论明确 | ✅ **PASS** |
| 三条硬条 | ✅ / ✅ / ✅ |
| ops 清空 | ✅ |
| 禁改配置 | ✅（仅读 YAML + 临时 OP 测） |
| 未 commit/push | ✅ |
| 禁 killall | ✅ 仅 attack/swingArm |

---

## 阻塞点

**无。**（硬条2 首轮因 leave→再进间隔过长未触发，同会话内收紧至 1.2s 复测通过。）

**最终结论：** 锈轨侧袭 + 冷却 chat 短验收 **PASS**。
