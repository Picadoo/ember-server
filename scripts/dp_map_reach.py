# Reachable-room scan for DP map templates (1.12 anvil). Needs: pip install anvil-parser
import anvil, re, glob, os, sys, collections
cache={}
def blk(m,x,y,z):
    rx,rz=x>>9,z>>9
    key=(m,rx,rz)
    if key not in cache:
        f=f'map/{m}/region/r.{rx}.{rz}.mca'
        cache[key]=anvil.Region.from_file(f) if os.path.exists(f) else None
    r=cache[key]
    if r is None: return 1
    try: return r.get_chunk((x>>4)&31,(z>>4)&31).get_block(x&15,y,z&15).id
    except Exception: return 1
PASS={0,50,63,68,65,106,171,31,37,38,175,78,66,27,28,55,69,77,143,70,72,147,148}  # air, torch, signs, ladder, vine, carpet, grass, flowers, snow layer, rails, buttons, plates
def open_(m,x,y,z): return blk(m,x,y,z) in PASS and blk(m,x,y+1,z) in PASS and blk(m,x,y-1,z) not in PASS
def reach(m,sx,sy,sz,lim=4000):
    seen={(sx,sy,sz)}; q=collections.deque([(sx,sy,sz)])
    while q and len(seen)<lim:
        x,y,z=q.popleft()
        for dx,dz in ((1,0),(-1,0),(0,1),(0,-1)):
            for dy in (0,1,-1):
                n=(x+dx,y+dy,z+dz)
                if n in seen: continue
                if open_(m,*n) and (dy<1 or blk(m,x,y+2,z) in PASS):
                    seen.add(n); q.append(n); break
    return seen
if __name__=='__main__':
    for m in sys.argv[1:]:
        R=reach(m,-40,65,270)
        xs=[p[0] for p in R]; zs=[p[2] for p in R]; ys=[p[1] for p in R]
        print(m,len(R),'x',min(xs),max(xs),'z',min(zs),max(zs),'y',min(ys),max(ys))
