package com.github.hanseter.json.queryengine.fx.queryUis

import com.github.hanseter.json.queryengine.fx.AttributePathTextField
import javafx.scene.control.TextField
import com.github.hanseter.json.queryengine.BetweenQuery.BetweenQueryBuilder
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.scene.Node
import com.github.hanseter.json.queryengine.fx.QueryBuilderUi
import org.json.JSONObject
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import com.github.hanseter.json.queryengine.BetweenQuery

class BetweenQueryUi(fieldNames: JSONObject) : QueryBuilderUi<BetweenQuery> {
	private val attrPathField = AttributePathTextField(fieldNames)
	private val lowerBoundTextField = TextField()
	private val upperBoundTextField = TextField()
	private val queryBuilder = BetweenQueryBuilder()
	private val isComplete = SimpleBooleanProperty(false)
	private val node = HBox(attrPathField, lowerBoundTextField, Label("<x<"), upperBoundTextField)

	init {
		lowerBoundTextField.setPromptText("lowerBound")
		upperBoundTextField.setPromptText("upperBound")
		attrPathField.textProperty().addListener { _, _, new ->
			queryBuilder.withAttributePath(new)
			isComplete.set(queryBuilder.isComplete())
		}
		lowerBoundTextField.textProperty().addListener { _, _, new ->
			queryBuilder.withLowerBound(new)
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