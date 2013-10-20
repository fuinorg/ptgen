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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.fuin.srcgen4j.commons.GenerateException;
import org.fuin.srcgen4j.commons.ParseException;
import org.fuin.srcgen4j.commons.SrcGen4J;
import org.fuin.srcgen4j.commons.SrcGen4JConfig;
import org.fuin.srcgen4j.commons.UnmarshalObjectException;
import org.fuin.srcgen4j.core.velocity.PTGenHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Builder for the SrcGen4J parameterized template generator.
 */
public final class PTGenBuilder extends IncrementalProjectBuilder {

    /** Builder ID to use in ".project" file. */
    public static final String BUILDER_ID = "org.fuin.ptgen.plugin.ptgenbuilder";

    private static final Logger LOG = LoggerFactory.getLogger(PTGenBuilder.class);

    private Map<String, SrcGen4J> configs;

    /**
     * Default constructor.
     */
    public PTGenBuilder() {
        super();
        configs = new HashMap<String, SrcGen4J>();
    }

    @Override
    protected void clean(final IProgressMonitor monitor) throws CoreException {
        final IProject project = getProject();
        final boolean hasPTGenNature = project.hasNature(PTGenNature.NATURE_ID);
        if (hasPTGenNature) {
            if (LOG.isInfoEnabled()) {
                LOG.info("CLEAN " + getProject().getName());
            }
            configs.remove(project.getName());
        }
    }

    @Override
    protected final IProject[] build(final int kind, final Map<String, String> args,
            final IProgressMonitor monitor) throws CoreException {

        if (LOG.isTraceEnabled()) {
            LOG.trace("BEGIN build(int, Map, IProgressMonitor)");
            LOG.trace("build=" + kind);
            LOG.trace("args=" + args);
        }

        if (LOG.isInfoEnabled()) {
            LOG.info(getBuildType(kind) + " " + getProject().getName());
        }

        final IProject project = getProject();
        final boolean hasPTGenNature = project.hasNature(PTGenNature.NATURE_ID);
        if (LOG.isDebugEnabled()) {
            LOG.debug(PTGenNature.NATURE_ID + " Nature=" + hasPTGenNature);
        }
        if (hasPTGenNature) {
            if (kind == IncrementalProjectBuilder.FULL_BUILD
                    || kind == IncrementalProjectBuilder.CLEAN_BUILD) {
                fullBuild(project, monitor);
            } else {
                final IResourceDelta delta = getDelta(getProject());
                if (delta == null) {
                    fullBuild(project, monitor);
                } else {
                    incrementalBuild(project, delta, monitor);
                }
            }
        }
        LOG.trace("END build(int, Map, IProgressMonitor)");
        return null;
    }

    private void incrementalBuild(final IProject project, final IResourceDelta delta,
            final IProgressMonitor monitor) throws CoreException {

        LOG.trace("BEGIN incrementalBuild(IResourceDelta, IProgressMonitor)");
        LOG.info("Incremental build on: " + project);

        final SrcGen4J srcGen4J = getSrcGen4J(project);
        if (srcGen4J != null) {

            // Create filtered file list
            final Set<File> files = new HashSet<File>();
            delta.accept(new IResourceDeltaVisitor() {
                @Override
                public final boolean visit(final IResourceDelta delta) throws CoreException {
                    addFile(srcGen4J, files, delta.getResource());
                    return true;
                }
            });

            if (files.size() == 0) {
                LOG.info("No changed files");
            } else {

                // Execute generation
                try {
                    srcGen4J.execute(files);
                } catch (final ParseException ex) {
                    LOG.error(
                            "Error parsing the model [incrementalBuild, Project='"
                                    + project.getName() + "']", ex);
                } catch (final GenerateException ex) {
                    LOG.error("Error generating [incrementalBuild, Project='" + project.getName()
                            + "']", ex);
                }

            }

        }
        LOG.trace("END incrementalBuild(IResourceDelta, IProgressMonitor)");

    }

    private void fullBuild(final IProject project, final IProgressMonitor monitor)
            throws CoreException {

        LOG.trace("BEGIN fullBuild(IProgressMonitor)");
        LOG.info("Full build on: " + project);

        final SrcGen4J srcGen4J = getSrcGen4J(project);
        if (srcGen4J != null) {
            // Execute generation
            try {
                srcGen4J.execute();
            } catch (final ParseException ex) {
                LOG.error("Error parsing the model [fullBuild, Project='" + project.getName()
                        + "']", ex);
            } catch (final GenerateException ex) {
                LOG.error("Error generating [fullBuild, Project='" + project.getName() + "']", ex);
            }
        }

        LOG.trace("END fullBuild(IProgressMonitor)");

    }

    private SrcGen4J getSrcGen4J(final IProject project) {
        SrcGen4J srcGen4J = configs.get(project.getName());
        if (srcGen4J == null) {
            final File configFile = project.getFile("srcgen4j-config.xml").getLocation().toFile();
            if (configFile.exists()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Reading config file: " + configFile);
                }
                try {
                    final SrcGen4JConfig config = PTGenHelper.createAndInit(configFile);
                    srcGen4J = new SrcGen4J(config, this.getClass().getClassLoader());
                    configs.put(project.getName(), srcGen4J);
                } catch (final UnmarshalObjectException ex) {
                    LOG.error("Error reading config file [Project='" + project.getName()
                            + "', file='" + configFile + "']", ex);
                }
            } else {
                LOG.error("Config file not found: " + configFile);
            }
        }
        return srcGen4J;
    }

    private void addFile(final SrcGen4J srcGen4J, final Set<File> files, final IResource resource) {

        if (resource.getType() == IResource.FILE) {
            final IPath path = resource.getLocation();
            if (path == null) {
                if (LOG.isTraceEnabled()) {
                    LOG.trace("No path " + getResourceType(resource) + ": " + resource.getName());
                }
            } else {
                final File file = path.toFile();
                if (srcGen4J.getFileFilter().accept(file)) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Added " + file.toString());
                    }
                    files.add(file);
                } else {
                    if (LOG.isTraceEnabled()) {
                        LOG.debug("No file filter match for " + file.toString());
                    }
                }
            }
        } else {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Ignored " + getResourceType(resource) + ": " + resource.getName());
            }
        }

    }

    private String getBuildType(final int kind) {
        if (kind == IncrementalProjectBuilder.AUTO_BUILD) {
            return "AUTO_BUILD";
        }
        if (kind == IncrementalProjectBuilder.CLEAN_BUILD) {
            return "CLEAN_BUILD";
        }
        if (kind == IncrementalProjectBuilder.FULL_BUILD) {
            return "FULL_BUILD";
        }
        if (kind == IncrementalProjectBuilder.INCREMENTAL_BUILD) {
            return "INC_BUILD";
        }
        return "UNKNOWN_" + kind;
    }

    private String getResourceType(final IResource resource) {
        if (resource == null) {
            return "null";
        }
        final int type = resource.getType();
        if (type == IResource.FILE) {
            return "FILE";
        }
        if (type == IResource.FOLDER) {
            return "FOLDER";
        }
        if (type == IResource.PROJECT) {
            return "PROJECT";
        }
        if (type == IResource.ROOT) {
            return "ROOT";
        }
        return "UNKNOWN_" + type;
    }

}
