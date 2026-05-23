/**
 */
package dataModelPackage.impl;

import dataModelPackage.AbstractObjectValue;
import dataModelPackage.DataModelPackage;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Abstract Object Value</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link dataModelPackage.impl.AbstractObjectValueImpl#getAbstractValue <em>Abstract Value</em>}</li>
 * </ul>
 *
 * @generated
 */
public class AbstractObjectValueImpl extends PrimitiveValueImpl implements AbstractObjectValue {
	/**
	 * The default value of the '{@link #getAbstractValue() <em>Abstract Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAbstractValue()
	 * @generated
	 * @ordered
	 */
	protected static final String ABSTRACT_VALUE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getAbstractValue() <em>Abstract Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAbstractValue()
	 * @generated
	 * @ordered
	 */
	protected String abstractValue = ABSTRACT_VALUE_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected AbstractObjectValueImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return DataModelPackage.Literals.ABSTRACT_OBJECT_VALUE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getAbstractValue() {
		return abstractValue;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setAbstractValue(String newAbstractValue) {
		String oldAbstractValue = abstractValue;
		abstractValue = newAbstractValue;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DataModelPackage.ABSTRACT_OBJECT_VALUE__ABSTRACT_VALUE, oldAbstractValue, abstractValue));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case DataModelPackage.ABSTRACT_OBJECT_VALUE__ABSTRACT_VALUE:
				return getAbstractValue();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case DataModelPackage.ABSTRACT_OBJECT_VALUE__ABSTRACT_VALUE:
				setAbstractValue((String)newValue);
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
			case DataModelPackage.ABSTRACT_OBJECT_VALUE__ABSTRACT_VALUE:
				setAbstractValue(ABSTRACT_VALUE_EDEFAULT);
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
			case DataModelPackage.ABSTRACT_OBJECT_VALUE__ABSTRACT_VALUE:
				return ABSTRACT_VALUE_EDEFAULT == null ? abstractValue != null : !ABSTRACT_VALUE_EDEFAULT.equals(abstractValue);
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
		result.append(" (abstractValue: ");
		result.append(abstractValue);
		result.append(')');
		return result.toString();
	}

} //AbstractObjectValueImpl
