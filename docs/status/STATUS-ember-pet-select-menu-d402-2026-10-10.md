# 状态 · D402：使魔选定盘（已批 A·M1 · 已落地 · ≠关观察 ≠抬日表 ≠sx06）

**日期：** 2026-10-10（上海时间）  
**上游：** DESIGN [`DESIGN-ember-pet-select-menu-2026-10-10.md`](../design/DESIGN-ember-pet-select-menu-2026-10-10.md) · tip [`STATUS-ember-next-hard-debt-pet-select-menu-need-design-2026-10-10.md`](STATUS-ember-next-hard-debt-pet-select-menu-need-design-2026-10-10.md) @ `e12c548c` · backlog `B-pet-select-menu`  
**裁决：** **已批 A · 批 M1 · D402 · 已落地** · 总控采纳方案 M1（薄页 `ember_pet_select`）· **≠关观察** · **≠抬 afk.tiers/daily_kills** · **≠开 gate_daily / R / K3** · **≠动 Stage2 三开关** · **≠交 sx06** · **≠教手打 `/corerpg pet`** · **零改 Java**  
**toplevel：** `/workspace/minecraft` @ `main`

## 人话

使魔页「图鉴」改成「选定使魔/换一只」：点开薄选定盘，灰灵/烬火一键切换出战。聊天 list 退出主路径；未解锁写诚实灰态，点后走插件拒门。可半行跳生活孵化。出战/收回/投喂原键保留。

## 落地对照（方案 M1）

| 窗 | 结果 | 落点 |
|----|------|------|
| W1a 薄选定页 | **PASS** | 新建 `ember_pet_select` · Title「使魔 · 选定」· A=灰灵 / C=烬火 → `corerpg pet summon pet_ember_*` |
| W1b 入口 L | **PASS** | `ember_pet` L「选定使魔/换一只」→ `menu: ember_pet_select` · **去掉** `corerpg pet list` |
| W1c Open / 状态 | **PASS** | Open tell「点灰灵或烬火切换出战」；选定页 I 挂 `active_name` / `level_line` / `unlocked_count` |
| W1d 未解锁诚实 + 生活半行 | **PASS** | lore「未解锁：生活页孵化/持蛋点选」· **无**已拥有假字 · L→`menu: ember_life` |
| 底栏返回 | **PASS** | B→`ember_pet`；E 出战/收回保留 |

## 改动清单（入仓）

| 文件 | 改动 |
|------|------|
| `plugins/TrMenu/menus/ember_pet_select.yml` | **新建** 选定盘 |
| `plugins/TrMenu/menus/ember_pet.yml` | L 改入口；Open tell；去 list |
| DESIGN / tip / backlog | 勾批 A·M1 · tip 关 · **已落地** |
| 本 STATUS | 施工结案 |
| Java / afk / Stage2 / gate_daily / jar | **未动 / 未 stage** |

## 验收（本号静态）

| ID | 结果 | 备注 |
|----|------|------|
| V1 L→选定盘 | **PASS** | 无 list |
| V2 两宠 summon id | **PASS** | ashling / cinder |
| V3 未解锁诚实 | **PASS** | 无「已拥有」 |
| V4 玩家路径无教斜杠 | **PASS** | 无手打 `/corerpg pet` 教学句 |
| V5 零改表 | **PASS** | 未碰 Java/afk/Stage2 |
| trmenu reload | 见旁注 | 本号热更 |

## 不动

- Java / pet.yml feed / life 价 · afk.tiers / daily_kills · Stage2 · gate_daily · jar  
- list 教斜杠文案收口（可选不绑）  
- 本号**不派测**

## 本号 commit 自检（D365）

| ID | 结果 |
|----|------|
| C1–C5 | **PASS** · 显式路径 add；未 `add -A`；未碰 live Stage2；未切分支 |

---

*D402 批 A·M1 · tip `e12c548c` · 使魔选定盘可点换宠 · ≠关观察 ≠抬日表 ≠sx06 ≠教斜杠。*
