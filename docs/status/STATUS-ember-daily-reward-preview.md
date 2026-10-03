# STATUS · 日常奖励预览真分页（方案 1）

**日期：** 2026-09-28（Asia/Shanghai）  
**岗：** 余烬-插件  
**依据：** `docs/design/design-ember-daily-reward-preview.md`（总控已批准方案 1 · 布局键 A/C/I，底栏 B 返回）

## Verdict

**✅ UI 可用 · 短抽 PASS**（测报 `docs/status/STATUS-ember-daily-reward-preview-test.md` · `6e2abc6`）

- 新菜单：`ember_daily_rewards`（Title `§6日常 · 奖励预览`）
- 入口：`ember_daily` 图标 **P** → sound + `menu: ember_daily_rewards`
- 返回：预览底栏 **B** → sound + `menu: ember_daily`（不回 hub）
- 布局：通关箱 **A/C/I** · Boss **D/E/F** · 装备 **X/Y/Z** · 底栏 **B** 专返回
- **未改** 体力 / DP / MM / CoreRpg 掉落与进本

## 落地勾选

- [x] 新建 `plugins/TrMenu/menus/ember_daily_rewards.yml`
- [x] 改 `ember_daily.yml` 仅图标 **P**（保留悬停摘要 + 末行点击提示 + actions）
- [x] 预览图标无 `corerpg enter` / close 进本（除 B 外无 actions）
- [x] Options 对齐日常（Arguments false / Default-Layout 0 / Hide-Player-Inventory false / Min-Click-Delay 200）
- [x] 热重载 TrMenu（见下）
- [x] `ops.json` 保持 `[]`
- [x] git commit+push（`4517021` 落地 · `6e2abc6` 测报）
- [x] 短抽 §7 **PASS**

## 改动文件

| 路径 | 动作 |
|------|------|
| `plugins/TrMenu/menus/ember_daily_rewards.yml` | **新建** |
| `plugins/TrMenu/menus/ember_daily.yml` | **仅 P** |
| `docs/status/STATUS-ember-daily-reward-preview.md` | 本 STATUS |

## Reload

| 项 | 记录 |
|----|------|
| 时间 | **2026-09-28 08:01:38 CST** |
| 命令 | FIFO `server-runtime/console.in` → `trmenu reload` |
| 结果 | `[TrMenu] 良好 \| 32 个菜单已加载 (75 ms)`（较先前 31 +1） |
| ops.json | `[]`（未改） |

## 验收硬条对照（设计 §7）

| # | 硬条 | 自检 |
|---|------|------|
| 1 | 点 P → 打开 `ember_daily_rewards`（有声） | YAML：P actions = sound + menu |
| 2 | 三类分区可见（箱 / Boss / 装） | Layout A C I · D E F · X Y Z |
| 3 | 展示名 + §8 NI id；概率约 12%/8%/3% | lore 已写；**未改**掉落表 |
| 4 | 返回 → `ember_daily`；不误 enter | B → `menu: ember_daily`；预览无 enter |
| 5 | 玩家零 slash | 全程 menu: 跳转 |
| 6 | diff 仅 TrMenu 上述 + STATUS | 无 DP/MM/CoreRpg |

## Git

- commit message：`feat(trmenu): 日常奖励预览真分页 ember_daily_rewards`
- **未 push**

## Blocker

无（本额度 YAML 落地完成；游戏内点按抽检交测试岗）。
