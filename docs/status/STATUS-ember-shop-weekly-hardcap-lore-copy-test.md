# STATUS · B2.88 ember_shop.yml 周体力包 lore · 纯静态薄验收

- **总评：PASS**
- **测报时间：**2026-09-30 22:26 Asia/Shanghai（CST）
- **验收岗：**余烬-测试岗执行器
- **工作区：**`/workspace/minecraft`（`git pull` 已执行，Already up to date）
- **依据：**设计 `3ddf5fd` · 批准 `f8a899d` · 施工 tip `45308ff`
- **施工 tip 全 SHA：**`45308ffdab2fc19b3539a833371e9fe6f08d2b2a`
- **范围：**仅 `plugins/TrMenu/menus/ember_shop.yml` 约 L151 周体力包 lore 一行
- **口径：**纯静态薄验收；未开服、未长测、未挑刺、未改配置键值
- **是否已 push：**否（仅本地 commit）
- **阻塞点：**无

## 一句话

`ember_shop.yml` L151 与 tip 荐案逐字一致；`f8a899d..45308ff` 相对批准基线仅周体力包 lore 一行变更，价/命令/tell/其它 lore/文件头零改；目标行及整份 live 文件均无「票已废」「B0.1」「维护备忘」。

## 总评

| # | 验收点 | 结果 |
|---|---|---|
| 1 | L151 与 tip 荐案逐字一致：`'§8限购 1/周 · 周硬顶 免费1+购买≤2'` | **PASS** |
| 2 | 仅该 lore 一行变更；价/命令/tell/其它 lore/文件头相对 `f8a899d..45308ff` 零改 | **PASS** |
| 3 | 该 lore 行无「票已废」「B0.1」「维护备忘」；live 文件无上述违禁词 | **PASS** |

## 各点证据

### 1 · 目标行原文

```yaml
        - '§8限购 1/周 · 周硬顶 免费1+购买≤2'
```

`plugins/TrMenu/menus/ember_shop.yml` L151；与设计 tip §2 荐案逐字一致，保留 `§8`、`限购 1/周`、`周硬顶 免费1+购买≤2`。

### 2 · 施工 diff / 零漂旁证

```text
git show 45308ff --stat
→ plugins/TrMenu/menus/ember_shop.yml | 2 +-（1 file changed, 1 insertion(+), 1 deletion(-)）

git diff f8a899d..45308ff -- plugins/TrMenu/menus/ember_shop.yml
→ 仅 L151 一行：
  - '§8限购 1/周 · 硬顶免费1+氪≤2'
  + '§8限购 1/周 · 周硬顶 免费1+购买≤2'
```

施工 tip 仅触及 `plugins/TrMenu/menus/ember_shop.yml` 的该 lore 行；价 `180`、购买命令 `corerpg shop buy weekly_ticket`、tell、其它 lore、文件头及其它槽位均未改。

### 3 · 违禁核对

```text
rg -n -F "        - '§8限购 1/周 · 周硬顶 免费1+购买≤2'" plugins/TrMenu/menus/ember_shop.yml
→ 151:        - '§8限购 1/周 · 周硬顶 免费1+购买≤2'

rg -n '票已废|B0\.1|维护备忘' plugins/TrMenu/menus/ember_shop.yml
→ 无命中（exit 1）
```

目标 lore 行单独核对同样无「票已废」「B0.1」「维护备忘」。设计 tip 中出现的这些词仅属禁项说明，未进入玩家可见 live lore。

## 纪律确认

- 未起服、未长测、未挑刺、未硬开精英壳。
- 未改配置键值；仅新增本 STATUS 测报文件。
- 脏工作区中与本窗无关的文件未 stage。
- **禁止 push：已遵守；本测报只做本地 commit。**

## 回报摘要（交总控）

| 项 | 值 |
|---|---|
| 总评 | **PASS** |
| 各点 | 1 PASS · 2 PASS · 3 PASS |
| 施工 tip SHA | `45308ff` · `45308ffdab2fc19b3539a833371e9fe6f08d2b2a` |
| 测报 tip short SHA + full | 本文件本地 commit 后回填于最终回报 |
| 是否已 push | **否** |
| 报告路径 | `docs/status/STATUS-ember-shop-weekly-hardcap-lore-copy-test.md` |
| 阻塞点 | 无 |
| 目标行原文 | `'§8限购 1/周 · 周硬顶 免费1+购买≤2'` |
