import zlib, struct, os, random
OUT = os.path.join(os.path.dirname(os.path.abspath(__file__)), "src/main/resources/assets/narutomod/textures")

def write_png(path, w, h, px):
    raw = b""
    for y in range(h):
        raw += b"\x00"
        for x in range(w):
            raw += bytes(px.get((x, y), (0, 0, 0, 0)))
    def chunk(t, d):
        c = struct.pack(">I", len(d)) + t + d
        return c + struct.pack(">I", zlib.crc32(t + d) & 0xffffffff)
    png = (b"\x89PNG\r\n\x1a\n" + chunk(b"IHDR", struct.pack(">IIBBBBB", w, h, 8, 6, 0, 0, 0))
           + chunk(b"IDAT", zlib.compress(raw)) + chunk(b"IEND", b""))
    os.makedirs(os.path.dirname(path), exist_ok=True)
    open(path, "wb").write(png)

def rect(px, x0, y0, x1, y1, c):
    for y in range(y0, y1 + 1):
        for x in range(x0, x1 + 1): px[(x, y)] = c

def line(px, x0, y0, x1, y1, c):
    dx, dy = abs(x1 - x0), -abs(y1 - y0)
    sx, sy = (1 if x0 < x1 else -1), (1 if y0 < y1 else -1)
    err = dx + dy
    while True:
        px[(x0, y0)] = c
        if x0 == x1 and y0 == y1: break
        e2 = 2 * err
        if e2 >= dy: err += dy; x0 += sx
        if e2 <= dx: err += dx; y0 += sy

def noise(px, x0, y0, x1, y1, base, var, seed, vertical=False):
    r = random.Random(seed)
    for y in range(y0, y1 + 1):
        for x in range(x0, x1 + 1):
            k = (x if vertical else 0)
            rr = random.Random(seed * 1000 + k) if vertical else r
            d = rr.randint(-var, var)
            px[(x, y)] = (max(0, min(255, base[0] + d)), max(0, min(255, base[1] + d)), max(0, min(255, base[2] + d)), 255)

PAPER=(232,215,170,255); PAPER2=(190,165,110,255); RED=(170,40,30,255); DARK=(40,40,50,255)

# --- block ---
p = {}; noise(p,0,0,15,15,(100,70,38),18,1,vertical=True)
for y in (3,8,12): line(p,0,y,15,y,(70,48,26,255))
write_png(f"{OUT}/block/mokuton_wood.png",16,16,p)
p = {}; noise(p,0,0,15,15,(52,140,52),30,2)
write_png(f"{OUT}/block/mokuton_leaf.png",16,16,p)

# --- scrolls ---
def scroll(name, accent):
    p = {}
    rect(p,3,5,12,10,PAPER); rect(p,3,10,12,10,PAPER2)
    rect(p,1,4,2,11,RED); rect(p,13,4,14,11,RED)
    rect(p,6,6,9,9,accent+(255,))
    rect(p,7,5,8,5,accent+(255,)); rect(p,7,10,8,10,(60,40,20,255))
    write_png(f"{OUT}/item/{name}.png",16,16,p)
for n,c in {"wood_clone_scroll":(150,100,50),"wood_house_scroll":(180,130,70),"wood_prison_scroll":(90,60,30),
            "wood_spear_scroll":(110,160,60),"wood_dragon_scroll":(30,130,50),"wood_forest_scroll":(60,200,80),
            "wood_golem_scroll":(140,140,120)}.items():
    scroll(n,c)

# --- throwing kunai (xám tối, vòng đỏ) ---
p = {}
line(p,13,2,8,7,(200,204,214,255)); line(p,13,3,9,7,(130,136,150,255)); p[(14,1)]=(220,224,232,255)
line(p,7,8,4,11,(60,60,70,255)); line(p,6,8,3,11,(110,70,40,255))
rect(p,1,12,3,14,RED); rect(p,2,13,2,13,(0,0,0,0))
write_png(f"{OUT}/item/throwing_kunai.png",16,16,p)

# --- mission book ---
p = {}
rect(p,3,2,12,13,(150,85,40,255)); rect(p,3,2,4,13,(100,55,25,255)); rect(p,12,3,12,12,(235,225,200,255))
rect(p,6,5,10,9,(60,170,70,255)); rect(p,7,6,9,8,(120,230,120,255)); rect(p,7,10,9,11,(60,40,20,255))
write_png(f"{OUT}/item/mission_book.png",16,16,p)

# --- kage hat item ---
p = {}
for i,y in enumerate(range(2,11)):
    rect(p,8-1-i//2,y,8+i//2,y,(190,35,35,255))
rect(p,2,11,13,12,(240,240,240,255)); rect(p,3,13,12,13,(190,190,190,255))
rect(p,6,9,9,10,(255,255,255,255)); p[(7,9)]=(190,35,35,255); p[(8,10)]=(190,35,35,255)
write_png(f"{OUT}/item/kage_hat.png",16,16,p)

# --- kage armor layer ---
p = {}
rect(p,0,0,31,7,(190,35,35,255)); rect(p,0,8,31,13,(190,35,35,255))
rect(p,0,12,31,14,(240,240,240,255)); rect(p,9,9,14,13,(255,255,255,255)); rect(p,10,10,13,12,(190,35,35,255))
write_png(f"{OUT}/models/armor/kage_layer_1.png",64,32,p)
write_png(f"{OUT}/models/armor/kage_layer_2.png",64,32,{})

# --- wood clone skin 64x64 ---
p = {}
bark = (110,76,42)
for (x0,y0,x1,y1,s) in [(0,0,31,15,11),(16,16,39,31,12),(0,16,15,31,13),(40,16,55,31,14),(16,48,31,63,15),(32,48,47,63,16)]:
    noise(p,x0,y0,x1,y1,bark,16,s,vertical=True)
rect(p,8,0,15,7,(70,150,60,255))                         # tóc lá
rect(p,10,11,11,11,(25,18,10,255)); rect(p,13,11,14,11,(25,18,10,255))  # mắt
rect(p,11,14,13,14,(60,38,20,255))                        # miệng
rect(p,20,20,35,22,(60,130,55,255))                       # dải lá trên thân
write_png(f"{OUT}/entity/wood_clone.png",64,64,p)
print("extra textures ok")
