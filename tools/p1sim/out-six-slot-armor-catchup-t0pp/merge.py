import pickle, sys
out = {}
src = pickle.load(open('/workspace/d313/t0p-ab.pkl', 'rb'))
out.update({k: v for k, v in src.items() if k[0] in ('base', 't0p_A')})
for f in sys.argv[2:]:
    out.update(pickle.load(open(f, 'rb')))
pickle.dump(out, open(sys.argv[1], 'wb'))
print(len(out), sorted({k[0] for k in out}))
