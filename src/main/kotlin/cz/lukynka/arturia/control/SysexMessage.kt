package cz.lukynka.arturia.control

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled

fun getInitializationMessage(): ByteBuf {
    val buffer = Unpooled.buffer()
    buffer.writeByte(0x00)
    buffer.writeByte(0x20)
    buffer.writeByte(0x6B)
    buffer.writeByte(0x7F)
    buffer.writeByte(0x42)
    buffer.writeByte(0x02)
    buffer.writeByte(0x02)
    buffer.writeByte(0x40)
    buffer.writeByte(0x6A)
    buffer.writeByte(0x21) // 21 - Arturia, 20 - DAW
    return buffer
}

fun getSysExMessage(payload: ByteBuf): ByteBuf {
    val buffer = Unpooled.buffer()
    buffer.writeByte(0xF0) // SysEx begin
    buffer.writeBytes(payload) // payload
    buffer.writeByte(0xF7) // SysEx end

    return buffer
}

class SetColorMessage(
    val id: Button,
    val red: Int,
    val green: Int,
    val blue: Int
) {
    constructor(id: Int, red: Int, green: Int, blue: Int): this(Button.entries[id], red, green, blue)

    fun write(buffer: ByteBuf) {
        buffer.writeByte(0x00)
        buffer.writeByte(0x20)
        buffer.writeByte(0x6B)
        buffer.writeByte(0x7F)
        buffer.writeByte(0x42)
        buffer.writeByte(0x02)
        buffer.writeByte(0x02)
        buffer.writeByte(0x16)
        buffer.writeByte(id.ordinal) // button id
        buffer.writeByte(red) // red
        buffer.writeByte(green) // green
        buffer.writeByte(blue) // blue
    }
}

enum class Button(val hex: Byte) {
    SHIFT(0),
    OCTAVE_MINUS(1),
    HOLD(2),
    OCTAVE_PLUS(3),
    PAD_1(4),
    PAD_2(5),
    PAD_3(6),
    PAD_4(7),
    PAD_5(8),
    PAD_6(9),
    PAD_7(0x0A),
    PAD_8(0x0B);
}