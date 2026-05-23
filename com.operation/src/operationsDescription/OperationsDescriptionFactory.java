/**
 */
package operationsDescription;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see operationsDescription.OperationsDescriptionPackage
 * @generated
 */
public interface OperationsDescriptionFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	OperationsDescriptionFactory eINSTANCE = operationsDescription.impl.OperationsDescriptionFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Operation Descriptions</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Operation Descriptions</em>'.
	 * @generated
	 */
	OperationDescriptions createOperationDescriptions();

	/**
	 * Returns a new object of class '<em>Operation</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Operation</em>'.
	 * @generated
	 */
	Operation createOperation();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	OperationsDescriptionPackage getOperationsDescriptionPackage();

} //OperationsDescriptionFactory
