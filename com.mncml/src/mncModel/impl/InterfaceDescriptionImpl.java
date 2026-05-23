/**
 */
package mncModel.impl;

import java.util.Collection;

import mncModel.Address;
import mncModel.Alarm;
import mncModel.Command;
import mncModel.DataPoint;
import mncModel.Event;
import mncModel.InterfaceDescription;
import mncModel.MncModelPackage;
import mncModel.Port;
import mncModel.Response;
import mncModel.SubscribableItemList;

import mncModel.utility.OperatingStateUtility;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Interface Description</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link mncModel.impl.InterfaceDescriptionImpl#getName <em>Name</em>}</li>
 *   <li>{@link mncModel.impl.InterfaceDescriptionImpl#getIpaddress <em>Ipaddress</em>}</li>
 *   <li>{@link mncModel.impl.InterfaceDescriptionImpl#getPort <em>Port</em>}</li>
 *   <li>{@link mncModel.impl.InterfaceDescriptionImpl#getDataPoints <em>Data Points</em>}</li>
 *   <li>{@link mncModel.impl.InterfaceDescriptionImpl#getAlarms <em>Alarms</em>}</li>
 *   <li>{@link mncModel.impl.InterfaceDescriptionImpl#getCommands <em>Commands</em>}</li>
 *   <li>{@link mncModel.impl.InterfaceDescriptionImpl#getEvents <em>Events</em>}</li>
 *   <li>{@link mncModel.impl.InterfaceDescriptionImpl#getResponses <em>Responses</em>}</li>
 *   <li>{@link mncModel.impl.InterfaceDescriptionImpl#getOperatingStatesUtility <em>Operating States Utility</em>}</li>
 *   <li>{@link mncModel.impl.InterfaceDescriptionImpl#getSubscribedItems <em>Subscribed Items</em>}</li>
 *   <li>{@link mncModel.impl.InterfaceDescriptionImpl#getUses <em>Uses</em>}</li>
 * </ul>
 *
 * @generated
 */
public class InterfaceDescriptionImpl extends SystemImpl implements InterfaceDescription {
	/**
	 * The default value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected static final String NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected String name = NAME_EDEFAULT;

	/**
	 * The cached value of the '{@link #getIpaddress() <em>Ipaddress</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIpaddress()
	 * @generated
	 * @ordered
	 */
	protected Address ipaddress;

	/**
	 * The cached value of the '{@link #getPort() <em>Port</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPort()
	 * @generated
	 * @ordered
	 */
	protected Port port;

	/**
	 * The cached value of the '{@link #getDataPoints() <em>Data Points</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDataPoints()
	 * @generated
	 * @ordered
	 */
	protected EList<DataPoint> dataPoints;

	/**
	 * The cached value of the '{@link #getAlarms() <em>Alarms</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAlarms()
	 * @generated
	 * @ordered
	 */
	protected EList<Alarm> alarms;

	/**
	 * The cached value of the '{@link #getCommands() <em>Commands</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCommands()
	 * @generated
	 * @ordered
	 */
	protected EList<Command> commands;

	/**
	 * The cached value of the '{@link #getEvents() <em>Events</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEvents()
	 * @generated
	 * @ordered
	 */
	protected EList<Event> events;

	/**
	 * The cached value of the '{@link #getResponses() <em>Responses</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getResponses()
	 * @generated
	 * @ordered
	 */
	protected EList<Response> responses;

	/**
	 * The cached value of the '{@link #getOperatingStatesUtility() <em>Operating States Utility</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOperatingStatesUtility()
	 * @generated
	 * @ordered
	 */
	protected OperatingStateUtility operatingStatesUtility;

	/**
	 * The cached value of the '{@link #getSubscribedItems() <em>Subscribed Items</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSubscribedItems()
	 * @generated
	 * @ordered
	 */
	protected SubscribableItemList subscribedItems;

	/**
	 * The cached value of the '{@link #getUses() <em>Uses</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getUses()
	 * @generated
	 * @ordered
	 */
	protected EList<InterfaceDescription> uses;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected InterfaceDescriptionImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MncModelPackage.Literals.INTERFACE_DESCRIPTION;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setName(String newName) {
		String oldName = name;
		name = newName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MncModelPackage.INTERFACE_DESCRIPTION__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Address getIpaddress() {
		return ipaddress;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetIpaddress(Address newIpaddress, NotificationChain msgs) {
		Address oldIpaddress = ipaddress;
		ipaddress = newIpaddress;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, MncModelPackage.INTERFACE_DESCRIPTION__IPADDRESS, oldIpaddress, newIpaddress);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setIpaddress(Address newIpaddress) {
		if (newIpaddress != ipaddress) {
			NotificationChain msgs = null;
			if (ipaddress != null)
				msgs = ((InternalEObject)ipaddress).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - MncModelPackage.INTERFACE_DESCRIPTION__IPADDRESS, null, msgs);
			if (newIpaddress != null)
				msgs = ((InternalEObject)newIpaddress).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - MncModelPackage.INTERFACE_DESCRIPTION__IPADDRESS, null, msgs);
			msgs = basicSetIpaddress(newIpaddress, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MncModelPackage.INTERFACE_DESCRIPTION__IPADDRESS, newIpaddress, newIpaddress));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Port getPort() {
		return port;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetPort(Port newPort, NotificationChain msgs) {
		Port oldPort = port;
		port = newPort;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, MncModelPackage.INTERFACE_DESCRIPTION__PORT, oldPort, newPort);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setPort(Port newPort) {
		if (newPort != port) {
			NotificationChain msgs = null;
			if (port != null)
				msgs = ((InternalEObject)port).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - MncModelPackage.INTERFACE_DESCRIPTION__PORT, null, msgs);
			if (newPort != null)
				msgs = ((InternalEObject)newPort).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - MncModelPackage.INTERFACE_DESCRIPTION__PORT, null, msgs);
			msgs = basicSetPort(newPort, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MncModelPackage.INTERFACE_DESCRIPTION__PORT, newPort, newPort));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<DataPoint> getDataPoints() {
		if (dataPoints == null) {
			dataPoints = new EObjectContainmentEList<DataPoint>(DataPoint.class, this, MncModelPackage.INTERFACE_DESCRIPTION__DATA_POINTS);
		}
		return dataPoints;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<Alarm> getAlarms() {
		if (alarms == null) {
			alarms = new EObjectContainmentEList<Alarm>(Alarm.class, this, MncModelPackage.INTERFACE_DESCRIPTION__ALARMS);
		}
		return alarms;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<Command> getCommands() {
		if (commands == null) {
			commands = new EObjectContainmentEList<Command>(Command.class, this, MncModelPackage.INTERFACE_DESCRIPTION__COMMANDS);
		}
		return commands;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<Event> getEvents() {
		if (events == null) {
			events = new EObjectContainmentEList<Event>(Event.class, this, MncModelPackage.INTERFACE_DESCRIPTION__EVENTS);
		}
		return events;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<Response> getResponses() {
		if (responses == null) {
			responses = new EObjectContainmentEList<Response>(Response.class, this, MncModelPackage.INTERFACE_DESCRIPTION__RESPONSES);
		}
		return responses;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public OperatingStateUtility getOperatingStatesUtility() {
		return operatingStatesUtility;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetOperatingStatesUtility(OperatingStateUtility newOperatingStatesUtility, NotificationChain msgs) {
		OperatingStateUtility oldOperatingStatesUtility = operatingStatesUtility;
		operatingStatesUtility = newOperatingStatesUtility;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, MncModelPackage.INTERFACE_DESCRIPTION__OPERATING_STATES_UTILITY, oldOperatingStatesUtility, newOperatingStatesUtility);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setOperatingStatesUtility(OperatingStateUtility newOperatingStatesUtility) {
		if (newOperatingStatesUtility != operatingStatesUtility) {
			NotificationChain msgs = null;
			if (operatingStatesUtility != null)
				msgs = ((InternalEObject)operatingStatesUtility).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - MncModelPackage.INTERFACE_DESCRIPTION__OPERATING_STATES_UTILITY, null, msgs);
			if (newOperatingStatesUtility != null)
				msgs = ((InternalEObject)newOperatingStatesUtility).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - MncModelPackage.INTERFACE_DESCRIPTION__OPERATING_STATES_UTILITY, null, msgs);
			msgs = basicSetOperatingStatesUtility(newOperatingStatesUtility, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MncModelPackage.INTERFACE_DESCRIPTION__OPERATING_STATES_UTILITY, newOperatingStatesUtility, newOperatingStatesUtility));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public SubscribableItemList getSubscribedItems() {
		return subscribedItems;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetSubscribedItems(SubscribableItemList newSubscribedItems, NotificationChain msgs) {
		SubscribableItemList oldSubscribedItems = subscribedItems;
		subscribedItems = newSubscribedItems;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, MncModelPackage.INTERFACE_DESCRIPTION__SUBSCRIBED_ITEMS, oldSubscribedItems, newSubscribedItems);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setSubscribedItems(SubscribableItemList newSubscribedItems) {
		if (newSubscribedItems != subscribedItems) {
			NotificationChain msgs = null;
			if (subscribedItems != null)
				msgs = ((InternalEObject)subscribedItems).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - MncModelPackage.INTERFACE_DESCRIPTION__SUBSCRIBED_ITEMS, null, msgs);
			if (newSubscribedItems != null)
				msgs = ((InternalEObject)newSubscribedItems).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - MncModelPackage.INTERFACE_DESCRIPTION__SUBSCRIBED_ITEMS, null, msgs);
			msgs = basicSetSubscribedItems(newSubscribedItems, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MncModelPackage.INTERFACE_DESCRIPTION__SUBSCRIBED_ITEMS, newSubscribedItems, newSubscribedItems));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<InterfaceDescription> getUses() {
		if (uses == null) {
			uses = new EObjectResolvingEList<InterfaceDescription>(InterfaceDescription.class, this, MncModelPackage.INTERFACE_DESCRIPTION__USES);
		}
		return uses;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case MncModelPackage.INTERFACE_DESCRIPTION__IPADDRESS:
				return basicSetIpaddress(null, msgs);
			case MncModelPackage.INTERFACE_DESCRIPTION__PORT:
				return basicSetPort(null, msgs);
			case MncModelPackage.INTERFACE_DESCRIPTION__DATA_POINTS:
				return ((InternalEList<?>)getDataPoints()).basicRemove(otherEnd, msgs);
			case MncModelPackage.INTERFACE_DESCRIPTION__ALARMS:
				return ((InternalEList<?>)getAlarms()).basicRemove(otherEnd, msgs);
			case MncModelPackage.INTERFACE_DESCRIPTION__COMMANDS:
				return ((InternalEList<?>)getCommands()).basicRemove(otherEnd, msgs);
			case MncModelPackage.INTERFACE_DESCRIPTION__EVENTS:
				return ((InternalEList<?>)getEvents()).basicRemove(otherEnd, msgs);
			case MncModelPackage.INTERFACE_DESCRIPTION__RESPONSES:
				return ((InternalEList<?>)getResponses()).basicRemove(otherEnd, msgs);
			case MncModelPackage.INTERFACE_DESCRIPTION__OPERATING_STATES_UTILITY:
				return basicSetOperatingStatesUtility(null, msgs);
			case MncModelPackage.INTERFACE_DESCRIPTION__SUBSCRIBED_ITEMS:
				return basicSetSubscribedItems(null, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case MncModelPackage.INTERFACE_DESCRIPTION__NAME:
				return getName();
			case MncModelPackage.INTERFACE_DESCRIPTION__IPADDRESS:
				return getIpaddress();
			case MncModelPackage.INTERFACE_DESCRIPTION__PORT:
				return getPort();
			case MncModelPackage.INTERFACE_DESCRIPTION__DATA_POINTS:
				return getDataPoints();
			case MncModelPackage.INTERFACE_DESCRIPTION__ALARMS:
				return getAlarms();
			case MncModelPackage.INTERFACE_DESCRIPTION__COMMANDS:
				return getCommands();
			case MncModelPackage.INTERFACE_DESCRIPTION__EVENTS:
				return getEvents();
			case MncModelPackage.INTERFACE_DESCRIPTION__RESPONSES:
				return getResponses();
			case MncModelPackage.INTERFACE_DESCRIPTION__OPERATING_STATES_UTILITY:
				return getOperatingStatesUtility();
			case MncModelPackage.INTERFACE_DESCRIPTION__SUBSCRIBED_ITEMS:
				return getSubscribedItems();
			case MncModelPackage.INTERFACE_DESCRIPTION__USES:
				return getUses();
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
			case MncModelPackage.INTERFACE_DESCRIPTION__NAME:
				setName((String)newValue);
				return;
			case MncModelPackage.INTERFACE_DESCRIPTION__IPADDRESS:
				setIpaddress((Address)newValue);
				return;
			case MncModelPackage.INTERFACE_DESCRIPTION__PORT:
				setPort((Port)newValue);
				return;
			case MncModelPackage.INTERFACE_DESCRIPTION__DATA_POINTS:
				getDataPoints().clear();
				getDataPoints().addAll((Collection<? extends DataPoint>)newValue);
				return;
			case MncModelPackage.INTERFACE_DESCRIPTION__ALARMS:
				getAlarms().clear();
				getAlarms().addAll((Collection<? extends Alarm>)newValue);
				return;
			case MncModelPackage.INTERFACE_DESCRIPTION__COMMANDS:
				getCommands().clear();
				getCommands().addAll((Collection<? extends Command>)newValue);
				return;
			case MncModelPackage.INTERFACE_DESCRIPTION__EVENTS:
				getEvents().clear();
				getEvents().addAll((Collection<? extends Event>)newValue);
				return;
			case MncModelPackage.INTERFACE_DESCRIPTION__RESPONSES:
				getResponses().clear();
				getResponses().addAll((Collection<? extends Response>)newValue);
				return;
			case MncModelPackage.INTERFACE_DESCRIPTION__OPERATING_STATES_UTILITY:
				setOperatingStatesUtility((OperatingStateUtility)newValue);
				return;
			case MncModelPackage.INTERFACE_DESCRIPTION__SUBSCRIBED_ITEMS:
				setSubscribedItems((SubscribableItemList)newValue);
				return;
			case MncModelPackage.INTERFACE_DESCRIPTION__USES:
				getUses().clear();
				getUses().addAll((Collection<? extends InterfaceDescription>)newValue);
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
			case MncModelPackage.INTERFACE_DESCRIPTION__NAME:
				setName(NAME_EDEFAULT);
				return;
			case MncModelPackage.INTERFACE_DESCRIPTION__IPADDRESS:
				setIpaddress((Address)null);
				return;
			case MncModelPackage.INTERFACE_DESCRIPTION__PORT:
				setPort((Port)null);
				return;
			case MncModelPackage.INTERFACE_DESCRIPTION__DATA_POINTS:
				getDataPoints().clear();
				return;
			case MncModelPackage.INTERFACE_DESCRIPTION__ALARMS:
				getAlarms().clear();
				return;
			case MncModelPackage.INTERFACE_DESCRIPTION__COMMANDS:
				getCommands().clear();
				return;
			case MncModelPackage.INTERFACE_DESCRIPTION__EVENTS:
				getEvents().clear();
				return;
			case MncModelPackage.INTERFACE_DESCRIPTION__RESPONSES:
				getResponses().clear();
				return;
			case MncModelPackage.INTERFACE_DESCRIPTION__OPERATING_STATES_UTILITY:
				setOperatingStatesUtility((OperatingStateUtility)null);
				return;
			case MncModelPackage.INTERFACE_DESCRIPTION__SUBSCRIBED_ITEMS:
				setSubscribedItems((SubscribableItemList)null);
				return;
			case MncModelPackage.INTERFACE_DESCRIPTION__USES:
				getUses().clear();
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
			case MncModelPackage.INTERFACE_DESCRIPTION__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case MncModelPackage.INTERFACE_DESCRIPTION__IPADDRESS:
				return ipaddress != null;
			case MncModelPackage.INTERFACE_DESCRIPTION__PORT:
				return port != null;
			case MncModelPackage.INTERFACE_DESCRIPTION__DATA_POINTS:
				return dataPoints != null && !dataPoints.isEmpty();
			case MncModelPackage.INTERFACE_DESCRIPTION__ALARMS:
				return alarms != null && !alarms.isEmpty();
			case MncModelPackage.INTERFACE_DESCRIPTION__COMMANDS:
				return commands != null && !commands.isEmpty();
			case MncModelPackage.INTERFACE_DESCRIPTION__EVENTS:
				return events != null && !events.isEmpty();
			case MncModelPackage.INTERFACE_DESCRIPTION__RESPONSES:
				return responses != null && !responses.isEmpty();
			case MncModelPackage.INTERFACE_DESCRIPTION__OPERATING_STATES_UTILITY:
				return operatingStatesUtility != null;
			case MncModelPackage.INTERFACE_DESCRIPTION__SUBSCRIBED_ITEMS:
				return subscribedItems != null;
			case MncModelPackage.INTERFACE_DESCRIPTION__USES:
				return uses != null && !uses.isEmpty();
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuilder result = new StringBuilder(super.toString());
		result.append(" (name: ");
		result.append(name);
		result.append(')');
		return result.toString();
	}

} //InterfaceDescriptionImpl
