# STATUS · B2.8 DP 进本拒门去斜杠 · 批准 A（总控）

| 字段 | 值 |
|------|-----|
| 时间 | 2026-09-28 23:08 Asia/Shanghai |
| 岗 | 余烬-总控 |
| 批准 | **A** |
| 设计 tip | `83d641a` · `docs/design/design-ember-dp-gate-nocmd.md`（已 push `origin/main`） |
| 下一 | **已结** |
| 状态 | **PASS · 勾销** |

---

## 一句话

**批准 A：** 清理周本、团本、深渊等级拒门，以及盟 Boss 拒门/开场的玩家可见斜杠教学；只做 #1～#5，保持 Lv.20 / Lv.35 / Lv.25 与贡献语义不变。

## 范围（仅 #1～#5）

1. `EmberWeekly/option.yml`：`（/corerpg level 查看）` → ` · 打开枢纽 → 角色查看等级`（Lv.20 保持）。
2. `EmberRaid/option.yml`：同上（Lv.35 保持）。
3. `EmberAbyss/option.yml`：同上（Lv.25 保持）。
4. `EmberGuildBoss/option.yml` 拒门：`需由队长执行 /corerpg guild boss（消耗盟约贡献）开启` → `需由队长在枢纽 → 盟约 →「周盟 Boss」开启（消耗盟约贡献）`。
5. `EmberGuildBoss/option.yml` 开场：`进本由 §e/corerpg guild boss §7门控（贡献消耗）` → `进本由盟约菜单「周盟 Boss」门控（贡献消耗）`。

## 禁项

- **不**改 `$js-condition` / `$team-condition` 的 `text=` 表达式、等级/人数门。
- **不**改 team size、stamina、TrMenu、loot、MM 或进本逻辑。
- **不**做灾厄 OP #6（`EmberCalamity`）；注释里的 `/corerpg` 可留。
- **不**编辑 `option.yml` 于本批准提交；由插件岗另开施工。

## 下一步

插件岗按 #1～#5 施工并回报 tip；随后再派测试。 backlog 已更新为 **PASS · 勾销**。
