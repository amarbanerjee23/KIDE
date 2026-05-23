package com.mncml.dsl.serializer;

import dataModelPackage.DateValue;
import java.text.SimpleDateFormat;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.resource.SaveOptions;
import org.eclipse.xtext.serializer.ISerializer;
import org.eclipse.xtext.serializer.impl.Serializer;

@SuppressWarnings("all")
public class MncCustomSerializer extends Serializer implements ISerializer {
  @Override
  public String serialize(final EObject obj) {
    if ((obj instanceof DateValue)) {
      return new SimpleDateFormat("dd-MM-yyyy").format(((DateValue)obj).getDateValue());
    } else {
      return super.serialize(obj);
    }
  }
  
  @Override
  public String serialize(final EObject obj, final SaveOptions options) {
    if ((obj instanceof DateValue)) {
      return new SimpleDateFormat("dd-MM-yyyy").format(((DateValue)obj).getDateValue());
    } else {
      return super.serialize(obj, options);
    }
  }
}
