/**
 */
package mncModel;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Command</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link mncModel.Command#isAsynch <em>Asynch</em>}</li>
 * </ul>
 *
 * @see mncModel.MncModelPackage#getCommand()
 * @model
 * @generated
 */
public interface Command extends AbstractInterfaceItems {
	/**
	 * Returns the value of the '<em><b>Asynch</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Asynch</em>' attribute.
	 * @see #setAsynch(boolean)
	 * @see mncModel.MncModelPackage#getCommand_Asynch()
	 * @model
	 * @generated
	 */
	boolean isAsynch();

	/**
	 * Sets the value of the '{@link mncModel.Command#isAsynch <em>Asynch</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Asynch</em>' attribute.
	 * @see #isAsynch()
	 * @generated
	 */
	void setAsynch(boolean value);

} // Command
