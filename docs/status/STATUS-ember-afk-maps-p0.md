# STATUS · P0 挂机②～④去空岛（贴地实景）

**日期：** 2026-09-27（Asia/Shanghai）  
**岗：** 余烬-插件岗执行器  
**依据：** `docs/design/design-ember-multiworld-maps.md` §2～§3 · `docs/design/design-ember-afk-p0-surface.md`  
**Verdict：** **✅ P0 插件侧完成**（build 贴地/洞穴、清旧岛、菜单文案、进层自检）

---

## 1. 变更文件

| 路径 | 变更 |
|------|------|
| `CoreRpg/.../AfkTierService.java` | `cmdBuild` 改为 ground/cave 主题生成；清 y110 旧岛；入口/撤离告示牌；spawn 取中心；玩家提示去 `/hub`/`/corerpg afk` 教学 |
| `plugins/CoreRpg/config.yml` | `afk_tiers` ②③④：`mode/theme`、`accent`、`relief`、`clear_old_y`；y 回写为探测值；入口 x/y/z |
| `CoreRpg/src/main/resources/config.yml` | 与现网同步 |
| `plugins/CoreRpg.jar` | Maven 重编换入 |
| `plugins/TrMenu/menus/ember_afk.yml` | 增「回枢纽」按钮（底层 `hub`）；lore 不暴露指令 |
| `plugins/TrMenu/menus/ember_hub.yml` | 挂机庭 lore：`/hub 回城` → `打开枢纽菜单可返回` |
| `plugins/MythicMobs/Spawners/EmberAfk{2,3,4}_*.yml` | 刷点 Y 对齐新地面（XZ 未改） |
| `docs/status/STATUS-ember-afk-maps-p0.md` | 本文件 |

**未改：** `afk_caps`、等级门槛、掉落表、伤害；①灰坡；无 commit/push。

---

## 2. ②③④ 新 build 要点与坐标

| 层 | 主题 | mode/theme | cx,cz | build.y（地面/厅） | 入口 spawn | 要点 |
|----|------|------------|-------|-------------------|------------|------|
| ② 荒原 | 废墟台地 | ground/ruins | 200,250 | **61** | **(200.5, 62.0, 250.5)** | 碎砖/圆石/砂砾缓坡；半墙掩体簇；矮墙围边；**无屏障牢笼**；石英入口垫 + 双告示 |
| ③ 焦土 | 焦裂谷 | ground/scorched | 400,250 | **69**（谷底） | **(400.5, 70.0, 250.5)** | 落差约 4～7；下界砖/地狱岩/黑曜石；岩浆沟+篱笆护栏；侧台地 |
| ④ 烬原深处 | 洞穴庭 | cave/cave | 600,250 | **63**（扩厅） | **(600.5, 64.0, 250.5)** | 地表竖井口+坡道；厅内柱与高低台；海晶灯；中心开阔防窒息 |

- `r=18` · `region_margin=24` 保留；**禁止 y=110**。  
- build 流程：清旧岛 → 自 y85 下探最高实心非屏障 → 主题塑形 → 回写 `build.y` 与入口 x/y/z。  
- 告示：入口「打开/ember→挂机庭」；撤离「回枢纽 / 打开枢纽菜单返回」（**不写请打 /hub**）。

### 旧坐标（对照）

| 层 | 旧 build | 旧 MM Y |
|----|----------|---------|
| ②③④ | `y:110` 空中平板 + 5 格屏障墙 | 111 |

---

## 3. 旧空岛是否清理

**是。** 每次 `/corerpg afk build n` 先空气化 `clear_old_y=110` 附近（y 107～124，r+3）：

- ②清 **3802** 格 · ③清 **3802** · ④清 **3802**

---

## 4. 菜单文案

- `ember_afk.yml`：进层仍底层 `corerpg afk n`；新增 **「回枢纽」**（lore：打开枢纽菜单返回；action：`hub`）。  
- `ember_hub.yml` 挂机庭 lore 去掉 `/hub` 教学。  
- 进层聊天：「打开枢纽菜单可返回」；升级解锁：「打开 /ember → 挂机庭」。

---

## 5. 自检与 ops

| 检查 | 结果 |
|------|------|
| Maven `CoreRpg.jar` → `plugins/` | ✅ |
| 短重启后 `afk build 2/3/4` | ✅ 见上表坐标 |
| RpgBot 进②③④落点 | ✅ 200.5/62/250.5 · 400.5/70/250.5 · 600.5/64/250.5 |
| `/hub` 回枢纽 | ✅ (-18.5,58,110.5) |
| `mm reload` / `trmenu reload` | ✅ |
| `ops.json` | ✅ **`[]`**（测后已 `/deop RpgBot`） |

---

## 6. STATUS 路径

`docs/status/STATUS-ember-afk-maps-p0.md`

---

## 7. 风险（刷点/地形）

1. **②地表 y=61** 略低于短篇「约 64～80」带（该柱实测地表）；玩法可用，若强求对齐可再垫高一层台地。  
2. **③刷点 XZ 仍在谷坡相对坐标**（192/208/200 @ z258/262），Y 已改 70；若个别怪站坡差格，suffocation 护栏仍在，可微调 MM XZ 或再 build。  
3. **④洞穴** 厅顶/坡道为程序生成，竖井口在中心偏南；极端情况下坡道路径可能需手工补 1～2 级台阶。  
4. `saveConfig()` 会把 `afk_tiers` 展开为多行 YAML（键成 `'2'` 等），与 resources 已对齐；勿与旧行内 `{ y:110 }` 混用。  
5. 世界存档不进 git——换机需再跑 `/corerpg afk build 2/3/4`。

