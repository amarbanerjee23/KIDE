/**
 */
package CapabilityDescription.impl;

import CapabilityDescription.CapabilitiesOutcome;
import CapabilityDescription.CapabilityDescriptionPackage;

import java.util.Collection;

import mncModel.Alarm;
import mncModel.DataPoint;
import mncModel.Event;
import mncModel.Response;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectResolvingEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Capabilities Outcome</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link CapabilityDescription.impl.CapabilitiesOutcomeImpl#getResponses <em>Responses</em>}</li>
 *   <li>{@link CapabilityDescription.impl.CapabilitiesOutcomeImpl#getEvents <em>Events</em>}</li>
 *   <li>{@link CapabilityDescription.impl.CapabilitiesOutcomeImpl#getAlarms <em>Alarms</em>}</li>
 *   <li>{@link CapabilityDescription.impl.CapabilitiesOutcomeImpl#getDataPoints <em>Data Points</em>}</li>
 * </ul>
 *
 * @generated
 */
public class CapabilitiesOutcomeImpl extends MinimalEObjectImpl.Container implements CapabilitiesOutcome {
	/**
	 * The cached value of the '{@link #getResponses() <em>Responses</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getResponses()
	 * @generated
	 * @ordered
	 */
	protected EList<Response> responses;

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
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected CapabilitiesOutcomeImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return CapabilityDescriptionPackage.Literals.CAPABILITIES_OUTCOME;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Response> getResponses() {
		if (responses == null) {
			responses = new EObjectResolvingEList<Response>(Response.class, this, CapabilityDescriptionPackage.CAPABILITIES_OUTCOME__RESPONSES);
		}
		return responses;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Event> getEvents() {
		if (events == null) {
			events = new EObjectResolvingEList<Event>(Event.class, this, CapabilityDescriptionPackage.CAPABILITIES_OUTCOME__EVENTS);
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
			alarms = new EObjectResolvingEList<Alarm>(Alarm.class, this, CapabilityDescriptionPackage.CAPABILITIES_OUTCOME__ALARMS);
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
			dataPoints = new EObjectResolvingEList<DataPoint>(DataPoint.class, this, CapabilityDescriptionPackage.CAPABILITIES_OUTCOME__DATA_POINTS);
		}
		return dataPoints;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case CapabilityDescriptionPackage.CAPABILITIES_OUTCOME__RESPONSES:
				return getResponses();
			case CapabilityDescriptionPackage.CAPABILITIES_OUTCOME__EVENTS:
				return getEvents();
			case CapabilityDescriptionPackage.CAPABILITIES_OUTCOME__ALARMS:
				return getAlarms();
			case CapabilityDescriptionPackage.CAPABILITIES_OUTCOME__DATA_POINTS:
				return getDataPoints();
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
			case CapabilityDescriptionPackage.CAPABILITIES_OUTCOME__RESPONSES:
				getResponses().clear();
				getResponses().addAll((Collection<? extends Response>)newValue);
				return;
			case CapabilityDescriptionPackage.CAPABILITIES_OUTCOME__EVENTS:
				getEvents().clear();
				getEvents().addAll((Collection<? extends Event>)newValue);
				return;
			case CapabilityDescriptionPackage.CAPABILITIES_OUTCOME__ALARMS:
				getAlarms().clear();
				getAlarms().addAll((Collection<? extends Alarm>)newValue);
				return;
			case CapabilityDescriptionPackage.CAPABILITIES_OUTCOME__DATA_POINTS:
				getDataPoints().clear();
				getDataPoints().addAll((Collection<? extends DataPoint>)newValue);
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
			case CapabilityDescriptionPackage.CAPABILITIES_OUTCOME__RESPONSES:
				getResponses().clear();
				return;
			case CapabilityDescriptionPackage.CAPABILITIES_OUTCOME__EVENTS:
				getEvents().clear();
				return;
			case CapabilityDescriptionPackage.CAPABILITIES_OUTCOME__ALARMS:
				getAlarms().clear();
				return;
			case CapabilityDescriptionPackage.CAPABILITIES_OUTCOME__DATA_POINTS:
				getDataPoints().clear();
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
			case CapabilityDescriptionPackage.CAPABILITIES_OUTCOME__RESPONSES:
				return responses != null && !responses.isEmpty();
			case CapabilityDescriptionPackage.CAPABILITIES_OUTCOME__EVENTS:
				return events != null && !events.isEmpty();
			case CapabilityDescriptionPackage.CAPABILITIES_OUTCOME__ALARMS:
				return alarms != null && !alarms.isEmpty();
			case CapabilityDescriptionPackage.CAPABILITIES_OUTCOME__DATA_POINTS:
				return dataPoints != null && !dataPoints.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //CapabilitiesOutcomeImpl
