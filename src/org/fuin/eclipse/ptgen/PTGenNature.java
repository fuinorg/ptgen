package org.fuin.eclipse.ptgen;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

public class PTGenNature implements IProjectNature {

    public static final String NATURE_ID = "org.fuin.eclipse.ptgen.ptgennature";

    public PTGenNature() {
        super();
    }

    @Override
    public void configure() throws CoreException {
        // Not used
    }

    @Override
    public void deconfigure() throws CoreException {
        // Not used
    }

    @Override
    public IProject getProject() {
        // Not used
        return null;
    }

    @Override
    public void setProject(IProject project) {
        // Not used
    }

}
