# STATUS · B-flex-2 可装配轻技 1 槽试点 A（插件施工）

> 一句话：**`PlayerData.flex_skill_id` 1 槽 + `flex_ember_step`「余烬踏步」+ `FlexSkillService` + TrMenu 装配/卸下/释放；CoreRpg 1.15.25；本窗无 NI。**

| 字段 | 值 |
|------|-----|
| 稿件 | 设计 tip `d1e88e4` · 批准 tip `7d7bf5a` · `docs/design-ember-flex-skill-slot-pilot.md` |
| 专岗 | 余烬-插件 · 2026-09-29 Asia/Shanghai |
| 版本 | CoreRpg **1.15.25**（自 1.15.24） |
| 热更 | play 短重启 Enabling **v1.15.25** @ **01:09:40 CST** · enabled @ **01:09:41** · Done @ **01:09:42 CST** · `trmenu reload` 37 菜单 @ **01:09:55 CST** |
| NI | **本窗未写**（批准：非必需） |
| push | 总控要求完工推 tip → 见交付 commit |

---

## 1. 对批

| 批准项 | 落地 |
|--------|------|
| 1 槽 `flex_skill_id`（空=`none`） | `PlayerData` 字段 + getter/setter；`PlayerDataStore` 读写键 `flex_skill_id` |
| 试点技 `flex_ember_step`「§a余烬踏步」 | 双路径 `skills.yml`：`type: step` · distance 5 · CD 14s · 无伤害 · 零体力 |
| 位移不穿墙 | `FlexSkillService.castStep`：水平看向步进 0.25，遇实体方块停；失败原地+「前方受阻，无法踏步」 |
| 与誓约主动并行 | 誓约仍 `SkillService.cast` / hub「技能」；轻技仅槽非空可放 |
| 三誓约技数值零 diff | `ember_blaze_slash` / `ember_ash_familiar` / `ember_warden_taunt` 块 **ZERO** |
| TrMenu | 新页 `ember_flex_skill.yml`：装配/卸下/释放；hub 增 `m`「轻技」入口；原 E「技能」仍放誓约 |
| 命令钩子 | `/corerpg flex equip\|unequip\|cast\|info`（菜单调；玩家 lore **不教**手打） |
| 全员可装配 | 无 Lv 门（优先全员） |
| 本窗无 NI | **EMPTY** |

---

## 2. 钩子 / YAML / 菜单

### FlexSkillService（新建）

- `CoreRpg/.../FlexSkillService.java`：从 `skills.yml` 加载 `type: step|dash|flex` 或 `flex_*`；CD 内存 Map；`castStep` 水平位移
- `CoreRpgPlugin`：字段/init/reload/quit/`flex` 子命令；PAPI `%corerpg_flex_skill%` / `%corerpg_flex_skill_name%`

### skills.yml（双路径）

- `plugins/CoreRpg/skills.yml` + `CoreRpg/src/main/resources/skills.yml`
- **仅追加** `flex_ember_step`；三誓约块零 diff

### TrMenu

| 菜单 | 改动 |
|------|------|
| `ember_flex_skill.yml` | **新建**：装配余烬踏步 / 卸下 / 释放 / 说明；无「请输入 /corerpg」lore |
| `ember_hub.yml` | Layout 增 `m`；图标「轻技」→ `menu: ember_flex_skill`；潜行→释放；**E 技能未改** |

### 版本

- `plugin.yml` / `pom.xml` → **1.15.25**

---

## 3. rg 证据（抽样）

```bash
rg -n "flex_skill_id|getFlexSkillId" CoreRpg/src/main/java/town/sunshine/corerpg/PlayerData.java CoreRpg/src/main/java/town/sunshine/corerpg/PlayerDataStore.java
rg -n "flex_ember_step" plugins/CoreRpg/skills.yml CoreRpg/src/main/resources/skills.yml
rg -n "castStep|FlexSkillService" CoreRpg/src/main/java/town/sunshine/corerpg/FlexSkillService.java
rg -n "余烬踏步|ember_flex_skill" plugins/TrMenu/menus/ember_flex_skill.yml plugins/TrMenu/menus/ember_hub.yml
rg "Enabling CoreRpg v1.15.25" server-runtime/logs/latest.log | tail -1
```

预期：1 槽读写；双路径 flex 定义；踏步逻辑；菜单装配入口；日志 Enabling v1.15.25。

---

## 4. 构建 / 热更

```
export JAVA_HOME=/workspace/minecraft/tools/jdk8u504-b01
export PATH="$JAVA_HOME/bin:/workspace/minecraft/tools/apache-maven-3.9.16/bin:$PATH"
cd CoreRpg && mvn -o -q -DskipTests package
cp -f target/CoreRpg.jar ../plugins/CoreRpg.jar
# FIFO 短重启（console.in + keeper）→ Enabling v1.15.25
printf 'trmenu reload\n' > server-runtime/console.in
```

- jar **未** git add。
- 热更 CST：**01:09:40** Enabling · **01:09:42** Done · **01:09:55** TrMenu 37。

---

## 5. 禁项自检（EMPTY）

| 项 | 结果 |
|----|------|
| 誓约三主动 id/数值/CD | **ZERO**（块级比对） |
| 体力日周门 / cost / `afk_caps` / `over_chance*` | **零 diff**（config 未动） |
| 四件甲 | **未动** |
| 锻炉 / forge.yml | **零 diff** |
| T0–T3 刃护符 NI | **零 diff**（本窗无 NI） |
| talent.yml / passives / B-flex-1 副手 | **零 diff** |
| B2.x talent 文案 | **未动** |
| 保命 B1 双上 | **无** |
| 宣称 B0.1 已清 | **无** |
| wall-clock / DPS | **未做** |
| runtime/player/world dirty | **未入库** |

---

## 6. 菜单路径说明

1. `/ember` → 枢纽第 1 行 **「轻技」**（羽）→ `ember_flex_skill`
2. 点「装配 · 余烬踏步」→ 槽=`flex_ember_step`
3. 点「释放轻技」或 hub 轻技潜行 → 短位移 / CD 提示 / 未装配拒门
4. 枢纽原 **「技能」**（焰粉）仍 `corerpg skill` 释放誓约主动

---

## 7. 回传摘要（母代理）

1. STATUS：`docs/STATUS-ember-flex-skill-slot-pilot.md`
2. tip：见本 commit 短 hash · push origin/main
3. CoreRpg **1.15.25** Enabling **01:09:40 CST**；TrMenu 37 @ **01:09:55**
4. rg：flex 槽 + 踏步 + 双路径 skills + 菜单
5. 禁项：**EMPTY**
6. 下一棒：测岗静态 rg + 菜单轻测（本窗未做进服菜单轻测）
