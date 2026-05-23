package com.model.domain.activity.design.embedededitor;


import org.obeonetwork.dsl.viewpoint.xtext.support.action.OpenXtextEmbeddedEditor;
import com.google.inject.Injector;
import com.smr.activity.dsl.ui.internal.DslActivator;
 
public class OpenEmbeddedEditor extends OpenXtextEmbeddedEditor {

	@Override
	protected Injector getInjector() {
		System.out.println("Opening Embeded Editor");
		 return  DslActivator.getInstance().getInjector(DslActivator.COM_SMR_ACTIVITY_DSL_ACTIVITYDIAGRAM);
	}

}
