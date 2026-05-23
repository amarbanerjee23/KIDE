/**
 */
package mncModel;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Data Point Block</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link mncModel.DataPointBlock#getDataPoint <em>Data Point</em>}</li>
 * </ul>
 *
 * @see mncModel.MncModelPackage#getDataPointBlock()
 * @model
 * @generated
 */
public interface DataPointBlock extends BehaviorBlock {
	/**
	 * Returns the value of the '<em><b>Data Point</b></em>' reference list.
	 * The list contents are of type {@link mncModel.DataPoint}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Data Point</em>' reference list.
	 * @see mncModel.MncModelPackage#getDataPointBlock_DataPoint()
	 * @model
	 * @generated
	 */
	EList<DataPoint> getDataPoint();

} // DataPointBlock
