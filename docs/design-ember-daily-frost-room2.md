# 设计稿 · 霜晶裂隙第二房节奏（链式重叠）

> 债源：`STATUS-ember-daily-room2-expand-closeout-review.md` 软债#1  
> **已批准（总控 2026-09-28）：** 方案 A 房2链式；**不动房1**（左右交错记忆点）；禁止 Boss 前压本轮。

---

## 0. 稿件信息

| 字段 | 填写 |
|------|------|
| 稿件 | 日常 · 霜晶裂隙第二房链式重叠 |
| 负责人 / 日期 | 余烬-总控薄批 / 2026-09-28 Asia/Shanghai |
| 关联 | `EmberDailyFrost/monster.yml` 仅 `wave2a`/`wave2b` |
| 状态 | **已批准 · 本额度施工** |
| 对照 | 焦骨 `design-ember-daily-ash-room2.md` / 地窖房2 / 断塔试点 |

### 硬约束

| 可动 | 不可动 |
|------|--------|
| 霜晶 **房2** `wave2a.start`→delay2 `wave2b`；开门仍在 `wave2b.end` | 房1 `wave1`/`wave1b` 左右交错与开门 |
| 房2 Display 文案（重叠体感） | 体力/掉落/Boss·小怪 HP；门位 z=16/34；坐标默认不动 |
| | 庭院/潮蚀/断塔/焦骨/地窖/锈轨 YAML；`kill-any`；跨组合并 `$kill` |

**总量：** 小怪仍 11+Boss；零增怪。

---

## 1. 问题

房1 交错很香；**房2 仍「尸×3 等清 → delay2 霜矢×3」**。挑刺收口：肝霜晶+庭院+潮蚀时房2 仍像亲戚排队。

## 2. 改后体感

进折台打霜晶尸的同时 **约 2s 内霜矢已压上**；两组都清完才开霜厅门；房1 左右交错原样。

## 3. 施工要点（对齐焦骨）

现网：`wave2a.end` → `$monstergroup wave2b delay=2`（等清）。  
目标：`wave2a.start` → `$monstergroup wave2b delay=2`；`wave2a.end` **不再**拉 wave2b；开门/`boss` 仍挂 `wave2b.end`。

文案：`wave2a` 提示「霜矢将交错压上」；`wave2b` 提示「重叠压上 · 清完开霜厅门」。

## 4. 验收

- 静态：仅 `EmberDailyFrost/monster.yml` 房2 diff；房1 零 diff  
- 测岗：房2 live 重叠窗（尸未清完时霜矢已出）；`$kill` 两组独立；开门在 wave2b；door1/Boss 行为不变  
- 非本线零误伤

## 5. 明确不改

庭院/潮蚀房2 等清 + Boss 前压：挑刺软债#2 标为**故意保留**，本轮不动。
