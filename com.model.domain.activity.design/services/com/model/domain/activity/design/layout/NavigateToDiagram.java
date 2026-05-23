package com.model.domain.activity.design.layout;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.operation.IRunnableWithProgress;

import org.eclipse.sirius.business.api.dialect.DialectManager;
import org.eclipse.sirius.business.api.session.Session;
import org.eclipse.sirius.business.api.session.SessionManager;
import org.eclipse.sirius.diagram.DSemanticDiagram;
import org.eclipse.sirius.ui.business.api.dialect.DialectUIManager;
import org.eclipse.sirius.viewpoint.DRepresentation;
import org.eclipse.sirius.viewpoint.DSemanticDecorator;
import org.eclipse.sirius.viewpoint.description.RepresentationDescription;
import org.eclipse.ui.PlatformUI;

import com.model.domain.activity.design.services.ActivityDiagramServices;
import com.model.domain.activity.design.services.ActivityServices;

import activityDiagramModel.Activity;
import activityDiagramModel.ActivityDiagram;

public class NavigateToDiagram {

	public NavigateToDiagram() {

	}

	public static final String ACTIVITY_DIAGRAM = "ActivityDiagram";
	public static final String ACTIVITY_DESCRTPTION_DIAGRAM = "ActivityDescriptionDiagram";
	EObject currentContextObject = null;

	public void createActivityDiagram(ActivityDiagram element) {
		createRepresentation(element, ACTIVITY_DIAGRAM);
	}

	public void createActivityDescriptionDiagram(Activity element) {
		createRepresentation(element, ACTIVITY_DESCRTPTION_DIAGRAM);
	}

	// creating new diagram representation in representation.arid file
	private void createRepresentation(final EObject eObject, final String representationName,
			final RepresentationDescription representationDescription) {
		final Session session = SessionManager.INSTANCE.getSession(eObject);
		try {
			DialectManager manager = DialectManager.INSTANCE;
			final DRepresentation representation1 = manager.createRepresentation(representationName, eObject,
					representationDescription, session, new NullProgressMonitor());
			try {
				openRepresentation(representation1);
				return;
			} catch (Exception e) {
				final Session activeSession = SessionManager.INSTANCE.getSession(eObject);
				final Collection<DRepresentation> representations = DialectManager.INSTANCE
						.getAllRepresentations(activeSession);
				for (final DRepresentation representation : representations) {
					if (representation instanceof DSemanticDiagram) {
						final DSemanticDiagram diagram = (DSemanticDiagram) representation;
						if (representationName.trim().equals(diagram.getDescription().getName().trim())) {
							try {
								PlatformUI.getWorkbench().getActiveWorkbenchWindow().run(true, true,
										new IRunnableWithProgress() {
											public void run(IProgressMonitor monitor) {
												monitor.beginTask("Open representation " + representation.getName(), //$NON-NLS-1$
														100);
												DialectUIManager.INSTANCE.openEditor(session, representation, monitor);
												monitor.done();
											}
										});

							} catch (Exception e2) {
								System.out.println("" + e2);
							}

						}
					}
				}
				return;
			}

		}

		catch (Exception e) {

			// If representation is previously present the by one diagram representation
			// validation it shows exception
			final Session activeSession = SessionManager.INSTANCE.getSession(eObject);
			final Collection<DRepresentation> representations = DialectManager.INSTANCE
					.getAllRepresentations(activeSession);
			for (final DRepresentation representation : representations) {
				if (representation instanceof DSemanticDiagram) {
					final DSemanticDiagram diagram = (DSemanticDiagram) representation;
					if (representationName.trim().equals(diagram.getDescription().getName().trim())) {
						try {
							PlatformUI.getWorkbench().getActiveWorkbenchWindow().run(true, true,
									new IRunnableWithProgress() {
										public void run(IProgressMonitor monitor) {
											monitor.beginTask("Open representation " + representation.getName(), 100); //$NON-NLS-1$
											DialectUIManager.INSTANCE.openEditor(session, representation, monitor);
											monitor.done();
										}
									});

						} catch (Exception e2) {
							System.out.println("" + e2);
						}

					}
				}
			}

		}

	}

	// part of create representation, Setting name to diagram
	public void createRepresentation(EObject namedElement, String representationId) {
		System.out.println("Creating Diagram");
		currentContextObject = namedElement;
		final Session session = SessionManager.INSTANCE.getSession(namedElement);
		final RepresentationDescription representationDescription = getRepresentationDescription(namedElement, session,
				representationId);
		System.out.println("Representation Description ::" + representationDescription);
		/*
		 * createRepresentation(namedElement, namedElement.getName() + " " +
		 * representationId, //$NON-NLS-1$ representationDescription);
		 */
		String diagramName = " ";
		if (representationId.equals(ACTIVITY_DESCRTPTION_DIAGRAM)) {
			Activity elementActivity = (Activity)namedElement;
			diagramName = ActivityServices.ACTIVITY_DESCRIPTION_DIAGRAM_TITLE_PART + elementActivity.getName();
		}
		if (representationId.equals(ACTIVITY_DIAGRAM)) {
			ActivityDiagram elementActivityDiagram = (ActivityDiagram)namedElement;
			diagramName = ActivityDiagramServices.ACTIVITY_DIAGRAM_TITLE_PART + elementActivityDiagram.getName();
		}
		createRepresentation(namedElement, diagramName, // $NON-NLS-1$
				representationDescription);
	}

	// getting avaiable/created representation for opening
	private RepresentationDescription getRepresentationDescription(EObject eObject, Session session,
			String representationDescriptionId) {
		System.out.println("Session "+session);
		final Collection<RepresentationDescription> representationDescriptions = DialectManager.INSTANCE
				.getAvailableRepresentationDescriptions(session.getSelectedViewpoints(false), eObject);
		System.out.println("Rep Descs ::" + representationDescriptions + eObject);
		System.out.println("My ID ::: " + representationDescriptionId);
		for (final RepresentationDescription representationDescription : representationDescriptions) {
			if (representationDescriptionId.equals(representationDescription.getName())) {
				return representationDescription;
			}
		}
		return null;
	}

	// opening representation
	public void openRepresentation(final DRepresentation representation) {
		System.out.println("Opening Representation");
		// refressing current project
		for (IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
			// if(currentContextObject.eResource().getURI().toString().substring(19,currentContextObject.eResource().getURI().toString().lastIndexOf('/')).equals(""+project.getName()))
			// {
			try {
				project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
			} catch (CoreException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			// break;
			// }

		}

		final Session session = SessionManager.INSTANCE.getSession(((DSemanticDecorator) representation).getTarget());

		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().run(true, true, new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) {
					monitor.beginTask("Open representation " + representation.getName(), 100); //$NON-NLS-1$
					DialectUIManager.INSTANCE.openEditor(session, representation, monitor);
					monitor.done();
				}
			});
		} catch (final InvocationTargetException e) {
			System.out.println("" + e);
		} catch (final InterruptedException e) {
			System.out.println("" + e);
		}
	}
}
