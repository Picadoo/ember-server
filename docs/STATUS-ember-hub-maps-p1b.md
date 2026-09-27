# STATUS · P1b 灰烛右键交互返工

**日期：** 2026-09-27 20:45–20:48（Asia/Shanghai）  
**岗：** 余烬-插件岗执行器  
**依据：** P1 测试 FAIL（`STATUS-ember-hub-maps-p1-test.md` 项 2b）；Ady 包实体 `use_entity` 无对话、`/corerpg quest talk` 可通  
**Verdict：** **✅ 右键 talk 已通**（mineflayer `activateEntity` / `use_entity` → 灰烛对话并推进主线）

---

## 1. 根因结论

| 线索 | 结论 |
|------|------|
| Adyeshach NPC 为**包实体** | 客户端可见 villager（id≈450191，名「引路人 · 灰烛」），但 Bukkit 侧无对应实体；mineflayer 点包实体 id 时，若 Ady 事件未进本插件监听则静默失败 |
| 原 hook 只注册 `ink.ptms.adyeshach.**core**.event.AdyeshachEntityInteractEvent` | jar 内同时有 `api.event`（Outdated but usable）与 `core.event`；单注册可能漏事件 |
| `isMainHand` 硬过滤 | 协议/副手差异下 bot 右键可能被直接丢弃 |
| 命令 talk 走坐标半径 | 故 `/corerpg quest talk` 一直 PASS，掩盖交互链路断点 |

**综合：** 交互 FAIL = Ady 事件监听面过窄 + 无真实 Bukkit hitbox 兜底；QuestService 业务 `talk()` 本身正常。

---

## 2. 改动文件

| 文件 | 变更 |
|------|------|
| `CoreRpg/.../QuestService.java` | ① **双注册** `api` + `core` 的 `AdyeshachEntityInteractEvent`；② **放宽**不再要求 `isMainHand`；匹配 `id=ember_guide`，fallback 自定义名含「灰烛」或距配置坐标 ≤2；③ **真实 hitbox**：隐形 Silent NoAI Nitwit 村民，metadata `corerpg_quest_npc=ember_guide`；`PlayerInteractEntityEvent` → `talk`（取消交易 UI）；④ 近距 `PlayerInteractEvent` RIGHT_CLICK_AIR/BLOCK 且视线朝向 NPC（dot≥0.72、≤3 格）兜底；⑤ hitbox 免伤 |
| `CoreRpg/.../HubPlazaService.java` | `hubbuild` 完成后 `ensureNpc(true)`（同步 Ady + hitbox） |
| `CoreRpg/.../CoreRpgPlugin.java` | `reloadLocal` 后异步 `ensureNpc(false)` |
| `CoreRpg/pom.xml` · `plugin.yml` | 版本 **1.15.2 → 1.15.3** |

**未改：** afk ②～④、战斗数值、主角本 YAML 大改；**未 commit / push**。

制品：`plugins/CoreRpg.jar`（1.15.3）；短停 `server-runtime` 换 jar 后重启。

---

## 3. 自检方式与结果

### 服务端日志
- `CoreRpg 1.15.3 enabled`
- `Quest: Adyeshach interact hook registered: …core.event…`
- `Quest: Adyeshach interact hook registered: …api.event…`
- `Quest: Adyeshach interact hooks active=2 (npc id ember_guide)`
- `Quest: Bukkit hitbox villager ensured at ember_hub -16.5,58.0,106.5`

### mineflayer 右键（`/tmp/hub-p1b-interact.js`）
- 客户端同时见 **2** 只 villager @ (-16.5,58,106.5)：id **45**（Bukkit hitbox，无外显名）+ id **450191**（Ady 包实体，名「引路人 · 灰烛」）
- 走近至距 NPC ≈0.77 格后 `activateEntity(45)` → 立即：
  - `灰烛：这城烧了三十年，火没灭，只是躲进了地底。`
  - `灰烛：挂机庭有游荡的尸骸，先去活动活动筋骨。`
  - 主线推进至「挂机庭击败僵尸 0/6」
- **PASS**（协议右键链路已通）

### 场景块 / ops
| 检查 | 结果 |
|------|------|
| NPC 脚下 (-17,57,106) | `sea_lantern`（垫灯，不挡站立） |
| NPC 脚位 y58 | `air` |
| 灰烛告示 (-19,58,107) | `standing_sign`（偏西，不挡右键） |
| `server-runtime/ops.json` · `login-runtime/ops.json` | **`[]`** |

---

## 4. STATUS 路径

`docs/STATUS-ember-hub-maps-p1b.md`（本文件）

关联：`STATUS-ember-hub-maps-p1-test.md`（2b FAIL）· `docs/STATUS-ember-hub-maps-p1.md`（广场施工）

---

## 5. 风险

| 风险 | 说明 / 缓解 |
|------|-------------|
| 双村民叠位 | hitbox 隐身 + 无自定义名，玩家只见 Ady 名牌；极端客户端可能实体列表多一条 |
| Nitwit / 交易 | 已 `NITWIT` + 清空 recipes + `PlayerInteractEntityEvent` cancel，避免弹出交易 |
| 碰撞 / 挡路 | `setCollidable(false)`、`setAI(false)`；不挡视线 |
| Ady 事件仍可能不进 bot | hitbox 兜底已验证；真实玩家右键 Ady 视觉时优先走双 hook，点空则 look-talk |
| look-talk 误触 | 需 ≤3 格且朝向 dot≥0.72；另有 1s talk 冷却 |
| 热重载不足 | 新 hook/类需停服换 jar（本轮已短重启）；`/corerpg reload` 仅刷新 hitbox 坐标 |
| 挂机/战斗 | 本轮未触碰相关配置与数值 |

**建议测试岗：** 复跑 P1 项 2b（灰烛可交互）；可用普通号走近右键，或 mineflayer `activateEntity` 点 hitbox/Ady 任一 villager。
