# STATUS · B2.4 枢纽 / 工坊告示降级为指路

**日期：** 2026-09-27 23:53 CST（Asia/Shanghai）<br>
**依据：** `docs/design-ember-content-backlog.md` §B2.4 · `docs/design-ember-hub-workshop-npcs.md`
**Verdict：** **✅ PASS（源码已清理；世界牌面已用 CoreRpg 1.15.11 重建；jar 由 F2 岗部署）**

## 结论

工坊 NPC（烬砧、余晶、灰粮）与灰烛已存在，枢纽告示不再把 `/ember` 作为唯一入口。主牌现在只做指路：右键灰烛看主线、右键工坊 NPC，或打开枢纽菜单。所有本轮涉及的生成式告示文案均不再教学 `/ember`、`/corerpg` 或 `/dp`。

## Before / After

| 位置 / 生成器 | Before | After |
|---|---|---|
| `ember_hub` 入口牌（代码坐标 `(-19,58,113)`） | `枢纽广场 / 打开 /ember / 看菜单与玩法 / 勿手打指令` | `枢纽广场 / 右键灰烛·看主线 / 工坊：右键 NPC / 或打开枢纽菜单` |
| `ember_hub` 工坊牌（`(-13,58,105)`） | 已是 `锻炉师·烬砧 / 就在棚里 / 右键他 / 勿手打指令` | 保持；已有 NPC 指路，不是命令入口 |
| `ember_hub` 灰烛指路牌 | `引路人·灰烛 / 右键交谈 / 主线在此` | 保持；已是右键指路 |
| `ember_hub` 回枢纽牌 | `回枢纽 / 你已在枢纽 / 打开枢纽菜单 / 继续旅程` | 保持；已是菜单指路 |
| `ember_afk` 分层入口牌（`AfkTierService`） | `标题 / 打开/ember / →挂机庭换层 / 死亡回入口` | `标题 / 枢纽菜单 / →挂机庭换层 / 死亡回入口` |

## 审计证据

- **CoreRpg / 世界编辑：** `HubPlazaService.placeSigns` 是枢纽入口牌的生成源；`AfkTierService.placeTierSigns` 是另一处玩家可见的 `/ember` 告示生成源，均已改为 NPC / 枢纽菜单式指路。
- **HolographicDisplays：** `plugins/HolographicDisplays/database.yml` 只有战力、深渊、竞速榜及挂机庭标题全息；无 workshop/hub 命令入口，按其 README 约定不手改数据库。
- **DecentHolograms：** 仓库未发现该插件或其配置。
- **Adyeshach：** 四个 NPC JSON 只有名称、位置和实体属性；未发现命令教学文本。
- **TrMenu：** `ember_hub.yml` 已用「也可找工坊：烬砧 / 余晶 / 灰粮（右键）」；锻炉、附魔引导、补给菜单的玩家文案均通过菜单 / 右键 NPC 导航。菜单 action 中保留的 `command:` 是后台点击动作，不是告示教学。

## 文件 touched

- `CoreRpg/src/main/java/town/sunshine/corerpg/HubPlazaService.java` — 枢纽入口牌由 `打开 /ember` 改为灰烛 / 工坊 NPC / 枢纽菜单指路。
- `CoreRpg/src/main/java/town/sunshine/corerpg/AfkTierService.java` — 分层入口牌去掉 `打开/ember`，改为「枢纽菜单」。
- `docs/STATUS-ember-b24-signs.md` — 本验收记录。

非告示的旧聊天 hint / 菜单后台命令属于 B0.3，不在本 B2.4 牌面清理范围内。

---

## 部署节（B2.4 牌面落地 · 2026-09-28 00:00 CST）

| 项 | 内容 |
|---|---|
| **CoreRpg 版本** | **1.15.11**（play 已启用；由深渊 F2 修复执行器升版并部署 jar，本岗**未**再打 jar / 未再换 jar） |
| **jar 路径** | `plugins/CoreRpg.jar`（`server-runtime/plugins` → `../plugins` 软链同源） |
| **源码告示** | commit `8a22023`：`HubPlazaService` 入口牌 →「右键灰烛·看主线 / 工坊：右键 NPC / 或打开枢纽菜单」；`AfkTierService` 分层入口牌 →「枢纽菜单 / →挂机庭换层」 |
| **本岗动作** | 短停写入临时 OP（RpgBot）→ 热启 play → 重建告示 → `/deop`；`ops.json` 终态 **`[]`** |
| **未改** | 玩法数值、票、掉落、Boss HP；未 commit / push |

### 重建命令与结果

| 命令 | 结果（日志 CST） |
|---|---|
| `/corerpg hubbuild` | `plaza done · blocks≈439` · spawn=(-18.5,58,110.5) · `hubbuild changed≈439`（00:00:29） |
| `/corerpg afk build 2` | 荒原 @ 200,61,250 · changed=462 · 入口 200.5,62.0,250.5（00:00:34） |
| `/corerpg afk build 3` | 焦土 @ 400,64,250 · changed=15857 · 入口 400.5,65.0,250.5（00:00:39） |
| `/corerpg afk build 4` | 烬原深处 @ 600,63,250 · changed=369 · 入口 600.5,64.0,250.5（00:00:44） |

层 1（灰坡）为自然地形、无 `afk build` 配置，无需重建。

### 验收建议

1. 进 `ember_hub`，看入口牌（约 -19,58,113）：应为「枢纽广场 / 右键灰烛·看主线 / 工坊：右键 NPC / 或打开枢纽菜单」，**不应**再出现「打开 /ember」。
2. `/corerpg afk 2`（或菜单进②），入口北侧牌：标题行 +「枢纽菜单」+「→挂机庭换层」，**不应**再出现「打开/ember」。
3. 抽检③④同理；回枢纽牌保持「打开枢纽菜单」。
4. 日志确认版本：`CoreRpg 1.15.11 enabled`。
5. 可复跑（勿在玩家堆物时盲跑）：`/corerpg hubbuild` · `/corerpg afk build 2|3|4`（需 `corerpg.admin` / OP）。

