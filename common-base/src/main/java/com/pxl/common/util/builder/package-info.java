/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * <p>Assists in creating consistent {@code equals(Object)}, {@code toString()}, {@code hashCode()}, and {@code compareTo(Object)} methods.
 * These classes are not thread-safe.</p>
 *
 * <p>When you write a {@link java.lang.Object#hashCode() hashCode()}, do you check Bloch's Effective Java? No?
 * You just hack in a quick number?
 * Well {@link com.rdm.common.util.builder.HashCodeBuilder} will save your day.
 * It, and its buddies ({@link com.rdm.common.util.builder.EqualsBuilder}, {@link com.rdm.common.util.builder.CompareToBuilder}, {@link com.rdm.common.util.builder.ToStringBuilder}), take care of the nasty bits while you focus on the important bits, like which fields will go into making up the hashcode.</p>
 *
 * @see java.lang.Object#equals(Object)
 * @see java.lang.Object#toString()
 * @see java.lang.Object#hashCode()
 * @see java.lang.Comparable#compareTo(Object)
 *
 * @since 1.0
 * @version $Id: package-info.java 1559146 2014-01-17 15:23:19Z britter $
 */
package com.pxl.common.util.builder;
