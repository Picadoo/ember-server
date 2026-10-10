# 状态 · D456：签名获取清晰度 · PASS

**日期：** 2026-10-10  
**裁决：** **已批 A · 批 M · D456 · PASS**  
**版本：** jar **1.65.148-d456.local** · SEVERE0  
**冒烟：** FreshQ987 · PASS=4 FAIL=0 · `%corerpg_p1_sig_acq%` →「重打：每局徽记 + 约12% 签名；未出仍拿徽记，烙印可保底」  
**单测：** `EmberSigAcqTest` PASS  
**硬禁：** 未抬 `STAMP_RATE` · ≠烙印价 · ≠AFK · ≠K3 · ≠sx20

## 交付

| 项 | 内容 |
|----|------|
| `EmberSigAcq` | missLine / menuLine / ratePct |
| settle | 付了 `sig_mark` 且无 stamp → 结算半行 |
| PAPI | `sig_acq` / `sig_acq_line` |
| TrMenu | adventure + sig 挂行 |

*D456 PASS · 签名获取清晰度（未出也可读 · 零抬掉率）。*
