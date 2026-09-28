# STATUS · B2.48 公会菜单 Open tell 管理口吻 · TrMenu · 薄验收

**日期：** 2026-09-29 05:02 → 05:03 Asia/Shanghai（CST）  
**岗：** 余烬-测试岗执行器  
**依据：** 施工 tip `cd4f6c0` B2.48: update guild menu open tell  
**范围：** `ember_guild.yml` Open Events tell（L22）去「逻辑待 CoreRpg」管理口吻 + 含「点下方即可」；保留 公会/日捐/周盟 Boss  
**禁项：** **禁改配置** · 禁长测/挑刺 · **不宣称 B0.1 已清** · friends/mail 文件头同句本窗不捆 · 勿带 dirty runtime  
**Verdict：** ✅ **PASS**（验收点全过 · ops=`[]`）

---

## 一句话

Open tell L22=`创建公会 · 日捐盟课 · 周盟 Boss，点下方即可。`；`rg '逻辑待 CoreRpg' ember_guild.yml` → **0**；icons/actions 相对 tip 父零漂；hub→盟约 chat 见点下方、无管理口吻；ops=`[]`。

---

## 总评

| # | 验收点 | 结果 |
|---|--------|------|
| 1 | 静态：`rg -n '逻辑待 CoreRpg' …/ember_guild.yml` → **0**；Open tell 含「点下方即可」；保留 公会/日捐/周盟 Boss | **PASS** |
| 2 | friends/mail 文件头仍可有同句（本窗不捆 · 勿判 FAIL） | **PASS**（记证，不捆） |
| 3 | icons/其它 actions 未漂；tip 仅 Open tell ±1 | **PASS** |
| 4 | 目视：hub→盟约 开菜单 chat 无「逻辑待 CoreRpg」 | **PASS** |
| 5 | 本岗未改配置；ops=`[]`；写测报 · commit+push | **PASS** |

---

## 环境

| 项 | 值 |
|----|-----|
| 工作区 | `/workspace/minecraft` |
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |
| 口 | proxy 25565 / login 25566 / play 25567 |
| 施工 tip | `cd4f6c0` B2.48: update guild menu open tell |
| 设计 / 批准 | `6b0e743` / `811dc78` |
| TrMenu | tip 后自动热更 `ember_guild.yml`（04:59:43 CST · 3ms）· live=`server-runtime` 与 `plugins/` diff 空 |
| 账号 | 验收 `OtPqhyv0`（非 OP · Lv.10）· 辅助 `OtOpqhyv0`（临时 OP，已 deop + LP unset） |
| 探针 | `/tmp/b248-guild-open-tell-copy-test.js` · JSON `/tmp/b248-guild-open-tell-copy-test.json`（未入 git） |
| 坐标 | hub (-18.5, 58, 110.5) 全程未变 |

---

## 各点证据

### 1 · 静态

```
L22 - 'tell: §2[盟约] §7创建公会 · 日捐盟课 · 周盟 Boss，点下方即可。'
```

- `rg -n '逻辑待 CoreRpg' plugins/TrMenu/menus/ember_guild.yml` → **0**（exit 1）
- Open tell 含「点下方即可」；保留「创建公会」「日捐盟课」「周盟 Boss」
- tip `cd4f6c0`：仅 `ember_guild.yml` `1 file changed, 1 insertion(+), 1 deletion(-)`  
  （旧「…周盟 Boss。逻辑待 CoreRpg 接线。」→「…周盟 Boss，点下方即可。」）
- 相对 tip 父：Icons / actions（`corerpg guild` / `corerpg guild boss` / 创建·捐献·Boss tell）**零 diff**（本窗未漂）
- live sync：`plugins/` == `server-runtime/plugins/`（diff 空）

### 2 · friends/mail 文件头（不捆）

```
ember_friends.yml:2:# …逻辑待 CoreRpg 接线
ember_mail.yml:2:# …逻辑待 CoreRpg 接线
```

本窗明确不捆 · **勿判 FAIL** · 仅记证。

### 3 · UX 菜单路径 hub→盟约 → Open tell chat

| 步骤 | 结果 |
|------|------|
| `/ember` → hub | 标题「余烬 · 冒险枢纽」 |
| 点「盟约」 | 标题「余烬 · 盟约」· 槽见「创建盟约 / 捐献 · 盟课 / 周盟 Boss / 我的盟约 / 返回主菜单」 |
| 开菜单瞬间 chat | **`盟约 创建公会 · 日捐盟课 · 周盟 Boss，点下方即可。`** · **无**「逻辑待 CoreRpg」 |

（clickWindow 可能报 transaction timeout，属既有 TrMenu/mineflayer 吞事务；**菜单标题已切到「余烬 · 盟约」且 Open tell 已入 chat**，以窗口 dump + chat 为准。）

### 4 · 禁项

| 项 | 证据 | 判定 |
|----|------|------|
| tip 仅 Open tell 1 行 | `git show cd4f6c0 --stat` → 仅 `ember_guild.yml` ±1 | **PASS** |
| icons/actions 未漂 | tip 父 vs 现：除 L22 外全文一致；command 仍在 | **PASS** |
| 本岗未改配置 | 仅写测报 | **PASS** |
| 未进本 / 坐标 hub | (-18.5, 58, 110.5) 前后同 | **PASS** |
| 未宣称 B0.1 | 本报不涉及 B0.1 | **PASS** |
| 不叫挑刺 / 禁长测 | 仅开菜单轻点 | **PASS** |
| 未带 dirty runtime | commit 仅 docs 测报 | **PASS** |
| ops=`[]` | play=`[]` · login=`[]`；临时 OP 已 deop | **PASS** |

---

## 阻塞点

无。

---

## 交付

- 报告：`docs/STATUS-ember-guild-open-tell-copy-test.md`
- 施工 tip SHA：`cd4f6c0`
- ops：`[]`
- push：见本 commit 是否已 push（失败则交总控代推）
