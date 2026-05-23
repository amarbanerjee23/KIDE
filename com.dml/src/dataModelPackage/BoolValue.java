/**
 */
package dataModelPackage;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Bool Value</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link dataModelPackage.BoolValue#isBoolValue <em>Bool Value</em>}</li>
 * </ul>
 *
 * @see dataModelPackage.DataModelPackage#getBoolValue()
 * @model
 * @generated
 */
public interface BoolValue extends PrimitiveValue {
	/**
	 * Returns the value of the '<em><b>Bool Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Bool Value</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Bool Value</em>' attribute.
	 * @see #setBoolValue(boolean)
	 * @see dataModelPackage.DataModelPackage#getBoolValue_BoolValue()
	 * @model
	 * @generated
	 */
	boolean isBoolValue();

	/**
	 * Sets the value of the '{@link dataModelPackage.BoolValue#isBoolValue <em>Bool Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Bool Value</em>' attribute.
	 * @see #isBoolValue()
	 * @generated
	 */
	void setBoolValue(boolean value);

} // BoolValue
