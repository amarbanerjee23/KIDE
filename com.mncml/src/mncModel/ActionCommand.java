/**
 */
package mncModel;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Action Command</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link mncModel.ActionCommand#getCommand <em>Command</em>}</li>
 *   <li>{@link mncModel.ActionCommand#getResponseHandling <em>Response Handling</em>}</li>
 * </ul>
 *
 * @see mncModel.MncModelPackage#getActionCommand()
 * @model
 * @generated
 */
public interface ActionCommand extends ActionItem {
	/**
	 * Returns the value of the '<em><b>Command</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Command</em>' reference.
	 * @see #setCommand(Command)
	 * @see mncModel.MncModelPackage#getActionCommand_Command()
	 * @model
	 * @generated
	 */
	Command getCommand();

	/**
	 * Sets the value of the '{@link mncModel.ActionCommand#getCommand <em>Command</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Command</em>' reference.
	 * @see #getCommand()
	 * @generated
	 */
	void setCommand(Command value);

	/**
	 * Returns the value of the '<em><b>Response Handling</b></em>' containment reference list.
	 * The list contents are of type {@link mncModel.ResponseBlock}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Response Handling</em>' containment reference list.
	 * @see mncModel.MncModelPackage#getActionCommand_ResponseHandling()
	 * @model containment="true"
	 * @generated
	 */
	EList<ResponseBlock> getResponseHandling();

} // ActionCommand
