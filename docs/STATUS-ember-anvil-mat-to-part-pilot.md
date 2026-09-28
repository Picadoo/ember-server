# STATUS · B-anvil-1 烬砧材料→部件短链试点 A（插件施工）

> 一句话：**`part.yml` 灰箍配方 + `PartService` + `/corerpg part craft` + TrMenu 炼部件 + `stats.offhand` 追加；CoreRpg 1.15.26；NI 由物品岗 `57a6540`，本窗未写 NI。**

| 字段 | 值 |
|------|-----|
| 稿件 | 设计 tip `ab33c21` · 批准 tip `f10779b` · `docs/design-ember-anvil-mat-to-part-pilot.md` |
| 专岗 | 余烬-插件 · 2026-09-29 Asia/Shanghai |
| 版本 | CoreRpg **1.15.26**（自 1.15.25） |
| 热更 | play 短重启 Enabling **v1.15.26** @ **01:28:12 CST** · enabled @ **01:28:13** · Done @ **01:28:14 CST** · TrMenu 38 菜单 @ **01:28:14/15** · `corerpg reload` Part:1 @ **01:28:16 CST** |
| NI | **本窗未写**；物品 tip `57a6540`（`ember-gear-parts.yml` · `part_ember_ash_brace`） |
| push | tip 见交付 commit → origin/main |

---

## 1. 对批

| 批准项 | 落地 |
|--------|------|
| 薄配方表 `part.yml` | 双路径：`plugins/CoreRpg/part.yml` + `CoreRpg/src/main/resources/part.yml` |
| 配方 | `mat_ember_shard`×12 + `ingot_ember_iron`×2 → `part_ember_ash_brace`×1；**无币无体力** |
| `forge.yml` 升阶 4 条 | **ZERO diff**（git diff 空） |
| 命令 | `/corerpg part craft ash_brace`（TrMenu 调）；材料不足人话 tell、不扣物 |
| TrMenu | `ember_forge` 增 P「炼部件」→ 薄页 `ember_part.yml`；短 lore；**不教**手打 `/corerpg` |
| `stats.offhand` | 双路径 config **追加** `part_ember_ash_brace`；未改 ward/vita；**未进** accessories |
| 禁 B1 骨饰 | **EMPTY**（无 `part_ember_bone_charm`） |
| 本窗不写 NI | **EMPTY**（接物品 tip `57a6540`） |

---

## 2. 钩子 / YAML / 菜单

### PartService（新建）

- `CoreRpg/.../PartService.java`：读 `part.yml`；`craft` 先检材料→不足 tell 列表不扣→足则 consumeExact + giveNiItem
- `CoreRpgPlugin`：字段/init/reload/`part`|`部件` 子命令；enabled 日志含 `part`

### part.yml（双路径）

```yaml
enabled: true
recipes:
  ash_brace:
    output: part_ember_ash_brace
    amount: 1
    name: 余烬灰箍
    cost: { mat_ember_shard: 12, ingot_ember_iron: 2 }
```

### Config（双路径 offhand 追加）

```yaml
stats:
  offhand:
  - acc_ember_offhand_ward
  - acc_ember_offhand_vita
  - part_ember_ash_brace
```

- **未改** ward/vita 数值、`afk_caps` / `over_chance*`、体力门

### TrMenu

| 菜单 | 改动 |
|------|------|
| `ember_forge.yml` | Layout `# E P S #`；图标 P「§a炼部件」→ `menu: ember_part`；升阶 A/B/C **未改动作** |
| `ember_part.yml` | **新建**：灰箍预览 +「确认炼制」→ `command: corerpg part craft ash_brace`；返回锻炉 |

### 版本

- `plugin.yml` / `pom.xml` → **1.15.26**

---

## 3. rg 证据（抽样）

```bash
rg -n "part_ember_ash_brace" plugins/NeigeItems/Items/ember-gear-parts.yml
rg -n "part_ember_ash_brace" plugins/CoreRpg/config.yml CoreRpg/src/main/resources/config.yml
rg -n "ash_brace|mat_ember_shard|ingot_ember_iron" plugins/CoreRpg/part.yml CoreRpg/src/main/resources/part.yml
rg -n "PartService|part craft" CoreRpg/src/main/java/town/sunshine/corerpg/PartService.java CoreRpg/src/main/java/town/sunshine/corerpg/CoreRpgPlugin.java
rg -n "炼部件|ember_part|ash_brace" plugins/TrMenu/menus/ember_forge.yml plugins/TrMenu/menus/ember_part.yml
git diff -- plugins/CoreRpg/forge.yml   # EMPTY
rg -n "part_ember_bone_charm" plugins/ CoreRpg/   # EMPTY
rg "Enabling CoreRpg v1.15.26" server-runtime/logs/latest.log | tail -1
```

预期：NI 物品岗命中；双路径 offhand+part.yml；命令+菜单；forge ZERO；骨饰 EMPTY；Enabling v1.15.26。

---

## 4. 构建 / 热更

```
export JAVA_HOME=/workspace/minecraft/tools/jdk8u504-b01
export PATH="$JAVA_HOME/bin:/workspace/minecraft/tools/apache-maven-3.9.16/bin:$PATH"
cd CoreRpg && mvn -o -q -DskipTests package
cp -f target/CoreRpg.jar ../plugins/CoreRpg.jar   # 与 server-runtime 同 inode（plugins 软链）
# FIFO 短重启（console.in + keeper）→ Enabling v1.15.26
printf 'trmenu reload\n' > server-runtime/console.in
printf 'corerpg reload\n' > server-runtime/console.in
```

- jar **未** git add（`*.jar` ignore）。
- 热更 CST：**01:28:12** Enabling · **01:28:13** enabled · **01:28:14** Done · TrMenu **38** · **01:28:16** Part:1 reload。

---

## 5. 禁项自检（EMPTY）

| 项 | 结果 |
|----|------|
| `forge.yml` 升阶 4 条 | **ZERO**（diff 空字节） |
| `enhance.yml` | **零 diff** |
| 四件甲 / 多部位锻炉大改 | **未动** |
| 誓约主动 / 体力 / afk / over_chance | **零 diff**（config 仅 offhand +1 行） |
| B-flex-1 守腕/生坠数值 | **未动**（仅白名单追加灰箍 id） |
| B-flex-2 踏步 | **未动** |
| B1 `part_ember_bone_charm` | **EMPTY** |
| 本窗写 NI YAML | **无**（物品 `57a6540`） |
| 宣称 B0.1 已清 | **无** |
| wall-clock / DPS | **未做** |
| runtime/player/world dirty | **未入库** |

---

## 6. 菜单路径说明

1. 烬砧 NPC / `/ember` → 锻炉 `ember_forge`
2. 点 **「炼部件」**（flint）→ `ember_part`
3. 点 **「确认炼制」** → 材料够：灰箍入包；不足：人话提示、不扣物
4. 灰箍放 **副手槽** 生效（`stats.offhand`）

---

## 7. 回传摘要（母代理）

1. STATUS：`docs/STATUS-ember-anvil-mat-to-part-pilot.md`
2. tip：见本窗 commit · push origin/main ✅
3. CoreRpg **1.15.26** Enabling **01:28:12 CST**；TrMenu 38；Part:1
4. NI 已存在：物品 tip `57a6540` · `ember-gear-parts.yml`
5. forge.yml 升阶 **ZERO**；禁项 **EMPTY**
6. 菜单：`plugins/TrMenu/menus/ember_forge.yml` + `ember_part.yml`
7. 下一棒：测岗静态 rg + 菜单轻测（本窗未做进服菜单轻测）
