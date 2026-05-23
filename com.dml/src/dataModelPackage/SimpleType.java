/**
 */
package dataModelPackage;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Simple Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link dataModelPackage.SimpleType#getName <em>Name</em>}</li>
 *   <li>{@link dataModelPackage.SimpleType#getType <em>Type</em>}</li>
 *   <li>{@link dataModelPackage.SimpleType#getValue <em>Value</em>}</li>
 * </ul>
 *
 * @see dataModelPackage.DataModelPackage#getSimpleType()
 * @model
 * @generated
 */
public interface SimpleType extends Parameter {
	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see dataModelPackage.DataModelPackage#getSimpleType_Name()
	 * @model
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link dataModelPackage.SimpleType#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Type</b></em>' attribute.
	 * The literals are from the enumeration {@link dataModelPackage.PrimitiveValueType}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Type</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Type</em>' attribute.
	 * @see dataModelPackage.PrimitiveValueType
	 * @see #setType(PrimitiveValueType)
	 * @see dataModelPackage.DataModelPackage#getSimpleType_Type()
	 * @model
	 * @generated
	 */
	PrimitiveValueType getType();

	/**
	 * Sets the value of the '{@link dataModelPackage.SimpleType#getType <em>Type</em>}' attribute.
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
	 * <p>
	 * If the meaning of the '<em>Value</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Value</em>' containment reference.
	 * @see #setValue(PrimitiveValue)
	 * @see dataModelPackage.DataModelPackage#getSimpleType_Value()
	 * @model containment="true"
	 * @generated
	 */
	PrimitiveValue getValue();

	/**
	 * Sets the value of the '{@link dataModelPackage.SimpleType#getValue <em>Value</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Value</em>' containment reference.
	 * @see #getValue()
	 * @generated
	 */
	void setValue(PrimitiveValue value);

} // SimpleType
