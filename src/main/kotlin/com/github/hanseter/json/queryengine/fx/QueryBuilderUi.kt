package com.github.hanseter.json.queryengine.fx

import javafx.scene.Node
import com.github.hanseter.json.queryengine.QueryBuilder
import com.github.hanseter.json.queryengine.Query
import javafx.beans.property.ReadOnlyBooleanProperty

interface QueryBuilderUi<T : Query> {
	fun isComplete(): ReadOnlyBooleanProperty

	fun getNode(): Node

	fun getQueryBuilder(): QueryBuilder<T>
	
	fun setPreview(preview: Boolean)

}