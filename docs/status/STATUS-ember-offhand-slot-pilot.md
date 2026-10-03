# STATUS · B-flex-1 副手/饰品位试点 A（插件施工）

> 一句话：**独立 `stats.offhand` 白名单 + `StatService` OffHand 钩子 + hub/set 薄展示；CoreRpg 1.15.24；NI 由物品岗 `52f817a`，本窗未改。**

| 字段 | 值 |
|------|-----|
| 稿件 | 设计 tip `7dda194` · 批准 tip `eb3c843` · `docs/design/design-ember-offhand-slot-pilot.md` |
| 专岗 | 余烬-插件 · 2026-09-29 Asia/Shanghai |
| 版本 | CoreRpg **1.15.24**（自 1.15.23） |
| 热更 | play 短重启 Enabling **v1.15.24** @ **00:54:14 CST** · Done @ **00:54:15 CST** · `trmenu reload` 36 菜单 @ **00:54:26 CST** · `corerpg reload` 配置已重载 @ **00:54:28 CST** |
| NI | **本窗未写**；物品 tip `52f817a`（`ember-gear-offhand.yml` · ward/vita） |
| push | 本窗总控要求完工推 tip → 见交付 commit |

---

## 1. 对批

| 批准项 | 落地 |
|--------|------|
| 独立 OffHand 白名单 | `stats.offhand`：`acc_ember_offhand_ward` / `acc_ember_offhand_vita`（双路径 config） |
| `StatService` OffHand 钩子 | `offhandIds`；`getItemInOffHand()` ∈ 白名单 → `itemStats` 累加；与护符 best **并行** |
| 试点 id **不进** accessories | config 仅 `offhand:`；护符扫描若 id∈offhandIds **跳过**（防误配双计） |
| hub/set 薄展示 | `ember_set` 增图标 O + Open tell；`ember_hub` 套装 lore 半句「副手饰品 · 放副手槽生效」 |
| 不教斜杠 | 无 `/corerpg` 教学；短 tell 人话 |

---

## 2. 钩子 / config / 菜单

### StatService（`CoreRpg/.../StatService.java`）

- 字段 `List<String> offhandIds`；`reload()` 读 `stats.offhand`（空则空列表，**勿**塞护符默认）
- `compute()`：护符 best 之后读 OffHand；复用 lore `生命力`/`物理防御`（**不加**物伤轴）
- 护符扫描：`offhandIds.contains(id)` → continue
- 刷新：`PlayerSwapHandItemsEvent` + `InventoryClickEvent`（slot 40 / raw 45）+ `InventoryDragEvent`（slot 40/45）→ `refreshLater`
- 类注释已说明 offhand 独立档

### Config（双路径）

```yaml
stats:
  offhand:
  - acc_ember_offhand_ward
  - acc_ember_offhand_vita
```

- `plugins/CoreRpg/config.yml` + `CoreRpg/src/main/resources/config.yml`
- **未改** `afk_caps` / `over_chance*` / 体力门

### TrMenu

| 菜单 | 改动 |
|------|------|
| `ember_set.yml` | Layout 增 `O`；Open tell「副手饰品 · 放副手槽生效」；图标 lore + 短 tell |
| `ember_hub.yml` | 套装键 `V` lore 增半句「副手饰品 · 放副手槽生效」 |

### 版本

- `plugin.yml` `1.15.23` → `1.15.24`（jar 内嵌；无独立 live plugin.yml）

---

## 3. rg 证据（抽样）

```bash
rg -n "offhandIds|stats.offhand|PlayerSwapHandItems|getItemInOffHand" \
  CoreRpg/src/main/java/town/sunshine/corerpg/StatService.java
rg -n "offhand:" plugins/CoreRpg/config.yml CoreRpg/src/main/resources/config.yml
rg -n "副手饰品" plugins/TrMenu/menus/ember_{set,hub}.yml
rg -n "acc_ember_offhand_" plugins/NeigeItems/Items/ember-gear-offhand.yml
rg "Enabling CoreRpg v1.15.24" server-runtime/logs/latest.log | tail -1
```

预期：源码钩子齐全；双路径 offhand 两 id；菜单「副手」；NI 两件（物品岗）；日志 Enabling v1.15.24。

---

## 4. 构建 / 热更

```
export JAVA_HOME=/workspace/minecraft/tools/jdk8u504-b01
export PATH="$JAVA_HOME/bin:/workspace/minecraft/tools/apache-maven-3.9.16/bin:$PATH"
cd CoreRpg && mvn -o -q -DskipTests package
cp -f target/CoreRpg.jar ../plugins/CoreRpg.jar   # 与 server-runtime 同 inode
# FIFO 短重启（console.in + keeper）→ Enabling v1.15.24
printf 'trmenu reload\n' > server-runtime/console.in
printf 'corerpg reload\n' > server-runtime/console.in
```

- jar **未** git add。
- 热更 CST：**00:54:14** Enabling · **00:54:15** Done · **00:54:26** TrMenu 36 · **00:54:28** 配置已重载。

---

## 5. 禁项自检（EMPTY）

| 项 | 结果 |
|----|------|
| 四件甲 / 盔甲槽 | **未动** |
| 技能大改 / skills.yml | **未动** |
| 锻炉重做 / forge.yml | **零 diff** |
| B2.17 talent 文案 | **零 diff**（本窗未改 `ember_talent.yml`） |
| T0–T3 刃/护符 NI 数值 | **零 diff** |
| 体力门 / cost | **零 diff** |
| `over_chance*` / afk_caps 数值 | **零 diff**（仅 stats 下增 offhand） |
| wall-clock / DPS | **未做** |
| 宣称 B0.1 已清 | **无** |
| runtime/player/world dirty | **未入库** |
| NI YAML 本窗新建/改曲线 | **无**（物品 tip `52f817a`） |

---

## 6. 回传摘要（母代理）

1. tip：见本 commit 短 hash · push origin/main  
2. CoreRpg **1.15.24** Enabling 已确认；jar 同 inode  
3. `rg offhand`：StatService 钩子 + 双路径 config + hub/set 副手文案  
4. 禁项：**EMPTY**  
5. NI：本窗未写；物品 `52f817a` 已核对 id/lore（ward 物防+2 / vita 生命+8）
