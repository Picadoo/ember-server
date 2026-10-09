# 状态 · D357 薄抽：hub 装备格导语 Stage2 护甲诚实（U1–U3）

**日期：** 2026-10-09（上海时间）  
**号：** D357Spot · 证据 `/workspace/tmp/d357-spot/`  
**上游施工：** tip [`744a9bec`](https://github.com/Picadoo/ember-server/commit/744a9bec) · STATUS [`STATUS-ember-six-slot-hub-equip-blurb-honesty-d357-2026-10-09.md`](STATUS-ember-six-slot-hub-equip-blurb-honesty-d357-2026-10-09.md) · DESIGN [`DESIGN-ember-six-slot-hub-equip-blurb-honesty-2026-10-09.md`](../design/DESIGN-ember-six-slot-hub-equip-blurb-honesty-2026-10-09.md) U1–U3  
**范围：** 只读 + 菜单活扫 · **≠关观察** · **≠开 K3** · **未拧** set_bonus / enabled / migrate / bv / jar / ×0.97  
**总结果：** **PASS**

## 人话

主菜单装备格导语已是「套装、觉醒与护甲」；同格觉醒/护甲四件套 PAPI 还在；三开关与 bv62 未动。静态与 `/ember` 活窗一致。

## 验收表

| ID | 要点 | 结果 | 证据 |
|----|------|------|------|
| U1 | hub `o` 导语含「护甲」；不再裸「套装与觉醒」独占 | **PASS** | `00-static.json` · `11-equip.json` |
| U1_live | `/ember` 装备格可见「套装、觉醒与护甲」 | **PASS** | `10-hub.json` · `11-equip.json` |
| U2 | 同格 `%corerpg_p1_armor_set%` / awaken / actions→gear 仍在 | **PASS** | `00-u2.json` · `hub-o-live.yml` |
| U2_live | 活窗仍见「护甲四件套」+「套装：」行 | **PASS** | `11-equip.json` |
| U3 | 三开关 true · bv62 · md5 未拧 | **PASS** | `00-precheck.json` · `90-endstate.json` |

## 关键证据摘要

- tip `744a9bec` · hub src/live md5 同 `fdb62c90083d5286e183e7f2688238cd`
- 导语：`§7主手刃、已选护符、实际属性、套装、觉醒与护甲`
- 活窗 PAPI 解析例：`套装：未成套` · `护甲四件套：先让刃与护符同族`（测号未成套，文案标签在）
- `ember-v1.yml` md5 `48b963229dd261d0036cc8e7e65c3f8e` · six_slot enabled/migrate/set_bonus=true · `balance_version: 62`
- jar `1.65.101-d335.local` · 分支 `main` · 本号未切分支 / 未 stash / 未 reset

## 不动确认

×0.97 · set_bonus / enabled / migrate · bv · jar · K3 · 关观察 · CoreRpg live yml 手改 · gear/help/forge 正文

## 总控旁注

**≠关观察**（满窗仍须 ≥2026-10-10 17:40 CST）。显示 only 薄抽结案。

---

*D357Spot · U1–U3 PASS · tip `744a9bec` · 证据 `/workspace/tmp/d357-spot/` · ≠关观察。*
