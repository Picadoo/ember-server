# STATUS · E3 p1sim 读 `ember-v1-economy.yml`（D225，2026-10-06）

> **不改 CoreRpg、不 bump bv57、不动 live。** 与线上 D224 `amount()` 同真源。

## 做了什么

- `tools/p1sim/rules.py`：`PAIRS['economy']` = plugins + src `ember-v1-economy.yml`；`balance_version` 必须等于 runs；`rules.economy()` / `rules.amount(id, key)`。
- `p1config.py`：`base` / `treasure_coin` / `elite_*` / `marks_per` ← S01 / S02 / S03 / C07；加载后与 Java `BASE_*` / `TREASURE_*` / `ELITE_*` / `MARKS_PER_EXCHANGE` dual-assert，漂移 → `RuleError`。
- `insignia.py`：S07/S08/S17/S18/C12/C13 数量在 `install()` / `_load_amounts()` 从 yml 拉。
- `afk.py`：`install()` 时 `DAILY_KILLS` ← S22 `daily_kills`。
- README 补 E3 用法说明。
- 设计笔记：`docs/design/DESIGN-ember-insignia-weekly-highmap-2026-10-06.md`（D222 发现 → R1 三选项 + re-sim 闸 + REG 文案差）。

## 验收

- `python3 tools/p1sim/p2econ.py --players 8 --weeks 4`：除 `# rules sha256 …` 戳外，表体与改前 **逐位相同**（yml = 金样）。
- `selfcheck.py`：与改前同为 2 个既有 FAIL（codex 预警文案、signin 直读 yml），无新增回归。
- Live：未部署、未重启、未改 jar。

## 下一步

- R1-sim（徽记高图周来源 what-if）按设计笔记；REG S09/S23/C12 文案重写（文档窗）。
- C15 化妆品仍暂停。
