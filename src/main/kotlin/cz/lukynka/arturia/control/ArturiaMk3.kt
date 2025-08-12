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
//            animatePads(0, 255, 0)
//            animateCenterPulse(0, 255, 0)
            animateRainbowWave()
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

    fun animateRainbowWave(fadeSteps: Int = 10, delay: Long = 10) {
        setAllPadsColor(0, 0, 0)

        val padPairs = listOf(
            Pair(Button.PAD_4, Button.PAD_5),
            Pair(Button.PAD_3, Button.PAD_6),
            Pair(Button.PAD_2, Button.PAD_7),
            Pair(Button.PAD_1, Button.PAD_8)
        )

        var hueOffset = 0.0

        while (true) {
            val currentColor = hslToRgb((hueOffset % 360.0), 1.0, 0.5)
            val r = currentColor.red
            val g = currentColor.green
            val b = currentColor.blue

            for (i in 0..fadeSteps) {
                val rStep = (r * i) / fadeSteps
                val gStep = (g * i) / fadeSteps
                val bStep = (b * i) / fadeSteps
                setButtonColor(padPairs[0].first, rStep, gStep, bStep)
                setButtonColor(padPairs[0].second, rStep, gStep, bStep)
                Thread.sleep(delay)
            }

            for (pairIndex in 1 until padPairs.size) {
                val currentPair = padPairs[pairIndex]
                val previousPair = padPairs[pairIndex - 1]

                for (i in 0..fadeSteps) {
                    val rIn = (r * i) / fadeSteps
                    val gIn = (g * i) / fadeSteps
                    val bIn = (b * i) / fadeSteps

                    val rOut = (r * (fadeSteps - i)) / fadeSteps
                    val gOut = (g * (fadeSteps - i)) / fadeSteps
                    val bOut = (b * (fadeSteps - i)) / fadeSteps

                    setButtonColor(currentPair.first, rIn, gIn, bIn)
                    setButtonColor(currentPair.second, rIn, gIn, bIn)
                    setButtonColor(previousPair.first, rOut, gOut, bOut)
                    setButtonColor(previousPair.second, rOut, gOut, bOut)

                    Thread.sleep(delay)
                }
            }

            val lastPair = padPairs.last()
            for (i in fadeSteps downTo 0) {
                val rStep = (r * i) / fadeSteps
                val gStep = (g * i) / fadeSteps
                val bStep = (b * i) / fadeSteps
                setButtonColor(lastPair.first, rStep, gStep, bStep)
                setButtonColor(lastPair.second, rStep, gStep, bStep)
                Thread.sleep(delay)
            }

            hueOffset += 25.0
            Thread.sleep(429)
        }
    }

    fun animateCenterPulse(r: Int, g: Int, b: Int, fadeSteps: Int = 10, delay: Long = 10) {
        setAllPadsColor(0, 0, 0)

        val padPairs = listOf(
            Pair(Button.PAD_4, Button.PAD_5),
            Pair(Button.PAD_3, Button.PAD_6),
            Pair(Button.PAD_2, Button.PAD_7),
            Pair(Button.PAD_1, Button.PAD_8)
        )

        while (true) {
            padPairs.forEach { (pad1, pad2) ->
                for (i in 0..fadeSteps) {
                    val gStep = (g * i) / fadeSteps
                    setButtonColor(pad1, 0, gStep, 0)
                    setButtonColor(pad2, 0, gStep, 0)
                    Thread.sleep(delay)
                }
            }

            Thread.sleep(250)

            padPairs.reversed().forEach { (pad1, pad2) ->
                for (i in fadeSteps downTo 0) {
                    val gStep = (g * i) / fadeSteps
                    setButtonColor(pad1, 0, gStep, 0)
                    setButtonColor(pad2, 0, gStep, 0)
                    Thread.sleep(delay)
                }
            }

            Thread.sleep(250)
        }
    }
}