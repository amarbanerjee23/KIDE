/**
 */
package activityDiagramModel;

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
 * @see activityDiagramModel.ActivityDiagramModelFactory
 * @model kind="package"
 * @generated
 */
public interface ActivityDiagramModelPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "activityDiagramModel";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://activitydiagram/1.0";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "ActivityDiagram";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	ActivityDiagramModelPackage eINSTANCE = activityDiagramModel.impl.ActivityDiagramModelPackageImpl.init();

	/**
	 * The meta object id for the '{@link activityDiagramModel.impl.ActivityDiagramImpl <em>Activity Diagram</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see activityDiagramModel.impl.ActivityDiagramImpl
	 * @see activityDiagramModel.impl.ActivityDiagramModelPackageImpl#getActivityDiagram()
	 * @generated
	 */
	int ACTIVITY_DIAGRAM = 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTIVITY_DIAGRAM__NAME = 0;

	/**
	 * The feature id for the '<em><b>Activities</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTIVITY_DIAGRAM__ACTIVITIES = 1;

	/**
	 * The feature id for the '<em><b>Data Objects</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTIVITY_DIAGRAM__DATA_OBJECTS = 2;

	/**
	 * The feature id for the '<em><b>Results</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTIVITY_DIAGRAM__RESULTS = 3;

	/**
	 * The feature id for the '<em><b>Context Data Model</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTIVITY_DIAGRAM__CONTEXT_DATA_MODEL = 4;

	/**
	 * The feature id for the '<em><b>Physical Context</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTIVITY_DIAGRAM__PHYSICAL_CONTEXT = 5;

	/**
	 * The number of structural features of the '<em>Activity Diagram</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTIVITY_DIAGRAM_FEATURE_COUNT = 6;

	/**
	 * The number of operations of the '<em>Activity Diagram</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTIVITY_DIAGRAM_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link activityDiagramModel.impl.ActivityImpl <em>Activity</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see activityDiagramModel.impl.ActivityImpl
	 * @see activityDiagramModel.impl.ActivityDiagramModelPackageImpl#getActivity()
	 * @generated
	 */
	int ACTIVITY = 1;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTIVITY__NAME = 0;

	/**
	 * The feature id for the '<em><b>Bind Capability</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTIVITY__BIND_CAPABILITY = 1;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTIVITY__DESCRIPTION = 2;

	/**
	 * The feature id for the '<em><b>Conditional Activity</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTIVITY__CONDITIONAL_ACTIVITY = 3;

	/**
	 * The feature id for the '<em><b>Use Control Capabilities</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTIVITY__USE_CONTROL_CAPABILITIES = 4;

	/**
	 * The feature id for the '<em><b>Requires Operation</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTIVITY__REQUIRES_OPERATION = 5;

	/**
	 * The feature id for the '<em><b>Child Activity Diagram</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTIVITY__CHILD_ACTIVITY_DIAGRAM = 6;

	/**
	 * The feature id for the '<em><b>Input Parameters</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTIVITY__INPUT_PARAMETERS = 7;

	/**
	 * The feature id for the '<em><b>Interrupted By</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTIVITY__INTERRUPTED_BY = 8;

	/**
	 * The feature id for the '<em><b>Interrupts</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTIVITY__INTERRUPTS = 9;

	/**
	 * The feature id for the '<em><b>Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTIVITY__TIME = 10;

	/**
	 * The feature id for the '<em><b>Unit</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTIVITY__UNIT = 11;

	/**
	 * The feature id for the '<em><b>Next Activity</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTIVITY__NEXT_ACTIVITY = 12;

	/**
	 * The feature id for the '<em><b>Next Activity Diagram</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTIVITY__NEXT_ACTIVITY_DIAGRAM = 13;

	/**
	 * The feature id for the '<em><b>Required Capability</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTIVITY__REQUIRED_CAPABILITY = 14;

	/**
	 * The number of structural features of the '<em>Activity</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTIVITY_FEATURE_COUNT = 15;

	/**
	 * The number of operations of the '<em>Activity</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTIVITY_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link activityDiagramModel.impl.ConditionalActivityImpl <em>Conditional Activity</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see activityDiagramModel.impl.ConditionalActivityImpl
	 * @see activityDiagramModel.impl.ActivityDiagramModelPackageImpl#getConditionalActivity()
	 * @generated
	 */
	int CONDITIONAL_ACTIVITY = 2;

	/**
	 * The feature id for the '<em><b>On True Next Activity</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONDITIONAL_ACTIVITY__ON_TRUE_NEXT_ACTIVITY = 0;

	/**
	 * The feature id for the '<em><b>On False Next Activity</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONDITIONAL_ACTIVITY__ON_FALSE_NEXT_ACTIVITY = 1;

	/**
	 * The feature id for the '<em><b>On True Final Result</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONDITIONAL_ACTIVITY__ON_TRUE_FINAL_RESULT = 2;

	/**
	 * The feature id for the '<em><b>On False Final Result</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONDITIONAL_ACTIVITY__ON_FALSE_FINAL_RESULT = 3;

	/**
	 * The feature id for the '<em><b>Outcome</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONDITIONAL_ACTIVITY__OUTCOME = 4;

	/**
	 * The feature id for the '<em><b>BOp</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONDITIONAL_ACTIVITY__BOP = 5;

	/**
	 * The number of structural features of the '<em>Conditional Activity</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONDITIONAL_ACTIVITY_FEATURE_COUNT = 6;

	/**
	 * The number of operations of the '<em>Conditional Activity</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONDITIONAL_ACTIVITY_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link activityDiagramModel.impl.OutcomeImpl <em>Outcome</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see activityDiagramModel.impl.OutcomeImpl
	 * @see activityDiagramModel.impl.ActivityDiagramModelPackageImpl#getOutcome()
	 * @generated
	 */
	int OUTCOME = 3;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OUTCOME__NAME = 0;

	/**
	 * The feature id for the '<em><b>Capability Outcome</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OUTCOME__CAPABILITY_OUTCOME = 1;

	/**
	 * The feature id for the '<em><b>Outcome Validation</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OUTCOME__OUTCOME_VALIDATION = 2;

	/**
	 * The number of structural features of the '<em>Outcome</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OUTCOME_FEATURE_COUNT = 3;

	/**
	 * The number of operations of the '<em>Outcome</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OUTCOME_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link activityDiagramModel.UnitTime <em>Unit Time</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see activityDiagramModel.UnitTime
	 * @see activityDiagramModel.impl.ActivityDiagramModelPackageImpl#getUnitTime()
	 * @generated
	 */
	int UNIT_TIME = 4;


	/**
	 * Returns the meta object for class '{@link activityDiagramModel.ActivityDiagram <em>Activity Diagram</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Activity Diagram</em>'.
	 * @see activityDiagramModel.ActivityDiagram
	 * @generated
	 */
	EClass getActivityDiagram();

	/**
	 * Returns the meta object for the attribute '{@link activityDiagramModel.ActivityDiagram#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see activityDiagramModel.ActivityDiagram#getName()
	 * @see #getActivityDiagram()
	 * @generated
	 */
	EAttribute getActivityDiagram_Name();

	/**
	 * Returns the meta object for the containment reference list '{@link activityDiagramModel.ActivityDiagram#getActivities <em>Activities</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Activities</em>'.
	 * @see activityDiagramModel.ActivityDiagram#getActivities()
	 * @see #getActivityDiagram()
	 * @generated
	 */
	EReference getActivityDiagram_Activities();

	/**
	 * Returns the meta object for the reference list '{@link activityDiagramModel.ActivityDiagram#getDataObjects <em>Data Objects</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Data Objects</em>'.
	 * @see activityDiagramModel.ActivityDiagram#getDataObjects()
	 * @see #getActivityDiagram()
	 * @generated
	 */
	EReference getActivityDiagram_DataObjects();

	/**
	 * Returns the meta object for the containment reference list '{@link activityDiagramModel.ActivityDiagram#getResults <em>Results</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Results</em>'.
	 * @see activityDiagramModel.ActivityDiagram#getResults()
	 * @see #getActivityDiagram()
	 * @generated
	 */
	EReference getActivityDiagram_Results();

	/**
	 * Returns the meta object for the reference list '{@link activityDiagramModel.ActivityDiagram#getContextDataModel <em>Context Data Model</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Context Data Model</em>'.
	 * @see activityDiagramModel.ActivityDiagram#getContextDataModel()
	 * @see #getActivityDiagram()
	 * @generated
	 */
	EReference getActivityDiagram_ContextDataModel();

	/**
	 * Returns the meta object for the attribute list '{@link activityDiagramModel.ActivityDiagram#getPhysicalContext <em>Physical Context</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Physical Context</em>'.
	 * @see activityDiagramModel.ActivityDiagram#getPhysicalContext()
	 * @see #getActivityDiagram()
	 * @generated
	 */
	EAttribute getActivityDiagram_PhysicalContext();

	/**
	 * Returns the meta object for class '{@link activityDiagramModel.Activity <em>Activity</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Activity</em>'.
	 * @see activityDiagramModel.Activity
	 * @generated
	 */
	EClass getActivity();

	/**
	 * Returns the meta object for the attribute '{@link activityDiagramModel.Activity#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see activityDiagramModel.Activity#getName()
	 * @see #getActivity()
	 * @generated
	 */
	EAttribute getActivity_Name();

	/**
	 * Returns the meta object for the reference '{@link activityDiagramModel.Activity#getBindCapability <em>Bind Capability</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Bind Capability</em>'.
	 * @see activityDiagramModel.Activity#getBindCapability()
	 * @see #getActivity()
	 * @generated
	 */
	EReference getActivity_BindCapability();

	/**
	 * Returns the meta object for the attribute '{@link activityDiagramModel.Activity#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see activityDiagramModel.Activity#getDescription()
	 * @see #getActivity()
	 * @generated
	 */
	EAttribute getActivity_Description();

	/**
	 * Returns the meta object for the containment reference list '{@link activityDiagramModel.Activity#getConditionalActivity <em>Conditional Activity</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Conditional Activity</em>'.
	 * @see activityDiagramModel.Activity#getConditionalActivity()
	 * @see #getActivity()
	 * @generated
	 */
	EReference getActivity_ConditionalActivity();

	/**
	 * Returns the meta object for the reference list '{@link activityDiagramModel.Activity#getUseControlCapabilities <em>Use Control Capabilities</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Use Control Capabilities</em>'.
	 * @see activityDiagramModel.Activity#getUseControlCapabilities()
	 * @see #getActivity()
	 * @generated
	 */
	EReference getActivity_UseControlCapabilities();

	/**
	 * Returns the meta object for the reference list '{@link activityDiagramModel.Activity#getRequiresOperation <em>Requires Operation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Requires Operation</em>'.
	 * @see activityDiagramModel.Activity#getRequiresOperation()
	 * @see #getActivity()
	 * @generated
	 */
	EReference getActivity_RequiresOperation();

	/**
	 * Returns the meta object for the reference '{@link activityDiagramModel.Activity#getChildActivityDiagram <em>Child Activity Diagram</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Child Activity Diagram</em>'.
	 * @see activityDiagramModel.Activity#getChildActivityDiagram()
	 * @see #getActivity()
	 * @generated
	 */
	EReference getActivity_ChildActivityDiagram();

	/**
	 * Returns the meta object for the reference list '{@link activityDiagramModel.Activity#getInputParameters <em>Input Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Input Parameters</em>'.
	 * @see activityDiagramModel.Activity#getInputParameters()
	 * @see #getActivity()
	 * @generated
	 */
	EReference getActivity_InputParameters();

	/**
	 * Returns the meta object for the reference list '{@link activityDiagramModel.Activity#getInterruptedBy <em>Interrupted By</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Interrupted By</em>'.
	 * @see activityDiagramModel.Activity#getInterruptedBy()
	 * @see #getActivity()
	 * @generated
	 */
	EReference getActivity_InterruptedBy();

	/**
	 * Returns the meta object for the reference list '{@link activityDiagramModel.Activity#getInterrupts <em>Interrupts</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Interrupts</em>'.
	 * @see activityDiagramModel.Activity#getInterrupts()
	 * @see #getActivity()
	 * @generated
	 */
	EReference getActivity_Interrupts();

	/**
	 * Returns the meta object for the attribute '{@link activityDiagramModel.Activity#getTime <em>Time</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Time</em>'.
	 * @see activityDiagramModel.Activity#getTime()
	 * @see #getActivity()
	 * @generated
	 */
	EAttribute getActivity_Time();

	/**
	 * Returns the meta object for the attribute '{@link activityDiagramModel.Activity#getUnit <em>Unit</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Unit</em>'.
	 * @see activityDiagramModel.Activity#getUnit()
	 * @see #getActivity()
	 * @generated
	 */
	EAttribute getActivity_Unit();

	/**
	 * Returns the meta object for the reference '{@link activityDiagramModel.Activity#getNextActivity <em>Next Activity</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Next Activity</em>'.
	 * @see activityDiagramModel.Activity#getNextActivity()
	 * @see #getActivity()
	 * @generated
	 */
	EReference getActivity_NextActivity();

	/**
	 * Returns the meta object for the reference '{@link activityDiagramModel.Activity#getNextActivityDiagram <em>Next Activity Diagram</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Next Activity Diagram</em>'.
	 * @see activityDiagramModel.Activity#getNextActivityDiagram()
	 * @see #getActivity()
	 * @generated
	 */
	EReference getActivity_NextActivityDiagram();

	/**
	 * Returns the meta object for the attribute '{@link activityDiagramModel.Activity#getRequiredCapability <em>Required Capability</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Required Capability</em>'.
	 * @see activityDiagramModel.Activity#getRequiredCapability()
	 * @see #getActivity()
	 * @generated
	 */
	EAttribute getActivity_RequiredCapability();

	/**
	 * Returns the meta object for class '{@link activityDiagramModel.ConditionalActivity <em>Conditional Activity</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Conditional Activity</em>'.
	 * @see activityDiagramModel.ConditionalActivity
	 * @generated
	 */
	EClass getConditionalActivity();

	/**
	 * Returns the meta object for the reference '{@link activityDiagramModel.ConditionalActivity#getOnTrueNextActivity <em>On True Next Activity</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>On True Next Activity</em>'.
	 * @see activityDiagramModel.ConditionalActivity#getOnTrueNextActivity()
	 * @see #getConditionalActivity()
	 * @generated
	 */
	EReference getConditionalActivity_OnTrueNextActivity();

	/**
	 * Returns the meta object for the reference '{@link activityDiagramModel.ConditionalActivity#getOnFalseNextActivity <em>On False Next Activity</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>On False Next Activity</em>'.
	 * @see activityDiagramModel.ConditionalActivity#getOnFalseNextActivity()
	 * @see #getConditionalActivity()
	 * @generated
	 */
	EReference getConditionalActivity_OnFalseNextActivity();

	/**
	 * Returns the meta object for the reference '{@link activityDiagramModel.ConditionalActivity#getOnTrueFinalResult <em>On True Final Result</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>On True Final Result</em>'.
	 * @see activityDiagramModel.ConditionalActivity#getOnTrueFinalResult()
	 * @see #getConditionalActivity()
	 * @generated
	 */
	EReference getConditionalActivity_OnTrueFinalResult();

	/**
	 * Returns the meta object for the reference '{@link activityDiagramModel.ConditionalActivity#getOnFalseFinalResult <em>On False Final Result</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>On False Final Result</em>'.
	 * @see activityDiagramModel.ConditionalActivity#getOnFalseFinalResult()
	 * @see #getConditionalActivity()
	 * @generated
	 */
	EReference getConditionalActivity_OnFalseFinalResult();

	/**
	 * Returns the meta object for the containment reference list '{@link activityDiagramModel.ConditionalActivity#getOutcome <em>Outcome</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Outcome</em>'.
	 * @see activityDiagramModel.ConditionalActivity#getOutcome()
	 * @see #getConditionalActivity()
	 * @generated
	 */
	EReference getConditionalActivity_Outcome();

	/**
	 * Returns the meta object for the attribute list '{@link activityDiagramModel.ConditionalActivity#getBOp <em>BOp</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>BOp</em>'.
	 * @see activityDiagramModel.ConditionalActivity#getBOp()
	 * @see #getConditionalActivity()
	 * @generated
	 */
	EAttribute getConditionalActivity_BOp();

	/**
	 * Returns the meta object for class '{@link activityDiagramModel.Outcome <em>Outcome</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Outcome</em>'.
	 * @see activityDiagramModel.Outcome
	 * @generated
	 */
	EClass getOutcome();

	/**
	 * Returns the meta object for the attribute '{@link activityDiagramModel.Outcome#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see activityDiagramModel.Outcome#getName()
	 * @see #getOutcome()
	 * @generated
	 */
	EAttribute getOutcome_Name();

	/**
	 * Returns the meta object for the reference '{@link activityDiagramModel.Outcome#getCapabilityOutcome <em>Capability Outcome</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Capability Outcome</em>'.
	 * @see activityDiagramModel.Outcome#getCapabilityOutcome()
	 * @see #getOutcome()
	 * @generated
	 */
	EReference getOutcome_CapabilityOutcome();

	/**
	 * Returns the meta object for the containment reference list '{@link activityDiagramModel.Outcome#getOutcomeValidation <em>Outcome Validation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Outcome Validation</em>'.
	 * @see activityDiagramModel.Outcome#getOutcomeValidation()
	 * @see #getOutcome()
	 * @generated
	 */
	EReference getOutcome_OutcomeValidation();

	/**
	 * Returns the meta object for enum '{@link activityDiagramModel.UnitTime <em>Unit Time</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Unit Time</em>'.
	 * @see activityDiagramModel.UnitTime
	 * @generated
	 */
	EEnum getUnitTime();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	ActivityDiagramModelFactory getActivityDiagramModelFactory();

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
		 * The meta object literal for the '{@link activityDiagramModel.impl.ActivityDiagramImpl <em>Activity Diagram</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see activityDiagramModel.impl.ActivityDiagramImpl
		 * @see activityDiagramModel.impl.ActivityDiagramModelPackageImpl#getActivityDiagram()
		 * @generated
		 */
		EClass ACTIVITY_DIAGRAM = eINSTANCE.getActivityDiagram();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ACTIVITY_DIAGRAM__NAME = eINSTANCE.getActivityDiagram_Name();

		/**
		 * The meta object literal for the '<em><b>Activities</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ACTIVITY_DIAGRAM__ACTIVITIES = eINSTANCE.getActivityDiagram_Activities();

		/**
		 * The meta object literal for the '<em><b>Data Objects</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ACTIVITY_DIAGRAM__DATA_OBJECTS = eINSTANCE.getActivityDiagram_DataObjects();

		/**
		 * The meta object literal for the '<em><b>Results</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ACTIVITY_DIAGRAM__RESULTS = eINSTANCE.getActivityDiagram_Results();

		/**
		 * The meta object literal for the '<em><b>Context Data Model</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ACTIVITY_DIAGRAM__CONTEXT_DATA_MODEL = eINSTANCE.getActivityDiagram_ContextDataModel();

		/**
		 * The meta object literal for the '<em><b>Physical Context</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ACTIVITY_DIAGRAM__PHYSICAL_CONTEXT = eINSTANCE.getActivityDiagram_PhysicalContext();

		/**
		 * The meta object literal for the '{@link activityDiagramModel.impl.ActivityImpl <em>Activity</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see activityDiagramModel.impl.ActivityImpl
		 * @see activityDiagramModel.impl.ActivityDiagramModelPackageImpl#getActivity()
		 * @generated
		 */
		EClass ACTIVITY = eINSTANCE.getActivity();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ACTIVITY__NAME = eINSTANCE.getActivity_Name();

		/**
		 * The meta object literal for the '<em><b>Bind Capability</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ACTIVITY__BIND_CAPABILITY = eINSTANCE.getActivity_BindCapability();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ACTIVITY__DESCRIPTION = eINSTANCE.getActivity_Description();

		/**
		 * The meta object literal for the '<em><b>Conditional Activity</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ACTIVITY__CONDITIONAL_ACTIVITY = eINSTANCE.getActivity_ConditionalActivity();

		/**
		 * The meta object literal for the '<em><b>Use Control Capabilities</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ACTIVITY__USE_CONTROL_CAPABILITIES = eINSTANCE.getActivity_UseControlCapabilities();

		/**
		 * The meta object literal for the '<em><b>Requires Operation</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ACTIVITY__REQUIRES_OPERATION = eINSTANCE.getActivity_RequiresOperation();

		/**
		 * The meta object literal for the '<em><b>Child Activity Diagram</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ACTIVITY__CHILD_ACTIVITY_DIAGRAM = eINSTANCE.getActivity_ChildActivityDiagram();

		/**
		 * The meta object literal for the '<em><b>Input Parameters</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ACTIVITY__INPUT_PARAMETERS = eINSTANCE.getActivity_InputParameters();

		/**
		 * The meta object literal for the '<em><b>Interrupted By</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ACTIVITY__INTERRUPTED_BY = eINSTANCE.getActivity_InterruptedBy();

		/**
		 * The meta object literal for the '<em><b>Interrupts</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ACTIVITY__INTERRUPTS = eINSTANCE.getActivity_Interrupts();

		/**
		 * The meta object literal for the '<em><b>Time</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ACTIVITY__TIME = eINSTANCE.getActivity_Time();

		/**
		 * The meta object literal for the '<em><b>Unit</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ACTIVITY__UNIT = eINSTANCE.getActivity_Unit();

		/**
		 * The meta object literal for the '<em><b>Next Activity</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ACTIVITY__NEXT_ACTIVITY = eINSTANCE.getActivity_NextActivity();

		/**
		 * The meta object literal for the '<em><b>Next Activity Diagram</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ACTIVITY__NEXT_ACTIVITY_DIAGRAM = eINSTANCE.getActivity_NextActivityDiagram();

		/**
		 * The meta object literal for the '<em><b>Required Capability</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ACTIVITY__REQUIRED_CAPABILITY = eINSTANCE.getActivity_RequiredCapability();

		/**
		 * The meta object literal for the '{@link activityDiagramModel.impl.ConditionalActivityImpl <em>Conditional Activity</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see activityDiagramModel.impl.ConditionalActivityImpl
		 * @see activityDiagramModel.impl.ActivityDiagramModelPackageImpl#getConditionalActivity()
		 * @generated
		 */
		EClass CONDITIONAL_ACTIVITY = eINSTANCE.getConditionalActivity();

		/**
		 * The meta object literal for the '<em><b>On True Next Activity</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CONDITIONAL_ACTIVITY__ON_TRUE_NEXT_ACTIVITY = eINSTANCE.getConditionalActivity_OnTrueNextActivity();

		/**
		 * The meta object literal for the '<em><b>On False Next Activity</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CONDITIONAL_ACTIVITY__ON_FALSE_NEXT_ACTIVITY = eINSTANCE.getConditionalActivity_OnFalseNextActivity();

		/**
		 * The meta object literal for the '<em><b>On True Final Result</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CONDITIONAL_ACTIVITY__ON_TRUE_FINAL_RESULT = eINSTANCE.getConditionalActivity_OnTrueFinalResult();

		/**
		 * The meta object literal for the '<em><b>On False Final Result</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CONDITIONAL_ACTIVITY__ON_FALSE_FINAL_RESULT = eINSTANCE.getConditionalActivity_OnFalseFinalResult();

		/**
		 * The meta object literal for the '<em><b>Outcome</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CONDITIONAL_ACTIVITY__OUTCOME = eINSTANCE.getConditionalActivity_Outcome();

		/**
		 * The meta object literal for the '<em><b>BOp</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CONDITIONAL_ACTIVITY__BOP = eINSTANCE.getConditionalActivity_BOp();

		/**
		 * The meta object literal for the '{@link activityDiagramModel.impl.OutcomeImpl <em>Outcome</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see activityDiagramModel.impl.OutcomeImpl
		 * @see activityDiagramModel.impl.ActivityDiagramModelPackageImpl#getOutcome()
		 * @generated
		 */
		EClass OUTCOME = eINSTANCE.getOutcome();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute OUTCOME__NAME = eINSTANCE.getOutcome_Name();

		/**
		 * The meta object literal for the '<em><b>Capability Outcome</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference OUTCOME__CAPABILITY_OUTCOME = eINSTANCE.getOutcome_CapabilityOutcome();

		/**
		 * The meta object literal for the '<em><b>Outcome Validation</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference OUTCOME__OUTCOME_VALIDATION = eINSTANCE.getOutcome_OutcomeValidation();

		/**
		 * The meta object literal for the '{@link activityDiagramModel.UnitTime <em>Unit Time</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see activityDiagramModel.UnitTime
		 * @see activityDiagramModel.impl.ActivityDiagramModelPackageImpl#getUnitTime()
		 * @generated
		 */
		EEnum UNIT_TIME = eINSTANCE.getUnitTime();

	}

} //ActivityDiagramModelPackage
