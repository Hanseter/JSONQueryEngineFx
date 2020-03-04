package com.github.hanseter.json.queryengine.fx.queryUis

import javafx.scene.control.TitledPane
import javafx.scene.Node
import com.github.hanseter.json.queryengine.AndQuery.AndQueryBuilder
import com.github.hanseter.json.queryengine.AndQuery
import com.github.hanseter.json.queryengine.QueryBuilder
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.binding.BooleanBinding
import javafx.beans.binding.Bindings
import javafx.beans.binding.BooleanExpression
import javafx.scene.control.ListView
import javafx.scene.control.ListCell
import com.github.hanseter.json.queryengine.fx.DragAndDropManager.DragableQuery
import javafx.scene.control.Button
import javafx.scene.layout.VBox
import javafx.scene.layout.Priority
import javafx.scene.control.Label
import javafx.collections.ListChangeListener.Change
import com.github.hanseter.json.queryengine.fx.DragAndDropManager
import com.github.hanseter.json.queryengine.fx.QueryBuilderUi

class AndQueryUi(dndManager: DragAndDropManager) : TitledPane(), QueryBuilderUi<AndQuery> {
	private val queryBuilder = AndQueryBuilder()
	private val isComplete = SimpleBooleanProperty(false)
	private val queryView = dndManager.createTargetList()

	init {
		setText("AND")
		queryView.getItems().addListener { change: Change<out DragableQuery> ->
			isComplete.bind(createSubQueriesCompleteBinding(change.getList()))
			queryBuilder.setSubQueries(change.getList().map { it.query.getQueryBuilder() })
		}
	}

	private fun createSubQueriesCompleteBinding(subQueries: List<DragableQuery>): BooleanExpression =
		if (subQueries.isEmpty()) {
			SimpleBooleanProperty(false)
		} else {
			subQueries.map { it.query.isComplete() as BooleanExpression }.reduce { a, b -> a.and(b) }
		}

	override fun getNode(): Node = this

	override fun getQueryBuilder(): QueryBuilder<AndQuery> = queryBuilder

	override fun isComplete(): ReadOnlyBooleanProperty = isComplete

	override fun setPreview(preview: Boolean) {
		this.setDisabled(preview)
		if (preview)  {
			setContent(null)
		} else {
		    setContent(queryView)
		}
	}
}