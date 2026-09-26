# DESIGN · 余烬简易技能（可控数值）

**日期：** 2026-09-13  
**目标：** 玩法向轻量主动技，**不是**复杂技能树连招；数值全部 YAML，可一眼管住。  
**约束：** 不改方块；不依赖 AttributePlus；不引入 %最大生命 / 无限叠乘。

---

## 1. 原则（防数值爆炸）

| 要 | 不要 |
|----|------|
| 每誓约 **1** 个主动技 | 多键位连招、充能层数 |
| **固定伤害** 或 **固定药水效果** | 按怪最大生命%、按战力平方 |
| CD 秒数 YAML | 无 CD 清场技 |
| 伤害有 **硬顶** `damage_cap` | 随强化等级无限涨 |
| 释放消耗可选「无」或微量体力占位 | 复杂资源条三套 |

可选成长（仍可控）：`damage = min(cap, base + talent_bonus)`，`talent_bonus` 最多 +2～+4 写死。

---

## 2. 三招（对齐已有 skill_id）

| skill_id | 誓约 | 手感 | 效果（默认） | CD |
|----------|------|------|--------------|-----|
| `ember_blaze_slash` | 烬刃 blaze | 前方短距斩 | 扇形/前方 3 格内敌方 **flat 伤害 6**（cap 10）+ 轻微火焰粒子 | 8s |
| `ember_ash_mark`（原 familiar 简化） | 灰行 ash | 印记减速 | 视线目标或最近敌 **缓慢 I 3s** + 下次普攻 **+3 flat**（4s 内，不叠层） | 10s |
| `ember_warden_taunt` | 守墓 warden | 嘲讽减伤 | 自身 **抗性 I 4s** + 周身 4 格敌 **缓慢 I 2s**（嘲讽用慢代替 AI 拉怪） | 12s |

> 原 `ember_ash_familiar` 名可保留为 id 别名，实装用 mark，避免真召唤物数值难管。

解锁：天赋树对应 skill 节点 **或** 选定誓约后赠送「基础技」一档（推荐：**选誓约即给基础技**，天赋节点只加 `talent_bonus`）。本期采用：**选誓约即解锁该系基础技**；点到 skill 节点再 +bonus。

---

## 3. 释放方式（简单）

```
/corerpg skill          # 放当前誓约技
/corerpg skill info     # 看 CD / 效果
```

可选：TrMenu 按钮调用同一命令。不做独立热键模组。

---

## 4. YAML 形状 `plugins/CoreRpg/skills.yml`

```yaml
enabled: true
# 全局：技能伤害不吃强化倍率、不吃孔石（避免双乘）
ignore_enhance_multiplier: true
damage_cap_global: 12

skills:
  ember_blaze_slash:
    covenant: blaze
    display: "§6烬斩"
    cooldown_seconds: 8
    range: 3.0
    arc_degrees: 90
    damage: 6
    damage_cap: 10
    talent_bonus_max: 3
    particles: FLAME
    sound: ENTITY_PLAYER_ATTACK_SWEEP
  ember_ash_familiar:   # id 兼容天赋树
    covenant: ash
    display: "§7灰印"
    cooldown_seconds: 10
    range: 8.0
    slow_amplifier: 0
    slow_ticks: 60
    mark_bonus_damage: 3
    mark_duration_ticks: 80
    damage_cap: 8
  ember_warden_taunt:
    covenant: warden
    display: "§9守墓壁垒"
    cooldown_seconds: 12
    radius: 4.0
    resistance_amplifier: 0
    resistance_ticks: 80
    slow_amplifier: 0
    slow_ticks: 40
```

---

## 5. 实现要点（插件岗）

1. `SkillService`：读 YAML；按 UUID 记 CD；`cast(player)`  
2. 目标：LivingEntity，排除玩家（或 PvP 关时只打怪物）；Mythic 怪可打  
3. 伤害：`entity.damage(amount, player)` 固定值  
4. 命令挂 `/corerpg skill`  
5. 版本建议 **1.4.1**；MySQL 无新表（CD 内存即可）  
6. 冒烟：选 blaze → `/corerpg skill` → 对僵尸见伤害/CD 提示  

---

## 6. 明确不做（本期）

- 技能等级 1～10  
- 装备触发技（on-hit 连环）  
- 与强化/%战力挂钩  
- 多技能栏切换  

装备特效仍走孔石微量；主动玩法只靠这三招。
