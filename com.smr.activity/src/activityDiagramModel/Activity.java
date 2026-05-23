/**
 */
package activityDiagramModel;

import CapabilityDescription.Capability;

import dataModelPackage.Parameter;

import mncModel.AbstractInterfaceItems;

import operationsDescription.Operation;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Activity</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link activityDiagramModel.Activity#getName <em>Name</em>}</li>
 *   <li>{@link activityDiagramModel.Activity#getBindCapability <em>Bind Capability</em>}</li>
 *   <li>{@link activityDiagramModel.Activity#getDescription <em>Description</em>}</li>
 *   <li>{@link activityDiagramModel.Activity#getConditionalActivity <em>Conditional Activity</em>}</li>
 *   <li>{@link activityDiagramModel.Activity#getUseControlCapabilities <em>Use Control Capabilities</em>}</li>
 *   <li>{@link activityDiagramModel.Activity#getRequiresOperation <em>Requires Operation</em>}</li>
 *   <li>{@link activityDiagramModel.Activity#getChildActivityDiagram <em>Child Activity Diagram</em>}</li>
 *   <li>{@link activityDiagramModel.Activity#getInputParameters <em>Input Parameters</em>}</li>
 *   <li>{@link activityDiagramModel.Activity#getInterruptedBy <em>Interrupted By</em>}</li>
 *   <li>{@link activityDiagramModel.Activity#getInterrupts <em>Interrupts</em>}</li>
 *   <li>{@link activityDiagramModel.Activity#getTime <em>Time</em>}</li>
 *   <li>{@link activityDiagramModel.Activity#getUnit <em>Unit</em>}</li>
 *   <li>{@link activityDiagramModel.Activity#getNextActivity <em>Next Activity</em>}</li>
 *   <li>{@link activityDiagramModel.Activity#getNextActivityDiagram <em>Next Activity Diagram</em>}</li>
 *   <li>{@link activityDiagramModel.Activity#getRequiredCapability <em>Required Capability</em>}</li>
 * </ul>
 *
 * @see activityDiagramModel.ActivityDiagramModelPackage#getActivity()
 * @model
 * @generated
 */
public interface Activity extends EObject {
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
	 * @see activityDiagramModel.ActivityDiagramModelPackage#getActivity_Name()
	 * @model
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link activityDiagramModel.Activity#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Bind Capability</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Bind Capability</em>' reference.
	 * @see #setBindCapability(Capability)
	 * @see activityDiagramModel.ActivityDiagramModelPackage#getActivity_BindCapability()
	 * @model
	 * @generated
	 */
	Capability getBindCapability();

	/**
	 * Sets the value of the '{@link activityDiagramModel.Activity#getBindCapability <em>Bind Capability</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Bind Capability</em>' reference.
	 * @see #getBindCapability()
	 * @generated
	 */
	void setBindCapability(Capability value);

	/**
	 * Returns the value of the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Description</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Description</em>' attribute.
	 * @see #setDescription(String)
	 * @see activityDiagramModel.ActivityDiagramModelPackage#getActivity_Description()
	 * @model
	 * @generated
	 */
	String getDescription();

	/**
	 * Sets the value of the '{@link activityDiagramModel.Activity#getDescription <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Description</em>' attribute.
	 * @see #getDescription()
	 * @generated
	 */
	void setDescription(String value);

	/**
	 * Returns the value of the '<em><b>Conditional Activity</b></em>' containment reference list.
	 * The list contents are of type {@link activityDiagramModel.ConditionalActivity}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Conditional Activity</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Conditional Activity</em>' containment reference list.
	 * @see activityDiagramModel.ActivityDiagramModelPackage#getActivity_ConditionalActivity()
	 * @model containment="true"
	 * @generated
	 */
	EList<ConditionalActivity> getConditionalActivity();

	/**
	 * Returns the value of the '<em><b>Use Control Capabilities</b></em>' reference list.
	 * The list contents are of type {@link mncModel.AbstractInterfaceItems}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Use Control Capabilities</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Use Control Capabilities</em>' reference list.
	 * @see activityDiagramModel.ActivityDiagramModelPackage#getActivity_UseControlCapabilities()
	 * @model
	 * @generated
	 */
	EList<AbstractInterfaceItems> getUseControlCapabilities();

	/**
	 * Returns the value of the '<em><b>Requires Operation</b></em>' reference list.
	 * The list contents are of type {@link operationsDescription.Operation}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Requires Operation</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Requires Operation</em>' reference list.
	 * @see activityDiagramModel.ActivityDiagramModelPackage#getActivity_RequiresOperation()
	 * @model
	 * @generated
	 */
	EList<Operation> getRequiresOperation();

	/**
	 * Returns the value of the '<em><b>Child Activity Diagram</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Child Activity Diagram</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Child Activity Diagram</em>' reference.
	 * @see #setChildActivityDiagram(ActivityDiagram)
	 * @see activityDiagramModel.ActivityDiagramModelPackage#getActivity_ChildActivityDiagram()
	 * @model
	 * @generated
	 */
	ActivityDiagram getChildActivityDiagram();

	/**
	 * Sets the value of the '{@link activityDiagramModel.Activity#getChildActivityDiagram <em>Child Activity Diagram</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Child Activity Diagram</em>' reference.
	 * @see #getChildActivityDiagram()
	 * @generated
	 */
	void setChildActivityDiagram(ActivityDiagram value);

	/**
	 * Returns the value of the '<em><b>Input Parameters</b></em>' reference list.
	 * The list contents are of type {@link dataModelPackage.Parameter}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Input Parameters</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Input Parameters</em>' reference list.
	 * @see activityDiagramModel.ActivityDiagramModelPackage#getActivity_InputParameters()
	 * @model
	 * @generated
	 */
	EList<Parameter> getInputParameters();

	/**
	 * Returns the value of the '<em><b>Interrupted By</b></em>' reference list.
	 * The list contents are of type {@link activityDiagramModel.Activity}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Interrupted By</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Interrupted By</em>' reference list.
	 * @see activityDiagramModel.ActivityDiagramModelPackage#getActivity_InterruptedBy()
	 * @model
	 * @generated
	 */
	EList<Activity> getInterruptedBy();

	/**
	 * Returns the value of the '<em><b>Interrupts</b></em>' reference list.
	 * The list contents are of type {@link activityDiagramModel.Activity}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Interrupts</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Interrupts</em>' reference list.
	 * @see activityDiagramModel.ActivityDiagramModelPackage#getActivity_Interrupts()
	 * @model
	 * @generated
	 */
	EList<Activity> getInterrupts();

	/**
	 * Returns the value of the '<em><b>Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Time</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Time</em>' attribute.
	 * @see #setTime(float)
	 * @see activityDiagramModel.ActivityDiagramModelPackage#getActivity_Time()
	 * @model
	 * @generated
	 */
	float getTime();

	/**
	 * Sets the value of the '{@link activityDiagramModel.Activity#getTime <em>Time</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Time</em>' attribute.
	 * @see #getTime()
	 * @generated
	 */
	void setTime(float value);

	/**
	 * Returns the value of the '<em><b>Unit</b></em>' attribute.
	 * The literals are from the enumeration {@link activityDiagramModel.UnitTime}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Unit</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Unit</em>' attribute.
	 * @see activityDiagramModel.UnitTime
	 * @see #setUnit(UnitTime)
	 * @see activityDiagramModel.ActivityDiagramModelPackage#getActivity_Unit()
	 * @model
	 * @generated
	 */
	UnitTime getUnit();

	/**
	 * Sets the value of the '{@link activityDiagramModel.Activity#getUnit <em>Unit</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Unit</em>' attribute.
	 * @see activityDiagramModel.UnitTime
	 * @see #getUnit()
	 * @generated
	 */
	void setUnit(UnitTime value);

	/**
	 * Returns the value of the '<em><b>Next Activity</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Next Activity</em>' reference.
	 * @see #setNextActivity(Activity)
	 * @see activityDiagramModel.ActivityDiagramModelPackage#getActivity_NextActivity()
	 * @model
	 * @generated
	 */
	Activity getNextActivity();

	/**
	 * Sets the value of the '{@link activityDiagramModel.Activity#getNextActivity <em>Next Activity</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Next Activity</em>' reference.
	 * @see #getNextActivity()
	 * @generated
	 */
	void setNextActivity(Activity value);

	/**
	 * Returns the value of the '<em><b>Next Activity Diagram</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Next Activity Diagram</em>' reference.
	 * @see #setNextActivityDiagram(ActivityDiagram)
	 * @see activityDiagramModel.ActivityDiagramModelPackage#getActivity_NextActivityDiagram()
	 * @model
	 * @generated
	 */
	ActivityDiagram getNextActivityDiagram();

	/**
	 * Sets the value of the '{@link activityDiagramModel.Activity#getNextActivityDiagram <em>Next Activity Diagram</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Next Activity Diagram</em>' reference.
	 * @see #getNextActivityDiagram()
	 * @generated
	 */
	void setNextActivityDiagram(ActivityDiagram value);

	/**
	 * Returns the value of the '<em><b>Required Capability</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Required Capability</em>' attribute.
	 * @see #setRequiredCapability(String)
	 * @see activityDiagramModel.ActivityDiagramModelPackage#getActivity_RequiredCapability()
	 * @model
	 * @generated
	 */
	String getRequiredCapability();

	/**
	 * Sets the value of the '{@link activityDiagramModel.Activity#getRequiredCapability <em>Required Capability</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Required Capability</em>' attribute.
	 * @see #getRequiredCapability()
	 * @generated
	 */
	void setRequiredCapability(String value);

} // Activity
