/**
 */
package operationsDescription;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Operation Descriptions</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link operationsDescription.OperationDescriptions#getOperations <em>Operations</em>}</li>
 * </ul>
 *
 * @see operationsDescription.OperationsDescriptionPackage#getOperationDescriptions()
 * @model
 * @generated
 */
public interface OperationDescriptions extends EObject {
	/**
	 * Returns the value of the '<em><b>Operations</b></em>' containment reference list.
	 * The list contents are of type {@link operationsDescription.Operation}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Operations</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Operations</em>' containment reference list.
	 * @see operationsDescription.OperationsDescriptionPackage#getOperationDescriptions_Operations()
	 * @model containment="true"
	 * @generated
	 */
	EList<Operation> getOperations();

} // OperationDescriptions
