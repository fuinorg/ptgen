package org.fuin.eclipse.ptgen;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

public final class ParameterizedTemplateGeneratorBuilder extends IncrementalProjectBuilder {

    public ParameterizedTemplateGeneratorBuilder() {
        super();
    }

    @Override
    protected final IProject[] build(final int kind, final Map<String, String> args,
            final IProgressMonitor monitor) throws CoreException {

        if (kind == IncrementalProjectBuilder.FULL_BUILD) {
            fullBuild(monitor);
        } else {
            final IResourceDelta delta = getDelta(getProject());
            if (delta == null) {
                fullBuild(monitor);
            } else {
                incrementalBuild(delta, monitor);
            }
        }
        System.out.println();
        return null;
    }

    private void incrementalBuild(IResourceDelta delta, IProgressMonitor monitor)
            throws CoreException {
        System.out.println("incremental build on: " + delta);
        delta.accept(new IResourceDeltaVisitor() {
            @Override
            public boolean visit(IResourceDelta delta) throws CoreException {
                System.out.println("inc: " + delta.getResource());
                return true; // visit children too
            }
        });
    }

    private void fullBuild(IProgressMonitor monitor) throws CoreException {
        System.out.println("full build on: " + getProject());
        getProject().accept(new IResourceVisitor() {
            @Override
            public boolean visit(IResource resource) throws CoreException {
                System.out.println(resource);
                return true;
            }
        });
    }

}
