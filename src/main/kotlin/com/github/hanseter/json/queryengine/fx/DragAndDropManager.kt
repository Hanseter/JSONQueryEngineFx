package com.github.hanseter.json.queryengine.fx

import javafx.scene.input.DataFormat
import javafx.scene.Node
import javafx.scene.layout.HBox
import javafx.scene.Group
import javafx.scene.shape.Polygon
import javafx.scene.shape.Polyline
import javafx.scene.input.TransferMode
import javafx.scene.input.ClipboardContent
import javafx.scene.control.ListView
import javafx.scene.control.ListCell
import javafx.scene.control.Label
import javafx.scene.input.DragEvent
import javafx.scene.layout.Priority

/**
 * This manager is used to register query UIs being dragged and dropped. We do not want to serialize the data as it is important that one created object retains the same reference.
 **/
class DragAndDropManager {
	companion object {
		val jsonQueryDndFormat = DataFormat("jsonQuery.ui")
	}

	private var queryBeingDragged: DragableQuery? = null

	fun wrapQueryForDnd(query: QueryBuilderUi<*>): Node {
		val draggableNode = DragableQuery(query, null)
		initDragDrop(draggableNode)
		return draggableNode
	}

	private fun initDragDrop(source: DragableQuery) {
		source.setOnDragDetected {
			val db = source.startDragAndDrop(TransferMode.MOVE)
			val content = ClipboardContent()
			content.put(jsonQueryDndFormat, "foobar")
			db.setContent(content)
			db.setDragView(source.snapshot(null, null))
			queryBeingDragged = source
			it.consume()
		}
		source.setOnDragDone {
			queryBeingDragged?.assignedList?.getItems()?.remove(queryBeingDragged)
			queryBeingDragged = null
		}
	}

	fun createTargetList(): ListView<DragableQuery> {
		val list = ListView<DragableQuery>()
		list.setCellFactory {
			object : ListCell<DragableQuery>() {
				init {
					setOnDragOver { 
						if (it.getDragboard().hasContent(jsonQueryDndFormat)) {
							it.acceptTransferModes(TransferMode.MOVE)
							it.consume()
						}
					}
					setOnDragDropped { handleDragFinished(it, list, this.getIndex()) }
				}

				override fun updateItem(item: DragableQuery?, empty: Boolean) {
					super.updateItem(item, empty)
					if (item == null || empty) {
						setGraphic(null)
					} else {
						setGraphic(item)
					}
				}

			}
		}
		list.setOnDragOver {
			if (it.getDragboard().hasContent(jsonQueryDndFormat)) {
				it.acceptTransferModes(TransferMode.MOVE)
				it.consume()
			}
		}
		list.setOnDragDropped { handleDragFinished(it, list) }
		list.setPlaceholder(Label("Drag queries here."))
		return list
	}

	private fun handleDragFinished(event: DragEvent, list: ListView<DragableQuery>, dropPos: Int = -1) {
		if (event.getDragboard().hasContent(jsonQueryDndFormat) && queryBeingDragged != null) {
			queryBeingDragged?.assignedList?.getItems()?.remove(queryBeingDragged)
			queryBeingDragged?.assignedList = list
			if (dropPos == -1 || dropPos > list.getItems().size) {
				list.getItems().add(queryBeingDragged)
			} else {
				list.getItems().add(dropPos, queryBeingDragged)
			}
			queryBeingDragged = null
			event.setDropCompleted(true)
			event.consume()
		}
	}

	public class DragableQuery(val query: QueryBuilderUi<*>, assignedList: ListView<DragableQuery>?) : HBox(
		Group(
			Polygon(4.0, 5.5, 0.5, 10.0, 4.0, 14.5), Polygon(5.5, 4.0, 10.0, 0.5, 14.5, 4.0),
			Polygon(16.0, 5.5, 19.5, 10.0, 16.0, 14.5), Polygon(5.5, 16.0, 10.0, 19.5, 14.5, 16.0),
			Polyline(10.0, 4.0, 10.0, 16.0), Polyline(4.0, 10.0, 16.0, 10.0)
		), query.getNode()
	) {
		init {
			HBox.setHgrow(query.getNode(), Priority.ALWAYS)
		}
		var assignedList = assignedList
			set(value) {
				if (field == null) {
					query.setPreview(false)
				}
				field = value
			}
	}
}