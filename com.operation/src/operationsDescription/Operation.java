/**
 */
package operationsDescription;

import dataModelPackage.Parameter;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Operation</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link operationsDescription.Operation#getName <em>Name</em>}</li>
 *   <li>{@link operationsDescription.Operation#getInputParameters <em>Input Parameters</em>}</li>
 *   <li>{@link operationsDescription.Operation#getExecutableScript <em>Executable Script</em>}</li>
 *   <li>{@link operationsDescription.Operation#getOutputParameters <em>Output Parameters</em>}</li>
 * </ul>
 *
 * @see operationsDescription.OperationsDescriptionPackage#getOperation()
 * @model
 * @generated
 */
public interface Operation extends EObject {
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
	 * @see operationsDescription.OperationsDescriptionPackage#getOperation_Name()
	 * @model
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link operationsDescription.Operation#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Input Parameters</b></em>' containment reference list.
	 * The list contents are of type {@link dataModelPackage.Parameter}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Input Parameters</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Input Parameters</em>' containment reference list.
	 * @see operationsDescription.OperationsDescriptionPackage#getOperation_InputParameters()
	 * @model containment="true"
	 * @generated
	 */
	EList<Parameter> getInputParameters();

	/**
	 * Returns the value of the '<em><b>Executable Script</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Executable Script</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Executable Script</em>' attribute.
	 * @see #setExecutableScript(String)
	 * @see operationsDescription.OperationsDescriptionPackage#getOperation_ExecutableScript()
	 * @model
	 * @generated
	 */
	String getExecutableScript();

	/**
	 * Sets the value of the '{@link operationsDescription.Operation#getExecutableScript <em>Executable Script</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Executable Script</em>' attribute.
	 * @see #getExecutableScript()
	 * @generated
	 */
	void setExecutableScript(String value);

	/**
	 * Returns the value of the '<em><b>Output Parameters</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Output Parameters</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Output Parameters</em>' containment reference.
	 * @see #setOutputParameters(Parameter)
	 * @see operationsDescription.OperationsDescriptionPackage#getOperation_OutputParameters()
	 * @model containment="true"
	 * @generated
	 */
	Parameter getOutputParameters();

	/**
	 * Sets the value of the '{@link operationsDescription.Operation#getOutputParameters <em>Output Parameters</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Output Parameters</em>' containment reference.
	 * @see #getOutputParameters()
	 * @generated
	 */
	void setOutputParameters(Parameter value);

} // Operation
