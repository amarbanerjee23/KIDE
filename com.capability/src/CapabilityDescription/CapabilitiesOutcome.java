/**
 */
package CapabilityDescription;

import mncModel.Alarm;
import mncModel.DataPoint;
import mncModel.Event;
import mncModel.Response;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Capabilities Outcome</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link CapabilityDescription.CapabilitiesOutcome#getResponses <em>Responses</em>}</li>
 *   <li>{@link CapabilityDescription.CapabilitiesOutcome#getEvents <em>Events</em>}</li>
 *   <li>{@link CapabilityDescription.CapabilitiesOutcome#getAlarms <em>Alarms</em>}</li>
 *   <li>{@link CapabilityDescription.CapabilitiesOutcome#getDataPoints <em>Data Points</em>}</li>
 * </ul>
 *
 * @see CapabilityDescription.CapabilityDescriptionPackage#getCapabilitiesOutcome()
 * @model
 * @generated
 */
public interface CapabilitiesOutcome extends EObject {
	/**
	 * Returns the value of the '<em><b>Responses</b></em>' reference list.
	 * The list contents are of type {@link mncModel.Response}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Responses</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Responses</em>' reference list.
	 * @see CapabilityDescription.CapabilityDescriptionPackage#getCapabilitiesOutcome_Responses()
	 * @model
	 * @generated
	 */
	EList<Response> getResponses();

	/**
	 * Returns the value of the '<em><b>Events</b></em>' reference list.
	 * The list contents are of type {@link mncModel.Event}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Events</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Events</em>' reference list.
	 * @see CapabilityDescription.CapabilityDescriptionPackage#getCapabilitiesOutcome_Events()
	 * @model
	 * @generated
	 */
	EList<Event> getEvents();

	/**
	 * Returns the value of the '<em><b>Alarms</b></em>' reference list.
	 * The list contents are of type {@link mncModel.Alarm}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Alarms</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Alarms</em>' reference list.
	 * @see CapabilityDescription.CapabilityDescriptionPackage#getCapabilitiesOutcome_Alarms()
	 * @model
	 * @generated
	 */
	EList<Alarm> getAlarms();

	/**
	 * Returns the value of the '<em><b>Data Points</b></em>' reference list.
	 * The list contents are of type {@link mncModel.DataPoint}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Data Points</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Data Points</em>' reference list.
	 * @see CapabilityDescription.CapabilityDescriptionPackage#getCapabilitiesOutcome_DataPoints()
	 * @model
	 * @generated
	 */
	EList<DataPoint> getDataPoints();

} // CapabilitiesOutcome
