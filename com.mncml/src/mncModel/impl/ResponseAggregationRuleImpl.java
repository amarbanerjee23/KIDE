/**
 */
package mncModel.impl;

import java.util.Collection;

import mncModel.MncModelPackage;
import mncModel.ParameterTranslation;
import mncModel.Response;
import mncModel.ResponseAggregationRule;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Response Aggregation Rule</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link mncModel.impl.ResponseAggregationRuleImpl#getInputResponses <em>Input Responses</em>}</li>
 *   <li>{@link mncModel.impl.ResponseAggregationRuleImpl#getParameterTranslations <em>Parameter Translations</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ResponseAggregationRuleImpl extends MinimalEObjectImpl.Container implements ResponseAggregationRule {
	/**
	 * The cached value of the '{@link #getInputResponses() <em>Input Responses</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getInputResponses()
	 * @generated
	 * @ordered
	 */
	protected EList<Response> inputResponses;

	/**
	 * The cached value of the '{@link #getParameterTranslations() <em>Parameter Translations</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getParameterTranslations()
	 * @generated
	 * @ordered
	 */
	protected EList<ParameterTranslation> parameterTranslations;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ResponseAggregationRuleImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MncModelPackage.Literals.RESPONSE_AGGREGATION_RULE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<Response> getInputResponses() {
		if (inputResponses == null) {
			inputResponses = new EObjectResolvingEList<Response>(Response.class, this, MncModelPackage.RESPONSE_AGGREGATION_RULE__INPUT_RESPONSES);
		}
		return inputResponses;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<ParameterTranslation> getParameterTranslations() {
		if (parameterTranslations == null) {
			parameterTranslations = new EObjectContainmentEList<ParameterTranslation>(ParameterTranslation.class, this, MncModelPackage.RESPONSE_AGGREGATION_RULE__PARAMETER_TRANSLATIONS);
		}
		return parameterTranslations;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case MncModelPackage.RESPONSE_AGGREGATION_RULE__PARAMETER_TRANSLATIONS:
				return ((InternalEList<?>)getParameterTranslations()).basicRemove(otherEnd, msgs);
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
			case MncModelPackage.RESPONSE_AGGREGATION_RULE__INPUT_RESPONSES:
				return getInputResponses();
			case MncModelPackage.RESPONSE_AGGREGATION_RULE__PARAMETER_TRANSLATIONS:
				return getParameterTranslations();
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
			case MncModelPackage.RESPONSE_AGGREGATION_RULE__INPUT_RESPONSES:
				getInputResponses().clear();
				getInputResponses().addAll((Collection<? extends Response>)newValue);
				return;
			case MncModelPackage.RESPONSE_AGGREGATION_RULE__PARAMETER_TRANSLATIONS:
				getParameterTranslations().clear();
				getParameterTranslations().addAll((Collection<? extends ParameterTranslation>)newValue);
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
			case MncModelPackage.RESPONSE_AGGREGATION_RULE__INPUT_RESPONSES:
				getInputResponses().clear();
				return;
			case MncModelPackage.RESPONSE_AGGREGATION_RULE__PARAMETER_TRANSLATIONS:
				getParameterTranslations().clear();
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
			case MncModelPackage.RESPONSE_AGGREGATION_RULE__INPUT_RESPONSES:
				return inputResponses != null && !inputResponses.isEmpty();
			case MncModelPackage.RESPONSE_AGGREGATION_RULE__PARAMETER_TRANSLATIONS:
				return parameterTranslations != null && !parameterTranslations.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //ResponseAggregationRuleImpl
