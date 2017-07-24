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
 * <p> Provides classes for handling and manipulating text, partly as an extension to {@link java.text}.
 * The classes in this package are, for the most part, intended to be instantiated (i.e. they are not utility classes
 * with lots of static methods). </p>
 *
 * <p>Amongst other classes, the text package provides a replacement for {@link java.lang.StringBuffer} named {@link com.rdm.common.util.text.StrBuilder}, a class for substituting variables within a String named {@link com.rdm.common.util.text.StrSubstitutor} and a replacement for {@link java.util.StringTokenizer} named {@link com.rdm.common.util.text.StrTokenizer}.
 * While somewhat ungainly, the <code>Str</code> prefix has been used to ensure we don't clash with any current or future standard Java classes. </p>
 *
 * @since 2.1
 * @version $Id: package-info.java 1559146 2014-01-17 15:23:19Z britter $
 */
package com.pxl.common.util.text;