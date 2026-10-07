#!/usr/bin/env bash
# Verify the live DungeonPlus P1 map templates (Q01–Q07 / R01–R03 / B1 / B2: plugins/DungeonPlus/map/ember_daily*_v1)
# against the GitHub release maps-v2-2026-10-03 (SHA256SUMS.txt inside the combined zip), optionally restore them.
#
#   scripts/dp-maps-v2-verify.sh                    check only; exit 0 = all 7 templates byte-identical to the release
#   scripts/dp-maps-v2-verify.sh --restore          replace every template that is missing / a symlink / different
#   scripts/dp-maps-v2-verify.sh --restore-broken   replace only templates that are missing or a symlink (keeps edits)
#
# Why: on 2026-10-08 the 7 *_v1 templates were symlinks to the old S2 shell maps (ember_daily, ember_daily_ash, …):
# every P1 dungeon ran on the wrong geometry (mobs spawned inside walls, rooms without chunks). The v1/v2 templates are
# not in the ember-binaries-20261001 pack, so a restore from that pack alone leaves them missing.
# Restore refuses while the play server runs (DungeonPlus copies templates into dungeon-caches at start; restart after).
# Replaced folders/symlinks are moved to $CACHE/replaced-<ts>/ (nothing is deleted). Needs `gh` only if the zip is not cached.
set -uo pipefail
M="${EMBER_RUNTIME:-/workspace/minecraft}"
TAG="${EMBER_MAPS_TAG:-maps-v2-2026-10-03}"
ZIPNAME="${EMBER_MAPS_ZIP:-ember-p1-maps-v2-2026-10-03.zip}"
ZIPSHA="${EMBER_MAPS_ZIP_SHA256:-561e9decd32501505e6b42f224491424cbb30911c786e7fce974af03e98c5083}"
CACHE="${EMBER_MAPS_CACHE:-/workspace/maps-v2}"
MODE=check
case "${1:-}" in
  "") ;; --restore) MODE=all ;; --restore-broken) MODE=broken ;;
  -h|--help) sed -n '2,15p' "$0"; exit 0 ;;
  *) echo "unknown option $1" >&2; exit 2 ;;
esac
say() { printf '[dp-maps] %s\n' "$*"; }
mkdir -p "$CACHE"
ZIP="$CACHE/$ZIPNAME"
if [ ! -f "$ZIP" ]; then
  command -v gh >/dev/null || { say "FAIL: $ZIP not cached and gh not installed"; exit 3; }
  say "downloading $ZIPNAME from release $TAG"
  gh release download "$TAG" --repo Picadoo/ember-server -p "$ZIPNAME" -D "$CACHE" --clobber || { say "FAIL: download"; exit 3; }
fi
[ "$(sha256sum "$ZIP" | cut -d' ' -f1)" = "$ZIPSHA" ] || { say "FAIL: $ZIP sha256 mismatch"; exit 3; }
X="$CACHE/x"
if [ ! -f "$X/SHA256SUMS.txt" ] || ! (cd "$X" && sha256sum -c --quiet SHA256SUMS.txt >/dev/null 2>&1); then
  rm -rf "$X" && mkdir -p "$X"
  python3 -c "import sys,zipfile; zipfile.ZipFile(sys.argv[1]).extractall(sys.argv[2])" "$ZIP" "$X" || { say "FAIL: unzip"; exit 3; }
  (cd "$X" && sha256sum -c --quiet SHA256SUMS.txt) || { say "FAIL: release SHA256SUMS self-check"; exit 3; }
fi

BAD=(); BROKEN=()
for t in $(awk '{print $2}' "$X/SHA256SUMS.txt" | awk -F/ '{print $4}' | sort -u); do
  live="$M/plugins/DungeonPlus/map/$t"
  if [ -L "$live" ]; then say "BROKEN  $t is a symlink -> $(readlink "$live")"; BAD+=("$t"); BROKEN+=("$t"); continue; fi
  if [ ! -d "$live" ]; then say "BROKEN  $t missing"; BAD+=("$t"); BROKEN+=("$t"); continue; fi
  if (cd "$M" && grep " plugins/DungeonPlus/map/$t/" "$X/SHA256SUMS.txt" | sha256sum -c --quiet >/dev/null 2>&1); then
    say "ok      $t"
  else
    say "DIFF    $t differs from $TAG"; BAD+=("$t")
  fi
done
[ ${#BAD[@]} -eq 0 ] && { say "all templates match $TAG"; exit 0; }
[ "$MODE" = check ] && { say "${#BAD[@]} template(s) not matching (${#BROKEN[@]} missing/symlink); run with --restore-broken or --restore"; exit 1; }
if [ -f "$M/server-runtime/server.pid" ] && kill -0 "$(cat "$M/server-runtime/server.pid")" 2>/dev/null; then
  say "FAIL: play server is running; stop it first (server-runtime/stop.sh)"; exit 4
fi
LIST=("${BAD[@]}"); [ "$MODE" = broken ] && LIST=("${BROKEN[@]}")
[ ${#LIST[@]} -eq 0 ] && { say "nothing missing/symlinked; content diffs left as they are (--restore replaces them)"; exit 1; }
BK="$CACHE/replaced-$(date +%Y%m%d-%H%M%S)"; mkdir -p "$BK"
for t in "${LIST[@]}"; do
  live="$M/plugins/DungeonPlus/map/$t"
  if [ -L "$live" ] || [ -e "$live" ]; then mv "$live" "$BK/$t"; fi
  cp -a "$X/plugins/DungeonPlus/map/$t" "$live" && say "restored $t (old → $BK/$t)"
done
(cd "$M" && grep -E " plugins/DungeonPlus/map/($(IFS='|'; echo "${LIST[*]}"))/" "$X/SHA256SUMS.txt" | sha256sum -c --quiet) \
  && say "restored templates verified; start the play server, then: python3 scripts/check-dp-spawns.py" || { say "FAIL: verify after restore"; exit 5; }
