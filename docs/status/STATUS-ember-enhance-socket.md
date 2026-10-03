# STATUS · 余烬强化 + 镶嵌

**日期：** 2026-09-12（Asia/Shanghai）  
**版本：** CoreRpg **1.2.0**  
**规格：** `docs/design/DESIGN-ember-enhance-socket.md`

## 已落地

- `plugins/CoreRpg/enhance.yml` + `src/main/resources/enhance.yml`（§6 形状：levels/chance/fail/costs、stat_per_level、socket.unlock_at、gems）
- Lore：`§8强化 §f+N` + `§8§o#ember_en:N`；孔 `§8孔N: …` + `§8§o#ember_sk:`
- 命令：`/corerpg enhance` · `enhance info` · `socket list` · `socket insert <gemId>` · `socket remove <slot>`
- 管理测试：`/corerpg enhance set <0-10>`（`corerpg.admin`；无玩家直达 +10）
- NI：`mat_ember_protect_scroll` / `mat_ember_stable_charm` / `gem_ember_sharp|steady|drain|gale`
- 孔解锁：刃 孔1@+0、孔2@+5；护符 孔1@+3（max 1）
- 币/签到/活跃/悬赏/记分板/刷怪 **保留**；`config.yml` 未删旧键
- `_golden/` 已快照 jar+src

## 重启

**必须重启 Paper**（新类，不可热重载生效）。

## 未改

Paper / MythicMobs / EmberCrypt 掉落 / AttributePlus。

## 验收提示（RpgBot）

见设计 §8：空手 info 拒、+0→+3、保护券失败不掉、刃/护符孔解锁、`remove` 回包。
