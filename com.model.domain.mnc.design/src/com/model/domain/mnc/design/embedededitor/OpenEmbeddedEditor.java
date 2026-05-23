package com.model.domain.mnc.design.embedededitor;


import org.obeonetwork.dsl.viewpoint.xtext.support.action.OpenXtextEmbeddedEditor;

import com.google.inject.Injector;
import com.mncml.dsl.ui.internal.DslActivator;

public class OpenEmbeddedEditor extends OpenXtextEmbeddedEditor {

	@Override
	protected Injector getInjector() {
		System.out.println("Opening Embeded Editor");
		 return  DslActivator.getInstance().getInjector("com.mncml.dsl.Mnc");
	}

}
