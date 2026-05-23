/**
 */
package mncModel;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Address</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link mncModel.Address#getIpaddress <em>Ipaddress</em>}</li>
 * </ul>
 *
 * @see mncModel.MncModelPackage#getAddress()
 * @model
 * @generated
 */
public interface Address extends EObject {
	/**
	 * Returns the value of the '<em><b>Ipaddress</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Ipaddress</em>' attribute.
	 * @see #setIpaddress(String)
	 * @see mncModel.MncModelPackage#getAddress_Ipaddress()
	 * @model
	 * @generated
	 */
	String getIpaddress();

	/**
	 * Sets the value of the '{@link mncModel.Address#getIpaddress <em>Ipaddress</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Ipaddress</em>' attribute.
	 * @see #getIpaddress()
	 * @generated
	 */
	void setIpaddress(String value);

} // Address
