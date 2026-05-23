/**
 */
package activityDiagramModel;

import mncModel.AbstractOutcomeItems;
import mncModel.CheckParameterCondition;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Outcome</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link activityDiagramModel.Outcome#getName <em>Name</em>}</li>
 *   <li>{@link activityDiagramModel.Outcome#getCapabilityOutcome <em>Capability Outcome</em>}</li>
 *   <li>{@link activityDiagramModel.Outcome#getOutcomeValidation <em>Outcome Validation</em>}</li>
 * </ul>
 *
 * @see activityDiagramModel.ActivityDiagramModelPackage#getOutcome()
 * @model
 * @generated
 */
public interface Outcome extends EObject {
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
	 * @see activityDiagramModel.ActivityDiagramModelPackage#getOutcome_Name()
	 * @model
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link activityDiagramModel.Outcome#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Capability Outcome</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Capability Outcome</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Capability Outcome</em>' reference.
	 * @see #setCapabilityOutcome(AbstractOutcomeItems)
	 * @see activityDiagramModel.ActivityDiagramModelPackage#getOutcome_CapabilityOutcome()
	 * @model
	 * @generated
	 */
	AbstractOutcomeItems getCapabilityOutcome();

	/**
	 * Sets the value of the '{@link activityDiagramModel.Outcome#getCapabilityOutcome <em>Capability Outcome</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Capability Outcome</em>' reference.
	 * @see #getCapabilityOutcome()
	 * @generated
	 */
	void setCapabilityOutcome(AbstractOutcomeItems value);

	/**
	 * Returns the value of the '<em><b>Outcome Validation</b></em>' containment reference list.
	 * The list contents are of type {@link mncModel.CheckParameterCondition}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Outcome Validation</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Outcome Validation</em>' containment reference list.
	 * @see activityDiagramModel.ActivityDiagramModelPackage#getOutcome_OutcomeValidation()
	 * @model containment="true"
	 * @generated
	 */
	EList<CheckParameterCondition> getOutcomeValidation();

} // Outcome
