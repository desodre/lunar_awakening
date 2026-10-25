package com.lunarawakening

/** Estado recebido do servidor; usado apenas no lado cliente. */
object EventStateBridge {
    @JvmField
    @Volatile
    var clientEvent: CelestialEvent = CelestialEvent.NONE
}
