package test.jsonquery

import org.json.JSONObject
import javafx.scene.Node
import javafx.scene.layout.VBox
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import javafx.scene.control.ComboBox
import javafx.scene.control.Button
import com.github.hanseter.json.queryengine.fx.QueryChooserUi
import javafx.scene.control.Separator
import com.github.hanseter.json.queryengine.fx.DragAndDropManager
import com.github.hanseter.json.queryengine.Query
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.SimpleBooleanProperty
import com.github.hanseter.json.queryengine.fx.queryUis.AndQueryUi

class QueryEditorUi(
	fieldNames: JSONObject
) {
	val node = VBox()
	val queryUi: AndQueryUi

	init {
		val dndManager = DragAndDropManager()
		queryUi = AndQueryUi(dndManager)
		queryUi.setPreview(false)
		val queryChooser = QueryChooserUi(fieldNames, dndManager)
		node.getChildren().addAll(queryChooser, Separator(), queryUi.getNode())
	}

	constructor(previewData: List<JSONObject>) : this(createFieldNames(previewData))

	fun isComplete() = queryUi.isComplete()
	
	fun getQuery(): Query? = queryUi.getQueryBuilder().build()

	companion object {
		fun createFieldNames(objects: List<JSONObject>) =
			objects.fold(JSONObject()) { acc, it ->
				copyKeys(it, acc)
			}

		private fun copyKeys(from: JSONObject, to: JSONObject?): JSONObject =
			from.keySet().fold(to ?: JSONObject()) { acc, it ->
				if (!acc.has(it)) {
					acc.put(it, "")
				}
				val nestedObject = acc.optJSONObject(it)
				if (nestedObject != null) {
					acc.put(it, copyKeys(nestedObject, acc.optJSONObject(it)))
				}
				acc
			}
	}

}