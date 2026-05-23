package com.smr.activity.activity2mnc.handlers;

import com.google.inject.Injector;
import com.mncml.dsl.MncStandaloneSetup;
import javax.inject.Inject;
import mncModel.Model;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.InputOutput;

@SuppressWarnings("all")
public class Demo {
  @Inject
  private ParseHelper<Model> parser;
  
  public void doParse() {
    try {
      final Injector injector = new MncStandaloneSetup().createInjectorAndDoEMFRegistration();
      injector.injectMembers(this);
      final Model model = this.parser.parse("Model Demo\n     InterfaceDescription Demo {}");
      final Model entity = ((Model) model);
      InputOutput.<String>print(entity.getName());
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  public static void main(final String[] args) {
    new Demo().doParse();
  }
}
