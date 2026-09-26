# 灾厄入口文案 · Multiverse `ember_event`

**日期：** 2026-09-13（Asia/Shanghai）  
**依据：** `DESIGN-ember-growth-curve.md` §4  
**分工：** 公共场 = MV；Boss 技能回归测 = 仍保留 `/dp start EmberCalamity`。

---

## 1. 双入口策略

| 入口 | 命令 | 谁用 | 文案语气 |
|------|------|------|----------|
| **主（玩家）** | `/mvtp ember_event` | 窗内奔赴公共祭坛 | 世界事件、集合 |
| **测（工 / OP）** | `/dp start EmberCalamity` | 测 Boss 波次与 ni give | 标明「测试实例」 |
| 菜单主按钮 | 接 `mvtp ember_event` | 全员 | 无「即将点燃」 |
| 菜单次按钮 | `dp start EmberCalamity` | lore 写「仅测试」 | 灰色提示 |

窗外主按钮：`tell` 下一窗，**不** `mvtp`（或 tp 后世界内提示未开放——优先菜单侧拦截）。

---

## 2. 主界面 · 灾厄 lore（替换用）

```yaml
name: '§4灾厄'
lore:
  - ''
  - '§7限时公共世界事件 · 灰烬巨像'
  - '§8世界 §fember_event §8· 日 2～3 窗'
  - '§8每日宝箱限 1 次 · 产灾厄余烬/外观'
  - '§e➥ §f打开灾厄菜单'
actions:
  all:
    - 'sound: BLOCK_NOTE_PLING-1-2'
    - 'menu: ember_calamity'
```

---

## 3. 子菜单 `ember_calamity` 文案

### 奔赴灾厄（主）

```yaml
name: '§a奔赴灾厄'
lore:
  - ''
  - '§7传送至公共世界 §fember_event'
  - '§7仅在开放窗口内有Boss与领奖'
  - '§8建议窗：12:00 / 20:00 / 22:00（上海）'
  - ''
  - '§e➥ §f/mvtp ember_event'
actions:
  - 'command: mvtp ember_event'
  - 'tell: §4已送往灾厄祭坛。若未开窗，请等待下一时刻。'
  - close
```

### 奖励预览

```yaml
name: '§6奖励预览'
lore:
  - ''
  - '§7参战：核心、§4灾厄余烬§7、外观碎片'
  - '§7日箱：核心×2 + 附魔晶×1 + 余烬×2'
  - '§7低概率 §cT3 §7刃/护符（见掉落表）'
  - '§8docs/ember-gear-drop-t0-t3.md'
```

### 窗口说明

```yaml
name: '§e窗口说明'
lore:
  - ''
  - '§7公共场：全服同图可见'
  - '§7可多窗参战，§f日箱只领 1 次'
  - '§7测试Boss实例：§8/dp start EmberCalamity'
```

### 测试实例（次要格，可选）

```yaml
name: '§8测试实例（OP）'
lore:
  - ''
  - '§8DungeonPlus 隔离本，用于调Boss'
  - '§8正式游玩请用「奔赴灾厄」'
  - '§e➥ §f/dp start EmberCalamity'
actions:
  - 'command: dp start EmberCalamity'
  - close
```

---

## 4. 世界内告示 / 全息（给地图岗）

| 位置 | 文案 |
|------|------|
| 出生牌 | `§4余烬灾厄祭坛` / `§7窗口内击败灰烬巨像` / `§8日箱限领一次` |
| 未开窗 | `§c灾厄沉睡中` / `§7下一窗请看公告或菜单` |
| 开窗 | `§a灾厄进行中！` / `§e前往中央祭坛` |
| 回城 | `§7回枢纽：§f/mvtp ember_hub`（世界就绪后） |

---

## 5. 验收

1. 玩家菜单主路径是 **mvtp**，不是误进 DP。  
2. DP 仍可 start，lore 标明测试。  
3. 与 `docs/ember-gear-drop-t0-t3.md` 灾厄段一致。
