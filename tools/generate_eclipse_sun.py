"""Draw a compact, transparent solar-eclipse sprite for Minecraft's celestial atlas."""

from math import atan2, cos, exp, hypot, pi
from pathlib import Path
from struct import pack
from zlib import compress, crc32


SIZE = 64
MID = (SIZE - 1) / 2
OUTPUT = (
    Path(__file__).resolve().parents[1]
    / "src/client/resources/assets/ciclos_celestiais/textures/environment/celestial/eclipse_sun.png"
)


def chunk(kind: bytes, data: bytes) -> bytes:
    return pack(">I", len(data)) + kind + data + pack(">I", crc32(kind + data) & 0xFFFFFFFF)


pixels = bytearray()
for y in range(SIZE):
    pixels.append(0)  # PNG filter: none
    for x in range(SIZE):
        dx, dy = x - MID, y - MID
        r = hypot(dx, dy)
        angle = atan2(dy, dx)
        rays = max(0.0, cos(12 * angle + 0.16)) ** 18 * 5.5
        rays += max(0.0, cos(20 * angle - 0.28)) ** 24 * 2.5

        if r < 15.2:
            # Nearly black lunar silhouette, with a subtle warm rim.
            edge = max(0.0, (r - 12.8) / 2.4)
            rgba = (round(7 + 18 * edge), round(8 + 5 * edge), round(15 + 2 * edge), 255)
        elif r < 17.1:
            rgba = (255, 219, 142, 255)
        elif r < 19.2:
            rgba = (255, 159, 58, round(225 - (r - 17.1) * 43))
        else:
            reach = 24.0 + rays
            if r > reach:
                rgba = (0, 0, 0, 0)
            else:
                fade = exp(-(r - 19.2) / 3.3)
                taper = min(1.0, max(0.0, (reach - r) / 2.1))
                alpha = round(170 * fade * taper)
                rgba = (255, 137, 42, alpha)

        pixels.extend(rgba)

header = b"\x89PNG\r\n\x1a\n"
ihdr = chunk(b"IHDR", pack(">IIBBBBB", SIZE, SIZE, 8, 6, 0, 0, 0))
raw = chunk(b"IDAT", compress(bytes(pixels), 9))
OUTPUT.parent.mkdir(parents=True, exist_ok=True)
OUTPUT.write_bytes(header + ihdr + raw + chunk(b"IEND", b""))
print(OUTPUT)
