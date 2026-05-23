/**
 */
package mncModel;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Event</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link mncModel.Event#isPublish <em>Publish</em>}</li>
 * </ul>
 *
 * @see mncModel.MncModelPackage#getEvent()
 * @model
 * @generated
 */
public interface Event extends AbstractInterfaceItems, AbstractOutcomeItems {
	/**
	 * Returns the value of the '<em><b>Publish</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Publish</em>' attribute.
	 * @see #setPublish(boolean)
	 * @see mncModel.MncModelPackage#getEvent_Publish()
	 * @model
	 * @generated
	 */
	boolean isPublish();

	/**
	 * Sets the value of the '{@link mncModel.Event#isPublish <em>Publish</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Publish</em>' attribute.
	 * @see #isPublish()
	 * @generated
	 */
	void setPublish(boolean value);

} // Event
