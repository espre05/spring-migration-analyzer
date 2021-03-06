/*
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.migrationanalyzer.contributions.apiusage;

/**
 * An enumeration of the different types of API usage
 */
public enum ApiUsageType {

    IMPLEMENTS_INTERFACE, //
    EXTENDS_TYPE, //
    FIELD, //
    METHOD_ARGUMENT, //
    LOCAL_VARIABLE, //
    RETURN_ARGUMENT, //
    THROWS_EXCEPTION, //
    ANNOTATED_TYPE, //
    ANNOTATED_FIELD, //
    ANNOTATED_METHOD, //
    ANNOTATED_METHOD_ARGUMENT, //
    SPRING_CONFIGURATION, //
    DEPLOYMENT_DESCRIPTOR;
}
