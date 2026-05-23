/**
 */
package mncModel;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Response Aggregation Rule</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link mncModel.ResponseAggregationRule#getInputResponses <em>Input Responses</em>}</li>
 *   <li>{@link mncModel.ResponseAggregationRule#getParameterTranslations <em>Parameter Translations</em>}</li>
 * </ul>
 *
 * @see mncModel.MncModelPackage#getResponseAggregationRule()
 * @model
 * @generated
 */
public interface ResponseAggregationRule extends EObject {
	/**
	 * Returns the value of the '<em><b>Input Responses</b></em>' reference list.
	 * The list contents are of type {@link mncModel.Response}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Input Responses</em>' reference list.
	 * @see mncModel.MncModelPackage#getResponseAggregationRule_InputResponses()
	 * @model
	 * @generated
	 */
	EList<Response> getInputResponses();

	/**
	 * Returns the value of the '<em><b>Parameter Translations</b></em>' containment reference list.
	 * The list contents are of type {@link mncModel.ParameterTranslation}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Parameter Translations</em>' containment reference list.
	 * @see mncModel.MncModelPackage#getResponseAggregationRule_ParameterTranslations()
	 * @model containment="true"
	 * @generated
	 */
	EList<ParameterTranslation> getParameterTranslations();

} // ResponseAggregationRule
