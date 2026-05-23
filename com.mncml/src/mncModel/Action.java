/**
 */
package mncModel;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Action</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link mncModel.Action#getFireCommand <em>Fire Command</em>}</li>
 *   <li>{@link mncModel.Action#getPublishEvent <em>Publish Event</em>}</li>
 *   <li>{@link mncModel.Action#getRaiseAlarm <em>Raise Alarm</em>}</li>
 *   <li>{@link mncModel.Action#getTriggerDataPoint <em>Trigger Data Point</em>}</li>
 *   <li>{@link mncModel.Action#getExecuteOperation <em>Execute Operation</em>}</li>
 *   <li>{@link mncModel.Action#getTransitionStates <em>Transition States</em>}</li>
 * </ul>
 *
 * @see mncModel.MncModelPackage#getAction()
 * @model
 * @generated
 */
public interface Action extends EObject {
	/**
	 * Returns the value of the '<em><b>Fire Command</b></em>' containment reference list.
	 * The list contents are of type {@link mncModel.ActionCommand}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Fire Command</em>' containment reference list.
	 * @see mncModel.MncModelPackage#getAction_FireCommand()
	 * @model containment="true"
	 * @generated
	 */
	EList<ActionCommand> getFireCommand();

	/**
	 * Returns the value of the '<em><b>Publish Event</b></em>' containment reference list.
	 * The list contents are of type {@link mncModel.ActionEvent}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Publish Event</em>' containment reference list.
	 * @see mncModel.MncModelPackage#getAction_PublishEvent()
	 * @model containment="true"
	 * @generated
	 */
	EList<ActionEvent> getPublishEvent();

	/**
	 * Returns the value of the '<em><b>Raise Alarm</b></em>' containment reference list.
	 * The list contents are of type {@link mncModel.ActionAlarm}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Raise Alarm</em>' containment reference list.
	 * @see mncModel.MncModelPackage#getAction_RaiseAlarm()
	 * @model containment="true"
	 * @generated
	 */
	EList<ActionAlarm> getRaiseAlarm();

	/**
	 * Returns the value of the '<em><b>Trigger Data Point</b></em>' containment reference list.
	 * The list contents are of type {@link mncModel.ActionDataPoint}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Trigger Data Point</em>' containment reference list.
	 * @see mncModel.MncModelPackage#getAction_TriggerDataPoint()
	 * @model containment="true"
	 * @generated
	 */
	EList<ActionDataPoint> getTriggerDataPoint();

	/**
	 * Returns the value of the '<em><b>Execute Operation</b></em>' containment reference list.
	 * The list contents are of type {@link mncModel.ActionOperation}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Execute Operation</em>' containment reference list.
	 * @see mncModel.MncModelPackage#getAction_ExecuteOperation()
	 * @model containment="true"
	 * @generated
	 */
	EList<ActionOperation> getExecuteOperation();

	/**
	 * Returns the value of the '<em><b>Transition States</b></em>' containment reference list.
	 * The list contents are of type {@link mncModel.Transition}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Transition States</em>' containment reference list.
	 * @see mncModel.MncModelPackage#getAction_TransitionStates()
	 * @model containment="true"
	 * @generated
	 */
	EList<Transition> getTransitionStates();

} // Action
