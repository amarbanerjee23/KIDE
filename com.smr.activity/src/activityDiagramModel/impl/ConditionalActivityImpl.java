/**
 */
package activityDiagramModel.impl;

import activityDiagramModel.Activity;
import activityDiagramModel.ActivityDiagramModelPackage;
import activityDiagramModel.ConditionalActivity;
import activityDiagramModel.Outcome;

import dataModelPackage.Parameter;

import java.util.Collection;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;
import org.eclipse.emf.ecore.util.EDataTypeUniqueEList;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Conditional Activity</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link activityDiagramModel.impl.ConditionalActivityImpl#getOnTrueNextActivity <em>On True Next Activity</em>}</li>
 *   <li>{@link activityDiagramModel.impl.ConditionalActivityImpl#getOnFalseNextActivity <em>On False Next Activity</em>}</li>
 *   <li>{@link activityDiagramModel.impl.ConditionalActivityImpl#getOnTrueFinalResult <em>On True Final Result</em>}</li>
 *   <li>{@link activityDiagramModel.impl.ConditionalActivityImpl#getOnFalseFinalResult <em>On False Final Result</em>}</li>
 *   <li>{@link activityDiagramModel.impl.ConditionalActivityImpl#getOutcome <em>Outcome</em>}</li>
 *   <li>{@link activityDiagramModel.impl.ConditionalActivityImpl#getBOp <em>BOp</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ConditionalActivityImpl extends MinimalEObjectImpl.Container implements ConditionalActivity {
	/**
	 * The cached value of the '{@link #getOnTrueNextActivity() <em>On True Next Activity</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOnTrueNextActivity()
	 * @generated
	 * @ordered
	 */
	protected Activity onTrueNextActivity;

	/**
	 * The cached value of the '{@link #getOnFalseNextActivity() <em>On False Next Activity</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOnFalseNextActivity()
	 * @generated
	 * @ordered
	 */
	protected Activity onFalseNextActivity;

	/**
	 * The cached value of the '{@link #getOnTrueFinalResult() <em>On True Final Result</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOnTrueFinalResult()
	 * @generated
	 * @ordered
	 */
	protected Parameter onTrueFinalResult;

	/**
	 * The cached value of the '{@link #getOnFalseFinalResult() <em>On False Final Result</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOnFalseFinalResult()
	 * @generated
	 * @ordered
	 */
	protected Parameter onFalseFinalResult;

	/**
	 * The cached value of the '{@link #getOutcome() <em>Outcome</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOutcome()
	 * @generated
	 * @ordered
	 */
	protected EList<Outcome> outcome;

	/**
	 * The cached value of the '{@link #getBOp() <em>BOp</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBOp()
	 * @generated
	 * @ordered
	 */
	protected EList<String> bOp;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ConditionalActivityImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ActivityDiagramModelPackage.Literals.CONDITIONAL_ACTIVITY;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Activity getOnTrueNextActivity() {
		if (onTrueNextActivity != null && onTrueNextActivity.eIsProxy()) {
			InternalEObject oldOnTrueNextActivity = (InternalEObject)onTrueNextActivity;
			onTrueNextActivity = (Activity)eResolveProxy(oldOnTrueNextActivity);
			if (onTrueNextActivity != oldOnTrueNextActivity) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ActivityDiagramModelPackage.CONDITIONAL_ACTIVITY__ON_TRUE_NEXT_ACTIVITY, oldOnTrueNextActivity, onTrueNextActivity));
			}
		}
		return onTrueNextActivity;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Activity basicGetOnTrueNextActivity() {
		return onTrueNextActivity;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setOnTrueNextActivity(Activity newOnTrueNextActivity) {
		Activity oldOnTrueNextActivity = onTrueNextActivity;
		onTrueNextActivity = newOnTrueNextActivity;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ActivityDiagramModelPackage.CONDITIONAL_ACTIVITY__ON_TRUE_NEXT_ACTIVITY, oldOnTrueNextActivity, onTrueNextActivity));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Activity getOnFalseNextActivity() {
		if (onFalseNextActivity != null && onFalseNextActivity.eIsProxy()) {
			InternalEObject oldOnFalseNextActivity = (InternalEObject)onFalseNextActivity;
			onFalseNextActivity = (Activity)eResolveProxy(oldOnFalseNextActivity);
			if (onFalseNextActivity != oldOnFalseNextActivity) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ActivityDiagramModelPackage.CONDITIONAL_ACTIVITY__ON_FALSE_NEXT_ACTIVITY, oldOnFalseNextActivity, onFalseNextActivity));
			}
		}
		return onFalseNextActivity;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Activity basicGetOnFalseNextActivity() {
		return onFalseNextActivity;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setOnFalseNextActivity(Activity newOnFalseNextActivity) {
		Activity oldOnFalseNextActivity = onFalseNextActivity;
		onFalseNextActivity = newOnFalseNextActivity;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ActivityDiagramModelPackage.CONDITIONAL_ACTIVITY__ON_FALSE_NEXT_ACTIVITY, oldOnFalseNextActivity, onFalseNextActivity));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Parameter getOnTrueFinalResult() {
		if (onTrueFinalResult != null && onTrueFinalResult.eIsProxy()) {
			InternalEObject oldOnTrueFinalResult = (InternalEObject)onTrueFinalResult;
			onTrueFinalResult = (Parameter)eResolveProxy(oldOnTrueFinalResult);
			if (onTrueFinalResult != oldOnTrueFinalResult) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ActivityDiagramModelPackage.CONDITIONAL_ACTIVITY__ON_TRUE_FINAL_RESULT, oldOnTrueFinalResult, onTrueFinalResult));
			}
		}
		return onTrueFinalResult;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Parameter basicGetOnTrueFinalResult() {
		return onTrueFinalResult;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setOnTrueFinalResult(Parameter newOnTrueFinalResult) {
		Parameter oldOnTrueFinalResult = onTrueFinalResult;
		onTrueFinalResult = newOnTrueFinalResult;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ActivityDiagramModelPackage.CONDITIONAL_ACTIVITY__ON_TRUE_FINAL_RESULT, oldOnTrueFinalResult, onTrueFinalResult));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Parameter getOnFalseFinalResult() {
		if (onFalseFinalResult != null && onFalseFinalResult.eIsProxy()) {
			InternalEObject oldOnFalseFinalResult = (InternalEObject)onFalseFinalResult;
			onFalseFinalResult = (Parameter)eResolveProxy(oldOnFalseFinalResult);
			if (onFalseFinalResult != oldOnFalseFinalResult) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ActivityDiagramModelPackage.CONDITIONAL_ACTIVITY__ON_FALSE_FINAL_RESULT, oldOnFalseFinalResult, onFalseFinalResult));
			}
		}
		return onFalseFinalResult;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Parameter basicGetOnFalseFinalResult() {
		return onFalseFinalResult;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setOnFalseFinalResult(Parameter newOnFalseFinalResult) {
		Parameter oldOnFalseFinalResult = onFalseFinalResult;
		onFalseFinalResult = newOnFalseFinalResult;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ActivityDiagramModelPackage.CONDITIONAL_ACTIVITY__ON_FALSE_FINAL_RESULT, oldOnFalseFinalResult, onFalseFinalResult));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<Outcome> getOutcome() {
		if (outcome == null) {
			outcome = new EObjectContainmentEList<Outcome>(Outcome.class, this, ActivityDiagramModelPackage.CONDITIONAL_ACTIVITY__OUTCOME);
		}
		return outcome;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<String> getBOp() {
		if (bOp == null) {
			bOp = new EDataTypeUniqueEList<String>(String.class, this, ActivityDiagramModelPackage.CONDITIONAL_ACTIVITY__BOP);
		}
		return bOp;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case ActivityDiagramModelPackage.CONDITIONAL_ACTIVITY__OUTCOME:
				return ((InternalEList<?>)getOutcome()).basicRemove(otherEnd, msgs);
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
			case ActivityDiagramModelPackage.CONDITIONAL_ACTIVITY__ON_TRUE_NEXT_ACTIVITY:
				if (resolve) return getOnTrueNextActivity();
				return basicGetOnTrueNextActivity();
			case ActivityDiagramModelPackage.CONDITIONAL_ACTIVITY__ON_FALSE_NEXT_ACTIVITY:
				if (resolve) return getOnFalseNextActivity();
				return basicGetOnFalseNextActivity();
			case ActivityDiagramModelPackage.CONDITIONAL_ACTIVITY__ON_TRUE_FINAL_RESULT:
				if (resolve) return getOnTrueFinalResult();
				return basicGetOnTrueFinalResult();
			case ActivityDiagramModelPackage.CONDITIONAL_ACTIVITY__ON_FALSE_FINAL_RESULT:
				if (resolve) return getOnFalseFinalResult();
				return basicGetOnFalseFinalResult();
			case ActivityDiagramModelPackage.CONDITIONAL_ACTIVITY__OUTCOME:
				return getOutcome();
			case ActivityDiagramModelPackage.CONDITIONAL_ACTIVITY__BOP:
				return getBOp();
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
			case ActivityDiagramModelPackage.CONDITIONAL_ACTIVITY__ON_TRUE_NEXT_ACTIVITY:
				setOnTrueNextActivity((Activity)newValue);
				return;
			case ActivityDiagramModelPackage.CONDITIONAL_ACTIVITY__ON_FALSE_NEXT_ACTIVITY:
				setOnFalseNextActivity((Activity)newValue);
				return;
			case ActivityDiagramModelPackage.CONDITIONAL_ACTIVITY__ON_TRUE_FINAL_RESULT:
				setOnTrueFinalResult((Parameter)newValue);
				return;
			case ActivityDiagramModelPackage.CONDITIONAL_ACTIVITY__ON_FALSE_FINAL_RESULT:
				setOnFalseFinalResult((Parameter)newValue);
				return;
			case ActivityDiagramModelPackage.CONDITIONAL_ACTIVITY__OUTCOME:
				getOutcome().clear();
				getOutcome().addAll((Collection<? extends Outcome>)newValue);
				return;
			case ActivityDiagramModelPackage.CONDITIONAL_ACTIVITY__BOP:
				getBOp().clear();
				getBOp().addAll((Collection<? extends String>)newValue);
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
			case ActivityDiagramModelPackage.CONDITIONAL_ACTIVITY__ON_TRUE_NEXT_ACTIVITY:
				setOnTrueNextActivity((Activity)null);
				return;
			case ActivityDiagramModelPackage.CONDITIONAL_ACTIVITY__ON_FALSE_NEXT_ACTIVITY:
				setOnFalseNextActivity((Activity)null);
				return;
			case ActivityDiagramModelPackage.CONDITIONAL_ACTIVITY__ON_TRUE_FINAL_RESULT:
				setOnTrueFinalResult((Parameter)null);
				return;
			case ActivityDiagramModelPackage.CONDITIONAL_ACTIVITY__ON_FALSE_FINAL_RESULT:
				setOnFalseFinalResult((Parameter)null);
				return;
			case ActivityDiagramModelPackage.CONDITIONAL_ACTIVITY__OUTCOME:
				getOutcome().clear();
				return;
			case ActivityDiagramModelPackage.CONDITIONAL_ACTIVITY__BOP:
				getBOp().clear();
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
			case ActivityDiagramModelPackage.CONDITIONAL_ACTIVITY__ON_TRUE_NEXT_ACTIVITY:
				return onTrueNextActivity != null;
			case ActivityDiagramModelPackage.CONDITIONAL_ACTIVITY__ON_FALSE_NEXT_ACTIVITY:
				return onFalseNextActivity != null;
			case ActivityDiagramModelPackage.CONDITIONAL_ACTIVITY__ON_TRUE_FINAL_RESULT:
				return onTrueFinalResult != null;
			case ActivityDiagramModelPackage.CONDITIONAL_ACTIVITY__ON_FALSE_FINAL_RESULT:
				return onFalseFinalResult != null;
			case ActivityDiagramModelPackage.CONDITIONAL_ACTIVITY__OUTCOME:
				return outcome != null && !outcome.isEmpty();
			case ActivityDiagramModelPackage.CONDITIONAL_ACTIVITY__BOP:
				return bOp != null && !bOp.isEmpty();
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
		result.append(" (bOp: ");
		result.append(bOp);
		result.append(')');
		return result.toString();
	}

} //ConditionalActivityImpl
