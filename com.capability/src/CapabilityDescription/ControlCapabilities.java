/**
 */
package CapabilityDescription;

import mncModel.Alarm;
import mncModel.Command;
import mncModel.DataPoint;
import mncModel.Event;

import operationsDescription.Operation;
import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Control Capabilities</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link CapabilityDescription.ControlCapabilities#getCommands <em>Commands</em>}</li>
 *   <li>{@link CapabilityDescription.ControlCapabilities#getEvents <em>Events</em>}</li>
 *   <li>{@link CapabilityDescription.ControlCapabilities#getAlarms <em>Alarms</em>}</li>
 *   <li>{@link CapabilityDescription.ControlCapabilities#getDataPoints <em>Data Points</em>}</li>
 *   <li>{@link CapabilityDescription.ControlCapabilities#getOperation <em>Operation</em>}</li>
 * </ul>
 *
 * @see CapabilityDescription.CapabilityDescriptionPackage#getControlCapabilities()
 * @model
 * @generated
 */
public interface ControlCapabilities extends EObject {
	/**
	 * Returns the value of the '<em><b>Commands</b></em>' reference list.
	 * The list contents are of type {@link mncModel.Command}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Commands</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Commands</em>' reference list.
	 * @see CapabilityDescription.CapabilityDescriptionPackage#getControlCapabilities_Commands()
	 * @model
	 * @generated
	 */
	EList<Command> getCommands();

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
	 * @see CapabilityDescription.CapabilityDescriptionPackage#getControlCapabilities_Events()
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
	 * @see CapabilityDescription.CapabilityDescriptionPackage#getControlCapabilities_Alarms()
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
	 * @see CapabilityDescription.CapabilityDescriptionPackage#getControlCapabilities_DataPoints()
	 * @model
	 * @generated
	 */
	EList<DataPoint> getDataPoints();

	/**
	 * Returns the value of the '<em><b>Operation</b></em>' containment reference list.
	 * The list contents are of type {@link operationsDescription.Operation}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Operation</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Operation</em>' containment reference list.
	 * @see CapabilityDescription.CapabilityDescriptionPackage#getControlCapabilities_Operation()
	 * @model containment="true"
	 * @generated
	 */
	EList<Operation> getOperation();

} // ControlCapabilities
