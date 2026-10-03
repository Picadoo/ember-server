# STATUS · 使魔魂尘 feed（CoreRpg 1.3.9）

**日期：** 2026-09-13 12:32（Asia/Shanghai）  
**规格：** `docs/design/DESIGN-ember-pet-bestiary.md` · feed 轻量升级  
**约束：** 未改 Paper；保留 arena / auction / guild boss；魂尘用既有 `mat_ember_soul_dust`（`ember-pets.yml`）

## 已落地

| 项 | 状态 |
|----|------|
| `PlayerData.petLevels` | DONE · Map 默认 1 · YAML 持久化 |
| `pet.yml` feed 段 | DONE · max 10 · cost_base/per_level · power_per_level |
| `PetService.getPowerBonus` | DONE · base + (Lv-1)*power_per_level |
| `/corerpg pet feed [次数]` | DONE · 优先主手魂尘 |
| list 显示 Lv | DONE |
| NI `ember-soul-dust.yml` | **未新建**（物品岗已写入 `ember-pets.yml`） |

### 命令

```
/corerpg pet list
/corerpg pet feed          # 升 1 级
/corerpg pet feed 3        # 尝试连升 3 级
/ni give <玩家> mat_ember_soul_dust 16
```

### jar

| 项 | 结果 |
|----|------|
| 路径 | `plugins/CoreRpg.jar` |
| 版本 | **1.3.9** |
| 大小 | **202379** bytes |
| 重启 | `./start.sh custom` · log `CoreRpg 1.3.9 enabled (.../pet/guild/arena/auction)` |
