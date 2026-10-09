# 状态 · D380 薄抽：日更七线菜单「终厅读条可躲」半行（静态）

**日期：** 2026-10-10（上海时间）  
**号：** D380Spot · 证据 `/workspace/tmp/d380-daily-menu/`  
**上游施工：** tip [`4b24bb93`](https://github.com/Picadoo/ember-server/commit/4b24bb93) · STATUS [`STATUS-ember-daily-menu-dodge-half-line-d380-2026-10-10.md`](STATUS-ember-daily-menu-dodge-half-line-d380-2026-10-10.md) · DESIGN [`DESIGN-ember-daily-menu-dodge-half-line-2026-10-10.md`](../design/DESIGN-ember-daily-menu-dodge-half-line-2026-10-10.md)  
**范围：** 只读静态 `rg` + 结构核验 · **≠关观察** · **≠开 K3** · **未改** 配置 / 开关 / MM / 体力 / 掉落 · **未切分支** · **未抢** D379 测号/证据  
**总结果：** **PASS**

## 人话

`ember_daily` 七键主 lore + 七灰态均已插「终厅 · 读条提示后环伤 · 拉开可躲」；顶栏 T 有等价总述。开关 / bv62 未拧。本号只读，未活改配置。

## 验收表

| ID | 要点 | 结果 | 证据 |
|----|------|------|------|
| T1 | 七键 S/A/C/D/E/F/G 主 lore 均含半行 | **PASS** | `structure-check.txt` · `rg-repo-ember_daily.txt` |
| T2 | 七灰态（体力不足子 lore）同含半行 | **PASS** | `structure-check.txt`（每键 gray=1） |
| T3 | T 顶栏等价总述「七线终厅：读条提示后环伤 · 拉开可躲」 | **PASS** | L37 · `90-endstate.json` |
| T4 | 无「测岗已签 / PASS / 数值暴露」禁写；开关未拧 | **PASS** | banned check · `90-endstate.json` |

## 关键证据摘要

- tip `4b24bb93`（`4b24bb93b0060da18cd5897a120da3db82f77570`）· 分支 `main`
- `plugins/TrMenu/menus/ember_daily.yml` md5 `f9c29a48a2bbf8aa1d303546424b3f52`
- 半行精确命中 **14**（七主+七灰）· 「可躲」合计 **15**（+T 总述 1）
- 按键：S/A/C/D/E/F/G 各 main=1 gray=1；T main=1
- `ember-v1.yml` md5 `48b963229dd261d0036cc8e7e65c3f8e` · six_slot enabled/migrate/set_bonus=true · `balance_version: 62`
- 本号未切分支 / 未 stash / 未 reset / 未改 live yml / 未关观察

## 不动确认

配置 · enter/体力 condition · MM Cast · 掉落 · ×0.97 · set_bonus/enabled/migrate · bv · jar · K3 · 关观察 · D379 测号/证据

## 总控旁注

**≠关观察**。菜单宣传薄抽结案；D379 残誓战斗抽测另窗，本号未碰。

---

*D380Spot · T1–T4 PASS · tip `4b24bb93` · 证据 `/workspace/tmp/d380-daily-menu/` · ≠关观察。*
