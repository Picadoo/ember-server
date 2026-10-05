# STATUS · Affix Pack 5 旋光 / 火链（D196 / CoreRpg 1.65.34）

- **线上：** 1.65.34 / bv57，PID 1527320，2026-10-05 11:40 起；发布凭证 `docs/status/RELEASE-ember-1.65.34.md`。
- **内容：** 词缀池 10 → 12。旋光（D3 Arcane Enchanted）：精英脚下光束预览 1.5 秒后 3 秒转半圈，每次每人最多中 1 下；火链（D3 Fire Chains）：精英 ↔ 同房最近活怪的火链，碰到 0.3×atk、每人每秒最多 1 次，伙伴死了 1.2 秒烟线后换人。奖励 / 闸门 / 掉落不变。
- **模拟：** `tools/p1sim/affixpack5.py` Part A MAX_ABS_DPP 0.8、压力 ≤ 0.4（比毒十字）、Part B p2econ 3.0 → within range（cap 3.0）。
- **测试：** 单测 300/0；冒烟按政策合批（当前未测批次：1.65.32 D194、1.65.33 D195、1.65.34 D196）；不碰资产路径 → 不需要 persist-roundtrip。
- **回滚：** `/workspace/backup/CoreRpg-1.65.33-pre-1.65.34.jar` + 恢复 `plugins/CoreRpg/ember-v1-runs.yml` 到 81e1d2a（旧 jar 遇到 arcane / firechain id 会当普通发光精英）。
