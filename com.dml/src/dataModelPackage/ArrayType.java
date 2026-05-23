/**
 */
package dataModelPackage;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Array Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link dataModelPackage.ArrayType#getName <em>Name</em>}</li>
 *   <li>{@link dataModelPackage.ArrayType#getValues <em>Values</em>}</li>
 *   <li>{@link dataModelPackage.ArrayType#getPrimitiveType <em>Primitive Type</em>}</li>
 *   <li>{@link dataModelPackage.ArrayType#getDataModelType <em>Data Model Type</em>}</li>
 * </ul>
 *
 * @see dataModelPackage.DataModelPackage#getArrayType()
 * @model
 * @generated
 */
public interface ArrayType extends Parameter {
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
	 * @see dataModelPackage.DataModelPackage#getArrayType_Name()
	 * @model
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link dataModelPackage.ArrayType#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Values</b></em>' containment reference list.
	 * The list contents are of type {@link dataModelPackage.PrimitiveValue}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Values</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Values</em>' containment reference list.
	 * @see dataModelPackage.DataModelPackage#getArrayType_Values()
	 * @model containment="true"
	 * @generated
	 */
	EList<PrimitiveValue> getValues();

	/**
	 * Returns the value of the '<em><b>Primitive Type</b></em>' attribute.
	 * The literals are from the enumeration {@link dataModelPackage.PrimitiveValueType}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Primitive Type</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Primitive Type</em>' attribute.
	 * @see dataModelPackage.PrimitiveValueType
	 * @see #setPrimitiveType(PrimitiveValueType)
	 * @see dataModelPackage.DataModelPackage#getArrayType_PrimitiveType()
	 * @model
	 * @generated
	 */
	PrimitiveValueType getPrimitiveType();

	/**
	 * Sets the value of the '{@link dataModelPackage.ArrayType#getPrimitiveType <em>Primitive Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Primitive Type</em>' attribute.
	 * @see dataModelPackage.PrimitiveValueType
	 * @see #getPrimitiveType()
	 * @generated
	 */
	void setPrimitiveType(PrimitiveValueType value);

	/**
	 * Returns the value of the '<em><b>Data Model Type</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Data Model Type</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Data Model Type</em>' reference.
	 * @see #setDataModelType(DataModel)
	 * @see dataModelPackage.DataModelPackage#getArrayType_DataModelType()
	 * @model
	 * @generated
	 */
	DataModel getDataModelType();

	/**
	 * Sets the value of the '{@link dataModelPackage.ArrayType#getDataModelType <em>Data Model Type</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Data Model Type</em>' reference.
	 * @see #getDataModelType()
	 * @generated
	 */
	void setDataModelType(DataModel value);

} // ArrayType
