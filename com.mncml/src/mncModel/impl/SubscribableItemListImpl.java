/**
 */
package mncModel.impl;

import java.util.Collection;

import mncModel.Alarm;
import mncModel.DataPoint;
import mncModel.Event;
import mncModel.MncModelPackage;
import mncModel.SubscribableItemList;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectResolvingEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Subscribable Item List</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link mncModel.impl.SubscribableItemListImpl#getSubscribedEvents <em>Subscribed Events</em>}</li>
 *   <li>{@link mncModel.impl.SubscribableItemListImpl#getSubscribedAlarms <em>Subscribed Alarms</em>}</li>
 *   <li>{@link mncModel.impl.SubscribableItemListImpl#getSubscribedDataPoints <em>Subscribed Data Points</em>}</li>
 * </ul>
 *
 * @generated
 */
public class SubscribableItemListImpl extends MinimalEObjectImpl.Container implements SubscribableItemList {
	/**
	 * The cached value of the '{@link #getSubscribedEvents() <em>Subscribed Events</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSubscribedEvents()
	 * @generated
	 * @ordered
	 */
	protected EList<Event> subscribedEvents;

	/**
	 * The cached value of the '{@link #getSubscribedAlarms() <em>Subscribed Alarms</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSubscribedAlarms()
	 * @generated
	 * @ordered
	 */
	protected EList<Alarm> subscribedAlarms;

	/**
	 * The cached value of the '{@link #getSubscribedDataPoints() <em>Subscribed Data Points</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSubscribedDataPoints()
	 * @generated
	 * @ordered
	 */
	protected EList<DataPoint> subscribedDataPoints;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected SubscribableItemListImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MncModelPackage.Literals.SUBSCRIBABLE_ITEM_LIST;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<Event> getSubscribedEvents() {
		if (subscribedEvents == null) {
			subscribedEvents = new EObjectResolvingEList<Event>(Event.class, this, MncModelPackage.SUBSCRIBABLE_ITEM_LIST__SUBSCRIBED_EVENTS);
		}
		return subscribedEvents;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<Alarm> getSubscribedAlarms() {
		if (subscribedAlarms == null) {
			subscribedAlarms = new EObjectResolvingEList<Alarm>(Alarm.class, this, MncModelPackage.SUBSCRIBABLE_ITEM_LIST__SUBSCRIBED_ALARMS);
		}
		return subscribedAlarms;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<DataPoint> getSubscribedDataPoints() {
		if (subscribedDataPoints == null) {
			subscribedDataPoints = new EObjectResolvingEList<DataPoint>(DataPoint.class, this, MncModelPackage.SUBSCRIBABLE_ITEM_LIST__SUBSCRIBED_DATA_POINTS);
		}
		return subscribedDataPoints;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case MncModelPackage.SUBSCRIBABLE_ITEM_LIST__SUBSCRIBED_EVENTS:
				return getSubscribedEvents();
			case MncModelPackage.SUBSCRIBABLE_ITEM_LIST__SUBSCRIBED_ALARMS:
				return getSubscribedAlarms();
			case MncModelPackage.SUBSCRIBABLE_ITEM_LIST__SUBSCRIBED_DATA_POINTS:
				return getSubscribedDataPoints();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case MncModelPackage.SUBSCRIBABLE_ITEM_LIST__SUBSCRIBED_EVENTS:
				getSubscribedEvents().clear();
				getSubscribedEvents().addAll((Collection<? extends Event>)newValue);
				return;
			case MncModelPackage.SUBSCRIBABLE_ITEM_LIST__SUBSCRIBED_ALARMS:
				getSubscribedAlarms().clear();
				getSubscribedAlarms().addAll((Collection<? extends Alarm>)newValue);
				return;
			case MncModelPackage.SUBSCRIBABLE_ITEM_LIST__SUBSCRIBED_DATA_POINTS:
				getSubscribedDataPoints().clear();
				getSubscribedDataPoints().addAll((Collection<? extends DataPoint>)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case MncModelPackage.SUBSCRIBABLE_ITEM_LIST__SUBSCRIBED_EVENTS:
				getSubscribedEvents().clear();
				return;
			case MncModelPackage.SUBSCRIBABLE_ITEM_LIST__SUBSCRIBED_ALARMS:
				getSubscribedAlarms().clear();
				return;
			case MncModelPackage.SUBSCRIBABLE_ITEM_LIST__SUBSCRIBED_DATA_POINTS:
				getSubscribedDataPoints().clear();
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case MncModelPackage.SUBSCRIBABLE_ITEM_LIST__SUBSCRIBED_EVENTS:
				return subscribedEvents != null && !subscribedEvents.isEmpty();
			case MncModelPackage.SUBSCRIBABLE_ITEM_LIST__SUBSCRIBED_ALARMS:
				return subscribedAlarms != null && !subscribedAlarms.isEmpty();
			case MncModelPackage.SUBSCRIBABLE_ITEM_LIST__SUBSCRIBED_DATA_POINTS:
				return subscribedDataPoints != null && !subscribedDataPoints.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //SubscribableItemListImpl
