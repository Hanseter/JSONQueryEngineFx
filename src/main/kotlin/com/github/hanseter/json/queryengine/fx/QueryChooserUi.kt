package com.github.hanseter.json.queryengine.fx

import javafx.scene.layout.HBox
import javafx.scene.control.ComboBox
import javafx.collections.FXCollections
import javafx.scene.Node
import javafx.scene.text.Text
import org.json.JSONObject
import javafx.scene.layout.Priority
import javafx.scene.control.ListCell
import javafx.scene.control.Tooltip
import javafx.util.StringConverter
import javafx.scene.input.TransferMode
import javafx.scene.input.ClipboardContent
import javafx.collections.ListChangeListener.Change
import com.github.hanseter.json.queryengine.fx.queryUis.OrQueryUi
import com.github.hanseter.json.queryengine.fx.queryUis.AttributeEqualsQueryUi
import com.github.hanseter.json.queryengine.AttributeExistsQuery
import com.github.hanseter.json.queryengine.fx.queryUis.AttributePresentQueryUi
import com.github.hanseter.json.queryengine.fx.queryUis.BetweenQueryUi
import com.github.hanseter.json.queryengine.LessThanQuery
import com.github.hanseter.json.queryengine.fx.queryUis.GreaterThanQueryUi
import com.github.hanseter.json.queryengine.fx.queryUis.LessThanQueryUi
import com.github.hanseter.json.queryengine.fx.queryUis.NotQueryUi
import com.github.hanseter.json.queryengine.fx.queryUis.AndQueryUi

class QueryChooserUi(fieldNames: JSONObject, dndManager: DragAndDropManager) : HBox() {
	val creatableQueries = listOf(
		CreatableQueryUi("equals", { Text("=") }, { AttributeEqualsQueryUi(fieldNames) }),
		CreatableQueryUi("exists", { Text("?") }, { AttributePresentQueryUi(fieldNames) }),
		CreatableQueryUi("between", { Text(">x>") }, { BetweenQueryUi(fieldNames) }),
		CreatableQueryUi("less", { Text("<") }, { LessThanQueryUi(fieldNames) }),
		CreatableQueryUi("greater", { Text(">") }, { GreaterThanQueryUi(fieldNames) }),
		CreatableQueryUi("not", { Text("NOT") }, { NotQueryUi(dndManager) }),
		CreatableQueryUi(
			"Multiple queries which will be concatenated with an logical 'and'.",
			{ Text("AND") },
			{ AndQueryUi(dndManager) }),
		CreatableQueryUi(
			"Multiple queries which will be concatenated with an logical 'or'.",
			{ Text("OR") },
			{ OrQueryUi(dndManager) })
	)

	init {
		val queryCombobx = ComboBox<CreatableQueryUi>(FXCollections.observableArrayList(creatableQueries))
		val queryContent = HBox()
		HBox.setHgrow(queryContent, Priority.ALWAYS)
		queryCombobx.setCellFactory {
			object : ListCell<CreatableQueryUi>() {
				override fun updateItem(item: CreatableQueryUi?, empty: Boolean) {
					super.updateItem(item, empty)
					if (item == null || empty) {
						setGraphic(null)
						setTooltip(null)
					} else {
						setGraphic(item.img())
						setTooltip(Tooltip(item.text))
					}
				}
			}
		}
		queryCombobx.setButtonCell(queryCombobx.getCellFactory().call(null))
		queryCombobx.valueProperty().addListener { _, _, new ->
			val tmp = new.createNew()
			tmp.setPreview(true)
			queryContent.getChildren().setAll(dndManager.wrapQueryForDnd(tmp))
		}
		queryContent.getChildren().addListener { change: Change<out Node> ->
			if (change.getList().isEmpty() && queryCombobx.getValue() != null) {
				val tmp = queryCombobx.getValue().createNew()
				tmp.setPreview(true)
				queryContent.getChildren().setAll(dndManager.wrapQueryForDnd(tmp))
			}
		}
		this.getChildren().addAll(queryCombobx, queryContent)
	}

	class CreatableQueryUi(val text: String, val img: () -> Node, val createNew: () -> QueryBuilderUi<*>) {
	}
}