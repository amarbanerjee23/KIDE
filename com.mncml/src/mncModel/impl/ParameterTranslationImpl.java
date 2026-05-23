/**
 */
package mncModel.impl;

import dataModelPackage.Parameter;

import java.util.Collection;

import mncModel.MncModelPackage;
import mncModel.ParameterTranslation;

import operationsDescription.Operation;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectResolvingEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Parameter Translation</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link mncModel.impl.ParameterTranslationImpl#getInputParameters <em>Input Parameters</em>}</li>
 *   <li>{@link mncModel.impl.ParameterTranslationImpl#getTransformationFunction <em>Transformation Function</em>}</li>
 *   <li>{@link mncModel.impl.ParameterTranslationImpl#getTranslatedParameters <em>Translated Parameters</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ParameterTranslationImpl extends MinimalEObjectImpl.Container implements ParameterTranslation {
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
	 * The cached value of the '{@link #getTransformationFunction() <em>Transformation Function</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTransformationFunction()
	 * @generated
	 * @ordered
	 */
	protected Operation transformationFunction;

	/**
	 * The cached value of the '{@link #getTranslatedParameters() <em>Translated Parameters</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTranslatedParameters()
	 * @generated
	 * @ordered
	 */
	protected Parameter translatedParameters;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ParameterTranslationImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MncModelPackage.Literals.PARAMETER_TRANSLATION;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<Parameter> getInputParameters() {
		if (inputParameters == null) {
			inputParameters = new EObjectResolvingEList<Parameter>(Parameter.class, this, MncModelPackage.PARAMETER_TRANSLATION__INPUT_PARAMETERS);
		}
		return inputParameters;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Operation getTransformationFunction() {
		if (transformationFunction != null && transformationFunction.eIsProxy()) {
			InternalEObject oldTransformationFunction = (InternalEObject)transformationFunction;
			transformationFunction = (Operation)eResolveProxy(oldTransformationFunction);
			if (transformationFunction != oldTransformationFunction) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, MncModelPackage.PARAMETER_TRANSLATION__TRANSFORMATION_FUNCTION, oldTransformationFunction, transformationFunction));
			}
		}
		return transformationFunction;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Operation basicGetTransformationFunction() {
		return transformationFunction;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setTransformationFunction(Operation newTransformationFunction) {
		Operation oldTransformationFunction = transformationFunction;
		transformationFunction = newTransformationFunction;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MncModelPackage.PARAMETER_TRANSLATION__TRANSFORMATION_FUNCTION, oldTransformationFunction, transformationFunction));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Parameter getTranslatedParameters() {
		if (translatedParameters != null && translatedParameters.eIsProxy()) {
			InternalEObject oldTranslatedParameters = (InternalEObject)translatedParameters;
			translatedParameters = (Parameter)eResolveProxy(oldTranslatedParameters);
			if (translatedParameters != oldTranslatedParameters) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, MncModelPackage.PARAMETER_TRANSLATION__TRANSLATED_PARAMETERS, oldTranslatedParameters, translatedParameters));
			}
		}
		return translatedParameters;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Parameter basicGetTranslatedParameters() {
		return translatedParameters;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setTranslatedParameters(Parameter newTranslatedParameters) {
		Parameter oldTranslatedParameters = translatedParameters;
		translatedParameters = newTranslatedParameters;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MncModelPackage.PARAMETER_TRANSLATION__TRANSLATED_PARAMETERS, oldTranslatedParameters, translatedParameters));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case MncModelPackage.PARAMETER_TRANSLATION__INPUT_PARAMETERS:
				return getInputParameters();
			case MncModelPackage.PARAMETER_TRANSLATION__TRANSFORMATION_FUNCTION:
				if (resolve) return getTransformationFunction();
				return basicGetTransformationFunction();
			case MncModelPackage.PARAMETER_TRANSLATION__TRANSLATED_PARAMETERS:
				if (resolve) return getTranslatedParameters();
				return basicGetTranslatedParameters();
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
			case MncModelPackage.PARAMETER_TRANSLATION__INPUT_PARAMETERS:
				getInputParameters().clear();
				getInputParameters().addAll((Collection<? extends Parameter>)newValue);
				return;
			case MncModelPackage.PARAMETER_TRANSLATION__TRANSFORMATION_FUNCTION:
				setTransformationFunction((Operation)newValue);
				return;
			case MncModelPackage.PARAMETER_TRANSLATION__TRANSLATED_PARAMETERS:
				setTranslatedParameters((Parameter)newValue);
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
			case MncModelPackage.PARAMETER_TRANSLATION__INPUT_PARAMETERS:
				getInputParameters().clear();
				return;
			case MncModelPackage.PARAMETER_TRANSLATION__TRANSFORMATION_FUNCTION:
				setTransformationFunction((Operation)null);
				return;
			case MncModelPackage.PARAMETER_TRANSLATION__TRANSLATED_PARAMETERS:
				setTranslatedParameters((Parameter)null);
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
			case MncModelPackage.PARAMETER_TRANSLATION__INPUT_PARAMETERS:
				return inputParameters != null && !inputParameters.isEmpty();
			case MncModelPackage.PARAMETER_TRANSLATION__TRANSFORMATION_FUNCTION:
				return transformationFunction != null;
			case MncModelPackage.PARAMETER_TRANSLATION__TRANSLATED_PARAMETERS:
				return translatedParameters != null;
		}
		return super.eIsSet(featureID);
	}

} //ParameterTranslationImpl
