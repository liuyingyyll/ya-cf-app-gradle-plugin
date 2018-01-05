/*
 * Copyright 2013-2016 the original author or authors.
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

package io.pivotal.services.plugin;

import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

import java.util.List;
import java.util.Map;

/**
 * Holds the set of properties that will drive the various Gradle tasks
 *
 * @author Biju Kunjummen
 */
@Value.Immutable(copy = true)
public abstract class CfProperties {
    public abstract String ccHost();

    @Nullable
    public abstract String ccUser();

    @Nullable
    public abstract String ccPassword();

    @Nullable
    public abstract String ccToken();

    public abstract String org();

    public abstract String space();

    public abstract String name();

    @Nullable
    public abstract String filePath();

    @Nullable
    public abstract String host();

    @Nullable
    public abstract String domain();

    @Nullable
    public abstract String path();

    @Nullable
    public abstract String state();

    @Nullable
    public abstract String buildpack();

    @Nullable
    public abstract String command();

    @Nullable
    public abstract Boolean console();

    @Nullable
    public abstract Boolean debug();

    @Nullable
    public abstract String detectedStartCommand();

    @Nullable
    public abstract Integer diskQuota();

    @Nullable
    public abstract Boolean enableSsh();

    @Nullable
    public abstract Map<String, String> environment();

    @Nullable
    public abstract Integer timeout();

    @Nullable
    public abstract String healthCheckType();

    @Nullable
    public abstract Integer instances();

    @Nullable
    public abstract Integer memory();

    @Nullable
    public abstract List<Integer> ports();

    @Nullable
    public abstract List<String> services();

    @Nullable
    public abstract Integer stagingTimeout();

    @Nullable
    public abstract Integer startupTimeout();

    @Nullable
    public abstract List<CfServiceDetail> cfServices();

    @Nullable
    public abstract List<CfUserProvidedServiceDetail> cfUserProvidedServices();

    @Nullable
    public abstract CfProxySettingsDetail cfProxySettings();
}
