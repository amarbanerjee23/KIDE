/**
 */
package mncModel;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Subscribable Item List</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link mncModel.SubscribableItemList#getSubscribedEvents <em>Subscribed Events</em>}</li>
 *   <li>{@link mncModel.SubscribableItemList#getSubscribedAlarms <em>Subscribed Alarms</em>}</li>
 *   <li>{@link mncModel.SubscribableItemList#getSubscribedDataPoints <em>Subscribed Data Points</em>}</li>
 * </ul>
 *
 * @see mncModel.MncModelPackage#getSubscribableItemList()
 * @model
 * @generated
 */
public interface SubscribableItemList extends EObject {
	/**
	 * Returns the value of the '<em><b>Subscribed Events</b></em>' reference list.
	 * The list contents are of type {@link mncModel.Event}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Subscribed Events</em>' reference list.
	 * @see mncModel.MncModelPackage#getSubscribableItemList_SubscribedEvents()
	 * @model
	 * @generated
	 */
	EList<Event> getSubscribedEvents();

	/**
	 * Returns the value of the '<em><b>Subscribed Alarms</b></em>' reference list.
	 * The list contents are of type {@link mncModel.Alarm}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Subscribed Alarms</em>' reference list.
	 * @see mncModel.MncModelPackage#getSubscribableItemList_SubscribedAlarms()
	 * @model
	 * @generated
	 */
	EList<Alarm> getSubscribedAlarms();

	/**
	 * Returns the value of the '<em><b>Subscribed Data Points</b></em>' reference list.
	 * The list contents are of type {@link mncModel.DataPoint}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Subscribed Data Points</em>' reference list.
	 * @see mncModel.MncModelPackage#getSubscribableItemList_SubscribedDataPoints()
	 * @model
	 * @generated
	 */
	EList<DataPoint> getSubscribedDataPoints();

} // SubscribableItemList
