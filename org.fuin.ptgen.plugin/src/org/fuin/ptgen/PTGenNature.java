/**
 * Copyright (C) 2013 Future Invent Informationsmanagement GmbH. All rights
 * reserved. <http://www.fuin.org/>
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see <http://www.gnu.org/licenses/>.
 */
package org.fuin.ptgen;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

/**
 * Eclipse nature for {@link ParameterizedTemplateGeneratorBuilder} projects.
 */
public final class PTGenNature implements IProjectNature {

    /** Nature ID to use in ".project" file. */
    public static final String NATURE_ID = "org.fuin.ptgen.plugin.ptgennature";

    /**
     * Default constructor.
     */
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
    public void setProject(final IProject project) {
        // Not used
    }

}
