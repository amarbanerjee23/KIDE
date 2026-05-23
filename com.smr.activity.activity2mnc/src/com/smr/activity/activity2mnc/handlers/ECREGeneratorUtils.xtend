package com.smr.activity.activity2mnc.handlers

import dataModelPackage.ArrayType
import dataModelPackage.AbstractType
import dataModelPackage.PrimitiveValue
import dataModelPackage.IntValue
import dataModelPackage.FloatValue
import dataModelPackage.StringValue
import dataModelPackage.BoolValue
import dataModelPackage.AbstractObjectValue
import dataModelPackage.ArrayValues
import dataModelPackage.Parameter
import dataModelPackage.SimpleType
import mncModel.ControlNode
import mncModel.AbstractInterfaceItems
import org.eclipse.emf.common.util.BasicEList
import mncModel.Command
import mncModel.DataPoint
import mncModel.Event
import dataModelPackage.DataModelFactory
import dataModelPackage.PrimitiveValueType
import java.text.SimpleDateFormat

class ECREGeneratorUtils {
	def static String getParameterName(Parameter parameter) {
		if (parameter !== null) {
			if (parameter instanceof SimpleType) {
				return parameter.name
			}
			if (parameter instanceof ArrayType) {
				return parameter.name
			}
			if (parameter instanceof AbstractType) {
				return parameter.name
			}
		}
	}

	def static String getParameterValue(Parameter parameter) {
		if (parameter instanceof SimpleType) {
			var value = parameter.value
			if (value !== null) {
				return getPrimitiveValue(value)
			}
		}
		if (parameter instanceof ArrayType) {
			var string = ""
			var values = parameter.values
			if (values !== null) {
				for (value : values) {

					string = string + getPrimitiveValue(value)
					if (!value.equals(values.last)) {
						string = string + ","
					}
				}
			}
			return "[" + string + "]"
		}
		if (parameter instanceof AbstractType) {
			if (parameter.value !== null) {
				return getPrimitiveValue(parameter.value)
			}

		}
	}

	def static String getPrimitiveValue(PrimitiveValue value) {
		if (value !== null) {
			if (value instanceof IntValue) {
				return value.intValue.toString
			}
			if (value instanceof FloatValue) {
				return value.floatValue.toString
			}
			if (value instanceof StringValue) {
				return value.stringValue.toString
			}
			if (value instanceof BoolValue) {
				return value.boolValue.toString.toFirstUpper
			}
			if (value instanceof AbstractObjectValue) {
				return value.abstractValue.toString + "()"
			}
			if (value instanceof ArrayValues) {
				var string = ""
				var values = value.values
				for (va : values) {
					string = string + getPrimitiveValue(va)
					if (!va.equals(values.last)) {
						string = string + ","
					}
				}
				return '[' + string + ']'
			}
		}

	}
	
	def static controlNodeTravellerForBlocks(ControlNode controlNode, AbstractInterfaceItems item) {
		var cnList = new BasicEList<ControlNode>()
		cnList.add(controlNode)
		cnList.addAll(controlNode.childNodes)

		for (cn : cnList) {
			if (cn !== null) {
				if (item instanceof Command) {
					var command = item as Command
					if (cn.commandResponseBlocks !== null && cn.commandResponseBlocks !== null) {
						for (cb : cn.commandResponseBlocks) {
							if (cb.command.equals(command)) {
								return cb
							}
						}
					}
				}
				if (item instanceof DataPoint) {
					var dataPoint = item as DataPoint
					if (cn.dataPointBlocks !== null && cn.dataPointBlocks !== null) {
						for (dpb : cn.dataPointBlocks) {
							if (dpb.dataPoint.equals(dataPoint)) {
								return dpb 
							}
						}
					}
				}
				if (item instanceof Event) {
					var event = item as Event
					if (cn.eventBlocks !== null && cn.eventBlocks !== null) {
						for (eb : cn.eventBlocks) {
							if (eb.event.equals(event)) {
								return eb
							}
						}
					}
				}
			}
		}

	}
	
	def populateDate(){
		return DataModelFactory.eINSTANCE.createSimpleType=>[
				name='mydate'
				type = PrimitiveValueType.DATE
				
				var sdf = new SimpleDateFormat('dd-MM-yyyy')
				val va = sdf.parse("23-04-1991")
				val vas = sdf.format(va)
				println(vas)
				value = (DataModelFactory.eINSTANCE.createDateValue=>[
					dateValue = va
				]) 
			]
	}
}
