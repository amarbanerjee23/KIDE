/**
 */
package mncModel.impl;

import java.util.Collection;

import mncModel.DataPoint;
import mncModel.DataPointBlock;
import mncModel.MncModelPackage;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.util.EObjectResolvingEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Data Point Block</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link mncModel.impl.DataPointBlockImpl#getDataPoint <em>Data Point</em>}</li>
 * </ul>
 *
 * @generated
 */
public class DataPointBlockImpl extends BehaviorBlockImpl implements DataPointBlock {
	/**
	 * The cached value of the '{@link #getDataPoint() <em>Data Point</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDataPoint()
	 * @generated
	 * @ordered
	 */
	protected EList<DataPoint> dataPoint;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected DataPointBlockImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MncModelPackage.Literals.DATA_POINT_BLOCK;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<DataPoint> getDataPoint() {
		if (dataPoint == null) {
			dataPoint = new EObjectResolvingEList<DataPoint>(DataPoint.class, this, MncModelPackage.DATA_POINT_BLOCK__DATA_POINT);
		}
		return dataPoint;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case MncModelPackage.DATA_POINT_BLOCK__DATA_POINT:
				return getDataPoint();
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
			case MncModelPackage.DATA_POINT_BLOCK__DATA_POINT:
				getDataPoint().clear();
				getDataPoint().addAll((Collection<? extends DataPoint>)newValue);
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
			case MncModelPackage.DATA_POINT_BLOCK__DATA_POINT:
				getDataPoint().clear();
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
			case MncModelPackage.DATA_POINT_BLOCK__DATA_POINT:
				return dataPoint != null && !dataPoint.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //DataPointBlockImpl
