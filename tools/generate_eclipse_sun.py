"""Draw a square solar eclipse matching Minecraft's square sun silhouette."""

from pathlib import Path
from struct import pack
from zlib import compress, crc32


SIZE = 32
MID = (SIZE - 1) / 2
OUTPUT = (
    Path(__file__).resolve().parents[1]
    / "src/client/resources/assets/lunar_awakening/textures/environment/celestial/eclipse_sun.png"
)


def chunk(kind: bytes, data: bytes) -> bytes:
    return pack(">I", len(data)) + kind + data + pack(">I", crc32(kind + data) & 0xFFFFFFFF)


pixels = bytearray()
for y in range(SIZE):
    pixels.append(0)  # PNG filter: none
    for x in range(SIZE):
        # Chebyshev distance keeps the shadow, corona, and glow square.
        square_radius = max(abs(x - MID), abs(y - MID))
        if square_radius < 7.0:
            rgba = (8, 9, 15, 255)
        elif square_radius < 8.0:
            rgba = (255, 231, 166, 255)
        elif square_radius < 9.0:
            rgba = (255, 178, 74, 240)
        elif square_radius < 13.5:
            fade = (13.5 - square_radius) / 4.5
            alpha = round(150 * fade**1.5)
            rgba = (255, 153, 59, alpha)
        else:
            rgba = (0, 0, 0, 0)

        pixels.extend(rgba)

header = b"\x89PNG\r\n\x1a\n"
ihdr = chunk(b"IHDR", pack(">IIBBBBB", SIZE, SIZE, 8, 6, 0, 0, 0))
raw = chunk(b"IDAT", compress(bytes(pixels), 9))
OUTPUT.parent.mkdir(parents=True, exist_ok=True)
OUTPUT.write_bytes(header + ihdr + raw + chunk(b"IEND", b""))
print(OUTPUT)
