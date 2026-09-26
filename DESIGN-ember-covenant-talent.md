# 余烬誓约 + 天赋 — 落地规格（对齐菜单 / CoreRpg / RpgBot）

**日期：** 2026-09-12（Asia/Shanghai）  
**承接：** `DESIGN-ember-rpg-systems.md` §2（角色成长柱）；货币见 `DESIGN-ember-economy-monetization.md`  
**栈：** Paper 1.12.2 · CoreRpg（插件岗实现）· TrMenu ·（可选）AttributePlus / MythicMobs skill  
**硬约束：** 不改 Paper；**不重建** `CoreRpg.jar`（本文件只定规格、YAML 形状与菜单壳）；所有系数进 YAML。

---

## 0. 范围与职责

| 岗 | 活 |
|----|----|
| 总控 / 本规格 | 定 ID、点经济、重置价、命令面、`covenant.yml` / `talent.yml` 形状、TrMenu 接线、验收 |
| 余烬-插件 | CoreRpg：`covenant` / `talent` 子命令 + 读 YAML + 玩家存档字段 |
| 余烬-菜单 | TrMenu：`ember_covenant.yml` / `ember_talent.yml`；hub 入口已改「打开菜单」 |
| 余烬-测试 (RpgBot) | §8 冒烟（命令落地后） |

**本期不做：** 真实 MM 技能释放、AttributePlus 公式接线、成就→天赋点自动发放（只留 YAML 旋钮与命令面）。

---

## 1. 三誓约（轻量职业）

三选一，决定**技能倾向与装备权重**，不锁死玩法。命令/配置 ID 用英文；展示名中文。

| ID | 显示名 | 定位 | 天赋偏 | 推荐武器权重 |
|----|--------|------|--------|--------------|
| `blaze` | 烬刃 | 近战爆发 | 暴击、吸血、斩击技 | 刃高 |
| `ash` | 灰行 | 持续 / 风筝 | 攻速、减速、微弱使魔召唤 | 刃或远程占位 |
| `warden` | 守墓 | 生存 / 团队 | 减伤、嘲讽、护符加成 | 护符权重高 |

### 1.1 选定规则

- **首次选定免费**（`covenant == none` → `set` 成功，不扣费）。  
- 之后换誓约 = **洗誓约**：扣晶钻 **或** 消耗 NI `mat_ember_covenant_reset`（誓约重置券）。  
- 洗誓约默认 **清空该角色当前天赋分配**（点数退回可用池；已学主动技槽清空）。可用 `covenant.yml` → `reset_clears_talent: true` 关掉清空则禁止（本期固定 true）。  
- 未选定前打开天赋菜单：提示先选誓约，不给点。

### 1.2 誓约被动（展示 / 伪属性，进 YAML）

微量、有 cap；**不**做成毕业碾压。示意默认：

| ID | stats（示意） | cap 备注 |
|----|---------------|----------|
| `blaze` | `crit_chance_pct: 0.03`, `life_steal_pct: 0.01` | 暴击/吸血另有全局 cap |
| `ash` | `attack_speed_pct: 0.04`, `move_speed_pct: 0.02` | 移速全局 cap |
| `warden` | `damage_taken_pct: -0.04`, `charm_stat_bonus_pct: 0.05` | 减伤绝对值有 floor |

插件岗可先只写 lore/记分板展示，伤害公式未接 AP 前以「标记 + 展示」验收。

---

## 2. 天赋点经济

### 2.1 供给

| 来源 | 默认旋钮 | 说明 |
|------|----------|------|
| 角色等级 | `points_per_level: 1`，`ember_level` 1～60 → 理论 59（1 级起算可配） | 主供给 |
| 成就点兑换 | `points_per_achievement_chunk: 1` / 每 N 成就点 | 副供给；成就未实装前命令可 `talent grant` 测 |
| 图录阶段奖 | 设计预留 | 见总纲 §5.3 |
| 硬顶 | `max_spendable_points: 20` | **可点上限约 15～20**（总纲）；多余点仍可攒但不可超硬顶花费 |

推荐验收曲线：标准号约 **18** 点可花完一棵树主干；休闲号 12～15。

### 2.2 花费

- 每点天赋节点 `cost: 1`（少数终极节点可 `cost: 2`）。  
- 树内有前置：`requires: [node_id, …]`。  
- 每誓约一棵独立树；换誓约后切到对应树（旧树分配已清）。

### 2.3 洗点（天赋重置）

| 方式 | 默认 | 说明 |
|------|------|------|
| 日免费 1 次 | `talent.reset.free_per_day: 1` | Asia/Shanghai 日历日 |
| 额外重置 | `talent.reset.extra_crystal_cash: 25` | 晶钻（硬通货 E） |
| 或券 | NI `mat_ember_talent_reset`（天赋重置券） | 商城便利柜 / 活跃箱低概率 |

洗点：已分配点全部退回可用池；节点状态清空；**不**改变誓约。

### 2.4 洗誓约费用（与 §1.1 对齐）

| 方式 | 默认 |
|------|------|
| 晶钻 | `covenant.reset.crystal_cash: 80` |
| 或券 | NI `mat_ember_covenant_reset` ×1 |
| 冷却（可选） | `covenant.reset.cooldown_hours: 0`（本期 0） |

**禁止：** 用材料层 A/B（碎片/晶/核心）付洗点——避免战力材料进便利柜短路。

---

## 3. 天赋树形状（每誓约约 12～16 节点示意）

节点类型：

| type | 含义 | 本期实现 |
|------|------|----------|
| `passive` | 数值被动 | CoreRpg 伪表 / AP |
| `skill` | 主动技解锁 | 记 `skill_id`；实际释放交 MM / CoreCombat（可先 stub） |
| `qol` | 微量 QoL（移速等） | 严格 cap |

### 3.1 烬刃 `blaze`（示意 ID）

| node | 名 | type | cost | requires | stats / skill |
|------|----|------|------|----------|---------------|
| `blaze_root` | 燃锋 | passive | 1 | — | `phys_damage: 1` |
| `blaze_crit1` | 燃眼 | passive | 1 | blaze_root | `crit_chance_pct: 0.01` |
| `blaze_leech1` | 饮烬 | passive | 1 | blaze_root | `life_steal_pct: 0.005` |
| `blaze_crit2` | 灼心 | passive | 1 | blaze_crit1 | `crit_damage_pct: 0.05` |
| `blaze_slash` | 烬斩 | skill | 2 | blaze_crit1, blaze_leech1 | `skill_id: ember_blaze_slash` |
| `blaze_cap` | 燎原 | passive | 2 | blaze_slash, blaze_crit2 | `phys_damage: 2`, `crit_chance_pct: 0.01` |

（完整 15 点树由插件岗按同形状扩；菜单壳先展示 6 个代表节点 +「更多见 YAML」。）

### 3.2 灰行 `ash` / 守墓 `warden`

同形状：`ash_root`… / `warden_root`…；偏攻速·减速·召唤 / 减伤·嘲讽·护符。详见 `talent.yml` stub。

---

## 4. 玩家存档字段（`players/<uuid>.yml`）

在现有 coin/sign/activity 旁追加（插件岗写入）：

```yaml
covenant: none          # none | blaze | ash | warden
covenantChosenAt: ""    # ISO 或 yyyy-MM-dd HH:mm
talentPointsEarned: 0
talentPointsSpent: 0
talentNodes: []         # [ "blaze_root", "blaze_crit1", ... ]
talentFreeResetDate: "" # yyyy-MM-dd Asia/Shanghai（上次免费洗点日）
talentFreeResetsUsed: 0 # 当日已用免费次数
```

可选 PAPI（落地后）：`%corerpg_covenant%` `%corerpg_talent_points%` `%corerpg_talent_spent%`。

---

## 5. 命令面（CoreRpg · 插件岗实现）

权限：玩家 `corerpg.covenant` / `corerpg.talent`（默认真玩家有）；管理 `corerpg.admin`。

| 命令 | 行为 |
|------|------|
| `/corerpg covenant` | 查看当前誓约与洗约费用提示 |
| `/corerpg covenant set <blaze\|ash\|warden>` | 首次免费选定；已选定则走洗约扣费 |
| `/corerpg covenant reset` | 显式洗约（同 set 到另一誓约前的确认流可二选一） |
| `/corerpg talent` | 查看可用点 / 已花 / 已点节点 |
| `/corerpg talent info <nodeId>` | 节点说明与前置 |
| `/corerpg talent unlock <nodeId>` | 扣点解锁 |
| `/corerpg talent reset` | 洗点（日免费或扣费） |
| `/corerpg talent grant <player> <n>` | 管理：加可用点（admin） |

别名：挂在 `/crpg` `/rpg` 同一子树。

### 5.1 聊天前缀（便于 Bot 断言）

- `§a[誓约] 已选定：烬刃（blaze）`
- `§e[誓约] 首次选定免费。之后洗约需晶钻或誓约重置券。`
- `§c[誓约] 费用不足 / 无效 ID`
- `§a[天赋] 解锁：燃锋（blaze_root）剩余 x 点`
- `§c[天赋] 点不足 / 前置未点 / 请先选定誓约`
- `§a[天赋] 洗点完成（免费）/（已扣晶钻）`

### 5.2 菜单壳当前行为（命令未实装前）

TrMenu 点击：**tell 提示文案** + 建议玩家执行  
`/corerpg covenant set blaze|ash|warden`  
（命令可能尚不存在；以本文档为插件岗契约。）

---

## 6. YAML 旋钮形状

### 6.1 `plugins/CoreRpg/covenant.yml`（及 `src/main/resources/covenant.yml`）

```yaml
# CoreRpg covenant — 余烬誓约（DESIGN-ember-covenant-talent.md）
enabled: true

first_pick_free: true
reset_clears_talent: true

reset:
  crystal_cash: 80
  ticket_ni_id: mat_ember_covenant_reset
  cooldown_hours: 0

covenants:
  blaze:
    display: "§6烬刃"
    description: "近战爆发 · 暴击 / 吸血 / 斩击"
    weapon_bias: blade
    stats:
      crit_chance_pct: 0.03
      life_steal_pct: 0.01
  ash:
    display: "§7灰行"
    description: "持续风筝 · 攻速 / 减速 / 微弱使魔"
    weapon_bias: hybrid
    stats:
      attack_speed_pct: 0.04
      move_speed_pct: 0.02
  warden:
    display: "§9守墓"
    description: "生存团队 · 减伤 / 嘲讽 / 护符加成"
    weapon_bias: charm
    stats:
      damage_taken_pct: -0.04
      charm_stat_bonus_pct: 0.05

caps:
  crit_chance_pct: 0.35
  life_steal_pct: 0.05
  move_speed_pct: 0.08
  damage_taken_pct_floor: -0.25
```

### 6.2 `plugins/CoreRpg/talent.yml`

```yaml
# CoreRpg talent — 余烬天赋树（DESIGN-ember-covenant-talent.md）
enabled: true

points_per_level: 1
level_points_from: 2          # ember_level>=2 开始每级 +1
max_spendable_points: 20
points_per_achievement_chunk: 1
achievement_chunk_size: 50    # 预留

reset:
  free_per_day: 1
  extra_crystal_cash: 25
  ticket_ni_id: mat_ember_talent_reset

# trees.<covenantId>.nodes.<nodeId>
trees:
  blaze:
    nodes:
      blaze_root:
        display: "§6燃锋"
        type: passive
        cost: 1
        requires: []
        stats: { phys_damage: 1 }
      blaze_crit1:
        display: "§c燃眼"
        type: passive
        cost: 1
        requires: [blaze_root]
        stats: { crit_chance_pct: 0.01 }
      blaze_leech1:
        display: "§4饮烬"
        type: passive
        cost: 1
        requires: [blaze_root]
        stats: { life_steal_pct: 0.005 }
      blaze_crit2:
        display: "§c灼心"
        type: passive
        cost: 1
        requires: [blaze_crit1]
        stats: { crit_damage_pct: 0.05 }
      blaze_slash:
        display: "§6烬斩"
        type: skill
        cost: 2
        requires: [blaze_crit1, blaze_leech1]
        skill_id: ember_blaze_slash
      blaze_cap:
        display: "§6燎原"
        type: passive
        cost: 2
        requires: [blaze_slash, blaze_crit2]
        stats: { phys_damage: 2, crit_chance_pct: 0.01 }
  ash:
    nodes:
      ash_root:
        display: "§7灰踪"
        type: passive
        cost: 1
        requires: []
        stats: { attack_speed_pct: 0.02 }
      ash_haste1:
        display: "§e疾灰"
        type: passive
        cost: 1
        requires: [ash_root]
        stats: { attack_speed_pct: 0.02 }
      ash_slow1:
        display: "§8滞步"
        type: passive
        cost: 1
        requires: [ash_root]
        stats: { on_hit_slow_pct: 0.05 }
      ash_fam:
        display: "§7微尘使魔"
        type: skill
        cost: 2
        requires: [ash_haste1]
        skill_id: ember_ash_familiar
      ash_cap:
        display: "§7烟幕"
        type: passive
        cost: 2
        requires: [ash_fam, ash_slow1]
        stats: { move_speed_pct: 0.02, attack_speed_pct: 0.02 }
  warden:
    nodes:
      warden_root:
        display: "§9守碑"
        type: passive
        cost: 1
        requires: []
        stats: { phys_defense: 1 }
      warden_guard1:
        display: "§9石肤"
        type: passive
        cost: 1
        requires: [warden_root]
        stats: { damage_taken_pct: -0.02 }
      warden_taunt:
        display: "§1墓鸣"
        type: skill
        cost: 2
        requires: [warden_root]
        skill_id: ember_warden_taunt
      warden_charm1:
        display: "§e护符共鸣"
        type: passive
        cost: 1
        requires: [warden_root]
        stats: { charm_stat_bonus_pct: 0.03 }
      warden_cap:
        display: "§9永护"
        type: passive
        cost: 2
        requires: [warden_taunt, warden_guard1, warden_charm1]
        stats: { damage_taken_pct: -0.02, max_health: 4 }
```

插件岗把上述 stub **落盘**到 `plugins/CoreRpg/` 与 `src/main/resources/`；本任务**不**改 jar、不落 Java。

---

## 7. TrMenu 接线

| 文件 | 打开 | 说明 |
|------|------|------|
| `ember_hub.yml` | `/ember` | 誓约 / 天赋 → `menu: ember_covenant` / `ember_talent`（**已去掉「即将点燃」**） |
| `ember_covenant.yml` | hub | 三誓约图标；点击 tell + 提示 `/corerpg covenant set …` |
| `ember_talent.yml` | hub | 查看/洗点/代表节点；点击 tell + 提示 `/corerpg talent …` |

热更：TrMenu reload；**无需**重启 Paper（纯菜单）。命令实装后需部署新 CoreRpg.jar（插件岗）。

---

## 8. 验收（RpgBot / 手工）

命令未实装前（菜单岗）：

- [ ] `/ember` → 誓约 lore **无**「即将点燃」；点击打开 `ember_covenant`
- [ ] `/ember` → 天赋同上 → `ember_talent`
- [ ] 选烬刃图标：聊天出现 tell，文案含 `/corerpg covenant set blaze`
- [ ] 返回箭头回到 `ember_hub`

命令落地后（插件岗）：

- [ ] 新号 `covenant set blaze` → 免费成功；再 `set ash` → 扣费或拒（无钻无券）
- [ ] 未选誓约 `talent unlock …` → 拒
- [ ] `talent unlock blaze_root` → 扣 1 点；前置未点拒
- [ ] `talent reset` 首日免费；第二次要钻/券
- [ ] 洗誓约后 `talentNodes` 清空

---

## 9. NI 预留（物品岗可选）

| NI ID | 显示名 | 用途 |
|-------|--------|------|
| `mat_ember_covenant_reset` | 誓约重置券 | 洗约免晶钻 |
| `mat_ember_talent_reset` | 天赋重置券 | 额外洗点 |

产出：商城便利柜限购、战令、活跃箱；**不**进挂机 MM 掉落。

---

## 10. 明确不做（本期）

- 不改 Paper / 不建 jar  
- 不接 AttributePlus 实伤（可只同步 lore）  
- 不实现 MM skill 实体效果（只存 `skill_id`）  
- 不改日周本 / 强化镶嵌已有命令
