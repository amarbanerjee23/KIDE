/**
 */
package mncModel;

import dataModelPackage.Parameter;
import dataModelPackage.PrimitiveValue;

import operationsDescription.Operation;
import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Check Parameter Condition</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link mncModel.CheckParameterCondition#getParameter <em>Parameter</em>}</li>
 *   <li>{@link mncModel.CheckParameterCondition#getCheckValues <em>Check Values</em>}</li>
 *   <li>{@link mncModel.CheckParameterCondition#getCheckMaxValue <em>Check Max Value</em>}</li>
 *   <li>{@link mncModel.CheckParameterCondition#getCheckMinValue <em>Check Min Value</em>}</li>
 *   <li>{@link mncModel.CheckParameterCondition#getOperations <em>Operations</em>}</li>
 * </ul>
 *
 * @see mncModel.MncModelPackage#getCheckParameterCondition()
 * @model
 * @generated
 */
public interface CheckParameterCondition extends EObject {
	/**
	 * Returns the value of the '<em><b>Parameter</b></em>' reference list.
	 * The list contents are of type {@link dataModelPackage.Parameter}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Parameter</em>' reference list.
	 * @see mncModel.MncModelPackage#getCheckParameterCondition_Parameter()
	 * @model
	 * @generated
	 */
	EList<Parameter> getParameter();

	/**
	 * Returns the value of the '<em><b>Check Values</b></em>' containment reference list.
	 * The list contents are of type {@link dataModelPackage.PrimitiveValue}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Check Values</em>' containment reference list.
	 * @see mncModel.MncModelPackage#getCheckParameterCondition_CheckValues()
	 * @model containment="true"
	 * @generated
	 */
	EList<PrimitiveValue> getCheckValues();

	/**
	 * Returns the value of the '<em><b>Check Max Value</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Check Max Value</em>' containment reference.
	 * @see #setCheckMaxValue(PrimitiveValue)
	 * @see mncModel.MncModelPackage#getCheckParameterCondition_CheckMaxValue()
	 * @model containment="true"
	 * @generated
	 */
	PrimitiveValue getCheckMaxValue();

	/**
	 * Sets the value of the '{@link mncModel.CheckParameterCondition#getCheckMaxValue <em>Check Max Value</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Check Max Value</em>' containment reference.
	 * @see #getCheckMaxValue()
	 * @generated
	 */
	void setCheckMaxValue(PrimitiveValue value);

	/**
	 * Returns the value of the '<em><b>Check Min Value</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Check Min Value</em>' containment reference.
	 * @see #setCheckMinValue(PrimitiveValue)
	 * @see mncModel.MncModelPackage#getCheckParameterCondition_CheckMinValue()
	 * @model containment="true"
	 * @generated
	 */
	PrimitiveValue getCheckMinValue();

	/**
	 * Sets the value of the '{@link mncModel.CheckParameterCondition#getCheckMinValue <em>Check Min Value</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Check Min Value</em>' containment reference.
	 * @see #getCheckMinValue()
	 * @generated
	 */
	void setCheckMinValue(PrimitiveValue value);

	/**
	 * Returns the value of the '<em><b>Operations</b></em>' reference list.
	 * The list contents are of type {@link operationsDescription.Operation}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Operations</em>' reference list.
	 * @see mncModel.MncModelPackage#getCheckParameterCondition_Operations()
	 * @model
	 * @generated
	 */
	EList<Operation> getOperations();

} // CheckParameterCondition
