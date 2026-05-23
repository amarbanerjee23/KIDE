/**
 */
package mncModel;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Action Alarm</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link mncModel.ActionAlarm#getAlarm <em>Alarm</em>}</li>
 * </ul>
 *
 * @see mncModel.MncModelPackage#getActionAlarm()
 * @model
 * @generated
 */
public interface ActionAlarm extends ActionItem {
	/**
	 * Returns the value of the '<em><b>Alarm</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Alarm</em>' reference.
	 * @see #setAlarm(Alarm)
	 * @see mncModel.MncModelPackage#getActionAlarm_Alarm()
	 * @model
	 * @generated
	 */
	Alarm getAlarm();

	/**
	 * Sets the value of the '{@link mncModel.ActionAlarm#getAlarm <em>Alarm</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Alarm</em>' reference.
	 * @see #getAlarm()
	 * @generated
	 */
	void setAlarm(Alarm value);

} // ActionAlarm
