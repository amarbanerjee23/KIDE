/**
 */
package mncModel;

import dataModelPackage.PrimitiveValue;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Action Paremeter</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link mncModel.ActionParemeter#getParameterMappings <em>Parameter Mappings</em>}</li>
 *   <li>{@link mncModel.ActionParemeter#getParameterValues <em>Parameter Values</em>}</li>
 * </ul>
 *
 * @see mncModel.MncModelPackage#getActionParemeter()
 * @model
 * @generated
 */
public interface ActionParemeter extends EObject {
	/**
	 * Returns the value of the '<em><b>Parameter Mappings</b></em>' containment reference list.
	 * The list contents are of type {@link mncModel.ParameterTranslation}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Parameter Mappings</em>' containment reference list.
	 * @see mncModel.MncModelPackage#getActionParemeter_ParameterMappings()
	 * @model containment="true"
	 * @generated
	 */
	EList<ParameterTranslation> getParameterMappings();

	/**
	 * Returns the value of the '<em><b>Parameter Values</b></em>' containment reference list.
	 * The list contents are of type {@link dataModelPackage.PrimitiveValue}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Parameter Values</em>' containment reference list.
	 * @see mncModel.MncModelPackage#getActionParemeter_ParameterValues()
	 * @model containment="true"
	 * @generated
	 */
	EList<PrimitiveValue> getParameterValues();

} // ActionParemeter
