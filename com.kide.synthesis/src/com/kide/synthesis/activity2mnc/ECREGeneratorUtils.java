package com.kide.synthesis.activity2mnc;

import dataModelPackage.AbstractObjectValue;
import dataModelPackage.AbstractType;
import dataModelPackage.ArrayType;
import dataModelPackage.ArrayValues;
import dataModelPackage.BoolValue;
import dataModelPackage.DataModelFactory;
import dataModelPackage.DateValue;
import dataModelPackage.FloatValue;
import dataModelPackage.IntValue;
import dataModelPackage.Parameter;
import dataModelPackage.PrimitiveValue;
import dataModelPackage.PrimitiveValueType;
import dataModelPackage.SimpleType;
import dataModelPackage.StringValue;
import java.text.SimpleDateFormat;
import java.util.Date;
import mncModel.AbstractInterfaceItems;
import mncModel.BehaviorBlock;
import mncModel.Command;
import mncModel.CommandResponseBlock;
import mncModel.ControlNode;
import mncModel.DataPoint;
import mncModel.DataPointBlock;
import mncModel.Event;
import mncModel.EventBlock;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.InputOutput;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import org.eclipse.xtext.xbase.lib.StringExtensions;

@SuppressWarnings("all")
public class ECREGeneratorUtils {
  public static String getParameterName(final Parameter parameter) {
    if ((parameter != null)) {
      if ((parameter instanceof SimpleType)) {
        return ((SimpleType)parameter).getName();
      }
      if ((parameter instanceof ArrayType)) {
        return ((ArrayType)parameter).getName();
      }
      if ((parameter instanceof AbstractType)) {
        return ((AbstractType)parameter).getName();
      }
    }
    return null;
  }
  
  public static String getParameterValue(final Parameter parameter) {
    if ((parameter instanceof SimpleType)) {
      PrimitiveValue value = ((SimpleType)parameter).getValue();
      if ((value != null)) {
        return ECREGeneratorUtils.getPrimitiveValue(value);
      }
    }
    if ((parameter instanceof ArrayType)) {
      String string = "";
      EList<PrimitiveValue> values = ((ArrayType)parameter).getValues();
      if ((values != null)) {
        for (final PrimitiveValue value_1 : values) {
          {
            String _primitiveValue = ECREGeneratorUtils.getPrimitiveValue(value_1);
            String _plus = (string + _primitiveValue);
            string = _plus;
            boolean _equals = value_1.equals(IterableExtensions.<PrimitiveValue>last(values));
            boolean _not = (!_equals);
            if (_not) {
              string = (string + ",");
            }
          }
        }
      }
      return (("[" + string) + "]");
    }
    if ((parameter instanceof AbstractType)) {
      AbstractObjectValue _value = ((AbstractType)parameter).getValue();
      boolean _tripleNotEquals = (_value != null);
      if (_tripleNotEquals) {
        return ECREGeneratorUtils.getPrimitiveValue(((AbstractType)parameter).getValue());
      }
    }
    return null;
  }
  
  public static String getPrimitiveValue(final PrimitiveValue value) {
    if ((value != null)) {
      if ((value instanceof IntValue)) {
        return Integer.valueOf(((IntValue)value).getIntValue()).toString();
      }
      if ((value instanceof FloatValue)) {
        return Float.valueOf(((FloatValue)value).getFloatValue()).toString();
      }
      if ((value instanceof StringValue)) {
        return ((StringValue)value).getStringValue().toString();
      }
      if ((value instanceof BoolValue)) {
        return StringExtensions.toFirstUpper(Boolean.valueOf(((BoolValue)value).isBoolValue()).toString());
      }
      if ((value instanceof AbstractObjectValue)) {
        String _string = ((AbstractObjectValue)value).getAbstractValue().toString();
        return (_string + "()");
      }
      if ((value instanceof ArrayValues)) {
        String string = "";
        EList<PrimitiveValue> values = ((ArrayValues)value).getValues();
        for (final PrimitiveValue va : values) {
          {
            String _primitiveValue = ECREGeneratorUtils.getPrimitiveValue(va);
            String _plus = (string + _primitiveValue);
            string = _plus;
            boolean _equals = va.equals(IterableExtensions.<PrimitiveValue>last(values));
            boolean _not = (!_equals);
            if (_not) {
              string = (string + ",");
            }
          }
        }
        return (("[" + string) + "]");
      }
    }
    return null;
  }
  
  public static BehaviorBlock controlNodeTravellerForBlocks(final ControlNode controlNode, final AbstractInterfaceItems item) {
    BasicEList<ControlNode> cnList = new BasicEList<ControlNode>();
    cnList.add(controlNode);
    cnList.addAll(controlNode.getChildNodes());
    for (final ControlNode cn : cnList) {
      if ((cn != null)) {
        if ((item instanceof Command)) {
          Command command = ((Command) item);
          if (((cn.getCommandResponseBlocks() != null) && (cn.getCommandResponseBlocks() != null))) {
            EList<CommandResponseBlock> _commandResponseBlocks = cn.getCommandResponseBlocks();
            for (final CommandResponseBlock cb : _commandResponseBlocks) {
              boolean _equals = cb.getCommand().equals(command);
              if (_equals) {
                return cb;
              }
            }
          }
        }
        if ((item instanceof DataPoint)) {
          DataPoint dataPoint = ((DataPoint) item);
          if (((cn.getDataPointBlocks() != null) && (cn.getDataPointBlocks() != null))) {
            EList<DataPointBlock> _dataPointBlocks = cn.getDataPointBlocks();
            for (final DataPointBlock dpb : _dataPointBlocks) {
              boolean _equals_1 = dpb.getDataPoint().equals(dataPoint);
              if (_equals_1) {
                return dpb;
              }
            }
          }
        }
        if ((item instanceof Event)) {
          Event event = ((Event) item);
          if (((cn.getEventBlocks() != null) && (cn.getEventBlocks() != null))) {
            EList<EventBlock> _eventBlocks = cn.getEventBlocks();
            for (final EventBlock eb : _eventBlocks) {
              boolean _equals_2 = eb.getEvent().equals(event);
              if (_equals_2) {
                return eb;
              }
            }
          }
        }
      }
    }
    return null;
  }
  
  public SimpleType populateDate() {
    SimpleType _createSimpleType = DataModelFactory.eINSTANCE.createSimpleType();
    final Procedure1<SimpleType> _function = new Procedure1<SimpleType>() {
      @Override
      public void apply(final SimpleType it) {
        try {
          it.setName("mydate");
          it.setType(PrimitiveValueType.DATE);
          SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
          final Date va = sdf.parse("23-04-1991");
          final String vas = sdf.format(va);
          InputOutput.<String>println(vas);
          DateValue _createDateValue = DataModelFactory.eINSTANCE.createDateValue();
          final Procedure1<DateValue> _function = new Procedure1<DateValue>() {
            @Override
            public void apply(final DateValue it) {
              it.setDateValue(va);
            }
          };
          DateValue _doubleArrow = ObjectExtensions.<DateValue>operator_doubleArrow(_createDateValue, _function);
          it.setValue(_doubleArrow);
        } catch (Throwable _e) {
          throw Exceptions.sneakyThrow(_e);
        }
      }
    };
    return ObjectExtensions.<SimpleType>operator_doubleArrow(_createSimpleType, _function);
  }
}
