package com.github.hanseter.json.queryengine.fx

import javafx.scene.control.TextField
import org.json.JSONObject
import org.controlsfx.control.textfield.TextFields
import com.github.hanseter.json.queryengine.AttributePath
import org.controlsfx.control.textfield.AutoCompletionBinding

class AttributePathTextField(fieldNames: JSONObject) : TextField() {
	val autoCompletion: AutoCompletionBinding<String>

	init {
		setPromptText("Attribute")
		autoCompletion = TextFields.bindAutoCompletion(this) {
			if (it.isCancelled()) {
				emptyList()
			} else {
				AttributePath(it.getUserText()).findPossibleAttributePaths(fieldNames).take(100)
					.sorted().toList()
			}
		}
	}

} 