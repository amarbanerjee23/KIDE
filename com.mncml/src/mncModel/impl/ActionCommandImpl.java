/**
 */
package mncModel.impl;

import java.util.Collection;

import mncModel.ActionCommand;
import mncModel.Command;
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
 * An implementation of the model object '<em><b>Action Command</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link mncModel.impl.ActionCommandImpl#getCommand <em>Command</em>}</li>
 *   <li>{@link mncModel.impl.ActionCommandImpl#getResponseHandling <em>Response Handling</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ActionCommandImpl extends ActionItemImpl implements ActionCommand {
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
	 * The cached value of the '{@link #getResponseHandling() <em>Response Handling</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getResponseHandling()
	 * @generated
	 * @ordered
	 */
	protected EList<ResponseBlock> responseHandling;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ActionCommandImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MncModelPackage.Literals.ACTION_COMMAND;
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
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, MncModelPackage.ACTION_COMMAND__COMMAND, oldCommand, command));
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
			eNotify(new ENotificationImpl(this, Notification.SET, MncModelPackage.ACTION_COMMAND__COMMAND, oldCommand, command));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<ResponseBlock> getResponseHandling() {
		if (responseHandling == null) {
			responseHandling = new EObjectContainmentEList<ResponseBlock>(ResponseBlock.class, this, MncModelPackage.ACTION_COMMAND__RESPONSE_HANDLING);
		}
		return responseHandling;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case MncModelPackage.ACTION_COMMAND__RESPONSE_HANDLING:
				return ((InternalEList<?>)getResponseHandling()).basicRemove(otherEnd, msgs);
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
			case MncModelPackage.ACTION_COMMAND__COMMAND:
				if (resolve) return getCommand();
				return basicGetCommand();
			case MncModelPackage.ACTION_COMMAND__RESPONSE_HANDLING:
				return getResponseHandling();
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
			case MncModelPackage.ACTION_COMMAND__COMMAND:
				setCommand((Command)newValue);
				return;
			case MncModelPackage.ACTION_COMMAND__RESPONSE_HANDLING:
				getResponseHandling().clear();
				getResponseHandling().addAll((Collection<? extends ResponseBlock>)newValue);
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
			case MncModelPackage.ACTION_COMMAND__COMMAND:
				setCommand((Command)null);
				return;
			case MncModelPackage.ACTION_COMMAND__RESPONSE_HANDLING:
				getResponseHandling().clear();
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
			case MncModelPackage.ACTION_COMMAND__COMMAND:
				return command != null;
			case MncModelPackage.ACTION_COMMAND__RESPONSE_HANDLING:
				return responseHandling != null && !responseHandling.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //ActionCommandImpl
