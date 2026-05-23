/**
 */
package mncModel;

import dataModelPackage.PrimitiveValue;
import dataModelPackage.PrimitiveValueType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Data Point</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link mncModel.DataPoint#getType <em>Type</em>}</li>
 *   <li>{@link mncModel.DataPoint#getValue <em>Value</em>}</li>
 *   <li>{@link mncModel.DataPoint#isPublish <em>Publish</em>}</li>
 * </ul>
 *
 * @see mncModel.MncModelPackage#getDataPoint()
 * @model
 * @generated
 */
public interface DataPoint extends AbstractInterfaceItems, AbstractOutcomeItems {
	/**
	 * Returns the value of the '<em><b>Type</b></em>' attribute.
	 * The literals are from the enumeration {@link dataModelPackage.PrimitiveValueType}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Type</em>' attribute.
	 * @see dataModelPackage.PrimitiveValueType
	 * @see #setType(PrimitiveValueType)
	 * @see mncModel.MncModelPackage#getDataPoint_Type()
	 * @model
	 * @generated
	 */
	PrimitiveValueType getType();

	/**
	 * Sets the value of the '{@link mncModel.DataPoint#getType <em>Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Type</em>' attribute.
	 * @see dataModelPackage.PrimitiveValueType
	 * @see #getType()
	 * @generated
	 */
	void setType(PrimitiveValueType value);

	/**
	 * Returns the value of the '<em><b>Value</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Value</em>' containment reference.
	 * @see #setValue(PrimitiveValue)
	 * @see mncModel.MncModelPackage#getDataPoint_Value()
	 * @model containment="true"
	 * @generated
	 */
	PrimitiveValue getValue();

	/**
	 * Sets the value of the '{@link mncModel.DataPoint#getValue <em>Value</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Value</em>' containment reference.
	 * @see #getValue()
	 * @generated
	 */
	void setValue(PrimitiveValue value);

	/**
	 * Returns the value of the '<em><b>Publish</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Publish</em>' attribute.
	 * @see #setPublish(boolean)
	 * @see mncModel.MncModelPackage#getDataPoint_Publish()
	 * @model
	 * @generated
	 */
	boolean isPublish();

	/**
	 * Sets the value of the '{@link mncModel.DataPoint#isPublish <em>Publish</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Publish</em>' attribute.
	 * @see #isPublish()
	 * @generated
	 */
	void setPublish(boolean value);

} // DataPoint
