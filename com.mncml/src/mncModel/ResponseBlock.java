/**
 */
package mncModel;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Response Block</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link mncModel.ResponseBlock#getResponse <em>Response</em>}</li>
 *   <li>{@link mncModel.ResponseBlock#getResponseAggregationRules <em>Response Aggregation Rules</em>}</li>
 * </ul>
 *
 * @see mncModel.MncModelPackage#getResponseBlock()
 * @model
 * @generated
 */
public interface ResponseBlock extends BehaviorBlock {
	/**
	 * Returns the value of the '<em><b>Response</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Response</em>' reference.
	 * @see #setResponse(Response)
	 * @see mncModel.MncModelPackage#getResponseBlock_Response()
	 * @model
	 * @generated
	 */
	Response getResponse();

	/**
	 * Sets the value of the '{@link mncModel.ResponseBlock#getResponse <em>Response</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Response</em>' reference.
	 * @see #getResponse()
	 * @generated
	 */
	void setResponse(Response value);

	/**
	 * Returns the value of the '<em><b>Response Aggregation Rules</b></em>' containment reference list.
	 * The list contents are of type {@link mncModel.ResponseAggregationRule}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Response Aggregation Rules</em>' containment reference list.
	 * @see mncModel.MncModelPackage#getResponseBlock_ResponseAggregationRules()
	 * @model containment="true"
	 * @generated
	 */
	EList<ResponseAggregationRule> getResponseAggregationRules();

} // ResponseBlock
