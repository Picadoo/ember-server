# STATUS · Ember 使魔 pet smoke — ready / waiting for jar

**日期：** 2026-09-13 12:00（Asia/Shanghai）  
**规格：** `docs/design/DESIGN-ember-pet-bestiary.md`  
**约束：** 未改 Paper；**未覆写** `CoreRpg.jar`；**未跑** server smoke

---

## CoreRpg 部署检查

| 项 | 值 |
|----|-----|
| `plugins/CoreRpg.jar` | **148366** bytes · mtime **11:55 CST**（2026-09-13 03:55 UTC） |
| `plugin.yml` version | **1.3.4** |
| description | 含天梯；**无**使魔 / pet |
| usage | spawn…ladder… · **无** `pet` |
| `PetService.class` / `pet.yml` in jar | **无**（deployed = target 同 inode 级内容） |
| 源码 | `CoreRpg/src/.../PetService.java` · `resources/pet.yml` **已有** |

**结论：** pet 模块 **尚未打进 jar** → **不跑** `pet-smoke.js`，等插件岗出含 PetService 的包后再测。

---

## 已准备

| 路径 | 内容 |
|------|------|
| `mineflayer-tests/pet-smoke.js` | RpgBot · NI give 蛋 ×2 → `/corerpg pet` / `list` / `summon ashling\|cinder` + 全 ID / `dismiss` / `unlock` / `feed` |
| `plugins/NeigeItems/Items/ember-pets.yml` | stub 蛋 `pet_ember_ashling` · `pet_ember_cinder`（既有） |
| `plugins/TrMenu/menus/ember_pet.yml` | 使魔菜单壳（既有） |
| `docs/design/DESIGN-ember-pet-bestiary.md` | ID / 命令占位 |

### 脚本期望命令面

```
/ni give <玩家> pet_ember_ashling 1
/ni give <玩家> pet_ember_cinder 1
/corerpg pet
/corerpg pet list
/corerpg pet summon ashling|cinder   # 短名；亦试 pet_ember_* 全 ID
/corerpg pet dismiss
/corerpg pet unlock pet_ember_ashling
/corerpg pet feed                    # 可 tell「暂未开放魂尘」
```

源码 `PetService` 键为 `pet_ember_ashling` / `pet_ember_cinder`；短名 `ashling`/`cinder` 是否别名以落地 jar 为准（smoke 两边都打）。

---

## 等待插件岗

1. 将 `PetService` + `pet.yml` 编入 `CoreRpg.jar`（接线 `/corerpg pet …`）  
2. `plugin.yml` usage / description 出现 **pet**（或 STATUS 标明已接线）  
3. **不要**用本机随意覆盖 jar；由插件岗正式部署后再测  
4. 服重启（或可靠重载）后跑 smoke  

---

## 部署后复跑

```bash
cd /workspace/minecraft/mineflayer-tests
# 确保无其它 RpgBot 在线；CoreRpg.jar 已含 PetService + pet.yml
node pet-smoke.js
```

期望：`[使魔]` 列表/出战/收回 tell；未知子命令不崩；NI 蛋可给。

---

## 未改

Paper / `CoreRpg.jar` / 其它 jar · 未执行 pet smoke · 未改 NI / TrMenu 使魔壳
