# DESIGN · 余烬深渊「冲层 + 撤离结算」

**日期：** 2026-09-13（Asia/Shanghai）  
**正式规格：** `docs/ember-abyss-evacuate-spec.md`（验收以该文为准）  
**承接：** `docs/ember-abyss-calamity.md` §1 · `STATUS-ember-abyss.md`  
**约束：** 不改 Paper；票进本扣、不退；奖励走 console `ni` / `corerpg abyss *`

---

## 玩家规则（摘要）

1. 进本扣 **余烬深渊票 ×1**，撤离 / 超时 / 团灭均不返还。  
2. **无尽层**：清层后续层；无「打完 5 层必须通关结束」。  
3. 结束方式：主动撤离（`/corerpg abyss evacuate` 或 jar 监听 leave）/ 超时 / 团灭 → **按本局最高层发结算箱一次**。  
4. 结算档：1～4 / 5～9 / 10～14 / 15～19 / 20+（见正式规格表）；`recordAbyssFloor` 在 settle 时写入。  
5. COMPLETE（打到本期配置顶层，如 floor8）额外发 T2 刃/护符；材料/孔石一律由 `settle` 按最高层查表。

---

## 命令面（CoreRpg ≥1.4.6；现网 jar 可能尚无，YAML 先接线）

| 命令 | 时机 | 作用 |
|------|------|------|
| `corerpg abyss progress <player> N` | 清层 end / 进本 N=0 | 本局 `abyss_floor=N` |
| `corerpg abyss settle <player>` | 奖励脚本 / 超时 / 撤离 | 按最高层发箱一次 + 天梯 |
| `corerpg abyss evacuate` | 菜单「撤离」/ 玩家主动 | settle + 离本（票不退） |

---

## DP / 菜单改动点

| 文件 | 改动 |
|------|------|
| `EmberAbyss/monster.yml` | floor1～N 清层：`progress N` + 撤离提示行；floor5 续下层；顶层 `$end reward=true COMPLETE` |
| `EmberAbyss/option.yml` | start：`progress 0`；reward：`settle` +（COMPLETE）T2 + `mvtp ember_hub` |
| `EmberAbyss/task/timeout.yml` | 超时：`settle` → `$end reward=false`（防双箱） |
| `TrMenu/menus/ember_abyss.yml` | 撤离键 + lore：冲层 / 最高层结算 / 票不退 |

本期实现 **8 层 stub**（6～8 加压克隆）；无尽语义靠 jar progress + 撤离/超时 settle，后续可再加层 YAML。
