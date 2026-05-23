/**
 */
package CapabilityDescription;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see CapabilityDescription.CapabilityDescriptionPackage
 * @generated
 */
public interface CapabilityDescriptionFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	CapabilityDescriptionFactory eINSTANCE = CapabilityDescription.impl.CapabilityDescriptionFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Capability</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Capability</em>'.
	 * @generated
	 */
	Capability createCapability();

	/**
	 * Returns a new object of class '<em>Control Capabilities</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Control Capabilities</em>'.
	 * @generated
	 */
	ControlCapabilities createControlCapabilities();

	/**
	 * Returns a new object of class '<em>Capabilities Outcome</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Capabilities Outcome</em>'.
	 * @generated
	 */
	CapabilitiesOutcome createCapabilitiesOutcome();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	CapabilityDescriptionPackage getCapabilityDescriptionPackage();

} //CapabilityDescriptionFactory
