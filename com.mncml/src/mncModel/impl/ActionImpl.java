/**
 */
package mncModel.impl;

import java.util.Collection;

import mncModel.Action;
import mncModel.ActionAlarm;
import mncModel.ActionCommand;
import mncModel.ActionDataPoint;
import mncModel.ActionEvent;
import mncModel.ActionOperation;
import mncModel.MncModelPackage;
import mncModel.Transition;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Action</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link mncModel.impl.ActionImpl#getFireCommand <em>Fire Command</em>}</li>
 *   <li>{@link mncModel.impl.ActionImpl#getPublishEvent <em>Publish Event</em>}</li>
 *   <li>{@link mncModel.impl.ActionImpl#getRaiseAlarm <em>Raise Alarm</em>}</li>
 *   <li>{@link mncModel.impl.ActionImpl#getTriggerDataPoint <em>Trigger Data Point</em>}</li>
 *   <li>{@link mncModel.impl.ActionImpl#getExecuteOperation <em>Execute Operation</em>}</li>
 *   <li>{@link mncModel.impl.ActionImpl#getTransitionStates <em>Transition States</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ActionImpl extends MinimalEObjectImpl.Container implements Action {
	/**
	 * The cached value of the '{@link #getFireCommand() <em>Fire Command</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFireCommand()
	 * @generated
	 * @ordered
	 */
	protected EList<ActionCommand> fireCommand;

	/**
	 * The cached value of the '{@link #getPublishEvent() <em>Publish Event</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPublishEvent()
	 * @generated
	 * @ordered
	 */
	protected EList<ActionEvent> publishEvent;

	/**
	 * The cached value of the '{@link #getRaiseAlarm() <em>Raise Alarm</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRaiseAlarm()
	 * @generated
	 * @ordered
	 */
	protected EList<ActionAlarm> raiseAlarm;

	/**
	 * The cached value of the '{@link #getTriggerDataPoint() <em>Trigger Data Point</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTriggerDataPoint()
	 * @generated
	 * @ordered
	 */
	protected EList<ActionDataPoint> triggerDataPoint;

	/**
	 * The cached value of the '{@link #getExecuteOperation() <em>Execute Operation</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExecuteOperation()
	 * @generated
	 * @ordered
	 */
	protected EList<ActionOperation> executeOperation;

	/**
	 * The cached value of the '{@link #getTransitionStates() <em>Transition States</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTransitionStates()
	 * @generated
	 * @ordered
	 */
	protected EList<Transition> transitionStates;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ActionImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MncModelPackage.Literals.ACTION;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<ActionCommand> getFireCommand() {
		if (fireCommand == null) {
			fireCommand = new EObjectContainmentEList<ActionCommand>(ActionCommand.class, this, MncModelPackage.ACTION__FIRE_COMMAND);
		}
		return fireCommand;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<ActionEvent> getPublishEvent() {
		if (publishEvent == null) {
			publishEvent = new EObjectContainmentEList<ActionEvent>(ActionEvent.class, this, MncModelPackage.ACTION__PUBLISH_EVENT);
		}
		return publishEvent;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<ActionAlarm> getRaiseAlarm() {
		if (raiseAlarm == null) {
			raiseAlarm = new EObjectContainmentEList<ActionAlarm>(ActionAlarm.class, this, MncModelPackage.ACTION__RAISE_ALARM);
		}
		return raiseAlarm;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<ActionDataPoint> getTriggerDataPoint() {
		if (triggerDataPoint == null) {
			triggerDataPoint = new EObjectContainmentEList<ActionDataPoint>(ActionDataPoint.class, this, MncModelPackage.ACTION__TRIGGER_DATA_POINT);
		}
		return triggerDataPoint;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<ActionOperation> getExecuteOperation() {
		if (executeOperation == null) {
			executeOperation = new EObjectContainmentEList<ActionOperation>(ActionOperation.class, this, MncModelPackage.ACTION__EXECUTE_OPERATION);
		}
		return executeOperation;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<Transition> getTransitionStates() {
		if (transitionStates == null) {
			transitionStates = new EObjectContainmentEList<Transition>(Transition.class, this, MncModelPackage.ACTION__TRANSITION_STATES);
		}
		return transitionStates;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case MncModelPackage.ACTION__FIRE_COMMAND:
				return ((InternalEList<?>)getFireCommand()).basicRemove(otherEnd, msgs);
			case MncModelPackage.ACTION__PUBLISH_EVENT:
				return ((InternalEList<?>)getPublishEvent()).basicRemove(otherEnd, msgs);
			case MncModelPackage.ACTION__RAISE_ALARM:
				return ((InternalEList<?>)getRaiseAlarm()).basicRemove(otherEnd, msgs);
			case MncModelPackage.ACTION__TRIGGER_DATA_POINT:
				return ((InternalEList<?>)getTriggerDataPoint()).basicRemove(otherEnd, msgs);
			case MncModelPackage.ACTION__EXECUTE_OPERATION:
				return ((InternalEList<?>)getExecuteOperation()).basicRemove(otherEnd, msgs);
			case MncModelPackage.ACTION__TRANSITION_STATES:
				return ((InternalEList<?>)getTransitionStates()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case MncModelPackage.ACTION__FIRE_COMMAND:
				return getFireCommand();
			case MncModelPackage.ACTION__PUBLISH_EVENT:
				return getPublishEvent();
			case MncModelPackage.ACTION__RAISE_ALARM:
				return getRaiseAlarm();
			case MncModelPackage.ACTION__TRIGGER_DATA_POINT:
				return getTriggerDataPoint();
			case MncModelPackage.ACTION__EXECUTE_OPERATION:
				return getExecuteOperation();
			case MncModelPackage.ACTION__TRANSITION_STATES:
				return getTransitionStates();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case MncModelPackage.ACTION__FIRE_COMMAND:
				getFireCommand().clear();
				getFireCommand().addAll((Collection<? extends ActionCommand>)newValue);
				return;
			case MncModelPackage.ACTION__PUBLISH_EVENT:
				getPublishEvent().clear();
				getPublishEvent().addAll((Collection<? extends ActionEvent>)newValue);
				return;
			case MncModelPackage.ACTION__RAISE_ALARM:
				getRaiseAlarm().clear();
				getRaiseAlarm().addAll((Collection<? extends ActionAlarm>)newValue);
				return;
			case MncModelPackage.ACTION__TRIGGER_DATA_POINT:
				getTriggerDataPoint().clear();
				getTriggerDataPoint().addAll((Collection<? extends ActionDataPoint>)newValue);
				return;
			case MncModelPackage.ACTION__EXECUTE_OPERATION:
				getExecuteOperation().clear();
				getExecuteOperation().addAll((Collection<? extends ActionOperation>)newValue);
				return;
			case MncModelPackage.ACTION__TRANSITION_STATES:
				getTransitionStates().clear();
				getTransitionStates().addAll((Collection<? extends Transition>)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case MncModelPackage.ACTION__FIRE_COMMAND:
				getFireCommand().clear();
				return;
			case MncModelPackage.ACTION__PUBLISH_EVENT:
				getPublishEvent().clear();
				return;
			case MncModelPackage.ACTION__RAISE_ALARM:
				getRaiseAlarm().clear();
				return;
			case MncModelPackage.ACTION__TRIGGER_DATA_POINT:
				getTriggerDataPoint().clear();
				return;
			case MncModelPackage.ACTION__EXECUTE_OPERATION:
				getExecuteOperation().clear();
				return;
			case MncModelPackage.ACTION__TRANSITION_STATES:
				getTransitionStates().clear();
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case MncModelPackage.ACTION__FIRE_COMMAND:
				return fireCommand != null && !fireCommand.isEmpty();
			case MncModelPackage.ACTION__PUBLISH_EVENT:
				return publishEvent != null && !publishEvent.isEmpty();
			case MncModelPackage.ACTION__RAISE_ALARM:
				return raiseAlarm != null && !raiseAlarm.isEmpty();
			case MncModelPackage.ACTION__TRIGGER_DATA_POINT:
				return triggerDataPoint != null && !triggerDataPoint.isEmpty();
			case MncModelPackage.ACTION__EXECUTE_OPERATION:
				return executeOperation != null && !executeOperation.isEmpty();
			case MncModelPackage.ACTION__TRANSITION_STATES:
				return transitionStates != null && !transitionStates.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //ActionImpl
