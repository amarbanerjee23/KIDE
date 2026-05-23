/**
 */
package mncModel.impl;

import dataModelPackage.PrimitiveValue;
import dataModelPackage.PrimitiveValueType;

import mncModel.DataPoint;
import mncModel.MncModelPackage;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Data Point</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link mncModel.impl.DataPointImpl#getType <em>Type</em>}</li>
 *   <li>{@link mncModel.impl.DataPointImpl#getValue <em>Value</em>}</li>
 *   <li>{@link mncModel.impl.DataPointImpl#isPublish <em>Publish</em>}</li>
 * </ul>
 *
 * @generated
 */
public class DataPointImpl extends AbstractInterfaceItemsImpl implements DataPoint {
	/**
	 * The default value of the '{@link #getType() <em>Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getType()
	 * @generated
	 * @ordered
	 */
	protected static final PrimitiveValueType TYPE_EDEFAULT = PrimitiveValueType.OBJECT;

	/**
	 * The cached value of the '{@link #getType() <em>Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getType()
	 * @generated
	 * @ordered
	 */
	protected PrimitiveValueType type = TYPE_EDEFAULT;

	/**
	 * The cached value of the '{@link #getValue() <em>Value</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getValue()
	 * @generated
	 * @ordered
	 */
	protected PrimitiveValue value;

	/**
	 * The default value of the '{@link #isPublish() <em>Publish</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isPublish()
	 * @generated
	 * @ordered
	 */
	protected static final boolean PUBLISH_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isPublish() <em>Publish</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isPublish()
	 * @generated
	 * @ordered
	 */
	protected boolean publish = PUBLISH_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected DataPointImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MncModelPackage.Literals.DATA_POINT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public PrimitiveValueType getType() {
		return type;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setType(PrimitiveValueType newType) {
		PrimitiveValueType oldType = type;
		type = newType == null ? TYPE_EDEFAULT : newType;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MncModelPackage.DATA_POINT__TYPE, oldType, type));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public PrimitiveValue getValue() {
		return value;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetValue(PrimitiveValue newValue, NotificationChain msgs) {
		PrimitiveValue oldValue = value;
		value = newValue;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, MncModelPackage.DATA_POINT__VALUE, oldValue, newValue);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setValue(PrimitiveValue newValue) {
		if (newValue != value) {
			NotificationChain msgs = null;
			if (value != null)
				msgs = ((InternalEObject)value).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - MncModelPackage.DATA_POINT__VALUE, null, msgs);
			if (newValue != null)
				msgs = ((InternalEObject)newValue).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - MncModelPackage.DATA_POINT__VALUE, null, msgs);
			msgs = basicSetValue(newValue, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MncModelPackage.DATA_POINT__VALUE, newValue, newValue));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isPublish() {
		return publish;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setPublish(boolean newPublish) {
		boolean oldPublish = publish;
		publish = newPublish;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MncModelPackage.DATA_POINT__PUBLISH, oldPublish, publish));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case MncModelPackage.DATA_POINT__VALUE:
				return basicSetValue(null, msgs);
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
			case MncModelPackage.DATA_POINT__TYPE:
				return getType();
			case MncModelPackage.DATA_POINT__VALUE:
				return getValue();
			case MncModelPackage.DATA_POINT__PUBLISH:
				return isPublish();
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
			case MncModelPackage.DATA_POINT__TYPE:
				setType((PrimitiveValueType)newValue);
				return;
			case MncModelPackage.DATA_POINT__VALUE:
				setValue((PrimitiveValue)newValue);
				return;
			case MncModelPackage.DATA_POINT__PUBLISH:
				setPublish((Boolean)newValue);
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
			case MncModelPackage.DATA_POINT__TYPE:
				setType(TYPE_EDEFAULT);
				return;
			case MncModelPackage.DATA_POINT__VALUE:
				setValue((PrimitiveValue)null);
				return;
			case MncModelPackage.DATA_POINT__PUBLISH:
				setPublish(PUBLISH_EDEFAULT);
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
			case MncModelPackage.DATA_POINT__TYPE:
				return type != TYPE_EDEFAULT;
			case MncModelPackage.DATA_POINT__VALUE:
				return value != null;
			case MncModelPackage.DATA_POINT__PUBLISH:
				return publish != PUBLISH_EDEFAULT;
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
		result.append(" (type: ");
		result.append(type);
		result.append(", publish: ");
		result.append(publish);
		result.append(')');
		return result.toString();
	}

} //DataPointImpl
