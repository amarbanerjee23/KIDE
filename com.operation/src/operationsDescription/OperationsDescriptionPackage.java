/**
 */
package operationsDescription;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each operation of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see operationsDescription.OperationsDescriptionFactory
 * @model kind="package"
 * @generated
 */
public interface OperationsDescriptionPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "operationsDescription";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://operationsDescription/1.0";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "operationsDescription";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	OperationsDescriptionPackage eINSTANCE = operationsDescription.impl.OperationsDescriptionPackageImpl.init();

	/**
	 * The meta object id for the '{@link operationsDescription.impl.OperationDescriptionsImpl <em>Operation Descriptions</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see operationsDescription.impl.OperationDescriptionsImpl
	 * @see operationsDescription.impl.OperationsDescriptionPackageImpl#getOperationDescriptions()
	 * @generated
	 */
	int OPERATION_DESCRIPTIONS = 0;

	/**
	 * The feature id for the '<em><b>Operations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPERATION_DESCRIPTIONS__OPERATIONS = 0;

	/**
	 * The number of structural features of the '<em>Operation Descriptions</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPERATION_DESCRIPTIONS_FEATURE_COUNT = 1;

	/**
	 * The number of operations of the '<em>Operation Descriptions</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPERATION_DESCRIPTIONS_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link operationsDescription.impl.OperationImpl <em>Operation</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see operationsDescription.impl.OperationImpl
	 * @see operationsDescription.impl.OperationsDescriptionPackageImpl#getOperation()
	 * @generated
	 */
	int OPERATION = 1;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPERATION__NAME = 0;

	/**
	 * The feature id for the '<em><b>Input Parameters</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPERATION__INPUT_PARAMETERS = 1;

	/**
	 * The feature id for the '<em><b>Executable Script</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPERATION__EXECUTABLE_SCRIPT = 2;

	/**
	 * The feature id for the '<em><b>Output Parameters</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPERATION__OUTPUT_PARAMETERS = 3;

	/**
	 * The number of structural features of the '<em>Operation</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPERATION_FEATURE_COUNT = 4;

	/**
	 * The number of operations of the '<em>Operation</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPERATION_OPERATION_COUNT = 0;


	/**
	 * Returns the meta object for class '{@link operationsDescription.OperationDescriptions <em>Operation Descriptions</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Operation Descriptions</em>'.
	 * @see operationsDescription.OperationDescriptions
	 * @generated
	 */
	EClass getOperationDescriptions();

	/**
	 * Returns the meta object for the containment reference list '{@link operationsDescription.OperationDescriptions#getOperations <em>Operations</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Operations</em>'.
	 * @see operationsDescription.OperationDescriptions#getOperations()
	 * @see #getOperationDescriptions()
	 * @generated
	 */
	EReference getOperationDescriptions_Operations();

	/**
	 * Returns the meta object for class '{@link operationsDescription.Operation <em>Operation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Operation</em>'.
	 * @see operationsDescription.Operation
	 * @generated
	 */
	EClass getOperation();

	/**
	 * Returns the meta object for the attribute '{@link operationsDescription.Operation#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see operationsDescription.Operation#getName()
	 * @see #getOperation()
	 * @generated
	 */
	EAttribute getOperation_Name();

	/**
	 * Returns the meta object for the containment reference list '{@link operationsDescription.Operation#getInputParameters <em>Input Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Input Parameters</em>'.
	 * @see operationsDescription.Operation#getInputParameters()
	 * @see #getOperation()
	 * @generated
	 */
	EReference getOperation_InputParameters();

	/**
	 * Returns the meta object for the attribute '{@link operationsDescription.Operation#getExecutableScript <em>Executable Script</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Executable Script</em>'.
	 * @see operationsDescription.Operation#getExecutableScript()
	 * @see #getOperation()
	 * @generated
	 */
	EAttribute getOperation_ExecutableScript();

	/**
	 * Returns the meta object for the containment reference '{@link operationsDescription.Operation#getOutputParameters <em>Output Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Output Parameters</em>'.
	 * @see operationsDescription.Operation#getOutputParameters()
	 * @see #getOperation()
	 * @generated
	 */
	EReference getOperation_OutputParameters();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	OperationsDescriptionFactory getOperationsDescriptionFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each operation of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link operationsDescription.impl.OperationDescriptionsImpl <em>Operation Descriptions</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see operationsDescription.impl.OperationDescriptionsImpl
		 * @see operationsDescription.impl.OperationsDescriptionPackageImpl#getOperationDescriptions()
		 * @generated
		 */
		EClass OPERATION_DESCRIPTIONS = eINSTANCE.getOperationDescriptions();

		/**
		 * The meta object literal for the '<em><b>Operations</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference OPERATION_DESCRIPTIONS__OPERATIONS = eINSTANCE.getOperationDescriptions_Operations();

		/**
		 * The meta object literal for the '{@link operationsDescription.impl.OperationImpl <em>Operation</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see operationsDescription.impl.OperationImpl
		 * @see operationsDescription.impl.OperationsDescriptionPackageImpl#getOperation()
		 * @generated
		 */
		EClass OPERATION = eINSTANCE.getOperation();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute OPERATION__NAME = eINSTANCE.getOperation_Name();

		/**
		 * The meta object literal for the '<em><b>Input Parameters</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference OPERATION__INPUT_PARAMETERS = eINSTANCE.getOperation_InputParameters();

		/**
		 * The meta object literal for the '<em><b>Executable Script</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute OPERATION__EXECUTABLE_SCRIPT = eINSTANCE.getOperation_ExecutableScript();

		/**
		 * The meta object literal for the '<em><b>Output Parameters</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference OPERATION__OUTPUT_PARAMETERS = eINSTANCE.getOperation_OutputParameters();

	}

} //OperationsDescriptionPackage
