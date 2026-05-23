package com.model.domain.activity.design.services

import activityDiagramModel.Activity
import activityDiagramModel.ActivityDiagram
import activityDiagramModel.ConditionalActivity
import activityDiagramModel.Outcome
import com.model.domain.activity.design.layout.NavigateToDiagram
import java.util.Collection
import java.util.HashSet
import org.eclipse.emf.common.util.BasicEList
import org.eclipse.emf.common.util.EList
import org.eclipse.emf.ecore.EObject
import org.eclipse.sirius.business.api.dialect.DialectManager
import org.eclipse.sirius.business.api.session.Session
import org.eclipse.sirius.business.api.session.SessionManager
import org.eclipse.sirius.diagram.DSemanticDiagram
import org.eclipse.sirius.viewpoint.DRepresentation
import org.eclipse.xtext.EcoreUtil2
import dataModelPackage.Parameter

class ActivityDiagramServices {
	public static String DIAGRAM_TITLE = ""
	public static String ACTIVITY_DIAGRAM_TITLE_PART = "Activity Diagram for "
	var NavigateToDiagram navigateToDiagram;

	new() {
		navigateToDiagram = new NavigateToDiagram();
	}

	// precondition do restrict generation of many diagram by same name for same
	// model
	def boolean singleActivityRepresentationCheckForActivityDiagram(EObject any) {
		DIAGRAM_TITLE = getActivityDiagramTitle(any)
		val Session session = SessionManager.INSTANCE.getSession(any)
		val Collection<DRepresentation> representations = DialectManager.INSTANCE.getAllRepresentations(session)
		for (DRepresentation representation : representations) {
			if (representation instanceof DSemanticDiagram) {
				val DSemanticDiagram diagram = (representation as DSemanticDiagram)
				if (DIAGRAM_TITLE.equals(diagram.getName())) {
					return false
				}
			}
		}
		return true
	}

	// Setting title for diagram
	def String getActivityDiagramTitle(EObject eObject) {
		var String title = ""
		if (eObject instanceof ActivityDiagram)
			title = ACTIVITY_DIAGRAM_TITLE_PART + eObject.name

		return title
	}

	def Activity getStartPointForActivityDiagram(ActivityDiagram activityDiagram) {
		// System.out.println("Hello Start Point")
		var Activity startActivity = null
		if (activityDiagram.getActivities() !== null) {
			startActivity = activityDiagram.getActivities().head
		}
		return startActivity
	}

	def Activity getStartActivity(Activity activity) {
		// println(activity.name)
		return activity
	}

	def getActivitiesInOrder(ActivityDiagram ad) {
		var orderedActivityList = new HashSet<Activity>()
		var currentActivity = getStartPointForActivityDiagram(ad)
		orderedActivityList.add(currentActivity)
		
		while(currentActivity!==null){
			if (currentActivity.conditionalActivity !== null) {
				for (condition : currentActivity.conditionalActivity) {
					if (condition.onTrueNextActivity !== null) {
						orderedActivityList.add(condition.onTrueNextActivity)
						currentActivity = condition.onTrueNextActivity
					}
				}
			}
		}
	}


def getConditionsForAnActivity(Activity activity){
	if(activity.conditionalActivity!==null)
		return activity.conditionalActivity
		
}

def getAllTheConditionalActivities(ActivityDiagram ad){
	var conditionalActivities = new HashSet<ConditionalActivity>()
	if(ad.activities!==null)
		for(activity : ad.activities){
			if(activity.conditionalActivity!==null)
				conditionalActivities.addAll(activity.conditionalActivity)
		}
	return conditionalActivities
}

	def Parameter getFinalOutcomes(ConditionalActivity condition) {
		if (condition !== null && condition.onTrueFinalResult !== null) {
			return condition.onTrueFinalResult
		}
	}

	def EList<Parameter> getEndPointForOutcomes(ActivityDiagram activityDiagram) {
		// println("ENd Point Outcome")
		var EList<Parameter> outcomes = new BasicEList<Parameter>()
		System.out.println(activityDiagram.getName())
		outcomes.addAll(activityDiagram.results)
		return outcomes
	}   

	def Activity getNextActivityForAnActivityCondition(ConditionalActivity conditionalActivity) {
		if (conditionalActivity !== null) {
			if (conditionalActivity !== null && conditionalActivity.onTrueNextActivity !== null) {
				return conditionalActivity.onTrueNextActivity
			}
		}
	}

	def openChildActivityDiagramForAnActivity(ActivityDiagram element) {
		println(element.name)
		navigateToDiagram.createActivityDiagram(element)
	}
 
	def getNextConditionForACondition(ConditionalActivity condition){
		var  parentActivity = EcoreUtil2.getContainerOfType(condition,Activity)
		if(parentActivity!==null && parentActivity.conditionalActivity!==null && parentActivity.conditionalActivity.size>0){
			var conditionsList = parentActivity.conditionalActivity
			var index = conditionsList.indexOf(condition)
			//println(index + " Condition "+condition.outcome.name)
			var nextIndex = index + 1
			if(nextIndex<conditionsList.size){
			//	println(nextIndex + " Condition "+conditionsList.get(nextIndex).outcome.name)
				return conditionsList.get(nextIndex)
			}
		}
	}

}
