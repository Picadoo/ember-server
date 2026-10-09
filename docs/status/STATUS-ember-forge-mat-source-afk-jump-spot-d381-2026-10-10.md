# 状态 · D381 薄抽：工坊「材料哪来」→挂机庭 / 仓库半对齐 / 挂机回指（静态）

**日期：** 2026-10-10（上海时间）  
**号：** D381Spot · 证据 `/workspace/tmp/d381-forge-afk-jump/`  
**上游施工：** tip [`0131087a`](https://github.com/Picadoo/ember-server/commit/0131087a) · STATUS [`STATUS-ember-forge-mat-source-afk-jump-d381-2026-10-10.md`](STATUS-ember-forge-mat-source-afk-jump-d381-2026-10-10.md) · DESIGN [`DESIGN-ember-forge-mat-source-afk-jump-2026-10-10.md`](../design/DESIGN-ember-forge-mat-source-afk-jump-2026-10-10.md)  
**范围：** 只读静态 `rg` + Layout/Icons 结构核验 · **≠关观察** · **≠开 K3** · **未改** 配置 / 开关 / afk.tiers / 价表 · **未切分支** · play 0 人在线故**未活扫点击**（静态已覆盖 V1–V7）  
**总结果：** **PASS**

## 人话

工坊底栏 A「材料哪来 · 挂机庭」→ `ember_p1_afk`；Open/强化半行互指在。仓库 V 有「挂机战利品进本仓」、邻格 A 轻跳挂机庭。挂机「去哪花」仍回工坊，并有「工坊内可回本庭」。开关 / bv62 / daily_kills=2400 未拧。

## 验收表

| ID | 要点 | 结果 | 证据 |
|----|------|------|------|
| V1 | forge Layout `# A b  B#` · Icons.A 短名「材料哪来 · 挂机庭」 | **PASS** | `layout-switches.txt` · `rg-static.txt` |
| V2 | A actions → `menu: ember_p1_afk` | **PASS** | forge A block L351 |
| V3 | afk S「去哪花」仍 → `menu: ember_p1_forge`；+「工坊内可回本庭」 | **PASS** | `rg-static.txt` afk S |
| V4 | lore 产→仓→花；有「魂尘≠挂机」；**无**「挂机产魂尘」 | **PASS** | `structure-check.txt` negative |
| V5 | E/U/R/Q/S/D + e/u/r/q/s/d 确认列仍在；A 未挤列 | **PASS** | `structure-check.txt` |
| V6 | tip 未动 `ember-v1.yml`；daily_kills 仍 2400 | **PASS** | tip --stat · switches snapshot |
| V7 | storage V「挂机战利品进本仓」+ A → `menu: ember_p1_afk` | **PASS** | `rg-static.txt` storage |
| Ux | Open tell「缺料？点「材料哪来」回挂机庭」；强化预览「材料多从挂机进仓」 | **PASS** | forge Events/Open + E lore |
| Sw | six_slot enabled/migrate/set_bonus=true · bv62 | **PASS** | `90-endstate.json` |

## 关键证据摘要

- tip `0131087a`（`0131087a389eca1f0ead55fcb3f1531d1c61199c`）· 分支 `main` · HEAD 同 tip
- md5：forge `db57ccfa…` · storage `b33b9811…` · afk `90351ff7…` · `ember-v1.yml` `48b96322…` · runs `2fa15766…`
- play `list`：0/20 · 本号未 `trmenu reload` / 未 `/corerpg reload`（施工侧已于 03:32 CST reload 70 菜单）
- 本号未切分支 / 未 stash / 未 reset / 未改 live yml / 未关观察

## 不动确认

配置 · afk.tiers / daily_kills · 工坊价表 · ×0.97 · set_bonus/enabled/migrate · bv · jar · K3 · 关观察 · 切分支

## 总控旁注

**≠关观察**。产花双向互指薄抽结案（静态 PASS）；活扫可另窗有人时补点 A 跳转，非本号阻塞。

---

*D381Spot · V1–V7 PASS · tip `0131087a` · 证据 `/workspace/tmp/d381-forge-afk-jump/` · ≠关观察。*
