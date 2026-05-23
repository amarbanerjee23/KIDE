/**
 */
package dataModelPackage;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Abstract Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link dataModelPackage.AbstractType#getName <em>Name</em>}</li>
 *   <li>{@link dataModelPackage.AbstractType#getType <em>Type</em>}</li>
 *   <li>{@link dataModelPackage.AbstractType#getValue <em>Value</em>}</li>
 * </ul>
 *
 * @see dataModelPackage.DataModelPackage#getAbstractType()
 * @model
 * @generated
 */
public interface AbstractType extends Parameter {
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
	 * @see dataModelPackage.DataModelPackage#getAbstractType_Name()
	 * @model
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link dataModelPackage.AbstractType#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Type</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Type</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Type</em>' reference.
	 * @see #setType(DataModel)
	 * @see dataModelPackage.DataModelPackage#getAbstractType_Type()
	 * @model
	 * @generated
	 */
	DataModel getType();

	/**
	 * Sets the value of the '{@link dataModelPackage.AbstractType#getType <em>Type</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Type</em>' reference.
	 * @see #getType()
	 * @generated
	 */
	void setType(DataModel value);

	/**
	 * Returns the value of the '<em><b>Value</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Value</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Value</em>' containment reference.
	 * @see #setValue(AbstractObjectValue)
	 * @see dataModelPackage.DataModelPackage#getAbstractType_Value()
	 * @model containment="true"
	 * @generated
	 */
	AbstractObjectValue getValue();

	/**
	 * Sets the value of the '{@link dataModelPackage.AbstractType#getValue <em>Value</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Value</em>' containment reference.
	 * @see #getValue()
	 * @generated
	 */
	void setValue(AbstractObjectValue value);

} // AbstractType
