# STATUS · 挂机日顶合并验收（afk_caps）

**日期：** 2026-09-27 15:53–15:59（Asia/Shanghai）  
**执行器：** 余烬-测试岗  
**范围：** HANDOFF「挂机日顶合并」——跨层共用每日上限、上限提示文案、超限 over_chance≈25%  
**未改：** 怪物 YAML / Paper / `CoreRpg/src/main/resources/config.yml`  
**总评：** **PASS**

---

## 配置路径与原值

| 项 | 值 |
|---|---|
| 线上生效 | `/workspace/minecraft/plugins/CoreRpg/config.yml`（与 `server-runtime/plugins/CoreRpg/config.yml` 同 inode `793305`） |
| 备份 | `/tmp/corerpg-config-afk-caps.bak`（完整文件） |
| 源码副本 | `CoreRpg/src/main/resources/config.yml` **未动**（始终 150/150） |

### 改前原值（`afk_caps`）

```
enabled: true
worlds: [ember_afk, world]
over_chance: 0.25
kill_coin: 150
items.mat_ember_shard: 150
（其余 items 未改：bone_dust 80 / core_fragment 10 / …）
```

### 临时验收值

| 键 | 临时 | 验收后 |
|---|---|---|
| `items.mat_ember_shard` | **15** | **150**（已从 bak 精确还原） |
| `kill_coin` | **20** | **150** |
| `over_chance` / worlds / 其它 | 不动 | 不动 |

临时改后执行：`/corerpg reload` → `[CoreRpg] 配置已重载 (storage=mysql)`。

---

## 复现步骤（已执行）

1. 备份 plugins `config.yml` → `/tmp/corerpg-config-afk-caps.bak`
2. 临时改 `mat_ember_shard→15`、`kill_coin→20`
3. 停游玩服 → `ops.json` 写入 RpgBot（uuid `148ec8b6-253f-36f4-bcd7-bf2395b8df80` level 4）→ `JAVA_HOME=…/jdk8u504-b01 ./start.sh custom` → 25567 + Done；proxy/login 仍在听
4. `cd mineflayer-tests && node op-cmd.js "/corerpg reload"`
5. `NODE_PATH=…/mineflayer-tests/node_modules node /tmp/afk-caps-merge-test.js`（stdout → `/tmp/afk-caps-merge-out.txt`）
6. 从 bak 还原 config → `/corerpg reload` → `/deop RpgBot` → 停服 → `ops.json=[]` → 再起 → 确认 ops=[]、25565/66/67

### 测试账号与脚本

- 账号：`CapM4518`（Lv.20；OP 辅助 `RpgBot`）
- 临时脚本：`/tmp/afk-caps-merge-test.js`（未提交 git）
- 流程：同账号 `/corerpg afk 1` 攒计数 → 切 `/corerpg afk 2` 继续；用 `/corerpg afk` 的「今日：碎片 x/cap」作计数证据

---

## 判定

| 项 | 结果 | 证据摘要 |
|---|---|---|
| 跨层合并 | **PASS** | ①结束 `碎片 12/15`；切②前、进②后仍为 `12/15`（未从 0 重算）；②继续涨到 `28/15` |
| 上限提示文案 | **PASS** | 聊天出现：`[余烬] 今日野外掉落已达收益上限，之后掉率降为 25%。去打日常/周本/深渊，或钓鱼做饭吧。（每日 0 点重置）` |
| 超限约 25% | **PASS** | 达顶后再杀 **40** 只碎片怪（期望满掉 40、期望 25%=10）；**实测 inventory 增量 12**，比率 **0.30**（样本波动可接受，落在 ~0.08–0.50） |
| 配置恢复 / ops | **必须做到 ✓** | `shard/kill_coin` 已回 **150/150**；`ops.json=[]`；端口 25565/66/67 在听 |

### kill_coin 观察（非硬性）

- 临时上限 20；终态计数 `击杀币 32/20`（已超顶并继续按 over 逻辑），自然触发，记一笔。

---

## CAPS_RESULT（一行）

```
CAPS_RESULT {"account":"CapM4518","tempCaps":{"shard":15,"kill_coin":20,"over_chance":0.25},"merge":{"t1After":{"cur":12,"cap":15},"onT2BeforeFight":{"cur":12,"cap":15},"line":"碎片 12/15 · 骨尘 0/80 · 核心碎片 0/10 · 魂尘 0/2 · 击杀币 12/20","carried":true},"capMsg":"[余烬] 今日野外掉落已达收益上限，之后掉率降为 25%。去打日常/周本/深渊，或钓鱼做饭吧。（每日 0 点重置）","over":{"shardKills":40,"invGain":12,"expectedFull":40,"expectedOver":10,"sec":106.5,"ratio":0.3,"ok":true},"coin":{"cur":32,"cap":20},"verdict":{"merge":"PASS","capMsg":"PASS","over25":"PASS"},"level":20,"finalCap":"碎片 28/15 · 骨尘 0/80 · 核心碎片 0/10 · 魂尘 1/2 · 击杀币 32/20","finalInvShard":16}
```

完整 stdout：`/tmp/afk-caps-merge-out.txt`

### 阶段数字

| 阶段 | kills / shardKills | 计数（`/corerpg afk`） | inv shard gain |
|---|---|---|---|
| T1 灰坡 | 12 / 12 | 0/15 → **12/15** | +12 |
| 切层合并检查 | — | **仍 12/15** | （clear 后 inv 重置，计数不重置） |
| T2 荒原 | 44 / 44 | 12/15 → **28/15** | +16（含达顶前满掉 + 达顶后 12） |
| 达顶后样本 | shardKills **40** | — | **+12**（期望 10 ≈25%） |

---

## 收尾确认

| 检查 | 状态 |
|---|---|
| `afk_caps.items.mat_ember_shard` | **150** |
| `afk_caps.kill_coin` | **150** |
| 源码 resources/config.yml | 未改（150/150） |
| `server-runtime/ops.json` | **[]** |
| 端口 | **25565**（proxy）/ **25566**（login）/ **25567**（play）均 LISTEN |
| RpgBot | 已 `/deop` |

---

## 结论

三项验收（跨层合并 / 上限文案 / 超限≈25%）均为 **PASS**；临时配置与 ops 已恢复。总评 **PASS**。
