package jtop

import jtop.jmx.{JMX, Client, ClientConfiguration}

object Main extends scala.scalajs.js.JSApp {
  import scala.scalajs.js
  import js.Dynamic
  import js.Dynamic.global

  def main(): Unit = {
    println("Creating client")
    val client = JMX.createClient(ClientConfiguration(host = "localhost", port = 8855))

    println("Connecting...")
    client.connect()
    client.on("connect", () => {
      refresh(client)
    })
  }

  def refresh(client: Client): Unit = {
    client.getAttribute("java.lang:type=Memory", "HeapMemoryUsage", (data: Dynamic) => {
      val used = data.getSync("used")
      println(s"HeapMemoryUsage used: $used")
    })

    client.getAttribute("java.lang:type=Threading", "ThreadCount", (count: Dynamic) => {
      println(s"ThreadCount: $count")
    })

    client.getAttribute("java.lang:type=ClassLoading", "TotalLoadedClassCount", (count: Dynamic) => {
      println(s"TotalLoadedClassCount: $count")
    })

    // HEAP

    client.getAttribute("java.lang:type=MemoryPool,name=PS Eden Space", "Usage", (data: Dynamic) => {
      val used = data.getSync("used")
      println(s"PS Eden Space used: $used")
    })

    client.getAttribute("java.lang:type=MemoryPool,name=PS Survivor Space", "Usage", (data: Dynamic) => {
      val used = data.getSync("used")
      println(s"PS Survivor Space used: $used")
    })

    client.getAttribute("java.lang:type=MemoryPool,name=PS Old Gen", "Usage", (data: Dynamic) => {
      val used = data.getSync("used")
      println(s"PS Old Gen used: $used")
    })

    // NON_HEAP

    client.getAttribute("java.lang:type=MemoryPool,name=Code Cache", "Usage", (data: Dynamic) => {
      val used = data.getSync("used")
      println(s"Code Cache used: $used")
    })

    client.getAttribute("java.lang:type=MemoryPool,name=Compressed Class Space", "Usage", (data: Dynamic) => {
      val used = data.getSync("used")
      println(s"Compressed Class Space used: $used")
    })

    client.getAttribute("java.lang:type=MemoryPool,name=Metaspace", "Usage", (data: Dynamic) => {
      val used = data.getSync("used")
      println(s"Metaspace used: $used")
    })
  }

}
