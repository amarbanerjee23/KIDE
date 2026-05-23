/**
 */
package mncModel.impl;

import java.util.Collection;

import mncModel.Action;
import mncModel.MncModelPackage;
import mncModel.OperatingState;
import mncModel.Transition;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectResolvingEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Transition</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link mncModel.impl.TransitionImpl#getCurrentState <em>Current State</em>}</li>
 *   <li>{@link mncModel.impl.TransitionImpl#getNextState <em>Next State</em>}</li>
 *   <li>{@link mncModel.impl.TransitionImpl#getEntryAction <em>Entry Action</em>}</li>
 *   <li>{@link mncModel.impl.TransitionImpl#getExitAction <em>Exit Action</em>}</li>
 * </ul>
 *
 * @generated
 */
public class TransitionImpl extends MinimalEObjectImpl.Container implements Transition {
	/**
	 * The cached value of the '{@link #getCurrentState() <em>Current State</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCurrentState()
	 * @generated
	 * @ordered
	 */
	protected EList<OperatingState> currentState;

	/**
	 * The cached value of the '{@link #getNextState() <em>Next State</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getNextState()
	 * @generated
	 * @ordered
	 */
	protected OperatingState nextState;

	/**
	 * The cached value of the '{@link #getEntryAction() <em>Entry Action</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEntryAction()
	 * @generated
	 * @ordered
	 */
	protected Action entryAction;

	/**
	 * The cached value of the '{@link #getExitAction() <em>Exit Action</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExitAction()
	 * @generated
	 * @ordered
	 */
	protected Action exitAction;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected TransitionImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MncModelPackage.Literals.TRANSITION;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<OperatingState> getCurrentState() {
		if (currentState == null) {
			currentState = new EObjectResolvingEList<OperatingState>(OperatingState.class, this, MncModelPackage.TRANSITION__CURRENT_STATE);
		}
		return currentState;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public OperatingState getNextState() {
		if (nextState != null && nextState.eIsProxy()) {
			InternalEObject oldNextState = (InternalEObject)nextState;
			nextState = (OperatingState)eResolveProxy(oldNextState);
			if (nextState != oldNextState) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, MncModelPackage.TRANSITION__NEXT_STATE, oldNextState, nextState));
			}
		}
		return nextState;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public OperatingState basicGetNextState() {
		return nextState;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setNextState(OperatingState newNextState) {
		OperatingState oldNextState = nextState;
		nextState = newNextState;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MncModelPackage.TRANSITION__NEXT_STATE, oldNextState, nextState));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Action getEntryAction() {
		return entryAction;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetEntryAction(Action newEntryAction, NotificationChain msgs) {
		Action oldEntryAction = entryAction;
		entryAction = newEntryAction;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, MncModelPackage.TRANSITION__ENTRY_ACTION, oldEntryAction, newEntryAction);
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
	public void setEntryAction(Action newEntryAction) {
		if (newEntryAction != entryAction) {
			NotificationChain msgs = null;
			if (entryAction != null)
				msgs = ((InternalEObject)entryAction).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - MncModelPackage.TRANSITION__ENTRY_ACTION, null, msgs);
			if (newEntryAction != null)
				msgs = ((InternalEObject)newEntryAction).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - MncModelPackage.TRANSITION__ENTRY_ACTION, null, msgs);
			msgs = basicSetEntryAction(newEntryAction, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MncModelPackage.TRANSITION__ENTRY_ACTION, newEntryAction, newEntryAction));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Action getExitAction() {
		return exitAction;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetExitAction(Action newExitAction, NotificationChain msgs) {
		Action oldExitAction = exitAction;
		exitAction = newExitAction;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, MncModelPackage.TRANSITION__EXIT_ACTION, oldExitAction, newExitAction);
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
	public void setExitAction(Action newExitAction) {
		if (newExitAction != exitAction) {
			NotificationChain msgs = null;
			if (exitAction != null)
				msgs = ((InternalEObject)exitAction).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - MncModelPackage.TRANSITION__EXIT_ACTION, null, msgs);
			if (newExitAction != null)
				msgs = ((InternalEObject)newExitAction).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - MncModelPackage.TRANSITION__EXIT_ACTION, null, msgs);
			msgs = basicSetExitAction(newExitAction, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MncModelPackage.TRANSITION__EXIT_ACTION, newExitAction, newExitAction));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case MncModelPackage.TRANSITION__ENTRY_ACTION:
				return basicSetEntryAction(null, msgs);
			case MncModelPackage.TRANSITION__EXIT_ACTION:
				return basicSetExitAction(null, msgs);
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
			case MncModelPackage.TRANSITION__CURRENT_STATE:
				return getCurrentState();
			case MncModelPackage.TRANSITION__NEXT_STATE:
				if (resolve) return getNextState();
				return basicGetNextState();
			case MncModelPackage.TRANSITION__ENTRY_ACTION:
				return getEntryAction();
			case MncModelPackage.TRANSITION__EXIT_ACTION:
				return getExitAction();
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
			case MncModelPackage.TRANSITION__CURRENT_STATE:
				getCurrentState().clear();
				getCurrentState().addAll((Collection<? extends OperatingState>)newValue);
				return;
			case MncModelPackage.TRANSITION__NEXT_STATE:
				setNextState((OperatingState)newValue);
				return;
			case MncModelPackage.TRANSITION__ENTRY_ACTION:
				setEntryAction((Action)newValue);
				return;
			case MncModelPackage.TRANSITION__EXIT_ACTION:
				setExitAction((Action)newValue);
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
			case MncModelPackage.TRANSITION__CURRENT_STATE:
				getCurrentState().clear();
				return;
			case MncModelPackage.TRANSITION__NEXT_STATE:
				setNextState((OperatingState)null);
				return;
			case MncModelPackage.TRANSITION__ENTRY_ACTION:
				setEntryAction((Action)null);
				return;
			case MncModelPackage.TRANSITION__EXIT_ACTION:
				setExitAction((Action)null);
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
			case MncModelPackage.TRANSITION__CURRENT_STATE:
				return currentState != null && !currentState.isEmpty();
			case MncModelPackage.TRANSITION__NEXT_STATE:
				return nextState != null;
			case MncModelPackage.TRANSITION__ENTRY_ACTION:
				return entryAction != null;
			case MncModelPackage.TRANSITION__EXIT_ACTION:
				return exitAction != null;
		}
		return super.eIsSet(featureID);
	}

} //TransitionImpl
