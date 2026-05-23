/**
 */
package dataModelPackage;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Array Values</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link dataModelPackage.ArrayValues#getValues <em>Values</em>}</li>
 * </ul>
 *
 * @see dataModelPackage.DataModelPackage#getArrayValues()
 * @model
 * @generated
 */
public interface ArrayValues extends PrimitiveValue {
	/**
	 * Returns the value of the '<em><b>Values</b></em>' containment reference list.
	 * The list contents are of type {@link dataModelPackage.PrimitiveValue}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Values</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Values</em>' containment reference list.
	 * @see dataModelPackage.DataModelPackage#getArrayValues_Values()
	 * @model containment="true"
	 * @generated
	 */
	EList<PrimitiveValue> getValues();

} // ArrayValues
