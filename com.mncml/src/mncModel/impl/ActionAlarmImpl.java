/**
 */
package mncModel.impl;

import mncModel.ActionAlarm;
import mncModel.Alarm;
import mncModel.MncModelPackage;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Action Alarm</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link mncModel.impl.ActionAlarmImpl#getAlarm <em>Alarm</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ActionAlarmImpl extends ActionItemImpl implements ActionAlarm {
	/**
	 * The cached value of the '{@link #getAlarm() <em>Alarm</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAlarm()
	 * @generated
	 * @ordered
	 */
	protected Alarm alarm;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ActionAlarmImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MncModelPackage.Literals.ACTION_ALARM;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Alarm getAlarm() {
		if (alarm != null && alarm.eIsProxy()) {
			InternalEObject oldAlarm = (InternalEObject)alarm;
			alarm = (Alarm)eResolveProxy(oldAlarm);
			if (alarm != oldAlarm) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, MncModelPackage.ACTION_ALARM__ALARM, oldAlarm, alarm));
			}
		}
		return alarm;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Alarm basicGetAlarm() {
		return alarm;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setAlarm(Alarm newAlarm) {
		Alarm oldAlarm = alarm;
		alarm = newAlarm;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MncModelPackage.ACTION_ALARM__ALARM, oldAlarm, alarm));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case MncModelPackage.ACTION_ALARM__ALARM:
				if (resolve) return getAlarm();
				return basicGetAlarm();
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
			case MncModelPackage.ACTION_ALARM__ALARM:
				setAlarm((Alarm)newValue);
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
			case MncModelPackage.ACTION_ALARM__ALARM:
				setAlarm((Alarm)null);
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
			case MncModelPackage.ACTION_ALARM__ALARM:
				return alarm != null;
		}
		return super.eIsSet(featureID);
	}

} //ActionAlarmImpl
