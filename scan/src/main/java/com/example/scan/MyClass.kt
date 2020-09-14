package com.example.scan

import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.BorderFactory
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JOptionPane
import kotlin.random.Random
import kotlin.system.exitProcess


object MyClass {
    private const val count = 20
    private const val nei = 100
    private val jFrame = JFrame()

    @JvmStatic
    fun main(args: Array<String>) {
        jFrame.layout = GridLayout(count, count, 0, 0)
        initJButtons()
        jButtons.forEachIndexed { index, it ->
            jFrame.add(it)
            it.apply {
                addMouseListener(
                        object : MouseAdapter() {
                            override fun mousePressed(p0: MouseEvent) {
                                println("addMouseWheelListener ${p0.button}")
                                if (p0.button == MouseEvent.BUTTON1) {
                                    when (state) {
                                        0 -> {
                                            state = 1
                                            if (num == 0) {
                                                openNum0(index)
                                            } else if (num == -1) {
                                                openAll()
                                                showFailedDialog("你被炸死了,要重新来一局吗?")
                                            }
                                            checkResult()
                                        }
                                    }
                                } else if (p0.button == MouseEvent.BUTTON3) {
                                    when (state) {
                                        0 -> {
                                            state = 2
                                        }
                                        2 -> {
                                            state = 0
                                        }
                                    }
                                }
                            }
                        })
            }
        }

        jFrame.title = "扫雷"
        jFrame.setSize(count * 30, count * 30)
        jFrame.minimumSize = Dimension(count * 30, count * 30)
        jFrame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        jFrame.setLocation(300, 0)
        jFrame.isVisible = true

    }

    private fun openAll() {
        jButtons.forEach {
            it.state = 1
        }
    }

    private fun checkResult() {
        jButtons.forEach {
            if (it.state != 1 && it.num != -1) {
                return
            } else {
                println("check:${it.state} ..${it.num}")
            }
        }
        openAll()
        showFailedDialog("恭喜了，你赢了! 用时:${(System.currentTimeMillis() - time) / 1000}s")
    }

    private fun showFailedDialog(result: String) {
        val showConfirmDialog = JOptionPane.showConfirmDialog(jFrame, result, "提示", JOptionPane.YES_NO_OPTION)
        if (showConfirmDialog == 0) {
            restJButtons()
        } else {
            exitProcess(0)
        }
    }

    private fun openNum0(index: Int) {
        if (jButtons[index].open) {
            return
        }
        jButtons[index].open = true
        val iy = index / count
        val ix = index % count
        for (y in iy - 1..iy + 1) {
            for (x in ix - 1..ix + 1) {
                val i1 = y * count + x
                if (x in 0 until count && y in 0 until count && index != i1) {
                    val jMyButton1 = jButtons[i1]
                    if (jMyButton1.num == 0) {
                        jButtons[index].state = 1
                        openNum0(i1)
                    } else if (jMyButton1.num != -1) {
                        jMyButton1.state = 1
                    }
                }
            }
        }
    }


    private var intArray = arrayListOf<Int>()
    private var jButtons = arrayListOf<JMyButton>()

    var time = 0L

    private fun initJButtons() {
        time = System.currentTimeMillis()
        var i = 0
        while (i < nei) {
            val nextInt = Random.nextInt(count * count)
            println("nextInt: $nextInt")
            if (!intArray.contains(nextInt)) {
                i++
                intArray.add(nextInt)
            }
        }

        repeat(count * count) {
            if (intArray.contains(it)) {
                jButtons.add(JMyButton().apply {
                    num = -1
                })
            } else {
                jButtons.add(JMyButton())
            }
        }

        intArray.forEach {
            val iy = it / count
            val ix = it % count
            for (y in iy - 1..iy + 1) {
                for (x in ix - 1..ix + 1) {
                    val i1 = y * count + x
                    if (x in 0 until count && y in 0 until count && !intArray.contains(i1)) {
                        jButtons[i1].num++
                    }
                }
            }
        }
    }


    private fun restJButtons() {
        time = System.currentTimeMillis()
        intArray.clear()
        var i = 0
        while (i < nei) {
            val nextInt = Random.nextInt(count * count)
            println("nextInt: $nextInt")
            if (!intArray.contains(nextInt)) {
                i++
                intArray.add(nextInt)
            }
        }

        repeat(count * count) {
            jButtons[it].reset()
            if (intArray.contains(it)) {
                jButtons[it].apply {
                    num = -1
                }
            }
        }

        intArray.forEach {
            val iy = it / count
            val ix = it % count
            for (y in iy - 1..iy + 1) {
                for (x in ix - 1..ix + 1) {
                    val i1 = y * count + x
                    if (x in 0 until count && y in 0 until count && !intArray.contains(i1)) {
                        jButtons[i1].num++
                    }
                }
            }
        }
    }
}


class JMyButton : JButton() {
    var open = false
    var num = 0
        set(value) {
            field = value
            checkState()
        }

    private fun checkState() {
        foreground = when {
            num > 4 -> {
                Color.MAGENTA
            }
            num > 2 -> {
                Color.RED
            }
            num > 1 -> {
                Color.BLUE
            }
            else -> {
                Color.CYAN
            }
        }

        background = when (state) {
            0 -> {
                text = ""
                Color.BLUE
            }
            1 -> {
                if (num == -1) {
                    Color.RED
                } else {
                    text = if (num == 0) {
                        ""
                    } else {
                        num.toString()
                    }
                    Color.LIGHT_GRAY
                }
            }
            2 -> {
                Color.GREEN
            }
            else -> {
                Color.BLUE
            }
        }

    }

    var state = 0
        set(value) {
            field = value
            checkState()
        }

    init {
        font = Font("宋体", Font.BOLD, 24)
        margin = Insets(0, 0, 0, 0)
        border = BorderFactory.createRaisedBevelBorder()
        checkState()
    }

    fun reset() {
        open = false
        num = 0
        state = 0
    }
}