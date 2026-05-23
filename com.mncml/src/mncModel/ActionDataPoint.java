/**
 */
package mncModel;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Action Data Point</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link mncModel.ActionDataPoint#getDataPoint <em>Data Point</em>}</li>
 * </ul>
 *
 * @see mncModel.MncModelPackage#getActionDataPoint()
 * @model
 * @generated
 */
public interface ActionDataPoint extends ActionItem {
	/**
	 * Returns the value of the '<em><b>Data Point</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Data Point</em>' reference.
	 * @see #setDataPoint(DataPoint)
	 * @see mncModel.MncModelPackage#getActionDataPoint_DataPoint()
	 * @model
	 * @generated
	 */
	DataPoint getDataPoint();

	/**
	 * Sets the value of the '{@link mncModel.ActionDataPoint#getDataPoint <em>Data Point</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Data Point</em>' reference.
	 * @see #getDataPoint()
	 * @generated
	 */
	void setDataPoint(DataPoint value);

} // ActionDataPoint
