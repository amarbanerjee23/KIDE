/**
 */
package mncModel.utility.impl;

import java.util.Collection;

import mncModel.OperatingState;

import mncModel.utility.OperatingStateUtility;
import mncModel.utility.UtilityPackage;

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
 * An implementation of the model object '<em><b>Operating State Utility</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link mncModel.utility.impl.OperatingStateUtilityImpl#getOperatingStates <em>Operating States</em>}</li>
 *   <li>{@link mncModel.utility.impl.OperatingStateUtilityImpl#getStartState <em>Start State</em>}</li>
 *   <li>{@link mncModel.utility.impl.OperatingStateUtilityImpl#getEndState <em>End State</em>}</li>
 * </ul>
 *
 * @generated
 */
public class OperatingStateUtilityImpl extends MinimalEObjectImpl.Container implements OperatingStateUtility {
	/**
	 * The cached value of the '{@link #getOperatingStates() <em>Operating States</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOperatingStates()
	 * @generated
	 * @ordered
	 */
	protected EList<OperatingState> operatingStates;

	/**
	 * The cached value of the '{@link #getStartState() <em>Start State</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getStartState()
	 * @generated
	 * @ordered
	 */
	protected EList<OperatingState> startState;

	/**
	 * The cached value of the '{@link #getEndState() <em>End State</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEndState()
	 * @generated
	 * @ordered
	 */
	protected EList<OperatingState> endState;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected OperatingStateUtilityImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return UtilityPackage.Literals.OPERATING_STATE_UTILITY;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<OperatingState> getOperatingStates() {
		if (operatingStates == null) {
			operatingStates = new EObjectContainmentEList<OperatingState>(OperatingState.class, this, UtilityPackage.OPERATING_STATE_UTILITY__OPERATING_STATES);
		}
		return operatingStates;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<OperatingState> getStartState() {
		if (startState == null) {
			startState = new EObjectResolvingEList<OperatingState>(OperatingState.class, this, UtilityPackage.OPERATING_STATE_UTILITY__START_STATE);
		}
		return startState;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<OperatingState> getEndState() {
		if (endState == null) {
			endState = new EObjectResolvingEList<OperatingState>(OperatingState.class, this, UtilityPackage.OPERATING_STATE_UTILITY__END_STATE);
		}
		return endState;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case UtilityPackage.OPERATING_STATE_UTILITY__OPERATING_STATES:
				return ((InternalEList<?>)getOperatingStates()).basicRemove(otherEnd, msgs);
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
			case UtilityPackage.OPERATING_STATE_UTILITY__OPERATING_STATES:
				return getOperatingStates();
			case UtilityPackage.OPERATING_STATE_UTILITY__START_STATE:
				return getStartState();
			case UtilityPackage.OPERATING_STATE_UTILITY__END_STATE:
				return getEndState();
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
			case UtilityPackage.OPERATING_STATE_UTILITY__OPERATING_STATES:
				getOperatingStates().clear();
				getOperatingStates().addAll((Collection<? extends OperatingState>)newValue);
				return;
			case UtilityPackage.OPERATING_STATE_UTILITY__START_STATE:
				getStartState().clear();
				getStartState().addAll((Collection<? extends OperatingState>)newValue);
				return;
			case UtilityPackage.OPERATING_STATE_UTILITY__END_STATE:
				getEndState().clear();
				getEndState().addAll((Collection<? extends OperatingState>)newValue);
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
			case UtilityPackage.OPERATING_STATE_UTILITY__OPERATING_STATES:
				getOperatingStates().clear();
				return;
			case UtilityPackage.OPERATING_STATE_UTILITY__START_STATE:
				getStartState().clear();
				return;
			case UtilityPackage.OPERATING_STATE_UTILITY__END_STATE:
				getEndState().clear();
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
			case UtilityPackage.OPERATING_STATE_UTILITY__OPERATING_STATES:
				return operatingStates != null && !operatingStates.isEmpty();
			case UtilityPackage.OPERATING_STATE_UTILITY__START_STATE:
				return startState != null && !startState.isEmpty();
			case UtilityPackage.OPERATING_STATE_UTILITY__END_STATE:
				return endState != null && !endState.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //OperatingStateUtilityImpl
