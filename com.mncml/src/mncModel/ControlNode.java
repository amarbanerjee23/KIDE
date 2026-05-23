/**
 */
package mncModel;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Control Node</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Control Node
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link mncModel.ControlNode#getName <em>Name</em>}</li>
 *   <li>{@link mncModel.ControlNode#getInterfaceDescription <em>Interface Description</em>}</li>
 *   <li>{@link mncModel.ControlNode#getChildNodes <em>Child Nodes</em>}</li>
 *   <li>{@link mncModel.ControlNode#getParentNode <em>Parent Node</em>}</li>
 *   <li>{@link mncModel.ControlNode#getCommandResponseBlocks <em>Command Response Blocks</em>}</li>
 *   <li>{@link mncModel.ControlNode#getEventBlocks <em>Event Blocks</em>}</li>
 *   <li>{@link mncModel.ControlNode#getAlarmBlocks <em>Alarm Blocks</em>}</li>
 *   <li>{@link mncModel.ControlNode#getDataPointBlocks <em>Data Point Blocks</em>}</li>
 * </ul>
 *
 * @see mncModel.MncModelPackage#getControlNode()
 * @model
 * @generated
 */
public interface ControlNode extends mncModel.System {
	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see mncModel.MncModelPackage#getControlNode_Name()
	 * @model
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link mncModel.ControlNode#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Interface Description</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Interface Description</em>' reference.
	 * @see #setInterfaceDescription(InterfaceDescription)
	 * @see mncModel.MncModelPackage#getControlNode_InterfaceDescription()
	 * @model
	 * @generated
	 */
	InterfaceDescription getInterfaceDescription();

	/**
	 * Sets the value of the '{@link mncModel.ControlNode#getInterfaceDescription <em>Interface Description</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Interface Description</em>' reference.
	 * @see #getInterfaceDescription()
	 * @generated
	 */
	void setInterfaceDescription(InterfaceDescription value);

	/**
	 * Returns the value of the '<em><b>Child Nodes</b></em>' reference list.
	 * The list contents are of type {@link mncModel.ControlNode}.
	 * It is bidirectional and its opposite is '{@link mncModel.ControlNode#getParentNode <em>Parent Node</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Child Nodes</em>' reference list.
	 * @see mncModel.MncModelPackage#getControlNode_ChildNodes()
	 * @see mncModel.ControlNode#getParentNode
	 * @model opposite="parentNode" resolveProxies="false"
	 * @generated
	 */
	EList<ControlNode> getChildNodes();

	/**
	 * Returns the value of the '<em><b>Parent Node</b></em>' reference.
	 * It is bidirectional and its opposite is '{@link mncModel.ControlNode#getChildNodes <em>Child Nodes</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Parent Node</em>' reference.
	 * @see #setParentNode(ControlNode)
	 * @see mncModel.MncModelPackage#getControlNode_ParentNode()
	 * @see mncModel.ControlNode#getChildNodes
	 * @model opposite="childNodes"
	 * @generated
	 */
	ControlNode getParentNode();

	/**
	 * Sets the value of the '{@link mncModel.ControlNode#getParentNode <em>Parent Node</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Parent Node</em>' reference.
	 * @see #getParentNode()
	 * @generated
	 */
	void setParentNode(ControlNode value);

	/**
	 * Returns the value of the '<em><b>Command Response Blocks</b></em>' containment reference list.
	 * The list contents are of type {@link mncModel.CommandResponseBlock}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Command Response Blocks</em>' containment reference list.
	 * @see mncModel.MncModelPackage#getControlNode_CommandResponseBlocks()
	 * @model containment="true"
	 * @generated
	 */
	EList<CommandResponseBlock> getCommandResponseBlocks();

	/**
	 * Returns the value of the '<em><b>Event Blocks</b></em>' containment reference list.
	 * The list contents are of type {@link mncModel.EventBlock}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Event Blocks</em>' containment reference list.
	 * @see mncModel.MncModelPackage#getControlNode_EventBlocks()
	 * @model containment="true"
	 * @generated
	 */
	EList<EventBlock> getEventBlocks();

	/**
	 * Returns the value of the '<em><b>Alarm Blocks</b></em>' containment reference list.
	 * The list contents are of type {@link mncModel.AlarmBlock}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Alarm Blocks</em>' containment reference list.
	 * @see mncModel.MncModelPackage#getControlNode_AlarmBlocks()
	 * @model containment="true"
	 * @generated
	 */
	EList<AlarmBlock> getAlarmBlocks();

	/**
	 * Returns the value of the '<em><b>Data Point Blocks</b></em>' containment reference list.
	 * The list contents are of type {@link mncModel.DataPointBlock}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Data Point Blocks</em>' containment reference list.
	 * @see mncModel.MncModelPackage#getControlNode_DataPointBlocks()
	 * @model containment="true"
	 * @generated
	 */
	EList<DataPointBlock> getDataPointBlocks();

} // ControlNode
