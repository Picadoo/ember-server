# 状态 · D386 薄抽：生活页副手守腕/生坠回 Layout（静态）

**日期：** 2026-10-10（上海时间）  
**号：** D386Spot · 证据 `/workspace/tmp/d386-life-offhand/`  
**上游施工：** tip [`ce64fbdd`](https://github.com/Picadoo/ember-server/commit/ce64fbdd) · STATUS [`STATUS-ember-life-offhand-layout-d386-2026-10-10.md`](STATUS-ember-life-offhand-layout-d386-2026-10-10.md) · DESIGN [`DESIGN-ember-life-offhand-layout-2026-10-10.md`](../design/DESIGN-ember-life-offhand-layout-2026-10-10.md)  
**范围：** 只读静态 `rg` + Layout/Icons 结构核验 · **≠关观察** · **≠开 K3** · **未改** 配置 / 开关 / life 价表 / weekly · **未切分支** · **未回盘 E/F** · play 本号未活扫点购（静态已覆盖 V1–V6）  
**总结果：** **PASS**

## 人话

补给·生活底栏现见守腕 O / 生坠 W，点购走既有 `corerpg life buy offhand_ward|offhand_vita`；套装副手格一点进生活。价表仍 80 币·每周 1；重铸/稳固 E/F（及旧票 V）仍只在 Icons、不进 Layout。三开关 / bv62 / jar 未拧。

## 验收表

| ID | 要点 | 结果 | 证据 |
|----|------|------|------|
| V1 | `ember_life` Layout 含 **O/W**；保留 A–D / G/K / H/J / I/P/Z；**无** E/F/V | **PASS** | `structure-check.txt` · `static-scan.txt` |
| V2 | O→`corerpg life buy offhand_ward`；W→`offhand_vita` | **PASS** | life Icons O/W |
| V3 | `ember_set` 副手格 O → `menu: ember_life`；lore「打开补给·生活」 | **PASS** | set O block |
| V4 | tip **未动** `life.yml`；live 价仍 coin **80** / weekly **1** | **PASS** | tip --stat · `md5.txt` · life.yml |
| V5 | E/F（及 V）Icons 仍在、**Layout 仍收**（stowed 注释 D386+） | **PASS** | Layout keys `ABCDGHIJKOPWZ` |
| V6 | Open 半行「副手：本页守腕/生坠周购 · 不进主线战力」；无教裸 `/corerpg life buy` | **PASS** | Events.Open |
| Sw | six_slot enabled/migrate/set_bonus=true · bv62 · jar `1.65.102-d384.local` | **PASS** | `90-endstate.json` · `layout-switches.txt` |

## 关键证据摘要

- tip `ce64fbdd`（`ce64fbdd232c070329d6528d0a5d2e979d37244d`）· 分支 `main` · 扫时 HEAD `61cce6c7`（其上为 D384+D385 spot docs）
- tip 六文件：两菜单 + DESIGN/STATUS/tip旁注/backlog · **零** life.yml / ember-v1 / jar
- md5：见 `md5.txt`（life.yml / ember-v1.yml / runs 与 tip 前一致口径）
- 施工侧已于 **04:00:04 CST** `trmenu reload` → 70 菜单；本号未再 reload / 未 `/corerpg reload`
- 本号未切分支 / 未 stash / 未 reset / 未改 live yml / 未关观察

## 旁注（非本号主交付）

D385 tip `1e9a57b4` 使魔菜单已挂 `%corerpg_pet_level_line%` / `feed_hint` / `active_name`（I/S/E）——本地静态可见；活测另号并行，**不**并入本号 PASS 条件。见 `d385-旁注.txt`。

## 不动确认

配置 · life.yml 价/weekly · E/F/V 回盘 · ×0.97 · set_bonus/enabled/migrate · bv · jar · K3 · 关观察 · 切分支

## 总控旁注

**≠关观察**。生活副手回盘薄抽结案（静态 PASS）；活扫点购可另窗币够时补，非本号阻塞。

---

*D386Spot · V1–V6 PASS · tip `ce64fbdd` · 证据 `/workspace/tmp/d386-life-offhand/` · ≠关观察。*
