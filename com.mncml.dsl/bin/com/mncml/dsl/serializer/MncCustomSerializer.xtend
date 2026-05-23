package com.mncml.dsl.serializer

import dataModelPackage.DateValue
import java.text.SimpleDateFormat
import org.eclipse.emf.ecore.EObject
import org.eclipse.xtext.serializer.impl.Serializer
import org.eclipse.xtext.resource.SaveOptions
import org.eclipse.xtext.serializer.ISerializer

class MncCustomSerializer extends Serializer implements ISerializer{
	
	override serialize(EObject obj) {
		if (obj instanceof DateValue)
			return new SimpleDateFormat('dd-MM-yyyy').format(obj.dateValue)
		else
			return super.serialize(obj)
	}
	
	override serialize(EObject obj, SaveOptions options) {
		if (obj instanceof DateValue)
			return new SimpleDateFormat('dd-MM-yyyy').format(obj.dateValue)
		else
			return super.serialize(obj,options)
	}

}
