/**
 */
package dataModelPackage.impl;

import dataModelPackage.BoolValue;
import dataModelPackage.DataModelPackage;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Bool Value</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link dataModelPackage.impl.BoolValueImpl#isBoolValue <em>Bool Value</em>}</li>
 * </ul>
 *
 * @generated
 */
public class BoolValueImpl extends PrimitiveValueImpl implements BoolValue {
	/**
	 * The default value of the '{@link #isBoolValue() <em>Bool Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isBoolValue()
	 * @generated
	 * @ordered
	 */
	protected static final boolean BOOL_VALUE_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isBoolValue() <em>Bool Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isBoolValue()
	 * @generated
	 * @ordered
	 */
	protected boolean boolValue = BOOL_VALUE_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected BoolValueImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return DataModelPackage.Literals.BOOL_VALUE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isBoolValue() {
		return boolValue;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setBoolValue(boolean newBoolValue) {
		boolean oldBoolValue = boolValue;
		boolValue = newBoolValue;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DataModelPackage.BOOL_VALUE__BOOL_VALUE, oldBoolValue, boolValue));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case DataModelPackage.BOOL_VALUE__BOOL_VALUE:
				return isBoolValue();
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
			case DataModelPackage.BOOL_VALUE__BOOL_VALUE:
				setBoolValue((Boolean)newValue);
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
			case DataModelPackage.BOOL_VALUE__BOOL_VALUE:
				setBoolValue(BOOL_VALUE_EDEFAULT);
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
			case DataModelPackage.BOOL_VALUE__BOOL_VALUE:
				return boolValue != BOOL_VALUE_EDEFAULT;
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
		result.append(" (boolValue: ");
		result.append(boolValue);
		result.append(')');
		return result.toString();
	}

} //BoolValueImpl
