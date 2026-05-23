/**
 */
package operationsDescription.impl;

import dataModelPackage.Parameter;

import java.util.Collection;
import operationsDescription.Operation;
import operationsDescription.OperationsDescriptionPackage;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Operation</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link operationsDescription.impl.OperationImpl#getName <em>Name</em>}</li>
 *   <li>{@link operationsDescription.impl.OperationImpl#getInputParameters <em>Input Parameters</em>}</li>
 *   <li>{@link operationsDescription.impl.OperationImpl#getExecutableScript <em>Executable Script</em>}</li>
 *   <li>{@link operationsDescription.impl.OperationImpl#getOutputParameters <em>Output Parameters</em>}</li>
 * </ul>
 *
 * @generated
 */
public class OperationImpl extends MinimalEObjectImpl.Container implements Operation {
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
	 * The cached value of the '{@link #getInputParameters() <em>Input Parameters</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getInputParameters()
	 * @generated
	 * @ordered
	 */
	protected EList<Parameter> inputParameters;

	/**
	 * The default value of the '{@link #getExecutableScript() <em>Executable Script</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExecutableScript()
	 * @generated
	 * @ordered
	 */
	protected static final String EXECUTABLE_SCRIPT_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getExecutableScript() <em>Executable Script</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExecutableScript()
	 * @generated
	 * @ordered
	 */
	protected String executableScript = EXECUTABLE_SCRIPT_EDEFAULT;

	/**
	 * The cached value of the '{@link #getOutputParameters() <em>Output Parameters</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOutputParameters()
	 * @generated
	 * @ordered
	 */
	protected Parameter outputParameters;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected OperationImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return OperationsDescriptionPackage.Literals.OPERATION;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getName() {
		return name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setName(String newName) {
		String oldName = name;
		name = newName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OperationsDescriptionPackage.OPERATION__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Parameter> getInputParameters() {
		if (inputParameters == null) {
			inputParameters = new EObjectContainmentEList<Parameter>(Parameter.class, this, OperationsDescriptionPackage.OPERATION__INPUT_PARAMETERS);
		}
		return inputParameters;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getExecutableScript() {
		return executableScript;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setExecutableScript(String newExecutableScript) {
		String oldExecutableScript = executableScript;
		executableScript = newExecutableScript;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OperationsDescriptionPackage.OPERATION__EXECUTABLE_SCRIPT, oldExecutableScript, executableScript));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Parameter getOutputParameters() {
		return outputParameters;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetOutputParameters(Parameter newOutputParameters, NotificationChain msgs) {
		Parameter oldOutputParameters = outputParameters;
		outputParameters = newOutputParameters;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, OperationsDescriptionPackage.OPERATION__OUTPUT_PARAMETERS, oldOutputParameters, newOutputParameters);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setOutputParameters(Parameter newOutputParameters) {
		if (newOutputParameters != outputParameters) {
			NotificationChain msgs = null;
			if (outputParameters != null)
				msgs = ((InternalEObject)outputParameters).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - OperationsDescriptionPackage.OPERATION__OUTPUT_PARAMETERS, null, msgs);
			if (newOutputParameters != null)
				msgs = ((InternalEObject)newOutputParameters).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - OperationsDescriptionPackage.OPERATION__OUTPUT_PARAMETERS, null, msgs);
			msgs = basicSetOutputParameters(newOutputParameters, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OperationsDescriptionPackage.OPERATION__OUTPUT_PARAMETERS, newOutputParameters, newOutputParameters));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case OperationsDescriptionPackage.OPERATION__INPUT_PARAMETERS:
				return ((InternalEList<?>)getInputParameters()).basicRemove(otherEnd, msgs);
			case OperationsDescriptionPackage.OPERATION__OUTPUT_PARAMETERS:
				return basicSetOutputParameters(null, msgs);
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
			case OperationsDescriptionPackage.OPERATION__NAME:
				return getName();
			case OperationsDescriptionPackage.OPERATION__INPUT_PARAMETERS:
				return getInputParameters();
			case OperationsDescriptionPackage.OPERATION__EXECUTABLE_SCRIPT:
				return getExecutableScript();
			case OperationsDescriptionPackage.OPERATION__OUTPUT_PARAMETERS:
				return getOutputParameters();
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
			case OperationsDescriptionPackage.OPERATION__NAME:
				setName((String)newValue);
				return;
			case OperationsDescriptionPackage.OPERATION__INPUT_PARAMETERS:
				getInputParameters().clear();
				getInputParameters().addAll((Collection<? extends Parameter>)newValue);
				return;
			case OperationsDescriptionPackage.OPERATION__EXECUTABLE_SCRIPT:
				setExecutableScript((String)newValue);
				return;
			case OperationsDescriptionPackage.OPERATION__OUTPUT_PARAMETERS:
				setOutputParameters((Parameter)newValue);
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
			case OperationsDescriptionPackage.OPERATION__NAME:
				setName(NAME_EDEFAULT);
				return;
			case OperationsDescriptionPackage.OPERATION__INPUT_PARAMETERS:
				getInputParameters().clear();
				return;
			case OperationsDescriptionPackage.OPERATION__EXECUTABLE_SCRIPT:
				setExecutableScript(EXECUTABLE_SCRIPT_EDEFAULT);
				return;
			case OperationsDescriptionPackage.OPERATION__OUTPUT_PARAMETERS:
				setOutputParameters((Parameter)null);
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
			case OperationsDescriptionPackage.OPERATION__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case OperationsDescriptionPackage.OPERATION__INPUT_PARAMETERS:
				return inputParameters != null && !inputParameters.isEmpty();
			case OperationsDescriptionPackage.OPERATION__EXECUTABLE_SCRIPT:
				return EXECUTABLE_SCRIPT_EDEFAULT == null ? executableScript != null : !EXECUTABLE_SCRIPT_EDEFAULT.equals(executableScript);
			case OperationsDescriptionPackage.OPERATION__OUTPUT_PARAMETERS:
				return outputParameters != null;
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
		result.append(", executableScript: ");
		result.append(executableScript);
		result.append(')');
		return result.toString();
	}

} //OperationImpl
