# STATUS · Ember 使魔魂尘 feed smoke — ready / waiting for CoreRpg 1.3.9

**日期：** 2026-09-13 12:31（Asia/Shanghai）  
**规格：** `docs/design/DESIGN-ember-pet-bestiary.md` · `docs/status/STATUS-ember-pet.md` · 源码 `pet.yml` `feed:`  
**约束：** 未改 Paper；**未覆写** `CoreRpg.jar`；**未跑** server smoke

---

## CoreRpg 部署检查

| 项 | 值 |
|----|-----|
| `plugins/CoreRpg.jar` | **198825** bytes · mtime **12:17 CST**（2026-09-13 04:17 UTC） |
| `plugin.yml` version（已部署） | **1.3.8** |
| jar 内 `pet.yml` | **无** `feed:` 块（仅 ashling/cinder + follow） |
| `PetService` feed | **stub** · tell「暂未开放魂尘」 |
| 源码 `plugin.yml` | **1.3.9**（未部署） |
| 源码 `resources/pet.yml` | **有** `feed.enabled` · `item: mat_ember_soul_dust` · `max_level: 10` · cost/power_per_level |
| `PlayerData.petLevels` | 源码 **已有**；需 1.3.9 jar 接线 `cmdFeed` |

**结论：** 魂尘喂养 **尚未打进已部署 1.3.8 jar** → **不跑** `pet-feed-smoke.js`，等插件岗正式部署 **CoreRpg 1.3.9**（真实 feed，非 stub）后再测。

---

## 已准备

| 路径 | 内容 |
|------|------|
| `mineflayer-tests/pet-feed-smoke.js` | RpgBot · NI give `mat_ember_soul_dust`（若不存在则 NOTE）→ unlock/summon `pet_ember_ashling` → `/corerpg pet feed` ×2 + list |
| `plugins/NeigeItems/Items/ember-pets.yml` | 含 `mat_ember_soul_dust`（余烬魂尘）· 蛋 `pet_ember_ashling` |
| `docs/design/DESIGN-ember-pet-bestiary.md` | 升级吃魂尘 · `/corerpg pet feed` |

### 脚本期望命令面（1.3.9）

```
/ni give RpgBot pet_ember_ashling 1
/ni give RpgBot mat_ember_soul_dust 16   # 若 NI 无此 ID → NOTE_SOUL_DUST_MISSING，仍探测 feed
/corerpg pet unlock pet_ember_ashling
/corerpg pet summon pet_ember_ashling
/corerpg pet feed                        # 消耗魂尘升级；非「暂未开放魂尘」
/corerpg pet list                        # 可见等级 / 展示分变化（以落地为准）
```

源码 `pet.yml` 草案：`cost = cost_base + (level-1)*cost_per_level`；`power_per_level` 叠展示分；`max_level: 10`。

---

## 等待插件岗

1. 将真实 `cmdFeed`（读 `feed:`、扣 `mat_ember_soul_dust`、写 `petLevels`）编入 **`CoreRpg.jar` 1.3.9**  
2. 部署后 `plugin.yml` version **1.3.9**；jar/`plugins/CoreRpg/pet.yml` 含 `feed:`；feed **不再**仅 stub「暂未开放魂尘」  
3. **不要**用本机随意覆盖 jar；由插件岗正式部署后再测  
4. 服重启（新类不可热重载）后跑 smoke；若运行时 `pet.yml` 缺 `feed:`，以 jar 默认/插件岗补丁为准  

---

## 部署后复跑

```bash
cd /workspace/minecraft/mineflayer-tests
# 确保无其它 RpgBot 在线；CoreRpg.jar == 1.3.9 且 pet feed 已接线
node pet-feed-smoke.js
```

期望：`[使魔]` 喂养成功 / 等级上升 tell（或魂尘不足明确提示）；**不是**仅「暂未开放魂尘」。NI 无魂尘 ID 时脚本会 `NOTE_SOUL_DUST_MISSING` 仍打 feed。

---

## 未改

Paper / `CoreRpg.jar` / 其它 jar · 未执行 pet-feed smoke · 未改 NI / TrMenu 使魔壳
