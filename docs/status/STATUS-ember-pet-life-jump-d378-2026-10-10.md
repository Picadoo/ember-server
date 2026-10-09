# 状态 · D378：使魔页→生活兑魂尘跳转（闭环反向互指 · 已施工）

**日期：** 2026-10-10（上海时间）  
**上游：** tip [`STATUS-ember-next-hard-debt-pet-life-jump-need-design-2026-10-10.md`](STATUS-ember-next-hard-debt-pet-life-jump-need-design-2026-10-10.md) @ `83aced08` · DESIGN [`DESIGN-ember-pet-life-jump-2026-10-10.md`](../design/DESIGN-ember-pet-life-jump-2026-10-10.md)  
**裁决：** **已批 A · 批 M · D378** · 总控采纳方案 M（W1a/W1b/W1c；W1d 未做）· tip 旁注已关 · backlog 已对齐 · **≠关观察** · **≠改 life.yml / pet.yml 价或次数** · **≠抬 afk 日表** · **≠开 R** · **≠改 ×0.97 / 三开关 / bv**  
**版本：** TrMenu + docs · jar / afk / set_bonus / bv / life / pet **未动**  
**toplevel（落字时）：** `/workspace/minecraft` @ `main`（C0 合格）

## 人话

站在使魔页缺魂尘时，能点「去生活兑魂尘」直接进补给·生活兑旧靴/碎骨；投喂和打开页也会提示这条路。出战/收回/投喂原键保留。产量与兑换价零改。

## 落地摘要（方案 M）

| 窗 | 本号 |
|----|------|
| W1a 跳转格入盘 | Layout 松排：`#   F   #` + `# L E S #`；F=`§d去生活兑魂尘` → `menu: ember_life` |
| W1b 投喂/Open 互指 | Open +「缺魂尘：点本页去生活」；S lore +「缺尘点旁格」；保留「非挂机」诚实句 |
| W1c 失败导向（同批） | S 点击后追加 tell「没有魂尘？点本页去生活兑换」（不伪造成功；不教裸 buy） |
| W1d 枢纽半指 | **未做**（D377 旁轨已够） |

## 验收自检

| ID | 结果 | 备注 |
|----|------|------|
| V1 | **PASS（静态）** | Layout 含 F；Icons.F → `menu: ember_life`；短名「去生活兑魂尘」 |
| V2 | **PASS（静态）** | L=list · E=summon/dismiss · S=feed **仍在** |
| V3 | **PASS（静态）** | Open/S 含生活互指；**无**「挂机副产」回潮；仍写「非挂机」 |
| V4 | **PASS** | 本号未改 `life.yml` / `pet.yml` / afk.tiers / ×0.97 / 三开关 / bv |
| V5 | **PASS（热更）** | `trmenu reload` 见下 |

## 改动清单（本号）

| 文件 | 改动 |
|------|------|
| `plugins/TrMenu/menus/ember_pet.yml` | F 跳转格 + Layout；Open/S 互指；W1c tell；保留 summon/dismiss/feed |
| `DESIGN-ember-pet-life-jump-2026-10-10.md` | 勾批 M；STATUS→已批·D378；变更记录 |
| tip `…-pet-life-jump-need-design-…` | 旁注已关 |
| `design-ember-content-backlog.md` | `B-pet-life-jump` → **已关 · D378 已施工** |
| 本 STATUS | 落地摘要 · 验收 |
| life.yml / pet.yml / afk / ×0.97 / 三开关 / bv / jar / ladder / calamity / p1-six | **未动 / 未 stage** |

## 热更

- play 热更：`scripts/console.sh play "trmenu reload"`（见落字后执行日志）
- **未**执行 `/corerpg reload`（零 life/pet/afk/开关变更）

## 本号 commit 自检（按 D365）

| ID | 结果 | 备注 |
|----|------|------|
| C1 | **PASS** | 工作区有 ladder/calamity-state/p1-six/MM SavedData 脏迹（常态） |
| C2 | **PASS** | cached 仅授权路径（TrMenu/docs） |
| C3 | **PASS** | 显式 `git add` 各路径；未用 `add -A` |
| C4 | **PASS** | 未碰 live ember-v1；未切分支；toplevel=`/workspace/minecraft` |
| C5 | **PASS** | 脏 runtime 未入暂存 · 可 commit |

## 不动

关观察 · 改 life/pet 价或次数 · 改 afk.tiers / daily_kills · 改 ×0.97 / set_bonus / 三开关 / bv · 开 R · 开 K3 · Pack6 / 天赋 / 灰印 · stage 脏 runtime · 复述 D373–D377 主交付

---

*D378 批 A·M · tip `83aced08` · 使魔↔生活兑尘双向可点 · ≠关观察 ≠抬挂机表 ≠改价。*
