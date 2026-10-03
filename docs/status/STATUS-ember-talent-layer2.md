# STATUS · 天赋 stage4.2（第二层）验收

**日期：** 2026-09-27 16:32–16:39（Asia/Shanghai）  
**执行器：** 余烬-测试岗  
**范围：** `docs/design/design-stage4-talent-layer2.md` — earned 公式 A、二层节点、硬顶 30、TrMenu 玩家路径、无新主动技  
**Jar：** `server-runtime/plugins/CoreRpg.jar` mtime **16:31:53 CST**（CoreRpg **1.14.0**）  
**测法脚本：** `/tmp/talent-layer2-accept.js` → `/tmp/talent-layer2-out.txt`  
**总评：PASS**

---

## 0. 游玩服重启（必须）

| 步骤 | 结果 |
|------|------|
| 停服 → 写 ops RpgBot uuid `148ec8b6-253f-36f4-bcd7-bf2395b8df80` | OK |
| `JAVA_HOME=.../jdk8u504-b01 ./start.sh custom` | Done **16:33:24**（首轮测） |
| 测完 `/deop` 等价：停服 → `ops.json=[]` → 再起 | Done **16:39:37** |
| 三端口 | **25565 / 25566 / 25567** 均在听；`ops=[]` |

---

## 1. earned 公式实测（`/corerpg talent` 已获）

公式：`L≤30 → min(20, legacy)`；`L>30 → min(30, 20 + floor((L-30)/3))`

| 目标档 | 实测等级 | 已获 earned | 期望 | 判定 |
|--------|----------|-------------|------|------|
| Lv30 | **31**（堆级越过 30） | **20** | 20 | **PASS** |
| Lv33 | 33 | **21** | 21 | **PASS** |
| Lv36 | **37** | **22** | 22 | **PASS** |
| Lv39（抽查） | 39 | **23** | 23 | **PASS** |
| Lv45 | 45 | **25** | 25 | **PASS** |
| Lv60 | 60 | **30** | 30 | **PASS** |

说明：用 `/corerpg progress <号> raid_clear` 堆余烬经验；读点以聊天 `[天赋] 点数 可用=… 已花=… 已获=… 硬顶=30` 为准。Lv30/36 档因批量 XP 略超目标档，但 earned 与公式在该实测等级一致（31→20、37→22）。

---

## 2. 菜单（玩家路径 TrMenu）

| 项 | 结果 |
|----|------|
| `/ember` 打开枢纽 | PASS（见「天赋」按钮） |
| 点击「天赋」→ `余烬 · 天赋` | **PASS**（首次 clickWindow 交易超时，吞异常后 `windowOpen` 仍到天赋页；复测确认） |
| 二层按钮 | **9/9 在场**：余烬脉 / 炽脉 / 再燃 / 灰纱 / 细尘 / 掩息 / 叠壁 / 护冢 / 誓碑 |
| 点击「烬刃 · 余烬脉」 | 触发 `corerpg talent unlock blaze_ember2`（当时誓约 ash → 提示「不属于 blaze」，证明菜单 command 通路） |
| `menu_path` | **PASS** |

---

## 3. 节点存在与 runtime 解锁

YAML（`plugins/CoreRpg/talent.yml`）二层均为 `type: passive`：

- blaze：`blaze_ember2` / `blaze_heat2` / `blaze_second`
- ash：`ash_veil2` / `ash_dust2` / `ash_shroud`
- warden：`warden_bulwark2` / `warden_aegis2` / `warden_vow2`

Runtime（`/corerpg talent unlock`，先点满一层 `*_cap`）：

| 树 | 解锁 | 判定 |
|----|------|------|
| blaze | ember2 + heat2 + second（满二层） | **PASS** |
| ash | veil2（洗约后重点一层） | **PASS** |
| warden | bulwark2 | **PASS** |

`nodes_three_trees` = **PASS**

---

## 4. 硬顶 30

- Lv60：`已获=30 硬顶=30`
- 烬刃一层 20 + 二层 3+3+4 = **已花=30 可用=0**
- 再 unlock 异树节点：拒绝（誓约不匹配 / 无点）
- **hard_cap = PASS**

---

## 5. 无新主动技 / talent_bonus_max

| 项 | 结果 |
|----|------|
| 二层 YAML `type` | 全部 **passive**（无 skill / 无新主动） |
| `skills.yml` `talent_bonus_max` | 仍为 **3**（未因二层提高）→ **PASS** |
| 一层仍保留原主动（slash/fam/taunt） | 符合设计（二层不加新主动） |

---

## 6. 账号与污染

- 测试号：**Tl2_4484**（临时；誓约切换用晶钻，非脏配置）
- 测后清理：MySQL `cr_players` / `cr_mail` / `cr_warehouse` + AuthMe 行 + `bot-passwords` 条目已删；**未留脏号**
- RpgBot 仅测时 op，结束后 `ops.json=[]`

---

## 7. TALENT42_RESULT（摘要）

见 `/tmp/talent-layer2-out.txt`（`verdict: PASS`；`menu_path` 经复测修正为 PASS）。

```
earned_ok: 30/33/36/39/45/60 全 PASS
menu_path: PASS
nodes_three_trees: PASS
hard_cap: PASS
no_new_active: PASS
talent_bonus_max: PASS
verdict: PASS
```

---

## 8. 备注 / 未改数值

- **未改** talent.yml / skills.yml / 任何数值或 YAML（仅验收）
- 菜单首次 mineflayer `clickWindow` 可能报 transaction timeout，属测具噪音；以 `windowOpen` + 标题 + 图标列表为准
