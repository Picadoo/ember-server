# D436 烬突套装身份 · S0 报告（vs live DS15 对照）

**日期：** 2026-10-10  
**钉：** `P1SIM_RULES=out-guard-parry-t0b-rules-export.json` · n=1500 · 可达 36 格（Q02+）  
**纪律：** 不改 DS15 倍率表 · 只加套装落点 · 相对 `D_ctrl_*` 判 ±2

## 定稿

| 套装 | 候选 | 判定 | Live |
|------|------|------|------|
| 焚烬 | **D_ig012** 点燃×0.12 | ✅ 36/36 | 烬途点燃 |
| 烬爆 | **D_sl15** 缓速窗 1.5s | ✅ 36/36 | 烬途缓速（否决伤脉冲） |
| 承烬 | **D_h005** 疗 0.005H | 🟡 35/36 | 烬途微疗 |

## 否决摘要

- 点燃×0.25/0.5 · 伤脉冲 0.05–0.25B · 疗 0.010H → ❌（空廊/过强）
- 伤脉冲同格悬崖，改缓速过线

## 复现

```bash
P1SIM_RULES=out-guard-parry-t0b-rules-export.json NPROC=6 SK_ROUND=d436 \
  python3 -u skillkit.py run 1500 /tmp/sk/d436.pkl
# 相对 D_ctrl_* 差分脚本见施工窗
```

*D436 S0 · 2026-10-10*
