/**
 */
package mncModel.impl;

import mncModel.ActionDataPoint;
import mncModel.DataPoint;
import mncModel.MncModelPackage;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Action Data Point</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link mncModel.impl.ActionDataPointImpl#getDataPoint <em>Data Point</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ActionDataPointImpl extends ActionItemImpl implements ActionDataPoint {
	/**
	 * The cached value of the '{@link #getDataPoint() <em>Data Point</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDataPoint()
	 * @generated
	 * @ordered
	 */
	protected DataPoint dataPoint;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ActionDataPointImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MncModelPackage.Literals.ACTION_DATA_POINT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public DataPoint getDataPoint() {
		if (dataPoint != null && dataPoint.eIsProxy()) {
			InternalEObject oldDataPoint = (InternalEObject)dataPoint;
			dataPoint = (DataPoint)eResolveProxy(oldDataPoint);
			if (dataPoint != oldDataPoint) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, MncModelPackage.ACTION_DATA_POINT__DATA_POINT, oldDataPoint, dataPoint));
			}
		}
		return dataPoint;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DataPoint basicGetDataPoint() {
		return dataPoint;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setDataPoint(DataPoint newDataPoint) {
		DataPoint oldDataPoint = dataPoint;
		dataPoint = newDataPoint;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MncModelPackage.ACTION_DATA_POINT__DATA_POINT, oldDataPoint, dataPoint));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case MncModelPackage.ACTION_DATA_POINT__DATA_POINT:
				if (resolve) return getDataPoint();
				return basicGetDataPoint();
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
			case MncModelPackage.ACTION_DATA_POINT__DATA_POINT:
				setDataPoint((DataPoint)newValue);
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
			case MncModelPackage.ACTION_DATA_POINT__DATA_POINT:
				setDataPoint((DataPoint)null);
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
			case MncModelPackage.ACTION_DATA_POINT__DATA_POINT:
				return dataPoint != null;
		}
		return super.eIsSet(featureID);
	}

} //ActionDataPointImpl
