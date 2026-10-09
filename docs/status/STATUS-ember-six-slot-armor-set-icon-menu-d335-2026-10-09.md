# 状态 · D335：四件套三态菜单改用 armor_sa / armor_sb

**日期：** 2026-10-09（上海时间）  
**上游：** tip @ `985942be`（插件 D335：`armor_sa`≡active · `armor_sb`≡busy 已装 play `1.65.101-d335.local`）  
**裁决：** **菜单落地** · 仅改 TrMenu 条件 · **请测试复测 H1**（装备页 M / 护甲页 S 三态）  
**版本：** jar **未动**（仍 `1.65.101-d335.local`）· **未改** set_bonus / enabled / migrate / bv62 / lore / actions / ×0.97

## 人话

装备页护甲入口（M）和护甲页四件套格（S）的三态条件，从 `armor_set contains …` 改成短别名 `armor_sa` / `armor_sb` / `armor_on`。文案和点击行为没动；重载后请测试岗复测 H1。

## 改动

| 文件 | 改动 |
|------|------|
| `plugins/TrMenu/menus/ember_p1_armor.yml` | S 格：`contains 受伤/需同族` → `armor_sa/sb == 1`；`armor_on` 保留 |
| `plugins/TrMenu/menus/ember_p1_gear.yml` | M 格：同上 |
| lore / actions / priority | **未动** |
| jar / yml 开关 / bv | **未动** |

## 条件（落地后）

```yaml
check papi %corerpg_p1_armor_sa% == 1   # active，priority 最高
check papi %corerpg_p1_armor_sb% == 1   # busy
check papi %corerpg_p1_armor_on% == 1   # 未激活
```

已删除：`contains` / `armor_set_active` / `armor_set_busy` 条件写法。

## 重载

| 项 | 结果 |
|----|------|
| 命令 | `scripts/console.sh play "trmenu reload"` |
| 日志 | `[TrMenu] 良好 \| 70 个菜单已加载 (121 ms)` |

## 验收（本号）

| # | 项 | 结果 |
|---|----|------|
| M1 | S/M 条件为 sa/sb/on | PASS（文件核） |
| M2 | 无 contains / set_active\|busy 条件 | PASS（rg 核） |
| M3 | lore/actions 未动 | PASS（diff 限 condition/注释） |
| M4 | trmenu reload 成功 | PASS |
| H1 | 装备页 M / 护甲页 S 三态实机 | **请测试复测** |

## 不动

jar · set_bonus / enabled / migrate · bv62 · 价表 · ×0.97 · K3 · 观察期 Stage1

---

*D335 菜单落地 · 依赖 tip 985942be · 请测试复测 H1。*
