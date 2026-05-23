/**
 */
package mncModel.impl;

import java.util.Collection;

import mncModel.MncModelPackage;
import mncModel.Response;
import mncModel.ResponseAggregationRule;
import mncModel.ResponseBlock;

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
 * An implementation of the model object '<em><b>Response Block</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link mncModel.impl.ResponseBlockImpl#getResponse <em>Response</em>}</li>
 *   <li>{@link mncModel.impl.ResponseBlockImpl#getResponseAggregationRules <em>Response Aggregation Rules</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ResponseBlockImpl extends BehaviorBlockImpl implements ResponseBlock {
	/**
	 * The cached value of the '{@link #getResponse() <em>Response</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getResponse()
	 * @generated
	 * @ordered
	 */
	protected Response response;

	/**
	 * The cached value of the '{@link #getResponseAggregationRules() <em>Response Aggregation Rules</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getResponseAggregationRules()
	 * @generated
	 * @ordered
	 */
	protected EList<ResponseAggregationRule> responseAggregationRules;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ResponseBlockImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MncModelPackage.Literals.RESPONSE_BLOCK;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Response getResponse() {
		if (response != null && response.eIsProxy()) {
			InternalEObject oldResponse = (InternalEObject)response;
			response = (Response)eResolveProxy(oldResponse);
			if (response != oldResponse) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, MncModelPackage.RESPONSE_BLOCK__RESPONSE, oldResponse, response));
			}
		}
		return response;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Response basicGetResponse() {
		return response;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setResponse(Response newResponse) {
		Response oldResponse = response;
		response = newResponse;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MncModelPackage.RESPONSE_BLOCK__RESPONSE, oldResponse, response));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<ResponseAggregationRule> getResponseAggregationRules() {
		if (responseAggregationRules == null) {
			responseAggregationRules = new EObjectContainmentEList<ResponseAggregationRule>(ResponseAggregationRule.class, this, MncModelPackage.RESPONSE_BLOCK__RESPONSE_AGGREGATION_RULES);
		}
		return responseAggregationRules;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case MncModelPackage.RESPONSE_BLOCK__RESPONSE_AGGREGATION_RULES:
				return ((InternalEList<?>)getResponseAggregationRules()).basicRemove(otherEnd, msgs);
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
			case MncModelPackage.RESPONSE_BLOCK__RESPONSE:
				if (resolve) return getResponse();
				return basicGetResponse();
			case MncModelPackage.RESPONSE_BLOCK__RESPONSE_AGGREGATION_RULES:
				return getResponseAggregationRules();
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
			case MncModelPackage.RESPONSE_BLOCK__RESPONSE:
				setResponse((Response)newValue);
				return;
			case MncModelPackage.RESPONSE_BLOCK__RESPONSE_AGGREGATION_RULES:
				getResponseAggregationRules().clear();
				getResponseAggregationRules().addAll((Collection<? extends ResponseAggregationRule>)newValue);
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
			case MncModelPackage.RESPONSE_BLOCK__RESPONSE:
				setResponse((Response)null);
				return;
			case MncModelPackage.RESPONSE_BLOCK__RESPONSE_AGGREGATION_RULES:
				getResponseAggregationRules().clear();
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
			case MncModelPackage.RESPONSE_BLOCK__RESPONSE:
				return response != null;
			case MncModelPackage.RESPONSE_BLOCK__RESPONSE_AGGREGATION_RULES:
				return responseAggregationRules != null && !responseAggregationRules.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //ResponseBlockImpl
