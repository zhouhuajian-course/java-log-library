/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2015, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */
package ch.qos.logback.core.subst;

import ch.qos.logback.core.ContextBase;
import ch.qos.logback.core.spi.ScanException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author Ceki G&uuml;lc&uuml;
 */
public class NodeToStringTransformerTest {

    ContextBase propertyContainer0 = new ContextBase();

    @BeforeEach
    public void setUp() {
        propertyContainer0.putProperty("k0", "v0");
        propertyContainer0.putProperty("zero", "0");
        propertyContainer0.putProperty("v0.jdbc.url", "http://..");
        propertyContainer0.putProperty("host", "local");

    }

    private Node makeNode(String input) throws ScanException {
        Tokenizer tokenizer = new Tokenizer(input);
        Parser parser = new Parser(tokenizer.tokenize());
        return parser.parse();
    }

    @Test
    public void literal() throws ScanException {
        String input = "abv";
        Node node = makeNode(input);
        NodeToStringTransformer nodeToStringTransformer = new NodeToStringTransformer(node, propertyContainer0);
        Assertions.assertEquals(input, nodeToStringTransformer.transform());
    }

    void checkInputEqualsOutput(String input) throws ScanException {
        Node node = makeNode(input);
        NodeToStringTransformer nodeToStringTransformer = new NodeToStringTransformer(node, propertyContainer0);
        Assertions.assertEquals(input, nodeToStringTransformer.transform());
    }

    @Test
    public void literalWithNestedAccolades() throws ScanException {
        checkInputEqualsOutput("%logger{35}");
        checkInputEqualsOutput("%a{35} %b{35} c");
        checkInputEqualsOutput("%replace(%msg){'\\d{14,16}', 'XXXX'}");
        checkInputEqualsOutput("TEST %d{HHmmssSSS} [%thread] %-5level %logger{36} - %msg%n");
    }

    @Test
    public void variable() throws ScanException {
        String input = "${k0}";
        Node node = makeNode(input);
        NodeToStringTransformer nodeToStringTransformer = new NodeToStringTransformer(node, propertyContainer0);
        Assertions.assertEquals("v0", nodeToStringTransformer.transform());
    }

    @Test
    public void recursion0() throws ScanException {
        assumeCycle("${nested:-${nested}}");
        assumeCycle("${:-${}}");
        assumeCycle("${$a:-${a:-${a:-b}}");
    }

    @Test
    public void recursion1() throws ScanException {
        propertyContainer0.putProperty("k", "${a}");
        propertyContainer0.putProperty("a", "${k}");
        assumeCycle("${k}");
    }

    // Is this a feature or a bug?
    @Test
    public void cascadedTransformation() throws ScanException {
        propertyContainer0.putProperty("x", "${a}");
        propertyContainer0.putProperty("a", "b");
        propertyContainer0.putProperty("b", "c");
        String result = transform("${${x}}");
        Assertions.assertEquals("c", result);
    }

    public void assumeCycle(String input) throws ScanException {

        try {
            transform(input);
        } catch (IllegalArgumentException e) {
            return;
        }
        Assertions.fail("circular reference should have been caught input=" + input);
    }

    private String transform(String input) throws ScanException {
        Node node = makeNode(input);
        NodeToStringTransformer nodeToStringTransformer = new NodeToStringTransformer(node, propertyContainer0);
        return nodeToStringTransformer.transform();
    }

    @Test
    public void literalVariableLiteral() throws ScanException {
        String input = "a${k0}c";
        Node node = makeNode(input);
        NodeToStringTransformer nodeToStringTransformer = new NodeToStringTransformer(node, propertyContainer0);
        Assertions.assertEquals("av0c", nodeToStringTransformer.transform());
    }

    @Test
    public void nestedVariable() throws ScanException {
        String input = "a${k${zero}}b";
        Node node = makeNode(input);
        NodeToStringTransformer nodeToStringTransformer = new NodeToStringTransformer(node, propertyContainer0);
        Assertions.assertEquals("av0b", nodeToStringTransformer.transform());
    }

    @Test
    public void LOGBACK729() throws ScanException {
        String input = "${${k0}.jdbc.url}";
        Node node = makeNode(input);
        NodeToStringTransformer nodeToStringTransformer = new NodeToStringTransformer(node, propertyContainer0);
        Assertions.assertEquals("http://..", nodeToStringTransformer.transform());
    }

    @Test
    public void LOGBACK744_withColon() throws ScanException {
        String input = "%d{HH:mm:ss.SSS} host:${host} %logger{36} - %msg%n";
        Node node = makeNode(input);
        NodeToStringTransformer nodeToStringTransformer = new NodeToStringTransformer(node, propertyContainer0);
        System.out.println(nodeToStringTransformer.transform());
        Assertions.assertEquals("%d{HH:mm:ss.SSS} host:local %logger{36} - %msg%n", nodeToStringTransformer.transform());
    }

    @Test
    public void loneColonShouldReadLikeAnyOtherCharacter() throws ScanException {
        String input = "java:comp/env/jdbc/datasource";
        Node node = makeNode(input);
        NodeToStringTransformer nodeToStringTransformer = new NodeToStringTransformer(node, propertyContainer0);
        Assertions.assertEquals(input, nodeToStringTransformer.transform());
    }

    @Test
    public void withDefaultValue() throws ScanException {
        String input = "${k67:-b}c";
        Node node = makeNode(input);
        NodeToStringTransformer nodeToStringTransformer = new NodeToStringTransformer(node, propertyContainer0);
        Assertions.assertEquals("bc", nodeToStringTransformer.transform());
    }

    @Test
    public void defaultValueNestedAsVar() throws ScanException {
        String input = "a${k67:-x${k0}}c";
        Node node = makeNode(input);
        NodeToStringTransformer nodeToStringTransformer = new NodeToStringTransformer(node, propertyContainer0);
        Assertions.assertEquals("axv0c", nodeToStringTransformer.transform());
    }

    @Test
    public void LOGBACK_1101() throws ScanException {
        String input = "a: {y}";
        Node node = makeNode(input);
        NodeToStringTransformer nodeToStringTransformer = new NodeToStringTransformer(node, propertyContainer0);
        Assertions.assertEquals("a: {y}", nodeToStringTransformer.transform());
    }

    @Test
    public void definedAsEmpty() throws ScanException {
        propertyContainer0.putProperty("empty", "");
        String input = "a=${empty}";
        Node node = makeNode(input);
        NodeToStringTransformer nodeToStringTransformer = new NodeToStringTransformer(node, propertyContainer0);
        Assertions.assertEquals("a=", nodeToStringTransformer.transform());
    }

    @Test
    public void emptyDefault() throws ScanException {
        propertyContainer0.putProperty("empty", "");
        String input = "a=${undef:-${empty}}";
        Node node = makeNode(input);
        NodeToStringTransformer nodeToStringTransformer = new NodeToStringTransformer(node, propertyContainer0);
        Assertions.assertEquals("a=", nodeToStringTransformer.transform());
    }
}
