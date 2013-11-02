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

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.fuin.srcgen4j.commons.FileMarker;
import org.fuin.srcgen4j.commons.FileMarkerCapable;
import org.fuin.srcgen4j.commons.FileMarkerSeverity;
import org.fuin.srcgen4j.commons.SrcGen4JContext;
import org.fuin.utils4j.Utils4J;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Context for the build process.
 */
public final class PTGenContext implements SrcGen4JContext, FileMarkerCapable {

    private static final Logger LOG = LoggerFactory.getLogger(PTGenContext.class);

    private final IProject project;

    /**
     * Constructor with project.
     * 
     * @param project
     *            Project the context belongs to.
     */
    public PTGenContext(@NotNull final IProject project) {
        super();
        this.project = project;
    }

    private void setAttribute(final IMarker marker, final String name, final Object value) {
        try {
            marker.setAttribute(name, value);
        } catch (final CoreException ex) {
            LOG.error("Couldn't set attribute '" + name + "' to '" + value + "' for: " + marker, ex);
        }
    }

    private int toSeverity(final FileMarkerSeverity severity) {
        if (severity == FileMarkerSeverity.INFO) {
            return IMarker.SEVERITY_INFO;
        }
        if (severity == FileMarkerSeverity.WARNING) {
            return IMarker.SEVERITY_WARNING;
        }
        if (severity == FileMarkerSeverity.ERROR) {
            return IMarker.SEVERITY_ERROR;
        }
        throw new IllegalStateException("Unknown enum value: " + severity);
    }

    private IMarker createMarker(final IResource file) {
        try {
            return file.createMarker(IMarker.PROBLEM);
        } catch (final CoreException ex) {
            throw new RuntimeException("Couldn't create marker for: " + file);
        }
    }

    private IMarker createIMarker(final IResource file, final FileMarkerSeverity severity,
            final String message) {
        final IMarker marker = createMarker(file);
        if (marker.exists()) {
            setAttribute(marker, IMarker.MESSAGE, message);
            setAttribute(marker, IMarker.PRIORITY, toSeverity(severity));
        } else {
            LOG.error("Marker doesn't exist: " + severity + " ");
        }
        return marker;
    }

    private PTGenFileMarker createMarker(final IResource file, final FileMarkerSeverity severity,
            final String message) {
        return new PTGenFileMarker(createIMarker(file, severity, message));
    }

    private FileMarker createMarker(final IResource file, final FileMarkerSeverity severity,
            final String message, final int line) {
        final IMarker m = createIMarker(file, severity, message);
        setAttribute(m, IMarker.LINE_NUMBER, line);
        return new PTGenFileMarker(m);
    }

    private FileMarker createMarker(final IResource file, final FileMarkerSeverity severity,
            final String message, final int start, final int length) {
        final IMarker m = createIMarker(file, severity, message);
        setAttribute(m, IMarker.CHAR_START, start);
        setAttribute(m, IMarker.CHAR_END, start + length);
        return new PTGenFileMarker(m);
    }

    private boolean fileInsideProject(final File file) {
        return Utils4J.fileInsideDirectory(project.getLocation().toFile(), file);
    }

    private String getProjectRelativePath(final File file) {
        return Utils4J.getRelativePath(project.getLocation().toFile(), file);
    }

    private IResource getResource(final File file) {
        if (!fileInsideProject(file)) {
            return null;
        }
        return project.findMember(getProjectRelativePath(file));
    }

    private Iterator<FileMarker> getIterator(final IMarker[] markers) {
        final List<FileMarker> list = new ArrayList<FileMarker>();
        if (markers != null) {
            for (final IMarker marker : markers) {
                list.add(new PTGenFileMarker(marker));
            }
        }
        return list.iterator();
    }

    private IMarker[] findProblemMarkers(final IResource resource, final int depth) {
        try {
            return resource.findMarkers(IMarker.PROBLEM, false, depth);
        } catch (final CoreException ex) {
            LOG.error("Couldn't find problem markers for: " + resource, ex);
            return null;
        }
    }

    @Override
    public final FileMarker addMarker(final File file, final FileMarkerSeverity severity,
            final String message) {
        final IResource resource = getResource(file);
        if (resource == null) {
            return null;
        }
        return createMarker(resource, severity, message);
    }

    @Override
    public final FileMarker addMarker(final File file, final FileMarkerSeverity severity,
            final String message, final int line) {
        final IResource resource = getResource(file);
        if (resource == null) {
            return null;
        }
        return createMarker(resource, severity, message, line);
    }

    @Override
    public final FileMarker addMarker(final File file, final FileMarkerSeverity severity,
            final String message, final int start, final int length) {
        final IResource resource = getResource(file);
        if (resource == null) {
            return null;
        }
        return createMarker(resource, severity, message, start, length);
    }

    @Override
    public final void removeMarker(final File file, final FileMarker marker) {
        if (marker instanceof PTGenFileMarker) {
            final PTGenFileMarker fm = (PTGenFileMarker) marker;
            final IMarker im = fm.getMarker();
            if (im.exists()) {
                try {
                    fm.getMarker().delete();
                } catch (final CoreException ex) {
                    LOG.error("Couldn't remove problem marker for: " + file, ex);
                }
            }
        }
    }

    @Override
    public final void removeAllMarkers(final File file) {
        final IResource resource = getResource(file);
        if (resource != null) {
            try {
                resource.deleteMarkers(IMarker.PROBLEM, false, IResource.DEPTH_ZERO);
            } catch (final CoreException ex) {
                LOG.error("Couldn't remove all problem markers for: " + file, ex);
            }
        }
    }

    @Override
    public final Iterator<FileMarker> getMarkerIterator(final File file) {
        final IResource resource = getResource(file);
        if (resource == null) {
            return null;
        }
        final IMarker[] markers = findProblemMarkers(resource, IResource.DEPTH_ZERO);
        return getIterator(markers);
    }

    @Override
    public final void removeAllMarkers() {
        try {
            project.deleteMarkers(IMarker.PROBLEM, false, IResource.DEPTH_INFINITE);
        } catch (final CoreException ex) {
            LOG.error("Couldn't remove all problem markers for: " + project, ex);
        }
    }

    @Override
    public final Iterator<FileMarker> getMarkerIterator() {
        final IMarker[] markers = findProblemMarkers(project, IResource.DEPTH_INFINITE);
        return getIterator(markers);
    }

    @Override
    public final ClassLoader getClassLoader() {
        return PTGenContext.class.getClassLoader();
    }

}
