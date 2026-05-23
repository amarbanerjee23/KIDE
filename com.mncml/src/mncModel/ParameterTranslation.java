/**
 */
package mncModel;

import dataModelPackage.Parameter;

import operationsDescription.Operation;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Parameter Translation</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link mncModel.ParameterTranslation#getInputParameters <em>Input Parameters</em>}</li>
 *   <li>{@link mncModel.ParameterTranslation#getTransformationFunction <em>Transformation Function</em>}</li>
 *   <li>{@link mncModel.ParameterTranslation#getTranslatedParameters <em>Translated Parameters</em>}</li>
 * </ul>
 *
 * @see mncModel.MncModelPackage#getParameterTranslation()
 * @model
 * @generated
 */
public interface ParameterTranslation extends EObject {
	/**
	 * Returns the value of the '<em><b>Input Parameters</b></em>' reference list.
	 * The list contents are of type {@link dataModelPackage.Parameter}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Input Parameters</em>' reference list.
	 * @see mncModel.MncModelPackage#getParameterTranslation_InputParameters()
	 * @model
	 * @generated
	 */
	EList<Parameter> getInputParameters();

	/**
	 * Returns the value of the '<em><b>Transformation Function</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Transformation Function</em>' reference.
	 * @see #setTransformationFunction(Operation)
	 * @see mncModel.MncModelPackage#getParameterTranslation_TransformationFunction()
	 * @model
	 * @generated
	 */
	Operation getTransformationFunction();

	/**
	 * Sets the value of the '{@link mncModel.ParameterTranslation#getTransformationFunction <em>Transformation Function</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Transformation Function</em>' reference.
	 * @see #getTransformationFunction()
	 * @generated
	 */
	void setTransformationFunction(Operation value);

	/**
	 * Returns the value of the '<em><b>Translated Parameters</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Translated Parameters</em>' reference.
	 * @see #setTranslatedParameters(Parameter)
	 * @see mncModel.MncModelPackage#getParameterTranslation_TranslatedParameters()
	 * @model
	 * @generated
	 */
	Parameter getTranslatedParameters();

	/**
	 * Sets the value of the '{@link mncModel.ParameterTranslation#getTranslatedParameters <em>Translated Parameters</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Translated Parameters</em>' reference.
	 * @see #getTranslatedParameters()
	 * @generated
	 */
	void setTranslatedParameters(Parameter value);

} // ParameterTranslation
