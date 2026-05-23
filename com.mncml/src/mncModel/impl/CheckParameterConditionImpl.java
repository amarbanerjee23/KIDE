/**
 */
package mncModel.impl;

import dataModelPackage.Parameter;
import dataModelPackage.PrimitiveValue;

import java.util.Collection;

import mncModel.CheckParameterCondition;
import mncModel.MncModelPackage;

import operationsDescription.Operation;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Check Parameter Condition</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link mncModel.impl.CheckParameterConditionImpl#getParameter <em>Parameter</em>}</li>
 *   <li>{@link mncModel.impl.CheckParameterConditionImpl#getCheckValues <em>Check Values</em>}</li>
 *   <li>{@link mncModel.impl.CheckParameterConditionImpl#getCheckMaxValue <em>Check Max Value</em>}</li>
 *   <li>{@link mncModel.impl.CheckParameterConditionImpl#getCheckMinValue <em>Check Min Value</em>}</li>
 *   <li>{@link mncModel.impl.CheckParameterConditionImpl#getOperations <em>Operations</em>}</li>
 * </ul>
 *
 * @generated
 */
public class CheckParameterConditionImpl extends MinimalEObjectImpl.Container implements CheckParameterCondition {
	/**
	 * The cached value of the '{@link #getParameter() <em>Parameter</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getParameter()
	 * @generated
	 * @ordered
	 */
	protected EList<Parameter> parameter;

	/**
	 * The cached value of the '{@link #getCheckValues() <em>Check Values</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCheckValues()
	 * @generated
	 * @ordered
	 */
	protected EList<PrimitiveValue> checkValues;

	/**
	 * The cached value of the '{@link #getCheckMaxValue() <em>Check Max Value</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCheckMaxValue()
	 * @generated
	 * @ordered
	 */
	protected PrimitiveValue checkMaxValue;

	/**
	 * The cached value of the '{@link #getCheckMinValue() <em>Check Min Value</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCheckMinValue()
	 * @generated
	 * @ordered
	 */
	protected PrimitiveValue checkMinValue;

	/**
	 * The cached value of the '{@link #getOperations() <em>Operations</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOperations()
	 * @generated
	 * @ordered
	 */
	protected EList<Operation> operations;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected CheckParameterConditionImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MncModelPackage.Literals.CHECK_PARAMETER_CONDITION;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<Parameter> getParameter() {
		if (parameter == null) {
			parameter = new EObjectResolvingEList<Parameter>(Parameter.class, this, MncModelPackage.CHECK_PARAMETER_CONDITION__PARAMETER);
		}
		return parameter;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<PrimitiveValue> getCheckValues() {
		if (checkValues == null) {
			checkValues = new EObjectContainmentEList<PrimitiveValue>(PrimitiveValue.class, this, MncModelPackage.CHECK_PARAMETER_CONDITION__CHECK_VALUES);
		}
		return checkValues;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public PrimitiveValue getCheckMaxValue() {
		return checkMaxValue;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetCheckMaxValue(PrimitiveValue newCheckMaxValue, NotificationChain msgs) {
		PrimitiveValue oldCheckMaxValue = checkMaxValue;
		checkMaxValue = newCheckMaxValue;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, MncModelPackage.CHECK_PARAMETER_CONDITION__CHECK_MAX_VALUE, oldCheckMaxValue, newCheckMaxValue);
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
	public void setCheckMaxValue(PrimitiveValue newCheckMaxValue) {
		if (newCheckMaxValue != checkMaxValue) {
			NotificationChain msgs = null;
			if (checkMaxValue != null)
				msgs = ((InternalEObject)checkMaxValue).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - MncModelPackage.CHECK_PARAMETER_CONDITION__CHECK_MAX_VALUE, null, msgs);
			if (newCheckMaxValue != null)
				msgs = ((InternalEObject)newCheckMaxValue).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - MncModelPackage.CHECK_PARAMETER_CONDITION__CHECK_MAX_VALUE, null, msgs);
			msgs = basicSetCheckMaxValue(newCheckMaxValue, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MncModelPackage.CHECK_PARAMETER_CONDITION__CHECK_MAX_VALUE, newCheckMaxValue, newCheckMaxValue));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public PrimitiveValue getCheckMinValue() {
		return checkMinValue;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetCheckMinValue(PrimitiveValue newCheckMinValue, NotificationChain msgs) {
		PrimitiveValue oldCheckMinValue = checkMinValue;
		checkMinValue = newCheckMinValue;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, MncModelPackage.CHECK_PARAMETER_CONDITION__CHECK_MIN_VALUE, oldCheckMinValue, newCheckMinValue);
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
	public void setCheckMinValue(PrimitiveValue newCheckMinValue) {
		if (newCheckMinValue != checkMinValue) {
			NotificationChain msgs = null;
			if (checkMinValue != null)
				msgs = ((InternalEObject)checkMinValue).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - MncModelPackage.CHECK_PARAMETER_CONDITION__CHECK_MIN_VALUE, null, msgs);
			if (newCheckMinValue != null)
				msgs = ((InternalEObject)newCheckMinValue).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - MncModelPackage.CHECK_PARAMETER_CONDITION__CHECK_MIN_VALUE, null, msgs);
			msgs = basicSetCheckMinValue(newCheckMinValue, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MncModelPackage.CHECK_PARAMETER_CONDITION__CHECK_MIN_VALUE, newCheckMinValue, newCheckMinValue));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<Operation> getOperations() {
		if (operations == null) {
			operations = new EObjectResolvingEList<Operation>(Operation.class, this, MncModelPackage.CHECK_PARAMETER_CONDITION__OPERATIONS);
		}
		return operations;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case MncModelPackage.CHECK_PARAMETER_CONDITION__CHECK_VALUES:
				return ((InternalEList<?>)getCheckValues()).basicRemove(otherEnd, msgs);
			case MncModelPackage.CHECK_PARAMETER_CONDITION__CHECK_MAX_VALUE:
				return basicSetCheckMaxValue(null, msgs);
			case MncModelPackage.CHECK_PARAMETER_CONDITION__CHECK_MIN_VALUE:
				return basicSetCheckMinValue(null, msgs);
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
			case MncModelPackage.CHECK_PARAMETER_CONDITION__PARAMETER:
				return getParameter();
			case MncModelPackage.CHECK_PARAMETER_CONDITION__CHECK_VALUES:
				return getCheckValues();
			case MncModelPackage.CHECK_PARAMETER_CONDITION__CHECK_MAX_VALUE:
				return getCheckMaxValue();
			case MncModelPackage.CHECK_PARAMETER_CONDITION__CHECK_MIN_VALUE:
				return getCheckMinValue();
			case MncModelPackage.CHECK_PARAMETER_CONDITION__OPERATIONS:
				return getOperations();
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
			case MncModelPackage.CHECK_PARAMETER_CONDITION__PARAMETER:
				getParameter().clear();
				getParameter().addAll((Collection<? extends Parameter>)newValue);
				return;
			case MncModelPackage.CHECK_PARAMETER_CONDITION__CHECK_VALUES:
				getCheckValues().clear();
				getCheckValues().addAll((Collection<? extends PrimitiveValue>)newValue);
				return;
			case MncModelPackage.CHECK_PARAMETER_CONDITION__CHECK_MAX_VALUE:
				setCheckMaxValue((PrimitiveValue)newValue);
				return;
			case MncModelPackage.CHECK_PARAMETER_CONDITION__CHECK_MIN_VALUE:
				setCheckMinValue((PrimitiveValue)newValue);
				return;
			case MncModelPackage.CHECK_PARAMETER_CONDITION__OPERATIONS:
				getOperations().clear();
				getOperations().addAll((Collection<? extends Operation>)newValue);
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
			case MncModelPackage.CHECK_PARAMETER_CONDITION__PARAMETER:
				getParameter().clear();
				return;
			case MncModelPackage.CHECK_PARAMETER_CONDITION__CHECK_VALUES:
				getCheckValues().clear();
				return;
			case MncModelPackage.CHECK_PARAMETER_CONDITION__CHECK_MAX_VALUE:
				setCheckMaxValue((PrimitiveValue)null);
				return;
			case MncModelPackage.CHECK_PARAMETER_CONDITION__CHECK_MIN_VALUE:
				setCheckMinValue((PrimitiveValue)null);
				return;
			case MncModelPackage.CHECK_PARAMETER_CONDITION__OPERATIONS:
				getOperations().clear();
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
			case MncModelPackage.CHECK_PARAMETER_CONDITION__PARAMETER:
				return parameter != null && !parameter.isEmpty();
			case MncModelPackage.CHECK_PARAMETER_CONDITION__CHECK_VALUES:
				return checkValues != null && !checkValues.isEmpty();
			case MncModelPackage.CHECK_PARAMETER_CONDITION__CHECK_MAX_VALUE:
				return checkMaxValue != null;
			case MncModelPackage.CHECK_PARAMETER_CONDITION__CHECK_MIN_VALUE:
				return checkMinValue != null;
			case MncModelPackage.CHECK_PARAMETER_CONDITION__OPERATIONS:
				return operations != null && !operations.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //CheckParameterConditionImpl
