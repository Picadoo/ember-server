# STATUS · 分解 / 重铸（菜单壳 + NI）

**日期：** 2026-09-12（Asia/Shanghai）  
**承接：** `DESIGN-ember-rpg-systems.md` §3.4；强化/镶嵌 `DESIGN-ember-enhance-socket.md`（§9 曾列分解为非目标）  
**约束：** 未改 Paper；CoreRpg **1.3.2** 已接线 scrap/reforge（本岗）

---

## DESIGN 片段（本期规格）

对齐总纲 §3.4：分解多余刃/护符 → 强化材料 + 低概率孔石；重铸刷新**次要**词缀，主词缀不乱飙。

| 项 | 定案 |
|----|------|
| 白名单 | `gear_ember_blade` · `gear_ember_charm`（与强化同一白名单） |
| 分解产出 | `mat_ember_shard` / `mat_ember_bone_dust`；低概率四孔石（`gem_ember_sharp\|steady\|drain\|gale`） |
| 重铸消耗 | `mat_ember_reforge_stone`（余烬重铸石）×1；**不耗核心** |
| 重铸效果 | 只刷次要词缀；主词缀 / 强化等级 / 孔石 **不变** |
| 菜单 | `ember_disassemble.yml` · hub **W** → `menu: ember_disassemble` |
| 命令（插件岗后） | `/corerpg scrap` · `/corerpg reforge` |
| 聊天前缀 | `[分解]` / `[重铸]`（Bot 可断言） |

### 命令面（插件岗待接，本期只占位）

| 命令 | 行为（拟定） |
|------|----------------|
| `/corerpg scrap` | 手持白名单装备，销毁并按表给材料/低概率孔石 |
| `/corerpg scrap info` | 只读预览产出（可选，插件岗可后补） |
| `/corerpg reforge` | 手持装备，扣 1× `mat_ember_reforge_stone`，刷新次要词缀 |

权限拟定：`corerpg.scrap` / `corerpg.reforge`（默认真玩家有）。  
未实装时菜单 `command:` 可能失败，**tell 回退**不崩。

### 使魔蛋 stub（同批 NI，逻辑仍待 pet 模块）

| NI ID | 显示名 | 定位 |
|-------|--------|------|
| `pet_ember_ashling` | 余烬灰灵 | 外观 / 微光 |
| `pet_ember_cinder` | 余烬烬火 | 外观 / 余火 |

文件：`plugins/NeigeItems/Items/ember-pets.yml`。出战仍走 `/corerpg pet summon`（未接）。

---

## 已落地

| 路径 | 说明 |
|------|------|
| `plugins/NeigeItems/Items/ember-disassemble.yml` | **新增** `mat_ember_reforge_stone`（余烬重铸石） |
| `plugins/NeigeItems/Items/ember-pets.yml` | **新增** `pet_ember_ashling` · `pet_ember_cinder`（外观蛋） |
| `plugins/TrMenu/menus/ember_disassemble.yml` | 分解 / 重铸子菜单壳 |
| `plugins/TrMenu/menus/ember_hub.yml` | **W 分解** → `menu: ember_disassemble`；**已去「即将点燃」** |
| `plugins/TrMenu/menus/README-ember.md` | 登记 `ember_disassemble.yml` |
| `docs/ember-hub-copy.md` | 分解文案同步（已接壳） |
| `STATUS-ember-disassemble.md` | 本文（含 DESIGN 片段） |

`server-runtime/plugins` → `plugins` **同一 inode**，无需镜像拷贝。

### 菜单行为

- 打开：tell 提醒手持刃 / 护符；逻辑待接线。
- 分解图标：tell + `command: corerpg scrap` + close。
- 重铸图标：tell + `command: corerpg reforge` + close。
- 规则：tell 速览，不关菜单。
- 返回：`menu: ember_hub`。

---

## 插件岗 DONE（1.3.2）

1. **DONE** `/corerpg scrap` · `/corerpg scrap info` · `/corerpg reforge`  
2. **DONE** `plugins/CoreRpg/scrap.yml`（+ jar resources）；白名单同强化  
3. 词缀残页（精英掉落）若另做 NI，不与重铸石混 ID（仍开放）  
4. **DONE** CoreRpg.jar → **1.3.2**（保留 cash/monthly/covenant/talent/enhance；**未**做 pet summon；**未**改 Paper）

| 路径 | 说明 |
|------|------|
| `CoreRpg/.../ScrapService.java` | 分解 / 重铸逻辑 |
| `CoreRpg/.../GearLore.java` | `#ember_aff:` + `次要:` 读写 |
| `plugins/CoreRpg/scrap.yml` | 产出表 + 次要词缀池 |
| 前缀 | `[分解]` / `[重铸]` |
| 权限 | `corerpg.scrap` / `corerpg.reforge` default true |

**重启：** 已要求服务端重启以加载 1.3.2。

---

## 未改

Paper · MythicMobs / EmberCrypt 掉落 · AttributePlus · pet summon（未做）。CoreRpg.jar 已升 1.3.2（本岗）。

---

## 验收对照

| # | 标准 | 本期 |
|---|------|------|
| 1 | hub 分解 lore **无**「即将点燃」，打开 `ember_disassemble` | **是** |
| 2 | 子菜单可开可回 hub | **是**（YAML） |
| 3 | NI 可 `/ni give <p> mat_ember_reforge_stone 1`（需 `/ni reload`） | **是**（定义已落） |
| 4 | NI 可 give `pet_ember_ashling` / `pet_ember_cinder` | **是**（定义已落） |
| 5 | 命令未装时 tell 不崩；不改 Paper / 不覆写 jar | **是**（菜单期） |
| 6 | `/corerpg scrap` / `reforge` 可用；scrap.yml 落地；jar 1.3.2 | **是**（插件岗） |

热重载：`/ni reload` · `/trmenu reload`。插件岗：**服务端重启**加载 CoreRpg 1.3.2。
