package com.github.hanseter.json.queryengine.fx

import javafx.application.Application
import javafx.stage.Stage
import javafx.scene.Scene
import org.json.JSONObject
import javafx.scene.layout.StackPane
import java.io.File
import kotlin.random.Random
import java.nio.file.Files
import java.nio.file.Paths
import javafx.scene.control.Button
import javafx.scene.layout.VBox
import kotlin.streams.asStream
import com.github.hanseter.json.queryengine.QuerieableData
import com.github.hanseter.json.queryengine.QueryExecutionContext

fun main(args: Array<String>) {
	Application.launch(JsonQueryUiExample::class.java, *args)
}

class JsonQueryUiExample : Application() {
	//	private val objects: Map<String, JSONObject> =
//		mapOf(
//			"1" to JSONObject().put("int", 42),
//			"2" to JSONObject().put("int", 815).put("nested", JSONObject().put("decimal", 1.33)),
//			"3" to JSONObject().put("int", 78).put("nested", JSONObject().put("decimal2", 1.33))
//		)
	private val fieldNames = JSONObject()
	private val names: List<String>

	init {
		names = File(ClassLoader.getSystemResource("names.txt").getFile()).readLines()
		names.fold(fieldNames) { acc, it ->
			acc.put(it, "")
		}
//		objects.forEach {k,v ->
//			Files.write(Paths.get("V:/test/$k.json"),v.toString(1).toByteArray())
//		}
	}

	override fun start(primaryStage: Stage) {
		val queryUi = QueryRunnerUi({
			QueryExecutionContextForTests()
		}, fieldNames)
		primaryStage.setScene(
			Scene(VBox(queryUi.node), 400.0, 400.0)
		)
		primaryStage.show()
	}

	private fun createJSON(keys: List<String>): JSONObject {
		val entryCount = Random.nextInt(50, 150)
		return generateSequence { keys.get(Random.nextInt(0, 999)) }.take(entryCount).fold(JSONObject()) { acc, it ->
			acc.put(it, Random.nextInt(-50, 150))
		}
	}

	private class QuerieableDataForTests(val id: Int, override val data: JSONObject) : QuerieableData {}

	private inner class QueryExecutionContextForTests() : QueryExecutionContext<QuerieableDataForTests> {
		val elementsIterator = generateSequence { createJSON(names) }.withIndex().map { (i, it) ->
			QuerieableDataForTests(i, it)
		}.iterator()

		override fun getNextElement(): JsonQueryUiExample.QuerieableDataForTests? = elementsIterator.next()

		override fun addMatch(match: JsonQueryUiExample.QuerieableDataForTests) {
		}

		override fun executionFinished() {
		}
	}
}