# 状态 · D336：四件套三态 priority 倒置（sa > sb > on）

**日期：** 2026-10-09（上海时间）  
**上游 FAIL：** [`STATUS-ember-six-slot-armor-gear-honesty-retest-d335-2026-10-09.md`](STATUS-ember-six-slot-armor-gear-honesty-retest-d335-2026-10-09.md) @ `038f42f8`  
**裁决：** **菜单落地** · 仅改 TrMenu `priority` · **请测试复测 H1/H2**（装备页 M / 护甲页 S 三态）  
**版本：** jar **未动**（仍 `1.65.101-d335.local`）· **未改** set_bonus / enabled / migrate / bv62 / 条件字符串 / lore / actions / ×0.97

## 人话

PAPI 短别名 `armor_sa` / `armor_sb` 解析一直是对的，图标却总落在「先让刃与护符同族」（`armor_on`）。根因不是条件写法，而是 **本服 TrMenu 子图标：priority 数字越小越优先**。`sa=1` 时 `on` 也常为 `1`（双命中）；旧值 `sa/sb=4~5`、`on=2~3`，于是 `on` 一直压过 `sa/sb`。D333 工坊灰显能过，是因为非甲时 armor 条件不命中、没有双命中竞争。

本号把三态 priority 倒过来：`sa=1` · `sb=2` · `on=10`。装备页待领（stash）改为 `3`（低于 sa/sb、高于 on），避免压过已激活/进行中。

## 根因（已坐实）

| 项 | 说明 |
|----|------|
| 双命中 | `armor_sa==1` 时 `armor_on` 也常 `==1` |
| 旧 priority | armor S：sa=4 / sb=3 / on=2；gear M：sa=5 / sb=4 / on=3 / stash=2 |
| 本服规则 | **数字越小越优先**（与文件头旧注释「从高到低」相反；头注释已勘误） |
| 结果 | on 压过 sa/sb → 永远显示未激活文案 |

## 落地 priority

| 文件 · 格 | 分支 | 旧 | 新 |
|-----------|------|----|----|
| `ember_p1_armor.yml` · S | `armor_sa == 1` | 4 | **1** |
| 同上 | `armor_sb == 1` | 3 | **2** |
| 同上 | `armor_on == 1` | 2 | **10** |
| `ember_p1_gear.yml` · M | `armor_sa == 1` | 5 | **1** |
| 同上 | `armor_sb == 1` | 4 | **2** |
| 同上 | `armor_stash_has == 1` | 2 | **3** |
| 同上 | `armor_on == 1` | 3 | **10** |

条件字符串仍为 `check papi %corerpg_p1_armor_sa% == 1` 等（**未**改回 `contains`）。

## 重载

| 项 | 结果 |
|----|------|
| 命令 | `scripts/console.sh play "trmenu reload"` |
| 日志 | `[TrMenu] 良好 \| 70 个菜单已加载 (158 ms)`（约 20:04:29 CST） |
| 旁证 | TConfigWatcher 亦已自动重载两文件 |

## 验收（本号）

| # | 项 | 结果 |
|---|----|------|
| M1 | S：sa=1 / sb=2 / on=10 | PASS（文件核） |
| M2 | M：sa=1 / sb=2 / stash=3 / on=10 | PASS（文件核） |
| M3 | 条件仍为 `check papi … == 1`，无 contains | PASS（rg 核） |
| M4 | lore / actions / jar / 开关 / bv 未动 | PASS |
| M5 | trmenu reload 成功 | PASS |
| H1/H2 | 装备页 M / 护甲页 S 三态实机 | **请测试复测** |

## 不动

jar · set_bonus / enabled / migrate · bv62 · 价表 · ×0.97 · K3 · 观察期 Stage1 · 其他格 priority（穿着/候选/全部换上等）

---

*D336 菜单 priority 倒置 · 请测试复测 H1/H2。*
