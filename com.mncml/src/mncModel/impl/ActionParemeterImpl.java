/**
 */
package mncModel.impl;

import dataModelPackage.PrimitiveValue;

import java.util.Collection;

import mncModel.ActionParemeter;
import mncModel.MncModelPackage;
import mncModel.ParameterTranslation;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Action Paremeter</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link mncModel.impl.ActionParemeterImpl#getParameterMappings <em>Parameter Mappings</em>}</li>
 *   <li>{@link mncModel.impl.ActionParemeterImpl#getParameterValues <em>Parameter Values</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ActionParemeterImpl extends MinimalEObjectImpl.Container implements ActionParemeter {
	/**
	 * The cached value of the '{@link #getParameterMappings() <em>Parameter Mappings</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getParameterMappings()
	 * @generated
	 * @ordered
	 */
	protected EList<ParameterTranslation> parameterMappings;

	/**
	 * The cached value of the '{@link #getParameterValues() <em>Parameter Values</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getParameterValues()
	 * @generated
	 * @ordered
	 */
	protected EList<PrimitiveValue> parameterValues;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ActionParemeterImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MncModelPackage.Literals.ACTION_PAREMETER;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<ParameterTranslation> getParameterMappings() {
		if (parameterMappings == null) {
			parameterMappings = new EObjectContainmentEList<ParameterTranslation>(ParameterTranslation.class, this, MncModelPackage.ACTION_PAREMETER__PARAMETER_MAPPINGS);
		}
		return parameterMappings;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<PrimitiveValue> getParameterValues() {
		if (parameterValues == null) {
			parameterValues = new EObjectContainmentEList<PrimitiveValue>(PrimitiveValue.class, this, MncModelPackage.ACTION_PAREMETER__PARAMETER_VALUES);
		}
		return parameterValues;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case MncModelPackage.ACTION_PAREMETER__PARAMETER_MAPPINGS:
				return ((InternalEList<?>)getParameterMappings()).basicRemove(otherEnd, msgs);
			case MncModelPackage.ACTION_PAREMETER__PARAMETER_VALUES:
				return ((InternalEList<?>)getParameterValues()).basicRemove(otherEnd, msgs);
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
			case MncModelPackage.ACTION_PAREMETER__PARAMETER_MAPPINGS:
				return getParameterMappings();
			case MncModelPackage.ACTION_PAREMETER__PARAMETER_VALUES:
				return getParameterValues();
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
			case MncModelPackage.ACTION_PAREMETER__PARAMETER_MAPPINGS:
				getParameterMappings().clear();
				getParameterMappings().addAll((Collection<? extends ParameterTranslation>)newValue);
				return;
			case MncModelPackage.ACTION_PAREMETER__PARAMETER_VALUES:
				getParameterValues().clear();
				getParameterValues().addAll((Collection<? extends PrimitiveValue>)newValue);
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
			case MncModelPackage.ACTION_PAREMETER__PARAMETER_MAPPINGS:
				getParameterMappings().clear();
				return;
			case MncModelPackage.ACTION_PAREMETER__PARAMETER_VALUES:
				getParameterValues().clear();
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
			case MncModelPackage.ACTION_PAREMETER__PARAMETER_MAPPINGS:
				return parameterMappings != null && !parameterMappings.isEmpty();
			case MncModelPackage.ACTION_PAREMETER__PARAMETER_VALUES:
				return parameterValues != null && !parameterValues.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //ActionParemeterImpl
