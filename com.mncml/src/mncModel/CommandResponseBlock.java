/**
 */
package mncModel;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Command Response Block</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link mncModel.CommandResponseBlock#getCommand <em>Command</em>}</li>
 *   <li>{@link mncModel.CommandResponseBlock#getResponseBlock <em>Response Block</em>}</li>
 * </ul>
 *
 * @see mncModel.MncModelPackage#getCommandResponseBlock()
 * @model
 * @generated
 */
public interface CommandResponseBlock extends BehaviorBlock {
	/**
	 * Returns the value of the '<em><b>Command</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Command</em>' reference.
	 * @see #setCommand(Command)
	 * @see mncModel.MncModelPackage#getCommandResponseBlock_Command()
	 * @model
	 * @generated
	 */
	Command getCommand();

	/**
	 * Sets the value of the '{@link mncModel.CommandResponseBlock#getCommand <em>Command</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Command</em>' reference.
	 * @see #getCommand()
	 * @generated
	 */
	void setCommand(Command value);

	/**
	 * Returns the value of the '<em><b>Response Block</b></em>' containment reference list.
	 * The list contents are of type {@link mncModel.ResponseBlock}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Response Block</em>' containment reference list.
	 * @see mncModel.MncModelPackage#getCommandResponseBlock_ResponseBlock()
	 * @model containment="true"
	 * @generated
	 */
	EList<ResponseBlock> getResponseBlock();

} // CommandResponseBlock
