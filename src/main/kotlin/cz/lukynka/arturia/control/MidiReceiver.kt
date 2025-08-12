package cz.lukynka.arturia.control

import cz.lukynka.prettylog.log
import io.netty.buffer.ByteBuf
import javax.sound.midi.MidiMessage
import javax.sound.midi.Receiver
import javax.sound.midi.ShortMessage

class MidiReceiver : Receiver {

    override fun close() {
    }

    override fun send(message: MidiMessage, timeStamp: Long) {
        log("$timeStamp - $message")
    }



}