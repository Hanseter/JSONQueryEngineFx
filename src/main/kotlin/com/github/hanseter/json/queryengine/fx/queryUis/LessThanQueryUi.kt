package com.github.hanseter.json.queryengine.fx.queryUis

import org.json.JSONObject
import com.github.hanseter.json.queryengine.fx.QueryBuilderUi
import com.github.hanseter.json.queryengine.LessThanQuery
import com.github.hanseter.json.queryengine.fx.AttributePathTextField
import javafx.scene.control.TextField
import com.github.hanseter.json.queryengine.LessThanQuery.LessThanQueryBuilder
import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.layout.HBox
import javafx.scene.control.Label
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.scene.Node

class LessThanQueryUi(fieldNames: JSONObject) : QueryBuilderUi<LessThanQuery> {
	private val attrPathField = AttributePathTextField(fieldNames)
	private val upperBoundTextField = TextField()
	private val queryBuilder = LessThanQueryBuilder()
	private val isComplete = SimpleBooleanProperty(false)
	private val node = HBox(attrPathField, Label("<"), upperBoundTextField)

	init {
		upperBoundTextField.setPromptText("upperBound")
		attrPathField.textProperty().addListener { _, _, new ->
			queryBuilder.withAttributePath(new)
			isComplete.set(queryBuilder.isComplete())
		}
		upperBoundTextField.textProperty().addListener { _, _, new ->
			queryBuilder.withUpperBound(new)
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