/**
 */
package mncModel;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Alarm Block</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link mncModel.AlarmBlock#getAlarm <em>Alarm</em>}</li>
 * </ul>
 *
 * @see mncModel.MncModelPackage#getAlarmBlock()
 * @model
 * @generated
 */
public interface AlarmBlock extends BehaviorBlock {
	/**
	 * Returns the value of the '<em><b>Alarm</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Alarm</em>' reference.
	 * @see #setAlarm(Alarm)
	 * @see mncModel.MncModelPackage#getAlarmBlock_Alarm()
	 * @model
	 * @generated
	 */
	Alarm getAlarm();

	/**
	 * Sets the value of the '{@link mncModel.AlarmBlock#getAlarm <em>Alarm</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Alarm</em>' reference.
	 * @see #getAlarm()
	 * @generated
	 */
	void setAlarm(Alarm value);

} // AlarmBlock
