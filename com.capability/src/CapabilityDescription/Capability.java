/**
 */
package CapabilityDescription;

import mncModel.Action;
import mncModel.InterfaceDescription;
import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Capability</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link CapabilityDescription.Capability#getName <em>Name</em>}</li>
 *   <li>{@link CapabilityDescription.Capability#getComponentInterface <em>Component Interface</em>}</li>
 *   <li>{@link CapabilityDescription.Capability#getRequiredINITProcess <em>Required INIT Process</em>}</li>
 *   <li>{@link CapabilityDescription.Capability#getProvidesControlCapabilities <em>Provides Control Capabilities</em>}</li>
 *   <li>{@link CapabilityDescription.Capability#getProvidesOutcomes <em>Provides Outcomes</em>}</li>
 * </ul>
 *
 * @see CapabilityDescription.CapabilityDescriptionPackage#getCapability()
 * @model
 * @generated
 */
public interface Capability extends EObject {
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
	 * @see CapabilityDescription.CapabilityDescriptionPackage#getCapability_Name()
	 * @model
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link CapabilityDescription.Capability#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Component Interface</b></em>' reference list.
	 * The list contents are of type {@link mncModel.InterfaceDescription}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Component Interface</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Component Interface</em>' reference list.
	 * @see CapabilityDescription.CapabilityDescriptionPackage#getCapability_ComponentInterface()
	 * @model resolveProxies="false"
	 * @generated
	 */
	EList<InterfaceDescription> getComponentInterface();

	/**
	 * Returns the value of the '<em><b>Required INIT Process</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Required INIT Process</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Required INIT Process</em>' containment reference.
	 * @see #setRequiredINITProcess(Action)
	 * @see CapabilityDescription.CapabilityDescriptionPackage#getCapability_RequiredINITProcess()
	 * @model containment="true"
	 * @generated
	 */
	Action getRequiredINITProcess();

	/**
	 * Sets the value of the '{@link CapabilityDescription.Capability#getRequiredINITProcess <em>Required INIT Process</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Required INIT Process</em>' containment reference.
	 * @see #getRequiredINITProcess()
	 * @generated
	 */
	void setRequiredINITProcess(Action value);

	/**
	 * Returns the value of the '<em><b>Provides Control Capabilities</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Provides Control Capabilities</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Provides Control Capabilities</em>' containment reference.
	 * @see #setProvidesControlCapabilities(ControlCapabilities)
	 * @see CapabilityDescription.CapabilityDescriptionPackage#getCapability_ProvidesControlCapabilities()
	 * @model containment="true"
	 * @generated
	 */
	ControlCapabilities getProvidesControlCapabilities();

	/**
	 * Sets the value of the '{@link CapabilityDescription.Capability#getProvidesControlCapabilities <em>Provides Control Capabilities</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Provides Control Capabilities</em>' containment reference.
	 * @see #getProvidesControlCapabilities()
	 * @generated
	 */
	void setProvidesControlCapabilities(ControlCapabilities value);

	/**
	 * Returns the value of the '<em><b>Provides Outcomes</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Provides Outcomes</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Provides Outcomes</em>' containment reference.
	 * @see #setProvidesOutcomes(CapabilitiesOutcome)
	 * @see CapabilityDescription.CapabilityDescriptionPackage#getCapability_ProvidesOutcomes()
	 * @model containment="true"
	 * @generated
	 */
	CapabilitiesOutcome getProvidesOutcomes();

	/**
	 * Sets the value of the '{@link CapabilityDescription.Capability#getProvidesOutcomes <em>Provides Outcomes</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Provides Outcomes</em>' containment reference.
	 * @see #getProvidesOutcomes()
	 * @generated
	 */
	void setProvidesOutcomes(CapabilitiesOutcome value);

} // Capability
