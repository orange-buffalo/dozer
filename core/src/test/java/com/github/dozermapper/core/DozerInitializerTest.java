/*
 * Copyright 2005-2018 Dozer Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.dozermapper.core;

import com.github.dozermapper.core.builder.DestBeanBuilderCreator;
import com.github.dozermapper.core.classmap.generator.BeanMappingGenerator;
import com.github.dozermapper.core.config.BeanContainer;
import com.github.dozermapper.core.config.Settings;
import com.github.dozermapper.core.config.SettingsDefaults;
import com.github.dozermapper.core.factory.DestBeanCreator;
import com.github.dozermapper.core.propertydescriptor.PropertyDescriptorFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DozerInitializerTest extends AbstractDozerTest {

    private Settings settings;

    private DozerInitializer instance;
    private BeanContainer beanContainer;
    private DestBeanBuilderCreator destBeanBuilderCreator;
    private BeanMappingGenerator beanMappingGenerator;
    private PropertyDescriptorFactory propertyDescriptorFactory;
    private DestBeanCreator destBeanCreator;

    @Before
    public void setUp() throws Exception {
        beanContainer = new BeanContainer();
        settings = new Settings();
        destBeanBuilderCreator = new DestBeanBuilderCreator();
        propertyDescriptorFactory = new PropertyDescriptorFactory();
        destBeanCreator = new DestBeanCreator(beanContainer);
        beanMappingGenerator = new BeanMappingGenerator(beanContainer, destBeanCreator, propertyDescriptorFactory);
        instance = new DozerInitializer();
        instance.destroy(settings);
    }

    @After
    public void tearDown() {
        instance.destroy(settings);
    }

    @Test
    public void testIsInitialized() {
        assertFalse(instance.isInitialized());

        instance.init(settings, beanContainer, destBeanBuilderCreator, beanMappingGenerator, propertyDescriptorFactory, destBeanCreator);
        assertTrue(instance.isInitialized());

        instance.destroy(settings);
        assertFalse(instance.isInitialized());
    }

    @Test
    public void testDoubleCalls() {
        instance.destroy(settings);
        assertFalse(instance.isInitialized());

        instance.init(settings, beanContainer, destBeanBuilderCreator, beanMappingGenerator, propertyDescriptorFactory, destBeanCreator);
        instance.init(settings, beanContainer, destBeanBuilderCreator, beanMappingGenerator, propertyDescriptorFactory, destBeanCreator);
        assertTrue(instance.isInitialized());

        instance.destroy(settings);
        instance.destroy(settings);
        assertFalse(instance.isInitialized());
    }

    @Test(expected = MappingException.class)
    public void testBeanIsMissing() {
        Settings settings = mock(Settings.class);
        when(settings.getClassLoaderBeanName()).thenReturn(SettingsDefaults.CLASS_LOADER_BEAN);
        when(settings.getProxyResolverBeanName()).thenReturn("no.such.class.Found");

        instance.initialize(settings, getClass().getClassLoader(), beanContainer, destBeanBuilderCreator,
                            beanMappingGenerator, propertyDescriptorFactory, destBeanCreator);
        fail();
    }

    @Test(expected = MappingException.class)
    public void testBeanIsNotAssignable() {
        Settings settings = mock(Settings.class);
        when(settings.getClassLoaderBeanName()).thenReturn("java.lang.String");
        when(settings.getProxyResolverBeanName()).thenReturn(SettingsDefaults.PROXY_RESOLVER_BEAN);

        instance.initialize(settings, getClass().getClassLoader(), beanContainer, destBeanBuilderCreator,
                            beanMappingGenerator, propertyDescriptorFactory, destBeanCreator);
        fail();
    }
}