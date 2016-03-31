/**
 * Copyright 2006 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.apache.nutch.parse.xml;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Rida Benjelloun (rida.benjelloun@doculibre.com)
 */
public class XMLUtils {

    public static Document parseXml(InputStream is) {
        org.jdom.Document xmlDoc = new org.jdom.Document();
        try {
            SAXBuilder builder = new SAXBuilder();
            builder.setValidation(false);
            xmlDoc = builder.build(is);
        } catch (JDOMException e) {

        } catch (IOException e) {

        }
        return xmlDoc;
    }
}
