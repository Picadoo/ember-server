# 设计稿 · 消 kill-any 剩债（深渊 F2/F7 · 团本 wave2 · 公会Boss wave1）

> 债源 / 派工：总控【消 kill-any 剩债薄设计】  
> 对照：精英厅二双 `$kill` AND（`EmberEliteWeekly` wave2 · `design-ember-elite-wave-variance.md` 已结）；日常七线 / 周本中核 / 精英已消 kill-any。  
> **已批准（总控 2026-09-28）：** 方案 A · 同波双 `$kill` AND、不链式、不拆 wave；仅改 EmberAbyss / EmberRaid / EmberGuildBoss 三份 `monster.yml` × plugins+server-runtime；方案 B 否决。

---

## 0. 稿件信息

| 字段 | 填写 |
|------|------|
| 稿件 | 消 kill-any 剩债（三处实杀 → 独立 `$kill` / 双 `$kill` AND） |
| 日期 | 2026-09-28（Asia/Shanghai） |
| 当前状态 | **已批准 · 方案 A（总控 2026-09-28）** |
| 施工面 | **仅**对应三份 `monster.yml`（plugins + server-runtime 同步） |

### 硬禁

| 禁止 | 说明 |
|------|------|
| 改数值 / 门坐标 / MM Health | 刷点、amount、location、delay、end 脚本一律不动 |
| 碰日常七线 / 周本 / 精英已结本 | `EmberDaily*` / `EmberWeekly` / `EmberEliteWeekly` 零碰 |
| 新建 `$kill-any` | 本轮三处消掉后全服有效条件行不得再引入 |
| 改 option / obstacle / task / MM 定义 | 只动 condition 行 |
| 模板 `AAA,BBB,CCC` / 注释禁杀字样 | **不计债**；勿当 live 误改 |

---

## 1. 调研确认（2026-09-28）

`rg '$kill-any{'` 扫 `plugins/DungeonPlus/dungeon/**/monster.yml`：有效实杀仅下表三处（另有小写空壳模板 `AAA,BBB,CCC` 与日常注释「禁 $kill-any」不计）。

| # | 副本 / 组 | live 原文 | Display（去色） | 刷怪 | amount 对齐 | plugins ↔ server-runtime |
|---|-----------|-----------|-----------------|------|-------------|--------------------------|
| 1a | `EmberAbyss` `floor2` | `$kill-any{mobname=余烬深渊·混潮,余烬深渊·骨潮;amount=4}` | 混潮 / 骨潮 | Mix×2 + Skeleton×2 | 2+2=4 ✅ | **identical** |
| 1b | `EmberAbyss` `floor7` | 同上 | 混潮 / 骨潮 | Mix×2 + Skeleton×2 | 2+2=4 ✅ | **identical** |
| 2 | `EmberRaid` `wave2` | `$kill-any{mobname=团本·通道卫兵,团本·通道射手;amount=7}` | 通道卫兵 / 通道射手 | Footman×3 + Archer×4 | 3+4=7 ✅ | **identical** |
| 3 | `EmberGuildBoss` `wave1` | `$kill-any{mobname=余烬深渊·潮尸,余烬深渊·骨潮;amount=8}` | 潮尸 / 骨潮 | Zombie×5 + Skeleton×3 | 5+3=8 ✅ | **identical** |

**可复制范例（精英厅二 · 同波双 `$kill` AND、不链式）：**

```yaml
# plugins/DungeonPlus/dungeon/EmberEliteWeekly/monster.yml · wave2
condition:
- "$kill{mobname=余烬试炼·蛮纹;amount=1} @system"
- "$kill{mobname=余烬试炼·混纹;amount=2} @system"
```

同组两条 `$kill` = AND（须两类都清完才过条件）；monster 刷点仍同组同刷，无 `$monstergroup` 链式。

---

## 2. 方案

| 方案 | 一句 | 本轮 |
|------|------|------|
| **A. 同波双 `$kill` AND** | 各处把单条 `$kill-any` 换成两条独立 `$kill`（按 Display × 本组刷数）；**不拆 wave、不链式**；零碰 option/HP/刷点/人数/他线 | **推荐**（总控预倾向；对齐精英 wave2） |
| B. 拆组链式 | 拆成 `waveXa`/`waveXb`（或 floor2a/2b），start→delay 链式再串下一层 | **不推荐**（改动面大、抬时序体感；本债只需纪律对齐） |

**方案 A 目标文案（仅 condition，其余行原样）：**

| 组 | 改后 condition（两条 AND） |
|----|---------------------------|
| Abyss floor2 / floor7 | `$kill{mobname=余烬深渊·混潮;amount=2}` **与** `$kill{mobname=余烬深渊·骨潮;amount=2}` |
| Raid wave2 | `$kill{mobname=团本·通道卫兵;amount=3}` **与** `$kill{mobname=团本·通道射手;amount=4}` |
| GuildBoss wave1 | `$kill{mobname=余烬深渊·潮尸;amount=5}` **与** `$kill{mobname=余烬深渊·骨潮;amount=3}` |

---

## 3. §验收（4 条）

1. **YAML 有效行**：上述三文件实杀 `$kill-any{` 归零；模板 `AAA,BBB,CCC` / 注释「禁 kill-any」可保留、不计。
2. **条件语义**：各组为同波双 `$kill` AND；amount 与本组刷怪一一对应（2+2 / 3+4 / 5+3）；无新组名、无链式 `$monstergroup`。
3. **零碰面**：option/HP/刷点 location/人数/delay/end 脚本/日常七线/周本/精英 YAML 无 diff。
4. **双树同步**：`plugins/` 与 `server-runtime/plugins/` 对应三份 `monster.yml` 仍 **identical**（或施工后再次 cmp 对齐）。

---

## 4. 施工勾选（批准后）

- [ ] `plugins/DungeonPlus/dungeon/EmberAbyss/monster.yml` — `floor2` + `floor7` 各一处 condition
- [ ] `plugins/DungeonPlus/dungeon/EmberRaid/monster.yml` — `wave2` condition
- [ ] `plugins/DungeonPlus/dungeon/EmberGuildBoss/monster.yml` — `wave1` condition
- [ ] 同步同内容至：
  - [ ] `server-runtime/plugins/DungeonPlus/dungeon/EmberAbyss/monster.yml`
  - [ ] `server-runtime/plugins/DungeonPlus/dungeon/EmberRaid/monster.yml`
  - [ ] `server-runtime/plugins/DungeonPlus/dungeon/EmberGuildBoss/monster.yml`
- [ ] 抽检：`rg '$kill-any{'` 有效行仅剩模板/注释；cmp plugins ↔ server-runtime 三文件 identical

**不施工：** `option.yml` / MM Health / 门坐标 / 他线副本 / git commit·push（另派）。

---

## 5. 一句话结论

三处 live 原文与派工一致（Display + 刷数 + amount 全对齐；plugins=server-runtime）；**推荐方案 A**（同波双 `$kill` AND、不链式）；待批后只改上表六路径 condition 行。
