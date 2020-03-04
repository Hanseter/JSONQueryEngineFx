package com.github.hanseter.json.queryengine.fx

import com.github.hanseter.json.queryengine.QuerieableData
import java.util.stream.Stream
import org.json.JSONObject
import javafx.scene.layout.VBox
import javafx.scene.control.Button
import java.util.concurrent.atomic.AtomicLong
import java.util.stream.Collectors
import com.github.hanseter.json.queryengine.QueryExecutor
import javafx.scene.control.ButtonBar
import javafx.scene.layout.HBox
import com.github.hanseter.json.queryengine.Query
import javafx.application.Platform
import javafx.scene.control.Label
import javafx.scene.layout.Priority
import java.util.concurrent.Executors
import java.util.concurrent.ForkJoinPool
import test.jsonquery.QueryEditorUi
import com.github.hanseter.json.queryengine.QueryExecutionContextFx
import com.github.hanseter.json.queryengine.QueryExecutionContext

class QueryRunnerUi<T : QuerieableData>(
	executionContextProvider: () -> (QueryExecutionContext<T>),
	fieldNames: JSONObject
) {
	val builderUi = QueryEditorUi(fieldNames)
	val runButton = Button("Run")
	val pauseResumeButton = Button("Pause")
	val stopButton = Button("Stop")
	val progressLabel = Label()
	val node = VBox(
		builderUi.node,
		HBox(
			progressLabel,
			HBox().apply { HBox.setHgrow(this, Priority.ALWAYS) },
			runButton,
			pauseResumeButton,
			stopButton
		)
	)
	var runningQuery: QueryExecutor<T, QueryExecutionContextFx<T>>? = null

	constructor(executionContextProvider: () -> (QueryExecutionContext<T>), previewData: List<JSONObject>) : this(
		executionContextProvider,
		QueryEditorUi.createFieldNames(previewData)
	)

	init {
		runButton.setDisable(true)
		pauseResumeButton.setDisable(true)
		stopButton.setDisable(true)
		builderUi.isComplete().addListener { _, _, new ->
			runButton.setDisable(!new && runningQuery == null)
		}
		runButton.setOnAction {
			val query = builderUi.getQuery()
			if (query != null) {
				startQuery(query, executionContextProvider())
			}
		}
	}

	private fun startQuery(query: Query, delegate: QueryExecutionContext<T>) {
		synchronized(this) {
			runningQuery = QueryExecutor(query, createQueryExecutionContext(delegate), ForkJoinPool.commonPool(), 2)
			reportProgress(runningQuery)
			runningQuery?.resume()
			Platform.runLater {
				builderUi.node.setDisable(true)
				runButton.setDisable(true)
				stopButton.setDisable(false)
				pauseResumeButton.setDisable(false)
				pauseResumeButton.setText("Pause")
				pauseResumeButton.setOnAction {
					pauseQuery()
				}
				stopButton.setOnAction { stopQuery() }
			}
		}

	}

	private fun createQueryExecutionContext(delegate: QueryExecutionContext<T>): QueryExecutionContextFx<T> {
		return QueryExecutionContextFx({
			reportProgress(runningQuery)
			delegate.getNextElement()
		}, {
			reportProgress(runningQuery)
			delegate.addMatch(it)
		}, {
			stopQuery()
			delegate.executionFinished()
		})

	}

	private fun reportProgress(runningQuery: QueryExecutor<T, QueryExecutionContextFx<T>>?) {
		if (runningQuery == null) return
		val searchedElementCount = runningQuery.executionContext.getSearchedElementCount()
		if (searchedElementCount % 100000 != 0L) return
		reportProgress(searchedElementCount, runningQuery.executionContext.getMatchesCount())
	}

	private fun reportProgress(searchedElementCount: Long, matchesCount: Long) {
		Platform.runLater {
			progressLabel.setText("Processed: $searchedElementCount Matched: $matchesCount")
		}
	}

	private fun pauseQuery() {
		synchronized(this) {
			runningQuery?.pause()
			reportProgress(runningQuery)
			Platform.runLater {
				pauseResumeButton.setText("Resume")
				pauseResumeButton.setOnAction { resumeQuery() }
			}
		}
	}

	private fun resumeQuery() {
		synchronized(this) {
			runningQuery?.resume()
			Platform.runLater {
				pauseResumeButton.setText("Pause")
				pauseResumeButton.setOnAction { pauseQuery() }
			}
		}
	}

	private fun stopQuery() {
		synchronized(this) {
			val tmp = runningQuery
			runningQuery?.pause()
			if (tmp != null) {
				reportProgress(tmp.executionContext.getSearchedElementCount(), tmp.executionContext.getMatchesCount())
			}
			runningQuery = null
			Platform.runLater {
				builderUi.node.setDisable(false)
				runButton.setDisable(false)
				stopButton.setDisable(true)
				pauseResumeButton.setDisable(true)
				pauseResumeButton.setText("Pause")
			}
		}
	}
}