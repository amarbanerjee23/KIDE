/**
 */
package mncModel.impl;

import java.util.Collection;

import mncModel.Action;
import mncModel.CheckParameterCondition;
import mncModel.MncModelPackage;
import mncModel.Validation;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Validation</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link mncModel.impl.ValidationImpl#getOnSuccessAction <em>On Success Action</em>}</li>
 *   <li>{@link mncModel.impl.ValidationImpl#getOnFailedAction <em>On Failed Action</em>}</li>
 *   <li>{@link mncModel.impl.ValidationImpl#getParametersValidationRules <em>Parameters Validation Rules</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ValidationImpl extends MinimalEObjectImpl.Container implements Validation {
	/**
	 * The cached value of the '{@link #getOnSuccessAction() <em>On Success Action</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOnSuccessAction()
	 * @generated
	 * @ordered
	 */
	protected Action onSuccessAction;

	/**
	 * The cached value of the '{@link #getOnFailedAction() <em>On Failed Action</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOnFailedAction()
	 * @generated
	 * @ordered
	 */
	protected Action onFailedAction;

	/**
	 * The cached value of the '{@link #getParametersValidationRules() <em>Parameters Validation Rules</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getParametersValidationRules()
	 * @generated
	 * @ordered
	 */
	protected EList<CheckParameterCondition> parametersValidationRules;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ValidationImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MncModelPackage.Literals.VALIDATION;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Action getOnSuccessAction() {
		return onSuccessAction;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetOnSuccessAction(Action newOnSuccessAction, NotificationChain msgs) {
		Action oldOnSuccessAction = onSuccessAction;
		onSuccessAction = newOnSuccessAction;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, MncModelPackage.VALIDATION__ON_SUCCESS_ACTION, oldOnSuccessAction, newOnSuccessAction);
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
	public void setOnSuccessAction(Action newOnSuccessAction) {
		if (newOnSuccessAction != onSuccessAction) {
			NotificationChain msgs = null;
			if (onSuccessAction != null)
				msgs = ((InternalEObject)onSuccessAction).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - MncModelPackage.VALIDATION__ON_SUCCESS_ACTION, null, msgs);
			if (newOnSuccessAction != null)
				msgs = ((InternalEObject)newOnSuccessAction).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - MncModelPackage.VALIDATION__ON_SUCCESS_ACTION, null, msgs);
			msgs = basicSetOnSuccessAction(newOnSuccessAction, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MncModelPackage.VALIDATION__ON_SUCCESS_ACTION, newOnSuccessAction, newOnSuccessAction));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Action getOnFailedAction() {
		return onFailedAction;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetOnFailedAction(Action newOnFailedAction, NotificationChain msgs) {
		Action oldOnFailedAction = onFailedAction;
		onFailedAction = newOnFailedAction;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, MncModelPackage.VALIDATION__ON_FAILED_ACTION, oldOnFailedAction, newOnFailedAction);
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
	public void setOnFailedAction(Action newOnFailedAction) {
		if (newOnFailedAction != onFailedAction) {
			NotificationChain msgs = null;
			if (onFailedAction != null)
				msgs = ((InternalEObject)onFailedAction).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - MncModelPackage.VALIDATION__ON_FAILED_ACTION, null, msgs);
			if (newOnFailedAction != null)
				msgs = ((InternalEObject)newOnFailedAction).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - MncModelPackage.VALIDATION__ON_FAILED_ACTION, null, msgs);
			msgs = basicSetOnFailedAction(newOnFailedAction, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MncModelPackage.VALIDATION__ON_FAILED_ACTION, newOnFailedAction, newOnFailedAction));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<CheckParameterCondition> getParametersValidationRules() {
		if (parametersValidationRules == null) {
			parametersValidationRules = new EObjectContainmentEList<CheckParameterCondition>(CheckParameterCondition.class, this, MncModelPackage.VALIDATION__PARAMETERS_VALIDATION_RULES);
		}
		return parametersValidationRules;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case MncModelPackage.VALIDATION__ON_SUCCESS_ACTION:
				return basicSetOnSuccessAction(null, msgs);
			case MncModelPackage.VALIDATION__ON_FAILED_ACTION:
				return basicSetOnFailedAction(null, msgs);
			case MncModelPackage.VALIDATION__PARAMETERS_VALIDATION_RULES:
				return ((InternalEList<?>)getParametersValidationRules()).basicRemove(otherEnd, msgs);
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
			case MncModelPackage.VALIDATION__ON_SUCCESS_ACTION:
				return getOnSuccessAction();
			case MncModelPackage.VALIDATION__ON_FAILED_ACTION:
				return getOnFailedAction();
			case MncModelPackage.VALIDATION__PARAMETERS_VALIDATION_RULES:
				return getParametersValidationRules();
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
			case MncModelPackage.VALIDATION__ON_SUCCESS_ACTION:
				setOnSuccessAction((Action)newValue);
				return;
			case MncModelPackage.VALIDATION__ON_FAILED_ACTION:
				setOnFailedAction((Action)newValue);
				return;
			case MncModelPackage.VALIDATION__PARAMETERS_VALIDATION_RULES:
				getParametersValidationRules().clear();
				getParametersValidationRules().addAll((Collection<? extends CheckParameterCondition>)newValue);
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
			case MncModelPackage.VALIDATION__ON_SUCCESS_ACTION:
				setOnSuccessAction((Action)null);
				return;
			case MncModelPackage.VALIDATION__ON_FAILED_ACTION:
				setOnFailedAction((Action)null);
				return;
			case MncModelPackage.VALIDATION__PARAMETERS_VALIDATION_RULES:
				getParametersValidationRules().clear();
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
			case MncModelPackage.VALIDATION__ON_SUCCESS_ACTION:
				return onSuccessAction != null;
			case MncModelPackage.VALIDATION__ON_FAILED_ACTION:
				return onFailedAction != null;
			case MncModelPackage.VALIDATION__PARAMETERS_VALIDATION_RULES:
				return parametersValidationRules != null && !parametersValidationRules.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //ValidationImpl
