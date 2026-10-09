# 余烬 · 装备页「成套进度」Stage2 四件套叙事诚实（文案补丁规格）

STATUS=**已批 A · 批 M · 含软化 · D354 已落地**（观察期 TrMenu 施工 · 显示 only · 零数值 · **≠关观察** · **不**抢 K3 · **不**改 ×0.97 · **不**重开 D345/D346 `S`/adventure）· 2026-10-09 · 上游 tip `STATUS-ember-next-hard-debt-six-slot-gear-progress-honesty-need-design-2026-10-09.md` · 施工 STATUS `STATUS-ember-six-slot-gear-progress-honesty-d354-2026-10-09.md` · backlog `B-six-slot-gear-progress-honesty`

> **一句话：** D345/D346 已补冒险进度与装备套装格，但同页 **「成套进度」`P`** 仍只写觉醒终点。荐 **方案 M**：+1 行 `%armor_set%`（可选软化终点句）；零数值。

### 0. 证据

| # | 来源 | 缺口 |
|---|------|------|
| E1 | `plugins/TrMenu/menus/ember_p1_gear.yml` 图标 `P`「成套进度」 | lore=`%set_progress%` +「主线的终点 = 觉醒 III」；**无** `%corerpg_p1_armor_set%` |
| E2 | `EmberLoadout.setProgress` | `set_progress` = 刃+护符同族成套，**≠** 四件套护甲计数 |
| E3 | 同文件套装格 `S`（D346） | 已有 `%armor_set%` + 护甲页指针——**进度书未消费** |
| E4 | adventure `K`（D345） | `set_progress` 邻接已有 armor_set——**gear `P` 未对齐** |
| E5 | 本窗菜单扫（排除 hub_legacy） | 可达页 `三套装|后续开放|未上线` **零命中**；`主线的终点` **仅** gear `P`；hub_legacy 不可达后置 |

**玩家感知（≤3）：**
1. 打开装备页点「成套进度」，除刃护符进度外还能看到护甲四件套态。  
2. 未激活不静态谎称 −3%（只读 PAPI）。  
3. 不删觉醒终点说明；不教命令；不改数值。

**本窗不做：** 改倍率/触发、关观察、K3、重开套装格 `S` / adventure / hub / help、动 hub_legacy、附录/薄抽/关闭包/钉盘复述。

---

## §1 方案表（荐 M）

| 方案 | 内容 | 评价 |
|------|------|------|
| **M（荐）** | §2：`P` 在 `set_progress` 后 +1 行 armor_set；可选软化「主线的终点」句；只 TrMenu | **荐** |
| L | M + 改 hub「套装与觉醒」半行 + hub_legacy 全清 | 过厚；hub 已有 PAPI；legacy 不可达 |
| W | 改 ×0.97、关观察、动 set_bonus、重开 D346 `S` | **否决** |

---

## §2 文案补丁规格（方案 M）

### 2.1 `ember_p1_gear.yml` · 「成套进度」`P`

**保留：** name `§6成套进度`；`%corerpg_p1_set_progress%`；T3+10 / 成色卓越 / 追极品 / 深渊称号句；`update: 20`；material book。

**在 `set_progress` 后追加（示例，文案岗可改字）：**
```
- '§f%corerpg_p1_set_progress%'
- '§8护甲四件套：§f%corerpg_p1_armor_set%'   ← 新增；对齐 adventure D345 / 同页 S D346
- ''
- '§8刃·护符终点 = 同族 T3 两件 +9（觉醒 III）'  ← 可选：原「主线的终点」软化，避免读成否认四件套
- '§8T3 两件 +10、成色卓越以上 = 基本毕业'
- '§8之后是追极品（只比卓越多 4% 成长）和深渊层数、称号'
```

**若嫌软化过厚（最小 M）：** 只插 armor_set 一行，**保留**原「主线的终点 = …」不动亦可（总控可勾「最小 / 含软化」）。

**实现注意：**
- `%armor_set%` 已激活分支自带「受伤 −3%」→ **勿**再加静态 `§a受伤 −3%`（对齐 D329/D339/D345/D346）。  
- 标签用「护甲四件套」与 hub/adventure/S 一致。  
- `set_progress` **保留**（刃护符轨）；勿误删或当四件套替代。  
- **不**改 actions（当前无）；**不**改套装格 `S` / 护甲入口 `M`。

### 2.2 不动文件

| 文件 | 本债 |
|------|------|
| `ember_p1_gear.yml` 套装格 `S` / 护甲入口 `M`（D346/D329） | **不重开** |
| `ember_p1_adventure.yml` / hub / help / set / armor（已收） | **不重开** |
| `ember_hub_legacy.yml` | 后置（不可达） |
| jar / ×0.97 / set_bonus / bv / CoreRpg live yml / OPS | 不动 |
| D338 绿出口附录 / D349–D353 关闭包·钉盘·附录 | **不复述为本号主交付** |

### 2.3 验收（施工另号 · 静态+薄抽）

| ID | 步骤 | 预期 | 中止 |
|----|------|------|------|
| P1 | `rg` gear `P` lore | 有 `%corerpg_p1_armor_set%`；仍有 `set_progress` | 仍仅 set_progress + 觉醒终点 |
| P2 | 打开装备页「成套进度」 | 可见护甲四件套态或等价短述 | 空白/谎称未上线 |
| P3 | 未激活态 | 无静态「受伤 −3%」谎称 | 未激活也写 −3% |
| P4 | 开关/bv；同页 `S` / adventure | 本号未改开关；未误改 `S`/adventure | 误拧 / 抢已收轨 |

**绿出口：** P1–P4 PASS。  
**回滚：** 还原 gear `P` 对应行；不影响 Stage2 数值。

### 2.4 岗位

| 角色 | 职责 |
|------|------|
| 策划 | 批 A · 本规格 |
| 菜单 | 批后另号改 TrMenu · reload |
| 测试 | 另号 P1–P4 薄抽（≠ D345/D346/D348 轨） |
| 总控 | 批 A；显示部署可观察期 |

---

## §3 不动与否决

| 项 | 状态 |
|----|------|
| ×0.97 / set_bonus / F / 价表 / jar 玩法 | 不动 |
| 提前关观察 / 部署 K3 / 样本 R / Pack6 / 天赋 / 灰印 | 否决 |
| 附录续写 / 已 PASS 薄抽 / tip 关闭包 / 钉盘复述 | 否决（本号主交付） |
| 重开 D345 adventure / D346 套装格 `S` | 否决（本债只 `P`） |
| hub_legacy | 否决（不可达） |
| 主仓切分支冲 live（D344） | 禁；本债零触及 live yml |

---

## §4 批注栏

- [x] **批 A：采纳方案 M** → 另号按 §2.1 改 `ember_p1_gear`「成套进度」`P`（显示 only）  
  - [ ] 勾选：最小（只 +armor_set 一行）  
  - [x] 勾选：含软化「主线的终点」→「刃·护符终点」  
- [ ] 升级 L（不荐）  
- [ ] 否决 / 改派  

**总控批注：** **批 A · 批 M · 含软化** · 同号 D354 已落地 gear `P`：`set_progress` 后 +1 行 `%corerpg_p1_armor_set%`；「主线的终点」→「刃·护符终点」· `trmenu reload` · **≠关观察** · 不抢 K3 · 未改同页 `S`/`M` / adventure · jar/set_bonus/bv/CoreRpg live yml **未动**

---

## 变更记录

| 日期 | 事件 | 谁 |
|------|------|-----|
| 2026-10-09 | 初稿 · 待批 A · 荐 M · 菜单扫后主交付；D353 后玩家面残留 | 策划执行手 |
| 2026-10-09 | 批 A·M · **含软化** · D354 落地 gear `P` +armor_set + 终点句软化 · tip 关 · ≠关观察 | 总控委派 executor |

*gear「成套进度」Stage2 叙事诚实 · **已批 A·M · 含软化 · D354 已落地** · 显示 only · 零数值 · ≠关观察 · 不抢 K3。*
