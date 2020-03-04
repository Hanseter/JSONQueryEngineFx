package com.github.hanseter.json.queryengine.fx.queryUis

import com.github.hanseter.json.queryengine.fx.AttributePathTextField
import org.json.JSONObject
import com.github.hanseter.json.queryengine.fx.QueryBuilderUi
import javafx.scene.control.TextField
import com.github.hanseter.json.queryengine.GreaterThanQuery
import com.github.hanseter.json.queryengine.GreaterThanQuery.GreaterThanQueryBuilder
import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.layout.HBox
import javafx.scene.control.Label
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.scene.Node

class GreaterThanQueryUi(fieldNames: JSONObject) : QueryBuilderUi<GreaterThanQuery> {
	private val attrPathField = AttributePathTextField(fieldNames)
	private val lowerBoundTextField = TextField()
	private val queryBuilder = GreaterThanQueryBuilder()
	private val isComplete = SimpleBooleanProperty(false)
	private val node = HBox(attrPathField, Label(">"), lowerBoundTextField)

	init {
		lowerBoundTextField.setPromptText("lowerBound")
		attrPathField.textProperty().addListener { _, _, new ->
			queryBuilder.withAttributePath(new)
			isComplete.set(queryBuilder.isComplete())
		}
		lowerBoundTextField.textProperty().addListener { _, _, new ->
			queryBuilder.withLowerBound(new)
			isComplete.set(queryBuilder.isComplete())
		}
	}


	override fun isComplete(): ReadOnlyBooleanProperty = isComplete

	override fun getNode(): Node = node

	override fun getQueryBuilder() = queryBuilder

	override fun setPreview(preview: Boolean) {
		node.setDisable(preview)
	}
}