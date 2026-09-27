# B0.3 · TrMenu 玩家可见去指令替换表

- 日期：2026-09-28（Asia/Shanghai）
- 设计：余烬-策划 · 仅文档，**不改 YAML**（落地派插件）
- 范围：`plugins/TrMenu/menus/ember_*.yml` 中 **lore / tell / Open 提示** 仍含 `/corerpg` `/dp` `/hub` `/mvtp` 或「建议执行…」的玩家可见句
- **不改：** 体力数值；底层 `actions` 里的 `command:`（可保留，勿写入玩家可见文案）；YAML `#` 注释可留命令备忘
- **进本主路径：** 日常三线等已 PASS，本稿不重复扫本类；若日后回潮按同规则改
- **验收硬条：** 养成 / 设置 / 枢纽 / 社交相关菜单，玩家可见句 **无** 上述斜杠命令教学；管理测试钮须标 **「仅管理/测试」** 或移出玩家布局

**替换原则：** 「点击即可 / 打开××」；不写斜杠；参数类（好友名、寄售价）改「点此后按提示操作」或后续做输入 UI，勿教完整命令字。

---

## P0 · 枢纽 / 设置 / 誓约 / 镶嵌·强化壳 / 竞技（优先）

### `ember_hub.yml`

| 位置（约） | 旧句（玩家可见） | 新句 |
|------------|------------------|------|
| 悬赏 lore | `§8日自动分配 · /corerpg bounty` | `§8每日自动分配短目标` |
| 签到 lore | `§8Asia/Shanghai 日历日 · /corerpg sign` | `§8按上海日历日，每日一次` |
| 仓库 lore | `§8材料仓 · /corerpg warehouse` | `§8材料仓 · 点此打开` |
| 竞技 lore | `§8/corerpg arena queue 1v1\|2v2` | `§8点此打开 1v1 / 2v2 匹配` |

### `ember_settings.yml`

| 位置 | 旧句 | 新句 |
|------|------|------|
| 音效 lore | `§8点击切换：/corerpg settings sound` | `§8点击切换音效` |
| 音效 tell | `§e[设置·音效] §7建议：§f/corerpg settings sound` | `§e[设置·音效] §7已切换（或：点击即可切换）` |
| 提示 lore | `§8点击切换：/corerpg settings tip` | `§8点击切换提示` |
| 提示 tell | `…建议：§f/corerpg settings tip` | `§b[设置·提示] §7已切换` |
| 隐私 lore | `§8点击切换：/corerpg settings privacy` | `§8点击切换隐私` |
| 隐私 tell | `…建议：§f/corerpg settings privacy` | `§d[设置·隐私] §7已切换` |

### `ember_covenant.yml`

| 位置 | 旧句 | 新句 |
|------|------|------|
| 烬刃 tell×2 | `建议执行：/corerpg covenant set blaze` + `已接线 · /corerpg covenant` | `§6[誓约] §7已选定 · 烬刃`（可再加一句 `§8打开枢纽菜单 → 技能 查看`） |
| 灰行 tell×2 | `…set ash` + `…/corerpg covenant` | `§7[誓约] §7已选定 · 灰行` |
| 守墓 tell×2 | `…set warden` + 同上 | `§9[誓约] §7已选定 · 守墓` |

> 底层 `command: corerpg covenant set …` **保留**。

### `ember_socket.yml` / `ember_enhance.yml`

玩家可见 tell 已偏「请手持…」——**保持**；文件头 `#` 注释可留。  
若仍有 tell 复述完整 `/corerpg socket|enhance …`：**删除该 tell 行**，只留「点击即可 / 请确认手持余烬装备」。

| 文件 | 规则 |
|------|------|
| socket / enhance | lore 已是「点击查看/镶入/强化」→ **PASS**；勿新增命令句 |
| 任意残留 `建议：/corerpg socket…` | 改为 `§7点击即可` |

### `ember_arena.yml`

| 位置 | 旧句 | 新句 |
|------|------|------|
| Open tell | `…1v1 / 2v2 … · /corerpg arena\|pvp` | `§c[竞技] §71v1 / 2v2 积分赛。点下方排队或离开。` |
| 1v1 tell | `建议：/corerpg arena queue 1v1` | `§a[竞技] §7正在排队 1v1…` |
| 2v2 tell | `…queue 2v2` | `§a[竞技] §7正在排队 2v2…` |
| 离开 tell | `建议：/corerpg arena leave` | `§7[竞技] §7已离开队列` |

---

## P1 · 养成 / 角色 / 仓库 / 生活 / 商城

### `ember_character.yml`

| 旧句 | 新句 |
|------|------|
| Open：`点图标执行 /corerpg 查询` | `§b[角色] §7等级、誓约与钱包摘要。点下方查看。` |
| `请执行 /corerpg status` | `§b[角色·状态] §7已刷新摘要`（或关掉 tell，仅靠 lore 占位） |
| `请执行 /corerpg covenant` | `§6[角色·誓约] §7打开誓约页可重选（有冷却则以实际为准）` → 若按钮本意是开菜单：改 `menu: ember_covenant`，tell 写 `§7已打开誓约` |
| lore：`软通货另查 /corerpg coin` | `§8余烬币见钱包行` |
| `请执行 /corerpg cash` · `/corerpg coin` | `§a[角色·晶钻] §7余额已显示在本页` |
| `请执行 /corerpg monthly` | `§e[角色·月卡] §7状态见本页；续费请打开商城` |

### `ember_storage.yml`

| 旧句 | 新句 |
|------|------|
| `§e➥ §f/corerpg warehouse` | `§e➥ §f点击打开仓库` |
| tell：`取出：/corerpg warehouse withdraw …` | `§8[仓库] §7点击格位取出；或用本页取出按钮` |
| `也可：/corerpg warehouse unlock cash` | `§8也可在本页解锁格位（晶钻）` |

### `ember_life.yml`

| 旧句 | 新句 |
|------|------|
| `§8手持后 /corerpg pet 召唤`（两处孵化 lore） | `§8孵化后打开使魔菜单出战` |

### `ember_shop.yml`

| 旧句 | 新句 |
|------|------|
| lore：`§8限购 1 · /corerpg monthly buy` | `§8限购 1 · 点击购买月卡` |

（文件头注释可留 `/corerpg coin` 备忘，不算玩家可见。）

### `ember_pass.yml`

| 旧句 | 新句 |
|------|------|
| tell：`打开邮寄菜单 /corerpg mail` | `§8若未自动入账，打开枢纽 → 邮寄 领取` |
| lore：`§e➥ §f/corerpg mail` | `§e➥ §f打开邮寄` |

---

## P1 · 社交 / 邮寄 / 寄售 / 使魔

### `ember_mail.yml`

| 旧句 | 新句 |
|------|------|
| `建议：/corerpg mail claim all` | `§a[邮寄] §7正在领取全部…` |
| `建议：/corerpg mail delete <id>` | `§7[邮寄] §7点击邮件清理；无需手打编号` |

### `ember_auction.yml`

| 旧句 | 新句 |
|------|------|
| `建议：/corerpg auction list` | `§a[寄售] §7正在刷新列表…` |
| `建议：/corerpg auction sell <价>` | `§6[寄售] §7手持物品后点上架，按提示定价`（若仍无 UI：tell 写「功能筹备中」，**勿**教命令） |

### `ember_friends.yml`

| 旧句 | 新句 |
|------|------|
| Open：`/corerpg friend add <名>` | `§a[好友] §7好友与组队。点下方申请 / 邀请。` |
| `建议：/corerpg friend add <名>` | `§a[好友] §7点击申请；按提示填写名称` |
| `删除：/corerpg friend remove <名>` | `§8删除：在列表点对应好友` |
| `…friend invite <名>` | `§b[好友] §7点击邀请组队` |
| `…friend mentor <名>` | `§6[好友] §7点击发起师徒` |

### `ember_guild.yml`

| 旧句 | 新句 |
|------|------|
| `建议：/corerpg guild create <名>` | `§a[盟约] §7点击创建；按提示填写名称` |
| `…guild donate mat_ember_shard 10` | `§6[盟约] §7点击捐献今日盟课` |
| `…guild boss` | `§c[盟约] §7点击前往盟 Boss` |

### `ember_pet.yml`

| 旧句 | 新句 |
|------|------|
| lore：`/corerpg pet list` | `§8点击查看已拥有使魔` |
| lore：`/corerpg pet summon` | `§8点击出战` |
| tell：`建议：/corerpg pet summon <id>（未实装）` | `§a[使魔] §7出战（未实装则：§8功能筹备中）` |
| lore：`/corerpg pet feed` | `§8点击投喂` |
| tell：`…/corerpg pet feed [数量]` | `§6[使魔] §7投喂魂尘升级` |

---

## P2 · 天赋「管理：」行（玩家可见须处理）

`ember_talent.yml` 多处 lore：`§8管理：/corerpg talent…`——**对普通玩家是指令教学**，且按钮本身已是点击解锁。

| 规则 | 做法 |
|------|------|
| 玩家可点的查看 / 解锁 / 洗点 | 删掉 `§8管理：/corerpg …` 行；改为无命令说明，如 `§8点击即可` / 已有「点击下方节点即可解锁」则 **整行删除** |
| 若保留给运维的隐藏钮 | 单独 Icon，lore 首行加 `§c仅管理/测试`，命令可留；**勿**放在默认玩家布局中央 |

节点示例（全部同规则）：

| 旧 | 新 |
|----|----|
| `§8管理：/corerpg talent` | （删除）或 `§8点击查看` |
| `§8管理：/corerpg talent unlock <nodeId>` | （删除） |
| `§8管理：/corerpg talent reset` | （删除）；洗点按钮保留 `§c➥ §f点击洗点` |
| `§8管理：/corerpg talent unlock blaze_root` 等各节点 | （删除） |

---

## P2 · 套装说明

### `ember_set.yml`

| 旧句 | 新句 |
|------|------|
| Open tell 含 `YAML：CoreRpg/set.yml` | `§8击杀微量回能（≤战力 2%），无大额攻击乘区` |
| lore：`§8旋钮：plugins/CoreRpg/set.yml` | `§8具体数值以服内说明为准`（或删除） |
| tell 再提 `CoreRpg/set.yml` | 去掉路径；只留套装效果一句 |

---

## 仅管理 / 测试（保留命令，必须标注）

扫表时 **未发现** 独立「测试进本」钮仍教 `/dp start`（日常主路径已清）。若插件后加管理钮：

| 要求 | 文案 |
|------|------|
| lore 第一行 | `§c仅管理/测试` |
| 可保留 | `command: dp start …` / `corerpg …` |
| 禁止 | 把同一句放进无权限校验的玩家按钮 lore |

---

## 不在本单（已 PASS / 注释）

| 项 | 说明 |
|----|------|
| `ember_daily` / 周 / 深渊 / 团 / 精英 / 灾厄 进本文案 | 主路径已清则跳过；回归时按 P0 原则 |
| `ember_forge.yml` | 已有「勿手打命令」→ 保持 |
| `#` 文件头注释 | 可留 `/corerpg` 备忘 |
| `actions: command:` | 保留接线，不写进 lore/tell |

---

## 建议专岗与验收

| 专岗 | 动作 |
|------|------|
| **余烬-插件** | 按表改 TrMenu YAML；热重载菜单 |
| **余烬-测试** | 打开 hub→设置/誓约/镶嵌/强化/竞技/角色/仓库/天赋/好友/盟约/邮寄/寄售/使魔/商城月卡：截图或笔记确认无斜杠教学 |
| 策划 | 本稿；体力数值勿动 |

**验收：** P0+P1 菜单玩家可见句无 `/corerpg` `/dp` `/hub` `/mvtp`；天赋无「管理：/corerpg」暴露给玩家；管理钮有「仅管理/测试」标。

---

## 一页摘要（总控）

B0.3 替换表：`docs/design-ember-b03-menu-no-cmd.md`。优先清 **hub / settings / covenant / arena** 的 lore·tell，以及 character / storage / life / shop / mail / auction / friends / guild / pet；talent 去掉玩家可见「管理：/命令」；socket·enhance 以「点击即可」为准。不改 YAML、不动体力。派插件改菜单后测。
