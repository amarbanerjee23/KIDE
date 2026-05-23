/**
 */
package mncModel.impl;

import java.util.Collection;

import mncModel.Command;
import mncModel.CommandResponseBlock;
import mncModel.MncModelPackage;
import mncModel.ResponseBlock;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Command Response Block</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link mncModel.impl.CommandResponseBlockImpl#getCommand <em>Command</em>}</li>
 *   <li>{@link mncModel.impl.CommandResponseBlockImpl#getResponseBlock <em>Response Block</em>}</li>
 * </ul>
 *
 * @generated
 */
public class CommandResponseBlockImpl extends BehaviorBlockImpl implements CommandResponseBlock {
	/**
	 * The cached value of the '{@link #getCommand() <em>Command</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCommand()
	 * @generated
	 * @ordered
	 */
	protected Command command;

	/**
	 * The cached value of the '{@link #getResponseBlock() <em>Response Block</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getResponseBlock()
	 * @generated
	 * @ordered
	 */
	protected EList<ResponseBlock> responseBlock;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected CommandResponseBlockImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MncModelPackage.Literals.COMMAND_RESPONSE_BLOCK;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Command getCommand() {
		if (command != null && command.eIsProxy()) {
			InternalEObject oldCommand = (InternalEObject)command;
			command = (Command)eResolveProxy(oldCommand);
			if (command != oldCommand) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, MncModelPackage.COMMAND_RESPONSE_BLOCK__COMMAND, oldCommand, command));
			}
		}
		return command;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Command basicGetCommand() {
		return command;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setCommand(Command newCommand) {
		Command oldCommand = command;
		command = newCommand;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MncModelPackage.COMMAND_RESPONSE_BLOCK__COMMAND, oldCommand, command));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<ResponseBlock> getResponseBlock() {
		if (responseBlock == null) {
			responseBlock = new EObjectContainmentEList<ResponseBlock>(ResponseBlock.class, this, MncModelPackage.COMMAND_RESPONSE_BLOCK__RESPONSE_BLOCK);
		}
		return responseBlock;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case MncModelPackage.COMMAND_RESPONSE_BLOCK__RESPONSE_BLOCK:
				return ((InternalEList<?>)getResponseBlock()).basicRemove(otherEnd, msgs);
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
			case MncModelPackage.COMMAND_RESPONSE_BLOCK__COMMAND:
				if (resolve) return getCommand();
				return basicGetCommand();
			case MncModelPackage.COMMAND_RESPONSE_BLOCK__RESPONSE_BLOCK:
				return getResponseBlock();
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
			case MncModelPackage.COMMAND_RESPONSE_BLOCK__COMMAND:
				setCommand((Command)newValue);
				return;
			case MncModelPackage.COMMAND_RESPONSE_BLOCK__RESPONSE_BLOCK:
				getResponseBlock().clear();
				getResponseBlock().addAll((Collection<? extends ResponseBlock>)newValue);
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
			case MncModelPackage.COMMAND_RESPONSE_BLOCK__COMMAND:
				setCommand((Command)null);
				return;
			case MncModelPackage.COMMAND_RESPONSE_BLOCK__RESPONSE_BLOCK:
				getResponseBlock().clear();
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
			case MncModelPackage.COMMAND_RESPONSE_BLOCK__COMMAND:
				return command != null;
			case MncModelPackage.COMMAND_RESPONSE_BLOCK__RESPONSE_BLOCK:
				return responseBlock != null && !responseBlock.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //CommandResponseBlockImpl
