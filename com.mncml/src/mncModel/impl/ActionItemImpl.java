/**
 */
package mncModel.impl;

import mncModel.ActionItem;
import mncModel.ActionParemeter;
import mncModel.MncModelPackage;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Action Item</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link mncModel.impl.ActionItemImpl#getActionParemeter <em>Action Paremeter</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ActionItemImpl extends MinimalEObjectImpl.Container implements ActionItem {
	/**
	 * The cached value of the '{@link #getActionParemeter() <em>Action Paremeter</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getActionParemeter()
	 * @generated
	 * @ordered
	 */
	protected ActionParemeter actionParemeter;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ActionItemImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MncModelPackage.Literals.ACTION_ITEM;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ActionParemeter getActionParemeter() {
		return actionParemeter;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetActionParemeter(ActionParemeter newActionParemeter, NotificationChain msgs) {
		ActionParemeter oldActionParemeter = actionParemeter;
		actionParemeter = newActionParemeter;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, MncModelPackage.ACTION_ITEM__ACTION_PAREMETER, oldActionParemeter, newActionParemeter);
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
	public void setActionParemeter(ActionParemeter newActionParemeter) {
		if (newActionParemeter != actionParemeter) {
			NotificationChain msgs = null;
			if (actionParemeter != null)
				msgs = ((InternalEObject)actionParemeter).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - MncModelPackage.ACTION_ITEM__ACTION_PAREMETER, null, msgs);
			if (newActionParemeter != null)
				msgs = ((InternalEObject)newActionParemeter).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - MncModelPackage.ACTION_ITEM__ACTION_PAREMETER, null, msgs);
			msgs = basicSetActionParemeter(newActionParemeter, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MncModelPackage.ACTION_ITEM__ACTION_PAREMETER, newActionParemeter, newActionParemeter));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case MncModelPackage.ACTION_ITEM__ACTION_PAREMETER:
				return basicSetActionParemeter(null, msgs);
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
			case MncModelPackage.ACTION_ITEM__ACTION_PAREMETER:
				return getActionParemeter();
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
			case MncModelPackage.ACTION_ITEM__ACTION_PAREMETER:
				setActionParemeter((ActionParemeter)newValue);
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
			case MncModelPackage.ACTION_ITEM__ACTION_PAREMETER:
				setActionParemeter((ActionParemeter)null);
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
			case MncModelPackage.ACTION_ITEM__ACTION_PAREMETER:
				return actionParemeter != null;
		}
		return super.eIsSet(featureID);
	}

} //ActionItemImpl
