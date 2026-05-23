/**
 */
package mncModel;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Model</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link mncModel.Model#getSystems <em>Systems</em>}</li>
 *   <li>{@link mncModel.Model#getName <em>Name</em>}</li>
 *   <li>{@link mncModel.Model#getImportSection <em>Import Section</em>}</li>
 * </ul>
 *
 * @see mncModel.MncModelPackage#getModel()
 * @model
 * @generated
 */
public interface Model extends EObject {
	/**
	 * Returns the value of the '<em><b>Systems</b></em>' containment reference list.
	 * The list contents are of type {@link mncModel.System}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Systems</em>' containment reference list.
	 * @see mncModel.MncModelPackage#getModel_Systems()
	 * @model containment="true"
	 * @generated
	 */
	EList<mncModel.System> getSystems();

	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see mncModel.MncModelPackage#getModel_Name()
	 * @model
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link mncModel.Model#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Import Section</b></em>' containment reference list.
	 * The list contents are of type {@link mncModel.Import}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Import Section</em>' containment reference list.
	 * @see mncModel.MncModelPackage#getModel_ImportSection()
	 * @model containment="true"
	 * @generated
	 */
	EList<Import> getImportSection();

} // Model
