# D12225 Affix Pack 21 gate summary (spur 刺斩 / fen 沼洼)

- jar: CoreRpg **1.65.743-d12225.local**
- balance_version: **108** (was 107)
- pool: **44** (was 42)
- affix_shard: **2** unchanged

## Part A — stress vs venom (N=150 × seeds 7/11/13)

```
MAX_HARDER_THAN_VENOM_PP (stress) = 2.0 (cap 3.0) → WITHIN RANGE
```

Worst harder-than-venom: q07 dodge 0.5 → −2.0 pp (spur/fen). Full table: `out-affixpack21-d12225-stress.md`.

## Part B — p2econ 44-pool vs 42-pool (80 players × 8 weeks × dodge 0.5)

```
MAX_ABS_DPP (p2econ % columns, new 44 vs old 42 pool) = 0.0 (cap 3.0) → WITHIN RANGE
```

Artifacts: `out-affixpack21-econ-new.md` / `out-affixpack21-econ-old.md`.

## Unit tests (JDK8)

- `varietyPack21SpurFen_D12225` PASS
- `varietyPack20RidgeMarsh_D12224` PASS
- `EmberAffixExportTest` PASS (re-export write)
- `bundledMapsValidateAgainstTheCaps` PASS (bv golden 108)

## Verdict

**WITHIN RANGE — ship**
