/**
 */
package activityDiagramModel;

import dataModelPackage.Parameter;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Conditional Activity</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link activityDiagramModel.ConditionalActivity#getOnTrueNextActivity <em>On True Next Activity</em>}</li>
 *   <li>{@link activityDiagramModel.ConditionalActivity#getOnFalseNextActivity <em>On False Next Activity</em>}</li>
 *   <li>{@link activityDiagramModel.ConditionalActivity#getOnTrueFinalResult <em>On True Final Result</em>}</li>
 *   <li>{@link activityDiagramModel.ConditionalActivity#getOnFalseFinalResult <em>On False Final Result</em>}</li>
 *   <li>{@link activityDiagramModel.ConditionalActivity#getOutcome <em>Outcome</em>}</li>
 *   <li>{@link activityDiagramModel.ConditionalActivity#getBOp <em>BOp</em>}</li>
 * </ul>
 *
 * @see activityDiagramModel.ActivityDiagramModelPackage#getConditionalActivity()
 * @model
 * @generated
 */
public interface ConditionalActivity extends EObject {
	/**
	 * Returns the value of the '<em><b>On True Next Activity</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>On True Next Activity</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>On True Next Activity</em>' reference.
	 * @see #setOnTrueNextActivity(Activity)
	 * @see activityDiagramModel.ActivityDiagramModelPackage#getConditionalActivity_OnTrueNextActivity()
	 * @model
	 * @generated
	 */
	Activity getOnTrueNextActivity();

	/**
	 * Sets the value of the '{@link activityDiagramModel.ConditionalActivity#getOnTrueNextActivity <em>On True Next Activity</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>On True Next Activity</em>' reference.
	 * @see #getOnTrueNextActivity()
	 * @generated
	 */
	void setOnTrueNextActivity(Activity value);

	/**
	 * Returns the value of the '<em><b>On False Next Activity</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>On False Next Activity</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>On False Next Activity</em>' reference.
	 * @see #setOnFalseNextActivity(Activity)
	 * @see activityDiagramModel.ActivityDiagramModelPackage#getConditionalActivity_OnFalseNextActivity()
	 * @model
	 * @generated
	 */
	Activity getOnFalseNextActivity();

	/**
	 * Sets the value of the '{@link activityDiagramModel.ConditionalActivity#getOnFalseNextActivity <em>On False Next Activity</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>On False Next Activity</em>' reference.
	 * @see #getOnFalseNextActivity()
	 * @generated
	 */
	void setOnFalseNextActivity(Activity value);

	/**
	 * Returns the value of the '<em><b>On True Final Result</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>On True Final Result</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>On True Final Result</em>' reference.
	 * @see #setOnTrueFinalResult(Parameter)
	 * @see activityDiagramModel.ActivityDiagramModelPackage#getConditionalActivity_OnTrueFinalResult()
	 * @model
	 * @generated
	 */
	Parameter getOnTrueFinalResult();

	/**
	 * Sets the value of the '{@link activityDiagramModel.ConditionalActivity#getOnTrueFinalResult <em>On True Final Result</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>On True Final Result</em>' reference.
	 * @see #getOnTrueFinalResult()
	 * @generated
	 */
	void setOnTrueFinalResult(Parameter value);

	/**
	 * Returns the value of the '<em><b>On False Final Result</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>On False Final Result</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>On False Final Result</em>' reference.
	 * @see #setOnFalseFinalResult(Parameter)
	 * @see activityDiagramModel.ActivityDiagramModelPackage#getConditionalActivity_OnFalseFinalResult()
	 * @model
	 * @generated
	 */
	Parameter getOnFalseFinalResult();

	/**
	 * Sets the value of the '{@link activityDiagramModel.ConditionalActivity#getOnFalseFinalResult <em>On False Final Result</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>On False Final Result</em>' reference.
	 * @see #getOnFalseFinalResult()
	 * @generated
	 */
	void setOnFalseFinalResult(Parameter value);

	/**
	 * Returns the value of the '<em><b>Outcome</b></em>' containment reference list.
	 * The list contents are of type {@link activityDiagramModel.Outcome}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Outcome</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Outcome</em>' containment reference list.
	 * @see activityDiagramModel.ActivityDiagramModelPackage#getConditionalActivity_Outcome()
	 * @model containment="true"
	 * @generated
	 */
	EList<Outcome> getOutcome();

	/**
	 * Returns the value of the '<em><b>BOp</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.String}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>BOp</em>' attribute list.
	 * @see activityDiagramModel.ActivityDiagramModelPackage#getConditionalActivity_BOp()
	 * @model
	 * @generated
	 */
	EList<String> getBOp();

} // ConditionalActivity
