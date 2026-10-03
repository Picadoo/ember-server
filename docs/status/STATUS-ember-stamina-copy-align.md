# STATUS · S0 菜单日票假文案对齐 A（插件岗）

- 时间：2026-09-28 22:52（Asia/Shanghai）
- 执行：余烬-插件
- 对批：`004ee51` · 设计 `docs/design/design-ember-stamina-copy-align.md`（方案 A 已批）
- Verdict：**PASS** · 三菜单玩家 lore/tell 无「日票」「日周票」；禁项零 diff；热更成功；**未 push**

---

## 一句话

战令 / 角色 / 寄售玩家可见「日票 / 日周票」假文案已对齐商城与 `consumable_ember_stamina_30` 实发口径（体力药 / 体力）；可选 shop 头注释已改；未动价、限购、奖励表、quest、体力数值。

---

## 改了哪些句

| 文件 | 位 | 旧 → 新 |
|------|----|---------|
| `ember_pass.yml` U lore | `外观、币、日票 ×5（6/12/18/24/30 级）` | `外观、币、体力药 ×5（6/12/18/24/30 级）` |
| `ember_pass.yml` I tell | `付费轨外观+券+票，无核心` | `付费轨外观+券+体力药，无核心` |
| `ember_pass.yml` L lore | `… / 日票` | `… / 体力药` |
| `ember_pass.yml` L lore | `另有日票×5、保护券、孔石` | `另有体力药×5、保护券、孔石` |
| `ember_character.yml` S lore | `挂机 / 日周票 / 签到一览` | `挂机 / 体力 / 签到一览` |
| `ember_character.yml` $ lore | `晶钻余额 · 日票硬顶` | `晶钻余额 · 体力药日限` |
| `ember_auction.yml` 税 tell | `绑定物、日周票、邮件附件不可上架` | `绑定物、票券 / 体力药、邮件附件不可上架` |
| `ember_shop.yml` 头注释（可选） | `在售：…日票、周票…` | `在售：…体力药、周体力包（SKU 别名仍 daily_ticket/weekly_ticket）…` |

路径：`plugins/TrMenu/menus/`（与 `server-runtime/plugins/TrMenu/menus/` **同 inode**，改一处两边一致）。

auction lore「§8绑定物 / 票券不可上架」保留（设计允许）。

---

## 验收

| 项 | 结果 |
|----|------|
| 三文件玩家 lore/tell 无「日票」「日周票」 | ✅ `rg` 空 |
| shop 玩家可见按钮/价 | ✅ **未改**（仅头注释） |
| `command:` / `menu:` 未断 | ✅ |
| 禁项 `progress.yml` / `cash.yml` / `quest.yml` / 体力 cost / MM / loot / DP | ✅ **git diff EMPTY** |
| 热更 | ✅ 见下 |
| git push | ✅ **未执行** |

---

## 热更证据

- ConfigWatcher 自动：`[22:51:42] [TrMenu] 良好 | 自动重新载入菜单 ember_pass.yml / ember_character.yml / ember_auction.yml / ember_shop.yml`
- 显式：`printf 'trmenu reload\n' > server-runtime/console.in` → `[22:51:53] [TrMenu] 良好 | 36 个菜单已加载 (30 ms)`（`server-runtime/logs/latest.log`）

---

## 禁项 / 未做

- 未改 `progress.yml` 奖励表、`cash.yml` 数值、体力 cost/进本、`quest.yml`、MM/loot/DP、精英预览壳、商城价/限购/奖励表、教斜杠。
- 主线灰烛/hint「日票」台词仍挂（方案 B，本窗不宣称已清）。

---

## Git

- `git add`：本次四 TrMenu + 本 STATUS
- `git commit`：本 STATUS 落盘后提交
- **禁止 push**（遵守）
