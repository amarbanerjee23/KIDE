package com.model.domain.mnc.design.services;

import mncModel.OperatingState;

//Class For validation purpose
public class StateDiagramValidation {

	public StateDiagramValidation() {
		// TODO Auto-generated constructor stub
	}
	
	public boolean checkPresenseOfState(OperatingState state)
	{
		return true;
	}
	
}
//aql:self.eContainer().siblings().eAllContents()->filter(self.eClass()).name.equalsIgnoreCase(self.name)->count(true)
//aql:self.eContainer().eContainer().eContents()->filter(self.eContainer().eClass()).eAllContents()
//aql:self.differs(self.eContainer().siblings()->filter(self.eContainer().eClass()).eAllContents())