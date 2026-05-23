package com.smr.activity.dsl.serializer

import dataModelPackage.DateValue
import java.text.SimpleDateFormat
import org.eclipse.emf.ecore.EObject
import org.eclipse.xtext.serializer.impl.Serializer

class ActivityDiagramCustomSerializer extends Serializer {

	override serialize(EObject obj) {
		if (obj instanceof DateValue)
			return new SimpleDateFormat('dd-MM-yyyy').format(obj.dateValue)
		else
			return super.serialize(obj)
	}

}
