/**
 */
package activityDiagramModel;

import dataModelPackage.Parameter;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Activity Diagram</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link activityDiagramModel.ActivityDiagram#getName <em>Name</em>}</li>
 *   <li>{@link activityDiagramModel.ActivityDiagram#getActivities <em>Activities</em>}</li>
 *   <li>{@link activityDiagramModel.ActivityDiagram#getDataObjects <em>Data Objects</em>}</li>
 *   <li>{@link activityDiagramModel.ActivityDiagram#getResults <em>Results</em>}</li>
 *   <li>{@link activityDiagramModel.ActivityDiagram#getContextDataModel <em>Context Data Model</em>}</li>
 *   <li>{@link activityDiagramModel.ActivityDiagram#getPhysicalContext <em>Physical Context</em>}</li>
 * </ul>
 *
 * @see activityDiagramModel.ActivityDiagramModelPackage#getActivityDiagram()
 * @model
 * @generated
 */
public interface ActivityDiagram extends EObject {
	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see activityDiagramModel.ActivityDiagramModelPackage#getActivityDiagram_Name()
	 * @model
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link activityDiagramModel.ActivityDiagram#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Activities</b></em>' containment reference list.
	 * The list contents are of type {@link activityDiagramModel.Activity}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Activities</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Activities</em>' containment reference list.
	 * @see activityDiagramModel.ActivityDiagramModelPackage#getActivityDiagram_Activities()
	 * @model containment="true"
	 * @generated
	 */
	EList<Activity> getActivities();

	/**
	 * Returns the value of the '<em><b>Data Objects</b></em>' reference list.
	 * The list contents are of type {@link dataModelPackage.Parameter}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Data Objects</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Data Objects</em>' reference list.
	 * @see activityDiagramModel.ActivityDiagramModelPackage#getActivityDiagram_DataObjects()
	 * @model
	 * @generated
	 */
	EList<Parameter> getDataObjects();

	/**
	 * Returns the value of the '<em><b>Results</b></em>' containment reference list.
	 * The list contents are of type {@link dataModelPackage.Parameter}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Results</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Results</em>' containment reference list.
	 * @see activityDiagramModel.ActivityDiagramModelPackage#getActivityDiagram_Results()
	 * @model containment="true"
	 * @generated
	 */
	EList<Parameter> getResults();

	/**
	 * Returns the value of the '<em><b>Context Data Model</b></em>' reference list.
	 * The list contents are of type {@link dataModelPackage.Parameter}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Context Data Model</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Context Data Model</em>' reference list.
	 * @see activityDiagramModel.ActivityDiagramModelPackage#getActivityDiagram_ContextDataModel()
	 * @model
	 * @generated
	 */
	EList<Parameter> getContextDataModel();

	/**
	 * Returns the value of the '<em><b>Physical Context</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.String}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Physical Context</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Physical Context</em>' attribute list.
	 * @see activityDiagramModel.ActivityDiagramModelPackage#getActivityDiagram_PhysicalContext()
	 * @model
	 * @generated
	 */
	EList<String> getPhysicalContext();

} // ActivityDiagram
