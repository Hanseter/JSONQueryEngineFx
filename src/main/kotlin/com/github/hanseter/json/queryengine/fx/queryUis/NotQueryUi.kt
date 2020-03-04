package com.github.hanseter.json.queryengine.fx.queryUis

import com.github.hanseter.json.queryengine.fx.DragAndDropManager
import javafx.scene.control.TitledPane
import com.github.hanseter.json.queryengine.fx.QueryBuilderUi
import com.github.hanseter.json.queryengine.NotQuery
import com.github.hanseter.json.queryengine.NotQuery.NotQueryBuilder
import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.Node
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.collections.ListChangeListener.Change
import com.github.hanseter.json.queryengine.fx.DragAndDropManager.DragableQuery
import com.github.hanseter.json.queryengine.AndQuery.AndQueryBuilder
import javafx.beans.binding.BooleanExpression

class NotQueryUi(dndManager: DragAndDropManager) : TitledPane(), QueryBuilderUi<NotQuery> {
	private val queryBuilder = AndQueryBuilder()
	private val isComplete = SimpleBooleanProperty(false)
	private val queryView = dndManager.createTargetList()

	init {
		setText("NOT")
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

	override fun getQueryBuilder() = NotQueryBuilder(queryBuilder) 

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