"""M03: diff the real Java EmberBurnBook against tools/p1sim/burnbook.py on identical event scripts.
Copies EmberBurnBook.java / EmberSetRules.java from CoreRpg/src (read-only) + BurnCheck.java into a temp dir, compiles
with the repo JDK 8 (tools/jdk8u504-b01, or $JAVAC), runs N random scripts + the review's R03/R04 case.
Usage (repo root): python3 tools/p1sim/javacheck/burncheck.py [--scripts 300]   exit 0 = identical, 1 = mismatch, 2 = no javac
"""
import os, random, shutil, subprocess, sys, tempfile
HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, os.path.dirname(HERE))
import burnbook
ROOT = os.path.abspath(os.path.join(HERE, '..', '..', '..'))
SRC = os.path.join(ROOT, 'CoreRpg/src/main/java/town/sunshine/corerpg/p1')


def py_run(script):
    books, out = {}, []
    for line in script:
        f = line.split(' ')
        b = books.setdefault(f[0], burnbook.BurnBook())
        op = f[1]
        if op == 'ignite':
            ev = b.ignite(f[2], float(f[3]), int(f[4]))
            out.append('%s ignite %s evicted=%s size=%d' % (f[0], f[2], 'null' if ev is None else ev, len(b)))
        elif op == 'transfer':
            ok = b.transfer(f[2], f[3], int(f[4]), int(f[5]))
            out.append('%s transfer %s>%s %s size=%d' % (f[0], f[2], f[3], 'true' if ok else 'false', len(b)))
        elif op == 'due':
            ticks = b.due(int(f[2]))
            out.append('%s due %s:%s size=%d' % (f[0], f[2], ''.join(' %s=%.6f' % (t, a) for t, a in ticks), len(b)))
        elif op == 'remove':
            b.remove(f[2])
        elif op == 'ticks':
            b.set_ticks(int(f[2]))
        elif op == 'clear':
            b.clear()
    return out


def random_script(rng, n=120):
    now, out = 0, []
    tg = 'ABCDEFGH'
    for _ in range(n):
        now += rng.choice((0, 50, 100, 250, 500, 700, 1000, 1000, 1500, 2600, 4100))
        bk = rng.choice(('p1', 'p1', 'p2'))
        r = rng.random()
        if r < 0.35:
            out.append('%s ignite %s %.3f %d' % (bk, rng.choice(tg), rng.choice((10, 20, 31.25, 7.5)), now))
        elif r < 0.75:
            out.append('%s due %d' % (bk, now))
        elif r < 0.85:
            out.append('%s transfer %s %s %d %d' % (bk, rng.choice(tg), rng.choice(tg), now, rng.choice((0, 2000, 4000))))
        elif r < 0.92:
            out.append('%s remove %s' % (bk, rng.choice(tg)))
        elif r < 0.97:
            out.append('%s ticks %d' % (bk, rng.choice((4, 5, 6))))
        else:
            out.append('%s clear' % bk)
    return out


# review R03 / R04: two players' books on one target, 10 and 20 per tick; 500 ms → nothing, 1000 ms → 30
REVIEW = ['p1 ignite T 10 0', 'p2 ignite T 20 0', 'p1 due 500', 'p2 due 500', 'p1 due 1000', 'p2 due 1000']


def main():
    n = 300
    if '--scripts' in sys.argv:
        n = int(sys.argv[sys.argv.index('--scripts') + 1])
    javac = os.environ.get('JAVAC') or os.path.join(ROOT, 'tools/jdk8u504-b01/bin/javac')
    java = os.path.join(os.path.dirname(javac), 'java')
    if not os.path.exists(javac):
        print('burncheck: no javac at %s — SKIPPED' % javac)
        return 2
    tmp = tempfile.mkdtemp(prefix='burncheck-')
    try:
        pk = os.path.join(tmp, 'src')
        os.makedirs(pk)
        for f in ('EmberBurnBook.java', 'EmberSetRules.java'):
            shutil.copy(os.path.join(SRC, f), pk)
        shutil.copy(os.path.join(HERE, 'BurnCheck.java'), pk)
        cls = os.path.join(tmp, 'cls')
        os.makedirs(cls)
        subprocess.check_call([javac, '-nowarn', '-encoding', 'UTF-8', '-d', cls] + [os.path.join(pk, f) for f in os.listdir(pk)])
        rng = random.Random(20261004)
        scripts = [REVIEW] + [random_script(rng) for _ in range(n)]
        blob = []
        for i, s in enumerate(scripts):
            blob += ['s%d_%s' % (i, l) for l in s]  # book ids per script → independent books in one JVM run
        jout = subprocess.run([java, '-cp', cls, 'town.sunshine.corerpg.p1.BurnCheck'], input='\n'.join(blob) + '\n',
                              capture_output=True, text=True, check=True).stdout.splitlines()
        pout = py_run(blob)
        bad = [(j, p) for j, p in zip(jout, pout) if j != p]
        if len(jout) != len(pout):
            bad.append(('len %d' % len(jout), 'len %d' % len(pout)))
        ticks = sum(l.count('=') - 1 for l in jout if ' due ' in l)
        rev = [l for l in jout if l.startswith('s0_')]
        print('burncheck: %d scripts, %d events, %d burn ticks compared: %s' % (len(scripts), len(jout), ticks,
              'Java == Python' if not bad else '%d MISMATCHES' % len(bad)))
        print('  review case:', ' | '.join(rev[-4:]))
        for j, p in bad[:10]:
            print('  JAVA  ', j); print('  PYTHON', p)
        return 1 if bad else 0
    finally:
        shutil.rmtree(tmp, ignore_errors=True)


if __name__ == '__main__':
    sys.exit(main())
