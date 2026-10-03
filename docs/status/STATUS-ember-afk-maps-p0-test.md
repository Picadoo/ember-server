# STATUS · P0 挂机②～④去空岛地图 · 验收抽检

**日期：** 2026-09-27 20:28–20:32（Asia/Shanghai）  
**岗：** 余烬-测试岗  
**依据：** `docs/status/STATUS-ember-afk-maps-p0.md` · `docs/design/design-ember-afk-p0-surface.md` · `docs/design/design-ember-multiworld-maps.md` §4.2「挂机去平面」  
**账号：** `AfkMap1378`（Lv.42）；临时 OP `RpgBot`（uuid `148ec8b6-253f-36f4-bcd7-bf2395b8df80`）  
**脚本：** `/tmp/afk-maps-p0-smoke.js`（stdout → `/tmp/afk-maps-p0-out.txt`）  
**约束遵守：** **未改**玩法数值 / `afk_caps` / 怪物 YAML / 地图方块  

---

## 总评：**PASS**

| # | 必测项 | 结果 |
|---|--------|------|
| 1 | ②③④ 非 y≈110 空中屏障平板；落点接近 STATUS 表 | **PASS** |
| 2 | 每层入口牌 + 撤离/回枢纽语义；lore/牌面不教打 `/hub` | **PASS** |
| 3 | TrMenu 进②③④可进可打；菜单「回枢纽」可用 | **PASS** |
| 4 | 窒息≈0；刷点不卡墙（抽检） | **PASS** |
| 5 | `afk_caps` 未漂 | **PASS** |
| — | 测后 `ops.json=[]` | **PASS** |

---

## 1. 实测入口坐标（相对 STATUS 期望）

| 层 | STATUS 期望 | 实测 spawn | 偏差 | 脚下块 | y≈110 屏障抽检 |
|----|-------------|------------|------|--------|----------------|
| ② 荒原 | (200.5, **62.0**, 250.5) | **(200.5, 62.0, 250.5)** | 0 | `quartz_block` @ y61 | **0**（y107–124 无 barrier） |
| ③ 焦土 | (400.5, **70.0**, 250.5) | **(400.5, 70.0, 250.5)** | 0 | `quartz_block` @ y69 | **0** |
| ④ 烬原深处 | (600.5, **64.0**, 250.5) | **(600.5, 64.0, 250.5)** | 0 | `quartz_block` @ y63 | **0** |

- 三层均 **y&lt;100**，明确不是旧空岛 y110。  
- 地表材质抽检：② `stonebrick`/碎石缓坡带；③ `netherrack`/`nether_brick` 谷地起伏（ySpan 约 55–90）；④ `red_nether_brick` 洞穴厅（ySpan 约 55–78）。  
- 配置侧 `build.mode/theme`：② ground/ruins · ③ ground/scorched · ④ cave/cave；`clear_old_y: 110` 保留。

---

## 2. 牌面 / 菜单文案证据

### 2.1 场景告示牌（mineflayer `signText`）

| 层 | 入口牌 | 撤离牌 |
|----|--------|--------|
| ② | `(200,62,247)`「挂机·②荒原 / 打开/ember / →挂机庭换层 / 死亡回入口」 | `(200,62,253)`「回枢纽 / 打开枢纽菜单 / 返回」 |
| ③ | `(400,70,247)`「挂机·③焦土 / …」同上结构 | `(400,70,253)`「回枢纽 / 打开枢纽菜单 / 返回」 |
| ④ | `(600,64,247)`「挂机·④烬原 / …」 | `(600,64,253)`「回枢纽 / 打开枢纽菜单 / 返回」 |

牌面 **无**「请打 /hub」类教学。

### 2.2 TrMenu / 进层聊天

- `ember_hub`「挂机庭」lore：`打开枢纽菜单可返回`（**无** `/hub`）。  
- `ember_afk`「回枢纽」lore：`打开枢纽菜单返回`；进层按钮 lore 仅「点击传送」，**无**指令教学。  
- 静态扫 `ember_afk.yml` / `ember_hub.yml`：`/hub|请打` **命中 0**。  
- 进层聊天样例（②）：`[挂机] 已到达 荒原（Lv.20）… · 打开枢纽菜单可返回`（③④同结构，均不教 `/hub`）。

---

## 3. TrMenu 进层 / 轻量战斗 / 回枢纽

路径：`/ember` → 挂机庭 → ②/③/④；测账号 Lv.42（门槛均满足）。

| 层 | 菜单点进 | 进层确认 | 轻量战斗（~16s） | 备注 |
|----|----------|----------|------------------|------|
| ② | 点「② 荒原」→ 到达 | 聊天「已到达 荒原」+ 坐标精确 | hits=**15** · mobsSeen=**3** · deaths=0 | mineflayer click transaction 超时属窗关闭误报；传送与战斗已发生 |
| ③ | 点「③ 焦土」 | 「已到达 焦土」 | hits=**13** · mobsSeen=**4** · deaths=0 | 同上 |
| ④ | 点「④ 烬原深处」 | 「已到达 烬原深处」 | hits=**17** · mobsSeen=**3** · deaths=0 | 同上 |

- **回枢纽：** 挂机庭菜单点「回枢纽」→ 聊天 `[余烬] 已回到枢纽…`；落地 **(-18.5, 58, 110.5)**（与 hub spawn 一致）。  
- **未重跑 TTK**（按任务要求仅冒烟）。  
- 指令路径复检：`/corerpg afk 2` → 仍 (200.5, 62.0, 250.5)，文案同样不教 `/hub`。

---

## 4. 窒息 / 卡墙抽检

| 层 | 玩家头格实心（窒息 hint） | 怪身实心（inBlockSamples） |
|----|---------------------------|----------------------------|
| ② | **0** | **0** |
| ③ | **0** | **0** |
| ④ | **0** | **0** |

刷点配置 Y 已对齐新地面（② Y62 · ③ Y70 · ④ Y64），与 STATUS 插件侧描述一致。

---

## 5. `afk_caps` 抽检（对照 `docs/status/STATUS-afk-caps.md`）

现网 `plugins/CoreRpg/config.yml`（与 `server-runtime/plugins` 同 symlink）：

```
afk_caps:
  enabled: true
  worlds: [ember_afk, world]
  over_chance: 0.25
  kill_coin: 150
  items:
    mat_ember_shard: 150
    mat_ember_bone_dust: 80
    mat_ember_core_fragment: 10
    mat_ember_stable_charm: 1
    mat_ember_protect_scroll: 2
    mat_ember_soul_dust: 2
```

与 STATUS-afk-caps / 现网验收终态 **一致**；本轮测试 **未改写**该节。

---

## 6. ops / 环境收尾

| 项 | 状态 |
|----|------|
| 测中临时 OP | RpgBot（level 4）写入 `server-runtime/ops.json` 后短重启 play |
| 测后 `/deop RpgBot` | 已执行；再连确认无 op 权限 |
| `server-runtime/ops.json` | **`[]`** |
| `login-runtime/ops.json` | **`[]`** |
| 端口 | proxy **25565** / play **25567** 在听 |

---

## 7. 风险备忘（不挡本里程碑）

1. mineflayer 对 TrMenu 关窗传送会报 `Server didn't respond to transaction`——功能上已进层，报告以坐标/聊天/战斗为准。  
2. STATUS 插件侧风险（②地表 y61 略低、③刷点坡差、④竖井坡道）本轮抽检未触发窒息/卡墙，**未改**。  
3. 世界存档不进 git——换机需再 `/corerpg afk build 2/3/4`（文档既有说明）。

---

## 交付路径

`/workspace/minecraft/docs/status/STATUS-ember-afk-maps-p0-test.md`
