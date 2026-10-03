# STATUS · S0 体力药使用 · 独立正式短验收（测试岗）

- **时间：** 2026-09-28 04:23（Asia/Shanghai）
- **执行：** 余烬-测试岗执行器
- **依据：** `docs/status/STATUS-ember-stamina-potion-use.md`；设计 §A.5；总控五项硬条
- **CoreRpg：** **1.15.16**（jar `plugins/CoreRpg.jar` + play 日志 `CoreRpg 1.15.16 enabled` @ 04:21:46 CST）
- **Verdict：** **✅ PASS**（五项全绿）
- **未改：** jar / YAML / 数值（`potion_daily_cap=90`、日回/进本费用未动）
- **脚本：** `mineflayer-tests/s0-potion-use-test.js`（独立重写，未照抄插件岗 smoke 结论）
- **JSON：** `/tmp/s0-potion-use-test.json`

---

## 环境

| 项 | 值 |
|----|-----|
| 路径 | `/workspace/minecraft` |
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |
| 端口 | proxy 25565 / login 25566 / play 25567 |
| 登录 | `mineflayer-tests/lib/proxy-login.js` |
| 临时 OP | `S0pOpA`（ops.json offline UUID + LP admin；测后 `/deop` + unset + `ops=[]`） |
| 饮用验收号 | `S0pU665` |
| 发放验收号 | `S0pG617` |
| 短重启 | 测前写入 ops 后仅重启 play（未删 dungeon-caches） |

---

## 分项 1–5

| # | 硬条 | 结果 | 证据要点 |
|---|------|------|----------|
| 1 | `/ni give … consumable_ember_stamina_30` → 手持饮用 → 体力 **+30**、瓶减、`potionStaminaToday` 涨 | ✅ **PASS** | `S0pU665`：设 20 → 饮用后 **50/90**；药剂今日 **0→30**；文案「回复 +30（当前 50/90 · 药剂今日 30/90）」；瓶 1→0 |
| 2 | `_45` 同法 **+45**；与 `_30` **同池**日顶 90 | ✅ **PASS** | 同号设体力 20（药剂仍 30）→ 饮 `_45`：**20→65**；药剂 **30→75**；瓶减尽；同池累计 75/90 |
| 3 | 造超顶（药剂今日+本瓶>90）：拒服且 **不扣瓶** | ✅ **PASS** | 药剂 75 时再给 `_30` 饮用 →「今日药剂回体已达上限 90」；体力/药剂仍 **65 / 75**；瓶 **before=1 after=1** |
| 4 | 商城/发放给瓶后，**未饮用**时 `potionStaminaToday` **不预涨** | ✅ **PASS** | `S0pG617`：`shop buy daily_ticket` → 得 NI×1、「获得体力药 ×1」；药剂今日 **0→0**；另 `/ni give …_30` 对照亦 **0→0** 且瓶在 |
| 5 | **禁显示名匹配**（只认 NI id）；测后 **ops=[]**；CoreRpg 1.15.16 | ✅ **PASS** | 代码：`onPotionConsume` 仅 `ni.getNiId`；NI yml 注明禁显示名；测试侧一律 `ni give <id>`；jar+日志 **1.15.16**；`server-runtime/ops.json=[]` · `login-runtime/ops.json=[]` |

---

## 证据摘要

### 饮用 `_30` / `_45` / 超顶（`S0pU665`）

```text
基线：[体力] 20/90 · 药剂今日 0/90
_30：  [体力] 回复 +30（当前 50/90 · 药剂今日 30/90） · 瓶 0
_45：  体力 20→65 · 药剂今日 30→75 · 瓶 0
超顶： [体力] 今日药剂回体已达上限 90 · 仍 65/90 · 药剂 75/90 · 瓶保留 1
```

### 发放不预涨（`S0pG617`）

```text
购前：[体力] 20/90 · 药剂今日 0/90
商城： [体力] 获得体力药 ×1（使用后 +30/瓶）
       [商城] 已购买体力药 ×1（-60 晶钻）
购后：[体力] 20/90 · 药剂今日 0/90 · 背包余烬体力药·小 ×1
ni give 对照：药剂今日仍 0/90
```

### CoreRpg / ops

- jar `version: 1.15.16`
- play：`[04:21:46] CoreRpg 1.15.16 enabled … stamina/…`
- 测后：`ops.json` play+login = **`[]`**；`S0pOpA` 已 deop + LP admin 卸除

---

## 债 / 未覆盖

- **未**做「改名原版药冒充」反向实锤（硬条允许文档/代码旁证 + 测试侧只认 ni id；代码路径已确认无显示名分支）。
- **未**改 jar/YAML/数值；**未** git commit；**未**开 S1。
- 插件岗早期 use-smoke 在修复前曾出现商城「今日药剂回体已达上限」拒购；本轮独立号 `S0pG617` 购药成功且不预涨，与 1.15.16 CashService 放行一致。

---

## 回报主代理 / 总控

- **总评：** **PASS**
- **分项：** 1 PASS · 2 PASS · 3 PASS · 4 PASS · 5 PASS
- **报告：** `docs/status/STATUS-ember-stamina-potion-use-test.md`
- **JSON：** `/tmp/s0-potion-use-test.json`
- **CoreRpg：** 1.15.16 · **ops：** `[]`
