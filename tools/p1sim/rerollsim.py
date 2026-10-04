#!/usr/bin/env python3
"""D148 (review 10-04 #4): expected rerolls / cost to reach a chosen affix at the quality cap, with and without the
lock-affix option. Rules = EmberAffix.roll: affix uniform over the slot pool (3), tier from tier_weights truncated at the
cap, `pity` misses below the cap in a row → next roll is the cap tier (counter resets on a cap roll, any affix).
Lock (D148): keeps the current affix and rolls only the tier; costs lock_shard extra shards (same pity counter).
Strategy "lock": roll unlocked until the chosen affix shows up (any tier, keep it), then lock until the cap tier.
Cost per roll at T3: 120 shards + 1000 coins (shard payment), lock surcharge lock_shard[T3].
usage: python3 tools/p1sim/rerollsim.py [--n 50000] [--seed 1] [--lock-shard 120]
"""
import argparse, random

W = [50, 30, 15, 5]
CAP = [1, 2, 3, 4]
PITY = 5
POOL = 3


def tier_roll(rng, cap, pity):
    if pity >= PITY:
        return cap
    w = W[:cap]
    u = rng.random() * sum(w)
    acc = 0
    for i, x in enumerate(w):
        acc += x
        if u < acc:
            return i + 1
    return cap


def run(rng, cap, lock):
    """returns (rolls, lock_rolls) until the target affix (index 0) sits at the cap"""
    pity = 0
    have = False  # holding the target affix at some tier
    rolls = locks = 0
    while True:
        use_lock = lock and have
        affix = 0 if use_lock else rng.randrange(POOL)
        t = tier_roll(rng, cap, pity)
        pity = 0 if t >= cap else pity + 1
        rolls += 1
        locks += use_lock
        if affix == 0:
            have = True
            if t >= cap:
                return rolls, locks


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument('--n', type=int, default=50000)
    ap.add_argument('--seed', type=int, default=1)
    ap.add_argument('--lock-shard', type=int, default=120, help='extra shards per locked roll at T3')
    a = ap.parse_args()
    print('# ' + __import__('rules').stamp(), flush=True)  # M06: which rule snapshot produced this report
    rng = random.Random(a.seed)
    names = ['标准', '精良', '卓越', '极品']
    print('# rerollsim — chosen affix at the quality cap (T3 item: 120 shards + 1000 coins per roll, lock +%d shards)\n' % a.lock_shard)
    print('| 成色（上限档） | 不锁：次数 | 不锁：碎片 / 币 | 锁定：次数（其中锁定） | 锁定：碎片 / 币 | 任意词条满档（参考） |')
    print('|---|---|---|---|---|---|')
    for qi, cap in enumerate(CAP):
        r0 = [run(rng, cap, False) for _ in range(a.n)]
        r1 = [run(rng, cap, True) for _ in range(a.n)]
        # any affix at cap (reference): same as lock from the start
        anyc = []
        for _ in range(a.n):
            pity = n = 0
            while True:
                t = tier_roll(rng, cap, pity); pity = 0 if t >= cap else pity + 1; n += 1
                if t >= cap: break
            anyc.append(n)
        m0 = sum(x for x, _ in r0) / a.n
        m1 = sum(x for x, _ in r1) / a.n
        l1 = sum(y for _, y in r1) / a.n
        print('| %s（%d） | %.1f | %.0f / %.0f | %.1f（%.1f） | %.0f / %.0f | %.1f |' % (
            names[qi], cap, m0, m0 * 120, m0 * 1000, m1, l1, m1 * 120 + l1 * a.lock_shard, m1 * 1000, sum(anyc) / a.n))


if __name__ == '__main__':
    main()
