/**
 */
package dataModelPackage;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Data Package</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link dataModelPackage.DataPackage#getName <em>Name</em>}</li>
 *   <li>{@link dataModelPackage.DataPackage#getDataModelCollections <em>Data Model Collections</em>}</li>
 * </ul>
 *
 * @see dataModelPackage.DataModelPackage#getDataPackage()
 * @model
 * @generated
 */
public interface DataPackage extends EObject {
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
	 * @see dataModelPackage.DataModelPackage#getDataPackage_Name()
	 * @model
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link dataModelPackage.DataPackage#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Data Model Collections</b></em>' containment reference list.
	 * The list contents are of type {@link dataModelPackage.DataModel}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Data Model Collections</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Data Model Collections</em>' containment reference list.
	 * @see dataModelPackage.DataModelPackage#getDataPackage_DataModelCollections()
	 * @model containment="true"
	 * @generated
	 */
	EList<DataModel> getDataModelCollections();

} // DataPackage
