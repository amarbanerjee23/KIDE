/**
 */
package mncModel.impl;

import java.util.Collection;

import mncModel.AlarmBlock;
import mncModel.CommandResponseBlock;
import mncModel.ControlNode;
import mncModel.DataPointBlock;
import mncModel.EventBlock;
import mncModel.InterfaceDescription;
import mncModel.MncModelPackage;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.EObjectWithInverseEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Control Node</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link mncModel.impl.ControlNodeImpl#getName <em>Name</em>}</li>
 *   <li>{@link mncModel.impl.ControlNodeImpl#getInterfaceDescription <em>Interface Description</em>}</li>
 *   <li>{@link mncModel.impl.ControlNodeImpl#getChildNodes <em>Child Nodes</em>}</li>
 *   <li>{@link mncModel.impl.ControlNodeImpl#getParentNode <em>Parent Node</em>}</li>
 *   <li>{@link mncModel.impl.ControlNodeImpl#getCommandResponseBlocks <em>Command Response Blocks</em>}</li>
 *   <li>{@link mncModel.impl.ControlNodeImpl#getEventBlocks <em>Event Blocks</em>}</li>
 *   <li>{@link mncModel.impl.ControlNodeImpl#getAlarmBlocks <em>Alarm Blocks</em>}</li>
 *   <li>{@link mncModel.impl.ControlNodeImpl#getDataPointBlocks <em>Data Point Blocks</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ControlNodeImpl extends SystemImpl implements ControlNode {
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
	 * The cached value of the '{@link #getInterfaceDescription() <em>Interface Description</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getInterfaceDescription()
	 * @generated
	 * @ordered
	 */
	protected InterfaceDescription interfaceDescription;

	/**
	 * The cached value of the '{@link #getChildNodes() <em>Child Nodes</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getChildNodes()
	 * @generated
	 * @ordered
	 */
	protected EList<ControlNode> childNodes;

	/**
	 * The cached value of the '{@link #getParentNode() <em>Parent Node</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getParentNode()
	 * @generated
	 * @ordered
	 */
	protected ControlNode parentNode;

	/**
	 * The cached value of the '{@link #getCommandResponseBlocks() <em>Command Response Blocks</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCommandResponseBlocks()
	 * @generated
	 * @ordered
	 */
	protected EList<CommandResponseBlock> commandResponseBlocks;

	/**
	 * The cached value of the '{@link #getEventBlocks() <em>Event Blocks</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEventBlocks()
	 * @generated
	 * @ordered
	 */
	protected EList<EventBlock> eventBlocks;

	/**
	 * The cached value of the '{@link #getAlarmBlocks() <em>Alarm Blocks</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAlarmBlocks()
	 * @generated
	 * @ordered
	 */
	protected EList<AlarmBlock> alarmBlocks;

	/**
	 * The cached value of the '{@link #getDataPointBlocks() <em>Data Point Blocks</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDataPointBlocks()
	 * @generated
	 * @ordered
	 */
	protected EList<DataPointBlock> dataPointBlocks;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ControlNodeImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MncModelPackage.Literals.CONTROL_NODE;
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
			eNotify(new ENotificationImpl(this, Notification.SET, MncModelPackage.CONTROL_NODE__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public InterfaceDescription getInterfaceDescription() {
		if (interfaceDescription != null && interfaceDescription.eIsProxy()) {
			InternalEObject oldInterfaceDescription = (InternalEObject)interfaceDescription;
			interfaceDescription = (InterfaceDescription)eResolveProxy(oldInterfaceDescription);
			if (interfaceDescription != oldInterfaceDescription) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, MncModelPackage.CONTROL_NODE__INTERFACE_DESCRIPTION, oldInterfaceDescription, interfaceDescription));
			}
		}
		return interfaceDescription;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public InterfaceDescription basicGetInterfaceDescription() {
		return interfaceDescription;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setInterfaceDescription(InterfaceDescription newInterfaceDescription) {
		InterfaceDescription oldInterfaceDescription = interfaceDescription;
		interfaceDescription = newInterfaceDescription;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MncModelPackage.CONTROL_NODE__INTERFACE_DESCRIPTION, oldInterfaceDescription, interfaceDescription));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<ControlNode> getChildNodes() {
		if (childNodes == null) {
			childNodes = new EObjectWithInverseEList<ControlNode>(ControlNode.class, this, MncModelPackage.CONTROL_NODE__CHILD_NODES, MncModelPackage.CONTROL_NODE__PARENT_NODE);
		}
		return childNodes;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ControlNode getParentNode() {
		if (parentNode != null && parentNode.eIsProxy()) {
			InternalEObject oldParentNode = (InternalEObject)parentNode;
			parentNode = (ControlNode)eResolveProxy(oldParentNode);
			if (parentNode != oldParentNode) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, MncModelPackage.CONTROL_NODE__PARENT_NODE, oldParentNode, parentNode));
			}
		}
		return parentNode;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ControlNode basicGetParentNode() {
		return parentNode;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetParentNode(ControlNode newParentNode, NotificationChain msgs) {
		ControlNode oldParentNode = parentNode;
		parentNode = newParentNode;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, MncModelPackage.CONTROL_NODE__PARENT_NODE, oldParentNode, newParentNode);
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
	public void setParentNode(ControlNode newParentNode) {
		if (newParentNode != parentNode) {
			NotificationChain msgs = null;
			if (parentNode != null)
				msgs = ((InternalEObject)parentNode).eInverseRemove(this, MncModelPackage.CONTROL_NODE__CHILD_NODES, ControlNode.class, msgs);
			if (newParentNode != null)
				msgs = ((InternalEObject)newParentNode).eInverseAdd(this, MncModelPackage.CONTROL_NODE__CHILD_NODES, ControlNode.class, msgs);
			msgs = basicSetParentNode(newParentNode, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MncModelPackage.CONTROL_NODE__PARENT_NODE, newParentNode, newParentNode));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<CommandResponseBlock> getCommandResponseBlocks() {
		if (commandResponseBlocks == null) {
			commandResponseBlocks = new EObjectContainmentEList<CommandResponseBlock>(CommandResponseBlock.class, this, MncModelPackage.CONTROL_NODE__COMMAND_RESPONSE_BLOCKS);
		}
		return commandResponseBlocks;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<EventBlock> getEventBlocks() {
		if (eventBlocks == null) {
			eventBlocks = new EObjectContainmentEList<EventBlock>(EventBlock.class, this, MncModelPackage.CONTROL_NODE__EVENT_BLOCKS);
		}
		return eventBlocks;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<AlarmBlock> getAlarmBlocks() {
		if (alarmBlocks == null) {
			alarmBlocks = new EObjectContainmentEList<AlarmBlock>(AlarmBlock.class, this, MncModelPackage.CONTROL_NODE__ALARM_BLOCKS);
		}
		return alarmBlocks;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<DataPointBlock> getDataPointBlocks() {
		if (dataPointBlocks == null) {
			dataPointBlocks = new EObjectContainmentEList<DataPointBlock>(DataPointBlock.class, this, MncModelPackage.CONTROL_NODE__DATA_POINT_BLOCKS);
		}
		return dataPointBlocks;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case MncModelPackage.CONTROL_NODE__CHILD_NODES:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getChildNodes()).basicAdd(otherEnd, msgs);
			case MncModelPackage.CONTROL_NODE__PARENT_NODE:
				if (parentNode != null)
					msgs = ((InternalEObject)parentNode).eInverseRemove(this, MncModelPackage.CONTROL_NODE__CHILD_NODES, ControlNode.class, msgs);
				return basicSetParentNode((ControlNode)otherEnd, msgs);
		}
		return super.eInverseAdd(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case MncModelPackage.CONTROL_NODE__CHILD_NODES:
				return ((InternalEList<?>)getChildNodes()).basicRemove(otherEnd, msgs);
			case MncModelPackage.CONTROL_NODE__PARENT_NODE:
				return basicSetParentNode(null, msgs);
			case MncModelPackage.CONTROL_NODE__COMMAND_RESPONSE_BLOCKS:
				return ((InternalEList<?>)getCommandResponseBlocks()).basicRemove(otherEnd, msgs);
			case MncModelPackage.CONTROL_NODE__EVENT_BLOCKS:
				return ((InternalEList<?>)getEventBlocks()).basicRemove(otherEnd, msgs);
			case MncModelPackage.CONTROL_NODE__ALARM_BLOCKS:
				return ((InternalEList<?>)getAlarmBlocks()).basicRemove(otherEnd, msgs);
			case MncModelPackage.CONTROL_NODE__DATA_POINT_BLOCKS:
				return ((InternalEList<?>)getDataPointBlocks()).basicRemove(otherEnd, msgs);
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
			case MncModelPackage.CONTROL_NODE__NAME:
				return getName();
			case MncModelPackage.CONTROL_NODE__INTERFACE_DESCRIPTION:
				if (resolve) return getInterfaceDescription();
				return basicGetInterfaceDescription();
			case MncModelPackage.CONTROL_NODE__CHILD_NODES:
				return getChildNodes();
			case MncModelPackage.CONTROL_NODE__PARENT_NODE:
				if (resolve) return getParentNode();
				return basicGetParentNode();
			case MncModelPackage.CONTROL_NODE__COMMAND_RESPONSE_BLOCKS:
				return getCommandResponseBlocks();
			case MncModelPackage.CONTROL_NODE__EVENT_BLOCKS:
				return getEventBlocks();
			case MncModelPackage.CONTROL_NODE__ALARM_BLOCKS:
				return getAlarmBlocks();
			case MncModelPackage.CONTROL_NODE__DATA_POINT_BLOCKS:
				return getDataPointBlocks();
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
			case MncModelPackage.CONTROL_NODE__NAME:
				setName((String)newValue);
				return;
			case MncModelPackage.CONTROL_NODE__INTERFACE_DESCRIPTION:
				setInterfaceDescription((InterfaceDescription)newValue);
				return;
			case MncModelPackage.CONTROL_NODE__CHILD_NODES:
				getChildNodes().clear();
				getChildNodes().addAll((Collection<? extends ControlNode>)newValue);
				return;
			case MncModelPackage.CONTROL_NODE__PARENT_NODE:
				setParentNode((ControlNode)newValue);
				return;
			case MncModelPackage.CONTROL_NODE__COMMAND_RESPONSE_BLOCKS:
				getCommandResponseBlocks().clear();
				getCommandResponseBlocks().addAll((Collection<? extends CommandResponseBlock>)newValue);
				return;
			case MncModelPackage.CONTROL_NODE__EVENT_BLOCKS:
				getEventBlocks().clear();
				getEventBlocks().addAll((Collection<? extends EventBlock>)newValue);
				return;
			case MncModelPackage.CONTROL_NODE__ALARM_BLOCKS:
				getAlarmBlocks().clear();
				getAlarmBlocks().addAll((Collection<? extends AlarmBlock>)newValue);
				return;
			case MncModelPackage.CONTROL_NODE__DATA_POINT_BLOCKS:
				getDataPointBlocks().clear();
				getDataPointBlocks().addAll((Collection<? extends DataPointBlock>)newValue);
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
			case MncModelPackage.CONTROL_NODE__NAME:
				setName(NAME_EDEFAULT);
				return;
			case MncModelPackage.CONTROL_NODE__INTERFACE_DESCRIPTION:
				setInterfaceDescription((InterfaceDescription)null);
				return;
			case MncModelPackage.CONTROL_NODE__CHILD_NODES:
				getChildNodes().clear();
				return;
			case MncModelPackage.CONTROL_NODE__PARENT_NODE:
				setParentNode((ControlNode)null);
				return;
			case MncModelPackage.CONTROL_NODE__COMMAND_RESPONSE_BLOCKS:
				getCommandResponseBlocks().clear();
				return;
			case MncModelPackage.CONTROL_NODE__EVENT_BLOCKS:
				getEventBlocks().clear();
				return;
			case MncModelPackage.CONTROL_NODE__ALARM_BLOCKS:
				getAlarmBlocks().clear();
				return;
			case MncModelPackage.CONTROL_NODE__DATA_POINT_BLOCKS:
				getDataPointBlocks().clear();
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
			case MncModelPackage.CONTROL_NODE__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case MncModelPackage.CONTROL_NODE__INTERFACE_DESCRIPTION:
				return interfaceDescription != null;
			case MncModelPackage.CONTROL_NODE__CHILD_NODES:
				return childNodes != null && !childNodes.isEmpty();
			case MncModelPackage.CONTROL_NODE__PARENT_NODE:
				return parentNode != null;
			case MncModelPackage.CONTROL_NODE__COMMAND_RESPONSE_BLOCKS:
				return commandResponseBlocks != null && !commandResponseBlocks.isEmpty();
			case MncModelPackage.CONTROL_NODE__EVENT_BLOCKS:
				return eventBlocks != null && !eventBlocks.isEmpty();
			case MncModelPackage.CONTROL_NODE__ALARM_BLOCKS:
				return alarmBlocks != null && !alarmBlocks.isEmpty();
			case MncModelPackage.CONTROL_NODE__DATA_POINT_BLOCKS:
				return dataPointBlocks != null && !dataPointBlocks.isEmpty();
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

} //ControlNodeImpl
