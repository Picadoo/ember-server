# STATUS · R1-sim 徽记高图周来源 what-if（D226，2026-10-06）

> **不改 CoreRpg、不 bump bv57、不动 live。** 默认 `insignia.py` 无 `--whatif` = D222 baseline。

## 做了什么

- `tools/p1sim/insignia.py`：临时 what-if（`--whatif A|B|C` / `--echo-mode` / `--swap-ratio` / `--swap-weekly`）。
  - **A**：S18 → 已首通最高签名图（同 S23）。
  - **B**：S18 图池扩到 q01–q07，共用现网周领奖 3 次；仍结余最少优先。
  - **C**：每周最多 M 次、N 枚低图 → 1 枚高图（sim 默认 N=5、M=2；非 live 数）。
- W30 报告（40 玩家 × 0.3/0.5/0.7 × rot+abyssg，`--signin`，誓约 0，seed0=5000）：
  - `tools/p1sim/out-insignia-r1-A-w30.md`
  - `tools/p1sim/out-insignia-r1-B-w30.md`
  - `tools/p1sim/out-insignia-r1-C-w30.md`
  - 对照 + 推荐：`tools/p1sim/out-insignia-r1-compare.md`
- REG 文案 only：`REG-ember-source-sink-cap` S09 / S23 / C12 对齐代码事实（Q06 门槛、最高图发放、`mapCleared`）；**数量与发放逻辑未改**。
- 设计笔记 §9 更新为 D226 结果。

## 推荐（设计，待业主确认）

**选项 B。** 不增加每周徽记总量；熟练档 W12 调律中位 6、两槽 Q07 100%、Q05–Q06 有周流入。A 把 Q07 喂到 176 却调律仍 3；C 最终也能到调律 6，但 W12 慢且需新兑换口。

## Live

未部署、未改 jar、CoreRpg 仍 **1.65.52 / D224 / bv57**。

## 下一步

业主确认 B → R1-yml/code/gate（另窗）。C15 仍暂停。ARCH S3/S4 需改遭遇/装备结构源码，本窗未开。
