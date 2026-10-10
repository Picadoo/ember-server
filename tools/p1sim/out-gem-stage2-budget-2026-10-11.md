# Gem Stage2 p1sim 预算 · 收紧2 PASS · 2026-10-11

**钉：** `out-guard-parry-t0b-rules-export.json` · n=1000 · `/tmp/sk/gem_s2c.pkl`  
**批：** 总控续做 · 方案 M 收紧2

## 定稿（可接线）

| 石 | 值 | 帽 | 附加 |
|----|----|-----|------|
| sharp | **+0.1 A / 石** | gemFlatA ≤ **0.2** | **仅刃 T2+ 计分**（T0/T1 可镶无战斗） |
| steady | **+0.1 D / 石** | gemFlatD ≤ **0.2** | 全程 |
| drain | 1% / 石 | ≤0.05 | 普攻吸血；烬斩不吃 |
| gale | 2% / 石 | ≤0.08 | refreshP1 移速 |

## 剖面判定摘要

| 候选 | burst | scorch | sustain |
|------|-------|--------|---------|
| G1_sharp2 A+0.2 | 🟡 | ✅ | ✅ |
| G1_sharp3 A+0.3 | ❌ | ✅ | ✅ → **否决满帽 0.3** |
| G2_steady1 D+0.1 | ✅ | ✅ | ✅ |
| G2_steady3 D+0.3 | 🟡 | ❌ | 🟡 → **帽收 0.2** |
| G5_full A+0.2 D+0.1 | 🟡 | ✅ | ✅ |
| G1_t2only A+0.2 | ✅ | ✅ | ✅ → **锋利 T2+** |

## 复现

```bash
P1SIM_RULES=out-guard-parry-t0b-rules-export.json NPROC=6 \
  python3 -u skillkit.py run 1000 /tmp/sk/gem_s2c.pkl \
  G1_sharp2,G1_sharp3,G2_steady1,G2_steady3,G5_full,G1_t2only
```

*PASS · 接线 EmberGems + Loadout + 吸血/移速 · 永不 legacy socket*
