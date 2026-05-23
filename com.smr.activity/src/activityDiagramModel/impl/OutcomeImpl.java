/**
 */
package activityDiagramModel.impl;

import activityDiagramModel.ActivityDiagramModelPackage;
import activityDiagramModel.Outcome;

import java.util.Collection;

import mncModel.AbstractOutcomeItems;
import mncModel.CheckParameterCondition;

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
 * An implementation of the model object '<em><b>Outcome</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link activityDiagramModel.impl.OutcomeImpl#getName <em>Name</em>}</li>
 *   <li>{@link activityDiagramModel.impl.OutcomeImpl#getCapabilityOutcome <em>Capability Outcome</em>}</li>
 *   <li>{@link activityDiagramModel.impl.OutcomeImpl#getOutcomeValidation <em>Outcome Validation</em>}</li>
 * </ul>
 *
 * @generated
 */
public class OutcomeImpl extends MinimalEObjectImpl.Container implements Outcome {
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
	 * The cached value of the '{@link #getCapabilityOutcome() <em>Capability Outcome</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCapabilityOutcome()
	 * @generated
	 * @ordered
	 */
	protected AbstractOutcomeItems capabilityOutcome;

	/**
	 * The cached value of the '{@link #getOutcomeValidation() <em>Outcome Validation</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOutcomeValidation()
	 * @generated
	 * @ordered
	 */
	protected EList<CheckParameterCondition> outcomeValidation;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected OutcomeImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ActivityDiagramModelPackage.Literals.OUTCOME;
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
			eNotify(new ENotificationImpl(this, Notification.SET, ActivityDiagramModelPackage.OUTCOME__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public AbstractOutcomeItems getCapabilityOutcome() {
		if (capabilityOutcome != null && capabilityOutcome.eIsProxy()) {
			InternalEObject oldCapabilityOutcome = (InternalEObject)capabilityOutcome;
			capabilityOutcome = (AbstractOutcomeItems)eResolveProxy(oldCapabilityOutcome);
			if (capabilityOutcome != oldCapabilityOutcome) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ActivityDiagramModelPackage.OUTCOME__CAPABILITY_OUTCOME, oldCapabilityOutcome, capabilityOutcome));
			}
		}
		return capabilityOutcome;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public AbstractOutcomeItems basicGetCapabilityOutcome() {
		return capabilityOutcome;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setCapabilityOutcome(AbstractOutcomeItems newCapabilityOutcome) {
		AbstractOutcomeItems oldCapabilityOutcome = capabilityOutcome;
		capabilityOutcome = newCapabilityOutcome;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ActivityDiagramModelPackage.OUTCOME__CAPABILITY_OUTCOME, oldCapabilityOutcome, capabilityOutcome));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<CheckParameterCondition> getOutcomeValidation() {
		if (outcomeValidation == null) {
			outcomeValidation = new EObjectContainmentEList<CheckParameterCondition>(CheckParameterCondition.class, this, ActivityDiagramModelPackage.OUTCOME__OUTCOME_VALIDATION);
		}
		return outcomeValidation;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case ActivityDiagramModelPackage.OUTCOME__OUTCOME_VALIDATION:
				return ((InternalEList<?>)getOutcomeValidation()).basicRemove(otherEnd, msgs);
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
			case ActivityDiagramModelPackage.OUTCOME__NAME:
				return getName();
			case ActivityDiagramModelPackage.OUTCOME__CAPABILITY_OUTCOME:
				if (resolve) return getCapabilityOutcome();
				return basicGetCapabilityOutcome();
			case ActivityDiagramModelPackage.OUTCOME__OUTCOME_VALIDATION:
				return getOutcomeValidation();
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
			case ActivityDiagramModelPackage.OUTCOME__NAME:
				setName((String)newValue);
				return;
			case ActivityDiagramModelPackage.OUTCOME__CAPABILITY_OUTCOME:
				setCapabilityOutcome((AbstractOutcomeItems)newValue);
				return;
			case ActivityDiagramModelPackage.OUTCOME__OUTCOME_VALIDATION:
				getOutcomeValidation().clear();
				getOutcomeValidation().addAll((Collection<? extends CheckParameterCondition>)newValue);
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
			case ActivityDiagramModelPackage.OUTCOME__NAME:
				setName(NAME_EDEFAULT);
				return;
			case ActivityDiagramModelPackage.OUTCOME__CAPABILITY_OUTCOME:
				setCapabilityOutcome((AbstractOutcomeItems)null);
				return;
			case ActivityDiagramModelPackage.OUTCOME__OUTCOME_VALIDATION:
				getOutcomeValidation().clear();
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
			case ActivityDiagramModelPackage.OUTCOME__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case ActivityDiagramModelPackage.OUTCOME__CAPABILITY_OUTCOME:
				return capabilityOutcome != null;
			case ActivityDiagramModelPackage.OUTCOME__OUTCOME_VALIDATION:
				return outcomeValidation != null && !outcomeValidation.isEmpty();
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
		result.append(')');
		return result.toString();
	}

} //OutcomeImpl
