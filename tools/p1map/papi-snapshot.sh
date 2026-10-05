#!/usr/bin/env bash
# papi-snapshot.sh <player> <out.tsv> — D240: resolve every %corerpg_*% / %ember_*% key the live configs use
# (+ a few fallthrough edge keys) for one online player via `papi parse`, write key<TAB>value (sorted).
# Used before/after a PAPI refactor to diff values across two sessions of the same bot.
set -uo pipefail
P=$1; OUT=$2
M=/workspace/minecraft; C=$M/scripts/console.sh
KEYS=$(mktemp)
{ rg -o --no-filename "%(corerpg|ember)_[a-z0-9_]+%" $M/server-runtime/plugins $M/CoreRpg/src/main/resources 2>/dev/null | tr -d '%'
  # fallthrough / edge keys (must keep resolving the same)
  printf '%s\n' corerpg_p1_rush_nosuch corerpg_p1_shop_nosuch corerpg_p1_shopnosuch corerpg_p1_goalnosuch corerpg_p1_fest_nosuch \
    corerpg_p1_q01_nosuch corerpg_p1_nosuch corerpg_p1_marks_tx corerpg_p1_abyss_tx corerpg_p1_top_abyss_x corerpg_p1_top_abyss_11 \
    corerpg_p1_codex_stage_99 corerpg_p1_codexx corerpg_p1_passd_emberq01 corerpg_p1_q09done corerpg_nosuch corerpg_gate_nosuch \
    corerpg_stamina corerpg_stamina_max corerpg_level corerpg_ember_xp_need corerpg_vip_title corerpg_talent_spent corerpg_talent_earned \
    corerpg_cash corerpg_monthly corerpg_daily_tickets corerpg_daily_cap corerpg_mail_unread corerpg_quest_chapter corerpg_signed \
    corerpg_activity corerpg_abyss_used corerpg_calamity_next corerpg_covenant corerpg_flex_skill corerpg_kit_shape_key corerpg_kit_step_dir_key \
    corerpg_skill_charge_ready corerpg_talent_points ember_power_score ember_abyss_best ember_weekly_best_sec ember_ladder_power_1_name \
    ember_ladder_abyss_2_value ember_ladder_speed_0_name ember_nosuch
} | sort -u > "$KEYS"
: > "$OUT.raw"
mapfile -t ALL < "$KEYS"
for ((i=0; i<${#ALL[@]}; i+=12)); do
  S=""; for k in "${ALL[@]:i:12}"; do S+="⟦$k⟧%$k%"; done
  $C play "papi parse $P ${S}⟦END⟧" 0.35 2>/dev/null | tr -d '\r' | grep -a "⟦END⟧" >> "$OUT.raw"
done
python3 - "$OUT.raw" "$OUT" <<'PY'
import re,sys
rows={}
for line in open(sys.argv[1],encoding='utf-8',errors='replace'):
    body=line.split('⟦',1)
    if len(body)<2: continue
    for part in ('⟦'+body[1]).split('⟦')[1:]:
        if '⟧' not in part: continue
        k,v=part.split('⟧',1)
        if k=='END': continue
        rows[k]=v.rstrip('\n')
with open(sys.argv[2],'w',encoding='utf-8') as f:
    for k in sorted(rows): f.write(k+'\t'+rows[k]+'\n')
print(len(rows),'keys')
PY
echo "requested $(wc -l < "$KEYS")"; rm -f "$KEYS" "$OUT.raw"
