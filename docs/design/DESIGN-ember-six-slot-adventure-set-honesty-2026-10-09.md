# 余烬 · 冒险页「我的进度」Stage2 四件套叙事诚实（文案补丁规格）

STATUS=**已批 A · 批 M · D345 已落地**（观察期 TrMenu 施工 · 显示 only · 零数值 · **≠关观察** · **不**抢 K3 · **不**改 ×0.97）· 2026-10-09 · 上游 tip `STATUS-ember-next-hard-debt-six-slot-adventure-set-honesty-need-design-2026-10-09.md` · 施工 STATUS `STATUS-ember-six-slot-adventure-set-honesty-d345-2026-10-09.md`

> **一句话：** D339 hub/help 已诚实，但 **冒险页「我的进度」** 仍只写族觉醒，无护甲四件套态。荐 **方案 M**：补 1 行 `%corerpg_p1_armor_set%`；零数值。

### 0. 证据

| # | 来源 | 缺口 |
|---|------|------|
| E1 | `plugins/TrMenu/menus/ember_p1_adventure.yml` 图标 `K`「我的进度」 | lore 有 `%corerpg_p1_awaken%` / `awaken_next` / `set_progress`；**无** `%corerpg_p1_armor_set%` |
| E2 | `EmberLoadout.setProgress` | `set_progress` = 刃+护符同族成套（同族/T3/T3+9），**≠** 四件套护甲计数 |
| E3 | `EmberSixPapi` `armor_set` | 已诚实三态：先同族 / `族 护甲 n/2 · 需…` / 已激活含「受伤 −3%」 |
| E4 | D339 `ember_hub` 装备格 | 已有 `§8护甲四件套：%corerpg_p1_armor_set%`——**冒险进度格未对齐** |
| E5 | 上游 tip 后置点名 | D342/D344 tip：adventure 仅 awaken；防冲已落，本窗升主交付 |
| E6 | D344 OPS 复核 | §1.5 已含禁主仓切分支 / K3 worktree / skip-worktree——**本债不旁注补 OPS** |

**玩家感知（≤3）：**
1. 打开冒险页「我的进度」即可看到护甲四件套态（与 hub 装备格同口径）。  
2. 未激活不静态谎称 −3%（只读 PAPI）。  
3. 不教命令；不改数值；族觉醒行保留。

**本窗不做：** 改倍率/触发、关观察、K3、重写团本 lore、动 hub/help/set/gear/armor（已收）、改 jar。

---

## §1 方案表（荐 M）

| 方案 | 内容 | 评价 |
|------|------|------|
| **M（荐）** | §2：冒险页 `K` 觉醒块后 +1 行 armor_set PAPI；只 TrMenu；只读镜像 | **荐** |
| L | M + 改写全部团本「三套装不变」句 + challenge/modes/codex 广扫 | 过厚；「三套装不变」可解为规则未改 |
| W | 改 ×0.97、借机关观察/开 K3、动 set_bonus | **否决** |

---

## §2 文案补丁规格（方案 M）

### 2.1 `ember_p1_adventure.yml` · 「我的进度」`K`

**保留：** 目标族 / 印记 / 挑战版 / `%awaken%` / `awaken_next` / `set_progress` / 待领 / 花样说明。

**在「套装：%awaken%」块后追加（示例，文案岗可改字）：**
```
- '§7套装：§f%corerpg_p1_awaken%'
- '§7%corerpg_p1_awaken_next%'
- '§7%corerpg_p1_set_progress%'
- '§8护甲四件套：§f%corerpg_p1_armor_set%'   ← 新增；对齐 hub D339
- '§7暂存待领：§f%corerpg_p1_pending% §7项'
```

**实现注意：**
- `%armor_set%` 已含「受伤 −3%」于**已激活**分支 → **只加一行 PAPI**，勿再静态「受伤 −3%」行（对齐 D329/D339 三态纪律）。  
- 标签用「护甲四件套」与 hub 一致，避免与刃护符「套装/觉醒」混称。  
- `set_progress` **保留**（刃护符成套进度）；勿误删或当四件套替代。

### 2.2 可选同号旁扫（非必须）

| 文件 | 动作 |
|------|------|
| `ember_p1_challenge.yml` / `ember_p1_modes.yml` | 若**无**对称「我的进度」套装块 → **不动** |
| 团本 lore「战斗规则和三套装不变」 | **本债不改**（可选后置软抛光；语义≠否认四件套上线） |
| `ember_p1_codex.yml` 帮助捷径 | **本债不改**（help 正文已 D339） |

### 2.3 不动文件

| 文件 | 本债 |
|------|------|
| `ember_hub.yml` / `ember_help.yml` / `ember_set.yml` / `ember_p1_gear.yml` / `ember_p1_armor.yml` | **不重开**（D339/D331/D329/D336 已收） |
| `ember_hub_legacy.yml` | 不动 |
| jar / ×0.97 / set_bonus / bv / OPS 防冲正文 | 不动（D344 已复核齐全） |

### 2.4 验收（施工另号 · 静态+薄抽）

| ID | 步骤 | 预期 | 中止 |
|----|------|------|------|
| A1 | `rg` adventure `K` lore | 有 `%corerpg_p1_armor_set%`；仍有 awaken/set_progress | 仍仅 awaken 块 |
| A2 | 打开冒险「我的进度」 | 可见护甲四件套态或等价短述 | 空白/谎称未上线 |
| A3 | 未激活态 | 无静态「受伤 −3%」谎称 | 未激活也写 −3% |
| A4 | 开关/bv | 本号未改 | 误拧 set_bonus |

**绿出口：** A1–A4 PASS。  
**回滚：** 还原 adventure 对应行；不影响 Stage2 数值。

### 2.5 岗位

| 角色 | 职责 |
|------|------|
| 策划 | 批 A · 本规格 |
| 菜单 | 批后另号改 TrMenu · reload |
| 测试 | 另号 A1–A4 薄抽 |
| 总控 | 批 A；显示部署可观察期 |

---

## §3 不动与否决

| 项 | 状态 |
|----|------|
| ×0.97 / set_bonus / F / 价表 / jar 玩法 | 不动 |
| 提前关观察 / 部署 K3 / 样本 R / Pack6 / 天赋 / 灰印 | 否决 |
| 重开 D338 签字包当本号关观察 | 否决 |
| 默默改 D169 触发 / 改代码默认 true | 否决 |
| 主仓切分支冲 live（D344 纪律） | 禁；本债零触及 live yml |

---

## §4 批注栏

- [x] **批 A：采纳方案 M** → 另号按 §2.1 改 `ember_p1_adventure`（显示 only）  
- [ ] 升级 L（不荐）  
- [ ] 否决 / 改派  

**总控批注：** **批 A · 批 M** · 同号 D345 已落地 adventure「我的进度」+1 行 `%corerpg_p1_armor_set%` · `trmenu reload` · **≠关观察** · 不抢 K3 · jar/set_bonus/bv **未动**

---

## 变更记录

| 日期 | 事件 | 谁 |
|------|------|-----|
| 2026-10-09 | 初稿 · 待批 A · 荐 M · 上游 D339/D344；OPS 禁切分支已复核无需旁注 | 策划执行手 |
| 2026-10-09 | 批 A·M · D345 落地 adventure K +1 armor_set · tip 关 · ≠关观察 | 总控委派 executor |

*adventure 进度格 Stage2 叙事诚实 · **已批 A·M · D345 已落地** · 显示 only · 零数值 · ≠关观察 · 不抢 K3。*
