# STATUS · B0.3 菜单去指令 · 独立短抽检

- **时间：** 2026-09-28 04:32（Asia/Shanghai）
- **执行：** 余烬-测试岗执行器
- **依据：** `docs/STATUS-ember-b03-menu-no-cmd.md` · `docs/design-ember-b03-menu-no-cmd.md`
- **Verdict：** **✅ PASS**（硬条全绿）
- **未改：** YAML / jar / 体力数值 / DP；仅临时 OP + `/trmenu reload` + 点按抽检
- **JSON：** `/tmp/b03-menu-test.json`

---

## 环境

| 项 | 值 |
|----|-----|
| 路径 | `/workspace/minecraft` |
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |
| 端口 | 25565 proxy / 25566 login / 25567 play |
| 登录 | `mineflayer-tests/lib/proxy-login.js` |
| 菜单目录 | `server-runtime/plugins/TrMenu/menus/`（symlink → `plugins/TrMenu/menus`） |
| 临时 OP | `B03OpA1`（ops.json 离线 UUID 写入后 **play 重启** 生效；测后 deop） |
| 验收号 | `B03p3413`（非 OP） |
| 热重载 | `/trmenu reload` → `[TrMenu] FINE \| 30 menus were loaded (76 ms)` |

> 债注：play 为 `nogui` 且 stdin=/dev/null、无 rcon；写入 `ops.json` **不会**热生效，需重启 play 才能加载临时 OP（本轮已重启一次）。`ember_weekly.yml` 启动仍 YAML 解析失败（表外，B0.3 未改周本菜单）。

---

## 硬条总表

| # | 硬条 | 结果 | 证据要点 |
|---|------|------|----------|
| 1 | 玩家可见 lore/tell **无** `/corerpg` `/dp` `/hub` `/mvtp` 教学 | ✅ **PASS** | 静态 16 文件 hits=**0**；进服窗口 lore hits=[]；会话无「建议：/…」「请执行 /…」教学 tell |
| 2 | 点按仍开功能（菜单 / 设置切换 / 誓约选定有反馈） | ✅ **PASS** | 悬赏/签到有 CoreRpg 反馈；仓库·竞技·设置·誓约·角色等均打开；设置三钮 → OFF/ON；誓约选定灰行 |
| 3 | 天赋菜单 **无** 玩家可见「管理：/corerpg…」 | ✅ **PASS** | 静态 mgmtLines=0；live 天赋窗 sample 无「管理」+corerpg |
| 4 | 测后 **ops=[]**（play+login） | ✅ **PASS** | `ops_final.play=[]` · `ops_final.login=[]` |
| 5 | 勿改 YAML/jar | ✅ **PASS** | 测试岗仅读菜单 + 临时 OP/reload/点按 |

旁证：`actions.command` 仍保留（settings=3 · covenant=4 · hub=8 · talent=15）。

---

## 路径明细

### 0 · 静态扫（16 目标 yml）

| 结果 | 说明 |
|------|------|
| ✅ PASS | `ember_hub/settings/covenant/arena/character/storage/life/shop/pass/mail/auction/friends/guild/pet/set/talent` 非注释、非 `command:` 行 **无** 斜杠教学；天赋无「管理：/corerpg」 |

### 1 · 枢纽：悬赏 · 签到 · 仓库 · 竞技

| 项 | 结果 | 证据 |
|----|------|------|
| `/ember` 打开枢纽 | ✅ | title=`余烬 · 冒险枢纽` · slots=43 |
| lore 无斜杠教学 | ✅ | 悬赏「每日自动分配短目标」· 签到「按上海日历日」· 仓库「点此打开」· 竞技「点此打开 1v1/2v2」 |
| 点悬赏 | ✅ | `[余烬] 今日悬赏：清剿亡灵` · 目标 30 · 奖励 80 |
| 点签到 | ✅ | `签到成功！获得余烬币 ×100` |
| 点仓库 | ✅ | title=`余烬 · 仓库` · tell「材料仓…」 |
| 点竞技 | ✅ | title=`余烬 · 竞技` · tell「1v1 / 2v2…」 |

### 2 · 设置三钮（音效 / 提示 / 隐私）

| 项 | 结果 | 证据 |
|----|------|------|
| 打开设置 | ✅ | title=`余烬 · 设置`；lore「点击切换音效/提示/隐私」（无 `/corerpg settings …`） |
| 音效 | ✅ | tell「已切换」→ `[设置] 音效 → OFF` |
| 提示 | ✅ | tell「已切换」→ `聊天提示 → OFF` |
| 隐私 | ✅ | tell「已切换」→ `隐私 → ON` |

### 3 · 誓约（三选仍能设）

| 项 | 结果 | 证据 |
|----|------|------|
| 打开誓约 | ✅ | title=`余烬 · 誓约`；lore 无斜杠 |
| 点灰行 | ✅ | tell「已选定 · 灰行」+ `[誓约] 已选定：灰行（ash）`（首次免费） |

### 4 · 角色 / 仓库 / 天赋 / 好友 / 盟约 / 邮寄 / 寄售 / 使魔 / 商城月卡

| 菜单 | 打开 | lore/tell 无教学 | 备注 |
|------|------|------------------|------|
| 角色 | ✅ `余烬 · 角色` | ✅ | |
| 仓库 | ✅ `余烬 · 仓库` | ✅ | 与路径1一致 |
| 天赋 | ✅ `余烬 · 天赋` | ✅ | **无「管理：/corerpg」** |
| 好友 | ✅ `余烬 · 好友` | ✅ | |
| 盟约 | ✅ `余烬 · 盟约` | ✅ | |
| 邮寄 | ✅ `余烬 · 邮寄` | ✅ | |
| 寄售 | ✅ `余烬 · 寄售` | ✅ | |
| 使魔 | ✅ `余烬 · 使魔` | ✅ | |
| 商城月卡 | ✅ `余烬 · 商城` | ✅ | 月卡 lore「限购 1 · 点击购买月卡」（无 `/corerpg monthly buy`） |

---

## ops 终态

- `server-runtime/ops.json` = **`[]`**
- `login-runtime/ops.json` = **`[]`**
- `B03OpA1`：`/deop` + LP `corerpg.admin` unset

---

## 债 / 范围外

| 项 | 说明 |
|----|------|
| `ember_weekly.yml` 载入失败 | 启动日志仍报 YAML 解析错（Lv.20 tell 引号）；**非本单**；30 菜单已加载含 B0.3 目标 |
| 临时 OP 需重启 play | nogui 无 stdin/rcon；仅写 ops.json 不热生效 |
| 进本主路径 / 体力 / DP | 本单未测、未改 |

---

## 回报主代理 / 总控

- **总评：** **PASS**
- **路径：** 1 枢纽 PASS · 2 设置 PASS · 3 誓约 PASS · 4 养成/社交/商城 PASS
- **硬条：** lore/tell 无斜杠教学 · 点按有效 · 天赋无「管理：」 · ops=[] · 未改 YAML/jar
- **报告：** `docs/STATUS-ember-b03-menu-no-cmd-test.md`
- **JSON：** `/tmp/b03-menu-test.json`
