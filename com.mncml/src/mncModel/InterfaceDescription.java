/**
 */
package mncModel;

import mncModel.utility.OperatingStateUtility;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Interface Description</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link mncModel.InterfaceDescription#getName <em>Name</em>}</li>
 *   <li>{@link mncModel.InterfaceDescription#getIpaddress <em>Ipaddress</em>}</li>
 *   <li>{@link mncModel.InterfaceDescription#getPort <em>Port</em>}</li>
 *   <li>{@link mncModel.InterfaceDescription#getDataPoints <em>Data Points</em>}</li>
 *   <li>{@link mncModel.InterfaceDescription#getAlarms <em>Alarms</em>}</li>
 *   <li>{@link mncModel.InterfaceDescription#getCommands <em>Commands</em>}</li>
 *   <li>{@link mncModel.InterfaceDescription#getEvents <em>Events</em>}</li>
 *   <li>{@link mncModel.InterfaceDescription#getResponses <em>Responses</em>}</li>
 *   <li>{@link mncModel.InterfaceDescription#getOperatingStatesUtility <em>Operating States Utility</em>}</li>
 *   <li>{@link mncModel.InterfaceDescription#getSubscribedItems <em>Subscribed Items</em>}</li>
 *   <li>{@link mncModel.InterfaceDescription#getUses <em>Uses</em>}</li>
 * </ul>
 *
 * @see mncModel.MncModelPackage#getInterfaceDescription()
 * @model
 * @generated
 */
public interface InterfaceDescription extends mncModel.System {
	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see mncModel.MncModelPackage#getInterfaceDescription_Name()
	 * @model
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link mncModel.InterfaceDescription#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Ipaddress</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Ipaddress</em>' containment reference.
	 * @see #setIpaddress(Address)
	 * @see mncModel.MncModelPackage#getInterfaceDescription_Ipaddress()
	 * @model containment="true"
	 * @generated
	 */
	Address getIpaddress();

	/**
	 * Sets the value of the '{@link mncModel.InterfaceDescription#getIpaddress <em>Ipaddress</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Ipaddress</em>' containment reference.
	 * @see #getIpaddress()
	 * @generated
	 */
	void setIpaddress(Address value);

	/**
	 * Returns the value of the '<em><b>Port</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Port</em>' containment reference.
	 * @see #setPort(Port)
	 * @see mncModel.MncModelPackage#getInterfaceDescription_Port()
	 * @model containment="true"
	 * @generated
	 */
	Port getPort();

	/**
	 * Sets the value of the '{@link mncModel.InterfaceDescription#getPort <em>Port</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Port</em>' containment reference.
	 * @see #getPort()
	 * @generated
	 */
	void setPort(Port value);

	/**
	 * Returns the value of the '<em><b>Data Points</b></em>' containment reference list.
	 * The list contents are of type {@link mncModel.DataPoint}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Data Points</em>' containment reference list.
	 * @see mncModel.MncModelPackage#getInterfaceDescription_DataPoints()
	 * @model containment="true"
	 * @generated
	 */
	EList<DataPoint> getDataPoints();

	/**
	 * Returns the value of the '<em><b>Alarms</b></em>' containment reference list.
	 * The list contents are of type {@link mncModel.Alarm}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Alarms</em>' containment reference list.
	 * @see mncModel.MncModelPackage#getInterfaceDescription_Alarms()
	 * @model containment="true"
	 * @generated
	 */
	EList<Alarm> getAlarms();

	/**
	 * Returns the value of the '<em><b>Commands</b></em>' containment reference list.
	 * The list contents are of type {@link mncModel.Command}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Commands</em>' containment reference list.
	 * @see mncModel.MncModelPackage#getInterfaceDescription_Commands()
	 * @model containment="true"
	 * @generated
	 */
	EList<Command> getCommands();

	/**
	 * Returns the value of the '<em><b>Events</b></em>' containment reference list.
	 * The list contents are of type {@link mncModel.Event}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Events</em>' containment reference list.
	 * @see mncModel.MncModelPackage#getInterfaceDescription_Events()
	 * @model containment="true"
	 * @generated
	 */
	EList<Event> getEvents();

	/**
	 * Returns the value of the '<em><b>Responses</b></em>' containment reference list.
	 * The list contents are of type {@link mncModel.Response}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Responses</em>' containment reference list.
	 * @see mncModel.MncModelPackage#getInterfaceDescription_Responses()
	 * @model containment="true"
	 * @generated
	 */
	EList<Response> getResponses();

	/**
	 * Returns the value of the '<em><b>Operating States Utility</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Operating States Utility</em>' containment reference.
	 * @see #setOperatingStatesUtility(OperatingStateUtility)
	 * @see mncModel.MncModelPackage#getInterfaceDescription_OperatingStatesUtility()
	 * @model containment="true"
	 * @generated
	 */
	OperatingStateUtility getOperatingStatesUtility();

	/**
	 * Sets the value of the '{@link mncModel.InterfaceDescription#getOperatingStatesUtility <em>Operating States Utility</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Operating States Utility</em>' containment reference.
	 * @see #getOperatingStatesUtility()
	 * @generated
	 */
	void setOperatingStatesUtility(OperatingStateUtility value);

	/**
	 * Returns the value of the '<em><b>Subscribed Items</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Subscribed Items</em>' containment reference.
	 * @see #setSubscribedItems(SubscribableItemList)
	 * @see mncModel.MncModelPackage#getInterfaceDescription_SubscribedItems()
	 * @model containment="true"
	 * @generated
	 */
	SubscribableItemList getSubscribedItems();

	/**
	 * Sets the value of the '{@link mncModel.InterfaceDescription#getSubscribedItems <em>Subscribed Items</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Subscribed Items</em>' containment reference.
	 * @see #getSubscribedItems()
	 * @generated
	 */
	void setSubscribedItems(SubscribableItemList value);

	/**
	 * Returns the value of the '<em><b>Uses</b></em>' reference list.
	 * The list contents are of type {@link mncModel.InterfaceDescription}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Uses</em>' reference list.
	 * @see mncModel.MncModelPackage#getInterfaceDescription_Uses()
	 * @model
	 * @generated
	 */
	EList<InterfaceDescription> getUses();

} // InterfaceDescription
