/**
 */
package dataModelPackage.impl;

import dataModelPackage.DataModel;
import dataModelPackage.DataModelPackage;
import dataModelPackage.Parameter;

import java.util.Collection;

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
 * An implementation of the model object '<em><b>Data Model</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link dataModelPackage.impl.DataModelImpl#getPrimitives <em>Primitives</em>}</li>
 *   <li>{@link dataModelPackage.impl.DataModelImpl#getComposites <em>Composites</em>}</li>
 *   <li>{@link dataModelPackage.impl.DataModelImpl#getName <em>Name</em>}</li>
 * </ul>
 *
 * @generated
 */
public class DataModelImpl extends ParameterImpl implements DataModel {
	/**
	 * The cached value of the '{@link #getPrimitives() <em>Primitives</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPrimitives()
	 * @generated
	 * @ordered
	 */
	protected EList<Parameter> primitives;

	/**
	 * The cached value of the '{@link #getComposites() <em>Composites</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getComposites()
	 * @generated
	 * @ordered
	 */
	protected EList<DataModel> composites;

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
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected DataModelImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return DataModelPackage.Literals.DATA_MODEL;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<Parameter> getPrimitives() {
		if (primitives == null) {
			primitives = new EObjectContainmentEList<Parameter>(Parameter.class, this, DataModelPackage.DATA_MODEL__PRIMITIVES);
		}
		return primitives;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<DataModel> getComposites() {
		if (composites == null) {
			composites = new EObjectResolvingEList<DataModel>(DataModel.class, this, DataModelPackage.DATA_MODEL__COMPOSITES);
		}
		return composites;
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
			eNotify(new ENotificationImpl(this, Notification.SET, DataModelPackage.DATA_MODEL__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case DataModelPackage.DATA_MODEL__PRIMITIVES:
				return ((InternalEList<?>)getPrimitives()).basicRemove(otherEnd, msgs);
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
			case DataModelPackage.DATA_MODEL__PRIMITIVES:
				return getPrimitives();
			case DataModelPackage.DATA_MODEL__COMPOSITES:
				return getComposites();
			case DataModelPackage.DATA_MODEL__NAME:
				return getName();
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
			case DataModelPackage.DATA_MODEL__PRIMITIVES:
				getPrimitives().clear();
				getPrimitives().addAll((Collection<? extends Parameter>)newValue);
				return;
			case DataModelPackage.DATA_MODEL__COMPOSITES:
				getComposites().clear();
				getComposites().addAll((Collection<? extends DataModel>)newValue);
				return;
			case DataModelPackage.DATA_MODEL__NAME:
				setName((String)newValue);
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
			case DataModelPackage.DATA_MODEL__PRIMITIVES:
				getPrimitives().clear();
				return;
			case DataModelPackage.DATA_MODEL__COMPOSITES:
				getComposites().clear();
				return;
			case DataModelPackage.DATA_MODEL__NAME:
				setName(NAME_EDEFAULT);
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
			case DataModelPackage.DATA_MODEL__PRIMITIVES:
				return primitives != null && !primitives.isEmpty();
			case DataModelPackage.DATA_MODEL__COMPOSITES:
				return composites != null && !composites.isEmpty();
			case DataModelPackage.DATA_MODEL__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
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

} //DataModelImpl
