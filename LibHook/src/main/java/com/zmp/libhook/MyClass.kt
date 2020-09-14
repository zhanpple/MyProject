package com.zmp.libhook

import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import org.openqa.selenium.WebDriver
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.DatagramPacket
import java.net.InetAddress
import java.net.MulticastSocket
import java.net.Socket
import java.util.*

object MyClass {

    private const val port = 8006

    private var webDriver: WebDriver? = null
    private var debug = false
    private var ip = "192.168.12.1"

    @JvmStatic
    fun main(args: Array<String>) {


        println("main:${args.contentToString()}")

        webDriver = create()

        if (args.contains("0")) {
            debug = true
        }

        if (args.size >= 2) {
            while (true) {
                try {
                    initTcp2(args[1])
                } catch (e: Exception) {
                    Thread.sleep(2000)
                    e.printStackTrace()
                }
            }
        }

        while (true) {
            val group = InetAddress.getByName("224.0.0.1")
            var ds: MulticastSocket? = null
            try {
                val port = 8003
                ds = MulticastSocket(port) // 1.创建一个用于发送和接收的MulticastSocket组播套接字对象
                ds.joinGroup(group) // 3.使用组播套接字joinGroup(),将其加入到一个组播
                println("joinGroup")
                val buf = ByteArray(8192)
                val dp = DatagramPacket(buf, buf.size)
                while (true) {
                    println("receive")
                    ds.soTimeout = 10 * 1000
                    ds.receive(dp)
                    val a = String(dp.data, 0, dp.length)
                    if (debug) {
                        println("receive:$a")
                    }
                    if ("I am 02" == a) {
                        initTcp(dp.address)
                        break
                    }
                }
            } catch (e1: Exception) {
                e1.printStackTrace()
                Thread.sleep(2000)
                try {
                    ds?.leaveGroup(group)
                } catch (e: Exception) {
                }
                ds?.close()
            }
        }
    }


    private fun initTcp(address: InetAddress) {
        println("initTcp:$address")
        val socket = Socket(address, port)
        socket.keepAlive = true;//开启保持活动状态的套接字
        socket.soTimeout = 5000//设置超时时间
        val inputStream = socket.getInputStream()
        val dataInputStream = DataInputStream(inputStream)
        val dataOutStream = DataOutputStream(socket.getOutputStream())
        dataOutStream.writeUTF(Gson().toJson(SitesMsg(SitesMsg.HEART_TYPE, "heart")))
        while (true) {
            if (debug) {
                println("readUTF:")
            }
            try {
                val readUTF = dataInputStream.readUTF()
                handSites(dataOutStream, readUTF)
            } catch (e: Exception) {
                e.printStackTrace()
                socket.close()
                throw e
            }
        }
    }


    private fun initTcp2(address: String) {
        println("initTcp2:$address")
        val socket = Socket(address, port)
        socket.keepAlive = true;//开启保持活动状态的套接字
        socket.soTimeout = 5000//设置超时时间
        val inputStream = socket.getInputStream()
        val dataInputStream = DataInputStream(inputStream)
        val dataOutStream = DataOutputStream(socket.getOutputStream())
        dataOutStream.writeUTF(Gson().toJson(SitesMsg(SitesMsg.HEART_TYPE, "heart")))
        while (true) {
            if (debug) {
                println("readUTF:")
            }
            try {
                val readUTF = dataInputStream.readUTF()
                handSites(dataOutStream, readUTF)
            } catch (e: Exception) {
                e.printStackTrace()
                socket.close()
                throw e
            }
        }
    }


    private fun handSites(dataOutStream: DataOutputStream, readUTF: String) {
        val fromJson = Gson().fromJson<SitesMsg>(readUTF, SitesMsg::class.java)

        fromJson?.let {
            if (debug) {
                println("readUTF:$readUTF")
            } else if (it.type != SitesMsg.HEART_TYPE) {
                println("readUTF:$readUTF")
            }
            when (it.type) {
                SitesMsg.HEART_TYPE -> {
                    GlobalScope.async {
                        delay(2000)
                        dataOutStream.writeUTF(readUTF)
                        dataOutStream.flush()
                    }
                }
                SitesMsg.URL_TYPE -> {
                    webDriver?.get(fromJson.content)
                }

                SitesMsg.OPEN_TYPE -> {
                    webDriver ?: create()
                }

                SitesMsg.CLOSE_TYPE -> {
                    webDriver?.quit()
                    webDriver = null
                }
                else -> {

                }
            }
        }
    }


    private fun create(): WebDriver? {
        val webDriver: WebDriver? = null
        try {
            val config: Properties = DriverTools.getProperties()
            val chrome = config.getProperty("chrome")
            val homeUrl = config.getProperty("homeUrl")
            println("homeUrl:$homeUrl")
            return DriverTools.getWebDriver(chrome, homeUrl)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        } finally {
            if (webDriver != null) {
                try {
                    Thread.sleep(5000)
                    webDriver.quit()
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        }
        return null
    }

}

