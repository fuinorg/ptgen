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

import javax.validation.constraints.NotNull;

import org.eclipse.core.resources.IMarker;
import org.fuin.objects4j.common.Contract;
import org.fuin.srcgen4j.commons.FileMarker;

/**
 * Maps the Eclipse specific marker to the general SrcGen4J marker.
 */
public final class PTGenFileMarker implements FileMarker {

    private final IMarker marker;

    /**
     * Constructor with marker.
     * 
     * @param marker
     *            Marker.
     */
    public PTGenFileMarker(@NotNull final IMarker marker) {
        super();
        Contract.requireArgNotNull("marker", marker);
        this.marker = marker;
    }

    /**
     * Returns the marker.
     * 
     * @return Marker.
     */
    @NotNull
    public final IMarker getMarker() {
        return marker;
    }

}
