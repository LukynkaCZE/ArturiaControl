package cz.lukynka.arturia.control

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.netty.buffer.ByteBuf
import javax.sound.midi.MidiMessage
import javax.sound.midi.MidiSystem
import javax.sound.midi.SysexMessage
import kotlin.system.exitProcess

fun main() {
    val midiDevices = MidiSystem.getMidiDeviceInfo()
    log("Please type number based on what MIDI device you want to use:", LogType.USER_ACTION)
    println()
    midiDevices.forEachIndexed { index, device ->
        val deviceInfo = MidiSystem.getMidiDevice(device)
        if (deviceInfo.maxReceivers == 0) return@forEachIndexed
        println("${if (index < 10) "$index. " else "$index."}  -  ${device.name} - ${device.description} (${device.vendor} ${device.version})")
    }
    println()

    val line = readln()
    val deviceIndex = line.toIntOrNull()
    if (deviceIndex == null) {
        log("\"$line\" is not valid device index", LogType.ERROR)
        exitProcess(0)
    }

    ArturiaMk3.connect(deviceIndex)

    log("Press Enter to stop", LogType.USER_ACTION)
    readlnOrNull()
    ArturiaMk3.dispose()
    exitProcess(0)
}

fun createSysexMessageFromBuffer(buffer: ByteBuf): MidiMessage {
    val bytes = ByteArray(buffer.readableBytes())
    buffer.readBytes(bytes)
    val sysexMessage = SysexMessage()
    sysexMessage.setMessage(bytes, bytes.size)
    return sysexMessage
}