# STATUS · 非日常进本体力灰显 A

> 一句话：**深渊字面量 `<30` 灰显；周本/团本/枢纽精英薄 PAPI `stamina_blocked_*` + 灰显；CoreRpg 1.15.23；cost/扣费/日常七线零动。**

| 字段 | 值 |
|------|-----|
| 稿件 | `docs/design-ember-enter-stamina-gray.md`（已批 A · tip `7046c8e` / 设计 `2c49e1b`） |
| 专岗 | 余烬-插件 · 2026-09-28 Asia/Shanghai |
| 版本 | CoreRpg **1.15.23**（自 1.15.22） |
| 热更 | play 短重启 Enabling **v1.15.23** @ **22:06:58 CST** · Done @ **22:06:59 CST** · `trmenu reload` 良好 32 菜单 @ **22:07:07 CST** |
| push | **未 push**（禁） |

---

## 1. PAPI 语义（只读）

落点：`CoreRpgExpansion` · `st.ensure(d)` 后：

| 占位 | 语义 | 返回 |
|------|------|------|
| `stamina_blocked_weekly` | 周免费 credit>0 → 不灰；否则 `stamina < costOf("weekly")` | `"1"` / `"0"` |
| `stamina_blocked_raid` | 同理 · kind `raid` | `"1"` / `"0"` |
| `stamina_blocked_elite` | 同理 · kind `elite` | `"1"` / `"0"` |

**未改** `StaminaService.consumeForEnter`、cost 配置、日重置、周免费发放。

---

## 2. 四门 TrMenu 改动

| 菜单 | 键 | 灰显 condition | 灰态 |
|------|----|----------------|------|
| `ember_abyss.yml` | `S` | `check papi %corerpg_stamina% < 30` | gray pane · 无 enter · tell 人话 · `update/refresh: 20` |
| `ember_weekly.yml` | `S` | `check papi %corerpg_stamina_blocked_weekly% > 0` | 同上 · 可补「本周免费已用完」 |
| `ember_raid.yml` | `S` | `check papi %corerpg_stamina_blocked_raid% > 0` | 同上 |
| `ember_hub.yml` | `'2'` | `check papi %corerpg_stamina_blocked_elite% > 0` | 同上 · 无 `elite start` |

默认绿枝保留现网 display/actions（含 enter / elite start）。**禁双侧 PAPI**（右侧均为字面量）。hub **只动**精英键 `'2'`，日常七线未触。

---

## 3. 构建 / 部署

```
export JAVA_HOME=/workspace/minecraft/tools/jdk8u504-b01
export PATH="$JAVA_HOME/bin:/workspace/minecraft/tools/apache-maven-3.9.16/bin:$PATH"
cd /workspace/minecraft/CoreRpg && mvn -o -q -DskipTests package
cp -f target/CoreRpg.jar /workspace/minecraft/plugins/CoreRpg.jar
```

- 离线初缺传递依赖 → pom 按仓库既有加 `papermc` + `minecraft-libraries` repos 后 `-o` 通过。
- jar 与 server-runtime 同 inode；**未** git add jar。
- FIFO 短重启（`start.sh` 默认 stdin=/dev/null，本窗用 console.in + keeper）。

---

## 4. 禁项自检

| 项 | 结果 |
|----|------|
| `consumeForEnter` / 扣费逻辑 | **零 diff**（`StaminaService.java` 未改） |
| cost / max / 日重置 / 周发放 | **零 diff**（仅只读 `costOf`） |
| `over_chance*` | **零 diff** |
| 日常七线 `ember_daily.yml` | **零 diff** |
| 双侧 PAPI | **无**（右侧字面量 30 / `> 0`） |
| 灰态 enter / elite start | **无** |
| 玩家面新增斜杠教学 / 裸 NI id | **无** |
| git push | **未执行** |

---

## 5. 测岗验收命令（抽样）

```bash
# 版本
rg "Enabling CoreRpg v1.15.23" /workspace/minecraft/server-runtime/logs/latest.log | tail -1

# PAPI（进服后）
# /papi parse me %corerpg_stamina_blocked_weekly%
# /papi parse me %corerpg_stamina_blocked_raid%
# /papi parse me %corerpg_stamina_blocked_elite%

# 菜单条件（仓库静态）
rg -n "condition:" plugins/TrMenu/menus/ember_{abyss,weekly,raid}.yml plugins/TrMenu/menus/ember_hub.yml | rg "stamina"

# 场景
# 1) 深渊 stamina<30 → S 灰 pane，点之 tell、无 enter；≥30 绿+enter
# 2) 周本：credit≥1 且 stamina 低 → 仍可点；credit=0 且 stamina<45 → 灰
# 3) 团本 / 枢纽精英同理（cost 50 / 40）
# 4) git diff StaminaService / ember_daily / over_chance → 空
```

---

## 6. 回传摘要（主代理）

1. tip：见本 commit 短 hash  
2. CoreRpg **1.15.23** Enabling 已确认；jar 同 inode  
3. 四门 condition：深渊 `< 30`；周本/团本/精英 `blocked_* > 0`  
4. 禁项：consumeForEnter / cost / over_chance / 日常 → **零 diff**  
5. 阻塞：无（FIFO 热更已补；`start.sh` 默认无 stdin，后续短重启需 console.in keeper）
