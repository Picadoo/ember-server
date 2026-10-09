# 状态 · D383 薄抽：挂机庭今日软目标与层差展示钩（静态）

**日期：** 2026-10-10（上海时间）  
**号：** D383Spot · 证据 `/workspace/tmp/d383-afk-dwell/`  
**上游施工：** tip [`137058ee`](https://github.com/Picadoo/ember-server/commit/137058ee) · STATUS [`STATUS-ember-afk-layer-dwell-hook-d383-2026-10-10.md`](STATUS-ember-afk-layer-dwell-hook-d383-2026-10-10.md) · DESIGN [`DESIGN-ember-afk-layer-dwell-hook-2026-10-10.md`](../design/DESIGN-ember-afk-layer-dwell-hook-2026-10-10.md)  
**范围：** 只读静态扫 `ember_p1_afk` I/②③④/S + Layout/actions 对照 + `daily_kills`/tiers 核验 · **≠关观察** · **≠开 K3** · **未改** 配置 / 开关 / afk.tiers / daily_kills · **未切分支** · play 0 人在线故**未活扫点击**（静态已覆盖 V1–V5）  
**总结果：** **PASS**

## 人话

挂机庭战况 I 有「今日软目标」框住 N/2400 +「层差一览」；②③④开放态有「相对下层多养」半行；S「去哪花」旁文案加厚且 actions 仍指工坊。Layout 未加格；产量与日表零改。

## 验收表

| ID | 要点 | 结果 | 证据 |
|----|------|------|------|
| V1 | I「今日软目标：%corerpg_p1_afk_today%」+ 未满/满了指针 | **PASS** | `I-lore.txt` · `markers.txt` |
| V2 | I「层差一览」四行短签；②`相对灰坡：+核心` · ③`相对荒原：+胚料` · ④`相对焦土：币/碎片/核心最高` | **PASS** | `I-lore.txt` · `markers.txt` |
| V3 | S +「今日条未满先攒 · 满了来这花 / 或去冒险」；actions 仍 `menu: ember_p1_forge` | **PASS** | `S-lore.txt` |
| V4 | Layout 仍 `1 2 3 / S 4 V / I F H`；①–④/S/F/H/V/B 跳转目标与 tip 前一致；**无新格** | **PASS** | `layout-keys.txt` · `actions-diff.txt`（仅行号漂移） |
| V5 | tip **未**动 `ember-v1.yml`；`daily_kills: 2400`；tiers core/blank 真表未拧；无发明 PAPI；有「魂尘≠挂机」 | **PASS** | `static-scan.txt` · tip --stat · `md5.txt` |
| Sw | six_slot enabled/migrate/set_bonus=true · bv62 · jar `1.65.101-d335.local` | **PASS** | `six-slot.txt` · `90-endstate.json` |

## 关键证据摘要

- tip `137058ee`（`137058ee3e3922c9b73153afedac1e71c8e9ba9d`）· 分支 `main` · HEAD 同 tip
- md5：afk `4265f2ed…` · hub `72ed5dde…` · `ember-v1.yml` `48b96322…`（与 D381 同，未拧）· runs `2fa15766…`
- play `list`：0/20 · 本号未 `trmenu reload` / 未 `/corerpg reload`（施工侧已于 **03:42:27 CST** reload 70 菜单）
- 本号未切分支 / 未 stash / 未 reset / 未改 live yml / 未关观察

## 不动确认

配置 · afk.tiers / daily_kills · ×0.97 · set_bonus/enabled/migrate · bv · jar · K3 · 关观察 · 切分支 · 新空跳转格 · 新 PAPI

## 总控旁注

**≠关观察**。挂机庭软目标+层差薄抽结案（静态 PASS）；活扫可另窗有人时补开 I 看 PAPI 展开，非本号阻塞。

---

*D383Spot · V1–V5 PASS · tip `137058ee` · 证据 `/workspace/tmp/d383-afk-dwell/` · ≠关观察。*
