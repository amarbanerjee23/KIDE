/**
 */
package dataModelPackage.impl;

import dataModelPackage.ArrayType;
import dataModelPackage.DataModel;
import dataModelPackage.DataModelPackage;
import dataModelPackage.PrimitiveValue;
import dataModelPackage.PrimitiveValueType;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Array Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link dataModelPackage.impl.ArrayTypeImpl#getName <em>Name</em>}</li>
 *   <li>{@link dataModelPackage.impl.ArrayTypeImpl#getValues <em>Values</em>}</li>
 *   <li>{@link dataModelPackage.impl.ArrayTypeImpl#getPrimitiveType <em>Primitive Type</em>}</li>
 *   <li>{@link dataModelPackage.impl.ArrayTypeImpl#getDataModelType <em>Data Model Type</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ArrayTypeImpl extends ParameterImpl implements ArrayType {
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
	 * The cached value of the '{@link #getValues() <em>Values</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getValues()
	 * @generated
	 * @ordered
	 */
	protected EList<PrimitiveValue> values;

	/**
	 * The default value of the '{@link #getPrimitiveType() <em>Primitive Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPrimitiveType()
	 * @generated
	 * @ordered
	 */
	protected static final PrimitiveValueType PRIMITIVE_TYPE_EDEFAULT = PrimitiveValueType.OBJECT;

	/**
	 * The cached value of the '{@link #getPrimitiveType() <em>Primitive Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPrimitiveType()
	 * @generated
	 * @ordered
	 */
	protected PrimitiveValueType primitiveType = PRIMITIVE_TYPE_EDEFAULT;

	/**
	 * The cached value of the '{@link #getDataModelType() <em>Data Model Type</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDataModelType()
	 * @generated
	 * @ordered
	 */
	protected DataModel dataModelType;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ArrayTypeImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return DataModelPackage.Literals.ARRAY_TYPE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setName(String newName) {
		String oldName = name;
		name = newName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DataModelPackage.ARRAY_TYPE__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<PrimitiveValue> getValues() {
		if (values == null) {
			values = new EObjectContainmentEList<PrimitiveValue>(PrimitiveValue.class, this, DataModelPackage.ARRAY_TYPE__VALUES);
		}
		return values;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public PrimitiveValueType getPrimitiveType() {
		return primitiveType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setPrimitiveType(PrimitiveValueType newPrimitiveType) {
		PrimitiveValueType oldPrimitiveType = primitiveType;
		primitiveType = newPrimitiveType == null ? PRIMITIVE_TYPE_EDEFAULT : newPrimitiveType;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DataModelPackage.ARRAY_TYPE__PRIMITIVE_TYPE, oldPrimitiveType, primitiveType));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public DataModel getDataModelType() {
		if (dataModelType != null && dataModelType.eIsProxy()) {
			InternalEObject oldDataModelType = (InternalEObject)dataModelType;
			dataModelType = (DataModel)eResolveProxy(oldDataModelType);
			if (dataModelType != oldDataModelType) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, DataModelPackage.ARRAY_TYPE__DATA_MODEL_TYPE, oldDataModelType, dataModelType));
			}
		}
		return dataModelType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DataModel basicGetDataModelType() {
		return dataModelType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setDataModelType(DataModel newDataModelType) {
		DataModel oldDataModelType = dataModelType;
		dataModelType = newDataModelType;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DataModelPackage.ARRAY_TYPE__DATA_MODEL_TYPE, oldDataModelType, dataModelType));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case DataModelPackage.ARRAY_TYPE__VALUES:
				return ((InternalEList<?>)getValues()).basicRemove(otherEnd, msgs);
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
			case DataModelPackage.ARRAY_TYPE__NAME:
				return getName();
			case DataModelPackage.ARRAY_TYPE__VALUES:
				return getValues();
			case DataModelPackage.ARRAY_TYPE__PRIMITIVE_TYPE:
				return getPrimitiveType();
			case DataModelPackage.ARRAY_TYPE__DATA_MODEL_TYPE:
				if (resolve) return getDataModelType();
				return basicGetDataModelType();
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
			case DataModelPackage.ARRAY_TYPE__NAME:
				setName((String)newValue);
				return;
			case DataModelPackage.ARRAY_TYPE__VALUES:
				getValues().clear();
				getValues().addAll((Collection<? extends PrimitiveValue>)newValue);
				return;
			case DataModelPackage.ARRAY_TYPE__PRIMITIVE_TYPE:
				setPrimitiveType((PrimitiveValueType)newValue);
				return;
			case DataModelPackage.ARRAY_TYPE__DATA_MODEL_TYPE:
				setDataModelType((DataModel)newValue);
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
			case DataModelPackage.ARRAY_TYPE__NAME:
				setName(NAME_EDEFAULT);
				return;
			case DataModelPackage.ARRAY_TYPE__VALUES:
				getValues().clear();
				return;
			case DataModelPackage.ARRAY_TYPE__PRIMITIVE_TYPE:
				setPrimitiveType(PRIMITIVE_TYPE_EDEFAULT);
				return;
			case DataModelPackage.ARRAY_TYPE__DATA_MODEL_TYPE:
				setDataModelType((DataModel)null);
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
			case DataModelPackage.ARRAY_TYPE__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case DataModelPackage.ARRAY_TYPE__VALUES:
				return values != null && !values.isEmpty();
			case DataModelPackage.ARRAY_TYPE__PRIMITIVE_TYPE:
				return primitiveType != PRIMITIVE_TYPE_EDEFAULT;
			case DataModelPackage.ARRAY_TYPE__DATA_MODEL_TYPE:
				return dataModelType != null;
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
		result.append(", primitiveType: ");
		result.append(primitiveType);
		result.append(')');
		return result.toString();
	}

} //ArrayTypeImpl
