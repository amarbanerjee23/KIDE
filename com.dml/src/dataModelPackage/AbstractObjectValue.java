/**
 */
package dataModelPackage;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Abstract Object Value</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link dataModelPackage.AbstractObjectValue#getAbstractValue <em>Abstract Value</em>}</li>
 * </ul>
 *
 * @see dataModelPackage.DataModelPackage#getAbstractObjectValue()
 * @model
 * @generated
 */
public interface AbstractObjectValue extends PrimitiveValue {
	/**
	 * Returns the value of the '<em><b>Abstract Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Abstract Value</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Abstract Value</em>' attribute.
	 * @see #setAbstractValue(String)
	 * @see dataModelPackage.DataModelPackage#getAbstractObjectValue_AbstractValue()
	 * @model
	 * @generated
	 */
	String getAbstractValue();

	/**
	 * Sets the value of the '{@link dataModelPackage.AbstractObjectValue#getAbstractValue <em>Abstract Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Abstract Value</em>' attribute.
	 * @see #getAbstractValue()
	 * @generated
	 */
	void setAbstractValue(String value);

} // AbstractObjectValue
