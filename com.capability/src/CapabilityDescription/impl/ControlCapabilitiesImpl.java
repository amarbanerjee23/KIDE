/**
 */
package CapabilityDescription.impl;

import CapabilityDescription.CapabilityDescriptionPackage;
import CapabilityDescription.ControlCapabilities;

import java.util.Collection;

import mncModel.Alarm;
import mncModel.Command;
import mncModel.DataPoint;
import mncModel.Event;

import operationsDescription.Operation;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Control Capabilities</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link CapabilityDescription.impl.ControlCapabilitiesImpl#getCommands <em>Commands</em>}</li>
 *   <li>{@link CapabilityDescription.impl.ControlCapabilitiesImpl#getEvents <em>Events</em>}</li>
 *   <li>{@link CapabilityDescription.impl.ControlCapabilitiesImpl#getAlarms <em>Alarms</em>}</li>
 *   <li>{@link CapabilityDescription.impl.ControlCapabilitiesImpl#getDataPoints <em>Data Points</em>}</li>
 *   <li>{@link CapabilityDescription.impl.ControlCapabilitiesImpl#getOperation <em>Operation</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ControlCapabilitiesImpl extends MinimalEObjectImpl.Container implements ControlCapabilities {
	/**
	 * The cached value of the '{@link #getCommands() <em>Commands</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCommands()
	 * @generated
	 * @ordered
	 */
	protected EList<Command> commands;

	/**
	 * The cached value of the '{@link #getEvents() <em>Events</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEvents()
	 * @generated
	 * @ordered
	 */
	protected EList<Event> events;

	/**
	 * The cached value of the '{@link #getAlarms() <em>Alarms</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAlarms()
	 * @generated
	 * @ordered
	 */
	protected EList<Alarm> alarms;

	/**
	 * The cached value of the '{@link #getDataPoints() <em>Data Points</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDataPoints()
	 * @generated
	 * @ordered
	 */
	protected EList<DataPoint> dataPoints;

	/**
	 * The cached value of the '{@link #getOperation() <em>Operation</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOperation()
	 * @generated
	 * @ordered
	 */
	protected EList<Operation> operation;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ControlCapabilitiesImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return CapabilityDescriptionPackage.Literals.CONTROL_CAPABILITIES;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Command> getCommands() {
		if (commands == null) {
			commands = new EObjectResolvingEList<Command>(Command.class, this, CapabilityDescriptionPackage.CONTROL_CAPABILITIES__COMMANDS);
		}
		return commands;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Event> getEvents() {
		if (events == null) {
			events = new EObjectResolvingEList<Event>(Event.class, this, CapabilityDescriptionPackage.CONTROL_CAPABILITIES__EVENTS);
		}
		return events;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Alarm> getAlarms() {
		if (alarms == null) {
			alarms = new EObjectResolvingEList<Alarm>(Alarm.class, this, CapabilityDescriptionPackage.CONTROL_CAPABILITIES__ALARMS);
		}
		return alarms;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<DataPoint> getDataPoints() {
		if (dataPoints == null) {
			dataPoints = new EObjectResolvingEList<DataPoint>(DataPoint.class, this, CapabilityDescriptionPackage.CONTROL_CAPABILITIES__DATA_POINTS);
		}
		return dataPoints;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Operation> getOperation() {
		if (operation == null) {
			operation = new EObjectContainmentEList<Operation>(Operation.class, this, CapabilityDescriptionPackage.CONTROL_CAPABILITIES__OPERATION);
		}
		return operation;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case CapabilityDescriptionPackage.CONTROL_CAPABILITIES__OPERATION:
				return ((InternalEList<?>)getOperation()).basicRemove(otherEnd, msgs);
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
			case CapabilityDescriptionPackage.CONTROL_CAPABILITIES__COMMANDS:
				return getCommands();
			case CapabilityDescriptionPackage.CONTROL_CAPABILITIES__EVENTS:
				return getEvents();
			case CapabilityDescriptionPackage.CONTROL_CAPABILITIES__ALARMS:
				return getAlarms();
			case CapabilityDescriptionPackage.CONTROL_CAPABILITIES__DATA_POINTS:
				return getDataPoints();
			case CapabilityDescriptionPackage.CONTROL_CAPABILITIES__OPERATION:
				return getOperation();
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
			case CapabilityDescriptionPackage.CONTROL_CAPABILITIES__COMMANDS:
				getCommands().clear();
				getCommands().addAll((Collection<? extends Command>)newValue);
				return;
			case CapabilityDescriptionPackage.CONTROL_CAPABILITIES__EVENTS:
				getEvents().clear();
				getEvents().addAll((Collection<? extends Event>)newValue);
				return;
			case CapabilityDescriptionPackage.CONTROL_CAPABILITIES__ALARMS:
				getAlarms().clear();
				getAlarms().addAll((Collection<? extends Alarm>)newValue);
				return;
			case CapabilityDescriptionPackage.CONTROL_CAPABILITIES__DATA_POINTS:
				getDataPoints().clear();
				getDataPoints().addAll((Collection<? extends DataPoint>)newValue);
				return;
			case CapabilityDescriptionPackage.CONTROL_CAPABILITIES__OPERATION:
				getOperation().clear();
				getOperation().addAll((Collection<? extends Operation>)newValue);
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
			case CapabilityDescriptionPackage.CONTROL_CAPABILITIES__COMMANDS:
				getCommands().clear();
				return;
			case CapabilityDescriptionPackage.CONTROL_CAPABILITIES__EVENTS:
				getEvents().clear();
				return;
			case CapabilityDescriptionPackage.CONTROL_CAPABILITIES__ALARMS:
				getAlarms().clear();
				return;
			case CapabilityDescriptionPackage.CONTROL_CAPABILITIES__DATA_POINTS:
				getDataPoints().clear();
				return;
			case CapabilityDescriptionPackage.CONTROL_CAPABILITIES__OPERATION:
				getOperation().clear();
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
			case CapabilityDescriptionPackage.CONTROL_CAPABILITIES__COMMANDS:
				return commands != null && !commands.isEmpty();
			case CapabilityDescriptionPackage.CONTROL_CAPABILITIES__EVENTS:
				return events != null && !events.isEmpty();
			case CapabilityDescriptionPackage.CONTROL_CAPABILITIES__ALARMS:
				return alarms != null && !alarms.isEmpty();
			case CapabilityDescriptionPackage.CONTROL_CAPABILITIES__DATA_POINTS:
				return dataPoints != null && !dataPoints.isEmpty();
			case CapabilityDescriptionPackage.CONTROL_CAPABILITIES__OPERATION:
				return operation != null && !operation.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //ControlCapabilitiesImpl
