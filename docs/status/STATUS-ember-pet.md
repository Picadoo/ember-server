# STATUS · 使魔 pet 模块（CoreRpg 1.3.5）

**日期：** 2026-09-13（Asia/Shanghai）  
**规格：** `docs/design/DESIGN-ember-pet-bestiary.md` · NI `ember-pets.yml`  
**约束：** 未改 Paper；**不卖满级战力宠**（外观跟随 + 天梯展示分小加成）

---

## 已落地

| 项 | 状态 |
|----|------|
| `PetService` | **DONE** · ArmorStand 小标记跟随（10t / ~2.5 格） |
| `pet.yml` | **DONE** · ashling / cinder · power_bonus 5（展示） |
| PlayerData `petsUnlocked` / `activePet` | **DONE** |
| 解锁消耗蛋×1；解锁后 summon 免费 | **DONE** |
| 首次 summon 持蛋也可解锁（耗蛋） | **DONE** |
| 退出 / dismiss / 换世界 清实体 | **DONE** |
| `/corerpg pet feed` stub | **DONE** · 暂未开放魂尘 |
| Ladder `use_pet_bonus` | **DONE** · activePet 解锁后加 YAML bonus |
| CoreRpg.jar **1.3.5** | **DONE** |
| 公会/guild | **未做**（本期不做） |

### 命令（前缀 `[使魔]`）

| 命令 | 行为 |
|------|------|
| `/corerpg pet` / `pet list` | 已解锁 + 选定/出战 |
| `/corerpg pet summon [id]` | 出战（默认 active/末个）；未解锁需持蛋并消耗×1 |
| `/corerpg pet dismiss` | 收回实体 |
| `/corerpg pet unlock <id>` | 持蛋消耗×1 永久解锁 |
| `/corerpg pet feed` | 暂未开放魂尘 |

### NI 蛋

- `pet_ember_ashling` · 余烬灰灵  
- `pet_ember_cinder` · 余烬烬火  

```
/ni give <玩家> pet_ember_ashling 1
/corerpg pet unlock pet_ember_ashling
/corerpg pet summon
/corerpg pet dismiss
```

---

## jar

| 项 | 结果 |
|----|------|
| 路径 | `plugins/CoreRpg.jar` |
| 版本 | **1.3.5** |
| 大小 | **160594** bytes |

**Restart required** — 新类不可热重载。

---

## 未做

魂尘喂养升级 · 图录登记 · 公会 · 卖战力宠（刻意不做）
