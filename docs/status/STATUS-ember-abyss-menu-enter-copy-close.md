# STATUS — B2.111 TrMenu ember_abyss.yml L77 进本 click tell · 结案

Date: 2026-10-01 13:37 Asia/Shanghai
Owner: 余烬-总控（Grok Bot）

## Verdict
**PASS · 勾销**（reload 实测：待恢复服后实测，SKIP 不挡）

## Tips
- 设计 `227bec9` · 批准 `4e0c11a` · 插件 `4cd3be3` · 测 `7ef8d00`
- 报告：`docs/tests/TEST-B2.111-abyss-menu-enter-tell.md`

## Acceptance (测岗)
- 仅 `plugins/TrMenu/menus/ember_abyss.yml` L77 1+/1-：「尝试下潜……（需余烬 Lv.25 · 体力 30；等级不足不扣）」→「尝试下潜……（需余烬 Lv.25）」，逐字一致，按字节无 tab/CR/行尾空格；其余行逐字节未动
- `/tmp/chk-b2111.js 4cd3be3` → `only-L77 /Icons/S/actions/all/1`；旧对旧/新对新 exit 1；11 例变异均按预期；独立 deep diff 仅一处
- rg 0 命中；口径：深渊无周首免，发起者等级不足先挡不扣，队员等级不足先扣后退，OP 跳过等级门免扣，均有私聊
- HANDOFF §8 查密码 0；`ops.json` = `[]`

## Corrections (不影响结论)
- 校验脚本 exit 5 只在两侧 all/3 同错时可达（同 B2.110）。
- DP 人数门在 EmberAbyss `option.yml:17`（非 L16）。
- 退还例外（下线不退、5.5s 内重试不提示）归 B2.119。

## Later candidates
- B2.120 / B2.121（已排）；L41 灰显不区分 OP、L78 撤离句与 DP L23 重复（只记）
- B2.109 追加：深渊菜单点击用例

## Next
- 流水线暂停：用户要求先交接，请 GPT 协助策划。默认下一窗 **B2.113**（ember_hub.yml L215/L266/L280 占位符）。
