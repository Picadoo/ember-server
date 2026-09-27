# STATUS · B0.3 TrMenu 玩家可见去指令

- **时间：** 2026-09-28 04:22（Asia/Shanghai）
- **执行：** 余烬-插件岗执行器
- **依据：** `docs/design-ember-b03-menu-no-cmd.md`（替换表）
- **Verdict：** **✅ PASS**（按表改完；玩家可见 lore/tell 无 `/corerpg` `/dp` `/hub` `/mvtp` 教学；`actions.command` 保留）
- **未改：** 体力数值、进本费用、DP 配置；YAML `#` 注释中的命令备忘可留
- **Git：** **未** commit / push；**未**动 jar

---

## 一句话

按 B0.3 替换表清掉养成/设置/枢纽/社交等菜单里「教玩家打斜杠命令」的 lore/tell；底层 `command:` 接线不断；天赋去掉玩家可见「管理：/corerpg…」；灾厄测试钮原有「仅管理/测试」标保持。

---

## 改动菜单列表（16）

| 文件 | 摘要 |
|------|------|
| `ember_hub.yml` | 悬赏/签到/仓库/竞技 lore 去 `/corerpg` |
| `ember_settings.yml` | 音效/提示/隐私 lore+tell →「点击切换 / 已切换」 |
| `ember_covenant.yml` | 烬刃/灰行/守墓 tell →「已选定」；保留 `covenant set` |
| `ember_arena.yml` | Open/排队/离开 tell 去建议命令 |
| `ember_character.yml` | Open/状态/誓约/钱包/月卡 tell+lore |
| `ember_storage.yml` | 打开/取出/解锁文案去命令 |
| `ember_life.yml` | 孵化 lore「手持后 /corerpg pet」→「打开使魔菜单出战」×2 |
| `ember_shop.yml` | 月卡 lore「/corerpg monthly buy」→「点击购买月卡」 |
| `ember_pass.yml` | 邮寄 tell/lore 去 `/corerpg mail` |
| `ember_mail.yml` | 领取/清理 tell |
| `ember_auction.yml` | 浏览/上架 tell |
| `ember_friends.yml` | Open/申请/删除/组队/师徒 tell |
| `ember_guild.yml` | 创建/捐献/Boss tell；另清 lore「dp start EmberGuildBoss」（表外、验收硬条） |
| `ember_pet.yml` | list/summon/feed lore+tell |
| `ember_set.yml` | Open/旋钮路径去 `CoreRpg/set.yml` |
| `ember_talent.yml` | **删除** 16 行 `§8管理：/corerpg talent…`；洗点仍「点击洗点」+`command` |

**未改（设计已 PASS / 跳过）：** `ember_daily` / weekly / abyss / raid / calamity 进主路径；`ember_forge`；`ember_socket` / `ember_enhance`（已是「点击/请手持」）。

---

## 替换摘要（原则落地）

| 原则 | 落地 |
|------|------|
| 点击即可 / 打开×× | hub·storage·shop·pet·pass 等 |
| 不写斜杠 | lore/tell 已无 `/corerpg` `/dp` `/hub` `/mvtp`（除 `#` 注释） |
| 参数类勿教完整命令 | friends/guild/auction →「按提示填写/定价」 |
| `actions.command` 保留 | 抽检：hub 8 / settings 3 / covenant 4 / arena 4 / talent 15 / … 均在 |
| 管理测试标 | `ember_calamity` 测试实例已有「仅管理/测试」· 未动 |

---

## 同步

- `server-runtime/plugins` → `../plugins`（**symlink**）→ 改 `plugins/TrMenu/menus` 一次即可，无需双写。

---

## 抽检建议（交卷 · 测试短抽）

打开并确认 lore/tell **无斜杠教学**，点击仍有效：

1. 枢纽 → 悬赏 / 签到 / 仓库 / 竞技  
2. 设置三钮（音效/提示/隐私）  
3. 誓约三选  
4. 角色 / 仓库 / 天赋（无「管理：」）/ 好友 / 盟约 / 邮寄 / 寄售 / 使魔 / 商城月卡  

热重载：`/trmenu reload`（或等价）。

---

## 给总控

- **priority: true** · B0.3 结案交卷  
- 请派 **余烬-测试** 短抽检上表 4 条路径  
- S0 体力药已结案，本单未触体力/进本费/DP  
