#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""D311 · 样本窗周报脚本（只读遥测 → markdown 检查单）

- 施工号：D311（docs+tools 同号 · 批 M+R）
- 契约：docs/design/DESIGN-ember-sample-week-report-script-2026-10-08.md §2.1
- 出参栏目真源：docs/status/STATUS-ember-sample-week-report-checklist-d310.md
- 门槛数字钉 D308 §2.1（本脚本不得改数；runs≥30 仅作分母对照）
- 只读 p1-telemetry/<week>.yml；不写回源文件；不改战斗/经济
- 禁自动开闸；禁输出「建议立即开 X R」；D/E/G 人感与签字栏留空
- 依赖：Python3 + PyYAML（仓库环境已有）；stdlib argparse/json/pathlib
"""

from __future__ import annotations

import argparse
import json
import sys
from pathlib import Path
from typing import Any, Dict, Optional, Tuple

try:
    import yaml
except ImportError:  # pragma: no cover
    sys.stderr.write(
        "缺少 PyYAML。请安装 pyyaml，或在已有依赖的环境运行本脚本。\n"
    )
    sys.exit(1)

# D308 §2.1 门槛（只引用 · 不得改）
RUNS_THRESHOLD = 30

FORBIDDEN_SUBSTRINGS = (
    "建议立即开",
    "可以开闸",
    "批准施工 R",
    "自动开 R",
    "无需总控签字",
    "样本已满可开",
    "请玩家查看周报",
    "排行榜",
    "成就进度",
    "将门槛改为",
    "runs 门槛降至",
    "已施工事件 R",
    "D307–D310 重开",
    "D307-D310 重开",
)

COUNT_KEYS = (
    "p1_pf_runs",
    "p1_pf_evt_roll",
    "p1_pf_evt_ok",
    "p1_pf_sig_wear",
    "p1_pf_sig_alt",
    "p1_pf_wall",
    "p1_pf_whiff",
    "p1_pf_break",
    "p1_pf_vb_hit",
)


def die(code: int, msg: str) -> None:
    sys.stderr.write(msg.rstrip() + "\n")
    sys.exit(code)


def as_int(v: Any) -> Optional[int]:
    if v is None:
        return None
    if isinstance(v, bool):
        return None
    if isinstance(v, int):
        return v
    if isinstance(v, float) and v.is_integer():
        return int(v)
    if isinstance(v, str):
        s = v.strip()
        if s.isdigit() or (s.startswith("-") and s[1:].isdigit()):
            return int(s)
    return None


def pct(n: Optional[int], d: Optional[int], *, zero_div: str = "n/a") -> str:
    if n is None or d is None:
        return "—"
    if d == 0:
        return zero_div
    return f"{(100.0 * n / d):.1f}%"


def pct_max1(n: Optional[int], d: Optional[int]) -> str:
    if n is None or d is None:
        return "—"
    denom = max(d, 1)
    return f"{(100.0 * n / denom):.1f}%"


def per_run(n: Optional[int], runs: Optional[int]) -> str:
    if n is None or runs is None:
        return "—"
    if runs == 0:
        return "n/a"
    return f"{(n / runs):.2f}"


def load_yml(path: Path) -> Dict[str, Any]:
    if not path.is_file():
        die(2, f"缺文件或不可读：{path}")
    try:
        with path.open("r", encoding="utf-8") as f:
            data = yaml.safe_load(f)
    except OSError as e:
        die(2, f"缺文件或不可读：{path} ({e})")
    except yaml.YAMLError as e:
        die(3, f"yml 解析失败：{e}")
    if not isinstance(data, dict):
        die(3, "yml 根须为 mapping")
    return data


def validate(data: Dict[str, Any]) -> Tuple[str, Dict[str, Any], int]:
    week = data.get("week")
    if week is None or str(week).strip() == "":
        die(3, "缺键 week")
    counts = data.get("counts")
    if not isinstance(counts, dict):
        die(3, "缺键 counts（须为 mapping）")
    runs = as_int(counts.get("p1_pf_runs"))
    if runs is None:
        die(3, "缺键 counts.p1_pf_runs")
    if runs < 0:
        die(3, "counts.p1_pf_runs 须 ≥ 0")
    return str(week), counts, runs


def get_count(counts: Dict[str, Any], key: str) -> Optional[int]:
    if key not in counts:
        return None
    return as_int(counts.get(key))


def fmt_count(v: Optional[int]) -> str:
    return "—" if v is None else str(v)


def assert_no_forbidden(md: str) -> None:
    """Reject affirmative open-gate phrasing. Meta lines that *document* the ban
    (含「不得出现 / 禁止文案」) are allowed so the md can mirror the D310 checklist.
    """
    for line in md.splitlines():
        # allow meta / allowed phrases from §2.1.6
        if any(
            m in line
            for m in (
                "不得出现",
                "禁止文案",
                "禁输出",
                "不得改门槛",
                "勾满 ≠ 自动开 R",
                "仍不得自动开",
                "不得自动开闸",
                "禁自动开闸",
            )
        ):
            continue
        for s in FORBIDDEN_SUBSTRINGS:
            if s in line:
                die(1, f"内部验收失败：输出含禁句子串「{s}」· 行：{line[:120]}")


def maybe_json_note(json_log: Optional[Path]) -> str:
    if json_log is None:
        return ""
    if not json_log.is_file():
        return f"> **json-log 旁注：** 路径不可读（{json_log}）；**未**用于门槛判定（主源仍为 yml counts）。\n"
    try:
        text = json_log.read_text(encoding="utf-8")
    except OSError as e:
        return f"> **json-log 旁注：** 读取失败（{e}）；**未**覆盖 yml counts。\n"
    # Best-effort: count lines / try parse array length for OP note only
    n_lines = len([ln for ln in text.splitlines() if ln.strip()])
    note = f"> **json-log 旁注：** 已读 `{json_log}`（约 {n_lines} 非空行）；**仅对账**，**不得**覆盖 yml `counts` 作门槛主源。\n"
    try:
        obj = json.loads(text)
        if isinstance(obj, list):
            note = (
                f"> **json-log 旁注：** 已读 JSON 数组 {len(obj)} 条（`{json_log}`）；"
                f"**仅对账**，**不得**覆盖 yml `counts` 作门槛主源。\n"
            )
    except json.JSONDecodeError:
        pass
    return note


def render(
    week: str,
    counts: Dict[str, Any],
    runs: int,
    yml_path: Path,
    updated: Any,
    note: Any,
    json_note: str,
) -> str:
    evt_roll = get_count(counts, "p1_pf_evt_roll")
    evt_ok = get_count(counts, "p1_pf_evt_ok")
    sig_wear = get_count(counts, "p1_pf_sig_wear")
    sig_alt = get_count(counts, "p1_pf_sig_alt")
    wall = get_count(counts, "p1_pf_wall")
    whiff = get_count(counts, "p1_pf_whiff")
    brk = get_count(counts, "p1_pf_break")
    vb = get_count(counts, "p1_pf_vb_hit")

    missing = [k for k in COUNT_KEYS if k != "p1_pf_runs" and k not in counts]
    miss_note = ""
    if missing:
        miss_note = f"> **缺键摘要：** {', '.join(missing)} → 对应格填 `—`（脚本不 invent）。\n"

    # A2
    if runs >= RUNS_THRESHOLD:
        a2_check = "[x]"
        a2_note = (
            f"runs = {runs} · 分母数字已达门槛（对照 D308：runs≥{RUNS_THRESHOLD}）"
            f" · 仍须 A1 人工确认完整周"
        )
        a2_mark = ""
    else:
        a2_check = "[ ]"
        a2_note = f"runs = {runs}"
        a2_mark = f" · **未满 · 不得开（对照 D308：runs≥{RUNS_THRESHOLD}）**"

    # B/C 已抄录预勾（数字已写入，非开闸）
    b_copied = "[x]" if evt_roll is not None or evt_ok is not None else "[ ]"
    c_copied = "[x]" if sig_wear is not None or sig_alt is not None else "[ ]"
    # if any B rate keys present, mark copied for B1-B3
    if evt_roll is not None or evt_ok is not None:
        b1c = b2c = b3c = "[x]"
    else:
        b1c = b2c = b3c = "[ ]"
    if sig_wear is not None or sig_alt is not None:
        c1c = c2c = c3c = c4c = "[x]"
    else:
        c1c = c2c = c3c = c4c = "[ ]"

    red_sample = "[x]" if runs < RUNS_THRESHOLD else "[ ]"

    updated_s = "—" if updated is None else str(updated)
    note_s = "—" if note is None else str(note).replace("\n", " ")

    out_rate = pct(evt_roll, runs)
    ok_rate = pct_max1(evt_ok, evt_roll if evt_roll is not None else None)
    wear_rate = pct(sig_wear, runs)
    alt_of_wear = pct_max1(sig_alt, sig_wear if sig_wear is not None else None)
    alt_of_runs = pct(sig_alt, runs)
    vb_rate = pct(vb, runs)

    lines = []
    a = lines.append

    a("# 余烬 · 样本窗周报检查单（脚本预填 · D311）")
    a("")
    a(f"> **周键（P 周 `weekKey`）：** `{week}`  ")
    a("> **填表人（OP/策划）：** ______________  ")
    a("> **填表日（Asia/Shanghai）：** ______________  ")
    a(f"> **真源文件：** `{yml_path.as_posix()}`  ")
    a("> **命令对照：** `/corerpg p1 telemetry server`（须 `corerpg.admin` · **OP-only**）  ")
    a(
        "> **口径声明：** 门槛数字以 "
        "[`DESIGN-ember-sample-window-readiness-2026-10-08.md`](../design/DESIGN-ember-sample-window-readiness-2026-10-08.md) "
        f"**§2.1 为准**；本单**不得改门槛**；**勾满 ≠ 自动开 R**；脚本只读 counts"
        f"（周文件 note 口径：排除测试号后的合计 · 脚本不二次过滤）。  "
    )
    a(f"> **yml.updated：** {updated_s}  ")
    a(f"> **yml.note：** {note_s}  ")
    if json_note:
        a(json_note.rstrip())
    if miss_note:
        a(miss_note.rstrip())
    a("")
    a("---")
    a("")
    a("## A. 周滚与分母")
    a("")
    a("| # | 检查项 | 读哪 | 门槛（D308 原样） | 本周实填 | 勾选 |")
    a("|---|--------|------|-------------------|----------|------|")
    a(
        f"| A1 | 是否为**完整 P 周**（非「仅日级」半周） | 日历 / `week` 字段 | "
        f"战斗向：≥1 完整 P 周（荐满 2 周再批事件/调律） | "
        f"`week={week}` · 【须人工：是/否 · 第 __ 个完整周】 | [ ] |"
    )
    a(
        f"| A2 | 全服 `p1_pf_runs`（排除测试号后） | `counts.p1_pf_runs` / `telemetry server` | "
        f"**≥ {RUNS_THRESHOLD}** | {a2_note}{a2_mark} | {a2_check} |"
    )
    a(
        "| A3 | 合格重打口径仍为 q01–q07 已扣体力结算（通关+失败） | D298/D308 | 是 | "
        "【须人工：确认 / 有疑】 | [ ] |"
    )
    a(
        "| A4 | 深渊/团本**未**误计入本分母冒充样本满 | D308 红线 8 | 未冒充 | "
        "【须人工：确认】 | [ ] |"
    )
    a("")
    a("---")
    a("")
    a("## B. 事件两率（观察性 · 非硬砍）")
    a("")
    a("| # | 指标 | 公式 / 键 | 本周实填 | 勾选（已抄录） |")
    a("|---|------|-----------|----------|----------------|")
    a(
        f"| B1 | `evt_roll` / `evt_ok` | `p1_pf_evt_roll` · `p1_pf_evt_ok` | "
        f"roll={fmt_count(evt_roll)} ok={fmt_count(evt_ok)} | {b1c} |"
    )
    a(f"| B2 | 出房率 | `evt_roll` / `runs` | {out_rate} | {b2c} |")
    a(f"| B3 | 成功率 | `evt_ok` / `max(evt_roll,1)` | {ok_rate} | {b3c} |")
    a("| B4 | 人感「事件仍空/撞不到」记档？ | 总控/策划笔记 | 【须人工：有 / 无】 | [ ] |")
    a("")
    a(
        "> 事件 R/W 开闸仍须：A1+A2 满 + 人感记档 + **总控另批该债硬设计 A**。"
        "本单勾满**不**等于开闸。"
    )
    a("")
    a("---")
    a("")
    a("## C. 调律两率（观察性 · 非硬砍）")
    a("")
    a("| # | 指标 | 公式 / 键 | 本周实填 | 勾选（已抄录） |")
    a("|---|------|-----------|----------|----------------|")
    a(
        f"| C1 | `sig_wear` / `sig_alt` | `p1_pf_sig_wear` · `p1_pf_sig_alt` | "
        f"wear={fmt_count(sig_wear)} alt={fmt_count(sig_alt)} | {c1c} |"
    )
    a(f"| C2 | 佩戴率 | `sig_wear` / `runs` | {wear_rate} | {c2c} |")
    a(f"| C3 | 调律率（占佩戴） | `sig_alt` / `max(sig_wear,1)` | {alt_of_wear} | {c3c} |")
    a(f"| C4 | 调律率（占局） | `sig_alt` / `runs` | {alt_of_runs} | {c4c} |")
    a("| C5 | 人感「进本前看不见 / 懒得进调律页」？ | 笔记 | 【须人工：有 / 无】 | [ ] |")
    a("")
    a("---")
    a("")
    a("## D. 排除表是否脏")
    a("")
    a("| # | 检查项 | 读哪 | 本周实填 | 勾选 |")
    a("|---|--------|------|----------|------|")
    a(
        "| D1 | `telemetry.exclude_uuids` 已写入已知冒烟/压测 UUID？ | "
        "`ember-v1.yml` → `telemetry.exclude_uuids` | 【须人工：已写 / 空 / 不全】 | [ ] |"
    )
    a(
        "| D2 | `leaderboard_exclude` 与名缀（如 `FreshQ*`）是否仍生效？ | "
        "配置 + 周文件 note | 【须人工：是 / 否】 | [ ] |"
    )
    a(
        "| D3 | 周文件是否明显被 bot 污染（runs 虚高且未排除）？ | "
        "对照点名 telemetry | 【须人工：干净 / **脏**】 | [ ] |"
    )
    a("")
    a(
        "> 脚本**默认不读**玩法配置写死结论；请人工对照 `exclude_uuids` / "
        "`leaderboard_exclude`。若 D3=脏 → **本周不得用全服合计开闸**（D308 红线 3）。"
    )
    a("")
    a("---")
    a("")
    a(
        "## E. 各 R 红线对照（D308 §2.1.2 当前态 · 2026-10-08 钉死「不得开」；"
        "升「满」须总控改表）"
    )
    a("")
    a("| 债 | 最小门槛摘要（D308 原样 · 勿改数） | 本周是否满 | 红线触达？ | 当前态勾选 |")
    a("|----|--------------------------------------|------------|------------|------------|")
    rows = [
        ("事件 R/W", "≥1 完整 P 周（荐 2）且 `runs`≥30 + 人感「空」", "[x] **未满 · 不得开**"),
        ("调律 R", "同上周滚 + 人感「看不见/懒」", "[x] **未满 · 不得开**"),
        (
            "工坊 R（forge 价）",
            "≥3 非测试人感「养不起」或总控记档 + D307≥3 日 + 经济草稿；**禁**借 D307 拧价",
            "[x] **未满 · 不得开**",
        ),
        (
            "走廊 W2",
            "≥3 人感「一条廊」+ D300≥3 日；W2-D 须 p1sim 42±2",
            "[x] **未满/等人感 · 不得开**",
        ),
        ("深渊 R", "≥3 人感 + D302≥3 日；**不以** `pf_runs` 冒充", "[x] **未满 · 不得开**"),
        ("周本 R", "≥3 人感 + D303≥3 日；不以日刷分母冒充", "[x] **未满 · 不得开**"),
        ("Boss 预警 R", "≥3 人感 + D304≥3 日", "[x] **未满 · 不得开**"),
        ("挂机 R", "≥3 人感 + D305≥3 日；禁抬产能", "[x] **未满 · 不得开**"),
    ]
    for name, thr, cur in rows:
        a(f"| {name} | {thr} | 【须人工】 | 【须人工】 | {cur} |")
    a("")
    a("**通用红线速勾（任一 [x] → 本周禁止批对应 R）：**")
    a("")
    a(f"- {red_sample} 样本周未满 / `runs`<{RUNS_THRESHOLD}（战斗向）")
    a("- [ ] 对应 M 收仓不足 3 自然日")
    a("- [ ] 排除表脏 / 未配")
    a("- [ ] 仅因薄 UX（组队/图录/钱包/VIP/技能页）想「顺便」开 R")
    a("- [ ] 任何改 refine/quality/enhance/upgrade 价却挂菜单热修名义")
    a("- [ ] 玩家面遥测 KPI / 排行 / 成就挂钩提议")
    a("- [ ] 天赋 T0''' / 灰印续跑 / 守招再调 / Pack6 / 六槽 / 抬体力掉落夹带")
    a("- [ ] 用日刷 `pf_runs` 冒充深渊/周本/挂机样本满")
    a("- [ ] 无总控批 A 想自开 R")
    a("- [ ] 想重开 D307 / 改 D308 门槛数 / 重开 D309 副手")
    a("")
    a(
        "> **对照 D308：未满 · 不得开。** 脚本**不得**因 `runs≥30` 自动改成「可开」；"
        "须总控签字后方可进入某债待批 A。"
    )
    a("")
    a("---")
    a("")
    a("## F. 辅助观察（可选 · 不算开闸）")
    a("")
    a("| 项 | 键 / 公式 | 本周实填 |")
    a("|----|-----------|----------|")
    a(
        f"| 破绽局均 wall/whiff/break | 各 / `runs` | "
        f"{per_run(wall, runs)} / {per_run(whiff, runs)} / {per_run(brk, runs)} |"
    )
    a(f"| `p1_pf_vb_hit` 率 | vb / runs | {vb_rate} |")
    a("| 备注（人感原话摘录） | — |  |")
    a("")
    a("---")
    a("")
    a("## G. 签字栏（总控 · 必填才算「可进入某债待批 A」讨论）")
    a("")
    a("| 角色 | 结论 | 签名 | 日 |")
    a("|------|------|------|-----|")
    a(
        "| 填表人 | 本周样本：**未满** / **观察满（仍不得自动开）** / **有脏表** | "
        "________ | ____ |"
    )
    a("| 策划复核 | 同意上表勾选；**不**改门槛数字 | ________ | ____ |")
    a(
        "| **总控** | [ ] 仅收悉周报 · [ ] 批准进入 **____** 债硬设计待批 A（点名债名）"
        "· [ ] 驳回（理由：________） | ________ | ____ |"
    )
    a("")
    a(
        "> **禁止文案：** 本单**不得**出现「建议立即开事件 R / 调律 R / forge R …」"
        "而无上表总控签字占位。"
    )
    a(
        "> **开闸路径：** 总控勾「批准进入某债待批 A」→ 另派该债 DESIGN → 再批 A → "
        "另派施工号。周报施工号**不得**兼开战斗/经济 R。"
    )
    a("")
    a("---")
    a("")
    a(
        "本文件由脚本生成数字栏；人感与总控签字须人工；勾满 ≠ 自动开 R；"
        "门槛数字以 D308 §2.1 为准（未改）。"
    )
    a("")
    a("*生成器：`tools/p1-telemetry-week-report.py` · D311 · 只读遥测 · 禁自动开闸*")

    # silence unused (b_copied/c_copied kept for clarity / future)
    _ = (b_copied, c_copied)

    md = "\n".join(lines) + "\n"
    assert_no_forbidden(md)
    return md


def build_parser() -> argparse.ArgumentParser:
    p = argparse.ArgumentParser(
        prog="p1-telemetry-week-report.py",
        description=(
            "D311：从 p1-telemetry/<week>.yml 生成样本窗周报检查单 markdown"
            "（只读；禁自动开闸；门槛钉 D308）。"
        ),
    )
    p.add_argument(
        "--yml",
        dest="yml",
        metavar="PATH",
        help="周文件路径（真源 plugins/CoreRpg/p1-telemetry/<week>.yml）",
    )
    p.add_argument(
        "yml_pos",
        nargs="?",
        metavar="YML",
        help="周文件路径（位置参数；与 --yml 二选一）",
    )
    p.add_argument(
        "--json-log",
        dest="json_log",
        metavar="PATH",
        default=None,
        help="可选旁路 JSON/日志；仅对账备注，不覆盖 yml counts",
    )
    p.add_argument(
        "--out",
        dest="out",
        metavar="PATH",
        default=None,
        help="输出 markdown 路径；缺省写 stdout（契约荐写 docs/status/reports/…）",
    )
    return p


def main(argv: Optional[list] = None) -> int:
    args = build_parser().parse_args(argv)
    yml = args.yml or args.yml_pos
    if not yml:
        die(2, "须提供 --yml PATH 或位置参数 YML")
    yml_path = Path(yml)
    data = load_yml(yml_path)
    week, counts, runs = validate(data)
    json_note = maybe_json_note(Path(args.json_log) if args.json_log else None)
    md = render(
        week=week,
        counts=counts,
        runs=runs,
        yml_path=yml_path,
        updated=data.get("updated"),
        note=data.get("note"),
        json_note=json_note,
    )
    if args.out:
        out_path = Path(args.out)
        try:
            out_path.parent.mkdir(parents=True, exist_ok=True)
            out_path.write_text(md, encoding="utf-8")
        except OSError as e:
            die(4, f"--out 无法写入：{out_path} ({e})")
    else:
        sys.stdout.write(md)
    return 0


if __name__ == "__main__":
    sys.exit(main())
