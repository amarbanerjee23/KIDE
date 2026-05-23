/**
 */
package activityDiagramModel.impl;

import activityDiagramModel.Activity;
import activityDiagramModel.ActivityDiagram;
import activityDiagramModel.ActivityDiagramModelPackage;

import dataModelPackage.Parameter;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EDataTypeUniqueEList;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Activity Diagram</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link activityDiagramModel.impl.ActivityDiagramImpl#getName <em>Name</em>}</li>
 *   <li>{@link activityDiagramModel.impl.ActivityDiagramImpl#getActivities <em>Activities</em>}</li>
 *   <li>{@link activityDiagramModel.impl.ActivityDiagramImpl#getDataObjects <em>Data Objects</em>}</li>
 *   <li>{@link activityDiagramModel.impl.ActivityDiagramImpl#getResults <em>Results</em>}</li>
 *   <li>{@link activityDiagramModel.impl.ActivityDiagramImpl#getContextDataModel <em>Context Data Model</em>}</li>
 *   <li>{@link activityDiagramModel.impl.ActivityDiagramImpl#getPhysicalContext <em>Physical Context</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ActivityDiagramImpl extends MinimalEObjectImpl.Container implements ActivityDiagram {
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
	 * The cached value of the '{@link #getActivities() <em>Activities</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getActivities()
	 * @generated
	 * @ordered
	 */
	protected EList<Activity> activities;

	/**
	 * The cached value of the '{@link #getDataObjects() <em>Data Objects</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDataObjects()
	 * @generated
	 * @ordered
	 */
	protected EList<Parameter> dataObjects;

	/**
	 * The cached value of the '{@link #getResults() <em>Results</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getResults()
	 * @generated
	 * @ordered
	 */
	protected EList<Parameter> results;

	/**
	 * The cached value of the '{@link #getContextDataModel() <em>Context Data Model</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getContextDataModel()
	 * @generated
	 * @ordered
	 */
	protected EList<Parameter> contextDataModel;

	/**
	 * The cached value of the '{@link #getPhysicalContext() <em>Physical Context</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPhysicalContext()
	 * @generated
	 * @ordered
	 */
	protected EList<String> physicalContext;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ActivityDiagramImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ActivityDiagramModelPackage.Literals.ACTIVITY_DIAGRAM;
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
			eNotify(new ENotificationImpl(this, Notification.SET, ActivityDiagramModelPackage.ACTIVITY_DIAGRAM__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<Activity> getActivities() {
		if (activities == null) {
			activities = new EObjectContainmentEList<Activity>(Activity.class, this, ActivityDiagramModelPackage.ACTIVITY_DIAGRAM__ACTIVITIES);
		}
		return activities;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<Parameter> getDataObjects() {
		if (dataObjects == null) {
			dataObjects = new EObjectResolvingEList<Parameter>(Parameter.class, this, ActivityDiagramModelPackage.ACTIVITY_DIAGRAM__DATA_OBJECTS);
		}
		return dataObjects;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<Parameter> getResults() {
		if (results == null) {
			results = new EObjectContainmentEList<Parameter>(Parameter.class, this, ActivityDiagramModelPackage.ACTIVITY_DIAGRAM__RESULTS);
		}
		return results;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<Parameter> getContextDataModel() {
		if (contextDataModel == null) {
			contextDataModel = new EObjectResolvingEList<Parameter>(Parameter.class, this, ActivityDiagramModelPackage.ACTIVITY_DIAGRAM__CONTEXT_DATA_MODEL);
		}
		return contextDataModel;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<String> getPhysicalContext() {
		if (physicalContext == null) {
			physicalContext = new EDataTypeUniqueEList<String>(String.class, this, ActivityDiagramModelPackage.ACTIVITY_DIAGRAM__PHYSICAL_CONTEXT);
		}
		return physicalContext;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case ActivityDiagramModelPackage.ACTIVITY_DIAGRAM__ACTIVITIES:
				return ((InternalEList<?>)getActivities()).basicRemove(otherEnd, msgs);
			case ActivityDiagramModelPackage.ACTIVITY_DIAGRAM__RESULTS:
				return ((InternalEList<?>)getResults()).basicRemove(otherEnd, msgs);
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
			case ActivityDiagramModelPackage.ACTIVITY_DIAGRAM__NAME:
				return getName();
			case ActivityDiagramModelPackage.ACTIVITY_DIAGRAM__ACTIVITIES:
				return getActivities();
			case ActivityDiagramModelPackage.ACTIVITY_DIAGRAM__DATA_OBJECTS:
				return getDataObjects();
			case ActivityDiagramModelPackage.ACTIVITY_DIAGRAM__RESULTS:
				return getResults();
			case ActivityDiagramModelPackage.ACTIVITY_DIAGRAM__CONTEXT_DATA_MODEL:
				return getContextDataModel();
			case ActivityDiagramModelPackage.ACTIVITY_DIAGRAM__PHYSICAL_CONTEXT:
				return getPhysicalContext();
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
			case ActivityDiagramModelPackage.ACTIVITY_DIAGRAM__NAME:
				setName((String)newValue);
				return;
			case ActivityDiagramModelPackage.ACTIVITY_DIAGRAM__ACTIVITIES:
				getActivities().clear();
				getActivities().addAll((Collection<? extends Activity>)newValue);
				return;
			case ActivityDiagramModelPackage.ACTIVITY_DIAGRAM__DATA_OBJECTS:
				getDataObjects().clear();
				getDataObjects().addAll((Collection<? extends Parameter>)newValue);
				return;
			case ActivityDiagramModelPackage.ACTIVITY_DIAGRAM__RESULTS:
				getResults().clear();
				getResults().addAll((Collection<? extends Parameter>)newValue);
				return;
			case ActivityDiagramModelPackage.ACTIVITY_DIAGRAM__CONTEXT_DATA_MODEL:
				getContextDataModel().clear();
				getContextDataModel().addAll((Collection<? extends Parameter>)newValue);
				return;
			case ActivityDiagramModelPackage.ACTIVITY_DIAGRAM__PHYSICAL_CONTEXT:
				getPhysicalContext().clear();
				getPhysicalContext().addAll((Collection<? extends String>)newValue);
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
			case ActivityDiagramModelPackage.ACTIVITY_DIAGRAM__NAME:
				setName(NAME_EDEFAULT);
				return;
			case ActivityDiagramModelPackage.ACTIVITY_DIAGRAM__ACTIVITIES:
				getActivities().clear();
				return;
			case ActivityDiagramModelPackage.ACTIVITY_DIAGRAM__DATA_OBJECTS:
				getDataObjects().clear();
				return;
			case ActivityDiagramModelPackage.ACTIVITY_DIAGRAM__RESULTS:
				getResults().clear();
				return;
			case ActivityDiagramModelPackage.ACTIVITY_DIAGRAM__CONTEXT_DATA_MODEL:
				getContextDataModel().clear();
				return;
			case ActivityDiagramModelPackage.ACTIVITY_DIAGRAM__PHYSICAL_CONTEXT:
				getPhysicalContext().clear();
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
			case ActivityDiagramModelPackage.ACTIVITY_DIAGRAM__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case ActivityDiagramModelPackage.ACTIVITY_DIAGRAM__ACTIVITIES:
				return activities != null && !activities.isEmpty();
			case ActivityDiagramModelPackage.ACTIVITY_DIAGRAM__DATA_OBJECTS:
				return dataObjects != null && !dataObjects.isEmpty();
			case ActivityDiagramModelPackage.ACTIVITY_DIAGRAM__RESULTS:
				return results != null && !results.isEmpty();
			case ActivityDiagramModelPackage.ACTIVITY_DIAGRAM__CONTEXT_DATA_MODEL:
				return contextDataModel != null && !contextDataModel.isEmpty();
			case ActivityDiagramModelPackage.ACTIVITY_DIAGRAM__PHYSICAL_CONTEXT:
				return physicalContext != null && !physicalContext.isEmpty();
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
		result.append(", physicalContext: ");
		result.append(physicalContext);
		result.append(')');
		return result.toString();
	}

} //ActivityDiagramImpl
