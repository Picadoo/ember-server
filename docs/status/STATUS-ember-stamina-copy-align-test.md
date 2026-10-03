# STATUS · S0 菜单日票假文案对齐 A · 测试验收

- **时间：** 2026-09-28 22:53–22:55 CST（Asia/Shanghai）
- **执行：** 余烬-测试岗执行器
- **依据：** `docs/design/design-ember-stamina-copy-align.md`（批 A）· `docs/status/STATUS-ember-stamina-copy-align.md`
- **施工 tip：** `71473da` · TrMenu 热更（ConfigWatcher + `trmenu reload` → 36 菜单 @ 22:51:53）
- **Verdict：** **✅ PASS**（验收点 1–5 全绿 · ops=`[]` · **未**宣称主线 quest 日票台词已清）
- **JSON：** `/tmp/stamina-copy-align-A-test.json` · shop 补抽 `/tmp/stamina-copy-align-A-shop.json`

---

## 一句话

经枢纽菜单路径 live 抽战令 U/L、角色 S/$、寄售税 tell：玩家可见「日票 / 日周票」已清，改为体力药/体力口径，并与商城热销「累计体力药 ×5」同口径；`command:`/`menu:` 未断；禁项 progress/cash/quest/价/限购/体力 cost **git diff 空**；本窗不宣称主线灰烛日票台词已清。

---

## 验收点

| # | 点 | 结果 | 证据要点 |
|---|----|------|----------|
| 1 | 战令 U/L：可见「体力药 ×5」，**无**「日票」 | ✅ **PASS** | live `ScA_380`：U lore「外观、币、**体力药 ×5**（6/12/18/24/30 级）」；L lore「… / **体力药**」「另有**体力药×5**…」；页内 lore **无**日票；I tell「付费轨外观+券+**体力药**」 |
| 2 | 角色 S/$：挂机/体力、晶钻·体力药日限；无「日周票」「日票硬顶」 | ✅ **PASS** | S lore「挂机 / **体力** / 签到一览」；$ lore「晶钻余额 · **体力药日限**」；页内 **无**日周票/日票硬顶 |
| 3 | 寄售税 tell：票券/体力药；对照商城热销「累计体力药 ×5」同口径 | ✅ **PASS** | 点「税率说明」tell「绑定物、**票券 / 体力药**、邮件附件不可上架」（无日周票）；商城 `ScB_304`「战令解锁」lore「外观 + 币 + **累计体力药 ×5**（赛季内发）」 |
| 4 | 路径可用；禁项 diff 空 | ✅ **PASS** | hub→战令/角色/寄售/商城均打开；三菜单 `command:`/`menu:` 仍在；tip 相对父仅四 TrMenu + STATUS；`progress.yml`/`cash.yml`/`quest.yml`/体力 cost **EMPTY**；shop 仅头注释，价/限购未动 |
| 5 | **不**宣称主线 quest 日票台词已清 | ✅ **PASS** | `quest.yml` 仍含灰烛/hint「日票 / 每日 3 张免费日票」等（方案 B 另窗）；本报**不**宣称已清 |

**总评：** **PASS**

---

## 环境 / 动线

| 项 | 值 |
|----|-----|
| 工作区 | `/workspace/minecraft` |
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |
| 口 | proxy 25565 / login 25566 / play 25567 |
| UX 路径 | 枢纽 `/ember` → 战令 / 角色 / 寄售 / 商城（**禁教玩家斜杠**；测号仅用枢纽按钮） |
| 测号 | `ScA_380`（战令/角色/寄售）· `ScB_304`（商城热销补抽） |
| 临时 OP | **未用**（纯 UI 文案） |
| ops 终态 | play=`[]` · login=`[]` |
| Git 施工 tip | `71473da`（测报本 commit 另落；**未 push**） |

### Live lore / tell（节选）

```
[战令 U] 外观、币、体力药 ×5（6/12/18/24/30 级）
[战令 L] 赛季 Lv1～30：币 / 碎片 / 附魔晶 / 体力药
         付费轨（已解锁）另有体力药×5、保护券、孔石
[战令 I] 付费轨外观+券+体力药，无核心
[角色 S] 挂机 / 体力 / 签到一览
[角色 $] 晶钻余额 · 体力药日限
[寄售税] 绑定物、票券 / 体力药、邮件附件不可上架
[商城 B] 外观 + 币 + 累计体力药 ×5（赛季内发）
```

### 静态 / 禁项

- 三目标菜单 `rg`「日票|日周票」→ **空**（shop 仅 `#` 注释「原日票/周票」备忘，非玩家按钮）
- tip 文件：`ember_pass/character/auction/shop.yml` + 插件 STATUS；shop diff **仅**头注释一行
- 玩家 lore **无**新增 `/(corerpg|dp|trmenu)` 斜杠教学
- 热更：`[22:51:42]` 自动重载四菜单；`[22:51:53]` 36 个菜单已加载 (30 ms)

### 残余（明示不宣称）

- `plugins/CoreRpg/quest.yml` 主线灰烛/hint 仍写「日票」——属方案 B，**本窗未清、不宣称已清**

---

## 回报总控

| 项 | 值 |
|----|-----|
| **总评** | **PASS** |
| 点1 战令 U/L | PASS |
| 点2 角色 S/$ | PASS |
| 点3 寄售税 + 商城对照 | PASS |
| 点4 路径 + 禁项 diff 空 | PASS（diff **空**） |
| 点5 不宣称 quest 已清 | PASS（quest 日票仍挂） |
| 报告 | `docs/status/STATUS-ember-stamina-copy-align-test.md` |
| ops | `[]` |
| 阻塞点 | 无 |
| tip（测报 commit 后报） | 见本 STATUS 落盘后的 git tip；**未 push**，总控代推 |
