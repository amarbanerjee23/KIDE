/**
 */
package CapabilityDescription.impl;

import CapabilityDescription.*;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class CapabilityDescriptionFactoryImpl extends EFactoryImpl implements CapabilityDescriptionFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static CapabilityDescriptionFactory init() {
		try {
			CapabilityDescriptionFactory theCapabilityDescriptionFactory = (CapabilityDescriptionFactory)EPackage.Registry.INSTANCE.getEFactory(CapabilityDescriptionPackage.eNS_URI);
			if (theCapabilityDescriptionFactory != null) {
				return theCapabilityDescriptionFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new CapabilityDescriptionFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public CapabilityDescriptionFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
			case CapabilityDescriptionPackage.CAPABILITY: return createCapability();
			case CapabilityDescriptionPackage.CONTROL_CAPABILITIES: return createControlCapabilities();
			case CapabilityDescriptionPackage.CAPABILITIES_OUTCOME: return createCapabilitiesOutcome();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Capability createCapability() {
		CapabilityImpl capability = new CapabilityImpl();
		return capability;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ControlCapabilities createControlCapabilities() {
		ControlCapabilitiesImpl controlCapabilities = new ControlCapabilitiesImpl();
		return controlCapabilities;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public CapabilitiesOutcome createCapabilitiesOutcome() {
		CapabilitiesOutcomeImpl capabilitiesOutcome = new CapabilitiesOutcomeImpl();
		return capabilitiesOutcome;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public CapabilityDescriptionPackage getCapabilityDescriptionPackage() {
		return (CapabilityDescriptionPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static CapabilityDescriptionPackage getPackage() {
		return CapabilityDescriptionPackage.eINSTANCE;
	}

} //CapabilityDescriptionFactoryImpl
