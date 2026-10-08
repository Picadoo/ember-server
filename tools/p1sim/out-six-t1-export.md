# D318 六槽 T1-2 · Java ↔ p1sim 同参对拍（2026-10-08 09:33 本地，离线）

- 导出：`python3 six_t1_export.py OUT.tsv.gz`（F 档：follow all · w = [0.8, 0.05×4] · charm cost ×1.0；表来自 p1config.load() = 线上 yml + Java 常量）；python3 3.13.5；2–3 s，≈1.9 MB gz（不入库，Java 测试缺文件时在 CoreRpg/target/ 现生成）。
- 格子：g6 = T0″ G6 同一网格 101,376（甲 = 护符成色 / 精工）；var = 同网格 101,376，甲成色 / 精工逐格随机 0–3（常高于护符）、约 1/5 空甲位（Java 给 null，p1sim 当 q0 f0）。无护符 101,376 为 Java 侧（p1sim 没有"无护符"状态）：六槽 == 2 槽逐位。
- 比较：`EmberSixP1simTest`，B / H / M / D 用 `Double.doubleToLongBits` **逐位相等，无容差**。
- 结果：g6 101,376 · var 101,376 · **mismatches = 0**（B 0 / H 0 / M 0 / D 0）；无护符 0 差。本次导出 sha256 前缀 `0a739d286e0a25e0`。
- 过程记录：第一次跑 var 臂 H 有 176 / 101,376 格差 1 ulp——原因是 CPython ≥ 3.12 的 `sum()` 对浮点用 Neumaier 补偿求和，而 Java 原写法是朴素累加；改为 `EmberFormula.pySum`（逐行复刻 CPython 的 sum 算法）后 0 差。迁移不变量（差项全 0.0）两种写法都精确为 0.0，T1-1 / G6 不受影响。
- 白板经济模型未覆盖（本页只对拍生命 / 防御公式）。
