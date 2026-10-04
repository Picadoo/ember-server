"""D189 Affix Pack 4 gate: p2econ with the bundled variety pool (10 affixes) vs the pre-D189 pool (8, no venom/jailer).
Usage (repo root or here): python3 tools/p1sim/affixpack4.py [--old] <p2econ args>   → compare the two outputs."""
import os, sys
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import p1config, p2econ

if '--old' in sys.argv:
    sys.argv.remove('--old')
    _orig = p1config.load

    def _load(*a, **k):
        c = _orig(*a, **k)
        v = c.get('variety') or {}
        v['affixes'] = [x for x in v.get('affixes', []) if x not in ('venom', 'jailer')]
        return c
    p1config.load = _load

p2econ.main()
