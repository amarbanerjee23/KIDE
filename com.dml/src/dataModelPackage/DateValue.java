/**
 */
package dataModelPackage;

import java.util.Date;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Date Value</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link dataModelPackage.DateValue#getDateValue <em>Date Value</em>}</li>
 * </ul>
 *
 * @see dataModelPackage.DataModelPackage#getDateValue()
 * @model
 * @generated
 */
public interface DateValue extends PrimitiveValue {
	/**
	 * Returns the value of the '<em><b>Date Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Date Value</em>' attribute.
	 * @see #setDateValue(Date)
	 * @see dataModelPackage.DataModelPackage#getDateValue_DateValue()
	 * @model
	 * @generated
	 */
	Date getDateValue();

	/**
	 * Sets the value of the '{@link dataModelPackage.DateValue#getDateValue <em>Date Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Date Value</em>' attribute.
	 * @see #getDateValue()
	 * @generated
	 */
	void setDateValue(Date value);

} // DateValue
