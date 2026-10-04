# D159 — 洗练可用装备库重复件 — CoreRpg 1.64.2 — 2026-10-04

## 部署
- **版本：** CoreRpg **1.64.2**（JDK8，class major 52）
- **重启：** 10:30 CST（`server-runtime/stop.sh` → `start.sh`），0 人在线
- **日志：** `Enabling CoreRpg v1.64.2` · `[storage] MySQL connected` · `Enabling CoreGacha v1.0.1` · `[db] MySQL connected` · `Done (5.904s)`
- **备份：** `/workspace/backup/CoreRpg-1.64.1-pre-d159.jar`
- **单测：** `EmberGrowthTest` + `EmberStorageRulesTest`（含新 `libDupEligibleForReroll`）绿

## 改动
1. **洗练吃装备库重复件（review round2 #10）**
   - `EmberStorageRules.libDupEligible`：未锁定 / 未收藏 + `EmberAffix.duplicateOk`
   - `EmberGearLib.findDupForReroll` / `consumeForReroll`：`stored → dismantled`，ledger kind `reroll`（不可撤销，不给胚料）
   - 进服 `ensureLoaded`，避免自动入库后缓存空、PAPI 仍说没有重复件
   - `EmberGrowthService`：优先背包重复件，否则装备库；预览 / PAPI 「装备库里有重复件」/「背包或装备库」
2. **Hub 图标：** `ember_hub.yml` U 格 `nether star` → **`ender pearl`**（与赛季 nether star 区分）
3. **扭蛋页文案（#9，不改经济）：** 「外观商店的件可以直接用余烬币买；已经买过的抽到返还光屑。」（`genmenus.py` 再生）
4. **洗练页说明：** 重复件来源写明「背包或装备库」
5. **不加周规则**（仍 6 条）

## 冒烟 FreshQ51 — `tools/p1map/d159-reroll-gearlib-smoke.sh` → **PASS=7 FAIL=0**
- 发 T1 焚烬刃 + `givedup` → `/corerpg p1 stash` 后背包只剩手上刃
- 预览：「花费：一件同部位同阶的重复件 **（装备库）** + 300 余烬币」
- `confirm` → 洗练结果装上词条；装备库件被吃掉（kind=reroll）
- 再 `givedup` 留在背包 → 预览「（背包）」→ confirm 仍可用
- hub 菜单可打开；`ops.json` = `[]`

## 下一测号
FreshQ52 / FreshG07（FreshG 未在本窗用）
