# STATUS · 日常菜单必改 2+3 · 短抽检

- **时间：** 2026-09-28 05:20（Asia/Shanghai）
- **执行：** 余烬-测试岗执行器
- **依据：** `docs/status/STATUS-ember-daily-menu-mustfix.md`（必改 2 hub 文案 · 必改 3 体力灰显 · 顺手奖励预览）
- **Verdict：** **❌ FAIL**（必改 3 live 灰显未生效）
- **未改：** YAML / jar / 体力数值 / 平衡；仅临时 OP、`/trmenu reload`、管理 `stamina set`、点按抽检
- **未 commit/push**
- **JSON：** `/tmp/daily-menu-mustfix-test.json` · 探针 `/tmp/papi-stamina-probe.json`

---

## 环境

| 项 | 值 |
|----|-----|
| 路径 | `/workspace/minecraft` |
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |
| 端口 | 25565 proxy / 25566 login / 25567 play |
| CoreRpg | 1.15.18 |
| TrMenu | 3.12.5 · 启动载入 31 菜单 |
| 菜单文件 | `plugins/TrMenu/menus/ember_hub.yml` · `ember_daily.yml`（runtime symlink 同源） |
| 登录 | `mineflayer-tests/lib/proxy-login.js` |
| 临时 OP | 多轮短唯一号（`DmMf*` / `PapiProbeOp`）；测后 deop + `ops=[]` |
| 五线硬条 | 庭院 / 焦骨 / 地窖 / 潮蚀 / 断塔（键 `S/A/C/D/E`） |

---

## 三项总表

| # | 必验 | 结果 | 证据要点 |
|---|------|------|----------|
| 1 | hub「日常 · 余烬窟」lore：无「日限次数」；有体力语义 + 多线（庭院…断塔） | ✅ **PASS** | 静态 H 块 + live `/ember` 图标 lore 一致 |
| 2 | `ember_daily`：体力≥30 五钮可进；&lt;30 灰显 +「0:00 回满」且点击不执行 enter | ❌ **FAIL** | ≥30 进本 OK；&lt;30 **YAML 有灰态但 live 不切换**，点击仍走默认 `corerpg enter`（底层拒进） |
| 3 | 奖励预览 P：通关箱材料 + 各线 Boss 概率刃/符/T1（非空 tell） | ✅ **PASS** | lore 真预览，无空 tell |
| — | 测后 ops=[]（play+login） | ✅ **PASS** | 两文件均为 `[]` |

---

## 1 · hub「日常 · 余烬窟」→ ✅ PASS

**文件：** `plugins/TrMenu/menus/ember_hub.yml` 图标 `H`

| 检查 | 结果 | 证据 |
|------|------|------|
| 无「日限次数」 | ✅ | 静态 + live lore 均无该词 |
| 体力语义 | ✅ | `消耗 30 体力闯余烬窟 · 多线可选`（`%corerpg_stamina_cost_daily%`） |
| 多线五名 | ✅ | `庭院 · 焦骨 · 地窖 · 潮蚀 · 断塔` |
| 当前体力 | ✅ | `当前体力 90/90`（PAPI） |
| 无 slash 教学 | ✅ | lore 无 `/dp` `/corerpg` 教学 |

Live 样例（玩家 `/ember`）：

```
日常 · 余烬窟
消耗 30 体力闯余烬窟 · 多线可选
庭院 · 焦骨 · 地窖 · 潮蚀 · 断塔
当前体力 90/90
➥ 打开日常副本菜单
```

---

## 2 · ember_daily 体力门 → ❌ FAIL（灰显未生效）

### 2a 静态 YAML（写了，结构在）

五键 `S/A/C/D/E` 均具备：

- `icons[]` 子图标：`condition: 'check papi *%corerpg_stamina% < *%corerpg_stamina_cost_daily%'`
- 材质 `gray stained glass pane` · lore「体力不足 · 每日 0:00 回满」
- 灰态 actions **仅** sound + tell，**无** `corerpg enter`
- 默认态：`update: 20` + `command: corerpg enter daily|daily_ash|…`

（F/G 霜晶/锈轨亦有同类灰态模板；本单硬条只验五线。）

### 2b 体力 ≥30 → ✅ PASS

| 项 | 结果 | 证据 |
|----|------|------|
| 管理补满 90 | ✅ | `[体力] 90/90` |
| 顶栏 | ✅ | `余烬体力 90/90` |
| 五钮非灰 | ✅ | 庭院 iron_sword / 焦骨 netherrack / 地窖 mossy_cobblestone / 潮蚀 prismarine / 断塔 cobblestone；名称无「体力不足」 |
| 点庭院进本 | ✅ | `日 正在进入 余烬窟·庭院 …` → `[日常] 正在进入……（体力 -30）` → `成功创建了新的队伍` → `余烬窟·庭院 开始！` |

### 2c 体力 &lt;30 → ❌ FAIL

| 项 | 期望 | 实况 | 结果 |
|----|------|------|------|
| 管理压到 10 | show=`10/90` | ✅ show 成功 | — |
| 顶栏反映 &lt;30 | 灰显前置 | 探针：先 set10 再开菜单 → 顶栏 **`余烬体力 10/90`** | 顶栏可对 |
| 五钮灰显 | gray pane +「体力不足」+「0:00 回满」 | 仍为正常材质/名称（庭院 iron_sword 等），**无**「体力不足」 | ❌ |
| 点击不执行 enter | 仅短提示 | 仍触发默认 tell「正在进入…」并执行 `corerpg enter`；CoreRpg 回 `体力不足（需 30，当前 10/90）· 明日 0 点恢复`（底层拦下，**未真正进本**） | ❌ 菜单侧未按灰态路径 |

**探针结论**（`/tmp/papi-stamina-probe.json`）：在顶栏已正确显示 `10/90` 的同一菜单窗口内，庭院/焦骨仍为正常子图标 → **条件表达式未选出灰态子图标**（非「没 set 到体力」）。

疑点（供插件岗，测试岗不改配置）：

1. Kether `check papi *%corerpg_stamina% < *%corerpg_stamina_cost_daily%` 在 TrMenu 3.12.5 是否支持双侧 PAPI 数值比较；
2. 仅有 `update: 20`、未见 `refresh:`（文档：update 刷占位，refresh 才重算条件子图标）——但即便如此，**开菜单瞬间**亦应重算，当前开窗仍不灰，更像 condition 本身未成立。

---

## 3 · 奖励预览 P → ✅ PASS

**文件：** `ember_daily.yml` 图标 `P`（无空 tell actions）

Live lore：

```
奖励预览
通关箱材料（各线相同）：
  核心碎片 ×1 · 附魔晶 ×1 · 碎片 ×5
Boss 掉落（各线对应材料）：
  Boss 核心 ×1 · 碎片 / 骨尘
  各线均有概率获得：刃 / 符 / T1 装备
```

---

## ops 终态

- `server-runtime/ops.json` = **`[]`**
- `login-runtime/ops.json` = **`[]`**
- 临时 OP 均 `/deop` + LP `corerpg.admin` unset（能执行时）

---

## 债 / 阻塞 / 范围外

| 项 | 说明 |
|----|------|
| **阻塞：必改 3 live 灰显** | YAML 已写，游戏内不切换；进本命令仍被点击触发（靠 CoreRpg 拒进）。需插件岗修条件/refresh，**测试岗禁改 YAML** |
| RCON | 抽检中段 `enable-rcon` 曾被他岗关掉；改走 ops.json + 短重启 play |
| 他岗并行 | 测中 play 曾被关过一次（`Server closed`）；本岗亦为加载 OP 短重启数次 |
| F/G 灰显 | YAML 有模板；硬条未要求 live 验霜晶/锈轨 |

---

## 回报主代理 / 总控

- **总评：** **FAIL**
- **三项：** ① hub lore **PASS** · ② 体力门（灰显）**FAIL** · ③ 奖励预览 P **PASS**
- **报告：** `docs/status/STATUS-ember-daily-menu-mustfix-test.md`
- **JSON：** `/tmp/daily-menu-mustfix-test.json`
- **ops：** play=`[]` · login=`[]`
- **阻塞点：** 体力&lt;30 时子图标条件不生效；点击仍走 `corerpg enter`（底层拒绝）。请插件岗复查 TrMenu 条件语法 / 是否需 `refresh`
