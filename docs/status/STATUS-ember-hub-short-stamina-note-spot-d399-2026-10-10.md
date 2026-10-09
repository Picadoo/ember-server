# 状态 · D399 薄抽：枢纽短征体力旁注（静态）

**日期：** 2026-10-10（上海时间）  
**号：** D399Spot · 证据 `/workspace/tmp/d399-hub-short-stamina/`  
**上游施工：** tip [`aa9b7505`](https://github.com/Picadoo/ember-server/commit/aa9b7505) · STATUS [`STATUS-ember-hub-short-stamina-note-d399-2026-10-10.md`](STATUS-ember-hub-short-stamina-note-d399-2026-10-10.md) · DESIGN [`DESIGN-ember-hub-short-stamina-note-2026-10-10.md`](../design/DESIGN-ember-hub-short-stamina-note-2026-10-10.md)  
**范围：** 只读静态 `rg` + Icons.H 三分支结构核验 · **≠关观察** · **≠开 K3** · **未改** 配置 / stamina / route_pri / afk / Stage2 / gate_daily · **未切分支** · play 有 D398Recipe 在线故**未活扫点击**（静态已覆盖 V1–V6）  
**总结果：** **PASS**

## 人话

打开枢纽「今天该打哪」：三分支体力行都写「短征30」；默认/挂机满「今日可追」含短征并挂 `sx_day_left_sum`；默认右键进短征选本；体力&lt;30 无进本跳、有「短征需30」旁注，生活·工坊可追仍在。零拧 stamina / route_pri / afk / Stage2 / gate_daily。

## 验收表

| ID | 要点 | 结果 | 证据 |
|----|------|------|------|
| V1 | 三分支体力行含「短征30」 | **PASS** | `branch-*.yml` · `structure-check.txt` |
| V2 | 默认/挂机满「今日可追」含短征 + `%corerpg_p1_sx_day_left_sum%` | **PASS** | branch-default / branch-afk |
| V3 | 体力&lt;30：无短征/冒险进本跳；有「短征需30」；左挂机/右生活 | **PASS** | branch-low |
| V4 | 默认右键 → `menu: ember_p1_short`（挂机满 Shift+左键同） | **PASS** | branch-default / afk-shift-left.txt |
| V5 | 无旧日常窟 / sx05 / 「短征不耗体力」/「挂机产魂尘」 | **PASS** | rg-neg · structure-check |
| V5b | 生活·工坊可追三分支仍在 | **PASS** | V5b_* |
| V6 | tip 仅 hub+docs；six_slot 三开 true；daily_kills=2400；route_pri 占位保留 | **PASS** | tip-files · switches.json |
| Live=tip | live `ember_hub.yml` md5 = tip `aa9b7505` | **PASS** | hub-tip-md5 / hub-live-md5 |

## 关键证据摘要

- tip `aa9b7505`（`aa9b75055505c4696ebcbc9ce4d82c47032c8a51`）· 祖先于 HEAD `e3f8c948` · 分支 `main`
- md5：hub `ff197dee1a8770f7b911561173d5a76b` · ember-v1.yml `ce05e137616aae6fad4b2f28ed7d945e` · runs `2b7b0572a1f2ad73314859353ac26880`
- switches：enabled/migrate/set_bonus=true · bv67 · afk.daily_kills=2400
- play `list`：1/20（D398Recipe）· 本号未活扫 · 未 `trmenu reload` / 未 `/corerpg reload`（施工侧已 reload 72）
- 本号未切分支 / 未 stash / 未 reset / 未改 live yml / 未关观察

## 不动确认

stamina 数值 · route_pri 主逻辑 · afk.tiers / daily_kills · Stage2 三开关 · gate_daily · ×0.97 · jar · K3 · 关观察 · 切分支

## 总控旁注

**≠关观察**。枢纽短征体力旁注薄抽结案（静态 PASS）；活扫可另窗补点默认右键→短征选页，非本号阻塞。

---

*D399Spot · V1–V6 PASS · tip `aa9b7505` · 证据 `/workspace/tmp/d399-hub-short-stamina/` · ≠关观察 · checked 2026-10-10 06:17:10 CST。*
