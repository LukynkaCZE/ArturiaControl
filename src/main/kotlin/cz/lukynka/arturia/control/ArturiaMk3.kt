package cz.lukynka.arturia.control

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.netty.buffer.Unpooled
import javax.sound.midi.MidiDevice
import javax.sound.midi.MidiSystem

object ArturiaMk3 {

    var device: MidiDevice? = null

    fun connect(deviceIndex: Int) {
        val deviceInfo = MidiSystem.getMidiDeviceInfo()[deviceIndex]
        device = MidiSystem.getMidiDevice(deviceInfo)

        device!!.open()
        log("Connected to device ${device!!.deviceInfo.name} (${deviceInfo.vendor} ${deviceInfo.version}", LogType.SUCCESS)
//        device!!.receiver.send(createSysexMessageFromBuffer(getSysExMessage(getInitializationMessage())), -1)

        Thread.startVirtualThread {
            animatePads(0, 255, 0)
        }
    }

    fun setButtonColor(buttonIndex: Int, r: Int, g: Int, b: Int) {
        setButtonColor(Button.entries[buttonIndex], r, g, b)
    }

    fun setButtonColor(button: Button, r: Int, g: Int, b: Int) {
        require(device != null)

        val color = Triple(
            (r / 2.0).toInt(),
            (g / 2.0).toInt(),
            (b / 2.0).toInt()
        )

        val buffer = Unpooled.buffer()
        val message = SetColorMessage(button, color.first, color.second, color.third)
        message.write(buffer)
        device!!.receiver.send(createSysexMessageFromBuffer(getSysExMessage(buffer)), -1)
    }

    fun setAllPadsColor(r: Int, g: Int, b: Int) {

        (Button.PAD_1.ordinal..Button.PAD_8.ordinal).forEach { buttonIndex ->
            setButtonColor(Button.entries[buttonIndex], r, g, b)
        }
    }

    fun dispose() {
        setAllPadsColor(0, 0, 0)
        device?.close()
        log("Disconnected from MIDI Device", LogType.SUCCESS)
    }

    fun fadeIn(button: Button, r: Int, g: Int, b: Int, steps: Int = 10, delay: Long = 10) {
        for (i in 0..steps) {
            val rStep = (r * i) / steps
            val gStep = (g * i) / steps
            val bStep = (b * i) / steps
            setButtonColor(button, rStep, gStep, bStep)
            Thread.sleep(delay)
        }
    }

    fun fadeOut(button: Button, steps: Int = 10, delay: Long = 10) {
        val currentG = 255
        for (i in steps downTo 0) {
            val gStep = (currentG * i) / steps
            setButtonColor(button, 0, gStep, 0)
            Thread.sleep(delay)
        }
    }

    fun animatePads(r: Int, g: Int, b: Int, fadeSteps: Int = 10, delay: Long = 10) {
        setAllPadsColor(0, 0, 0)

        for (i in 0..fadeSteps) {
            val gStep = (g * i) / fadeSteps
            setButtonColor(Button.PAD_1, 0, gStep, 0)
            Thread.sleep(delay)
        }

        (Button.PAD_2.ordinal..Button.PAD_8.ordinal).forEachIndexed { index, currentPadIndex ->
            val previousPadIndex = (Button.PAD_1.ordinal + index)
            val currentPad = Button.entries[currentPadIndex]
            val previousPad = Button.entries[previousPadIndex]

            for (i in 0..fadeSteps) {
                val gIn = (g * i) / fadeSteps
                val gOut = (g * (fadeSteps - i)) / fadeSteps

                setButtonColor(currentPad, 0, gIn, 0)
                setButtonColor(previousPad, 0, gOut, 0)

                Thread.sleep(delay)
            }
        }

        for (i in fadeSteps downTo 0) {
            val gStep = (g * i) / fadeSteps
            setButtonColor(Button.PAD_8, 0, gStep, 0)
            Thread.sleep(delay)
        }
    }


}