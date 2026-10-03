# STATUS · 枢纽 hitbox 叠层 purge + 灰烛 quest 零指令

**日期：** 2026-09-28 06:20–06:24 CST（Asia/Shanghai）  
**岗：** 余烬-插件岗执行器  
**派工：** 总控【派工 · 枢纽维护两件】  
**制品：** CoreRpg **1.15.19** · `plugins/CoreRpg.jar`（= `server-runtime/plugins/CoreRpg.jar` 同 inode）  
**Verdict：** **✅ 两件可结案**（叠层已清且 ensure×2 不叠；quest 玩家可见句零指令；ops=`[]`；**未** commit/push）

---

## 1. 根因（已核实）

| 点 | 说明 |
|----|------|
| 假设 | Bukkit metadata `corerpg_hub_npc` / `corerpg_quest_npc` **不随区块存盘** |
| 旧逻辑 | `ensureHitbox` / `ensureHitboxNpc` **只按 metadata** purge → 重启/区块晚加载后旧隐形 NoAI 村民丢 meta，purge 跳过，ensure 再刷 → **叠层** |
| 测报债 | 工坊三岗 ≈100 层；灰钥仅 2；灰烛 guide≈111（`docs/status/STATUS-ember-hub-dungeon-clerk-test.md`） |
| live 印证 | 本轮短重启后首次 ensure：**guide purged 110** · smith **100** · enchanter **99** · qm **101** · clerk **1** —— 与债条数量级一致 |

Ady 包实体不进 `World#getEntities*`，本修只清 Bukkit `LivingEntity` hitbox，**不会误删 Ady**。

---

## 2. 修法

### Hitbox（`HubNpcService` + `QuestService`）

1. **ensure 前 purge**（可重复调用不叠层）  
   - (a) 同世界：metadata id 匹配 **或** 可存盘 **scoreboard tag**（`corerpg_hub_hb:<id>` / `corerpg_quest_hb:<id>`）  
   - (b) 坐标附近特征清（hub ≤3 格 / 灰烛 ≤4 格）：隐形 NoAI Nitwit 村民（无交易、无名牌）/ 同款隐形无重力 ArmorStand —— **即使丢了 metadata** 也清  
   - 避开：有 AI / 有配方交易 / 可见名牌的真村民；互不误伤对方 meta/tag（hub↔quest）
2. **purge 前 `loadNearChunks`（3×3）** —— 无玩家时也能扫到存盘残留  
3. 新 hitbox 同时写 **metadata + scoreboard tag**（tag 跨重启仍可认）  
4. Admin：`/corerpg hubnpc purge|count` · `/corerpg quest npc purge|count`

### Quest 零指令（两份 `quest.yml` 同源）

玩家可见 `hint` / `done` / 系统提示等 **29 处**：去掉 `/ember` `/corerpg` `/hub` `/dp` 等教指令，改为「打开枢纽菜单 → …」或右键 NPC 语义（对齐 B0.3）。**未改** quest 逻辑键名 / 目标数值 / 体力。

---

## 3. 版本与部署

| 项 | 结果 |
|----|------|
| bump | **1.15.18 → 1.15.19**（`pom.xml` + `plugin.yml`） |
| 构建 | `mvn -q -DskipTests package`（JDK8）✅ |
| 部署 | `CoreRpg/target/CoreRpg.jar` → `plugins/CoreRpg.jar` |
| 热更 | play 短重启（FIFO `console.in` + keeper）；`latest.log`：`Loading/Enabling/enabled CoreRpg v1.15.19` · `Done` |
| quest.yml | 已落 `plugins/CoreRpg/quest.yml`；`corerpg reload` → `Quest: 10 chapters loaded` |
| ops | play + login **`[]`** |

---

## 4. live 冒烟证据

| 步骤 | 证据（`server-runtime/logs/latest.log` · CST） |
|------|-----------------------------------------------|
| 启动清债 | 06:23:47 guide **purged 110**；06:23:49 smith **100** / enchanter **99** / qm **101** / clerk **1** |
| `hubnpc ensure`×2 | 每次 purge 1 再刷 1；不叠 |
| `hubnpc count`（ensure 后） | 四岗均为 **near_candidates=1 tagged_or_meta=1** |
| `quest npc count` | **near_candidates=1 tagged_or_meta=1** @ -16.5,58,106.5 |
| `corerpg reload` | 配置已重载 · 10 chapters · hubnpc n=4 |
| Citizens | **未引入**；`plugins/` 无 Citizens |
| 体力/玩法数值 | **未改** |

---

## 5. 改动文件

| 路径 | 变更 |
|------|------|
| `CoreRpg/.../HubNpcService.java` | 特征+tag purge · chunk load · `purge`/`count` |
| `CoreRpg/.../QuestService.java` | 同症 · `quest npc purge\|count` |
| `CoreRpg/.../CoreRpgPlugin.java` | help 一行 |
| `CoreRpg/src/main/resources/quest.yml` | 29 处零指令 |
| `plugins/CoreRpg/quest.yml` | 同源同步 |
| `CoreRpg/pom.xml` · `plugin.yml` | **1.15.19** |
| `plugins/CoreRpg.jar` | 换入 |
| `docs/status/STATUS-ember-hub-hitbox-quest-cleanup.md` | 本文件 |

**未** commit/push。

---

## 6. 给总控结案正文（可直接转发 · priority=true）

```
【结案 · 枢纽维护两件】priority=true
岗：插件 · CoreRpg 1.15.19
1) hitbox 叠层：根因核实——Bukkit metadata 不存盘，旧 ensure 只按 meta purge → 重启叠层。
   修：ensure 前按 meta/scoreboard-tag + 近距特征清（隐形 NoAI/无名牌村民、同款 AS）；purge 前 loadNearChunks；
   新 hitbox 打可存盘 tag。命令 hubnpc purge|count / quest npc purge|count。
   live：启动即清 guide110 / smith100 / enchanter99 / qm101 / clerk1；ensure×2 后各岗 Bukkit hitbox=1（+Ady 另计）。
2) 灰烛 quest 零指令：resources+plugins quest.yml 同源改 29 处 hint/done 等，去 /ember /corerpg 等，改枢纽菜单/右键语义；逻辑键与数值未动；corerpg reload 已加载。
禁项：未 Citizens · 未改体力/玩法数值 · 未 commit/push · ops=[]。
STATUS：docs/status/STATUS-ember-hub-hitbox-quest-cleanup.md
```

---

## 7. 风险 / 备注

1. 客户端仍可见 **Ady 包实体 + 1 只 Bukkit hitbox**（设计如此；层数应稳定为 2/岗，不再百层）。  
2. 特征 purge 半径有意收紧（hub≤3 / quest≤4），避免误伤真村民；若日后工坊旁放置真交易村民需避开半径或加白名单。  
3. 旧无 tag 残留已在本轮启动 purge 清掉；之后主要靠 tag+meta，特征路径作兜底。
