package com.dml.dsl.serializer;

import dataModelPackage.DateValue;
import java.text.SimpleDateFormat;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.serializer.impl.Serializer;

@SuppressWarnings("all")
public class DmlCustomSerializer extends Serializer {
  @Override
  public String serialize(final EObject obj) {
    if ((obj instanceof DateValue)) {
      return new SimpleDateFormat("dd-MM-yyyy").format(((DateValue)obj).getDateValue());
    } else {
      return super.serialize(obj);
    }
  }
}
