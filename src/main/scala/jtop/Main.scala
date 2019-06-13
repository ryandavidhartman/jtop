package jtop

import jtop.jmx.{JMX, Client, ClientConfiguration}

object Main extends scala.scalajs.js.JSApp {
  import scala.scalajs.js
  import js.Dynamic
  import js.Dynamic.global
  import js.JSConverters._

  val maxRetentionSize = 100

  var screen: js.Dynamic = null
  var heapUsageLine: js.Dynamic = null
  var loadedClassesLine: js.Dynamic = null
  var threadsLine: js.Dynamic = null
  var heapUsageBars: js.Dynamic = null
  var offHeapUsageBars: js.Dynamic = null

  var heapUsagedInMbData = scala.collection.mutable.ArraySeq[Double](0.0)
  var loadedClassesData = scala.collection.mutable.ArraySeq[Double](0.0)
  var threadsData = scala.collection.mutable.ArraySeq[Double](0.0)
  val heapUsageBarsData = scala.collection.mutable.ArraySeq[Double](0.0, 0.0, 0.0, 0.0, 0.0)
  val offHeapUsageBarsData = scala.collection.mutable.ArraySeq[Double](0.0, 0.0, 0.0)

  val host = "localhost"
  val port = 8855 // NOTE: this would be better as a command-line argument
  val refreshInterval = 300


  /**
   * The entry point of our jtop application.
   */
  def main(): Unit = {
    initScreen()

    val client = JMX.createClient(ClientConfiguration(host = host, port = port))
    client.connect()
    client.on("connect", () => {
      refreshData(client)
      renderScreen()

      global.setInterval(() => {
        refreshData(client)
        renderScreen()
      }, refreshInterval)
    })
  }

  def initScreen() = {
    val blessed = global.require("blessed")
    val contrib = global.require("blessed-contrib")

    screen = blessed.screen()

    val grid = js.Dynamic.newInstance(contrib.grid)(js.Dynamic.literal(rows = 2, cols = 3, screen = screen))

    heapUsageLine = grid.set(0, 0, 1, 1, contrib.line,
      js.Dynamic.literal(showNthLabel = 9999, label = "Heap Memory Usage (Mb)",
        style = js.Dynamic.literal(line = "blue", text = "white"))
    )
    loadedClassesLine = grid.set(0, 1, 1, 1, contrib.line,
      js.Dynamic.literal(showNthLabel = 9999, label = "Loaded Classes",
        style = js.Dynamic.literal(line = "green", text = "white"))
    )

    threadsLine = grid.set(0, 2, 1, 1, contrib.line,
      js.Dynamic.literal(showNthLabel = 9999, label = "Threads",
        style = js.Dynamic.literal(line = "red", text = "white"))
    )

    heapUsageBars = grid.set(1, 0, 1, 2, contrib.bar,
      js.Dynamic.literal(barWidth = 5, barSpacing = 10, maxHeight = 10, label = "Heap Usage (%)")
    )
    offHeapUsageBars = grid.set(1, 2, 1, 1, contrib.bar,
      js.Dynamic.literal(barWidth = 5, barSpacing = 10, maxHeight = 10, label = "Off-Heap Usage (%)")
    )

  }


  /**
   * Collect new data from JMX
   */
  def refreshData(client: Client): Unit = {
    client.getAttribute("java.lang:type=Memory", "HeapMemoryUsage", (data: Dynamic) => {
      val used = data.getSync("used").toString.toDouble / 1048576.0
      heapUsagedInMbData = (heapUsagedInMbData :+ used).takeRight(maxRetentionSize)
    })

    client.getAttribute("java.lang:type=Threading", "ThreadCount", (count: Dynamic) => {
      threadsData = (threadsData :+ count.toString.toDouble).takeRight(maxRetentionSize)
    })

    client.getAttribute("java.lang:type=ClassLoading", "TotalLoadedClassCount", (count: Dynamic) => {
      loadedClassesData = (loadedClassesData :+ count.toString.toDouble).takeRight(maxRetentionSize)
    })

    def percentFor(data: Dynamic): Double = {
      val used = data.getSync("used").toString.toDouble
      val max = data.getSync("max").toString.toDouble
      math.abs(math.ceil((used / max) * 100.0D))
    }
    // HEAP

    client.getAttribute("java.lang:type=MemoryPool,name=Par Eden Space", "Usage", (data: Dynamic) => {
      heapUsageBarsData(0) = percentFor(data)
    })

    client.getAttribute("java.lang:type=MemoryPool,name=Par Survivor Space", "Usage", (data: Dynamic) => {
      heapUsageBarsData(1) = percentFor(data)
    })

    client.getAttribute("java.lang:type=MemoryPool,name=CMS Old Gen", "Usage", (data: Dynamic) => {
      heapUsageBarsData(2) = percentFor(data)
    })

    client.getAttribute("java.lang:type=MemoryPool,name=CMS Perm Gen", "Usage", (data: Dynamic) => {
      heapUsageBarsData(3) = percentFor(data)
    })

    client.getAttribute("java.lang:type=MemoryPool,name=Code Cache", "Usage", (data: Dynamic) => {
      heapUsageBarsData(4) = percentFor(data)
    })

    // NON_HEAP

    client.getAttribute("java.lang:type=MemoryPool,name=Code Cache", "Usage", (data: Dynamic) => {
      offHeapUsageBarsData(1) = percentFor(data)
    })

    client.getAttribute("java.lang:type=MemoryPool,name=Compressed Class Space", "Usage", (data: Dynamic) => {
      offHeapUsageBarsData(2) = percentFor(data)
    })

    client.getAttribute("java.lang:type=MemoryPool,name=Metaspace", "Usage", (data: Dynamic) => {
      val used = data.getSync("committed").toString.toDouble
      val max = used * 5
      offHeapUsageBarsData(0) = math.abs(math.ceil((used / max) * 100.0D))
    })
  }


  /**
   * Update the data on the screen and render it again
   */
  def renderScreen(): Unit = {
    val heapUsageLineDataObj = js.Dynamic.literal("x" ->  " ", "y" -> heapUsagedInMbData.toJSArray)
    heapUsageLine.setData(heapUsageLineDataObj)

    val loadedClassesDataObj = js.Dynamic.literal("x" ->  " ", "y" -> loadedClassesData.toJSArray)
    loadedClassesLine.setData(loadedClassesDataObj)

    val threadLineDataObj = js.Dynamic.literal("x" -> " ", "y" -> threadsData.toJSArray)
    threadsLine.setData(threadLineDataObj)

    val heapBarTitles = Vector[String]("Par Eden", "Par Survivor", "CMS Old Gen", "CMS Perm Gen", "Code Cache")
    val heapUsageBarsDataObj = js.Dynamic.literal("titles" -> heapBarTitles.toJSArray, "data" -> heapUsageBarsData.toJSArray)
    heapUsageBars.setData(heapUsageBarsDataObj)

    val offHeapBarTitles = Vector[String]("Meta", "Cache", "Compr")
    val offHeapUsageBarsDataObj = js.Dynamic.literal("titles" -> offHeapBarTitles.toJSArray, "data" -> offHeapUsageBarsData.toJSArray)
    offHeapUsageBars.setData(offHeapUsageBarsDataObj)

    screen.render()
  }

}

