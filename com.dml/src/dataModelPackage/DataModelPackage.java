/**
 */
package dataModelPackage;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
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
 * @see dataModelPackage.DataModelFactory
 * @model kind="package"
 * @generated
 */
public interface DataModelPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "dataModelPackage";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://dataModelPackage/1.0";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "DataModelPackage";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	DataModelPackage eINSTANCE = dataModelPackage.impl.DataModelPackageImpl.init();

	/**
	 * The meta object id for the '{@link dataModelPackage.impl.DataPackageImpl <em>Data Package</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see dataModelPackage.impl.DataPackageImpl
	 * @see dataModelPackage.impl.DataModelPackageImpl#getDataPackage()
	 * @generated
	 */
	int DATA_PACKAGE = 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_PACKAGE__NAME = 0;

	/**
	 * The feature id for the '<em><b>Data Model Collections</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_PACKAGE__DATA_MODEL_COLLECTIONS = 1;

	/**
	 * The number of structural features of the '<em>Data Package</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_PACKAGE_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Data Package</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_PACKAGE_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link dataModelPackage.impl.DataValueImpl <em>Data Value</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see dataModelPackage.impl.DataValueImpl
	 * @see dataModelPackage.impl.DataModelPackageImpl#getDataValue()
	 * @generated
	 */
	int DATA_VALUE = 14;

	/**
	 * The meta object id for the '{@link dataModelPackage.impl.DataModelImpl <em>Data Model</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see dataModelPackage.impl.DataModelImpl
	 * @see dataModelPackage.impl.DataModelPackageImpl#getDataModel()
	 * @generated
	 */
	int DATA_MODEL = 1;

	/**
	 * The meta object id for the '{@link dataModelPackage.impl.ParameterImpl <em>Parameter</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see dataModelPackage.impl.ParameterImpl
	 * @see dataModelPackage.impl.DataModelPackageImpl#getParameter()
	 * @generated
	 */
	int PARAMETER = 2;

	/**
	 * The number of structural features of the '<em>Parameter</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER_FEATURE_COUNT = 0;

	/**
	 * The number of operations of the '<em>Parameter</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER_OPERATION_COUNT = 0;

	/**
	 * The feature id for the '<em><b>Primitives</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_MODEL__PRIMITIVES = PARAMETER_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Composites</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_MODEL__COMPOSITES = PARAMETER_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_MODEL__NAME = PARAMETER_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>Data Model</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_MODEL_FEATURE_COUNT = PARAMETER_FEATURE_COUNT + 3;

	/**
	 * The number of operations of the '<em>Data Model</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_MODEL_OPERATION_COUNT = PARAMETER_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link dataModelPackage.impl.AbstractTypeImpl <em>Abstract Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see dataModelPackage.impl.AbstractTypeImpl
	 * @see dataModelPackage.impl.DataModelPackageImpl#getAbstractType()
	 * @generated
	 */
	int ABSTRACT_TYPE = 3;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_TYPE__NAME = PARAMETER_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Type</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_TYPE__TYPE = PARAMETER_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Value</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_TYPE__VALUE = PARAMETER_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>Abstract Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_TYPE_FEATURE_COUNT = PARAMETER_FEATURE_COUNT + 3;

	/**
	 * The number of operations of the '<em>Abstract Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_TYPE_OPERATION_COUNT = PARAMETER_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link dataModelPackage.impl.ArrayTypeImpl <em>Array Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see dataModelPackage.impl.ArrayTypeImpl
	 * @see dataModelPackage.impl.DataModelPackageImpl#getArrayType()
	 * @generated
	 */
	int ARRAY_TYPE = 4;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ARRAY_TYPE__NAME = PARAMETER_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Values</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ARRAY_TYPE__VALUES = PARAMETER_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Primitive Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ARRAY_TYPE__PRIMITIVE_TYPE = PARAMETER_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Data Model Type</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ARRAY_TYPE__DATA_MODEL_TYPE = PARAMETER_FEATURE_COUNT + 3;

	/**
	 * The number of structural features of the '<em>Array Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ARRAY_TYPE_FEATURE_COUNT = PARAMETER_FEATURE_COUNT + 4;

	/**
	 * The number of operations of the '<em>Array Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ARRAY_TYPE_OPERATION_COUNT = PARAMETER_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link dataModelPackage.impl.SimpleTypeImpl <em>Simple Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see dataModelPackage.impl.SimpleTypeImpl
	 * @see dataModelPackage.impl.DataModelPackageImpl#getSimpleType()
	 * @generated
	 */
	int SIMPLE_TYPE = 5;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SIMPLE_TYPE__NAME = PARAMETER_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SIMPLE_TYPE__TYPE = PARAMETER_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Value</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SIMPLE_TYPE__VALUE = PARAMETER_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>Simple Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SIMPLE_TYPE_FEATURE_COUNT = PARAMETER_FEATURE_COUNT + 3;

	/**
	 * The number of operations of the '<em>Simple Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SIMPLE_TYPE_OPERATION_COUNT = PARAMETER_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link dataModelPackage.impl.PrimitiveValueImpl <em>Primitive Value</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see dataModelPackage.impl.PrimitiveValueImpl
	 * @see dataModelPackage.impl.DataModelPackageImpl#getPrimitiveValue()
	 * @generated
	 */
	int PRIMITIVE_VALUE = 6;

	/**
	 * The number of structural features of the '<em>Primitive Value</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PRIMITIVE_VALUE_FEATURE_COUNT = 0;

	/**
	 * The number of operations of the '<em>Primitive Value</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PRIMITIVE_VALUE_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link dataModelPackage.impl.IntValueImpl <em>Int Value</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see dataModelPackage.impl.IntValueImpl
	 * @see dataModelPackage.impl.DataModelPackageImpl#getIntValue()
	 * @generated
	 */
	int INT_VALUE = 7;

	/**
	 * The feature id for the '<em><b>Int Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INT_VALUE__INT_VALUE = PRIMITIVE_VALUE_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Int Value</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INT_VALUE_FEATURE_COUNT = PRIMITIVE_VALUE_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Int Value</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INT_VALUE_OPERATION_COUNT = PRIMITIVE_VALUE_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link dataModelPackage.impl.BoolValueImpl <em>Bool Value</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see dataModelPackage.impl.BoolValueImpl
	 * @see dataModelPackage.impl.DataModelPackageImpl#getBoolValue()
	 * @generated
	 */
	int BOOL_VALUE = 8;

	/**
	 * The feature id for the '<em><b>Bool Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BOOL_VALUE__BOOL_VALUE = PRIMITIVE_VALUE_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Bool Value</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BOOL_VALUE_FEATURE_COUNT = PRIMITIVE_VALUE_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Bool Value</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BOOL_VALUE_OPERATION_COUNT = PRIMITIVE_VALUE_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link dataModelPackage.impl.FloatValueImpl <em>Float Value</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see dataModelPackage.impl.FloatValueImpl
	 * @see dataModelPackage.impl.DataModelPackageImpl#getFloatValue()
	 * @generated
	 */
	int FLOAT_VALUE = 9;

	/**
	 * The feature id for the '<em><b>Float Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FLOAT_VALUE__FLOAT_VALUE = PRIMITIVE_VALUE_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Float Value</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FLOAT_VALUE_FEATURE_COUNT = PRIMITIVE_VALUE_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Float Value</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FLOAT_VALUE_OPERATION_COUNT = PRIMITIVE_VALUE_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link dataModelPackage.impl.StringValueImpl <em>String Value</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see dataModelPackage.impl.StringValueImpl
	 * @see dataModelPackage.impl.DataModelPackageImpl#getStringValue()
	 * @generated
	 */
	int STRING_VALUE = 10;

	/**
	 * The feature id for the '<em><b>String Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRING_VALUE__STRING_VALUE = PRIMITIVE_VALUE_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>String Value</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRING_VALUE_FEATURE_COUNT = PRIMITIVE_VALUE_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>String Value</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRING_VALUE_OPERATION_COUNT = PRIMITIVE_VALUE_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link dataModelPackage.impl.AbstractObjectValueImpl <em>Abstract Object Value</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see dataModelPackage.impl.AbstractObjectValueImpl
	 * @see dataModelPackage.impl.DataModelPackageImpl#getAbstractObjectValue()
	 * @generated
	 */
	int ABSTRACT_OBJECT_VALUE = 11;

	/**
	 * The feature id for the '<em><b>Abstract Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_OBJECT_VALUE__ABSTRACT_VALUE = PRIMITIVE_VALUE_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Abstract Object Value</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_OBJECT_VALUE_FEATURE_COUNT = PRIMITIVE_VALUE_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Abstract Object Value</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_OBJECT_VALUE_OPERATION_COUNT = PRIMITIVE_VALUE_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link dataModelPackage.impl.ArrayValuesImpl <em>Array Values</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see dataModelPackage.impl.ArrayValuesImpl
	 * @see dataModelPackage.impl.DataModelPackageImpl#getArrayValues()
	 * @generated
	 */
	int ARRAY_VALUES = 12;

	/**
	 * The feature id for the '<em><b>Values</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ARRAY_VALUES__VALUES = PRIMITIVE_VALUE_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Array Values</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ARRAY_VALUES_FEATURE_COUNT = PRIMITIVE_VALUE_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Array Values</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ARRAY_VALUES_OPERATION_COUNT = PRIMITIVE_VALUE_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link dataModelPackage.impl.DateValueImpl <em>Date Value</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see dataModelPackage.impl.DateValueImpl
	 * @see dataModelPackage.impl.DataModelPackageImpl#getDateValue()
	 * @generated
	 */
	int DATE_VALUE = 13;

	/**
	 * The feature id for the '<em><b>Date Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATE_VALUE__DATE_VALUE = PRIMITIVE_VALUE_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Date Value</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATE_VALUE_FEATURE_COUNT = PRIMITIVE_VALUE_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Date Value</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATE_VALUE_OPERATION_COUNT = PRIMITIVE_VALUE_OPERATION_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Data Value</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_VALUE_FEATURE_COUNT = 0;

	/**
	 * The number of operations of the '<em>Data Value</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_VALUE_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link dataModelPackage.PrimitiveValueType <em>Primitive Value Type</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see dataModelPackage.PrimitiveValueType
	 * @see dataModelPackage.impl.DataModelPackageImpl#getPrimitiveValueType()
	 * @generated
	 */
	int PRIMITIVE_VALUE_TYPE = 15;


	/**
	 * Returns the meta object for class '{@link dataModelPackage.DataPackage <em>Data Package</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Data Package</em>'.
	 * @see dataModelPackage.DataPackage
	 * @generated
	 */
	EClass getDataPackage();

	/**
	 * Returns the meta object for the attribute '{@link dataModelPackage.DataPackage#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see dataModelPackage.DataPackage#getName()
	 * @see #getDataPackage()
	 * @generated
	 */
	EAttribute getDataPackage_Name();

	/**
	 * Returns the meta object for the containment reference list '{@link dataModelPackage.DataPackage#getDataModelCollections <em>Data Model Collections</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Data Model Collections</em>'.
	 * @see dataModelPackage.DataPackage#getDataModelCollections()
	 * @see #getDataPackage()
	 * @generated
	 */
	EReference getDataPackage_DataModelCollections();

	/**
	 * Returns the meta object for class '{@link dataModelPackage.DataModel <em>Data Model</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Data Model</em>'.
	 * @see dataModelPackage.DataModel
	 * @generated
	 */
	EClass getDataModel();

	/**
	 * Returns the meta object for the containment reference list '{@link dataModelPackage.DataModel#getPrimitives <em>Primitives</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Primitives</em>'.
	 * @see dataModelPackage.DataModel#getPrimitives()
	 * @see #getDataModel()
	 * @generated
	 */
	EReference getDataModel_Primitives();

	/**
	 * Returns the meta object for the reference list '{@link dataModelPackage.DataModel#getComposites <em>Composites</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Composites</em>'.
	 * @see dataModelPackage.DataModel#getComposites()
	 * @see #getDataModel()
	 * @generated
	 */
	EReference getDataModel_Composites();

	/**
	 * Returns the meta object for the attribute '{@link dataModelPackage.DataModel#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see dataModelPackage.DataModel#getName()
	 * @see #getDataModel()
	 * @generated
	 */
	EAttribute getDataModel_Name();

	/**
	 * Returns the meta object for class '{@link dataModelPackage.Parameter <em>Parameter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Parameter</em>'.
	 * @see dataModelPackage.Parameter
	 * @generated
	 */
	EClass getParameter();

	/**
	 * Returns the meta object for class '{@link dataModelPackage.AbstractType <em>Abstract Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Abstract Type</em>'.
	 * @see dataModelPackage.AbstractType
	 * @generated
	 */
	EClass getAbstractType();

	/**
	 * Returns the meta object for the attribute '{@link dataModelPackage.AbstractType#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see dataModelPackage.AbstractType#getName()
	 * @see #getAbstractType()
	 * @generated
	 */
	EAttribute getAbstractType_Name();

	/**
	 * Returns the meta object for the reference '{@link dataModelPackage.AbstractType#getType <em>Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Type</em>'.
	 * @see dataModelPackage.AbstractType#getType()
	 * @see #getAbstractType()
	 * @generated
	 */
	EReference getAbstractType_Type();

	/**
	 * Returns the meta object for the containment reference '{@link dataModelPackage.AbstractType#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Value</em>'.
	 * @see dataModelPackage.AbstractType#getValue()
	 * @see #getAbstractType()
	 * @generated
	 */
	EReference getAbstractType_Value();

	/**
	 * Returns the meta object for class '{@link dataModelPackage.ArrayType <em>Array Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Array Type</em>'.
	 * @see dataModelPackage.ArrayType
	 * @generated
	 */
	EClass getArrayType();

	/**
	 * Returns the meta object for the attribute '{@link dataModelPackage.ArrayType#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see dataModelPackage.ArrayType#getName()
	 * @see #getArrayType()
	 * @generated
	 */
	EAttribute getArrayType_Name();

	/**
	 * Returns the meta object for the containment reference list '{@link dataModelPackage.ArrayType#getValues <em>Values</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Values</em>'.
	 * @see dataModelPackage.ArrayType#getValues()
	 * @see #getArrayType()
	 * @generated
	 */
	EReference getArrayType_Values();

	/**
	 * Returns the meta object for the attribute '{@link dataModelPackage.ArrayType#getPrimitiveType <em>Primitive Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Primitive Type</em>'.
	 * @see dataModelPackage.ArrayType#getPrimitiveType()
	 * @see #getArrayType()
	 * @generated
	 */
	EAttribute getArrayType_PrimitiveType();

	/**
	 * Returns the meta object for the reference '{@link dataModelPackage.ArrayType#getDataModelType <em>Data Model Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Data Model Type</em>'.
	 * @see dataModelPackage.ArrayType#getDataModelType()
	 * @see #getArrayType()
	 * @generated
	 */
	EReference getArrayType_DataModelType();

	/**
	 * Returns the meta object for class '{@link dataModelPackage.SimpleType <em>Simple Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Simple Type</em>'.
	 * @see dataModelPackage.SimpleType
	 * @generated
	 */
	EClass getSimpleType();

	/**
	 * Returns the meta object for the attribute '{@link dataModelPackage.SimpleType#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see dataModelPackage.SimpleType#getName()
	 * @see #getSimpleType()
	 * @generated
	 */
	EAttribute getSimpleType_Name();

	/**
	 * Returns the meta object for the attribute '{@link dataModelPackage.SimpleType#getType <em>Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Type</em>'.
	 * @see dataModelPackage.SimpleType#getType()
	 * @see #getSimpleType()
	 * @generated
	 */
	EAttribute getSimpleType_Type();

	/**
	 * Returns the meta object for the containment reference '{@link dataModelPackage.SimpleType#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Value</em>'.
	 * @see dataModelPackage.SimpleType#getValue()
	 * @see #getSimpleType()
	 * @generated
	 */
	EReference getSimpleType_Value();

	/**
	 * Returns the meta object for class '{@link dataModelPackage.PrimitiveValue <em>Primitive Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Primitive Value</em>'.
	 * @see dataModelPackage.PrimitiveValue
	 * @generated
	 */
	EClass getPrimitiveValue();

	/**
	 * Returns the meta object for class '{@link dataModelPackage.IntValue <em>Int Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Int Value</em>'.
	 * @see dataModelPackage.IntValue
	 * @generated
	 */
	EClass getIntValue();

	/**
	 * Returns the meta object for the attribute '{@link dataModelPackage.IntValue#getIntValue <em>Int Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Int Value</em>'.
	 * @see dataModelPackage.IntValue#getIntValue()
	 * @see #getIntValue()
	 * @generated
	 */
	EAttribute getIntValue_IntValue();

	/**
	 * Returns the meta object for class '{@link dataModelPackage.BoolValue <em>Bool Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Bool Value</em>'.
	 * @see dataModelPackage.BoolValue
	 * @generated
	 */
	EClass getBoolValue();

	/**
	 * Returns the meta object for the attribute '{@link dataModelPackage.BoolValue#isBoolValue <em>Bool Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Bool Value</em>'.
	 * @see dataModelPackage.BoolValue#isBoolValue()
	 * @see #getBoolValue()
	 * @generated
	 */
	EAttribute getBoolValue_BoolValue();

	/**
	 * Returns the meta object for class '{@link dataModelPackage.FloatValue <em>Float Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Float Value</em>'.
	 * @see dataModelPackage.FloatValue
	 * @generated
	 */
	EClass getFloatValue();

	/**
	 * Returns the meta object for the attribute '{@link dataModelPackage.FloatValue#getFloatValue <em>Float Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Float Value</em>'.
	 * @see dataModelPackage.FloatValue#getFloatValue()
	 * @see #getFloatValue()
	 * @generated
	 */
	EAttribute getFloatValue_FloatValue();

	/**
	 * Returns the meta object for class '{@link dataModelPackage.StringValue <em>String Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>String Value</em>'.
	 * @see dataModelPackage.StringValue
	 * @generated
	 */
	EClass getStringValue();

	/**
	 * Returns the meta object for the attribute '{@link dataModelPackage.StringValue#getStringValue <em>String Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>String Value</em>'.
	 * @see dataModelPackage.StringValue#getStringValue()
	 * @see #getStringValue()
	 * @generated
	 */
	EAttribute getStringValue_StringValue();

	/**
	 * Returns the meta object for class '{@link dataModelPackage.AbstractObjectValue <em>Abstract Object Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Abstract Object Value</em>'.
	 * @see dataModelPackage.AbstractObjectValue
	 * @generated
	 */
	EClass getAbstractObjectValue();

	/**
	 * Returns the meta object for the attribute '{@link dataModelPackage.AbstractObjectValue#getAbstractValue <em>Abstract Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Abstract Value</em>'.
	 * @see dataModelPackage.AbstractObjectValue#getAbstractValue()
	 * @see #getAbstractObjectValue()
	 * @generated
	 */
	EAttribute getAbstractObjectValue_AbstractValue();

	/**
	 * Returns the meta object for class '{@link dataModelPackage.ArrayValues <em>Array Values</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Array Values</em>'.
	 * @see dataModelPackage.ArrayValues
	 * @generated
	 */
	EClass getArrayValues();

	/**
	 * Returns the meta object for the containment reference list '{@link dataModelPackage.ArrayValues#getValues <em>Values</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Values</em>'.
	 * @see dataModelPackage.ArrayValues#getValues()
	 * @see #getArrayValues()
	 * @generated
	 */
	EReference getArrayValues_Values();

	/**
	 * Returns the meta object for class '{@link dataModelPackage.DateValue <em>Date Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Date Value</em>'.
	 * @see dataModelPackage.DateValue
	 * @generated
	 */
	EClass getDateValue();

	/**
	 * Returns the meta object for the attribute '{@link dataModelPackage.DateValue#getDateValue <em>Date Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Date Value</em>'.
	 * @see dataModelPackage.DateValue#getDateValue()
	 * @see #getDateValue()
	 * @generated
	 */
	EAttribute getDateValue_DateValue();

	/**
	 * Returns the meta object for class '{@link dataModelPackage.DataValue <em>Data Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Data Value</em>'.
	 * @see dataModelPackage.DataValue
	 * @generated
	 */
	EClass getDataValue();

	/**
	 * Returns the meta object for enum '{@link dataModelPackage.PrimitiveValueType <em>Primitive Value Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Primitive Value Type</em>'.
	 * @see dataModelPackage.PrimitiveValueType
	 * @generated
	 */
	EEnum getPrimitiveValueType();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	DataModelFactory getDataModelFactory();

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
		 * The meta object literal for the '{@link dataModelPackage.impl.DataPackageImpl <em>Data Package</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see dataModelPackage.impl.DataPackageImpl
		 * @see dataModelPackage.impl.DataModelPackageImpl#getDataPackage()
		 * @generated
		 */
		EClass DATA_PACKAGE = eINSTANCE.getDataPackage();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DATA_PACKAGE__NAME = eINSTANCE.getDataPackage_Name();

		/**
		 * The meta object literal for the '<em><b>Data Model Collections</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DATA_PACKAGE__DATA_MODEL_COLLECTIONS = eINSTANCE.getDataPackage_DataModelCollections();

		/**
		 * The meta object literal for the '{@link dataModelPackage.impl.DataModelImpl <em>Data Model</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see dataModelPackage.impl.DataModelImpl
		 * @see dataModelPackage.impl.DataModelPackageImpl#getDataModel()
		 * @generated
		 */
		EClass DATA_MODEL = eINSTANCE.getDataModel();

		/**
		 * The meta object literal for the '<em><b>Primitives</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DATA_MODEL__PRIMITIVES = eINSTANCE.getDataModel_Primitives();

		/**
		 * The meta object literal for the '<em><b>Composites</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DATA_MODEL__COMPOSITES = eINSTANCE.getDataModel_Composites();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DATA_MODEL__NAME = eINSTANCE.getDataModel_Name();

		/**
		 * The meta object literal for the '{@link dataModelPackage.impl.ParameterImpl <em>Parameter</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see dataModelPackage.impl.ParameterImpl
		 * @see dataModelPackage.impl.DataModelPackageImpl#getParameter()
		 * @generated
		 */
		EClass PARAMETER = eINSTANCE.getParameter();

		/**
		 * The meta object literal for the '{@link dataModelPackage.impl.AbstractTypeImpl <em>Abstract Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see dataModelPackage.impl.AbstractTypeImpl
		 * @see dataModelPackage.impl.DataModelPackageImpl#getAbstractType()
		 * @generated
		 */
		EClass ABSTRACT_TYPE = eINSTANCE.getAbstractType();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ABSTRACT_TYPE__NAME = eINSTANCE.getAbstractType_Name();

		/**
		 * The meta object literal for the '<em><b>Type</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ABSTRACT_TYPE__TYPE = eINSTANCE.getAbstractType_Type();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ABSTRACT_TYPE__VALUE = eINSTANCE.getAbstractType_Value();

		/**
		 * The meta object literal for the '{@link dataModelPackage.impl.ArrayTypeImpl <em>Array Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see dataModelPackage.impl.ArrayTypeImpl
		 * @see dataModelPackage.impl.DataModelPackageImpl#getArrayType()
		 * @generated
		 */
		EClass ARRAY_TYPE = eINSTANCE.getArrayType();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ARRAY_TYPE__NAME = eINSTANCE.getArrayType_Name();

		/**
		 * The meta object literal for the '<em><b>Values</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ARRAY_TYPE__VALUES = eINSTANCE.getArrayType_Values();

		/**
		 * The meta object literal for the '<em><b>Primitive Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ARRAY_TYPE__PRIMITIVE_TYPE = eINSTANCE.getArrayType_PrimitiveType();

		/**
		 * The meta object literal for the '<em><b>Data Model Type</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ARRAY_TYPE__DATA_MODEL_TYPE = eINSTANCE.getArrayType_DataModelType();

		/**
		 * The meta object literal for the '{@link dataModelPackage.impl.SimpleTypeImpl <em>Simple Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see dataModelPackage.impl.SimpleTypeImpl
		 * @see dataModelPackage.impl.DataModelPackageImpl#getSimpleType()
		 * @generated
		 */
		EClass SIMPLE_TYPE = eINSTANCE.getSimpleType();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SIMPLE_TYPE__NAME = eINSTANCE.getSimpleType_Name();

		/**
		 * The meta object literal for the '<em><b>Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SIMPLE_TYPE__TYPE = eINSTANCE.getSimpleType_Type();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SIMPLE_TYPE__VALUE = eINSTANCE.getSimpleType_Value();

		/**
		 * The meta object literal for the '{@link dataModelPackage.impl.PrimitiveValueImpl <em>Primitive Value</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see dataModelPackage.impl.PrimitiveValueImpl
		 * @see dataModelPackage.impl.DataModelPackageImpl#getPrimitiveValue()
		 * @generated
		 */
		EClass PRIMITIVE_VALUE = eINSTANCE.getPrimitiveValue();

		/**
		 * The meta object literal for the '{@link dataModelPackage.impl.IntValueImpl <em>Int Value</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see dataModelPackage.impl.IntValueImpl
		 * @see dataModelPackage.impl.DataModelPackageImpl#getIntValue()
		 * @generated
		 */
		EClass INT_VALUE = eINSTANCE.getIntValue();

		/**
		 * The meta object literal for the '<em><b>Int Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute INT_VALUE__INT_VALUE = eINSTANCE.getIntValue_IntValue();

		/**
		 * The meta object literal for the '{@link dataModelPackage.impl.BoolValueImpl <em>Bool Value</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see dataModelPackage.impl.BoolValueImpl
		 * @see dataModelPackage.impl.DataModelPackageImpl#getBoolValue()
		 * @generated
		 */
		EClass BOOL_VALUE = eINSTANCE.getBoolValue();

		/**
		 * The meta object literal for the '<em><b>Bool Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute BOOL_VALUE__BOOL_VALUE = eINSTANCE.getBoolValue_BoolValue();

		/**
		 * The meta object literal for the '{@link dataModelPackage.impl.FloatValueImpl <em>Float Value</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see dataModelPackage.impl.FloatValueImpl
		 * @see dataModelPackage.impl.DataModelPackageImpl#getFloatValue()
		 * @generated
		 */
		EClass FLOAT_VALUE = eINSTANCE.getFloatValue();

		/**
		 * The meta object literal for the '<em><b>Float Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FLOAT_VALUE__FLOAT_VALUE = eINSTANCE.getFloatValue_FloatValue();

		/**
		 * The meta object literal for the '{@link dataModelPackage.impl.StringValueImpl <em>String Value</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see dataModelPackage.impl.StringValueImpl
		 * @see dataModelPackage.impl.DataModelPackageImpl#getStringValue()
		 * @generated
		 */
		EClass STRING_VALUE = eINSTANCE.getStringValue();

		/**
		 * The meta object literal for the '<em><b>String Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute STRING_VALUE__STRING_VALUE = eINSTANCE.getStringValue_StringValue();

		/**
		 * The meta object literal for the '{@link dataModelPackage.impl.AbstractObjectValueImpl <em>Abstract Object Value</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see dataModelPackage.impl.AbstractObjectValueImpl
		 * @see dataModelPackage.impl.DataModelPackageImpl#getAbstractObjectValue()
		 * @generated
		 */
		EClass ABSTRACT_OBJECT_VALUE = eINSTANCE.getAbstractObjectValue();

		/**
		 * The meta object literal for the '<em><b>Abstract Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ABSTRACT_OBJECT_VALUE__ABSTRACT_VALUE = eINSTANCE.getAbstractObjectValue_AbstractValue();

		/**
		 * The meta object literal for the '{@link dataModelPackage.impl.ArrayValuesImpl <em>Array Values</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see dataModelPackage.impl.ArrayValuesImpl
		 * @see dataModelPackage.impl.DataModelPackageImpl#getArrayValues()
		 * @generated
		 */
		EClass ARRAY_VALUES = eINSTANCE.getArrayValues();

		/**
		 * The meta object literal for the '<em><b>Values</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ARRAY_VALUES__VALUES = eINSTANCE.getArrayValues_Values();

		/**
		 * The meta object literal for the '{@link dataModelPackage.impl.DateValueImpl <em>Date Value</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see dataModelPackage.impl.DateValueImpl
		 * @see dataModelPackage.impl.DataModelPackageImpl#getDateValue()
		 * @generated
		 */
		EClass DATE_VALUE = eINSTANCE.getDateValue();

		/**
		 * The meta object literal for the '<em><b>Date Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DATE_VALUE__DATE_VALUE = eINSTANCE.getDateValue_DateValue();

		/**
		 * The meta object literal for the '{@link dataModelPackage.impl.DataValueImpl <em>Data Value</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see dataModelPackage.impl.DataValueImpl
		 * @see dataModelPackage.impl.DataModelPackageImpl#getDataValue()
		 * @generated
		 */
		EClass DATA_VALUE = eINSTANCE.getDataValue();

		/**
		 * The meta object literal for the '{@link dataModelPackage.PrimitiveValueType <em>Primitive Value Type</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see dataModelPackage.PrimitiveValueType
		 * @see dataModelPackage.impl.DataModelPackageImpl#getPrimitiveValueType()
		 * @generated
		 */
		EEnum PRIMITIVE_VALUE_TYPE = eINSTANCE.getPrimitiveValueType();

	}

} //DataModelPackage
