package cz.lukynka.arturia.control

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled

fun getInitializationMessage(): ByteBuf {
    val buffer = Unpooled.buffer()
    // no clue what these bytes are
    buffer.writeByte(0x00)
    buffer.writeByte(0x20)
    buffer.writeByte(0x6B)
    buffer.writeByte(0x7F)
    // arturia brand id
    buffer.writeByte(0x42)
    buffer.writeByte(0x02)
    buffer.writeByte(0x02)
    buffer.writeByte(0x40) // no clue
    buffer.writeByte(0x6A) // no clue
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
        buffer.writeByte(0x00) // no clue
        buffer.writeByte(0x20) // no clue
        buffer.writeByte(0x6B) // no clue
        buffer.writeByte(0x7F) // no clue
        buffer.writeByte(0x42) // brand
        buffer.writeByte(0x02) // brand
        buffer.writeByte(0x02) // brand
        buffer.writeByte(0x16) // no clue
        buffer.writeByte(id.ordinal) // button id
        buffer.writeByte(red) // red
        buffer.writeByte(green) // green
        buffer.writeByte(blue) // blue
    }
}

enum class Button {
    SHIFT,
    OCTAVE_MINUS,
    HOLD,
    OCTAVE_PLUS,
    PAD_1,
    PAD_2,
    PAD_3,
    PAD_4,
    PAD_5,
    PAD_6,
    PAD_7,
    PAD_8;
}