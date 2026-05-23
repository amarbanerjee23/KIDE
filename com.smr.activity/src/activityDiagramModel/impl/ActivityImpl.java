/**
 */
package activityDiagramModel.impl;

import CapabilityDescription.Capability;

import activityDiagramModel.Activity;
import activityDiagramModel.ActivityDiagram;
import activityDiagramModel.ActivityDiagramModelPackage;
import activityDiagramModel.ConditionalActivity;
import activityDiagramModel.UnitTime;

import dataModelPackage.Parameter;

import java.util.Collection;

import mncModel.AbstractInterfaceItems;

import operationsDescription.Operation;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Activity</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link activityDiagramModel.impl.ActivityImpl#getName <em>Name</em>}</li>
 *   <li>{@link activityDiagramModel.impl.ActivityImpl#getBindCapability <em>Bind Capability</em>}</li>
 *   <li>{@link activityDiagramModel.impl.ActivityImpl#getDescription <em>Description</em>}</li>
 *   <li>{@link activityDiagramModel.impl.ActivityImpl#getConditionalActivity <em>Conditional Activity</em>}</li>
 *   <li>{@link activityDiagramModel.impl.ActivityImpl#getUseControlCapabilities <em>Use Control Capabilities</em>}</li>
 *   <li>{@link activityDiagramModel.impl.ActivityImpl#getRequiresOperation <em>Requires Operation</em>}</li>
 *   <li>{@link activityDiagramModel.impl.ActivityImpl#getChildActivityDiagram <em>Child Activity Diagram</em>}</li>
 *   <li>{@link activityDiagramModel.impl.ActivityImpl#getInputParameters <em>Input Parameters</em>}</li>
 *   <li>{@link activityDiagramModel.impl.ActivityImpl#getInterruptedBy <em>Interrupted By</em>}</li>
 *   <li>{@link activityDiagramModel.impl.ActivityImpl#getInterrupts <em>Interrupts</em>}</li>
 *   <li>{@link activityDiagramModel.impl.ActivityImpl#getTime <em>Time</em>}</li>
 *   <li>{@link activityDiagramModel.impl.ActivityImpl#getUnit <em>Unit</em>}</li>
 *   <li>{@link activityDiagramModel.impl.ActivityImpl#getNextActivity <em>Next Activity</em>}</li>
 *   <li>{@link activityDiagramModel.impl.ActivityImpl#getNextActivityDiagram <em>Next Activity Diagram</em>}</li>
 *   <li>{@link activityDiagramModel.impl.ActivityImpl#getRequiredCapability <em>Required Capability</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ActivityImpl extends MinimalEObjectImpl.Container implements Activity {
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
	 * The cached value of the '{@link #getBindCapability() <em>Bind Capability</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBindCapability()
	 * @generated
	 * @ordered
	 */
	protected Capability bindCapability;

	/**
	 * The default value of the '{@link #getDescription() <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDescription()
	 * @generated
	 * @ordered
	 */
	protected static final String DESCRIPTION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDescription() <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDescription()
	 * @generated
	 * @ordered
	 */
	protected String description = DESCRIPTION_EDEFAULT;

	/**
	 * The cached value of the '{@link #getConditionalActivity() <em>Conditional Activity</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getConditionalActivity()
	 * @generated
	 * @ordered
	 */
	protected EList<ConditionalActivity> conditionalActivity;

	/**
	 * The cached value of the '{@link #getUseControlCapabilities() <em>Use Control Capabilities</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getUseControlCapabilities()
	 * @generated
	 * @ordered
	 */
	protected EList<AbstractInterfaceItems> useControlCapabilities;

	/**
	 * The cached value of the '{@link #getRequiresOperation() <em>Requires Operation</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRequiresOperation()
	 * @generated
	 * @ordered
	 */
	protected EList<Operation> requiresOperation;

	/**
	 * The cached value of the '{@link #getChildActivityDiagram() <em>Child Activity Diagram</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getChildActivityDiagram()
	 * @generated
	 * @ordered
	 */
	protected ActivityDiagram childActivityDiagram;

	/**
	 * The cached value of the '{@link #getInputParameters() <em>Input Parameters</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getInputParameters()
	 * @generated
	 * @ordered
	 */
	protected EList<Parameter> inputParameters;

	/**
	 * The cached value of the '{@link #getInterruptedBy() <em>Interrupted By</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getInterruptedBy()
	 * @generated
	 * @ordered
	 */
	protected EList<Activity> interruptedBy;

	/**
	 * The cached value of the '{@link #getInterrupts() <em>Interrupts</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getInterrupts()
	 * @generated
	 * @ordered
	 */
	protected EList<Activity> interrupts;

	/**
	 * The default value of the '{@link #getTime() <em>Time</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTime()
	 * @generated
	 * @ordered
	 */
	protected static final float TIME_EDEFAULT = 0.0F;

	/**
	 * The cached value of the '{@link #getTime() <em>Time</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTime()
	 * @generated
	 * @ordered
	 */
	protected float time = TIME_EDEFAULT;

	/**
	 * The default value of the '{@link #getUnit() <em>Unit</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getUnit()
	 * @generated
	 * @ordered
	 */
	protected static final UnitTime UNIT_EDEFAULT = UnitTime.SECS;

	/**
	 * The cached value of the '{@link #getUnit() <em>Unit</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getUnit()
	 * @generated
	 * @ordered
	 */
	protected UnitTime unit = UNIT_EDEFAULT;

	/**
	 * The cached value of the '{@link #getNextActivity() <em>Next Activity</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getNextActivity()
	 * @generated
	 * @ordered
	 */
	protected Activity nextActivity;

	/**
	 * The cached value of the '{@link #getNextActivityDiagram() <em>Next Activity Diagram</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getNextActivityDiagram()
	 * @generated
	 * @ordered
	 */
	protected ActivityDiagram nextActivityDiagram;

	/**
	 * The default value of the '{@link #getRequiredCapability() <em>Required Capability</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRequiredCapability()
	 * @generated
	 * @ordered
	 */
	protected static final String REQUIRED_CAPABILITY_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getRequiredCapability() <em>Required Capability</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRequiredCapability()
	 * @generated
	 * @ordered
	 */
	protected String requiredCapability = REQUIRED_CAPABILITY_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ActivityImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ActivityDiagramModelPackage.Literals.ACTIVITY;
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
			eNotify(new ENotificationImpl(this, Notification.SET, ActivityDiagramModelPackage.ACTIVITY__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Capability getBindCapability() {
		if (bindCapability != null && bindCapability.eIsProxy()) {
			InternalEObject oldBindCapability = (InternalEObject)bindCapability;
			bindCapability = (Capability)eResolveProxy(oldBindCapability);
			if (bindCapability != oldBindCapability) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ActivityDiagramModelPackage.ACTIVITY__BIND_CAPABILITY, oldBindCapability, bindCapability));
			}
		}
		return bindCapability;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Capability basicGetBindCapability() {
		return bindCapability;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setBindCapability(Capability newBindCapability) {
		Capability oldBindCapability = bindCapability;
		bindCapability = newBindCapability;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ActivityDiagramModelPackage.ACTIVITY__BIND_CAPABILITY, oldBindCapability, bindCapability));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getDescription() {
		return description;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setDescription(String newDescription) {
		String oldDescription = description;
		description = newDescription;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ActivityDiagramModelPackage.ACTIVITY__DESCRIPTION, oldDescription, description));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<ConditionalActivity> getConditionalActivity() {
		if (conditionalActivity == null) {
			conditionalActivity = new EObjectContainmentEList<ConditionalActivity>(ConditionalActivity.class, this, ActivityDiagramModelPackage.ACTIVITY__CONDITIONAL_ACTIVITY);
		}
		return conditionalActivity;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<AbstractInterfaceItems> getUseControlCapabilities() {
		if (useControlCapabilities == null) {
			useControlCapabilities = new EObjectResolvingEList<AbstractInterfaceItems>(AbstractInterfaceItems.class, this, ActivityDiagramModelPackage.ACTIVITY__USE_CONTROL_CAPABILITIES);
		}
		return useControlCapabilities;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<Operation> getRequiresOperation() {
		if (requiresOperation == null) {
			requiresOperation = new EObjectResolvingEList<Operation>(Operation.class, this, ActivityDiagramModelPackage.ACTIVITY__REQUIRES_OPERATION);
		}
		return requiresOperation;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ActivityDiagram getChildActivityDiagram() {
		if (childActivityDiagram != null && childActivityDiagram.eIsProxy()) {
			InternalEObject oldChildActivityDiagram = (InternalEObject)childActivityDiagram;
			childActivityDiagram = (ActivityDiagram)eResolveProxy(oldChildActivityDiagram);
			if (childActivityDiagram != oldChildActivityDiagram) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ActivityDiagramModelPackage.ACTIVITY__CHILD_ACTIVITY_DIAGRAM, oldChildActivityDiagram, childActivityDiagram));
			}
		}
		return childActivityDiagram;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ActivityDiagram basicGetChildActivityDiagram() {
		return childActivityDiagram;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setChildActivityDiagram(ActivityDiagram newChildActivityDiagram) {
		ActivityDiagram oldChildActivityDiagram = childActivityDiagram;
		childActivityDiagram = newChildActivityDiagram;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ActivityDiagramModelPackage.ACTIVITY__CHILD_ACTIVITY_DIAGRAM, oldChildActivityDiagram, childActivityDiagram));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<Parameter> getInputParameters() {
		if (inputParameters == null) {
			inputParameters = new EObjectResolvingEList<Parameter>(Parameter.class, this, ActivityDiagramModelPackage.ACTIVITY__INPUT_PARAMETERS);
		}
		return inputParameters;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<Activity> getInterruptedBy() {
		if (interruptedBy == null) {
			interruptedBy = new EObjectResolvingEList<Activity>(Activity.class, this, ActivityDiagramModelPackage.ACTIVITY__INTERRUPTED_BY);
		}
		return interruptedBy;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<Activity> getInterrupts() {
		if (interrupts == null) {
			interrupts = new EObjectResolvingEList<Activity>(Activity.class, this, ActivityDiagramModelPackage.ACTIVITY__INTERRUPTS);
		}
		return interrupts;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public float getTime() {
		return time;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setTime(float newTime) {
		float oldTime = time;
		time = newTime;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ActivityDiagramModelPackage.ACTIVITY__TIME, oldTime, time));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public UnitTime getUnit() {
		return unit;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setUnit(UnitTime newUnit) {
		UnitTime oldUnit = unit;
		unit = newUnit == null ? UNIT_EDEFAULT : newUnit;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ActivityDiagramModelPackage.ACTIVITY__UNIT, oldUnit, unit));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Activity getNextActivity() {
		if (nextActivity != null && nextActivity.eIsProxy()) {
			InternalEObject oldNextActivity = (InternalEObject)nextActivity;
			nextActivity = (Activity)eResolveProxy(oldNextActivity);
			if (nextActivity != oldNextActivity) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ActivityDiagramModelPackage.ACTIVITY__NEXT_ACTIVITY, oldNextActivity, nextActivity));
			}
		}
		return nextActivity;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Activity basicGetNextActivity() {
		return nextActivity;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setNextActivity(Activity newNextActivity) {
		Activity oldNextActivity = nextActivity;
		nextActivity = newNextActivity;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ActivityDiagramModelPackage.ACTIVITY__NEXT_ACTIVITY, oldNextActivity, nextActivity));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ActivityDiagram getNextActivityDiagram() {
		if (nextActivityDiagram != null && nextActivityDiagram.eIsProxy()) {
			InternalEObject oldNextActivityDiagram = (InternalEObject)nextActivityDiagram;
			nextActivityDiagram = (ActivityDiagram)eResolveProxy(oldNextActivityDiagram);
			if (nextActivityDiagram != oldNextActivityDiagram) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ActivityDiagramModelPackage.ACTIVITY__NEXT_ACTIVITY_DIAGRAM, oldNextActivityDiagram, nextActivityDiagram));
			}
		}
		return nextActivityDiagram;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ActivityDiagram basicGetNextActivityDiagram() {
		return nextActivityDiagram;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setNextActivityDiagram(ActivityDiagram newNextActivityDiagram) {
		ActivityDiagram oldNextActivityDiagram = nextActivityDiagram;
		nextActivityDiagram = newNextActivityDiagram;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ActivityDiagramModelPackage.ACTIVITY__NEXT_ACTIVITY_DIAGRAM, oldNextActivityDiagram, nextActivityDiagram));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getRequiredCapability() {
		return requiredCapability;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setRequiredCapability(String newRequiredCapability) {
		String oldRequiredCapability = requiredCapability;
		requiredCapability = newRequiredCapability;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ActivityDiagramModelPackage.ACTIVITY__REQUIRED_CAPABILITY, oldRequiredCapability, requiredCapability));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case ActivityDiagramModelPackage.ACTIVITY__CONDITIONAL_ACTIVITY:
				return ((InternalEList<?>)getConditionalActivity()).basicRemove(otherEnd, msgs);
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
			case ActivityDiagramModelPackage.ACTIVITY__NAME:
				return getName();
			case ActivityDiagramModelPackage.ACTIVITY__BIND_CAPABILITY:
				if (resolve) return getBindCapability();
				return basicGetBindCapability();
			case ActivityDiagramModelPackage.ACTIVITY__DESCRIPTION:
				return getDescription();
			case ActivityDiagramModelPackage.ACTIVITY__CONDITIONAL_ACTIVITY:
				return getConditionalActivity();
			case ActivityDiagramModelPackage.ACTIVITY__USE_CONTROL_CAPABILITIES:
				return getUseControlCapabilities();
			case ActivityDiagramModelPackage.ACTIVITY__REQUIRES_OPERATION:
				return getRequiresOperation();
			case ActivityDiagramModelPackage.ACTIVITY__CHILD_ACTIVITY_DIAGRAM:
				if (resolve) return getChildActivityDiagram();
				return basicGetChildActivityDiagram();
			case ActivityDiagramModelPackage.ACTIVITY__INPUT_PARAMETERS:
				return getInputParameters();
			case ActivityDiagramModelPackage.ACTIVITY__INTERRUPTED_BY:
				return getInterruptedBy();
			case ActivityDiagramModelPackage.ACTIVITY__INTERRUPTS:
				return getInterrupts();
			case ActivityDiagramModelPackage.ACTIVITY__TIME:
				return getTime();
			case ActivityDiagramModelPackage.ACTIVITY__UNIT:
				return getUnit();
			case ActivityDiagramModelPackage.ACTIVITY__NEXT_ACTIVITY:
				if (resolve) return getNextActivity();
				return basicGetNextActivity();
			case ActivityDiagramModelPackage.ACTIVITY__NEXT_ACTIVITY_DIAGRAM:
				if (resolve) return getNextActivityDiagram();
				return basicGetNextActivityDiagram();
			case ActivityDiagramModelPackage.ACTIVITY__REQUIRED_CAPABILITY:
				return getRequiredCapability();
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
			case ActivityDiagramModelPackage.ACTIVITY__NAME:
				setName((String)newValue);
				return;
			case ActivityDiagramModelPackage.ACTIVITY__BIND_CAPABILITY:
				setBindCapability((Capability)newValue);
				return;
			case ActivityDiagramModelPackage.ACTIVITY__DESCRIPTION:
				setDescription((String)newValue);
				return;
			case ActivityDiagramModelPackage.ACTIVITY__CONDITIONAL_ACTIVITY:
				getConditionalActivity().clear();
				getConditionalActivity().addAll((Collection<? extends ConditionalActivity>)newValue);
				return;
			case ActivityDiagramModelPackage.ACTIVITY__USE_CONTROL_CAPABILITIES:
				getUseControlCapabilities().clear();
				getUseControlCapabilities().addAll((Collection<? extends AbstractInterfaceItems>)newValue);
				return;
			case ActivityDiagramModelPackage.ACTIVITY__REQUIRES_OPERATION:
				getRequiresOperation().clear();
				getRequiresOperation().addAll((Collection<? extends Operation>)newValue);
				return;
			case ActivityDiagramModelPackage.ACTIVITY__CHILD_ACTIVITY_DIAGRAM:
				setChildActivityDiagram((ActivityDiagram)newValue);
				return;
			case ActivityDiagramModelPackage.ACTIVITY__INPUT_PARAMETERS:
				getInputParameters().clear();
				getInputParameters().addAll((Collection<? extends Parameter>)newValue);
				return;
			case ActivityDiagramModelPackage.ACTIVITY__INTERRUPTED_BY:
				getInterruptedBy().clear();
				getInterruptedBy().addAll((Collection<? extends Activity>)newValue);
				return;
			case ActivityDiagramModelPackage.ACTIVITY__INTERRUPTS:
				getInterrupts().clear();
				getInterrupts().addAll((Collection<? extends Activity>)newValue);
				return;
			case ActivityDiagramModelPackage.ACTIVITY__TIME:
				setTime((Float)newValue);
				return;
			case ActivityDiagramModelPackage.ACTIVITY__UNIT:
				setUnit((UnitTime)newValue);
				return;
			case ActivityDiagramModelPackage.ACTIVITY__NEXT_ACTIVITY:
				setNextActivity((Activity)newValue);
				return;
			case ActivityDiagramModelPackage.ACTIVITY__NEXT_ACTIVITY_DIAGRAM:
				setNextActivityDiagram((ActivityDiagram)newValue);
				return;
			case ActivityDiagramModelPackage.ACTIVITY__REQUIRED_CAPABILITY:
				setRequiredCapability((String)newValue);
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
			case ActivityDiagramModelPackage.ACTIVITY__NAME:
				setName(NAME_EDEFAULT);
				return;
			case ActivityDiagramModelPackage.ACTIVITY__BIND_CAPABILITY:
				setBindCapability((Capability)null);
				return;
			case ActivityDiagramModelPackage.ACTIVITY__DESCRIPTION:
				setDescription(DESCRIPTION_EDEFAULT);
				return;
			case ActivityDiagramModelPackage.ACTIVITY__CONDITIONAL_ACTIVITY:
				getConditionalActivity().clear();
				return;
			case ActivityDiagramModelPackage.ACTIVITY__USE_CONTROL_CAPABILITIES:
				getUseControlCapabilities().clear();
				return;
			case ActivityDiagramModelPackage.ACTIVITY__REQUIRES_OPERATION:
				getRequiresOperation().clear();
				return;
			case ActivityDiagramModelPackage.ACTIVITY__CHILD_ACTIVITY_DIAGRAM:
				setChildActivityDiagram((ActivityDiagram)null);
				return;
			case ActivityDiagramModelPackage.ACTIVITY__INPUT_PARAMETERS:
				getInputParameters().clear();
				return;
			case ActivityDiagramModelPackage.ACTIVITY__INTERRUPTED_BY:
				getInterruptedBy().clear();
				return;
			case ActivityDiagramModelPackage.ACTIVITY__INTERRUPTS:
				getInterrupts().clear();
				return;
			case ActivityDiagramModelPackage.ACTIVITY__TIME:
				setTime(TIME_EDEFAULT);
				return;
			case ActivityDiagramModelPackage.ACTIVITY__UNIT:
				setUnit(UNIT_EDEFAULT);
				return;
			case ActivityDiagramModelPackage.ACTIVITY__NEXT_ACTIVITY:
				setNextActivity((Activity)null);
				return;
			case ActivityDiagramModelPackage.ACTIVITY__NEXT_ACTIVITY_DIAGRAM:
				setNextActivityDiagram((ActivityDiagram)null);
				return;
			case ActivityDiagramModelPackage.ACTIVITY__REQUIRED_CAPABILITY:
				setRequiredCapability(REQUIRED_CAPABILITY_EDEFAULT);
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
			case ActivityDiagramModelPackage.ACTIVITY__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case ActivityDiagramModelPackage.ACTIVITY__BIND_CAPABILITY:
				return bindCapability != null;
			case ActivityDiagramModelPackage.ACTIVITY__DESCRIPTION:
				return DESCRIPTION_EDEFAULT == null ? description != null : !DESCRIPTION_EDEFAULT.equals(description);
			case ActivityDiagramModelPackage.ACTIVITY__CONDITIONAL_ACTIVITY:
				return conditionalActivity != null && !conditionalActivity.isEmpty();
			case ActivityDiagramModelPackage.ACTIVITY__USE_CONTROL_CAPABILITIES:
				return useControlCapabilities != null && !useControlCapabilities.isEmpty();
			case ActivityDiagramModelPackage.ACTIVITY__REQUIRES_OPERATION:
				return requiresOperation != null && !requiresOperation.isEmpty();
			case ActivityDiagramModelPackage.ACTIVITY__CHILD_ACTIVITY_DIAGRAM:
				return childActivityDiagram != null;
			case ActivityDiagramModelPackage.ACTIVITY__INPUT_PARAMETERS:
				return inputParameters != null && !inputParameters.isEmpty();
			case ActivityDiagramModelPackage.ACTIVITY__INTERRUPTED_BY:
				return interruptedBy != null && !interruptedBy.isEmpty();
			case ActivityDiagramModelPackage.ACTIVITY__INTERRUPTS:
				return interrupts != null && !interrupts.isEmpty();
			case ActivityDiagramModelPackage.ACTIVITY__TIME:
				return time != TIME_EDEFAULT;
			case ActivityDiagramModelPackage.ACTIVITY__UNIT:
				return unit != UNIT_EDEFAULT;
			case ActivityDiagramModelPackage.ACTIVITY__NEXT_ACTIVITY:
				return nextActivity != null;
			case ActivityDiagramModelPackage.ACTIVITY__NEXT_ACTIVITY_DIAGRAM:
				return nextActivityDiagram != null;
			case ActivityDiagramModelPackage.ACTIVITY__REQUIRED_CAPABILITY:
				return REQUIRED_CAPABILITY_EDEFAULT == null ? requiredCapability != null : !REQUIRED_CAPABILITY_EDEFAULT.equals(requiredCapability);
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
		result.append(", description: ");
		result.append(description);
		result.append(", time: ");
		result.append(time);
		result.append(", unit: ");
		result.append(unit);
		result.append(", requiredCapability: ");
		result.append(requiredCapability);
		result.append(')');
		return result.toString();
	}

} //ActivityImpl
