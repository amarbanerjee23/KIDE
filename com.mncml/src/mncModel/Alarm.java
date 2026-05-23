/**
 */
package mncModel;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Alarm</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link mncModel.Alarm#getType <em>Type</em>}</li>
 *   <li>{@link mncModel.Alarm#getLevel <em>Level</em>}</li>
 *   <li>{@link mncModel.Alarm#isPublish <em>Publish</em>}</li>
 * </ul>
 *
 * @see mncModel.MncModelPackage#getAlarm()
 * @model
 * @generated
 */
public interface Alarm extends AbstractInterfaceItems, AbstractOutcomeItems {
	/**
	 * Returns the value of the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Type</em>' attribute.
	 * @see #setType(String)
	 * @see mncModel.MncModelPackage#getAlarm_Type()
	 * @model
	 * @generated
	 */
	String getType();

	/**
	 * Sets the value of the '{@link mncModel.Alarm#getType <em>Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Type</em>' attribute.
	 * @see #getType()
	 * @generated
	 */
	void setType(String value);

	/**
	 * Returns the value of the '<em><b>Level</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Level</em>' attribute.
	 * @see #setLevel(int)
	 * @see mncModel.MncModelPackage#getAlarm_Level()
	 * @model
	 * @generated
	 */
	int getLevel();

	/**
	 * Sets the value of the '{@link mncModel.Alarm#getLevel <em>Level</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Level</em>' attribute.
	 * @see #getLevel()
	 * @generated
	 */
	void setLevel(int value);

	/**
	 * Returns the value of the '<em><b>Publish</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Publish</em>' attribute.
	 * @see #setPublish(boolean)
	 * @see mncModel.MncModelPackage#getAlarm_Publish()
	 * @model
	 * @generated
	 */
	boolean isPublish();

	/**
	 * Sets the value of the '{@link mncModel.Alarm#isPublish <em>Publish</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Publish</em>' attribute.
	 * @see #isPublish()
	 * @generated
	 */
	void setPublish(boolean value);

} // Alarm
