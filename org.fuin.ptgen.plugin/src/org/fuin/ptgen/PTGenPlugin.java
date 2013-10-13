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

import java.util.ArrayList;
import java.util.List;

import javax.validation.Validation;
import javax.validation.ValidationProviderResolver;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.spi.ValidationProvider;

import org.eclipse.core.runtime.Plugin;
import org.fuin.objects4j.common.Contract;
import org.hibernate.validator.HibernateValidator;
import org.osgi.framework.BundleContext;

/**
 * Plugin for the SrcGen4J parameterized template generator.
 */
public final class PTGenPlugin extends Plugin {

    @Override
    public final void start(final BundleContext context) throws Exception {
        super.start(context);
        final ValidatorFactory factory = Validation.byProvider(HibernateValidator.class)
                .providerResolver(new ValidationProviderResolver() {
                    @Override
                    public List<ValidationProvider<?>> getValidationProviders() {
                        final List<ValidationProvider<?>> list = new ArrayList<ValidationProvider<?>>();
                        list.add(new HibernateValidator());
                        return list;
                    }
                }).configure().buildValidatorFactory();
        final Validator validator = factory.getValidator();
        Contract.setValidator(validator);
    }

    @Override
    public final void stop(final BundleContext context) throws Exception {
        Contract.setValidator(null);
        super.stop(context);
    }

}
