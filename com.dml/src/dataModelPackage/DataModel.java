/**
 */
package dataModelPackage;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Data Model</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link dataModelPackage.DataModel#getPrimitives <em>Primitives</em>}</li>
 *   <li>{@link dataModelPackage.DataModel#getComposites <em>Composites</em>}</li>
 *   <li>{@link dataModelPackage.DataModel#getName <em>Name</em>}</li>
 * </ul>
 *
 * @see dataModelPackage.DataModelPackage#getDataModel()
 * @model
 * @generated
 */
public interface DataModel extends Parameter {
	/**
	 * Returns the value of the '<em><b>Primitives</b></em>' containment reference list.
	 * The list contents are of type {@link dataModelPackage.Parameter}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Primitives</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Primitives</em>' containment reference list.
	 * @see dataModelPackage.DataModelPackage#getDataModel_Primitives()
	 * @model containment="true"
	 * @generated
	 */
	EList<Parameter> getPrimitives();

	/**
	 * Returns the value of the '<em><b>Composites</b></em>' reference list.
	 * The list contents are of type {@link dataModelPackage.DataModel}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Composites</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Composites</em>' reference list.
	 * @see dataModelPackage.DataModelPackage#getDataModel_Composites()
	 * @model
	 * @generated
	 */
	EList<DataModel> getComposites();

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
	 * @see dataModelPackage.DataModelPackage#getDataModel_Name()
	 * @model
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link dataModelPackage.DataModel#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

} // DataModel
