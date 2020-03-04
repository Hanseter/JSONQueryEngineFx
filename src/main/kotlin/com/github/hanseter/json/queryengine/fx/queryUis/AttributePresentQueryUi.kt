package com.github.hanseter.json.queryengine.fx.queryUis

import javafx.scene.layout.HBox
import javafx.scene.control.TextField
import javafx.scene.control.Label
import javafx.scene.Node
import org.controlsfx.control.textfield.TextFields
import org.json.JSONObject
import com.github.hanseter.json.queryengine.AttributePath
import com.github.hanseter.json.queryengine.fx.AttributePathTextField
import com.github.hanseter.json.queryengine.fx.QueryBuilderUi
import com.github.hanseter.json.queryengine.AttributeExistsQuery
import javafx.beans.property.SimpleBooleanProperty
import com.github.hanseter.json.queryengine.AttributeExistsQuery.AttributeExistsQueryBuilder
import javafx.beans.property.ReadOnlyBooleanProperty

class AttributePresentQueryUi(fieldNames: JSONObject) : QueryBuilderUi<AttributeExistsQuery> {

	private val attrPathField = AttributePathTextField(fieldNames)
	private val queryBuilder = AttributeExistsQueryBuilder()
	private val isComplete = SimpleBooleanProperty(false)
	private val node: Node = HBox(Label("has"), attrPathField)

	init {
		attrPathField.textProperty().addListener { _, _, new ->
			queryBuilder.withAttributePath(new)
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