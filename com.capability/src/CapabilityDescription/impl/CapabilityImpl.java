/**
 */
package CapabilityDescription.impl;

import CapabilityDescription.CapabilitiesOutcome;
import CapabilityDescription.Capability;
import CapabilityDescription.CapabilityDescriptionPackage;

import CapabilityDescription.ControlCapabilities;
import java.util.Collection;
import mncModel.Action;
import mncModel.InterfaceDescription;
import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Capability</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link CapabilityDescription.impl.CapabilityImpl#getName <em>Name</em>}</li>
 *   <li>{@link CapabilityDescription.impl.CapabilityImpl#getComponentInterface <em>Component Interface</em>}</li>
 *   <li>{@link CapabilityDescription.impl.CapabilityImpl#getRequiredINITProcess <em>Required INIT Process</em>}</li>
 *   <li>{@link CapabilityDescription.impl.CapabilityImpl#getProvidesControlCapabilities <em>Provides Control Capabilities</em>}</li>
 *   <li>{@link CapabilityDescription.impl.CapabilityImpl#getProvidesOutcomes <em>Provides Outcomes</em>}</li>
 * </ul>
 *
 * @generated
 */
public class CapabilityImpl extends MinimalEObjectImpl.Container implements Capability {
	/**
	 * The default value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected static final String NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected String name = NAME_EDEFAULT;

	/**
	 * The cached value of the '{@link #getComponentInterface() <em>Component Interface</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getComponentInterface()
	 * @generated
	 * @ordered
	 */
	protected EList<InterfaceDescription> componentInterface;

	/**
	 * The cached value of the '{@link #getRequiredINITProcess() <em>Required INIT Process</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRequiredINITProcess()
	 * @generated
	 * @ordered
	 */
	protected Action requiredINITProcess;

	/**
	 * The cached value of the '{@link #getProvidesControlCapabilities() <em>Provides Control Capabilities</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getProvidesControlCapabilities()
	 * @generated
	 * @ordered
	 */
	protected ControlCapabilities providesControlCapabilities;

	/**
	 * The cached value of the '{@link #getProvidesOutcomes() <em>Provides Outcomes</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getProvidesOutcomes()
	 * @generated
	 * @ordered
	 */
	protected CapabilitiesOutcome providesOutcomes;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected CapabilityImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return CapabilityDescriptionPackage.Literals.CAPABILITY;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getName() {
		return name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setName(String newName) {
		String oldName = name;
		name = newName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CapabilityDescriptionPackage.CAPABILITY__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<InterfaceDescription> getComponentInterface() {
		if (componentInterface == null) {
			componentInterface = new EObjectEList<InterfaceDescription>(InterfaceDescription.class, this, CapabilityDescriptionPackage.CAPABILITY__COMPONENT_INTERFACE);
		}
		return componentInterface;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Action getRequiredINITProcess() {
		return requiredINITProcess;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetRequiredINITProcess(Action newRequiredINITProcess, NotificationChain msgs) {
		Action oldRequiredINITProcess = requiredINITProcess;
		requiredINITProcess = newRequiredINITProcess;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, CapabilityDescriptionPackage.CAPABILITY__REQUIRED_INIT_PROCESS, oldRequiredINITProcess, newRequiredINITProcess);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setRequiredINITProcess(Action newRequiredINITProcess) {
		if (newRequiredINITProcess != requiredINITProcess) {
			NotificationChain msgs = null;
			if (requiredINITProcess != null)
				msgs = ((InternalEObject)requiredINITProcess).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - CapabilityDescriptionPackage.CAPABILITY__REQUIRED_INIT_PROCESS, null, msgs);
			if (newRequiredINITProcess != null)
				msgs = ((InternalEObject)newRequiredINITProcess).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - CapabilityDescriptionPackage.CAPABILITY__REQUIRED_INIT_PROCESS, null, msgs);
			msgs = basicSetRequiredINITProcess(newRequiredINITProcess, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CapabilityDescriptionPackage.CAPABILITY__REQUIRED_INIT_PROCESS, newRequiredINITProcess, newRequiredINITProcess));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ControlCapabilities getProvidesControlCapabilities() {
		return providesControlCapabilities;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetProvidesControlCapabilities(ControlCapabilities newProvidesControlCapabilities, NotificationChain msgs) {
		ControlCapabilities oldProvidesControlCapabilities = providesControlCapabilities;
		providesControlCapabilities = newProvidesControlCapabilities;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, CapabilityDescriptionPackage.CAPABILITY__PROVIDES_CONTROL_CAPABILITIES, oldProvidesControlCapabilities, newProvidesControlCapabilities);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setProvidesControlCapabilities(ControlCapabilities newProvidesControlCapabilities) {
		if (newProvidesControlCapabilities != providesControlCapabilities) {
			NotificationChain msgs = null;
			if (providesControlCapabilities != null)
				msgs = ((InternalEObject)providesControlCapabilities).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - CapabilityDescriptionPackage.CAPABILITY__PROVIDES_CONTROL_CAPABILITIES, null, msgs);
			if (newProvidesControlCapabilities != null)
				msgs = ((InternalEObject)newProvidesControlCapabilities).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - CapabilityDescriptionPackage.CAPABILITY__PROVIDES_CONTROL_CAPABILITIES, null, msgs);
			msgs = basicSetProvidesControlCapabilities(newProvidesControlCapabilities, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CapabilityDescriptionPackage.CAPABILITY__PROVIDES_CONTROL_CAPABILITIES, newProvidesControlCapabilities, newProvidesControlCapabilities));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public CapabilitiesOutcome getProvidesOutcomes() {
		return providesOutcomes;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetProvidesOutcomes(CapabilitiesOutcome newProvidesOutcomes, NotificationChain msgs) {
		CapabilitiesOutcome oldProvidesOutcomes = providesOutcomes;
		providesOutcomes = newProvidesOutcomes;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, CapabilityDescriptionPackage.CAPABILITY__PROVIDES_OUTCOMES, oldProvidesOutcomes, newProvidesOutcomes);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setProvidesOutcomes(CapabilitiesOutcome newProvidesOutcomes) {
		if (newProvidesOutcomes != providesOutcomes) {
			NotificationChain msgs = null;
			if (providesOutcomes != null)
				msgs = ((InternalEObject)providesOutcomes).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - CapabilityDescriptionPackage.CAPABILITY__PROVIDES_OUTCOMES, null, msgs);
			if (newProvidesOutcomes != null)
				msgs = ((InternalEObject)newProvidesOutcomes).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - CapabilityDescriptionPackage.CAPABILITY__PROVIDES_OUTCOMES, null, msgs);
			msgs = basicSetProvidesOutcomes(newProvidesOutcomes, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CapabilityDescriptionPackage.CAPABILITY__PROVIDES_OUTCOMES, newProvidesOutcomes, newProvidesOutcomes));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case CapabilityDescriptionPackage.CAPABILITY__REQUIRED_INIT_PROCESS:
				return basicSetRequiredINITProcess(null, msgs);
			case CapabilityDescriptionPackage.CAPABILITY__PROVIDES_CONTROL_CAPABILITIES:
				return basicSetProvidesControlCapabilities(null, msgs);
			case CapabilityDescriptionPackage.CAPABILITY__PROVIDES_OUTCOMES:
				return basicSetProvidesOutcomes(null, msgs);
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
			case CapabilityDescriptionPackage.CAPABILITY__NAME:
				return getName();
			case CapabilityDescriptionPackage.CAPABILITY__COMPONENT_INTERFACE:
				return getComponentInterface();
			case CapabilityDescriptionPackage.CAPABILITY__REQUIRED_INIT_PROCESS:
				return getRequiredINITProcess();
			case CapabilityDescriptionPackage.CAPABILITY__PROVIDES_CONTROL_CAPABILITIES:
				return getProvidesControlCapabilities();
			case CapabilityDescriptionPackage.CAPABILITY__PROVIDES_OUTCOMES:
				return getProvidesOutcomes();
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
			case CapabilityDescriptionPackage.CAPABILITY__NAME:
				setName((String)newValue);
				return;
			case CapabilityDescriptionPackage.CAPABILITY__COMPONENT_INTERFACE:
				getComponentInterface().clear();
				getComponentInterface().addAll((Collection<? extends InterfaceDescription>)newValue);
				return;
			case CapabilityDescriptionPackage.CAPABILITY__REQUIRED_INIT_PROCESS:
				setRequiredINITProcess((Action)newValue);
				return;
			case CapabilityDescriptionPackage.CAPABILITY__PROVIDES_CONTROL_CAPABILITIES:
				setProvidesControlCapabilities((ControlCapabilities)newValue);
				return;
			case CapabilityDescriptionPackage.CAPABILITY__PROVIDES_OUTCOMES:
				setProvidesOutcomes((CapabilitiesOutcome)newValue);
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
			case CapabilityDescriptionPackage.CAPABILITY__NAME:
				setName(NAME_EDEFAULT);
				return;
			case CapabilityDescriptionPackage.CAPABILITY__COMPONENT_INTERFACE:
				getComponentInterface().clear();
				return;
			case CapabilityDescriptionPackage.CAPABILITY__REQUIRED_INIT_PROCESS:
				setRequiredINITProcess((Action)null);
				return;
			case CapabilityDescriptionPackage.CAPABILITY__PROVIDES_CONTROL_CAPABILITIES:
				setProvidesControlCapabilities((ControlCapabilities)null);
				return;
			case CapabilityDescriptionPackage.CAPABILITY__PROVIDES_OUTCOMES:
				setProvidesOutcomes((CapabilitiesOutcome)null);
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
			case CapabilityDescriptionPackage.CAPABILITY__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case CapabilityDescriptionPackage.CAPABILITY__COMPONENT_INTERFACE:
				return componentInterface != null && !componentInterface.isEmpty();
			case CapabilityDescriptionPackage.CAPABILITY__REQUIRED_INIT_PROCESS:
				return requiredINITProcess != null;
			case CapabilityDescriptionPackage.CAPABILITY__PROVIDES_CONTROL_CAPABILITIES:
				return providesControlCapabilities != null;
			case CapabilityDescriptionPackage.CAPABILITY__PROVIDES_OUTCOMES:
				return providesOutcomes != null;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuilder result = new StringBuilder(super.toString());
		result.append(" (name: ");
		result.append(name);
		result.append(')');
		return result.toString();
	}

} //CapabilityImpl
