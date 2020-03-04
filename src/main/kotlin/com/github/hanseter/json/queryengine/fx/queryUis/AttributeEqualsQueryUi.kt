package com.github.hanseter.json.queryengine.fx.queryUis

import javafx.scene.layout.HBox
import javafx.scene.control.TextField
import javafx.scene.control.Label
import javafx.scene.Node
import org.controlsfx.control.textfield.TextFields
import org.json.JSONObject
import com.github.hanseter.json.queryengine.AttributePath
import com.github.hanseter.json.queryengine.fx.AttributePathTextField
import com.github.hanseter.json.queryengine.AttributeEqualsQuery
import com.github.hanseter.json.queryengine.AttributeEqualsQuery.AttributeEqualsQueryBuilder
import javafx.beans.property.SimpleBooleanProperty
import com.github.hanseter.json.queryengine.Query
import javafx.beans.property.ReadOnlyBooleanProperty
import com.github.hanseter.json.queryengine.fx.QueryBuilderUi

class AttributeEqualsQueryUi(fieldNames: JSONObject) : QueryBuilderUi<AttributeEqualsQuery> {
	private val attrPathField = AttributePathTextField(fieldNames)
	private val valueTextField = TextField()
	private val queryBuilder = AttributeEqualsQueryBuilder()
	private val isComplete = SimpleBooleanProperty(false)
	private val node = HBox(attrPathField, Label("equals"), valueTextField)

	init {
		valueTextField.setPromptText("value")
		attrPathField.textProperty().addListener { _, _, new ->
			queryBuilder.withAttributePath(new)
			isComplete.set(queryBuilder.isComplete())
		}
		valueTextField.textProperty().addListener { _, _, new ->
			queryBuilder.withValue(new)
			isComplete.set(queryBuilder.isComplete())
		}
	}


	override fun isComplete(): ReadOnlyBooleanProperty= isComplete

	override fun getNode(): Node = node

	override fun getQueryBuilder() = queryBuilder
	
	override fun setPreview(preview: Boolean) {
		node.setDisable(preview)
	}
}