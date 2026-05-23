package com.model.domain.activity.design.services;

import activityDiagramModel.Activity;
import activityDiagramModel.ActivityDiagram;
import activityDiagramModel.ConditionalActivity;
import com.model.domain.activity.design.layout.NavigateToDiagram;
import dataModelPackage.Parameter;
import java.util.Collection;
import java.util.HashSet;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.sirius.business.api.dialect.DialectManager;
import org.eclipse.sirius.business.api.session.Session;
import org.eclipse.sirius.business.api.session.SessionManager;
import org.eclipse.sirius.diagram.DSemanticDiagram;
import org.eclipse.sirius.viewpoint.DRepresentation;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.xbase.lib.InputOutput;
import org.eclipse.xtext.xbase.lib.IterableExtensions;

@SuppressWarnings("all")
public class ActivityDiagramServices {
  public static String DIAGRAM_TITLE = "";
  
  public static String ACTIVITY_DIAGRAM_TITLE_PART = "Activity Diagram for ";
  
  private NavigateToDiagram navigateToDiagram;
  
  public ActivityDiagramServices() {
    NavigateToDiagram _navigateToDiagram = new NavigateToDiagram();
    this.navigateToDiagram = _navigateToDiagram;
  }
  
  public boolean singleActivityRepresentationCheckForActivityDiagram(final EObject any) {
    ActivityDiagramServices.DIAGRAM_TITLE = this.getActivityDiagramTitle(any);
    final Session session = SessionManager.INSTANCE.getSession(any);
    final Collection<DRepresentation> representations = DialectManager.INSTANCE.getAllRepresentations(session);
    for (final DRepresentation representation : representations) {
      if ((representation instanceof DSemanticDiagram)) {
        final DSemanticDiagram diagram = ((DSemanticDiagram) representation);
        boolean _equals = ActivityDiagramServices.DIAGRAM_TITLE.equals(diagram.getName());
        if (_equals) {
          return false;
        }
      }
    }
    return true;
  }
  
  public String getActivityDiagramTitle(final EObject eObject) {
    String title = "";
    if ((eObject instanceof ActivityDiagram)) {
      String _name = ((ActivityDiagram)eObject).getName();
      String _plus = (ActivityDiagramServices.ACTIVITY_DIAGRAM_TITLE_PART + _name);
      title = _plus;
    }
    return title;
  }
  
  public Activity getStartPointForActivityDiagram(final ActivityDiagram activityDiagram) {
    Activity startActivity = null;
    EList<Activity> _activities = activityDiagram.getActivities();
    boolean _tripleNotEquals = (_activities != null);
    if (_tripleNotEquals) {
      startActivity = IterableExtensions.<Activity>head(activityDiagram.getActivities());
    }
    return startActivity;
  }
  
  public Activity getStartActivity(final Activity activity) {
    return activity;
  }
  
  public void getActivitiesInOrder(final ActivityDiagram ad) {
    HashSet<Activity> orderedActivityList = new HashSet<Activity>();
    Activity currentActivity = this.getStartPointForActivityDiagram(ad);
    orderedActivityList.add(currentActivity);
    while ((currentActivity != null)) {
      EList<ConditionalActivity> _conditionalActivity = currentActivity.getConditionalActivity();
      boolean _tripleNotEquals = (_conditionalActivity != null);
      if (_tripleNotEquals) {
        EList<ConditionalActivity> _conditionalActivity_1 = currentActivity.getConditionalActivity();
        for (final ConditionalActivity condition : _conditionalActivity_1) {
          Activity _onTrueNextActivity = condition.getOnTrueNextActivity();
          boolean _tripleNotEquals_1 = (_onTrueNextActivity != null);
          if (_tripleNotEquals_1) {
            orderedActivityList.add(condition.getOnTrueNextActivity());
            currentActivity = condition.getOnTrueNextActivity();
          }
        }
      }
    }
  }
  
  public EList<ConditionalActivity> getConditionsForAnActivity(final Activity activity) {
    EList<ConditionalActivity> _conditionalActivity = activity.getConditionalActivity();
    boolean _tripleNotEquals = (_conditionalActivity != null);
    if (_tripleNotEquals) {
      return activity.getConditionalActivity();
    }
    return null;
  }
  
  public HashSet<ConditionalActivity> getAllTheConditionalActivities(final ActivityDiagram ad) {
    HashSet<ConditionalActivity> conditionalActivities = new HashSet<ConditionalActivity>();
    EList<Activity> _activities = ad.getActivities();
    boolean _tripleNotEquals = (_activities != null);
    if (_tripleNotEquals) {
      EList<Activity> _activities_1 = ad.getActivities();
      for (final Activity activity : _activities_1) {
        EList<ConditionalActivity> _conditionalActivity = activity.getConditionalActivity();
        boolean _tripleNotEquals_1 = (_conditionalActivity != null);
        if (_tripleNotEquals_1) {
          conditionalActivities.addAll(activity.getConditionalActivity());
        }
      }
    }
    return conditionalActivities;
  }
  
  public Parameter getFinalOutcomes(final ConditionalActivity condition) {
    if (((condition != null) && (condition.getOnTrueFinalResult() != null))) {
      return condition.getOnTrueFinalResult();
    }
    return null;
  }
  
  public EList<Parameter> getEndPointForOutcomes(final ActivityDiagram activityDiagram) {
    EList<Parameter> outcomes = new BasicEList<Parameter>();
    System.out.println(activityDiagram.getName());
    outcomes.addAll(activityDiagram.getResults());
    return outcomes;
  }
  
  public Activity getNextActivityForAnActivityCondition(final ConditionalActivity conditionalActivity) {
    if ((conditionalActivity != null)) {
      if (((conditionalActivity != null) && (conditionalActivity.getOnTrueNextActivity() != null))) {
        return conditionalActivity.getOnTrueNextActivity();
      }
    }
    return null;
  }
  
  public void openChildActivityDiagramForAnActivity(final ActivityDiagram element) {
    InputOutput.<String>println(element.getName());
    this.navigateToDiagram.createActivityDiagram(element);
  }
  
  public ConditionalActivity getNextConditionForACondition(final ConditionalActivity condition) {
    Activity parentActivity = EcoreUtil2.<Activity>getContainerOfType(condition, Activity.class);
    if ((((parentActivity != null) && (parentActivity.getConditionalActivity() != null)) && (parentActivity.getConditionalActivity().size() > 0))) {
      EList<ConditionalActivity> conditionsList = parentActivity.getConditionalActivity();
      int index = conditionsList.indexOf(condition);
      int nextIndex = (index + 1);
      int _size = conditionsList.size();
      boolean _lessThan = (nextIndex < _size);
      if (_lessThan) {
        return conditionsList.get(nextIndex);
      }
    }
    return null;
  }
}
