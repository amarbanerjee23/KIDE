//package com.model.domain.mnc.design.ui;
//
//import org.eclipse.jface.wizard.Wizard;
//import org.eclipse.jface.wizard.WizardPage;
//import org.eclipse.swt.SWT;
//import org.eclipse.swt.widgets.Composite;
//import org.eclipse.swt.widgets.Label;
//import swt.SWTResourceManager;
//import org.eclipse.swt.widgets.List;
//import mncModel.CommandResponseBlock;
//import mncModel.ControlNode;
//import mncModel.Model;
//import mncModel.OperatingState;
//
//
//public class TransitionInformationWizard extends Wizard {
//
//	TransitionInfromationWizardPage infromationWizardPage;
//	OperatingState currentContextObject;
//	public TransitionInformationWizard() {
//		setWindowTitle("New Wizard");
//	}
//
//	@Override
//	public void addPages() {
//		infromationWizardPage=new TransitionInfromationWizardPage();
//		infromationWizardPage.setCurrentContextObject(currentContextObject);
//		addPage(infromationWizardPage);
//	}
//
//	@Override
//	public boolean performFinish() {
//		selectedUtility= infromationWizardPage.getSelectedTransitionContext();
//		return true;
//	}
//
//	public void setCurrentContextObject(OperatingState operatingState) {		
//		currentContextObject=operatingState;		
//	}
//	
//	public TransitionUtility getSelectedTransitionContext()
//	{		
//		return selectedUtility;
//	}
//	
//
//}
//
//class TransitionInfromationWizardPage extends WizardPage {
//
//	/**
//	 * Create the wizard.
//	 */
//	public TransitionInfromationWizardPage() {
//		super("wizardPage");
//		setTitle("Wizard Page title");
//		setDescription("Wizard Page description");
//	}
//
//	/**
//	 * Create contents of the wizard.
//	 * @param parent
//	 */
//	OperatingState operatingState;
//	List list;
//	
//	public void setCurrentContextObject(OperatingState operatingState) {		
//		this.operatingState=operatingState;
//		
//	}
//	
//	public void createControl(Composite parent) {
//		Composite container = new Composite(parent, SWT.NULL);
//
//		setControl(container);
//		
//		Label lblNewLabel = new Label(container, SWT.NONE);
//		lblNewLabel.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
//		lblNewLabel.setFont(SWTResourceManager.getFont("Verdana", 10, SWT.NORMAL));
//		lblNewLabel.setBounds(10, 10, 211, 30);
//		lblNewLabel.setText("Select CommandResponseBlock");
//		
//		list = new List(container, SWT.BORDER);
//		addListContents();
//		list.select(0);
//		list.setBounds(244, 8, 261, 116);
//	}
//
//	public void addListContents() {
//		
//		Model model=(Model) operatingState.eContainer().eContainer().eContainer();
//		for (mncModel.System system : model.getSystems()) {
//			if(system instanceof ControlNode)
//			{
//				for (CommandResponseBlock commandResponseBlock : ((ControlNode)system).getCommandResponseBlocks().getCommandResponseBlocks()) {
//					list.add(commandResponseBlock.getCommand().getName());
//				}	
//			}
//		}				
//	}
//	
//	public TransitionUtility getSelectedTransitionContext()
//	{
//		Model model=(Model) operatingState.eContainer().eContainer().eContainer();
//		for (mncModel.System system : model.getSystems()) {
//			if(system instanceof ControlNode)
//			{
//				for (CommandResponseBlock commandResponseBlock : ((ControlNode)system).getCommandResponseBlocks().getCommandResponseBlocks()) {
//					if(commandResponseBlock.getCommand().getName().equals(list.getSelection()[0]))
//					{
//						return (TransitionUtility)commandResponseBlock.getTransition();
//					}
//				}	
//			}
//		}
//		return null;
//	}
//}
//
